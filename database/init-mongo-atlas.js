// database/init-mongo-atlas.js
// Script de inserção de dados no MongoDB Atlas para o sistema Imobiliária
//
// Executar via:
//   mongosh "mongodb+srv://delvossribas:AemBsxTH1hEuFrHT@imobiliaria.f7x4ou9.mongodb.net/imobiliaria" database/init-mongo-atlas.js
//
// ATENÇÃO: Este script APAGA o banco existente antes de recriar!

const DB_NAME = "imobiliaria";
db = db.getSiblingDB(DB_NAME);
print("Dropando banco existente...");
db.dropDatabase();
db = db.getSiblingDB(DB_NAME);
print("Banco '" + DB_NAME + "' recriado.");

// =============================================================================
// 1. COLEÇÕES E ÍNDICES
// =============================================================================
print("Criando coleções e índices...");
db.createCollection("users");
db.createCollection("properties");
db.createCollection("contracts");
db.createCollection("notifications");
db.createCollection("contract_templates");
db.createCollection("indexes");
db.createCollection("readjustment_logs");
db.createCollection("counters");

db.users.createIndex({ "document": 1 }, { unique: true });
db.users.createIndex({ "nmuser": 1 });
db.properties.createIndex({ "nrregistration": 1 }, { unique: true });
db.properties.createIndex({ "address.district": 1 });
db.properties.createIndex({ "status": 1 });
db.contracts.createIndex({ "cdproperty": 1 });
db.contracts.createIndex({ "cdstatus": 1 });
db.contracts.createIndex({ "dtlimit": 1 });
db.contracts.createIndex({ "participants.cduser": 1 });
db.contracts.createIndex({ "installments.dtdue": 1 });
db.contracts.createIndex({ "installments.cdstatus": 1 });
db.notifications.createIndex({ "cdcontract": 1 });
db.notifications.createIndex({ "cduser": 1 });
db.notifications.createIndex({ "dtsend": -1 });
print("OK.");

// =============================================================================
// 2. CONTADORES (sequences)
// =============================================================================
print("Inicializando contadores...");
db.counters.insertMany([
    { _id: "users", seq: 18 },
    { _id: "properties", seq: 1 },
    { _id: "contracts", seq: 20 },
    { _id: "notifications", seq: 11 },
    { _id: "contract_templates", seq: 1 },
    { _id: "indexes", seq: 2 },
    { _id: "installments", seq: 81 },
    { _id: "readjustment_logs", seq: 25 },
    { _id: "index_rates", seq: 49 }
]);
print("OK.");

// =============================================================================
// 3. USERS (endereço + profissão embarcados)
// =============================================================================
print("Inserindo usuários...");
const addr = { cdzipcode: "89200000", nmstreet: "Rua XV de Novembro", nraddress: "1000", dscomplement: "Sala 2", district: "Centro", city: "Joinville", state: "SC", country: "Brasil" };

