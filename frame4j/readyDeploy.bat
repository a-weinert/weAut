@echo.
@set whereIwasCalledFrom=%CD%

@echo Make translated  Frame4J  ready and deploy locally 
@echo readyDeploy.bat 
@Echo Copyright (c)  2013, 2018, 2021 Albrecht Weinert  a-weinert.de
@echo $Revision: 47 $ ($Date: 2021-05-13 19:06:22 +0200 (Do, 13 Mai 2021) $)
@echo.

@if %JAVA_HOME%X==X goto :envError
@if %jdkUse%X==X set jdkUse=%JAVA_HOME%\bin

@if not exist .\.svn @goto :start_0
@echo do not use this in a repository or (SVN) local working copy
@goto :callError

:start_0

@if exist .\frame4j-not-signed.jar goto :signJar
@echo to be used after local Frame4J translation (see translatIt.bat) only
@goto :callError

@REM as there's no Let'encrypt for code signing, getting certificates for
@REM free open source projects is financially and technically infeasible 

:signJar
copy frame4j-not-signed.jar frame4j.jar 
@if %1X==X goto :doNotSign

@if exist C:\util\.keystoreJS (
   set keyParam=C:\util\.keystoreJS
   @set keyAlias=de_aw_sig
   @goto :keyStoreFound
) 
@if exist C:\util\etc\codesign.p12 (
   set keyParam=C:\util\etc\codesign.p12 -storetype pkcs12
   @set keyAlias=codesigning
   @goto :keyStoreFound
)
@set keyAlias=de_aw_sig
@echo could not find the expected keystore file
@if exist C:\programme\.keystore (
   set keyParam=C:\programme\.keystore
   @goto :keyStoreFound
)
@if exist C:\util\.keystore (
   set keyParam=C:\util\.keystore
   @goto :keyStoreFound
)
@if exist C:\util\.keystore2019 (
   set keyParam=C:\util\.keystore2019
   @goto :keyStoreFound
)
@if exist %USERPROFILE%\.keystore (
   set keyParam=%USERPROFILE%\.keystore
   @goto :keyStoreFound
)
@echo could not find any other keystore file, also.
@goto :envError

:keyStoreFound
@REM use call parameter as storepassword (secretly)
@if %1X==X goto :doTheSign
@set keyParam=%keyParam% -storepass %1

:doTheSign
@Echo.
@Echo Signing frame4j.jar
@Echo.
@REM The code signing certificate for A. Weinert from DFN-Verein is valid
@REM throughout 202x. But as DFN's root cert is unknown outside German 
@rem universities, A. Weinert's certificate might wrongly be taken 
@REM as self-signed.
%jdkUse%\jarsigner.exe -verbose -tsa http://zeitstempel.dfn.de -keystore %keyParam% frame4j.jar %keyAlias%
@if NOT errorLevel 1 goto :jarSigned

:jarNotSigned
@set keyParam=
@Echo Signing frame4j.jar failed with error %errorlevel%
@goto :error

:doNotSign
@Echo No jar signing requested
@Echo Going ahead for local Test only

:jarSigned
@set keyParam=
@if %frame4j.loc.dnl%X==X goto :noDnlDir_0
@if exist %frame4j.loc.dnl%\sources goto :dnlExist
@set frame4j.loc.dn=
@goto :noDnlDir_0

:dnlExist
@echo Frame4J download (prepare) directory is %frame4j.loc.dnl%\
xCopy /Y frame4j.jar %frame4j.loc.dnl%\

:noDnlDir_0
xCopy /Y frame4j.jar %JAVA_HOME%\jre\lib\ext\
@REM   if exist C:\util\frame4j.jar xCopy /Y frame4j.jar C:\util\
@Echo.

:makeDoku
@echo.
@echo generate documentation of de.frame4j.... (Frame4J)
@echo.
@echo This is in Java 8 with the buggy javadoc (hence on)
@echo Avoid javadoc 8 big selfclosing bug 
@set  theSource=%whereIwasCalledFrom%
@set  jdocLaunch=%jdkUse%\javadoc.exe -Xdoclint:all,-html,-missing 
@set  jdocLaunch=%jdocLaunch% -author -version -protected -J-mx96m -J-ms96m %externalClasses% -windowtitle " Frame4J -- the Java framework  de.frame4j... "

if not exist %JAVA_HOME%\docs\frame4j\nul md %JAVA_HOME%\docs\frame4j
@cd /D  %JAVA_HOME%\docs\frame4j
@set useLinks= -link ../api
if exist ..\mailapidocs set useLinks=%useLinks% -link ../mailapidocs
if exist ..\servletapi set useLinks=%useLinks% -link ../servletapi
if exist ..\googlegwtdocs\package-list set useLinks=%useLinks% -link ../googlegwtdocs
if exist ..\tomcatapi set useLinks=%useLinks% -link ../tomcatapi
if exist ..\junit set useLinks=%useLinks% -link ../junit

@echo  .%useLinks%.
@echo.

@copy /Y %theSource%\Frame4Jdoc.list %theSource%\Usedoc.list
@if "D:\temp\frame4j"=="%theSource%" goto :docIt
@REM D:\temp\frame4j is the source directory placeholder in Frame4Jdoc.list
@java.exe de.frame4j.FuR %theSource%\Usedoc.list "D:\temp\frame4j" %theSource%

:docIt
%jdocLaunch% %useLinks% -sourcepath %theSource%\  @%theSource%\Usedoc.list
@Echo.
@REM  out Echo One warning due to  "import sun.misc.BASE64Encoder"   is OK
@cd /D  %JAVA_HOME%\docs\

@echo.
@goto :noRepair6
@echo Repair the Java6 javaDoc //-bug (..//package), html and add icon
@REM echo.
@REM  java.exe de.frame4j.FuR %JAVA_HOME%\docs\frame4j\+.html  ../..//pac  ../../pac  -r -v
@java.exe de.frame4j.FuR .\frame4j\+.html  @.\frame4j\de\weAut\doc-files\FuRdoc.properties -r -silent
@REM  echo repaired javaDoc6 //-bug (..//package), html and added icon
:noRepair6 
 
@if %frame4j.loc.dnl%X==X goto :noDnlDir_1
@echo Generating Java-archive frame4jdoc.zip
@REM see above    cd /D  %JAVA_HOME%\docs 
%jdkUse%\jar.exe cfM  %frame4j.loc.dnl%\frame4jdoc.zip frame4j
@echo .
@dir %frame4j.loc.dnl%\frame4jdoc.zip
:noDnlDir_1

@cd /D %whereIwasCalledFrom%

@echo.
@echo (Re) generate   erg.zip
@Echo.
call ant.bat  ergZip
@Echo Made erg.zip
@dir erg.zip
@if not %frame4j.loc.dnl%X==X xCopy /Y erg.zip %frame4j.loc.dnl%\

@goto :end

:
callError
@echo error in call (exit 999)
@exit /b 999
:envError
@echo error in environment (exit 9999)
@exit /b 9999

:error
@echo.
@echo operation failed !
:end
@if ERRORLEVEL 1 @echo exit %errorlevel%
@cd /D %whereIwasCalledFrom%
@echo.
