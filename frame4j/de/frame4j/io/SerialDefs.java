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

import java.io.IOException;
import java.lang.reflect.Constructor;

import static de.frame4j.util.ComVar.*;

import de.frame4j.util.ComVar;
import de.frame4j.text.TextHelper;

/** <b>Definitions for the serial input and output</b> (comm interfaces).<br />
 *  <br />
 *  This interface describes the usual constants and and methods for the
 *  handling of serial computer interfaces (V.24, RS232, RS485, USB2serial;
 *  Com1, SerialA, TTY0 and so on). In this form they do appear in the
 *  so called COMM-extensions; i.e. the packages<ul>
 *    <li> javax.comm (late Java comm-extensions by SUN),</li>
 *    <li> gnu.io (RXTX, open source)</li>
 *    <li> and others.</li></ul>
 *  <br />
 *  The variety of those implementations of a quite simple hardware interface
 *  / protocol standard is &mdash; in the end &mdash; SUN's/Oracle's fault,
 *  namely by the deficiencies of SUN's javax.comm, it's missing embedding in
 *  standard JDKs and JREs and, since 2002, the dropping of Windows support.
 *  The main architectural (OO)  sin is the missing of a common interface
 *  definition like this one ({@link SerialDefs}) that would (at least) have
 *  made the rank growth of implementations interchangeable.<br />
 *  <br />
 *  Remark: SUN's just dropping the Windows support for serial links left 
 *  some people doing process control with Java (and making propaganda for
 *  doing so) high and dry. Many process control devices in use have those
 *  serial interfaces that are used (if not even for on-line process control)
 *  for maintenance, commissioning, calibration and so on. And there are a 
 *  variety of Java tools for those purposes. And, of course, they have to
 *  run on the industry standard platform, like WinCC etc., for those cases.
 *  The widespread USB2serial bridges mimic normal serial links on both sides,
 *  giving a new COMxy with Windows.<br />
 *  In that light SUN's 2002 decision to support the serial communication for
 *  just Solaris and some Linux variants is incomprehensible. The other way
 *  round (also wrong) would have made some sense.<br />    
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2006, 2007, 2009, 2013 &nbsp; Albrecht Weinert<br /> 
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 56 $ ($Date: 2021-06-28 12:11:29 +0200 (Mo, 28 Jun 2021) $)
 */
 // so far    V00.00 (21.12.2006) : extracted out of SerIO
 //           V.134+ (02.11.2015) : method made static
 //           V.  42 (05.05.2021) : parityAsString() etc. default implemented

