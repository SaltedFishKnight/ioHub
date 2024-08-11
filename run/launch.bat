@ECHO OFF
@TITLE ioHub
CD /D %~dp0
CD ..\..\..\
jdk-23+7\bin\java.exe @mods\ioHub\run\config.txt
if %errorlevel% neq 0 pause