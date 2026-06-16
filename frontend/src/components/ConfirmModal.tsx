type Props = {
  open: boolean;
  title: string;
  message: string;
  loading?: boolean;
  onConfirm: () => Promise<void> | void;
  onCancel: () => void;
};

export default function ConfirmModal({ open, title, message, loading, onConfirm, onCancel }: Props) {
  if (!open) return null;

  return (
    <div className="modal-overlay">
      <div className="confirm-modal panel-card">
        <h3>{title}</h3>
        <p style={{ marginTop: 8, color: '#cbd5e1' }}>{message}</p>
        <div className="modal-actions" style={{ marginTop: 18, display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
          <button className="tab" onClick={onCancel} disabled={loading}>Cancelar</button>
          <button className="danger-button" onClick={() => void onConfirm()} disabled={loading}>
            {loading ? 'Eliminando...' : 'Eliminar'}
          </button>
        </div>
      </div>
    </div>
  );
}
