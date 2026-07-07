// database/init-mongo.js
// Script de inicialização do MongoDB para o sistema Imobiliária
// Executar via: mongosh database/init-mongo.js
//
// Este script:
// 1. Verifica se o banco já existe (evita perda de dados)
// 2. Cria todas as coleções
// 3. Cria todos os índices
// 4. Inicializa contadores com valores atuais das sequences do PostgreSQL
// 5. Converte e insere todos os dados de exemplo do insert.sql em formato BSON

const DB_NAME = "imobiliaria";

// =============================================================================
// 1. VERIFICAÇÃO DE EXISTÊNCIA DO BANCO
// =============================================================================

const existingDbs = db.getMongo().getDBNames();
if (existingDbs.includes(DB_NAME)) {
    print("AVISO: Banco '" + DB_NAME + "' já existe. Abortando para evitar perda de dados.");
    print("Para recriar, execute: use imobiliaria; db.dropDatabase(); manualmente antes.");
    quit(1);
}

// Conectar ao banco
db = db.getSiblingDB(DB_NAME);
print("Criando banco de dados: " + DB_NAME);

// =============================================================================
// 2. CRIAÇÃO DAS COLEÇÕES
// =============================================================================

print("Criando coleções...");
db.createCollection("users");
db.createCollection("properties");
db.createCollection("contracts");
db.createCollection("notifications");
db.createCollection("contract_templates");
db.createCollection("indexes");
db.createCollection("readjustment_logs");
db.createCollection("counters");
print("Coleções criadas com sucesso.");

// =============================================================================
// 3. CRIAÇÃO DOS ÍNDICES
// =============================================================================

print("Criando índices...");

// users
db.users.createIndex({ "document": 1 }, { unique: true });
db.users.createIndex({ "nmuser": 1 });

// properties
db.properties.createIndex({ "nrregistration": 1 }, { unique: true });
db.properties.createIndex({ "address.district": 1 });
db.properties.createIndex({ "status": 1 });

// contracts
db.contracts.createIndex({ "cdproperty": 1 });
db.contracts.createIndex({ "cdstatus": 1 });
db.contracts.createIndex({ "dtlimit": 1 });
db.contracts.createIndex({ "participants.cduser": 1 });
db.contracts.createIndex({ "installments.dtdue": 1 });
db.contracts.createIndex({ "installments.cdstatus": 1 });

// notifications
db.notifications.createIndex({ "cdcontract": 1 });
db.notifications.createIndex({ "cduser": 1 });
db.notifications.createIndex({ "dtsend": -1 });

print("Índices criados com sucesso.");

// =============================================================================
// 4. INICIALIZAÇÃO DOS CONTADORES
// =============================================================================

print("Inicializando contadores...");
db.counters.insertMany([
    { _id: "users", seq: 18 },
    { _id: "properties", seq: 1 },
    { _id: "contracts", seq: 19 },
    { _id: "notifications", seq: 11 },
    { _id: "contract_templates", seq: 1 },
    { _id: "indexes", seq: 2 },
    { _id: "installments", seq: 81 },
    { _id: "readjustment_logs", seq: 25 }
]);
print("Contadores inicializados com sucesso.");

// =============================================================================
// 5. DADOS DE EXEMPLO (convertidos do insert.sql)
// =============================================================================

print("Inserindo dados de exemplo...");

// --- Endereço embarcado (usado em users e properties) ---
const addressEmbedded = {
    cdzipcode: "89200000",
    nmstreet: "Rua XV de Novembro",
    nraddress: "1000",
    dscomplement: "Sala 2",
    district: "Centro",
    city: "Joinville",
    state: "SC",
    country: "Brasil"
};

