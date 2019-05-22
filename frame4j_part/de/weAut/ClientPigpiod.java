/*  Copyright 2019 Albrecht Weinert, Bochum, Germany (a-weinert.de)
 *  All rights reserved.
 *  
 *  This file is part of Frame4J 
 *  ( frame4j.de  http://weinert-automation.de/software/frame4j/ )
 * 
 *  Frame4J is made available under the terms of the 
 *  Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/  or as text in
 *  [frame4jSourceRoot]/de/frame4j/doc-files/epl.txt within 
 *  the source distribution 
 */
package de.weAut;

import static de.weAut.PiGpioDdefs.*; // pigpio library defines

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;


/** <b>Handling of pigpio library's socket interface</b>.<br />
 *  <br />
 *   An object of this class provides a Java API to essential parts of the
 *   pigpio library via socket. For one device only one object should be
 *   created and used in one thread as the methods are not thread safe. For
 *   multi-threaded concurrent use a synchronising wrapper around method calls
 *   will be needed. On the other hand multiple objects connecting to the same
 *   Pi's GPIO should be feasible cause of different socket connections to the
 *   piogpio daemon.<br />
 *  <br />
*  <a href=./de/weAut/package-summary.html#co>&copy;</a> 
 *  Copyright 2019 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 22 $ ($Date: 2019-05-22 20:22:28 +0200 (Mi, 22 Mai 2019) $)
 */
