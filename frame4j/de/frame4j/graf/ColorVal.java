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
package de.frame4j.graf;

import java.awt.Color;

import de.frame4j.util.Action;
import de.frame4j.util.MinDoc;


/** <b>Common colour constants</b>. <br />
 *  <br />
 *  This interface defines common final graphical values.<br />
 *  <br />
 *  The final values for colours are intentionally lower case (against
 *  Java style rules), to be compatible with (German) &quot;DIN&quot; standard
 *  two letter abbreviations used in industrial applications: rt, gn, gr,
 *  and so on. English two letters are supplemented as long as unambiguous 
 *  (rt = rot aka rd = red; ge ye; ..). <br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 1998 - 2004 &nbsp; Albrecht Weinert 
 */
 // so far    V00.00 (15:13 28.04.2000) :  ex weinertBib 
 //           V02.00 (24.04.2003 16:54) :  CVS Eclipse
 //           V02.20 (20.07.2004 11:47) :  rosa and indigo
 //           V02.21 (02.09.2004 15:19) :  bl (blue, blau) added avoid bl.ack
 //           V02.24 (17.02.2005 11:35) :  /**, shortened
 //           V.o56+ (26.01.2009 15:06  :  1st translation
 //           V.  33 (23.03.2021) : nbl added, now extended by GrafVal

