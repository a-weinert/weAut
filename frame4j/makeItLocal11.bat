@echo
@echo makeItLocal.bat  $Revision: 6 $,  $Date: 2018-11-21 19:37:39 +0100 (Mi, 21 Nov 2018) $
@echo.

@set whereItwasCalledFrom=%CD%

@REM this script to locally build frame4j is an example. It must be
@REM adopted to the respective development platform.
@set frame4j.loc.fac=D:\temp\frame4j_11 

@echo To save the real file modification dates
call preCommitSaveDate.bat
@echo .

@echo Copy from local working copy to %frame4j.loc.fac%
@echo.
@call CopBea %frame4j.loc.fac%
@echo.
cd /D  %frame4j.loc.fac%
@echo.

@echo translate it all 
@call translateIt11.bat
@echo.

@echo make docu, jars and zips ... ready for deployment
@call readyDeploy11.bat byUserEntry

xCopy /Y erg.zip %whereItwasCalledFrom%\ergAll4w64.zip*
@echo.
@echo.

cd /D %whereItwasCalledFrom%

