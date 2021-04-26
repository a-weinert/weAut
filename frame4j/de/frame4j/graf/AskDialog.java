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
package de.frame4j.graf;

import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import de.frame4j.util.MinDoc;
import de.frame4j.util.App;
import de.frame4j.text.TextHelper;


/** <b>A dialog window for common use</b>. <br />
 *  <br />
 *  For intermediate displays and interposed questions graphical applications
 *  as well as non graphical ones often need a dialogue window. 
 *  Usually this consists of one or two texts (may be one of it with input
 *  facilities) and up to three buttons to get user responses. Figure 1 shows
 *  an example. With the options mentioned the majority of use cases is 
 *  covered.
 * 
 *  <a href="doc-files/askdialog-ex1.png" target="_top"><img
 *  src="doc-files/askdialog-ex1.png" style="border:0;" width="310" height="135"
 *  alt="Click for the picture alone" title="Click for the picture alone"
 *  style="margin: 8px 98px; textalign:left;" /></a><br style="clear:both;" />
 *  Figure 1: Simple example of an {@link AskDialog} window.<br />
 *  <br />
 *  Objects of this class are that kind of dialogue window featuring thread
 *  and event handling and hence giving all comfort to the using class.
 *  Compared to a  {@link javax.swing.JOptionPane} used often in similar use
 *  cases the main differences are:<ul>
 *  <li> <b> -</b> &nbsp; less flexibility in graphical layout,</li>
 *  <li> <b>+</b> &nbsp; less difficult handling,</li>
 *  <li> <b>+</b> &nbsp; optional limiting the wait time for user responses
 *         (as the time-out feature is often badly needed),</li>
 *  <li> <b>+</b> &nbsp; does not (contrary to 
 *      {@link javax.swing.JOptionPane}) forbid the normal usage and ending
 *       of a (non graphical) application.</li></ul>
 * 
 *  Remark: Normal ending means a well behaved end of all threads without 
 *  &quot;violence&quot; like System.exit(); see Hint&nbsp;1. Applications, 
 *  using {@link AskDialog} objects don't get exit() problems, even if non 
 *  graphical and also if not Frame4J or {@link App} based.<br />
 *  <br />
 *  Hint 1: Also with non graphical applications having used graphical 
 *  elements the JVM has to be killed with System.exit() sometimes. The reason
 *  is the (buggy) running of graphical event threads as non daemons even
 *  if the last usage / life / visibility of graphical elements was hours 
 *  ago. On the other hand calling System.exit() in one thread too early may
 *  have disastrous effects on server applications as undeniably killing
 *  all running threads. Frame4J organises the JVM end for all applications
 *  based on it (on {@link  App}) safely.<br />
 *  <br />
 *  Hint 2: Depending on the construction with or without parent window, the
 *  used dialogue window is made as
 *  {@link javax.swing.JDialog} respectively as
 *  {@link javax.swing.JFrame} (the common grandma is
 *  {@link java.awt.Window}). As the event handling of Dialogs is problematic
 *  on some platforms the usage of Frames should be preferred.<br />
 *  <br />
 *  Hint 3: The response code values {@link #YES}, {@link #NO} etc. differ 
 *  from those of {@link javax.swing.JOptionPane}. <br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 1998 - 2002, 2004 &nbsp; Albrecht Weinert<br />
 *  <br />
 */
 // so far    V00.00 (28.04.2000) :  ex weinertBib
 //           V00.01 (11.06.2000) :  makeButton() moved to GrafHelper 
 //           V00.03 (27.04.2001) :  Frame oder Dialog, Key
 //           V01.00 (07.12.2001) :  multipleUse, CLOSED, maxWait 
 //           V01.10 (27.05.2002) :  de, Exception f. modal catch 
 //           V02.00 (24.04.2003) :  CVS Eclipse
 //           V02.03 (26.04.2003) :  JavaDoc Bug (1.4.2beta) detected  
 //           V02.05 (22.07.2003) :  AWT and swing  
 //           V02.06 (23.07.2003) :  WakeThread, modal behaviour corrected
 //           V02.25 (30.12.2004) :  swing only; text entry
 //           V.o52+ (12.02.2009) :  ported to Frame4J
 //           V.179+ (10.01.2010) :  getLab2Text error corrected

