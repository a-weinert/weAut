/*  Copyright 2019 Albrecht Weinert, Bochum, Germany (a-weinert.de)
 *  All rights reserved.
 *  
 *  This file is part of Frame4J 
 *  ( frame4j.de  https://weinert-automation.de/software/frame4j/ )
 * 
 *  Frame4J is made available under the terms of the 
 *  Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/  or as text in
 https://weinert-automation.de/java/docs/frame4j/de/frame4j/doc-files/epl.txt
 *  within the source distribution 
 */
package de.weAut;

/** <b>Definitions for the usage of the pigpio library for Raspberry Pi I/O</b>.<br />
 *  <br />
 *  The definitions are ported from 
 *  <a href="http://abyz.me.uk/rpi/pigpio/cif.html"
 *  title="Joan N.N.'s pigpio library">abyz.me.uk/rpi/pigpio/cif.html</a> or
 *  the respective .h-files.<br />
 *  <br />
 *  <a href=package-summary.html#co>&copy;</a> 
 *  Copyright 2019 &nbsp; Albrecht Weinert<br />
 *  @see ThePi ClientPigpiod
 *  @author   Albrecht Weinert
 *  @version  $Revision: 40 $ ($Date: 2021-04-19 21:47:30 +0200 (Mo, 19 Apr 2021) $)
 */
// so far:   V. 19  (17.05.2019) :  new
//           V. 36  (06.04.2021) :  minor typo

public interface PiGpioDdefs {

//-------------------------------------   Socket command codes   ------------
   public static final int PI_CMD_MODES =  0; // set pin mode
   public static final int PI_CMD_MODEG =  1; // get pin mode
   public static final int PI_INPUT  = 0;
   public static final int PI_OUTPUT = 1;
   public static final int PI_ALT0   = 4;
   public static final int PI_ALT1   = 5;
   public static final int PI_ALT2   = 6;
   public static final int PI_ALT3   = 7;
   public static final int PI_ALT4   = 3;
   public static final int PI_ALT5   = 2;

   
   public static final int PI_CMD_PUD   =  2; // set pull resistor mode
   public static final int PI_PUD_OFF   =  0; // free float
   public static final int PI_PUD_DOWN  =  1; // pull down
   public static final int PI_PUD_UP    =  2; // pull up
 
   public static final int PI_CMD_READ  =  3; // read pin
   public static final int PI_CMD_WRITE =  4; // write pin
   public static final int PI_CMD_PWM   =  5; // set PWM duty cycle (0..255)
   
   public static final int PI_CMD_PRS   =  6; // 
   public static final int PI_CMD_PFS   =  7; // set frequency
   public static final int PI_CMD_SERVO =  8; // set servo position (0, 1500..
   public static final int PI_CMD_WDOG  =  9; // 
   public static final int PI_CMD_BR1   = 10; // read bank 0
   public static final int PI_CMD_BR2   = 11; // 
   public static final int PI_CMD_BC1   = 12; // clear bank 0
   public static final int PI_CMD_BC2   = 13; // 
   public static final int PI_CMD_BS1   = 14; // set bank 0
   public static final int PI_CMD_BS2   = 15; // 
   public static final int PI_CMD_TICK  = 16; // 
   public static final int PI_CMD_HWVER = 17; // 
   public static final int PI_CMD_NO    = 18; // 
   public static final int PI_CMD_NB    = 19; // 
   public static final int PI_CMD_NP    = 20; // 
   public static final int PI_CMD_NC    = 21; // 
   public static final int PI_CMD_PRG   = 22; // 
   public static final int PI_CMD_PFG   = 23; // get PWM frequency
   public static final int PI_CMD_PRRG  = 24; // 
   public static final int PI_CMD_HELP  = 25; // 
   public static final int PI_CMD_PIGPV = 26; // 
   public static final int PI_CMD_WVCLR = 27; // 
   public static final int PI_CMD_WVAG  = 28; // 
   public static final int PI_CMD_WVAS  = 29; // 
   public static final int PI_CMD_WVGO  = 30; // 
   public static final int PI_CMD_WVGOR = 31; // 
   public static final int PI_CMD_WVBSY = 32; // 
   public static final int PI_CMD_WVHLT = 33; // 
   public static final int PI_CMD_WVSM  = 34; // 
   public static final int PI_CMD_WVSP  = 35; // 
   public static final int PI_CMD_WVSC  = 36; // 
   public static final int PI_CMD_TRIG  = 37; // 
   public static final int PI_CMD_PROC  = 38; // 
   public static final int PI_CMD_PROCD = 39; // 
   public static final int PI_CMD_PROCR = 40; // 
   public static final int PI_CMD_PROCS = 41; // 
   public static final int PI_CMD_SLRO  = 42; // 
   public static final int PI_CMD_SLR   = 43; // 
   public static final int PI_CMD_SLRC  = 44; // 
   public static final int PI_CMD_PROCP = 45; // 
   public static final int PI_CMD_MICS  = 46; // 
   public static final int PI_CMD_MILS  = 47; // 
   public static final int PI_CMD_PARSE = 48; // 
   public static final int PI_CMD_WVCRE = 49; // 
   public static final int PI_CMD_WVDEL = 50; // 
   public static final int PI_CMD_WVTX  = 51; // 
   public static final int PI_CMD_WVTXR = 52; // 
   public static final int PI_CMD_WVNEW = 53; // 

