# Sistema Imobiliário

Sistema de gestão imobiliária desenvolvido em Java com banco de dados MongoDB Atlas (NoSQL nativo).

## Requisitos

- **Java JDK 11+**
- **mongosh** (MongoDB Shell) — para executar o script de seed
- Drivers já incluídos em `dist/lib/` (MongoDB Driver Sync 5.1.0, iText PDF, SLF4J, dnsjava)

---

## 1. Conexão com MongoDB Atlas

O sistema conecta diretamente ao **MongoDB Atlas** (cloud). A connection string está em `src/dao/Conexao.java`:

```java
private static final String CONNECTION_STRING =
    "mongodb+srv://delvossribas:AemBsxTH1hEuFrHT@imobiliaria.f7x4ou9.mongodb.net/?appName=imobiliaria";
```

Não é necessário instalar MongoDB localmente. O Atlas já está configurado e acessível.

### Dependência: dnsjava

O protocolo `mongodb+srv://` utiliza resolução DNS SRV. A biblioteca `dnsjava-3.5.3.jar` (inclusa em `dist/lib/`) é obrigatória para isso funcionar.

---

## 2. Instalação do mongosh (para seed dos dados)

O `mongosh` é necessário apenas para executar o script de inicialização dos dados. Se já tiver instalado, pule esta etapa.

### Linux (Ubuntu/WSL)

```bash
wget -qO- https://downloads.mongodb.com/compass/mongosh-2.2.1-linux-x64.tgz | tar xz
sudo cp mongosh-2.2.1-linux-x64/bin/* /usr/local/bin/
mongosh --version
```

### macOS

```bash
brew install mongosh
```

### Windows

Baixe o instalador em: https://www.mongodb.com/try/download/shell

---

## 3. Inicialização dos Dados (Seed)

Execute o script que cria todas as coleções, índices e dados de exemplo no Atlas:

```bash
cd /root/code/faculdade/Imobiliaria
mongosh "mongodb+srv://delvossribas:AemBsxTH1hEuFrHT@imobiliaria.f7x4ou9.mongodb.net/imobiliaria" database/init-mongo-atlas.js
```

O script realiza:
1. Drop do banco existente (recria do zero)
2. Criação de 8 coleções + índices
3. Inicialização dos contadores de IDs sequenciais
4. Inserção de 18 usuários, 1 imóvel, 12 contratos, 1 template, 2 índices, notificações e logs

> **ATENÇÃO:** O script apaga todos os dados existentes antes de recriar. Use com cuidado.

---

## 4. Compilação e Execução

### Compilar

```bash
cd /root/code/faculdade/Imobiliaria
find src -name "*.java" -not -path "*/test/*" > /tmp/sources.txt
javac -d bin -cp "dist/lib/*" -sourcepath src @/tmp/sources.txt
```

### Executar

```bash
java -cp "bin:dist/lib/*" view.Main
```

> No Windows, troque `:` por `;` no classpath: `java -cp "bin;dist/lib/*" view.Main`

### Comando único (compilar + rodar)

```bash
cd /root/code/faculdade/Imobiliaria
find src -name "*.java" -not -path "*/test/*" > /tmp/sources.txt && javac -d bin -cp "dist/lib/*" -sourcepath src @/tmp/sources.txt && java -cp "bin:dist/lib/*" view.Main
```

---

## 5. Estrutura do Projeto

```
Imobiliaria/
├── src/
│   ├── model/          # Entidades (Users, Properties, Contracts, etc.)
│   ├── dao/            # Acesso a dados MongoDB (Conexao, UserDAO, ContractDAO, etc.)
│   ├── service/        # Lógica de negócio (FinancialService, NotificationService, etc.)
│   ├── view/           # Interface console (Main, UserView, ContractView, ReportView)
│   ├── dto/            # Data Transfer Objects (relatórios)
│   └── test/           # Testes (property-based testing)
├── dist/lib/           # JARs de dependência (MongoDB Driver, iText, SLF4J, dnsjava)
├── database/
│   ├── init-mongo-atlas.js   # Script de seed para MongoDB Atlas
│   └── insert.sql            # Dados originais em SQL (referência)
├── pdfs/               # PDFs gerados pelo sistema
├── bin/                # Classes compiladas (.class)
└── README.md
```

