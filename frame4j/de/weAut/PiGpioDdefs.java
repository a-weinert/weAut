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
 *  @see ThePi
 *  @see ClientPigpiod
 *  @author   Albrecht Weinert
 *  @version  $Revision: 53 $ ($Date: 2021-06-21 19:59:41 +0200 (Mo, 21 Jun 2021) $)
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
   public static final int PI_CMD_NONE = 118; // 117 is the last command

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

/** Error: The initialisation of gpiod failed (-1). <br />
 *  <br />
 *  Hint: The use of the constants 
 *  {@link #PI_INIT_FAILED} .. {@link #PI_CMD_INTERRUPTED} or {@code PI_THeERROR}
 *  could be replaced by the enum {@link ErrPI} as
 *  {@link ErrPI}.{@code THeERROR}.{@link ErrPI#errNum()}.
 */
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
   public static final int PI_CMD_INTERRUPTED = -144; // Used by Python

   public static final int PI_PIGIF_ERR_0    = -2000; // 
   public static final int PI_PIGIF_ERR_99   = -2099; // 

   public static final int PI_CUSTOM_ERR_0   = -3000; // 
   public static final int PI_CUSTOM_ERR_999 = -3999; //    
   public static final int PI_SOCK_READ_LEN  = -3059; // socket read wrong length
   public static final int PI_CMD_BAD        = -3081; // command bad; not 0..117
   
