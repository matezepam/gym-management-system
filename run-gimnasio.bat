@echo off
setlocal
cd /d "%~dp0"

set "MAVEN_VERSION=3.9.11"
set "TOOLS_DIR=%CD%\.tools"
set "MAVEN_DIR=%TOOLS_DIR%\apache-maven-%MAVEN_VERSION%"
set "MAVEN_ZIP=%TOOLS_DIR%\apache-maven-%MAVEN_VERSION%-bin.zip"
set "MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"

where java >nul 2>nul
if errorlevel 1 (
  echo [ERROR] Java no esta instalado o no esta en PATH.
  pause
  exit /b 1
)

if not exist "%MAVEN_DIR%\bin\mvn.cmd" (
  echo.
  echo ========================================
  echo Preparando Maven automaticamente...
  echo No requiere permisos de administrador.
  echo ========================================
  echo.
  if not exist "%TOOLS_DIR%" mkdir "%TOOLS_DIR%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%'"
  if errorlevel 1 (
    echo.
    echo [ERROR] No se pudo descargar Maven. Revisa la conexion a Internet.
    pause
    exit /b 1
  )
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%TOOLS_DIR%' -Force"
  if errorlevel 1 (
    echo.
    echo [ERROR] No se pudo descomprimir Maven.
    pause
    exit /b 1
  )
)

set "MAVEN_HOME=%MAVEN_DIR%"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

echo.
echo ========================================
echo Iniciando Sistema Web Gimnasio
 echo Abre luego: http://localhost:8080
 echo ADMIN: admin / Admin123*
 echo CLIENTE: cliente / Cliente123*
echo ========================================
echo.

call "%MAVEN_DIR%\bin\mvn.cmd" spring-boot:run

echo.
echo La aplicacion se detuvo.
pause
endlocal