public interface SerialDefs {
   
/** The used interface's name. <br />
 *  <br />
 *  It will be platform dependent: something like &quot;COM1&quot;, 
 *  &quot;COM2&quot;, &quot;serialA&quot; etc.<br />
 *  @return the port's name
 */
   public abstract String getPortName();

/** Setting: 5 data bits. <br />
 *  <br />
 *  Hint: Five data bits are supported by most implementations as serial 
 *  interface's standard.<br />
 *  The old Intel IC 8251 USART (universal synchronous and asynchronous 
 *  receiver and transmitter), of course, can do it, but modern PCs and chip
 *  sets may have dropped this mode, like RXTX did.<br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #DATABITS_8
 *  @see #getDataBits()
 *  @see #setSerialPortParams(int, int, int, int)
 */
   static public final int DATABITS_5 = 5;

/** Setting: 6 data bits. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #DATABITS_5
 */
   static public final int DATABITS_6 = 6;

/** Setting: 7 data bits. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #DATABITS_5
 */
   static public final int DATABITS_7 = 7;

/** Setting: 8 data bits. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #DATABITS_5
 */
   static public final int DATABITS_8 = 8;

/** Setting: 1 stop bit. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #STOPBITS_2
 *  @see #getStopBits()
 *  @see #setSerialPortParams(int, int, int, int)
 */
   static public final int STOPBITS_1 = 1;

/** Setting: 2 stop bits. <br />
 *  <br />
 *  Using two stop bits in a serial link's hardware/software usually affects
 *  the transmitter only. The receiver would normally still accept one without
 *  reporting an error. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #STOPBITS_1
 *  @see #getStopBits()
 *  @see #setSerialPortParams(int, int, int, int)
 */
   static public final int STOPBITS_2 = 2;

/** Setting: 1.5 stop bits. <br />
 *  <br />
 *  Many USARTS (universal synchronous and asynchronous receiver and 
 *  transmitter) or chip sets do allow 1.5 stop bits only in combination with
 *  5 data bits ({@link #DATABITS_5}).<br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #DATABITS_5
 *  @see #STOPBITS_2
 *  @see #getStopBits()
 *  @see #setSerialPortParams(int, int, int, int)
 */
   static public final int STOPBITS_1_5 = 3;

/** Setting: no parity. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #PARITY_EVEN
 *  @see #parityAsString()
 *  @see #getParity()
 *  @see #setSerialPortParams(int, int, int, int)
 */
   static public final int PARITY_NONE = 0;

/** Setting: even parity. <br />
 *  <br />
 *  One parity bit will be appended in a way that the number of one's is
 *  even.<br /><br />
 *  Value: <code>{@value}</code>
 *  @see #PARITY_ODD
 *  @see #parityAsString()
 *  @see #getParity()
 *  @see #setSerialPortParams(int, int, int, int)
 */
   static public final int PARITY_EVEN = 2;

/** Setting: odd parity. <br />
 *  <br />
 *  One parity bit will be appended in a way that the number of one's is
 *  odd.<br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #PARITY_EVEN
 *  @see #parityAsString()
 *  @see #getParity()
 *  @see #setSerialPortParams(int, int, int, int)
 */
   static public final int PARITY_ODD = 1;

/** Setting: parity mark. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #PARITY_EVEN
 *  @see #parityAsString()
 *  @see #getParity()
 *  @see #setSerialPortParams(int, int, int, int)
 */
   static public final int PARITY_MARK = 3;

/** Setting: parity space. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #PARITY_EVEN
 *  @see #parityAsString()
 *  @see #getParity()
 *  @see #setSerialPortParams(int, int, int, int)
 */
   static public final int PARITY_SPACE = 4;
   
/* For developers only: from winbase.h :
 #define NOPARITY  0
 #define ODDPARITY 1
 #define EVENPARITY   2
 #define MARKPARITY   3
 #define SPACEPARITY  4
 #define ONESTOPBIT   0
 #define ONE5STOPBITS 1
 #define TWOSTOPBITS  2
 */   

/** Setting: data flow control: none. <br />
 *  <br />
 *  Value: <code>{@value}</code><br />
 *
 *  @see #FLOWCONTROL_RTSCTS
 *  @see #getFlowControlMode()
 *  @see #setSerialPortParams(int, int, int, int)
 */
   static public final int FLOWCONTROL_NONE = 0;

/** Setting: data flow control: hardware on input. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #FLOWCONTROL_NONE
 *  @see #getFlowControlMode()
 */
   static public final int FLOWCONTROL_RTSCTS_IN = 1;

/** Setting: data flow control: hardware on output. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #FLOWCONTROL_NONE
 *  @see #getFlowControlMode()
 */
   static public final int FLOWCONTROL_RTSCTS_OUT = 2;

/** Setting: data flow control: hardware both directions. <br />
 *  <br />
 *  Value: <code>{@value}</code> ({@link #FLOWCONTROL_RTSCTS_OUT} | 
 *                    {@link #FLOWCONTROL_RTSCTS_IN})<br />
 *  @see #FLOWCONTROL_NONE
 *  @see #getFlowControlMode()
 */
   static public final int FLOWCONTROL_RTSCTS = FLOWCONTROL_RTSCTS_OUT
                           | FLOWCONTROL_RTSCTS_IN;

/** Setting: data flow control: software on input. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #FLOWCONTROL_NONE
 *  @see #getFlowControlMode()
 */
   static public final int FLOWCONTROL_XONXOFF_IN = 4;

/** Setting: data flow control: software on output. <br />
 *  <br />
 *  Value: <code>{@value}</code><br />
 
 *  @see #FLOWCONTROL_NONE
 *  @see #getFlowControlMode()
 */
   static public final int FLOWCONTROL_XONXOFF_OUT = 8;

/** Setting: data flow control: software both directions. <br />
 *  <br />
 *  Value: <code>{@value}</code> ({@link #FLOWCONTROL_XONXOFF_OUT} | 
 *                    {@link #FLOWCONTROL_XONXOFF_IN})<br />
 */
   static public final int FLOWCONTROL_XONXOFF = FLOWCONTROL_XONXOFF_OUT
                           | FLOWCONTROL_XONXOFF_IN;

/** Event type: Overrun error. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #FLOWCONTROL_NONE
 *  @see #getFlowControlMode()
 */
   static public final int OE = 7;

/** Event type: Parity error. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final int PE = 8;

/** Event type: Framing error. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final int FE = 9;

/** Event type: Break / Interrupt. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final int BI = 10;

/** Event type: Carrier detect. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final int CD = 6;

/** Event type: Ring Indicator. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final int RI = 5;

/** Event type: Data set ready. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final int DSR = 4;

/** Event type: Clear to send. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final int CTS = 3;

/** Event type: Data available. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final int DATA_AVAILABLE = 1;

/** Event type:  Output buffer empty. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final int OUTPUT_BUFFER_EMPTY = 2;

/** The serial ports's receive timeout in ms. <br />
 *  <br />
 *  On receive timeout's ({@link #getRcvTimeout() rcvTimeout}) values
 *  &gt; 0 reading methods return after that time in ms (milliseconds)
 *  independent of having received any bytes.<br />
 *
 *  @return the current receive timeout in ms
 *  @see #setRcvTimeout(int)
 */
   public abstract int getRcvTimeout();

/** Set the serial port's receive timeout.  <br />
 *  <br />
 *  If the driver rejects this function the internal value stays at 0.<br />
 *  <br />
 *  @param rcvTimeout  the receive timeout in ms to be set
 *  @see #getRcvTimeout()
 */
   public abstract void setRcvTimeout(int rcvTimeout);
  

/** The serial port's baud rate. <br />
 *
 *  @return the port's actual baud rate
 *  @see Helper#baudRate(int)
 *  @see #setSerialPortParams(int, int, int, int)
 */
   public abstract int getBaud();

/** The serial port's data bits, size of one information piece. <br />
 *  <br />
 *  @return the port's actual item width in bits
 *  @see #setSerialPortParams(int, int, int, int)
 */
   public abstract int getDataBits();

/** The serial port's stop bits. <br />
 *  <br />
 *  default: 1 (){@link #STOPBITS_1}<br />
 *  <br />
 *  @return the actual number of stop bits
 *  @see #setSerialPortParams(int, int, int, int)
 *  @see #STOPBITS_1
 *  @see #STOPBITS_1_5
 *  @see #STOPBITS_2
 */
   public abstract int getStopBits();
   
/** The serial port's parity. <br />
 *  <br />
 *  @return the port's actual parity setting
 *  @see #setSerialPortParams(int, int, int, int)
 *  @see #parityAsString()
 *  @see #PARITY_NONE
 *  @see #PARITY_EVEN
 */
   public abstract int getParity();

/** The serial port's parity as String. <br />
 *  <br />
 *  Returned will be either mark, space, odd, even or none geliefert.<br />
 *  Applications might be satisfied by this default implementation using
 *  {@link Helper}.{@link Helper#parityAsString(int) parityAsString()}.<br /> 
 *
 *  @return the port's actual parity setting
 *  @see #setSerialPortParams(int, int, int, int)
 *  @see #getParity()
 */
   public default String parityAsString(){
     return Helper.parityAsString(getParity()); 
   } // parityAsString()
   
/** Set all relevant parameters of the serial port.  <br />
 *  <br />
 *  This method sets (almost) all parameters relevant for the port's
 *  communication protocol in one step. The set of parameters is that the 
 *  station at the other and of the line must have set the same way to enable
 *  any communication.<br />
 *  <br />
 *  A value -1 means no change of the setting in each case.<br />
 *  <br />
 *  baud rate:<br />
 *  Sensible values are only the usual rates, i.e. 9600 (usual default),
 *  19200, 38400 and so on.<br />
 *  <br />
 *  Implementations should evaluate the values 0..12 as index to the usual 
 *  baud rates:<br />
 *  &nbsp; 110, 150, 300, 600, 1200, 2400, 4800, 9600, 19200,  [0 .. 8]<br />
 *  &nbsp; 28800, 38400, 57600 and 115200 [9 .. 12].<br />
 *  <br />
 *  parity:<br />
 *  Here only the relevant PARITY_x (x= NONE, ODD,
 *  EVEN, MARK and SPACE), like {@link #PARITY_NONE} (default), make
 *  sense.<br />
 *  <br />
 *  dataBits:<br />
 *  Here only the relevant constant values DATABITS_x 
 *  (x=5..8), like DATABITS_8 (default), make sense. They are the values
 *  5..8.<br />
 *  <br />
 *  stopBits:<br />
 *  Here only the relevant constant values STOPBITS_x 
 *  (x=1, 2 or 1_5), like {@link #STOPBITS_1} (default), make sense.<br />
 *  sinnvoll.<br />
 *  <br />
 *  Hint 1:<br />
 *  Implementations are free (and often recommended), to let all other setters
 *  delegate to this method and to let only this method really talk to the 
 *  underlying hardware / platform.<br />
 *  <br />
 *  Hint 2:<br />
 *  In the case of successfully calling the the hardware / driver / platform,
 *  implementations may re-read the actual setting and signal discrepancies 
 *  (by returning false) as error. This proceeding is strongly recommended as
 *  some drivers silently accept standard baud rates or data widths they can't
 *  handle and replace them (silently) by some default or previous value.<br />
 *  <br /> 
 *  <br />
 *  @param baudrate the baudrate to be set
 *  @param dataBits the data item width to be set
 *  @param stopBits the number of stop bits to beset
 *  @param parity   the parity to be set
 *  @see #getParity()
 *  @see #getBaud()
 *  @see #getDataBits()
 *  @see #getStopBits()
 *  @see #STOPBITS_1
 *  @see #STOPBITS_1_5
 *  @see #STOPBITS_2
 *  @see #PARITY_NONE
 *  @see #PARITY_EVEN
 *  @see #PARITY_ODD
 *  @see #PARITY_MARK
 *  @see #PARITY_SPACE
 *  @return true on successful change or if already set so; false on any
 *          error or rejection. In the case of false, nevertheless, changes 
 *          may have taken place.
 */
   public abstract boolean setSerialPortParams(int baudrate,
                                 int dataBits, int stopBits, int parity);

/** Data flow control. <br />
 *  <br />
 *  The value may be any (bitwise) combination of the following values:<ul>
 *  <li>{@link #FLOWCONTROL_NONE}</li>
 *  <li>{@link #FLOWCONTROL_RTSCTS_IN}</li>
 *  <li>{@link #FLOWCONTROL_RTSCTS_OUT}</li>
 *  <li>{@link #FLOWCONTROL_RTSCTS}</li>
 *  <li>{@link #FLOWCONTROL_XONXOFF_IN}</li>
 *  <li>{@link #FLOWCONTROL_XONXOFF_OUT}</li>
 *  <li>{@link #FLOWCONTROL_XONXOFF}</li>
 *  </ul> 
 *  @return the actual flow control setting
 *  @see #setFlowControlMode(int)
 */
   public abstract int getFlowControlMode();

/** Data flow control as String. <br />
 *  <br />
 *  The {@link #getFlowControlMode() flowControlMode}'s value is returned as
 *  character sequence.<br />
 *  <br />
 *  Applications might be satisfied by this default implementation using
 *  {@link Helper}.{@link Helper#flowControlAsText(StringBuilder, int)
 *  flowControlAsText(null, getFlowControlMode())}.<br />
 *  @return the actual flow control setting
 */
   public default String flowControlAsString(){
     return Helper.flowControlAsText(null, getFlowControlMode()).toString();
   } // flowControlAsString()

/** Set the data flow control mode. <br />
 *  <br />
 *  @param flowControlMode the flow control mode to be set
 *  @see #getFlowControlMode()
 */
   public abstract void setFlowControlMode(int flowControlMode);
   
/** The modem control output's RTS (request to send) state. <br />
 *  <br />
 *  Implementations may return the last value set by 
 *  {@link #setRTS(boolean)} respectively {@link #setDtrRts(boolean, boolean)}
 *  instead of the interface's re-read value. If doing so false will be 
 *  returned prior to any first setting.<br />
 *  @return the state of the RTS signal
 */
   public abstract boolean isRTS();

/** Set the state of modem's control output RTS (request to send). <br />
 *  @param rts the state of the RTS signal
 */
   public abstract void setRTS(boolean rts); 
   
/** The modem control output's DTR (data terminal ready) state. <br />
 *  <br />
 *  Implementations may return the last value set by 
 *  {@link #setDTR(boolean)} respectively {@link #setDtrRts(boolean, boolean)}
 *  instead of the interface's re-read value. If doing so false will be 
 *  returned prior to any first setting.<br />
 *  @return  the state of the DTR signal
 */
   public abstract boolean isDTR(); 

/** Set the state of modem's control output DTR (data terminal ready). <br />
 *  @param dtr  the state of the DTR signal
 */
   public abstract void setDTR(boolean dtr); 

   
/** Set the modem control outputs DTR and RTS. <br />
 *  <br />
 *  In time critical cases calling this method should be preferred over the 
 *  single settings by {@link #setDTR setDTR()} and 
 *  {@link #setRTS setRTS()}.<br />
 *  <br />
 *  For example, time critical cases are those, where only one of the control
 *  signals really is used for control, while in co-operation the other one it
 *  powers the external hardware (via a bridge rectifier). There are HART
 *  modems working just that way.<br />
 *  <br />
 *  @param dtr  the state of the DTR signal
 *  @param rts the state of the RTS signal
 */
   public abstract void setDtrRts(boolean dtr, boolean rts); 
   
/** The modem control input's DSR (data set ready) state. <br /> */
   public abstract boolean isDSR();

/** The modem control input's  CTS (clear to send) state. <br /> */
   public abstract boolean isCTS();

/** state des modem's control input's CD (carrier detect) state. <br /> */
   public abstract boolean isCD();

/** The modem control input's  RI (ring indicator) state. <br /> */
   public abstract boolean isRI();

   
/** Check the serial port's usability. <br />
 *  <br />
 *  @see #close()
 */
   public abstract boolean isReady();

/** Close the serial port. <br />
 *  <br />
 *  Closing a closed port will have no effect at all.<br />
 *  <br />
 *  This method will close the port and hence its input and output, sitting it
 *  into a &quot;not ready&quot; state.<br />
 *  <br />
 *  @see #isReady()
 */
   public abstract void close(); 


/** Open the serial port. <br />
 *  <br />
 *  The named interface will be opened. If this {@link SerialDefs} object 
 *  already represents an open ({@link #isReady() ready}) serial port of the
 *  same {@link #getPortName() name} the call just does nothing.<br />
 *  <br />
 *  If this {@link SerialDefs} object already represents an open 
 *  ({@link #isReady() ready}) serial port of another 
 *  {@link #getPortName() name} it will be tried to {@link #close() close}
 *  that as first step.<br /> 
 *  <br />
 *  Failure will be signalled by Exceptions.<br />
 *  <br />
 *  Hint: On Windows the interfaces names are usually  &quot;COM1&quot;, 
 *  &quot;COM2&quot; and so on.<br />
 *  <br />
 *  @param comPort Portname
 *  @throws IllegalArgumentException wrong parameter values
 *  @throws IOException  file or stream problem
 */   
   public abstract void openSerial(CharSequence comPort) throws 
                                        IllegalArgumentException, IOException;

   
/** Read a character from the serial port. <br />
 *  <br />
 *  This method reads just one character from the serial port. If called 
 *  without {@link #setRcvTimeout rcvTimeout} it will wait / block for 
 *  unlimited time.<br />
 *  <br />
 *  On not ready, errors or timeout -1 will be returned.<br />
 *  <br />
 *  @return The byte read (0..255) or -1
 */
   public abstract int read(); 

/** Read some bytes from the serial port. <br />
 *  <br />
 *  This method reads a number of bytes from the serial port.<br />
 *  <br />
 *  The return behaviour depends from timeout and other settings. Under 
 *  certain circumstances this method will wait for input / block
 *  indefinitely.<br />
 *  <br />
 *  On not ready, errors or timeout -1 will be returned.<br />
 *
 *  @return The number of bytes read or -1
 *  @param b the buffer to read into
 *  @param o the index where to put the first byte read
 *  @param l the maximum number of bytes to be read / put into b
 */
   public abstract int read(byte[] b, int o, int l);
   
/** Write some bytes to the serial port. <br />
 *  <br />
 *  If not ready or if {@code b} is  null nothing happens at all.<br />
 *  <br />
 *  @return -1 on failure or not ready, otherwise the number of bytes written;
 *          that will be = {@code len} on total success
 */
   public abstract int write(byte[] b, int off, int len); 

/** Write some bytes to the serial port. <br />
 *  <br />
 *  If not ready or if {@code b} is null nothing happens at all.<br />
 *  <br />
 *  Implementations shall delegate (in case of b not null} to 
 *  {@link #write(byte[], int, int) write(b, 0, b.length)}.<br />
 *  
 *  @return -1 on failure or not ready, otherwise the number of bytes written;
 *          that will be = b.length on total success
 */
   public abstract int write(byte[] b);

/** Write one byte to the serial port. <br />
 *  <br />
 *  If not ready nothing happens at all.<br />
 *
 *  @return true on success
 */
   public abstract boolean write(int b); 
   

/** The last exception's message. <br />
 *  <br />
 *  If an operation failed by an exception, this method will deliver the
 *  explaining error text.<br />
 *  <br />
 *  The same event shall be &quot;explained&quot; only once. Implementations
 *  shall clear that text after the first delivery.<br />
 *  <br />
 *  Hint: Concurrent (multi-thread) use of serial ports is strongly
 *        discouraged anyway.<br />
 * 
 *  @return explaining error text or null
 */
    public abstract String getExMsg();
    
/** Text representation. <br />
 *  <br />
 *  The interface's state will be given as a (may be multi-line) text.<br />
 *  <br />
 *  If an error is pending according to the method's {@link #getExMsg()}
 *  criteria, its explanation will be part of the status text returned.<br />
 */
    @Override public abstract String toString();
   
   
/** <b>Implementation of (helper) methods for serial ports</b>. <br />
 *  <br />
 *  Copyright and version see enclosing interface {@link SerialDefs}.<br />
 */   
   public static final class Helper {
      
/** No objects, no javaDoc-comment. <br /> */      
      private Helper(){}
      

/** Parity as String. <br />
 *  <br />
 *  Depending of the parameter value's equality with the relevant constant
 *  values &quot;mark&quot;, &quot;space&quot;, &quot;odd&quot;, 
 *  &quot;even&quot; or &quot;none&quot; will be returned.<br />
 *  If the parameter is no legal parity constant (in the sense of 
 *  {@link SerialDefs})  &quot;--&quot; is returned.<br />
 *  <br /> 
 *  @see SerialDefs#parityAsString()
 *  @see SerialDefs#PARITY_EVEN
 */
     public static String parityAsString(int parity){
         switch (parity) {
            case PARITY_SPACE: return "space";
            case PARITY_MARK:  return "mark";
            case PARITY_EVEN:  return "even";
            case PARITY_ODD:   return "odd";
            case PARITY_NONE:  return "none";
         }
         return "--";
      } // parityAsString(int)
     
/** Event type as String. <br />
 *  <br />
 *  Depending of the parameter value's equality with the relevant constants
 *  {@link SerialDefs#PE PE}, {@link SerialDefs#FE FE},
 *  {@link SerialDefs#OE OE} and so on human readable texts like
 *  &quot;parity error&quot;,  &quot;output buffer empty&quot; and so on are
 *  returned. For no match to {@link SerialDefs}'s event type constants
 *  &quot;none&quot; is returned.<br />
 */
      public static String eventAsString(int eventType){
         switch (eventType) {
            case PE :  return "parity error";
            case FE :  return "framing error";
            case OE :  return "overrun error";
            case CTS : return "clear to send";
            case DSR : return "data set ready";
            case CD :  return "carrier detect";
            case RI :  return "ring indicator";
            case BI :  return "break";
            case DATA_AVAILABLE : return "data avail.";
            case OUTPUT_BUFFER_EMPTY : return "output buffer empty";
         }
         return "none";
      } // eventAsString(int)

/** Data flow control mode as String. <br />
 *  <br />
 *  The value (or partly only the lower 4 bits) of the parameter
 *  {@code flowControlMode} will be interpreted according to 
 *  {@link SerialDefs}'s flow control constants and appended as character 
 *  sequence to {@code bastel}.<br />
 *  <br />
 *  @param  bastel StringBuilder to append to; if null {@code bastel} is made
 *                 with an initial capacity of 20
 *  @param flowControlMode see {@link SerialDefs}
 *  @return bastel
 */
      public static StringBuilder flowControlAsText(StringBuilder bastel, 
                                                        int flowControlMode){
         if (bastel == null) bastel = new StringBuilder(20);
         if (flowControlMode <= FLOWCONTROL_NONE) {
            return bastel.append("none");
         }
         if (flowControlMode == FLOWCONTROL_XONXOFF) {
            return bastel.append("XonXoff");
         }
         if (flowControlMode == FLOWCONTROL_RTSCTS) {
            return bastel.append("RtsCtc");
         }
         int tfm = flowControlMode & FLOWCONTROL_XONXOFF;
         if (tfm != 0) {
            bastel.append ("XonXoff");
            if (tfm != FLOWCONTROL_XONXOFF) {
               bastel.append( tfm ==  FLOWCONTROL_XONXOFF_IN
                                       ? "(in)" : "(out)"); 
            }
            tfm = flowControlMode & FLOWCONTROL_RTSCTS;
            if (tfm != 0)
               bastel.append('&');
         } else
            tfm = flowControlMode & FLOWCONTROL_RTSCTS;
         if (tfm != 0) {
            bastel.append ("RtsCts");
            if (tfm != FLOWCONTROL_RTSCTS) {
               bastel.append( tfm ==  FLOWCONTROL_RTSCTS_IN
                                       ? "(in)" : "(out)"); 
            }
         }
         return bastel;
      } // flowControlAsString(StringBuilder, int)


/** Text representation of the interface's state. <br />
 *  <br />
 *  The interface's state will be appended to {@code sb} as three line 
 *  text.<br />
 *  <br >
 *  @param  sb StringBuilder to append to; if null {@code sb} is made with 
 *                 initial capacity of 180
 *  @param port the implementation, the state of which shall be reported
 *  @return sb
 */
   public static StringBuilder stateAsString(StringBuilder sb, 
                                                      final SerialDefs port){
      if (sb == null)  sb = new StringBuilder (180);
      if (port == null) return sb;

      sb.append(port.getPortName()).append(" : ");
      sb.append(port.getBaud()).append(" baud, ");
      sb.append(port.getDataBits()).append(" data bits, ");
      int stB = port.getStopBits();
      if (stB == STOPBITS_1_5) {
         sb.append("1.5");
      } else {
         sb.append(stB);
      }
      sb.append(" stop bits, Parity ");
      sb.append(parityAsString(port.getParity()));
     /// String lastEx = port.getExMsg();
     /// if (lastEx != null) {
        /// sb.append(",\n last error: ").append(lastEx);
     /// }
      boolean ready = port.isReady();
      if (!ready) {
         sb.append(", not ready.\n");
         return sb;
      }
      //sb.append(",\n RTS = ").append(port.isRTS());
      //sb.append(", DTR = ").append(port.isDTR());
      sb.append(",\n DSR = ").append(port.isDSR());
      sb.append(", CTS = ").append(port.isCTS());
      sb.append(", CD = ").append(port.isCD());
      sb.append(", RI = ").append(port.isRI());
      sb.append(",\n FlC = ");
      flowControlAsText(sb, port.getFlowControlMode());
      
      stB = port.getRcvTimeout();
      if (stB > 0) {
         sb.append(" Timeout = ");
         if (port instanceof SerNimpl) {
            //serTimeouts[0] = readIntervalTimeout;
            //serTimeouts[1] = readTotalTimeoutMultiplier;
            //serTimeouts[2] = readTotalTimeoutConstant;
            sb.append(stB).append(" + n* ");
            sb.append(((SerNimpl)port).serTimeouts[1]);
            sb.append(" || ");
            sb.append(((SerNimpl)port).serTimeouts[0]);
         } else {
           sb.append(stB);
         }
         sb.append(" ms");
      }   
      return sb.append(".\n");
   } // stateAsString(StringBuilder, SerialDefs)

/** Interpret a character sequence as parity specification. <br />
 *  <br />
 *  The character sequences  NONE, ODD, EVEN, MARK and SPACE (without
 *  regarding case or surrounding white space) will be interpreted as a
 *  {@link SerialDefs} parity constant and its value returned.<br />
 *  <br />
 *  If not to be interpreted (by first three character match) 
 *  -1 is returned.<br />
 *  <br />
 *  @see SerialDefs#PARITY_MARK
 *  @see TextHelper#trimUq(CharSequence, String)
 */
      public static int asParity(final CharSequence parityString){
         String parity = TextHelper.trimUq(parityString, null);
         if (parity == null) return -1;
         int len = parity.length();
         if (len < 3 || len > 5) return -1;
         final char c0 = TextHelper.simpLowerC(parity.charAt(0));
         final char c1 = TextHelper.simpLowerC(parity.charAt(1));
         final char c2 = TextHelper.simpLowerC(parity.charAt(2));
   
         if (c0 == 'o') {
            if (len != 3 || c1 != 'd') return -1;
            return c2 == 'd' ? PARITY_ODD : -1;
         }
   
         if (c0 == 's') {
            if (len != 5 ||  c1 != 'p') return -1;
            return c2 == 'a'  ? PARITY_SPACE : -1;
         }
   
         if (len != 4) return -1;
   
         if (c0 == 'n') {
            if (c1 != 'o') return -1;
            return  c2 == 'n'  ? PARITY_NONE : -1;
         }
   
         if (c0 == 'e') {
            if (c1 != 'v') return -1;
            return c2 == 'e'  ? PARITY_EVEN : -1;
         }
   
         if (c0 == 'm') {
            if (c1 != 'v') return -1;
            return c2 == 'r' ? PARITY_MARK : -1;
         }
         return -1;
      } // asParity(CharSequence)
   

/** Determine the baud rate from an baud index. <br />
 *  <br />
 *  Values of the parameter {@code baudIndex} in the range 0 to 12 will be
 *  interpreted as the number / index of one of the usual (standard) baud 
 *  rates:<br />
 *  &nbsp; 110, 150, 300, 600, 1200, 2400, 4800, 9600, 19200, [0 .. 8]<br />
 *  &nbsp; 28800, 38400, 57600 and 115200 [9 .. 12].<br />
 *  The corresponding rate will be returned.<br />
 *  <br />
 *  Parameter ({@code baudIndex}) values &gt;= 60 will be returned unchanged
 *  as they may be special baut rates.<br />
 *  Other parameter values (below 60) return -1.<br />
 *  <br />
 *  @param baudIndex Index 0..12 or itself a rate  (if &gt;= 60)
 *  @return Baudrate
 */
      public static int baudRate(int baudIndex){
         if (baudIndex >= 60) return baudIndex;
         if (baudIndex > 12 || baudIndex < 0) return -1;
         return baudTrans[baudIndex];
      } // baudRate(int)
      
