# A makefile include for raspberry projects  
# platform include for Raspberry type 3
# Copyright  2017  Albrecht Weinert    < a-weinert.de >

MAKE_INCLUDE_PLATFORM  = raspberry_03
MAKE_PLATFORM_LAST_CHANGE = '$Date: 2021-02-02 18:11:02 +0100 (Di, 02 Feb 2021) $ '
MAKE_PLATFORM_REVISION = '$Revision: 236 $ '

ifndef COPYRIGHT_YEAR
$(error includefile $(MAKE_INCLUDE_PLATFORM) used directly.)
endif

# PLATFORM might have been given in wrong case (at least on Windows)
override PLATFORM = $(MAKE_INCLUDE_PLATFORM)

# make_raspberry_03_settings.mk
# this include file is for platform specific settings only

# An optional short description of this platform's
# specifica and, especially; implemented variants, if any.
PLAF_DES_TEXT=\
Raspberry Pi 3 with 40 GPIO pins and 4 USB ports

MCU =  BCM2837
MCUcores = 4
F_CPU ?= 1200000000
GPIOpins = 40
WLAN = 1
USB = 4