// --- USERS ---
db.users.insertMany([
    {
        _id: 1,
        nmuser: "João Silva",
        dtbirth: "1990-05-20",
        fgdocument: true,
        document: "12345678900",
        nrcellphone: "47999999999",
        dsissuingbody: "SSP",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: [
            { nragency: "0001", nraccount: "12345-6", nrpixkey: "joao.silva@email.com" }
        ]
    },
    {
        _id: 2,
        nmuser: "Kauane",
        dtbirth: "2004-08-30",
        fgdocument: true,
        document: "12435611955",
        nrcellphone: "47 9956-8956",
        dsissuingbody: "SSP",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: [
            { nragency: "2564", nraccount: "25999874", nrpixkey: "2558996589" }
        ]
    },
    {
        _id: 3,
        nmuser: "Locador Job1 ContratoA",
        dtbirth: "1975-03-10",
        fgdocument: true,
        document: "11111111101",
        nrcellphone: "47991110001",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 4,
        nmuser: "Locatario Job1 ContratoA VenceEm7Dias",
        dtbirth: "1990-07-20",
        fgdocument: true,
        document: "11111111102",
        nrcellphone: "47991110002",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 5,
        nmuser: "Locador Job1 ContratoB",
        dtbirth: "1968-11-05",
        fgdocument: true,
        document: "11111111103",
        nrcellphone: "47991110003",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 6,
        nmuser: "Locatario Job1 ContratoB VenceHoje",
        dtbirth: "1995-01-15",
        fgdocument: true,
        document: "11111111104",
        nrcellphone: "47991110004",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 7,
        nmuser: "Locador Job2 ContratoA PagamentoRecebido",
        dtbirth: "1970-06-22",
        fgdocument: true,
        document: "22222222201",
        nrcellphone: "47992220001",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 8,
        nmuser: "Locatario Job2 ContratoA PagouHoje",
        dtbirth: "1988-09-30",
        fgdocument: true,
        document: "22222222202",
        nrcellphone: "47992220002",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 9,
        nmuser: "Locador Job2 ContratoB PagamentoRecebido",
        dtbirth: "1965-04-18",
        fgdocument: true,
        document: "22222222203",
        nrcellphone: "47992220003",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 10,
        nmuser: "Locatario Job2 ContratoB PagouHoje",
        dtbirth: "1993-12-01",
        fgdocument: true,
        document: "22222222204",
        nrcellphone: "47992220004",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 11,
        nmuser: "Locador Job3 ContratoA AniversarioAbril",
        dtbirth: "1960-08-14",
        fgdocument: true,
        document: "33333333301",
        nrcellphone: "47993330001",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 12,
        nmuser: "Locatario Job3 ContratoA AniversarioAbril",
        dtbirth: "1985-05-25",
        fgdocument: true,
        document: "33333333302",
        nrcellphone: "47993330002",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 13,
        nmuser: "Locador Job3 ContratoB AniversarioAbril",
        dtbirth: "1972-02-28",
        fgdocument: true,
        document: "33333333303",
        nrcellphone: "47993330003",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 14,
        nmuser: "Locatario Job3 ContratoB AniversarioAbril",
        dtbirth: "1998-10-10",
        fgdocument: true,
        document: "33333333304",
        nrcellphone: "47993330004",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 15,
        nmuser: "Locador Job4 ContratoA VenceEm3Dias",
        dtbirth: "1955-07-07",
        fgdocument: true,
        document: "44444444401",
        nrcellphone: "47994440001",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 16,
        nmuser: "Locatario Job4 ContratoA VenceEm3Dias",
        dtbirth: "1992-03-14",
        fgdocument: true,
        document: "44444444402",
        nrcellphone: "47994440002",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 17,
        nmuser: "Locador Job4 ContratoB VenceHoje",
        dtbirth: "1963-09-19",
        fgdocument: true,
        document: "44444444403",
        nrcellphone: "47994440003",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    },
    {
        _id: 18,
        nmuser: "Locatario Job4 ContratoB VenceHoje",
        dtbirth: "1991-06-03",
        fgdocument: true,
        document: "44444444404",
        nrcellphone: "47994440004",
        dsissuingbody: "SSP/SC",
        address: addressEmbedded,
        occupation: "Analista de Sistemas",
        bank_accounts: []
    }
]);
print("Users inseridos: 18");

// --- PROPERTIES ---
db.properties.insertMany([
    {
        _id: 1,
        nrregistration: "MAT-99887",
        dsdescription: "Apto com 2 quartos no centro",
        vltotalarea: 65.50,
        address: addressEmbedded,
        type: "Apartamento",
        purpose: "Residencial",
        status: "Alugado",
        owners: [1]
    }
]);
print("Properties inseridos: 1");

