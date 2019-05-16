@REM Beautify SVN tags as Doxygen filter
@REM file: doxySVNfilter.bat
@REM $Revision: 110 $  ($Date: 2018-02-17 12:59:59 +0100 (Sa, 17 Feb 2018) $)

@REM Copyright 2018 Albrecht Weinert   a-weinert.de weinert-automation.de

@REM doxySVNfilter.bat input-file 
@REM brings the beautified input-file content to standard output
@REM 
@REM in your Doxyfile just say:  INPUT_FILTER = doxySVNfilter.bat
@REM
@REM This batch is just a start wrapper for the java application
@REM de.frame4j.SVNkeysFilter
@REM 
@REM This script requires Java 8 and frame4j (as of 16.02.2018 or younger)
@REM being installed. 

@java.exe de.frame4j.SVNkeysFilter %1
