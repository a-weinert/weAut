package de.weAut.tests;

import jpigpio.PigpioSocket; // pigpio Java interface by Neil Kolban  

import de.weAut.PiUtil;     // Raspberry Pi handling utilities (IO lock)
import de.weAut.ClientPigpiod;
import de.weAut.Pi3Usage;  // Raspberry Pi3 handling

/** <b>Port of pigpiod test and demo program rdGnPiGpioDBlink from C to Java</b>.<br />
 *  <br />
 *  This port uses the PigpioSocket interface Jpigdio by Neil Kolban; see 
  <a href="https://github.com/nkolban/jpigpio">github.com/nkolban/jpigpio</a>.
 *  This library uses an own C part with Java native interface (JNI) even
 *  when using pigpiod's socket interface.<br />
 *  We deprecate this approach as sheer overhead, recommending the 100% pure
 *  Java approach implemented by {@link ClientPigpiod} and with equal
 *  functionality demonstrated by {@link RdGnPiGpioDBlink}.
 *  <br />
 *  Comment excerpt of the original/ported C source file: <pre>
  A fifth program for Raspberry's GPIO pins

  It uses two/three pins as output assuming two LEDs connected to as H=on
  Pi 1  / Pi 3       Pin
  GPIO17/ 17 : red    11
  GPIO21/ 27 : green  13
  GPIO25     : yellow 22  (since Revision 45)
  This program forces application singleton and may be used as service.

  Its functions are the same as rdGnBlinkBlink (even sharing the lockFile)
  except for using the pigpioD library. Our makefiles define MCU and 
  PLATFORM as make variables and macros. So we could make the GPIO pin and
  address assignment automatically in the C programmes.
</pre><br />  
 *  In a Java program (i.e. in a class like this one) one has to decide the
 *  target by implementing either {@link de.weAut.Pi3Usage} or 
 *  {@link de.weAut.Pi1Usage}; other targets might be added later. <br />
 *  <br />
 *  Copyright  &copy;  2019   Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 26 $ ($Date: 2019-05-31 15:33:23 +0200 (Fr, 31 Mai 2019) $)
 */
// so far:   V. 18  (17.05.2019) :  new
//           V. 20  (19.05.2019) :  minor corrections
public class RdGnJPiGpioDBlink implements PiUtil, Pi3Usage {
	
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
	     // we consider GPIO input pins as inactive and, in most cases, safe.
        pigpio.gpioSetMode(LEDrd, GPIO_INP);
        pigpio.gpioSetMode(LEDgn, GPIO_INP);
        pigpio.gpioSetMode(LEDye, GPIO_INP);
        pigpio.gpioTerminate();
      } catch (Throwable e) {
        e.printStackTrace();
      }
	  closeLock();
    })); // shutdownHook.run()
    
    runOn = openLock(null, true) == 0;
    if (! runOn) return;
    //  System.out.println("RdGnJPiGpioDBlink got lock");

    try {
      pigpio = new PigpioSocket(null, 8888);
      pigpio.gpioInitialize();
      pigpio.gpioSetMode(LEDrd, GPIO_OUT);
      pigpio.gpioSetMode(LEDgn, GPIO_OUT);
      pigpio.gpioSetMode(LEDye, GPIO_OUT);
      boolean yLd = true;
	  for(;runOn;) {                  // red green time/state  yellow
		  pigpio.gpioWrite(LEDrd, HI);  // on
		  pigpio.gpioDelay(200);        //          200 ms red
	     yLd = !yLd;                   //                      toggle
	     pigpio.gpioWrite(LEDye, yLd);
	     pigpio.gpioWrite(LEDgn, HI);  //      on
	     pigpio.gpioDelay(100);        //          100 ms both
	     pigpio.gpioWrite(LEDrd, LO);  // off
	     pigpio.gpioDelay(100);        //          100 ms green
	     pigpio.gpioWrite(LEDgn, LO);  //     off
	     pigpio.gpioDelay(200);        //          200 ms dark
      } // for endless
	} catch (Throwable e) {
	   e.printStackTrace();
	}
  } // doIt()
} // RdGnJPiGpioDBlink