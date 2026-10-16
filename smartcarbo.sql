CREATE DATABASE smartcarbo;
GO


USE smartcarbo;
GO

-- verificar tabelas existentes
SELECT
    TABLE_SCHEMA,
    TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_SCHEMA, TABLE_NAME;

-- Tabela de usuários
IF OBJECT_ID('dbo.usuario', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.usuario (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nome NVARCHAR(255),
        email NVARCHAR(255),
        senha NVARCHAR(255),
        dataNascimento DATE,
        altura FLOAT,
        peso FLOAT,
        sexo NVARCHAR(255),
        objetivo NVARCHAR(255),
        nivelAtividade NVARCHAR(255),
        restricoes NVARCHAR(255),
        condicaoSaude NVARCHAR(255),
        avaliacaoAlimentacao INT
    );
END;
GO

-- Tabela de registros de alimentação
IF OBJECT_ID('dbo.RegistroAlimentacao', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.RegistroAlimentacao (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nomeAlimento NVARCHAR(255),
        quantidade FLOAT,
        tipoRefeicao NVARCHAR(255),
        calorias FLOAT,
        carboidratos FLOAT,
        proteinas FLOAT,
        gorduras FLOAT,
        fibras FLOAT,
        sodio FLOAT,
        dataHora DATETIME2,
        usuarioId BIGINT
    );
END;
GO

-- Tabela de atividades físicas
IF OBJECT_ID('dbo.atividade_fisica', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.atividade_fisica (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nome NVARCHAR(255),
        descricao NVARCHAR(255),
        duracao INT,
        calorias INT
    );
END;
GO

USE smartcarbo;
GO

IF OBJECT_ID('dbo.Refeicao', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Refeicao (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nome NVARCHAR(255) NOT NULL,
        descricao NVARCHAR(500) NULL,
        dataRefeicao DATE NOT NULL,
        usuarioId BIGINT NOT NULL,

        CONSTRAINT FK_Refeicao_Usuario
            FOREIGN KEY (usuarioId)
            REFERENCES dbo.usuario(id)
    );
END;
GO

-- Conferir as tabelas criadas
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;

-- Usuário salvo
USE smartcarbo;
GO

SELECT
    id,
    nome,
    email,
    dataNascimento,
    altura,
    peso
FROM dbo.usuario
WHERE email = 'teste.smartcarbo@example.com';

-- verificar
USE smartcarbo;

SELECT id, nome, email, peso
FROM dbo.usuario
WHERE id = 1;

USE smartcarbo;
GO

SELECT TABLE_SCHEMA, TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
GO


USE smartcarbo;
GO

SELECT
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'dbo'
  AND TABLE_NAME = 'RegistroAlimentacao'
ORDER BY ORDINAL_POSITION;
GO


USE smartcarbo;
GO

SELECT
    id,
    nomeAlimento,
    quantidade,
    tipoRefeicao,
    calorias,
    dataHora,
    usuarioId
FROM dbo.RegistroAlimentacao
ORDER BY id DESC;
GO


USE smartcarbo;
GO

SELECT
    id,
    nomeAlimento,
    quantidade,
    tipoRefeicao,
    calorias,
    dataHora,
    usuarioId
FROM dbo.RegistroAlimentacao
ORDER BY id DESC;
GO

USE smartcarbo;
GO

SELECT id, nome, email
FROM dbo.usuario
ORDER BY id;
GO

USE smartcarbo;
GO

SELECT *
FROM dbo.RegistroAlimentacao
GO

USE smartcarbo;
GO

SELECT
    id,
    nomeAlimento,
    quantidade,
    tipoRefeicao,
    calorias,
    dataHora,
    usuarioId
FROM dbo.RegistroAlimentacao
ORDER BY id DESC;
GO

USE smartcarbo;
GO

SELECT *
FROM dbo.RegistroAlimentacao
GO

USE smartcarbo;
GO

SELECT
    id,
    nomeAlimento,
    tipoRefeicao,
    usuarioId
FROM dbo.RegistroAlimentacao
ORDER BY id DESC;
GO

SELECT *
FROM dbo.RegistroAlimentacao
WHERE id IN (22, 23, 24);
GO

USE smartcarbo;
GO

SELECT
    id,
    nomeAlimento,
    tipoRefeicao,
    usuarioId
FROM dbo.RegistroAlimentacao
WHERE tipoRefeicao = 'teste'
ORDER BY id DESC;
GO

USE smartcarbo;
GO

SELECT
    TABLE_SCHEMA,
    TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_NAME IN ('usuario', 'Refeicao', 'RegistroAlimentacao');
GO

SELECT
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'usuario'
ORDER BY ORDINAL_POSITION;
GO


USE smartcarbo;
GO

SELECT
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN ('usuario', 'Refeicao', 'RegistroAlimentacao')
ORDER BY TABLE_NAME, ORDINAL_POSITION;
GO


USE smartcarbo;
GO

SELECT
    OBJECT_NAME(fk.parent_object_id) AS Tabela,
    COL_NAME(
        fkc.parent_object_id,
        fkc.parent_column_id
    ) AS Coluna,
    OBJECT_NAME(fk.referenced_object_id) AS TabelaReferenciada,
    COL_NAME(
        fkc.referenced_object_id,
        fkc.referenced_column_id
    ) AS ColunaReferenciada
FROM sys.foreign_keys fk
JOIN sys.foreign_key_columns fkc
    ON fk.object_id = fkc.constraint_object_id
WHERE OBJECT_NAME(fk.parent_object_id)
    IN ('Refeicao', 'RegistroAlimentacao');
GO


USE smartcarbo;
GO

IF COL_LENGTH('dbo.RegistroAlimentacao', 'refeicaoId') IS NULL
BEGIN
    ALTER TABLE dbo.RegistroAlimentacao
    ADD refeicaoId BIGINT NULL;
END;
GO

USE smartcarbo;
GO

SELECT
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'RegistroAlimentacao'
  AND COLUMN_NAME = 'refeicaoId';
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_RegistroAlimentacao_Refeicao'
)
BEGIN
    ALTER TABLE dbo.RegistroAlimentacao
    ADD CONSTRAINT FK_RegistroAlimentacao_Refeicao
        FOREIGN KEY (refeicaoId)
        REFERENCES dbo.Refeicao(id);
END;
GO

USE smartcarbo;

CREATE TABLE dbo.HistoricoPeso (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    usuarioId BIGINT NOT NULL,
    peso DECIMAL(5,2) NOT NULL,
    data DATE NOT NULL,

    CONSTRAINT CK_HistoricoPeso_Peso
        CHECK (peso >= 20 AND peso <= 400),

    CONSTRAINT FK_HistoricoPeso_Usuario
        FOREIGN KEY (usuarioId)
        REFERENCES dbo.usuario(id)
);

CREATE INDEX IX_HistoricoPeso_Usuario_Data
    ON dbo.HistoricoPeso (usuarioId, data);

SELECT *
FROM dbo.HistoricoPeso


CREATE TABLE dbo.RegistroAgua (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    usuarioId BIGINT NOT NULL,
    quantidadeMl INT NOT NULL,
    dataHora DATETIME2 NOT NULL
        CONSTRAINT DF_RegistroAgua_DataHora
        DEFAULT SYSDATETIME(),

    CONSTRAINT CK_RegistroAgua_Quantidade
        CHECK (quantidadeMl BETWEEN 1 AND 5000),

    CONSTRAINT FK_RegistroAgua_Usuario
        FOREIGN KEY (usuarioId)
        REFERENCES dbo.usuario(id)
);

CREATE INDEX IX_RegistroAgua_Usuario_DataHora
    ON dbo.RegistroAgua (usuarioId, dataHora);

SELECT *
FROM dbo.RegistroAgua