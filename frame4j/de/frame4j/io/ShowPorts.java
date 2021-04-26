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

import java.io.PrintWriter;
import java.util.Enumeration;
import com.fazecast.jSerialComm.SerialPort;
import de.frame4j.io.SerNimpl;
import de.frame4j.io.WinDoesIt;
import de.frame4j.util.ComVar;

/** <b>Show computer interfaces available to Java applications</b>. <br />
 *  <br />
 *  This application determines and lists the serial and (if supplied by the
 *  underlying libraries) parallel computer interfaces available to
 *  Java and Frame4J applications. Their state is displayed, too. <br />
 *  <br />
 *  Hereby the successful installation of any combination of the following 
 *  can be tested / verified:<ul>
 *  <li> SUN's Java Comm extensions (commApi, not available for Windows!),</li>
 *  <li> the RXTX extensions or of </li>
 *  <li> com.fazecast.jSerialComm </li>
 *  <li> Frame4J'{@link SerNimpl}- / {@link WinDoesIt} implementation <br />
 *       As of January 2016 this solution is (regrettably) still serial only
 *       and  Windows 23&amp;64 only. On the other hand this is the most 
 *       stable and most portable solution: Java4..8, Windows NT, 
 *       Server2003, XP, 7, Server2008 and more w/o modifications or 
 *       adaption. *1) </li></ul>
 *  <br />
 *  <br />
 *  Hint: This application does run without of those extensions correctly
 *  installed. In those cases useful error reports are generated (missing .dll
 *  e.g.). <br />
 *  A re-translation and re-deployment of this application requires the
 *  (correct) installation of the the extensions mentioned / supported. <br />
 *  <br />
 *  Remark *1: To be correct, there was an adaption to handle two digit com 
 *  numbers and their ill syntax (\\.\com21) as &mdash; with the disappearance
 *  of the real ports com1 .. com4 &mdash; they got usual with USB2serial 
 *  converters.<br />
 *  <br />
 *  <br />
 *  Copyright 1998 - 2003, 2007 - 2013, 2015  &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 32 $ ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)
 *  @see      de.frame4j.io
 *  @see      de.frame4j.io
 */
 // Bisher    V00.00 (11.04.1999) :  new
 //           V00.01 (15.06.1999) :  adapted to comm-API V20
 //           V02.20 (05.12.2006) :  Ext. import weg > fully qual., Mld
 //           V02.21 (06.12.2006) :  RXTX and javax
 //           V02.23 (11.04.2007) :  w/o System.exit for sake of JMX
 //           V.022  (26.08.2012) :  RXTX, Win 64Bit dll, Java8
 //           V.98+  (10.10.2013) :  Windows' bug on comXY handled
 //           V.129+ (12.01.2016) :  start try porting 2 Linux&OracleJava8
 //           V.145+ (15.08.2016) :  moved into Frame4J: "" -> de.frame4j.io
 // Last changed by $Author: albrecht $ at $Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $

public class ShowPorts {

