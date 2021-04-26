# A makefile include for raspberry projects 
# platform include for Raspberry type 3
# Copyright  2017  Albrecht Weinert    < a-weinert.de >

MAKE_INCLUDE_PLATFORM  = raspberry_01
MAKE_PLATFORM_LAST_CHANGE = '$Date: 2021-02-02 18:11:02 +0100 (Di, 02 Feb 2021) $ '
MAKE_PLATFORM_REVISION = '$Revision: 236 $ '

ifndef COPYRIGHT_YEAR
$(error includefile $(MAKE_INCLUDE_PLATFORM) used directly.)
endif

# PLATFORM might have been given in wrong case (at least on Windows)
override PLATFORM = $(MAKE_INCLUDE_PLATFORM)

# make_raspberry_01_settings.mk
# this include file is for platform specific settings only

# An optional short description of this platform's
# specifica and, especially; implemented variants, if any.
PLAF_DES_TEXT=\
Outdated Raspberry Pi 1 or A: two USB,  no WLAN, only 26 GPIO pins

MCU =  BCM2835
MCUcores = 1
F_CPU ?= 700000000
GPIOpins = 26
WLAN = 0
USB = 2