// --- CONTRACT TEMPLATES (com topics e clauses embarcados) ---
db.contract_templates.insertMany([
    {
        _id: 1,
        nmtemplate: "Contrato de Locação Padrão",
        dsversion: "1.0",
        fgactive: true,
        topics: [
            {
                cdtopic: 1, nmtopic: "Do Objeto da Locação", nrorder: 1,
                clauses: [
                    { cdclause: 1, dstext: "O locador cede ao locatário o imóvel nas condições atuais.", nrorder: 1 }
                ]
            },
            {
                cdtopic: 2, nmtopic: "CONTRATO PARTICULAR DE LOCAÇÃO DE IMÓVEL RESIDENCIAL", nrorder: 1,
                clauses: [
                    { cdclause: 30, dstext: "LOCADOR: %locador%, brasileiro, aposentado, inscrito no CPF sob o nº %cpf%, portador de identidade nº %identidade%, Órgão emissor %orgao_emissor%, separado. Representado por seu filho %filho%, brasileiro, %estado_civil_e_profissao_filho%, inscrito no CPF nº %cpf_filho%, portador da identidade nº %identidade_filho%, conforme Procuração Pública lavrada no %numero_tabelionato%º Tabelionato de Notas de %cidade tabelionato%, Livro %livro_tabelionato%, Folha %folha_livro_tabelionato%, datada de %data_tabelionato%", nrorder: 1 },
                    { cdclause: 21, dstext: "LOCATÁRIOS: %nome_locatario%, brasileira, casada, inscrito no CPF. sob o n° %cpf_locatario%, portador de identidade n° %identidade_locatario%, Órgão emissor %orgao_emissor_locatario%.", nrorder: 2 }
                ]
            },
            {
                cdtopic: 3, nmtopic: "OBJETO DA LOCAÇÃO", nrorder: 2,
                clauses: [
                    { cdclause: 45, dstext: "CLÁUSULA PRIMEIRA: O LOCADOR, na condição de proprietário e possuidor de um:", nrorder: 1 },
                    { cdclause: 11, dstext: "Casa localizada na rua %rua_imovel%, nº %numero_imovel%, bairro %bairro_imovel%, neste município de Joinville, com área área privativa de %area_privativa% m² e área comum de %area_comum% m², matrícula sob o nº %matricula_imovel%.", nrorder: 2 },
                    { cdclause: 41, dstext: "Dá o referido imóvel e seus acessórios em locação aos LOCATÁRIOS, nas condições abaixo estabelecidas:", nrorder: 3 }
                ]
            },
            {
                cdtopic: 4, nmtopic: "DA VISTORIA DE ENTRADA", nrorder: 3,
                clauses: [
                    { cdclause: 32, dstext: "CLÁUSULA SEGUNDA: Os LOCATÁRIOS declaram que recebem o imóvel em perfeito estado de conservação e uso, conforme descrito no Termo de Vistoria de Entrada, que integra o presente contrato, obrigando-se a devolvê-lo no mesmo estado, ressalvadas apenas as deteriorações decorrentes do uso normal.", nrorder: 1 },
                    { cdclause: 9, dstext: "Os LOCATÁRIOS terão o prazo de 10 (dez) dias, contados do recebimento das chaves, para apresentar por escrito eventuais divergências em relação à vistoria inicial.", nrorder: 2 },
                    { cdclause: 37, dstext: "Parágrafo Primeiro: O laudo de vistoria tem como finalidade exclusiva registrar o estado do imóvel no momento da entrega e subsidiar a comparação quando da devolução, não obrigando o LOCADOR a reparar os vícios nele apontados.", nrorder: 3 },
                    { cdclause: 48, dstext: "Parágrafo Segundo: O Termo de Vistoria poderá ser enviado por meio eletrônico, contendo fotos e descrições, sendo vedada qualquer alteração posterior.", nrorder: 4 },
                    { cdclause: 19, dstext: "Parágrafo Terceiro: As partes estão cientes de que a cidade de Joinville (SC) sofre com longos períodos de chuva e alto índice de umidade, portanto cabe aos LOCATÁRIOS adotar os cuidados necessários para evitar danos decorrentes dessas condições climáticas, não sendo o LOCADOR responsável por prejuízos decorrentes do clima.", nrorder: 5 },
                    { cdclause: 6, dstext: "Parágrafo Quarto: Os LOCATÁRIOS declaram ter vistoriado previamente o imóvel, aceitando-o sem ressalvas.", nrorder: 6 },
                    { cdclause: 4, dstext: "Parágrafo Quinto: Caso o imóvel esteja sem fornecimento de energia elétrica, os LOCATÁRIOS deverão providenciar a ligação ou religação junto à CELESC, não sendo tal fato motivo para rescisão contratual. Débitos anteriores em nome do LOCADOR, se existentes, poderão ser pagos pelos LOCATÁRIOS mediante posterior reembolso.", nrorder: 7 },
                    { cdclause: 14, dstext: "Parágrafo Sexto: Após a entrega das chaves aos LOCATÁRIOS, recomenda-se que seja mudado o segredo das chaves, principalmente das portas de acesso ao imóvel, não respondendo o LOCADOR por quaisquer furtos ou roubos de bens que porventura acontecerem.", nrorder: 8 },
                    { cdclause: 24, dstext: "Parágrafo Sétimo: Os LOCATÁRIOS deverão informar ao LOCADOR, no prazo máximo de 05 (cinco) dias, qualquer defeito constatado, sob pena de concordância tácita, ressalvados os vícios ocultos.", nrorder: 9 },
                    { cdclause: 7, dstext: "Parágrafo Oitavo: O LOCADOR, sempre que entender conveniente ou necessário, poderá vistoriar o imóvel, avisando os LOCATÁRIOS com antecedência mínima de 72 (setenta e duas) horas.", nrorder: 10 }
                ]
            },
            {
                cdtopic: 5, nmtopic: "DO PRAZO", nrorder: 4,
                clauses: [
                    { cdclause: 23, dstext: "CLÁUSULA TERCEIRA: O prazo de locação é de %prazo_meses% meses, iniciando em %data_inicio% e terminando, de pleno direito, no dia %data_termino%, independentemente de notificação.", nrorder: 1 },
                    { cdclause: 10, dstext: "Parágrafo Primeiro: o contrato poderá ser prorrogado mediante manifestação expressa das partes.", nrorder: 2 },
                    { cdclause: 27, dstext: "Parágrafo Segundo: na renovação o valor do aluguel deve ser ajustado de acordo com o índice %index% acumulado do período.", nrorder: 3 },
                    { cdclause: 44, dstext: "Parágrafo Terceiro: a devolução antecipada do imóvel implicará multa equivalente a 02 (dois) meses de aluguel, calculada proporcionalmente ao período restante do contrato, nos termos do art. 4º da Lei nº 8.245/91.", nrorder: 4 }
                ]
            },
            {
                cdtopic: 6, nmtopic: "DO ALUGUEL", nrorder: 5,
                clauses: [
                    { cdclause: 36, dstext: "CLÁUSULA QUARTA: O valor mensal do aluguel do imóvel, objeto desse contrato é de %valor_aluguel%.", nrorder: 1 },
                    { cdclause: 26, dstext: "Parágrafo Primeiro: o consumo de água e energia elétrica será de responsabilidade exclusiva dos LOCATÁRIOS.", nrorder: 2 },
                    { cdclause: 28, dstext: "Parágrafo Segundo: O aluguel vence no dia 01 (um) de cada mês, sendo o primeiro pagamento para %primeiro_vencimento%, e deverão ser pagos por meio de transferência bancária para a seguinte conta: Banco: %banco% Agência: %agencia% Conta: %conta% Titular: %titular_conta% CPF: %cpf_titular%", nrorder: 3 },
                    { cdclause: 42, dstext: "Parágrafo Terceiro: O atraso no pagamento sujeitará os LOCATÁRIOS à multa de %multa_atraso% e juros de %juros_mensal% ao mês.", nrorder: 4 }
                ]
            },
            {
                cdtopic: 7, nmtopic: "A DESTINAÇÃO DO IMÓVEL", nrorder: 6,
                clauses: [
                    { cdclause: 35, dstext: "CLÁUSULA QUINTA: O imóvel destina-se exclusivamente a uso residencial, sendo vedada qualquer outra destinação.", nrorder: 1 },
                    { cdclause: 2, dstext: "Parágrafo Único: É permitida a permanência de animais de estimação, desde que não causem danos, barulho excessivo ou incômodos a terceiros, ficando os LOCATÁRIOS responsáveis por eventuais prejuízos.", nrorder: 2 }
                ]
            },
            {
                cdtopic: 8, nmtopic: "DAS MANUTENÇÕES", nrorder: 7,
                clauses: [
                    { cdclause: 34, dstext: "CLÁUSULA SEXTA: Os LOCATÁRIOS deverão devolver o imóvel locado, assim como o recebem. Fica ajustado entre as partes contratantes que os LOCATÁRIOS se comprometem a informar o LOCADOR qualquer ocorrência de vazamentos, infiltração, etc., que possa danificar a propriedade locada.", nrorder: 1 },
                    { cdclause: 40, dstext: "Parágrafo Primeiro: Os danos causados por LOCATÁRIOS, familiares ou visitantes deverão ser reparados às suas expensas.", nrorder: 2 }
                ]
            },
            {
                cdtopic: 9, nmtopic: "DA SUBLOCAÇÃO", nrorder: 8,
                clauses: [
                    { cdclause: 31, dstext: "CLÁUSULA SÉTIMA: É vedada a sublocação ou cessão do imóvel sem autorização expressa do LOCADOR.", nrorder: 1 }
                ]
            },
            {
                cdtopic: 10, nmtopic: "DAS BENFEITORIAS", nrorder: 9,
                clauses: [
                    { cdclause: 38, dstext: "CLÁUSULA OITAVA: Nenhuma benfeitoria poderá ser realizada sem autorização escrita do LOCADOR, não sendo devida qualquer indenização.", nrorder: 1 }
                ]
            },
            {
                cdtopic: 11, nmtopic: "DAS VEDAÇÕES", nrorder: 10,
                clauses: [
                    { cdclause: 18, dstext: "CLÁUSULA NONA: Não é permitido depositar materiais inflamáveis, explosivos ou corrosivos, nem cortar ou danificar árvores por acaso existentes, ficando os LOCATÁRIOS responsáveis pelos danos que causarem.", nrorder: 1 }
                ]
            },
            {
                cdtopic: 12, nmtopic: "DAS DESPESAS COM CARTÓRIO", nrorder: 11,
                clauses: [
                    { cdclause: 17, dstext: "CLÁUSULA DÉCIMA: Correrão por conta dos LOCATÁRIOS as despesas de registro do presente contrato no Cartório de Registro de Títulos e Documentos e/ou Cartório Registro de Imóveis e reconhecimento de firma no cartório.", nrorder: 1 }
                ]
            },
            {
                cdtopic: 13, nmtopic: "DA VISTORIA DE SAÍDA", nrorder: 12,
                clauses: [
                    { cdclause: 43, dstext: "CLÁUSULA DÉCIMA PRIMEIRA: Findo o prazo da locação, ora estabelecido, ou rescindido a locação, por qualquer motivo, será o imóvel restituído ao LOCADOR, em condições de ser imediatamente habitado com as quitações de energia e água.", nrorder: 1 },
                    { cdclause: 16, dstext: "Parágrafo Primeiro: Objetivando o cumprimento do disposto no \"caput\" desta cláusula, os LOCATÁRIOS obrigam-se a bem conservar o imóvel para entregá-lo limpo e plenas condições de imediato uso de acordo com o Termo de Vistoria de Ocupação. Se assim não fizerem, o LOCADOR deve fazê-lo e cobrar os custos dos LOCATÁRIOS mediante a exibição dos recibos de execução das obras e pagamentos efetuados, os quais deverão estar acompanhados de, no mínimo, 02 (dois) orçamentos, devendo sempre ser exigido o de menor valor.", nrorder: 2 },
                    { cdclause: 5, dstext: "Parágrafo Segundo: A vistoria de saída será realizada pelo LOCADOR tendo como base a vistoria de entrada e será utilizado o método comparativo, tanto de escrita como fotografia.", nrorder: 3 },
                    { cdclause: 8, dstext: "Parágrafo Terceiro: A solicitação da vistoria de saída deverá ser feita com no mínimo 72 (setenta e duas) horas de antecedência.", nrorder: 4 },
                    { cdclause: 33, dstext: "Parágrafo Quarto: A entrega das chaves para vistoria, quando da desocupação do imóvel, não exonera os LOCATÁRIOS das obrigações ora assumidas. NO CASO DO IMÓVEL NECESSITAR DE ALGUM REPARO, OS LOCATÁRIOS DEVERÃO PAGAR O ALUGUEL E ENCARGOS ATÉ A CONCLUSÃO DOS REPAROS E RESCISÃO DEFINITIVA DA LOCAÇÃO.", nrorder: 5 },
                    { cdclause: 49, dstext: "Parágrafo Quinto: Caso o imóvel locado, seja abandonado pelos LOCATÁRIOS, sem que estes tenham promovido a respectiva entrega das chaves, de modo a evitar a deterioração do imóvel e eventual agravamento de possível inadimplência das obrigações contratuais, fica facultado ao LOCADOR promover a retomada da posse do imóvel locado, o que poderá ser realizado por meio de ato acompanhado por duas testemunhas ou por meio de ata notarial. A data da retomada da posse do imóvel em caso de abandono, que será acompanhada da vistoria final visando identificar o estado material do imóvel, será anunciada aos LOCATÁRIOS por meio do contato apontado neste contrato.", nrorder: 6 }
                ]
            },
            {
                cdtopic: 14, nmtopic: "DA GARANTIA LOCATÍCIA - CAUÇÃO", nrorder: 13,
                clauses: [
                    { cdclause: 25, dstext: "CLÁUSULA DÉCIMA QUARTA: Os LOCATÁRIOS prestam caução no valor de 01 (um) aluguel, totalizando %valor_caucao%, a ser devolvida em até 30 dias após a vistoria final, inexistindo pendências.", nrorder: 1 },
                    { cdclause: 22, dstext: "Caso haja débitos ou danos, o LOCADOR poderá reter total ou parcialmente o valor da caução para compensação, mediante apresentação dos comprovantes correspondentes.", nrorder: 2 },
                    { cdclause: 13, dstext: "A caução não poderá ser utilizada como pagamento de aluguéis, encargos ou quaisquer obrigações durante a vigência do contrato.", nrorder: 3 }
                ]
            },
            {
                cdtopic: 15, nmtopic: "DOS ENCARGOS DA LOCAÇÃO", nrorder: 14,
                clauses: [
                    { cdclause: 12, dstext: "CLÁUSULA DÉCIMA QUINTA: Ficam a cargo exclusivo dos LOCATÁRIOS o pagamento das despesas de consumo de água e energia elétrica que devem manter as respectivas contas em seu nome e integralmente quitadas, devendo apresentá-las ao LOCADOR sempre que solicitado.", nrorder: 1 }
                ]
            },
            {
                cdtopic: 16, nmtopic: "DOS DADOS DE CONTATO E COMUNICAÇÕES", nrorder: 15,
                clauses: [
                    { cdclause: 29, dstext: "CLÁUSULA DÉCIMA SEXTA: Para todos os fins deste contrato, as partes informam como válidos os seguintes dados de contato, comprometendo-se a comunicar qualquer alteração:", nrorder: 1 },
                    { cdclause: 39, dstext: "LOCADOR: E-mail: %email_locador% Telefone/WhatsApp: %telefone_locador%", nrorder: 2 },
                    { cdclause: 20, dstext: "LOCATÁRIO: E-mail: %email_locatario% Telefone/WhatsApp: %telefone_locatario%", nrorder: 3 }
                ]
            },
            {
                cdtopic: 17, nmtopic: "OBSERVAÇÕES FINAIS E ASSINATURAS", nrorder: 16,
                clauses: [
                    { cdclause: 3, dstext: "É facultado ao LOCADOR ou seu representante recusar o recebimento das chaves, caso o imóvel objeto da locação, não esteja em perfeitas condições, exatamente como fora entregue aos LOCATÁRIOS no início da locação. Ocorrendo dita hipótese, ou seja, havendo irregularidades, continuará por conta dos LOCATÁRIOS e seus garantes, os aluguéis e demais encargos locatícios, até que o imóvel seja restituído ao LOCADOR, nas mesmas condições do início da locação.", nrorder: 1 },
                    { cdclause: 46, dstext: "E por estarem justos e contratados, assinam le presente instrumento em 02 (duas) vias de igual teor e forma, na presença de 02 (duas) testemunhas abaixo assinadas ou de forma digital com fundamento no artigo 740 § 4º do C.P.C, nos títulos executivos constituídos ou atestados por meio eletrônico, é admitida qualquer modalidade de assinatura eletrônica prevista em lei, dispensada a assinatura de testemunhas, para que produza os seus jurídicos e legais efeitos.", nrorder: 2 },
                    { cdclause: 47, dstext: "Joinville, %dia_assinatura% de %mes_assinatura% de %ano_assinatura%.", nrorder: 3 },
                    { cdclause: 15, dstext: "Assinaturas: LOCADOR: %nome_locador_assinatura%, Representante: %nome_filho_representante%, LOCATÁRIOS: %nome_locatario_assinatura%, TESTEMUNHAS: %nome_testemunha_1%, %nome_testemunha_2%", nrorder: 4 }
                ]
            }
        ]
    }
]);
print("Contract templates inseridos: 1");

