# A makefile include for raspberry projects 
# platform include for one concrete Raspberry target device
# including all attached extra hardware
# Copyright  2017  Albrecht Weinert    < a-weinert.de >

MAKE_INCLUDE_TARGET  = raspi61
MAKE_TARGET_LAST_CHANGE = '$Date: 2021-02-02 18:11:02 +0100 (Di, 02 Feb 2021) $ '
MAKE_TARGET_REVISION = '$Revision: 236 $ '

ifndef COPYRIGHT_YEAR
$(error includefile $(MAKE_INCLUDE_TARGET) used directly.)
endif

# PLATFORM might have been given in wrong case (at least on Windows)
override TARGET = $(MAKE_INCLUDE_TARGET)

# makeTarg_raspi61_settings.mk
# This include file describes one and only one concrete device including 
# all extra hardware (shields) etc. If this hardware configuration changes
# this file must usually be changed accordingly.

# raspi61 is a Raspberry 3b
override PLATFORM = raspberry_03

# An optional short description of this target's specifica and variants.
# May be empty.
TARG_DES_TEXT = \
Platform for process IO by pigpio[d] and libmodbus.


MAC     ?= b8:27:eb:71:1d:cd
IP     ?= 192.168.89.61
IPwifi ?= 192.168.89.53
MACwifi ?= b8:27:eb:24:48:98
FTPtarget = $(IP)
# this is make default FTPdir = bin
