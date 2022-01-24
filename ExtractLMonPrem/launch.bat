set DIR=%~dp0
cd %DIR%
C:\Pricer\Java\bin\java.exe -Dlog4j.configuration=file:%DIR%log4j.properties -jar %DIR%ExtractLMonPrem.jar prepare

pause