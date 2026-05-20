import { Link } from 'react-router-dom';

function Footer() {
  return (
    <footer style={{ marginTop: '4rem', padding: '0 1rem 1rem' }}>
      <div style={{
        background: 'linear-gradient(135deg, rgba(20,83,45,0.95) 0%, rgba(22,101,52,0.97) 100%)',
        backdropFilter: 'blur(16px)',
        borderRadius: '28px',
        border: '2px solid rgba(74,222,128,0.25)',
        boxShadow: '6px 6px 0px rgba(0,0,0,0.15), 0 16px 48px rgba(0,0,0,0.12)',
        overflow: 'hidden',
      }}>
        {/* Top accent */}
        <div style={{ height: '4px', background: 'linear-gradient(90deg, #4ade80, #22c55e, #86efac)' }} />

        <div className="container py-5">
          <div className="row g-4">
            {/* Brand */}
            <div className="col-lg-4 col-md-6">
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '1rem' }}>
                <div style={{ background: 'rgba(255,255,255,0.12)', borderRadius: '14px', padding: '8px', border: '2px solid rgba(255,255,255,0.2)', boxShadow: '3px 3px 0px rgba(0,0,0,0.12)' }}>
                  <img src="/Verdenovologo.png" alt="VerDenovo" height="32" />
                </div>
                <span style={{ color: 'white', fontWeight: 800, fontSize: '1.3rem', letterSpacing: '-0.02em' }}>VerDenovo</span>
              </div>
              <p style={{ color: 'rgba(255,255,255,0.65)', fontSize: '0.88rem', lineHeight: '1.7', marginBottom: '1.25rem' }}>
                Transformando o futuro através da reciclagem inteligente. Conectamos pessoas e pontos de coleta para um mundo mais sustentável.
              </p>
              <div style={{ display: 'flex', gap: '10px' }}>
                {['facebook', 'instagram', 'linkedin', 'twitter'].map(s => (
                  <a key={s} href={`https://${s}.com`} target="_blank" rel="noreferrer"
                    style={{ width: '38px', height: '38px', background: 'rgba(255,255,255,0.12)', borderRadius: '12px', border: '2px solid rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white', textDecoration: 'none', boxShadow: '2px 2px 0px rgba(0,0,0,0.12)', transition: 'all 0.2s ease' }}
                    onMouseEnter={e => { e.currentTarget.style.background = 'rgba(74,222,128,0.3)'; e.currentTarget.style.transform = 'translateY(-3px)'; }}
                    onMouseLeave={e => { e.currentTarget.style.background = 'rgba(255,255,255,0.12)'; e.currentTarget.style.transform = 'none'; }}>
                    <i className={`bi bi-${s}`}></i>
                  </a>
                ))}
              </div>
            </div>

            {/* Navegação */}
            <div className="col-lg-2 col-md-6 col-6">
              <h6 style={{ color: '#4ade80', fontWeight: 700, fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '1rem' }}>Navegação</h6>
              {[['/', 'Início'], ['/pontos', 'Pontos de Coleta'], ['/sobre', 'Sobre Nós']].map(([to, label]) => (
                <Link key={to} to={to} style={{ display: 'block', color: 'rgba(255,255,255,0.65)', textDecoration: 'none', fontSize: '0.88rem', marginBottom: '0.6rem', transition: 'color 0.2s ease' }}
                  onMouseEnter={e => e.currentTarget.style.color = '#4ade80'}
                  onMouseLeave={e => e.currentTarget.style.color = 'rgba(255,255,255,0.65)'}>
                  {label}
                </Link>
              ))}
            </div>

            {/* Serviços */}
            <div className="col-lg-2 col-md-6 col-6">
              <h6 style={{ color: '#4ade80', fontWeight: 700, fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '1rem' }}>Serviços</h6>
              {[['/materiais', 'Materiais Recicláveis'], ['/residuos', 'Gestão de Resíduos'], ['/conscientizacao', 'Conscientização'], ['/faq', 'FAQ']].map(([to, label]) => (
                <Link key={to} to={to} style={{ display: 'block', color: 'rgba(255,255,255,0.65)', textDecoration: 'none', fontSize: '0.88rem', marginBottom: '0.6rem', transition: 'color 0.2s ease' }}
                  onMouseEnter={e => e.currentTarget.style.color = '#4ade80'}
                  onMouseLeave={e => e.currentTarget.style.color = 'rgba(255,255,255,0.65)'}>
                  {label}
                </Link>
              ))}
            </div>

            {/* Contato */}
            <div className="col-lg-4 col-md-6">
              <h6 style={{ color: '#4ade80', fontWeight: 700, fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '1rem' }}>Contato</h6>
              {[
                { icon: 'bi-geo-alt-fill',    text: 'São Paulo, SP - Brasil' },
                { icon: 'bi-envelope-fill',   text: 'contato@verdenovo.com.br', href: 'mailto:contato@verdenovo.com.br' },
                { icon: 'bi-telephone-fill',  text: '(11) 99999-9999', href: 'tel:+5511999999999' },
                { icon: 'bi-clock-fill',      text: 'Seg - Sex: 8h às 18h' },
              ].map((item, i) => (
                <div key={i} style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '0.75rem' }}>
                  <div style={{ width: '32px', height: '32px', background: 'rgba(74,222,128,0.2)', borderRadius: '10px', border: '1.5px solid rgba(74,222,128,0.3)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, boxShadow: '2px 2px 0px rgba(0,0,0,0.10)' }}>
                    <i className={`bi ${item.icon}`} style={{ color: '#4ade80', fontSize: '0.8rem' }}></i>
                  </div>
                  {item.href
                    ? <a href={item.href} style={{ color: 'rgba(255,255,255,0.65)', textDecoration: 'none', fontSize: '0.88rem', transition: 'color 0.2s' }}
                        onMouseEnter={e => e.currentTarget.style.color = '#4ade80'}
                        onMouseLeave={e => e.currentTarget.style.color = 'rgba(255,255,255,0.65)'}>{item.text}</a>
                    : <span style={{ color: 'rgba(255,255,255,0.65)', fontSize: '0.88rem' }}>{item.text}</span>
                  }
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Bottom */}
        <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', padding: '1.25rem 2rem' }}>
          <p style={{ margin: 0, color: 'rgba(255,255,255,0.4)', fontSize: '0.82rem', textAlign: 'center' }}>
            © 2024 VerDenovo Soluções Ambientais. Todos os direitos reservados.
          </p>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
