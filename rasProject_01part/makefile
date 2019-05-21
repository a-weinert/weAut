# Makefile for a Raspberry project
# Copyright (c) 2019  Albrecht Weinert    < a-weinert.de >

# History:
#  Rev. 00 24.05.2017 : new as copy from atMegaBootloader Rev.38, 16.09.2015
#  Rev. 03 31.05.2017 : platform and target includes, ftp programme to target
#  Rev. 34 11.08.2017 : default flags for Modbus programmes w/o extra include
#  Rev.153 25.07.2018 : include handling enhanced; dev. station dep. macro
#  Rev.191 14.02.2019 : clean_all mentioned in help
#  Rev.205 16.05.2019 : default targets modified

MAKE_FILE_PROJ = rasProject_01
LAST_CHANGE_IN = $Revision: 205 $
LAST_CHANGE_AT = $Date: 2019-05-20 17:53:28 +0200 (Mo, 20 Mai 2019) $ 
LAST_CHANGE_BY = A. Weinert
COPYRIGHT_YEAR = 2018
COPYRIGHT_BY   = Albrecht Weinert, Bochum    < a-weinert.de >   
MAKE_WORKSTAT  := $(shell hostname)

# RUN_ON  evaluated here by standard trick  (there may be better criteria)
ifneq ($(COMSPEC)$(ComSpec),)
	RUN_ON ?= Windows
   EC=echo
   EL=@printf "     \n"
	FTPcom = winscp.com /script=progTransWin /parameter
	FTPopen = winscp.com /script=openWinSCP /parameter  
else
	RUN_ON ?= Linux
	EC=echo
   EL=@printf "     \n"
  	FTPcom = @$echo error no Linux CLI FTP 
	FTPopen = @$echo error no Linux CLI FTP 
endif	


# Evaluate target device if given (overiden by TARGET=name)
ifeq ($(MAKE_WORKSTAT),papaBig)
  TARGET ?= meterPi
else
  TARGET ?= raspi61
endif

# Look for the include file make_$(TARGET)_settings.mk in this
# actual directory and load it if it exists. And, if so, look for a file
# makeTarg_$(TARGET)_setFTP.mk to contain FTPuser = pi:raspberry if NOT
# the default pi:raspberry.
ifneq (,$(wildcard ./makeTarg_$(TARGET)_settings.mk))
	include ./makeTarg_$(TARGET)_settings.mk
	ifneq (,$(wildcard ./makeTarg_$(TARGET)_setFTP.mk))
	   include ./makeTarg_$(TARGET)_setFTP.mk
   endif
endif
MAC ?= unkown
IP ?= unkown
IPwifi ?= unkown
MACwifi ?= unkown
FTPtarget ?= unknown
FTPuser ?= pi:raspberry

ifeq ($(FTPtarget),unknown)
  	FTPcom = @$(EC) error no  FTPtarget
  	FTPopen =  @$(EC) error no  FTPtarget 
endif

# Evaluate platform device if given (overiden by PLATFORM=name)
# or set by target's incluce (should be, of course)
# PLATFORM ?= raspberry_01
# PLATFORM ?= raspberry_02
PLATFORM ?= raspberry_03

# Look for the include file make_$(PLATFORM)_settings.mk in the (this) actual
# directory. If it does not exist or if it emits an error this makefile fails. 
# The include files have to make platform specific setting of build variables.
include ./make_$(PLATFORM)_settings.mk


# Evaluate programme name (i.e. the .c souce with the static main, mostly)
# default rdGnBlinkBlink may be overidden in command line by MAIN_F=name or
# by PROGRAM=name
ifeq ($(origin MAIN_F), undefined)
	ifeq ($(origin PROGRAM), undefined)
#		MAIN_F = rdGnBlinkBlink
#		MAIN_F = keysPiGpioD
   	MAIN_F = hometersControl
	else
		MAIN_F =$(PROGRAM)
	endif
endif


