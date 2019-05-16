package de.weAut.tests;

import de.weAut.Pi3Usage;             // Raspberry Pi / Pi3 handling
import java.util.concurrent.TimeUnit; // millisecons etc.

import jpigpio.JPigpio;        // pigpio Java interface by Neil Kolban  
import jpigpio.PigpioSocket;   // the socket variant (the only one used)


/** Port of pigpiod test program rdGnPiGpioDBlink.c to Java.
 * 
 *  The port uses the PigpioSocket interface by Neil Kolban.
 * 
 *  The comment of the C source file: <br />
  A fifth program for Raspberry's GPIO pins

  Rev. $Revision: 17 $  $Date: 2019-05-15 21:51:04 +0200 (Mi, 15 Mai 2019) $

  Copyright  (c)  2017   Albrecht Weinert <br />
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
  
 * The Java port mirrors the automatic C pin defines for P3 40 pin header,
 * only.
 */
public class RdGnJPiGpioDBlink implements Pi3Usage {
	
/** The LEDs to blink.  */
  public final int LEDrd = PIN11;
  public final int LEDgn = PIN13;
  public final int LEDye = PIN22;
  
  PigpioSocket pigpio;
  boolean runOn;
  
  Process lockProcess;
  
/** Open and lock lock file. 
 * 
 *  @param lckPiGpioFil if not null or empty use this path instead of the
 *                       default one ({@link lckPiGpioPth})
 *  @param perr when true protocol errors (on System.out as of now)                    
 */
  boolean openLock(final String lckPiGpioFil, final boolean perr){
     
   final String lckPiGpio = lckPiGpioFil != null &&  lckPiGpioFil.length() > 3
		   ? lckPiGpioFil : lckPiGpioPth;
   
   try {
      lockProcess = Runtime.getRuntime().exec("justLock " + lckPiGpio); 
    //  if (perr) System.out.println("justLock started " 
      //                                + lockProcess.isAlive());
       // little delay needed here 
      lockProcess.waitFor(10, TimeUnit.MILLISECONDS);
      if (! (lockProcess.isAlive())) {
         int exV = lockProcess.exitValue();
         if (perr) System.out.println("justLock has stopped " + exV);
         return false;
      } // not running lock process
   } catch (Throwable ex) {
      if (perr) System.out.println("can't run justLock " + lckPiGpio);
      ex.printStackTrace();
      return false;
   }
   return true;
  } // openLockString, boolean)

/**  Unlock the lock file. */
  void closeLock(){
      if (lockProcess != null) lockProcess.destroy();
  } // closeLock()


/** The application start.
 *  
 *  Will blink with  three LEDs until killed.
 *  @param args Start parameters, not used (yet).
 */
  public static void main(String[] args){
   System.out.println("RdGnJPiGpioDBlink start");
   new RdGnJPiGpioDBlink().doIt();
  } // main(String[])

/** The applications work.
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