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

import java.awt.Button;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Component;
//import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.EventListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.border.Border;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.frame4j.xml.MLHelper;
import de.frame4j.xml.ParseErrorHandler;
import de.frame4j.util.Action;
import de.frame4j.math.ConstIntPair;
import de.frame4j.math.IntPairFix;
import de.frame4j.text.CleverSSS;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;
import de.frame4j.util.Action.Filter;
import de.frame4j.text.RK;
import de.frame4j.xml.SAXHandler;
import static de.frame4j.graf.GrafVal.*;

/** <b>Graphical helper methods</b>. <br />
 *  <br />
 *  This abstract class contains some helper methods for graphical 
 *  applications for:<ul>
 *  <li>colour choice,</li>
 *  <li>factories for buttons ({@link Button awt.Button}),</li>
 *  <li>factory methods for menu bars
 *        ({@link java.awt.MenuBar awt.MenuBar}, 
 *         {@link javax.swing.JMenuBar swing.JMenuBar}) as well as</li>
 *  <li>handling, i.e. setting, finding and removing, listeners for menu
 *      elements,</li>
 *  <li>XML encoder and decoder for menu bars,</li>
 *  <li>a simple, universal description of Menu/JMenuBar in XML,</li>
 *  <li>surrounding of pictures.</li>
 *  </ul>
 *
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2000 - 2003, 2005 &nbsp;  Albrecht Weinert  
 */
 // so far    V00.00 (11.06.2000) :  from AskDialog
 //           V00.03 (18.11.2001) :  KeyEvent error
 //           V00.04 (18.11.2001) :  raenderImage
 //           V00.05 (16.07.2002) :  de, CheckBoxMenuItem
 //           V02.00 (24.04.2003) :  CVS Eclipse
 //           V02.03 (26.04.2003) :  JavaDoc bug detected hereby  
 //           V02.04 (20.05.2003) :  platform indep.  
 //           V02.06 (16.07.2003) :  swing and awt are supported
 //           V02.09 (12.11.2003) :  Border, JLabel, ...
 //           V02.26 (29.04.2005) :  XML-SAX
 //           V02.27 (16.05.2005) :  String[][]-Menus out
 //           V02.30 (03.05.2007) :  MenuBarFactory (from WinApp)
 //           V.  34 (24.03.2021) : class -> interface 
