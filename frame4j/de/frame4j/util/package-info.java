/**
<b>Support for handling of texts, times and properties</b> <br />
base utilities of the framework Frame4J. <br />
Copyright <a href="#co">&copy;</a>  2009 &nbsp; Albrecht Weinert. <br />

<h3><a name="be">Description</a></h3>
This package de.frame4j.util contains the core helper classes.<br />
<br />
Providing a robust, comfortable and real time usable base for Java 
applications of all kinds is the premier class design goal. Another
one is avoiding throw-away objects and multiple evaluations, for performance
sake. Therefore something looks like a substitute for standard Java-classes 
&mdash; at first sight.<br />
<br />
<h3><a name="sf">Search, replace, parse, format</a></h3>
The class {@link de.frame4j.text.TextHelper} gives many (static helper) 
methods and types therefore. {@link de.frame4j.text.RK} implements the
Rabin Karp (String) search algorithm.<br />
<br />
<h3><a name="kp">Parsing commands</a></h3>
The class {@link de.frame4j.util.Action} heavily supports human friendly 
command line parsers and UIs.<br />
<br />
<h3><a name="ti">Time</a></h3> 
The classes now in  {@link de.frame4j.time} handle (with high-performance) all
time keeping aspects not ported from Frame4J (pre August 2016) to Java8.<br />
<br />
<br />
<br />
<br />
<h3>Terms of use, <a name="co" id="co">copyright</a></h3>
Copyright 2005, 2009 &nbsp; Albrecht Weinert.<br />
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
package de.frame4j.util;