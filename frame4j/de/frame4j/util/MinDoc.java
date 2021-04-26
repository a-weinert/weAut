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
package de.frame4j.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/** <b>A &quot;Mini&quot; documentation for classes and interfaces</b>. <br />
 *  <br />
 *  All classes and interfaces in Frame4J (should) use this annotation 
 *  replacing the default values accordingly.<br />
 *  <br />
 *  The usage of SVN-keywords in the String-values assumes, that the source
 *  files are always embellished by the the (Frame4J) tool
 *   {@link de.frame4j.SVNkeys SVNkeys} before deployment.<br />
 *  <br />
 *  The standard usage is the annotation of each class by 
 *  (example):<code><pre>
 * &nbsp; @MinDoc(
 * &nbsp;   copyright = "Copyright 2016  A. Weinert",
 * &nbsp;   author    = "Albrecht Weinert",
 * &nbsp;   version   = "V.&#36;Revision: 37 &#36;",
 * &nbsp;   lastModified   = "&#36;Date: 2010-02-14 16:34 &#36;",
 * &nbsp;   lastModifiedBy = "&#36;Author: a_weinert &#36;",
 * &nbsp;   usage   = "use where one Writer is handled but two are needed",  
 * &nbsp;   purpose = "a Writer that outputs the content to two branches"
 * &nbsp;) public class TeeWriter {  </pre></code>
 * 
 *  The values could be obtained at runtime for and or within class WithDoc 
 *  for example
 *  by<code><pre>
 * &nbsp; MinDoc ano  = WithDoc.class.getAnnotation(MinDoc.class);
 * &nbsp; String purp = ano.purpose(); 
 * &nbsp; String copr = ano.copyright(); 
 * &nbsp; String vers = ano.version();
 * &nbsp; String lamo = ano.lastModified();
 * &nbsp; String moby = ano.lastModifiedBy(); </pre></code>
 * 
 * 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright  2009  &nbsp; Albrecht Weinert <br />
 *  <br />
 *  @author $Author: A. Weinert$
 *  @version  $Revision: 33 $ ($Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $)
 */

@Target( {ElementType.TYPE} ) 
@Retention( RetentionPolicy.RUNTIME ) 
@Documented
@Inherited
@MinDoc(
      copyright = "Copyright 2009, 2021  A. Weinert",
      author    = "Albrecht Weinert",
      version   = "V.$Revision: 33 $",
      lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
   // lastModifiedBy = "$Author: albrecht $",
      usage   = "give version information for types in a uniform way",  
      purpose = "holds meta information for classes available at runtime"
) public @interface MinDoc {
   
/** The version / revision . <br /> */
   String version() default "(unknown)";
 
/** Last modified date. <br /> */
   String lastModified() default "(unspecified)";

/** Last modified by. <br /> */
   String lastModifiedBy() default ComVar.EMPTY_STRING;

/** Copyright notice. <br /> */
   String copyright() default "Copyright 2021";

/** The author. <br /> */
   String author() default "Albrecht Weinert";

/** The purpose. <br />  */
   String purpose() default ComVar.EMPTY_STRING;

/** The usage. <br />  */
   String usage() default ComVar.EMPTY_STRING;
   

} // annotation MinDoc (09.01.2009)