# load the include file makeProg_$(MAIN_F)_settings.mk from (this) actual
# directory if it exists. If the additional make-include is present, its
# main purpose might be the definition of needed extra (library) sources.
ifneq (,$(wildcard ./makeProg_$(MAIN_F)_settings.mk))
include ./makeProg_$(MAIN_F)_settings.mk
else
extraLDFLAGS ?= -lwiringPi -lmodbus
endif
FTPdir ?= bin

# This should have been set in makeProg_$(MAIN_F)_settings.mk
# For a MAIN_F / PROGRAM w/o own .._settings.mk we default to
#extraLDFLAGS ?= -lbcm2835 
#extraLDFLAGS ?= -lwiringPi -lmodbus
extraSOURCES ?= 
extraCFLAGS ?=

# List of all external and internal commands

.PHONY : version help_all helpall help help_greet help_vars helpvars
.PHONY : help_platf helpplatf help_platforms help_platform
.PHONY : help_targ helptarg help_targets help_target
.PHONY : help_comm helpcomm help_commands helpcommands
.PHONY : clean claen clean_app_compiles  clear_app
.PHONY : cleanapp clean_up clean_svn_tmp clean_docu clean_all
.PHONY : progapp prog_app docu

# Documentation and help

version :
	@$(EL)
	@$(EC) Makefile for the Raspberry Pi C projects                       
	@$(EL)
	@$(EC) "Copyright (c)    $(COPYRIGHT_YEAR)   $(COPYRIGHT_BY)  "
	@$(EL)
	@$(EC) Revision $(word 2, $(LAST_CHANGE_IN)), $(word 2, $(LAST_CHANGE_AT))
	@$(EL)
	@$(EC) On your development workstation $(MAKE_WORKSTAT) 
	@$(EC) use "make help" or "make help_commands" for guidance.       
	@$(EL) 
	@$(EL) 

help_all helpall : version help_greet help_targ help_platf help_comm help_vars 
help : help_greet help_comm

help_greet :
	@$(EC)
	@$(EC) 'Use it by:    make  [settings]  [commands]    '
	@$(EC) 'e.g. : make PLATFORM=raspberry_03 clean  '
	@$(EC) 'e.g. : make TARGET=raspi67 MAIN_F=rdGnSimpleBlink all  '
	
help_platf helpplatf help_platforms help_platform :	
	@$(EL)
	@$(EC) 'Supported hardware platforms are defined by the existence '
	@$(EC) 'of a specific make include file. We do have: '
	@ls -1 make_*_settings.mk 
	@$(EC) 'The  platformName  is the part between make_ and _settings.mk  '
	@$(EL)
	@$(EC) 'Always say "make PLATFORM=platformName" for your actual platform. '
	@$(EC) 'Say "make PLATFORM=platformName platfInf" to get platform info.   '
	@$(EL)
	@$(EC) 'To extend the spectrum of hardware platforms just add another  '
	@$(EC) 'platformName plus its make include make_*?*_settings.mk.       '
	@$(EC) 'Depending on your software project additional C include files   '
	@$(EC) 'with names computed from PLATFORM may be needed.                '
	@$(EL)
	
help_targ helptarg help_targets help_target :
	@$(EL)
	@$(EC) 'Individual target devices are defined by the existence '
	@$(EC) 'of a specific make include file. We do have: '
	@ls -1 makeTarg_*_settings.mk 
	@$(EC) 'The  targetName  is the part between make_ and _settings.mk  '
	@$(EL)
	@$(EC) 'Always say "make TARGET=targetName" for your target device, no '
	@$(EC) 'matter if the describing include file was made. '
	@$(EC) 'Say "make TARGET=targetName helpVar" to see the settings.   '
	@$(EL)
	@$(EC) 'To extend the zoo of your target devices just add another  '
	@$(EC) 'targetName plus its make include makeTarg_*?*_settings.mk.       '
	@$(EC) 'Depending ob your software project additional C include files   '
	@$(EC) 'with names computed from PLATFORM may be needed.                '
	@$(EL)
	
	
