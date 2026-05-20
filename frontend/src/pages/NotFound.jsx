import { Link } from 'react-router-dom';

function NotFound() {
  return (
    <div className="d-flex flex-column align-items-center justify-content-center text-center animate-fadeInUp"
      style={{ minHeight: '70vh', padding: '2rem' }}>
      <div className="mb-4" style={{
        width: '120px', height: '120px',
        background: 'linear-gradient(135deg, #dcfce7, #bbf7d0)',
        borderRadius: '50%',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        boxShadow: '0 10px 30px rgba(5, 150, 105, 0.2)'
      }}>
        <i className="bi bi-map" style={{ fontSize: '3.5rem', color: '#059669' }}></i>
      </div>
      <h1 className="fw-bold mb-2" style={{ fontSize: '5rem', color: '#059669', lineHeight: 1 }}>404</h1>
      <h4 className="fw-bold text-dark mb-3">Página não encontrada</h4>
      <p className="text-muted mb-4" style={{ maxWidth: '400px' }}>
        A página que você está procurando não existe ou foi movida.
      </p>
      <Link to="/" className="btn btn-success px-5 py-3" style={{ borderRadius: '12px', fontWeight: '600' }}>
        <i className="bi bi-house me-2"></i>Voltar ao Início
      </Link>
    </div>
  );
}

export default NotFound;
