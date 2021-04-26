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

import java.math.BigDecimal;
import java.math.MathContext;

/** <b>Special functions collection (the subset used by
 *                                      {@link Complex Complex})</b>. <br />
 *  <br />
 *  This class features some mathematical functions not to be found in
 *  {@link java.lang.Math java.lang.Math}. Most of them are used by 
 *  {@link de.frame4j.math.Complex Complex}.<br />
 *  <br />
 *  If at least one argument is NaN (invalid, not a number) the returned 
 *  result will be NaN if not explicitly stated otherwise.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 1999 - 2003, 2005 &nbsp; Albrecht Weinert 
 *  
 *  @author   Albrecht Weinert
 *  @version  $Revision: 39 $ ($Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $)
 *  @see de.frame4j.math
 *  @see de.frame4j.math.Complex
 */
 // so far    V00.00 (18.05.2000 16:51) : ex weinertBib, SFun-Teilm.
 //           V02.00 (24.04.2003 16:54) :  CVS Eclipse
 //           V02.20 (18.05.2005 10:17) :  /**
 //           V.166+ (01.11.2009 19:58) : ported to Frame4J, 
 //                                       replace DecFormat by MathContex

public interface CFun {

/* * No objects. <br /> */
//  CFun(){ }

/** The smallest relative spacing for doubles. <br /> */
  public final static double EPSILON_SMALL = 1.1102230246252e-16;

/** The invalid value (NaN) for double. <br>
 *  <br />
 *  D_NaN ist {@link java.lang.Double#NaN Double.NaN}.
 */
   public final static double D_NaN = Double.NaN;

/** pi . <br /> */
   public static double PI = Math.PI;

/** pi/2 . <br /> */
   public static double PI_2 = PI/2;

/** pi/4 . <br /> */
   public static double PI_4 = PI/4;

/** 3pi/4 . <br /> */
   public static double PI_3_4 = 3*PI/4;



/** Mask for the sign bit in IEEE 754 double coding. <br />
 *  <br />
 *  This is also the -0.0 (negative null) in EEE 754.<br />
 */
   public final static long D_SIGN_MASK  = 0x8000000000000000L;

/** Mask for exponent and mantissa in IEEE 754 double coding. <br /> */
   public final static long D_VALUE_MASK = 0x7FFFFFFFFFFFFFFFL;

   static class Impl {
     private Impl(){} // no objects no javadoc

