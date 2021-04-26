# Makefile for a Raspberry project
# 
#  Copyright  (c)  2017 2021   Albrecht Weinert
#  weinert-automation.de           a-weinert.de
#
#      /         /      /\
#     /         /___   /  \      |
#     \        /____\ /____\ |  _|__
#      \  /\  / \    /      \|   |
#       \/  \/   \__/        \__/|_ 


# History:
#  Rev. 00 24.05.2017 : new as copy from atMegaBootloader Rev.38, 16.09.2015
#  Rev. 03 31.05.2017 : platform and target includes, ftp program to target
#  Rev.153 25.07.2018 : include handling enhanced; dev. station dep. macro
#  Rev.226 11.07.2020 : noCleanNoAll replaced by mainFis for directory transf.
#  Rev.233 21.10.2020 : repaired: ECHO ist ausgeschaltet (OFF)
#  Rev.234 20.01.2021 : Doxydocu pdf titlel with  pdftk.exe

MAKE_FILE_PROJ = rasProject_01
LAST_CHANGE_IN = $Revision: 240 $
LAST_CHANGE_AT = $Date: 2021-04-10 09:45:45 +0200 (Sa, 10 Apr 2021) $ 
LAST_CHANGE_BY = A. Weinert
COPYRIGHT_YEAR = 2020
COPYRIGHT_BY   = Albrecht Weinert, Bochum    ( a-weinert.de )   
MAKE_WORKSTAT  := $(shell hostname)

# RUN_ON  evaluated here by standard trick  (there may be better criteria)
ifneq ($(COMSPEC)$(ComSpec),)
	RUN_ON ?= Windows
  EC=echo
  EL=@printf "     \n"
	FTPputF = winscp.com /script=progTransWin /parameter
	FTPopen = winscp.com /script=openWinSCP /parameter
	FTPputD = winscp.com /script=dirTransWin /parameter
else
	RUN_ON ?= Linux
	EC=echo
  EL=@printf "     \n"
  FTPputD = @$echo error no Linux CLI FTP
 	FTPputF = @$echo error no Linux CLI FTP 
	FTPopen = @$echo error no Linux CLI FTP 
endif	


# default for target device if not given by TARGET=name
ifeq ($(MAKE_WORKSTAT),papaBig)
  TARGET ?= raspi67
else
  TARGET ?= raspi61
endif

# Look for the include file make_$(TARGET)_settings.mk in this actual 
# directory. If it exists, load if and look for a file 
# makeTarg_$(TARGET)_setFTP.mk to contain FTPuser etc.
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
mainFis ?= recipRes

ifeq ($(FTPtarget),unknown)
  FTPputF = @$(EC) error no  FTPtarget
  FTPputD = @$(EC) error no  FTPtarget
  FTPopen = @$(EC) error no  FTPtarget 
endif

# Evaluate platform device if given (overiden by PLATFORM=name)
# or set by target's include (that's better, of course)
# PLATFORM ?= raspberry_01
# PLATFORM ?= raspberry_02
PLATFORM ?= raspberry_03
# PLATFORM ?= raspberry_04

# Look for the include file make_$(PLATFORM)_settings.mk in the (this) actual
# directory. If it does not exist or if it emits an error this makefile fails. 
# The include files have to make platform specific setting of build variables.
include ./make_$(PLATFORM)_settings.mk


# Evaluate program name (i.e. the .c souce with the static main, mostly)
# default rdGnBlinkBlink may be overidden in command line by MAIN_F=name or
# by PROGRAM=name
ifeq ($(origin MAIN_F), undefined)
	ifeq ($(origin PROGRAM), undefined)
#	  MAIN_F = testOnPi
#   MAIN_F = dcf77onPi
	  MAIN_F = homeDoorPhone
#		MAIN_F = rdGnBlinkBlink
#		MAIN_F = keysPiGpioD
#   MAIN_F = hometersControl
# 	MAIN_F = minTerm
#  	MAIN_F = growattRead
#   MAIN_F = growattLink
#   MAIN_F = getLocalWeatherData
	else
		MAIN_F = $(PROGRAM)
	endif
endif


# Load the include file makeProg_$(MAIN_F)_settings.mk from (this) actual
# directory. If this additional make-include is present, its main purpose
# might be the definition of needed extra (library) sources.
ifneq (,$(wildcard ./makeProg_$(MAIN_F)_settings.mk))
include ./makeProg_$(MAIN_F)_settings.mk
else
# default for programs w/o .._settings.mk, i.e. the libmodbus tests 
extraLDFLAGS ?= -lrt -pthread -lmodbus
endif
FTPdir ?= bin

