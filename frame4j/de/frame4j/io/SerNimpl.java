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
import java.io.InputStream;
import java.io.OutputStream;

import de.frame4j.text.TextHelper;

/** <b>Serial input and output (by native implementation)</b>. <br />
 *  <br />
 *  The input and output over the computer's serial (standard, V.24, RS232
 *  etc. pp.) interfaces is most efficiently implemented by JNI
 *  (native methods).<br />
 *  <br />
 *  The similarities to javax.comm respectively RXTX go as far as their
 *  habits are compatible to the implemented interface 
 *  {@link de.frame4j.io.SerialDefs}. Otherwise this class sticks to usual
 *  operating system habits as procured by the used class
 *  {@link de.frame4j.io.WinDoesIt}.<br />
 *  <br />
 *  The {@link SerNimpl} object is (itself) a {@link java.io.InputStream},
 *  allowing (in the ready state) the reading of bytes or byte arrays from the 
 *  serial interface.<br />
 *  <br />
 *  An OutputStream allowing the writing of bytes or byte arrays to the 
 *  serial interface is provided (as object of an anonymous inner class) by
 *  the method {@link #getOut()}  (in the ready state). But, the three 
 *  {@link #write write()} methods as well as {@link #close close()} should be
 *  called as directly provided by this {@link SerNimpl} object, except where
 *  an {@link OutputStream} object is explicitly needed.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2007 &nbsp; Albrecht Weinert<br /> 
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 44 $ ($Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $)
 */
 // So far   V00.00 (19.12.2006) :  new
 //          V.  42 (05.05.2021) : parityAsString() etc. default implemented

public class SerNimpl extends InputStream implements SerialDefs  {
   
/** The serial port's operating system name. <br /> */   
   String portName;
   
   @Override public final String getPortName(){ return this.portName; }
   
/** The serial port's number (handle). <br /> */
   long port;

/** The serial port's number (handle). <br />
 *  <br />
 *  The returned valid handle (if applicable) will be used (at least in the 
 *  Windows implementation  {@link WinDoesIt}) internally for all further 
 *  operations on the respective port.<br />
 *  <br />
 *  For (very) special applications that port (as its number / handle) is made
 *  available by this method.<br />
 *  <br />
 *  Hint: This goes (far) beyond the type {@link SerialDefs}'s contract.<br />
 *  <br />
 *  Hint 2: Until July 2013 this was an int. 64 bit Windows 64 bit made the
 *  &quot;handle&quot; type a long (without any evident need for the range).
 *  <br />
 *  @return Handle to the port used; on Windows it is a (pseudo) file handle
 */   
   public long getPort(){ return this.port; }

  
/** Connect to a serial port of given name. <br />
 *  <br />
 *  This {@link SerNimpl} respectively {@link SerialDefs} object will be
 *  connected to the serial port named {@code comPort}.<br />
 *  <br />
 *  Any failure will be signalled by an {@link IOException}.<br />
 *  <br />
 *  Hint: On Windows the serial ports' names are usually &quot;COM1&quot;,
 *        &quot;COM2&quot; and so on; and they are case-insensitive.<br />
 *  <br />
 *  @param comPort Portname (null or empty returns null without Exception)
 *  @see SerialDefs.Helper#newInstance(CharSequence, CharSequence)
 */   
   @Override public void openSerial(CharSequence comPort) 
                                                   throws IOException {
      String akt = TextHelper.trimUq(comPort, null);
      if (akt == null) {
         if (isReady()) return;
         throw new IllegalArgumentException("empty name");
      }
      if (isReady()) {
         if (TextHelper.areEqual(portName, akt, true)) return;
         close();
      }
      this.portName = akt;
      this.port = WinDoesIt.openSerialPort(akt);
      if (port == WinDoesIt.INVALID_HANDLE_VALUE) {
         throw new IOException(genIOexc("open", akt));
      }
      String namePort = akt + '\\' + port;
      
      serParams = WinDoesIt.getSerialParams(port);
      if (serParams == null) {
         throw new IOException(genIOexc("getParams", namePort));
      }

      serTimeouts = WinDoesIt.getSerialTimeouts(port);
      if (serTimeouts == null) {
         throw new IOException(genIOexc("getParams", namePort));
      }
     
      this.isOpen   = true;
      this.dtr = this.rts = false;
   } // openSerialPort(CharSequence)

