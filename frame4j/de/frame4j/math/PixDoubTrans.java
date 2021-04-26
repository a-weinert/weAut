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
package de.frame4j.math;

import java.io.Serializable;
// import java.awt.Point;

import de.frame4j.math.CFun;
import de.frame4j.math.Complex;

/** <b>Linear transformation between pixel and double coordinates</b>. <br />
 *  <br />
 *  An object of this class represents a (co-planar) linear transformation 
 *  between integer (pixel) coordinates (where right and down is + +) and 
 *  a pair (x, y) of double (where right up is + +).<br />
 *  <br />
 *  The transformation is described by the origin's double coordinates 
 *  ({@link #xOd}, {@link #yOd}), by the origin's integer or pixel coordinates 
 *  ({@link #xOp}, {@link #yOp}) and one pixel's length (as double) in both 
 *  x and y orientation ({@link #xL1p}, {@link #yL1p}).<br />
 *  <br /> 
 *  {@link #xL1p} and {@link #yL1p} must both be positive and greater than
 *  1e-12. On applying the transformation the factor {@link #yL1p} will be 
 *  used negative to hide the fact Java graphic's y pixel coordinates growing
 *  the wrong way (downwards).<br />
 *  <br />
 *  Objects of this class are serialisable and they are immutable (having
 *  hence all final object variables made public).<br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2002 - 2003, 2005 &nbsp; Albrecht Weinert<br />
 *  <br /> 
 *  @author   Albrecht Weinert
 *  @version  $Revision: 32 $ ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)
 *  @see de.frame4j.graf.Paintable
 */
 // Bisher    V00.00 (16.01.2002 10:08) :  new
 //           V02.00 (24.04.2003 16:54) :  CVS Eclipse
 //           V02.20 (07.01.2005 12:53) :  clone(), toString()
 //           V.031+ (31.01.2010 20:40) :  minor, /**
 
public final class PixDoubTrans implements Serializable, Cloneable {
   
/** State as String. <br />
 *  <br />
 *  A one line description of this transformation will be appended to the 
 *  StringBuffer bastel.<br />
 *  <br />
 *  @param bastel StringBuffer to be appended to; if null, it will be made 
 *                with starting capacity 66
 *  @return bastel
 */   
    public StringBuffer toString(StringBuffer bastel){
       if (bastel == null) bastel = new StringBuffer(66);
       bastel.append(" (").append(xOp).append(", ").append(yOp);
       bastel.append(" px ) <=> (").append(xOd).append(", ");
       bastel.append(yOd).append("); x : 1 px = ").append(xL1p);
       bastel.append(yOd).append("; y : 1 px = ").append(yL1p).append("; ");
       return bastel;
    } // toString(StringBuffer)


/** State as String. <br />
 *  <br />
 *  A one line description of this transformation will be returned.<br />
 *  <br />
 *  The method is equivalent to <br /> &nbsp;
 *  {@link #toString(StringBuffer) toString(null).toString()}.<br />
 */   
    @Override public String toString(){ return toString(null).toString(); }

/** The x origin in pixel. <br /> */
   public final int xOp;

/** The y origin in pixel. <br /> */
   public final int yOp;

/** The x origin  as double. <br /> */
   public final double xOd;

/** The y origin  as double. <br /> */
   public final double yOd;

/** The length of one x pixel as double value. <br />
 *  <br />
 *  This value must be &gt; 1e-12 (and hence positive). <br  />
 */
   public final double xL1p;

/** The length of one y pixel as double value. <br />
 *  <br />
 *  This value must be &gt; 1e-12 (and hence positive). <br  />
 *  On transformations the negative of this value will be applied to mask the
 *  wrong (downward) growth direction of Java graphic's y pixel 
 *  coordinates.<br />
 */
   public final double yL1p;

/** The length of (double) 1.0 in x pixels as double value. <br />
 *  <br />
 *  xL1pInv is the inverse of {@link #xL1p}. <br />
 */
   public final double xL1pInv;

/** The length of (double) 1.0 in y pixels as double value. <br />
 *  <br />
 *  yL1pInv is the inverse of {@link #yL1p}. <br />
 */
   public final double yL1pInv;
 
   
/** Compare to another PixDoubTrans object. <br />
 *  <br />
 *  The other object is considered as equal if it is of the same type 
 *  and has equal values in all 6 respectively 8 variables determining the 
 *  transformation. <br />
 */
   @Override public final boolean equals(final Object other) {
      if (!(other instanceof PixDoubTrans)) return false;
      if (this == other) return true;
      return   yL1p == ((PixDoubTrans)other).yL1p
           &&  xL1p == ((PixDoubTrans)other).xL1p
           &&  xOp == ((PixDoubTrans)other).xOp
           &&  yOp == ((PixDoubTrans)other).yOp
           &&  xOd == ((PixDoubTrans)other).xOd
           &&  yOd == ((PixDoubTrans)other).yOd;
   } // equals(Object 
   
