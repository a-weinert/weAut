@echo.
@set whereIwasCalledFrom=%CD%
@set frame4j.loc.fac=%1
@set frame4j.loc.dnl=%1_download
@set frame4j.loc.rep=%CD%
@set frame4j.o1.dir=.settings;CVS;.svn;
@set sourceTypes=java;html;properties;xml;gif;png;txt;mf;c;cpp;h;def;bat;list;cmd;php

@echo CopBea.bat $Revision: 9 $,  $Date: 2018-11-24 17:06:27 +0100 (Sa, 24 Nov 2018) $
@Echo.
@echo Copy and beautify project from repository / local working copy to a
@echo working area for building.
@echo from: %frame4j.loc.rep%
@echo to  : %frame4j.loc.fac%
@echo down: %frame4j.loc.dnl% ( \ sources)
@echo (Script for local development workstation and SVN server use.)
@echo.

@REM   echo called from: %whereIwasCalledFrom% parameter: %1

@if %1X==X          goto :callError
@if %1==.           goto :callError
@if /I %1==%whereIwasCalledFrom%  goto :callError

@if /I %1==-help    goto :usageHelp
@if /I %1==/help    goto :usageHelp
@if %1==-?          goto :usageHelp
@if %JAVA_HOME%X==X goto :envError

@if not exist %frame4j.loc.fac%   goto :newDest
cd /D %frame4j.loc.fac%
@if ERRORLEVEL 1 goto :error
goto :copyStart

:newDest
md %frame4j.loc.fac%
@if ERRORLEVEL 1 goto :error
cd /D %frame4j.loc.fac%
@if ERRORLEVEL 1 goto :error


:copyStart
@echo Clear destination %frame4j.loc.fac%
@java de.frame4j.Del -empty  .\+.+
@if ERRORLEVEL 1 goto :error

@if exist %frame4j.loc.dnl%\sources goto :dnlExist
md  %frame4j.loc.dnl%
md  %frame4j.loc.dnl%\sources
@if ERRORLEVEL 1 goto :noDNL_0
goto :dnlExist

:noDNL_0
@Echo download directory %frame4j.loc.dnl% not available
set frame4j.loc.dnl=
@goto :noDNL_00
 
:dnlExist
del %frame4j.loc.dnl%\sources\frame4j_trunk.zip
del %frame4j.loc.dnl%\sources\frame4j_2build.zip
:noDNL_00


java de.frame4j.Update  difOld=201 omitDirs=%frame4j.o1.dir% %frame4j.loc.rep%\%sourceTypes%  %frame4j.loc.fac%\
@if ERRORLEVEL 1 goto :error
xCopy /Y %frame4j.loc.rep%\ergAll4w64.zip  %frame4j.loc.fac%\


java de.frame4j.Update  -r difOld=201 omitDirs=build;buildCat;.settings;CVS;.svn %frame4j.loc.rep%\de\+.+  %frame4j.loc.fac%\de\
@if ERRORLEVEL 1 goto :error
java de.frame4j.Update  -r difOld=201 omitDirs=%frame4j.o1.dir% %frame4j.loc.rep%\factory\  %frame4j.loc.fac%\factory\
@if ERRORLEVEL 1 goto :error

rd  /S /Q .settings
rd  /S /Q .gwt-cache
del .classpath
del .project
@echo.
@if %frame4j.loc.dnl%X==X goto :noDNL_1
jar cfM %frame4j.loc.dnl%\sources\frame4j_trunk.zip .
@dir %frame4j.loc.dnl%\sources
@echo.
:noDNL_1
call ant copDocFiles
@if ERRORLEVEL 1 goto :error

java de.frame4j.SVNkeys . -omitDirs CVS;doc-files;.svn 
@if ERRORLEVEL 1 goto :error

@if %frame4j.loc.dnl%X==X goto :noDNL_2

rd  /S /Q factory
jar cfM %frame4j.loc.dnl%\sources\frame4j_2build.zip .
@dir %frame4j.loc.dnl%\sources\*.zip
@echo.
:noDNL_2


@goto :end

:usageHelp
@echo usage: CopBea.bat destination
@echo Call in source directory / local working copy as actual
@echo directory (now %CD%)
@echo destination must be given, may or may not exist, must not be
@echo the actual source directory and will be cleared (!!).
@echo Download files (.zip) will be prepared in destination_download\sources
@echo if that directory exists or can be made.
@echo.
@Echo ANT (ANT_HOME)= %ANT_HOME%
@Echo JAVA_HOME     = %JAVA_HOME%

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
