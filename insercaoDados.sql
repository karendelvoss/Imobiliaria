-- 1. Países, Estados e Cidades
INSERT INTO Countries (cdcountry, nmcountry, sgcountry) VALUES (1, 'Brasil', 'BRA');
INSERT INTO States (cdstate, nmstate, sgstate, cdcountry) VALUES (1, 'Santa Catarina', 'SC', 1);
INSERT INTO Cities (cdcity, nmcity, cdstate) VALUES (1, 'Joinville', 1);

-- 2. Bairros
INSERT INTO Districts (cddistrict, nmdistrict, cdcity) VALUES (1, 'Atiradores', 1);
INSERT INTO Districts (cddistrict, nmdistrict, cdcity) VALUES (2, 'Centro', 1);
INSERT INTO Districts (cddistrict, nmdistrict, cdcity) VALUES (3, 'Anita Garibaldi', 1);

-- 3. Endereços
INSERT INTO Addresses (cdaddress, cdzipcode, nmstreet, nraddress, dscomplement, cddistrict) VALUES 
(1, '89201000', 'Rua das Palmeiras', '100', NULL, 2),
(2, '89216200', 'Rua XV de Novembro', '2500', 'Bloco B', 1),
(3, '89203000', 'Rua Otto Boehm', '450', 'Sala 101', 2);

-- 4. Profissões
INSERT INTO Occupations (cdoccupation, nmoccupation) VALUES 
(1, 'Engenheiro de Software'), (2, 'Médico'), (3, 'Corretor de Imóveis');

-- 5. Usuários
INSERT INTO Users (cduser, dtbirth, fgdocument, document, nmuser, nrcellphone, cdaddress, cdoccupation) VALUES 
(1, '1990-05-15', 1, '000.111.222-33', 'Karen Oliveira', '(47) 99999-1111', 1, 1),
(2, '1985-10-20', 1, '444.555.666-77', 'Marta Silva', '(47) 98888-2222', 2, 2),
(3, '1992-03-12', 1, '888.999.000-11', 'Ricardo Santos', '(47) 97777-3333', 3, 3);

-- 6. Dados do Corretor (Broker_Data)
INSERT INTO Broker_Data (nrcreci, cduser) VALUES ('CRECI-SC-12345', 3);

-- 7. Tipos, Finalidades e Status de Imóveis
INSERT INTO Property_Types (cdtype, nmtype) VALUES (1, 'Casa'), (2, 'Apartamento'), (3, 'Sala Comercial');
INSERT INTO Property_Purposes (cdpurpose, nmpurpose) VALUES (1, 'Residencial'), (2, 'Comercial');
INSERT INTO Property_Status (cdstatus, nmstatus) VALUES (1, 'Disponível'), (2, 'Alugado'), (3, 'Vendido');

-- 8. Imóveis
INSERT INTO Properties (cdproperty, nrregistration, dsdescription, vltotalarea_, cdaddress, cdtype, cdpurpose, cdstatus) VALUES 
(1, 'MAT-1001', 'Apartamento mobiliado no Centro', 65.50, 1, 2, 1, 2),
(2, 'MAT-1002', 'Casa com piscina Atiradores', 250.00, 2, 1, 1, 1),
(3, 'MAT-1003', 'Sala comercial reformada', 45.00, 3, 3, 2, 1);

-- 9. Vínculo de Proprietários (N:N)
INSERT INTO Properties_Users (cdproperty, cduser) VALUES (1, 2), (2, 2);

-- 10. Índices e Modelos de Contrato
INSERT INTO Indexes (cdindex, nmindex) VALUES (1, 'IPCA'), (2, 'IGP-M');
INSERT INTO Contract_Templates (cdtemplate, nmtemplate, dsversion, fgactive) VALUES (1, 'Contrato de Locação Residencial', 'V1.0', 'S');

-- 11. Papéis (Roles) e Contrato
INSERT INTO Roles (cdrole, nmrole) VALUES (1, 'Proprietário'), (2, 'Locatário'), (3, 'Fiador'), (4, 'Comprador');

INSERT INTO Contracts (cdcontract, dtcreation, dstitle, cdtemplate, cdproperty, cdindex) VALUES 
(1, '2026-04-20', 'Locação Ap Centro - MAT-1001', 1, 1, 1);

-- 12. Partes do Contrato (N:N:N)
INSERT INTO User_Contract_Contracts_Users_Roles (cdcontract, cduser, cdrole) VALUES 
(1, 2, 1), -- Marta como Proprietária
(1, 1, 2); -- Karen como Locatária

-- 13. Parcelas (Installments)
INSERT INTO Installments (cdinstallment, dtdue, vlbase, vladjusted, cdstatus, dtpayment, nrinstallment, fk_Contracts_cdcontract) VALUES 
(1, '2026-05-10', 2500.00, 2500.00, 1, NULL, 1, 1),
(2, '2026-06-10', 2500.00, 2500.00, 1, NULL, 2, 1);

-- 14. Tópicos de Contrato (Topics)
INSERT INTO Topics (cdtopic, nmtopic) VALUES 
(1, 'Objeto do Contrato'), 
(2, 'Valores e Pagamentos'), 
(3, 'Rescisão e Multas');

-- 15. Cláusulas (Clauses)
INSERT INTO Clauses (cdclause, dstext, cdtopic) VALUES 
(1, 'O presente contrato tem por objeto a locação do imóvel matriculado sob nº MAT-1001.', 1),
(2, 'O valor mensal do aluguel deverá ser pago até o dia 10 de cada mês.', 2);

-- 16. Variáveis de Contrato (Variables)
INSERT INTO Variables (cdvariable, nmvariable, vlvariable, tpvariable, fgtriggeralert, cdcontract) VALUES 
(1, 'Valor_Multa_Rescisao', '5000.00', 'Decimal', true, 1);

-- 17. Contas Bancárias (Bank_Accounts)
-- Vinculando uma conta para a Marta (Proprietária) e uma para você (Karen)
INSERT INTO Bank_Accounts (cdbankaccount, nragency, nraccount, nrpixkey, cduser) VALUES 
(1, '001', '12345-6', '000.111.222-33', 1), -- Sua conta
(2, '033', '98765-4', 'marta.silva@email.com', 2); -- Conta da Marta

-- 18. Comissões (Commissions)
-- Vinculando uma comissão para o Ricardo (Corretor) referente ao contrato 1
INSERT INTO Commissions (cdcommission, vlcommission, dtpayment, cdcontract, cduser) VALUES 
(1, 1500.00, '2026-04-21', 1, 3);

-- 19. Notificações (Notifications)
INSERT INTO Notifications (cdnotification, dsmessage, dtsend, fgread, cdcontract, cduser) VALUES 
(1, 'Seu contrato de locação foi gerado com sucesso!', '2026-04-20', false, 1, 1);

-- 20. Taxas de Índices (Index_Rates)
-- Taxa de exemplo para o IPCA em Abril/2026
INSERT INTO Index_Rates (cdrate, refmonth, refyear, vlrate, fk_Indexes_cdindex) VALUES 
(1, 4, 2026, 0.0045, 1);

-- 21. Tópicos por Modelo de Contrato (Template_Topics)
INSERT INTO Template_Topics_Topics_Contract_Templates (cdtopic, cdtemplate) VALUES 
(1, 1), 
(2, 1), 
(3, 1);