   int[]   serParams;
   int[]   serTimeouts;
   String namePort;
   
/** Last consumed error. <br />
 *  <br />
 *  The generation of an exception text  will consume the last error to be 
 *  retrieved (by WinDoesIt.getLastError()). This value will be held here for
 *  further debugging only.<br />
 *  Not for normal use
 */
   public static volatile int errNoRetLast; 
   
/** Helper method: make an IOException. <br />
 *  <br />
 *  @param op the operation
 *  @param akt in ()
 *  @throws IOException
 */
   private static String genIOexc(final String op, final String akt){
      final int errNo  =   WinDoesIt.getLastError();
      errNoRetLast = errNo;
      String erM = errNo == 0  ? " refused " 
                               : WinDoesIt.errorMessage(errNo);
      return  op + "(" + akt + ") error " + errNo
                                                          + ": " + erM;
   } // genIOexc(String 
   
//-----------------------------------------------------------------------   

   @Override public int getRcvTimeout(){
      return serTimeouts == null ? 0 : serTimeouts[2];
   } // getRcvTimeout()


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
 *  output buffer / array provided).<br />
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
   public boolean setSerialTimeouts(int readIntervalTimeout, 
                           int readTotalTimeoutMultiplier,
                           int readTotalTimeoutConstant,
                           int writeTotalTimeoutMultiplier,
                           int writeTotalTimeoutConstant){
      boolean ret = WinDoesIt.setSerialTimeouts(port, readIntervalTimeout,
                readTotalTimeoutMultiplier, readTotalTimeoutConstant,
                writeTotalTimeoutMultiplier, writeTotalTimeoutConstant);
      if (ret) {
         if (serTimeouts == null) serTimeouts = new int[5];
         serTimeouts[0] = readIntervalTimeout;
         serTimeouts[1] = readTotalTimeoutMultiplier;
         serTimeouts[2] = readTotalTimeoutConstant;
         serTimeouts[3] = writeTotalTimeoutMultiplier;
         serTimeouts[4] = writeTotalTimeoutConstant;
      } else {
         int[] reread = WinDoesIt.getSerialTimeouts(port);
         if (reread != null)  serTimeouts = reread;
      }
      return ret;
   } // setSerialTimeouts(5*int)

   @Override public void setRcvTimeout(int rcvTimeout){
      if (rcvTimeout < 0 
            || ( serTimeouts != null &&rcvTimeout == serTimeouts[2])) return;
      setSerialTimeouts(serTimeouts[0], 0, rcvTimeout, serTimeouts[3],
                              serTimeouts[4]);
   } // setRcvTimeout(int)

   @Override public int getBaud(){
      return serParams == null ? -1 : serParams[0];
   } // getBaud()

   @Override public int getDataBits(){
      return serParams == null ? -1 : serParams[1];
   } // getDataBits() 

   @Override public int getStopBits(){
      if (serParams == null) return -1;
      int ret = serParams[2];
      if (ret == 0) return STOPBITS_1;
      if (ret == 1) return STOPBITS_1_5;
      return ret;
   } // getStopBits()

   @Override public int getParity(){
      return serParams == null ? -1 : serParams[3];
   } // getParity()

   @Override public String parityAsString(){
      if (serParams == null) return "--";
      return SerialDefs.Helper.parityAsString(serParams[3]);
   } // parityAsString()

   @Override public boolean setSerialPortParams(int baudrate,
                        final int dataBits, int stopBits, final int parity){
      if (!isOpen) return false;
      boolean anyChg = false;
      
      if (baudrate != -1) { // baud change
         baudrate = SerialDefs.Helper.baudRate(baudrate);
         anyChg = anyChg || baudrate != serParams[0];
      } // baud change
            
      if (stopBits == STOPBITS_1) { // stop translate
         stopBits = 0;
      } else if (stopBits == STOPBITS_1_5) {
         stopBits = 1;
      } else if (stopBits > 2) {
         stopBits = 0;
      } // // stop translate
      anyChg = anyChg || (stopBits != -1 && stopBits != serParams[2]);
      
      if (dataBits != -1) {
         anyChg = anyChg || dataBits != serParams[1];
      }
      
      anyChg = anyChg || (parity != -1 && parity != serParams[3]);
      
      if (!anyChg) return true;
      
      if (!WinDoesIt.setSerialParams(port,
                    baudrate, dataBits, stopBits, parity, -1)) return false;

      int[] newSerParams = WinDoesIt.getSerialParams(port);
      if (newSerParams  == null) return false;
      
      serParams = newSerParams;
      
      if (baudrate != -1 && baudrate != serParams[0]) return false;
      if (dataBits != -1 && dataBits  != serParams[1]) return false;
      if (stopBits != -1 && stopBits != serParams[2]) return false;
      return parity == -1 ||  parity == serParams[3];
   }  // setSerialPortParams(4*int)

   @Override public int getFlowControlMode(){
      return serParams == null ? 0 : serParams[4];
   }

   @Override public String flowControlAsString(){
      if (serParams == null) return "unknown";
      return SerialDefs.Helper.flowControlAsText(null, 
                                               serParams[4]).toString();
   } // flowControlAsString()

   @Override public void setFlowControlMode(int flowControlMode) {
      if (serParams == null) return;
      if (flowControlMode == -1 || flowControlMode == serParams[4]) return;
      if (flowControlMode == 0) {
         if (WinDoesIt.setSerialParams(port,
                                 -1, -1, -1, -1, 0)) serParams[0] = 0;
      }
      // TODO nur set 0 ist implementiert 
   }  // setFlowControlMode(int)
   
   @Override public boolean isRTS(){ return rts;  }