# This should have been set in makeProg_$(MAIN_F)_settings.mk
# For a MAIN_F / PROGRAM w/o own .._settings.mk we default to
#extraLDFLAGS ?= -lbcm2835 
#extraLDFLAGS ?= -lrt -pthread -lpigpiod_if2
extraSOURCES ?= 
extraCFLAGS ?=

# List of all external and internal commands
.PHONY : version helpVers help helpGreet
.phony : help_all helpall helpAll help_greet help_vars helpvars helpVars 
.PHONY : help_platf helpplatf help_platforms help_platform helpPlatf
.PHONY : help_targ helptarg help_targets help_target helpTarget helpTarg
.PHONY : help_comm helpcomm help_commands helpcommands helpComm
.PHONY : clean claen clean_app_compiles  clear_app
.PHONY : cleanapp clean_up clean_svn_tmp clean_docu clean_all
.PHONY : progapp prog_app docu upload uploadDir uploaddir


# Documentation and help

version helpVers :
	@$(EL)
	@$(EC) Makefile for the Raspberry Pi C projects                       
	@$(EL)
	@$(EC)  Copyright  (c)	 $(COPYRIGHT_YEAR)   $(COPYRIGHT_BY)
	@$(EC)  Revision $(word 2, $(LAST_CHANGE_IN)), $(word 2, $(LAST_CHANGE_AT))
	@$(EL)
	@$(EC) On your development workstation $(MAKE_WORKSTAT) use
	@$(EC) "make help" or "make help_commands" for guidance.       
	@$(EL) 

help_all helpall helpAll : version helpGreet helpTarg helpPlatf helpComm helpVars 
	
help : help_greet help_comm
	
help_greet helpGreet:
	@$(EL)
	@$(EC)  Use it by:    make  [settings]  [commands]   
	@$(EC)      e.g. : make PLATFORM=raspberry_03 clean  
	@$(EC)      e.g. : make TARGET=raspi67 MAIN_F=rdGnSimpleBlink all  
	
help_platf helpplatf help_platforms help_platform helpPlatf:	
	@$(EL)
	@$(EC)  Supported hardware platforms are defined by the existence 
	@$(EC)  of a specific make include file. We do have: 
	@$(EL)
	@ls -1 make_*_settings.mk
	@$(EC)       ^^^^^^^^^^^^^^^^^^^^^^^^
	@$(EC)  The  platformName  is the part between make_ and _settings.mk  
	@$(EL)
	@$(EC)  Always say "make PLATFORM=platformName" for your actual platform.
	@$(EC)  Say "make PLATFORM=platformName platfInf" to get platform info.  
	@$(EL)
	@$(EC)  To extend the spectrum of hardware platforms just add another  
	@$(EC)  platformName plus its make include make_*?*_settings.mk.      
	@$(EC)  Depending on your software project additional C include files  
	@$(EC)  with names computed from PLATFORM may be needed.     
	@$(EL)
	
help_targ helptarg help_targets help_target helpTarget helpTarg:
	@$(EL)
	@$(EC)  Individual target devices are defined by the existence 
	@$(EC)  of a specific make include file. We do have: 
	@$(EL)
	@ls -1 makeTarg_*_settings.mk 
	@$(EC)          ^^^^^^^^^^^^
	@$(EC)  The   targetName   is the part between makeTarg_ and _settings.mk  
	@$(EL)
	@$(EC)  Always say "make TARGET=targetName" for your target device, no 
	@$(EC)  matter if the describing include file was made. 
	@$(EC)  Say "make TARGET=targetName helpVar" to see the settings.   
	@$(EL)
	@$(EC)  To extend the zoo of your target devices just add another  
	@$(EC)  targetName plus its make include makeTarg_*?*_settings.mk.    
	@$(EC)  Depending ob your software project additional C include files 
	@$(EC)  with names computed from PLATFORM may be needed.              
	@$(EL)
	
	
