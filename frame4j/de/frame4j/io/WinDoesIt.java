/*  Copyright 2009 Albrecht Weinert, Bochum, Germany (a-weinert.de)
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
package de.frame4j.io;

import de.frame4j.util.App;
import de.frame4j.util.AppLangMap;

/** <b>System services &mdash; especially for the serial interface</b>. <br />
 *  <br />
 *  This class provides the underlying operating system's services by native
 *  methods (JNI) of the library  &quot;bsDoesItNative&quot;. On Windows this
 *  library must exist as &quot;bsDoesItNative.dll&quot;. Until July 2013 that
 *  Windows shared library (DLL) was available as C/C++ implementation for
 *  Win32 only. As of August 2013 all was modified to support Windows 64 bit
 *  systems as well.<br />
 *  <br />
 *  If functionally equivalent shared libraries were available for other
 *  platforms, like say a &quot;bsDoesItNative.so&quot; for Solaris e.g.,
 *  this class would be usable for other platforms too. As of writing it's 
 *  Frame4J just provides Windows implementations. (And therefore its badly
 *  needed as Sun dropped the comm-extensions for Windows.)<br />
 *  <br />
 *  Constants and methods are as far as possible (mostly one to one) those
 *  of the services provided by the Windows platform making those services
 *  available to Java applications.<br />
 *  Remark 1: This recommendable design approach may be called<br />
 *  &nbsp; * &nbsp; &quot;minimal native wrapper&quot;<br />
 *  or as a motto <br />
 *  &nbsp; * &nbsp; 
 &quot;If it has to be C/C++, let's take the absolute minimal dose.&quot;<br />
 *  <br />
 *  Remark 2: The serial interface services for Windows (and their quality)
 *            provided by this approach go beyond the SUN Comm API's
 *            possibilities.<br />
 *  <br />
 *  The (static) methods of this class may of cause be used directly. Usually
 *  a comfortable and OO like wrapper, as provided by {@link SerNimpl} is
 *  preferred by the application programmer. This wrapper is also robust
 *  in the sense that the (native) C functions (just forwarded by this class)
 *  by its nature lack Java's type safety. The direct caller of this class'
 *  methods has to check the
 *  ability to be called and the parameter values in every single case.<br />
 *  <br />
 *  <br />
 *  <b>Hints for making the Windows implementation
 *       &quot;bsDoesItNative.dll&quot;</b>:<br />
 *  <br />
 *  The fist step is the generation of a compatible include file by<br />
 *  <br /> &nbsp;
 *    javah -o de_a-weinert_io_WinDoesIt.h de.frame4j.io.WinDoesIt
 *  <br /><br />
 *  Any C or C++ sources etc., like e.g. WinDoesIt.cpp and WinDoesIt.def, have
 *  to be consistent to the actual WinDoesIt.h <br />
 *  <br />
 *  Hints on the library generation of are found in the comments of
 *  WinDoesIt.cpp, that is (now) part of Frame4J.<br />
 *  <br />
 *  <b>Important:</b><br />
 *  As the native Windows implementations differ for Win32 and Win64 be sure
 *  to pick the right WinDoesIt.dll to put into %JAVA_HOME%\jre\bin\.
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2007 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 44 $ ($Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $)
 */
 // so far   V00.00 (19.12.2006) : new
 //          V.022+ (23.08.2013) : began porting (compatible) to 64 bit

public class WinDoesIt {

