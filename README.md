# Sistema Imobiliário

Sistema de gestão imobiliária desenvolvido em Java com banco de dados MongoDB.

## Requisitos

- **Java JDK 17+**
- **MongoDB 7.x** (via Docker ou instalação local)
- **mongosh** (MongoDB Shell) — para executar o script de inicialização
- Drivers já incluídos em `dist/lib/` (MongoDB Driver Sync 5.1.0 e iText)

---

## 1. Instalação do MongoDB

Escolha **uma** das opções abaixo.

### Opção A — MongoDB via Docker (recomendado)

#### A.1. Criar o container com Replica Set

O sistema utiliza transações multi-documento, que requerem um replica set configurado.

```bash
docker run -d \
  --name mongo-imobiliaria \
  -p 27017:27017 \
  mongo:7 --replSet rs0
```

No Windows (cmd), use uma única linha (sem `\`):

```bat
docker run -d --name mongo-imobiliaria -p 27017:27017 mongo:7 --replSet rs0
```

#### A.2. Inicializar o Replica Set

```bash
docker exec mongo-imobiliaria mongosh --eval "rs.initiate()"
```

Aguarde alguns segundos até o replica set estar pronto. Você pode verificar o status com:

```bash
docker exec mongo-imobiliaria mongosh --eval "rs.status()"
```

#### A.3. Comandos úteis

```bash
docker logs -f mongo-imobiliaria               # ver logs
docker stop mongo-imobiliaria                   # parar
docker start mongo-imobiliaria                  # iniciar de novo
docker exec -it mongo-imobiliaria mongosh       # abrir mongosh interativo
docker rm -f mongo-imobiliaria                  # remover container (dados perdidos)
```

---

### Opção B — MongoDB instalação local (Linux/macOS)

#### B.1. Instalar o MongoDB 7.x

**Ubuntu/Debian:**

```bash
# Importar chave GPG
curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg --dearmor

# Adicionar repositório
echo "deb [ signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu $(lsb_release -cs)/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list

# Instalar
sudo apt-get update
sudo apt-get install -y mongodb-org
```

**macOS (Homebrew):**

```bash
brew tap mongodb/brew
brew install mongodb-community@7.0
```

#### B.2. Iniciar com Replica Set

O MongoDB deve ser iniciado com suporte a replica set para que transações funcionem:

```bash
mongod --replSet rs0 --dbpath /var/lib/mongodb --port 27017
```

Em outro terminal, inicialize o replica set:

```bash
mongosh --eval "rs.initiate()"
```

> **Nota:** Para iniciar automaticamente com replica set, adicione `replSetName: rs0` ao arquivo de configuração `/etc/mongod.conf` na seção `replication`.

---

## 2. Inicialização do Banco de Dados

Após o MongoDB estar rodando com replica set configurado, execute o script de inicialização:

```bash
mongosh database/init-mongo.js
```

Ou, se estiver usando Docker:

```bash
docker exec -i mongo-imobiliaria mongosh < database/init-mongo.js
```

O script realiza:
- Verificação se o banco `imobiliaria` já existe (evita perda de dados acidental)
- Criação de todas as coleções necessárias
- Criação dos índices (incluindo índices únicos)
- Inicialização dos contadores de IDs sequenciais
- Inserção dos dados de exemplo

> **Atenção:** Se o banco já existir, o script aborta com um aviso. Para recriar do zero, primeiro remova o banco manualmente: `mongosh --eval "use imobiliaria; db.dropDatabase()"`

---

## 3. Compilação e Execução

### Linux / macOS / WSL

```bash
javac -d bin -cp "dist/lib/*" $(find src -name "*.java")
java -cp "bin:dist/lib/*" view.Main
```

> No Linux/macOS o separador do classpath é `:`.

### Windows (cmd / PowerShell)

```bat
javac -encoding UTF-8 -d bin -cp "dist/lib/*" src/model/*.java src/dao/*.java src/view/*.java src/dto/*.java src/service/*.java
java -cp "bin;dist/lib/*" view.Main
```

> No Windows o separador do classpath é `;`.

### Configuração da conexão

A conexão com o MongoDB é configurada em `src/dao/Conexao.java`:

```java
private static final String HOST = "localhost";
private static final int PORT = 27017;
private static final String DATABASE = "imobiliaria";
```

Por padrão, conecta ao MongoDB na porta `27017` sem autenticação (ambiente de desenvolvimento).

---

## 4. Configuração de Replica Set (Transações)

O sistema utiliza transações multi-documento do MongoDB para garantir consistência em operações que envolvem múltiplas coleções (ex.: registro de contrato + atualização de status do imóvel). Transações requerem um **replica set** configurado.

### Por que Replica Set?

- Transações multi-documento só funcionam com replica set ou sharded cluster
- Mesmo em ambiente de desenvolvimento com um único nó, é necessário configurar como single-node replica set
- Operações dentro de um mesmo documento (parcelas dentro do contrato, por exemplo) são atômicas por natureza e não precisam de transação

### Verificar se o Replica Set está ativo

```bash
mongosh --eval "rs.status().ok"
```

Se retornar `1`, o replica set está funcional.

---

## 5. Estrutura das Coleções (Modelo de Dados)

O sistema utiliza um modelo orientado a documentos com embedding estratégico para reduzir joins:

### Coleções Principais

| Coleção | Descrição | Estratégia |
|---------|-----------|------------|
| `users` | Usuários (proprietários, locatários, corretores) | Endereço e profissão embarcados |
| `properties` | Imóveis | Endereço embarcado; tipo/finalidade/status como texto |
| `contracts` | Contratos de locação/venda | Parcelas e participantes embarcados |
| `notifications` | Notificações do sistema | Coleção independente com referências |
| `contract_templates` | Modelos de contrato | Tópicos e cláusulas embarcados |
| `indexes` | Índices financeiros (IPCA, IGP-M) | Taxas embarcadas como array |
| `readjustment_logs` | Logs de reajuste | Coleção independente |
| `counters` | Contadores de IDs sequenciais | Auxiliar para geração de IDs |

### Estrutura dos Documentos

#### `users`
```json
{
  "_id": 1,
  "nmuser": "João Silva",
  "dtbirth": "1990-05-20",
  "fgdocument": true,
  "document": "12345678900",
  "nrcellphone": "47999999999",
  "dsissuingbody": "SSP",
  "address": {
    "cdzipcode": "89200000",
    "nmstreet": "Rua XV de Novembro",
    "nraddress": "1000",
    "dscomplement": "Sala 2",
    "district": "Centro",
    "city": "Joinville",
    "state": "SC",
    "country": "Brasil"
  },
  "occupation": "Analista de Sistemas",
  "bank_accounts": [
    { "nragency": "0001", "nraccount": "12345-6", "nrpixkey": "joao@email.com" }
  ]
}
```

#### `properties`
```json
{
  "_id": 1,
  "nrregistration": "MAT-99887",
  "dsdescription": "Apto com 2 quartos no centro",
  "vltotalarea": 65.50,
  "address": {
    "cdzipcode": "89200000",
    "nmstreet": "Rua XV de Novembro",
    "nraddress": "1000",
    "district": "Centro",
    "city": "Joinville",
    "state": "SC",
    "country": "Brasil"
  },
  "type": "Apartamento",
  "purpose": "Residencial",
  "status": "Alugado",
  "owners": [1]
}
```

#### `contracts`
```json
{
  "_id": 9,
  "dtcreation": "2025-04-25",
  "dstitle": "Contrato Locação",
  "cdtemplate": 1,
  "cdproperty": 1,
  "cdindex": 1,
  "dtlimit": "2026-04-25",
  "cdstatus": 1,
  "notary": null,
  "participants": [
    { "cduser": 1, "cdrole": 2, "nmrole": "Locador" },
    { "cduser": 2, "cdrole": 1, "nmrole": "Locatário" }
  ],
  "installments": [
    {
      "cdinstallment": 31,
      "nrinstallment": 1,
      "dtdue": "2025-05-01",
      "vlbase": 1200.00,
      "vladjusted": 0.00,
      "cdstatus": 2,
      "dtpayment": "2026-04-25",
      "vlpenalty": 10.00,
      "vlinterest": 1.00
    }
  ]
}
```

#### `contract_templates`
```json
{
  "_id": 1,
  "nmtemplate": "Contrato de Locação Padrão",
  "dsversion": "1.0",
  "fgactive": true,
  "topics": [
    {
      "cdtopic": 1,
      "nmtopic": "Do Objeto da Locação",
      "nrorder": 1,
      "clauses": [
        { "cdclause": 1, "dstext": "O locador cede ao locatário...", "nrorder": 1 }
      ]
    }
  ]
}
```

#### `indexes`
```json
{
  "_id": 1,
  "nmindex": "IPCA",
  "rates": [
    { "refmonth": 4, "refyear": 2024, "vlrate": 0.0038 },
    { "refmonth": 5, "refyear": 2024, "vlrate": 0.0046 }
  ]
}
```

### Mapeamento PostgreSQL → MongoDB

| Tabelas PostgreSQL originais | Destino no MongoDB |
|------------------------------|-------------------|
| countries, states, cities, districts | Embarcados em `address` |
| addresses | Embarcado em `users` e `properties` |
| occupations | Campo texto `users.occupation` |
| users | Coleção `users` |
| bank_accounts | Array em `users.bank_accounts` |
| properties, property_types, property_purposes, property_status | Coleção `properties` (tipos como texto) |
| properties_users | Array `properties.owners` |
| contracts | Coleção `contracts` |
| user_contract + roles | Array `contracts.participants` |
| installments | Array `contracts.installments` |
| notaries | Subdocumento `contracts.notary` |
| notifications | Coleção `notifications` |
| contract_templates + topics + clauses | Coleção `contract_templates` (tudo embarcado) |
| indexes + index_rates | Coleção `indexes` (rates embarcado) |

---

## 6. Executar Testes

Os testes utilizam o framework [jqwik](https://jqwik.net/) para property-based testing e JUnit 5 para testes unitários.

### Compilar testes

```bash
javac -d bin -cp "dist/lib/*:dist/lib/test/*" $(find src -name "*.java")
```

### Executar testes

```bash
java -cp "bin:dist/lib/*:dist/lib/test/*" org.junit.platform.console.ConsoleLauncher --scan-classpath
```

> **Nota:** Os testes requerem uma instância MongoDB rodando localmente com replica set configurado.

---

## 7. Visualizar PDFs gerados (dentro do VS Code)

O sistema gera PDFs em `pdfs/` (ex.: `pdfs/contrato_preenchido_20.pdf`). Para visualizá-los **dentro do VS Code**, instale a extensão:

- **vscode-pdf** (autor: *tomoki1207*)
  ```bash
  code --install-extension tomoki1207.pdf
  ```

Alternativas fora do VS Code:

```bash
xdg-open pdfs/contrato_preenchido_20.pdf      # Linux
explorer.exe pdfs\contrato_preenchido_20.pdf  # Windows / WSL
open pdfs/contrato_preenchido_20.pdf          # macOS
```

---

## 8. Introdução explicativa do domínio de informação escolhido

O domínio escolhido abrange a gestão operacional e financeira de uma imobiliária.
O sistema visa centralizar o controle de imóveis (cadastros técnicos, metragens e localização), 
clientes (proprietários e locatários) e a formalização de negócios através de contratos de locação ou venda.
A solução proposta resolve a fragmentação de dados ao vincular automaticamente a situação financeira (parcelas/comissões) 
ao status do imóvel e às partes envolvidas, permitindo o rastreamento de índices de reajuste, 
notificações de eventos e a gestão de múltiplos proprietários por unidade, 
garantindo integridade referencial em todo o ciclo de vida do ativo imobiliário.