help_comm helpcomm help_commands helpcommands helpComm:	
	@$(EL) 
	@$(EL)
	@$(EC) Commands  (also known as "targets" in make speech)  are:
	@$(EL) 
	@$(EC)  help_all   = the full help output incl. version and platform info
	@$(EC)  version    = version and copyright information 
	@$(EC)  help_platf = info on supported hardware platforms       
	@$(EC)  help_targ  = info on supported target devices           
	@$(EC)  help_comm  = (this) info on commands only               
	@$(EC)  help_vars  = infos on settable build variables only     
	@$(EC)  platf_inf  = infos on compiler, target and platform settings  
	@$(EL) 
	@$(EC)  all        = Make / build all for the application / program
	@$(EL)  
	@$(EC)  clean_app  = Delete the application's executable, only
	@$(EC)  clean      = Delete all generated files for the application(s) 
	@$(EC)  clean_docu = Delete Doxygen results and artefacts           
	@$(EC)  clean_svn_tmp  = Delete temporary SVN handling files        
	@$(EC)  clean_all  = All clean .. above  
	@$(EL)
	@$(EC)  progapp    = use FTP to load the application to the target  
	@$(EC)  upload     = upload a given file or a directory with subdirectories
	@$(EC)               (if mainFis = directory) w/o looking for recipe  
	@$(EC)  openFTP    = open FTP CLI client program connected to the target   
	@$(EL)
	@$(EC)  docu = generate the Doxygen html documentation (all docu by .bat)
	@$(EL)
	@$(EC) Note: Almost all options, variables and values are case sensitive.
	@$(EL) 
	
