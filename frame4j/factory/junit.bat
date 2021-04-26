@echo on
@Echo.
@Echo junit.bat, V$Revision: 1 $, $Date: 2017-01-14 13:19:07 +0100 (Sa, 14 Jan 2017) $, A. Weinert
@Echo.
@if not %JAVA_HOME%X==X goto :jHset
if exist C:\programme\jdk\bin\java.exe  set JAVA_HOME=C:\Programme\jdk
@if not %JAVA_HOME%X==X goto :jHset
@Echo set JAVA_HOME ! 
@goto :end

:jHset
@Echo.
@echo %JAVA_HOME%\bin\java (+junit) %*
@Echo.

@%JAVA_HOME%\bin\java.exe -classpath C:\programme\junit\junit.jar;.  %*
@if not ERRORLEVEL 1 goto :end 

@Echo.
@echo J U N I T - E r r o r
@%JAVA_HOME%\bin\java -version


:end
@Echo.