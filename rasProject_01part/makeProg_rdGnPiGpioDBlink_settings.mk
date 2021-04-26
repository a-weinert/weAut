# A makefile include for raspberry projects 
# program include for one program
# Copyright  2017  Albrecht Weinert    < a-weinert.de >

MAKE_INCLUDE_PROGRAM  = rdGnPiGpioDBlink
MAKE_PROGRAM_LAST_CHANGE = '$Date: 2021-02-02 18:11:02 +0100 (Di, 02 Feb 2021) $ '
MAKE_PROGRAM_REVISION = '$Revision: 236 $ '

ifndef COPYRIGHT_YEAR
$(error includefile $(MAKE_INCLUDE_PROGRAM) used directly.)
endif

# PROGRAM or MAIN_F might have been given in wrong case (at least on Windows)
override MAIN_F = $(MAKE_INCLUDE_PROGRAM)

# makeProg_rdGnPiGpioBlinkD_settings.mk

# An optional short multiline description of this program's
# specifica.
# May be empty. Do not change the three lines define endef and export
define PROG_DES_TEXT
Program rdGnPiGpioDBlink

Process IO demo with two LED for Raspberry Pi and alike
uses the pigpiod  library
forces singleton application, suitable as service
endef
export PROG_DES_TEXT

extraLDFLAGS = -lpigpiod_if2 -lrt -pthread 
extraSOURCES = weRasp/sysBasic.c weRasp/weUtil.c weRasp/weLockWatch.c
extraSOURCES += weRasp/weGPIOd.c