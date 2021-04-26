/*  Copyright 2015 Albrecht Weinert, Bochum, Germany (a-weinert.de)
 *  All rights reserved.
 *  
 *  This file is part of Frame4J 
 *  ( frame4j.de  https://weinert-automation.de/software/frame4j/ )
 * 
 *  Frame4J is made available under the terms of the 
 *  Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-v10.html or as text in
 https://weinert-automation.de/java/docs/frame4j/de/frame4j/doc-files/epl.txt
 *  within the source distribution 
 */
package de.frame4j.graf;

import static de.frame4j.graf.GrafVal.STD_FONT;
import static de.frame4j.graf.GrafVal.ge;
import static de.frame4j.graf.GrafVal.gn;
import static de.frame4j.graf.GrafVal.hgr;
import static de.frame4j.graf.GrafVal.rt;
import static de.frame4j.graf.GrafVal.sw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.NoSuchElementException;

import javax.swing.JComponent;

import de.frame4j.graf.GrafHelper;
import de.frame4j.graf.GrafVal;
import de.frame4j.graf.ImageInfo;
import de.frame4j.graf.Paintable;
import de.frame4j.math.IntPairFix;
import de.frame4j.util.Prop;
import de.frame4j.text.TextHelper;

/** <b>A picture or tableau for graphical Java applications</b>. <br />
 *  <br />
 *  An object of this class is a {@link JComponent} (and hence a
 *  {@link java.awt.Component}), enhanced by some features:<ul>
 *  <li> An optional image from a file, a stream or another resource 
 *       (.gif, .png or .jpg) as {@link ImageInfo} object, </li>
 *  <li> serving as permanent background image, that may be added to and
 *       optionally changed.</li>
 *  <li> A list of paintable elements respectively {@link Paintable} objects
 *       assigned to a given spot and dimension within the image. Such element
 *       is once or multiple times tight to the image with 
 *       {@link Anchor Anchor} objects.</li>
 *  <li> Embedded classes respectively interfaces {@link Anchor Anchor}, 
 *       {@link Paintable Paintable}, {@link Element Element}
 *       and {@link Listener Listener}.</li>
 *  <li> Double buffering for flicker changes.</li>
 *  <li> Assigning of mouse events to {@link Paintable paintable} elements
 *       and forwarding the event, with {@link Paintable element} and
 *       {@link Anchor anchor} to a registered {@link Listener}.</li>
 *  <li> Possibility to draw an auxiliary grid (10*10 pixel) within the 
 *       (optional) margins of the {@link ImageInfo} object.</li>
 *  </ul>
 *  {@link Tableau.Anchor Anchor} is an inner class of Tableau. A
 *  {@link Tableau.Anchor} object hence knows its {@link Tableau Tableau}
 *  object and moors one {@link Paintable} in it.<br />
 *  <br />
 *  {@link Tableau} processes mouse events and assigns them to moored
 *  {@link Paintable} objects, within the dimensions of wich the mouse 
 *  was.<br />
 *  Mouse events pre-processed this way may be consumed by a registered 
 *  {@link Tableau.Listener Tableau.Listener}.<br />
 *  <br />
 *  {@link Tableau.Element Tableau.Element} is an implementation of 
 *  {@link Paintable} that may be extended. Even as is, it can act as
 *  respectively replace Labels and Buttons.<br /> 
 *  <br />
 *  <b> <a name="org" id="org">&nbsp; </a>
 *        Organisation of &quot;Pictures in Picture&quot;</b><br /> 
 *  <br />
 *  {@link Tableau} organises its presentment as a sequence of pictures 
 *  starting with<ul>
 *  <li>an {@link ImageInfo ImageInfo} object as background image<br />
 *   &nbsp; {@link #img}, set and modifiable at construction</li>
 *  </ul>via<ul>
 *  <li>a permanent background image<br />
 *      &nbsp; ({@link #imgPBg}, modifiable for mid term changes</li>
 *  </ul>and<ul>
 *  <li>the double buffer image<br />
 *      &nbsp; ({@link #imgDBu}, internal use only, serving for flicker free
 *      presentment</li>
 *  </ul>up to<ul>
 *  <li>the final presentment including all moored {@link Paintable} objects
 *     (mostly on screen) via a {@link java.awt.Graphics} object provided by
 *     the run time<br />
 *      &nbsp; may be manipulated via {@link #extraPaint extraPaint()}.</li>
 *  </ul>
 *  The image presented finally may, by {@link #copyImage copyImage()} or by
 *  {@link #copyImageInfo copyImageInfo()}, be extracted as copy.<br />
 *  <br />
 *  This (internal) organisation of Tableau allows firstly to avoid 
 *  multiple drawing of (mid term) stable elements and additionally a 
 *  presentment totally free of flicker. This, as well as the possibility to
 *  get actual copies, to serialise and de-serialise require no extra 
 *  programming at all when using or extending this class.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 1998 - 2002, 2004 &nbsp; Albrecht Weinert
 *  @author   Albrecht Weinert
 *  @version  $Revision: 33 $ ($Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $)
 */
 // so far    V00.00 (28.04.2000) :  ex weinertBib
 //           V00.05 (07.08.2001) :  Addenda and corrections for  facility
 //                           protoction / burglar alarm (Objektschutzsystem)
 //           V01.00 (19.08.2002) :  ImageInfo as permanent bg-image
 //           V.011+ (19.08.2009) :  Frame4J based  as Tableau
 //           V.140  (08.08.2015) :  comments improved 
 //                            (initially all German in original class Bild)