// --- INDEXES (com rates embarcados) ---
db.indexes.insertMany([
    {
        _id: 1,
        nmindex: "IPCA",
        rates: [
            { refmonth: 4, refyear: 2024, vlrate: 0.0038 },
            { refmonth: 5, refyear: 2024, vlrate: 0.0046 },
            { refmonth: 6, refyear: 2024, vlrate: 0.0021 },
            { refmonth: 7, refyear: 2024, vlrate: 0.0038 },
            { refmonth: 8, refyear: 2024, vlrate: -0.0002 },
            { refmonth: 9, refyear: 2024, vlrate: 0.0044 },
            { refmonth: 10, refyear: 2024, vlrate: 0.0056 },
            { refmonth: 11, refyear: 2024, vlrate: 0.0039 },
            { refmonth: 12, refyear: 2024, vlrate: 0.0052 },
            { refmonth: 1, refyear: 2025, vlrate: 0.0016 },
            { refmonth: 2, refyear: 2025, vlrate: 0.0131 },
            { refmonth: 3, refyear: 2025, vlrate: 0.0056 },
            { refmonth: 4, refyear: 2025, vlrate: 0.0043 },
            { refmonth: 5, refyear: 2025, vlrate: 0.0026 },
            { refmonth: 6, refyear: 2025, vlrate: 0.0024 },
            { refmonth: 7, refyear: 2025, vlrate: 0.0026 },
            { refmonth: 8, refyear: 2025, vlrate: -0.0011 },
            { refmonth: 9, refyear: 2025, vlrate: 0.0048 },
            { refmonth: 10, refyear: 2025, vlrate: 0.0009 },
            { refmonth: 11, refyear: 2025, vlrate: 0.0018 },
            { refmonth: 12, refyear: 2025, vlrate: 0.0033 },
            { refmonth: 1, refyear: 2026, vlrate: 0.0033 },
            { refmonth: 2, refyear: 2026, vlrate: 0.0070 },
            { refmonth: 3, refyear: 2026, vlrate: 0.0088 }
        ]
    },
    {
        _id: 2,
        nmindex: "IPCA",
        rates: []
    }
]);
print("Indexes inseridos: 2");

