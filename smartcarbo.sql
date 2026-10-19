
IF DB_ID(N'smartcarbo') IS NULL
BEGIN
    CREATE DATABASE smartcarbo;
END;
GO

USE smartcarbo;
GO

-- Tabela de usuários
IF OBJECT_ID(N'dbo.usuario', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.usuario
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        nome NVARCHAR(255) NULL,
        email NVARCHAR(255) NULL,
        senha NVARCHAR(255) NULL,
        dataNascimento DATE NULL,
        altura FLOAT NULL,
        peso FLOAT NULL,
        sexo NVARCHAR(255) NULL,
        objetivo NVARCHAR(255) NULL,
        nivelAtividade NVARCHAR(255) NULL,
        restricoes NVARCHAR(255) NULL,
        condicaoSaude NVARCHAR(255) NULL,
        avaliacaoAlimentacao INT NULL,
        CONSTRAINT PK_usuario PRIMARY KEY (id)
    );
END;
GO

-- Tabela de refeições
IF OBJECT_ID(N'dbo.Refeicao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Refeicao
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        nome NVARCHAR(255) NOT NULL,
        descricao NVARCHAR(500) NULL,
        dataRefeicao DATE NOT NULL,
        usuarioId BIGINT NOT NULL,
        CONSTRAINT PK_Refeicao PRIMARY KEY (id),
        CONSTRAINT FK_Refeicao_Usuario
            FOREIGN KEY (usuarioId) REFERENCES dbo.usuario(id)
    );
END;
GO

-- Tabela de registros alimentares
IF OBJECT_ID(N'dbo.RegistroAlimentacao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.RegistroAlimentacao
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        nomeAlimento NVARCHAR(255) NULL,
        quantidade FLOAT NULL,
        tipoRefeicao NVARCHAR(255) NULL,
        calorias FLOAT NULL,
        carboidratos FLOAT NULL,
        proteinas FLOAT NULL,
        gorduras FLOAT NULL,
        fibras FLOAT NULL,
        sodio FLOAT NULL,
        dataHora DATETIME2 NULL,
        usuarioId BIGINT NULL,
        refeicaoId BIGINT NULL,
        CONSTRAINT PK_RegistroAlimentacao PRIMARY KEY (id),
        CONSTRAINT FK_RegistroAlimentacao_Usuario
            FOREIGN KEY (usuarioId) REFERENCES dbo.usuario(id),
        CONSTRAINT FK_RegistroAlimentacao_Refeicao
            FOREIGN KEY (refeicaoId) REFERENCES dbo.Refeicao(id)
    );
END;
GO

-- Tabela de atividades físicas (catálogo de atividades)
IF OBJECT_ID(N'dbo.atividade_fisica', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.atividade_fisica
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        nome NVARCHAR(255) NULL,
        descricao NVARCHAR(255) NULL,
        duracao INT NULL,
        calorias INT NULL,
        CONSTRAINT PK_atividade_fisica PRIMARY KEY (id)
    );
END;
GO

-- Tabela de histórico de peso
IF OBJECT_ID(N'dbo.HistoricoPeso', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HistoricoPeso
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        usuarioId BIGINT NOT NULL,
        peso FLOAT NOT NULL,
        data DATE NOT NULL,
        CONSTRAINT PK_HistoricoPeso PRIMARY KEY (id),
        CONSTRAINT CK_HistoricoPeso_Peso
            CHECK (peso >= 20 AND peso <= 400),
        CONSTRAINT FK_HistoricoPeso_Usuario
            FOREIGN KEY (usuarioId) REFERENCES dbo.usuario(id)
    );
END;
GO

-- Tabela de registros de consumo de água
IF OBJECT_ID(N'dbo.RegistroAgua', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.RegistroAgua
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        usuarioId BIGINT NOT NULL,
        quantidadeMl INT NOT NULL,
        dataHora DATETIME2 NOT NULL
            CONSTRAINT DF_RegistroAgua_DataHora DEFAULT SYSDATETIME(),
        CONSTRAINT PK_RegistroAgua PRIMARY KEY (id),
        CONSTRAINT CK_RegistroAgua_Quantidade
            CHECK (quantidadeMl BETWEEN 1 AND 5000),
        CONSTRAINT FK_RegistroAgua_Usuario
            FOREIGN KEY (usuarioId) REFERENCES dbo.usuario(id)
    );
END;
GO

-- Índices para consultas frequentes e integridade dos dados
IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'UX_HistoricoPeso_Usuario_Data'
      AND object_id = OBJECT_ID(N'dbo.HistoricoPeso')
)
BEGIN
    CREATE UNIQUE INDEX UX_HistoricoPeso_Usuario_Data
        ON dbo.HistoricoPeso (usuarioId, data);
END;
GO

IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_RegistroAgua_Usuario_DataHora'
      AND object_id = OBJECT_ID(N'dbo.RegistroAgua')
)
BEGIN
    CREATE INDEX IX_RegistroAgua_Usuario_DataHora
        ON dbo.RegistroAgua (usuarioId, dataHora);
END;
GO