db.users.insertMany([
    { _id: 1, nmuser: "João Silva", dtbirth: "1990-05-20", fgdocument: true, document: "12345678900", nrcellphone: "47999999999", dsissuingbody: "SSP", address: addr, occupation: "Analista de Sistemas", bank_accounts: [{ nragency: "0001", nraccount: "12345-6", nrpixkey: "joao.silva@email.com" }] },
    { _id: 2, nmuser: "Kauane", dtbirth: "2004-08-30", fgdocument: true, document: "12435611955", nrcellphone: "47 9956-8956", dsissuingbody: "SSP", address: addr, occupation: "Analista de Sistemas", bank_accounts: [{ nragency: "2564", nraccount: "25999874", nrpixkey: "2558996589" }] },
    { _id: 3, nmuser: "Locador Job1 ContratoA", dtbirth: "1975-03-10", fgdocument: true, document: "11111111101", nrcellphone: "47991110001", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 4, nmuser: "Locatario Job1 ContratoA VenceEm7Dias", dtbirth: "1990-07-20", fgdocument: true, document: "11111111102", nrcellphone: "47991110002", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 5, nmuser: "Locador Job1 ContratoB", dtbirth: "1968-11-05", fgdocument: true, document: "11111111103", nrcellphone: "47991110003", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 6, nmuser: "Locatario Job1 ContratoB VenceHoje", dtbirth: "1995-01-15", fgdocument: true, document: "11111111104", nrcellphone: "47991110004", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 7, nmuser: "Locador Job2 ContratoA PagamentoRecebido", dtbirth: "1970-06-22", fgdocument: true, document: "22222222201", nrcellphone: "47992220001", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 8, nmuser: "Locatario Job2 ContratoA PagouHoje", dtbirth: "1988-09-30", fgdocument: true, document: "22222222202", nrcellphone: "47992220002", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 9, nmuser: "Locador Job2 ContratoB PagamentoRecebido", dtbirth: "1965-04-18", fgdocument: true, document: "22222222203", nrcellphone: "47992220003", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 10, nmuser: "Locatario Job2 ContratoB PagouHoje", dtbirth: "1993-12-01", fgdocument: true, document: "22222222204", nrcellphone: "47992220004", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 11, nmuser: "Locador Job3 ContratoA AniversarioAbril", dtbirth: "1960-08-14", fgdocument: true, document: "33333333301", nrcellphone: "47993330001", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 12, nmuser: "Locatario Job3 ContratoA AniversarioAbril", dtbirth: "1985-05-25", fgdocument: true, document: "33333333302", nrcellphone: "47993330002", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 13, nmuser: "Locador Job3 ContratoB AniversarioAbril", dtbirth: "1972-02-28", fgdocument: true, document: "33333333303", nrcellphone: "47993330003", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 14, nmuser: "Locatario Job3 ContratoB AniversarioAbril", dtbirth: "1998-10-10", fgdocument: true, document: "33333333304", nrcellphone: "47993330004", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 15, nmuser: "Locador Job4 ContratoA VenceEm3Dias", dtbirth: "1955-07-07", fgdocument: true, document: "44444444401", nrcellphone: "47994440001", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 16, nmuser: "Locatario Job4 ContratoA VenceEm3Dias", dtbirth: "1992-03-14", fgdocument: true, document: "44444444402", nrcellphone: "47994440002", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 17, nmuser: "Locador Job4 ContratoB VenceHoje", dtbirth: "1963-09-19", fgdocument: true, document: "44444444403", nrcellphone: "47994440003", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] },
    { _id: 18, nmuser: "Locatario Job4 ContratoB VenceHoje", dtbirth: "1991-06-03", fgdocument: true, document: "44444444404", nrcellphone: "47994440004", dsissuingbody: "SSP/SC", address: addr, occupation: "Analista de Sistemas", bank_accounts: [] }
]);
print("Users: 18");

// =============================================================================
// 4. PROPERTIES (tipo/finalidade/status como texto, endereço embarcado)
// =============================================================================
print("Inserindo imóveis...");
db.properties.insertMany([
    { _id: 1, nrregistration: "MAT-99887", dsdescription: "Apto com 2 quartos no centro", vltotalarea: 65.5, address: addr, type: "Apartamento", purpose: "Residencial", status: "Alugado", owners: [1] }
]);
print("Properties: 1");

