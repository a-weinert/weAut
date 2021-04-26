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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.PrintWriter;

import de.frame4j.graf.AskDialog;
import de.frame4j.util.AppLangMap;
import static de.frame4j.util.ComVar.*;
import de.frame4j.util.MinDoc;
import de.frame4j.util.PropMap;
import de.frame4j.util.Prop;
import de.frame4j.text.TextHelper;

/** <b>A Visitor for files and directories</b>. <br />
 *  <br />
 *  An object of an implementing class realises an action on (exactly) one 
 *  file or directory implemented as the method 
 *  {@link #visit(File) visit}({@link File}).
 *  This method is the concrete action and may be whatever the application
 *  has to to &mdash; delete, list, ask an controller / observer what to do
 *  or else.<br />
 *  <br />
 *  This interface is implemented by embedded classes here,
 *  {@link FileVisitor.Ask}, {@link AskConsImpl}, 
 *  {@link AskGrafImpl}) and in the class {@link FileService}. 
 *  Via {@link FileService} it is used in most Frame4J's file handling 
 *  tools.<br />
 *  <br />
 *  <br />   
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2000 - 2003, 2004 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see FileService
 *  @see FileCriteria
 *  @see FileService#doUpdate
 */
 // so far    V00.00 (17:09 06.09.2000) :  moved out of FS
 //           V01.00 (08.12.2001 12:35) :  AskConsImpl, AskGrafImpl 
 //           V01.20 (06.06.2002 15:31) :  de, /**
 //           V02.00 (24.04.2003 16:54) :  CVS Eclipse
 //           V02.20 (24.04.2003 16:54) :  abstract class Ask
 //           V.o52+ (21.01.2009 11:32) :  ported

