# Compilar
javac -encoding UTF-8 -d bin -cp "dist/lib/*" src/model/*.java src/dao/*.java src/view/*.java


# Rodar
java -cp "bin;dist/lib/*" view.Main


BACK UP BANCO DE DADOS 


-- 1. GEOGRAPHIC HIERARCHY
CREATE TABLE Countries (
    cdcountry SERIAL PRIMARY KEY,
    nmcountry VARCHAR(60) NOT NULL,
    sgcountry CHAR(3) NOT NULL
);

CREATE TABLE States (
    cdstate SERIAL PRIMARY KEY,
    nmstate VARCHAR(50) NOT NULL,
    sgstate CHAR(2) NOT NULL,
    cdcountry INT REFERENCES Countries(cdcountry)
);

CREATE TABLE Cities (
    cdcity SERIAL PRIMARY KEY,
    nmcity VARCHAR(60) NOT NULL,
    cdstate INT REFERENCES States(cdstate)
);

CREATE TABLE Districts (
    cddistrict SERIAL PRIMARY KEY,
    nmdistrict VARCHAR(60) NOT NULL,
    cdcity INT REFERENCES Cities(cdcity)
);

CREATE TABLE Addresses (
    cdaddress SERIAL PRIMARY KEY,
    cdzipcode CHAR(8) NOT NULL,
    nmstreet VARCHAR(100) NOT NULL,
    nraddress VARCHAR(10),
    dscomplement VARCHAR(50),
    cddistrict INT REFERENCES Districts(cddistrict)
);

-- 2. DOMAIN TABLES (Categorias)
CREATE TABLE Occupations (
    cdoccupation SERIAL PRIMARY KEY,
    nmoccupation VARCHAR(100) NOT NULL
);

CREATE TABLE Property_Types (
    cdtype SERIAL PRIMARY KEY,
    nmtype VARCHAR(30) NOT NULL -- House, Apartment, etc.
);

CREATE TABLE Property_Purposes (
    cdpurpose SERIAL PRIMARY KEY,
    nmpurpose VARCHAR(20) NOT NULL -- Residential, Commercial
);

CREATE TABLE Property_Status (
    cdstatus SERIAL PRIMARY KEY,
    nmstatus VARCHAR(30) NOT NULL -- Available, Rented, Sold
);

CREATE TABLE Roles (
    cdrole SERIAL PRIMARY KEY,
    nmrole VARCHAR(50) NOT NULL -- Tenant, Owner, Guarantor
);

CREATE TABLE Indexes (
    cdindex SERIAL PRIMARY KEY,
    nmindex VARCHAR(20) NOT NULL -- IPCA, IGPM
);

-- 3. ENTITIES
CREATE TABLE Users (
    cduser SERIAL PRIMARY KEY,
    dtbirth DATE,
    fgdocument INT, -- 1 para CPF, 2 para CNPJ
    document VARCHAR(20) UNIQUE NOT NULL,
    nmuser VARCHAR(100) NOT NULL,
    nrcellphone VARCHAR(15),
    cdaddress INT REFERENCES Addresses(cdaddress),
    cdoccupation INT REFERENCES Occupations(cdoccupation)
);

CREATE TABLE Broker_Data (
    cduser INT PRIMARY KEY REFERENCES Users(cduser) ON DELETE CASCADE,
    nrcreci VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE Bank_Accounts (
    cdbankaccount SERIAL PRIMARY KEY,
    nragency VARCHAR(30),
    nraccount VARCHAR(30),
    nrpixkey VARCHAR(100),
    cduser INT REFERENCES Users(cduser)
);

CREATE TABLE Properties (
    cdproperty SERIAL PRIMARY KEY,
    nrregistration VARCHAR(30) UNIQUE NOT NULL,
    dsdescription TEXT,
    vltotalarea DECIMAL(10,2),
    cdaddress INT REFERENCES Addresses(cdaddress),
    cdtype INT REFERENCES Property_Types(cdtype),
    cdpurpose INT REFERENCES Property_Purposes(cdpurpose),
    cdstatus INT REFERENCES Property_Status(cdstatus)
);

CREATE TABLE Properties_Users (
    cdproperty INT REFERENCES Properties(cdproperty),
    cduser INT REFERENCES Users(cduser),
    PRIMARY KEY (cdproperty, cduser)
);

-- 4. BUSINESS & FINANCE
CREATE TABLE Contract_Templates (
    cdtemplate SERIAL PRIMARY KEY,
    nmtemplate VARCHAR(100),
    dsversion VARCHAR(10),
    fgactive CHAR(1) DEFAULT 'Y'
);

CREATE TABLE Contracts (
    cdcontract SERIAL PRIMARY KEY,
    dtcreation DATE DEFAULT CURRENT_DATE,
    dstitle VARCHAR(100),
    cdtemplate INT REFERENCES Contract_Templates(cdtemplate),
    cdproperty INT REFERENCES Properties(cdproperty),
    cdindex INT REFERENCES Indexes(cdindex)
);

CREATE TABLE User_Contract (
    cdcontract INT REFERENCES Contracts(cdcontract),
    cduser INT REFERENCES Users(cduser),
    cdrole INT REFERENCES Roles(cdrole),
    PRIMARY KEY (cdcontract, cduser, cdrole)
);

CREATE TABLE Installments (
    cdinstallment SERIAL PRIMARY KEY,
    dtdue DATE NOT NULL,
    vlbase DECIMAL(12,2) NOT NULL,
    vladjusted DECIMAL(12,2),
    cdstatus INT, -- 1: Pending, 2: Paid, 3: Overdue
    dtpayment DATE,
    nrinstallment INT NOT NULL,
    cdcontract INT REFERENCES Contracts(cdcontract) ON DELETE CASCADE
);

CREATE TABLE Index_Rates (
    cdrate SERIAL PRIMARY KEY,
    refmonth INT,
    refyear INT,
    vlrate DECIMAL(10,4),
    cdindex INT REFERENCES Indexes(cdindex)
);

CREATE TABLE Commissions (
    cdcommission SERIAL PRIMARY KEY,
    vlcommission DECIMAL(12,2),
    dtpayment DATE,
    cdcontract INT REFERENCES Contracts(cdcontract),
    cduser INT REFERENCES Users(cduser) -- The Broker
);

-- 1. Categorias Básicas
INSERT INTO Property_Types (nmtype) VALUES ('Casa'), ('Apartamento'), ('Terreno'), ('Galpão');
INSERT INTO Property_Status (nmstatus) VALUES ('Disponível'), ('Alugado'), ('Vendido'), ('Reservado');
INSERT INTO Roles (nmrole) VALUES ('Proprietário'), ('Locatário'), ('Comprador'), ('Fiador');
INSERT INTO Indexes (nmindex) VALUES ('IPCA'), ('IGP-M'), ('SELIC');

-- 2. Hierarquia Geográfica (Brasil)
INSERT INTO Countries (nmcountry, sgcountry) VALUES ('Brasil', 'BRA');
INSERT INTO States (nmstate, sgstate, cdcountry) VALUES ('Santa Catarina', 'SC', 1);
INSERT INTO Cities (nmcity, cdstate) VALUES ('Joinville', 1);
INSERT INTO Districts (nmdistrict, cdcity) VALUES ('Centro', 1), ('Glória', 1), ('Atiradores', 1);

-- 3. Endereço de Exemplo 
INSERT INTO Addresses (cdzipcode, nmstreet, nraddress, dscomplement, cddistrict) 
VALUES ('89201000', 'Rua das Palmeiras', '100', 'Sala 201', 1);

-- 4. Ocupações Comuns
INSERT INTO Occupations (nmoccupation) VALUES ('Engenheiro'), ('Professor'), ('Autônomo'), ('Empresário');

-- 5. Template de Contrato Inicial
INSERT INTO Contract_Templates (nmtemplate, dsversion, fgactive) 
VALUES ('Contrato Padrão de Locação Residencial', '1.0', 'Y');


-- Já temos o Centro (ID 1). Vamos adicionar mais alguns:
INSERT INTO Districts (nmdistrict, cdcity) VALUES 
('Centro', 1),         -- Será ID 1
('Glória', 1),         -- Será ID 2
('Atiradores', 1),     -- Será ID 3
('Anita Garibaldi', 1), -- Será ID 4
('Costa e Silva', 1),   -- Será ID 5
('Santo Antônio', 1),   -- Será ID 6
('Pirabeiraba', 1);    -- Será ID 7

SELECT cdcity, nmdistrict FROM Districts;

-- Endereços diversos
INSERT INTO Addresses (cdzipcode, nmstreet, nraddress, dscomplement, cddistrict) VALUES 
('89203000', 'Rua Ottokar Doerffel', '500', 'Apto 101', 2),
('89218000', 'Rua Otto Pfuetzenreuter', '1200', NULL, 3),
('89221000', 'Rua Dona Francisca', '4500', 'Galpão B', 5);

-- Usuários (Users)
INSERT INTO Users (dtbirth, fgdocument, document, nmuser, nrcellphone, cdaddress, cdoccupation) VALUES 
('1985-05-20', 1, '123.456.789-00', 'João da Silva', '(47) 99999-1111', 1, 4), -- Empresário
('1992-10-15', 1, '987.654.321-11', 'Maria Oliveira', '(47) 98888-2222', 2, 2), -- Professora
('1980-01-30', 1, '555.444.333-22', 'Ricardo Corretor', '(47) 97777-3333', 3, 3); -- Autônomo


-- 1. Defina as Finalidades (Purposes)
INSERT INTO Property_Purposes (nmpurpose) VALUES ('Residencial'), ('Comercial'); 
-- O 'Residencial' será o ID 1

-- 2. Defina os Tipos (Types)
INSERT INTO Property_Types (nmtype) VALUES ('Casa'), ('Apartamento');
-- O 'Apartamento' será o ID 2 (se o ID 1 for Casa)

-- 3. Defina os Status
INSERT INTO Property_Status (nmstatus) VALUES ('Disponível'), ('Alugado');
-- O 'Disponível' será o ID 1

SELECT * FROM Property_Purposes;

INSERT INTO Properties (nrregistration, dsdescription, vltotalarea, cdaddress, cdtype, cdpurpose, cdstatus) 
VALUES ('MAT-1001', 'Apartamento mobiliado no Centro', 65.50, 1, 2, 1, 1);

-- Categorias de Imóveis
INSERT INTO Property_Types (nmtype) VALUES ('Casa'), ('Apartamento'), ('Terreno'), ('Galpão');
-- Finalidades
INSERT INTO Property_Purposes (nmpurpose) VALUES ('Residencial'), ('Comercial');
-- Status do Imóvel
INSERT INTO Property_Status (nmstatus) VALUES ('Disponível'), ('Alugado'), ('Vendido');
-- Papéis no Contrato
INSERT INTO Roles (nmrole) VALUES ('Proprietário'), ('Locatário'), ('Fiador'), ('Corretor');
-- Ocupações
INSERT INTO Occupations (nmoccupation) VALUES ('Engenheiro'), ('Professor'), ('Empresário'), ('Autônomo');

-- Localização (Simplificada para o teste)
INSERT INTO Countries (nmcountry, sgcountry) VALUES ('Brasil', 'BRA');
INSERT INTO States (nmstate, sgstate, cdcountry) VALUES ('Santa Catarina', 'SC', 1);
INSERT INTO Cities (nmcity, cdstate) VALUES ('Joinville', 1);
INSERT INTO Districts (nmdistrict, cdcity) VALUES ('Centro', 1);
INSERT INTO Addresses (cdzipcode, nmstreet, nraddress, cddistrict) VALUES ('89201000', 'Rua das Palmeiras', '100', 1);

-- Usuários (Dono, Inquilino e Corretor)
INSERT INTO Users (nmuser, document, nrcellphone, cdaddress, cdoccupation) VALUES 
('João Silva', '111.111.111-11', '(47) 9999-1111', 1, 3), -- ID 1 (Proprietário)
('Maria Souza', '222.222.222-22', '(47) 9999-2222', 1, 2), -- ID 2 (Locatária)
('Ricardo Corretor', '333.333.333-33', '(47) 9999-3333', 1, 4); -- ID 3 (Corretor)

-- Dados extras do Corretor
INSERT INTO Broker_Data (cduser, nrcreci) VALUES (3, 'CRECI-SC-12345');

-- inserimos o imóvel
INSERT INTO Properties (nrregistration, dsdescription, vltotalarea, cdaddress, cdtype, cdpurpose, cdstatus) 
VALUES ('MAT-1001', 'Apto mobiliado no Centro', 65.5, 1, 2, 1, 1);

INSERT INTO Properties_Users (cdproperty, cduser) VALUES (9, 1);


-- Definindo quem é o quê
INSERT INTO Broker_Data (cduser, nrcreci) VALUES (3, 'CRECI-SC-12345'); -- Ricardo é o corretor
INSERT INTO Properties_Users (cdproperty, cduser) VALUES (9, 1); -- João é dono do imóvel 1 (precisa cadastrar o imóvel antes)

-- Cadastro de alguns imóveis para teste
INSERT INTO Properties (nrregistration, dsdescription, vltotalarea, cdaddress, cdtype, cdpurpose, cdstatus) VALUES 
('MAT-1002', 'Casa com 3 quartos no Anita', 150.00, 2, 1, 1, 2), -- Casa Residencial Alugada
('MAT-1003', 'Galpão Industrial em Pirabeiraba', 450.00, 4, 4, 2, 1); -- Galpão Comercial Disponível

-- Contratos e Financeiro cenário teste

-- 1. O Contrato (Criado há exatamente 1 ano para disparar reajuste)
select * from properties
INSERT INTO Contracts (dtcreation, dstitle, cdtemplate, cdproperty, cdindex) 
VALUES ('2025-04-20', 'Aluguel Residencial - Maria Oliveira', 1, 12, 1); -- Vinculado ao IPCA (ID 1)

-- 2. As Partes do Contrato
SELECT cduser, nmuser FROM Users;
SELECT cdcontract, dstitle FROM Contracts;
INSERT INTO User_Contract (cdcontract, cduser, cdrole) VALUES 
(4, 1, 1), -- João é o Proprietário
(4, 2, 2); -- Maria é a Locatária

-- 3. Histórico de Taxas (Index_Rates) para o IPCA
INSERT INTO Index_Rates (refmonth, refyear, vlrate, cdindex) VALUES 
(4, 2025, 0.0045, 1), -- Taxa de quando o contrato começou
(3, 2026, 0.0052, 1); -- Taxa mais recente para o reajuste de 1 ano

-- 4. Parcelas (Installments)
INSERT INTO Installments (dtdue, vlbase, vladjusted, cdstatus, nrinstallment, cdcontract) VALUES 
('2026-05-10', 2500.00, NULL, 1, 13, 4);

