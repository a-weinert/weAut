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
package de.frame4j.io;

import de.frame4j.util.App;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;

/** <b>Values for possible modes of handling (output to) existing files</b>. <br />
 *  <br />
 *  If an existing file is to be used for output the usual choices are<ul>
 *  <li>to overwrite, </li>
 *  <li>to update = to overwrite if certain criteria are met, </li>
 *  <li>to append to, </li>
 *  <li>no action (to leave as is), </li>
 *  <li>create (must not yet exist) or </li>
 *  <li>ask someone what to do.</li></ul>
 *  
 *  This class represents these choices.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2009 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see App
 */
 // so far:   V.o95+  (13.03.2009) : new (String values before)

@MinDoc(
   copyright = "Copyright  2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 50 $",
   lastModified   = "$Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use instead of String or int values",  
   purpose = "values for the possible modes for handling an existing file"
) public enum OutMode {
   
/** Ask what to to if file exists. <br />
 *  ask, -1 
 */
   ASK("ask", -1),
   
/** Append at the end of the existing file. <br />
 *  append, 6 
 */
   APPEND("append", 6),
   
/** Overwrite an existing file. <br />
 *  overwrite, 2 
 */
   OVERWRITE("overwrite", 2),

/** Update, Overwrite an existing file if certain criteria are met. <br />
 *  update", 3
 */
   UPDATE ("update", 3),
 
/** No action / no change of an existing file. <br />
 *  <br />
 *  This is sometimes referred to as &quote;create&quot; in the sense of
 *  &quote;must not exist yet&quot;.<br />
 *  <br />
 *  no change, 1 
 */
   NO_ACTION("no change", 1);

   
/** The name of this OutMode. <br /> */
   public final String name;
   
/** A value for this OutMode. <br /> */
   public final int val;

/** As String (the name). <br />  */
   @Override public String toString(){ return name; }
   
   OutMode(final String name, final int val){
      this.name = name;
      this.val  = val;
   } // constructor
   
/** Get an OutMode by character sequence. <br />
 *  <br />
 *  This method implements the laziest fit to some English (and two German)
 *  keywords. The choice is made on base base of the first or first two non 
 *  white space characters of {@code chose} ignoring case:<ul>  
 *  <li>o?* (0verwrite)</li>
 *  <li>y?*, j? (Yes, Ja -&gt; 0verwrite)</li>
 *  <li>u?* (Update)</li>
 *  <li>n?* (no action)</li>
 *  <li>ap* (append)</li>
 *  <li>an* (append)</li>
 *  <li>a* &nbsp; (ask)<li>
 *  <li>* &nbsp;&nbsp;   (def or no action)</li></ul>
 *     
 * @param chose how to handle existing files (see above)
 * @param def substitute value if chose is empty or not fitting; null
 *        will act as {@link #NO_ACTION} 
 * @return best fit to chose or def or {@link #NO_ACTION}
 */
   public static OutMode of(final CharSequence chose, OutMode def){
      if (def == null) def = NO_ACTION;
      String ch = TextHelper.trimUq(chose, null);
      if (ch == null) return def;
      char c1 = ch.charAt(0);
      if (c1 <= 'Z' ) c1 += 32; // poor man's to lower case (
      if (c1 == 'o' || c1 == 'y'|| c1 == 'j') return OVERWRITE;
      if (c1 == 'u') return UPDATE;
      if (c1 == 'n') return NO_ACTION;
      
      if (c1 != 'a') return def;
      if (ch.length() != 1) {
         char c2 = ch.charAt(1);
         if (c2 <= 'Z' ) c2 += 32;
         if (c2 == 'p' || c2 == 'n') return APPEND;
      }
      return ASK;
   } // of

} // enum OutMode
