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

import java.io.ObjectStreamException;

import de.frame4j.demos.ComplDemo;
 
/** <b>Complex numbers</b>. <br />
 *  <br />
 *  Objects of this class are complex numbers, as they are very familiar to
 *  the Electrical Engineer. A Complex object represents the complex number 
 *  by its real and imaginary part (x, y) being two double values {@link #re}
 *  and {@link #im}. Beyond complex numbers, this class is also suitable for
 *  many other applications using such pairs like co-planary coordinates or
 *  vectors.<br />
 *  <br />
 *  Objects of this class are immutable. Methods returning a function of the
 *  complex number represented will return another object if the result is 
 *  another complex number.<br />
 *  <br />
 *  By using an internal hashing it is tried (hard) to use and return Complex
 *  objects already made instead of creating new ones representing the same
 *  value. (It is tried hard but not guaranteed in all cases.) It is 
 *  guaranteed that 14 (static) complex constants defined in this class are
 *  singleton in each case. For those values the operator <code>==</code>
 *  might be used instead of {@link #equals equals()} unhesitatingly.<br />
 *  <br />
 *  To enforce the uniqueness and re-use of Complex objects this class 
 *  publishes no constructors; one has to use the static factory  method
 *  ({@link #make make()}). No objects can be made that contradict this 
 *  classes rules for complex o, NaN and infinity as well as others.<br />
 *  <br />
 *  Complex objects are serialisable and immutable; the class is final.<br />
 *  <br />
 *  Mostly for performance class Complex is final and all final fields
 *  {@link #re} (real part), {@link #im} (imaginary part) and
 *  {@link #h} (hash value) are public (making getters dispensable).<br />
 *  <br />
 *  Besides the basic arithmetic this class implements some more complex (in
 *  a double sense) complex functions. Most of them might be tried in the 
 *  demonstrator application
 *  {@link de.frame4j.demos.ComplDemo ComplDemo}.<br />
 *  <br />
 *  With binary operations<br />
 *  &#160; u1.op(u2) &#160; means &#160; u1 op u2  &#160; and<br />
 *  &#160; u1.opReverse(u2) &#160; means &#160; u2 op u1 .<br />
 *  <code> opReverse() </code> does only exist for non commutative 
 *  operations.<br />
 *  <br />
 *  The rules for invalid (NaN) values are:<br />
 *  There is exactly one Complex object ({@link #NaN}) (having the hash value
 *  0).<br />
 *  Especially combinations of valid real and invalid imaginary, valid
 *  radius and invalid arc or all else such combinations are not supported
 *  in this class.<br />
 *  All operations with at least one {@link #NaN} operand end in the 
 *  &quot;NaN cul-de-sac&quot;.<br />
 *  <br />
 *  The sign of 0.0 is regarded as irrelevant. So there is just one Complex
 *  object {@link #ZERO} (hash value 9) with zero imaginary and real part
 *  &mdash; and not four of this sort. A product with one zero is regarded
 *  as zero (even if the other operand is infinite).<br />
 *  <br />
 *  On the other hand eight different complex numbers (and hence Complex 
 *  objects) are distinguished here, taking all (9-1) combinations of 
 *  -infinite, 0.0 and +infinite into account. These infinite complex values
 *  / Complex object have the {@linkplain #h hash values} 1 to 8.<br />
 *  There are no combinations of an infinite part with other values as
 *  zero or infinity.<br />
 *  <br />
 *  Hint: You may demonstrate some of this class' operations by
 *  {@link ComplDemo}. This may require frame4j as installed extension or
 *  by  WebStart.<br />
 *  <br />  
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 1999 - 2003, 2004, 2009 Albrecht Weinert
 *  
 *  @author   Albrecht Weinert
 *  @version  $Revision: 39 $ ($Date: 14.12.2009$)
 *  @see de.frame4j.math.CFun
 */
 //  so far V00.00 (23.05.2000) : ex-weinertBib
 //         V00.05 (03.01.2002) : readResolve
 //         V02.00 (24.04.2003) : CVS Eclipse
 //         V.165+ (01.11.2009) : to frame4J

