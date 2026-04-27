# Sistema Imobiliário

## Requisitos

- **Java JDK 17+**
- **PostgreSQL 14+** (instalado localmente, via Docker ou via pgAdmin)
- Drivers já incluídos em `dist/lib/` (PostgreSQL JDBC e iText)

---

## 1. Compilação e Execução

### Windows (cmd / PowerShell)

```bat
javac -encoding UTF-8 -d bin -cp "dist/lib/*" src/model/*.java src/dao/*.java src/view/*.java src/dto/*.java src/service/*.java
java -cp "bin;dist/lib/*" view.Main
```

> No Windows o separador do classpath é `;`.

### Linux / macOS / WSL

```bash
javac -d bin -cp "dist/lib/*" $(find src -name "*.java")
java -cp "bin:dist/lib/*" view.Main
```

> No Linux/macOS o separador do classpath é `:`.

### Configuração da conexão

Ajuste, se necessário, as credenciais em `src/dao/Conexao.java`:

```java
private static final String URL = "jdbc:postgresql://localhost:5433/postgres";
private static final String USER = "postgres";
private static final String SENHA = "postgres";
```

A porta padrão do PostgreSQL é `5432`. Aqui usamos `5433` para combinar com o container Docker descrito abaixo.

---

## 2. Banco de Dados

Há duas formas de preparar o banco. Escolha **uma**.

### Opção A — PostgreSQL via Docker (recomendado)

#### A.1. Criar o container

```bash
docker run -d \
  --name postgres-imobiliaria \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=postgres \
  -p 5433:5432 \
  -v postgres-imobiliaria-data:/var/lib/postgresql/data \
  postgres:16
```

No Windows (cmd), use uma única linha (sem `\`):

```bat
docker run -d --name postgres-imobiliaria -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=postgres -p 5433:5432 -v postgres-imobiliaria-data:/var/lib/postgresql/data postgres:16
```

#### A.2. Restaurar o schema (backup) e os dados (insert)

Rode a partir da raiz do projeto:

**Linux / macOS / WSL:**

```bash
docker exec -i postgres-imobiliaria psql -U postgres -d postgres < database/backup.sql
docker exec -i postgres-imobiliaria psql -U postgres -d postgres < database/insert.sql
```

**Windows (PowerShell):**

```powershell
Get-Content database/backup.sql | docker exec -i postgres-imobiliaria psql -U postgres -d postgres
Get-Content database/insert.sql | docker exec -i postgres-imobiliaria psql -U postgres -d postgres
```

**Windows (cmd):**

```bat
docker exec -i postgres-imobiliaria psql -U postgres -d postgres < database\backup.sql
docker exec -i postgres-imobiliaria psql -U postgres -d postgres < database\insert.sql
```

#### A.3. Verificar

```bash
docker exec -it postgres-imobiliaria psql -U postgres -d postgres -c "\dt"
```

Você deve ver a lista de tabelas (`contracts`, `properties`, `users`, etc.).

#### A.4. Comandos úteis

```bash
docker logs -f postgres-imobiliaria             # ver logs
docker stop postgres-imobiliaria                # parar
docker start postgres-imobiliaria               # iniciar de novo
docker exec -it postgres-imobiliaria psql -U postgres -d postgres   # abrir psql
docker rm -f postgres-imobiliaria               # remover container
docker volume rm postgres-imobiliaria-data      # apagar todos os dados (cuidado!)
```

---

### Opção B — PostgreSQL via pgAdmin (interface gráfica)

#### B.1. Criar o servidor de conexão

1. Abra o **pgAdmin**.
2. No painel esquerdo, clique com o direito em **Servers → Register → Server...**.
3. Aba **General**: preencha **Name** com `Imobiliaria`.
4. Aba **Connection**:
   - **Host name/address**: `localhost`
   - **Port**: `5432` (ou `5433` se estiver usando o container Docker)
   - **Maintenance database**: `postgres`
   - **Username**: `postgres`
   - **Password**: `postgres` (marque "Save password")
5. Clique em **Save**.

#### B.2. Restaurar o schema (`backup.sql`)

1. Expanda **Servers → Imobiliaria → Databases → postgres**.
2. Clique com o direito em **postgres → Query Tool**.
3. Na barra superior do Query Tool, clique no ícone de pasta (**Open File**) e selecione `database/backup.sql`.
4. Clique em **Execute/Run** (botão ▶ ou `F5`).
5. Atualize (clique direito em **Schemas → public → Tables → Refresh**) — as tabelas devem aparecer.

#### B.3. Carregar os dados (`insert.sql`)

1. Ainda no **Query Tool** apontado para o banco `postgres`.
2. Abra `database/insert.sql` (ícone de pasta).
3. Execute (▶ / `F5`).
4. Verifique consultando, por exemplo:

   ```sql
   SELECT COUNT(*) FROM users;
   SELECT COUNT(*) FROM properties;
   ```

> Se a sua instância roda na porta `5432`, ajuste `URL` em `Conexao.java` para `jdbc:postgresql://localhost:5432/postgres`.

---

## 3. Visualizar PDFs gerados (dentro do VS Code)

O sistema gera PDFs em `pdfs/` (ex.: `pdfs/contrato_preenchido_20.pdf`). PDFs são arquivos binários — abrir com `cat` mostra apenas bytes ilegíveis.

Para visualizá-los **dentro do VS Code**, instale a extensão:

- **vscode-pdf** (autor: *tomoki1207*)
  - Marketplace: <https://marketplace.visualstudio.com/items?itemName=tomoki1207.pdf>
  - Ou via terminal:
    ```bash
    code --install-extension tomoki1207.pdf
    ```

Depois disso, basta clicar no arquivo `.pdf` no Explorer do VS Code que ele será renderizado.

Alternativas fora do VS Code:

```bash
xdg-open pdfs/contrato_preenchido_20.pdf      # Linux
explorer.exe pdfs\contrato_preenchido_20.pdf  # Windows / WSL
open pdfs/contrato_preenchido_20.pdf          # macOS
```

---

## 4. Introdução explicativa do domínio de informação escolhido

O domínio escolhido abrange a gestão operacional e financeira de uma imobiliária.
O sistema visa centralizar o controle de imóveis (cadastros técnicos, metragens e localização), 
clientes (proprietários e locatários) e a formalização de negócios através de contratos de locação ou venda.
A solução proposta resolve a fragmentação de dados ao vincular automaticamente a situação financeira (parcelas/comissões) 
ao status do imóvel e às partes envolvidas, permitindo o rastreamento de índices de reajuste, 
notificações de eventos e a gestão de múltiplos proprietários por unidade, 
garantindo integridade referencial em todo o ciclo de vida do ativo imobiliário.