/** Storage: last set RTS. <br /> */   
   boolean rts;    
   
   @Override public void setRTS(final boolean rts){
      if (!isOpen) return;
      this.rts = rts;
      WinDoesIt.escapeComm(port, rts ? WinDoesIt.SETRTS : WinDoesIt.CLRRTS);
   } // setRTS(boolean)

/** Storage: last set DTR. <br /> */   
   boolean dtr;
   
   @Override public boolean isDTR(){ return dtr;  }

   @Override public void setDTR(final boolean dtr){
      if (!isOpen) return;
      this.dtr = dtr;
      WinDoesIt.escapeComm(port, dtr ? WinDoesIt.SETDTR : WinDoesIt.CLRDTR);
   } // setDTR(boolean)

   @Override public void setDtrRts(final boolean dtr, final boolean rts){
      if (isOpen) {
         this.dtr = dtr;
         this.rts = rts;
         WinDoesIt.setDtrRts(port, dtr, rts);
      }
   } // setDtrRts(2*boolean) 

   
   @Override public boolean isDSR(){
      int statusCode = WinDoesIt.getSerialModemStatus(port);
      return statusCode != -1 && (statusCode & WinDoesIt.MS_DSR_ON) != 0;
   }

   @Override public boolean isCTS(){
      int statusCode = WinDoesIt.getSerialModemStatus(port);
      return statusCode != -1 && (statusCode & WinDoesIt.MS_CTS_ON) != 0;
   } // isCTS()

   @Override public boolean isCD(){
      int statusCode = WinDoesIt.getSerialModemStatus(port);
      return statusCode != -1 && (statusCode & WinDoesIt.MS_RLSD_ON) != 0;
   } // isCD()

   @Override public boolean isRI(){
      int statusCode = WinDoesIt.getSerialModemStatus(port);
      return statusCode != -1 && (statusCode & WinDoesIt.MS_RING_ON) != 0;
   } // isRI()

   boolean isOpen;
   
   @Override public boolean isReady(){ return isOpen; }

   @Override public void close(){
      if (isOpen) WinDoesIt.closePort(port);
      isOpen = false;
      out = null;
   } // close()

   @Override public int read(){ return WinDoesIt.getSerial(port); }

   @Override public int read(byte[] b, int off, int len){
      if (b == null || len <= 0) return 0;
      if (off < 0) return -1;
      int lB = b.length;
      if (off + len > lB) return -1;
      return WinDoesIt.readSerial(port, b, off, len);
   } // read(byte[], 2*int)

   @Override public int write(byte[] b, int off, int len) {
      if (b == null || len <= 0) return 0;
      if (off < 0 || !isOpen) return -1;
      int lB = b.length;
      if (off + len > lB) return -1;
      return WinDoesIt.writeSerial(port, b, off, len);
   } // write(byte[], int, int)

   @Override public int write(byte[] b){
      if (b == null) return 0;
      final int len = b.length;
      return write(b, 0, len);
   } // write(byte[])

   @Override public boolean write(int b){
      if (isOpen) return WinDoesIt.putSerial(port, (byte)b) == 1;
      return false;
   } // write(int)

/** Empty the (system's) output buffer. <br />
 *  <br />
 *  The output buffers will be flushed.<br />
 *  <br />
 *  Hint: There are no useful informations on necessity and return behaviour
 *  for serial interfaces from platform specification. It should be not to
 *  naive, to infer that bytes fed to a USART or its driver will flow out
 *  by itself as fast as hardware and receiving station permit.<br />
 *  This method (i.e. the method provided native by the platform) called often
 *  or may be at all will probably make no sense.<br />
 *  <br />
 *  @return true on success
 *  @see WinDoesIt#flushSerial(long)
 */   
   public boolean flush(){
      if (isOpen) return WinDoesIt.flushSerial(port);
      return false;
   } // flush() 

/** Get rid of pending operations. <br />
 *  <br />
 *  It is quite usual to call this method with the OR of all PURGE constants
 *  after opening.<br />
 *  <br />
 *  @param ops  One or more (OR) PURGE constants
 *  @return true bei Erfolg
 *  @see WinDoesIt#PURGE_RXABORT
 *  @see WinDoesIt#PURGE_RXCLEAR
 *  @see WinDoesIt#PURGE_TXABORT
 *  @see WinDoesIt#PURGE_TXCLEAR
 *  @see WinDoesIt#PURGE_OR
 *  @see WinDoesIt#purgeSerialPort(long, int)
 */   
   public boolean purgeSerial(int ops){
      if (isOpen) return WinDoesIt.purgeSerialPort(port, ops);
      return false;
   } // purgeSerial(int)
   
   
   OutputStream out;

