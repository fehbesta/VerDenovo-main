@echo off
echo Executando VerDenovo Backend...
echo.

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Java nao encontrado!
    echo Instale Java 17 ou superior: https://adoptium.net/
    pause
    exit /b 1
)

if not exist .env (
    echo AVISO: Arquivo .env nao encontrado! Copie .env.example para .env e preencha os valores.
    pause
    exit /b 1
)

echo Carregando variaveis de ambiente do .env...
for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
    if not "%%A"=="" if not "%%A:~0,1%%"=="#" set "%%A=%%B"
)

if exist mvnw.cmd (
    echo Usando Maven Wrapper...
    mvnw.cmd spring-boot:run
) else (
    mvn spring-boot:run
)

pause
