import { database } from '../services/database';

export const excluirPontoPorNome = (nome) => {
  const pontos = database.listarPontos();
  const ponto = pontos.find(p => p.nome && p.nome.toLowerCase().includes(nome.toLowerCase()));
  if (ponto) {
    database.excluirPonto(ponto.id);
    return true;
  }
  return false;
};