      private static int[] baudTrans = {
           110, 150, 300, 600, 1200, 2400, 4800, 9600, 19200,
           28800, 38400, 57600, 115200 };
      
/** Fetch an object on an implementing class, optionally open the port.<br />
 *  <br /> 
 *  It will be tried to get an (not opened) object of a class implementing
 *  {@link SerialDefs} making it by its parameterless Constructor. If the 
 *  parameter {@code implClass} does not denominate a fitting class  
 *  &quot;de.frame4j.io.SerNimpl&quot; will be taken.<br />
 *  <br />
 *  Hint: The class used in the end may / should be checked by 
 {@link Object#getClass() getClass}.{@link Class#getName() getName()}.<br />
 *  <br />
 *  If the making of the not opened {@link SerialDefs} object fails, null is
 *  returned.<br />
 *  <br />
 *  If the parameter {@code portName} is not empty it will be tried to
 *  connect the {@link SerialDefs} object just made by 
 *  {@link SerialDefs#openSerial(CharSequence) openSerial(portName)}. The
 *  parameter {@code portName} may also be a list (according to the rules of
 *  the method {@link TextHelper#prepParams(CharSequence, String, boolean) 
 *  prepParams(portName, null, false)}) of port names. that will be tried 
 *  one by one until success or end of list.<br />
 *  <br />
 *  Hint: The success of the trial to
 *  {@link SerialDefs#openSerial(CharSequence) open} / to connect can be 
 *  checked by {@link SerialDefs#isReady() isReady()} of the object returned.
 *  In the case of a supplied port name list 
 *  {@link SerialDefs#getPortName() getPortName()} will return the single 
 *  port name used / connected to in the end.<br />
 *  <br />
 *  @param implClass the implementing classes name; default is 
 *                   de.frame4j.io.SerNimpl
 *  @param portName  operation system / platform name or names of the serial
 *                   port to optionally connect; e.g
 *                   &quot;COM2&quot; or a list like 
 *                   &quot;COM2 serialB&quot;; null or empty means no opening
 *                   connecting to be tried by using
 *     {@link SerialDefs}.{@link SerialDefs#openSerial(CharSequence) 
 *                openSerial(portName)}  
 *  @return the ordered {@link SerialDefs} object
 *  @see TextHelper#prepParams(CharSequence, String, boolean)
 */
      public static SerialDefs newInstance(final CharSequence implClass,
                                               final CharSequence portName){
         String impCl = TextHelper.trimUq(implClass, 
                                                 "de.frame4j.io.SerNimpl");
         SerialDefs commPort = null;
         try {
            Class<?> impSerDe = Class.forName(impCl); 
            if (SerialDefs.class.isAssignableFrom(impSerDe)) {
              // commPort = (SerialDefs) impSerDe.newInstance();
               @SuppressWarnings("unchecked")
               Constructor<? extends SerialDefs> con = (Constructor<? extends SerialDefs>) impSerDe.getConstructor(ComVar.NO_CLASSES);
               commPort = con.newInstance((Object[] )null);
            }
         } catch (Exception ex) { }
         if (commPort == null) {
            commPort = new SerNimpl();
            /// if (commPort == null) return null;
         }
         String[] portNs = TextHelper.prepParams(portName, null, false);
         if (portNs == NO_STRINGS) return commPort;
         for (int i = 0; i < portNs.length; ++i) {
            try {
               commPort.openSerial(portNs[i]);
               return commPort;
            } catch (Exception e) {} // ignore here
         } // for
         return commPort;
      } // newInstance(2*CharSequence)
      
   } // class SerialDefs.Helper (12.2006, 08.2009)
   
} // interface SerialDefs (12.2006, 08.2009, 08.2013)