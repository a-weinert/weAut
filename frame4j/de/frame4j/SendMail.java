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
package de.frame4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import de.frame4j.io.Input;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;
import de.frame4j.time.SynClock;
import de.frame4j.time.TimeHelper;

/** <b>A tiny E-Mail sender</b>. <br />
 *  <br />
 *  This application is a tiny command line controlled SMTP-Client using the
 *  JavaMail API ({@code javax.mail}).<br />
 *  <br />
 *  On a successful execution this application send a plain text e-mail
 *  ({@link javax.mail.BodyPart}) and / or one or more file attachments
 *  ({@link javax.mail.Multipart}) to one or more addressees.<br />
 *  <br />
 *  By start arguments, by an extra .properties file or by an RFC822 e-mail
 *  definition file among others the following mail properties may be 
 *  specified:<ul>
 *  <li>To  &nbsp; (addressee(s))</li>
 *  <li>CC and BCC  &nbsp; (further addressee(s)</li>
 *  <li>From (sender)</li>
 *  <li>Subject </li>
 *  <li>Text (content)</li></ul>
 *
 *  The content of a text file (or URL) or the text property set by command 
 *  line options may be taken as <ul>
 *  <li> just the (plain text) content or </li>
 *  <li> the full RFC822 parseable mail definition.</li></ul>
 *  
 *  A RFC822 mail definition file may define the content and all header fields
 *  a in the following example:<pre>
 *  From    : the cheap provider &lt;dev.executive@sioux.tv&gt;
 *  To      : the customer &lt;dear.customer@customers-ltd.tv&gt;
 *  Reply-To: call centre &lt;pontius.pilatus@sioux.tv&gt;
 *  Subject : You are disconnected after your Western Union payment
 *  
 *  This mail was only send to you to keep you calm. You should get the
 *  impression your error report is taken seriously and the repair is under
 *  way. But this is just an automated response by a clever computer
 *  programme.
 *  
 *  Thank you for your endless patience.
 *  </pre>
 *  On such  RFC822 mail definitions the very first empty line is the
 *  separation of the header definitions from the (optional) mail text.<br />
 *  <br />
 *  <b>Hint 1</b>: The basic .properties file named <a 
 *  href="./doc-files/SendMail.properties"
 *  target="_top">SendMail.properties</a> is an integral part of this 
 *  application and may be considered as part of the documentation.<br />
 *  <br />
 *  <b>Hint 2</b>: This application also has a convenience starter
 *  {@link SendMail} in the anonymous package.<br />
 *  <br />
 *  <b>Hint 3</b>: In a RFC-822 definition one can give lists of addressees
 *  for To, CC and BCC. The list separator has to be (best) 1 comma (,) +
 *   + 1 line + 1 space.<br />
 *  <br />
 *  <b>Hint 4</b>: The separating line between header definitions and text
 *  body has to be absolutely empty (otherwise &quot;miracles&quot; occur).
 *  This missing robustness bug is RFC 822 taken to SUN's Java mail API.
 *  (to do in a later version: parse here robustly.)<br />
 *  <br />
 *  <b>Hint 5</b>: On your server the default settings can (should) be
 *  overridden by placing a file SendMail.properties in jdk\jre\lib defining
 *  (at least) the following properties according to your environment:<pre>
 #  Property-File for SendMail.java  (jdk\jre\lib\SendMail.properties)
 #  V1.0  (So, 04.04.2010, 21:57 MEZ)
 #  for location / server: @ home 
 #  A. Weinert changes for 1&amp;1
 #
 mail.from= no.reply@frame4j.de
 mail.smtp.host = smtp.1und1.de
 userName= ano.reply@frame4j.de
 # use the next line if you like it here more than in the command line
 # userPass=monRepos
 proxySet = false
 mail.smtp.auth = true
 # if behind a proxy say:
 # proxySet   = true
 # proxyHost  = myCompany'sProxy
 # proxyPort  = 8080</pre>
 *  <br />
 *  <a href=./package-summary.html#co>&copy;</a> 
 *  Copyright 2002 - 2003, 2006 &nbsp;  Albrecht Weinert <br />
 *  <br />
 *  @see javax.mail.internet.InternetAddress
 *  @see javax.mail.internet.MimeMessage
 */
 // so far    V00.00 (11.03.2002 15:37) : new
 //           V00.01 (18.03.2002 13:26) : 1-i error correction
 //           V00.02 (30.03.2002 16:57) : RFC822 parsing is now default
 //           V00.03 (30.03.2002 16:57) : de, AppBase, Plug-In
 //           V01.31 (13.09.2002 12:35) : /**
 //           V01.32 (21.10.2002 13:45) : 822-parse also parameter text, timeF
 //           V01.33 (23.10.2002 09:02) : timeFormat, Stamp for reuse
 //           V02.00 (23.04.2003 17:00) :  CVS Eclipse
 //           V02.31 (18.09.2006 14:55) :  /**
 //           V.o56+ (03.02.2009 11:16) :  ported to Frame4J (and Kenai SVN)
 //           V.o01+ (03.02.2010 13:10) :  moved to Assembla due to Oracle-Sun
 //           V.o34+ (04.04.2010 22:20) :  Authentication and multiple recip.

