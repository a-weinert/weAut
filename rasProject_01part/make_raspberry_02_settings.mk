# A makefile include for raspberry projects 
# platform include for Raspberry type 2
# Copyright  2017  Albrecht Weinert    < a-weinert.de >

MAKE_INCLUDE_PLATFORM  = raspberry_02
MAKE_PLATFORM_LAST_CHANGE = '$Date: 2021-02-02 18:11:02 +0100 (Di, 02 Feb 2021) $ '
MAKE_PLATFORM_REVISION = '$Revision: 236 $ '

ifndef COPYRIGHT_YEAR
$(error includefile $(MAKE_INCLUDE_PLATFORM) used directly.)
endif

# PLATFORM might have been given in wrong case (at least on Windows)
override PLATFORM = $(MAKE_INCLUDE_PLATFORM)

# make_raspberry_02_settings.mk
# this include file is for platform specific settings only

# An optional short description of this platform's
# specifica and, especially; implemented variants, if any.
PLAF_DES_TEXT=\
Outdated Raspberry Pi 2, first with 40 GPIO and 4 USB

MCU =  BCM2836
MCUcores = 4
F_CPU ?= 900000000
GPIOpins = 40
WLAN = 1
USB = 4

