package de.weAut.tests;

import de.weAut.Pi3Usage;             // Raspberry Pi / Pi3 handling
import jpigpio.JPigpio;        // pigpio Java interface by Neil Kolban  
import jpigpio.PigpioSocket;   // the socket variant (the only one used)


/** Port of pigpiod test and demo program rdGnPiGpioDBlink.c to Java.
 * 
 *  The port uses the PigpioSocket interface by Neil Kolban.
 * 
 *  The comment of the C source file: <br />
  A fifth program for Raspberry's GPIO pins

  Rev. $Revision: 18 $  $Date: 2019-05-17 14:18:26 +0200 (Fr, 17 Mai 2019) $

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
  
 *  In a Java program (i.e. class like this one) one has to decide the
 *  target by implementing either Pi3Usage or Pi1Usage; other targets
 *  might be added later.
 */
public class RdGnJPiGpioDBlink implements Pi3Usage {
	
/** The LEDs to blink.  */
  public final int LEDrd = PIN11;
  public final int LEDgn = PIN13;
  public final int LEDye = PIN22;
  
  PigpioSocket pigpio;
  boolean runOn;

/** The application start.
 *  
 *  Will blink with three LEDs in an endless loop (in ::doIt()). 
 *  Can be stopped by signal (cntlC), kill command and the like.
 *  @param args start parameters, not used (yet).
 */
  public static void main(String[] args){
   System.out.println("RdGnJPiGpioDBlink start");
   new RdGnJPiGpioDBlink().doIt();
  } // main(String[])

/** The application's work.
 *  
 *  When no (startup) error occurs this will run in an endless loop.
 */
  public void doIt(){
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
     runOn = false;
     System.out.println("RdGnJPiGpioDBlink shutdown");
	  if (pigpio != null) try {
        pigpio.gpioSetMode(LEDrd, JPigpio.PI_INPUT);
        pigpio.gpioSetMode(LEDrd, JPigpio.PI_INPUT);
        pigpio.gpioSetMode(LEDye, JPigpio.PI_INPUT);
        pigpio.gpioTerminate();
      } catch (Throwable e) {
        e.printStackTrace();
      }
	  closeLock();
    })); // shutdownHook.run()
    
    runOn = openLock(null, true);
    if (! runOn) return;
    System.out.println("RdGnJPiGpioDBlink got lock");

    try {
      pigpio = new PigpioSocket(null, 8888);
      pigpio.gpioInitialize();
      pigpio.gpioSetMode(LEDrd, JPigpio.PI_OUTPUT);
      pigpio.gpioSetMode(LEDrd, JPigpio.PI_OUTPUT);
      pigpio.gpioSetMode(LEDye, JPigpio.PI_OUTPUT);
      boolean yLd = true;
	  for(;runOn;) {                    // red green time/state  yellow
		  pigpio.gpioWrite(LEDrd, true);  // on
		  pigpio.gpioDelay(200);          //          200 ms red
	     yLd = !yLd;                     //                      toggle
	     pigpio.gpioWrite(LEDye, yLd);
	     pigpio.gpioWrite(LEDgn, true);  //      on
	     pigpio.gpioDelay(100);          //          100 ms both
	     pigpio.gpioWrite(LEDrd, false); // off
	     pigpio.gpioDelay(100);          //          100 ms green
	     pigpio.gpioWrite(LEDgn, false); //     off
	     pigpio.gpioDelay(200);          //          200 ms dark
      } // for endless
	} catch (Throwable e) {
	   e.printStackTrace();
	}
  } // doIt()
} // RdGnJPiGpioDBlink