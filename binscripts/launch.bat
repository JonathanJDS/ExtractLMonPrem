set DIR=%~dp0
cd %DIR%
C:\PricerSOLFE\Java\bin\java.exe -Dlog4j.configuration=file:%DIR%log4j.properties -jar %DIR%MigrationCastorama.jar prepare

pause