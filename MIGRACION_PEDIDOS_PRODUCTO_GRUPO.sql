-- Migracion PostgreSQL/Supabase: pedidos por producto + agrupacion de compra
-- Fecha: 2026-07-09

BEGIN;

ALTER TABLE pedidos
    ADD COLUMN IF NOT EXISTS id_producto INTEGER,
    ADD COLUMN IF NOT EXISTS grupo_compra_id VARCHAR(64),
    ADD COLUMN IF NOT EXISTS pedido_referencia INTEGER;

-- Backfill para pedidos historicos sin producto/grupo.
-- 1) usa el primer detalle_pedido para completar id_producto.
UPDATE pedidos p
SET id_producto = src.id_producto
FROM (
    SELECT dp.id_pedido, MIN(dp.id_producto) AS id_producto
    FROM detalle_pedido dp
    GROUP BY dp.id_pedido
) src
WHERE p.id_pedido = src.id_pedido
  AND p.id_producto IS NULL;

-- 2) para legacy, un pedido individual se considera su propio grupo.
UPDATE pedidos
SET grupo_compra_id = CONCAT('LEGACY-', id_pedido)
WHERE grupo_compra_id IS NULL;

-- 3) referencia al pedido principal del grupo (legacy = si mismo).
UPDATE pedidos
SET pedido_referencia = id_pedido
WHERE pedido_referencia IS NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_pedido_producto'
    ) THEN
        ALTER TABLE pedidos
            ADD CONSTRAINT fk_pedido_producto
            FOREIGN KEY (id_producto) REFERENCES productos(id_producto);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_pedido_referencia'
    ) THEN
        ALTER TABLE pedidos
            ADD CONSTRAINT fk_pedido_referencia
            FOREIGN KEY (pedido_referencia) REFERENCES pedidos(id_pedido);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_pedidos_grupo_compra_id ON pedidos(grupo_compra_id);
CREATE INDEX IF NOT EXISTS idx_pedidos_pedido_referencia ON pedidos(pedido_referencia);
CREATE INDEX IF NOT EXISTS idx_pedidos_id_producto ON pedidos(id_producto);

COMMIT;
