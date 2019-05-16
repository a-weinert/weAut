/** <b>Definitions for the usage of the Raspberry Pi and its I/O. <br />
 *  <br />
 *  <a href=./de/weAut/package-summary.html#co>&copy;</a> 
 *  Copyright 2019 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 17 $ ($Date: 2019-05-15 21:51:04 +0200 (Mi, 15 Mai 2019) $)
 */
package de.weAut;

public interface PiUsage {
   
/** The default lock file.
 * 
 *  This is the (default) path of the file to lock the exclusive use of
 *  the pigpoi[d] on the RasPi. It is (has to be) the same as with control
 *  programmes written in C.   
 */
  public final String lckPiGpioPth = "/home/pi/bin/.lockPiGpio";


} // PiUsage
