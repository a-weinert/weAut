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
package de.frame4j.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import de.frame4j.util.App;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;
import static de.frame4j.util.ComVar.EMPTY_STRING;

/** <b>Base for a TCP/IP client</b>. <br />
 *  <br />
 *  An object of this class is a tiny (wee) client. For a given server host
 *  and TCP port a point to point (P2P-) connection can be handled and
 *  multiply opened and re-used.<br />
 *  <br />
 *  A short exchange initiated by this client is supported:<br /> &nbsp;
 *    n* (client sends, server responds), client closes.<br />
 *  <br />
 *  Basically the size and functionality of the client is not limited. An
 *  object of this class is also the base for the minimum useful client 
 *  functionality &mdash; hence  &quot;ClientLil&quot; (Scottish 
 *  diminutiv).<br />
 *  <br />
 *  Usage of ClientLil may look so:<pre><code>
 &nbsp; import de.frame4j.net.ClientLil;
 &nbsp; // ......
 &nbsp;  
 &nbsp;   ClientLil eventClientLil = new ClientLil();
 &nbsp;   String   eventHost;
 &nbsp;   int      eventPort;
 &nbsp;   int      timeout;
 &nbsp;   String   eventText;
 &nbsp;  
 &nbsp; // .......
 *  
 &nbsp;  
 &nbsp;   if (eventClientLil == null) try {
 &nbsp;      eventClientLil.setHost(eventHost); 
 &nbsp;      eventClientLil.setPort(eventPort); 
 &nbsp;      eventClientLil.setTimeOut(tiemOut); 
 &nbsp;      eventClientLil.connect(true);  
 &nbsp;   } catch (Exception e) {
 &nbsp;      log.println("  ///   warning : event server " + eventHost
 &nbsp;                    + " : " + e);
 &nbsp;   }
 *
 &nbsp;   // while (.......
 &nbsp;  
 &nbsp;    String answ  = null;
 &nbsp;    Exception ex = null;
 &nbsp;    try {
 &nbsp;      answ = eventClientLil.use(eventText); 
 &nbsp;      /// ..... eventClientLil.use(...... 
 &nbsp;      eventClientLil.disconnect();
 &nbsp;    } catch (Exception e) { ex = e; }
 &nbsp;    log.println("\n   /// eventserver &lt; " + eventText
 &nbsp;              + "\n   /// answered    &gt; " + answ 
 &nbsp;              + "\n   /// problems    : " + ex + "\n");
    </pre></code>
 *  
 *  As test client for this class is also an application. 
 *  For usage see:<br /> &nbsp;
 *  java de.frame4j.net.ClientLil -?<br />&nbsp;
 *  java de.frame4j.net.ClientLil -en -?<br />
 *  <br />
 *  Examples:<br /> &nbsp;
 *  java de.frame4j.net.ClientLil pd310s -p 7 &quot;try the echo&quot;<br />
 *  java de.frame4j.net.ClientLil -telnet 192.168.89.33 -v<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2002 - 2007, 2012 &nbsp; Albrecht Weinert<br />  
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 33 $ ($Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $)
 */
 // so far:   V00.00 (20.09.2002 14:04) :  new
 //           V02.00 (24.04.2003 16:54) :  CVS Eclipse
 //           V02.21 (01.10.2005 19:10) :  usePingPong() 
 //           V02.23 (15.08.2006 11:35) :  main() 
 //           V.109- (19.07.2009 11:35) :  SVN, Frame4J
 //           V.o65+ (22.05.2012 17:13) :  extends App (towards Telnet)
 //           V.102+ (11.04.2014 10:56) :  usePingPong() is back

