@echo
@echo genNativeLib.bat  $Revision: 1 $,  $Date: 2017-01-14 13:19:07 +0100 (Sa, 14 Jan 2017) $
@echo.


javah -o de_frame4j_io_WinDoesIt.h de.frame4j.io.WinDoesIt
REM In case of any changes do copy changed headers from 
REM de_a-weinert_io_WinDoesIt.h  to   WinDoesIt.cpp

@echo.
@echo use MinGw to make the .dll
C:\util\mingw64\bin\gcc.exe -Wl,--add-stdcall-alias -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -shared -o WinDoesIt.dll  WinDoesIt.cpp

@echo.
@echo Copy the .dll to the current JDK
copy /Y  WinDoesIt.dll  %JAVA_HOME%\bin\bsDoesItNative.dll

@echo.
@echo Copy the .dll for deployment on ...
@if defined ProgramFiles(x86) (
    @echo ... 64 Bit Windows to factory\4win64\bsDoesItNative.dll
    copy /Y  WinDoesIt.dll  .\factory\4win64\bsDoesItNative.dll
) else (
    REM yet unclear if mingw64 will make a 32 bit .dll
    REM if not use old BCC recipe
    @echo ... 32 Bit Windows to factory\4windows\bsDoesItNative.dll
    copy /Y  WinDoesIt.dll  .\factory\4windows\bsDoesItNative.dll
)
