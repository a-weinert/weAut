# A makefile include for raspberry projects 
# program include for one program
# Copyright  2019  Albrecht Weinert    < a-weinert.de >

MAKE_INCLUDE_PROGRAM  = justLock
MAKE_PROGRAM_LAST_CHANGE = '$Date: 2021-02-02 18:11:02 +0100 (Di, 02 Feb 2021) $ '
MAKE_PROGRAM_REVISION = '$Revision: 236 $ '

ifndef COPYRIGHT_YEAR
$(error includefile $(MAKE_INCLUDE_PROGRAM) used directly.)
endif

# PROGRAM or MAIN_F might have been given in wrong case (at least on Windows)
override MAIN_F = $(MAKE_INCLUDE_PROGRAM)

# makeProg_justLock_settings.mk

# An optional short multiline description of this program's
# specifica.
# May be empty. Do not change the three lines define endef and export
define PROG_DES_TEXT
Program  justLock

Program to enable programs and scripts written in other
languages to use standard linux/C file locking. It trys to
lock the standard lock file for piGpoi if it exists. 
On success it will run respectively sleep until getting a
signal, on which it will unlock the file and terminate.
endef
export PROG_DES_TEXT

extraLDFLAGS =  -pthread  ##  -lpigpio -lrt
extraSOURCES = weRasp/sysBasic.c weRasp/weUtil.c weRasp/weLockWatch.c