@MinDoc(
      copyright = "Copyright 2005, 2014  A. Weinert",
      author    = "Albrecht Weinert",
      version   = "V.$Revision: 33 $",
      lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
   // lastModifiedBy = "$Author: albrecht $",
      usage   = "start as Java application (-? for help)",  
      purpose = "a small IP client"
) public class ClientLil {

/** The server's (the host's) name. <br />
 *  <br />
 *  Name of a computer reachable via TCP/IP.<br />
 *  <br />
 *  default: null
 */
   protected String host;

/** The server's (the host's) name. <br />
 *  <br />
 *  Name of a computer reachable via TCP/IP.<br />
 *  <br />
 *  default: null
 */
   public final String getHost(){ return host; }

/** Set the server's or host's name (and address). <br />
 *  <br />
 *  The name of a computer reachable via TCP/IP will be set for this Client.
 *  It will be stripped from  leading and  trailing white spaces before use.
 *  Null or (then) empty will default to localhost.<br />
 *  This setting will only be effective with the
 *  next {@link #connect connecting}.<br />
 *  <br />
 *  @param host the server
 *  @throws UnknownHostException problems when resolving the server
 */
   public final void setHost(final CharSequence host)
                                   throws UnknownHostException {
      this.host = TextHelper.trimUq(host, "localhost");
      inetAddress = TextHelper.areEqual("localhost", this.host, true) 
                              ?  InetAddress.getLocalHost()
                              :  InetAddress.getByName(this.host);
   } // setHost(CharSequence )


/** The server's (the host's) port. <br />
 *  <br />
 *  The server port's number (&gt;0) to connect to.<br />
 *  <br />
 *  default: 7 (echo)
 */
   public final int getPort(){ return port; }

/** The server's (the host's) port. <br />
 *  <br />
 *  The server port's number (&gt;0) to connect to.<br />
 *  <br />
 *  This setting will only be effective with the
 *  next {@link #connect connecting}.<br />
 *  <br />
 *  @param port 0..65535 new port number; other values act as 7 (default)
 */
  public final void setPort(int port){
   this.port = port < 0 || port > 65535 ? 7 : port;
  } // setPort(int)
  
 
/** The server's (the host's) port. <br />
 *  <br />
 *  @see #getPort()
 */
   protected int port = 7;
   
/** Maximum wait time for the server's (host's) responses. <br />
 *  <br />
 *  @see #getTimeOut
 *  @see #setTimeOut
 */
   protected int timeOut = 30;

/** Maximum wait time for the server's (host's) responses. <br />
 *  <br />
 *  A value &gt; 0 is the maximum waiting time in seconds.<br />
 *  A value = 0 means unlimited waiting.<br />
 *  A value &lt; 0 is taken a 30s (default).
 *  <br />
 *  Is set by {@link #setTimeOut}.<br />
 *  <br />
 *  default: 30
 */
   public final int getTimeOut(){ return timeOut; }

/** Maximum wait time for the server's (host's) responses. <br />
 *  <br />
 *  This setting will only be effective with the
 *  next {@link #connect connecting}.
 *  @see #getTimeOut
 *  @param timeOut &gt;= 0 : the new time out value in ms;
 *                  &lt; 0 : defaults to 30s
 */
   public final void setTimeOut(int timeOut){
      this.timeOut = timeOut < 0 ? 30: timeOut;  
   } // setTimeOut(int)


/** Encoding for textual input and output. <br />
 *  <br />
 *  @see #getEncoding
 *  @see #setEncoding
 */
   protected String encoding;

/** Encoding for textual input and output. <br />
 *  <br />
 *  Is set by {@link #setEncoding}.<br />
 *  <br />
 *  default: null (means installation's default encoding). <br />
 */
   public final String getEncoding() { return encoding;  }

/** Encoding for textual input and output. <br />
 *  <br />
 *  This setting will only be effective with the next
 *  {@link #connect connecting}. Allowed values are iso8859-1 (..19), utf-8
 *  etc., see Java documentation. <br />
 *  @see #getEncoding
 *  @param encoding will be stripped from enclosing white space; null or empty
 *         will default to null.
 */
   public final void setEncoding(final CharSequence encoding){
      this.encoding = TextHelper.trimUq(encoding, null);  
   } // setEncoding(CharSequence)
   

/** The number of messages (still) to be accepted from server. <br />  
 * 
 *  Provided for application use. <br />
 *  Intended use: count down on incoming messages and stop when 0. <br />
 *
 *  default: 1 (and set so on {@link #connect(boolean) connect()} if smaller
 */
   public volatile int noOfAccIncMess  = 1;   

/** The host address (as String). <br />
 *  <br />
 *  @return null if not available
 */
   public final String getHostAddress(){
     if (inetAddress == null) return null ;
     return inetAddress.getHostAddress(); 
   } // getHostAddress()

/** The host address. <br />
 *  <br />
 *  Is set whith finally on construction.<br />
 *  default: null
 */
   protected InetAddress inetAddress;
 
/** The socket. <br >
 *  <br />
 *  The actual made connection's socket will be recorded here. Without 
 *  connection or after disconnect the value is null.<br />
 *  <br />
 *  Hint: sock will be provided for applications beyond the requirements 
 *  of this class' methods. For performance sake the access is public.
 *  Application shall not change or this variable or store its value 
 *  elsewhere.<br />
 */
   public volatile Socket sock;

/** The socket input stream. <br >
 *  <br />
 *  The socket's actual connection input is stored here.<br />
 *  Value without connection or after disconnect: null.<br />
 *  <br />
 *  Hint: sockIn will be provided for applications beyond the requirements 
 *  of this class' methods. For performance sake the access is public.
 *  Application shall not change or this variable or store its value 
 *  elsewhere.<br />
 */
   public volatile InputStream sockIn;

/** The socket output stream. <br >
 *  <br />
 *  The socket's actual connection output is stored here.<br />
 *  Value without  connection or after disconnect: null.<br />
 *  <br />
 *  Hint: sockOut will be provided for applications beyond the requirements 
 *  of this class' methods. For performance sake the access is public.
 *  Application shall not change or this variable or store its value 
 *  elsewhere.<br />
 */
   public volatile OutputStream sockOut;
 

/** The socket input as Reader. <br >
 *  <br />
 *  Will be made only on demand.<br />
 */
   public volatile BufferedReader sockReader;


/** The socket output as Writer. <br >
 *  <br />
 *  Will be made only on demand.<br />
 */
   public volatile PrintWriter sockWriter;


/** Connecting. <br />
 *  <br />
 *  A socket connection to the server's  {@link #port} is made. The streams
 *  and optionally the reader will be provided.<br />
 *  <br />
 *  An exception occurring during the proceeding will be forwarded. A partly
 *  built up connection will be destroyed (by {@link #disconnect()} in this
 *  case.<br />
 *  <br>
 *  @param textIO true: make also the Reader {@link #sockReader} and 
 *         Writer {@link #sockWriter}
 *  @throws IOException if the connecting to {@link #host}.{@link #port} fails
 */
   public Socket connect(boolean textIO) throws IOException {
      try {
         if (sock == null) {
            sock = new Socket(inetAddress, port);         
            sock.setSoTimeout(timeOut * 1000);
            sock.setReceiveBufferSize(8000);
            sockIn = sock.getInputStream();
            sockOut = sock.getOutputStream();
         }
         if (textIO) {
            if (encoding == null) {
            sockReader = new BufferedReader(new InputStreamReader(sockIn));
            sockWriter =  new PrintWriter(new BufferedWriter(new
                                       OutputStreamWriter(sockOut)), true);
            } else  {
               sockReader = new BufferedReader(
                                  new InputStreamReader(sockIn, encoding));
               sockWriter =  new PrintWriter(new BufferedWriter(
                         new OutputStreamWriter(sockOut, encoding)), true);
               } 
         }
         if (noOfAccIncMess <= 0) noOfAccIncMess = 1;
         return sock;
      } catch (IOException ex) {
         try {
            disconnect();
         } catch (Exception e){} // silently ignore disconnect exception
         throw ex; // but throw all others
      }
   } // connect

/** Use the connection &mdash; text. <br />
 *  <br />
 *  If not yet done a socket connection to {@link #port} is made by
 *  {@link #connect connect(true)}.<br />
 *  <br />
 *  The text {@code send} if not null will be written with a closing 
 *  line feed (println()) to the server. The response line will then be
 *  waited for (readln()).<br />
 *  <br />
 *  An exception occurring during the proceeding will be forwarded. A partly
 *  built up connection will be destroyed (by {@link #disconnect()} in this
 *  case.<br />
 *  <br>
 *  @param send   text to be send to the server
 *  @throws IOException if the connecting to {@link #host}:{@link #port} fails
 */
   public String use(String send) throws IOException {
      try {
         if (sockReader == null) connect(true);
         if (send != null)
            sockWriter.println(send);
         return sockReader.readLine();
      } catch (IOException ex) {
         try {
            disconnect();
         } catch (Exception e){} // silently
         throw ex;
      }
   } // use(String)


/** Use the connection once or for the last time &mdash; text. <br />
 *  <br />
 *  If a connection was (directly or indirectly via {@link #use(String)}) made
 *  by {@link #connect connect(true)} already, this method delegates to 
 *  {@link #use(String) use(send)} and {@link #disconnect()}.<br />
 *  <br />
 *  Otherwise the text {@code send} will by send (as default encoded 
 *  byte[] buffer) and a text of {@code maxLen} bytes received. If
 *  {@code maxLen} is outside the range  192 .. 30000 it is taken as 4090.<br />
 *  <br />
 *  The connection will be closed. Exceptions on writing or reading will be 
 *  forwarded; Exceptions on closing will be suppressed.<br />
 *  <br />
 *  This method is thought for the (practically frequent) &quot;Send
 *  Receive Throwaway&quot; connections (à la ping-pong or Echo).<br />
 *  <br>
 *  For a simple ping-pong connection use: &nbsp; 
 *   {@code usePingPong(send, 192);}<br />
 *  <br /> 
 *  @param send   text to be send to the server
 *  @param maxLen maximum length in bytes of the answer (192 .. 30000; 
 *                default: 4090) 
 *  @throws IOException on problems with the socket connettion to
 *                        {@link #host}.{@link #port}
 *  @see #use(String)                      
 */
   public String usePingPong(String send, int maxLen) throws IOException {
      if (sock == null) { // simple Variant
         sock = new Socket(inetAddress, port);
         sock.setSoTimeout(timeOut);
         //  sock.setReceiveBufferSize(8000);
         sockIn = sock.getInputStream();
         sockOut = sock.getOutputStream();
         byte[] buf = send.getBytes();
         sockOut.write(buf);
         if (maxLen < 192 || maxLen > 30000) maxLen = 4090;
         if (buf.length < maxLen) buf = new byte[maxLen];
         int br = sockIn.read(buf);
         send =  br > 0 ? new String(buf, 0, br) : EMPTY_STRING;
      } else { // simple variant / already connected, hence use
         send = use(send);   
      } // already connected, hence use
      try {
         disconnect();
      } catch (Exception e){} // silently
      return send;
   } // usePingPong(String, int)

   
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
         sockReader = null;
         sockWriter = null;
      }
   } // disconnect()

