@echo.
@echo Testing of a Frame4J (de.frame4j) installation 
@echo script:   frame4Jtest.bat     by   Albrecht Weinert   a-weinert.de
@REM  Copyright 2013   Albrecht Weinert   (a-weinert.de)
@echo.
@Echo script Revis. = $Revision: 31 $
@Echo last changed  = $Date: 2021-03-12 16:58:39 +0100 (Fr, 12 Mrz 2021) $
@echo.  
@echo.
@echo This just starts some Frame4J tools mostly in -help mode and pauses
@echo on errors. Type cntl-C to stop or any key to test/see the next tool. 
@echo use frame4Jtest -en  to see English help texts (-de for German).

@pause

java AskAlert
@if errorlevel 1 pause

java Update -? %1
@if errorlevel 1 pause
java FS -? %1
@if errorlevel 1 pause
java UCopy -? %1
@if errorlevel 1 pause
java de.frame4j.PKextr -? %1
@if errorlevel 1 pause
java FuR -? %1
@if errorlevel 1 pause
java Exec -? %1
@if errorlevel 1 pause
java TimeHelper -? %1
@if errorlevel 1 pause
java Era -? %1
@if errorlevel 1 pause
java Del -? %1
@if errorlevel 1 pause
java SVNkeys -? %1
@if errorlevel 1 pause
java AskAlert -? %1
@if errorlevel 1 pause
java SendMail -? %1
@if errorlevel 1 pause
java XMLio -? %1
@if errorlevel 1 pause

java ShowProps
@if errorlevel 1 pause
java ShowPorts
@if errorlevel 1 pause

:end
@echo.