help_comm helpcomm help_commands helpcommands :	
	@$(EL) 
	@$(EL)
	@$(EC) 'Commands  (also known as "targets" in make speech)  are:     '
	@$(EL) 
	@$(EC) "help_all   = the full help output incl. version and platform inf. "
	@$(EC) 'version    = version and copyright information '
	@$(EC) 'help_platf = info on supported hardware platforms            '
	@$(EC) 'help_targ  = info on supported target devices                '
	@$(EC) 'help_comm  = (this) info on commands only                    '
	@$(EC) 'help_vars  = infos on settable build variables only          '
	@$(EC) 'platf_inf  = infos on compiler, target and platform settings  '
	@$(EL) 
	@$(EC) 'all        = Make all applications '
	@$(EL)  
	@$(EC) "clean_app  = Delete the application's executable, only    "
	@$(EC) 'clean      = Delete all generated files for the application(s) '
	@$(EC) 'clean_docu = Delete Doxygen results and artefacts            '
	@$(EC) 'clean_svn_tmp  = Delete temporary SVN handling files         '
	@$(EC) 'clean_all  = All clean .. above    '
	@$(EL)
	@$(EC) 'progapp    = use FTP to load the application to the target   '
	@$(EC) 'openFTP    = open CLI FTP client connected to the target     '
	@$(EL)
	@$(EC) 'docu = generate the Doxygen html documentation (all docu by .bat) '
	@$(EL)
	@$(EC) Note: Almost all options, variables and values are case sensitive.
	@$(EL) 
	
help_vars helpvars helpvar :	
	@$(EL) 
	@$(EC) Makefile variables settable by command line are:
	@$(EL) 
	@$(EC)  'TARGET    : the target device name  (now $(TARGET))   '
	@$(EC)  '            a target specific include file should set '
	@$(EC)  '            IP ('$(IP)') MAC  ('$(MAC)')'
	@$(EC)  '            if applicable IPwifi ('$(IPwifi)') MAC  ('$(MACwifi)')'
	@$(EL) 
	@$(EC)  "FTPuser   : target's authentication for load; def.: pi:raspberry "
	@$(EC)  "FTPdir    : target's/FTPuser's directory for load (~/"$(FTPdir)')'
	@$(EL)
	@$(EC)  'PLATFORM  : the platform name  (now '$(PLATFORM)')   '
	@$(EC)  '            see the default by : make platf_inf     '
	@$(EC)  '            a platform specific include file should set '
	@$(EC)  '            MCU F_CPU MCUcores GPIOpins WLAN USB '
	@$(EC)  'MCU       : CPU type (now '$(MCU)') '
	@$(EC)  'F_CPU     : CPU clock frequency / Hz; (now '$(F_CPU)')       '
	@$(EC)  'MCUcores  : number of CPU cores '
	@$(EC)  'GPIOpins  : number of general purpose IO pins (now '$(GPIOpins)')  '
	@$(EC)  'WLAN      : 0 = no WLAN (now '$(WLAN)')                         '
	@$(EC)  'USB       : number of USB ports ('$(USB)')         '
	@$(EL)
	@$(EC)  'MAIN_F    : the programme name  (now '$(MAIN_F)')   '
	@$(EL)  '            also settable as PROGRAM=name in make command '



platf_inf platfinf platform plattform :
	@$(EL) 	
	@$(EC) compiler : $(CC)  on  $(RUN_ON) 
	@$(CC) --version
	@$(EC) -----------
	@$(EC) target   : $(TARGET)
	@$(EC) "$$TARG_DES_TEXT"
	@$(EC) -----------
	@$(EC) platform : $(PLATFORM)	
	@$(EC) "$$PLAF_DES_TEXT"
	@$(EC) -----------
	@$(EL) 


#----------------------------------------------------------------------------

# OBJDIR must be empty if compile artefacts beneath source or end with /
OBJDIR =
SRC = $(MAIN_F).c 
SRC += $(extraSOURCES)
# SRC = rdGnNoLibBlink.c weRasp/bcmGPIO.c