@MinDoc(
   copyright = "Copyright 1998 - 2009, 2014  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 39 $",
   lastModified   = "$Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "construct and get answer (or static getAnswer)",  
   purpose = "dialog or info window with timeout"
) public class AskDialog implements ActionListener, KeyListener,
                                                         DocumentListener {
   
/** One time (parent-less) button inquiry. <br />
 *  <br />
 *  This is the most common static helper method to get user responses 
 *  (as button clicks). It encapsulates the making of a {@link AskDialog} 
 *  object for one time usage and the waiting for an answer in the calling 
 *  thread, guarded by settable time-out.<br />
 *  <br />
 *  @param title  the title for this dialogue.<br />  &nbsp;
 *    if null and parent is of type App, the App's title 
 *    {@link App#getTitle() App.title} is used.
 *  @param upper  upper display line(s) (or null)
 *  @param yes    the left (Yes) button's label (or null)
 *  @param cancel the middle (Cancel) button's label (or null)
 *  @param no     the right (No) button's label (or null)
 *  @param lower  lower display line (or null)
 *  @param upperMonospaced true means an upper text is displayed by a 
 *          non proportional font
 *  @param bg   background colour (or null meaning platform standard)
 *  @see #getAnswer()
 *  @see #setUpperMonospaced()
 *  @see #AskDialog(Object, String, boolean, CharSequence, CharSequence, CharSequence, CharSequence, CharSequence)
 *  @return response; see upper table
 */   
   public static int getAnswer(String title, CharSequence upper, 
                           boolean upperMonospaced,
                           CharSequence yes, CharSequence cancel,
                           CharSequence no,
                           int waitMax,
                           CharSequence lower,
                           Color bg         ){
      AskDialog aD = new AskDialog(null, title,  // parent , title
                              false,  //  modal
                              upper,  //  the report
                              yes, cancel, no, // buttons
                              lower); //  question
      aD.maxWait     = waitMax;
      aD.multipleUse = false;
      if (upperMonospaced) aD.setUpperMonospaced(); // not proportional
      if (bg != null) aD.bg = bg;  // background colour
      return aD.getAnswer();
   } // static getAnswer(String, CharSequence, ...

/** One time (parent-less) text inquiry. <br />
 *  <br />
 *  This static helper method encapsulates the making of a {@link AskDialog} 
 *  object for one time usage and the waiting for an answer in the calling 
 *  thread, guarded by adjustable time-out.<br />
 *  <br />
 *  It returns the lower text that may be edited by the user, if the user
 *  closes the window by the Yes/OK button; otherwise null.<br />
 *  <br />
 *  If yes and lower are null, null is returned at once as consequence.<br />
 *  <br />
 *  @param title  the title for this dialogue.<br />  &nbsp;
 *    if null and parent is of type App, the App's title 
 *    {@link App#getTitle() App.title} is used.
 *  @param upper  upper display line(s) (or null)
 *  @param yes    the left (Yes) button's label (or null)
 *  @param cancel the middle (Cancel) button's label (or null)
 *  @param lower  lower display line (or null)
 *  @param upperMonospaced true means an upper text is displayed by a 
 *          non proportional font
 *  @param bg   background Color (or null meaning platform standard)
 *  @param passWordHide see {@link #passWordHide}
 *  @return upper may be edited by the user or null
 *  @see #getAnswer()
 *  @see #setUpperMonospaced()
 *  @see #AskDialog(Object, String, boolean, CharSequence, CharSequence, CharSequence, CharSequence, CharSequence)
 *  @see #clearChars(char[])
 */   
   public static char[] getAnswerText(String title, CharSequence upper, 
                           boolean upperMonospaced,
                           CharSequence yes, CharSequence cancel,
                           int waitMax,
                           CharSequence lower,
                           Color bg,
                           boolean passWordHide ){
      String loTe =  TextHelper.trimUq(lower, null);
      if (loTe == null) return null;
      String yeTe = TextHelper.trimUq(yes, null);
      if (yeTe == null) return null;
      AskDialog aD = new AskDialog(null, title,  // parent , title
                              false,  //  modal
                              upper,  //  message
                              yeTe, cancel, null, // buttons
                              loTe); //  question
      aD.maxWait     = waitMax;
      aD.multipleUse = false;
      if (upperMonospaced) aD.setUpperMonospaced(); // not proportional
      if (bg != null) aD.bg = bg;  // background colour 
      aD.passWordHide = passWordHide;
      return aD.getAnswerText(null, null, passWordHide);
   } // static getAnswerText(String, CharSequence, ...
   
/** Clearing a char[]. <br />
 *  <br />
 *  This method clears all elements of the passed char array 
 *  (to (char)0).<br />
 *  <br />
 *  The use case is the clearing of character arrays used for password input 
 *  and storage after use. This is recommended after using  
 {@link #getAnswerText(String, CharSequence, boolean, CharSequence, CharSequence, int, CharSequence, Color, boolean)}
 *  for password input for security.<br />
 *  <br />
 *  Hint: This possibility is the background of the often seen usage of
 *  char[] instead of {@link String} with passwords. {@link String}s leave 
 *  by nature &quot;non erasable&quot; traces in memory.<br />  
 */ 
   static public void clearChars(char[] ca){
      if (ca == null) return;
      for (int i = ca.length -1; i >= 0 ; --i) ca[i] = 0;
   } // clearChars(char[])

   static Font STD_B_FONT = new Font("SansSerif", Font.BOLD, 12);
   
/** Response value. <br />
 *  <br />
 *  No response yet or timed out.<br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #answer
 *  @see #maxWait
 *  @see #getAnswer getAnswer()
 */    
   public static final int NOT_YET = -1;

/** Response value.<br />
 *  <br />
 *  Window was closed (by X or Icon-Menu).<br >
 *  Value: {@value}<br />
 *  <br />
 *  @see #answer
 */    
   public static final int CLOSED = -2;

/** Response value. <br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #answer
 */    
   public static final int CANCEL = 0;

/** Response value. <br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #answer
 */    
   public static final int NO = 1;
 
/** Response value. <br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #answer
 */    
   public static final int YES = 2;
  
/** Response (code) as text. <br />
 *  <br />
 *  Translates the response values defined here to corresponding short English
 *  texts &quot;yes&quot;, &quot;no&quot;, &quot;cancel&quot;,
 *  &quot;time out / no answer&quot; respectively &quot;closed&quot;.<br />
 *  Unknown parameter values return &quot; ?? &quot;.<br />
 *  <br />
 *  @see #answer
 */
   public static String answerAsText(final int answer){
      switch (answer) {
         case NOT_YET: return "time out";
         case CLOSED:  return "closed";
         case CANCEL:  return "cancel";
         case NO:      return "no";
         case YES:     return "yes";
      }
      return " ?? ";
   } // answerAsText(int)
   
/** Minimal width. <br />
 *  <br />
 *  default: 310 pixel
 */   
   protected int minWidth = 310;  
   
/** Minimal height. <br />
 *  <br />
 *  default: 60 pixel
 */   
   protected int minHeigth = 60;   
   
/** Minimal width. <br />
 *  <br />
 *  default: 310 pixel
 */   
   public int getMinWidth() { return minWidth; }  
   
/** Minimal height. <br />
 *  <br />
 *  default: 60 pixel
 */   
   public int getMinHeigth(){ return minHeigth; }   
   
/** Minimal width. <br />
 *  <br />
 *  Range: 100 .. 2800 pixel<br />
 *  default: 310
 */   
   public void setMinWidth(int minWidth){
      this.minWidth = minWidth > 2800 || minWidth < 100 ? 310 : minWidth;
   }  
   
/** Minimal height. <br />
 *  <br />
 *  Range: 55 .. 1090 pixel<br />
 *  default: 60 
 */   
   public void setMinHeigth(int minHeigth){
      this.minHeigth = minHeigth < 55 || minHeigth > 1090 ? 90 :minHeigth;
   }   

//---------------------------------------------------

/** The dialogue window. <br />
 *  <br />
 *  The dialogue window use is a {@link javax.swing.JDialog JDialog}
 *  or a {@link javax.swing.JFrame JFrame}. This is decided finally at 
 *  construction.<br />
 *  <br />
 *  @see #modal
 */
   public final Window theDialog;
   
/** Is it a modal Dialog or JDialog. <br />
 *  <br />
 *  true, if {@link #theDialog} is of type {@link java.awt.Dialog Dialog}
 *  and if modal behaviour was ordered at construction.<br />
 */   
   public final boolean modal; 
   
/** The container. <br />
 *  <br />
 *  If {@link #theDialog} is a {@link javax.swing.JFrame JFrame} or a
 *  {@link javax.swing.JDialog JDialog}, this is its contentPane, otherwise 
 *  {@link #theDialog} itself.<br />
 */
   volatile Container theDialogCont;
   

/** The icon. <br /> */
   Image icon;

/** Yes (Button or JButton). <br /> */
   JButton yesButton;

/** No(Button or JButton). <br />  */
   JButton noButton;

/** Cancel (Button or JButton). <br />  */
   JButton cancelButton;

/** Display 1. <br />  */
   JTextArea  label1;
   
/** Display / input 2. <br />  */
   JPasswordField label2;
  
/** The (if given) lower text field's text. <br />
 *  <br />
 *  Internal helper method. <br />
 */   
   protected char[] getLab2Text(){
      if (label2 == null) return null;
      final Document doc = label2.getDocument();
      if (doc == null) return null;
      return label2.getPassword();
   } // getLab2Text() 

/** The (if given) lower text field's text. <br />
 *  <br />
 *  Internal helper method. <br />
 */   
   protected String getLab2String(){
      if (label2 == null) return null;
      final Document doc = label2.getDocument();
      if (doc == null) return null;
      try {
        return doc.getText(0, doc.getLength());
      } catch (Exception e) { } // ignore; ret null
      return null;
   } // getLab2String()
   
/** The answer. <br />
 *  <br />
 *  <table border="1" cellspacing="0" cellpadding="5" summary="Return values">
 *  <tr><th colspan="2"> Value </th><th> Meaning </th></tr>
 *  <tr><td style="text-align:center;"> -2 </td>
 *  <td style="text-align:center;"> CLOSED </td><td> window was explicitly closed (by
 *                  X oder icon menu). </td></tr>
 *  <tr><td style="text-align:center;"> -1 </td>
 *  <td style="text-align:center;"> NOT_YET </td><td> No response yet or timed 
 *                     out.</td></tr>
 *  <tr><td align=center> 0  </td>
 *  <td align=center> CANCEL </td>
 *           <td> Cancel / middle button</td></tr>
 *  <tr><td align=center> +1 </td>
 *  <td align=center> NO </td>
 *        <td>  No / right button</td></tr>
 *  <tr><td align=center> +2 </td>
 *  <td align=center> YES </td>
 *        <td> Yes / left button</td></tr></table>
 *  <br />
 *  @see #getAnswer()
 *  @see #getAnswer(String, String)
 *  @see #getAnswer(String, CharSequence, boolean, CharSequence, CharSequence, CharSequence, int, CharSequence, Color)  getAnswer()
 */
   protected  volatile int answer = NOT_YET;
   

/** The answer line. <br />
 *  <br />
 *  The lower text line (if given) is edible.<br />
 *  <br />
 *  If there is a lower text and a Yes/OK button, the clicking of this button
 *  transfers the actual lower line's content here. Otherwise 
 *  {@code answerText} will be set null.<br />
 *  <br />
 *  @see #getAnswer()
 */
   protected  volatile char[] answerText;


/** Multiple usage of this object and of the dialogue. <br />
 *  <br />
 *  If this control flag is set true, a given response just makes the 
 *  window invisible without destroying it. The AskDialog object may be
 *  then re-used.<br />
 *  <br />
 *  Should / can be set to false before the (very) last usage.<br />
 *  <br />
 *  default: false
 */
   public boolean multipleUse;

/** The dialogue's background colour. <br />
 *  <br />
 *  If set non null, the value is used as background colour for the window
 *  and both texts.<br />
 *  <br />
 *  default: {@link ColorVal}.{@link ColorVal#dsi dsi} (dark silver)
 */
   public Color bg = ColorVal.dsi;

/** Maximum wait time (in tenth of second) for human answer. <br />
 *  <br />
 *  This control variable determines the maximum waiting time for user's
 *  response for methods like {@link #getAnswer getAnswer()}. A value 
 *  &gt;= 50 is used, the unit being 1/10 second. A value  &lt; 50 means 
 *  endless waiting in the thread having called 
 *  {@link #getAnswer getAnswer()}.<br />
 *  <br />
 *  default: 0 (endless wait)
 */
   public int maxWait;
   
/** Password entry. <br />
 *  <br />
 *  If true input into the lower text filed displays sharps (#) instead of 
 *  echoing the actual input, as soon as the first modification is made.<br />
 *  <br />
 *  default: false<br />
 */   
   public boolean passWordHide;
   
//====================================================================   

/** Internal helper class, wake up thread. <br /> */
   class WakeThread extends Thread {
      
/** Wake up in ms. <br /> */      
      final long wait;
      
/** Call setAnswer() at time out. <br /> */
      boolean doSetAnswer;
      
/** Make and go. <br /> */
     WakeThread (int wTs){
        wait = wTs > 50 ? wTs * 100 : 12000; 
        doSetAnswer = true;
        if (wakeThread != null)
           wakeThread.off();
        wakeThread = this;   
        start(); 
     }  // WakeThread  

/** Do run. <br /> */
     @Override public void run(){
         try {
            sleep(wait);          
         } catch (InterruptedException e) {
            doSetAnswer = false;
         }
         synchronized (AskDialog.this) {
            boolean dsA = doSetAnswer;
            doSetAnswer = false;
            if (wakeThread == this) wakeThread = null;
            if (dsA) setAnswer(NOT_YET);
         }
      } // run

/** Abort. <br /> */
      void off(){
         synchronized (AskDialog.this) {
           doSetAnswer = false;
           if (wakeThread == this) wakeThread = null;
           try {
              interrupt();
           } catch (Exception e) {} 
         }                 
      } // off()
      
   } // WakeThread   ======================================================   
   
/** The wake up thread. <br /> */   
   volatile WakeThread wakeThread;
   
/** Use a  wake up thread. <br />
 *  <br />
 *  default: true
 */   
   volatile boolean useWT = true;
   
//-------------------------------------------------------------------------

/** Constructor with parent window / object. <br />
 *  <br />
 *  An AskDialog object is made according to the parameters. Single graphical
 *  elements may be omitted by having the corresponding parameter null.<br />
 *  <br />
 *  The dialogue made is fully prepared but keeps invisible. The querying
 *  of a user {@link #answer} or response may then be made by calling the
 *  method {@link #getAnswer getAnswer()} (as one possibility).<br />
 *  <br />
 *  Parameter parent may be null. In this case a {@link javax.swing.JFrame} 
 *  will be generated as dialogue window.<br /> 
 *  <br />
 *  A parameter parent non null may be of type {@link java.awt.Frame}, 
 *  {@link java.awt.Dialog} or  {@link  App}. If parent is or has a window, a
 *  modal dialogue is made and placed in the parent window's upper left 
 *  corner. Without a determinable parent window a {@link JFrame} will be 
 *  made.<br />
 *  <br />
 *  @param parent the parent (window); of type Frame, Dialog, App, String 
 *                or null
 *  @param title  the title for this dialogue. if null and parent of type App
 *                {@link App#getTitle() App.title} is used.
 *  @param modal  true blocks the parent window (if given) until the closing
 *                of this dialogue
 *  @param upper  upper display line(s) (or null)
 *  @param yes    the left (Yes) button's label (or null)
 *  @param cancel the middle (Cancel) button's label (or null)
 *  @param no     the right (No) button's label (or null)
 *  @param lower  lower display line (or null)
 */
   public AskDialog(Object parent, String title, boolean modal, 
                     CharSequence upper, 
                     CharSequence yes, CharSequence cancel, CharSequence no,
                     CharSequence lower){
      if (parent == null) {
         theDialog =  new JFrame(title);
         icon = WeAutLogo.getIcon();
         ((JFrame)theDialog).setIconImage(icon); 
         modal = false;               
      } else {
         if (parent instanceof App) { // App
            if (title == null) title = ((App)parent).getTitle(); 
            parent = ((App)parent).getMyFrame();
         }  // App (no else if as could have set parent)
         
         if (parent instanceof Frame) {
            icon = ((Frame)parent).getIconImage();
            theDialog = new JDialog((Frame)parent, title, modal);                
         } else if (parent instanceof Dialog) {   
            theDialog = new JDialog((Dialog)parent, title, modal);                
         } else if (parent == null) {
            modal = false;
            theDialog = new JFrame(title);
            if (icon ==  null) icon = WeAutLogo.getIcon();
            ((Frame)theDialog).setIconImage(icon);                
         } else {
            throw new ClassCastException("parent has illegal type");
         }
      } // parent  != null

      theDialog.setVisible(false);
      if (theDialog instanceof JFrame) {
         theDialogCont = ((JFrame)theDialog).getContentPane();
      } else {
         theDialogCont = ((JDialog)theDialog).getContentPane();
      }
      this.modal = modal;
      theDialogCont.setLayout(new BorderLayout(6, 6));

      String buText = TextHelper.trimUq(upper, null);
      if (buText != null) {
         label1 = new JTextArea(buText, 0, 0); /// JButton(buText);
        /// label1.setHorizontalAlignment(SwingConstants.LEFT);
         label1.setBorder(new EmptyBorder(12, 12, 5, 12));
         label1.setFocusable(false);
         label1.setFont(STD_B_FONT);
         theDialogCont.add(label1, "North");
      }

      buText = TextHelper.trimUq(lower, null);
      if (buText != null) {
         label2 = new JPasswordField(null, null, 0);
         label2.setEchoChar((char)0);
         label2.setText(buText);

         label2.getDocument().addDocumentListener(this);
         label2.setHorizontalAlignment(SwingConstants.LEFT);
         label2.setBorder(new EmptyBorder(5, 12, 12, 12));
         label2.setEditable(true);
         label2.setFocusable(true);
         label2.setFont(STD_B_FONT);
         theDialogCont.add(label2, "South");
      } 
                       //  ca        ye      no
                       //  ye        no
      boolean centerUse = true;
      
      buText = TextHelper.trimUq(cancel, null);
      if (buText != null) {
         centerUse = false;
         JButton cB = new JButton(buText);
         theDialogCont.add(cB, "Center");
         cB.addActionListener(this);
         cB.addKeyListener(this);
         cancelButton = cB;
      }
      
      buText = TextHelper.trimUq(no, null);
      if (buText  != null) {
         String pos = centerUse ? "Center" : "East";
         centerUse = false;
         JButton nB = new JButton(buText);
         theDialogCont.add(nB, pos);
         nB.addActionListener(this);
         nB.addKeyListener(this);
         noButton = nB;
     }
      
      buText = TextHelper.trimUq(yes, null);
      if (buText != null) {
         String pos = centerUse ? "Center" : "West";
         JButton yB = new JButton(buText);
         theDialogCont.add(yB, pos);
         yB.addActionListener(this);
         yB.addKeyListener(this);
         yesButton = yB;
      }
      
      theDialog.addWindowListener(new WindowAdapter() {
         @Override public void windowClosing(WindowEvent event)   {
            setAnswer(CLOSED);
         }
      });

      int x = 140;
      int y = 110;
      if (parent instanceof Window
         && ((Window)parent).isVisible()) {
         Point p = ((Window)parent).getLocationOnScreen();
         x = p.x + 5;
         y = p.y + 17;
      }
      theDialog.setLocation(x, y);
      
      /// --- added to avoid (later) a NullpointerExc   (30.09.2004)
      if (theDialog instanceof Dialog) {
         ((Dialog)theDialog).setResizable(true);
         theDialog.pack();
         ((Dialog)theDialog).setResizable(false);
      } else if (theDialog instanceof Frame) {
         ((Frame)theDialog).setResizable(true); 
         theDialog.pack();
         ((Frame)theDialog).setResizable(false);
      }
      ///  --- added to avoid (later) a NullpointerExc, if in critical thread
      
   } // AskDialog()


/** Parent-less Constructor. <br />
 *  <br />
 *  An AskDialog object is made according to the parameters. Single graphical
 *  elements may be omitted by having the corresponding parameter null.<br />
 *  <br />
 *  This is equivalent to <br /><code>
 *  {@link #AskDialog(Object, String, boolean, CharSequence, CharSequence, CharSequence, CharSequence, CharSequence)
 *    AskDialog}(null, title,  false, upper, 
 *       yes, cancel, no, lower)</code>
 *  <br />
 *  @param title  the title for this dialogue.
 *  @param upper  upper display line(s) (or null)
 *  @param yes    the left (Yes) button's label (or null)
 *  @param cancel the middle (Cancel) button's label (or null)
 *  @param no     the right (No) button's label (or null)
 *  @param lower  lower display line (or null)
 */
   public AskDialog(String title, CharSequence upper, 
                 CharSequence yes, CharSequence cancel, CharSequence no,
                 CharSequence lower){
      this(null, title, false, upper, yes, cancel, no, lower);
   } // AskDialog


/** Set the font for the upper text as non proportional. <br />
 *  <br />
 *  Setting the font of upper text mono-spaced is usually better to display
 *  file or directory names.<br />
 *  <br />
 *  default: false (hence proportional)<br />
 */
    public void setUpperMonospaced() {
      if (isMono ||  label1 == null) return;
      Font bish = label1.getFont();
      int size  = bish == null ? 12 : bish.getSize() + 2;
      label1.setFont(new Font("Monospaced", Font.PLAIN, size));
      isMono = true;
   } // setUpperMonospaced()
   
   boolean isMono;

//========================================================================

/** <b>Listener for AskDialog</b>. <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright Albrecht Weinert &nbsp; 2001  
 *  @author   Albrecht Weinert
 *  @see AskDialog#setVisible(String, String)
 */
 // so far V00.00 (07.12.2001 19:46) : Neu seit AskDialog V01.00
   
@MinDoc(
   copyright = "Copyright 1998 - 2002, 2004, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "as enclosing class AskDialog",
   lastModified   = "as enclosing class AskDialog",
   lastModifiedBy = "as enclosing class AskDialog",
   usage   = "register and wait",  
   purpose = "forward user answer and timeout event"
) public interface Listener {
   
/** Event: a new answer is there. <br />
 *  <br />
 *  This method of the {@link AskDialog.Listener} registered will be called
 *  (by {@link AskDialog}) in a special helper thread and not in the 
 *  (graphical) event thread forwarding the user's response.<br />
 *  <br />
 *  If {@link #multipleUse} is false (only if) immediately after this method 
 *  having been called the text response might be fetched by 
 *  {@link AskDialog#getAnswerText()} or
 *  {@link AskDialog#getAnswerText(String, String, boolean)}.<br />
 *  <br />
 *  @param answer the actual response; see table at {@link #answer}
 *  @see #answer
 *  @see #addListener addListener()
 */
      public void askAnswered(int answer);

   } // interface Listener   ================================================   

/** The maximal one Listener. <br /> */
   protected Listener listener;

/** Register an answer Listener. <br />
 *  <br />
 *  To wait without blocking for response or timeout (just) one 
 *  {@link Listener} object might be registered. To de-register this method is
 *  to be called with null.<br />
 *  The Listener's method {@link Listener#askAnswered(int) askAnswered()} 
 *  is called in an extra helper thread. So there are no restrictions
 *  whatsoever on what an how much might be done in this method.<br />
 *  <br />
 */
   public void addListener(Listener listener) {
      if (theDialogCont == null) return; // dead already
      this.listener = listener;
   } // addListener(Listener    

/** Internal event method: answer was determined. <br /> */
   synchronized  void setAnswer(int answer) {
      ///// System.out.println("TEST setAnswer( " + answer + " )  ");
      this.answer = answer;
      this.answerText = answer == YES ? getLab2Text(): null;
      given = true;
      if (wakeThread != null) wakeThread.off(); // no more wake up 
      useWT = true;
      notify(); // undoes wait-lock after returning from this method
      if (theDialogCont != null) {
         theDialog.setVisible(false); // undoes modal lock
         if (!multipleUse) {      
            theDialog.dispose();
            theDialogCont = null;
         }
      }
      if (listener != null) {
         final Listener list = listener;
         final int anwNow = answer;
         if (!multipleUse) listener = null;
         new Thread() { 
            @Override public void run() {
            list.askAnswered(anwNow);
         }}.start();  // as thread
      }      
   } // setAnswer

/** Answer was given: internal flag. <br /> */
   volatile boolean given;
 

/** Wait for answer. <br />
 *  <br />
 *  This method makes the window visible and waits for for its 
 *  &quot;end&quot; by means of the underlying graphical system (like 
 *  Window's X or icon menu) or by one of the (max.) three buttons.<br />
 *  <br />
 *  A button may be pressed by a pointing device (usually a mouse) or selected
 *  by keyboard buttons [Tab] [Back-Tab] and then be presses by keyboard's 
 *  return [Enter] key, if the window has the focus.<br />
 *  <br />
 *  If the control variable {@link #maxWait} is set accordingly, the waiting
 *  for one of these events will be limited. If this time out happens this 
 *  method returns  {@link #NOT_YET} as event code. Without time-out this 
 *  method will wait &quot;forever&quot; (blocking the caller's thread).<br />
 *  <br />
 *  @return The code for the ending event(see {@link #answer})
 *  @see #getAnswer(String, String)
 *  @see #getAnswerText()
 *  @see #getAnswerText(String, String, boolean)
 */
   public int getAnswer() {
       return getAnswer(null, null);
   } // getAnswer()


/** Wait for answer after setting new display texts. <br />
 *  <br />
 *  Apart from the newly setting of the texts, this method is equivalent to
 *  {@link #getAnswer()}.<br />
 *  <br />
 *  The setting of the texts happens only, if the window is not yet 
 *  visible.<br />
 *  <br />
 *  @param upper If the AskDialog was made with an upper Text and this
 *               parameter is not null, the upper text is set newly.
 *  @param lower the same for the lower text
 *  @see #getAnswerText()
 *  @see #getAnswer()
 */
   public int getAnswer(String upper, String lower){
      synchronized (this){
         if (given      // due to modal 27.05.2002
                                 && !multipleUse) { // w/o multiple use return old answer again
            return answer;  
         }  // if (given && !multipleUse
         int limit = maxWait;
         useWT = modal && limit >= 50;
         prepVisible(upper, lower);  // modal does not block in prepVi
         if (!modal) {
            theDialog.setFocusableWindowState(false);
            theDialog.setVisible(true);
            try { Thread.sleep(200); } catch (Exception e1) { /*intend*/ }
            if (!given) {
               theDialog.setFocusableWindowState(true);
               try {
                  if (limit < 50 )
                     wait();
                  else
                     wait(limit * 100);
               } catch (Exception e){} 
            }
            if (!given) setAnswer(NOT_YET);
            return answer;
         } // !modal
      } // sync
      // to here only with modal
      theDialog.setVisible(true); // modal blocks here
      return answer;   //  due to  27.05.2002
   } // getAnswer(String, String) 
   
/** Wait for answer after setting new display texts. <br />
 *  <br />
 *  This method is equivalent to
 *  {@link #getAnswer(String, String) getAnswer(upper, lower)} except for
 *  returning the actual lower text, perhaps been edited by the user, if the
 *  Window was closed by clicking the left Yes/OK button. If closed otherwise
 *  or timed out null will be returned.<br />
 *  <br />
 *  If this AskDialog was generated for one time usage and already closed
 *  this method returns the same value if called again.<br />
 *  <br />
 *  @param upper If the AskDialog was made with an upper Text and this
 *               parameter is not null, the upper text is set newly.
 *  @param lower the same for the lower text
 *  @see #getAnswer()
 *  @see #getAnswer(String, String)
 *  @see #getAnswerText()
 */
   public char[] getAnswerText(String upper, String lower,
                           boolean passWordHide) {
      this.passWordHide = passWordHide;
      getAnswer(upper, lower);
      return answerText;
   } // getAnswerText(String, String) 

/** Wait for answer without setting new display texts.<br />
 *  <br />
 *  This is equivalent to {@link #getAnswerText(String, String, boolean)
 *  getAnswerText(null, null, false)} with transforming the returned value
 *  to a String.<br />
 *  <br />
 *  @see #getAnswer()
 *  @see #getAnswer(String, String)
 */
   public final String getAnswerText() {
      char[] ret = getAnswerText(null, null, false);
      if (ret == null) return null;
      return new String(ret);
   } // getAnswerText(String, String) 

   
/** Prepare for making visible. <br />
 *  <br />
 *  This method itself makes nothing visible, its just a preparation 
 *  (especially if used multiply) with the possibility of changing the
 *  texts.<br />
 *  If the window is already visible nothing is done.<br />
 *  <br />
 *  @param upper If the AskDialog was made with an upper Text and this
 *               parameter is not null, the upper text is set newly.
 *  @param lower the same for the lower text
 */
   private synchronized final void prepVisible(String upper, String lower) {
      ///// System.out.println("TEST prepVisible(); modal = " + modal);
      if (theDialogCont != null && !theDialog.isVisible()) {
         if (bg != null) {
            theDialogCont.setBackground(bg);
            Color fg = ColorHelper.calcFgColor(bg);
            if (label1 != null) {
               label1.setBackground(bg);
               label1.setForeground(fg);
            }
            if (label2 != null) {
               label2.setBackground(bg);
               label2.setForeground(fg);
            }
         }

         if (label1 != null && upper != null ) {
           if  (! upper.equals(label1.getText())) 
                  label1.setText(upper);   
         }
         if (label2 != null) {
            if (lower != null && !lower.equals(getLab2String())) {
               label2.setEchoChar((char)0);
               label2.setText(lower);  
            }  
           ///  label2.setEchoChar(passWordHide ? '*' : 0);
         }
         Rectangle db = null;
         if (bg != null) theDialogCont.setBackground(bg);
         if (theDialog instanceof Dialog) {
            ((Dialog)theDialog).setResizable(true);
            theDialog.pack();
            db = theDialog.getBounds(db);
            if (db.width < minWidth) db.width = minWidth;
            if (db.height < minHeigth) db.height = minHeigth;
            theDialog.setBounds(db);

            ((Dialog)theDialog).setResizable(false);
         } else if (theDialog instanceof Frame) {
            ((Frame)theDialog).setResizable(true); 
            theDialog.pack();
            
            db = theDialog.getBounds(db);
            if (db.width < minWidth) db.width = minWidth;
            if (db.height < minHeigth) db.height = minHeigth;
            theDialog.setBounds(db);
            
            ((Frame)theDialog).setResizable(false); 
         }

         given = false;
         answer = NOT_YET; 
         answerText = null;
         int limit = maxWait;
         useWT = useWT && limit >= 50;
         if (useWT) {
            new WakeThread(limit);
         }
      } 
   } // prepVisible
   
  // Rectangle db;

/** Make visible without waiting for answer. <br />
 *  <br />
 *  This method makes the window visible.<br />
 *  If the window is already visible nothing is done.<br />
 *  <br />
 *  The {@link #answer} may be checked cyclicly, {@link #getAnswer()} may
 *  be called (later) or a {@link Listener} added by 
 *  {@link #addListener(Listener) addListener()} may be informed.<br />
 *  <br />
 *  <br />
 *  @param upper If the AskDialog was made with an upper Text and this
 *               parameter is not null, the upper text is set newly.
 *  @param lower the same for the lower text
 */
   public void setVisible(String upper, String lower) {
      prepVisible(upper, lower);
      ///// System.out.println("TEST setVisible(); useWT = " + useWT);
      theDialog.setVisible(true); // modal blocks here (!)
      //// System.out.println("TEST setVisible(); AFTER ");
      try { /// null-Pointer for (shit) modal
         theDialog.toFront();
      } catch (Exception e){} // 27.05.2002
   } // setVisible(String


/** Give up this AskDialog object and free all resources. <br />
 *  <br />
 *  This object and its windows may be used multiple times (saving resources
 *  for questions or reports predictably repeated), if {@link #multipleUse} is
 *  set true prior to first use. In this case (only) this object holds 
 *  graphical resources and threads. So its recommended (and sometimes
 *  necessary) to free those after last use.<br />
 *  <br />
 */
      public void dispose() {
         if (theDialogCont == null) return ;
         theDialog.setVisible(false);
         theDialog.dispose();
         theDialogCont = null;
         answer = NOT_YET;
         answerText = null;
         listener  = null;
      } // dispose() 

//-------------------------------------------------------------------


/** Execution of button commands. <br />
 *  <br />
 *  Internal event handling. <br />
 */
   @Override public void actionPerformed(ActionEvent event)  {
      if (event == null) return;
      final Object s = event.getSource();
      setAnswer(s == yesButton ?  YES : s == noButton  ? NO : CANCEL);
   } // actionPerformed(ActionEvent)

/** Execution of button commands. <br />
 *  <br />
 *  Internal event handling. <br />
 */
   @Override public void keyTyped(KeyEvent e) {}

/** Execution of button commands. <br />
 *  <br />
 *  Internal event handling. <br />
 */
   @Override public void keyReleased(KeyEvent e) {}

/** Execution of button commands. <br />
 *  <br />
 *  Internal event handling. <br />
 *  Reaction to return key for selected button.<br />
 */
   @Override public void keyPressed(KeyEvent event) {
      if (event == null || event.getKeyChar() != '\n') return;
      final Object s = event.getSource();
      setAnswer(s == yesButton ?  YES : s == noButton  ? NO : CANCEL);
   } // keyPressed(KeyEvent

/** Changes in entry fields. <br /> */   
   @Override public final void changedUpdate(DocumentEvent e) {
     insertUpdate(e);
   }
   
/** Changes in entry fields. <br /> 
 *  <br />
 *  The event is only used to hide entries to a field if used 
 *  for passwords to be hidden. <br />
 */  
   @Override public final void insertUpdate(DocumentEvent e) {
      if (!passWordHide)  return;
      if (label2.echoCharIsSet()) return;
      label2.setEchoChar('#');
   } // insertUpdate(DocumentEvent)
   
/** Changes in entry fields. <br /> */  
   @Override public final void removeUpdate(DocumentEvent e) {
      insertUpdate(e);
   }

} // AskDialog 23.07.2003 04.02.2004 09.09.2004, 30.09.2004, 09.04.2008