-- Ajustes para bancos SQL Server antigos do VerDenovo.
-- Execute este script apenas se voce ja possui tabelas criadas por versoes anteriores.

IF COL_LENGTH('Usuario', 'nivel_acesso') IS NULL
    ALTER TABLE Usuario ADD nivel_acesso VARCHAR(10) NULL;

IF COL_LENGTH('Usuario', 'nivelAcesso') IS NOT NULL
    EXEC('UPDATE Usuario SET nivel_acesso = nivelAcesso WHERE nivel_acesso IS NULL');

IF COL_LENGTH('Usuario', 'data_cadastro') IS NULL
    ALTER TABLE Usuario ADD data_cadastro DATETIME2 NULL;

IF COL_LENGTH('Usuario', 'dataCadastro') IS NOT NULL
    EXEC('UPDATE Usuario SET data_cadastro = dataCadastro WHERE data_cadastro IS NULL');

IF COL_LENGTH('Usuario', 'status_usuario') IS NULL
    ALTER TABLE Usuario ADD status_usuario VARCHAR(20) NULL;

IF COL_LENGTH('Usuario', 'statusUsuario') IS NOT NULL
    EXEC('UPDATE Usuario SET status_usuario = statusUsuario WHERE status_usuario IS NULL');

UPDATE Usuario SET status_usuario = 'ATIVO' WHERE status_usuario IS NULL;

IF COL_LENGTH('Usuario', 'reset_token') IS NULL
    ALTER TABLE Usuario ADD reset_token VARCHAR(100) NULL;

IF COL_LENGTH('Usuario', 'reset_token_expiry') IS NULL
    ALTER TABLE Usuario ADD reset_token_expiry DATETIME2 NULL;

IF COL_LENGTH('Usuario', 'reset_code') IS NULL
    ALTER TABLE Usuario ADD reset_code VARCHAR(6) NULL;

IF COL_LENGTH('Ponto', 'senha') IS NULL
    ALTER TABLE Ponto ADD senha VARCHAR(100) NULL;

IF COL_LENGTH('Ponto', 'descricao') IS NULL
    ALTER TABLE Ponto ADD descricao VARCHAR(500) NULL;

IF COL_LENGTH('Ponto', 'logradouro') IS NULL
    ALTER TABLE Ponto ADD logradouro VARCHAR(100) NULL;

IF COL_LENGTH('Ponto', 'bairro') IS NULL
    ALTER TABLE Ponto ADD bairro VARCHAR(80) NULL;

IF COL_LENGTH('Ponto', 'cidade') IS NULL
    ALTER TABLE Ponto ADD cidade VARCHAR(80) NULL;

IF COL_LENGTH('Ponto', 'estado') IS NULL
    ALTER TABLE Ponto ADD estado VARCHAR(2) NULL;

IF COL_LENGTH('Ponto', 'cnpj') IS NULL
    ALTER TABLE Ponto ADD cnpj VARCHAR(14) NULL;

IF COL_LENGTH('Ponto', 'status_verificacao') IS NULL
    ALTER TABLE Ponto ADD status_verificacao VARCHAR(30) NULL;

IF COL_LENGTH('Ponto', 'motivo_verificacao') IS NULL
    ALTER TABLE Ponto ADD motivo_verificacao VARCHAR(500) NULL;

IF COL_LENGTH('Ponto', 'data_verificacao') IS NULL
    ALTER TABLE Ponto ADD data_verificacao DATETIME2 NULL;

IF COL_LENGTH('Ponto', 'fonte_verificacao') IS NULL
    ALTER TABLE Ponto ADD fonte_verificacao VARCHAR(50) NULL;

IF COL_LENGTH('Ponto', 'hora_funcionamento') IS NULL
    ALTER TABLE Ponto ADD hora_funcionamento VARCHAR(200) NULL;

IF COL_LENGTH('Ponto', 'horaFuncionamento') IS NOT NULL
    EXEC('UPDATE Ponto SET hora_funcionamento = horaFuncionamento WHERE hora_funcionamento IS NULL');

IF COL_LENGTH('Ponto', 'data_cadastro') IS NULL
    ALTER TABLE Ponto ADD data_cadastro DATETIME2 NULL;

IF COL_LENGTH('Ponto', 'dataCadastro') IS NOT NULL
    EXEC('UPDATE Ponto SET data_cadastro = dataCadastro WHERE data_cadastro IS NULL');

IF COL_LENGTH('Ponto', 'status_ponto') IS NULL
    ALTER TABLE Ponto ADD status_ponto VARCHAR(20) NULL;

IF COL_LENGTH('Ponto', 'statusPonto') IS NOT NULL
    EXEC('UPDATE Ponto SET status_ponto = statusPonto WHERE status_ponto IS NULL');

UPDATE Ponto SET status_ponto = 'ATIVO' WHERE status_ponto IS NULL;

IF COL_LENGTH('Ponto', 'usuario_id') IS NULL
    ALTER TABLE Ponto ADD usuario_id BIGINT NULL;

IF COL_LENGTH('Ponto', 'reset_token') IS NULL
    ALTER TABLE Ponto ADD reset_token VARCHAR(100) NULL;

IF COL_LENGTH('Ponto', 'reset_token_expiry') IS NULL
    ALTER TABLE Ponto ADD reset_token_expiry DATETIME2 NULL;

IF COL_LENGTH('Ponto', 'reset_code') IS NULL
    ALTER TABLE Ponto ADD reset_code VARCHAR(6) NULL;

-- Bancos antigos tinham categoria_id obrigatorio, mas o backend atual nao usa Categoria em Ponto.
IF COL_LENGTH('Ponto', 'categoria_id') IS NOT NULL
    ALTER TABLE Ponto ALTER COLUMN categoria_id INT NULL;