   static { System.loadLibrary("bsDoesItNative"); }

/** No Objects, no javaDoc. <br /> */
   private WinDoesIt(){}

//----    operating system Windows 32  ---------------------------------------

/** Invalid value for the system's I/O handle. <br />
 *  <br />
 *  Hint WinBase.h: <br />
 *   &nbsp; #define INVALID_HANDLE_VALUE (HANDLE)(-1)<br />
 *  respectively for 64 bit: <br />
 *   &nbsp; #define INVALID_HANDLE_VALUE ((HANDLE)(LONG_PTR)-1) <br />
 *  <br />
 *  Value: {@value}
 */
   public static final long  INVALID_HANDLE_VALUE = -1;


/** Open a serial port. <br />
 *  <br />
 *  The valid handle returned in the case of success must be used for all
 *  further operations.<br />
 *  <br />
 *  Failures are signalled by returning the invalid handle
 *  {@link #INVALID_HANDLE_VALUE}.<br />
 *  The cause may be retrieved by {@link #getLastError()} (and by
 *  {@link #errorMessage(int)}):<br />
 *  &quot;error = 5 : access denied&quot; mostly means the interface being in
 *     use already.<br />
 *  &quot;error = 3 : The system cannot finde .....&quot; usually means that
 *     there is no interface of the given name at all.<br />
 *  <br />
 *  Hint: On Windows the interfaces names are usually  &quot;COM1&quot;,
 *  &quot;COM2&quot; and so on.<br />
 *
 *  @param comPort the port's name
 *  @return Handle to the port (it is a Windows file handle);
 *            {@link #INVALID_HANDLE_VALUE} on failure
 */
   public static native long openSerialPort(String comPort);

/** Close the port respectively handle. <br />
 *  <br />
 *  The device, the interface or even a file represented by the valid handle
 *  {@code port} will be closed for all further operations.<br />
 *
 *  @param port a handle
 *  @return true on success
 *  @see #getLastError()
 */
   public static native boolean closePort(long port);


/** Determine the last respectively actual error number. <br />
 *  <br />
 *  If any method returned had met no success (signalled by
 *  {@link #INVALID_HANDLE_VALUE}, false, null or similar) this method fetches
 *  the operating systems error number.<br />
 *  <br />
 *  Hint: The Windows implementation (bsDoesItNative.dll) returns the value
 *  got (already) by using the system call (GetLastError()). If that is 0 this
 *  library's last cached error value for the last failed operation is
 *  returned. <br />
 *  <br />
 *  Hint 2: A second call after a failed operation usually returns 0 (OK) as
 *  does a call after a successful operation.<br />
 *  <br />
 *  Hint 3: Java classes wrapping this class in an OO conforming way will
 *  forward the (non OK) information got here and translated by
 *  {@link #errorMessage(int)} to human readable text usually as an
 *  Exception.<br />
 *
 *  @return operating system specific error number
 *  @see #errorMessage(int)
 */
   public static native int getLastError();


/** Get the report String from the error number. <br />
 *  <br />
 *  For a valid (known) error number the system specific text report will be
 *  fetched.<br />
 *  <br />
 *  Hint: At least on Windows the text to number correlation is strictly
 *  static. The text provided will be (nationalised) mostly in the
 *  installation language of that Windows. That does not have to be the
 *  language (see {@link AppLangMap} etc.) set for the Java
 *  {@link App application}. Hence, the number to text translation may be
 *  cached and done self for the language expected.<br />
 *  <br />
 *  <br />
 *  @param  errNo error number; see {@link #getLastError()}
 *  @return operating system provided error text
 */
   public static native String errorMessage(int errNo);

/* Windows: winbase.h:
 *
#define NOPARITY            0   (0)
#define ODDPARITY           1   (1)
#define EVENPARITY          2   (2)
#define MARKPARITY          3   (3)
#define SPACEPARITY         4   (4)
                                (SerialDefs = javax.comm = rxtx)
#define ONESTOPBIT          0   (1)
#define ONE5STOPBITS        1   (3)
#define TWOSTOPBITS         2   (2)
 *
 */

/** Constant for stop bits. <br />
 *  <br />
 *  Hint: The Windows (winBase.h) value and not the one according to
 *  javax.comm, RXTX and hence {@link SerialDefs}.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #setSerialParams(long, int, int, int, int, int)
 *  @see #ONE5STOPBITS
 *  @see #TWOSTOPBITS
 */
   public final static int ONESTOPBIT = 0;

/** Constant for StoppBits. <br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #ONE5STOPBITS
 *  @see #ONESTOPBIT
 */
   public final static int TWOSTOPBITS = 2;

/** Constant for StoppBits. <br />
 *  <br />
 *  Hint: The values used here are those of Windows (winBase.h) and not those
 *  defined by javax.comm, RXTX and hence {@link SerialDefs}.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #ONESTOPBIT
 */
   public final static int ONE5STOPBITS = 1;

/** Do the serial port's basic settings. <br />
 *  <br />
 *  This method sets (almost) all parameters relevant for the port's
 *  communication protocol in one step. The set of parameters is that the
 *  station at the other and of the line must have set the same way to enable
 *  any communication.<br />
 *  <br />
 *  A value -1 and partly illegal values mean no change of the setting in
 *  each case.<br />
 *  <br />
 *  For those settings, when 0 is illegal, 0 means the default value.<br />
  *  <br />
 *  @param port     handle
 *  @param baud     baud rate
 *  @param dataBits 5..8
 *  @param stopBits fitting constant value
 *  @param parity   see {@link SerialDefs}
 *  @param flowControl (see {@link SerialDefs})
 *  @return true on success
 *  @see #ONESTOPBIT
 *  @see #ONE5STOPBITS
 *  @see #TWOSTOPBITS
 *  @see #getSerialParams(long)
 *  @see #setSerialTimeouts(long, int, int, int, int, int)
 *  @see #getSerialTimeouts(long)
 */
   public static native boolean setSerialParams(long port,
          int baud, int dataBits, int stopBits, int parity, int flowControl);


/** Get the serial port's actual settings. <br />
 *  <br />
 *  @param port     handle
 *  @return         int[] of length 5; for the entries see
 *                  {@link #setSerialParams(long, int, int, int, int, int)});
 *                  null on failure
 *  @see #setSerialParams(long, int, int, int, int, int)
 */
   public static native int[] getSerialParams(long port);

/*  DCB (Windows) Info:
   typedef struct _DCB {
   DWORD DCBlength;
   DWORD BaudRate;
   DWORD fBinary:1;
   DWORD fParity:1;
   DWORD fOutxCtsFlow:1;
   DWORD fOutxDsrFlow:1;
   DWORD fDtrControl:2;
   DWORD fDsrSensitivity:1;
   DWORD fTXContinueOnXoff:1;
   DWORD fOutX:1;
   DWORD fInX:1;
   DWORD fErrorChar:1;
   DWORD fNull:1;
   DWORD fRtsControl:2;
   DWORD fAbortOnError:1;
   DWORD fDummy2:17;
   WORD wReserved;
   WORD XonLim;
   WORD XoffLim;
   BYTE ByteSize;
   BYTE Parity;
   BYTE StopBits;
   char XonChar;
   char XoffChar;
   char ErrorChar;
   char EofChar;
   char EvtChar;
   WORD wReserved1;
} DCB,*LPDCB;

   dcb.fOutxCtsFlow = FALSE;
   dcb.fOutxDsrFlow = FALSE;
   dcb.fDtrControl = DTR_CONTROL_DISABLE;
   dcb.fDsrSensitivity = FALSE;
   dcb.fTXContinueOnXoff = FALSE;
   dcb.fOutX = FALSE;
   dcb.fInX = FALSE;
   dcb.fRtsControl = RTS_CONTROL_DISABLE;
   dcb.XonLim = 0;
   dcb.XoffLim = 0;

   dcb.ByteSize = 8;
   dcb.BaudRate = baud;
   dcb.ByteSize = (unsigned char)dataBits;
   dcb.Parity = (unsigned char)parity;
   dcb.StopBits = (unsigned char)stopBits;

   fSuccess = SetCommState(hCom, &dcb);
 */

/** Set the serial port's timeouts. <br />
 *  <br />
 *  readIntervalTimeout:<br />
 *  Maximum time in milliseconds before the arrival of a second and all
 *  following bytes.<br />
 *  A value -1 (interpreted by the driver as a very big unsigned value)
 *  combined with two times 0 for the next two values will provide immediate
 *  return with buffered input valued, even that may mean none at all.<br />
 *  <br />
 *  readTotalTimeoutMultiplier:<br />
 *  Factor in milliseconds for the whole read operations timeout. This value
 *  will be multiplied by the maximum number of bytes expected (length of
 *  input buffer provided).<br />
 *  <br />
 *  readTotalTimeoutConstant:<br />
 *  This value will be added to the product just described. If both are 0
 *  that means no timeout for read operations.<br />
 *  <br />
 *  writeTotalTimeoutMultiplier:<br />
 *  Factor in milliseconds for the whole write operations timeout. This value
 *  will be multiplied by the number of bytes to be written (length of
 *  output buffer / array provided)..<br />
 *  <br />
 *  writeTotalTimeoutConstant:<br />
 *  This value will be added to the product just described. If both are 0
 *  that means no timeout for write operations.<br />
 *  <br />
 *  Remark:<br />
 *  If readIntervalTimeout and readTotalTimeoutMultiplier are -1 (meaning
 *  unsigned maximum) and readTotalTimeoutConstant is &gt; 0 and &lt;
 *  (unsigned) maximum, the following applies:<br />
 *  If there is at least one byte (or more) in a system (driver) or hardware
 *  input buffer, the read operations will return immediately fetching values
 *  from that buffer.<br />
 *  If the buffer is empty, the read operations will wait for the first byte
 *  input and fetch it.<br />
 *  If no input byte arrives within readTotalTimeoutConstant a timeout
 *  occurs.<br />
 *  <br />
 *  @param readIntervalTimeout        between the bytes (see above)
 *  @param readTotalTimeoutMultiplier * number of bytes (see above)
 *  @param readTotalTimeoutConstant   + (see above)
 *  @param writeTotalTimeoutMultiplier  * number of bytes (see above)
 *  @param writeTotalTimeoutConstant    + (see above)
 *  @return true bei Erfolg
 */
   public static native boolean setSerialTimeouts(long port,
                           int readIntervalTimeout,
                           int readTotalTimeoutMultiplier,
                           int readTotalTimeoutConstant,
                           int writeTotalTimeoutMultiplier,
                           int writeTotalTimeoutConstant);

/* Implementation info (Windows)
   typedef struct _COMMTIMEOUTS {
   DWORD ReadIntervalTimeout;
   DWORD ReadTotalTimeoutMultiplier;
   DWORD ReadTotalTimeoutConstant;
   DWORD WriteTotalTimeoutMultiplier;
   DWORD WriteTotalTimeoutConstant;} COMMTIMEOUTS, *LPCOMMTIMEOUTS;
 */

/** Get the actual timeouts for the serial port. <br />
 *  <br />
 *  @param port     handle
 *  @return         int[] of length 5 containing the timeout values as seen by
 *  {@link #setSerialTimeouts(long, int, int, int, int, int)}) parameters;<br />
 *                  null on failure
 */
   public static native int[] getSerialTimeouts(long port);

/** Constant for purge method. <br />
 *  <br />
 *  Terminates all outstanding overlapped read operations and returns
 *  immediately, even if the read operations have not been completed.<br />
 *  <br />
 *  Hint: This class' access methods respectively their native
 *  implementation "bsDoesItNative.dll for Windows all are &quot;non
 *  overlapped&quot;.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #purgeSerialPort(long, int)
 *  @see #PURGE_TXABORT
 */
   public final static int PURGE_RXABORT = 0x0002;

/** Constant for purge method. <br />
 *  <br />
 *  Clears the input buffer (if the device driver has one).<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #purgeSerialPort(long, int)
 *  @see #PURGE_RXABORT
 */
   public final static int PURGE_RXCLEAR = 0x0008;

/** Constant for purge method. <br />
 *  <br />
 *  Terminates all outstanding overlapped write operations and returns
 *  immediately, even if the write operations have not been completed.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #purgeSerialPort(long, int)
 *  @see #PURGE_RXABORT
 *  @see #PURGE_RXOR
 */
  public final static int PURGE_TXABORT = 0x0001;

/** Constant for purge method. <br />
 *  <br />
 *  OR of the receive operations.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #purgeSerialPort(long, int)
 *  @see #PURGE_RXABORT
 */
  public final static int PURGE_RXOR = 0x000A;

/** Constant for purge method. <br />
 *  <br />
 *  Clears the output buffer (if the device driver has one).<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #purgeSerialPort(long, int)
 *  @see #PURGE_RXABORT
 */
   public final static int PURGE_TXCLEAR = 0x0004;

/** Constant for purge method. <br />
 *  <br />
 *  OR of all PURGE constants.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #purgeSerialPort(long, int)
 *  @see #PURGE_RXABORT
 *  @see #PURGE_RXCLEAR
 *  @see #PURGE_TXABORT
 *  @see #PURGE_TXCLEAR
 */
   public final static int PURGE_OR = 0x000F;

/** Clear / abort all outstanding operations. <br />
 *  <br />
 *  It is quite usual to call this method immediately after opening with the
 *  OR of all PURGE constants.<br />
 *  <br />
 *  @param port handle
 *  @param ops  Ein oder mehrere (Oder) PURGE-Konstanten
 *  @return true bei Erfolg
 *  @see #PURGE_RXABORT
 *  @see #PURGE_RXCLEAR
 *  @see #PURGE_TXABORT
 *  @see #PURGE_TXCLEAR
 *  @see #PURGE_OR
 */
   public static native boolean purgeSerialPort(long port, int ops);

/** Constant for modem state. <br />
 *  <br />
 *  The CTS (clear-to-send) signal is on.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #getSerialModemStatus(long)
 *  @see #MS_DSR_ON
 */
   public final static int MS_CTS_ON = 0x0010;

/** Constant for modem state. <br />
 *  <br />
 *  The DSR (data-set-ready) signal is on.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #getSerialModemStatus(long)
 *  @see #MS_RING_ON
 */
   public final static int MS_DSR_ON = 0x0020;

/** Constant for modem state. <br />
 *  <br />
 *  The ring indicator signal is on.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #getSerialModemStatus(long)
 *  @see #MS_RLSD_ON
 */
   public final static int MS_RING_ON = 0x0040;

/** Constant for modem state. <br />
 *  <br />
 *  The RLSD (receive-line-signal-detect; vulgo carrier detect ??)
 *  signal is on.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #getSerialModemStatus(long)
 *  @see #MS_CTS_ON
 */
   public final static int MS_RLSD_ON = 0x0080;


/** Read the modem state. <br />
 *  <br />
 *  @param port the Windows handle for the port
 *  @return -1 on failure, otherwise a combination of  modem state constants
 *  @see #MS_CTS_ON
 *  @see #MS_DSR_ON
 *  @see #MS_RING_ON
 *  @see #MS_RLSD_ON
 */
   public static native int getSerialModemStatus(long port);

/** Write some bytes. <br />
 *  <br />
 *  The specified number of bytes (len of the array data) will be written to
 *  the port. The serial interface's timing / time out behaviour will be
 *  determined by the method's
 *  {@link #setSerialTimeouts(long, int, int, int, int, int)
 *  setSerialTimeouts()} two relevant parameters.<br />
 *  <br />
 *  Hint: This method works on Windows for any valid handle and hence device
 *  or file.<br />
 *  <br />
 *  @param port handle
 *  @param data buffer
 *  @param off  index of first byte to be output (0 or larger)
 *  @param len  number of bytes  (has to be &lt;= data's length!)
 *  @return number of bytes written or -1 on failure
 */
   public static native int writeSerial(long port, byte[] data,
                                                           int off, int len);

/** Write just one byte. <br />
 *  <br />
 *  This method is to be preferred over
 *  {@link #writeSerial(long, byte[], int, int)} if and only if just one byte
 *  is to be output.<br />
 *  <br />
 *  Hint: This method works on Windows for any valid handle and hence device
 *  or file.<br />
 *  <br />
 *  @param port handle
 *  @param data the byte
 *  @return number of bytes written (i.e. 0 or 1); -1 on failure
 */
   public static native int putSerial(long port, byte data);

/** Empty the (system's) output buffer. <br />
 *  <br />
 *  The output buffers of the device represented by the handle {@code port}
 *  will be flushed.<br />
 *  <br />
 *  Hint: There are no useful informations on necessity and return behaviour
 *  for serial interfaces from platform specification. It should be not to
 *  naive, to infer that bytes fed to a USART or its driver will flow out
 *  by itself as fast as hardware and receiving station permit.<br />
 *  This method called for serial devices may probably make no sense.<br />
 *  <br />
 *  @return true on success
 */
   public static native boolean flushSerial(long port);

/** Read some bytes from the handle (port). <br />
 *  <br />
 *  This method reads a number of bytes from the represented device.<br />
 *  <br />
 *  The serial interface's timing / time out behaviour will be
 *  determined by the method's
 *  {@link #setSerialTimeouts(long, int, int, int, int, int)
 *  setSerialTimeouts()} three relevant parameters.<br />
 *  <br />
 *  Hint: This method works on Windows for any valid handle and hence device
 *  or file.<br />
 *  <br />
 *  @param port handle
 *  @param data the input buffer provided
 *  @param off  index of where the first byte read is to be put (0 or larger)
 *  @param len  number of bytes  (has to be &lt;= data's length!)
 *  @return number of bytes read or -1 on failure
 *  @see #writeSerial(long, byte[], int, int)
 */
   public static native int readSerial(long port, byte[] data,
                                                         int off, int len);

/**Read just one byte. <br />
 *  <br />
 *  This method is to be preferred over
 *  {@link #readSerial(long, byte[], int, int)} if and only if just one byte
 *  is to be read.<br />
 *  <br />
 *  Hint: This method works on Windows for any valid handle and hence device
 *  or file.<br />
 *  <br />
 *  @param  port handle
 *  @return the read byte's value (0..255) or -1 on failure or timeout
 */
   public static native int getSerial(long port);

//--------------------------------------------------------------------------


/** Set the modem control outputs DTR and RTS. <br />
 *  <br />
 *  In time critical cases calling this method should be preferred over the
 *  single settings by {@link #escapeComm(long, int)}.<br />
 *  <br />
 *  For example, time critical cases are those, when only one of the control
 *  signals really is used for control, while together the other one it
 *  powers the external hardware (via a bridge rectifier). There are HART
 *  modems working just that way.<br />
 *  <br />
 *  Hint: This is the (one and) only method that under Windows (i.e.
 *  bsDoesItNative.dll) does not procure exactly one fitting system call. (It
 *  procures two).<br />
 *  <br />
 *  @see #escapeComm(long, int)
 *  @see #SETDTR
 *  @see #CLRRTS
 */
   public static native boolean setDtrRts(long handle,
                                                   boolean dtr, boolean rts);

/** USART control. <br />
 *  <br />
 *  @param handle the Windows handle for the port
 *  @param op Eine der SET.. respectively CLR...-Konstanten
 *  @return true bei Erfolg
 *  @see #CLRDTR
 *  @see #CLRBREAK
 *  @see #CLRRTS
 *  @see #SETBREAK
 *  @see #SETDTR
 *  @see #SETRTS
 *  @see #SETXOFF
 *  @see #SETXON
 */
   public static native boolean escapeComm(long handle, int op);

/** Constant for escape method. <br />
 *  <br />
 *  Restores character transmission and places the transmission line into
 *  a non-break state.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #escapeComm(long, int)
 *  @see #CLRDTR
 */
   public final static int CLRBREAK = 9;

/** Constant for escape method. <br />
 *  <br />
 *  Clears DTR (data-terminal-ready).<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #escapeComm(long, int)
 *  @see #CLRBREAK
 */
   public final static int CLRDTR =  6; //  Clears DTR

/** Constant for escape method. <br />
 *  <br />
 *  Clears RTS (request-to-send).<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #escapeComm(long, int)
 *  @see #CLRBREAK
 */
   public final static int CLRRTS =  4; //  Clears the RTS

/** Constant for escape method. <br />
 *  <br />
 *  Suspends character transmission and places the transmission line in
 *  a break state.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #escapeComm(long, int)
 *  @see #CLRBREAK
 */
   public final static int SETBREAK = 8; // Suspends

/** Constant for escape method. <br />
 *  <br />
 *   Sets DTR (data-terminal-ready).<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #escapeComm(long, int)
 *  @see #CLRBREAK
 */
   public final static int SETDTR = 5;  // Sets DTR (data-terminal-ready)

/** Constant for escape method. <br />
 *  <br />
 *  Sets RTS (request-to-send).<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #escapeComm(long, int)
 *  @see #CLRBREAK
 */
   public final static int SETRTS = 3; // Sets RTS (request-to-send)

/** Constant for escape method. <br />
 *  <br />
 *  Causes transmission to act as
 *  if an XOFF character has been received.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #escapeComm(long, int)
 *  @see #CLRBREAK
 */
   public final static int SETXOFF = 1;

/** Constant for escape method. <br />
 *  <br />
 *  Causes transmission to act as
 *  if an XOFN character has been received.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #escapeComm(long, int)
 *  @see #CLRBREAK
 */
   public final static int SETXON = 2;

//-- implementation info on Windows for later use   -------------------------
/*    WinBase.h:
   #define DTR_CONTROL_DISABLE 0
   #define DTR_CONTROL_ENABLE 1
   #define DTR_CONTROL_HANDSHAKE 2

   #define RTS_CONTROL_DISABLE 0
   #define RTS_CONTROL_ENABLE 1
   #define RTS_CONTROL_HANDSHAKE 2
   #define RTS_CONTROL_TOGGLE 3

   Modemstatus masks
   #define MS_CTS_ON 16
   #define MS_DSR_ON 32
   #define MS_RING_ON 64
   #define MS_RLSD_ON 128

   USART-Error-Masks
   #define CE_DNS 2048
   #define CE_FRAME  8
   #define CE_IOE 1024
   #define CE_MODE   32768
   #define CE_OOP 4096
   #define CE_OVERRUN   2
   #define CE_PTO 512
   #define CE_RXOVER 1
   #define CE_RXPARITY  4
   #define CE_TXFULL 256


   /// An input buffer overflow has occurred.
   /// There is either no room in the input buffer,
   /// or a character was received after the EOF character.
   internal const UInt32 CE_RXOVER = 0x0001;

    /// A character-buffer overrun has occurred.
    /// The next character is lost.
       internal const UInt32 CE_OVERRUN = 0x0002;

    /// The hardware detected a parity error.
    internal const UInt32 CE_RXPARITY = 0x0004;

    /// The hardware detected a framing error.
    internal const UInt32 CE_FRAME = 0x0008;

    /// The hardware detected a break condition
    internal const UInt32 CE_BREAK = 0x0010;

    /// The application tried to transmit a
    /// character, but the output buffer was full.
    internal const UInt32 CE_TXFULL = 0x0100;

    /// Windows 95/98/Me: A time-out occurred on a parallel device.
    internal const UInt32 CE_PTO = 0x0200;

    /// An I/O error occurred during communications with the device.
    internal const UInt32 CE_IOE = 0x0400;

    /// Windows 95/98/Me: A parallel device is not selected.
    internal const UInt32 CE_DNS = 0x0800;

    /// Windows 95/98/Me: A parallel device signalled that it is out of paper.
    internal const UInt32 CE_OOP = 0x1000;

    /// The requested mode is not supported, or the file handle
    /// parameter is invalid. If this value is specified, it
    is the only valid error.
    internal const UInt32 CE_MODE = 0x8000;

   this.error.overflowError = ((this.error.status & CE_RXOVER)!=0);
   this.error.overrunError = ((this.error.status & CE_OVERRUN)!=0);
   this.error.parityError = ((this.error.status & CE_RXPARITY)!=0);
   this.error.framingError = ((this.error.status & CE_FRAME)!=0);
   this.error.breakCondition = ((this.error.status & CE_BREAK)!=0);
   this.error.txBufFullError = ((this.error.status & CE_TXFULL)!=0);
   this.error.parallelTmoutError = ((this.error.status & CE_PTO)!=0);
   this.error.deviceIOError = ((this.error.status & CE_IOE)!=0);
   this.error.notSelectedError = ((this.error.status & CE_DNS)!=0);
   this.error.outOfPaperError = ((this.error.status & CE_OOP)!=0);
   this.error.modeHandleError = ((this.error.status & CE_MODE)!=0);

----

   EVENT Masken
   #define EV_BREAK 64
   #define EV_CTS 8
   #define EV_DSR 16
   #define EV_ERR 128
   #define EV_EVENT1 2048
   #define EV_EVENT2 4096
   #define EV_PERR 512
   #define EV_RING 256
   #define EV_RLSD 32
   #define EV_RX80FULL 1024
   #define EV_RXCHAR 1
   #define EV_RXFLAG 2
   #define EV_TXEMPTY 4

   Event Flag  Description
   EV_BREAK  A break was detected on input.
   EV_CTS    The CTS (clear-to-send) signal changed state. To get the actual
             state of the CTS line, GetCommModemStatus should be called.
   EV_DSR    The DSR (data-set-ready) signal changed state. To get the actual
             state of the DSR line, GetCommModemStatus should be called.
   EV_ERR    A line-status error occurred. Line-status errors are CE_FRAME,
             CE_OVERRUN, and CE_RXPARITY. To find the cause of the error,
             ClearCommError should be called.
   EV_RING   A ring indicator was detected.
   EV_RLSD   The RLSD (receive-line-signal-detect) signal changed state.
             To get the actual state of the RLSD line, GetCommModemStatus
             should be called. Note that this is commonly referred to as the
             CD (carrier detect) line.
   EV_RXCHAR   A new character was received and placed in the input buffer.
               See the "Caveat" section below for a discussion of this flag.
   EV_RXFLAG   The event character was received and placed in the input
               buffer. The event character is specified in the EvtChar
               member of the DCB structure discussed later.
   EV_TXEMPTY  The last character in the output buffer was sent to the serial
               port device. If a hardware buffer is used, this flag only
               indicates that all data has been sent to the hardware. There
               is no way to detect when the hardware buffer is empty without
               talking directly to the hardware with a device driver.


   #define INVALID_HANDLE_VALUE (HANDLE)(-1)

   winnt.h:

   #define GENERIC_READ    0x80000000
   #define GENERIC_WRITE   0x40000000
   #define GENERIC_EXECUTE 0x20000000

   #define CREATE_NEW   1
   #define CREATE_ALWAYS   2
   #define OPEN_EXISTING   3
   #define OPEN_ALWAYS  4
   #define TRUNCATE_EXISTING  5
   #define FILE_FLAG_WRITE_THROUGH  0x80000000
   #define FILE_FLAG_OVERLAPPED  1073741824
   #define FILE_FLAG_NO_BUFFERING   536870912
*/

}  // WinDoesIt (03.01.2006, 26.08.2013)