   public static final int PI_CMD_I2CO  = 54; // 
   public static final int PI_CMD_I2CC  = 55; // 
   public static final int PI_CMD_I2CRD = 56; // 
   public static final int PI_CMD_I2CWD = 57; // 
   public static final int PI_CMD_I2CWQ = 58; // 
   public static final int PI_CMD_I2CRS = 59; // 
   public static final int PI_CMD_I2CWS = 60; // 
   public static final int PI_CMD_I2CRB = 61; // 
   public static final int PI_CMD_I2CWB = 62; // 
   public static final int PI_CMD_I2CRW = 63; // 
   public static final int PI_CMD_I2CWW = 64; // 
   public static final int PI_CMD_I2CRK = 65; // 
   public static final int PI_CMD_I2CWK = 66; // 
   public static final int PI_CMD_I2CRI = 67; // 
   public static final int PI_CMD_I2CWI = 68; // 
   public static final int PI_CMD_I2CPC = 69; // 
   public static final int PI_CMD_I2CPK = 70; // 

   public static final int PI_CMD_SPIO  = 71; // 
   public static final int PI_CMD_SPIC  = 72; // 
   public static final int PI_CMD_SPIR  = 73; // 
   public static final int PI_CMD_SPIW  = 74; // 
   public static final int PI_CMD_SPIX  = 75; // 

   public static final int PI_CMD_SERO  = 76; // 
   public static final int PI_CMD_SERC  = 77; // 
   public static final int PI_CMD_SERRB = 78; // 
   public static final int PI_CMD_SERWB = 79; // 
   public static final int PI_CMD_SERR  = 80; // 
   public static final int PI_CMD_SERW  = 81; // 
   public static final int PI_CMD_SERDA = 82; // 

   public static final int PI_CMD_GDC   = 83; // get duty cycle
   public static final int PI_CMD_GPW   = 84; // get pulse width

   public static final int PI_CMD_HC    = 85; // 
   public static final int PI_CMD_HP    = 86; // 

   public static final int PI_CMD_CF1   = 87; // 
   public static final int PI_CMD_CF2   = 88; // 

   public static final int PI_CMD_BI2CC = 89; // 
   public static final int PI_CMD_BI2CO = 90; // 
   public static final int PI_CMD_BI2CZ = 91; // 

   public static final int PI_CMD_I2CZ  = 92; // 

   public static final int PI_CMD_WVCHA = 93; // 

   public static final int PI_CMD_SLRI  = 94; // 

   public static final int PI_CMD_CGI   = 95; // 
   public static final int PI_CMD_CSI   = 96; // 