// =============================================================================
// 5. CONTRACT TEMPLATES (tópicos e cláusulas embarcados)
// =============================================================================
print("Inserindo templates...");
db.contract_templates.insertMany([
    { _id: 1, nmtemplate: "Contrato de Locação Padrão", dsversion: "1.0", fgactive: true, topics: [
        { cdtopic: 1, nmtopic: "Do Objeto da Locação", nrorder: 1, clauses: [{ cdclause: 1, dstext: "O locador cede ao locatário o imóvel nas condições atuais.", nrorder: 1 }] },
        { cdtopic: 2, nmtopic: "CONTRATO PARTICULAR DE LOCAÇÃO DE IMÓVEL RESIDENCIAL", nrorder: 1, clauses: [
            { cdclause: 30, dstext: "LOCADOR: %locador%, inscrito no CPF sob o nº %cpf%, portador de identidade nº %identidade%, Órgão emissor %orgao_emissor%.", nrorder: 1 },
            { cdclause: 21, dstext: "LOCATÁRIOS: %nome_locatario%, inscrito no CPF sob o n° %cpf_locatario%, portador de identidade n° %identidade_locatario%, Órgão emissor %orgao_emissor_locatario%.", nrorder: 2 }
        ]},
        { cdtopic: 3, nmtopic: "OBJETO DA LOCAÇÃO", nrorder: 2, clauses: [
            { cdclause: 45, dstext: "CLÁUSULA PRIMEIRA: O LOCADOR, na condição de proprietário e possuidor de um:", nrorder: 1 },
            { cdclause: 11, dstext: "Imóvel localizado na rua %rua_imovel%, nº %numero_imovel%, bairro %bairro_imovel%, matrícula sob o nº %matricula_imovel%.", nrorder: 2 },
            { cdclause: 41, dstext: "Dá o referido imóvel em locação aos LOCATÁRIOS, nas condições abaixo estabelecidas:", nrorder: 3 }
        ]},
        { cdtopic: 5, nmtopic: "DO PRAZO", nrorder: 4, clauses: [
            { cdclause: 23, dstext: "CLÁUSULA TERCEIRA: O prazo de locação é de %prazo_meses% meses, iniciando em %data_inicio% e terminando no dia %data_termino%.", nrorder: 1 },
            { cdclause: 10, dstext: "Parágrafo Primeiro: o contrato poderá ser prorrogado mediante manifestação expressa das partes.", nrorder: 2 },
            { cdclause: 27, dstext: "Parágrafo Segundo: na renovação o valor do aluguel deve ser ajustado de acordo com o índice %index% acumulado do período.", nrorder: 3 }
        ]},
        { cdtopic: 6, nmtopic: "DO ALUGUEL", nrorder: 5, clauses: [
            { cdclause: 36, dstext: "CLÁUSULA QUARTA: O valor mensal do aluguel é de %valor_aluguel%.", nrorder: 1 },
            { cdclause: 28, dstext: "Parágrafo Segundo: O aluguel vence no dia 01 de cada mês. Banco: %banco% Agência: %agencia% Conta: %conta% Titular: %titular_conta% CPF: %cpf_titular%", nrorder: 2 },
            { cdclause: 42, dstext: "Parágrafo Terceiro: O atraso sujeitará à multa de %multa_atraso% e juros de %juros_mensal% ao mês.", nrorder: 3 }
        ]},
        { cdtopic: 17, nmtopic: "OBSERVAÇÕES FINAIS E ASSINATURAS", nrorder: 16, clauses: [
            { cdclause: 46, dstext: "E por estarem justos e contratados, assinam o presente instrumento em 02 vias.", nrorder: 1 },
            { cdclause: 47, dstext: "Joinville, %dia_assinatura% de %mes_assinatura% de %ano_assinatura%.", nrorder: 2 },
            { cdclause: 15, dstext: "Assinaturas: LOCADOR: %nome_locador_assinatura%, LOCATÁRIOS: %nome_locatario_assinatura%, TESTEMUNHAS: %nome_testemunha_1%, %nome_testemunha_2%", nrorder: 3 }
        ]}
    ]}
]);
print("Templates: 1");

