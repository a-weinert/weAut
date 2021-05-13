@echo.
@set whereIwasCalledFrom=%CD%

@echo Translate  Frame4J
@echo translateIt.bat $Revision: 46 $,  $Date: 2021-05-11 19:01:23 +0200 (Di, 11 Mai 2021) $
@Echo Copyright (c)  2010 - 2015, 2018, 2021 Albrecht Weinert
@echo.

@REM usage: change to the beautified sources directory; see CopBea.bat
@REM and call this. 
@REM Must never be used inside repositories / local working copies.
@REM Needs a consistent frame4j sources.txt    

@REM For some of Oracle JDK versions the JDK installation pointed to by
@REM jdkUse must be a pure JDK without installed Frame4j extensions. Due
@REM to a bug some JDKs can't (partly) compile what's in extension jars.

@REM This script is NOT for Java 9+  --  hopefully here is Java 8
@if %JAVA_HOME%X==X goto :envError
@if not exist %JAVA_HOME%\bin\javac.exe goto :envError
@REM set jdkUse=%JAVA_HOME%\bin
@set jdkUse=%JAVA_HOME%_pure\bin
@REM  if not exist %jdkUse%\javac.exe set jdkUse=%JAVA_HOME%\bin

@if not exist .\.svn @goto :start_0
@echo do not use this in a repository or ^(SVN^) local working copy
@goto :callError

:start_0
@md build
@java de.frame4j.Del -empty  .\build\+.+
@if ERRORLEVEL 1 goto :error

@REM next build directories are made here instead by javac cause of the 
@REM ugly Java8 / 9 javac locking bug
md build\de
md build\de\frame4j
md build\de\frame4j\util
md build\de\frame4j\graf
md build\de\frame4j\io
md build\de\frame4j\demos
md build\de\frame4j\math
md build\de\frame4j\net
md build\de\frame4j\xml
md build\de\frame4j\text
md build\de\frame4j\time
md build\de\weAut
md build\de\weAut\demos

@set transOptions=-d build
@echo. 
@echo.
@echo transOptions=%transOptions%
@echo. 

%jdkUse%\javac.exe %transOptions% @sources.txt
@if errorlevel 1 goto :errorCopileAll

@Echo.
@echo Make frame4j.jar, catErgWe.jar (start deleting the JUnitTests)
java.exe de.frame4j.Del -r -v build\PackageTes*.class

cd build
xCopy /Y  ..\de\frame4j\util\*.properties  .\de\frame4j\util\
xCopy  /Y ..\de\frame4j\util\*.xml         .\de\frame4j\util\
xCopy /Y  ..\de\frame4j\*.properties       .\de\frame4j\
xCopy  /Y ..\de\frame4j\net\*.xml          .\de\frame4j\net\
xcopy  /Y ..\de\frame4j\net\*.properties   .\de\frame4j\net\
xCopy /Y  ..\de\frame4j\graf\*.png         .\de\frame4j\graf\
xCopy  /Y ..\de\frame4j\demos\*.properties .\de\frame4j\demos\
xCopy  /Y ..\de\frame4j\demos\*.gif        .\de\frame4j\demos\
xCopy  /Y ..\de\weAut\demos\*.properties   .\de\weAut\demos\
xCopy  /Y ..\de\weAut\*.properties   .\de\weAut\

@REM  ----------- trans-code .properties from development platform to UTF-8
for /R %%k in (*.properties) do java.exe de.frame4j.UCopy -outEncode UTF-8  %%k

@Echo Building frame4j.jar
%jdkUse%\jar.exe -cfvm  ..\frame4j-not-signed.jar ..\Frame4J.mf .
cd ..
%jdkUse%\jar.exe -i   frame4j-not-signed.jar 
@Echo.
@Echo Do sign frame4j-not-signed.jar  as frame4j.jar 
@Echo.
@goto :end

:callError
@echo error in call (exit 999)
@exit /b 999
:envError
@echo error in environment (exit 9999)
@exit /b 9999

:errorCopileAll 
@Echo Fatal Error: Java compilation by @sources.txt failed
exit /B 3

:error
@echo.
@echo operation failed !
:end
@if ERRORLEVEL 1 @echo exit %errorlevel%
@cd /D %whereIwasCalledFrom%

@echo.

