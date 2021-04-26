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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics;

import de.frame4j.math.ConstIntPair;
import de.frame4j.math.IntPairFix;
import de.frame4j.util.MinDoc;

/** <b>Paintable anywhere within given bounds</b>. <br />
 *  <br />
 *  Objects implementing this interface are (also) graphical elements, which
 *  can be painted / paint them selfs anywhere (in any graphical context) and
 *  within a given (rectangle) bound.<br />
 *  <br />
 *  This ability is to be implemented by one method with the 
 *  signature {@link #paint(Graphics, int, int, int, int)}. This method must 
 *  be overridden in implementing classes needing this feature (and not being
 *  content with this default implementation (just drawing a filled 
 *  rectangle).<br />
 *  The two other methods with shorter signatures are
 *  {@link #paint(Graphics, Point, Dimension)} (legacy) and 
 *  {@link #paint(Graphics, ConstIntPair, ConstIntPair)}. They can be used 
 *  wherever their signature is more fitting or better readable. Both shall 
 *  be left unchanged, as they delegate to 
 *  {@link #paint(Graphics, int, int, int, int)}.
 *  <br />
 *  There is a wee similarity to the (younger compared to Frame4J's 
 *  predecessor) interface {@link javax.swing.Icon}. The differences are:<ul>
 *  <li>A {@link javax.swing.Icon} object defines a fixed size. The 
 *      {@link Paintable} gets the (maximum) size with the paint call.</li>
 *  <li>A {@link Paintable} object defines all else properties of it's 
 *     appearance. A {@link javax.swing.Icon swing.Icon} on the other hand 
 *     may copy them from another {@link Component}.</li></ul> 
 *  
 *  It may make sense to implement both interfaces in one class.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright  2009 &nbsp; Albrecht Weinert  
 *  @see  WeAutLogo
 */
 // so far    V00.00 (10.10.1998 14:57) :  new 
 //           V02.00 (24.04.2003 16:54) :  CVS Eclipse
 //           V.o52+ (21.01.2009 16:54) :  ported and annotated 

@MinDoc(
   copyright = "Copyright 1998 - 2004, 2009, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "just paint it",  
   purpose = 
        "type for all elements, paintable anywhere within a given rectangle"
) public interface Paintable {

/** The (one) paint method. <br />
 *  <br />
 *  This method orders this {@link Paintable} object to paint itself to a
 *  given location and a given size.<br />
 *  <br />
 *  The contract / intent is, that 
 *  {@link #paint(Graphics, int, int, int, int) paint(...)} should give a 
 *  (simple) graphical representation of this object as does 
 *  {@link Object#toString()} in text form.<br />
 *  This interface's two other methods default implementations delegate to
 *  this method if applicable or do nothing. Hence, this is the one method 
 *  to be overridden in the implementing type.<br /> 
 *  
 *  @param g The notorious {@link java.awt.Graphics} object (that can
 *           nowadays be expected to be a {@link java.awt.Graphics2D}).<br />
 *           If null this method does nothing.
 *  @param x y are the left upper corner of where to paint; 
 *           y grows downwards [sic!]
 *  @param width height are the size of an surrounding rectangle
 *           to paint within; if null (0, 0) either this object's
 *           &quot;natural&quot; size (if there is such thing) shall be used
 *           or nothing is to be done
 */
   public abstract void paint(Graphics g, int x, int y, int width, int height);
/*  public default void paint(Graphics g, int x, int y, int width, int height){
     if (g == null) return;
     if (width == 0 && height == 0) return;
     g.fillRect(x, y,  width, height);
  } xxxx   */

/** The (legacy) paint method. <br />
 *  <br />
 *  This method orders this {@link Paintable} object to paint itself to a
 *  given location and a given size.<br />
 *  <br />
 *  The contract / intent is, that 
 *  {@link #paint(Graphics, Point, Dimension) paint(...)} should give a 
 *  (simple) graphical representation of this object as does 
 *  {@link Object#toString()} in text form.<br />
 *  <br />
 *  @param g The notorious {@link java.awt.Graphics} object (that can
 *           nowadays be expected to be a {@link java.awt.Graphics2D}).
 *  @param p The left upper corner of where to paint; if null (0,0)
 *           shall be used
 *  @param d The size of an surrounding rectangle to paint within; if null
 *           the object &quot;natural&quot; size (if there is such thing) 
 *           shall be used
 */
  public default void paint(Graphics g, Point p, Dimension d){
     if (g == null) return;
     final int x = p == null ? 0 : p.x;
     final int y = p == null ? 0 : p.y;
     final int w = d == null ? 0 : d.width;
     final int h = d == null ? 0 : d.height;
     paint(g, x, y,  w, h);
  } // paint(Graphics, Point, Dimension)

/** The (enhanced) paint method. <br />
 *  <br />
 *  This method orders this {@link Paintable} object to paint itself to a
 *  given location and a given size.<br />
 *  <br />
 *  Except for using immutable number pairs for location and size it (its
 *  default implementation) does exactly the same as
 *  {@link #paint(Graphics, Point, Dimension)}.<br />
 *  <br />
 *  @param g The notorious {@link java.awt.Graphics} object (that can
 *           nowadays be expected to be a {@link java.awt.Graphics2D}).
 *  @param p The left upper corner of where to paint; if null (0,0)
 *           shall be used
 *  @param d The size of an surrounding rectangle to paint within; if null or
 *           {@link IntPairFix#ZEROS} this object's &quot;natural&quot; size
 *           (if there is such thing) shall be used
 */
  public default void paint(Graphics g, ConstIntPair p, ConstIntPair d){
     if (g == null) return;
     final int x = p == null ? 0 : p.getX();
     final int y = p == null ? 0 : p.getY();
     final int w = d == null ? 0 : d.getX();
     final int h = d == null ? 0 : d.getY();
     paint(g, x, y,  w, h);
  } // paint(Graphics, 2*IntPair)

} // Paintable (16.09.2004, 04.03.2009, 24.08.2016)