// =============================================================================
// 6. INDEXES (com rates embarcados - INCLUINDO cdrate!)
// =============================================================================
print("Inserindo índices...");
db.indexes.insertMany([
    { _id: 1, nmindex: "IPCA", rates: [
        { cdrate: 26, refmonth: 4, refyear: 2024, vlrate: 0.0038 },
        { cdrate: 27, refmonth: 5, refyear: 2024, vlrate: 0.0046 },
        { cdrate: 28, refmonth: 6, refyear: 2024, vlrate: 0.0021 },
        { cdrate: 29, refmonth: 7, refyear: 2024, vlrate: 0.0038 },
        { cdrate: 30, refmonth: 8, refyear: 2024, vlrate: -0.0002 },
        { cdrate: 31, refmonth: 9, refyear: 2024, vlrate: 0.0044 },
        { cdrate: 32, refmonth: 10, refyear: 2024, vlrate: 0.0056 },
        { cdrate: 33, refmonth: 11, refyear: 2024, vlrate: 0.0039 },
        { cdrate: 34, refmonth: 12, refyear: 2024, vlrate: 0.0052 },
        { cdrate: 35, refmonth: 1, refyear: 2025, vlrate: 0.0016 },
        { cdrate: 36, refmonth: 2, refyear: 2025, vlrate: 0.0131 },
        { cdrate: 37, refmonth: 3, refyear: 2025, vlrate: 0.0056 },
        { cdrate: 38, refmonth: 4, refyear: 2025, vlrate: 0.0043 },
        { cdrate: 39, refmonth: 5, refyear: 2025, vlrate: 0.0026 },
        { cdrate: 40, refmonth: 6, refyear: 2025, vlrate: 0.0024 },
        { cdrate: 41, refmonth: 7, refyear: 2025, vlrate: 0.0026 },
        { cdrate: 42, refmonth: 8, refyear: 2025, vlrate: -0.0011 },
        { cdrate: 43, refmonth: 9, refyear: 2025, vlrate: 0.0048 },
        { cdrate: 44, refmonth: 10, refyear: 2025, vlrate: 0.0009 },
        { cdrate: 45, refmonth: 11, refyear: 2025, vlrate: 0.0018 },
        { cdrate: 46, refmonth: 12, refyear: 2025, vlrate: 0.0033 },
        { cdrate: 47, refmonth: 1, refyear: 2026, vlrate: 0.0033 },
        { cdrate: 48, refmonth: 2, refyear: 2026, vlrate: 0.0070 },
        { cdrate: 49, refmonth: 3, refyear: 2026, vlrate: 0.0088 }
    ]},
    { _id: 2, nmindex: "IGP-M", rates: [] }
]);
print("Indexes: 2");

