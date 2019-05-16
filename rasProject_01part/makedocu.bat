@echo Generating of all rasProject_01 / sweetHome documentation 
@REM  in situ by doxyGen. 
@REM  on success: html docu is all in .\doxydocu\html\...
@REM  and the pdf docu is  .\doxydocu\weRasProject_01.pdf
@REM  Note: An option -noPDF will inhibit the .pdf file generation or update.

@REM  $Revision: 119 $
@REM  $Date: 2018-02-27 15:37:00 +0100 (Di, 27 Feb 2018) $
@REM  Copyright 2014 Albrecht Weinert ( a-weinert.de )

@echo.

@REM Note:  This script needs a) Doxygen, b) [winAVR (util\bin)] 
@REM        c) BitKinex installed and on the PATH  as well as 
@REM        d) Java (8) and e) frame4j as installed extension
@REM Note2: This script should run on "deployed" (copies) of working copies,
@REM        i.e. those with SVN tags beautifully removed by  frame4j's 
@REM        CVSkeys.

rmdir /S /Q doxydocu\xml
rmdir /S /Q doxydocu\html
rmdir /S /Q doxydocu\latex
del dox_warnings.are
del doxySVNfilter.log

doxygen.exe 
@echo doxygen.exe ready

@if exist .\doxydocu\latex\*.tex goto :ahead1
@echo error not latex..tex
@echo No latex files generated (error) >> dox_warnings.are
@goto :errorPDF

:ahead1
@if /i %1x==-noPDFx goto :end
cd doxydocu\latex
pdflatex.exe  refman.tex

java FuR ./refman.tex "{tocdepth}{" "}" "{tocdepth}{1}" keepBraces=false

pdflatex.exe refman.tex

@cd ../..

@if NOT exist doxydocu\latex\refman.pdf goto :errorPDF

copy /Y doxydocu\latex\refman.pdf doxydocu\weRasProject_01.pdf
@echo The .pdf documentation is .\doxydocu\weRasProject_01.pdf
goto :end

:errorPDF
@echo No .pdf documentation could be generated (error) >> dox_warnings.are

:end
@echo.
type .\dox_warnings.are
@echo [ end of warnings ]
@echo.
