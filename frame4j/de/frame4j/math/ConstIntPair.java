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

/** <b>A contant pair of integer numbers</b>. <br />
 *  <br />
 *  Objects of implementing classes hold a pair 
 *  ({@link #getX() x}, {@link #getY() y}) of int values.<br />
 *  <br />
 *  <b>Background &quot;Const..&quot;<br /></b>
 *  <br />
 *  In this interface only those methods are defined, described and partly
 *  (default) implemented, that just get (read, format) information from
 *  an (implementing) IntPair-Object. 
 *  All those methods leave the object in question unchanged. Using this 
 *  interface type instead of the type of an implementing class with 
 *  changeable values, prevents accidental changes to the object's mutable
 *  state. Insofar this proceeding has the same effect (and limitations) as
 *  C++'s keyword &quot;const&quot;. Hence the name {@link ConstIntPair}.<br />
 *  <br />
 *  Beyond C++'s limitations, Java allows a hard &quot;watertight&quot; 
 *  enforcement of the described const-behaviour. This is done by a surrogate
 *  class implementing the interface, but being on another branch of 
 *  inheritance than the (real) source object. The source object can be kept 
 *  (secretly, of course) as explicit reference, as done in
 *  {@link IntPair} by {@link IntPair#constView() IntPair.constView()}.<br />
 *  <br />
 *  This proceeding, named  &quot;const-interface-pattern&quot;, was first 
 *  described in 
 *  <a href="https://a-weinert.de" target="_top">Albrecht Weinert</a> 
 *  <a href="https://a-weinert.de/java4ing/index.html"
 *     targe="_top">&quot;Java für Ingenieure&quot;</a> in all detail.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2016 &nbsp; Albrecht Weinert 
 *  
 *  @author   Albrecht Weinert
 *  @version  $Revision: 32 $ ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)
 *  @see de.frame4j.math.IntPairFix
 *  @see de.frame4j.math.IntPair
 */
 // so far    V.184  (26.08.206) : new

public interface ConstIntPair {


/** The first value (x, width, real and else). */
  public abstract int getX();

/** The second value (y, height, imaginary and else). */
  public abstract int getY();
  
/** Make a Dimension. */  
  public default Dimension asDim(){ return new Dimension(getX(), getY()); }

/** Make a Point. */  
   public default Point asPoint(){ return new Point(getX(), getY()); }
   

/** Make a IntPairFix object. <br />
 *  <br />
 *  @return an {@link IntPairFix} object that holds (freezes) the current 
 *          state of this ConstIntPair object
 */  
   public default IntPairFix asIntPairFix(){ 
      return IntPairFix.ofInts(getX(), getY());
   }
   
/** Compare with other Object. <br /> */
   public default boolean equals(final ConstIntPair o){
      if (o == null) return false;
      if (o == this) return true;
      return this.getX() == o.getX() && this.getY() == o.getY();
   } // equals(Object)   

/** Calculate the hash value. <br /> */
   public default int calcHash(){ return  (17 * 31 + getX()) * 31 + getY(); }
   
/** As String (x, y). <br /> */
   public default String asString(){ 
      return "(" + getX() + ", " + getY() + ')';
   } // asString

} // ConstIntPair (26.08.2016)