@MinDoc(
   copyright = "Copyright  2001 - 2002, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use implementing classes to act on directories and files",  
   purpose = "a visitor for files and directories"
) public interface FileVisitor {

/** The &quot;deed&quot; or action with the file visited. <br />
 *  <br />
 *  This method has to implement the concrete action on a single file or on a
 *  single directory. Usually implemented by application classes itself or 
 *  their inner classes the method will be responsible for getting all 
 *  application information from there as well as doing all the application's
 *  book keeping.<br />
 *  <br />
 *  In that sense for actions there is no need for a return value and 
 *  recommendation for it. The class {@link FileService}'s working methods
 {@link FileService#dirVisit(String, String, FileVisitor, FileVisitor, FileVisitor, FileVisitor)
 *  dirVisit(.. FileVisitors ..)} for example totally ignores those returned
 *  values. Nevertheless each implementation must document its 
 *  return values. See 
 * {@link FileService#makeInCollectionVisitor(java.util.Collection, boolean)
 *                     makeInCollectionVisitor()},
 *
 {@link FileService#makeOutputInfoVisitor(PrintWriter) makeOutputInfoVisitor()}
 *  and
 {@link FileService#makeOutputListVisitor(PrintWriter)  makeOutputListVisitor()},
 * as examples.<br />
 *  <br />
 *  Besides this procedural or action aspects classes of this type may 
 *  alternatively implement a filter or functional aspect (or both). For 
 *  providing a filter response or function value for the 
 *  {@link File file} {@link #visit(File) visited} the returned value
 *  is what matters. See {@link AskConsImpl} and 
 *  {@link AskGrafImpl AskGrafImpl} as examples. The return value has to be specified 
 *  fitting the intended application.<br />
 *  <br />
 *  The recommendation for questioning filters &mdash; implemented here by
 *  {@link AskConsImpl AskConsImpl} and {@link AskGrafImpl AskGrafImpl}
 *  &mdash; is:<br />
 *  <br style="clear:both;" />
 *  <table cols="4" style="width:80%;" summary="visit return values" 
 *  cellpadding="5" cellspacing="0" border="1" style="text-align:center;">
 *  <tr><th style="width:20%;">Return value</th><th style="width:16%;">Key</th>
 *  <th style="width:16%;">Button</th>
 *  <th>Meaning</th></tr>
 *  <tr><td style="text-align:center;">1</td><td style="text-align:center;"> j J y Y</td>
 *       <td style="text-align:center;"> Left </td>
 *      <td>Acknowledge (Answer Ja Yes)</td></tr>
 *  <tr><td style="text-align:center;">2</td><td style="text-align:center;">o O</td>
 *       <td style="text-align:center;"> - </td>
 *      <td>Acknowledge explicitly for overwriting</td></tr>
 *  <tr><td style="text-align:center;">3</td><td style="text-align:center;">a A</td>
 *      <td style="text-align:center;"> Middle </td>
 *      <td>Acknowledge explicitly for appending</td></tr>
 *  <tr><td style="text-align:center;">0</td><td style="text-align:center;">others</td>
 *       <td style="text-align:center;">Right</td>
 *      <td>Negative, No, Nein</td></tr>
 *  <tr><td style="text-align:center;">-2</td><td style="text-align:center;">q Q</td>
 *      <td style="text-align:center;"> X<br />Menu </td>
 *      <td>Request to end (Quit)</td></tr>
 *  <tr><td style="text-align:center;">-1</td>
 *       <td colspan="3">No answer / reaction within waiting time
 *        ( {@link Ask#maxWait maxWait}/10 s).</td></tr></table>
 *  <br style="clear:both;" />
 *  
 *  @param  dD the file or directory to act upon or to determine a filter / 
 *          function response about
 *  @return the response; mostly irrelevant under (pure) action / procedural 
 *          aspects
 *  @see FileService#doUpdate doUpDate()
 */ 
   public int visit(File dD);

//===========================================================================

/** <b>An implementation of FileVisitor: Ask a question on a file</b>. <br />
 *  <br />
 *  Objects of inheriting classes implement a {@link FileVisitor}, the method
 *  {@link #visit(File)} of which asks a question on the file or directory
 *  visited. Inheriting classes are {@link AskConsImpl} and
 *  {@link AskGrafImpl}.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2004 Albrecht Weinert.<br />
 *  <br />
 */
  // so far V00.01 (01.12.2000 15:52) : neu
  //        V00.02 (15.11.2001 11:52) : /**
  //        V00.03 (07.12.2001 18:03) : public, Q

@MinDoc(
   copyright = "Copyright  2001 - 2002, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "see enclosing interface FileVisitor",
   lastModified   = "see enclosing interface FileVisitor",
   lastModifiedBy = "see enclosing interface FileVisitor",
   usage   = "a (base for a) visitor for files",  
   purpose = "ask a question on the visited file (or directory)"
) public static abstract class Ask implements FileVisitor, Serializable {

/** Version number for serialising.  */
   static final long serialVersionUID = 260153007700201L;
//                                      magic /Id./maMi
      
/** Delete mode. <br />
 *  <br />
 *  Delete mode means displaying / offering only two not three possible 
 *  answers to the user as well as switching some property keys.<br />
 */      
      public final boolean deleteMode; 
      
/** The question. <br />
 *  <br />
 *  {@code askPatt} is a one line pattern for the message formatter
 *  {@link TextHelper#messageFormat(StringBuilder, CharSequence, Object)}.
 *  The spot {0} will be replaced by the file path.<br />
 *  <br />
 *  Example:<br />
 *  &nbsp; &quot;{0} löschen ?&quot; &nbsp; (German) or <br />
 *  &nbsp; &quot;Delete {0} ?&quot; &nbsp;(English)<br />
 *  <br />
 *  When setting from a {@link PropMap} or {@link AppLangMap} the key used is
 *  &quot;faskpatt&quot;. That (nationalised) message pattern can, of course
 *  be used elsewhere.<br />
 *  <br />
 */
      protected String askPatt;

/** Appendix to the question respectively answer suggestions. <br />
 *  <br />
 *  {@code ansPrompt} is to be shown as second line in a window respectively
 *  as not RET/LF terminated prompt in the console case.<br />
 *  It will be used as is (it no message format pattern).<br />
 *  <br />
 *  When setting from a {@link PropMap} or {@link AppLangMap} the key used is
 *  either &quot;faskpmt&quot; or &quot;daskpmt&quot; for 
 *  {@link #deleteMode} false / true.<br />
 */
      protected String ansPrompt;

/** Lower line / explanation. <br />
 *  <br />
 *  Shall be shown as lower line in case of window.<br />
 *  <br />
 *  When setting from a {@link PropMap} or {@link AppLangMap} the key used is
 *  either &quot;fasklow&quot; or &quot;dasklow&quot; for 
 *  {@link #deleteMode} false / true.<br />
 */
      protected String lower;

/** Text for left (acknowledge) button. <br />
 *  <br />
 *  Property key ({@link #deleteMode} false) : &quot;faskyes&quot;<br />
 *  respectively (true) &quot;daskyes&quot;.<br />
 */
      protected String yes;

/** Text for right (reject) button. <br />
 *  <br />
 *  Property key : &quot;faskno&quot;<br />
 */
      protected String no;

/** Text for middle button. <br />
 *  <br />
 *  The middle button is intended for the &quot;append&quot; answer.<br />
 *  The button (in case of graphical implementation) shall only be displayed
 *  if the text here is not empty.<br />
 *  <br />
 *  Property key ({@link #deleteMode} false) : &quot;faskappquot;<br />
 *  If {@link #deleteMode} is true {@code append} is forced to null.<br />
 */
      protected String append;

/** Maximum wait time for user response in 1/10 s. <br />
 *  <br />
 *  A value &lt; 0 means unlimited waiting / blocking.<br />
 *  A value &lt; 50 (5 seconds) is to short for most humans to give a sound 
 *  answer. It should be raised to 50.<br />
 *  <br />
 *  default: 180 (= 18s)
 */
      public int maxWait = 180;
      
/** Set by (two) PropMaps. <br />
 *  <br />
 *  The properties of this object will be set by those of {@code prop1} and as
 *  substitute {@code prop2}. Find the keys in the documentation of the 
 *  object variables.<br />
 *  <br />
 *  @return number of the set properties; -1, if {@code prop1} is null
 *  @see #set(PropMap)
 */      
      public int set(final PropMap prop1, final PropMap prop2){
         if (prop1 == null) return -1;
         int ret = 0;
         String value =prop1.value("faskno");
         if (value == null && prop2 != null)
                value = prop2.value("faskno");
         if (value != null) {
            this.no = value;
            ++ret;
         }   

         if (deleteMode) {
            this.append = null;
         } else {
            value = prop1.value("faskapp");
            if (value == null && prop2 != null)
                          value = prop2.value("faskapp");
            if (value != null) {
               this.append = value;
               ++ret;
            }   
         }
         
         String key = deleteMode ? "daskyes" : "faskyes";
         value = prop1.value(key);
         if (value == null && prop2 != null) value = prop2.value(key);
         if (value != null) {
            this.yes = value;
            ++ret;
         }   
         
         key = deleteMode ? "dasklow" : "fasklow";
         value = prop1.value(key);
         if (value == null && prop2 != null) value = prop2.value(key);
         if (value != null) {
            this.lower = value;
            ++ret;
         }   
         key = deleteMode ? "daskpmt" : "faskpmt";
         value = prop1.value(key);
         if (value == null && prop2 != null) value = prop2.value(key);
         if (value != null) {
            this.ansPrompt = value;
            ++ret;
         }   
         value = prop1.value("faskpatt");
         if (value == null && prop2 != null) value = prop2.value("faskpatt");
         if (value != null) {
            this.askPatt = value;
            ++ret;
         }   
         return ret;
      } // set(PropMap, PropMap)

/** Set by a PropMap.<br />
 *  <br />
 *  The properties of this object will be set by those of {@code prop} and as
 *  substitute by that of an {@link AppLangMap} object determined by the rules
 *  of the  {@link Prop#valueLang(CharSequence)}.<br />
 *  <br />
 *  Find the keys in the documentation of the object variables.<br />
 *  <br />
 *  @return number of the set properties; -1, if {@code prop1} is null
 *  @see #set(PropMap) *  Schlüssel siehe in den Einzelbeschreibungen der Variablen.<br />
 *  @see #set(PropMap, PropMap)
 *  @see AppLangMap#getMap(CharSequence)
 *  @see PropMap#getLanguage()
 */      
      public int set(final PropMap prop){
         if (prop == null) return -1;
         final String lang = prop.getLanguage();
         final AppLangMap alm = AppLangMap.getMap(lang);
         return set(prop, alm);
      } // set(Prop)

/** Make an Ask object (sole constructor). <br />
 *  <br />
 *  This constructor forces inheriting classes (respectively their 
 *  constructors) to finally determine the  {@link #deleteMode} before.<br />
 *  Additionally a {@link PropMap} may be provided to set all properties
 *  on construction.<br />
 */
      protected Ask(final boolean deleteMode, final PropMap prop){
         this.deleteMode = deleteMode;
         set(prop);
      } // Ask()
      
/** Dispose the Ask object. <br />
 *  <br />
 *  This method shall free all resources of this Ask object. It shall be 
 *  called by the application when using this  Ask object the last time.<br />
 *  <br />
 *  This implementation does nothing.<br />
 */
      public void dispose(){ }  

   } // Ask  (14.12.2004)        ==========================================

/** <b>An implementation of FileVisitor (question at console)</b>. <br />
 *  <br />
 *  Objects of this class implement a {@link FileVisitor} the  method
 *  {@link #visit(File)} of which asks a question on the file 
 *  visited by outputting a report / question on the console and 
 *  a prompt line. Then the answer is read from that console / shell.<br />
 *  <br />
 *  The report line, denomination the file using {@link #askPatt} will be 
 *  output on System.err (wrapped as PrintStream)  or on an extra provided 
 *  {@link java.io.PrintStream} {@link #daOut}.<br />
 *  The prompt line will be output always on the (decorated)  System.err. 
 *  Then, within a limited waiting time, an answer is expected on System.in,
 *  usually the keyboard with the console in focus. As Java (probably 
 *  due to platform independence / portability) is lacking a real keyboard
 *  input and status access the answer key has to be followed by return (at 
 *  least on Windows).<br />
 *  <br />
 *  Applications that do not own a console or shell, as they were started by 
 *  javaW.exe or by double click on a .jar must use 
 *  {@link AskGrafImpl} instead.<br />
 *  <br />
 *  Hint: The rationale behind the optionally different Writers for
 *  report and prompt line is the recommendation to use a  { TeeWriter}
 *  for the {@link java.io.PrintStream} {@link #daOut}. The TeeWriter
 *  may optionally branch to a file giving the logging or most of it 
 *  for free.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2000 - 2001, 2004 &nbsp; Albrecht Weinert.<br />
 *  <br />
 *  @see FileService 
 *  @see FileCriteria
 *  @see FileService#doUpdate
 *  @see AskGrafImpl
 */
  // so far V00.01 (01.12.2000 15:52) : new
  //        V00.02 (15.11.2001 11:52) : /**
  //        V00.03 (07.12.2001 18:03) : public, Q

@MinDoc(
   copyright = "Copyright  2001, 2004, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "see enclosing interface FileVisitor",
   lastModified   = "see enclosing interface FileVisitor",
   lastModifiedBy = "see enclosing interface FileVisitor",
   usage   = "a visitor for files",  
   purpose = "ask a question on the visited file (on console)"
) public static class AskConsImpl extends Ask {

/** Version number for serialising.  */
   static final long serialVersionUID = 260153007800201L;
//                                      magic /Id./maMi

/** The PrintStream to display the file denomination and the question. <br />
 *  <br />
 *  If not set separately by constructor parameter it will be the (wrapped) 
 *  {@link System}.{@link System#err err}.<br /> 
 *  <br />
 *  Never null.<br />
 */
      protected final PrintWriter daOut;

      
/** The PrintStream to display the user prompt. <br />
 *  <br />
 *  Will be connected to {@link System}.{@link System#err err} by the 
 *  constructor trying to use {@link de.frame4j.util.ComVar#CONSOL_ENCODING}.<br /> 
 *  <br />
 *  Never null.<br />
 */
      protected final PrintWriter daPrm = hasCons ? cons.writer()
                       : new PrintWriter(System.err, true);


/** Make an AskConsImpl object. <br />
 *  <br />
 *  {@link #deleteMode} will be set finally by the parameter provided.
 *  It will be tried, to use an  {@link AppLangMap} object to the the textual 
 *  properties, see  {@link FileVisitor.Ask Ask}.<br />
 *  <br />
 *  If parameters {@code fragPatt} resp. {@code ansPrompt} are given they will
 *  set {@link #askPatt} resp. {@link #ansPrompt}.</br>
 *  <br />
 *  @see AppLangMap#getUMap()
 *  @see #daOut
 */
      public AskConsImpl(final boolean deleteMode,
                              String fragPatt, String ansPrompt,
                              PrintWriter daOut) {
         super(deleteMode, AppLangMap.getUMap());
         if (fragPatt != null) this.askPatt = fragPatt;
         if (ansPrompt != null) this.ansPrompt = ansPrompt;


         this.daOut = daOut != null ? daOut : this.daPrm;
      } // AskConsImpl(boolean, ..
      

      static final PrintStream consAskPrStr = System.err;
/** Make an AskConsImpl object. <br />
 *  <br />
 *  {@link #deleteMode} will be set finally by the parameter provided.
 *  It will be tried to set this objects properties by {@code prop}.<br />
 *  <br />
 *  @see FileVisitor.Ask
 *  @see #daOut
 */
      public AskConsImpl(final boolean deleteMode, final PropMap prop,
                              PrintWriter daOut) {
         super(deleteMode, prop);
         this.daOut = daOut != null ? daOut : this.daPrm;
      } // AskConsImpl(boolean, PropMap,..


/** The &quot;deed&quot; or action with the file visited. <br />
 *  <br />
 *  If {@code dD} is null 0 (as negative acknowledge) is returned 
 *  immediately.<br />
 *  If the file {@code dD} does not exist 1 (as acknowledge) is returned 
 *  without further actions.<br />
 *  <br />
 *  Otherwise see {@link FileVisitor#visit(File) FileVisitor.visit()} 
 *  for the return values recommended and implemented here.<br />
 *  <br />
 *  @see FileVisitor#visit(File)
 *  @see FileVisitor.Ask#visit(File)
 *  @return the answer
 */
      @Override public int visit(File dD){
         if (dD == null ) return 0;
         if (! dD.exists()) return 1;
         String frage = TextHelper.messageFormat(null, askPatt, 
                                 dD.getPath()).toString();
        /*daOut*/System.out.println(frage);
        /*daOut*/System.out.flush();
         char antw = 'n';
         try   {
            while ( System.in.available() > 0) System.in.read();              
            /*daPrm.*/System.out.flush();
            /*daPrm.*/System.out.print(ansPrompt);
            /*daPrm.*/System.out.flush();
            waitForInput: for (int i = 0;;) {
               try {
                  Thread.sleep (100L);
               } catch (Exception e){} 
               if (System.in.available() > 0) {
                  antw = (char)System.in.read();
                  break waitForInput;
               } // if
               if (++i >= maxWait && i >= 50)  return -1; // time out
            } // for waitForInput
         } catch (IOException e){}
         if (antw < 'a') antw += 32; // so simple as only pure ASCII first c.
         if (antw == 'j' || antw == 'y') return 1;
         if (antw == 'o') return 2;
         if (antw == 'a') return 3;
         if (antw == 'q') return -2;

         return 0;  // no
      } // visit()

   } // AskConsImpl  (10.12.2001 19:48)

//===========================================================================

/** <b>An implementation of FileVisitor (question at a window)</b>. <br />
 *  <br />
 *  Objects of this class implement a {@link FileVisitor} the method
 *  {@link #visit(File)} of which asks a question on the file 
 *  visited graphically using an ({@link AskDialog}.<br />
 *  Up to three answers suggested may be given by mouse or Tab button and
 *  Return button.<br />
 *  <br />
 *  Applications that own a console or shell, by grace of their JVM being
 *  started there by java.exe (not javaW.exe) may use 
 *  {@link FileVisitor.AskConsImpl} instead.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2001 - 2002, Albrecht Weinert<br />
 *  <br />
 *  @see FileService
 *  @see FileVisitor
 *  @see FileVisitor.AskConsImpl
 */
  // so far V00.01 (08.12.2001 13:06) : new
  //        V00.02 (17.05.2002 12:52) : de, small corrections

@MinDoc(
   copyright = "Copyright  2001, 2004, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "see enclosing interface FileVisitor",
   lastModified   = "see enclosing interface FileVisitor",
   lastModifiedBy = "see enclosing interface FileVisitor",
   usage   = "a visitor for files",  
   purpose = "ask a question on the visited file (by dialog window)"
) public static class AskGrafImpl extends Ask {

/** Version number for serialising.  */
      static final long serialVersionUID = 260153007900201L;
//                                      magic /Id./maMi

/** The Dialogue. <br /> */
      protected final AskDialog theDialog;
 
/** Title for the graphical window. <br />
 *  <br />
 *  PropMap key: &quot;fileDialogTitle&quot;<br />
 *  default: null<br />
 */      
      public String title;
      
/** Parent object for the graphical (prompt) window. . <br />
 *  <br />
 *  default: null
 */
      protected Object parent;

/** Make an AskGrafImpl object, setting by a PropMap. <br />
 *  <br />
 *  {@link #deleteMode} will be set finally by the parameter provided.
 *  It will be tried to set this objects properties by {@code prop}.<br />
 *  <br />
 *  If {@code prop} has a property &quot;fileDialogTitle&quot; its value will 
 *  be used to set {@link #title}.<br /> 
 *  <br />
 *  If {@code parent} is not null and no {@link CharSequence},it will be tried
 *  to use it as parent of the asking window. If it is a 
 *  {@link CharSequence} it will be used as {@link #title}.<br /> 
 *  <br />
 *  @param parent used as parent window or title (if feasible)
 *  @param modal  true locks a given parent window while waiting for
 *                closing the dialog window
 *  @param prop   PropMap to set properties
 */
      public AskGrafImpl(final boolean deleteMode, final PropMap prop,
                        Object parent, boolean modal){
         super(deleteMode, prop);
         if (prop != null) {   //  XXXX  getString
            title = prop.valueLang("fileDialogTitle", title);
         }
         if (parent instanceof CharSequence) {
            title = parent.toString();
         } else {
            this.parent = parent;
         }
         
         theDialog = new AskDialog(this.parent, title, modal,
                 " <<<  file >>>>> ",
                 yes, append, no, this.lower);
         theDialog.multipleUse = true;
         theDialog.setUpperMonospaced();
      }  // constructor  


/** The &quot;deed&quot; or action with the file visited. <br />
 *  <br />
 *  If {@code dD} is null or if {@link #dispose()} was called 0 (as negative
 *  acknowledge) is returned immediately.<br />
 *  If the file {@code dD} does not exist 1 (as acknowledge) is returned 
 *  without further actions.<br />
 *  <br />
 *  Otherwise see {@link FileVisitor#visit(File) FileVisitor.visit()} 
 *  for the return values recommended and implemented here.<br />
 *  <br />
 *  @see FileVisitor#visit(File)
 *  @return the answer
 */
      @Override public int visit(File dD) {
         if (dD == null || theDialog == null) return 0;
         if (! dD.exists()) return 1;
         String frage = TextHelper.messageFormat(null, askPatt, 
                                 dD.getPath()).toString();
         theDialog.maxWait = this.maxWait;
         int winAnt = theDialog.getAnswer(frage, lower);

         if (winAnt == 0) return 3; // middle button (0) > append (3) 
         if (winAnt == 2) return 1; // left button   (2) > yes ja (1) 
         if (winAnt == -2) return -2; // X oder Menu Exit
         return 0;  // default no nein
      } // visit()

/** Dispose the AskGrafImpl object and its resources. </br>
 *  <br />
 *  This object and its graphical resources (window) and threads  may be used
 *  multiple times. Hence it is highly recommended to signal a clear end of
 *  usage.<br />
 *  <br />
 *  Afterwards {@link #visit(File) visit()} returns 0 always.
 */
      @Override public void dispose() {
         if (theDialog == null) return ;
         theDialog.dispose();
      }  // dispose() 

   } // AskGrafImpl  (17.05.2002, 17.12.2004)

} // interface FileVisitor (06.06.2002) 
