@echo update_parts.bat   V.02  26.04.2021
@REM       V.01  17.05.2019 : new (frame4j_part)
@REM       V.02  26.04.2021 : grame4j (full)
@echo Copyright (c)  2019 2021 Albrecht Weinert   a-weinert.de 
@echo Update the repository parts from the original SVN repostitories
@REM This (Windows) script needs Java, Frame4J and Git installed
@echo.
@set upd.opts=-nonew -r -OmitDirs ".svn;.settings" -en
@rem upd.opts=-nonew -r -OmitDirs ".svn;.settings" -test
@rem remove -en for German output by Update. Add -test for ... 

@REM change next two lines according to full SVN repo local paths
@set frame4j.fLocWD=D:\eclipse18-09WS\frame4j
@set rasProject_01.fLocWD=D:\eclipCe18-12WS\rasProject_01
@set frame4j.jar.Loc=C:\util\jdk\jre\lib\ext

java Update %upd.opts% %rasProject_01.fLocWD%\ .\rasProject_01part\
@echo.
java Update %upd.opts% %frame4j.fLocWD%\ .\frame4j\
@echo.
java Update %upd.opts% %frame4j.jar.Loc%\frame4j.jar .\binaries\
@echo.

git status
@echo.
@echo Content? Don't forget to:
@echo git add -u
@echo git commit -m "updates from the SVN repositories"
@echo git push
@echo.