public class Tableau extends  JComponent implements MouseListener,
                                          MouseMotionListener, Serializable {

/** Version number for serialising.  */
   static final long serialVersionUID = 260153006500201L;
//                                      magic /Id./maMi

/** The Tableau last made. <br />
 *  <br />
 *  The {@link Tableau} object referred to ba {@link #lastTableau} will be
 *  used by some {@link Tableau.Element Tableau.Element} constructors for
 *  mooring the new element there with spot and dimension.<br />
 *  <br />
 *  Hint / warning: This procedere is in no way (!) threadsafe. It works well
 *  (and is operationally well-tried with automation applications) if a 
 *  factory application constructs one {@link Tableau} object with all moored
 *  {@link Element elements} in one run (single threaded) and then serialises
 *  it for later use in the target (automation) application.<br />
 */
   protected static Tableau lastTableau;


//-----      Inner classes and interfaces      ----------------------------

/** <b>The anchorage of a Paintable element in the Tableau</b>. <br />
 *  <br />
 *  {@link Tableau.Anchor Anchor} objects encapsulate the presence of
 *  {@link Paintable} objects including spot and dimension on the
 *  {@link Tableau}.<br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 1998 - 2001, 2004 &nbsp; Albrecht Weinert 
 *  @author   Albrecht Weinert
 *  @version  see embedding class 
 */
   
   public class Anchor implements Serializable {

/** Version number for serialising.  */
   static final long serialVersionUID = 260153006600201L;
//                                      magic /Id./maMi

/** The Paintable element &qout;moored&quot;. */
      protected Paintable element;

/** The Paintable element &qout;moored&quot;. */
      public Paintable getElement(){ return element; }

/**  Set the Paintable element &qout;moored&quot;. */
      public void setElement(Paintable element){ this.element = element; }


/** The element's spot (left upper corner) in the Tableau. <br />
 *  <br />
 *  @see #getLoc()
 *  @see Tableau.Anchor#dim
 */      
      protected Point loc;

/** The element's spot (left upper corner) in the Tableau. <br />
 *  <br />
 *  @see #getLoc()
 *  @see Tableau.Anchor#dim
 */      
      public Point getLoc(){ return loc; }

/** The element's spot (left upper corner) in the Tableau, X coordinate. <br />
 *  <br />
 *  @see #getLoc()
 *  @see Tableau.Anchor#dim
 */        
      public int getX(){ return loc.x; }

/** The element's spot (left upper corner) in the Tableau, Y coordinate. <br />
 *  <br />
 *  @see #getLoc()
 *  @see Tableau.Anchor#dim
 */      
      public int getY(){ return loc.y; }

/** Set the spots. <br />
 *  <br />
 *  @see #getLoc()
 *  @param loc will be taken as reference, (no copy/clone here)
 */
      public void setLoc(Point loc){ this.loc = loc; }

/** Set the spot. <br />
 *  <br />
 *  The current spot (Point loc) will be changed to the parameter values
 *  x and y. No new Point object loc will be made. Is the object loc shared
 *  with other Anchor objects, those will move too.<br />
 *  <br />
 *  @see #getLoc()
 *  @see #newLoc(int, int)
 *  @see #setLoc(Point)
 *  @param x coordinate
 *  @param y coordinate
 */
      public void setLoc(int x, int y){ loc.x = x; loc.y = y; }

/** Set the spot. <br />
 *  <br />
 *  The current spot (Point loc) will be changed to a new one made with the
 *  parameter values x and y. (A new Point object {@link #getLoc() loc} will
 *  be made.) <br />
 *  Is the previous object {@link #getLoc() loc} shared with other Anchor 
 *  objects, those will not move.<br />
 *  @see #setLoc(int, int)
 *  @param x coordinate
 *  @param y coordinate
 */
      public void newLoc(int x, int y){ loc = new Point(x, y); }

/** Move the spot. <br />
 *  <br />
 *  The current spot (Point loc) will be changed relatively by adding the
 *  parameter values dx and dy. No new Point object {@link #getLoc() loc} will
 *  be made. Is the object {@link #getLoc() loc} being shared with other 
 *  Anchor objects, those will move too.<br />
 *  @param dx coordinate delta
 *  @param dy coordinate delta
 */
      public void moveLoc(int dx, int dy){
         loc.x += dx;
         loc.y += dy;
     } // moveLoc(2*int)


/** The element's dimension within the tableau. <br />
 *  <br />
 *  Like the spot the dimension will be handed to the elements paint method.
 *  And both will be used for assigning mouse events to elements.
 */      
      protected Dimension dim;

/** The element's dimension within the tableau. <br />
 *  <br />
 *  Like the spot the dimension will be handed to the elements paint method.
 *  And both will be used for assigning mouse events to elements.
 */       
      public Dimension getDim(){ return dim; }

/** Set the element's dimension within the tableau. <br />
 *  <br />
 *  @param dim this reference will be taken (no copy / clone will be made)
 *  @see #setDim(int, int)
 */
      public void setDim(Dimension dim){ this.dim = dim; }

/** Set the element's dimension within the tableau. <br />
 *  <br />
 *  The elements dimension will be changed without making a new Dimension 
 *  object. If this dimension is shared with other elements they will see this
 *  change too.<br />
 *  <br />
 *  @see #setDim(Dimension)
 *  @see #newDim(int, int)
 *  @param width size
 *  @param height size
 */
      public void setDim(int width, int height){
          dim.width = width;
          dim.height = height;
      } // setDim(2*int) 

/** Set the element's dimension within the tableau. <br />
 *  <br />
 *  The elements dimension will be changed by making a new Dimension 
 *  object. If the previous dimension was shared with other elements those
 *  won't see this change.<br />
 *  <br />
 *  @see #setDim(int, int)
 *  @see #setDim(Dimension)
 */
      public void newDim(int width, int height){
          this.dim = new Dimension(width, height);
      } // newDim(int 


/** Constructor with setting. <br />
 *  <br />
 *  The parameters dim and loc won't get copied but taken as reference.
 *  Hence changes on them will affect all anchors and elements in
 *  question. <br />
 *  In other words: Such sharing allows scaling and moving multiple 
 *  elements in one action.<br />
 *  <br />
 *  @param element the element paintable within the tableau
 *  @param  loc    its place (0,0) is left upper;<br />
 *          null will get (a new) (-100, -100), hence well outside.
 *  @param  dim    its dimensions;<br />
 *          null will be a new (0, 0), hence usually invisibly small.
 */
      public Anchor(Paintable element, Point loc, Dimension dim){
         this.element = element;
         this.loc = loc != null ? loc : new Point(-100, -100);
         this.dim = dim != null ? dim : new Dimension(0, 0);
      } // Anchor(Paintable, Point, Dimension)


/** Constructor with setting. <br />
 *  <br />
 *  This is like:<br />
 *   &nbsp;    Anchor(element, loc, new Dimension(width, height)<br />
 */
      public Anchor(Paintable element, Point loc,  int width, int height){
         this(element, loc, new Dimension(width, height));
      } //  Anchor(Paintable, Point,  2* int)

/** Constructor with setting; centred. <br />
 *  <br />
 *  The coordinate parameters x and y relate to the element's centre. The 
 *  new Anchor's fields {@link #getDim() dim} and {@link #getLoc() loc} 
 *  will be made accordingly. The  {@link #getLoc() loc} made in the end
 *  will as usual point to the element's upper left corner.<br />
 *  <br />
 *  This is like:<br />
 *   &nbsp;    Anchor(element,  new Point ( x - width / 2, y - height / 2),
 *                     width, height)<br />
 *                     
 *  @param element the element paintable within the tableau
 *  @param  x   its place (x, y) as centre of the element
 *  @param  y   its place (x, y) as centre of the element
 *  @param  width the element's width
 *  @param  height the element's height
 */
      public Anchor(Paintable element, int x, int y, int width, int height){
         this(element,  new Point ( x - width / 2, y - height / 2),
                      width, height);
      } // Anchor(Paintable, 4*int)


/** Determine if paintable. <br />
 *  <br />
 *  This method checks if this Anchor hold an element paintable within this
 *  tableau.<br />
 *  <br />
 *  For the element to be considered paintable element, spot 
 *  ({@link #getLoc() loc}) and dimension ({@link #getDim() dim}) have to
 *  exist, and the rectangle described by {@link #getLoc() loc} and 
 *  {@link #getDim() dim} has to be within the tableau &mdash; at least
 *  partly: one pixel is enough.<br />
 *  <br />
 *  @see Tableau.Anchor#dim
 *  @see Tableau.Anchor#loc 
 *  @see Tableau.Anchor#element
 *  @return true: the anchored element is considered paintable 
 */
   public boolean isPaintable(){
      if (element == null || loc == null || dim == null) return false;
      return   (loc.x + dim.width >= 0)
             &&  (loc.y + dim.height >= 0)
             &&  (loc.x <= Tableau.this.img.getWidth())
             &&  (loc.y <= Tableau.this.img.getHeight());
   } // isPaintable()

/** Is a point within this anchor. <br />
 *  <br />
 *  This method checks if a point p lies with in the element's rectangle 
 *  as given by loc and dim. <br />
 *  The parameter s gives a minimal width and height to which this rectangle
 *  will eventually be widened around its centre. This allows to "hit"
 *  comfortably element with 0-dimension respectively very small ones.<br />
 *  <br />
 *  @param p the point in question.
 *  @param s widening the "hit area" to at least s * s pixel (if smaller) 
 *  @return true if the point is within the anchored rectangle (+ tolerance)
 */
      public boolean pointInside(final Point p, final int s){
        int tmp = s - dim.width;
        tmp = (tmp > 1) ? tmp / 2 : 0;
        if (p.x < (loc.x - tmp))  return false;
        if (p.x > (loc.x + tmp + dim.width))  return false;
        tmp = s - dim.height;
        tmp = (tmp > 1) ? tmp / 2 : 0;
        if (p.y < (loc.y - tmp))  return false;
        return (p.y <= (loc.y + tmp + dim.height));
     } // pointInside(Point, int) 

   } // class Anchor  (02.11.1998, 05.07.2004)


//===========================================================================


/** <b>An element paintable and "moorable" (fixable) within a tableau</b>.<br />
 *  <br />
 *  This ({@link Paintable}-) class is here to be a parent class for 
 *  paintable tableau elements. Nevertheless this class isn't abstract.<br />
 *  <br />
 *  Extending classes will override the three parameter methods
 *  {@link #paint paint()}, {@link #paintBg paintBg()} or 
 *  {@link #paintString paintString()}.<br />
 *  <br />
 *  Objects of this class will with these methods paint a rectangular
 *  elements showing a centred text (if set) on background colour.<br /> 
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright Albrecht Weinert 1998 - 2001, 2004, 2015
 *  @author   Albrecht Weinert
 *  @version  see embedding class
 *  @see  de.frame4j.graf.Tableau
 *  @see  Tableau.Element#paint
 *  @see  Tableau.Element#paintBg
 *  @see  Tableau.Element#paintString
 */
   public static class Element implements Paintable, Serializable {

/** Version number for serialising.  */
   static final long serialVersionUID = 260153006700201L;
//                                      magic /Id./maMi

/** The element's font. <br />
 *  <br />
 *  @see #getFont()
 */
      protected Font  font;

/** The element's font. <br />
 *  <br />
 *  Default: {@link GrafVal#STD_FONT}
 */
      public Font getFont(){ return font; }

/** Set the element's font. */
      public void setFont(final Font font){ this.font = font; }
      
/** The element's font. */
      protected String text;

/** The element's background colour. <br />
 *  <br />
 *  Default: {@link GrafVal#hgr hgr} 
 */
      protected transient Color bgColor = hgr;
      
/** The element's background colour.  */
      public void setBgColor(final Color bgColor){ this.bgColor = bgColor; }
      

/** The element's foreground colour. */
     public final Color getFgColor(){ return fgColor; }

/** Set the element's foreground colour. */
      public void setFgColor(final Color fgColor){ this.fgColor = fgColor;}

/** The element's foreground colour. <br />
 *  <br />
 *  Default: {@link GrafVal#sw sw}
 */
      protected Color fgColor = sw;
     
      
/** The element's text. <br />
 *  <br />
 *  This text, if not empty, will be used to label this element by this class'
 *  {@link #paint paint} methods. It can be changed any time by
 *  {@link #setText setText(String)}.<br />
 *  <br />
 */
      public String getText(){ return text; }

/** Set the element's text. <br />
 *  <br />
 *  @see #getText()
 */
      public void setText(final String text){ this.text = text; }


/** Constructor with full setting. <br />
 *  <br />
 *  The parameter text, if not empty, will be used to label this element by
 *  this class' {@link #paint paint} methods. It can be changed any time by
 *  {@link #setText setText(String)}.<br />
 *  <br />
 *  @param bgColor  background colour
 *  @param fgColor  foreground colour
 *  @param font null will get {@link GrafVal#STD_FONT}
 *  @param text first {@link #getText text} (may be null or empty)
 */
      public Element(Color bgColor, Color fgColor, Font font, String text){
          this.bgColor = bgColor;
          this.fgColor = fgColor;
          this.font    = font == null ? STD_FONT : font;
          this.text    = text == null || text.isEmpty() ? null : text;
      } // Element(2*Color, Font, String)

/** Constructor with full setting and mooring (anchorage). <br />
 *  <br />
 *  The element made will get moored in the Tableau last made. This means
 *  making an appropriate Anchor and adding it to the tableau's anchor 
 *  list.<br />
 *  <br />
 *  @param bgColor  background colour
 *  @param fgColor  foreground colour
 *  @param font null will get {@link GrafVal#STD_FONT}
 *  @param text first {@link #getText text} (may be changed) and also final
 *              configuration name (must not be empty)
 *  @param x    mooring place, left upper corner, x-coordinate
 *  @param y    mooring place, left upper corner, y-coordinate
 *  @param width  Anchor's width
 *  @param height Anchor's height
 *
 *  @exception  NoSuchElementException if no Tableau has been made yet
 *  @see Tableau#lastTableau 
 */
      public Element(final Color bgColor, final Color fgColor, 
                       final Font font, final String text, 
                final int x, final int y, final int width, final int height){
         if (lastTableau == null) 
             throw new NoSuchElementException ("No Tableau made yet");
         this.bgColor = bgColor;
         this.fgColor = fgColor;
         this.font    = font == null ? STD_FONT : font;
         this.text    = text == null || text.isEmpty() ? null : text;
         lastTableau.addAnchor( lastTableau.new Anchor(this,
                 new Point (x,y),  new Dimension (width, height) )  );
      } // Element(2*Color, Font, String, 4*int)


/** The one method to paint the element within the Tableau. <br />
 *  <br />
 *  This implementation paint a borderless rectangle by using the method 
 *  {@link #paintBg paintBg()}) in background colour and sets the foreground
 *  colour afterwards. If there is a non empty {@link #text} set it will be
 *  output centrified using the method 
 *  {@link #paintString paintString()}).<br />
 *  <br />
 *  If another phenotype is intended for this element this class has to be
 *  extended and one or more of the three methods mentioned have to be
 *  overriden.<br />
 */
      @Override public void paint(Graphics g, int x, int y, int width, int height){
         if (g == null) return;
         paintBg(g, x,y, width,height);
         if (bgColor != fgColor) {
            g.setColor(fgColor);
            paintString(g, x,y, width,height); 
         }  // fg != bg
      }  // paint(Graphics, Point, Dimension)


/** Paint the background. <br />
 *  <br />
 *  This implementation paints a border-less rectangle in background 
 *  colour.<br />
 *  <br />
 *  The actual colour of g is set to {@link #bgColor} when this method 
 *  returns.<br />
 */
      public void paintBg(Graphics g, int x, int y, int width, int height){
         if (g == null) return;
         g.setColor(bgColor);
         g.fillRect(x, y,   width, height);
      }  // paintBg(Graphics, Point, Dimension)

/** Previous Font (for paintString only). */
      protected Font oldFont;

/** Previous Text (for paintString only). */
      protected String oldText;

/** Previous height (for paintString only). */
      protected int oldHeight;

/** Previous width (for paintString only). */
      protected int oldWidth;

/** Relative text position measured (for paintString only). */
      protected IntPairFix relPos;


/** Paint the centred text. <br />
 *  <br />
 *  This method sets the foreground colour {@link #fgColor} (in g).<br />
 *  If {@link #text} is not or empty a {@link #font} will be set (if given). 
 *  The {@link #text}'s dimensions will be evaluated and it will be 
 *  output centred with respect to in d.<br />
 *  <br />
 *  This exact centring with evaluating the dimension is quite resource
 *  hungry. This method remembers the data ({@link #relPos}) together with
 *  the parameters ({@link #oldFont}, {@link #oldText} etc.), lest to repeat
 *  the same calculations over again. Inheritors are recommended to keep
 *  this procedere.<br />
 *  <br />
 *  This method uses
 *  {@link GrafHelper}.{@link GrafHelper#paintString paintString()}.<br />  
 */
      public void paintString(Graphics g, int x, int y, int width, int height){
         if (g == null) return;
         g.setColor(fgColor);
         if (text == null || text.isEmpty()) return;
         if (width < 6 || height < 6) return; // no text fits
         g.setFont(font);
         if (text != oldText || width != oldWidth || height != oldHeight
                   || font != oldFont || relPos == null) { // measure (exp.)
            oldText   = text;
            oldWidth  = width;
            oldHeight = height;
            oldFont   = font;
            relPos = GrafHelper.paintString(g, x, y, 
                                            width, height, text, null, null);
            return;    
         } // measure (expensive)
         g.drawString(text, x + relPos.x, y + relPos.y); 
      }  // paintString


   } //  class Element (02.08.2002, 08.08.2015)

//===========================================================================


/** <b>Information on control actions within the Tableau (by mouse)</b>. <br />
 *  <br /> 
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright Albrecht Weinert 1998 - 2000 
 *  @author   Albrecht Weinert
 *  @version see enclosing class
 *  @see  de.frame4j.graf.Tableau
 */
   public interface Listener {

/** Report method for control event. <br />
 *  <br />
 *  This method will be called for the (max.) one registered listener on 
 *  MOUSE_PRESSED, MOUSE_RELEASED und MOUSE_CLICKED on a Paintable element 
 *  within the Tableau as well as for MOUSE_MOVED and MOUSE_DRAGGED events.
 */
      public void bildBedient(Anchor a, MouseEvent m);

   } // interface Listener

//===========================================================================

/** Set the size. <br />
 *  <br />
 *  The size will be set finally on construction by the size of an image
 *  and margins given.<br />
 *  <br />
 *  This (java.awt.Component) method hence never has to be called.<br />
 *  <br />
 *  This implementation ignores the parameter d and calls super.setsize()
 *  (again) with the size determined on construction.<br />
 *  <br />
 *  @param d ignored
 */
   @Override public final void setSize(final Dimension d){
      w = img.getWidth();
      h = img.getHeight();
      super.setSize(w, h);
   } // setSize(Dimension)
   
/** Internal store for size. */   
   int w, h;   
   
/** The Tableau's width. */
   @Override public final int getWidth(){ return w; }

/** The Tableau's height. */
   @Override public final int getHeight(){ return h; }


/** The optional background image. <br />
 *  <br />
 *  If given this image as well as the background colour
 *  ({@link java.awt.Component#setBackground setBackground()}) determines the
 *  so called permanent background image. <br />
 *  Set finally on construction and fitted with margins if given this also 
 *  determines this {@link Tableau}'s size.<br />
 *  <br />
 *  @see #setSize(Dimension)
 */
   protected ImageInfo img;

/** The background as image (colour + optional image). <br />
 *  <br />
 *  This image constructed will be the "permanent background". It can, by an
 *  own graphic context {@link #getPermBgGraphics()} be retouched or reset by
 *  {@link #restorePermBg()} to the construction state.<br />
 *  <br />
 *  This permanent background image will be made on first demand, i.e. on the
 *  Tableau's first paint after construction, de-serialising or reset by
 *  {@link #restorePermBg()}. Than only it may be manipulated via  
 *  {@link #getPermBgGraphics()}.
 */
   protected transient BufferedImage   imgPBg;  // Tableau + background

/** The permanent background image's graphic context. */
   protected transient Graphics        grPBg;  // double buffer


/** The double buffer image. <br />
 *  <br />
 *  The double buffer image is on one hand for flicker free animations: <br />
 *  {@link #paint Tableau.paint()} and {@link #update Tableau.update()} do
 *  paint the permanent  background image {@link #imgPBg} an all moored
 *  Paintable Elements at first on the double buffer image. Afterwards in 
 *  one beat and without clearing the background the whole bunch will be 
 *  painted to the target graphic context. On a target (Tableau) screen
 *  one sees just the changes without flicker.<br />
 *  <br />
 *  The same complete actual content can, with  {@link #copyImageInfo()}, got
 *  as copy.<br />
 */
   protected transient BufferedImage  imgDBu;  // double buffer

/** The double buffer image's graphic context. <br /> */
   protected transient Graphics       grDBu;  // double buffer

//----------------------------   Anchor-Liste ------------------------------
   
/** The List (set) of moored Elements. <br />
 *  <br />
 *  Internal Implementation hints: <br />
 *  After two other approaches in past Frame4J versions the direct 
 *  implementation as array was the fastest. This was crucial to some 
 *  automation applications. 
 */
   protected Anchor[] anchorList;

/** Empty array of Anchor. */
   public static final Anchor[] NO_ANCHOR = new Anchor[0];
   
/** Number of Anchor objects. */
   int anchorCount;

/** Number of Anchor objects. */
   public final int getAnchorCount(){ return anchorCount; }

   
/** Check if an Anchor is moored in this Tableau. */    
   public final boolean contains(final Anchor a){
      if (a == null || anchorCount == 0) return false;
      for (int i = 0; i < anchorCount; ++i) {
         if (a == anchorList[i]) return true;
      }
      return false;
   } // contains(Anchor)

/** Add one Anchor. <br />
 *  <br />
 *  The same Anchor object will not be added twice. The Anchor list is a 
 *  set.<br />
 *  null will not be added.<br />
 *  <br />
 *  @see #contains(Tableau.Anchor)
 *  @see #removeAnchor(Tableau.Anchor)
 *  @see #clearAnchor()
 *  @see #ensureCapacity(int, int)
 *  @see #addAnchor(Tableau.Anchor[])
 *  @see #addAnchorReverse(Tableau.Anchor[])
 */
    public final void addAnchor(final Anchor a){
       if (a == null) return;
       if (anchorCount == 0) {  // empty
          if (anchorList == null || anchorList.length < 9) {
             anchorList = new Anchor[21];
          }  // first new 
          anchorList[0] = a;
          anchorCount = 1;
          return;
       } // empty
       for (int i = 0; i < anchorCount; ++i) { // for
          if (a == anchorList[i]) return; // contained
       } // for vgl
       if (anchorCount >= anchorList.length) 
          ensureCapacity(0, 30);
       anchorList[anchorCount] = a;
       anchorCount += 1;
    } // addAnchor(Anchor)

/** Ensure or increase the Anchor list's reserve capacity. <br />
 *  <br />
 *  This method will be called internally in an appropriate way. Normally it
 *  will never be called directly by the application.<br />
 *  <br />
 *  Factory programs knowing big capacity demand beforehand may get small
 *  improvements by avoiding incremental and non fitting increases.<br />
 *  <br />
 *  @param absMin  Minimal required capacity (absolut)
 *  @param minInc  Minimal required capacity increase relative to the 
 *                 actual ({@link #getAnchorCount() anchorageCount})
 */
   public final void ensureCapacity(int absMin, int minInc){
      minInc += anchorCount;
      if (minInc > absMin) absMin = minInc;
      minInc = anchorList != null ? anchorList.length : 0;
      if (absMin <= minInc) return;  // genug Platz
      Anchor[] tmp = anchorList;
      anchorList = new Anchor[absMin];
      if (anchorCount == 0) return;      
      System.arraycopy(tmp, 0, anchorList, 0, anchorCount);
    } // ensureCapacity(2*int)

/** Remove the Anchor list's reserve capacity empty. <br />
 *  <br />
 *  The (internal) list of Anchor objects will be reduced the exact size 
 *  (@link #getAnchorCount()}) actually needed. This method may be called
 *  after adding the very last Anchor to improve the footprint of very long
 *  running application (automation applications).<br />
 *  <br />
 *  This method will be called internally before serialisation.<br />
 *  <br />
 */
   public final void trimToSize(){
      if (anchorCount == 0) { // empty
         anchorList = null;
         return;
      } // empty
      int curCap = anchorList.length;
      if (curCap == anchorCount) return;
      Anchor[] tmp = anchorList;
      anchorList = new Anchor[anchorCount];
      System.arraycopy(tmp, 0, anchorList, 0, anchorCount);
    } // trimToSize()

    
/** Add multiple Anchors. <br />
 *  <br />
 *  The Anchor objects given in the array aa will be put in this Tableau's 
 *  Anchor list in ascending order. Overlapping Elements intended to be in the
 *  foreground have to have higher indices in aa.<br />
 *  <br />
 *  An Anchor object will not be added twice. A null entry will be ignored 
 *  also.<br />
 *  <br />
 *  @see #addAnchor(Tableau.Anchor)
 *  @see #addAnchorReverse(Tableau.Anchor[])
 *  @see #removeAnchor(Tableau.Anchor)
 *  @param aa Ein Feld von Anchor
 */
   public final void addAnchor(final Anchor[] aa){
       int aaL = aa == null ? 0 : aa.length;
       if (aaL == 0) return;
       ensureCapacity(22, aaL);
       for (Anchor a : aa) addAnchor(a);
    } // addAnchor(Anchor[])

/** Add multiple Anchors. <br />
 *  <br />
 *  The Anchor objects given in the array aa will be put in this Tableau's 
 *  Anchor list in reverse order. Overlapping Elements intended to be in the
 *  foreground have to have lower indices in aa.<br />
 *  <br />
 *  An Anchor object will not be added twice. A null entry will be ignored 
 *  also.<br />
 *  <br />
 *  @see #addAnchor(Tableau.Anchor)
 *  @see #addAnchor(Tableau.Anchor[])
 *  @see #removeAnchor(Tableau.Anchor)
 *  @param aa an array of Anchor objects
 */
   public final void addAnchorReverse(final Anchor[] aa){
       int aaL = aa == null ? 0 : aa.length;
       if (aaL == 0) return;
       ensureCapacity(22, aaL);
       for (int i = aaL - 1; i >=0 ; --i) 
          addAnchor(aa[i]);
    } // addAnchorReverse(Anchor[])

/** Remove an Anchor. <br />
 *  <br />
 *  @see #addAnchor(Tableau.Anchor)
 *  @see #clearAnchor()
 */
    public final void removeAnchor(final Anchor a){
       if (a == null || anchorCount == 0) return;
       int found = -1;
       for (int i = anchorCount-1; i >= 0; --i) { // for compare
          if (a == anchorList[i]) {
             found = i; // contained
             break;
          }
       } // for compare / find
       if (found == -1) return; // not found
       --anchorCount;
       for (int i = found; i < anchorCount; ++i ) {
          anchorList[i] = anchorList[i + 1] ; // shift 
       }
    } // removeAnchor(Anchor)

/** Clear all Anchors. <br />
 *  <br />
 *  The list of Anchor objects will be emptied.<br />
 */
    public final void clearAnchor(){ 
       anchorCount = 0;
       anchorList = null;
   } // clearAnchor()


//--------- Used on facility protection system HMI (start) -----------

/** Get the complete list of Anchor objects. <br />
 *  <br />
 *  This method returns a new array of fitting length giving a copy of the 
 *  actual internal list of Anchor objects.<br />
 *  <br />
 *  @see #clearAnchor()
 *  @see #addAnchor(Tableau.Anchor)
 *  @since V00.05 (07.08.01)
 */
   public final Anchor[] getAnchorListe(){
      Anchor[] tmp = anchorList;
      int cap = anchorCount;    // almost threadsafe
      if (cap == 0 || tmp == null || anchorList == null) 
         return NO_ANCHOR;

      if (cap >= tmp.length) 
         return tmp.clone();
      
      Anchor[] ret = new Anchor[cap];
      System.arraycopy(tmp, 0, ret, 0, cap);
      return ret;
   } // getAnchorListe()

/** Get all Anchors within a range. <br />
 *  <br />
 *  Ale Anchor objects, fitting completely within the rectangle given by
 *  p1 and p2 will be returned in paint order.<br />
 *  <br />
 *  p1 is the upper left point and p2 right lower.<br />
 *  This method facilitates multiple selects (in GUIs) significantly.<br />
 *  <br />
 *  The returned array's length is (exactly) the number of Anchors fitting
 *  into to the given rectangle if any. If none fits or exists null is
 *  returned.<br />
 *  <br />
 *  @since V00.05 (07.08.2001)
 *  @param p1 left upper; default (0, 0)
 *  @param p2 right lower; default the Tableau's dimension
 *  @return A new array of the fitting Anchors or null if none fits
 */
   public Anchor[] getAnchorInside(final Point p1, final Point p2){
      int x1 = 0;
      int y1 = 0;
      if (p1 != null) {
          x1 = p1.x;
          y1 = p1.y;
      }
      int x2, y2;
      if (p2 != null) {
          x2 = p2.x;
          y2 = p2.y;
      } else {
         x2 = getWidth();
         y2 = getHeight();
      }
      Anchor[] ret = null;
      int newSize = 0; 

      for (int i = 0; i < anchorCount; i++)  {
         Anchor a = anchorList[i];
         int x = a.getX();
         int y = a.getY();
         if (x < x1 || y < y1) continue;
         x += a.dim.width;
         y += a.dim.height;
         if (x > x2 || y > y2) continue;
         if (newSize == 0) {
            ret = new Anchor[anchorCount - i]; // so much yet max.
         }
         ret[newSize] = a;
         ++newSize;
      } // 
      if (newSize == 0) return null;
      if (newSize == ret.length) return ret;
      Anchor[] newret = new Anchor[newSize];
      System.arraycopy(ret, 0, newret, 0, newSize);
      return newret;
   } // getAnchorInside(2*Point)

//----------- addendum for GUI (facility protection etc) end ----------

/** Serialise the  Tableau. */
    private void writeObject(ObjectOutputStream oos) throws IOException {
       trimToSize();  // Anchor list's reserve dropped
       oos.defaultWriteObject();       
    } // writeObject(ObjectOutputStream)

    
/** An auxiliary grid (raster 10 pixel) shall be painted. */
    boolean  grid;

/** An auxiliary grid (raster 10 pixel) shall be painted. <br />
 *  <br />
 *  If there are margins around the Tableau {@link #img}  the auxiliary grid 
 *  will be within the margins only. The raster's upper left corner is
 *  ({@link ImageInfo#leM ImageInfo.leM }, 
 *  {@link ImageInfo#upM ImageInfo.upM}).<br />
 *  <br />
 *  @see  ImageInfo#surrondImage(Color, int, int, int, int) ImageInfo.surrondImage()
 *  @see  #setGrid setGrid()
 */
   public final boolean isGrid(){ return grid; }

/** An auxiliary grid (raster 10 pixel) shall be painted.<br />
 *  <br />
 *  @see #isGrid()
 */
   public void setGrid(final boolean grid){
       if (this.grid == grid) return;
       this.grid = grid;
       restorePermBg();
       repaint();
   } // setGrid(boolean)
    
/** Constructor with background colour und size. <br />
 *  <br />
 *  This constructor does NOT call {@link #retouche retouche()}.<br />
 *  <br />
 *  @param bgColor background colour
 *  @param width  Tableau's width
 *  @param heigth Tableau's height
 */
   public Tableau(final Color bgColor, final int width, final int heigth){
      img = ImageInfo.getInstance(null, width, heigth);
      setBackground(bgColor);
      addMouseListener(this);
      w = img.getWidth();
      h = img.getHeight();
      setSize(w, h);
      lastTableau = this;
   } // Tableau(Color, 2*int) 


/** Constructor with background colour and background image. <br />
 *  <br />
 *  The background image without margins determines this Tableau's size.<br />
 *  <br />
 *  This is:<br />
 *  &nbsp; &nbsp; Tableau(bgColor, imgFile, null, -1,0, 0,0) // no margin
 *  <br />
 *  @param imgFile file name (.gif, .png oder .jpg) of background images
 *  @param bgColor colour for background and or margins (default ws/white).
 *  @exception java.util.NoSuchElementException if no file can be loaded
 */
   public Tableau(Color bgColor, String imgFile){
      this(bgColor, imgFile, (Prop)null, -1,0, 0,0);
   } // Tableau(Color, String) 

/** Constructor with background colour, background image and margins. <br />
 *  <br />
 *  The background image and the margins determine the Tableau object's size.
 *  The Tableau {@link #img} will be made in that size (from imgFile and 
 *  the margins given). Under this construction the Tableau  {@link #img} may
 *  be retouched once by the method {@link #retouche retouche()}. The image 
 *  made so will be the part of a Tableau object's serialisation state and is
 *  the blue print for the permanent background image {@link #imgPBg}.<br />
 *  <br /> 
 *  If just one of the margins is outside the allowed range 0 .. 400 there 
 *  will be no margins at all.<br />
 *  <br />
 *  This is: <br /> &nbsp; &nbsp; &nbsp; {@link
 *  #Tableau(java.awt.Color,java.lang.String,de.frame4j.util.Prop,int,int,int,int)
 *        Tableau(bgColor, imgFile, null, oR, uR, lR, rR)}.<br />
 *  <br />
 *  @param imgFile file name (.gif, .png oder .jpg) of background images
 *  @param bgColor colour for background and or margins (default ws/white).
 *  @param oR upper margin in pixel (0..400).
 *  @param uR lR - rR - dto. low (unten), left and right.
 *  @see GrafHelper#surrondImage(java.awt.Image, Color, int, int, int, int) 
 *            GrafHelper.surrondImage()
 *  @exception java.util.NoSuchElementException if no file can be loaded
 */ 
   public Tableau(final Color bgColor, final String imgFile, 
                                           int oR, int uR, int lR, int rR){
      this(bgColor, imgFile, (Prop)null, oR, uR, lR, rR);
   } // Tableau(Color, String, 4*int) 

/** Constructor with background colour, background image and margins. <br />
 *  <br />
 *  This constructor is mostly like {@linkplain
 *  #Tableau(java.awt.Color,java.lang.String,int,int,int,int)
 *  the one} without the
 *  {@link de.frame4j.util.Prop Prop} parameter prop.<br />
 *  <br />
 *  The difference is in case of failure to get the image from the file named
 *  and if prop is not null it will be tried to load the background image with 
 *  {@link  de.frame4j.util.Prop#getAsStream Prop.getAsStream()}. By this
 *  one could get is as resource from the application's .jar file. 
 *  Additionally an image file name not given by parameter imgFile will be 
 *  determined by the property bgFile in prop.<br />
 *  <br />
 *  @param imgFile file name (.gif, .png oder .jpg) of background images
 *  @param bgColor colour for background and or margins (default ws/white).
 *  @param prop A Prop object as has to find values and resources.
 *  @param oR upper margin in pixel (0..400).
 *  @param uR lR - rR - dto low (unten), left and right.
 *  @see GrafHelper#surrondImage(java.awt.Image, Color, int, int, int, int) 
 *            GrafHelper.surrondImage()
 *  @exception java.util.NoSuchElementException if no file or ressource can be 
 *           loaded
 */ 
   public Tableau(final Color bgColor, String imgFile, final Prop prop,
                     final int oR, final int uR, final int lR, final int rR){
      imgFile = TextHelper.makeFName(imgFile, null);
      if (imgFile == null) {
         if (prop == null) 
            throw new NoSuchElementException (
                 "No image file name (no param. & no prop)");
         imgFile = TextHelper.makeFName(prop.getString("bgFile"), null);
         if (imgFile == null) 
            throw new NoSuchElementException (
                 "No image file name (no param. & not in prop)");
      }
      setBackground(bgColor);
      img = ImageInfo.getInstance(imgFile, "gif", prop);
      if (img == null || img.getImg() == null) // 19.08.2002 11:21
         throw new NoSuchElementException ("No contained image " + imgFile);

      w = img.getWidth();
      h = img.getHeight();
      if (w <= 0 || h <= 0 ) // 19.08.2002 11:25
         throw new NoSuchElementException ("Dimless contained image " + imgFile);

      img.surrondImage(getBackground(), oR, uR, lR, rR);
      w = img.getWidth();
      h = img.getHeight();

      addMouseListener(this);
      setSize(img.getWidth(), img.getHeight());
      Graphics g = img.getGraphics();
      retouche(g, img.getWidth(), img.getHeight());
      g.dispose();
      lastTableau = this;
   } // Tableau(Color, String, Prop, 4*int) 

/** Constructor with background colour, background image and margins. <br />
 *  <br />
 *  This constructor is mostly like {@linkplain
 *  #Tableau(java.awt.Color,java.lang.String,int,int,int,int)
 *  the one} without the parameter klass.<br />
 *  <br />
 *  The difference is in case of failure to get the image from the file named
 *  and if prop is not null it will be tried to load the background image with 
 *  {@link  de.frame4j.util.Prop#getAsStream Prop.getAsStream()}. By this
 *  one could get is as resource from the application's .jar file. 
 *  Additionally an image file name not given by parameter imgFile will be 
 *  determined by the property bgFile in prop.<br />
 *  <br />
 *  @param imgFile file name (.gif, .png oder .jpg) of background images
 *  @param bgColor colour for background and or margins (default ws/white).
 *  @param klass A Class object to find resources.
 *  @param oR upper margin in pixel (0..400).
 *  @param uR lR - rR - dto low (unten), left and right.
 *  @see GrafHelper#surrondImage(java.awt.Image, Color, int, int, int, int) 
 *            GrafHelper.surrondImage()
 *  @exception java.util.NoSuchElementException if no file or ressource can be 
 *           loaded
 */ 
   public Tableau(final Color bgColor, String imgFile, final Class<?> klass,
                     final int oR, final int uR, final int lR, final int rR){
      imgFile = TextHelper.makeFName(imgFile, null);
      setBackground(bgColor);
      img = ImageInfo.getInstance(imgFile, "gif", klass);
      if (img == null || img.getImg() == null) // 19.08.2002 11:21
         throw new NoSuchElementException ("No contained image " + imgFile);

      w = img.getWidth();
      h = img.getHeight();
      if (w <= 0 || h <= 0 ) // 19.08.2002 11:25
         throw new NoSuchElementException ("Dimless contained image " + imgFile);

      img.surrondImage(getBackground(), oR, uR, lR, rR);
      w = img.getWidth();
      h = img.getHeight();

      addMouseListener(this);
      setSize(img.getWidth(), img.getHeight());
      Graphics g = img.getGraphics();
      retouche(g, img.getWidth(), img.getHeight());
      g.dispose();
      lastTableau = this;
   } // Tableau(Color, String, Prop, 4*int) 


/** One time change (retouching) the loaded and margined Tableau. <br />
 *  <br />
 *  The constructors loading and marginising a background image will call
 *  this method only once to enable final manipulations of this Tableau 
 *  before use (in an extending class). This implementation does
 *  nothing.<br />
 *  <br />
 *  @param g Malen in {@link #img}
 *  @param width width in pixels
 *  @param height height in pixels
 */
   public void retouche(final Graphics g, final int width, final int height){}

/** Get the preferred size. <br />
 *  <br />
 *  The methods {@link #getPreferredSize()}, {@link #getMaximumSize()}
 *  and {@link #getMinimumSize()} return the same size set at construction.
 *  (see {@link #img}).<br />
 *  <br />
 *  @see #getHeight()
 *  @see #getWidth()
 */
   @Override public final Dimension getPreferredSize(){ return img.getSize();}

/** Get the minimal size. <br />
 *  <br />
 *  @see  #getPreferredSize()
 */
   @Override public final Dimension getMinimumSize(){ return img.getSize();}

/** Get the maximal size. <br />
 *  <br />
 *  @see  #getPreferredSize()
 */
   @Override public final Dimension getMaximumSize() {return img.getSize();}

/** Get the preferred X alignment (0.5F). */
   @Override public float getAlignmentX(){ return 0.5F; } // no effect

/** Get the preferred Y alignment (0.5F). */
   @Override public float getAlignmentY(){ return 0.5F; } // no effect


/** Prepare the permanent background and double buffer image. <br />
 *  <br />
 *  Internal use. Will be called if imgDBu is null.
 */
   synchronized void prepareDBu(){
      int w = img.getWidth();
      int h = img.getHeight();
      imgDBu =  new BufferedImage(w, h,
                                  BufferedImage.TYPE_INT_ARGB); 
                //16:53 04.01.2002           BufferedImage.TYPE_INT_RGB); 
                //                BufferedImage.TYPE_3BYTE_BGR); 
                //            createImage(dim.width,dim.height);
      imgPBg =  new BufferedImage(w, h,
                                   BufferedImage.TYPE_INT_ARGB); 
      grPBg = imgPBg.getGraphics();
      grPBg.setColor(getBackground());
      grPBg.fillRect(0,0, w, h); // background
      if (img != null) grPBg.drawImage(img.getImg(), 0,0, this); 

      if (grid) {    /// grid for test
         w = w - img.riM - 6;
         h = h - img.loM - 6;
         for (int x = img.leM + 10; x < w - 3 ; x += 10 ) {
            grPBg.setColor (x % 50 == 0 ? x % 100 == 0 
                                    ? rt : gn : ge);
            grPBg.drawLine (x , 6,   x , h);
         }
         for (int y = img.upM + 10; y < h - 3 ; y += 10 ) {
            grPBg.setColor (y % 50 == 0 ? y % 100 == 0
                                    ? rt : gn : ge);
            grPBg.drawLine (6 , y , w, y);
         } // for
      } //  Gitter

      grDBu  = imgDBu.getGraphics();
    } // prepareDBu()


/** Update method. <br />
 *  <br />
 *  This method will be called by the underlying (Windows) system, too. It
 *  shall renew the presentment.<br />
 *  <br />
 *  Hint: This method has not to be called directly and this should not
 *  be done, at least never with the double buffer's graphic context. Doing so
 *  would spoil all the <a href="#org">organisation</a>; see also
 *  {@link #paint paint()}.
 */
   @Override public final void update(final Graphics g){
      if (imgDBu == null) prepareDBu();
      grDBu.setColor(getBackground());
      paintImpl(grDBu);
      g.drawImage(imgDBu, 0,0, this);
   } // update(Graphics g) 


/** Copy the Tableau's actual content. <br />
 *  <br />
 *  This method returns the Tableau's actual look as a
 *  {@link java.awt.image.BufferedImage} object. The content will be the same
 *  as the last double buffer image {@link #imgDBu} shown by this 
 *  Tableau.<br />
 *  <br />
 *  @see #copyImageInfo() 
 */
   public final BufferedImage copyImage(){
      if (imgDBu == null) prepareDBu();
      int w = img.getWidth();
      int h = img.getHeight();
      BufferedImage ret =  new BufferedImage(w, h,
                                  BufferedImage.TYPE_INT_ARGB); 
      Graphics gr = ret.getGraphics();
      gr.setColor(getBackground());
      paintImpl(gr);
      gr.dispose();
      return ret;
   } // copyImage() 

/** Copy the actual Tableau content. <br />
 *  <br />
 *  This method returns the Tableaue's actual look as an {@link ImageInfo}
 *  object getting the same information on margins, filename etc. as
 *  {@link #img}. The content will be the same as the last double 
 *  buffer image {@link #imgDBu} shown by this Tableau.<br />
 *  <br />
 *  @see #copyImage() 
 */
   public final ImageInfo copyImageInfo(){
      if (imgDBu == null) prepareDBu();
      ImageInfo ret =  (ImageInfo) img.clone();
      Graphics gr = ret.getGraphics();
      gr.setColor(getBackground());
      paintImpl(gr);
 ///     gr.dispose();
      return ret;
   } // copyImageInfo()


/** The permanent background's graphic context. <br />
 *  <br />
 *  This graphic context allows to manipulate or modify the permanent 
 *  background {@link #imgPBg} in an arbitrary way. Synchronising this 
 *  manipulation with other activities concerning the Tableau is the
 *  user's responsibility.
 *  <br />
 *  @return null if there's no permanent background available (yet).
 */
   public Graphics getPermBgGraphics(){
      if (imgDBu == null) prepareDBu();
      return grPBg;
   } // getPermBgGraphics()

/** Re-making the permanent background. <br />
 *  <br />
 *  If the permanent background has been modified or supplied with an 
 *  auxiliary grid it will be re-build according to constructor settings
 *  (colour and optional Tableau {@link #img}) on next {@link #paint paint()}, 
 *  {@link #update update()} or {@link #getPermBgGraphics()}.
 */
   public synchronized void restorePermBg(){ imgDBu = null; }
   

/** Paint method. <br />
 *  <br />
 *  Only on request by the underlying system this method updates the 
 *  the permanent background and all moored {@link Paintable} elements.<br />
 *  <br />
 *  Generally in an application this look should be determined only by the
 *  moored {@link Paintable} elements 
 *  {@link Paintable#paint Paintable.paint()} methods. Hence to protect this
 *  internal  <a href="#org">organisation</a> this method and
 *  {@link #update update()} cannot be overridden (final).<br />
 *  <br />
 *  If the paint procedure has to be modified one has to ovveride th 
 *  method {@link #extraPaint extraPaint()}.
 */
   @Override public final void paint(final Graphics g){
      if (g != grDBu) {
          update(g);
          return;
      }
      paintImpl(g);
   } // paint(Graphics) 


/** Internal paint implementation for the whole Tableau. */
   final void paintImpl(final Graphics g){
      g.setColor(getBackground());
      g.drawImage(imgPBg, 0,0, this);  // permanenter background

      extraPaint(g, true);
      Anchor a; 
      for (int i = 0; i <  anchorCount; ++i) {
         a = anchorList[i];
         if (a.isPaintable()) a.element.paint(g, a.loc, a.dim);
      } 
      extraPaint(g, false);
   } // paintImpl(Graphics) 


/** Paint method. <br />
 *  <br />
 *  This method will be called by {@link #paint paint()} (see 
 *  {@linkplain #paint dort}) before (beforElements = true) or after  
 *  (beforElements = false) the painting of all moored {@link Paintable}
 *  elements.<br />
 *  <br />
 *  This implementation does nothing with is good for all ausual cases.
 */
   public void extraPaint(Graphics g, boolean beforElements){}



//=============  MouseListener - methods  ----------------------------

   boolean     mouseInside;
   Point       mouseLocation = new Point (-1,-1);
   Anchor      mouseElement;

 /** The registered listener. <br /> */
   transient Listener listener;


/** Set a Tableau listener. */
   public final void addListener(Listener l){ listener = l; }
   

/** Mouse motion shall be used or not. <br />
 *  <br />
 *  @param motion true: mouse motions will be captured and forwarded;<br />
 *   false: mouse motions will be ignored for this Tableau.
 */
   public final void setMotion(final boolean motion){
      if (motion) 
         addMouseMotionListener(this);
      else
         removeMouseMotionListener(this); 
   } // setMotion(boolean) 

   
/** Mouse entered. <br />
 *  <br />
 *  This event will be forwarded to a Tableau listener (with
 *  Element == null).
 */
   @Override public final void  mouseEntered(final MouseEvent m){ 
      mouseInside = true;  
      if (listener != null) 
         listener.bildBedient(null, m); // Element, Event 
   } //  mouseEntered(MouseEvent)
   
/** Mouse exited. <br />
 *  <br />
 *  This event will be forwarded to a Tableau listener (with
 *  Element == null) and leads to repaint() afterwards. 
 */
   @Override public final void  mouseExited(final MouseEvent m){ 
      mouseInside = false; 
      if (listener != null) 
         listener.bildBedient(null, m); // Element, Event 
      repaint();  
   } // mouseExited(MouseEvent)


/** Mouse moved. <br />
 *  <br />
 *  The event will be forwarded with Element==null.<br />
 */
   @Override public final void  mouseMoved(final MouseEvent m){
      mouseInside = true;
      mouseLocation.x = m.getX();
      mouseLocation.y = m.getY();
      mouseElement = null;
      if (listener != null) 
         listener.bildBedient(null, m); // Element, Event 
   } //  mouseMoved(MouseEvent)
   
/** Mouse dragged (moved with key pressed). <br />
 *  <br />
 *  The event will be forwarded with with the Element determined with the last 
 *  (connected) pressed event.
 */
   @Override public final void mouseDragged(final MouseEvent m){
      mouseInside = true;
      mouseLocation.x = m.getX();
      mouseLocation.y = m.getY();
      if (listener != null) 
         listener.bildBedient(mouseElement, m); // Element, Event 
   } // mouseDragged(MouseEvent)
   
   
/** Mouse clicked. <br />
 *  <br />
 *  The event will be forwarded with with the Element determined.<br />
 *  <br />
 *  Elements, last put in the  Anchor list, will be painted later, that is
 *  more prominent, in the Tableau. To get the most prominent Element this
 *  method searches the Anchor list backwards until the the first match.<br />
 *  <br />
 */
   @Override public final void mouseClicked(final MouseEvent m){
      if (listener == null) return;
      mouseInside     = true;
      mouseLocation.x = m.getX();
      mouseLocation.y = m.getY();
      mouseElement    = null;

      //----   determine Element concerned  ------
      Anchor a;
      such: for (int s = 7; s < 20; s+=7) {
      for (int i = anchorCount - 1; i >=  0; --i) {
         a = anchorList[i];
         if (a.pointInside(mouseLocation, s)) {
            mouseElement = a;
            break such;
         }
      }} // for for
    
      if (listener != null) {
         listener.bildBedient(mouseElement, m); // Element, Event 
      } 
   } // mouseClicked

   
/** Mouse pressed. <br />
 *  <br />
 *  The event will be forwarded with with the Element determined to a
 *  Tableau listener.
 */
   @Override public final void  mousePressed(MouseEvent m){ mouseClicked(m); }
   
   
/** Mouse released. <br />
 *  <br />
 *  The event will be forwarded with with the Element determined to a
 *  Tableau listener.
 */
   @Override public final void mouseReleased(MouseEvent m){ mouseClicked(m); }

}  // Tableau (11.09.2002, 08.08.2015)