     //                              0   1    2        3     4
     //                              + / -pref  0    invalid infinite
     private final static String[] defaultForms =  
                                    {"", "-", "0.00", "NaN", "inf."};
   
/** sinh() coefficients for the range 0 to 1 .*/
     private static final double[]   SINH_COEF = {
      0.1730421940471796,
      0.08759422192276048,
      0.00107947777456713,
      0.00000637484926075,
      0.00000002202366404,
      0.00000000004987940,
      0.00000000000007973,
      0.00000000000000009};
 
/** tanh() coefficients for the range 0 to 1 .*/
     private static final double[]   TANH_COEF = {
      -.25828756643634710,
      -.11836106330053497,
      .009869442648006398,
      -.000835798662344582,
      .000070904321198943,
      -.000006016424318120,
      .000000510524190800,
      -.000000043320729077,
      .000000003675999055,
      -.000000000311928496,
      .000000000026468828,
      -.000000000002246023,
      .000000000000190587,
      -.000000000000016172,
      .000000000000001372,
      -.000000000000000116,
      .000000000000000009};

/** asinh() coefficients for the range 0 to 1  . */
     private static final double[]   ASINH_COEF = {
       -.12820039911738186343372127359268e+0,
      -.58811761189951767565211757138362e-1,
      .47274654322124815640725249756029e-2,
      -.49383631626536172101360174790273e-3,
      .58506207058557412287494835259321e-4,
      -.74669983289313681354755069217188e-5,
      .10011693583558199265966192015812e-5,
      -.13903543858708333608616472258886e-6,
      .19823169483172793547317360237148e-7,
      -.28847468417848843612747272800317e-8,
      .42672965467159937953457514995907e-9,
      -.63976084654366357868752632309681e-10,
      .96991686089064704147878293131179e-11,
      -.14844276972043770830246658365696e-11,
      .22903737939027447988040184378983e-12,
      -.35588395132732645159978942651310e-13,
      .55639694080056789953374539088554e-14,
      -.87462509599624678045666593520162e-15,
      .13815248844526692155868802298129e-15,
      -.21916688282900363984955142264149e-16,
      .34904658524827565638313923706880e-17
   };

/** The cache for mathematical contexts. */  
   private final static MathContext[] mCo = {
                        MathContext.UNLIMITED, // 0
                        null, null, null, // 1,2,3
                        FOUR_MC, // 4
                        null, null,  // 5, 6
                        MathContext.DECIMAL32, // 7
                        null, null, null, // 8,9,10
                        null, null, null, // 11,12,13
                        null, null,  // 14, 15
                        MathContext.DECIMAL64, // 16
                        null, null };  // 17,18  // MathContexts
   } // Impl

/** Number of special hash values. <br />
 *  <br />
 *  @see de.frame4j.math.CFun#hashCode hashCode()
 */ 
   public static final int NOSH = 14;


//-------    Helper methods     -----------------------------------

/** The sign of a double. <br />
 *  <br />
 *  true is returned, if the sign bit according to IEEE 754 coding is set.
 *  This will be done for all types of values including the zeros, infinity
 *  and of all the (too many) NaN codings.<br />
 *  <br />
 *  @see de.frame4j.math.CFun#sign(double, double)
 */
   static public boolean sign(double val){
     return (D_SIGN_MASK & Double.doubleToRawLongBits(val)) == D_SIGN_MASK; 
   } 

/** Value of x with the sign of y. <br />
 *  <br />
 *  This method returns the value of the parameter {@code val} with the 
 *  parameter's {@code sign} sign. This will also be done if parameter
 *  {@code sign} is NaN.<br />
 *  <br />
 *  If  {@code sign} is NaN in Java's canonical encoding (0x7ff8000000000000L)
 *  that will be positive.<br />
 *  <br />
 *  <br />
 *  @param val  take the value 
 *  @param sign take the sign (only if val is not NaN)
 *  @see de.frame4j.math.CFun#sign(double)
 */
   static public double sign(double val, double sign){
      if (Double.isNaN(val)) return D_NaN;
      return Double.longBitsToDouble(
          D_VALUE_MASK & Double.doubleToLongBits(val)
        | D_SIGN_MASK & Double.doubleToLongBits(sign)   );
   // doubleToRawLongBits wäre angemessen, geht aber mit alten
   // JVMs (Netscape (R)Communicator 4.06 z.B.) nicht.
   } // sign()

/** A hash value for a double pair. <br />
 *  <br />
 *  This method calculates a significant hash value for a pair of double
 *  numbers according to the requirements of the class 
 *  {@link de.frame4j.math.Complex Complex} (complex numbers). But it is 
 *  nevertheless suitable for other number pairs like common
 *  co-ordinates or factors in a transformation.<br />
 *  <br />
 *  For the hash values returned the following holds:<br /><br style="clear:left;"/>
 *  <table cols="5" cellpadding="6" 
 *   cellspacing="0" summary="Hashwerte" width="400" style="textalign:center; border:1;">
 *                 <tr><th> x  </th><th>   y    </th><th>Hash-Wert</th>
                                         <th>arcus</th><th>Grad</th></tr>
 <tr style="textalign:center;"><td>   NaN   </td><td>beliebig </td><td> 0</td>
                                           <td> D_NaN </td><td> -</td></tr>
 <tr style="textalign:center;"><td> any </td><td>   NaN  </td><td> 0</td>
                                           <td> D_NaN</td><td> -</td></tr>
 <tr style="textalign:center;"><td>+infinit</td><td> finite</td><td> 1</td>
                                           <td> 0.0 </td><td> 0 </td></tr>
 <tr style="textalign:center;"><td>+infinit</td><td>+infinit</td><td> 2</td>
                                           <td> PI_4 </td><td> 45</td></tr>
 <tr style="textalign:center;"><td> finite</td><td>+infinit</td><td> 3</td>
                                           <td> PI_2 </td><td> 90</td></tr>
 <tr style="textalign:center;"><td>-infinit</td><td>+infinit</td><td> 4</td>
                                          <td> PI_3_4</td><td>135</td></tr>
 <tr style="textalign:center;"><td>-infinit</td><td> finite</td><td> 5</td>
                                          <td> PI   </td><td>180</td></tr>
 <tr style="textalign:center;"><td>-infinit</td><td>-infinit</td><td> 6</td>
                                          <td>-PI_3_4</td><td>225</td></tr>
 <tr style="textalign:center;"><td> finite</td><td>-infinit</td><td> 7</td>
                                          <td>-PI_2 </td><td>270</td></tr>
 <tr style="textalign:center;"><td>+infinit</td><td>-infinit</td><td> 8</td>
                                          <td>-PI_4 </td><td>315</td></tr>
 <tr style="textalign:center;"><td>   0.0  </td><td>   0.0  </td><td> 9</td>
                                          <td> 0.0 </td><td> 0 </td></tr>
 <tr style="textalign:center;"><td>   1.0  </td><td>   0.0  </td><td>10</td>
                                          <td> 0.0 </td><td> 0 </td></tr>
 <tr style="textalign:center;"><td>   0.0  </td><td>   1.0  </td><td>11</td>
                                          <td> PI_2 </td><td> 90</td></tr>
 <tr style="textalign:center;"><td>  -1.0  </td><td>   0.0  </td><td>12</td>
                                           <td> PI   </td><td>180</td></tr>
 <tr style="textalign:center;"><td>   0.0  </td><td>  -1.0  </td><td>13</td>
                                           <td>-PI_2 </td><td>270</td></tr>
 *  </table><br style="clear:both;"/>
 *  
 *  beside that the following holds for the hash values returned:<br /><ul>
 *  <li>No other pairs of x and y will return the values 0 to 13.</li>
 *  <li>No values &lt; 0 are returned.</li></ul><br />
 * 
 *  Hint: The values in the radian measure (arcus) column are named constants 
 *  (finals) in this class.<br />
 *  <br />
 *  @param x first pair value
 *  @param y second pair value
 *  @return a hash value &gt;= 0
 *  @see de.frame4j.math.CFun#NOSH
 *  @see de.frame4j.math.CFun#D_NaN
 *  @see de.frame4j.math.CFun#PI
 *  @see de.frame4j.math.CFun#PI_2
 */ 
   static public int hashCode(double x, double y){
      if (Double.isNaN(x) || Double.isNaN(y)) return 0;
      if (Double.isInfinite(x)) { // x infinite
         if (Double.isInfinite(y)) // x,y infinite
            return x < 0 ? (y < 0 ? 6 : 4) : (y < 0 ? 8 : 2); 
         return x < 0 ? 5 : 1; // x infinite y finite
      } // x infinite;
      if (Double.isInfinite(y)) // y infinite, x finite
         return y < 0 ? 7 : 3;
      int h = 0;
      if (x == 0.0) { // x = 0.0
         if (y == 0.0) return 9;
         if (y == 1.0) return 11;
         if (y == -1.0) return 13;
      } else { // x != 0.0
         if (y == 0.0) { // y = 0.0
           if(x == 1.0) return 10;
           if(x == -1.0) return 12;
         } // y = 0
         h = (int)(Double.doubleToLongBits(x)>>32); // 32
      }  // x != 0.0
      if (y != 0.0)
         h+= (int)(Double.doubleToLongBits(y)>>33); // 33 not 32 important
      if (h < 0) h>>>=1;
      if (h < NOSH) {
         h += NOSH;
         if (h < 0) h>>>=1;
      }
      return h;
   } // hashCode(double, double)


/** Evaluate a Chebyshev series. <br />
 *  <br />
 *  This (package) helper method  takes for granted that the array of 
 *  coefficients <code>coef</code>is not null and does contain no NaN as
 *  well as that the argument <code>&#160;x&#160;</code> is in the allowed
 *  range (mostly 0..1).<br />
 *  <br />
 *  Under all other conditions the result is not predictable and exceptions
 *  might be thrown unexpectedly.<br />
 *  <br />
 *  @param x the argument
 *  @param coef the field (vector) of coefficients 
 */
   static double csevl(double x, final double[] coef){
      double b1 = 0.0;
      double b0 = 0.0;
      double b2 = 0.0;
      x = x + x; // 2*x
      for (int i = coef.length-1;  i >= 0;  i--) {
         b2 = b1;
         b1 = b0;
         b0 = x * b1 - b2 + coef[i];
      }
      return 0.5 * (b0 - b2);
   } // csevl(double, double[])

//---  Mathematical functions ------------------------

/** Hyperbolic sine. <br /> */
   static public double sinh(final double x){
      if (Double.isNaN(x)) return D_NaN;
      if (Double.isInfinite(x)) return x;
      double   y = Math.abs(x);
      if (y < 2.58096e-08) return x;
      double   ans;
      if (y <= 1.0) {
         ans = x * (1.0 + csevl(2.0 * x * x - 1.0, Impl.SINH_COEF));
      } else {
         y = Math.exp(y);
         if (y >= 94906265.62) {
            // 94906265.62 = 1.0 / Math.sqrt(EPSILON_SMALL)
            ans = sign(0.5 * y, x);
         } else {
            ans = sign(0.5 * (y - 1.0 / y), x);
         }
      }
      return ans;
   } // sinh(double)

/** Hyperbolic cosine. <br /> */
   static public double cosh(final double x){
      if (Double.isNaN(x)) return D_NaN;
      if (Double.isInfinite(x)) return x;
      double   y = Math.exp(Math.abs(x));
      if (y < 94906265.62) 
         // 94906265.62 = 1.0 / Math.sqrt(EPSILON_SMALL)
         return 0.5 * (y + 1.0 / y);
      return 0.5 * y;
   } // cosh(double)

/** Tangens hyperbolicus / hyperbolic tangent . <br /> */
   static public double tanh(final double x){
      if (Double.isNaN(x)) return D_NaN;
      double   ans, y;
      y = Math.abs(x);
  
      if (y < 1.82501e-08) {
         // 1.82501e-08 = Math.sqrt(3.0 * EPSILON_SMALL)
         ans = x;
      } else if (y <= 1.0) {    
         ans = x * (1.0 + csevl(2.0 * x*x - 1.0, Impl.TANH_COEF));
      } else if (y < 7.977294885) {
         // 7.977294885 = -0.5*Math.log(EPSILON_SMALL)
         y = Math.exp(y);
         ans = sign((y - 1.0/y)/(y + 1.0/y), x);
      } else {
         ans = sign(1.0, x);
      }
      return ans;
   } // tanh()

/** Arcussinus hyperbolicus / inverse (arc) hyperbolic sine. <br /> */
   static public double asinh(final double x){
      if (Double.isNaN(x)) return D_NaN;
      double   ans;
      double   y = Math.abs(x);
      if (y <= 1.05367e-08) {
         // 1.05367e-08 = Math.sqrt(EPSILON_SMALL)
         ans = x;
      } else if (y <= 1.0) {    
         ans = x * (1.0 + csevl(2.0* x*x -1.0, Impl.ASINH_COEF));
      } else if (y < 94906265.62) {
         // 94906265.62 = 1/Math.sqrt(EPSILON_SMALL)
         ans = Math.log(y + Math.sqrt(y*y + 1.0));
      } else { 
         ans = 0.69314718055994530941723212145818 + Math.log(y);
      }
      if (x < 0.0) ans = -ans;
      return ans;
   } // asinh()
   
/** Append the text representation of a double. <br />
 *  <br /> 
 *  The array {@code forms}, if null, will default to 
 *  {"", "-", "0.00", "NaN", "inf."}. As might be obvious 
 *  {@code forms} has to contain the prefixes for positive and negative, and
 *  the presentments of zero, infinity and invalid.<br />
 *  <br /> 
 *  @param bastel the StringBuilder to append to (if null one is made)
 *  @param dD the value to be formatted
 *  @param prec the decimal precision (2..18; default 4)
 *  @param forms an array of length 5 containing no null String, or null
 *  @return bastel   
 */
   public static StringBuilder append(StringBuilder bastel, 
                                  final double dD, int prec, String[] forms){
      if (forms == null || forms.length < 5 ) forms = Impl.defaultForms;
      if (bastel == null) bastel = new StringBuilder(20);
      if (Double.isNaN(dD)) {
         bastel.append(forms[3]);
         return bastel;
      }
      final long bits = Double.doubleToRawLongBits(dD);
      final boolean negative =  (bits &  D_SIGN_MASK) != 0;
      bastel.append(forms [negative ? 1 : 0]);
      if (dD == 0.0) {
         return bastel.append(forms[2]);
      }
      if (Double.isInfinite(dD)) {
         return bastel.append(forms[4]);
      }
      if (prec < 2 || prec > 18) prec = 4;
      final MathContext mc = getMathContext(prec);
      BigDecimal zP = new BigDecimal(negative ? -dD : dD, mc);
      bastel.append(zP.toEngineeringString());
      return bastel;
   } // append(StringBuilder, double, int, String[])

/** Return a mathematical context. <br />
 *  <br />
 *  This method returns a (cached) MathContext for precisions in the range 
 *  of 0 to 18.<br />
 *  <br />
 *  @see #FOUR_MC  
 *  @param prec a precision 0..18 (default 4, 0 meaning unlimited precision)
 *  @return a mathematical context
 *  @see MathContext#UNLIMITED
 */
   public static MathContext getMathContext(final int prec){
      if (prec < 0 || prec > 18 || prec == 4) {
         return FOUR_MC;
      }
      MathContext ret = Impl.mCo[prec];
      if (ret != null) return ret;
      ret = new MathContext(prec);
      return Impl.mCo[prec] = ret;
   } // getMathContext(int)
   
/** A mathematical context with precision 4. <br /> */
   public final static MathContext FOUR_MC = new MathContext(4);


} // interface CFun (24.09.2001, 13.12.2009, 24.03.2021)
