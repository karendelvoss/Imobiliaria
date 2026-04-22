# Compilar
javac -encoding UTF-8 -d bin -cp "dist/lib/*" src/model/*.java src/dao/*.java src/view/*.java
# Rodar
java -cp "bin;dist/lib/*" view.Main


# Instruções de Compilação e Execução:

Requisitos: Java JDK 17+ e PostgreSQL 14+.
Banco de Dados: Execute o script de backup fornecido (backup_banco.sql) para criar a estrutura e os dados iniciais.
Configuração: Ajuste as credenciais no arquivo Conexao.java (URL, usuário e senha do banco).
Execução: Compile o projeto via Maven/IDE e execute a classe view.Main.

# Links do Projeto:

https://github.com/karendelvoss/Imobiliaria

# Introdução explicativa do domínio de informação escolhido

O domínio escolhido abrange a gestão operacional e financeira de uma imobiliária.
O sistema visa centralizar o controle de imóveis (cadastros técnicos, metragens e localização), 
clientes (proprietários e locatários) e a formalização de negócios através de contratos de locação ou venda.
A solução proposta resolve a fragmentação de dados ao vincular automaticamente a situação financeira (parcelas/comissões) 
ao status do imóvel e às partes envolvidas, permitindo o rastreamento de índices de reajuste, 
notificações de eventos e a gestão de múltiplos proprietários por unidade, 
garantindo integridade referencial em todo o ciclo de vida do ativo imobiliário.


# ESQUEMA CONCEITUAL > NÃO ALTERADO 

# ESQUEMA LÓGICO > ANEXO (ESQUEMA LÓGICO SEGUNDO BD)

# BACKUP BD > ANEXO (BACKUPBANCOKAREN.SQL)

# DICIONARIO DE DADOS > ANEXO (DICIONARIO DE DADOS 2204.XLSX)