public final class Complex implements java.io.Serializable, Cloneable, CFun {

/** Version number for serialising.  */
   static final long serialVersionUID = 260153007100201L;
//                                      magic /Id./maMi

/** Real part / Realteil  */
   public final double re;

/** Imaginary part / Imaginärteil */
   public final double im;

/** Hash code / hash value. <br />
 *  <br />
 *  For the hash values the following holds:<br /><br style="clear:both;" />
 *  <table cols="4" border="1" cellpadding="6" 
 *   cellspacing="0" summary="Hashwerte" width="380" style="text-align:center;">
 *   <tr><th>Zahl</th><th>re</th><th>im</th><th>Hash-Wert</th></tr>
 <tr align=center><td>{@link #NaN}</td><td>(Double.)&nbsp;NaN</td>
                     <td>(Double.)&nbsp;NaN</td><td> 0 </td></tr>
 <tr align=center><td>{@link #INF_0}</td><td>+unendl.</td><td>0.0</td>
                                           <td> 1 </td></tr>
 <tr align=center><td>{@link #INF_45}</td>
             <td>+unendl. </td><td>+unendl. </td><td> 2 </td></tr>
 <tr align=center><td>{@link #INF_90}</td><td>0.0</td><td>+infinit. </td>
                                            <td> 3 </td></tr>
 <tr align=center><td>{@link #INF_135}</td><td>-infinit. </td>
              <td>+infinit. </td><td> 4 </td></tr>
 <tr align=center><td>{@link #INF_180}</td><td>-infinit. </td><td>0.0</td>
                                                        <td> 5 </td></tr>
 <tr align=center><td>{@link #INF_225}</td><td>-infinit. </td><td>-infinit. </td>
                                                        <td> 6 </td></tr>
 <tr align=center><td>{@link #INF_270}</td><td>0.0</td><td>-infinit. </td>
                                                        <td> 7 </td></tr>
 <tr align=center><td>{@link #INF_315}</td><td>+infinit. </td>
            <td>-infinit. </td><td> 8 </td></tr>
 <tr align=center><td>{@link #ZERO}</td><td>0.0</td><td>0.0</td>
              <td> 9 </td></tr>
 <tr align=center><td>{@link #ONE}</td><td>1.0</td><td>0.0</td>
              <td>10 </td></tr>
 <tr align=center><td>{@link #I} bzw. {@link #J}</td><td>0.0</td><td>1.0</td>
                                                         <td>11 </td></tr>
 <tr align=center><td>{@link #M_ONE}</td><td>-1.0</td><td>0.0</td><td>12 </td></tr>
 <tr align=center><td>{@link #M_I}&nbsp;bzw.&nbsp;{@link #M_J}</td>
                <td>0.0</td><td>-1.0</td><td>13 </td></tr>
 *  </table><br style="clear:both;"/>
 *  
 *  Besides that for the hash value the following holds:<ul>
 *  <li>The values  0 to 13 will not be given to any other combinations of
 *      re and im.</li>
 *  <li>For complex values with one infinite part a finite value of the other 
 *      part is irrelevant.</li>
 *  <li>The sign of 0.0 is irrelevant for the hash value (consistent with 
 *      equals). {@link #ZERO}, {@link #ONE} and {@link #I} respectively
 *      {@link #J} do have a positive 0.0 part.</li>
 *  <li>There are no hash values &lt; 0 for Complex objects.</li></ul><br />
 *  @see de.frame4j.math.CFun#hashCode(double, double)
 */
   public final int h;


/** Private constructor without any checks. <br /> */
   private Complex(final double re, final double im, final int h){
     this.re = re;
     this.im = im;
     this.h  = h;
   } // Complex(2*double, int)

/** Complex Constant NaN (invalid complex number). <br /> */
   public static final Complex NaN = new Complex(D_NaN, D_NaN, 0);

/** Infinity 0 degrees. <br /> */
   public static final Complex INF_0 = new Complex(
                                          Double.POSITIVE_INFINITY, 0.0, 1);

/** Infinity 45 degrees. <br /> */
   public static final Complex INF_45 = new Complex(
                     Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 2);

/** Infinity 90 degrees. <br /> */
   public static final Complex INF_90 = new Complex(
                                          0.0, Double.POSITIVE_INFINITY, 3);

/** Infinity 135 degrees. <br /> */
   public static final Complex INF_135 = new Complex(
                      Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 4);

/** Infinity 180 degrees. <br /> */
   public static final Complex INF_180 = new Complex(
                                           Double.NEGATIVE_INFINITY, 0.0, 5);

/** Infinity 225 degrees. <br /> */
   public static final Complex INF_225 = new Complex(
                      Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 6);

/** Infinity 270 degrees. <br /> */
   public static final Complex INF_270 = new Complex(
                                           0.0, Double.NEGATIVE_INFINITY, 7);

/** Infinity 315 degrees. <br /> */
   public static final Complex INF_315 = new Complex(
                      Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 8);

/** Complex Constant ZERO (0,0). <br /> */
   public static final Complex ZERO = new Complex(0.0, 0.0, 9);

/** Complex Constant ONE (1,0). <br /> */
   public static final Complex ONE = new Complex(1.0, 0.0, 10);

/** Complex Constant M_ONE  (-1,0). <br /> */
   public static final Complex M_ONE = new Complex(-1.0, 0.0, 12);

/** Complex Constant I respectively  i = (0,1). <br />
 *  <br />
 *  same as {@link #J}.
 */
   public static final Complex I = new Complex(0.0, 1.0, 11);

/** Complex Constant J respectively j = (0,1). <br />
 *  <br />
 *  same as {@link #I}.
 */
   public static final Complex J = I;

/** Complex Constant M_I respectively -i = (0,-1). <br />
 *  <br />
 *  same as {@link #M_J}.
 */
   public static final Complex M_I = new Complex(0.0, -1.0, 13);

/** Complex Constant M_J respectively -j = (0,-1). <br />
 *  <br />
 *  same as {@link #M_I}.
 */
   public static final Complex M_J = M_I;


/** Prefix for complex part / name of -1's square root. <br />
 *  <br />
 *  The letter will be used at some places for the String representation. 
 *  The mathematician will usually choose i, but the electrical engineer will
 *  most often use j (as i is reserved for current). As this class was 
 *  originally made for an electrical engineering project, j is the default 
 *  here.<br />
 *  <br />
 *  The setting is made for the class; i.e. for all Complex objects. Possible 
 *  values are i, j, I and J, only. The setter 
 *  {@link #setPrefix setPrefix(char)} ignores all others.<br />
 *  <br />
 *  @see #infString
 *  @see #toString
 */
   public static char getPrefix(){ return prefix; }

/** Prefix for complex part / name of -1's square root. <br /> */
   static char prefix = 'j';

/** Prefix for complex part / name of -1's square root. <br />
 *  <br />
 *  @see #infString
 *  @see #toString()
 *  @see #getPrefix()
 */
   public static void setPrefix(final char prefix) {
      if (prefix == 'i' || prefix == 'j' || prefix == 'I'
          || prefix == 'J') {
         if (Complex.prefix ==  prefix) return;
         Complex.prefix = prefix;
         defaultFormsIm[0] = "+" + prefix;
         defaultFormsIm[1] = "-" + prefix;
      }
   } // setPrefix(char)


/** Textual representation of infinity. <br />
 *  <br />
 *  The text will (indirectly) be used for the String representation of 
 *  infinite complex numbers.<br />
 *  <br />
 *  The setting is made for the class / for all Complex objects. Sensible 
 *  values are &quot;infinit&quot; (default), &quot;infinity&quot; or
 *  &quot;infinit&quot; or alike. The text must never be null.<br />
 *  <br />
 *  @see #getPrefix getPrefix()
 *  @see #setPrefix setPrefix()
 *  @see #toString
 */
    public static String infString = "inf.";
    private final static String[] defaultFormsRe = 
                               {"", "-", "0.00", "NaN", infString};
    private final static String[] defaultFormsIm = 
                               {"+j", "-j", "0.00", "NaN", infString};

/** Length of internal HashMap. <br />
 *  <br />
 *  This value is the number of additional entries (extra to the 
 *  {@link de.frame4j.math.CFun#NOSH CFun.NOSH} (14) ones) of the table for
 *  reusable Complex objects.<br />
 *  <br />
 *  <br />
 *  @see de.frame4j.math.CFun#NOSH
 *  @see de.frame4j.math.CFun#hashCode(double, double)
 *  @see #h
 */ 
   public static final int HASH_MAP_LENGTH = 111;


/** The internal Complex storage. <br /> */
   private static Complex[] hm = new Complex[HASH_MAP_LENGTH + NOSH];

/** Initialising block. <br />
 *  <br />
 *  If this is documented, it is an error / bug of the javaDoc tool.<br />
 *  Ignore it please.<br />
 */
   static {
     hm[0] = NaN;
     hm[1] = INF_0;
     hm[2] = INF_45;
     hm[3] = INF_90;
     hm[4] = INF_135;
     hm[5] = INF_180;
     hm[6] = INF_225;
     hm[7] = INF_270;
     hm[8] = INF_315;
     hm[9] = ZERO;
     hm[10] = ONE;
     hm[11] = I;
     hm[12] = M_ONE;
     hm[13] = M_I;
   } // static  


/** Compare with another Complex. <br />
 *  <br />
 *  This method returns true if the other object  ({@code z}) is a Complex and
 *  if this and {@code z} are equal in both real and imaginary part.<br />
 *  <br />
 *  If both this and {@code z} are {@link #NaN} this method returns false
 *  (according to the rule that no two invalid numbers should be considered 
 *  as equal). The operator <code>==</code> on the other hand would get true
 *  in this case, as this class impedes the making of two complex 
 *  {@link Complex#NaN}s.<br />
 *  <br />
 *  @see #isNaN
 *  @see #h
 *  @see de.frame4j.math.CFun#hashCode(double, double)
 */
   @Override public boolean equals(final Object z){
      if (! (z instanceof Complex)) return false; 
      return h != 0 &&  h == ((Complex)z).h 
            && re == ((Complex)z).re && im == ((Complex)z).im;
   } // equals(Object)

/** Copy of this Complex. <br />
 *  <br />
 *  This method does not make any copies; it just returns this. (This is the
 *  only sensible way for immutable objects.)<br />
 *  <br />
 *  @see #equals(Object)
 *  @see #h
 *  @see de.frame4j.math.CFun#hashCode(double, double)
 */
   @Override public Object clone(){ return this; }


/** A hashcode for this Complex / hash value. <br />
 *  <br />
 *  This method just returns the final hash value {@link #h}.<br />
 *  <br />
 *  @see #h
 *  @see de.frame4j.math.CFun#hashCode(double, double)
 */
   @Override public int hashCode(){ return h; }


/** A String representation for this Complex. <br />
 *  <br />
 *  The usage of i or j depends on the set <code>prefix</code>.<br />
 *  <br />
 *  The representation of infinity will be ruled by {@link #infString}.<br />
 *  The text is returned like:<ul>
 *  <li>NaN</li>
 *  <li>99.99 +j88.3</li>
 *  <li>0.0</li>
 *  <li>1.0</li>
 *  <li>1.0-j</li>
 *  <li>j</li>
 *  <li>infinit-j*infinit</li></ul>
 *  
 *  @see #infString
 *  @see #getPrefix
 *  @see #setPrefix
 *  @see #h
 *  @see #valueOf
 */
   @Override public String toString() {
      if (h < 14) switch (h) {
         case  0 : return "NaN";
         case  1 : return infString;
         case  2 : return infString + "+" + prefix + "*" + infString;
         case  3 : return prefix + "*" + infString;
         case  4 : return "-"+infString + "+" + prefix + "*" + infString;
         case  5 : return "-"+infString;
         case  6 : return "-"+infString + "-" + prefix + "*" + infString;
         case  7 : return "-" + prefix + "*" +  infString;
         case  8 : return infString + "-" + prefix + "*" + infString;
         case  9 : return "0.0";
         case 10 : return "1.0";
         case 11 : return "+" + prefix;
         case 12 : return "-1.0";
         case 13 : return "-" + prefix;
      } // switch h

      StringBuilder bastel = new StringBuilder(40);
      if (re != 0.0) {
         bastel.append(Double.toString(re));
         if (im == 0.0) return bastel.toString();
         bastel.append(im > 0.0 ?  " +" : " ");
      }
      boolean imNeg = CFun.sign(im);

      if (imNeg) bastel.append("-");
      bastel.append(prefix);
      bastel.append(Double.toString(imNeg ? -im : im));
      return bastel.toString();
   }  // toString()


/** A String representation for this Complex. <br />
 *  <br />
 *  Compared to the method {@link #toString() toString()} the precision can
 *  be specified. The formatting will be delegated to
 *  {@link CFun CFun}.{@link 
 *  CFun#append(StringBuilder, double, int, String[]) append()}.<br />
 *  <br />
 *  @param prec the precision (2..16) default 4
 *  @return  the complex number (as text)
 */
   public String toString(final int prec){
      if (h < 14) return toString();
      StringBuilder bastel = new StringBuilder(40);
      if (re != 0.0) {
         CFun.append(bastel, re, prec, defaultFormsRe);
         if (im == 0.0) return bastel.toString();
      }
      CFun.append(bastel, im, prec, defaultFormsIm);
      return bastel.toString();
   }  // toString(int)


//-----------------------------------------------------------------------

/** De-serialising a Complex object. <br />
 *  <br />
 *  This method does not allow the indirect cloning of the singleton Complex
 *  object having {@linkplain #h hash} values &lt; 
 *  {@link CFun#NOSH CFun.NOSH} .<br />
 *  <br />
 *  In the the same way the cloning of Complex objects already stored 
 *  internally for re-use will be avoided.<br /> 
 *  <br />
 *  @throws java.io.ObjectStreamException will never be thrown
 */
   protected Object readResolve() throws ObjectStreamException {
      if (h < NOSH)  return hm[h];
      int hi = h % HASH_MAP_LENGTH;
      if (hi < NOSH) hi += HASH_MAP_LENGTH;
      synchronized(hm){
         Complex hashed = hm[hi];
         if (hashed == null || hashed.re != re || hashed.im != im) 
            return hm[hi] = this;
         return hashed; 
      }
  } // readResolve()

/** Factory of Complex objects. <br />
 *  <br />
 *  As the constructors are hidden on purpose, one has to use the factories
 *  to make a distinct Complex object.<br />
 *  <br />
 *  A Complex object fitting the parameter values will be returned. As far as 
 *  possible a stored object will be re-used. The sign of a 0.0 (zero) value
 *  is irrelevant.<br />
 *  <br />
 *  @param re the real part of the complex number
 *  @param im the imaginary part of the complex number
 *  @return a new or re-used Complex object fitting re and im
 *  @see #re
 *  @see #im
 *  @see #make(double)
 */
    public static Complex make(final double re, final double im){
       int h = CFun.hashCode(re, im);
       if (h < CFun.NOSH) return hm[h];
       int hi = h % HASH_MAP_LENGTH;
       if (hi < CFun.NOSH) hi += HASH_MAP_LENGTH;
       synchronized(hm) {
          Complex hashed = hm[hi];
          if (hashed == null || hashed.re != re || hashed.im != im) 
             return hm[hi] = new Complex(re, im, h);
          return hashed;
       }
    } // make(2*double)


/** Factory of Complex objects representing a real number. <br />
 *  <br />
 *  A Complex object fitting the parameter value will be returned. As far as 
 *  possible a stored object will be re-used. The sign of a 0.0 (zero) value
 *  is irrelevant.<br />
 *  <br />
 *  @param re the real part of the complex number
 *  @return a new or re-used Complex object fitting re and im==0.0
 *  @see #re
 *  @see #im
 *  @see #make(double, double)
 */
    public static Complex make(final double re){ return make(re, 0.0); }


/** Tests if NaN. <br />
 *  <br />
 *  As this class allows for just one (singleton) invalid Complex object<br />
 *  <code>co.isNaN() </code> is equivalent to<br />
 *  <code>co == Complex.NaN </code> as well as to<br />
 *  <code>co.h == 0 </code> .<br />
 *  <br />
 *  @return true if this is not a number (i.e. an illegal value or result)
 *  @see #NaN
 *  @see #h
 *  @see de.frame4j.math.CFun#hashCode(double, double)
 */
   public boolean isNaN(){ return h == 0; }

/** Tests if finite. <br />
 *  <br />
 *  This method return true if the complex value is neither infinite nor
 *  invalid.<br />
 *  <br />
 *  According to this class' singleton rules<br />
 *  <code>co.isfinite() </code> is equivalent to<br />
 *  <code>co.h &gt;= 9 </code> .<br />
 *  <br />
 *  @see #NaN
 *  @see #h
 *  @see de.frame4j.math.CFun#hashCode(double, double)
 *  @see #isReFinite
 *  @see #isImFinite
 *  @return true if neither component is infinite and all is a valid as number
 */
   public boolean isFinite(){ return h >= 9; }

/** Tests if imaginary is part finite. <br />
 *  <br />
 *  This method returns true if if the complex value is neither invalid nor
 *  has an infinite imaginary part.<br />
 *  <br />
 *  @return  true if the imaginary part is infinite and all is a valid as number
 *  @see #isFinite
 *  @see #isReFinite
 *  @see #NaN
 *  @see #h
 *  @see de.frame4j.math.CFun#hashCode(double, double)
 */
   public boolean isImFinite(){
     return h >= 9 || h == 1 || h == 5;
   } // isImFinite()

/** Tests if real part is finite. <br />
 *  <br />
 *  This method returns true if if the complex value is neither invalid nor
 *  has an infinite real part.<br />
 *  <br />
 *  @return  true if the real part is infinite and all is a valid as number
 *  @see #isFinite
 *  @see #isImFinite
 *  @see #NaN
 *  @see #h
 *  @see de.frame4j.math.CFun#hashCode(double, double)
 */
   public boolean isReFinite(){
     return h >= 9 || h == 3 || h == 7;
   } // isReFinite()

   
/** The real part of a Complex object. <br />
 *  <br />
 *  As Complex objects are immutable and have public real ({@link #re}) and
 *  imaginary ({@link #im}) parts, those could be accessed directly as 
 *  well.<br />
 *  @return the real part
 */
   public double real(){ return re; }

/** The imaginary part of a Complex object. <br />
 *  <br />
 *  As Complex objects are immutable and have public real ({@link #re}) and
 *  imaginary ({@link #im}) parts, those could be accessed directly as 
 *  well.<br />
 *  @return the imaginary  part
 */
   public double imag(){ return im; }
 
/** Translation table to negate the special singleton values. <br /> */
   static final int negInd[] = {
//   0   1   2   3   4   5    6    7   8    9   10   11  12  13 
//  NaN  0  45  90  135 180 -135 -90  -45  0.0  1.0  i  -1.0 -i
     0,  5,  6,  7,  8,  1,   2,   3,  4,   9,  12,  13, 10, 11 };

/** The negative of a Complex object (akind of  -this). <br />
 *  @return this complex number negated
 */
   public Complex negative(){
      if (h < CFun.NOSH) return hm[negInd[h]];
      return make(-re, -im);
   } // negative()

/** Translation table to conjugate the special singleton values. <br /> */
   static final int conjInd[] = {
//   0   1   2   3   4   5    6    7   8    9   10   11  12  13 
//  NaN  0  45  90  135 180 -135 -90  -45  0.0  1.0  i  -1.0 -i
     0,  1,  8,  7,  6,  5,   4,   3,  2,   9,  10,  13, 12, 11 };


/** The the complex conjugate. <br />
 *  @return this complexe numbers conjugate
 */
   public Complex conjugate() {
      if (h < CFun.NOSH) return hm[conjInd[h]];
      return make(re, -im);
   } // conjugate()

 
/** The sum of two Complex objects (this + z). <br /> */
   public Complex plus(Complex z) {
      if (h==0 || z.h==9) return this; // NaN, +0
      if (h==9 || z.h==0) return z;    // 0+, NaN
      return make(re + z.re, im + z.im);
   } // plus(Complex)

/** The sum of this Complex and a double x (this + x). <br /> */ 
   public Complex plus(double x) {
      if (h==0 || Double.isNaN(x)) return NaN;
      return make(re + x, im);
   } // plus(double)

/** The difference of two Complex objects (this - z). <br /> */
   public Complex minus(final Complex z) {
      if (h == 0 || z.h == 0) return NaN; // ein Op NaN
      if (z.h == 9) return this;          // -0
      return make(re - z.re, im - z.im);
   } // minus(Complex)

/** The difference of two Complex objects (z - this). <br /> */
   public Complex minusReverse(final Complex z){
      if (h == 0 || z.h == 0) return NaN;
      if (h == 9) return z;               // z - 0
      return make(z.re - re, z.im - im);
   } // minusReverse(Complex)

/** The difference of this Complex and a double (this - x). <br /> */
   public Complex minus(final double x){
      if (h==0 || Double.isNaN(x)) return NaN;
      return make(re - x, im);
   } // minus(double)

/** The difference of a double and this Complex (x - this). <br /> */
   public Complex minusReverse(final double x){
      if (h==0 || Double.isNaN(x)) return NaN;
      return make(x - re, im);
   } // minusReverse(double)


/** The product of two Complex objects (this * z). <br /> */
   public Complex times(final Complex z){
      if (h == 0 || z.h == 0) return NaN;
      if (h == 9 || z.h == 9) return ZERO;
      if (h < 9 && z.h < 9) { // beide Operanden infinit
         int hp = h + z.h -1; // addiert die Winkel (45grad-Schritte)
         if (hp > 8) hp -= 8;
         return hm[hp];
      } // beide Operanden infinit
      // ab hier höchstens noch ein Operand infinit, keiner ganz 0.0 
      double a = re * z.re;
      if (Double.isNaN(a)) a = 0.0; // 0 * infinit wird 0
      a -= im * z.im;
      if (Double.isNaN(a)) a = 0.0; // 0 * infinit wird 0
      double b = re * z.im;
      if (Double.isNaN(b)) b = 0.0; // 0 * infinit wird 0
      b += im * z.re;
      if (Double.isNaN(b)) b = 0.0; // 0 * infinit wird 0
      return make(a, b);
   } // times(Complex)


/** The square of this Complex object (this * this). <br /> */
   public Complex square(){
      if (h<1 || h==9 || h==10) return this; // NaN, +infinit, 0, 1
      if (h==11 || h==13) return M_ONE;
      if (h==12) return ONE;
      if (h < 9) { // infinit
        int hp = h + h -1; // Winkel * 2
        return hm[hp > 8 ? hp-8 : hp];
      } // infinit
      return make (re*re - im*im, 2*re*im);
   } // square()


/** The product of this Complex and i (this * i) . <br />
 *  <br />
 *  The complex number will be multiplied by i respectively j. This is a
 *  90 degree &quot;turn&quot; counterclockwise and the same as dividing
 *  by -i respectively -j.<br />
 */
   public Complex timesI(){
      if (h == 0 || h == 9) return this;  // NaN, 0
      if (h < 9) { // infinit
         int hp = h + 2; // adds 90 degrees (45 degree steps)
         return hm[hp > 8 ? hp-8 : hp];
      } // infinit
      if (h < CFun.NOSH) { // 1,i,-1,-i
         int hp = h + 1; // adds 90 degrees (90 degree steps)
         if (hp > 13) hp -= 4;
         return hm[hp];
      } // 1,i,-1,-i
      // from here on neither infinit nor 0 nor 1,i,-1,-j
      return make(-im, re);
  } // timesI()

/** The product of this Complex and a double (this * x). <br />
 *  <br />
 */
   public Complex times(final double x){
      if (h==0 || Double.isNaN(x)) return NaN;
      if (h == 9 || x == 0.0) return ZERO;
      if (h < 9 && Double.isInfinite(x)) { // both operands infinit
         if (x > 0) return this;
         //  turn by 180 degrees forward or backwards
         return hm[h > 5 ? h -4 : h + 4];
      } // both operands infinit
      // from here at most one is infinite and none is zero
      return make(re * x, im * x);
   } // times(double)

/** The product of this Complex and +infinity  (this * infinit). <br />
 *  <br />
 *  Real and imaginary part of this complex number will get infinite, but 
 *  zero (0.0), {@link #ZERO} and {@link #NaN} are kept.<br />
 *  <br />
 *  If you wish to multiply by -infinity {@code co.explode().negate()} is 
 *  better than {@code co.negate().explode()}.
 */
   public Complex explode(){
      if (h <= 9) return this; // NaN, infinit, 0
      if (h == 10) return INF_0;
      if (h == 11) return INF_90;
      if (h == 12) return INF_180;
      if (h == 13) return INF_270;
      // not totally 0
      if (im == 0.0) { // Imaginärteil 0
          return re < 0 ? INF_180 : INF_0; 
      } // Imaginary part 0
      if (re == 0.0) { // Real par 0
          return im < 0 ? INF_270 : INF_90; 
      } // Real part 0
      return re < 0.0 
         ? im < 0.0 ? INF_225 : INF_135  // links
         : im < 0.0 ? INF_315 : INF_45;  // rechts
   } // explode()

/** The quotient of two Complex objects (this / z). <br />
 *  <br />
 *  If this is {@link #ZERO Complex.ZERO} or z is infinite 
 *  {@link #ZERO Complex.ZERO} is returned.<br />
 */
   public Complex divide(final Complex z){
      if (h == 0 || z.h == 0) return NaN; // NaN
      if (h == 9 || z.h < 9) return ZERO; // 0/z  this/infinite
      if (z.h == 9) return explode();     // divisor 0
      // from here on no zero 
      if (h < 9) { // Dividend infinit, Divisor normal
         int hp = h -1; // 0..7 = 0 .. 315 grad (45 degree steps)
         if (z.re < 0.0) hp += 4; // /-re ==> +180 degrees
         if (z.im < 0.0)
            hp += 2; // /-im ==> +90 degrees
         else if (z.im > 0.0)
            hp += 6; // /+im ==> +270 degrees = -90 degrees
         if (hp > 7) hp -= 8;
         return hm[hp + 1];
      } // Dividend infinit
      // from here on no zero nor infinite    
      double   c = z.re;
      double   d = z.im;
      double scale = Math.max(Math.abs(c), Math.abs(d));
      c /= scale;
      d /= scale;
      double den = c*c + d*d;
      return make ((re*c+im*d)/den/scale, (im*c-re*d)/den/scale);
   } // divide(Complex)

/** The quotient of this Complex and i (this / i). <br />
 *  <br />
 *  The complex number will be divided by i respectively j. This is a
 *  90 degree &quot;turn&quot; clockwise and the same as multiplying
 *  by -i respectively -j.<br />
 */
  public Complex divideI(){
      if (h == 0 || h == 9) return this;  // NaN, 0
      if (h < 9) { // infinit
         int hp = h - 2; // - 90 degree (45 degree steps)
         if (hp < 1) hp += 8;
         return hm[hp];
      } // infinit
      if (h < CFun.NOSH) { // 1,i,-1,-i
         int hp = h - 1; // - 90 degree (90 degree steps)
         if (hp < 10) hp += 4;
         return hm[hp];
      } // 1,i,-1,-i
      // from here neither infinit nor 0 nor 1,i,-1,-j
      return make(im, -re);
  } // divideI()


/** The quotient of two Complex objects (z / this). <br />
 *  <br />
 *  @see #divide(Complex)
 */
   public Complex divideReverse(final Complex z){
      if (h == 0 || z.h == 0) return NaN;  // NaN
      if (z.h == 9 ||  h < 9) return ZERO; // 0/z  z/infinite
      if (h == 9)      return z.explode(); // divisor 0
      // from here no zero
      if (z.h < 9) { // Dividend infinit, Divisor normal
         int hp = z.h -1; // 0..7 = 0 .. 315 degrees (45 degree steps)
         if (re < 0.0) hp += 4; // /-re ==> +180 degrees
         if (im < 0.0)
            hp += 2; // /-im ==> +90 Grad
         else if (im > 0.0)
            hp += 6; // /+im ==> +270 Grad = -90 degrees
         if (hp > 7) hp -= 8;
         return hm[hp + 1];
      } // Dividend infinit
      // from here not zero nor infinite
      double   c = re;
      double   d = im;
      double scale = Math.max(Math.abs(c), Math.abs(d));
      c /= scale;
      d /= scale;
      double den = c*c + d*d;
      return make ((z.re*c+z.im*d)/den/scale, (z.im*c-z.re*d)/den/scale);
   } // divideReverse(Complex)



/** The quotient of this Complex by double (this / x). <br />
 *  <br />
 *  If this is {@link #ZERO} or x is infinite {@link #ZERO Complex.ZERO} is
 *  returned.<br />
 */
   public Complex divide(final double x){
      if (h == 0 || Double.isNaN(x)) return NaN; // NaN
      if (h == 9 || Double.isInfinite(x)) return ZERO; // 0/x  this/infinite
      if (x == 0.0) return explode(); // divisor 0
      // no zero
      if (h < 9) { // Dividend infinit, Divisor normal
         int hp = h -1; // 0..7 = 0 .. 315 degrees (45 degree steps)
         if (x < 0.0) hp += 4; // /-x ==> +180 degrees
         if (hp > 7) hp -= 8;
         return hm[hp + 1];
      } // Dividend infinit
      //no zero nor infinite     
      return make(re/x, im/x);
   } // divide(double)


/** A double divided by this Complex object (x / this). <br /> */
   public Complex divideReverse(final double x){
      if (h == 0 || Double.isNaN(x)) return NaN; // NaN
      if (x == 0.0 || h < 9) return ZERO; // 0/this  x/infinite
      if (h == 9) { // divisor 0
         return x < 0.0 ? INF_180 : INF_0;
      } // divisor 0
      // from here on no zero 
      if (Double.isInfinite(x)) { // Dividend infinit, Divisor normal
         int hp = 1; // 1..8 = 0 .. 315 degrees (45 degree steps)
         if (re < 0.0) hp += 4; // /-re ==> +180 degrees
         if (im < 0.0)
            hp += 2; // /-im ==> +90 degrees
         else if (im > 0.0)
            hp += 6; // /+im ==> +270 degrees = -90 degrees
         if (hp > 8) hp -= 8;
         return hm[hp];
      } // Dividend infinit
      // from here on no zero nor infinite   
      double   c = re;
      double   d = im;
      double scale = Math.max(Math.abs(c), Math.abs(d));
      c /= scale;
      d /= scale;
      double den = c*c + d*d;
      return make ((x*c)/den, (x*d)/den);
   } // divideReverse(double)

/** The absolute value (modulus) of this Complex ( |z| ). <br /> */
   public double abs(){
      if (h == 0) return D_NaN; // NaN
      if (h == 9) return 0.0;
      if (h < 9)  return Double.POSITIVE_INFINITY;
      double x = Math.abs(re);
      double y = Math.abs(im);
      if (x > y) {
         y /= x;
         return x * Math.sqrt(1.0 +  y * y);
      } 
      x /= y;
      return y * Math.sqrt(x * x  + 1.0);
   } // abs();

/** The argument (phase) of this Complex in radian measure. <br /> */
   public double argument() {
      if (h < CFun.NOSH) return ARCS[h];
      return Math.atan2(im, re);
   } // abs()

/** Phase for special hash values. <br />
 *  <br />
 *  This array contains the arguments (phases) in  radian measure for the 
 *  complex values (x,y pairs) having the hash values 0 to 13.<br />
 *  @see de.frame4j.math.CFun#hashCode(double, double) hashCode(double, double)
 */
   static final double[] ARCS = {
//  0      1     2     3     4     5      6        7      8 
   D_NaN, 0.0, PI_4, PI_2, PI_3_4, PI, -PI_3_4, -PI_2, -PI_4,
//     9    10    11   12   13
      0.0,  0.0, PI_2, PI, -PI_2};


/** The translation table for special singleton values. <br /> */ 
   static final int sqrtInd[] = {
//   0   1   2   3   4   5    6    7   8    9   10 
//  NaN  0  45  90  135 180 -135 -90  -45  0.0  1.0
     0,  0,  2,  2,  3,  3,   7,   8,  8,   9,  10 };
 
/** The square root of this Complex. <br />
 *  <br />
 *  The cut is on the negative real half axis. <br />
 */
   public Complex sqrt() {
      if (h <  sqrtInd.length)    // NaN, infinit,  0, 1
          return hm[sqrtInd[h]];
      if (h ==12) return I;
      // Numerically correct version of formula 3.7.27
      // in the NBS Handbook, as suggested by Pete Stewart.
      double t = abs();
      double a, b;  
      if (Math.abs(re) <= Math.abs(im)) {
         // No cancellation in these formulas
         a = Math.sqrt(0.5 * (t + re));
         b = Math.sqrt(0.5 * (t - re));
      } else {
         // Stable computation of the above formulas
         if (re > 0) {
           a = t + re;
           b = Math.abs(im) * Math.sqrt( 0.5 / a);
           a = Math.sqrt(0.5 * a);
         } else {
           b = t - re;
           a = Math.abs(im)*Math.sqrt(0.5 / b);
           b = Math.sqrt(0.5 * b);
         }
      }
      if (im < 0) b = -b;
      return make(a,b);
   } // sqrt()

/**  The exponential of this Complex ( exp(this) ). <br /> */
   public Complex exp(){
      if (h == 0) return NaN;
      if (h == 9) return ONE;
      if (h < 3 || h == 8) return INF_0; // infinit "right"
      if (h > 3 && h < 8)  return ZERO;  // infinit "left"
      if (h == 3 || h == 7) return ONE;  // imaginary infinite ?? 
  
      double cosa = Math.cos(im);
      if (Math.abs(cosa)>1) return NaN;

      double r = Math.exp(re);
      if (Double.isInfinite(r))  r = re;
      if (im == 0.0) return make(r, 0.0);
      return make(r * cosa, r *  Math.sin(im));
   } // exp()

/** The logarithm of this Complex ( log(this) ). <br />
 *  <br />
 *  The cut is on the negative real half axis. <br />
 *  <br />
 *  The result's imaginary part is in the range -pi...+pi.
 */
   public Complex log(){
      if (h == 0 || h == 9) return NaN; // NaN, 0
      if (h == 10) return ZERO;  // 1
      if (h < 9)   return INF_0;
      return make(Math.log(abs()), argument()); 
   } // log()

/** The hyperbolic sine of this Complex (  sinh(this) ). <br /> */
   public Complex sinh(){
      if (h == 0) return NaN; // NaN
      double   coshx = CFun.cosh(re);
      double   sinhx = CFun.sinh(re);
      double   cosy  = Math.cos(im);
      double   siny  = Math.sin(im);
      boolean infiniteX = Double.isInfinite(coshx);
      boolean infiniteY = Double.isInfinite(im);
      double x = 0.0;
      double y = 0.0;

      if (im == 0) {
         x = sinhx;
      } else {
         // A&S 4.5.49
         x = sinhx * cosy;
         y = coshx * siny;
         if (infiniteY)
            return NaN;
         if (infiniteX) {
            x = re * cosy;
            y = re * siny;
            if (infiniteY) x = im;
         }
      }
      return make(x, y);
   }  // sinh()


/** The sine of this Complex ( sin(this) ). <br /> */
   public Complex sin(){
      // sin(z) = -i*sinh(i*z)
      if (h == 0) return NaN;
      return timesI().sinh().divideI();
   }

/** The cosine of this Complex  ( cos(this) ). <br /> */
   public Complex cos(){
      if (h == 0) return NaN;
      // cos(z) = cosh(i*z)
      return timesI().cosh();
   } // cos()

/** The tangent of this Complex ( tan(this) ). <br /> */
   public Complex tan(){
      if (h == 0) return NaN;
      // tan = -i*tanh(i*z)
      return timesI().tanh().divideI();
   } // tan()


/** The inverse sine (arc sine) of this Complex ( asin(this) ). <br />
 *  <br />
 *  The cut is outside of the interval [-1, 1] of the real axis. The result's
 *  real part is in the range [-pi/2, +pi/2].<br />
 */
   public Complex asin(){
      if (h == 0) return NaN;
      double x = 0.0;
      double y = 0.0;
      double r = abs();
      if (Double.isInfinite(r)) {
         boolean infiniteX = !isReFinite();
         boolean infiniteY = !isImFinite();
         if (infiniteX) {
            x = (re > 0 ? CFun.PI_2 : -CFun.PI_2);
            if (infiniteY) x /= 2;
         } else if (infiniteY) {
            x = 0.0; // re / Double.POSITIVE_INFINITY;
         }
         y = 0.0; // im * Double.POSITIVE_INFINITY;
         return make(x, y);
      } 
      if (Double.isNaN(r)) {
         x = y = Double.NaN;
         if (re == 0)  x = 0.0;
      } else if (r < 2.58095e-08) {
         // sqrt(6.0*dmach(3)) = 2.58095e-08
         x = re;
         y = im;
      } else if (re == 0) {
         x = 0;
         y = CFun.asinh(im);
      } else if (r <= 0.1) {
         Complex z2 = square();
         //log(eps)/log(rmax) = 8 where rmax = 0.1
         for (int i = 1;  i <= 8;  ++i) {
            double twoi = 2*(8-i) + 1;
            double twoiD = twoi/(twoi+1.0);
            double tx = (x * z2.re - y * z2.im) * twoiD + 1.0/twoi;
            y  =  (x * z2.im + y * z2.re) * twoiD;
            x  = tx;   
         }
         double tx = x * re - y * im;
         y  =  x * im + y * re;
         x  = tx;   
      } else {
         // asin(z) = -i*log(z+sqrt(1-z)*sqrt(1+z))
         // or, since log(iz) = log(z) +i*pi/2,
         // asin(z) = pi/2 - i*log(z+sqrt(z+1)*sqrt(z-1))
         Complex w = im < 0 ? negative() : this;
         Complex sqzp1 = w.plus(1.0).sqrt();
         if (sqzp1.im < 0.0)
            sqzp1 = sqzp1.negative();
         Complex sqzm1 = w.minus(1.0).sqrt();
         Complex result =  w.plus(sqzp1.times(sqzm1)).log();
         double rx = result.re;
         x = CFun.PI_2 + result.im;
         y = -rx;
      }

      if (x > CFun.PI_2) {
         x = CFun.PI - x;
         y = -y;
      }
      if (x < -CFun.PI_2) {
         x = -CFun.PI - x;
         y = -y;
      }
      if (y < 0) {
         x = -x;
         y = -y;
      }
      return make(x,y);
   } // asin()


/** The inverse cosine (arc cosine) of this Complex ( acos(this) ). <br />
 *  <br />
 *  The cut is outside of the interval [-1, 1] of the real axis.<br />
 */
   public Complex acos(){
      if (h == 0) return NaN;
      if (!isImFinite()) return NaN; // or INF_180 ???
      if (h == 9) 
         return make(CFun.PI_2, 0.0);

      double r = abs();
      if (Double.isInfinite(r)) 
         return make (Math.atan2(Math.abs(im),re), 
                     im * Double.NEGATIVE_INFINITY);
      if (r == 0)
         return make(CFun.PI_2, -im);
      return make(CFun.PI_2, 0.0).minus(asin());
   } // acos();


/** The inverse tangent (arc tangent) of this Complex ( atan(this) ). <br />
 *  <br />
 *  The cut is outside of the interval [-1, 1] of the real axis. The result's
 *  real part is in the range [-pi/2, +pi/2].<br />
 */
   public Complex atan(){
      if (h == 0) return NaN;

      double   r = abs();
      if (Double.isNaN(r)) return NaN;
      if (r < 1.82501e-08) {
         // sqrt(3.0*dmach(3)) = 1.82501e-08
         return this;
      }
      if (Double.isInfinite(r)) 
         return make (re < 0 ? -CFun.PI_2: CFun.PI_2, 0.0);
      if (r < 0.1) {
         Complex z2 = square();
         // -0.4343*log(dmach(3))+1 = 17
         double x = 0.0;
         double y = 0.0;
         for (int k = 0;  k < 17;  k++) {
            double tempRe = z2.re * x - z2.im * y;
            double tempIm = z2.re * y + z2.im * x; 
            int twoi = 2*(17-k) - 1;
            x = 1.0/twoi -tempRe;
            y = -tempIm;
         }
         x = x * re - y * im; // (x,y) * this
         y = y * re + x * im; // (x,y) * this
         return make(x, y);
      }
      if (r < 9.0072e+15) {
         // 1.0/dmach(3) = 9.0072e+15
         double r2 = r*r;
         return make (0.5*Math.atan2(2*re,1.0-r2), 
                      0.25*Math.log((r2+2*im+1)/(r2-2*im+1)));
      }
      return make( (re < 0.0) ? -CFun.PI_2 : CFun.PI_2 , 0.0);
   }  // atan()


/** The hyperbolic of this Complex ( cosh(this) ). <br /> */
   public Complex cosh(){
      if (h == 0) return NaN;
      if (im == 0.0) 
         return make(CFun.cosh(re), 0.0);
  
      double   coshx = CFun.cosh(re);
      double   sinhx = CFun.sinh(re);
      double   cosy  = Math.cos(im);
      double   siny  = Math.sin(im);
      boolean infiniteX = Double.isInfinite(coshx);
      boolean infiniteY = Double.isInfinite(im);
      
      double x = infiniteY ? CFun.D_NaN : coshx * cosy;
      double y = sinhx * siny;

      if (re == 0.0) {
         y = 0;
      } else if (infiniteX) {
         x = re * cosy;
         y = re * siny;
         if (im == 0)  y = 0;
         if (infiniteY) {
            x = y;
         }
      }
      return make(x,y);
   } // cosh()


/** The hyperbolic tanh of this Complex ( tanh(this) ). <br /> */
   public Complex tanh(){
      if (h == 0) return NaN;   // NaN
      if (h == 1) return ONE;   // re=infinit
      if (h == 5) return M_ONE; // re=-infinit
      if (h < 9 ) return NaN;   // im=+infinit
      if (im == 0)
         return make(CFun.tanh(re), 0.0);
      double   sinh2x = CFun.sinh(2*re);
      if (sinh2x == 0) {
         return make(0.0, Math.tan(im));
      }
      double   cosh2x = CFun.cosh(2 * re);
      if (Double.isInfinite(cosh2x)) {  // re doch etwa infinit
         return re > 0 ? ONE : M_ONE;
      }
      double   cos2y  = Math.cos(2 * im);
      double   sin2y  = Math.sin(2 * im);

      // A&S 4.5.51
      double den = (cosh2x + cos2y);
      return make(sinh2x/den, sin2y/den);
   } // tanh()
 

/** Returns this Complex raised to the x power ( this**x ). <br />
 *  <br />
 *  The cut is on the negative real half axis.  Infinity to the zeroth 
 *  gives 1 ({@link #ONE}). 
 */
   public Complex pow(double x){
      if (h == 0 || Double.isNaN(x)) return NaN;
      if (h == 9) return ZERO;
      if (h == 10 || x == 0.0) return ONE;
      double e = Math.pow(abs(), x);
      x *= argument();
      return make (e * Math.cos(x), e *  Math.sin(x));
   } // pow()


/** The inverse hyperbolic sine of this Complex ( asinh(this) ). <br />
 *  <br />
 *  The cut is outside of the interval [-i, i] of the real axis. The result's
 *  imaginary part is in the range [-pi/2, +pi/2].<br />
 *  <br />
 */
   public Complex asinh(){
      if (h == 0) return NaN;
      // asinh(z) = i*asin(-i*z)
      return divideI().asin().timesI();
   } // asinh() 
 
/** The inverse hyperbolic cosine of this Complex ( acosh(this) ). <br />
 *  <br />
 *  The cut is below 1 on the real axis. The result's
 *  imaginary part is in the range [-i*pi, +i*pi].<br />
 *  <br />
 */
   public Complex acosh(){
      if (h == 0) return NaN;
      Complex result = acos().timesI();
      if ( CFun.sign(result.re) ) 
        return result.negative();
      return result;
   }


/** Inverse hyperbolic tangent of this Complex ( atanh(this) ). <br />
 *  <br />
 *  The cut is outside of the interval [-1, 1] of the real axis.<br />
 *  The result's
 *  imaginary part is in the range [-i*pi/2, +i*pi/2].<br />
 */
   public Complex atanh(){
      if (h == 0) return NaN;
      // atanh(z) = i*atan(-i*z)
      return divideI().atan().timesI();
   } // atanh()
 

/** This Complex raised to the Complex z power (this ** z). <br /> */
   public Complex pow(Complex z){
      if (h == 0 || z.h == 0) return NaN;
      if (z.h == 10) return this; // this ** 1;
      if (z.h == 9)  return ONE;  // this ** 0;
      return log().times(z).exp();
   } // pow()


/** Parses a string into a Complex. <br />
 *  <br />
 *  An empty or null String raises a NumberFormatException.<br />
 *  <br />
 *  The syntax of the understood texts is roughly the one given by 
 *  {@link Complex#toString toString()}. Examples:<br />
 *  ( 90 ) ; &#160; 90.0e5 + j 5.8 ; &#160; -j7 ; &#160; 3+I5<br />
 *  ( + j 90 ) ; &#160; 90.0e5 + j *  -5.8 ; &#160; -j7 ; 
 *  &#160; 3 - I * 5<br />
 *  <br />
 *  One surrounding pair of braces &quot;( 99-j )&quot; will be accepted.<br />
 *  Empty braces &quot;(  )&quot; give 0.  A multiplication sign (*) between
 *  j and the imaginary part  &quot;99- j * 89&quot; will be accepted.<br />
 *  Instead of j  i, I and J can be used.<br />
 *  A negative sign before and after the j gives a positive imaginary 
 *  part.<br />
 *  @param s   the complex number (as text)
 *  @return    the complex number (as object)
 *  @throws    NumberFormatException if s can't be interpreted as number
 */
   public static Complex valueOf(String s) throws NumberFormatException {
      if (s == null || (s = s.trim()).isEmpty()) 
          throw new NumberFormatException("No Complex by empty String");
      if (s.charAt(0) == '(') { // (
         if (s.charAt(s.length() - 1) != ')')
            throw new NumberFormatException("No Complex by \"(...");
         s = s.substring(1, s.length() - 1).trim();
         if (s.isEmpty()) return ZERO;
      } // (
      
      int jPos = s.indexOf('j'); // Prefeix's position
      if (jPos < 0) jPos = s.indexOf('J');
      if (jPos < 0) jPos = s.indexOf('i');
      if (jPos < 0) jPos = s.indexOf('I');
      if (jPos < 0) { // no imaginary part
          return make(Double.parseDouble(s),0.0);
      } //  no imaginary part

      boolean sigIm = false;  // false +, true -
      int jSpos = jPos;       // pos of j or Im sign
      for (int i = jPos-1; i >= 0; --i) { // Search for Im  sign
         char c = s.charAt(i);
         if ( c <= ' ' ) continue;
         if ( c == '-') { // - -sign
            jSpos = i;
            sigIm = true;
            break;
         } // - -sign
         if ( c == '+') { // - -sign
            jSpos = i;
            break;
         } // + -sign
      } // sign of Im search

      double y = 1.0;
      while (jPos < (s.length()-1)) {  // Im part as after j 
         String imPart = s.substring(jPos+1).trim();
         if (imPart.isEmpty()) break;
         int malZei =  imPart.indexOf('*');
         if (malZei >= 0) { // j * 
            ++malZei;
            if (imPart.length() == malZei) break;
            imPart = imPart.substring(malZei).trim();
            if (imPart.isEmpty()) break;
         } // j * 
         y = Double.parseDouble(imPart);
         break;
      } // Im part after j is there
      if (sigIm) y = -y;
      double x = 0.0;
      if (jSpos > 1) { // re part is there
         String rePart = s.substring(0,jSpos).trim();
         if (rePart.length() > 0)
            x = Double.parseDouble(s.substring(0,jSpos));
      } // re-Teil da
      return make(x,y);
   }  // valueOf

} // Complex (27.08.2003, 07.01.2005, 14.12.2009)