/** The (main static) connection's settings as String. <br /> */
   @Override public String toString() {
      return host + ":" + port
      + (encoding == null ? " " : "    " + encoding) + " ";
   } // toString()
   
/** The ClientLil (wee client) as application. <br />
 *  <br />
 *  Execute: <br /> &nbsp;
 *  java de.frame4j.net.ClientLil host [&quot;message for host&quot;]<br />
 *  <br />
 *  Example (Echo): <br /> &nbsp;
 *  java de.frame4j.net.ClientLil pd310s -p 7 &quot;try the echo&quot;<br />
 *  <br />
 *  Example (Time, Daytime, RFC 867): <br /> &nbsp;
 *  java de.frame4j.net.ClientLil pd310s -p 13 <br />
 *  <br />
 *  Example (Quote of the day): <br /> &nbsp;
 *  java de.frame4j.net.ClientLil pd310s -p 17 <br />
 *  <br />
 *  Example (Telnet grudge line mode only): <br /> &nbsp;
 *  java de.frame4j.net.ClientLil -telnet weAut_01_a <br />
 *  <br />
 *  Use <br />
 *   java de.frame4j.net.ClientLil [-en] -?  <br />
 *  for help 
 *  <br />
 *  @param args use  &quot;-?&quot; if in doubt
 */
   static public void main(String[] args){
      new App(args){
         ClientLil clientle;
         volatile String request;  // vo.erg.21.11.12
         
         // to signal application end from every anonymous embedded thread
         void appStopp() { clientle.noOfAccIncMess = 0; stop();  }
         
         @Override protected int doIt(){
            log.println();
            if (verbose) { // version, start time
               log.println();
               log.println(twoLineStartMsg().append('\n'));
            } // optional version, start time 

            clientle = new ClientLil();
            prop.setFields(clientle);
            if (!isSilent()) {
               log.println("  ///  connect : " + clientle);
            }
            
            Thread inpForw =  new Thread() {  // standard input forwarding
               @Override public void run(){
                  try {
                     if (request == null && runFlag) {
                        request = appIO.in.readLine();
                     }

                     while (request != null && runFlag // forward request
                                            && clientle.sockWriter != null) {
                        clientle.sockWriter.println(request);
                        if (clientle.noOfAccIncMess <= 0 || !runFlag) break;
                        request = appIO.in.readLine();
                     } // while forward
                     clientle.disconnect();
                  } catch (Exception e) {
                     e.printStackTrace(log);
                  }
                  clientle.noOfAccIncMess = 0; // signal end
                  appStopp(); 
               } // run()
            }; // new Thread() inpForw

            request = prop.getString("request", null);
            try {
               clientle.connect(true);
               if (request != null) { // first request by start arguments
                  log.println("  ///  request : \"" + request + "\" (1)"); 
               } // log  first request by start arguments

               inpForw.setDaemon(true);
               inpForw.start();
            } catch (Exception e) {
               e.printStackTrace(log);
               return 5;
            }

            try { // main thread is response handling as main thread
               char[] response = new char[2049];
               int readC = clientle.sockReader.read(response, 0, 2048);
               while (readC >= 0 && runFlag) {
                  log.write(response, 0, readC);
                  log.flush();
                  if (--clientle.noOfAccIncMess <= 0 || !runFlag) break;
                  readC = clientle.sockReader.read(response, 0, 2048);
               } // while
            } catch (Exception ex) {
               clientle.noOfAccIncMess = 0; // signal end
            }
            try {
               clientle.disconnect();
            } catch (IOException e) { } 

            if (verbose) log.println(twoLineEndMsg());
            return 0;
         }  // doIt
      }; // anonymous App inheritor
   } // main(String[]) 

} // ClientLil (30.09.2002, 29.10.2007, 28.05.2008, 23.05.2012, 28.06.2012)
