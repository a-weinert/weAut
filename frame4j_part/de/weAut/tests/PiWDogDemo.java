package de.weAut.tests;

import de.weAut.PiUtil;  // Raspberry Pi handling utilities (IO lock, watchdog)

/** <b>Demo (Test) of using Raspberry PI (BCM) watchdog with Java</b>.<br />
 *  <br />
 *  This programme initialises the watchdog,
 *  triggers it 3 times after 4, 8, and 14 s and 
 *  closes it (and the program) after 2 s. <br />
 *  This shall not lead to a reset. If the program was started with option
 *  --boot the times will be 14, 17,  20 s. Here the 17 must lead to a 
 *  reset when the program is not killed early enough. 
 *  <br />
 *  Run by: <pre><code>
 *    java de.weAut.tests.PiWDogDemo [--boot]
 *  </pre></code>
 *  To use the watchdog in Java programs like this one the watchdog's access
 *  rights have to be changed; see the publication
 *  <a href="http://a-weinert.de/pub/raspberry4remoteServices.pdf" 
 *  title="by Albrecht Weinert">Raspberry for remote services</a>.<br />
 *  <br />
 *  Copyright  &copy;  2019   Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 26 $ ($Date: 2019-05-31 15:33:23 +0200 (Fr, 31 Mai 2019) $)
 */
// so far:   V. 25  (27.05.2019) :  new, minimal functionality
//           V. 26  (30.05.2019) :  minor, typo, docu 

public class PiWDogDemo implements PiUtil {

/** Watch dog trigger after delay with log output.
 * 
 *  Helper method.
 *    
 *  @param s delays in s before triggering the watchdog.
 */
  private void testStep(int s) {
     System.out.println("PiWDogDemo trigger WDog after " + s + " s");
    if (s > 15) {
     System.out.println("           will reset the Pi; kill process to aviod"); 
    }
    thrDelay(s * 1000); // s s
    triggerWatchdog();
  } // testStep(int s)


/** The application start.
 *  
 *  Can be stopped by signal (cntlC), kill command and the like.
 *  @param args start parameters, none or --boot
 */
  public static void main(String[] args){
     System.out.println("\nPiWDogDemo start");
     new PiWDogDemo().doIt(args);  // need be an PiUtil object
  } // main(String[])
  

/** The application's work.  */
  public void doIt(String[] args){ 
     Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("\nPiWDogDemo shutdown");
        closeWatchdog();
        closeLock();
     })); // shutdownHook.run()
     
     boolean boot = (args.length >= 1) && "--boot".equals(args[0]);
     
     int err = openLock(null, false);
     if (err != 0) {
        System.out.println("PiWDogDemo getIOlock error: " + errorText(err)
        + "\n           " + impl.lastExept.getMessage());
        return;
     }

     err = openWatchdog();
     if (err != 0) {
        System.out.println("PiWDogDemo open WDog error: " + errorText(err)
                       + "\n           " + impl.lastExept.getMessage());
        closeLock();
        return;
     }
     
     testStep(boot ? 14 : 4);
     testStep(boot ? 17 : 8);
     testStep(boot ? 20 : 14);

     thrDelay(2000); // 2s
     System.out.println("\nPiWDogDemo close down after 2 s");

     err =  closeWatchdog();
     if (err != 0) {
        System.out.println("\nPiWDogDemo open WDog error: " + errorText(err));
        impl.lastExept.printStackTrace();
     }
     closeLock();
     return;
  } // doIt()
} // PiWDogDemo (28.05.2019)