/** Fetching the {@link OutputStream}. <br />
 *  <br />
 *  Only in case of a {@link #isReady() ready} port this method provides the 
 *  &quot;out direction&quot; as  stream. {@link #close()} of the port or 
 *  of that {@link OutputStream} invalidate both.<br />
 *  <br />
 *  Hint 1: The {@link OutputStream} object mainly delegates to the
 *  &quot;direct&quot; {@link SerNimpl} respectively {@link SerialDefs} write
 *  methods. They should be used directly as far as possible. In other
 *  words, this method should not be called, except in cases where an 
 *  {@link OutputStream} object is needed explicitly and unavoidably.<br />
 *  <br />
 *  Hint 2: If an {@link InputStream} is needed, this {@link SerNimpl} object
 *          (itself) is it.<br />
 *  <br />
 *  Hint 3: Repeated calls return the same {@link OutputStream} object, as
 *          long as the serial port stays {@link #isReady() ready}.<br />
 *  <br />
 *  @return A fitting {@link OutputStream} or null
 */   
   public OutputStream getOut(){
      if (out == null && isOpen) {
         out = new OutputStream(){

            @Override public void write(int b) throws IOException {
               if (!SerNimpl.this.write(b)) 
                  throw new IOException("SerNimpl.out.write() failed");
            }
            
            @Override public void write(byte[] b, int off, int len) throws IOException {
               if (SerNimpl.this.write(b, off, len) != len)
                  throw new IOException("SerNimpl.out.write(,,) failed");
            }
            
            @Override public void write(byte b[]) throws IOException {
               if (b == null) 
                  throw new IOException("SerNimpl.out.write(null)");
               final int len = b.length;
               if (len == 0) return;
               if (SerNimpl.this.write(b, 0, len) != len)
                  throw new IOException("SerNimpl.out.write([]) failed");
            }
            
            @Override public void close() {
               SerNimpl.this.close();
            }
            @Override public void flush() {
               SerNimpl.this.flush();
            }
         };
      }
      return out;
   } // getOut()

   @Override public String getExMsg(){
      int errNo = WinDoesIt.getLastError();
      if (errNo == 0) return null;
      return ("error = " + errNo
                 + " : " + WinDoesIt.errorMessage(errNo));
   } // getExMsg()
   

/** Text representation.  <br />
 *  <br />
 *  The interface's state will be given as a (may be multi-line) text.<br />
 *  <br />
 *  If an error is pending according to the method's {@link #getExMsg()}
 *  criteria, its explanation will be part of the status text returned.<br />
 *  <br >
 */
   @Override public String toString(){
      return SerialDefs.Helper.stateAsString(null, this).toString();
   } // toString()

/** Text representation.  <br />
 *  <br />
 *  The interface's state will be given as a (may be multi-line) text.<br />
 *  <br />
 *  If an error is pending according to the method's {@link #getExMsg()}
 *  criteria, its explanation will be part of the status text returned.<br />
 *  <br />
 *  @param bastel the StringBuilder to append to. If null, it will be 
 *                          generated  with starting capacity 180. 
 *  @return bastel
 */
   public StringBuilder toString(StringBuilder bastel){
      if (bastel == null)  bastel = new StringBuilder (180);
         bastel.append(portName).append(" : ");
         if (serParams == null) {
            bastel.append("unknown setting, ");
         } else {   
            bastel.append(serParams[0]).append(" baud, ");
            bastel.append(serParams[1]).append(" data bits, ");
            int tmp = serParams[2];
            if (tmp == 2) {
               bastel.append("2 stop bits, ");
            } else if (tmp == 1) {
               bastel.append("1,5 stop bits, ");
            } else {
               bastel.append("1 stop bit, ");
            }
            bastel.append(" parity ");
            bastel.append(SerialDefs.Helper.parityAsString(serParams[3]));
         }
         bastel.append("\n    ");
         if (! isOpen) {
            bastel.append(", not ready.  ");
            return bastel;
         }
         bastel.append("timeout settings: ");
         if (serTimeouts == null) {
            bastel.append("unknown.  ");
         } else {
            bastel.append(serTimeouts[2]).append(" + n* ");
            bastel.append(serTimeouts[1]);
            bastel.append(" || ");
            bastel.append(serTimeouts[0]);
            bastel.append(" ms.  ");
         }
         return bastel;
   } // toString(StringBuilder)

}  // class SerNimpl  (07.01.2007)

