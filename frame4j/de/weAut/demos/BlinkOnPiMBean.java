/*  Copyright 2021 Albrecht Weinert, Bochum, Germany (a-weinert.de)
 *  All rights reserved.
 *  
 *  This file is part of Frame4J notwithstading being in de.weAut... 
 *  ( frame4j.de  https://weinert-automation.de/software/frame4j/ )
 * 
 *  Frame4J is made available under the terms of the 
 *  Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/  or as text in
 https://weinert-automation.de/java/docs/frame4j/de/frame4j/doc-files/epl.txt
 *  within the source distribution 
 */
package  de.weAut.demos;

import de.frame4j.util.AppMBean;

/** <b>Test and demo program BlinkOnPi as MBean</b>. <br />
 *  <br />
 *  Contrary to {@link  de.weAut.demos.RdGnPiGpioDBlink} the test and demo
 *  program {@link  de.weAut.demos.BlinkOnPi} makes full use of Frame4J
 *  by inheriting {@link de.frame4j.util.App}. <br />
 *  Hence, additionally to the operations and properties of 
 *  {@link  de.weAut.demos.RdGnPiGpioDBlink} respectively
 *  {@link  de.weAut.demos.RdGnPiGpioDBlinkMBean} extended here we have also
 *  {@link de.frame4j.util.AppMBean}.<br />
 *  Optionally, also {@link de.frame4j.util.UIInfo} (implemented by 
 *  {@link de.frame4j.util.App} can be extended (not recommended).<br />
 *  <br />
 *  As for standard MBeans the interface shall be named according to the 
 *  applications class one can't use interface multiple inheritance there.
 *  Hence, this interface just bundles those needed for JMX/JConsole.<br />
 *  <br />
 *  Copyright  &copy;  2021  Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 41 $ ($Date: 2021-04-23 20:44:27 +0200 (Fr, 23 Apr 2021) $)
 */
// so far:   V.  33  (27.03.2021) : new, minimal functionality
//           V.  34  (04.04.2021) : getPiType added 

public interface BlinkOnPiMBean extends RdGnPiGpioDBlinkMBean, AppMBean { }
