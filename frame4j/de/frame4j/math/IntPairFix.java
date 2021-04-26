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
 *  An object of this class holds a pair ({@link #x}, {@link #y}) of int 
 *  values.<br />
 *  {@link IntPairFix}s are immutable, i.e. all fields are final.<br />
 *  <br />
 *  Hence,  {@link IntPair}s can substitute or be a base for substitution
 *  of {@link Point},  {@link Dimension}, {@link Rectangle} and others 
 *  to eliminate their design flaws.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2016 &nbsp; Albrecht Weinert 
 *  
 *  @author   Albrecht Weinert
 *  @version  $Revision: 32 $ ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)
 *  @see de.frame4j.math
 *  @see de.frame4j.math.Complex
 *  @see de.frame4j.math.CFun
 */
 // so far    V.184  (26.08.206) : new

public final class IntPairFix implements ConstIntPair, 
                                                   Serializable, Cloneable {
   
/** The pair (0, 0). */  
   public static final IntPairFix ZEROS = new IntPairFix(0, 0);

 /** The pair (1, 1). */  
   public static final IntPairFix ONES = new IntPairFix(1, 1);

/** No public constructor. <br /> */
  private IntPairFix(final int x, final int y){ this.x = x; this.y = y; }
  
/** The first value (x, width, real and else). */
  public final int x;

/** The second value (y, height, imaginary and else). */
  public final int y;
  
/** The first value (x, width, real and else). */
  @Override public final int getX(){ return this.x; }

/** The second value (y, height, imaginary and else). */
  @Override public final int getY(){ return this.y; } 

  
/** Make of two integer values. <br /> */
  public static final IntPairFix ofInts(final int x, final int y){ 
     if (x == 0) {
        if (y == 0) return ZEROS;
     } else  if (x == 1) {
        if (y == 1) return ONES;        
     }
     // to do hashcash
     return new IntPairFix(x, y);
  } // ofInts(2* int)
  
/** Make or get of Dimension. <br />
 *  <br />
 *  @param dim for width, height; null gets {@link #ZEROS}
 *  */
  public static final IntPairFix of(final Dimension dim){
     if (dim == null) return ZEROS;
     return ofInts(dim.width, dim.height);
  } // ofDim(Dimension)
  

/** Make or get of Point.  <br />
 *  <br />
 *  @param point for x, y; null gets {@link #ZEROS}
 *  */
  public static final IntPairFix of(final Point point){
     if (point == null) return ZEROS;
     return ofInts(point.x, point.y);
   }
  
/** Compare with other Object. <br /> 
 *  @see ConstIntPair#equals(ConstIntPair)
 */
  @Override public boolean equals(final Object o){
     return o instanceof ConstIntPair ? equals((ConstIntPair)o) : false;
  } // equals(Object)
  
  volatile int hash;
/** Get the hashCode. <br /> */
  @Override public int hashCode(){
     return hash != 0 ? hash : (hash = calcHash());
  } // hashCode()
  
/** As String (x, y). <br /> 
 *  @see #asString()
 */
  @Override public String toString(){ return asString(); }

/** Make a copy. <br />
 *  @return this as this object is immutable  
 */
  @Override public Object clone(){ return this; }

/** Meke a IntPairFis in current state. <br />
 *  @return this as this object is immutable  
 */  
  @Override public IntPairFix asIntPairFix(){ return this; }

} // class IntPairFix (26.08.2016)
