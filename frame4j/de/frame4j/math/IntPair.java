/*  Copyright (c) 2016 Albrecht Weinert, Bochum, Germany (a-weinert.de)
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
package de.frame4j.math;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

/** <b>A pair of integer numbers</b>. <br />
 *  <br />
 *  Objects of this class hold a pair ({@link #getX x}, {@link #getY y}) of int 
 *  values.<br />
 *  Contrary to {@link IntPairFix}, {@link IntPair}s are mutable. 
 *  <br />
 *  Hence,  when {@link IntPairFix}s were used to substitute  {@link Point},
 *  {@link Dimension}, {@link Rectangle} and others due to  their design
 *  flaws, {@link IntPair}s can substitute them under a common 
 *  {@link ConstIntPair type} in the rare cases, where their mutability is 
 *  really used, e.g. for changeable common locations of multiple 
 *  objects.<br />
 *  In those cases best use Albrecht Weinert's
 *  {@link ConstIntPair const interface patters} by giving the dependent 
 *  objects a watertight {@link #constView() r/o view}.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2016 &nbsp; Albrecht Weinert 
 *  
 *  @author   Albrecht Weinert
 *  @version  $Revision: 39 $ ($Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $)
 *  @see de.frame4j.math
 *  @see de.frame4j.math.Complex
 *  @see de.frame4j.math.CFun
 */
 // so far    V.184  (26.08.2016) : new
 //           V.009  (21.09.2016) : fault removed; revJump SVN server bug

public final class IntPair implements ConstIntPair, Serializable {

/** No public constructor. <br /> */
  private IntPair(final int x, final int y){ this.x = x; this.y = y; }
  
/** The first value (x, width, real and else). */
  int x;

/** The second value (y, height, imaginary and else). */
  int y;

/** The first value (x, width, real and else). */
  @Override public final int getX(){ return this.x; }

/** The second value (y, height, imaginary and else). */
  @Override public final int getY(){ return this.y; }

/** Set the first value (x, width, real and else). */
  public final void  setX(int x){ this.x = x; }

/** The second value (y, height, imaginary and else). */
  public final void  setY(int y){ this.y = y; }
  
/** Make of two integer values. <br /> */
  public static final IntPair ofInts(final int x, final int y){ 
     return new IntPair(x, y);
  } // ofInts(2* int)
  
/** Make of Dimension. <br />
 *  <br />
 *  @param dim for width, height; null gets (0, 0)
 *  */
  public static final IntPair of(final Dimension dim){
     if (dim == null) return new IntPair(0, 0);
     return ofInts(dim.width, dim.height);
  } // ofDim(Dimension)

/** Make of Point.  <br />
 *  <br />
 *  @param point for x, y; null gets (0, 0)
 *  */
  public static final IntPair of(final Point point){
     if (point == null) new IntPair(0, 0);
     return ofInts(point.x, point.y);
   }
  
/** Compare with other Object. <br /> 
 *  @see ConstIntPair#equals(ConstIntPair)
 */
  @Override public boolean equals(final Object o){
     return o instanceof ConstIntPair ? equals((ConstIntPair)o) : false;
  } // equals(Object)

  /** Get the hashCode. <br /> */
  @Override public int hashCode(){ return calcHash(); } 
  // hashCode()

  /** As String (x, y). <br /> 
   *  @see #asString()
   */
  @Override public String toString(){ return asString(); }


  private volatile transient ConstIntPair constView;

/** Provide a r/o view to the IntPair object. <br />
 *  <br />
 *  This method provides a {@link ConstIntPair} object giving a complete view
 *  to the state of this {@link IntPair}. All changes will be seen, but the
 *  {@link ConstIntPair} object will under no circumstances allow the holder 
 *  to change this {@link IntPair}'s state.<br />
 *  <br />
 *  Hint this is (sematically) different from {@link #asIntPairFix()}.
 */
  public ConstIntPair constView(){
     if (constView == null) constView = new ConstIntPair() {
        @Override public int getY(){ return IntPair.this.y; }
        @Override public int getX(){ return IntPair.this.x; }
        @Override public int hashCode(){ return IntPair.this.calcHash(); } 
        @Override public boolean equals(final Object o){
           return o instanceof ConstIntPair
                                   ? IntPair.this.equals((ConstIntPair)o) : false;
        } // equals(Object)
        @Override public String toString(){ return IntPair.this.asString(); }
     };
     return constView;
  } // constView()
  
/** Provide a r/o view to the IntPair object. <br />
  *  <br />
  *  This method provides a {@link ConstIntPair} object giving a complete view
  *  to the state of this {@link IntPair}. All changes will be seen, but the
  *  {@link ConstIntPair} object will under no circumstances allow the holder 
  *  to change this {@link IntPair}'s state.<br />
  */  

} // class IntPair (24.08.2016)
