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

import static de.weAut.PiGpioDdefs.*; // pigpio library defines
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import de.frame4j.text.TextHelper;
import de.frame4j.util.ComVar;

/** <b>Handling of pigpio library's socket interface</b>.<br />
 *  <br />
 *  An object of this class represents one Raspberry Pi's GPIO by 
 *  being a socket connection to the Pi's pigpiod server. Who has such
 *  object may do almost all possible IO with Java &mdash; no matter if the
 *  JVM + frame4j is on this Pi or another machine including Windows or
 *  Linux PCs and servers.
 *   
 *  For one Pi and its IO only one {@link ClientPigpiod} object should be
 *  created and used. The IO methods and most others are threadsafe but 
 *  initialising and shutting down / cleaning up the control IO application
 *  is usually better done in one thread. <br />
 *  The C IO functions of the 
 *  <a href="http://abyz.me.uk/rpi/pigpio/sif.html" taget="_blank"
 *  title="pigdiod socket interface">piogpio daemon</a> on the Raspberry Pi 
 *  are also threadsafe, there. <br />
 *  <br />
 *  <br />
 *  <a href=package-summary.html#co>&copy;</a> 
 *  Copyright 2019, 2021  &nbsp; Albrecht Weinert<br />
 *  @see Pi1
 *  @see Pi2
 *  @see Pi3
 *  @see ThePi
 *  @see PiVals 
 *  @author   Albrecht Weinert
 *  @version  $Revision: 47 $ ($Date: 2021-05-13 19:06:22 +0200 (Do, 13 Mai 2021) $)
 */
