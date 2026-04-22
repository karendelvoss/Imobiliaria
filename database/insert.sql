-- 1. Tabelas de Domínio e Localização (Sem chaves estrangeiras ou dependentes apenas entre si)
INSERT INTO Countries (cdcountry, nmcountry, sgcountry) 
VALUES (1, 'Brasil', 'BRA');

INSERT INTO States (cdstate, nmstate, sgstate, cdcountry) 
VALUES (1, 'Santa Catarina', 'SC', 1);

INSERT INTO Cities (cdcity, nmcity, cdstate) 
VALUES (1, 'Joinville', 1);

INSERT INTO Districts (cddistrict, nmdistrict, cdcity) 
VALUES (1, 'Centro', 1);

INSERT INTO Addresses (cdaddress, cdzipcode, nmstreet, nraddress, dscomplement, cddistrict) 
VALUES (1, '89200000', 'Rua XV de Novembro', '1000', 'Sala 2', 1);

INSERT INTO Occupations (cdoccupation, nmoccupation) 
VALUES (1, 'Analista de Sistemas');

INSERT INTO Roles (cdrole, nmrole) 
VALUES (1, 'Locatário'), (2, 'Locador');

INSERT INTO Property_Types (cdtype, nmtype) 
VALUES (1, 'Apartamento');

INSERT INTO Property_Purposes (cdpurpose, nmpurpose) 
VALUES (1, 'Residencial');

INSERT INTO Property_Status (cdstatus, nmstatus) 
VALUES (1, 'Alugado'), (2, 'Disponível');

INSERT INTO Indexes (cdindex, nmindex) 
VALUES (1, 'IGP-M');

INSERT INTO Contract_Templates (cdtemplate, nmtemplate, dsversion, fgactive) 
VALUES (1, 'Contrato de Locação Padrão', '1.0', '1');

INSERT INTO Topics (cdtopic, nmtopic) 
VALUES (1, 'Do Objeto da Locação');

INSERT INTO Broker_Data (nrcreci) 
VALUES ('CRECI-SC-12345');


-- 2. Entidades Principais (Usuários e Imóveis)
INSERT INTO Users (cduser, dtbirth, fgdocument, document, nmuser, nrcellphone, cdaddress, cdoccupation) 
VALUES (1, '1990-05-20', 1, '12345678900', 'João Silva', '47999999999', 1, 1);

INSERT INTO Bank_Accounts (cdbankaccount, nragency, nraccount, nrpixkey, cduser) 
VALUES (1, '0001', '12345-6', 'joao.silva@email.com', 1);

INSERT INTO Properties (cdproperty, nrregistration, dsdescription, vltotalarea_, cdaddress, cdtype, cdpurpose, cdstatus) 
VALUES (1, 'MAT-99887', 'Apto com 2 quartos no centro', 65.50, 1, 1, 1, 1);

INSERT INTO Properties_Users (cdproperty, cduser) 
VALUES (1, 1);


-- 3. Detalhamentos e Cruzamentos Básicos
INSERT INTO Index_Rates (cdrate, refmonth, refyear, vlrate, cdindex) 
VALUES (1, 4, 2026, 0.50, 1);

INSERT INTO Template_Topics (cdtopic, cdtemplate) 
VALUES (1, 1);

INSERT INTO Clauses (cdclause, dstext, cdtopic) 
VALUES (1, 'O locador cede ao locatário o imóvel nas condições atuais.', 1);


-- 4. Contratos e Movimentações (Dependem de quase tudo)
INSERT INTO Contracts (cdcontract, dtcreation, dstitle, cdtemplate, cdproperty, cdindex) 
VALUES (1, '2026-04-22', 'Locação Residencial - João Silva', 1, 1, 1);

INSERT INTO User_Contract (cdcontract, cduser, cdrole) 
VALUES (1, 1, 1);

INSERT INTO Variables (cdvariable, nmvariable, vlvariable, tpvariable, fgtriggeralert, cdcontract) 
VALUES (1, 'valor_aluguel', '1500.00', 1, TRUE, 1);

INSERT INTO Commissions (cdcommission, vlcommission, dtpayment, cdcontract) 
VALUES (1, 150.00, '2026-05-05', 1);

INSERT INTO Installments (cdinstallment, dtdue, vlbase, vladjusted, cdstatus, dtpayment, nrinstallment, cdcontract) 
VALUES (1, '2026-05-10', 1500.00, 1500.00, 1, NULL, 1, 1);

INSERT INTO Notifications (cdnotification, dsmessage, dtsend, fgstatus, tpnotification, cdcontract, cduser) 
VALUES (1, 'Seu contrato foi gerado com sucesso.', '2026-04-22', 0, 1, 1, 1);