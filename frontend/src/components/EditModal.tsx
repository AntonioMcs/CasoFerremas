import type { ReactNode } from 'react';

type Props = {
  open: boolean;
  title: string;
  dirty: boolean;
  saving?: boolean;
  onSave: () => Promise<void> | void;
  onCancel: () => void;
  children: ReactNode;
};

export default function EditModal({ open, title, dirty, saving, onSave, onCancel, children }: Props) {
  if (!open) return null;

  return (
    <div className="modal-overlay">
      <div className="confirm-modal panel-card edit-modal">
        <div className="card-head">
          <h3>{title}</h3>
        </div>
        <form
          className="modal-form"
          onSubmit={(event) => {
            event.preventDefault();
            void onSave();
          }}
        >
          <div className="modal-fields">{children}</div>
          <div className="modal-actions">
            <button className="tab" type="button" onClick={onCancel} disabled={saving}>
              Cancelar
            </button>
            <button className="update-button" type="submit" disabled={!dirty || saving}>
              {saving ? 'Actualizando...' : 'Actualizar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
