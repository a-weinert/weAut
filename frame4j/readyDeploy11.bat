@echo.
@set whereIwasCalledFrom=%CD%

@echo Make translated  Frame4J ready and deploy locally 
@echo readyDeploy11.bat $Revision: 31 $,  $Date: 2021-03-12 16:58:39 +0100 (Fr, 12 Mrz 2021) $
@Echo Copyright (c)  2018 Albrecht Weinert
@echo.

@REM for both local and server (weinert-automation, common deploy) use.

@if %JAVA_HOME%X==X goto :envError
@if %jdkUse%X==X set jdkUse=%JAVA_HOME%\bin

set jdk4Doc=C:\util\jdk11

@if not exist .\.svn @goto :start_0
@echo do not use this in a repository or (SVN) local working copy
@goto :callError

:start_0

@if exist .\frame4j-not-signed.jar goto :signJar
@echo to be used after local Frame4J translation (see translatIt.bat) only
@goto :callError

:signJar
@if exist C:\util\etc\codesign.p12 (
   set keyParam=C:\util\etc\codesign.p12 -storetype pkcs12
   @set keyAlias=codesigning
   @goto :keyStoreFound
)
@set keyAlias=de_aw_sig
@if exist C:\util\etc\sigAWcert.jks (
   set keyParam=C:\util\etc\sigAWcert.jks
   @goto :keyStoreFound
) 
@echo could not find the expected keystore file
@if exist C:\programme\.keystore (
   set keyParam=C:\programme\.keystore
   @goto :keyStoreFound
)
@if exist C:\util\.keystore (
   set keyParam=C:\util\.keystore
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
copy frame4j-not-signed.jar frame4j.jar 
@Echo.
@Echo Signing frame4j.jar
@Echo.
@REM proxy setting as FH local test only 
@REM -J-Dhttp.proxyHost=193.175.85.10 -J-Dhttp.proxyPort=8080 

%jdkUse%\jarsigner.exe  -verbose   -tsa http://zeitstempel.dfn.de -keystore %keyParam% frame4j.jar %keyAlias%
@if NOT errorLevel 1 goto :jarSigned

:jarNotSigned
@set keyParam=
@Echo Signing frame4j.jar failed with error %errorlevel%
@REM goto :error
@Echo Going ahead for local Test only

:jarSigned

@if %jdkUseVers%==9 goto :end9

@set keyParam=
@if %frame4j.loc.dnl%X==X goto :noDnlDir_0
@if exist %frame4j.loc.dnl%\sources goto :dnlExist
@set frame4j.loc.dn=
@goto :noDnlDir_0

:dnlExist
@echo Frame4J download (prepare) directory is %frame4j.loc.dnl%\
xCopy /Y frame4j.jar %frame4j.loc.dnl%\

:noDnlDir_0
@if exist %jdk4Doc%\lib\frame4j.jar xCopy /Y frame4j.jar %jdk4Doc%\lib\
@Echo.

:makeDoku
@echo.
@echo generate documentation of de.frame4j.... (Frame4J)
@set  theSource=%whereIwasCalledFrom%
@set  jdocLaunch=%jdkUse%\javadoc.exe


   @echo.
   @echo This is in Java 11 with the buggy javadoc
   @echo Avoid javadoc 8 and 11 big selfclosing bug 
   @set  jdocLaunch=%jdocLaunch% -Xdoclint:all,-html,-missing 
   @echo.



@set  jdocLaunch=%jdocLaunch% -author -version -protected -J-mx96m -J-ms96m %externalClasses% -windowtitle " Frame4J -- the Java framework  de.frame4j... "


if not exist %jdk4Doc%\docs\frame4j\nul md %jdk4Doc%\docs\frame4j
@cd /D  %jdk4Doc%\docs\frame4j
@set useLinks= -link ../api
if exist ..\mailapidocs set useLinks=%useLinks% -link ../mailapidocs
if exist ..\servletapi set useLinks=%useLinks% -link ../servletapi
if exist ..\googlegwtdocs\package-list set useLinks=%useLinks% -link ../googlegwtdocs
if exist ..\tomcatapi set useLinks=%useLinks% -link ../tomcatapi

if exist ..\junit set useLinks=%useLinks% -link ../junit

@echo  .%useLinks%.
@echo.


@copy /Y %theSource%\Frame4J11doc.list %theSource%\Usedoc.list

@REM grep -v "ShowPorts" %theSource%\Frame4Jdoc.list > %theSource%\Usedoc.list
@if "D:\temp\frame4j"=="%theSource%" goto :docIt
@REM D:\temp\frame4j is the source directory placeholder in Frame4Jdoc.list
@java.exe de.frame4j.FuR %theSource%\Usedoc.list "D:\temp\frame4j" %theSource%

:docIt
%jdocLaunch% %useLinks% -sourcepath %theSource%\  @%theSource%\Usedoc.list
@Echo.
@REM  out Echo One warning due to  "import sun.misc.BASE64Encoder"   is OK
@cd /D  %jdk4Doc%\docs\

@echo.
@echo Repair the Java6 javaDoc //-bug (..//package)
@echo.
@java.exe de.frame4j.FuR %jdk4Doc%\docs\frame4j\+.html  ../..//pac  ../../pac  -r

 
@if %frame4j.loc.dnl%X==X goto :noDnlDir_1
@echo Generating Java-archive frame4jdoc.zip
@cd /D  %jdk4Doc%\docs
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

:end9
@echo no installation of Java 9 generated jars
@goto :end

:callError
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