# Define all object files.
OBJ = $(SRC:%.c=$(OBJDIR)%.o)
# Define all listing files.
LST = $(SRC:%.c=$(OBJDIR)%.lst)

EXTRAINCDIRS = ./include
# CSTANDARD = -std=gnu99

CDEFS = -DF_CPU=$(F_CPU) -DPLATFORM=$(PLATFORM) -DMCU=$(MCU)
CDEFS += -DTARGET=$(TARGET)

ifeq ($(MAKE_WORKSTAT),PD3023)
 CDEFS += -DSMALL_SETUP=1
endif
CFLAGS = $(CDEFS) 
# -mcpu=cortex-a53 -mfpu=neon-vfpv4


CFLAGS += $(patsubst %,-I%,$(EXTRAINCDIRS))

CFLAGS += $(extraCFLAGS)

#for main 
LDFLAGS = -Wl,-Map=$(MAIN_F).map,--cref
LDFLAGS += $(EXTMEMOPTS)
LDFLAGS += $(patsubst %,-L%,$(EXTRALIBDIRS))
LDFLAGS += $(extraLDFLAGS)


ALL_CFLAGS = -I. $(CFLAGS) $(GENDEPFLAGS)

# External programs and commands.
# Make sure all the following being installed and on search path.
# On Windows this would be something like 
# C:\util\WinAVR\bin;C:\util\WinAVR\utils\bin;C:\util\WinRaspi\bin
#TOOLPREFIX=avr-
TOOLPREFIX=arm-linux-gnueabihf-
CC = $(TOOLPREFIX)gcc
PP = $(TOOLPREFIX)g++
OBJCOPY = $(TOOLPREFIX)objcopy
OBJDUMP = $(TOOLPREFIX)objdump
SIZE = $(TOOLPREFIX)size
AR = $(TOOLPREFIX)ar rcs
NM = $(TOOLPREFIX)nm
REMOVE = rm -f
REMOVEDIR = rm -rf
COPY = cp
SLEEP = sleep
GREP = grep
GAWK = gawk


# Define messages (English only)

MSG_EXTENDED_LISTING = Creating Extended Listing:
MSG_SYMBOL_TABLE = Creating Symbol Table:
MSG_LINKING = Linking:
MSG_COMPILING = Compiling gcC:
MSG_CLEANING = Cleaning project on $(RUN_ON) 
MSG_CREATING_LIBRARY = Creating library:
MSG_PROG_TRANS = Transfering programme 
MSG_FTP_OPEN = FTP command line client connected to  $(TARGET)  
MSG_NOCLEANALL = No building or cleaning 

all: application
application: build $(MAIN_F) 

# Display compiler version information.
gccversion : 
	@$(EC) The compiler version
	@$(CC) --version

build: elf lss sym 

elf: $(MAIN_F).elf 
#eep: $(MAIN_F).eep
lss: $(MAIN_F).lss
sym: $(MAIN_F).sym
# LIBNAME=lib$(MAIN_F).a
# lib: $(LIBNAME)

#default rule replaced a) to monitor the .h b) to get compiles listed
%.o: %.c include/arch/config.h \
include/arch/config_raspberry_03.h include/weUtil.h include/sysBasic.h \
include/weModbus.h include/weUtil.h include/pigpiod_if2.h \
include/pigpio.h include/weGPIOd.h include/pigpiod_if2.h \
include/hometersControl.h include/weShareMem.h
	@$(EL)
	@$(EC) $(MSG_COMPILING) $@
	$(CC) -c $(CPPFLAGS) $(CFLAGS) $< -o $@

# Create extended listing file from ELF output file.
%.lss: %.elf
	@$(EL)
ifeq ($(noCleanNoAll),true)
	@$(EC) "$(MSG_NOCLEANALL) of .elf .sym etc. "
else
	@$(EC) $(MSG_EXTENDED_LISTING) $@
	$(OBJDUMP) -h -S -z $< > $@
endif

