@echo.
@echo cleanAll.bat (c) 2017 2020 Albrecht Weinert   a-weinert.de
@echo Revision $Revision: 233 $ ($Date: 2020-11-30 18:17:59 +0100 (Mo, 30 Nov 2020) $)
@echo.
@REM Up to version 233, October 2020, this Windows batch organised a loop to
@REM make PROGRAM=* clean for all existing programs identified by existence
@REM of a make include file makeProg_*_settings.mk
@REM 
@REM Since SVN revision 233 we bypass the really slow make interpreter by
@REM deleting *.o *.lss *.elf *.map *.sym as well as the file 
@REM programMameWithoutExtension directly by the del command.
@REM 
@REM Plan: Spilt this file in make / no make variants or chose make by option.  

:fastDelete1
del *.o *.lss *.elf *.map *.sym
del weRasp\*.o
:endFastDelete1

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
@echo - - - - - - 
@echo.
@del programList.tmp

@for /F  %%i in (programList.txt) do @(
 @set varLoop=%%i
 @if exist !varLoop!\* (
   @echo !varLoop! is a directory
 ) else (
   @if exist !varLoop! (
     del !varLoop!
   )
 )
)
@goto :end

:makeVariant
make clean_docu
make clean_svn_tmp

@for /F  %%i in (programList.txt) do @(
 @set varLoop=%%i
 @echo.
 @echo cleaning up program  !varLoop!  ----------------
 make PROGRAM=!varLoop!  clean
 @if ERRORLEVEL 1 (
   @echo.
   @java AskAlert -break -wM "Error cleaning !varLoop!"
   @if ERRORLEVEL 1  exit /B 5
 ) else (
   @echo.
   @REM sleep 1
 )
 @echo.
)
:end
@del programList.txt
@echo.