   public static final int PI_CMD_FG    = 97; // 
   public static final int PI_CMD_FN    = 98; // 

   public static final int PI_CMD_NOIB  = 99; // 

   public static final int PI_CMD_WVTXM = 100; // 
   public static final int PI_CMD_WVTAT = 101; // 

   public static final int PI_CMD_PADS  = 102; // set pad strength
   public static final int PI_CMD_PADG  = 103; // get pad strength

   public static final int PI_CMD_FO    = 104; // 
   public static final int PI_CMD_FC    = 105; // 
   public static final int PI_CMD_FR    = 106; // 
   public static final int PI_CMD_FW    = 107; // 
   public static final int PI_CMD_FS    = 108; // 
   public static final int PI_CMD_FL    = 109; // 

   public static final int PI_CMD_SHELL = 110; // 

   public static final int PI_CMD_BSPIC = 111; // 
   public static final int PI_CMD_BSPIO = 112; // 
   public static final int PI_CMD_BSPIX = 113; // 

   public static final int PI_CMD_BSCX  = 114; // 

   public static final int PI_CMD_EVM   = 115; // 
   public static final int PI_CMD_EVT   = 116; // 

   public static final int PI_CMD_PROCU = 117; //


//-------------------------------------   p1 semantics  ---------------------   

  public static final int GPIO = 1;
  public static final int BITS = 2; 
  public static final int PAD  = 2; 
  public static final int MODE = 3; 
  public static final int SUBCMD = 4; 
  
  public static final int MICROS = 5;
  public static final int MILLIS = 6; 
  public static final int BAUD = 7; 
  public static final int COUNT = 8;   
  public static final int SDA = 9; 
  public static final int ARG1 = 10; 
  
  public static final int LEN_NAME = 11; 
  public static final int CONFIG = 12;  
  public static final int CHANNEL = 13;  
  public static final int WAVE_ID = 14; 
  public static final int SCRIPT_ID = 15; 
  public static final int EVENT = 16; 
  public static final int HANDLE = 17; 
  public static final int BUS = 18; 
  public static final int CS = 19; 
  public static final int CONTROL = 20; 
  public static final int IGNORE = 21; 

   
//-------------------------------------   Error codes   ---------------------
   
