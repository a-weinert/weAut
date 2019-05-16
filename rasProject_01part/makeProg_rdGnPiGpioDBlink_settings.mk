# A makefile include for raspberry projects 
# programme include for one programme
# Copyright  2017  Albrecht Weinert    < a-weinert.de >

MAKE_INCLUDE_PROGRAM  = rdGnPiGpioDBlink
MAKE_PROGRAM_LAST_CHANGE = '$Date: 2019-04-26 14:27:14 +0200 (Fr, 26 Apr 2019) $ '
MAKE_PROGRAM_REVISION = '$Revision: 14 $ '

ifndef COPYRIGHT_YEAR
$(error includefile $(MAKE_INCLUDE_PROGRAM) used directly.)
endif

# PROGRAM or MAIN_F might have been given in wrong case (at least on Windows)
override MAIN_F = $(MAKE_INCLUDE_PROGRAM)

# makeProg_rdGnPiGpioBlinkD_settings.mk

# An optional short multiline description of this programme's
# specifica.
# May be empty. Do not change the three lines define endef and export
define PROG_DES_TEXT
Programme rdGnPiGpioDBlink

Process IO demo with two LED for Raspberry Pi and alike
uses the pigpiod  library
forces singleton application, suitable as service
endef
export PROG_DES_TEXT

extraLDFLAGS = -lpigpiod_if2 -lrt -pthread 
extraSOURCES = weRasp/sysBasic.c weRasp/weUtil.c