// =============================================================================
// 7. CONTRACTS (participants + installments embarcados)
//    Valores monetários com .0 para forçar Double no MongoDB
// =============================================================================
print("Inserindo contratos...");
db.contracts.insertMany([
    { _id: 8, dtcreation: "2026-04-25", dstitle: "Titulinho", cdtemplate: 1, cdproperty: 1, cdindex: null, dtlimit: "2026-04-25", cdstatus: 2, notary: null,
      participants: [{ cduser: 1, cdrole: 2, nmrole: "Locador" }, { cduser: 2, cdrole: 1, nmrole: "Locatário" }], installments: [] },

    { _id: 9, dtcreation: "2025-04-25", dstitle: "nominio", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2026-04-25", cdstatus: 1, notary: null,
      participants: [{ cduser: 1, cdrole: 2, nmrole: "Locador" }, { cduser: 2, cdrole: 1, nmrole: "Locatário" }],
      installments: [
        { cdinstallment: 31, nrinstallment: 1, dtdue: "2025-05-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 2, dtpayment: "2026-04-25", vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 32, nrinstallment: 2, dtdue: "2025-06-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 33, nrinstallment: 3, dtdue: "2025-07-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 34, nrinstallment: 4, dtdue: "2025-08-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 35, nrinstallment: 5, dtdue: "2025-09-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 36, nrinstallment: 6, dtdue: "2025-10-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 37, nrinstallment: 7, dtdue: "2025-11-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 38, nrinstallment: 8, dtdue: "2025-12-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 39, nrinstallment: 9, dtdue: "2026-01-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 40, nrinstallment: 10, dtdue: "2026-02-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 41, nrinstallment: 11, dtdue: "2026-03-01", vlbase: 1200.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 42, nrinstallment: 12, dtdue: "2026-04-28", vlbase: 1200.0, vladjusted: 1249.71, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: "2026-04-25" },
        { cdinstallment: 43, nrinstallment: 13, dtdue: "2026-05-01", vlbase: 1301.48, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00033, dtlastadjustment: null },
        { cdinstallment: 44, nrinstallment: 14, dtdue: "2026-06-01", vlbase: 1301.48, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00033, dtlastadjustment: null }
      ] },

    { _id: 10, dtcreation: "2025-04-25", dstitle: "[JOB1-A] Parcela Vence Em 7 Dias", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2026-10-25", cdstatus: 1, notary: null,
      participants: [{ cduser: 4, cdrole: 1, nmrole: "Locatário" }, { cduser: 3, cdrole: 2, nmrole: "Locador" }],
      installments: [{ cdinstallment: 55, nrinstallment: 1, dtdue: "2026-07-16", vlbase: 1500.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00033, dtlastadjustment: null }] },

    { _id: 11, dtcreation: "2025-04-25", dstitle: "[JOB1-B] Parcela Vence Hoje", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2026-10-25", cdstatus: 1, notary: null,
      participants: [{ cduser: 6, cdrole: 1, nmrole: "Locatário" }, { cduser: 5, cdrole: 2, nmrole: "Locador" }],
      installments: [{ cdinstallment: 56, nrinstallment: 1, dtdue: "2026-07-09", vlbase: 1800.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00033, dtlastadjustment: null }] },

    { _id: 12, dtcreation: "2026-01-01", dstitle: "[JOB2-A] Pagamento Registrado Hoje", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2027-01-01", cdstatus: 1, notary: null,
      participants: [{ cduser: 8, cdrole: 1, nmrole: "Locatário" }, { cduser: 7, cdrole: 2, nmrole: "Locador" }],
      installments: [{ cdinstallment: 57, nrinstallment: 1, dtdue: "2026-07-09", vlbase: 1200.0, vladjusted: 1200.0, cdstatus: 2, dtpayment: "2026-07-09", vlpenalty: 0.02, vlinterest: 0.00033, dtlastadjustment: null }] },

    { _id: 13, dtcreation: "2026-02-01", dstitle: "[JOB2-B] Pagamento Registrado Hoje B", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2027-02-01", cdstatus: 1, notary: null,
      participants: [{ cduser: 10, cdrole: 1, nmrole: "Locatário" }, { cduser: 9, cdrole: 2, nmrole: "Locador" }],
      installments: [{ cdinstallment: 58, nrinstallment: 1, dtdue: "2026-07-09", vlbase: 2200.0, vladjusted: 2200.0, cdstatus: 2, dtpayment: "2026-07-09", vlpenalty: 0.02, vlinterest: 0.00033, dtlastadjustment: null }] },

    { _id: 14, dtcreation: "2025-04-10", dstitle: "[JOB3-A] Aniversario 1Ano", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2027-04-10", cdstatus: 1, notary: null,
      participants: [{ cduser: 12, cdrole: 1, nmrole: "Locatário" }, { cduser: 11, cdrole: 2, nmrole: "Locador" }],
      installments: [{ cdinstallment: 59, nrinstallment: 13, dtdue: "2026-05-10", vlbase: 1700.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00033, dtlastadjustment: null }] },

    { _id: 15, dtcreation: "2024-04-20", dstitle: "[JOB3-B] Aniversario 2Anos", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2027-04-20", cdstatus: 1, notary: null,
      participants: [{ cduser: 14, cdrole: 1, nmrole: "Locatário" }, { cduser: 13, cdrole: 2, nmrole: "Locador" }],
      installments: [{ cdinstallment: 60, nrinstallment: 25, dtdue: "2026-05-20", vlbase: 1900.0, vladjusted: 2050.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00033, dtlastadjustment: null }] },

    { _id: 16, dtcreation: "2025-07-12", dstitle: "[JOB4-A] Contrato Vence Em 3 Dias", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2026-07-12", cdstatus: 1, notary: null,
      participants: [{ cduser: 16, cdrole: 1, nmrole: "Locatário" }, { cduser: 15, cdrole: 2, nmrole: "Locador" }],
      installments: [{ cdinstallment: 61, nrinstallment: 12, dtdue: "2026-07-12", vlbase: 1600.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00033, dtlastadjustment: null }] },

    { _id: 17, dtcreation: "2025-07-09", dstitle: "[JOB4-B] Contrato Vence Hoje", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2026-07-09", cdstatus: 1, notary: null,
      participants: [{ cduser: 18, cdrole: 1, nmrole: "Locatário" }, { cduser: 17, cdrole: 2, nmrole: "Locador" }],
      installments: [{ cdinstallment: 62, nrinstallment: 12, dtdue: "2026-07-09", vlbase: 2100.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00033, dtlastadjustment: null }] },

    { _id: 18, dtcreation: "2026-04-30", dstitle: "Contrato Exemplo A", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2027-05-23", cdstatus: 1, notary: { cdnotary: 2 },
      participants: [{ cduser: 1, cdrole: 2, nmrole: "Locador" }, { cduser: 2, cdrole: 1, nmrole: "Locatário" }],
      installments: [
        { cdinstallment: 63, nrinstallment: 1, dtdue: "2026-05-25", vlbase: 3000.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 64, nrinstallment: 2, dtdue: "2026-06-25", vlbase: 3000.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 65, nrinstallment: 3, dtdue: "2026-07-25", vlbase: 3000.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null },
        { cdinstallment: 66, nrinstallment: 4, dtdue: "2026-08-25", vlbase: 3000.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.01, dtlastadjustment: null }
      ] },

    { _id: 19, dtcreation: "2026-04-25", dstitle: "Contrato Exemplo B", cdtemplate: 1, cdproperty: 1, cdindex: 1, dtlimit: "2026-12-03", cdstatus: 1, notary: { cdnotary: 3 },
      participants: [{ cduser: 1, cdrole: 2, nmrole: "Locador" }, { cduser: 2, cdrole: 1, nmrole: "Locatário" }, { cduser: 3, cdrole: 3, nmrole: "Testemunha" }, { cduser: 4, cdrole: 3, nmrole: "Testemunha" }],
      installments: [
        { cdinstallment: 75, nrinstallment: 1, dtdue: "2026-05-25", vlbase: 2000.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.02, dtlastadjustment: null },
        { cdinstallment: 76, nrinstallment: 2, dtdue: "2026-06-25", vlbase: 2000.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.02, dtlastadjustment: null },
        { cdinstallment: 77, nrinstallment: 3, dtdue: "2026-07-25", vlbase: 2000.0, vladjusted: 0.0, cdstatus: 1, dtpayment: null, vlpenalty: 0.10, vlinterest: 0.02, dtlastadjustment: null }
      ] }
]);
print("Contracts: 12");

