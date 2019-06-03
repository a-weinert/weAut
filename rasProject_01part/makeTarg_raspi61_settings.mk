# A makefile include for raspberry projects 
# platform include for one concrete Raspberry target device
# including all attached extra hardware
# Copyright  2017  Albrecht Weinert    < a-weinert.de >

MAKE_INCLUDE_TARGET  = raspi61
MAKE_TARGET_LAST_CHANGE = '$Date: 2017-08-22 14:25:13 +0200 (Di, 22. Aug 2017) $ '
MAKE_TARGET_REVISION = '$Revision: 77 $ '

ifndef COPYRIGHT_YEAR
$(error includefile $(MAKE_INCLUDE_TARGET) used directly.)
endif

# PLATFORM might have been given in wrong case (at least on Windows)
override TARGET = $(MAKE_INCLUDE_TARGET)

# makeTarg_raspi61_settings.mk
# this include file describes one and only one concrete device including 
# all extra hardware (shields) etc. If this hardware configuration changes
# this file must usually changed accordingly.

# raspi61 is a Raspberry 3b
override PLATFORM = raspberry_03

# An optional short multiline description of this target's
# specifica and, especially; implemented variants, if any.
# May be empty. Do not change the three lines define endef and export
override define TARG_DES_TEXT
Device raspi61 (Raspberry Pi 3)
     
Platform for process IO by pigpio[d] and libmodbus.
endef
export TARG_DES_TEXT

MAC     ?= b8:27:eb:71:1d:cd
IP     ?= 192.168.89.61
IPwifi ?= 192.168.89.53
MACwifi ?= b8:27:eb:24:48:98
# FTPtarget = $(IP)       # use known LAN IP
# FTPtarget = $(IPwifiP)  # use known WLAN IP
FTPtarget = piWLan97 # raspberrypi  # use Pi default host name 
# this is make default FTPdir = bin