@MinDoc(
   copyright = "Copyright 1998 - 2009, 2021  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "Albrecht Weinert",
// usage   = "static import",  
   purpose = "common final values for colours"
) public interface ColorVal  {
   

/** Standard colours, grey scale. <br />
 *  <br />
 *  ws=wt=weiß=white, si=silber, dsi=dunkelsilber, hgr=hellgrau, 
 *  gr=grau=gray, dgr=dunkelgrau=dark-grey, sw=bk=schwarz=black.<br />
 *  <br />
 *  Albeit final (demotic constant) the name is lower case according to
 *  (German) &quot;DIN&quot; standard for two letter colour 
 *  abbreviations.<br />
 */
   static public final Color 
     ws = Color.white, wt = ws,
     si  = new Color(238, 238, 238),
     dsi = new Color(204, 204, 204), //  CC Button-Background
     hgr = Color.lightGray,
     gr  = Color.gray,
     dgr = Color.darkGray,
     sw  = Color.black, bk = sw;

/** Standard colours, basic. <br />
 *  <br />
 *  rt=rot, gn=grün, bl= blau, ge = gelb.<br />
 *  pi=pink, or=orange.<br />
 *  dXY = dunkelXY = dark-XY, hXY = hellXY = bright-XY.<br />
 *  <br />
 *  @see #ws
 *  @see #wt
 */
   static public final Color 
     hrt = Color.red.brighter(),
     hrd = hrt, 
     rt  = Color.red,
     rd = rt,
     drt = new Color(120,31,0),
     gn  = Color.green,
     dgn = Color.green.darker(), 
     hbl = Color.blue.brighter(),
     bl  = Color.blue,
     dbl = Color.blue.darker(),
     nbl = new Color(0, 0, 102), // night blue
     dge = Color.yellow.darker(),
     dye = dge,
     ge  = Color.yellow,
     ye = ge,
     pi = Color.pink,
     or = Color.orange;


/** Action list: choose colour. <br />
 *  <br />
 *  Colour names and abbreviations in English and German (partly also French)
 *  are assigned to 24-bit-RGB-values.<br />
 *  <br />
 *  Hint: Any improvement and meaningful amplification here give benefit to
 *  all framework applications parsing or asking colours.<br />
 *  <br />
 *  <b>Overview<br /></b>
 * <table cellpadding="8" cellspacing="0" border="1" summary="Colours">
<tbody><tr>
<td style="width:250;">black, schwarz, sw, noir</td>
<td style="background-color:#000000; width:70; color:white">0x000000</td>
<td style="width:240;">gray</td>
<td style="background-color:#808080; width:70; color:white;">0x808080</td>
</tr><tr>
<td >maroon</td>
<td style="background-color:#800000; color:white;">0x800000</td>
<td >red, rot, rt, rouge</td>
<td style="background-color:#ff0000">0xFF0000</td>
</tr><tr>
<td >dunkelgrün, dgn, darkgreen, dark_green *)</td>
<td style="background-color:#008000; color:white;">0x008000</td>
<td >green, grün, gruen, gn, vert, lime</td>
<td style="background-color:#00ff00">0x00FF00</td>
</tr><tr>
<td >olive</td>
<td style="background-color:#808000; color:white;">0x808000</td>
<td >yellow, gelb, ge</td>
<td style="background-color:#ffff00">0xFFFF00</td>
</tr><tr>
<td >navy, dunkelblau</td>
<td style="background-color:#000080; color:white;">0x000080</td>
<td >blue, blau, bleu</td>
<td style="background-color:#0000ff; color:white;">0x0000FF</td>
</tr><tr>
<td >purple, violet, flieder</td>
<td style="background-color:#800080; color:white;">0x800080</td>
<td >fuchsia, magenta, lavendel</td>
<td style="background-color:#ff00ff">0xFF00FF</td>
</tr><tr>
<td >indigo</td>
<td style="background-color:#4B0082; color:white;">0x4B0082</td>
<td >rosa, pink</td>
<td style="background-color:#FFC0CB">0xFFC0CB</td>
</tr><tr>
<td >teal, mintgrün</td>
<td style="background-color:#008080; color:white;">0x008080</td>
<td >aqua, cyan</td>
<td style="background-color:#00ffff">0x00FFFF</td>
</tr><tr>
<td >silver, silbern, silber</td>
<td style="background-color:#c0c0c0">0xC0C0C0</td>
<td style="background-color:#FCFCFC">white, weiß, weiss, ws, blanche</td>
<td style="background-color:#ffffff">0xFFFFFF</td>
 *  </tr></tbody></table>
 *  
 *  Hint *):<br />
 *  For W3C-HTML &quot;green&quot; is 0x008000 (here dark green #008000). The 
 *  W3C, astonishingly, doesn't call the basic (RGB-) green green. (That may
 *  be a trap somewhere.)<br />
 *  <br style="clear:both;" />
 *  @see Action
 *  @see ColorHelper#getColor(CharSequence)
 */
   static public final Action[] COLOR_CHOOSE = {
      new Action(Action.SCOLOR, 0xFF0000, new String[] {
         "red", "rot", "rt", "rouge" 
      }),
      new Action(Action.SCOLOR, 0x00FF00, new String[] {
         "green", "grün", "gruen", "gn", "vert", "lime" 
      }),
      new Action(Action.SCOLOR, 0x0000FF, new String[] {
         "bl", "blue", "blau", "bleu" // bl non abbreviating cause of black
      }),
      new Action(Action.SCOLOR, 0x000000, new String[] {
         "black", "schwarz", "sw", "noir", "bk" 
      }),
      new Action(Action.SCOLOR, 0xFFFFFF, new String[] {
         "white", "weiß", "weiss", "ws", "blanche", "wt" 
      }),
      new Action(Action.SCOLOR, 0xFFFF00, new String[] {
         "yellow", "ge", "gelb", "jaune"
      }),
      new Action(Action.SCOLOR, 0x808080, new String[] {
         "gray", "gr",  "grau", "gris"
      }),
      new Action(Action.SCOLOR, 0xC0C0C0, new String[] {
         "silver", "silbern", "argent" 
      }),
      new Action(Action.SCOLOR, 0xFF00FF, new String[] {
         "magenta", "fuchsia", "lavendel", "purpurrot"
      }),
      new Action(Action.SCOLOR, 0x00FFFF, new String[] {
         "aqua", "cyan", "blaugrün"
      }),
      new Action(Action.SCOLOR, 0x808000, new String[] {
         "olive", "olivgrün", 
      }),
      new Action(Action.SCOLOR, 0x800000, new String[] {
         "maroon", "weinrot", "kastanienbraun" 
      }),
      new Action(Action.SCOLOR, 0x808000, new String[] {
         "purple", "violet", "flieder"
      }),
      new Action(Action.SCOLOR, 0x008080, new String[] {
         "teal", "mintgrün"
      }),
      new Action(Action.SCOLOR, 0x008000, new String[] {
         "dunkelgrün", "dgn", "darkgreen", "dark_green"
      }),
      new Action(Action.SCOLOR, 0x000080, new String[] {
         "navy", "dunkelblau", "dbl"
      }),
      new Action(Action.SCOLOR, 0x000080, new String[] {
         "nightblue", "nachtblau", "nbl"
      }),
      new Action(Action.SCOLOR, 0xFFC0CB, new String[] {
        "rosa", "pink", "rosarot", "rose"
      }),
      new Action(Action.SCOLOR, 0x4B0082, new String[] {
         "indigo",
      }),
   };
/* WWW HTML
    Black  = #000000    Green  = #008000 (not so)
    Silver = #C0C0C0    Lime   = #00FF00
    Gray   = #808080    Olive  = #808000
    White  = #FFFFFF    Yellow = #FFFF00
    Maroon = #800000    Navy   = #000080
    Red    = #FF0000    Blue   = #0000FF
    Purple = #800080    Teal   = #008080
    Fuchsia= #FF00FF    Aqua   = #00FFFF
*/

}  // ColorVal (24.04.2003, 26.01.2009, 23.03.2021)


