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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

import de.frame4j.util.Action;
import de.frame4j.util.Action.Filter;
import de.frame4j.text.CleverSSS;
import de.frame4j.util.MinDoc;
import de.frame4j.text.RK;
import de.frame4j.text.TextHelper;


/** <b>Colour helper methods</b>. <br />
 *  <br />
 *  This abstract class contains some helper methods; mainly:<ul>
 *  <li>colour choice,</li>
 *  </ul>
 *  It is a subset of former GrafHelper
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2000 - 2005, 2016 &nbsp;  Albrecht Weinert  
 */
 // so far    V00.00 (11.06.2000) :  from AskDialog
 //           V02.00 (24.04.2003) :  CVS Eclipse
 //           V02.03 (26.04.2003) :  JavaDoc bug detected hereby  
 //           (since 1.4.2beta (reported) and shamefully recurring ever since) 
 //           V02.04 (20.05.2003) :  platform indep.  
 //           V.o52+ (12.02.2009) :  ported to Frame4J
 //           V.  34 (24.03.2021) : class -> interface 

@MinDoc(
   copyright = "Copyright 2000 - 2009, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 39 $",
   lastModified   = "$Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
// usage   = "static methods and embedded classes",  
   purpose = "graphical helpers"
) public interface ColorHelper extends ColorVal {
     
/** HashMap for colours. <br />
 *  <br />
 *  Used to avoid multiple decoding and generating.<br />
 *  Filled / checked by {@link #getColor(CharSequence)}.<br />
 *  <br /> 
 */
  public static ConcurrentHashMap<String, Color> colourCmap = Impl.getMap(); 

/** <b>Private helper class</b>. */   
  static class Impl { 
     
/** (Serial) number of last made button.<br />
 *  <br />
 *  @see GrafHelper#getButtonNo()
 */ 
    static volatile int buttonNo = -1;  // used in GrafHelper

    private Impl() {} // no objects no docu

    static private ConcurrentHashMap<String, Color> getMap(){
       ConcurrentHashMap<String, Color> colourCmap =
                                                  new ConcurrentHashMap<>();
      colourCmap.put("white",  ws); // optimising prefill
      colourCmap.put("ws",     ws);
      colourCmap.put("ge",     ge);
      colourCmap.put("yellow", ge);
      colourCmap.put("sw",     sw);
      colourCmap.put("black",  sw);
      colourCmap.put("si",     si); 
      colourCmap.put("silver", si);
      return colourCmap; 
    } // getMap()
  } // Impl
/* * No Objects. */
//   private ColorHelper(){}


/** Determine a foreground colour for a given background colour. <br />
 *  <br />
 *  The colour returned to the given (background) colour {@code bg} is
 *  either white {@link ColorVal#ws}) or black ({@link ColorVal#sw}), depending
 *  on the (weighted) brightness of  {@code bg}.<br />
 *  Algorithm: From {@code bg}'s RGB values a gray value will be calculated, 
 *  using ITU standard weights (222, 707, 71; according to recommendation 709,
 *  D65). About 51% will be used as threshold.<br />
 *  <br />
 *  @param bg  the background colour (null returns black)
 *  @return a fitting / contrasting foreground either white or black
 */
   public static Color calcFgColor(final Color bg){
      if (bg == null) return sw;
      int v = bg.getRGB();
      int gray255000 = 
           ((v >> 16) & 0xFF) * 222   // R
         + ((v >> 8) & 0xFF) * 707   // G
         +  (v & 0xFF) * 71;        // B
      return gray255000 > 130000 ? sw : ws;
      // int rg = (hg.getRed() + hg.getBlue()) * 4 + hg.getGreen() * 5;
      //return rg > 1274 ? sw : ws;
   } // calcFgColor(Color)


/** Set a component's foreground and background colours. <br />
 *  <br />
 *  The background colour of {@code c} will be set by {@code bg}.<br />
 *  The foreground colour will be set as white or black, whatever gives a
 *  good contrast to {@code bg}.<br />
 *  <br />
 *  @see #calcFgColor(Color)
 */
   public static void setBgColor(final Component c, final Color bg){
      if (c == null || bg == null) return;
      c.setBackground(bg);
      c.setForeground(calcFgColor(bg));
   }  // setBgColor (Component, Color)

/** The subsequence &quot;color=&quot; for white space and case ignoring search. */    
   public static final CleverSSS rkColIs = RK.make("color=", true, true);  
   
/** Determine a colour by a character sequence. <br />
 *  <br />
 *  The character sequence {@code colourDef} is stripped from surrounding 
 *  whitespace and quotes. If null or empty null is returned.<br />
 *  <br />
 *  If the subsequence &quot;color=&quot; is found (ignoring white spaces)
 *  within  {@code colourDef} only the token following the = is 
 *  considered.<br />
 *  <br />
 *  The  sequence {@code colourDef} or its remaining token is interpreted
 *  decimal, octal or hexadecimal as 24 bit number to define a colour
 *  as RGB (0xrrggbb); see also  
 *  {@link java.awt.Color#decode Color.decode(String)}. A leading sharp (#)
 *  will be replaced before by 0x (#FFC0C0 -&gt; 0xffcoco) to accept HTML
 *  colour values, too.<br />
 *  <br />
 *  The second possible interpretation of {@code colourDef} is as an English,
 *  German or French colour name or industry standard abbreviation.<br />
 *  <br />
 *  As an example, the following sequences (among others) will be understood
 *  as pure blue:<ul>
 *  <li> 255 </li>
 *  <li> &quot;255&quot; </li>
 *  <li> 0x0000ff </li>
 *  <li> blue </li>
 *  <li> blau </li>
 *  <li> bleu </li>
 *  <li> bl </li>
 *  <li> bcColor=&quot;blue&quot; align=&quot;center&quot; </li>
 *  </ul>
 *  See the hints at 
 *  {@link ColorVal#COLOR_CHOOSE ColorVal.COLOR_CHOOSE}.<br />
 *  <br />
 *  If all (those toils of) interpretation fail, null is returned.<br />
 *  <br />
 *  The lower cased token part of  {@code colourDef} is used as key
 *  to a {@link #colourCmap cache} used and filled by this method.
 *  This partly avoids double decoding and generating.<br />
 *  <br />
 *  @param colourDef the colour definition
 *  @return Color fitting colourDef or null
 *  @see ColorVal#COLOR_CHOOSE 
 *  @see Action
 */
   static public Color getColor(final CharSequence colourDef){
      String colDef = TextHelper.trimUq(colourDef, null);
      if (colDef == null) return null;
      colDef = TextHelper.simpLowerC(colDef);
      Color ret = colourCmap.get(colDef); 
      if (ret != null) return ret;
      
      int parPos = (int) rkColIs.whereImpl(colDef, 0, 0);
      int len;
      if (parPos > 0) { // color =
          colDef = colDef.substring(parPos).trim();
          len = colDef.length();
          if (len == 0) return null;
          parPos = colDef.indexOf(' ');
          if (parPos > 0) {
             colDef = colDef.substring(0, parPos);
             len = parPos;
          } 
          colDef = TextHelper.trimUq(colDef, null); // remove ""
          if (colDef == null) return null;
       } else { // color=
          len = colDef.length();
       }
       boolean onlyNumber = false;
       if (colDef.charAt(0) == '#' && len > 1) {
          onlyNumber = true;
          colDef = "0x" + colDef.substring(1);
       }
       ret = colourCmap.get(colDef); 
       if (ret != null) return ret;
       try {
          ret =  Color.decode(colDef);
       } catch (NumberFormatException nfe) { }
       if (!onlyNumber) {   
          Action ac = Action.select(ColorVal.COLOR_CHOOSE, 
                                           colourChooseFilter, colDef, true);
          if (ac != null) ret =  new Color(ac.value);
       }
       if (ret == null) return null;
       colourCmap.put(colDef, ret);
       return ret;
   } // getColor
   
/** A filter for Actions by code. <br />
 *  <br />
 *  This filter accepts all s non null action with code 
 *  &quot;choose colour&quot;.<br />
 *  @see Action
 *  @see Action#SCOLOR
 */
   public static final Filter colourChooseFilter  
                                     = Action.makeCodeFilter(Action.SCOLOR);
   

//------------------------------------------------------------------------

 
/** Surround a picture. <br />
 *  <br />
 *  The given picture {@code img} ( 3 * 8 bit colour model) will be enlarged
 *  by the given margins (the result being returned as new image).<br />
 *  <br >
 *  Is {@code img} null or of zero or unknown size, null will be 
 *  returned.<br />
 *  <br />
 *  If (just) one margin is outside the range 0 to 400, all (!) margins are 
 *  set to 0.<br />
 *  <br />
 *  @param img the picture to be surrounded
 *  @param bgColor background respectively margin colour (default {@link ColorVal#ws}).
 *  @param upM upper margin in pixel (0..400).
 *  @param loM leM - riM - ditto lower, left and right.
 *  @return the picture as buffered image and (if given in range) surrounded
 *          / enlarged by margins
 */ 
   public static BufferedImage surrondImage(final Image img, 
                           final Color bgColor,
                           int upM, final int loM, int leM, final int riM) {
      if (img == null) return null;
      int w = img.getWidth(null);
      int h = img.getHeight(null);
      if (w < 0 || h < 0) return null;

      if (upM < 0 || loM < 0 || leM < 0 || riM < 0 
          ||  upM > 400 || loM > 400 | leM > 400 || riM > 400 ) {
         leM = upM = 0;
      } else {
         w += leM + riM;
         h += upM + loM; 
      }
      BufferedImage ret = 
            new BufferedImage(w , h, BufferedImage.TYPE_INT_RGB);
      Graphics gr = ret.getGraphics();
      gr.setColor(bgColor == null ? ws : bgColor);
      gr.fillRect(0, 0, w, h); 
      gr.drawImage(img, leM, upM,  null);
      gr.dispose();
      return ret;
   } // surrondImage(Image


/** Scaling a picture. <br />
 *  <br />
 *  The given picture {@code img} ( 3 * 8 bit colour model) will be stretched
 *  or shrunk to the given size and returned as {@link BufferedImage}.<br />
 *  <br >
 *  Is {@code img} null or of zero or unknown size, null or is the one of the 
 *  wanted sizes less than 1, null  will be returned.<br />
 *  <br />
 *  Are the given and ordered dimensions the same and is {@code img} of type 
 *  {@link BufferedImage}, {@code img} itself will be returned (not
 *  copied).<br /> 
 *  <br />
 *  @param img the picture to scale
 *  @param bgColor background colour (used to prefill the new buffered 
 *      image, if not null)
 *  @param newHeight new height
 *  @param newWidth  new width
 *  @return the scaled picture as buffered image
 */ 
   public static BufferedImage scaleImage(Image img, Color bgColor,
                                    int newWidth, int newHeight) {
      if (img == null || newWidth <= 0 || newHeight <= 0) return null;
      int w = img.getWidth(null);
      int h = img.getHeight(null);
      if (w < 0 || h < 0) return null;
      boolean noScale = w == newWidth && h == newHeight;
      if (noScale  && img instanceof BufferedImage) 
         return (BufferedImage) img;
      BufferedImage ret = 
          new BufferedImage(newWidth ,newHeight, BufferedImage.TYPE_INT_RGB);
      Graphics gr = ret.getGraphics();
      if (bgColor != null) {
         gr.setColor(bgColor);
         gr.fillRect(0, 0, newWidth, newHeight);
      }   
      gr.drawImage(img, 0, 0, newWidth, newHeight, null);
      gr.dispose();
      return ret;
   } // surrondImage(Image

} // class ColorHelper (16.07.2003, 03.03.2009)
   