   int anzPortsJava;  // number of interfaces detected by gnu.io (javax)
   int anzPortsSerN;  // number detected by native implementation (Frame4J)
   PrintWriter log;
   boolean notWindows; // if set true: definitely not Windows

/** Start the application. <br />
 *  <br />
 *  This method makes an object of this class and calls {@link #doIt()}.<br />
 *  <br />
 *  @param args start parameters  (irrelevant)
 */
   public static void main(String[] args){
      ComVar.RUNTIME.exit(new de.frame4j.io.ShowPorts().doIt());
   }  // main  (looks de.frame4j.util.App based, but isn't)

/** Working method. <br />
 *  <br />
 *  @return 0 other values are not yet used
 */
   public int doIt(){
      log = new PrintWriter(System.out, true);
      
      String comPrf1 = null;
      try {
         comPrf1 = System.getProperty("os.name");
      } catch (Exception e) {}
      if (comPrf1 != null && comPrf1.length() > 6         // definitely 
         && ! comPrf1.startsWith("Windows")) notWindows = true; //  no Windows

      log.println ("\n\n  ShowPorts (.java) V.$Revision: 32 $"
      + " ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)\n"
                     + "  Copyright 1998 - 2007, 2016  Albrecht Weinert, "
      + " (a-weinert.de)");
      

   //   if (anzPortsSerN == 0) {
      log.println ("\n  gnu.io - extension scans:" +
                   "\n  ========================\n");
      try {
         new JavaxHlp().testIt();
      } catch (Throwable ex) {
         log.println("   failed: " + ex.getMessage()
                   + "\n   installed extension missing: comm.jar (javax)\n");
      }
      log.println(".\n"); //---------------- gnu.io \\ SerNimpl (native) ---

      log.println ("\n  com.fazecast.jSerialComm scans:" +
                   "\n  ==============================\n"); 
      try {
        SerialPort port = null;
        SerialPort[] ports = 
                          com.fazecast.jSerialComm.SerialPort.getCommPorts();
        for (int i = 0; i < ports.length; ++i) {
           port = ports[i];
           boolean openedSuccessfully = port.openPort();
           log.println((openedSuccessfully ? " avail "
                                           : " busy  "  )   
                 + i + ": " + port.getSystemPortName() + "/"
                  + port.getDescriptivePortName() + " - " 
                                                + port.getPortDescription());
           if (openedSuccessfully) port.closePort();
         } // for
      } catch (Throwable ex) {
         log.println("   failed: " + ex.getMessage()
                   + "\n   installed extension missing: comm.jar (javax)\n");
      }

         
         
      log.println(".\n"); //----------------  com.fazecast.jSerialComm  ---
      
      log.println ("\n  de.frame4j.io.SerNimpl / native lib  scans:" +
                   "\n  ==========================================\n");
                
      String comPrf2;
      int comNum = 1;
      if (notWindows) {
         comPrf2 = comPrf1 = "/dev/ttyS";
         comNum = 0;
      } else {
         comPrf1 = "com";
         comPrf2 = "\\\\.\\com";
      }

      try {
         SerNimpl serNimpl = new SerNimpl();
         int missCnt = 0;
         for(;;) {
            String comPort = comPrf1 + comNum;
            try {
               serNimpl.openSerial(comPort);
               ++ anzPortsSerN;
               log.print("\nPort " + serNimpl.toString());
               serNimpl.close();
               missCnt = 0;
            } catch (Exception ex) {
               if (SerNimpl.errNoRetLast == 5) { // not accessible / in use
                  log.print("\nPort " + comPort + " :  no access / in use");
                  ++ anzPortsSerN;
               } else {
                 if (SerNimpl.errNoRetLast !=  2) // 2 : not found
                   log.println(comPort + " got: " + ex.getMessage() );
                 if (++missCnt > 39) break;
               }
            }
            if (++comNum == 10) comPrf1 = comPrf2;
         } // for
      } catch (Throwable ex) {
         log.println("   failed: " + ex.getMessage()
                            + "\n   lib missing (e.g. bsDoesItNative.dll)\n" +
          "\n  Hint: native implementation probably for Windows only" +
          "\n  used by other applications are not shown.");
      }

      //----------------------------------------------
      log.println(".\n\n  " + anzPortsJava +  " Ports (javax) & "
         + anzPortsSerN  + " Ports (SerNimpl)\n");
      return 0;
   } // doIt()


/** Helper class for javax-comm-API test. <br />
 *  <br />
 *  This extra class is necessary just to determine if classes respectively
 *  .jar files are available. Having put these requirements into the
 *  embedding main class would impede the application's start in case of
 *  missing extensions.<br />
 *  <br />
 *  Autor, Copyright and Version see enclosing class.<br />
 *  <br />
 *  @author weinert<br /
 */
   class JavaxHlp {
      Enumeration<gnu.io.CommPortIdentifier> portList;

/** Use and test the gnu.io API. <br /> */
      @SuppressWarnings("unchecked") public void testIt(){
      try {
         portList = gnu.io.CommPortIdentifier.getPortIdentifiers(); // @su
         gnu.io.CommPortIdentifier portId = null;
         boolean isOwned = false;
         boolean isSerial= false;
         boolean isParallel=false;
         gnu.io.SerialPort serialPort     = null;
         gnu.io.ParallelPort parallelPort = null;

         while (portList.hasMoreElements()) {
            portId = portList.nextElement();
            isOwned = portId.isCurrentlyOwned();  // is owned may not work
            isSerial= portId.getPortType() 
                                    == gnu.io.CommPortIdentifier.PORT_SERIAL;
            isParallel= portId.getPortType()
                                  == gnu.io.CommPortIdentifier.PORT_PARALLEL;
            log.println ("\nPort " + portId.getName() + " "
                  + ( isOwned ? "in use ("+portId.getCurrentOwner()+") "
                              :"free " )
                  + (isSerial ? "serial " : (isParallel ? "parallel "
                                                          : "special comm. "))
                  + "interface");
            ++anzPortsJava;
            if (isSerial && !isOwned) { // free (?) serial interface
               try {
                  serialPort =
                     (gnu.io.SerialPort) portId.open("ShowPorts", 2000);
                  log.println ( serialPort.getBaudRate() + " Baud, "
                        +  serialPort.getDataBits() + " data bits, " +
                           serialPort.getStopBits() + " stop bits, " +
                           serialPort.getParity() +   " par(code)\n" +
                           " CD = " + serialPort.isCD() +
                           " CTS = " + serialPort.isCTS() +
                           " DSR = " + serialPort.isDSR() +
                           " DTR = " + serialPort.isDTR() +
                           " RI = " + serialPort.isRI() + ".");

                  serialPort.close();
               } catch (gnu.io.PortInUseException e) {
                  log.println (e.getMessage());
               }
            } // isSerial && !isOwned

            if (isParallel && !isOwned) { // free parallel
               try {
                  parallelPort =
                     (gnu.io.ParallelPort) portId.open("ShowPorts", 2000);
                  log.println (
                      " PaperOut = " + parallelPort.isPaperOut() +
                      " PrinterBusy = " + parallelPort.isPrinterBusy() +
                      " PrinterError = " + parallelPort.isPrinterError()
                      + ".");
                  parallelPort.close();
               } catch (gnu.io.PortInUseException e) {
                  log.println (e.getMessage());
               }
            } // isParallel && !isOwned

         }  // while

      } catch (Exception ex) {
         log.println("   failed: " + ex.getMessage());
      }

      } // testIt
   } // class JavaxHlp

} // class ShowPorts (04.2003, 12.2006, 01.2010, 08.2013, 08.2016)