   @Override public final int hashCode(){  return hashValue; }
   
/** The hash code of this transformation. <br /> */   
   public final int hashValue;

/** A &quot;copy&quot; of this object. <br />
 *  <br />
 *  As {@link PixDoubTrans} objects are immutable cloning them makes no sense.
 *  This method hence returns the original (this) object.<br />
 */   
   @Override public Object clone(){ return this; }   

//-----------------------------------------------------------------

/** Make the pixel value from the double coordinate value (X). <br />
 *  <br />
 *  NaN and +infinity return {@link java.lang.Integer}.MAX_VALUE.<br />
 *  -infinity returns {@link java.lang.Integer}.MIN_VALUE.<br />
 */
   public int xPix(final double x){
     if (Double.isNaN(x) || x == Double.POSITIVE_INFINITY) 
         return Integer.MAX_VALUE;
     if (x == Double.NEGATIVE_INFINITY) 
         return Integer.MIN_VALUE;
     return ((int)((x - xOd) * xL1pInv)) + xOp;
   } // xPix(double)


/** Make the pixel value from the double coordinate value (Y). <br />
 *  <br />
 *  NaN and +infinity return {@link java.lang.Integer}.MAX_VALUE.<br />
 *  -infinity returns {@link java.lang.Integer}.MIN_VALUE.<br />
 */
   public int yPix(final double y){
     if (Double.isNaN(y) || y == Double.NEGATIVE_INFINITY) 
         return Integer.MAX_VALUE;
     if (y == Double.POSITIVE_INFINITY) 
         return Integer.MIN_VALUE;
     return ((int)((yOd - y) * yL1pInv)) + yOp;
   } // yPix(double


/** Make a pixel point from the double coordinate values (X, Y). <br />
 *  <br />
 *  @see #xPix xPix()
 *  @see #yPix yPix()
 */
   public IntPairFix xyPix(final double x, final double y){
      return  IntPairFix.ofInts(xPix(x), yPix(y));
   } // xyPix(double

//-----------------------------------------------------------------

/** Make the double coordinate value from the pixel value (X). <br /> */
   public double xD(final int xP){
     return (xP - xOp) * xL1p + xOd;
   } // xD(int

/** Make the double coordinate value from the pixel value (Y). <br /> */
   public double yD(final int yP){
     return (yOp - yP) * yL1p + yOd;
   } // yD(int


/** Make a Complex from pixel coordinates (X, Y). <br />
 *  <br />
 *  The pixel coordinates (xP, yP) will be made a double pair by this 
 *  transformation. That pair will be used to fetch a {@link Complex}
 *  object.<br />
 *  <br />
 *  @see #xD xD()
 *  @see #yD yD()
 *  @see de.frame4j.math.Complex#make Complex.make()
 */
   public Complex pixComplex(final int xP, final int yP){
     return Complex.make(xD(xP), yD(yP));
   } // pixComplex(int

/** Make a Complex from pixel coordinates given as Point (X, Y). <br />
 *  <br />
 *  The pixel coordinates (xP, yP) will be made a double pair by this 
 *  transformation. That pair will be used to fetch a {@link Complex}
 *  object.<br />
 *  <br />
 *  @see #xD xD()
 *  @see #yD yD()
 *  @see de.frame4j.math.Complex#make Complex.make()
 *  @param p point in pixels (must not be null)
 */
   public Complex pixComplex(final ConstIntPair p){
     return Complex.make(xD(p.getX()), yD(p.getY()));
   } // pixComplex(Point

//-------------------------------------------------------------------

/** Make a transformation. <br />
 *  <br />
 *  This constructor just takes the values as supplied as object state, if
 *  they meet this class' conditions. Violating values will get an
 *  {@link java.lang.IllegalArgumentException}.<br />
 *  <br />
 *  @param xOp  X origin in pixel
 *  @param yOp  Y origin in pixel
 *  @param xL1p X length of a pixel as double  (&gt;=+1e-12 !)
 *  @param yL1p Y -length of a pixel as double  (&gt;=+1e-12 !)
 *  @param xOd  X origin in double
 *  @param yOd  Y origin in double
 */
   public PixDoubTrans(final int xOp, final int yOp, // zero in Pixel
                final double xL1p, final double yL1p, // 1 Pixel in double
                final double xOd, final double yOd) { // zero in double
      if (Double.isNaN(xL1p) || Double.isInfinite(xL1p) 
           || Double.isNaN(yL1p) || Double.isInfinite(yL1p)  
           || xL1p < 1.0e-12 || yL1p < 1.0e-12 
           || Double.isNaN(xOd) || Double.isInfinite(xOd)  
           || Double.isNaN(yOd) || Double.isInfinite(yOd) )
         throw new IllegalArgumentException("infinite or invalid");
      this.xOp = xOp;
      this.yOp = yOp;
      this.xOd = xOd;
      this.yOd = yOd;
 
      this.xL1p = xL1p;
      this.xL1pInv = 1.0 / xL1p;

      this.yL1p = yL1p;
      this.yL1pInv = 1.0 / yL1p;
      this.hashValue =  xOp << 17  + yOp + CFun.hashCode(xL1p, yL1p);
   } //  PixDoubTrans(int, int, ...


/** Make a transformation. <br />
 *  <br />
 *  This constructor just takes the values as supplied as object state, if
 *  they meet this class' conditions. Violating values will get an
 *  {@link java.lang.IllegalArgumentException}.<br />
 *  <br />
 *  The (double) coordinate origin is taken as (0.0, 0.0).<br />
 *  This call is equivalent to:<br /> &nbsp;
 *  &nbsp;  PixDoubTrans(xOp, yOp,  xL1p, yL1p, 0.0, 0.0)<br />
 *  <br />
 *  @param xOp  X origin in pixel
 *  @param yOp  Y origin in pixel
 *  @param xL1p X length of a pixel as double  (&gt;=+1e-12 !)
 *  @param yL1p Y -length of a pixel as double  (&gt;=+1e-12 !)
 */
   public PixDoubTrans(final int xOp, final int yOp, 
                                       final double xL1p, final double yL1p){ 
      this(xOp, yOp,  xL1p, yL1p, 0.0, 0.0);
   } //  PixDoubTrans(int, int, ...

/** Make a transformation. <br />
 *  <br />
 *  This constructor just takes the values as supplied as object state, if
 *  they meet this class' conditions. Violating values will get an
 *  {@link java.lang.IllegalArgumentException}.<br />
 *  <br />
 *  The (double) coordinate origin is taken as (0.0, 0.0).<br />
 *  <br />
 *  @param xOp  X origin in pixel
 *  @param yOp  Y origin in pixel
 *  @param xL1  the length of (double) 1.0 in x pixels (&gt;=1!)
 *  @param yL1  the length of (double) 1.0 in x pixels (&gt;=1!)
 */
   public PixDoubTrans(final int xOp, final int yOp, 
                                               final int xL1, final int yL1){
      if (xL1 < 1 || yL1 < 1)
         throw new IllegalArgumentException("infinite or invalid");
      this.xOp = xOp;
      this.yOp = yOp;
      this.xOd = this.yOd = 0.0;
 
      this.xL1pInv = xL1;
      this.xL1p = 1.0 / this.xL1pInv ;

      this.yL1pInv = yL1;
      this.yL1p = 1.0 / this.yL1pInv ;
      
      this.hashValue =  xOp << 17  + yOp + CFun.hashCode(xL1p, yL1p);
   } //  PixDoubTrans(4*int)


} // class PixDoubTrans (24.04.2003, 31.01.2010)