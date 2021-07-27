@echo off
rem !!!close sbt shell & other sbt servers before run otherwise sbt waits for user input !!!

rem to pass system properties run the bat with one or more -Dprop=value parameters

rem Changes working directory to the place this bat resides so it may be run from any place:
cd /d "%~dp0"
rem Changes working directory to the project root where STDOUT will be put:
cd ..\..
@echo on
@echo === Running sbt --java-home "%JAVA8_HOME%" %* +test >tmpFeaturesTestAllScalaAllJDK.txt
call sbt --java-home "%JAVA8_HOME%" %* +test >>tmpFeaturesTestAllScalaAllJDK.txt
@echo === Running sbt --java-home "%JAVA11_HOME%" %* +test >>tmpFeaturesTestAllScalaAllJDK.txt
call sbt --java-home "%JAVA11_HOME%" %* +test >>tmpFeaturesTestAllScalaAllJDK.txt
@echo === Running sbt %* +test === for default JDK %JAVA_HOME% >>tmpFeaturesTestAllScalaAllJDK.txt
call sbt %* +test >>tmpFeaturesTestAllScalaAllJDK.txt

