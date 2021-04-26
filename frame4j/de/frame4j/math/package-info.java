/**
<b>Support for numerical applications</b><br />
functions, complex numbers. <br />
Copyright <a href="#co">&copy;</a> 1999 - 2002, 2009  &nbsp; Albrecht Weinert<br />
<br />
<br />
<h3><a name="be">Description</a></h3>
This package de.frame4j.math contains classes to support a variety of
numerical applications:<ul>
<li>{@link de.frame4j.math.Complex Complex}<br />
  de.frame4j.math.Complex objects are immutable complex numbers.
  Complex features many functions and operations in complex arithmetic. Most
  technical applications, especially in electrical engineering, may be dealt
  with this set.</li>
<li>{@link de.frame4j.math.CFun CFun}<br />
  {@link de.frame4j.math.SFun SFun}
  de.frame4j.math.CFun and de.frame4j.math.SFun is a collection of 
  (static final) values and functions. Having two classes instead of a bigger
  one is just to have the subset needed for complex arithmetic 
  ({@link de.frame4j.math.CFun CFun}) separate.</li>
<li>Immutable pairs and quadruples of int as base for replacement and 
  to overcome the design flaws of java.awt.Point, java.awtDimension and 
  java.awtRectangle. </li>   
  </ul>

<br />
<h3>Status</h3>
This package is yet only partly ported from de.a_weinert.math to
de.frame4j.math. The urgent need for the 
{@link de.frame4j.math.Complex Complex} class in one Frame4J based project was
the reason to port it here in this partial state. Missing are mainly high 
order and range segmented polynoms as needed e.g. for thermocouples and
thermoresistor applications.<br />

<br />
<h3>Terms of use, <a name="co" id="co">copyright</a></h3>
Copyright 1997 - 2003, 2005 &nbsp; Albrecht Weinert.<br />
<br />
Long time ago some classes from Visual Numerics, Inc. (VNI) 
named SFun and Complex were used in Industrial Development project. A lot of
bugs and errors had to be corrected and at some point the classes were 
modified beyond recognition.<br /> 
<br />
Nevertheless we are very grateful to VNI. Without their sources the mentioned
first project would have been a lot harder. So, of course, VNI's request to
pass on their Copyright and condition remarks, is gladly obeyed:<br />
<br style="clear:both;"/>
<table border="3"  cellspacing="5" cellpadding="10" style="text-align:center;" 
 summary="VN-Copyright"><tr><td>
  Copyright &copy; 1997 - 1998 by Visual Numerics, Inc. 
  All rights reserved.<br />
  Copyright &copy; 1999 - 2002 &nbsp; Albrecht Weinert.<br /><br /> 
  Permission to use, copy, modify, and distribute this software is freely
  granted, provided that the copyright notice above and the following 
  warranty disclaimer are preserved in human readable form.<br /><br />
  Because this software is licenses free of charge, it is provided
  &quot;AS IS&quot;, with NO WARRANTY.<br />
  To the extent permitted by law we
  disclaim all warranties, express or implied, including but not limited
  to its performance, merchantability and fitness for a particular purpose.
  VNI will not be liable for any damages whatsoever arising out of the use
  of or inability to use this software, including but not limited to direct,
  indirect, special, consequential, punitive, and exemplary damages, even
  if advised of the possibility of such damages.</td></tr></table> 

<br />
Please find <a href="../package-summary.html#co">here</a> the terms of use 
for the 
<a href="../package-summary.html#package_description">framework</a> 
 <a href="../package-summary.html#be">Frame4J</a>.<br />
 
 @see <a href="#be">Package description</a>
 @see <a href="#co">Terms of use<br /></a>
 @see de.frame4j
 @author   Albrecht Weinert
 @version  Revision $Revision: 1 $ ($Date: 2017-01-14 13:19:07 +0100 (Sa, 14 Jan 2017) $) 
 */
package de.frame4j.math;
