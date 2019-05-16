@echo.
@echo cleanAll.bat (c) 2017 Albrecht Weinert   a-weinert.de
@echo Revision $Revision : 1.0 $ ($Date : now $)
@echo.
@REM Windows batch to                    make PROGRAM=* clean  
@REM for all existing programmes
@REM identified by existence of make include makeProg_*_settings.mk

@del  programList.txt
@setlocal   ENABLEDELAYEDEXPANSION

@ls -1 makeProg_*_settings.mk > programList.tmp
@for /F  %%i in (programList.tmp) do @(
 @set varLoop=%%i
 echo !varLoop:~9,-12! >> programList.txt
)

@ls -1 *.c > programList.tmp
@REM type  programList.tmp
@REM echo.

@for /F  %%i in (programList.tmp) do @(
 @set varLoop=%%i
 echo !varLoop:~0,-2! >> programList.txt
)

sort programList.txt > programList.tmp
uniq programList.tmp programList.txt
@echo unique list:
@type  programList.txt
@sleep 1
@del programList.tmp

make clean_docu
make clean_svn_tmp

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
   @REM sleep 1
 )
 @echo.
 @echo.
)
@del programList.txt
