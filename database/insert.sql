-- 1. Tabelas de Domínio e Localização 
INSERT INTO Countries (nmcountry, sgcountry) 
VALUES ('Brasil', 'BRA');

INSERT INTO States (nmstate, sgstate, cdcountry) 
VALUES ('Santa Catarina', 'SC', 1);

INSERT INTO Cities (nmcity, cdstate) 
VALUES ('Joinville', 1);

INSERT INTO Districts (nmdistrict, cdcity) 
VALUES ('Centro', 1);

INSERT INTO Addresses (cdzipcode, nmstreet, nraddress, dscomplement, cddistrict) 
VALUES ('89200000', 'Rua XV de Novembro', '1000', 'Sala 2', 1);

INSERT INTO Occupations (nmoccupation) 
VALUES ('Analista de Sistemas');

INSERT INTO Roles (nmrole) 
VALUES ('Locatário'), ('Locador');

INSERT INTO Property_Types (nmtype) 
VALUES ('Apartamento');

INSERT INTO Property_Purposes (nmpurpose) 
VALUES ('Residencial');

INSERT INTO Property_Status (nmstatus) 
VALUES ('Alugado'), ('Disponível');

INSERT INTO Indexes (nmindex) 
VALUES ('IGP-M');

-- Ajustado '1' para TRUE
INSERT INTO Contract_Templates (nmtemplate, dsversion, fgactive) 
VALUES ('Contrato de Locação Padrão', '1.0', TRUE);

INSERT INTO Topics (nmtopic) 
VALUES ('Do Objeto da Locação');


-- 2. Entidades Principais (Usuários e Imóveis)

-- Ajustado '1' para TRUE no fgdocument
INSERT INTO Users (dtbirth, fgdocument, document, nmuser, nrcellphone, cdaddress, cdoccupation) 
VALUES ('1990-05-20', TRUE, '12345678900', 'João Silva', '47999999999', 1, 1);

-- Broker_Data movido para cá: Depende de Users existir (Relação 1:1)
-- Aqui INSERIMOS a chave, pois não é auto-incremento, ela vem da tabela Users
INSERT INTO Broker_Data (cduser, nrcreci) 
VALUES (1, 'CRECI-SC-12345');

INSERT INTO Bank_Accounts (nragency, nraccount, nrpixkey, cduser) 
VALUES ('0001', '12345-6', 'joao.silva@email.com', 1);

-- Ajustado o nome vltotalarea (sem o underline no final)
INSERT INTO Properties (nrregistration, dsdescription, vltotalarea, cdaddress, cdtype, cdpurpose, cdstatus) 
VALUES ('MAT-99887', 'Apto com 2 quartos no centro', 65.50, 1, 1, 1, 1);

-- Tabela associativa: inserimos as chaves manualmente
INSERT INTO Properties_Users (cdproperty, cduser) 
VALUES (1, 1);


-- 3. Detalhamentos e Cruzamentos Básicos
INSERT INTO Index_Rates (refmonth, refyear, vlrate, cdindex) 
VALUES (4, 2026, 0.50, 1);

-- Tabela associativa: inserimos as chaves manualmente
INSERT INTO Template_Topics (cdtopic, cdtemplate) 
VALUES (1, 1);

INSERT INTO Clauses (dstext, cdtopic) 
VALUES ('O locador cede ao locatário o imóvel nas condições atuais.', 1);


-- 4. Contratos e Movimentações (Dependem de quase tudo)
INSERT INTO Contracts (dtcreation, dstitle, cdtemplate, cdproperty, cdindex) 
VALUES ('2026-04-22', 'Locação Residencial - João Silva', 1, 1, 1);

-- Tabela associativa: inserimos as chaves manualmente
INSERT INTO User_Contract (cdcontract, cduser, cdrole) 
VALUES (1, 1, 1);

-- fgtriggeralert já estava como TRUE
INSERT INTO Variables (nmvariable, vlvariable, tpvariable, fgtriggeralert, cdcontract) 
VALUES ('valor_aluguel', '1500.00', 1, TRUE, 1);

INSERT INTO Commissions (vlcommission, dtpayment, cdcontract) 
VALUES (150.00, '2026-05-05', 1);

INSERT INTO Installments (dtdue, vlbase, vladjusted, cdstatus, dtpayment, nrinstallment, cdcontract) 
VALUES ('2026-05-10', 1500.00, 1500.00, 1, NULL, 1, 1);

INSERT INTO Notifications (dsmessage, dtsend, fgstatus, tpnotification, cdcontract, cduser) 
VALUES ('Seu contrato foi gerado com sucesso.', '2026-04-22', 0, 1, 1, 1);