help_vars helpvars helpvar helpVars:	
	@$(EL) 
	@$(EC) Makefile variables settable by command line are:
	@$(EL) 
	@$(EC)  TARGET    : the target device name  (now $(TARGET))   
	@$(EC)              a target specific include file should set PLATFORM  
	@$(EC)              ('$(PLATFORM)'), IP ('$(IP)'), MAC 
	@$(EC)              ('$(MAC)') and if applicable IPwifi
	@$(EC)              ('$(IPwifi)') and  MACwifi ('$(MACwifi)')
	@$(EL) 
	@$(EC)  FTPuser   : target's authentication for load; def.: pi:raspberry 
	@$(EC)  FTPdir    : target's/FTPuser's directory for load (~/"$(FTPdir)')
	@$(EL)
	@$(EC)  PLATFORM  : the platform name  (now '$(PLATFORM)')   
	@$(EC)              see the default by: make platf_inf     
	@$(EC)              a platform specific include file should set 
	@$(EC)              MCU F_CPU MCUcores GPIOpins WLAN USB 
	@$(EC)  MCU       : CPU type (now '$(MCU)') 
	@$(EC)  F_CPU     : CPU clock frequency / Hz; (now '$(F_CPU)')       
	@$(EC)  MCUcores  : number of CPU cores '
	@$(EC)  GPIOpins  : number of general purpose IO pins (now '$(GPIOpins)')  
	@$(EC)  WLAN      : 0 = no WLAN; 1,2 = number of bands (now '$(WLAN)')     
	@$(EC)  USB       : number of USB ports ('$(USB)')         
	@$(EC)  USB2 USB3 : optionally number of USB2 and USB3 ports               
	@$(EL)
	@$(EC)  MAIN_F    : the program name  (now '$(MAIN_F)')      
	@$(EC)              also settable as PROGRAM=name in make command 
	@$(EC)  mainFis   : recipRes (default), directory or file; now $(mainFis).
	@$(EC)              Not recipRes means no generate nor clean.
	@$(EL)


platf_inf platfinf platform plattform :
	@$(EL) 	
	@$(EC) compiler : $(CC)  on  $(RUN_ON) 
	@$(CC) --version
	@$(EC) -----------
	@$(EC) target   : $(TARGET)
	@$(EC) $(TARG_DES_TEXT)
	@$(EC) -----------
	@$(EC) platform : $(PLATFORM)	
	@$(EC) $(PLAF_DES_TEXT)
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
MSG_PROG_TRANS = Transfering program 
MSG_FILE_TRANS = Transfering file 
MSG_DIR_UPDATE = Updating directory 
MSG_FTP_OPEN = FTP command line client connected to  $(TARGET)  
MSG_NOCLEANALL = No building or cleaning 

ifeq ($(mainFis),recipRes)
all: application
else
all:
	@$(EC) $(MSG_NOCLEANALL) for $(MAIN_F)
endif

application: build $(MAIN_F) 

ifeq ($(mainFis),recipRes)
build: elf lss sym 
else
build:
	@$(EC) "$(MSG_NOCLEANALL) of PROGRAM $(MAIN_F)  "
endif

# Display compiler version information.
gccversion : 
	@$(EC) The compiler version
	@$(CC) --version



elf: $(MAIN_F).elf 
#eep: $(MAIN_F).eep
lss: $(MAIN_F).lss
sym: $(MAIN_F).sym
# LIBNAME=lib$(MAIN_F).a
# lib: $(LIBNAME)

ifeq ($(mainFis),recipRes)
#default rule replaced a) to monitor the .h b) to get compiles listed
%.o: %.c include/arch/config.h \
include/arch/config_raspberry_03.h include/weUtil.h include/sysBasic.h \
include/weModbus.h include/weUtil.h include/pigpiod_if2.h \
include/pigpio.h include/weGPIOd.h include/pigpiod_if2.h \
include/hometersControl.h include/weShareMem.h
#%.o : %.c
	@$(EL)
	@$(EC) $(MSG_COMPILING) $@
 ifeq ($(mainFis),recipRes)	
	$(CC) -c $(CPPFLAGS) $(CFLAGS) $< -o $@
 endif
else	
%.o :
	@$(EC) "$(MSG_NOCLEANALL) of .o

endif

# Create extended listing file from ELF output file.
%.lss: %.elf
	@$(EL)
ifneq ($(mainFis),recipRes)
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

	
# Link: create ELF output file = program from object files.
.SECONDARY : $(MAIN_F).elf
.PRECIOUS : $(OBJ)
%.elf: $(OBJ)
	@$(EL)
ifeq ($(mainFis),recipRes)
	@$(EC) $(MSG_LINKING) $@
	$(CC) $(ALL_CFLAGS) $^ --output $@ $(LDFLAGS)
endif

$(MAIN_F) : $(MAIN_F).elf
ifeq ($(mainFis),recipRes)
	$(COPY)  $(MAIN_F).elf $(MAIN_F)
endif

ifeq ($(mainFis),recipRes)	
progapp prog_app : $(MAIN_F)
	@$(EC) $(MSG_PROG_TRANS) $@
	$(FTPputF) $(FTPuser) $(FTPtarget) $(FTPdir) $(subst /,\,$(MAIN_F))
else
progapp prog_app : upload
endif
	
upload :
ifeq ($(mainFis),directory)
 ifeq ($(useDefSdir),true)
	@$(EC) $(MSG_DIR_UPDATE) $(defaultSourceDir)
	$(FTPputD) $(FTPuser) $(FTPtarget) $(FTPdir) $(subst /,\,$(defaultSourceDir))
 endif
	@$(EC) $(MSG_DIR_UPDATE) $(MAIN_F)
	$(FTPputD) $(FTPuser) $(FTPtarget) $(FTPdir) $(subst /,\,$(MAIN_F))
else
	@$(EC) $(FILE_TRANS) $(MAIN_F)
	$(FTPputF) $(FTPuser) $(FTPtarget) $(FTPdir) $(subst /,\,$(MAIN_F))
endif

openFTP openWinSCP :
	@$(EC) $(MSG_OPEN_FTP) $@
	$(FTPopen) $(FTPuser) $(FTPtarget) $(FTPdir) $(subst /,\,$(MAIN_F))
	

# clean the project         ------------------------------------------------
# change 18.06.2018 : separate docu and svn from programs
clean claen : clean_app_compiles  clear_app
clean_all : clean_app_compiles  clear_app clean_docu clean_svn_tmp
clean_app cleanapp :  clear_app

clear_app :
	@$(EL)
ifneq ($(mainFis),recipRes)
	@$(EC) "$(MSG_NOCLEANALL) of executable "
else
	@$(EC) "$(MSG_CLEANING) application executable "
	$(REMOVE) $(MAIN_F)
endif

clean_app_compiles : 
	@$(EL)
ifneq ($(mainFis),recipRes)
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
	@$(EL)
	@$(EC) "$(MSG_CLEANING) doxygen artefacts  "
	$(REMOVEDIR) doxydocu/xml
	$(REMOVEDIR) doxydocu/html
	$(REMOVEDIR) doxydocu/latex
	$(REMOVEDIR) doxydocu/rtf
	$(REMOVE) dox_warnings.are
	
clean_svn_tmp :
	@$(EL)
	@$(EC) "$(MSG_CLEANING) svn temp files  "
	$(REMOVE) svnDiary.log
	$(REMOVE) svn.stati.be4.txt
	$(REMOVE) svn.stati.be4.txt
	$(REMOVE) svn.mtime.got.txt
	$(REMOVE) weAutSys_tag.xml
	
# Target: docu (doxygen experimental)
docu : clean_docu
	@$(EL)
	@$(EC) "Generating Doxygen documentation "
	doxygen
	@$(EL)
	head ./dox_warnings.are
	@echo [ end of warnings ]
	@$(EL)
	@$(EC) "On no errors run makePDFdocu.bat to get forDocu/weRasProject_01.pdf"
	
	
	
	