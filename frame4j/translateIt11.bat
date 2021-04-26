@echo.
@set whereIwasCalledFrom=%CD%

@echo Translate  Frame4J with Java11 
@echo translateIt11.bat $Revision: 31 $,  $Date: 2021-03-12 16:58:39 +0100 (Fr, 12 Mrz 2021) $
@Echo Copyright (c)  2018   Albrecht Weinert
@echo.

@REM for both local and server -- weinert-automation.de -- use.

@REM usage: change to the beautified sources directory; see CopBea.bat
@REM and call this. 
@REM Must never be used inside repositories / local working copies.    


@set jdkUse=C:\util\jdk11\bin
@if not exist %jdkUse%\javac.exe goto :envError

@if not exist .\.svn @goto :start_0
@echo do not use this in a repository or (SVN) local working copy
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
@set transOptions=-d build



:in11
@set jdkUseVers=11
@echo. 
@echo killing what is killed anyway  by Java 11 (9) * * 
del /S de\PackageTest.java
del de\frame4j\io\ShowPorts.java
del ShowPorts.java
del de\frame4j\SendMail.*
del   SendMail.java
del Test.java


:in89
@echo.
@echo transOptions=%transOptions%
@echo. 


%jdkUse%\javac.exe %transOptions%  de\frame4j\text\*.java de\frame4j\util\*.java 
@if errorlevel 1 goto :error2


%jdkUse%\javac.exe %transOptions%  de\frame4j\time\*.java  de\frame4j\graf\*.java
@if errorlevel 1 goto :error2

%jdkUse%\javac.exe %transOptions%  de\frame4j\automation\*.java 
@if errorlevel 1 goto :error2

@echo.
@echo %jdkUse%\javac.exe %transOptions% de\frame4j\io\*.java
%jdkUse%\javac.exe %transOptions% de\frame4j\io\*.java
@if errorlevel 1 goto :error2


%jdkUse%\javac.exe  %transOptions%  de\frame4j\*.java
@if errorlevel 1 goto :error2


%jdkUse%\javac.exe  %transOptions%  de\frame4j\demos\*.java
@if errorlevel 1 goto :error2


%jdkUse%\javac.exe  %transOptions%  de\frame4j\net\*.java
@if errorlevel 1 goto :error2

@REM de\frame4j\security\*.java
%jdkUse%\javac.exe  %transOptions%  de\frame4j\xml\*.java  de\frame4j\math\*.java
@if errorlevel 1 goto :error2



%jdkUse%\javac.exe  %transOptions% Era.java SVNkeys.java SVNkeysFilter.java
@if errorlevel 1 echo ignore starter error - but repeat  Era SVNkeys/Filter

%jdkUse%\javac.exe  %transOptions%  CVSkeys.java AskAlert.java FS.java 
@if errorlevel 1 echo ignore starter error - but repeat  CVSkeys AskAlert FS 

%jdkUse%\javac.exe  %transOptions%   UCopy.java XMLio.java
@if errorlevel 1 echo ignore starter error - but repeat  UCopy XMLio


%jdkUse%\javac.exe  %transOptions%  Del.java Exec.java
@if errorlevel 1 echo ignore starter error - but repeat  Del or Exec


%jdkUse%\javac.exe  %transOptions%  TimeHelper.java 
@if errorlevel 1 echo ignore starter error - but repeat TimeHelper

%jdkUse%\javac.exe  %transOptions%  ShowProps.java
@if errorlevel 1 echo ignore starter error - but repeat ShowProps

%jdkUse%\javac.exe  %transOptions%  TvH.java ComplDemo.java
@if errorlevel 1 echo ignore starter error - but repeat TvH or ComplDemo



@if jdkUseVers==9 (
 @echo -cp RXTXcomm.jar;build for java 9 only  
 %jdkUse%\javac.exe %transOptions% -cp C:\util\jdk\jre\lib\ext\RXTXcomm.jar;build ShowPorts.java
) else (
 %REM 11 11  11  11   jdkUse%\javac.exe %transOptions%  ShowPorts.java
) 
@if errorlevel 1 echo ignore starter error - but repeat ShowPorts


if %jdkUseVers%==8 (
 %jdkUse%\javac.exe  %transOptions%  SendMail.java
 @if errorlevel 1 echo ignore starter error - but repeat SendMail
)

@REM %jdkUse%\javac.exe  %transOptions%  MakeDigest.java  
@REM if errorlevel 1 echo ignore starter error - but repeat MakeDigest

%jdkUse%\javac.exe  %transOptions%  ClientLil.java
@if errorlevel 1 echo ignore starter error - but repeat ClientLil

%jdkUse%\javac.exe  %transOptions%  Update.java 
@if errorlevel 1 echo ignore starter error - but repeat Update


%jdkUse%\javac.exe  %transOptions%  FuR.java
@if errorlevel 1 echo ignore starter error - but repeat  FuR

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

@REM  ----------- trans-code .properties from development platform to UTF-8
for /R %%k in (*.properties) do java.exe de.frame4j.UCopy -outEncode UTF-8  %%k

@Echo Building frame4j.jar
%jdkUse%\jar.exe -cfvm  ..\frame4j-not-signed.jar ..\Frame4J.mf .
cd ..
@Echo.
@Echo Do sign frame4j-not-signed.jar  as frame4j.jar 
@Echo.


@if %jdkUseVers%==9 goto :end9

@goto :end

:end9
@echo Compiled with Java9 -- won't deploy yet
exit /B 9

:callError
@echo error in call (exit 999)
@exit /b 999
:envError
@echo error in environment (exit 9999)
@exit /b 9999
:error2
@Echo Fatal Error: Java compilation failed
pause
exit /B 3

:error
@echo.
@echo operation failed !
:end
@if ERRORLEVEL 1 @echo exit %errorlevel%
@cd /D %whereIwasCalledFrom%

@echo.

