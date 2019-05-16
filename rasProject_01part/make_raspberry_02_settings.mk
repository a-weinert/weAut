# A makefile include for raspberry projects 
# platform include for Raspberry type 2
# Copyright  2017  Albrecht Weinert    < a-weinert.de >

MAKE_INCLUDE_PLATFORM  = raspberry_02
MAKE_PLATFORM_LAST_CHANGE = '$Date: 2017-08-22 14:25:13 +0200 (Di, 22 Aug 2017) $ '
MAKE_PLATFORM_REVISION = '$Revision: 38 $ '

ifndef COPYRIGHT_YEAR
$(error includefile $(MAKE_INCLUDE_PLATFORM) used directly.)
endif

# PLATFORM might have been given in wrong case (at least on Windows)
override PLATFORM = $(MAKE_INCLUDE_PLATFORM)

# make_raspberry_02_settings.mk
# this include file is for platform specific settings only

# An optional short multiline description of this platform's
# specifica and, especially; implemented variants, if any.
# May be empty. Do not change the three lines define endef and export
define PLAF_DES_TEXT
Outdated Raspberry Pi 2
     
Attention / Hints:

This platform file is for all Pi 2 with 40 GPIO and 4 USB
As far as we know there were no variants. Should this not be true this
make include will have to be updated and forks of it be made.
endef
export PLAF_DES_TEXT


MCU =  BCM2836
MCUcores = 4
F_CPU ?= 900000000
GPIOpins = 40
WLAN = 1
USB = 4

