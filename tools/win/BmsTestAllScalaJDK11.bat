@echo off
rem !!!close sbt shell & other sbt servers before run otherwise sbt waits for user input !!!

rem to pass Bms test options as system properties run the bat with one or more -Dprop=value parameters:
rem >BmsTest*.bat -DbmsTestDebug=true // to enable bms debug & trace output in tests
rem >BmsTest*.bat -DbmsTestDebug=true -DbmsTestTrace=false // to enable bms debug without trace output in tests
rem >BmsTest*.bat -DbmsTestHeavy=true // to enable hard tests (4 arguments boolean expressions)

rem Changes working directory to the place this bat resides so it may be run from any place:
cd /d "%~dp0"
rem Changes working directory to the project root where STDOUT will be put:
cd ..\..
sbt --java-home "%JAVA11_HOME%" %* +sugar-bms/test >tmpTestAllScalaJDK11.txt

