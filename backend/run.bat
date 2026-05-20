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

if exist .env (
    echo Carregando variaveis de ambiente do .env...
    for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
        if not "%%A"=="" if not "%%A:~0,1%%"=="#" set "%%A=%%B"
    )
) else (
    echo AVISO: Arquivo .env nao encontrado. Usando variaveis de ambiente da sessao atual.
)

if "%DB_URL%"=="" (
    echo ERRO: DB_URL nao configurado.
    pause
    exit /b 1
)

if "%DB_USERNAME%"=="" (
    echo ERRO: DB_USERNAME nao configurado.
    pause
    exit /b 1
)

if "%DB_PASSWORD%"=="" (
    echo ERRO: DB_PASSWORD nao configurado.
    pause
    exit /b 1
)

if "%JWT_SECRET%"=="" (
    echo ERRO: JWT_SECRET nao configurado.
    pause
    exit /b 1
)

if "%ADMIN_EMAIL%"=="" (
    echo ERRO: ADMIN_EMAIL nao configurado.
    pause
    exit /b 1
)

if "%ADMIN_SENHA%"=="" (
    echo ERRO: ADMIN_SENHA nao configurado.
    pause
    exit /b 1
)

if exist mvnw.cmd (
    echo Usando Maven Wrapper...
    mvnw.cmd spring-boot:run
) else (
    mvn spring-boot:run
)

pause