---

## 6. Modelo de Dados (NoSQL Nativo)

O sistema utiliza uma abordagem NoSQL-nativa com **embedding estratégico**:

### Coleções

| Coleção | Descrição | Embedding |
|---------|-----------|-----------|
| `users` | Usuários | Endereço + profissão + contas bancárias embarcados |
| `properties` | Imóveis | Endereço embarcado; tipo/finalidade/status como texto |
| `contracts` | Contratos | Participantes + parcelas embarcados |
| `notifications` | Notificações | Coleção independente |
| `contract_templates` | Modelos | Tópicos + cláusulas embarcados |
| `indexes` | Índices financeiros | Taxas embarcadas como array |
| `readjustment_logs` | Logs de reajuste | Coleção independente |
| `counters` | Contadores de IDs | Auxiliar |

### Diferenças do modelo relacional

| Conceito Relacional | Abordagem MongoDB |
|---------------------|-------------------|
| Tabelas de lookup (tipos, status, profissões) | Texto embarcado diretamente |
| JOIN users + addresses | Endereço como subdocumento em users |
| JOIN contracts + installments + user_contract | Arrays embarcados no contrato |
| JOIN indexes + index_rates | Array `rates` dentro do index |
| FK para countries/states/cities/districts | Campos texto no address (city, state, country) |

---

## 7. Funcionalidades

### Módulo 1 — Cadastros (CRUD)
- Usuários (com validação de CPF, celular, endereço)
- Imóveis (tipo/finalidade/status como texto, endereço embarcado)
- Modelos de Contrato (templates, tópicos, cláusulas)
- Índices Financeiros (IPCA + taxas mensais)
- Contas Bancárias

### Módulo 2 — Processos de Negócio
- Efetivar novo contrato (fluxo completo com geração de PDF)
- Vincular/desvincular proprietário a imóvel
- Alterar/excluir contrato
- Estruturar modelo de contrato
- Processar reajustes mensais (Job automático)
- Processar notificações (4 tipos: lembrete, pagamento, reajuste, vencimento)
- Registrar pagamento de parcela (com cálculo de multa/juros)

### Módulo 3 — Relatórios
1. Relatório Financeiro de Locação
2. Relatório Financeiro de Venda (correção monetária)
3. Relatório de Partes do Contrato
4. Relatório de Reajustes do Ano
5. Listagem Geral de Imóveis (com filtro por bairro)
6. Fluxo de Caixa Mensal e Adimplência (aggregation pipeline)

---

## 8. Geração de PDF

O sistema gera PDFs de contratos preenchidos automaticamente em `pdfs/`. Para visualizar:

```bash
xdg-open pdfs/contrato_preenchido_20.pdf      # Linux
explorer.exe pdfs\contrato_preenchido_20.pdf  # Windows/WSL
open pdfs/contrato_preenchido_20.pdf          # macOS
```

---

## 9. Transações Multi-Documento

O sistema utiliza transações MongoDB para operações atômicas:
- Registro completo de contrato (contrato + atualização de status do imóvel)
- Exclusão de contrato (contrato + notificações vinculadas)

> **Nota:** MongoDB Atlas já suporta transações nativamente (replica set configurado na cloud).

---

## 10. Sobre o Domínio

O domínio abrange a gestão operacional e financeira de uma imobiliária. O sistema centraliza o controle de imóveis (cadastros técnicos, metragens e localização), clientes (proprietários e locatários) e a formalização de negócios através de contratos de locação ou venda.

A solução resolve a fragmentação de dados ao vincular automaticamente a situação financeira (parcelas) ao status do imóvel e às partes envolvidas, permitindo rastreamento de índices de reajuste, notificações automáticas de eventos e gestão de múltiplos proprietários por unidade.
