@echo.
@echo makeTests4Pi.bat  
@REM  $Revision: 29 $,  $Date: 2019-06-01 18:47:20 +0200 (Sa, 01 Jun 2019) $
@REM param 1 : user:passwd  (ftp user)  default pi:raspberry
@REM param 2 : target machine (IP)      default 192.168.178.97
@echo.

@REM @set whereItwasCalledFrom=%CD%
@if %JAVA_HOME%X==X goto :envError
@if not exist %JAVA_HOME%\bin\javac.exe goto :envError
@set jdkUse=%JAVA_HOME%\bin
@set transOptions=-d build
@if %1X==X (
@set piUserPW=pi:raspberry
) else (
@set piUserPW=%1
)
@if %2X==X (
@set piIP=192.168.178.97
) else (
@set piIP=%2
)

%jdkUse%\javac.exe %transOptions%  de\weAut\tests\*.java
@if errorlevel 1 goto :error2
winscp.com /script=progTransWin /parameter %piUserPW% %piIP% de/weAut/tests build\de\weAut\tests\*
@if errorlevel 1 goto :error
@goto :end

:envError
@echo error in environment (exit 9999)
@goto :end
:error2
@Echo Fatal Error: Java compilation failed
@goto :end
:error
@echo.
@echo operation failed !

:end
@if ERRORLEVEL 1 @echo exit %errorlevel%
@echo.
@set piUserPW=
@set piIP=
@REM   cd /D %whereItwasCalledFrom%