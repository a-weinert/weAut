//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;

import java.io.Console;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JFileChooser;
// import org.junit.ComparisonFailure;
import de.frame4j.io.FileCriteria;
import de.frame4j.io.FileHelper;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.ComVar;
import de.frame4j.text.KMP;
import de.frame4j.text.TextHelper;
import de.frame4j.util.MinDoc;
import de.frame4j.text.CleverSSS;
import static de.frame4j.text.TextHelper.threeDigit;
import static de.frame4j.text.TextHelper.twoDigit;

/** <b>Test playground</b>. <br />
 *  <br />
 *  Test does a lot of tests and provides some information. It might easily
 *  extended with more tests and used to play with features. 
 *  Test will NOT be included in frame4j.jar. <br />
 *  Hence to run it do<code><pre>
 *   javac -d build Test.java
 *   java -cp build Test [-en] [-failIntent] [parameters for playground tests
 *   java -cp build -failIntend tto +.java e.g.
 *  </pre><code>
 *  {code -failIntend} will switch on tests that fail intentionally. <br>
 *  This might, e.g., be used to test the test procedures / test framework.
 *  @author A. Weinert
 */
@MinDoc(
   copyright = "Copyright 2010  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 40 $",
   lastModified   = "$Date: 2021-04-19 21:47:30 +0200 (Mo, 19 Apr 2021) $",
//   lastModifiedBy = "$Author: albrecht $",
   usage   = "start as Java application (-? for help)",  
   purpose = "a skeleton App inheritor to work / play with"
) public class Test extends App {
  @Override public final boolean parsePartial(){ return true; }
  @Override protected boolean allowNoPropertiesFile(){ return true; }
  
/** Block test of cronoUnit. <br /> */
  public final boolean cronoUnitTest = false;
/** Block test of zoneList. <br /> */   
  public final boolean zoneListTest = false;
/** Block test of fileChooser. <br /> */   
  public final boolean fileChooserTest = true;
  
/** Block intentionally failing tests. <br /> */   
  public boolean intentFailTest;


// substitute for ever changing Junit and its changing extra jars --- [ 
  void assertFalse(String mess, boolean mbF){
    if(mbF)  log.println("assFals failed: " + mess);
  }
  void assertTrue(String mess, boolean mbT){
    if(!mbT) log.println("assTrue failed: " + mess);
  }
  static boolean equals(Object expected, Object actual){
    if (expected == null) return actual == null;
    return expected.equals(actual);
  }
  void assertEquals(String mess, Object expected, Object actual){
    if (equals(expected, actual)) return;
    log.println("assEqul failed" + mess + " : " 
          + expected + "!=" + actual);
  } //assertEquals(String, 2*Object)
  void assertEquals(String mess, int expected, int actual){
    if (expected == actual) return;
    log.println("assEqul failed" + mess + " : " 
          + expected + "!=" + actual);
  } //assertEquals(String, 2*Object)
// substitute for ever changing Junit and its changing extra jars --- ]  
      
/** Startmethode von Test. <br />
 *  @param  args   command line parameter
 *  @see #doIt
 *  @see App#go(String[]) App.go()
 */
   public static void main(String[] args){
      try {
         new Test().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main

   
/** String to search in. <br /> */
   public String sIn =
  //   0123456789x123456789v123456789t123456789q123456789c
      " a b b cababaottouhababaabbcottottootto $" +
                                               "Date: $";
   
/** The programme's work. <br /> */   
   @Override public int doIt(){
      log.println();
      log.println(twoLineStartMsg());
      
      // start parameters were partially parsed asApp inheritance. As Test 
      // has no own .properties file we look at unparsed rest of arg here
      // programmatically just to look for -failIntent
      for (int i = 0; i < args.length; ++i) { 
        String arg = args[i]; // n.b. arg may be null as used args are set so
        if ("-failIntend".equals(arg)) { 
          intentFailTest = true;
          args[i] = null; // clear this arg for other tests or play ground
          break; // nothing else to look for; leave other args for other tests
        } // -failIntend 
      } // for over remaining args
      
   // Get the current time
      Instant instant = Instant.now();
      // Output format is ISO-8601
      log.println("now:   " + instant);
   // Adding 5 hours and 4 minutes to an Instant
      Instant instant2 = instant.plus(Duration.ofHours(5).plusMinutes(4));
      log.println("+5:02: " + instant2);
   // Create from a String
      instant = Instant.parse("1995-10-23T10:12:35Z");
      log.println("1995:  " + instant); // when Test appeared in Frame4J
      
      LocalDate localDate = LocalDate.now();
      log.println("nowLoc:" + localDate);
      LocalDateTime localDateTime = LocalDateTime.now();
      log.println("nowLoc:" + localDateTime);
      
      ZonedDateTime zonedDateTime = ZonedDateTime.now();
      log.println("nowZon:" + zonedDateTime);
      
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
      log.println("nowZon:" + zonedDateTime.format(formatter));
      
      DateTimeFormatter formatter2 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
      log.println("nowFul:" + zonedDateTime.format(formatter2));
      DateTimeFormatter formatter3 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
      log.println("nowMed:" + zonedDateTime.format(formatter3));
      
      if (cronoUnitTest) for( ChronoUnit chronoUnit : ChronoUnit.values()){
         log.println("chronoUnit: " + chronoUnit);
         log.println("  Duration: " + chronoUnit.getDuration());        
      } // cronoUnitTest
      log.println();
      if (cronoUnitTest) for (ChronoField c : ChronoField.values()){
         log.println("chronoField: " + c);   
         log.println("    Name.de: " + c.getDisplayName(Locale.GERMANY));
      } // cronoUnitTest

      LocalDateTime dt = LocalDateTime.now();
      Instant instNow = Instant.now();
      log.println();
      ZoneId zone = ZoneId.systemDefault();
      log.printf("We're in zone%22s %n",  zone);
      if (zoneListTest) {
        Set<String> allZones = ZoneId.getAvailableZoneIds();
        // Create a List using the set of zones and sort it.
        List<String> zoneList = new ArrayList<String>(allZones);
        Collections.sort(zoneList);
        for (String s : zoneList) {
          zone = ZoneId.of(s);
          ZoneId nrmZ = zone.normalized();
          ZoneRules rules = zone.getRules();

          ZonedDateTime zdt = dt.atZone(zone);
          ZoneOffset offset = zdt.getOffset();
          int secondsOfHour = offset.getTotalSeconds() % (60 * 60);
          String txt = String.format("%35s %10s ", zone, offset);
          if (secondsOfHour != 0) txt += "non hour offset";
          else if (! s.equals(nrmZ.toString())) txt += nrmZ;
          if (rules.isFixedOffset()) txt += "   fixed offset";
          else if(rules.isDaylightSavings(instNow)) txt += "   DST";
          log.println(txt);
      }} // zoneListTest for
      log.println();
      
      final Console cons = System.console();
      assertTrue("console :", cons != null);
      
      File df = FileHelper.getInstance("D:\\puseMuckel\\uhu.XcL");
      assertTrue("file i: type", df != null);
      assertTrue("file 0: type", FileHelper.getType(df).equals(".XcL"));
      assertTrue("file 1: type", FileHelper.isOfType(df, ";;;*.xcl"));
      assertTrue("file 2: type", FileHelper.isOfType(df, ";;;xc*"));
      assertTrue("file 3: type", FileHelper.isOfType(df, ";;;*.x*l;iii"));
      assertTrue("file 4: type", FileHelper.isOfType(df, ".xcl;"));
      assertFalse("file 5: type", FileHelper.isOfType(df, "xx;html;xml;java;txt;;"));

      testKMP();
      if (fileChooserTest ) testFileChooser();
      
      assertTrue("twoDigit    0", twoDigit(  0).equals("00"));
      assertTrue("twoDigit  100", twoDigit(100).equals("00"));
      assertTrue("twoDigit    7", twoDigit(  7).equals("07"));
      assertTrue("twoDigit   71", twoDigit( 71).equals("71"));


      assertTrue("threeDigit     0", threeDigit(   0).equals("000"));
      assertTrue("threeDigit  1000", threeDigit(1000).equals("000"));
      assertTrue("threeDigit     7", threeDigit(   7).equals("007"));
      assertTrue("threeDigit  1007", threeDigit(1007).equals("007"));
      assertTrue("threeDigit   300", threeDigit( 300).equals("300"));
      assertTrue("threeDigit   571", threeDigit( 571).equals("571"));
      assertEquals("threeDigit", "057", threeDigit(  57));
      if (intentFailTest) {
         assertEquals("3Digit(57)", "059", threeDigit(  57));
      } // intentFailTest
      
      assertEquals("BLANK_STRING", 40, ComVar.BLANK_STRING.length());
      assertEquals("formdec 57,6", "    57",
                              TextHelper.formDec(null, 57, 6).toString());

      if (intentFailTest) assertTrue("This must fail", false);
      log.println();
      log.println(twoLineEndMsg().append('\n'));
      return 0;
   }  // doIt()

   protected void testKMP(){
      for (String arg : args) {
         if (arg == null) continue;
         log.println("\n param \"" + arg + "\"");
         
         CleverSSS kmp1 = KMP.make(arg, false);
         log.println(kmp1.state());
         int[] we = kmp1.where(sIn, 0, 0);
         log.println(" KMP match \"" + sIn + "\" at " + we[0]+ ".." + we[1]
                 + "\n ------ ");
   
         CleverSSS  kmp2 = KMP.make(arg, true, true);
         log.println(kmp2.state());
         we = kmp2.where(sIn, 0, 0);
         log.println(" KMP match \"" + sIn + "\" at " + we[0]+ ".." + we[1]
                   + "\n ===== \n");
      } // for over remaining parameters (parse partial)
   } // testKMP()
 
   protected void testFileChooser(){
      for (String arg : args) {
         if (arg == null) continue;
         log.println("\n param \"" + arg + "\"");
         
         FileCriteria fCr = new FileCriteria();
         fCr.setWildName(arg);
         
         JFileChooser jFc = new JFileChooser(".");
         
         jFc.setAcceptAllFileFilterUsed(true);
         jFc.setFileFilter(fCr.getFileFilter());
 
         
         jFc.setMultiSelectionEnabled(true); 
         
         jFc.setDialogTitle("Choose the files not to be twiddled"); 

         int ret = jFc.showDialog(null, // parent
                                        "salvage army it");
         switch (ret) {
            case JFileChooser.CANCEL_OPTION: 
               log.println("cancelled");
               continue;
            case JFileChooser.ERROR_OPTION: 
               log.println("error occurred");
               continue;
         }
         
         File[] sels = jFc.getSelectedFiles();
         if (sels != null ) {
            log.println("selected as array " + sels.length);
            
            for (File f : sels) {
               log.println("select[] " + f);
            }
         } else {
            File selected = jFc.getSelectedFile();
            log.println("selected " + selected);

         }
      } // for over remaining parameters (parse partial)
   } // testFileChooser()

   protected void postMail( String recipient, String subject,
                  String content, String from ) throws MessagingException {

      Properties props = new Properties();
      props.put( "mail.smtp.host", "SMTP" );
      props.put( "mail.from", from);
      props.put( "mail.debug", "false");

      Session session = Session.getDefaultInstance( props );
      Message message = new MimeMessage( session );
     ///  InternetAddress addressTo = new InternetAddress( recipient );
      InternetAddress[] lesTos = InternetAddress.parse(recipient, false);
      
      Transport transport = session.getTransport("smtp");
      transport.connect();


      message.setRecipients(Message.RecipientType.TO, lesTos );
      message.setSubject( subject );
      message.setContent( content, "text/plain" );
      message.saveChanges(); // implicit with send()
      transport.sendMessage(message, message.getAllRecipients());
      log.println(" erste Nachricht, subj: " + subject);

      // AppHelper.sleep(3000);
      message.setSubject( "message + transport re-used" );
      message.setContent( subject, "text/plain" );
      message.saveChanges(); // implicit with send()
      transport.sendMessage(message, message.getAllRecipients());

      log.println(" zweite Nachricht, subj: " + message.getSubject());
      
      transport.close();
   } // postMail( ...)

} // class Test  (04.02.2010 f4jg, 16.03.2021 go reform)   
      
      
      
      