@MinDoc(
   copyright = "Copyright 2000 - 2005, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 44 $",
   lastModified   = "$Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $",
   purpose = "graphical helpers and factories"
) public interface GrafHelper extends ColorHelper, ColorVal  {

/** (Serial) number of last made button. <br /> <br />
 *  <br />
 *  Every button made by {@link #makeButton makeButton()} and by 
 *  {@link #makeJButton(String, Font, Color, Color, Border) makeJButton()}
 *  will be numbered.<br />
 */ 
   public static int getButtonNo(){ return ColorHelper.Impl.buttonNo; }

/** Making a button (Button). <br />
 *  <br />
 *  This method delivers a ready made and initialised button
 *  ({@link java.awt.Button awt.Button}) with text, command, colours and
 *  {@link java.awt.event.ActionListener ActionListener}.<br />
 *  Is <code>text</code> null &quot;Button56&quot; (serial number) will
 *  be used.<br />
 *  Is <code>font</code> null, the Button class takes the parent container's
 *  font setting.<br />
 *  Is <code>command</code> null, the <code>text</code> will be used (which
 *  is usually a mistake in (inter) nationalisable applications).<br />
 *  <br />
 *  @param text the button's labelling. Should be changed by (inter) 
 *         nationalising.
 *  @param font for labelling
 *  @param command the action command initiated by this button. Often chosen
 *         equal or similar to the labelling. Must keep its value even 
 *         under  (inter) nationalising. 
 *  @param bgColor background colour
 *  @param fgColor text colour
 *  @param al the ActionListener (the object obeying to or forwarding
 *               the action command)
 */
   public static Button makeButton(final String text, final Font font,
                                   final String command, 
                                   final Color bgColor, final Color fgColor,
                                   final ActionListener al){
      Button ret = new Button();
      ++ColorHelper.Impl.buttonNo;
      if (font != null) ret.setFont(font);
      if (bgColor != null) ret.setBackground(bgColor);
      if (fgColor != null) ret.setForeground(fgColor);
      ret.setLabel(text != null ? text : "Button" + ColorHelper.Impl.buttonNo);
      if (command !=null) ret.setActionCommand(command);
      if (al !=null) ret.addActionListener(al);
      return ret;
   }  // makeButton(String...)


/** Making a button (JButton). <br />
 *  <br />
 *  This method delivers a ready made and initialised button
 *  {@link javax.swing.JButton swing.JButton}) with text, command,
 *   colours and
 *  {@link java.awt.event.ActionListener ActionListener}.<br />
 *  <br />
 *  Except for {@link javax.swing.JButton swing.JButton JButton}'s extra
 *  possibility to define margins via Border objects this method is 
 *  equivalent to (see there) {@link #makeButton makeButton()}.<br />
 *  <br />
 *  If a Border object {@code bo} is given, it will (of course) be used. Is 
 *  {@code bo} null and bgColor not null, a (Line-) Border object in
 *  {@code bgColor} will be fetched and set. This makes the JButton rimless.
 *  In many respects is a &quot;rimless&quot; JButton a better substitute
 *  for a JLabel; see also
 *  {@link #makeJButton(String, Font, Color, Color, Border)}.<br />
 *  <br />
 *  If {@code text} is longer than 3 and does not yet start by &lt;html&gt;
 *  (match case insensitive) and does contain &lt;, &gt; or &amp;, 
 *  &lt;html&gt; will be put in front. (JButtons, friendly, allow 
 *  text formatting by HTML tags.)<br />
 *  <br />
 *  An action {@code command} independent from the label gets more 
 *  important in the light of those HTML-features, not to say
 *  indispensable.<br />
 *  <br />
 *  @see #makeButton(String, Font, String, Color, Color, ActionListener)
 *  @see #makeJButton(String, Font, Color, Color, Border)
 */
   public static JButton makeJButton(String text, final Font font,
                                     final String command, 
                                     final Color bgColor, final Color fgColor,
                                     final ActionListener al,
                                     final Border bo){
      ++ColorHelper.Impl.buttonNo;
      if (text == null) {
         text =  "JButton" + ColorHelper.Impl.buttonNo;
      } else if (text.length() > 3 
          && !TextHelper.startsWith(text, "<html>", true )
               && (text.indexOf('<') >= 0 || text.indexOf('>') >= 0 
                       || text.indexOf('&') >= 0) ) {
         text = "<html>" + text;
      }
      JButton ret = new JButton();
      if (font != null) ret.setFont(font);
      if (bgColor != null) ret.setBackground(bgColor);
      if (fgColor != null) ret.setForeground(fgColor);
      ret.setText(text);
      if (command != null) ret.setActionCommand(command);
      if (al != null) ret.addActionListener(al);
      if (bo != null) { 
         ret.setBorder(bo);
      } else if (bgColor != null) {
         ret.setBorder(BorderFactory.createLineBorder(bgColor));
      }
      return ret;
   }  // makeJButton(String...)

/** Making a button (JButton). <br />
 *  <br />
 *  This method delivers a ready made and initialised button
 *  {@link javax.swing.JButton swing.JButton}) with text, colour and so on
 *  but with no command nor 
 *  {@link java.awt.event.ActionListener ActionListener}.<br />
 *  <br />
 *  It's the simple variant of 
 {@link #makeJButton(String, Font, String, Color, Color, ActionListener, Border)}
 *  when generating (pseudo) labels.<br />
 */
   public static JButton makeJButton(final String text, final Font font,
                   final Color bgColor, final Color fgColor, final Border bo){
      return makeJButton(text, font, null, bgColor, fgColor, null, bo);
   } // makeJButton(...


// --------------------------------------------------------------------------

/** Filling a rectangle. <br />
 *  <br />
 *  This method fills a rectangle with a colour, a picture or a picture + 
 *  background colour, depending on what parameter is not null.<br />
 *  <br />
 *  If g is null or w or h are 0, nothing happens (except return false).<br />
 *  If c and i are both null, nothing happens, too.<br />
 *  <br />
 *  Is c given the rectangle will be filled by it.<br />
 *  Is i given, the picture will be scaled to rectangle and painted on it.
 *  A given c will shine through transparent areas of i.<br />
 *  <br />
 *  If something is painted true respectively the result of 
 *  {@code g.drawImage(i,...)} will be returned.<br />
 *
 * @param g graphical context
 * @param c (background) colour
 * @param i picture
 * @param x starting point (upper left)
 * @param y starting point (upper left)
 * @param w width 
 * @param h height
 * @return true: something painted
 */
   public static boolean fillRect(final Graphics g, final Color c,
           final Image i, final int x, final int y, final int w, final int h){
      if (g == null || w == 0 || h == 0) return false;
      if (i == null && c == null) return false;
      if (c != null) {
         g.setColor(c);
         g.fillRect(x, y,  w, h);
      }
      if (i != null)
         return g.drawImage(i, x, y,  w, h, null);
      return true;                    
   } // fillRect


/** Centred painting of a text in (p, d). <br />
 *  <br />
 *  If {@code g} is  null, nothing happens. <br />
 *  Otherwise this method sets foreground colour and font if those parameters
 *  are not null.<br />
 *  If p, d or text are null (or empty) nothing else happens; the same is
 *  true for a d smaller than 6 * 6.<br />
 *  <br />
 *  The FontMetrics  (dependent from font and text)  are determined (a quite
 *  expensive procedure) and the starting positions relative to p for
 *  centred text output are calculated. This position is also the return
 *  value.<br />
 *  <br />
 *  Afterwards the text is painted centred in the rectangle d (starting
 *  at p).<br />
 *  <br />
 *  If the same text has to be painted with the same font within a rectangle
 *  of the same size the returned p can (and should) be reused.<br />
 */
   public static IntPairFix paintString(final Graphics g,
                       final ConstIntPair p, final ConstIntPair d,
                          final String text, final Color fgColor, Font font){
      if (g == null) return null;
      if (fgColor != null) g.setColor(fgColor);
      if (font != null) 
         g.setFont(font);
      else
         font = g.getFont();
      if (p == null || d == null ) return null;
      return paintString(g, p.getX(), p.getY(), d.getX(), d.getY(),
                                                        text, fgColor, font);
   } // paintString(Graphics, 2*ConstIntPair, String, Color, Font)

/** Centred painting of a text in (x,y), (width,height)). <br />
 *  <br />
 *  This is the (real) implementation of 
{@link #paintString(Graphics, ConstIntPair, ConstIntPair, String, Color, Font)
 paintString(Graphics g, ConstIntPair p, ConstIntPair d, String text, 
 Color fG, Font f)}
 *  just getting (x, y) from p and (width, height) from d.<br />
 *  <br />
 *  Notwithstanding the not recommendable signature (8 parameters) this 
 *  method may very well be used directly.
 */   
  public static IntPairFix paintString(final Graphics g, 
                        int x, int y, int width, int height, 
                        final String text, final Color fgColor, Font font){
    if (g == null) return null;
    if (fgColor != null) g.setColor(fgColor);
    if (font != null) 
       g.setFont(font);
    else
       font = g.getFont();

    if (text == null || text.isEmpty()) return null;

    if (width < 6 || height < 6) return null; // no text whatever will fit within
    FontMetrics fm = g.getFontMetrics();
    int xRel = (width - fm.stringWidth(text)) / 2;
    int yRel = (fm.getAscent()
                           - fm.getDescent() - fm.getLeading() + height) / 2;
    g.drawString(text, x + xRel, y + yRel);
    return IntPairFix.ofInts(xRel, yRel); 
 }  // paintString(Graphics, 4*int, String, Color, Font)

//---------------------------------------------------------------------------

/** Determine a foreground colour for a given background colour. <br />
 *  <br />
 *  The colour returned to the given (background) colour {@code bg} is
 *  either white {@link GrafVal#ws}) or black ({@link GrafVal#sw}), depending
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

/** The subsequence &quot;color=&quot; for white space
 *         and case ignoring search. <br /> */    
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
 *  {@link GrafVal#COLOR_CHOOSE GrafVal.COLOR_CHOOSE}.<br />
 *  <br />
 *  If all (those toils of) interpretation fail, null is returned.<br />
 *  <br />
 *  The lower cased token part of  {@code colourDef} is used as key
 *  to a {@link GrafHelper#colourCmap cache} used and filled by this method.
 *  This partly avoids double decoding and generating.<br />
 *
 *  @param colourDef the colour definition
 *  @return Color fitting colourDef or null
 *  @see GrafVal#COLOR_CHOOSE 
 *  @see Action
 */
   static public Color getColor(final CharSequence colourDef){
      String colDef = TextHelper.trimUq(colourDef, null);
      if (colDef == null) return null;
      colDef = TextHelper.simpLowerC(colDef);
      Color ret = GrafHelper.colourCmap.get(colDef); 
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
       ret = GrafHelper.colourCmap.get(colDef); 
       if (ret != null) return ret;
       try {
          ret =  Color.decode(colDef);
       } catch (NumberFormatException nfe) { }
       if (!onlyNumber) {   
          Action ac = Action.select(GrafVal.COLOR_CHOOSE, 
                                           colourChooseFilter, colDef, true);
          if (ac != null) ret =  new Color(ac.value);
       }
       if (ret == null) return null;
       GrafHelper.colourCmap.put(colDef, ret);
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

/** Default dialogue font description. <br />
 *  <br />
 *  Value: Dialog-plain-12
 */
   public final static String DEF_DIAG_FONT_DES = "Dialog-plain-12";

/** Fetch a character set (font) by name and attributes. <br />
 *  <br />
 *  The character sequence fontDef is stripped from surrounding whitespace.
 *  If (then) empty or null, null is returned. The same happens if 
 *  size is outside 4..200.<br />
 *  <br />
 *  Otherwise an according Font is returned from a  
 *  {@link GrafVal#fontCmap cache} or constructed.<br />
 *  <br />
 *  Besides the checking and caching this method is mostly equivalent to
 {@link Font}.{@link Font#Font(String, int, int) Font(name, style, size)}.<br />
 *  <br />  
 *  @param name a font's name 
 *       (not empty after stripping surrounding white space)
 *  @param style the Font's style value (Font class constants ORed)
 *  @param size  the Font's the point size (4..200)
 *  @return the according font or null if parameter conditions violated
 *  @see #getFont(CharSequence)
 */  
   public static Font getFont(final CharSequence name, final int style,
                                                           final int size){
      if (size < 4 || size > 200) return null;
      String styleName = ComVar.EMPTY_STRING;
      switch (style)  {
         case Font.BOLD | Font.ITALIC : styleName = "-bolditalic-";  break;
         case Font.ITALIC:             styleName = "-italic-";    break;   
         case Font.BOLD :             styleName = "-bold-";    break;
         case Font.PLAIN:            styleName = "-plain-"; break;   
      }
      String fontCh = TextHelper.trimUq(name, null);
      if (fontCh == null) return null;
      String fontKey = fontCh + styleName + size;
      Font ret = fontCmap.get(fontKey);  
      if (ret != null) return ret;         
      ret = new Font(fontCh, style, size);
      fontCmap.put(fontKey, ret);
      return ret;
   } // getFont(CharSequence, 2*int)

   
/** Fetch a character set (font) by a character sequence. <br />
 *  <br />
 *  The character sequence fontDef is stripped from surrounding whitespace.
 *  If empty it is substituted by {@link #DEF_DIAG_FONT_DES}. The 
 *  resulting sequence is used for getting a Font as returned by
 *  {@link java.awt.Font Font}.{@link java.awt.Font#decode decode()}. <br />
 *  <br />
 *  This method either gets this font from a {@link GrafVal#fontCmap cache}
 *  or uses  {@link java.awt.Font Font}.{@link java.awt.Font#decode
 *  decode()} to get (and cache) it.<br />
 *  <br />  
 *  @param fontDef Font definition (face-style-size)
 *  @return an according font
 *  @see #getFont(CharSequence, int, int)
 */
   public static Font getFont(final CharSequence fontDef){
      String fontCh = TextHelper.trimUq(fontDef, DEF_DIAG_FONT_DES);
      Font ret =  fontCmap.get(fontCh);
      if (ret != null) return ret; // cached
      ret = Font.decode(fontCh);
      if (ret == null) return null;  // should not happen
      fontCmap.put(fontCh, ret);
      return ret;
   } // getFont(CharSequence)

   
//----------  MenuBar  ------------------------------------

/** XML coding a menu bar (MenuBar). <br />
 *  <br />
 *  The {@link java.awt.MenuBar awt.MenuBar} given as parameter mb 
 *  or any other  {@link java.awt.MenuContainer awt.MenuMenuContainer} 
 *  or {@link javax.swing.JMenuBar swing.JMenuBar} will be XML encoded
 *  including all its contained 
 *  {@link java.awt.Menu Menus} and 
 *  {@link java.awt.MenuItem MenuItems} respectively
 *  {@link javax.swing.JMenu JMenus}
 *  and {@link javax.swing.JMenuItem JMenuItems}.<br />
 *  <br />
 *  In case of success true is returned. The OutputStream out is 
 *  closed afterwards.<br />
 *  <br />
 *  @see MLHelper#xmlEncode(Object, String)
 */
   public static boolean xmlEncode(MenuContainer mb, OutputStream out){
     if (mb == null || out == null) return false;
      try {
         XMLEncoder e = new XMLEncoder(out);
         e.writeObject(mb);
         e.close();
         return true;
      } catch (Exception ex) {
         return false;
      } catch (NoClassDefFoundError ndf) {
         return false;
      }
   } // xmlEncode(MenuContainer, OutputStream)

//---------------------------------------------------------------------------

/** Setting the listeners of a MenuBar. <br />
 *  <br />
 *  For all Menus of (J)MenuBar {@code mb} all  (J)MenuItems will get the 
 *  ActionListener aL and all (J)CheckboxMenuItems will get the 
 *  ItemListener {@code iL} if they have no such listener yet (using the 
 *  method
 *  {@link #initMenu(JMenu, ActionListener, ItemListener) initMenu()}).<br />
 *  <br />
 *  @param mb MenuBar, may be a {@link java.awt.MenuBar awt.MenuBar}
 *            or a {@link javax.swing.JMenuBar swing.JMenuBar} sein
 */
   public static void initMenuBar(final MenuContainer mb, 
                           final ActionListener aL, final ItemListener iL){
      if (mb == null) return;
      if (aL == null && iL == null) return;
      if (mb instanceof MenuBar) {
        int anzMenus = ((MenuBar)mb).getMenuCount();
        for (int mn = 0; mn < anzMenus; ++mn)
           initMenu (((MenuBar)mb).getMenu(mn), aL, iL);
     } else if (mb instanceof JMenuBar) {
        int anzMenus = ((JMenuBar)mb).getMenuCount();
        for (int mn = 0; mn < anzMenus; ++mn)
           initMenu(((JMenuBar)mb).getMenu(mn), aL, iL);
     }
   } // initMenuBar(MenuContainer, ActionListener, ItemListener)


/** Setting the listeners of a Menu. <br />
 *  <br />
 *  For the {@link java.awt.Menu} m all {@link java.awt.MenuItem}s get
 *  the ActionListener {@code aL}.<br />
 *  If iL is not null all {@link CheckboxMenuItem}s get the 
 *  ItemListener iL (instead of aL).<br />
 *  This only happens for those {@link java.awt.MenuItem}s having no such
 *  listener yet.<br />
 *  Sub menus are visited recursively.<br />
 */
   public static void initMenu(final Menu m, 
                   final ActionListener aL, final ItemListener iL){
      if (m == null) return;
      if (aL == null && iL == null) return;
      int anzItems = m.getItemCount();
      itemLoop: for (int in=0; in < anzItems; ++in) {
         MenuItem mi = m.getItem(in);
         if (mi instanceof Menu) {
            initMenu((Menu) mi, aL, iL);
            continue itemLoop;
         }
         if (mi == null) continue itemLoop;
         if (mi instanceof CheckboxMenuItem && iL != null) {
            if (mi.getListeners(ItemListener.class).length == 0 ) 
               ((CheckboxMenuItem)mi).addItemListener(iL);
            continue itemLoop;
         } 
         if (aL != null 
               && mi.getListeners(ActionListener.class).length == 0 ) {
               mi.addActionListener(aL);
         }
      } // itemLoop
   } // initMenu(Menu, ActionListener, ItemListener)


/** Setting the listeners of a (J)Menu. <br />
 *  <br />
 *  For the {@link javax.swing.JMenu} m all {@link JMenuItem}s get
 *  the ActionListener {@code aL}.<br />
 *  If iL is not null all {@link CheckboxMenuItem}s get the 
 *  ItemListener iL (instead of aL).<br />
 *  This only happens for those {@link java.awt.MenuItem}s having no such
 *  listener yet.<br />
 *  Sub menus are visited recursively.<br />
 */
   public static void initMenu(final JMenu m, 
                             final ActionListener aL, final ItemListener iL){
      if (m == null) return;
      if (aL == null && iL == null) return;
      int anzItems = m.getItemCount();
      itemLoop: for (int in=0; in < anzItems; ++in) {
         JMenuItem mi = m.getItem(in);
         if (mi instanceof JMenu) {
            initMenu((JMenu) mi, aL, iL);
            continue itemLoop;
         }
         if (mi == null) continue itemLoop;
         if (mi instanceof JCheckBoxMenuItem  && iL != null) {
           if (mi.getListeners(ItemListener.class).length == 0) 
               ((JCheckBoxMenuItem)mi).addItemListener(iL);
         } else if (aL != null 
               && mi.getListeners(ActionListener.class).length == 0 ) {
               mi.addActionListener(aL);
         }
      }
   } // initMenu(JMenu, ActionListener, ItemListener)

//---------------------------------------------------------------------------

/** Removing all listeners from a MenuBar. <br />
 *  <br />
 *  In Menus of the MenuBar {@code mb} the menu items will be freed from the 
 *  listeners (ActionListener or ItemListener) using the (fitting overloaded)
 *  method {@link #removeListenersMenu(JMenu) removeListenersMenu()}.<br />
 *  <br />
 *  @param mb MenuBar, may be a {@link java.awt.MenuBar awt.MenuBar}
 *            or a {@link javax.swing.JMenuBar swing.JMenuBar}
 */
   public static void removeListenersMenuBar(final MenuContainer mb){
      if (mb instanceof MenuBar) {
        int anzMenus = ((MenuBar)mb).getMenuCount();
        for (int mn = 0; mn < anzMenus; ++mn)
           removeListenersMenu(((MenuBar)mb).getMenu(mn));
     } else if (mb instanceof JMenuBar) {
        int anzMenus = ((JMenuBar)mb).getMenuCount();
        for (int mn = 0; mn < anzMenus; ++mn)
           removeListenersMenu(((JMenuBar)mb).getMenu(mn));
     }
  } // removeListenersMenuBar(MenuContainer)


/** Removing all listeners of a (J)Menu. <br />
 *  <br />
 *  In the  {@link javax.swing.JMenu} {@code m} from all 
 *  {@link javax.swing.JMenuItem}s the ActionListeners are removed and
 *  from all JCheckboxMenuItems the ItemListeners.<br />
 *  Sub menus are visited recursively.<br />
 */
   public static void removeListenersMenu(JMenu m) {
      if (m == null) return;
      int anzItems = m.getItemCount();
      itemLoop: for (int in=0; in < anzItems; ++in) {
         JMenuItem mi = m.getItem(in);
         if (mi instanceof JMenu) {
            removeListenersMenu((JMenu) mi);
            continue itemLoop;
         }
         if (mi instanceof JCheckBoxMenuItem) {
            EventListener[] eli = mi.getListeners(ItemListener.class);
            int elil = eli.length;
            for (int i = 0; i < elil; ++i) {
               ((JCheckBoxMenuItem)mi).removeItemListener(
                                                 (ItemListener)eli[i]);
            } // remove-for  
         } 
         if (mi != null) {
            EventListener[] eli = mi.getListeners(ActionListener.class);
            int elil = eli.length;
            for (int i = 0; i < elil; ++i)
                mi.removeActionListener((ActionListener)eli[i]);
         }                                     
      } // itemLoop
   } // removeListenersMenu(JMenu)

/** Removing all listeners of a Menu. <br />
 *  <br />
 *  In the  {@link java.awt.Menu} m from all 
 *  {@link java.awt.MenuItem}s the ActionListeners are removed and
 *  from all CheckboxMenuItems the ItemListeners.<br />
 *  Sub menus are visited recursively.<br />
 */
   public static void removeListenersMenu(final Menu m){
      if (m == null) return;
      int anzItems = m.getItemCount();
      itemLoop: for (int in=0; in < anzItems; ++in) {
         MenuItem mi = m.getItem(in);
         if (mi instanceof Menu) {
            removeListenersMenu((Menu) mi);
            continue itemLoop;
         }
         if (mi instanceof CheckboxMenuItem) {
            EventListener[] eli = mi.getListeners(ItemListener.class);
            int elil = eli.length;
            for (int i = 0; i < elil; ++i)
               ((CheckboxMenuItem)mi).removeItemListener(
                                                 (ItemListener)eli[i]);
         }
         if (mi != null) {
            EventListener[] eli = mi.getListeners(ActionListener.class);
            int elil = eli.length;
            for (int i = 0; i < elil; ++i)
                mi.removeActionListener((ActionListener)eli[i]);
         }
      } 
   } // removeListenersMenu(Menu)


//---------------------------------------------------------------------------

/** Determine the MenuItems to an action command. <br />
 *  <br />
 *  This method designates a MenuItem represented by or contained in
 *  menuComp. Container like MenuBars or Menus are searched recursively.<br />
 *  <br />
 *  The search ends at the first CheckboxMenuItem and if check is false
 *  also MenuItem, having actionCommand set as action command.<br />
 *  <br />
 *  If no fitting item is found null is returned.<br />
 *  <br />
 *  Due to the recursive search the method may be a bit expensive. After
 *  generating menuComp the result for every interesting action command should
 *  be determined once and stored.<br />
 *
 *  @param menuComp the containing menu component<br /> &nbsp;
 *      &nbsp; &nbsp; (usually a MenuBar or a Menu)
 *  @param actionCommand the search criterion action command
 *  @param check true: consider only CheckboxMenuItems<br /> &nbsp;
 *      &nbsp; &nbsp; false: consider all MenuItems
 *  @return fitting [Checkbox-]MenuItem or null
 */
   public static MenuItem byAction(Object menuComp,
                                   String actionCommand, boolean check){
      if (menuComp == null || actionCommand == null 
                                    || actionCommand.isEmpty()) return null;
      itemCheck: if (menuComp instanceof MenuItem) {
          MenuItem mIt = (MenuItem)menuComp;
          if (!actionCommand.equals(mIt.getActionCommand()))
             break itemCheck;
          if (mIt instanceof CheckboxMenuItem || !check) return mIt;
      } // itemCheck

 
      menuCkeck: if (menuComp instanceof Menu) {
         Menu m = (Menu)menuComp;
         int anzIt = m.getItemCount();
         iL: for (int j = 0; j < anzIt; ++j) {
            MenuItem mIt = byAction(m.getItem(j), actionCommand, check);
            if (mIt != null) return mIt;
         }
      } // menuCkeck
      barCheck: if (menuComp instanceof MenuBar) {
         MenuBar menuBar = (MenuBar)menuComp;
         int anzMenus = menuBar.getMenuCount();
         if (anzMenus == 0) break barCheck;
         mL: for (int i = 0; i < anzMenus; ++i) {
            Menu m = menuBar.getMenu(i);
            if (m == null) continue mL;
            MenuItem mIt = byAction(m, actionCommand, check);
            if (mIt != null) return mIt;
         } // mL
          break barCheck; // breakable if
      } // barCheck
      return null;
   } // MenuItem byAction(Object, String, boolean)


/** Determine the CheckboxMenuItem to an action command. <br />
 *  <br />
 *  The call is equivalent to:<code><pre>
 *  (CheckBoxMenuItem)byAction(menuComp, actionCommand, true)</pre>
 *  </code>
 *  Only for better readability.<br />
 *
 *  @param menuComp the containing menu component<br /> &nbsp;
 *      &nbsp; &nbsp; (usually a MenuBar or a Menu)
 *  @param actionCommand the search criterion action command
 *  @return fitting CheckboxMenuItem or null
 */
   public static CheckboxMenuItem byAction(MenuComponent menuComp,
                                                     String actionCommand){
      return (CheckboxMenuItem)byAction(menuComp, actionCommand, true);
   } // byAction(MenuComponent, String)

/** Determine the JMenuItem to an action command. <br />
 *  <br />
 *  This method designates a JMenuItem represented by or contained in
 *  menuComp. Container like JMenuBars or JMenus are searched recursively.<br />
 *  <br />
 *  The search ends at the first JCheckboxMenuItem and if check is false
 *  also JMenuItem, having actionCommand set as action command.<br />
 *  <br />
 *  If no fitting item is found null is returned.<br />
 *  <br />
 *  Due to the recursive search the method may be a bit expensive. After
 *  generating menuComp the result for every interesting action command should
 *  be determined once and stored.<br />
 *  <br />
 *  @param menuComp the containing JMenu component<br /> &nbsp;
 *      &nbsp; &nbsp; (usually a JMenuBar or a JMenu)
 *  @param actionCommand the search criterion action command
 *  @param check true: consider only JCheckboxMenuItems<br /> &nbsp;
 *      &nbsp; &nbsp; false: consider all JMenuItems
 *  @return fitting J[Checkbox-]MenuItem or null
 */
   public static JMenuItem byAction(MenuElement menuComp,
                                      String actionCommand, boolean check){
      if (menuComp == null || actionCommand == null 
                                 || actionCommand.isEmpty()) return null;
      itemCheck: while (menuComp instanceof JMenuItem) {
          JMenuItem mIt = (JMenuItem)menuComp;
          if (!actionCommand.equals(mIt.getActionCommand()))
             break itemCheck;
          if (mIt instanceof JCheckBoxMenuItem || !check) return mIt;
          break itemCheck; // breakable if
      } // itemCheck
      menuCkeck: while (menuComp instanceof JMenu) {
         JMenu m = (JMenu)menuComp;
         int anzIt = m.getItemCount();
         iL: for (int j = 0; j < anzIt; ++j) {
            JMenuItem mIt = byAction(m.getItem(j), actionCommand, check);
            if (mIt != null) return mIt;
         }
          break menuCkeck; // breakable if
      } // menuCkeck
      barCheck: while (menuComp instanceof JMenuBar) {
         JMenuBar menuBar = (JMenuBar)menuComp;
         int anzMenus = menuBar.getMenuCount();
         if (anzMenus == 0) break barCheck;
         mL: for (int i = 0; i < anzMenus; ++i) {
            JMenu m = menuBar.getMenu(i);
            if (m == null) continue mL;
            JMenuItem mIt = byAction(m, actionCommand, check);
            if (mIt != null) return mIt;
         } // mL
          break barCheck; // breakable if
      } // barCheck
      return null;
   } // byAction(MenuElement, String, boolean)

/** Determine the JCheckboxMenuItem to an action command. <br />
 *  <br />
 *  The call is equivalent to:<code><pre>
 *  (CheckBoxMenuItem)byAction(menuComp, actionCommand, true)</pre>
 *  </code>
 *  @param menuComp the containing JMenu component<br /> &nbsp;
 *      &nbsp; &nbsp; (usually a JMenuBar or a JMenu)
 *  @param actionCommand the search criterion action command
 *  @return fitting JCheckboxMenuItem or null
 */
   public static JCheckBoxMenuItem byAction(MenuElement menuComp,
                                         String actionCommand){
      return (JCheckBoxMenuItem)byAction(menuComp, actionCommand, true);
   } // byAction(MenuElement, String)


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
 *
 *  @param img the picture to be surrounded
 *  @param bgColor background respectively margin colour (default {@link GrafVal#ws}).
 *  @param upM upper margin in pixel (0..400).
 *  @param loM leM - riM - ditto lower, left and right.
 *  @return the picture as buffered image and (if given in range) surrounded
 *          / enlarged by margins
 */ 
   public static BufferedImage surrondImage(final Image img, 
                           final Color bgColor,
                           int upM, final int loM, int leM, final int riM){
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
   } // surrondImage(Image, Color, 4*int)

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
                                         int newWidth, int newHeight){
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
   } // surrondImage(Image, Color, 2*int)
  

//=========================================================================   
   
/** <b>The menu bar factory</b>. <br />
 *  <br />
 *  An object of this class is a {@link SAXHandler}, factoring either a
 *  {@link MenuBar} or a {@link JMenuBar} depending on the property 
 *  {@link #swingy}). The XML syntax &mdash; just five tags as of V.111 
 *  &mdash; is quite simple. The <br /> &nbsp; &nbsp; 
 *  &nbsp; example &nbsp; <a href="./doc-files/ProzessBuBMb.xml"
 *  target="_top">ProzessBuBMb.xml</a><br />
 *  should be sufficient to explain the syntax.<br />
 *  <br />
 *  The properties of the menu elements are specified by parameters of the
 *  corresponding tags. Instead of label and shortcut parameter names prefixed
 *  by a two letter language code are allowed and evaluated using the 
 *  application's or platform's set language (named la in some examples below)
 *  as  a first choice. Example: de.label or pt.shortcut.<br />
 *  <br />
 *  For determining menu element properties by tag parameters the following 
 *  default or replacement rules apply in this sequence:<ol>
 *  <li> action = parameter action, as a substitute parameter en.label
 *       (sic!),</li>
 *  <li> id = parameter id, as a substitute action,</li>
 *  <li> label = parameter la.label, as a substitute label, as a substitute 
 *       en.label, as a substitute id, and as last substitute action,</li>
 *  <li> shortcut = parameter la.shortCut, as a substitute shortcut.<br />
 *       If this does not give an one character value, this is interpreted
 *       as no shortcut key wanted.</li>
 *  </ol>  
 *  Compared to the Java-Beans-XML-De/Encoder's syntax (available since 
 *  JDK1.4.0) this {@link SAXHandler}'s syntax to produce {@link MenuBar}s or
 *  {@link JMenuBar}s is<ul>
 *  <li> simpler,</li>
 *  <li> compatible over AWT and the Swing versions (1.4.x / 1.5.x) and</li>
 *  <li> it provides for the (inter) nationalising of labels and shortcuts
 *     within one XML description.</li></ul>
 *  
 *  <br />
 *  Copyright <a href="./package-summary.html#co">&copy;</a>
 *            2005 &nbsp; Albrecht Weinert<br />
 *
 *  @see de.frame4j.xml
 *  @see de.frame4j.text.TextHelper
 *  @see de.frame4j.util.PropMap
 *  @author   Albrecht Weinert
 *  @version  like embedding class
 */
@MinDoc(
   copyright = "Copyright 2005, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "see enclosing class GrafHelper",
   lastModified   = "see enclosing class GrafHelper",
   lastModifiedBy = "$Author: albrecht $",
   usage   = "make a (multilingual) XML description for a menubar and produce it",  
   purpose = "a factory for MenuBars"
   ) public static class MBarFactory  extends SAXHandler {

/** Providing the factory. <br />
 *   <br />
 *  A {@link SAXHandler} inheritor is made. It's {@link #name name} is the
 *  parameter value or &quot;...&quot;.<br />
 *  <br />
 *  Messages and reports use the {@code log} supplied.<br />
 *  XML (parser / syntax) errors will stop.<br />
 *  <br />
 *  @param name the handler's name
 *  @param language the preferred language's two letter code, default en
 *  @param swingy true: Swing; false: AWT
 *  @param log the log
 @see ParseErrorHandler#ParseErrorHandler(CharSequence, PrintWriter, boolean)
 */
      public MBarFactory(CharSequence name,  CharSequence language,
                                         boolean swingy, PrintWriter log){
         super(name, log, true);
         this.swingy = swingy;
         this.la  = TextHelper.checkLanguage(language, "en");
      } // MBarFactory(2*CharSequence, boolean, PrintWriter)

/** Swing, not AWT. <br /> */      
      protected boolean swingy;
      
/** Re-initialise the handler. <br /> */
      @Override public void startDocument(){ 
         mCs = new MenuContainer[5];
         mcL = -1;
         laLabel = la + ".label";
         laShortcut = la + ".shortcut";
         if (debug) log.println("\n\n MM     MBarFactory = " + name 
                     + " \t (" + la            
                     + (swingy ? ")   -- swing\n" : ")   --  AWT\n"));
      } // startDocument()


/** MenuContainer. <br />
 *  <br />
 *  [0]: Top MenuContainer (MenuBar or JMenuBar).<br />
 *  [1]: Menu in MenuBar (Menu or JMenu).<br />
 *  [2..4]: sub menus (max. 3 levels).<br />
 *  <br />
 *  @see #product()
 */      
      MenuContainer[] mCs;

/** MenuContainer / result. <br />
 *  <br />
 *  This method returns the MenuBar or JMenuBar made.<br />
 *  <br />
 *  @return a {@link MenuBar}, {@link JMenuBar} or null.
 *  @see #swingy
 */      
      public MenuContainer product(){ return mCs[0]; }
      @Override public Object getProduct() { return mCs[0]; }

/** MenuContainer recursion level. <br />
 *  @see #mCs
 */      
      int mcL;
      
/** Debug outputs. <br />
 *  @see #isDebug()
 */      
      protected boolean debug;

/** Debug outputs. <br />
 *  <br />
 *  If true reports on made elements will be output to {@link #log}.<br />
 */      
      public final boolean isDebug() { return this.debug; }

/** Debug outputs. <br />
 * 
 *  @see #isDebug()
 */
      public void setDebug(boolean debug) { this.debug = debug; }


/** Two letter language code (de, en, fr, etc.). <br />
 *
 *  @see #laLabel
 *  @see #laShortcut
 */  
      protected String la;

/** Language specific parameter names. <br />
 *  <br />
 *  Example: &quot;de.label&quot;, &quot;en.shortcut&quot;.<br />
 *  @see #la
 */ 
      protected String laLabel, laShortcut;

/** Grouping. <br /> */
      ButtonGroup  butG;
      
/** Start of a XML element. <br />
 *  <br />
 *  This implementation makes either a Swing or an AWT MenuBar, containing
 *  Menus with MenuItems, CheckboxMenuItems, separators or (recursively) sub
 *  menus.<br />
 *  <br />
 */
      @Override public void startElement(String namespaceURI,
                        String localName, String qName,
                        Attributes atts) throws SAXException {
         if ("separator".equals(qName)) { // menu-separator
            if (mcL < 1) {
               throw new SAXException("no menu yet to add separator");
            }
            if (swingy) {
               ((JMenu)mCs[mcL]).addSeparator();
            } else {
               ((Menu)mCs[mcL]).addSeparator();
            } 
            return;
         } // separator
         
         if ("checkgroup".equals(qName)) {
            if (debug) log.println("\n  <  group ...  " );
            if (butG != null)   //// || ckBG != null)
               throw new SAXException("illegal nested <checkgroup>");
            if (swingy) {
               butG = new ButtonGroup();
            } else {
              ////  ckBG = new CheckboxGroup();
            }
            return;  // group end
         } // Gruppe

         String action = atts.getValue("action");
         if (action == null) action = atts.getValue("en.label");
         String id = atts.getValue("id");
         if (id == null) id = action;
         
         if ("menubar".equals(qName)) { // TopElement menuBar
            if (mCs[0] != null || mcL >= 0) { // 2 menubars
               throw new SAXException("illegal multiple <menubar>");
            } // 2 menubars
            mcL = 0;
            if (swingy) {
               mCs[0] = new JMenuBar();
               if (id != null) ((JMenuBar)mCs[0]).setName(id);
             } else {
               mCs[0] = new MenuBar();
               if (id != null) ((MenuBar)mCs[0]).setName(id);
            }
            if (debug) log.println(" MMmbar id = " + id);
            return;
         } // TopElement menuBar; exactly once and at beginning

         if (mcL < 0) { // no menubar
            throw new SAXException("one <menubar> must be top");
         } // no menubar
         
         String label = atts.getValue(laLabel);
         if (label == null)  
            label = atts.getValue("label");
         if (label == null && !("de".equals(la)))  
            label = atts.getValue("de.label");
         if (label == null)
            label = id != null ? id : action;
         // label and id now determined, may be with default substitutes
         // action evtl. null
         if (label == null) { // no label
            throw new SAXException(
                    "<menus and menu-elements need label=.. >");
         } // no label
        
         if ("menu".equals(qName)) { // second level Elements menu
            ++mcL;
            if (swingy) {
               JMenu tmp = new JMenu(label);
               mCs[mcL] = tmp;
               tmp.setName(id);
            } else {
               Menu tmp = new Menu(label);
               mCs[mcL] = tmp;
               if (id != null) tmp.setName(id);
            }
            if (debug) log.println(" MMmenu id = " + id 
                       + " \tlevel = " + mcL  + " label = " + label);
            return;
         } // second level Elements menu
 
         if (mcL < 1) { // no menu
            throw new SAXException("no menu yet to add element" + qName);
         } // no menu
         if (action == null) {  // no action
            throw new SAXException(
                    "<menus and menu-items need action=.. >");
         } // no action
         
         boolean cb = "checkbox".equals(qName);
         
         if (cb || "menuitem".equals(qName)) { // MenuElement, item or check
            String shortC = atts.getValue("laShortcut");
            if (shortC == null)  shortC = atts.getValue("shortcut");
            char shCc = ' ';
            if (shortC == null || shortC.length() != 1) {
               shortC = null;
            } else shCc = shortC.charAt(0);
            
            boolean state = false;
            if (cb) {
               if ("true".equals(atts.getValue("set"))) state = true;
               if (debug) log.println(" MMchck action = "
                    + action + " \t cut = " + shCc   + " label = " + label
                        + (state ? " \tset" : " \toff"));
               
            } else {
               if (debug) log.println(" MMitem action = " + action
                       + " \t cut = " + shCc   + " label = " + label);
            }  
            
            if (swingy) {
               JMenuItem tmp = cb ? new JCheckBoxMenuItem(label, state)
                                       : new JMenuItem(label);
               tmp.setActionCommand(action);
               if (shortC != null) 
                  tmp.setMnemonic(shCc);
               
               if (cb && butG != null) {
                  butG.add(tmp);
                  if (state) ((JCheckBoxMenuItem)tmp).setSelected(true);
               }
               ((JMenu)mCs[mcL]).add(tmp);
            } else  {
               MenuItem  tmp = cb ? new CheckboxMenuItem(label, state)
                                       : new MenuItem(label);
               tmp.setActionCommand(action);
               if (shortC != null) 
                  tmp.setShortcut(new MenuShortcut(shCc, false));
               ((Menu)mCs[mcL]).add(tmp);
            }
            return;
         } // MenuElement, item or check
         
         // --------------  the rest  is (still) illegal -------------------
         throw new SAXException(
                             "MBarFactory knows nothing about <" + qName);
      } // startElement(3*String, Attributes)

/** End of a XML element. <br />
 *  <br />
 *  For &lt;/menubar&gt; and &lt;/menubar&gt; this implementation organises
 *  a tree structure of sub menus  down to a maximum level of three.<br />
 */
      @Override public void endElement(String namespaceURI,
                     String localName, String qName) throws SAXException {
      
         if ("menubar".equals(qName)) {
            if (debug) log.println("    ...MBar  >\n\n" );
            mcL = -1;
            return;  // only menu to work on
         }
         
         if ("checkgroup".equals(qName)) {
            if (debug) log.println("    ...group  >\n\n" );
            butG = null;
            //   ckBG = null;
            return;  // Group end
         }

         if (!("menu".equals(qName))) return;  // only menu to work on
         if (mcL == 1) { // top Menu zu
            if (debug) log.println("       ...MTop  >\n");
            if (swingy) {
               ((JMenuBar)mCs[0]).add((JMenu)mCs[1]);
            } else {
               ((MenuBar)mCs[0]).add((Menu)mCs[1]);
            } 
            mcL = 0; // done
            return;
         } // MenuBar zu
         
         if (debug) log.println("          ...MSub  >\n");
         if (swingy) {
            ((JMenu)mCs[mcL-1]).add((JMenu)mCs[mcL]);
         } else {
            ((Menu)mCs[mcL-1]).add((Menu)mCs[mcL]);
         } 
         --mcL; // Fertig 
      } // endElement(3*String)
   } // class MBarFactory  (27.04.2005, 03.03.2009) ===
} // class GrafHelper (16.07.2003, 03.03.2009)
   