-- Índice único filtrado para evitar e-mails duplicados preenchidos.
-- Valores NULL não entram no índice; validação de e-mail continua no backend.
IF NOT EXISTS
(
    SELECT 1 FROM sys.indexes
    WHERE name = N'UX_usuario_email'
      AND object_id = OBJECT_ID(N'dbo.usuario')
)
BEGIN
    CREATE UNIQUE INDEX UX_usuario_email
        ON dbo.usuario (email)
        WHERE email IS NOT NULL;
END;
GO

-- Tabelas da Comunidade (publicações, curtidas e pessoas seguidas)
IF OBJECT_ID(N'dbo.PublicacaoComunidade', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.PublicacaoComunidade
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        usuarioId BIGINT NOT NULL,
        texto NVARCHAR(500) NULL,
        imagem NVARCHAR(MAX) NULL,
        categoria NVARCHAR(80) NOT NULL,
        criadoEm DATETIME2 NOT NULL CONSTRAINT DF_PublicacaoComunidade_CriadoEm DEFAULT SYSDATETIME(),
        CONSTRAINT PK_PublicacaoComunidade PRIMARY KEY (id),
        CONSTRAINT FK_PublicacaoComunidade_Usuario FOREIGN KEY (usuarioId) REFERENCES dbo.usuario(id),
        CONSTRAINT CK_PublicacaoComunidade_Conteudo CHECK (LEN(LTRIM(RTRIM(ISNULL(texto, N'')))) > 0 OR imagem IS NOT NULL)
    );
END;
GO

IF OBJECT_ID(N'dbo.CurtidaPublicacao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.CurtidaPublicacao
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        publicacaoId BIGINT NOT NULL,
        usuarioId BIGINT NOT NULL,
        criadoEm DATETIME2 NOT NULL CONSTRAINT DF_CurtidaPublicacao_CriadoEm DEFAULT SYSDATETIME(),
        CONSTRAINT PK_CurtidaPublicacao PRIMARY KEY (id),
        CONSTRAINT FK_CurtidaPublicacao_Publicacao FOREIGN KEY (publicacaoId) REFERENCES dbo.PublicacaoComunidade(id),
        CONSTRAINT FK_CurtidaPublicacao_Usuario FOREIGN KEY (usuarioId) REFERENCES dbo.usuario(id)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UX_CurtidaPublicacao_Publicacao_Usuario' AND object_id = OBJECT_ID(N'dbo.CurtidaPublicacao'))
BEGIN
    CREATE UNIQUE INDEX UX_CurtidaPublicacao_Publicacao_Usuario ON dbo.CurtidaPublicacao (publicacaoId, usuarioId);
END;
GO

IF OBJECT_ID(N'dbo.SeguimentoUsuario', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.SeguimentoUsuario
    (
        id BIGINT IDENTITY(1,1) NOT NULL,
        seguidorId BIGINT NOT NULL,
        seguidoId BIGINT NOT NULL,
        criadoEm DATETIME2 NOT NULL CONSTRAINT DF_SeguimentoUsuario_CriadoEm DEFAULT SYSDATETIME(),
        CONSTRAINT PK_SeguimentoUsuario PRIMARY KEY (id),
        CONSTRAINT FK_SeguimentoUsuario_Seguidor FOREIGN KEY (seguidorId) REFERENCES dbo.usuario(id),
        CONSTRAINT FK_SeguimentoUsuario_Seguido FOREIGN KEY (seguidoId) REFERENCES dbo.usuario(id),
        CONSTRAINT CK_SeguimentoUsuario_NaoSeguirASi CHECK (seguidorId <> seguidoId)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UX_SeguimentoUsuario_Seguidor_Seguido' AND object_id = OBJECT_ID(N'dbo.SeguimentoUsuario'))
BEGIN
    CREATE UNIQUE INDEX UX_SeguimentoUsuario_Seguidor_Seguido ON dbo.SeguimentoUsuario (seguidorId, seguidoId);
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_PublicacaoComunidade_CriadoEm' AND object_id = OBJECT_ID(N'dbo.PublicacaoComunidade'))
BEGIN
    CREATE INDEX IX_PublicacaoComunidade_CriadoEm ON dbo.PublicacaoComunidade (criadoEm DESC);
END;
GO

-- Conferência: tabelas do banco
SELECT TABLE_SCHEMA AS Esquema, TABLE_NAME AS Tabela
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = 'dbo'
ORDER BY TABLE_NAME;
GO

-- Conferência: chaves estrangeiras e relacionamentos
SELECT
    fk.name AS NomeRelacionamento,
    OBJECT_NAME(fk.parent_object_id) AS TabelaOrigem,
    COL_NAME(fkc.parent_object_id, fkc.parent_column_id) AS ColunaOrigem,
    OBJECT_NAME(fk.referenced_object_id) AS TabelaDestino,
    COL_NAME(fkc.referenced_object_id, fkc.referenced_column_id) AS ColunaDestino
FROM sys.foreign_keys AS fk
INNER JOIN sys.foreign_key_columns AS fkc
    ON fk.object_id = fkc.constraint_object_id
ORDER BY TabelaOrigem, NomeRelacionamento;
GO