# Create a symbol table from ELF output file.
%.sym: %.elf
	@$(EL)
	@$(EC) $(MSG_SYMBOL_TABLE) $@
	$(NM) -n $< > $@
	
	
# Create library from object files.
.SECONDARY : $(MAIN_F).a
.PRECIOUS : $(OBJ)
%.a: $(OBJ)
	@$(EL)
	@$(EC) $(MSG_CREATING_LIBRARY) $@
	$(AR) $@ $(OBJ)

	
# Link: create ELF output file = programme from object files.
.SECONDARY : $(MAIN_F).elf
.PRECIOUS : $(OBJ)
%.elf: $(OBJ)
	@$(EL)
	@$(EC) $(MSG_LINKING) $@
	$(CC) $(ALL_CFLAGS) $^ --output $@ $(LDFLAGS)

$(MAIN_F) : $(MAIN_F).elf
	$(COPY)  $(MAIN_F).elf $(MAIN_F)
	
progapp prog_app : $(MAIN_F)
	@$(EC) $(MSG_PROG_TRANS) $@
	$(FTPcom) $(FTPuser) $(FTPtarget) $(FTPdir) $(subst /,\,$(MAIN_F))

openFTP openWinSCP :
	@$(EC) $(MSG_OPEN_FTP) $@
	$(FTPopen) $(FTPuser) $(FTPtarget) $(FTPdir) $(subst /,\,$(MAIN_F))
	


# clean the project         ------------------------------------------------
# change 18.06.2018 : separate docu and svn from programmes
clean claen : clean_app_compiles  clear_app
clean_all : clean_app_compiles  clear_app clean_docu clean_svn_tmp
clean_app cleanapp :  clear_app

clear_app :
	@$(EL)
ifeq ($(noCleanNoAll),true)
	@$(EC) "$(MSG_NOCLEANALL) of executable "
else
	@$(EC) "$(MSG_CLEANING) application executable "
	$(REMOVE) $(MAIN_F)
endif

clean_app_compiles : 
	@$(EL)
ifeq ($(noCleanNoAll),true)
	@$(EC) "$(MSG_NOCLEANALL) of compile artefacts "
else
	@$(EC) "$(MSG_CLEANING) compile artefacts "
	$(REMOVE) $(MAIN_F).eep
	$(REMOVE) $(MAIN_F).cof
	$(REMOVE) $(MAIN_F).elf
	$(REMOVE) $(MAIN_F).map
	$(REMOVE) $(MAIN_F).sym
	$(REMOVE) $(MAIN_F).lss

	$(REMOVE) $(SRC:%.c=$(OBJDIR)%.o)
	$(REMOVE) $(SRC:%.c=$(OBJDIR)%.lst)
	$(REMOVE) $(SRC:.c=.s)
	$(REMOVE) $(SRC:.c=.d)
	$(REMOVE) $(SRC:.c=.i)
	$(REMOVEDIR) .dep
# repair GCC's -save-temps bug
	$(REMOVE) *.i
	$(REMOVE) *.s
endif

clean_docu : 
	@$(EC)
	@$(EC) "$(MSG_CLEANING) doxygen artefacts  "
	$(REMOVEDIR) doxydocu/xml
	$(REMOVEDIR) doxydocu/html
	$(REMOVEDIR) doxydocu/latex
	$(REMOVE) dox_warnings.are
	
clean_svn_tmp :
	@$(EC)
	@$(EC) "$(MSG_CLEANING) svn temp files  "
	$(REMOVE) svnDiary.log
	$(REMOVE) svn.stati.be4.txt
	$(REMOVE) svn.stati.be4.txt
	$(REMOVE) svn.mtime.got.txt
	
# Target: docu (doxygen experimental)
docu : clean_docu
	@$(EC)
	@$(EC) "Generating Doxygen documentation "
	doxygen
	@echo
	head ./dox_warnings.are
	@echo [ end of warnings ]
	@$(EC)
	@$(EC) "On no errors run make.bat in /doxydocu/latex to make .pdf"
	
	
	
	