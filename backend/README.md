# VerDenovo Backend - Spring Boot

Backend da aplicacao VerDenovo desenvolvido em Spring Boot.

## Requisitos

- JDK 17 recomendado. JDK mais novo pode funcionar, mas o projeto compila com `java.version=17`.
- Maven Wrapper incluido no projeto: `mvnw.cmd`.
- Frontend local esperado em `http://localhost:5173`.

## Banco usado pelo projeto

O projeto originalmente usa SQL Server. O suporte a SQL Server foi mantido no perfil padrao por meio do driver `mssql-jdbc`.

Para testes rapidos, existe tambem o perfil `local`, que usa H2 em memoria. Nao remova esse perfil: ele e util para validar frontend/backend sem depender do banco real.

## Rodar com H2 local

```powershell
cd C:\Users\NAFELU\Desktop\Projeto\VerDenovo-main\VerDenovo-main\backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

Admin local criado automaticamente:

- Email: `admin@verdenovo.local`
- Senha: `admin123`

O banco H2 e apagado quando o backend para.

## Rodar com SQL Server persistente

Configure variaveis de ambiente antes de iniciar:

```powershell
cd C:\Users\NAFELU\Desktop\Projeto\VerDenovo-main\VerDenovo-main\backend

$env:DB_URL="jdbc:sqlserver://SEU_HOST:1433;databaseName=VerdNovo;encrypt=true;trustServerCertificate=true"
$env:DB_USERNAME="seu_usuario"
$env:DB_PASSWORD="sua_senha"
$env:JWT_SECRET="troque_por_uma_chave_longa_com_64_caracteres_ou_mais"
$env:ADMIN_EMAIL="admin@verdenovo.com"
$env:ADMIN_SENHA="troque_esta_senha"
$env:CORS_ALLOWED_ORIGINS="http://localhost:5173"
$env:FRONTEND_URL="http://localhost:5173"

.\mvnw.cmd spring-boot:run
```

Exemplo para o banco Somee do VerDenovo:

```powershell
cd C:\Users\NAFELU\Desktop\Projeto\VerDenovo-main\VerDenovo-main\backend

$env:DB_HOST="VerDenovo.mssql.somee.com"
$env:DB_NAME="VerDenovo"
$env:DB_URL="jdbc:sqlserver://VerDenovo.mssql.somee.com:1433;databaseName=VerDenovo;encrypt=true;trustServerCertificate=true"
$env:DB_USERNAME="fernando14112008@gmail.com"
$env:DB_PASSWORD="cole_a_senha_aqui_somente_na_sessao"
$env:JWT_SECRET="troque_por_uma_chave_longa_com_64_caracteres_ou_mais"
$env:ADMIN_EMAIL="admin@verdenovo.com"
$env:ADMIN_SENHA="troque_esta_senha"
$env:CORS_ALLOWED_ORIGINS="http://localhost:5173"

.\mvnw.cmd spring-boot:run
```

Para um banco novo, use `database.sql` como referencia de schema. Para um banco antigo do projeto, revise e execute `migration.sql` no SQL Server antes de subir a aplicacao.

## Configurar email SMTP

A recuperacao de senha envia um codigo de 6 digitos por email. Configure SMTP por variaveis de ambiente:

```powershell
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="seu_email@gmail.com"
$env:MAIL_PASSWORD="sua_app_password"
$env:MAIL_SMTP_AUTH="true"
$env:MAIL_STARTTLS_ENABLE="true"
```

Para Gmail, use uma App Password, nao a senha normal da conta. Ative verificacao em duas etapas na conta Google, gere uma senha de app e coloque esse valor em `MAIL_PASSWORD`.

Se `MAIL_USERNAME` estiver vazio, a API retorna uma mensagem amigavel informando que o servico de email nao esta configurado. Nenhum codigo ou token e retornado pela API.

Voce pode testar email real tambem com H2 local:

```powershell
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="seu_email@gmail.com"
$env:MAIL_PASSWORD="sua_app_password"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

## Variaveis de ambiente

Obrigatorias no perfil padrao:

- `DB_URL`: URL JDBC do SQL Server.
- `DB_USERNAME`: usuario do banco.
- `DB_PASSWORD`: senha do banco.
- `JWT_SECRET`: chave longa usada para assinar tokens JWT.
- `ADMIN_EMAIL`: email do admin inicial.
- `ADMIN_SENHA`: senha do admin inicial.

Recomendadas:

- `CORS_ALLOWED_ORIGINS`: origens permitidas, separadas por virgula. Exemplo: `http://localhost:5173,https://seusite.com`.
- `FRONTEND_URL`: URL do frontend usada em fluxos de email.
- `MAIL_HOST`: servidor SMTP.
- `MAIL_PORT`: porta SMTP.
- `MAIL_USERNAME`: usuario/remetente SMTP.
- `MAIL_PASSWORD`: senha/app password SMTP.
- `MAIL_SMTP_AUTH`: geralmente `true`.
- `MAIL_STARTTLS_ENABLE`: geralmente `true`.

Tambem existe `backend/.env.example` como modelo. Copie para `.env`, preencha localmente e rode `run.bat` se preferir usar arquivo de ambiente. Nao versione o arquivo `.env`.

## Endpoints principais

- POST `/api/auth/login`
- POST `/api/auth/cadastro`
- POST `/api/auth/recuperar-senha`
- POST `/api/auth/verificar-codigo`
- POST `/api/auth/redefinir-senha`
- GET `/api/pontos`
- GET `/api/pontos/todos`
- GET `/api/pontos/pendentes`
- GET `/api/pontos/meus`
- POST `/api/pontos`
- PUT `/api/pontos/{id}`
- DELETE `/api/pontos/{id}`
- POST `/api/pontos/login`
- GET `/api/categorias`

## Teste manual do fluxo de senha

1. Rode backend com SMTP configurado.
2. Cadastre um usuario ou ponto com um email real.
3. No frontend, abra Recuperar Senha.
4. Informe o email.
5. Confira o codigo recebido na caixa de entrada.
6. Digite o codigo no site.
7. Redefina a senha.
8. Tente fazer login com a nova senha.

O mesmo endpoint atende Usuario e Ponto. Senhas sao salvas com hash BCrypt, e campos como senha, codigo e token de reset nao sao expostos no JSON.

## Teste rapido das APIs

```powershell
Invoke-RestMethod http://localhost:8080/api/pontos
Invoke-RestMethod http://localhost:8080/api/categorias
```

Se retornar JSON sem erro, o backend esta respondendo.