// so far:   V.  19 (17.05.2019) : new
//           V.  42 (29.04.2021) : overhaul (Frame4J)
//           V.  46 (09.05.2021) : threadsafe cmd and response buffers
public class ClientPigpiod {

/** The socket. <br />
 *  <br />
 *  The actual made connection's socket will be recorded here. Without 
 *  connection or after disconnect the value is null.<br />
 */
   Socket sock;

/** The socket input stream. <br /> */
   InputStream sockIn;

/** The socket output stream. <br /> */
   OutputStream sockOut;
   
/** The output for logging. <br /> */
   PrintWriter out;
 
/** Set own PrintWriter out. <br />
 *  <br />
 *  Helper method for constructors / factories. <br />
 *  <br />
 *  This method sets this {@link ClientPigpiod} objects {@link PrintWriter}
 *  for reports. If the parameter {@code app} is a {@link PrintWriter}
 *  {@code app} will be be used. <br />
 *  Otherwise, if {@code app} is of type {@link de.frame4j.util.App},
 *  {@link de.frame4j.util.App} or {@link de.weAut.PiUtil} that objects
 *  own (current) {@link PrintWriter} out will be taken. <br />
 *  All other cases default to (wrapped)
 *  {@link java.lang.System#out System.out}.   
 */
   protected PrintWriter setOut(Object app){
     if (app instanceof PrintWriter) {
       return this.out = (PrintWriter)app;
     }
     if (app instanceof de.frame4j.util.App) {
       return this.out = ((de.frame4j.util.App)app).out;
     }
     if (app instanceof PiUtil) {
       return this.out = ((PiUtil)app).getOut();
     }
     if (app instanceof de.frame4j.io.AppIO) {
       return this.out = ((de.frame4j.io.AppIO)app).out;
     }
     return this.out = new PrintWriter(System.out, true);
   } // setOut(Object)

/** Connecting. <br />
 *  <br />
 *  A socket connection to the server's  {@link ThePi#port() port} is made.
 *  The streams will be provided.<br />
 *  <br />
 *  An exception occurring during the proceeding will be forwarded. A partly
 *  built up connection will be destroyed (by {@link #disconnect()} in this
 *  case.<br />
 *  <br />
 *  Hint: After successfully making an ClientPigpiod object it <b>is</b>
 *  connected. Hence, this method is normally not needed. The only exception
 *  would be re-connect after {@link #disconnect()}. So far, no use case for
 *  this was reported. If this does not change {@link #connect()} and
 *  even {@link #disconnect()} might be removed / made private in future.
 *  
 *  @throws IOException if the connecting
 *          to {@link ThePi#host() host}.{@link ThePi#port() port} fails
 */
   public synchronized Socket connect() throws IOException {
     try {
         if (sock == null) {
            sock = new Socket(thePi.host(), thePi.port());        
            sock.setSoTimeout(thePi.timeout()); // default 10 s
            sock.setReceiveBufferSize(260);
            sockIn = sock.getInputStream();
            sockOut = sock.getOutputStream();
         }
         return sock;
      } catch (IOException ex) {
         try {
            disconnect();
         } catch (Exception e){} // silently ignore disconnect exception
         throw ex; // but throw all others
      }
   } // connect()
   
/** The Raspberry Pi used for IO. <br />
 *  <br />
 *  This object describes the used Pi type. <br />
 *  <br />
 *  It is either {@link Pi3} (default, describing Pi0, Pi3 and Pi4) or
 *  {@link Pi1} (with the 26 pin connector only) or the practically unused
 *  {@link Pi2} (with two IO connectors). 
 */
   public final ThePi thePi;
   
/** Make and connect to a new Pi. <br />
 *  <br />
 *  Depending on parameter type this method makes a new {@link ThePi Pi}
 *  object. Then it tries to make a a new {@link ClientPigpiod} connected 
 *  to a real Raspberry Pi according to parameters host, port and timeout.<br />
 *  Finally (and on success, only) the new ClientPigpiod object will
 *  get a PrintWriter for reports according to 
 *  {@link #setOut(Object) setOut(app)}. <br />
 *
 *  @return a ClientPipiod with the given
 *        {@link thePi Pi} = {@link ThePi#host() host} 
 *        connected via {@link ThePi#port() port} within 
 *        {@link ThePi#timeout() timeout} 
 *  @throws IOException when connecting to the pigpiod server fails
 *  @see Pi3#make(String, int, int, int)
 *  @see Pi1#make(String, int, int)    
 */
  static public ClientPigpiod make(final String host, final int port, 
    final int timeout, final int type, final Object app) throws IOException {
    if (type == 1) return new ClientPigpiod(Pi1.make(host, port, timeout), app);
    if (type == 2) return new ClientPigpiod(Pi2.make(host, port, timeout), app);
    return new ClientPigpiod(Pi3.make(host, port, timeout, type), app);
  } // make(String, 3*int, Object)
  
/** Make and connect to a new Pi. <br />
 *  <br />
 *  This method does the same as
 {@link #make(String,int,int,int,Object) make(host, port, timeout, type, app)}.
 *  The values host, port, timeout and type are got from app's 
 *  {@link PiUtil} genes as default values or by start parameter evaluation.
 *
 *  @param app the application as {@link PiUtil} object
 *  @return a ClientPipiod with the given
 *        {@link thePi Pi} = {@link ThePi#host() host} 
 *        connected via {@link ThePi#port() port} within 
 *        {@link ThePi#timeout() timeout} 
 *  @throws IOException when connecting to the pigpiod server fails
 *  @see Pi3#make(String, int, int, int)
 *  @see Pi1#make(String, int, int)    
 */
  static public ClientPigpiod make(final PiUtil app) throws IOException {
    final int type = app.argPiType();
    final String host = app.argHost();
    final int port = app.argPort();
    final int timeout = app.argTimeout();
    if (type == 1) return new ClientPigpiod(Pi1.make(host, port, timeout), app);
    if (type == 2) return new ClientPigpiod(Pi2.make(host, port, timeout), app);
    return new ClientPigpiod(Pi3.make(host, port, timeout, type), app);
  } // make(String, 3*int, Object)

/** Construct and connect to a given Pi. <br />
 *
 *  @param thePi if null a default {@link Pi3} is made and used
 *  @param app the application using the ClientPigpiod object to be made
 *  @throws IOException when connecting to the pigpiod server fails
 *  @see #make(String, int, int, int, Object) {@link #setOut(Object)}
 */
   public ClientPigpiod(final ThePi thePi, Object app) throws IOException {
     this.thePi = thePi != null ? thePi : Pi3.make(null, 0, 0, 3);
      sock = connect();
      setOut(app); // helper method
   } // ClientPigpiod(CharSequence, 2 * int) 
   
/** Disconnecting. <br />
 *  <br />
 *  The socket connection made by {@link #connect connect()} will be
 *  closed.<br />
 *  <br />
 *  Hint: Another thread blocked on this connection's I/O will get a
 *  SocketException. <br />
 *
 *  @throws IOException on closing problems
 */
   public synchronized void disconnect() throws IOException {
      if (sock != null) {
        sock.close();
        sock    = null;
        sockIn  = null;
        sockOut = null;
      }
   } // disconnect()

/** The connection's settings as String. <br /> */
   @Override public String toString() {
      return "pigpiod: " + thePi.host() + (sock != null ? ":" : "-")
                         + thePi.port() + " Pi" + thePi.type();
   } // toString()
   
//---------------------------- pigpiod GPIO on the Pi ---------------------     

/** <b>Status of a command executed</b>. <br />
 *  <br >   
 *  One object of this class will be held per thread to hold the values of
 *  the current / last pigpiod IO command executed for the purpose of
 *  analysis or {@linkplain ClientPigpiod#logCommand(int) logging}. As this is
 *  done on a per thread base there is no need to guard an
 *  [IO method + log method] pair be synchronized.
 */
   public final static class CmdState {
     
/** Response buffer. <br /> */
  byte[] response = new byte[16];

/** Command buffer. <br /> */  
  byte[] command = new byte[] { 
// | command   0    |   p1     gpio   |    assume little endian               
    0,  0,  0,  0,     17,  0,  0, 0,    // 17 PIN11 rd LED
// |    p2   mode   | p3 (extLen)     |
    1,  0,  0,  0,     0,  0,  0,  0  }; // 1 output

/** Record form last pigpiod command. <br />
 *  <br />
 *  If the last execution of {@link #stdCmd(int, int, int)} raised an
 *  exception it is caught and recorded here for subsequent logging or 
 *  debugging. Otherwise the {@link #stdCmd(int, int, int)} sets
 *  {@link #lastException} to null.<br />
 *  An exception can only happen in the course of the socket communication
 *  with the pigpiod daemon on the Pi.
 *  @see #logCommand(int) {@link #debugCommand(int)} {@link #lastCmd} 
 */
     public Throwable lastException;

/** Record form last pigpiod command.<br />
 *  <br />
 *  The last execution of {@link #stdCmd(int, int, int)} records its 
 *  parameters command, and its p1 and p2 parameter here for subsequent
 *  logging or debugging before the next execution of
 *  {@link #stdCmd(int, int, int)} directly or more often indirectly by one
 *  of the other IO commands. <br />
 *  The approach is not threadsafe. Applications with multiple control
 *  threads or cycles must not use these recorded informations in normal 
 *  operation.
 *  @see #logCommand(int) {@link #debugCommand(int)} {@link #lastCmd} 
 */
     public int lastCmd, lastP1, lastP2;
     int cmdExecStage; // 0: none; 2..15: request; 16..31: + response
   } // CmdState
   
/** A container holding one CmdState object per thread. <br /> */  
   static final ThreadLocal<CmdState> lastCmdState = 
                                            new ThreadLocal<CmdState>(){
     @Override protected CmdState initialValue(){
       final CmdState initVal = new CmdState();
       initVal.lastCmd = PI_CMD_NONE; // if logged w/o command recorded
       return initVal;
     } // initialValue()
   }; // embedded ThreadLocal child
   
/** Debug info on last command execution. <br />
 *  <br />
 *  When this method is called immediately after a command method it will 
 *  log the command on {@link out}.<br />
 *  Compared to {@link #logCommand(int)} debugCommand also logs the socket 
 *  request and response buffers which is only very seldom of any
 *  interest. <br />
 *  
 *  @param  ret the return value of the command method; <br /> &nbsp; &nbsp;
 *     &nbsp; &nbsp; e.g. by placing the call as parameter
 *           <code>logCommand(commandMethod(...))</code>
 *  @return ret (the return value of the command method)
 *  @see #logCommand(int) #logIfBad(int)
 */
   public int debugCommand(final int ret){
     CmdState cmdSt = lastCmdState.get();
     final int lastCmd = cmdSt.lastCmd, lastP1 = cmdSt.lastP1,
               lastP2 = cmdSt.lastP2, cmdExecStage = cmdSt.cmdExecStage;
     if (ret ==  PI_CMD_BAD) { // bad command nothing done
        out.println(" ERR stdCmd(" + cmdSt.lastCmd + ", " + lastP1
                                      + ", " + lastP2 +") -> bad command");
        return ret;
     } // bad command nothing done  
     if (ret >= 0 || uint32ret[lastCmd]) { // positive or unsigned
        out.println("  DEB ioCmd(" + lastCmd + ", " + lastP1
                 + ", " + lastP2 +") -> " + (ret & 0xFFFFFFFFL) 
                                                + " " + cmdNam[lastCmd]);
     } else {
        out.println("  ERR ioCmd(" + lastCmd + ", " + lastP1
                 + ", " + lastP2 +") -> " + ret + " " + cmdNam[lastCmd]);
     }
     if (cmdExecStage >= 1) { // request used //  logRequest &&
        out.println("   request : " + Arrays.toString(cmdSt.command));
     } // request used
     if (cmdExecStage >= 16) { // request used //  logResponse &&
        out.println("   respons : " + Arrays.toString(cmdSt.response));
     } // request used
     if (cmdSt.lastException != null) 
        out.println("      " + cmdSt.lastException.getMessage());
        out.flush();
     return ret;
   } // debugCommand(int)
   
/** Recording helper for IO methods. <br />
 *  <br />
 *  Some of the comfortable IO methods bypass the implementing command
 *  {@link #stdCmd(int, int, int)} when recognising erroneous
 *  parameters (e.g.) hence bypassing the socket communication for the 
 *  thrill of just getting an error value in return  (for those cases where
 *  {@link #stdCmd(int, int, int)} does not do pre-checks itself). <br />
 *  As the parameter recording is bypassed also the methods doing so
 *  must (should) set lastCmd, lastP1, lastP2 in this case in the thread 
 *  local {@link #lastCmdState} object.<br />
 *  This method is a helper therefore, as are {@link #rErr(int, int)}
 *  and {@link #rIgn(int, int)}.     
 */
  protected final int rErr(final int error, 
                          final int cmd, final int p1, final int p2){
    CmdState cmdSt = lastCmdState.get();
    cmdSt.lastCmd = cmd; cmdSt.lastP1 = p1; cmdSt.lastP2 = p2; 
    return error;
  } //rErr(4*int)

/** Recording helper for IO methods for errors. <br />
 *  <br />
 *  @see #rErr(int, int, int, int)
 */   
  protected final int rErr(final int error, final int cmd){
    CmdState cmdSt = lastCmdState.get();
    cmdSt.lastCmd = cmd; cmdSt.lastP1 = cmdSt.lastP2 = 0; return error;
  } //rErr(2*int)

/** Recording helper for IO methods. <br />
 *  when operating on a pin/GPIO to be ignored.
 *  <br />
 *  @see #rErr(int, int, int, int)
 */
  protected final int rIgn(final int cmd, final int p2){
    CmdState cmdSt = lastCmdState.get();
    cmdSt.lastCmd = cmd; cmdSt.lastP1 = ThePi.PINig; 
    cmdSt.lastP2 = p2; return 0;
  } //rErr(2*int)

  
/** Log of last command execution. <br />
 *  <br />
 *  When this method is called immediately after a command method with the 
 *  command's return value it will log the command on {@link out}.<br />
 *  Scheme: {@code [int r = ]logCommand(anyIOcommand(p1, p2));}
 *  <br />
 *  @param  ret the return value of the command method; <br /> &nbsp; &nbsp;
 *  &nbsp; &nbsp; e.g. by placing the call as parameter
 *           <code>logCommand(commandMethod(...))</code>
 *  @return ret the return value of the command method to be logged.)
 *              It must have been passed as parameter
 *  @see #logIfBad(int)
 */
  public int logCommand(final int ret){
    CmdState cmdSt = lastCmdState.get();
    final int lastCmd = cmdSt.lastCmd, lastP1 = cmdSt.lastP1,
                            lastP2 = cmdSt.lastP2;
    StringBuilder lCmd = (StringBuilder)TextHelper.formDec(
                                     new StringBuilder(34), lastCmd, 4);
    lCmd.append(',');
    TextHelper.formDec(lCmd, lastP1, 4);
    lCmd.append(',');
    TextHelper.formDec(lCmd, lastP2, 4);
     if (ret ==  PI_CMD_BAD) { // bad command nothing done
       out.println(" ERR stdCmd(" + lCmd + ") -> bad command");
       return ret;
     } // bad command nothing done  
     if (ret >= 0 || uint32ret[lastCmd]) { // positive or unsigned
       out.println("  LOG ioCmd(" + lCmd + ") -> " + (ret & 0xFFFFFFFFL) 
                                               + " " + cmdNam[lastCmd]);
     } else {
        out.println("  ERR ioCmd(" + lCmd + ") -> " + ret 
                                               + " " + cmdNam[lastCmd]);
     }
     if (cmdSt.lastException != null) 
       out.println("      " + cmdSt.lastException.getMessage());
       out.flush();
     return ret;
  } // logCommand(int)

/** Log of last bad command execution. <br />
 *  <br />
 *  When this method is called immediately after a command method it will 
 *  log the command on <code>System.out</code> if and only if the execution
 *  failed (with a negative return code). In that case it will delegate to
 *  {@link #logCommand}. <br />
 *   @param  ret the return value of the command method; <br /> &nbsp; &nbsp;
 *   &nbsp; &nbsp; e.g. by placing the call as parameter
 *           <code>logIfBad(commandMethod(...))</code>
 *   @return ret the return value of the IO command method
 *   @see #logCommand(int)
 */
   public int logIfBad(final int ret){
     if (ret >= 0) return ret;
     CmdState cmdSt = lastCmdState.get();
     final int lastCmd = cmdSt.lastCmd;
     if (uint32ret[lastCmd]) return ret; // no negative error, just unsigned
     return logCommand(ret);
   } // logIfBad(int);
 

/** Implementation of two parameter (standard) commands. <br />
 *  <br />
 *  This method handles (is the swiss army knife for) all (81) socket
 *  interface commands with up to two parameters and no extra extension data.
 *  These 81 commands are the great majority and usually the others are not
 *  needed in most control applications. This method is the base for the more 
 *  comfortable convenient methods (see some mentioned below).<br />
 *  <br />
 *  This method does the bookkeeping for logging the IO actions afterwards.
 *  It also checks a lot of command (cmd) and parameter combinations to
 *  save the pains of socket communication for just getting an error. 
 *  {@link #stdCmd(int, int, int) stdCmd()} does the communication with the
 *  pigpiod daemon as well as the bookkeeping threadsafe. <br />
 *  <br />
 *  It is not complicated to use stdCmd(3*int) directly &ndash; nevertheless
 *  this is not recommended. Better use the convenient implementations of
 *  IO operations, provided here, as  {@link #setMode(int, int)},
 *  {@link #getMode(int)}, {@link #setPadS(int, int)}, {@link #getPadS(int)},
 *  {@link #setPullR(int, int)}, {@link #setOutput(int, boolean)}
 *  etc. pp.. <br />
 *  @param cmd the command number 
 *  @param p1 first parameter, mostly GPIO number
 *  @param p2 optional second parameter
 *  @return commands return value &gt;= 0; or error number (&lt;0) 
 *          except for commands returning an unsigned value (see 
 *          {@link #uint32ret(int)}
 */
   public int stdCmd(final int cmd, final int p1, final int p2){
     CmdState cmdSt = lastCmdState.get();
     cmdSt.lastP1 = p1; cmdSt.lastP2 = p2; cmdSt.lastCmd = cmd; // enable log
     cmdSt.cmdExecStage = 0; cmdSt.lastException = null;
     
     // pre checks with own return (inside thread) - no sync needed
      if (cmd < 0 || cmd > 117 || hasExtension[cmd]) return PI_CMD_BAD;
      final int par1Sem = p1Kind[cmd]; // parameter 1 sematic
      if (par1Sem == GPIO) { // a Command for a specific gpio
        if (p1 < 0 || p1 > 53) return PI_BAD_GPIO;
        if (cmd == PI_CMD_MODES) { // command is mode set
          if ((p2 < 0 || p2 > 7)) return PI_BAD_MODE;
          if (p1 <= 31) { // set mode is for GPIO 0..31
            final int bitMsk = gpio2bit[p1];
            areOut = p2 == 0 ? areOut & ~ bitMsk : areOut | bitMsk;
          } // set mode is for GPIO 0..31
        } else if (cmd == PI_CMD_WRITE || cmd == PI_CMD_PWM
                     || cmd == PI_CMD_SERVO) { // (mode set else) write cmds
          if (p1 > 31) return PI_BAD_GPIO; // we allow no output exc. bank 0
          if (p2 < 0) return  PI_BAD_LEVEL; // wrong error for PWM don't care
          if (cmd == PI_CMD_WRITE && p2 > 1) return  PI_BAD_LEVEL;
          // we do not care on PWM value errors here --  should have come
          // here from one of the "comfort" methods which did check. 
            areOut |= gpio2bit[p1];; // pigpiod's auto set to output mode
        } // write is for GPIO 0..31
      } else if (par1Sem == BITS) { // p1  a specific gpio else a bitmask
        if (cmd == PI_CMD_BC1 || cmd == PI_CMD_BS1) { // set | clear bits
          if (p2 == 0) return 0; // nothing to do; return OK 
          areOut |= p2; // pigpiod's auto set to output mode
        }  // set or clear bits in bank 0
      } else if (par1Sem == PAD) { // (bank) bitmask (set or clear) else pad
        if (p1 < 0 || p1 > 2) return PI_BAD_PAD;
        if (cmd == PI_CMD_PADS 
                         && (p2 < 1 || p2 > 16)) return  PI_BAD_STRENGTH; 
      } // command is a pad command
      int ret = 0;
      // prepare and execute command - sync with this ClientPigpiod needed

      Arrays.fill(cmdSt.command, (byte) 0); // init all 0
      cmdSt.command[0] = (byte)cmd;
      cmdSt.command[4] = (byte)p1;
      if ((p1 & 0xFFFFFF00) != 0) {
        cmdSt.command[5] = (byte)(p1 >> 8);
        cmdSt.command[6] = (byte)(p1 >> 16);
        cmdSt.command[7] = (byte)(p1 >> 24);
      }
      cmdSt.command[8] = (byte)p2;
      if ((p2 & 0xFFFFFF00) != 0) {
        cmdSt.command[ 9] = (byte)(p2 >> 8);
        cmdSt.command[10] = (byte)(p2 >> 16);
        cmdSt.command[11] = (byte)(p2 >> 24);
      }
      cmdSt.cmdExecStage = 1;
      synchronized (this) {
      try { // the whole method is quasi sync as cmdSt is threadlocal
         sockOut.write(cmdSt.command, 0, 16);
         try {
            int bRead = sockIn.available();
            bRead = sockIn.read(cmdSt.response, 0, 16);
            cmdSt.cmdExecStage = 16;
            ret = bRead != 16 ?  PI_SOCK_READ_LEN
                       : cmdSt.response[12] | cmdSt.response[13] << 8
                 | cmdSt.response[14] << 16 | cmdSt.response[15] << 24;
         } catch (IOException e) {
           cmdSt.lastException = e;
            ret =  PI_SOCK_READ_FAILED;
         }
      } catch (IOException e) {
         cmdSt.lastException = e;
         ret = PI_SOCK_WRIT_FAILED;
      } // try
      } // synchronized 
      return ret;
   } // stdCmd(3*int)


//-------------------------    Pin / gpio initialisation  ------------------

/** Set the mode of one GPIO pin.<br />
 *  <br />
 *  @param gpio a legal IO number 0.. 31
 *  @param mode a mode like {@link PiGpioDdefs#PI_INPUT}, 
 *            {@link PiGpioDdefs#PI_OUTPUT}, {@link PiGpioDdefs#PI_ALT0} etc.
 *  @return 0: OK; &lt; 0: error
 */
   public int setMode(int gpio, int mode){
      return stdCmd(PI_CMD_MODES, gpio, mode);
   } // setMode(2 * int) 

/** Get the mode of one GPIO pin.<br />
 *  <br />
 *  @param gpio a legal BCM IO number 0.. 56
 *  @return     the mode like {@link PiGpioDdefs#PI_INPUT}, 
 *            {@link PiGpioDdefs#PI_OUTPUT}, {@link PiGpioDdefs#PI_ALT0} etc.
 *            or  &lt; 0: error
 */
   public int getMode(int gpio){
      return stdCmd(PI_CMD_MODEG, gpio, 0);
   } // getMode(int) 

/** Set the pull resistor for one GPIO pin. <br />
 *  <br />
 *  @param gpio a legal BCM IO number 0.. 31
 *  @param pud  a mode: {@link PiGpioDdefs#PI_PUD_OFF}, 
 *            {@link PiGpioDdefs#PI_PUD_DOWN} or {@link PiGpioDdefs#PI_PUD_UP}
 *  @return 0: OK; &lt; 0: error
 */
   public int setPullR(final int gpio, final int pud){
     if (pud < 0 || pud > 4) return PI_BAD_PUD;
     if (gpio < 0 || gpio > 31) return PI_BAD_USER_GPIO;
     if (pud > 2) return 0; // do nothing on default or keep
     return stdCmd(PI_CMD_PUD, gpio, pud);
   } // setMode(2 * int) 

/** Names for input's pull resistor settings. <br />
 *  @param pud  a mode: {@link PiGpioDdefs#PI_PUD_OFF}, 
 *            {@link PiGpioDdefs#PI_PUD_DOWN}, {@link PiGpioDdefs#PI_PUD_UP},
 *            {@link PiUtil#PI_PUD_KP}, {@link PiUtil#PI_PUD_DT}
 *  @return "off ", "down", "up  ", "keep", "dflt" (0..4) else "err ".
 */
  public static String pudTxt(final int pud) {
    if (pud < 0 || pud > 4) return "err ";
    return pudTxt[pud];
  } // pudTxt(int)

   
/** Set the output strength of a set of GPIO pins.<br />
 *  <br />
 *  For pins set as output (by {@link #setMode(int, int)} e.g.)
 *  this function sets the drive capacity in the range of 2..16 mA.<br />
 *  Note 1: All pins of the set/pad get the same common value. Hence, one has
 *  to set the maximum needed for any pin. <br />
 *  Note 2: This value is no current limit nor pin overload protection. It is
 *  the maximum load current, which a valid 0 or 1 output voltage can be
 *  guaranteed under. <br />
 *  Note 3: The BCM processor can set the strength in 2mA steps (2, 4.. 14, 16).
 *  Nevertheless, this function accepts all values 2..16 incrementing
 *  odd values.
 *  
 *  @see #getPadS(int) PiGpioDdefs#PI_BAD_PAD PiGpioDdefs#PI_BAD_STRENGTH 
 * 
 *  @param pad The pad or I/O port set number; 0 is GPIO numbers 0..27. <br />
 *   1 would be 28..45 and 2 46..53; To use 1 and 2 is not recommended as GPIO
 *   &gt; 31 should not be touched in regard to output.
 *  @param mA 2..16, the strength in mA
 *  @return 0: OK; &lt; 0: error
 */
   public int setPadS(final int pad, final int mA){
     if (mA < 1 || mA > 16) return rErr(PI_BAD_STRENGTH, PI_CMD_PADS, pad, mA); 
     if (pad < 0 || pad > 2) return rErr(PI_BAD_PAD, PI_CMD_PADS, pad, mA);
     return stdCmd(PI_CMD_PADS, pad, mA);
   } // setPadS(2 * int) 

/** Get the output strength of a set of GPIO pins.<br />
 *  <br />
 *  For pins set as output (by {@link #setMode(int, int)} e.g.)
 *  this function gets the drive capacity in the range of 2..16 mA.<br />
 *  
 *  @see #setPadS(int, int) PiGpioDdefs#PI_BAD_PAD PiGpioDdefs#PI_BAD_STRENGTH 
 * 
 *  @param pad The pad or I/O port set number; 0 is GPIO numbers 0..27
 *  @return  2..16 pad strength in mA
 */
   public int getPadS(final int pad){
     if (pad < 0 || pad > 2) return rErr(PI_BAD_PAD, PI_CMD_PADG);
     return stdCmd(PI_CMD_PADG, pad, 0);
   } // getPadS(int) 
   
/** Initialise one or more GPIO pin as input. <br />
 *  <br />
 *  This functions sets the pins listed as GPIOs to input mode. This
 *  is also used to release them from any output modes as input means
 *  hi impedance.
 *
 *  @see #initAsOutputs(int[]) 
 *  @param lesGPIOs array of GPIO numbers (0..53); use {@link ThePi#PINix}
 *    as end marker if the array is too long and {@link ThePi#PINig} as 
 *    an entry to be ignored.
 */
  public void initAsInputs(final int[] lesGPIOs){
    if (lesGPIOs == null) return;
    for(int act : lesGPIOs) {
        if (act >= ThePi.PINix || act < 0) break; //
        if (act > 53) continue; 
        initAsInput(act); // make input; release as output
     } // for over GPIO number list
  } // initAsInputs(unsigned const[])

/** Put one or more GPIO pins to output mode.<br />
 *  <br />
 *  This functions sets the pins listed as GPIOs to output mode.
 *
 *  @see #initAsInputs(int[]) 
 *  @param lesGPIOs array of GPIO numbers (0..53); use {@link ThePi#PINix}
 *    as end marker if the array is too long and {@link ThePi#PINig} as 
 *    an entry to be ignored.
 *  @return the bit mask for all GPIOs set (or left) as output by this call   
 */
  public int initAsOutputs(final int[] lesGPIOs){
    if (lesGPIOs == null) return 0;
     int ret = 0;
     for(int act : lesGPIOs) {
       if (act >= ThePi.PINix || act < 0) break; //
       if (act > 53) continue; 
       ret |= initAsOutput(act); // make output
     } // for over GPIO number list
     return ret;
  } // initAsOutputs(unsigned const[])


/** Report an arbitrary operation on a list of GPIOs. <br />
 *  <br />
 *  The report lines on ::outLog will be
 *  {@code   progNam   ___operation GPIO: 13 pin: 27 \endcode }
 *  @param out the writer to output to
 *  @param op  the operation displayed as 12 characters right justified
 *  @param lesGPIOs a GPIO list 
 *         (terminated by a value &gt;= {@link ThePi#PINix})
 */
  public void reportPinOp(final Appendable out, 
                               final String op, final int[] lesGPIOs){
    if (out == null || op == null || lesGPIOs == null) return;
    final Flushable flushi =
                 out instanceof Flushable ? (Flushable)out : null;
    for(int act : lesGPIOs) {
       if (act >= ThePi.PINix || act < 0) break;
       if (act > 53) continue;
       try {
       out.append("  ").append(ComVar.PROG_SHORT).append(" ")
         .append(op).append(" GPIO");
       PiUtil.twoDigitDec(out, act).append(" pin");
       PiUtil.twoDigitDec(out, thePi.gpio2pin(act)).append('\n');      
       if (flushi != null) flushi.flush();
       } catch (IOException ex) {} // ignore
     } // for
  } // reportPinOp(char const *, unsigned const [])

/** Stores all output GPIO as bitmask. <br />
 *  <br />
 *  For all GPIOs in the range 0..31 set here as output the corresponding 
 *  bit is set. For all GPIOs in the range 0..31 set here as input the
 *  corresponding bit is cleared.
 *  @see #areOut()
 */
  int areOut; // omit volatile if set clear is put to synchonised method
  
/** Stores all output GPIO as bitmask. <br />
 *  <br />
 *  For all GPIOs in the range 0..31 set here as output *) the corresponding 
 *  bit is set. For all GPIOs in the range 0..31 set here as input the
 *  corresponding bit is cleared. <br />
 *  The rationale is an automatic bookkeeping to enable a comfortable
 *  "release as input" function mainly for the end of / shutdown of the 
 *  application. <br />
 *  <br />
 *  Note *): To be precise, it is set as "not input" as we wan't also to 
 *  release alternate GPIO functions.  
 */
  public final int areOut(){ return areOut; }   

/**  Initialise a GPIO pin as output. <br />
 *  <br />
 *  This sets a GPIO as output and puts it in the list of GPIOs used as
 *  outputs by the program if in the range of 0..31 (resp. 2..27). For
 *  other values nothing is done and 0 is returned. <br />
 *  <br />
 *  All functions setting setting as output should use this function.
 *
 *  @param gpio  the GPIO number (0..31); PINig means no action
 *  @return the bank mask of this output if set (0 no action or error)
 */
  public int initAsOutput(final int gpio){
    if (gpio < 0 || gpio > 31) return rIgn(PI_CMD_MODES, PI_OUTPUT);
    int ret = stdCmd(PI_CMD_MODES, gpio, PI_OUTPUT); // make it output
    if (ret < 0) return 0; // error, nothing done
    ret = gpio2bit[gpio];
    //    areOut |= ret;  // done in stdCmd
    return ret;
  } // initAsLoInput(int, unsigned)

/**  Release all GPIO pins set as output. <br />
 *  <br />
 *  This releases all GPIO in the range 0..27 that this program has set as
 *  outputs (by setting those as inputs). <br />
 *  The order of this operation is by increasing GPIO number. If another
 *  order or extra actions are required this must be done before or
 *  afterwards.
 *
 *  @return the bank mask of the previous resp. released outputs
 */
  public int releaseOutputs(){
    final int wereOut = areOut;
    int gpio = 0;
    for (; gpio < 28; ++gpio) {
      if ((wereOut & gpio2bit[gpio]) != 0) {
        stdCmd(PI_CMD_MODES, gpio, PI_INPUT); // make it PI_INPUT
      }
    } // for
    //  areOut = 0; should have been done in stdCmd
    return wereOut;
  } // releaseOutputs(int)

/** Release all GPIO pins set as output with report. <br />
 *  <br />
 *  Same as {@link #releaseOutputs()} plus a "releaseToIn" report line for
 *  every output released if a usable non null out is supplied.
 *
 *  @param out a destination to report to; 
 *          for example a PrintWriter, PrintStream or StringBuilder
 *  @return the bank mask of the released (previous) outputs
 */
  public int releaseOutputsReport(final Appendable out){
    final int wereOut = areOut;
    if (wereOut == 0) return 0; // nothing to do and to report
    if (out == null) return releaseOutputs();
    final Flushable flushi = out instanceof Flushable ? (Flushable) out : null;
    try {
      out.append("\n  ").append(ComVar.PROG_SHORT)
             .append(" outputs are  GPIOs: 0x");
      PiUtil.eightDigitHex(out, wereOut).append('\n');
      if (flushi != null) flushi.flush();
    } catch (IOException ex) {} // ignore 
    int gpio = 0;
    for (; gpio < 28; ++gpio) {
      if ((wereOut & gpio2bit[gpio]) != 0) {
        stdCmd(PI_CMD_MODES, gpio, PI_INPUT); // make it PI_INPUT
        try {
          out.append("  ").append(ComVar.PROG_SHORT)
           .append(" releaseToIn  GPIO");
          PiUtil.twoDigitDec(out, gpio).append(" pin");
          PiUtil.twoDigitDec(out, thePi.gpio2pin(gpio)).append('\n');
          if (flushi != null) flushi.flush();
        } catch (IOException ex) {} // ignore 
       }
    } // for
  //     areOut = 0; // done in stdCmd()
    return wereOut;
  } // releaseOutputsReport(int const)

  
/** Initialise a GPIO pin as input. <br />
 *  <br />
 *  This sets a GPIO as input and removes it form the list of GPIOs set as
 *  output if in the range of 0..31 (resp. 2..27). <br />
 *  <br />
 *  All functions setting as input should use this function.
 *  
 *  @param gpio  the GPIO number (0..53)
 */
  public int initAsInput(final int gpio){
    if (gpio < 0 || gpio > 53) {
      return rErr(PI_BAD_GPIO, PI_CMD_MODES, gpio, PI_INPUT);
    }
    return stdCmd(PI_CMD_MODES, gpio, PI_INPUT); // make it PI_INPUT
   // done in stdCmd      areOut &= ~gpio2bit[gpio];
  } // initAsLoInput(int, unsigned)

/** Initialise a GPIO pin as input with pull up. <br />
 *  <br />
 *  This initialisation is for an input sensing a switch (button) or transistor
 *  (optocoupler) connected to ground (gnd, 0V). This is the normal
 *  configuration instead of switching to Hi (3.3V). <br />
 *  In most of the cases the Pi's internal pull up resistor (about 50 kOhm) is
 *  sufficient for Lo-switches and should then be used.
 *
 *  @param gpio  the GPIO number (0..53)
 *  @return &lt; 0 : error
 */
 public int initAsLoInput(final int gpio){
     int ret = initAsInput(gpio);
     if (ret < 0) return ret;
     return stdCmd(PI_CMD_PUD, gpio, PI_PUD_UP); // switch or transistor to gnd
  } // initAsLoInput(int, unsigned)

/** Initialise a GPIO pin as input with pull down. <br />
 *  <br />
 *  This initialisation is for an input sensing an electronic device delivering
 *  a voltage about 3 V when active respectively ON. Some of those devices
 *  require a pull down to deliver clean signals. <br />
 *  The Pi's internal pull down resistor (about 50 kOhm) may be
 *  sufficient for Hi-switches and should then be used.
 *
 *  @param gpio  the GPIO number (0..53)
 *  @return &lt; 0 : error
 */
 public int initAsHiInput(final int gpio){
    int ret = initAsInput(gpio);
    if (ret < 0) return ret;
    return stdCmd(PI_CMD_PUD, gpio, PI_PUD_DOWN); // switch or transistor to +
  } // initAsHiInput(int, unsigned)

/** Initialise a GPIO pin as high drive. <br />
 *  <br />
 *  Of course, Raspberry's (BCM2837's) GPIO pins are high and low drivers as
 *  output. Hi-drive is provided by turning on pull-up as to allow broken
 *  wire diagnosis when shortly switching to input.
 *
 *  @param gpio  the GPIO number (o..53)
 *  @param init  0 or 1: the initial output value; else: leave unchanged
 *  @return &lt; 0 : error
 */
 public int initAsHiDrive(final int gpio, final int init){
    if (gpio < 0 || gpio > 31) {
      return rErr(PI_BAD_USER_GPIO, PI_CMD_MODES, gpio, PI_OUTPUT); 
    }
    int ret =  stdCmd(PI_CMD_MODES, gpio, PI_OUTPUT); // make it output
    if (ret < 0) return ret; // error
    ret = stdCmd(PI_CMD_PUD, gpio,  PI_PUD_UP); // set pull up
     if (init == 1 || init == 0) {
       ret = stdCmd(PI_OUTPUT, gpio, init);
     }
     return ret;
  } // initAsHiDrive(2 * int)


/** Initialise a GPIO pin as output. <br />
 *  <br />
 *  This function sets the GPIO pi as output, optionally sets the drive
 *  capacity and leaves a pull resistor setting unchanged.
 *
 *  @param gpio  the GPIO number (0..53)
 *  @param init  0 or 1: the initial output value; else: leave unchanged
 *  @return &lt; 0 : error
 */
 public int initAsDrive(final int gpio, final int init){
    if (gpio < 0 || gpio > 31) {
      return rErr(PI_BAD_USER_GPIO, PI_CMD_MODES, gpio, PI_OUTPUT); 
    }
    int ret =  stdCmd(PI_CMD_MODES, gpio, PI_OUTPUT); // make it output
    if (init == 1 || init == 0) {
      ret = stdCmd(PI_OUTPUT, gpio, init);
    }
    return ret;
  } // initAsHiDrive(2 *int)
     
//-------------------------  read and write --------------------------------

/** Set one GPIO output pin. <br />
 *  <br />
 *  This functions sets an output pin gpio ON or OFF. <br />
 *  If gpio is {@link ThePi#PINig} nothing is done and 0 is returned.
 *  That handles the meaning of {@link ThePi#PINig PINig} as unused.
 *
 *  @param gpio 0..31 gpio a GPIO to output to 
 *  @param level OFF or ON (0 or 1)
 *  @return &lt; 0: pigpiod error
 */
  public int setOutput(final int gpio, final boolean level){
    if (gpio == ThePi.PINig) {
      return rErr(0, PI_CMD_MODES, gpio, level ? 1 : 0); 
    } // no action ignore
    if (gpio < 0 || gpio > 31) {
      return rErr(PI_CMD_WRITE, PI_CMD_MODES, gpio, level ? 1 : 0); 
    }
    return stdCmd(PI_CMD_WRITE, gpio, level ? 1 : 0); // set the, OFF
  } // setOutput(2* unsigned const)

/** Set a list/mask of GPIO output pins. <br />
 *  <br />
 *  This functions sets the (output) pins set in the bank mask ON or OFF.
 *  The method only works for bank 0 (GPIO 0..27) which pigpiod (falsely
 *  calls bank1),  
 *
 *  @param lesOuts bank mask of outputs to be set
 *  @param level OFF or ON (0 or 1)
 *  @return &lt; 0: pigpiod error
 */
  public int setOutputSet(final int lesOuts, final boolean level){
    if (lesOuts == 0) return rIgn(level ? PI_CMD_BS1 : PI_CMD_BC1, 0); // none
    if (level) return stdCmd(PI_CMD_BS1, lesOuts, 0); // set them ON
    return stdCmd(PI_CMD_BC1, lesOuts, 0); // set the, OFF
  } // setOutputs(int, boolean)
   
/** Read the pin state. <br />
 *     
 *  @param gpio a legal BCM IO number 0..56
 *  @return 0 or 1: OK; &lt; 0: error
 */
   public int getInp(int gpio){
     if (gpio == ThePi.PINig) return rIgn(PI_CMD_READ, 0); // return 0 = Low
     if (gpio < 0 || gpio > 56) return rErr(PI_BAD_GPIO, PI_CMD_READ, gpio, 0);
     return stdCmd(PI_CMD_READ, gpio, 0);
   }  // getInp(int)

/** Set the PWM duty cycle. <br />
 *     
 *  @param gpio a legal BCM IO number 0..28
 *  @param val  0..255 as 0 .. 100% (in default pwm range)
 *  @return 0: OK; &lt; 0: error
 */
   public int setPWMcycle(int gpio, int val){
     if (gpio == ThePi.PINig) return rIgn(PI_CMD_PWM, val); // no action ignore
     if (gpio < 0 || gpio > 31) return rErr(PI_BAD_USER_GPIO, PI_CMD_PWM, gpio, val);
     if (val < 0 || val > 40000) return rErr(PI_BAD_DUTYCYCLE, PI_CMD_PWM, gpio, val);
     return stdCmd(PI_CMD_PWM, gpio, val);
   }  // setPWMcycle(2 * int) 

/** Get the PWM duty cycle. <br />
 *     
 *  @param gpio a legal BCM IO number 0.28
 *  @return  0..255 as 0 .. 100% or  &lt; 0: error
 */
   public int getPWMcycle(int gpio){
     if (gpio == ThePi.PINig) return rIgn(PI_CMD_GDC, 0); // ign. 0 = Off
     if (gpio < 0 || gpio > 31) return rErr(PI_BAD_USER_GPIO, PI_CMD_GDC, gpio, 0);
     return stdCmd(PI_CMD_GDC, gpio, 0);
   }  // getPWMcycle(int) 

/** Set the servo position. <br />
 *  <br />
 *  This is a special PWM command for RC serveros
 *     
 *  @param gpio a legal BCM IO number 0..28
 *  @param val  0 (Off) or 500 (full left) .. 2500 (full right)
 *  @return 0: OK; &lt; 0: error
 */
   public int setServoPos(int gpio, int val){
     if (gpio == ThePi.PINig) return rIgn(PI_CMD_SERVO, 0); // no action ignore
     if (gpio < 0 || gpio > 31) return rErr(PI_BAD_USER_GPIO, PI_CMD_SERVO, gpio, val);  
     if ((val != 0) && (val < 500 || val > 2500)) {
       return rErr(PI_BAD_PULSEWIDTH, PI_CMD_SERVO, gpio, val);  
     }
     return stdCmd(PI_CMD_SERVO, gpio, val);
   }  // setServoPos(2 * int) 

/** Get the servo pulse width.
 *     
 *  @param gpio a legal BCM IO number 0.28
 *  @return  0 Off, or pulse width in µs (1500..2500)
 */
   public int getServoPw(int gpio){
     if (gpio == ThePi.PINig) return rIgn(PI_CMD_GPW, 0); // no action ignore
     if (gpio < 0 || gpio > 31) return rErr(PI_BAD_USER_GPIO, PI_CMD_GPW, gpio, 0); 
     return stdCmd(PI_CMD_GPW, gpio, 0);
   }  // getServoPw(int) 


/** Set the PWM frequency. <br />
*     
*  @param gpio a legal BCM IO number 0..28
*  @param hz frequency in Hz  5... 40000 depending on duty cycle
*  @return 0: OK; &lt; 0: error
*/
  public int setPWMhertz(int gpio, int hz){
    if (gpio == ThePi.PINig) return rIgn(PI_CMD_PFS, hz); // no action ignore
    if (gpio < 0 || gpio > 31) return rErr(PI_BAD_USER_GPIO, PI_CMD_PFS, gpio, hz);
    return stdCmd(PI_CMD_PFS, gpio, hz);
  }  // setPWMhertz(2 * int) 

/** Get the PWM frequency. <br />
*     
*  @param gpio a legal BCM IO number 0..28
*  @return  frequency in Hz  5... 40000 depending on duty cycle
*            or  &lt; 0: error
*/
  public int getPWMhertz(int gpio){
    if (gpio == ThePi.PINig) return rIgn(PI_CMD_PFG, 0); // ign return 0 Hz
    if (gpio < 0 || gpio > 31) rErr(PI_BAD_USER_GPIO, PI_CMD_PFG, gpio, 0);
    return stdCmd(PI_CMD_PFG, gpio, 0);
  }  // getPWMhertz(int) 
  
  
//------------------- helper methods  and  final 'const' arrays -------------

/** GPIO number to bank pin number lookup. <br />
 *  <br />
 *  The parameter gpio in the range 0..31 is a GPIO number partly available
 *  on the Pi's 40 (26) pins connector.<br />
 *  Returned result is then 0x00000001..0x80000000: a 32 bit number with
 *  exactly one bit set corresponding to place in a 32 bit bank mask. <br />
 *  Outside the range 0..31 this method returns 0 (no bit set.
 *  @see ThePi#gpio2pin(int)
 *  @see ThePi#gpio4pin(int)
 */
  public static int gpio2bit(final int gpio){ 
    if (gpio < 0 || gpio > 32) return 0;
    return gpio2bit[gpio]; 
  } //  gpio2bit
  
  
/** GPIO number to bank pin number lookup. <br />
 *  <br />
 *  Index [0..31] is a GPIO number partly (0, 2..27) available on the Pi's 40
 *  (26) pins connector.<br />
 *  Result 0x00000001..0x80000000: a 32 bit number with exactly one bit set
 *  corresponding to place in a 32 bit bank mask. <br />
 *  Outside [0..31] index out of bound.
 */
 private static final int[] gpio2bit = new int[] {
         0x00000001, 0x00000002, 0x00000004, 0x00000008, //  0.. 3
         0x00000010, 0x00000020, 0x00000040, 0x00000080, //  4.. 7
         0x00000100, 0x00000200, 0x00000400, 0x00000800, //  8..11
         0x00001000, 0x00002000, 0x00004000, 0x00008000, // 12..15
         0x00010000, 0x00020000, 0x00040000, 0x00080000, // 16..19
         0x00100000, 0x00200000, 0x00400000, 0x00800000, // 20..23
         0x01000000, 0x02000000, 0x04000000, 0x08000000, // 24..27
         0x10000000, 0x20000000, 0x40000000, 0x80000000};// 28..31

 
//-------------------------------------   Command properties tables   -------
 
/** Names for input's pull resistor settings. <br />
 *  <br />
 *  Mapping of pull resistor setting numbers 0..3 (5) to Strings of 
 *  length 4: "off ", "down", "up  ", "keep", "dflt".
 */
 private static String[] pudTxt = {"off ", "down", "up  ", "keep", "dflt"};
 
//-------------------------------------   result/p3 semantics  --------------   


/** Command result type. <br />
 *  <br />
 *  Most commands return a (signed) int value as result. Here any not
 *  negative value means a good return value or without a return value 0
 *  means command was executing OK. A negative value means an error; 
 {@link PiGpioDdefs#PI_INIT_FAILED}..{@link PiGpioDdefs#PI_BAD_EVENT_ID}.<br />
 *  <br />
 *  Very few commands return an unsigned 32 bit value (uint32_t in C), where
 *  the Java (always signed) int negative value interpretation as error
 *  would be utterly wrong.<br />
 *  These five commands returning uint32_t are: <br />
 *  BR1 * 10, BR2 * 11, TICK * 16, HWVER * 17, PIGPV * 26.<br />
 *  In Joan N.N.'s <a href="http://abyz.me.uk/rpi/pigpio/sif.html"
 *  >documentation</a> they are marked with *; and they can't fail. <br />
 *  @param cmd command number (0..117)
 *  @return true: the commands return value is 32 bit unsigned
 */
 public static final boolean uint32ret(int cmd){
   if (cmd < 10 || cmd > 26) return false;
   return uint32ret[cmd];
 } // uint32ret(int)
 
/** Command result type. <br />
 *  <br />
 *  These five uint32_t commands are: <br />
 *  BR1 * 10, BR2 *   11, TICK * 16, HWVER * 17, PIGPV * 26.<br />
 *  In Joan N.N.'s <a href="http://abyz.me.uk/rpi/pigpio/sif.html"
 *  >documentation</a> they are marked with *; and they can't fail. <br />
 *  See the explanation at {@link #uint32ret(int)}. <br />
 */
   private static final boolean[] uint32ret = {
//   0      1      2      3      4      5      6      7      8      9                        
 false, false, false, false, false, false, false, false, false, false, // 00 
 true,   true, false, false, false, false, true,   true, false, false, // 10 
 false, false, false, false, false, false, true,  false, false, false, // 20 
 false, false, false, false, false, false, false, false, false, false, // 30 
 false, false, false, false, false, false, false, false, false, false, // 40 
 false, false, false, false, false, false, false, false, false, false, // 50 
 false, false, false, false, false, false, false, false, false, false, // 60
 false, false, false, false, false, false, false, false, false, false, // 70 
 false, false, false, false, false, false, false, false, false, false, // 80 
 false, false, false, false, false, false, false, false, false, false, // 90 
 false, false, false, false, false, false, false, false, false, false, //100 
 false, false, false, false, false, false, false, false, false, };     //110 
  

/** Command has extension. <br />
 *  <br />
 *  Most commands have just two parameters p1 and p2 and must have p3 = 0 on 
 *  request. But 37 commands prolong the request by an extension the length
 *  of which in bytes must be put in p3.<br />
 *  Those more special command get a true in this table. They can't be 
 *  handled by {@link ClientPigpiod#stdCmd(int, int, int)}. Trying to do so
 *  gets a {@link PiGpioDdefs#PI_CMD_BAD} error.<br />
 *  Hint: As of April 2021 here is no implementation of non standard commands
 *  as no need arose so far. This might change in future.
 */
  private static final boolean[] hasExtension = {
//   0      1      2      3      4      5      6      7      8      9                        
 false, false, false, false, false, false, false, false, false, false, // 00
 false, false, false, false, false, false, false, false, false, false, // 10
 false, false, false, false, false, false, false, false,  true,  true, // 20
 false, false, false, false, false, false, false,  true,  true, false, // 30
  true, false,  true, false, false, false, false, false, false, false, // 40
 false, false, false, false,  true, false, false,  true, false, false, // 50
 false, false,  true, false,  true, false,  true,  true,  true,  true, // 60
  true,  true, false, false,  true,  true,  true, false, false, false, // 70
 false,  true, false, false, false, false,  true,  true,  true, false, // 80
  true,  true,  true,  true, false, false, false, false,  true, false, // 90
 false, false, false, false,  true, false, false,  true,  true,  true, //100
  true, false,  true,  true,  true, false, false,  true};              //110

//-------------------------------------   p1 semantics  --------------------
  
/** Kind of parameter p1 by command number. <br />
 *  <br />
 *  The kind of the byte or int parameter p1 depends on the command 
 *  respectively the command number 0..117. <br />
 *  An entry 0 means p1 must be 0. <br />
 *  {@link GPIO} means it has to be a GPIO/BCM I/O number in the range
 *  0..31 or 0..53; and so on. <br />
 *  There are 22 kinds of p1 semantics.
 */
   private static final int[] p1Kind = {
  GPIO,  GPIO,  GPIO,  GPIO,  GPIO,  GPIO,  GPIO,  GPIO,  GPIO,  GPIO, // 0..9
  0,     0,  BITS,  BITS,  BITS,  BITS,     0,     0,     0,  HANDLE,
  HANDLE, HANDLE, GPIO, GPIO, GPIO, IGNORE,    0,     0,     0,  GPIO,
  0,     0,     0,     0, SUBCMD, SUBCMD, SUBCMD, GPIO, 0, SCRIPT_ID,
  SCRIPT_ID, SCRIPT_ID, GPIO, GPIO, GPIO, SCRIPT_ID, MICROS, MILLIS, IGNORE, 0,
  WAVE_ID, WAVE_ID, WAVE_ID, 0, BUS, HANDLE, HANDLE, HANDLE, HANDLE, HANDLE,
  HANDLE, HANDLE, HANDLE, HANDLE, HANDLE, HANDLE, HANDLE, HANDLE, HANDLE, HANDLE,
  HANDLE, CHANNEL, HANDLE, HANDLE, HANDLE, HANDLE,  BAUD, HANDLE, HANDLE, HANDLE,
  HANDLE,  HANDLE,  HANDLE, GPIO, GPIO, GPIO, GPIO, ARG1,  ARG1,  SDA,
  SDA,   SDA, HANDLE,     0,  GPIO,    0, CONFIG,  GPIO,  GPIO,     0,
  WAVE_ID, 0,  PAD,  PAD,  MODE, HANDLE, HANDLE, HANDLE, HANDLE, COUNT,
  LEN_NAME,  CS,  CS,  CS,  CONTROL,  HANDLE,  EVENT,  SCRIPT_ID};  

/** Command short names. <br />
 *  <br />
 *  This array yields a short command name by command number. The short
 *  names are those from the
<a href="http://abyz.me.uk/rpi/pigpio/sif.html">socket interface documentation</a>. <br />
 *  <br />
 *  Parameter (index) is the command number in the range 0..117. Names of
 *  unimplemented commands in that range start with &quot;bad&quot;.
 */
 private static final String[] cmdNam = {
// 0        1       2      3       4       5      6      7     8      9
"MODES", "MODEG", "PUD", "READ", "WRITE", "PWM", "PRS", "PFS", "SERVO", "WDOG",
"BR1", "BR2", "BC1", "BC2", "BS1", "BS2", "TICK", "HWVER", "NO", "NB", // 10
"NP", "NC", "PRG", "PFG", "PRRG", "HELP", "PIGPV", "WVCLR", "WVAG", "WVAS",
"bad30", "bad31", "WVBSY", "WVHLT", "WVSM", "WVSP", "WVSC", "TRIG", "PROC", "PROCD",
"PROCR", "PROCS", "SLRO", "SLR", "SLRC", "PROCP", "MICS", "MILS", "PARSE", "WVCRE",
"WVDEL", "WVTX", "WVTXR", "WVNEW", "I2CO", "I2CC", "I2CRD", "I2CWD", "I2CWQ", "I2CRS",
"I2CWS", "I2CRB", "I2CWB", "I2CRW", "I2CWW", "I2CRK", "I2CWK", "I2CRI", "I2CWI", "I2CPC",
"I2CPK", "SPIO", "SPIC", "SPIR", "SPIW", "SPIX", "SERO", "SERC", "SERRB", "SERWB",
"SERR", "SERW", "SERDA", "GDC", "GPW", "HC", "HP", "CF1", "CF2", "BI2CC", // 80
"BI2CO", "BI2CZ", "I2CZ", "WVCHA", "SLRI", "CGI", "CSI", "FG", "FN", "NOIB",
"WVTXM", "WVTAT", "PADS", "PADG", "FO", "FC", "FR", "FW", "FS", "FL",  // 100
"SHELL", "BSPIC", "BSPIO", "BSPIX", "BSCX", "EVM", "EVT", "PROCU", // 110..117
"none", "none"};  // 118 .. no command (as of 05.2021)

} // ClientPigpiod (21.05.2019, 17.04.2021)