// so far:   V. 19  (17.05.2019) :  new
//           V. 21  (19.05.2019) :  ALT numbers, typo
public class ClientPigpiod { 

/** The server's (the host's) name. <br />
 *  <br />
 *  Name of a computer reachable via TCP/IP.<br />
 *  <br />
 *  default: 127.0.0.1
 */
  protected String host = "127.0.0.1";

/** The server's (the host's) name. <br />
 *  <br />
 *  Name of a computer reachable via TCP/IP.<br />
 *  <br />
 *  default: 127.0.0.1
 */
   public final String getHost(){ return host; }

/** The server's (the host's) port. <br />
 *  <br />
 *  @see #getPort()
 */
   protected int port = 8888;

/** The server's (the host's) port. <br />
 *  <br />
 *  The server port's number (&gt;0) to connect to.<br />
 *  <br />
 *  default: 8888 (pigpiod)
 */
   public final int getPort(){ return port; }
  
/** Maximum wait time for the server's (host's) responses. <br />
 *  <br />
 *  @see #getTimeOut()
 */
   protected int timeOut = 30;

/** Maximum wait time for the server's (host's) responses. <br />
 *  <br />
 *  A value &gt; 0 is the maximum waiting time in seconds.<br />
 *  A value = 0 means unlimited waiting.<br />
 *  A value &lt; 0 is taken a 30s (default).
 *  <br />
 *  default: 30
 */
   public final int getTimeOut() {  return timeOut;  }
 
/** The socket. <br >
 *  <br />
 *  The actual made connection's socket will be recorded here. Without 
 *  connection or after disconnect the value is null.<br />
 *  <br />
 */
   public volatile Socket sock;

/** The socket input stream. */
   public volatile InputStream sockIn;

/** The socket output stream. */
   public volatile OutputStream sockOut;
 

/** Connecting. <br />
 *  <br />
 *  A socket connection to the server's  {@link #port} is made. The streams
 *  will be provided.<br />
 *  <br />
 *  An exception occurring during the proceeding will be forwarded. A partly
 *  built up connection will be destroyed (by {@link #disconnect()} in this
 *  case.<br />
 *  <br>
 *  @throws IOException if the connecting to {@link #host}.{@link #port} fails
 */
   public Socket connect() throws IOException {
      try {
         if (sock == null) {
            sock = new Socket(host, port);        
            sock.setSoTimeout(timeOut * 1000);
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
   } // connect

   public ClientPigpiod(final String host, int port, int timeout)
                                       throws IOException{
      this.timeOut = timeOut < 0 ? 10 : timeOut;  
      this.port = port < 20 || port > 65535 ? 8888 : port;
      this.host = host;
      sock = new Socket(host, port);
      sockIn = sock.getInputStream();
      sockOut = sock.getOutputStream();
 //       System.out.println(" TEST Pigpio(...) -> " + sock); 
   } // Pigpio(final CharSequence host, 2 * int) 

   public ClientPigpiod() throws IOException{
      this("127.0.0.1", 8888, 10);
   } //  Pigpio()


   
/** Disconnecting. <br />
 *  <br />
 *  The socket connection made by {@link #connect connect()} will be
 *  closed.<br />
 *  <br />
 *  Hint: Another thread blocked on this connection's I/O will get a
 *  SocketException.<br />
 *  <br />
 *  @throws IOException on closing problems
 */
   public void disconnect() throws IOException {
      if (sock != null) {
         sock.close();
         sock    = null;
         sockIn  = null;
         sockOut = null;
      }
   } // disconnect()

/** The connection's settings as String. <br /> */
   @Override public String toString() {
      if (sock != null) return sock.toString();
      return "ClientPiogpiod " + host + ":" + port + " not connected";
   } // toString()
   byte[] response = new byte[16];
   
   byte[] command = new byte[] { 
//    | command   0    |   p1     port   |    assume little endian               
       0,  0,  0,  0,     17,  0,  0, 0,    // 17 PIN11 rd LED
//    |    p2   mode   | p3 (extLen)     |
       1,  0,  0,  0,     0,  0,  0,  0  }; // 1 output
   
   public Throwable lastException;
   public int lastCmd, lastP1, lastP2;
   int cmdExecStage; // 0: none; 2..15: request; 16..31: + response
   
/** Log of last command execution. 
 * 
 *  When this method is called immediately after a command method it will 
 *  log the command on <code>System.out</code>.
 *  
 *   @param  ret the return value of the command method; <br /> &nbsp; &nbsp;
 *   &nbsp; &nbsp; e.g. by placing the call as parameter
 *           <code>logCommand(commandMethod(...))</code>
 *   @return ret (the return value of the command method)
 */
   public int logCommand(final int ret){
      if (ret ==  PI_CMD_BAD) { // bad command nothing done
        System.out.println(" ERR stdCmd(" + lastCmd + ", " + lastP1
                                      + ", " + lastP2 +") -> bad command");
        return ret;
      } // bad command nothing done  
      if (ret >= 0 || uint32ret[lastCmd]) { // positive or unsigned
         System.out.println(" LOG stdCmd(" + lastCmd + ", " + lastP1
                 + ", " + lastP2 +") -> " + (ret & 0xFFFFFFFFL) 
                                                + " " + cmdNam[lastCmd]);
      } else {
         System.out.println(" ERR stdCmd(" + lastCmd + ", " + lastP1
                 + ", " + lastP2 +") -> " + ret + " " + cmdNam[lastCmd]);
      }
      if (cmdExecStage >= 1) { // request used //  logRequest &&
         System.out.println("   request : " + Arrays.toString(command));
      } // request used
      if (cmdExecStage >= 16) { // request used //  logResponse &&
         System.out.println("   respons : " + Arrays.toString(response));
      } // request used
      if (lastException != null) 
          System.out.println("      " + lastException.getMessage());
       System.out.flush();
      return ret;
   } // logCommand(int)

/** Log of last bad command execution. 
 * 
 *  When this method is called immediately after a command method it will 
 *  log the command on <code>System.out</code> if and only if the execution
 *  failed (with a negative return code). In that case it will delegate to
 *  {@link #logCommand}.
 *  
 *   @param  ret the return value of the command method; <br /> &nbsp; &nbsp;
 *   &nbsp; &nbsp; e.g. by placing the call as parameter
 *           <code>logIfBad(commandMethod(...))</code>
 *   @return ret (the return value of the command method)
 */
   public int logIfBad(final int ret){
      if (ret >= 0 || uint32ret[lastCmd]) return ret;
      return logCommand(ret);
   } // logBadCommand(int);
  

/** Implementation of two parameter (standard) commands
 *     
 *  @param cmd the command number 
 *  @param p1 first parameter, mostly GPIO number
 *  @param p2 optional second parameter
 *  @return commands return value &gt;= 0; or error number  (&lt;0)
 */
   public int stdCmd(final int cmd, final int p1, final int p2){
      lastP1 = p1; lastP2 = p2; lastCmd = cmd; 
      cmdExecStage = 0; lastException = null;
      if (cmd < 0 || cmd > 117) return PI_CMD_BAD;
      if (p1Kind[cmd] == GPIO) { // a Command for a specific IO port
        if (p1 < 0 || p1 > 53) return PI_BAD_GPIO;
        if (cmd == PI_CMD_MODES && (p2 < 0 || p2 > 7)) return PI_BAD_MODE;
      } // a Command for a specific IO port
      
      if (cmd == PI_CMD_PADS || cmd == PI_CMD_PADG) { // pad commands
         if (p1 < 0 || p1 > 2) return PI_BAD_PAD;
         if (cmd == PI_CMD_PADS 
                          && (p2 < 1 || p2 > 16)) return  PI_BAD_STRENGTH; 
      }  // pad commands
      
      Arrays.fill(command, (byte) 0); // init all 0
      command[0] = (byte)cmd;
      command[4] = (byte)p1;
      if ((p1 & 0xFFFFFF00) != 0) {
         command[5] = (byte)(p1 >> 8);
         command[6] = (byte)(p1 >> 16);
         command[7] = (byte)(p1 >> 24);
      }
      command[8] = (byte)p2;
      if ((p2 & 0xFFFFFF00) != 0) {
         command[ 9] = (byte)(p2 >> 8);
         command[10] = (byte)(p2 >> 16);
         command[11] = (byte)(p2 >> 24);
      }
      int ret = 0;
      cmdExecStage = 1;
      try {
         sockOut.write(command, 0, 16);
         try {
            int bRead = sockIn.available();
            bRead = sockIn.read(response, 0, 16);
            cmdExecStage = 16;
            ret = bRead != 16 ?  PI_SOCK_READ_LEN
                       : response[12] | response[13] << 8
                 | response[14] << 16 | response[15] << 24;
         } catch (IOException e) {
            // TODO Auto-generated catch block
            lastException = e;
            ret =  PI_SOCK_READ_FAILED;
         }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         lastException = e;
         ret = PI_SOCK_WRIT_FAILED;
      }
      return ret;
   } // stdCmd(3*int)
   
   public int setMode(int port, int mode){
      return stdCmd(PI_CMD_MODES, port, mode);
   } // gpioSetMode(2 * int) 
   
   public int setPadS(int pad, int mA){
      return stdCmd(PI_CMD_PADS, pad, mA);
   } // gpioSetMode(2 * int) 

    
   public int pinWrit(int port, boolean val){
      if (port < 0 || port > 28) return PI_BAD_GPIO;
      return stdCmd(PI_CMD_WRITE, port, val ? 1 : 0);
   }  // gpioWrite(int, boolean) 

/** Periodic delay. <br />
 *  <br />
 *  This method delays the calling thread for the given number of 
 *  milliseconds relative to its last call. Hence, it is able to implement
 *  strong long term periodicity. Called 86400 times with 1000 will end
 *  one day later, e.g..<br />
 *  <br />
 *  If this call's (corrected) relative end time would be in the past
 *  the current delay will be relative to now. In that case an existing long
 *  term periodicity would be destroyed. This would happen if other threads
 *  or processes hinder the wake up of this thread for more than 
 *  millies ms. <br />
 *  <br />
 *  @param millies the number of ms to delay relativ to the last call
 */  
   public void thrDelay(int millies){
      if (millies < 1) return; // must be positive
      long now = java.lang.System.currentTimeMillis();
      Long lastTick = lastThTick.get();
      if (lastTick == null) { //
         lastTick = new Long(now + millies);
      } else { // have threads last tick
         long target = lastTick + millies;
         if (target > now) {
            millies = (int)(target - now); // keep exact period
            lastTick = target;
         } else lastTick = now + millies;  
      } //  have threads last tick
      lastThTick.set(lastTick);
      try {
        Thread.sleep(millies);
     } catch (InterruptedException e) { } // ignore exception
   } // gpioDelay(int)
   
      
   static public final ThreadLocal<Long> lastThTick = new ThreadLocal<>();
   //  long lastTick = 0; // java.lang.System.currentTimeMillis()
   //  long now;
 
} // ClientPigpiod (21.05.2019)
