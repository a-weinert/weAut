# A makefile include for raspberry projects 
# platform include for Raspberry type 3
# Copyright  2017  Albrecht Weinert    < a-weinert.de >

MAKE_INCLUDE_PLATFORM  = raspberry_01
MAKE_PLATFORM_LAST_CHANGE = '$Date: 2017-08-22 14:25:13 +0200 (Di, 22 Aug 2017) $ '
MAKE_PLATFORM_REVISION = '$Revision: 38 $ '

ifndef COPYRIGHT_YEAR
$(error includefile $(MAKE_INCLUDE_PLATFORM) used directly.)
endif

# PLATFORM might have been given in wrong case (at least on Windows)
override PLATFORM = $(MAKE_INCLUDE_PLATFORM)

# make_raspberry_01_settings.mk
# this include file is for platform specific settings only

# An optional short multiline description of this platform's
# specifica and, especially; implemented variants, if any.
# May be empty. Do not change the three lines define endef and export
define PLAF_DES_TEXT
Outdated Raspberry Pi 1
     
Attention / Hints:
      Only two USB,  no WLAN, only 26 GPOI pins

This platform file is for all Pi 1s without + (plus) after the A or B
Should this not be sufficient forks of this make include will be made.
endef
export PLAF_DES_TEXT

MCU =  BCM2835
MCUcores = 1
F_CPU ?= 700000000
GPIOpins = 26
WLAN = 0
USB = 2