// --- CONTRACTS (com participants e installments embarcados) ---
// Roles: 1=Locatário, 2=Locador, 3=Testemunha, 4=Representante
// Installment status: 1=Pendente, 2=Pago

db.contracts.insertMany([
    {
        _id: 8,
        dtcreation: "2026-04-25",
        dstitle: "Titulinho",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: null,
        dtlimit: "2026-04-25",
        cdstatus: 2,
        notary: null,
        participants: [
            { cduser: 1, cdrole: 2, nmrole: "Locador" },
            { cduser: 2, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: []
    },
    {
        _id: 9,
        dtcreation: "2025-04-25",
        dstitle: "nominio",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2026-04-25",
        cdstatus: 1,
        notary: null,
        participants: [
            { cduser: 1, cdrole: 2, nmrole: "Locador" },
            { cduser: 2, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: [
            { cdinstallment: 31, nrinstallment: 1, dtdue: "2025-05-01", vlbase: 1200.00, vladjusted: 0.00, cdstatus: 2, dtpayment: "2026-04-25", vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 32, nrinstallment: 2, dtdue: "2025-06-01", vlbase: 1200.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 33, nrinstallment: 3, dtdue: "2025-07-01", vlbase: 1200.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 34, nrinstallment: 4, dtdue: "2025-08-01", vlbase: 1200.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 35, nrinstallment: 5, dtdue: "2025-09-01", vlbase: 1200.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 36, nrinstallment: 6, dtdue: "2025-10-01", vlbase: 1200.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 37, nrinstallment: 7, dtdue: "2025-11-01", vlbase: 1200.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 38, nrinstallment: 8, dtdue: "2025-12-01", vlbase: 1200.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 39, nrinstallment: 9, dtdue: "2026-01-01", vlbase: 1200.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 40, nrinstallment: 10, dtdue: "2026-02-01", vlbase: 1200.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 41, nrinstallment: 11, dtdue: "2026-03-01", vlbase: 1200.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 42, nrinstallment: 12, dtdue: "2026-04-28", vlbase: 1200.00, vladjusted: 1249.71, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 1.00 },
            { cdinstallment: 43, nrinstallment: 13, dtdue: "2026-05-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 44, nrinstallment: 14, dtdue: "2026-06-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 45, nrinstallment: 15, dtdue: "2026-07-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 46, nrinstallment: 16, dtdue: "2026-08-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 47, nrinstallment: 17, dtdue: "2026-09-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 48, nrinstallment: 18, dtdue: "2026-10-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 49, nrinstallment: 19, dtdue: "2026-11-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 50, nrinstallment: 20, dtdue: "2026-12-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 51, nrinstallment: 21, dtdue: "2027-01-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 52, nrinstallment: 22, dtdue: "2027-02-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 53, nrinstallment: 23, dtdue: "2027-03-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 },
            { cdinstallment: 54, nrinstallment: 24, dtdue: "2027-04-01", vlbase: 1301.48, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 }
        ]
    },
    {
        _id: 10,
        dtcreation: "2025-04-25",
        dstitle: "[JOB1-A] Locacao - Parcela Vence Em 7 Dias",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2026-10-25",
        cdstatus: 1,
        notary: null,
        participants: [
            { cduser: 3, cdrole: 2, nmrole: "Locador" },
            { cduser: 4, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: [
            { cdinstallment: 55, nrinstallment: 1, dtdue: "2026-05-02", vlbase: 1500.00, vladjusted: 1500.00, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 }
        ]
    },
    {
        _id: 11,
        dtcreation: "2025-04-25",
        dstitle: "[JOB1-B] Locacao - Parcela Vence Hoje",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2026-10-25",
        cdstatus: 1,
        notary: null,
        participants: [
            { cduser: 5, cdrole: 2, nmrole: "Locador" },
            { cduser: 6, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: [
            { cdinstallment: 56, nrinstallment: 1, dtdue: "2026-04-25", vlbase: 1800.00, vladjusted: 1800.00, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 }
        ]
    },
    {
        _id: 12,
        dtcreation: "2026-01-01",
        dstitle: "[JOB2-A] Locacao - Pagamento Registrado Hoje ContratoA",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2027-01-01",
        cdstatus: 1,
        notary: null,
        participants: [
            { cduser: 7, cdrole: 2, nmrole: "Locador" },
            { cduser: 8, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: [
            { cdinstallment: 57, nrinstallment: 1, dtdue: "2026-04-10", vlbase: 1200.00, vladjusted: 1200.00, cdstatus: 2, dtpayment: "2026-04-25", vlpenalty: 0.02, vlinterest: 0.00 }
        ]
    },
    {
        _id: 13,
        dtcreation: "2026-02-01",
        dstitle: "[JOB2-B] Locacao - Pagamento Registrado Hoje ContratoB",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2027-02-01",
        cdstatus: 1,
        notary: null,
        participants: [
            { cduser: 9, cdrole: 2, nmrole: "Locador" },
            { cduser: 10, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: [
            { cdinstallment: 58, nrinstallment: 1, dtdue: "2026-04-15", vlbase: 2200.00, vladjusted: 2200.00, cdstatus: 2, dtpayment: "2026-04-25", vlpenalty: 0.02, vlinterest: 0.00 }
        ]
    },
    {
        _id: 14,
        dtcreation: "2025-04-10",
        dstitle: "[JOB3-A] Locacao - Aniversario 1Ano Em Abril 2026",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2027-04-10",
        cdstatus: 1,
        notary: null,
        participants: [
            { cduser: 11, cdrole: 2, nmrole: "Locador" },
            { cduser: 12, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: [
            { cdinstallment: 59, nrinstallment: 13, dtdue: "2026-05-10", vlbase: 1700.00, vladjusted: 1700.00, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 }
        ]
    },
    {
        _id: 15,
        dtcreation: "2024-04-20",
        dstitle: "[JOB3-B] Locacao - Aniversario 2Anos Em Abril 2026",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2027-04-20",
        cdstatus: 1,
        notary: null,
        participants: [
            { cduser: 13, cdrole: 2, nmrole: "Locador" },
            { cduser: 14, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: [
            { cdinstallment: 60, nrinstallment: 25, dtdue: "2026-05-20", vlbase: 1900.00, vladjusted: 2050.00, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 }
        ]
    },
    {
        _id: 16,
        dtcreation: "2025-04-28",
        dstitle: "[JOB4-A] Locacao - Contrato Vence Em 3 Dias",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2026-04-28",
        cdstatus: 1,
        notary: null,
        participants: [
            { cduser: 15, cdrole: 2, nmrole: "Locador" },
            { cduser: 16, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: [
            { cdinstallment: 61, nrinstallment: 12, dtdue: "2026-04-28", vlbase: 1600.00, vladjusted: 1600.00, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 }
        ]
    },
    {
        _id: 17,
        dtcreation: "2025-04-25",
        dstitle: "[JOB4-B] Locacao - Contrato Vence Hoje",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2026-04-25",
        cdstatus: 1,
        notary: null,
        participants: [
            { cduser: 17, cdrole: 2, nmrole: "Locador" },
            { cduser: 18, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: [
            { cdinstallment: 62, nrinstallment: 12, dtdue: "2026-04-25", vlbase: 2100.00, vladjusted: 2100.00, cdstatus: 1, dtpayment: null, vlpenalty: 0.02, vlinterest: 0.00 }
        ]
    },
    {
        _id: 18,
        dtcreation: "2026-04-30",
        dstitle: "asidsdas",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2027-05-23",
        cdstatus: 1,
        notary: { cdcity: 1, book: "1", leaf: "1", dt: "2026-04-25", nrnotary: null },
        participants: [
            { cduser: 1, cdrole: 2, nmrole: "Locador" },
            { cduser: 2, cdrole: 1, nmrole: "Locatário" }
        ],
        installments: [
            { cdinstallment: 63, nrinstallment: 1, dtdue: "2026-05-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 64, nrinstallment: 2, dtdue: "2026-06-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 65, nrinstallment: 3, dtdue: "2026-07-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 66, nrinstallment: 4, dtdue: "2026-08-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 67, nrinstallment: 5, dtdue: "2026-09-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 68, nrinstallment: 6, dtdue: "2026-10-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 69, nrinstallment: 7, dtdue: "2026-11-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 70, nrinstallment: 8, dtdue: "2026-12-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 71, nrinstallment: 9, dtdue: "2027-01-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 72, nrinstallment: 10, dtdue: "2027-02-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 73, nrinstallment: 11, dtdue: "2027-03-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 },
            { cdinstallment: 74, nrinstallment: 12, dtdue: "2027-04-25", vlbase: 3000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 10.00 }
        ]
    },
    {
        _id: 19,
        dtcreation: "2026-04-25",
        dstitle: "sidjasidjasd",
        cdtemplate: 1,
        cdproperty: 1,
        cdindex: 1,
        dtlimit: "2026-12-03",
        cdstatus: 1,
        notary: { cdcity: 1, book: "1", leaf: "1", dt: "2026-04-25", nrnotary: 1 },
        participants: [
            { cduser: 1, cdrole: 2, nmrole: "Locador" },
            { cduser: 2, cdrole: 1, nmrole: "Locatário" },
            { cduser: 3, cdrole: 3, nmrole: "Testemunha" },
            { cduser: 4, cdrole: 3, nmrole: "Testemunha" }
        ],
        installments: [
            { cdinstallment: 75, nrinstallment: 1, dtdue: "2026-05-25", vlbase: 2000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 20.00 },
            { cdinstallment: 76, nrinstallment: 2, dtdue: "2026-06-25", vlbase: 2000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 20.00 },
            { cdinstallment: 77, nrinstallment: 3, dtdue: "2026-07-25", vlbase: 2000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 20.00 },
            { cdinstallment: 78, nrinstallment: 4, dtdue: "2026-08-25", vlbase: 2000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 20.00 },
            { cdinstallment: 79, nrinstallment: 5, dtdue: "2026-09-25", vlbase: 2000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 20.00 },
            { cdinstallment: 80, nrinstallment: 6, dtdue: "2026-10-25", vlbase: 2000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 20.00 },
            { cdinstallment: 81, nrinstallment: 7, dtdue: "2026-11-25", vlbase: 2000.00, vladjusted: null, cdstatus: 1, dtpayment: null, vlpenalty: 10.00, vlinterest: 20.00 }
        ]
    }
]);
print("Contracts inseridos: 12");

// --- NOTIFICATIONS ---
db.notifications.insertMany([
    {
        _id: 2,
        dsmessage: "[LEMBRETE] Aluguel do Contrato #10 vence em 02/05/2026 (R$ 1500,00). Por favor, efetue o pagamento.",
        dtsend: "2026-04-25",
        cdcontract: 10,
        cduser: 4,
        cdnotificationtemplate: null,
        fgchannel: 1
    },
    {
        _id: 3,
        dsmessage: "[LEMBRETE] Aluguel do Contrato #16 vence em 28/04/2026 (R$ 1600,00). Por favor, efetue o pagamento.",
        dtsend: "2026-04-25",
        cdcontract: 16,
        cduser: 16,
        cdnotificationtemplate: null,
        fgchannel: 1
    },
    {
        _id: 4,
        dsmessage: "[LEMBRETE] Aluguel do Contrato #11 vence em 25/04/2026 (R$ 1800,00). Por favor, efetue o pagamento.",
        dtsend: "2026-04-25",
        cdcontract: 11,
        cduser: 6,
        cdnotificationtemplate: null,
        fgchannel: 1
    },
    {
        _id: 5,
        dsmessage: "[LEMBRETE] Aluguel do Contrato #17 vence em 25/04/2026 (R$ 2100,00). Por favor, efetue o pagamento.",
        dtsend: "2026-04-25",
        cdcontract: 17,
        cduser: 18,
        cdnotificationtemplate: null,
        fgchannel: 1
    },
    {
        _id: 6,
        dsmessage: "[LEMBRETE] Aluguel do Contrato #10 vence em 02/05/2026 (R$ 1500,00). Por favor, efetue o pagamento.",
        dtsend: "2026-04-25",
        cdcontract: 10,
        cduser: 4,
        cdnotificationtemplate: 1,
        fgchannel: 1
    },
    {
        _id: 7,
        dsmessage: "[LEMBRETE] Aluguel do Contrato #16 vence em 28/04/2026 (R$ 1600,00). Por favor, efetue o pagamento.",
        dtsend: "2026-04-25",
        cdcontract: 16,
        cduser: 16,
        cdnotificationtemplate: 2,
        fgchannel: 1
    },
    {
        _id: 8,
        dsmessage: "[LEMBRETE] Aluguel do Contrato #11 vence em 25/04/2026 (R$ 1800,00). Por favor, efetue o pagamento.",
        dtsend: "2026-04-25",
        cdcontract: 11,
        cduser: 6,
        cdnotificationtemplate: 3,
        fgchannel: 1
    },
    {
        _id: 9,
        dsmessage: "[LEMBRETE] Aluguel do Contrato #17 vence em 25/04/2026 (R$ 2100,00). Por favor, efetue o pagamento.",
        dtsend: "2026-04-25",
        cdcontract: 17,
        cduser: 18,
        cdnotificationtemplate: 3,
        fgchannel: 1
    },
    {
        _id: 10,
        dsmessage: "[PAGAMENTO CONFIRMADO] Parcela #1 do Contrato #12 foi paga em 25/04/2026. Valor: R$ 1200,00.",
        dtsend: "2026-04-25",
        cdcontract: 12,
        cduser: 7,
        cdnotificationtemplate: 4,
        fgchannel: 1
    },
    {
        _id: 11,
        dsmessage: "[PAGAMENTO CONFIRMADO] Parcela #1 do Contrato #13 foi paga em 25/04/2026. Valor: R$ 2200,00.",
        dtsend: "2026-04-25",
        cdcontract: 13,
        cduser: 9,
        cdnotificationtemplate: 4,
        fgchannel: 1
    }
]);
print("Notifications inseridas: 10");

// --- READJUSTMENT LOGS ---
db.readjustment_logs.insertMany([
    {
        _id: 25,
        cdcontract: 9,
        cdinstallment: 42,
        cdindex: 1,
        vlold: 1200.00,
        vlnew: 1249.71,
        dtreadjustment: "2026-04-25"
    }
]);
print("Readjustment logs inseridos: 1");

// =============================================================================
// FINALIZAÇÃO
// =============================================================================

print("");
print("=== Inicialização do MongoDB concluída com sucesso! ===");
print("Banco: " + DB_NAME);
print("Coleções criadas: users, properties, contracts, notifications, contract_templates, indexes, readjustment_logs, counters");
print("Índices criados para: users, properties, contracts, notifications");
print("Contadores inicializados com valores das sequences do PostgreSQL");
print("Dados de exemplo convertidos e inseridos do insert.sql");
print("");
