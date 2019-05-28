package de.weAut.tests;

import de.weAut.PiGpioDdefs; // pigpio library defines
import de.weAut.PiUtil;     // Raspberry Pi handling utilities (IO lock)
import de.weAut.ClientPigpiod;
import de.weAut.Pi3Usage;  // Raspberry Pi3 handling

import java.io.IOException;

/** <b>Port of pigpiod test and demo program rdGnPiGpioDBlink from C to Java</b>.<br />
 *  <br />
 *  This port uses Joan N.N's pigpio library's 
 *  <a href="http://abyz.me.uk/rpi/pigpio/sif.html">socket interface</a> 
 *  directly. <br />
 *  <br />
 *  The comment of the C source file: <br /><pre>
  A fifth program for Raspberry's GPIO pins

  Rev. $Revision: 25 $  $Date: 2019-05-28 13:21:30 +0200 (Di, 28 Mai 2019) $

  Copyright  (c)  2019   Albrecht Weinert <br />
  weinert-automation.de      a-weinert.de

  It uses two/three pins as output assuming two LEDs connected to as H=on
  Pi 1  / Pi 3       Pin
  GPIO17/ 17 : red    11
  GPIO21/ 27 : green  13
  GPIO25     : yellow 22  (since Revision 45)
  This program forces application singleton and may be used as service.

  Its functions are the same as rdGnBlinkBlink (even sharing the lockFile)
  except for using the pigpioD library. Our makefiles define
  MCU and PLATFORM as make variables and macros. So we could make the
  GPIO pin and address assignment automatically in the C programmes.
</pre><br />  
 *  In a Java program (i.e. class like this one) one has to decide the
 *  target by implementing either Pi3Usage or Pi1Usage; other targets
 *  might be added later.<br />
 *  <br />
 *  Copyright  &copy;  2019   Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 25 $ ($Date: 2019-05-28 13:21:30 +0200 (Di, 28 Mai 2019) $)
 */
// so far:   V. 21  (21.05.2019) :  new, minimal functionality
//           V. 21  (21.05.2019) :  

public class RdGnPiGpioDBlink implements PiUtil, Pi3Usage, PiGpioDdefs {

/** The LEDs to blink.  */
  public final int LEDrd = PIN11;
  public final int LEDgn = PIN13;
  public final int LEDye = PIN22;
  
  ClientPigpiod  pI; 
  boolean runOn;

/** The application start.
*  
*  Will blink with three LEDs in an endless loop (in ::doIt()). 
*  Can be stopped by signal (cntlC), kill command and the like.
*  @param args start parameters, not used (yet).
*/
  public static void main(String[] args){
     System.out.println("RdGnPiGpioDBlink start");
     new RdGnPiGpioDBlink().doIt();
  } // main(String[])

/** The application's work.
*  
*  When no (startup) error occurs this will run in an endless loop.
*/
  public void doIt(){
     Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        runOn = false;
        System.out.println("\nRdGnPiGpioDBlink shutdown");
        if (pI != null){
            // we consider GPIO input pins as inactive and, in most cases, safe.
           pI.setMode(LEDrd, GPIO_INP);
           pI.setMode(LEDgn, GPIO_INP);
           pI.setMode(LEDye, GPIO_INP);
           try {
              pI.disconnect();
           } catch (IOException e) { } 

           // pigpio.gpioTerminate();
        } // catch (Throwable e) {
          // e.printStackTrace();
     //   }
        closeLock();
     })); // shutdownHook.run()

     runOn = openLock(null, true) == 0;
     if (! runOn) return;
     //  System.out.println("RdGnJPiGpioDBlink got lock");
     
     try {
      pI = new ClientPigpiod(); // local connect
   } catch (IOException e1) {
      e1.printStackTrace();
      return; // can't open socket
   }  // null, 8888, 2
   System.out.println(" TEST set mode output rd gn ye 14 mA");
   pI.logCommand(pI.setMode(LEDrd, GPIO_OUT));
   pI.logCommand(pI.setMode(LEDgn, GPIO_OUT));
   pI.logCommand(pI.setPullR(LEDgn, PI_PUD_DOWN));
   pI.logCommand(pI.setMode(LEDye, GPIO_OUT));
   pI.logCommand(pI.setPadS(0, 14));
   boolean yLd = true;
   System.out.println(" TEST start endless loop");
   for(;runOn;) {                        // red green time/state  yellow
      pI.logIfBad(pI.pinWrit(LEDrd, HI)); // on
      thrDelay(200);                      //          200 ms red
      yLd = !yLd;                         //                      toggle
      pI.logIfBad(pI.pinWrit(LEDye, yLd));
      pI.logIfBad(pI.pinWrit(LEDgn, HI)); //      on
      thrDelay(100);                      //          100 ms both
      pI.logIfBad(pI.pinWrit(LEDrd, LO)); // off
      thrDelay(100);                      //          100 ms green
      pI.logIfBad(pI.pinWrit(LEDgn, LO)); //     off
      thrDelay(200);                      //          200 ms dark
   } // for endless
  } // doIt()
} // RdGnPiGpioDBlink
