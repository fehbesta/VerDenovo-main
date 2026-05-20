CREATE DATABASE VerdNovo;
GO

USE VerdNovo;
GO

CREATE TABLE Usuario
(
    id BIGINT IDENTITY(1,1) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    nivel_acesso VARCHAR(10) NULL,
    data_cadastro DATETIME2 NULL,
    status_usuario VARCHAR(20) NOT NULL,
    reset_token VARCHAR(100) NULL,
    reset_token_expiry DATETIME2 NULL,
    reset_code VARCHAR(6) NULL,

    CONSTRAINT PK_Usuario PRIMARY KEY (id)
);
GO

CREATE TABLE Categoria
(
    id BIGINT IDENTITY(1,1) NOT NULL,
    nome VARCHAR(50) NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    statusCategoria VARCHAR(20) NOT NULL,

    CONSTRAINT PK_Categoria PRIMARY KEY (id)
);
GO

CREATE TABLE Ponto
(
    id BIGINT IDENTITY(1,1) NOT NULL,
    nome VARCHAR(50) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    complemento VARCHAR(50) NULL,
    telefone VARCHAR(20) NULL,
    email VARCHAR(50) NULL,
    hora_funcionamento VARCHAR(200) NOT NULL,
    material VARCHAR(400) NOT NULL,
    senha VARCHAR(100) NOT NULL,
    data_cadastro DATETIME2 NULL,
    status_ponto VARCHAR(20) NOT NULL,
    descricao VARCHAR(500) NULL,
    logradouro VARCHAR(100) NULL,
    usuario_id BIGINT NULL,
    reset_token VARCHAR(100) NULL,
    reset_token_expiry DATETIME2 NULL,
    reset_code VARCHAR(6) NULL,

    CONSTRAINT PK_Ponto PRIMARY KEY (id),
    CONSTRAINT FK_Ponto_Usuario FOREIGN KEY (usuario_id) REFERENCES Usuario (id)
);
GO

INSERT INTO Categoria (nome, descricao, statusCategoria) VALUES
('Geral', 'Categoria padrao para pontos de coleta', 'ATIVO');
GO
