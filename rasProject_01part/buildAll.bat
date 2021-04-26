@echo.
@echo buildAll.bat (c) 2017 Albrecht Weinert   a-weinert.de
@echo Revision $Revision : 1.0 $ ($Date : now $)
@echo.
@REM Windows batch to make PROGRAM=* clean all for all existing programs
@REM identified by existence of make include makeProg_*_settings.mk

@del  programList.txt
@setlocal   ENABLEDELAYEDEXPANSION

@ls -1 makeProg_*_settings.mk > programList.tmp
@for /F  %%i in (programList.tmp) do @(
 @set varLoop=%%i
 echo !varLoop:~9,-12! >> programList.txt
)

@ls -1 *.c > programList.tmp
@for /F  %%i in (programList.tmp) do @(
 @set varLoop=%%i
 echo !varLoop:~0,-2! >> programList.txt
)

sort programList.txt | grep -v HTML > programList.tmp
uniq programList.tmp programList.txt
@echo unique list:
@type  programList.txt
@sleep 2
@del programList.tmp
@echo.
@goto :noClean
@echo cleaning all before
@for /F  %%i in (programList.txt) do @(
 @set varLoop=%%i
 @echo.
 @echo cleaning up program  !varLoop!  ----------------
 make PROGRAM=!varLoop!  clean
 @if ERRORLEVEL 1 (
   @echo.
   @java AskAlert -break -wM "Error cleaning !varLoo!"
   @if ERRORLEVEL 1  exit /B 5
 ) else (
   @echo.
 )
 @echo.
)
@echo.
:noClean
@echo building all now

@for /F  %%i in (programList.txt) do @(
 @set varLoop=%%i
 @echo.
 @echo building program  %%i ------------------------------
 make PROGRAM=!varLoop! all
 @if ERRORLEVEL 1 (
   @echo.
   @java AskAlert -break -wM "Error building !varLoop!"
   @if ERRORLEVEL 1  exit /B 5

 ) else (
   @echo.
   @echo No errors and warnings for %%i
   @sleep 1
 )
 @echo.
 @echo.
)
