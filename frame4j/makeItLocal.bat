@echo.
@echo makeItLocal.bat  $Revision: 33 $,  $Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $
@echo.

@set whereItwasCalledFrom=%CD%

@REM this script to locally build frame4j is an example. It must be
@REM adopted to the respective development platform.
@set frame4j.loc.fac=D:\temp\frame4j 

@echo To save the real file modification dates
@echo Use call preCommitSaveDate.bat
@echo .

@echo Copy from local working copy to %frame4j.loc.fac%
@echo.
@call CopBea %frame4j.loc.fac%
@echo.
cd /D  %frame4j.loc.fac%
@echo.

@echo translate it all 
@call translateIt.bat
@echo.

@echo make docu, jars and zips ... ready for deployment
@call readyDeploy.bat byUserEntry

xCopy /Y erg.zip %whereItwasCalledFrom%\ergAll4w64.zip*
@echo.
@echo.

cd /D %whereItwasCalledFrom%

