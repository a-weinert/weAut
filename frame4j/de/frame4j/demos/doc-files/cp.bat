@echo.
@echo Changing the font set / code page  of the command shell
@REM  script:   cp.bat V.$Revision: 1 $
@REM  variable  activeCodePage used by framework Frame4J
@REM  Copyright 2009   Albrecht Weinert   (a-weinert.de)
@REM  last changed: $Date: 2017-01-14 13:19:07 +0100 (Sa, 14 Jan 2017) $
@Echo.


@if %1x==x             goto :iso8859_1
@if %1==8859_1         goto :iso8859_1
@if %1==8859-1         goto :iso8859_1
@if %1==28591          goto :iso8859_1
@if /I %1==iso-8859-1  goto :iso8859_1
@if /I %1==iso8859-1   goto :iso8859_1
@if /I %1==cp28591     goto :iso8859_1
@if /I %1==latin1      goto :iso8859_1

@if %1==8859_2         goto :polski
@if %1==8859-2         goto :polski
@if %1==1250           goto :polski
@if /I %1==iso-8859-2  goto :polski
@if /I %1==iso8859-2   goto :polski
@if /I %1==cp1250      goto :polski
@if /I %1==latin2      goto :polski

@if %1==850       goto :oldIBMpc
@if /I %1==cp850  goto :oldIBMpc

@if %1==8859_15         goto :iso8859_15
@if %1==8859-15         goto :iso8859_15
@if %1==28605           goto :iso8859_15
@if /I %1==iso-8859-15  goto :iso8859_15
@if /I %1==iso8859-15   goto :iso8859_15
@if /I %1==cp28605      goto :iso8859_15
@if /I %1==latin9       goto :iso8859_15

@REM use parameter 1 as codepage number
chcp %1
set  activeCodePage=Cp%1
@Echo Font set now to Cp%1 (please check)
@goto :ende

:oldIBMpc
@chcp 850
@set activeCodePage=Cp850
@Echo Font set is now  Cp850 / Charly Chaplin's 1980 PC 1980  (back again) 
@goto :ende


:polski
@chcp 1250
@set activeCodePage=Cp1250
@echo Font set is now  ISO 8859-2 / Cp1250 / Latin 2
@goto :ende

:iso8859_15
@chcp 28605
@set activeCodePage=ISO-8859-15
@Echo Font set is now  ISO 8859-15 / Cp28605 / Latin 9 (0)
@goto :end

:iso8859_1
@chcp 28591
@set activeCodePage=ISO-8859-1
@Echo Font set is now  ISO 8859-1  (Unicode's 1st 256) / Cp28591 / Latin 1

:ende
@echo. 