/** <b>pigpiod errors  plus short explanation as enumeration</b>. <br />
 *  <br >
 *  The pigpiod {@link #errNum() errors} -1..-143 plus a very short English
 *  {@link #expl explanation} are hier as enum objects. Their
 *  {@link #ordinal() ordinal} numbers 1..143 are the positive
 *  {@linkplain #errNum() error codes}:  <br />
 *  &nbsp; {@link #ordinal()} {@code  ==  -}{@link #errNum()}  <br />
 *  This is supplemented by {@link #NO_ERROR no error (0)}, a the Python error
 *  {@link #CMD_INTERRUPTED command interrupted (-144)}, and a   
 *  {@link #UNKNOWN_ERROR unknown PI_error} for a wrong pigpiod error
 *  number.
 */
  public static enum ErrPI {
    // Before you change anything:
    // N.b.: The sequence of definition assigns the error number.
    // Joan N.N. has no gaps in their error numbers -1 .. -144, thus allowing
    // this efficient enum implementation. 
    NO_ERROR("no error (0)"),
    INIT_FAILED("pigpio initialisation failed"),
    BAD_USER_GPIO("GPIO not 0..31"),
    BAD_GPIO("GPIO not 0..53"),
    BAD_MODE("mode not 0..7"),
    BAD_LEVEL("level not 0 or 1"),
    BAD_PUD("pud not 0..2"),
    BAD_PULSEWIDTH("pulsewidth not 0 or 500..2500"),
    BAD_DUTYCYCLE("dutycycle not in range (default 0..255)"),
    BAD_TIMER("timer not 0..9"),
    BAD_MS("ms not 10..60000"),
    BAD_TIMETYPE("timetype not 0 or 1"),
    BAD_SECONDS("seconds < 0"),
    BAD_MICROS("micros not 0..999999"),
    TIMER_FAILED("gpioSetTimerFunc failed"),
    BAD_WDOG_TIMEOUT("timeout not 0..60000"),
    NO_ALERT_FUNC("deprecated"),
    BAD_CLK_PERIPH("clock peripheral not 0 or 1"),
    BAD_CLK_SOURCE("deprecated"),
    BAD_CLK_MICROS("clock micros not 1, 2, 4, 5, 8 or 10"),
    BAD_BUF_MILLIS("buf millis not 100..10000"),
    BAD_DUTYRANGE("dutycycle range not 25..40000"),
    BAD_SIGNUM("signum not 0..63"),
    BAD_PATHNAME("can't open pathname"),
    NO_HANDLE("no handle available"),
    BAD_HANDLE("unknown handle"),
    BAD_IF_FLAGS("ifFlags > 3"),
    BAD_CHANNEL("DMA channel not 0..14"),
    BAD_SOCKET_PORT("socket port not 1024..30000"),
    BAD_FIFO_COMMAND("unknown fifo command"),
    BAD_SECO_CHANNEL("DMA secondary channel not 0..14"),
    NOT_INITIALISED("function called before gpioInitialise"),
    INITIALISED("function called after gpioInitialise"),
    BAD_WAVE_MODE("waveform mode not 0..3"),
    BAD_CFG_INTERNAL("bad parameter in gpioCfgInternals call"),
    BAD_WAVE_BAUD("baud rate not 50-250000(RX)/1000000(TX)"), // -35
    TOO_MANY_PULSES("waveform has too many pulses"),
    TOO_MANY_CHARS("waveform has too many chars"),
    NOT_SERIAL_GPIO("no bit bang serial read in progress on GPIO"),
    BAD_SERIAL_STRUC("bad (null) serial structure parameter"),
    BAD_SERIAL_BUF("bad (null) serial buf parameter"),
    NOT_PERMITTED("GPIO operation not permitted"), // -41
    SOME_PERMITTED("one or more GPIO not permitted"),
    BAD_WVSC_COMMND("bad WVSC subcommand"),
    BAD_WVSM_COMMND("bad WVSM subcommand"),
    BAD_WVSP_COMMND("bad WVSP subcommand"),
    BAD_PULSELEN("trigger pulse length not 1..100"),
    BAD_SCRIPT("invalid script"),
    BAD_SCRIPT_ID("unknown script id"),
    BAD_SER_OFFSET("add serial data offset > 30 min"),
    GPIO_IN_USE("GPIO already in use"),
    BAD_SERIAL_COUNT("must read at least a byte at a time"),
    BAD_PARAM_NUM("script parameter id not 0..9"),
    DUP_TAG("script has duplicate tag"),
    TOO_MANY_TAGS("script has too many tags"),
    BAD_SCRIPT_CMD("illegal script command"),
    BAD_VAR_NUM("script variable id not 0..149"),
    NO_SCRIPT_ROOM("no more room for scripts"),
    NO_MEMORY("can't allocate temporary memory"),
    SOCK_READ_FAILED("socket read failed"),
    SOCK_WRIT_FAILED("socket write failed"),
    TOO_MANY_PARAM("too many script parameters (> 10)"),
    SCRIPT_NOT_READY("script initialising"),  // -62
    BAD_TAG("script has unresolved tag"),
    BAD_MICS_DELAY("bad MICS delay (too large)"),
    BAD_MILS_DELAY("bad MILS delay (too large)"),
    BAD_WAVE_ID("non existent wave id"),
    TOO_MANY_CBS("No more CBs for waveform"),
    TOO_MANY_OOL("No more OOL for waveform"),
    EMPTY_WAVEFORM("attempt to create an empty waveform"),
    NO_WAVEFORM_ID("No more waveform ids"),
    I2C_OPEN_FAILED("can't open I2C device"),
    SER_OPEN_FAILED("can't open serial device"),
    SPI_OPEN_FAILED("can't open SPI device"),
    BAD_I2C_BUS("bad I2C bus"),
    BAD_I2C_ADDR("bad I2C address"),
    BAD_SPI_CHANNEL("bad SPI channel"),
    BAD_FLAGS("bad i2c/spi/ser open flags"),
    BAD_SPI_SPEED("bad SPI speed"),
    BAD_SER_DEVICE("bad serial device name"),
    BAD_SER_SPEED("bad serial baud rate"),
    BAD_PARAM("bad i2c/spi/ser parameter"),
    I2C_WRITE_FAILED("I2C write failed"),
    I2C_READ_FAILED("I2C read failed"),
    BAD_SPI_COUNT("bad SPI count"),
    SER_WRITE_FAILED("ser write failed"),
    SER_READ_FAILED("ser read failed"),
    SER_READ_NO_DATA("ser read no data available"),
    UNKNOWN_COMMAND("unknown command"),
    SPI_XFER_FAILED("SPI xfer/read/write failed"),
    BAD_POINTER("bad(NULL) pointer"),
    NO_AUX_SPI("no auxiliary SPI on Pi A or B"),
    NOT_PWM_GPIO("GPIO is not in use for PWM"),
    NOT_SERVO_GPIO("GPIO is not in use for servo pulses"),
    NOT_HCLK_GPIO("GPIO has no hardware clock"),
    NOT_HPWM_GPIO("GPIO has no hardware PWM"),
    BAD_HPWM_FREQ("hardware PWM frequency not 1..125M"),
    BAD_HPWM_DUTY("hardware PWM dutycycle not 0..1M"),
    BAD_HCLK_FREQ("hardware clock frequency not 4689..250M"),
    BAD_HCLK_PASS("need password to use hardware clock 1"),
    HPWM_ILLEGAL("illegal, PWM in use for main clock"),
    BAD_DATABITS("serial data bits not 1..32"),
    BAD_STOPBITS("serial(half) stop bits not 2..8"),
    MSG_TOOBIG("socket/pipe message too big"),
    BAD_MALLOC_MODE("bad memory allocation mode"),
    TOO_MANY_SEGS("too many I2C transaction segments"),
    BAD_I2C_SEG("an I2C transaction segment failed"),
    BAD_SMBUS_CMD("SMBus command not supported"),
    NOT_I2C_GPIO("no bit bang I2C in progress on GPIO"),  // -108
    BAD_I2C_WLEN("bad I2C write length"),
    BAD_I2C_RLEN("bad I2C read length"),
    BAD_I2C_CMD("bad I2C command"),
    BAD_I2C_BAUD("bad I2C baud rate, not 50-500k"),
    CHAIN_LOOP_CNT("bad chain loop count"),
    BAD_CHAIN_LOOP("empty chain loop"),
    CHAIN_COUNTER("too many chain counters"),
    BAD_CHAIN_CMD("bad chain command"), // -116
    BAD_CHAIN_DELAY("bad chain delay micros"),
    CHAIN_NESTING("chain counters nested too deeply"),
    CHAIN_TOO_BIG("chain is too long"),
    DEPRECATED("deprecated function removed"),
    BAD_SER_INVERT ("bit bang serial invert not 0 or 1"),
    BAD_EDGE       ("bad ISR edge value, not 0..2"),
    BAD_ISR_INIT   ("bad ISR initialisation"),
    BAD_FOREVER    ("loop forever must be last chain command"),
    BAD_FILTER     ("bad filter parameter"), //-125
    BAD_PAD        ("bad pad number"), // -126 
    BAD_STRENGTH   ("bad pad drive strength"), // -127 
    FIL_OPEN_FAILED("file open failed"), // -128 
    BAD_FILE_MODE  ("bad file mode"),   // -129 
    BAD_FILE_FLAG  ("bad file flag"),  // -130 
    BAD_FILE_READ  ("bad file read"), // -131 
    BAD_FILE_WRITE ("bad file write"), // -132 
    FILE_NOT_ROPEN ("file not open for read"), // -133 
    FILE_NOT_WOPEN ("file not open for write"), // -134 
    BAD_FILE_SEEK  ("bad file seek"), // -135 
    NO_FILE_MATCH  ("no files match pattern"), // -136 
    NO_FILE_ACCESS ("no permission to access file"), // -137 
    FILE_IS_A_DIR  ("file is a directory"), // -138 
    BAD_SHELL_STATUS("bad shell return status"), // -139 
    BAD_SCRIPT_NAME("bad script name"), // -140 
    BAD_SPI_BAUD   ("SPI baud rate not 50..500k"), // -141 
    NOT_SPI_GPIO   ("no bit bang SPI in progress on GPIO"), // -142 
    BAD_EVENT_ID   ("bad event id"),    // -143 
    CMD_INTERRUPTED("command interrupted"), // -144   (Python, only)
    UNKNOWN_ERROR  ("unknown PI_error");  // currently -145 (may raise)

/** The pigpiod error's short explanation. <br > **/   
    public final String expl;

/** constructor. */  
    private ErrPI(String expl){ this.expl = expl; }
 
/** The error explanations as private array. <br />
 *  <br />
 *  index 0..145 is the ordinal, which is (must be!) 
 *  the negative error number. <br />
 *  [0] (-0) is no error (OK) <br />
 *  [145] (-145) represent unknown error or bad error number.<br />
 *  This value may / will raise when Joan N.N. defines errors -145 ...
 */
    private static final ErrPI errors[] = values();
  
/** Get explained error by error number (-) or index. <br />
 *  <br />
 *  @param num the negative error number or its absolute (positive) value 
 *  @return the error enum object or {@link #UNKNOWN_ERROR}; <br >
 *         0 yields  {@link #NO_ERROR} 
 */
    public static ErrPI byErrNum(int num){
      if (num < 0) num = -num; // non negative index
      if (num > 145) return UNKNOWN_ERROR;
      return errors[num];
    } // byErrNum(int)

/** Get the error number (-). <br />
 *  <br />
 *  @return the error number; it is negative (-{@link #ordinal()})
 */
    public int errNum(){ return -ordinal(); }

/** Description of the enum object. <br />
 *  <br />
 *  This method returns a description in the form <br />
 *  {@code ErrPI.NOT_PERMITTED    -41:  GPIO operation not permitted}   
 */
    @Override public String toString(){
      StringBuilder dest = new StringBuilder(89);
      dest.append("ErrPI.").append(name());
      do { dest.append(' '); } while (dest.length() < 23);
      dest.append('-').append(ordinal()).append(':');
      do { dest.append(' '); } while (dest.length() < 29);
      return dest.append(expl).toString();
    } // toString()

/** The enum ErrPi as application. <br />
 *  <br />
 *  This little helper lists all enum (pigpiod error) objects on standard
 *  output. Thats a nice list for reference.<br />
 *  Additionally, if something should be changed or added, this is the test
 *  output to check the correct error number assignment.<br />
 *  N.b.: for this enum class must hold: <br />   
 *  &nbsp;  &nbsp; {@link #ordinal()} {@code ==  -}{@link #errNum()}<br />
 *  <br >
 *  Hint: Start this application by <br  /> 
 *  {@code    java de.weAut.PiGpioDdefs$ErrPI }   
 *  @param args start parameters, ignored
 */
    public static void main(String[] args){
      System.out.println("\n\n  enum PiGpioDdefs.ErrPi "
                       + "\n  pigpiod error explanations"
                       + "\n  (c)  2021  Albrecht Weinert   a-weinert.de"
                       + "\n  Rev.   53  (21.06.2021) \n");
      for (ErrPI err : errors) { System.out.println(err); }
  } // main(String[])
 } // ErrPI (21.06.2021) 
  
/** Listing the ErrPi enums. <br />
 *  <br />
 *  This little helper lists all enum (pigpiod error) objects on standard
 *  output.
 *  
 *  @see ErrPI#main(String[])
 *  @param args start parameters, ignored
 */
 public static void main(String[] args){  ErrPI.main(args); }  
  
} // PiGpioDdefs (22.05.2019, 06.04.2021)