// =============================================================================
// 8. NOTIFICATIONS
// =============================================================================
print("Inserindo notificações...");
db.notifications.insertMany([
    { _id: 2, dsmessage: "[LEMBRETE] Aluguel do Contrato #10 vence em 16/07/2026 (R$ 1500,00).", dtsend: "2026-07-09", cdcontract: 10, cduser: 4, cdnotificationtemplate: 1, fgchannel: 1 },
    { _id: 3, dsmessage: "[LEMBRETE] Aluguel do Contrato #16 vence em 12/07/2026 (R$ 1600,00).", dtsend: "2026-07-09", cdcontract: 16, cduser: 16, cdnotificationtemplate: 2, fgchannel: 1 },
    { _id: 4, dsmessage: "[LEMBRETE] Aluguel do Contrato #11 vence em 09/07/2026 (R$ 1800,00).", dtsend: "2026-07-09", cdcontract: 11, cduser: 6, cdnotificationtemplate: 3, fgchannel: 1 },
    { _id: 5, dsmessage: "[LEMBRETE] Aluguel do Contrato #17 vence em 09/07/2026 (R$ 2100,00).", dtsend: "2026-07-09", cdcontract: 17, cduser: 18, cdnotificationtemplate: 3, fgchannel: 1 },
    { _id: 10, dsmessage: "[PAGAMENTO CONFIRMADO] Parcela #1 do Contrato #12 foi paga em 09/07/2026. Valor: R$ 1200,00.", dtsend: "2026-07-09", cdcontract: 12, cduser: 7, cdnotificationtemplate: 4, fgchannel: 1 },
    { _id: 11, dsmessage: "[PAGAMENTO CONFIRMADO] Parcela #1 do Contrato #13 foi paga em 09/07/2026. Valor: R$ 2200,00.", dtsend: "2026-07-09", cdcontract: 13, cduser: 9, cdnotificationtemplate: 4, fgchannel: 1 }
]);
print("Notifications: 6");

// =============================================================================
// 9. READJUSTMENT LOGS
// =============================================================================
print("Inserindo logs de reajuste...");
db.readjustment_logs.insertMany([
    { _id: 25, cdcontract: 9, cdinstallment: 42, cdindex: 1, vlold: 1200.0, vlnew: 1249.71, dtreadjustment: "2026-04-25" }
]);
print("Readjustment logs: 1");

// =============================================================================
// FINALIZADO
// =============================================================================
print("\n========================================");
print("  INSERÇÃO CONCLUÍDA COM SUCESSO!");
print("  Banco: " + DB_NAME);
print("========================================");
