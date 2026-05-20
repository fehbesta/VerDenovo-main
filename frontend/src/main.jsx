import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import { apiService } from './services/api'
import './index.css'
import './animations.css'
import App from './App.jsx'

apiService.restoreSession();

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)