   public static final int PI_INIT_FAILED       = -1; // gpioInitialise failed
   public static final int PI_BAD_USER_GPIO     = -2; // GPIO not 0-31
   public static final int PI_BAD_GPIO          = -3; // GPIO not 0-53
   public static final int PI_BAD_MODE          = -4; // mode not 0-7
   public static final int PI_BAD_LEVEL         = -5; // level not 0-1
   public static final int PI_BAD_PUD           = -6; // pud not 0-2
   public static final int PI_BAD_PULSEWIDTH    = -7; // pulsewidth not 0 or 500-2500
   public static final int PI_BAD_DUTYCYCLE     = -8; // dutycycle outside set range
   public static final int PI_BAD_TIMER         = -9; // timer not 0-9
   public static final int PI_BAD_MS           = -10; // ms not 10-60000
   public static final int PI_BAD_TIMETYPE     = -11; // timetype not 0-1
   public static final int PI_BAD_SECONDS      = -12; // seconds < 0
   public static final int PI_BAD_MICROS       = -13; // micros not 0-999999
   public static final int PI_TIMER_FAILED     = -14; // gpioSetTimerFunc failed
   public static final int PI_BAD_WDOG_TIMEOUT = -15; // timeout not 0-60000
   public static final int PI_NO_ALERT_FUNC    = -16; // DEPRECATED
   public static final int PI_BAD_CLK_PERIPH   = -17; // clock peripheral not 0-1
   public static final int PI_BAD_CLK_SOURCE   = -18; // DEPRECATED
   public static final int PI_BAD_CLK_MICROS   = -19; // clock micros not 1, 2, 4, 5, 8, or 10
   public static final int PI_BAD_BUF_MILLIS   = -20; // buf millis not 100-10000
   public static final int PI_BAD_DUTYRANGE    = -21; // dutycycle range not 25-40000
   public static final int PI_BAD_DUTY_RANGE   = -21; // DEPRECATED (use PI_BAD_DUTYRANGE)
   public static final int PI_BAD_SIGNUM       = -22; // signum not 0-63
   public static final int PI_BAD_PATHNAME     = -23; // can't open pathname
   public static final int PI_NO_HANDLE        = -24; // no handle available
   public static final int PI_BAD_HANDLE       = -25; // unknown handle
   public static final int PI_BAD_IF_FLAGS     = -26; // ifFlags > 4
   public static final int PI_BAD_CHANNEL      = -27; // DMA channel not 0-14
   public static final int PI_BAD_PRIM_CHANNEL = -27; // DMA primary channel not 0-14
   public static final int PI_BAD_SOCKET_PORT  = -28; // socket port not 1024-32000
   public static final int PI_BAD_FIFO_COMMAND = -29; // unrecognised fifo command
   public static final int PI_BAD_SECO_CHANNEL = -30; // DMA secondary channel not 0-6
   public static final int PI_NOT_INITIALISED  = -31; // function called before gpioInitialise
   public static final int PI_INITIALISED      = -32; // function called after gpioInitialise
   public static final int PI_BAD_WAVE_MODE    = -33; // waveform mode not 0-3
   public static final int PI_BAD_CFG_INTERNAL = -34; // bad parameter in gpioCfgInternals call
   public static final int PI_BAD_WAVE_BAUD    = -35; // baud rate not 50-250K(RX)/50-1M(TX)
   public static final int PI_TOO_MANY_PULSES  = -36; // waveform has too many pulses
   public static final int PI_TOO_MANY_CHARS   = -37; // waveform has too many chars
   public static final int PI_NOT_SERIAL_GPIO  = -38; // no bit bang serial read on GPIO
   public static final int PI_BAD_SERIAL_STRUC = -39; // bad (null) serial structure parameter
   public static final int PI_BAD_SERIAL_BUF   = -40; // bad (null) serial buf parameter
   public static final int PI_NOT_PERMITTED    = -41; // GPIO operation not permitted
   public static final int PI_SOME_PERMITTED   = -42; // one or more GPIO not permitted
   public static final int PI_BAD_WVSC_COMMND  = -43; // bad WVSC subcommand
   public static final int PI_BAD_WVSM_COMMND  = -44; // bad WVSM subcommand
   public static final int PI_BAD_WVSP_COMMND  = -45; // bad WVSP subcommand
   public static final int PI_BAD_PULSELEN     = -46; // trigger pulse length not 1-100
   public static final int PI_BAD_SCRIPT       = -47; // invalid script
   public static final int PI_BAD_SCRIPT_ID    = -48; // unknown script id
   public static final int PI_BAD_SER_OFFSET   = -49; // add serial data offset > 30 minutes
   public static final int PI_GPIO_IN_USE      = -50; // GPIO already in use
   public static final int PI_BAD_SERIAL_COUNT = -51; // must read at least a byte at a time
   public static final int PI_BAD_PARAM_NUM    = -52; // script parameter id not 0-9
   public static final int PI_DUP_TAG          = -53; // script has duplicate tag
   public static final int PI_TOO_MANY_TAGS    = -54; // script has too many tags
   public static final int PI_BAD_SCRIPT_CMD   = -55; // illegal script command
   public static final int PI_BAD_VAR_NUM      = -56; // script variable id not 0-149
   public static final int PI_NO_SCRIPT_ROOM   = -57; // no more room for scripts
   public static final int PI_NO_MEMORY        = -58; // can't allocate temporary memory
   public static final int PI_SOCK_READ_FAILED = -59; // socket read failed
   public static final int PI_SOCK_WRIT_FAILED = -60; // socket write failed
   public static final int PI_TOO_MANY_PARAM   = -61; // too many script parameters (> 10)
   public static final int PI_NOT_HALTED       = -62; // DEPRECATED
   public static final int PI_SCRIPT_NOT_READY = -62; // script initialising
   public static final int PI_BAD_TAG          = -63; // script has unresolved tag
   public static final int PI_BAD_MICS_DELAY   = -64; // bad MICS delay (too large)
   public static final int PI_BAD_MILS_DELAY   = -65; // bad MILS delay (too large)
   public static final int PI_BAD_WAVE_ID      = -66; // non existent wave id
   public static final int PI_TOO_MANY_CBS     = -67; // No more CBs for waveform
   public static final int PI_TOO_MANY_OOL     = -68; // No more OOL for waveform
   public static final int PI_EMPTY_WAVEFORM   = -69; // attempt to create an empty waveform
   public static final int PI_NO_WAVEFORM_ID   = -70; // no more waveforms
   public static final int PI_I2C_OPEN_FAILED  = -71; // can't open I2C device
   public static final int PI_SER_OPEN_FAILED  = -72; // can't open serial device
   public static final int PI_SPI_OPEN_FAILED  = -73; // can't open SPI device
   public static final int PI_BAD_I2C_BUS      = -74; // bad I2C bus
   public static final int PI_BAD_I2C_ADDR     = -75; // bad I2C address
   public static final int PI_BAD_SPI_CHANNEL  = -76; // bad SPI channel
   public static final int PI_BAD_FLAGS        = -77; // bad i2c/spi/ser open flags
   public static final int PI_BAD_SPI_SPEED    = -78; // bad SPI speed
   public static final int PI_BAD_SER_DEVICE   = -79; // bad serial device name
   public static final int PI_BAD_SER_SPEED    = -80; // bad serial baud rate
   public static final int PI_BAD_PARAM        = -81; // bad i2c/spi/ser parameter
   public static final int PI_I2C_WRITE_FAILED = -82; // i2c write failed
   public static final int PI_I2C_READ_FAILED  = -83; // i2c read failed
   public static final int PI_BAD_SPI_COUNT    = -84; // bad SPI count
   public static final int PI_SER_WRITE_FAILED = -85; // ser write failed
   public static final int PI_SER_READ_FAILED  = -86; // ser read failed
   public static final int PI_SER_READ_NO_DATA = -87; // ser read no data available
   public static final int PI_UNKNOWN_COMMAND  = -88; // unknown command
   public static final int PI_SPI_XFER_FAILED  = -89; // spi xfer/read/write failed
   public static final int PI_BAD_POINTER      = -90; // bad (NULL) pointer
   public static final int PI_NO_AUX_SPI       = -91; // no auxiliary SPI on Pi A or B
   public static final int PI_NOT_PWM_GPIO     = -92; // GPIO is not in use for PWM
   public static final int PI_NOT_SERVO_GPIO   = -93; // GPIO is not in use for servo pulses
   public static final int PI_NOT_HCLK_GPIO    = -94; // GPIO has no hardware clock
   public static final int PI_NOT_HPWM_GPIO    = -95; // GPIO has no hardware PWM
   public static final int PI_BAD_HPWM_FREQ    = -96; // hardware PWM frequency not 1-125M
   public static final int PI_BAD_HPWM_DUTY    = -97; // hardware PWM dutycycle not 0-1M
   public static final int PI_BAD_HCLK_FREQ    = -98; // hardware clock frequency not 4689-250M
   public static final int PI_BAD_HCLK_PASS    = -99; // need password to use hardware clock 1
   public static final int PI_HPWM_ILLEGAL    = -100; // illegal, PWM in use for main clock
   public static final int PI_BAD_DATABITS    = -101; // serial data bits not 1-32
   public static final int PI_BAD_STOPBITS    = -102; // serial (half) stop bits not 2-8
   public static final int PI_MSG_TOOBIG      = -103; // socket/pipe message too big
   public static final int PI_BAD_MALLOC_MODE = -104; // bad memory allocation mode
   public static final int PI_TOO_MANY_SEGS   = -105; // too many I2C transaction segments
   public static final int PI_BAD_I2C_SEG     = -106; // an I2C transaction segment failed
   public static final int PI_BAD_SMBUS_CMD   = -107; // SMBus command not supported by driver
   public static final int PI_NOT_I2C_GPIO    = -108; // no bit bang I2C in progress on GPIO
   public static final int PI_BAD_I2C_WLEN    = -109; // bad I2C write length
   public static final int PI_BAD_I2C_RLEN    = -110; // bad I2C read length
   public static final int PI_BAD_I2C_CMD     = -111; // bad I2C command
   public static final int PI_BAD_I2C_BAUD    = -112; // bad I2C baud rate, not 50-500k
   public static final int PI_CHAIN_LOOP_CNT  = -113; // bad chain loop count
   public static final int PI_BAD_CHAIN_LOOP  = -114; // empty chain loop
   public static final int PI_CHAIN_COUNTER   = -115; // too many chain counters
   public static final int PI_BAD_CHAIN_CMD   = -116; // bad chain command
   public static final int PI_BAD_CHAIN_DELAY = -117; // bad chain delay micros
   public static final int PI_CHAIN_NESTING   = -118; // chain counters nested too deeply
   public static final int PI_CHAIN_TOO_BIG   = -119; // chain is too long
   public static final int PI_DEPRECATED      = -120; // deprecated function removed
   public static final int PI_BAD_SER_INVERT  = -121; // bit bang serial invert not 0 or 1
   public static final int PI_BAD_EDGE        = -122; // bad ISR edge value, not 0-2
   public static final int PI_BAD_ISR_INIT    = -123; // bad ISR initialisation
   public static final int PI_BAD_FOREVER     = -124; // loop forever must be last command
   public static final int PI_BAD_FILTER      = -125; // bad filter parameter
   public static final int PI_BAD_PAD         = -126; // bad pad number
   public static final int PI_BAD_STRENGTH    = -127; // bad pad drive strength
   public static final int PI_FIL_OPEN_FAILED = -128; // file open failed
   public static final int PI_BAD_FILE_MODE   = -129; // bad file mode
   public static final int PI_BAD_FILE_FLAG   = -130; // bad file flag
   public static final int PI_BAD_FILE_READ   = -131; // bad file read
   public static final int PI_BAD_FILE_WRITE  = -132; // bad file write
   public static final int PI_FILE_NOT_ROPEN  = -133; // file not open for read
   public static final int PI_FILE_NOT_WOPEN  = -134; // file not open for write
   public static final int PI_BAD_FILE_SEEK   = -135; // bad file seek
   public static final int PI_NO_FILE_MATCH   = -136; // no files match pattern
   public static final int PI_NO_FILE_ACCESS  = -137; // no permission to access file
   public static final int PI_FILE_IS_A_DIR   = -138; // file is a directory
   public static final int PI_BAD_SHELL_STATUS = -139; // bad shell return status
   public static final int PI_BAD_SCRIPT_NAME = -140; // bad script name
   public static final int PI_BAD_SPI_BAUD    = -141; // bad SPI baud rate, not 50-500k
   public static final int PI_NOT_SPI_GPIO    = -142; // no bit bang SPI in progress on GPIO
   public static final int PI_BAD_EVENT_ID    = -143; // bad event id
//   public static final int PI_CMD_INTERRUPTED = -144; // Used by Python

   public static final int PI_PIGIF_ERR_0    = -2000; // 
   public static final int PI_PIGIF_ERR_99   = -2099; // 

   public static final int PI_CUSTOM_ERR_0   = -3000; // 
   public static final int PI_CUSTOM_ERR_999 = -3999; //    
   public static final int PI_SOCK_READ_LEN  = -3059; // socket read wrong length
   public static final int PI_CMD_BAD        = -3081; // command bad; not 0..117

} // PiGpioDdefs (22.05.2019, 06.04.2021)