@MinDoc(
   copyright = "Copyright 2002 - 2004 , 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "start as Java application (-? for help)",  
   purpose = "a tiny E-mail sender (command line controlled)"
) public class SendMail extends App {

   private SendMail(){} // no objects; no javaDoc 
   
/** Start method of SendMail. <br />
 *  <br />
 *  Execution is: Java SendMail [options] [contFile] | [-help]<br />
 *  <br >
 *  @param  args   command line parameter
 *  @see #doIt
 *  @see App#go(String[])
 */
   public static void main(final String[] args){
      try {
         new SendMail().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main(String[])

//----  Elements set by Prop's command line parsing / evaluation  ---

/** Log length of content. <br />
 *  <br />
 *  If  {@link #verbose} is true the mail content will be logged, but
 *  truncated in the log output after a maximum of  {@link #contLogLength}
 *  characters. The truncation does not happen if  {@link #contLogLength} is
 *  &lt;= 42. If the mail {@link #text} is truncated in the log, that will
 *  be signalled by dots ( ....).<br />
 *  <br />
 *  default: see SendMail.properties.
 */
   public int contLogLength  =  133;


/** The recipient(s) (may be a list). <br />
 *  <br />
 *  This denotes the mail's recipient. It may be a comma-separated list of
 *  more than one recipient. If used together with a RFC822 parsed file this
 *  properties specifies additional recipient(s). Additional means that in 
 *  case of {@link #parse}ed RFC 822 definition file {@link #contFile} that
 *  will be added to the addressees defined in the parsed file.<br />
 *  <br />
 *  default: see SendMail.properties.
 */
   public String  to;

/** The copy recipient(s) (may be a list). <br />
 *  <br />
 *  This denotes the mail's copy recipient. The same conditions as for the 
 *  {@link #to} property apply.<br />
 *  <br />
 *  default: null
 */
   public String  cc;

/** The secret copy recipient(s) (may be a list). <br />
 *  <br />
 *  This denotes the mail's secret or blind copy recipient. The same 
 *  conditions as for the {@link #to} property apply.<br />
 *  <br />
 *  default: null
 */
   public String  bcc;

/** The subject. <br />
 *  <br />
 *  For the mail's subject (header) field.<br />
 *  <br />
 *  default: seeSendMail.properties.
 */
   public String subject;

/** Set actual time stamp. <br />
 *  <br />
 *  If true the mails's date (header) field will be set by the actual 
 *  time.<br />
 *  <br />
 *  default: false <br />
 *  @see de.frame4j.time.TimeHelper#formatRFC(java.time.ZonedDateTime)
 *  @see de.frame4j.time.TimeHelper#formatGMT(java.time.ZonedDateTime)
 */
   public boolean timeStamp;

/** Format for time stamp. <br />
 *  <br />
 *  If {@link #timeStamp} is true, this format will be used for the mails's
 *  date (header) field.<br />
 *  <br />
 *  default: &quot;r&quot; (meaning ugly rfc822 standard)<br />
 */
   public String timeFormat = "r";

/** The content. <br />
 *  <br />
 *  The (normal) text content of the mail (if any).<br />
 *  Will be set by start argument, .properties file or by a file
 *  ({@link #contFile}) read.<br />
 *  <br />
 */
   public String text;

/** The content file. <br />
 *  <br />
 *  If {@link #contFile} is not empty, it is tried to read the mail's text
 *  content directly from there respectively to {@link #parse} the whole
 *  RFC822 mail definition from it.<br />
 *  {@link #contFile} may denote a file or an URL.<br />
 *  <br />
 *  default: null
 */
   public String contFile;

/** The file is not only the content. <br />
 *  <br />
 *  If {@link #parse} is true the file or URL denoted by {@link #contFile}
 *  will not be taken for content only, but parsed as whole RFC 822 mail
 *  definition.<br />
 *  <br />
 *  default: false (set by options)
 */
   public boolean parse;

/** File encoding for content file. <br />
 *  <br />
 *  If {@link #contFile} is not empty, it is tried to read the mail's text
 *  content directly from there respectively to {@link #parse} the whole
 *  RFC822 mail definition from it.<br />
 *  In that case {@link #contFileEncoding} will be used for that file or 
 *  URL.<br />
 *  <br />
 *  default: null, meaning the platform's encoding (mostly 8859-1 on European
 *  Windows)
 */
   public String contFileEncoding;


/** First attachment file. <br />
 *  <br />
 *  If {@link #attach} is nor empty it is tried to add the file denoted by
 *  it as a true attachment to the mail.<br />
 *  <br />
 *  In case of success a property attach2, attach3 and so on is looked for
 *  and treated in the same way.<br />
 *  <br />
 *  default: null
 */
   public String attach;

   public String userName, userPass;

/** Use a proxy server connecting to mail host. <br />
 *  <br />
 *  If true a proxy will be used to connect to the mail server and the
 *  properties {@link #proxyHost} and {@link #proxyHost} are in effect.<br />
 *  <br />
 *  default: false<br />
 */
   public boolean proxySet;

/** The proxy server's name. <br />
 *  <br />
 *  default: &quot;cache&quot;<br />
 *  <br />
 *  @see #proxySet
 */
   public String  proxyHost;

/** The proxy server's port. <br />
 *  <br />
 *  default: 8080<br />
 *  <br />
 *  @see #proxySet
 */
   public String  proxyPort;


//---------------------------------------------------------------------------

/** Working method of SendMail. <br />
 *  <br />
 *  @see #main main(Strin[])
 *  @see App#go(String[])
 */
   @Override public  int doIt(){
      log.println();
      if (verbose) log.println(twoLineStartMsg().append('\n'));

      if (proxySet) {
         System.setProperty("proxySet",  "true");
         System.setProperty("proxyHost", proxyHost);
         System.setProperty("proxyPort", proxyPort);
      }

      to = TextHelper.trimUq(to, null);
      if (to == null) { // parse RFC or addressee
         if (!parse ) return errMeld(11, "no to-field");
         to = null;
      } // to - check
      cc = TextHelper.trimUq(cc, null);
      bcc = TextHelper.trimUq(bcc, null);
      subject = TextHelper.trimUq(subject, null);
      contFile = TextHelper.trimUq(contFile, null);
 
      //-- Read input file or URL

      Input   inp      = null; // if given file or URL input
      String  prepProc = null; // input's handling hint (text or parse)
      String  stamp    = null; // if given own time stamp

      if (contFile != null) {
        if (parse) {
           prepProc = "parse(822) ";
        } else {
           prepProc = "text input ";
           text = null; // will be read or error
        }
        try {
         inp = Input.openFileOrURL(contFile);
         if (verbose) {
            log.println("\n  ///   " + prepProc  + ": \n  "
                                                           + inp.listLine()); 
            if ((inp.url != null) && proxySet)
            log.println(  "  proxy host = " + proxyHost 
                                            + ", proxy port = " + proxyPort);
         }
         if ( !parse) 
            text = inp.getAsString(contFileEncoding);
        } catch (IOException e) {
         if (proxySet)
            return errMeld(21, "proxy host = " + proxyHost 
                     + ", proxy port = " + proxyPort+ "\n" + e.getMessage()); 
         return errMeld(23, e.getMessage());
         } // try-catch open content file
      } // content file given
      
      int textLen = text == null ? 0 : text.length();
      if (textLen == 0 && subject == null && !parse) {
         return errMeld(27, "no text or subject");
      }

// ----- parameter evaluation and all initialisation complete ----

// -----  prepare message   --------------------------------------
     
      Properties mailProps = prop.asProperties("mail.");
      //// log.println( "\n  ///   TEST mailProps : " + mailProps.toString());
      final String smtpHost = prop.getProperty("mail.smtp.host", "SMTP"); 
      Session session = Session.getDefaultInstance(mailProps);
 
      MimeMessage message = null;
      if (!parse) 
         message = new MimeMessage(session);
      else try {
         if (inp == null) {
            if (textLen == 0) {
               log.println("\n   *** No text or input   *** ");
               return 18;
            }
            message = new MimeMessage(session, 
                new ByteArrayInputStream(text.getBytes())); 
            } else {
               message = new MimeMessage(session, inp); 
               inp.close();
            }
            textLen = 0; // Remark: if parsed text is obsolete afterwards
      } catch (Exception e) {
         log.println("\n   *** Error while generating the mail:\n   *** "
             + e.getMessage());
         return 18;
      }

      final String from = prop.getString("mail.from");
      
      try {
         InternetAddress[] lesTos;
         if (to != null) { // The one or the extra addressee
            //  InternetAddress toInA = new InternetAddress(to);
            lesTos = InternetAddress.parse(to, false);
            message.addRecipients(Message.RecipientType.TO, lesTos);
         } //  The one or the extra addressee

         if (cc != null) { //  The one or the extra copy addressee
            // InternetAddress toInA = new InternetAddress(cc);
            lesTos = InternetAddress.parse(cc, false);
            message.addRecipients(Message.RecipientType.CC, lesTos);
         } //  The one or the extra copy addressee

         if (bcc != null) { //  The one or the extra secret copy addressee
            //  InternetAddress toInA = new InternetAddress(bcc);
            lesTos = InternetAddress.parse(bcc, false);
            message.addRecipients(Message.RecipientType.BCC, lesTos);
         } //  The one or the extra secret copy addressee
         
         if (from != null) { //  The sender address(es)
            message.setFrom(new InternetAddress(from));
         } //  The one or the extra secret copy addressee


         if (timeStamp) { // time stamp (Date header field) self made
            //  message.removeHeader("Date");  // date from parse remove 
            stamp = TimeHelper.format(timeFormat, SynClock.sys.getActTime());
                      //              AppBase.setActTime().toString(timeFormat);
            message.addHeader("Date", stamp);  // will replace = remove
         } // time stamp (Date header field) self made

         if (subject != null)
            message.setSubject(subject);
         
       //  -----  Multipart
         if (attach != null && (attach=attach.trim()).length() != 0) {
            Multipart mp = new MimeMultipart();
            MimeBodyPart mbp = new MimeBodyPart();
            if (textLen == 0) { // text may have been parsed into
               Object oCo = message.getContent();
               if (oCo instanceof String) {
                   text = (String) oCo;
                   textLen = text.length();
               }
            }  // text may have been parsed into
 
           if (textLen > 0) {
               mbp.setText(text);
               mp.addBodyPart(mbp); // add the text (first)
            }
            int atNum = 1;
            String saveAttach = attach;
            while (attach != null && (attach=attach.trim()).length() != 0) {
               mbp = new MimeBodyPart();
               DataSource ds = new FileDataSource(attach);
               mbp.setDataHandler(new DataHandler(ds));
               mbp.setFileName(attach);
               mp.addBodyPart(mbp); // add the file(s)
               ++atNum;
               attach = prop.getString("attach" + atNum);
            } // while over attaches
            attach = saveAttach;

            message.setContent(mp);
         } else if (textLen > 0) {
            message.setContent(text, "text/plain" );

            // message.setText(text); // set if text && no attach
         }
      } catch (Exception e) {
         log.println("\n   *** Error while preparing the mail:\n   *** "
             + e.getMessage());
         return 19;
      }
     
      
// -----  log message  --------------------------------------

       log.println(  "  /// From: " + from);
      if (verbose && stamp != null)
         log.println("\n  /// Stamped Date : " + stamp);
      try {
         Address[] rec =  message.getRecipients(Message.RecipientType.TO);
         if (rec == null || rec.length == 0) 
            log.println(  "  /// To  : no recipient");
         else {
            log.println(  "  /// To  : " + rec[0]);
            for (int i = 1; i <  rec.length; ++i)
              log.println("           " + rec[i]);
         }
         rec =  message.getRecipients(Message.RecipientType.CC);
         if (rec != null && rec.length != 0) {
            log.println(  "  /// Cc  : " + rec[0]);
            for (int i = 1; i <  rec.length; ++i)
              log.println("           " + rec[i]);
         }
         rec =  message.getRecipients(Message.RecipientType.BCC);
         if (rec != null && rec.length != 0) {
            log.println(  "  /// Bcc : " + rec[0]);
            for (int i = 1; i <  rec.length; ++i)
              log.println("           " + rec[i]);
         }

         if (verbose) {
            String res = message.getHeader("Subject", "\n");
            if (res != null && res.length() !=0)
               log.println(  "  /// Subj: " + res + "\n" );
         }

         if (verbose &&  contLogLength > 42) { // log ggf. Text
            if (textLen == 0) {
               Object oCo = message.getContent();
               if (oCo instanceof String) {
                   text = (String) oCo;
                   textLen = text.length();
               }
            }
            if (textLen > 0) {
               log.println();
               if (textLen <= contLogLength)
                  log.println(text);
               else {
                  log.print(text.substring(0, contLogLength)); 
                  log.println(" ....");
               }
            }
         } // log ggf. Text

         if (attach != null) {
            log.println("\n  /// Attach: " + attach);
            int atNum = 2;
            while ((attach = prop.getString("attach" + atNum)) != null) {
               log.println( "///         " + attach);
               ++atNum;
            }
         } // Attachments

      } catch (Exception e) {
         log.println("\n   *** error while logging the message:\n   *** "
             + e.getMessage());
         if (isTest())  e.printStackTrace(log);
      }
      log.println();
      log.flush();

      try { // -----  Send the message  -----------------------
         if (userName == null && userPass == null) {
            Transport.send(message);            
         } else {
            if (userName == null) userName = from;
            Transport tr = session.getTransport("smtp");
            tr.connect(smtpHost, userName, userPass);
            message.saveChanges();   // don't forget this
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();
         }
      } catch (Exception e) {
        return errorExit(21, e, "   *** error while sending the mail");
      }
      if (verbose) log.println(twoLineEndMsg());
      return 0;
   } // doIt()

} // SendMail (18.07.2003, 15.11.2004, 01.03.2009, 11.02.2010)

