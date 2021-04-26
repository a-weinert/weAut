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

import static de.frame4j.text.CleverSSS.asPair;
import static de.frame4j.text.CleverSSS.asPairs;
import static de.frame4j.text.CleverSSS.pair2long;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;                       //\\
import org.junit.runner.Result;             //  \\ this works 18.04.2021
import org.junit.runner.notification.Failure;   //

import de.frame4j.io.FileHelper;
import de.frame4j.time.AClock;
import de.frame4j.time.TimeHelper;
import de.frame4j.text.TextHelper;
import de.frame4j.text.CleverSSS;
import de.frame4j.text.RK;
import de.frame4j.text.KMP;

/** <b>The (JUnit) Tests for this package</b>. <br />
 *  <br />
 *  This class contains all the the Tests mainly for the containing 
 *  package.<br />
 *  <br />
 *  JUnit Tests may be started by the standard JUnit 4.x annotation mechanism.
 *  There's also a static ({@link #main(String[])}-) starter as 
 *  application.<br />
 *  <br />
 *  The classes {@link PackageTest} may be contained in a Fram4J deployment. 
 *  Usually it is not for sake of compactness of the deployment .jar.<br />
 *  <br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2009 &nbsp; Albrecht Weinert.<br />
 *  <br /> 
 *  @author   Albrecht Weinert
 *  @version  $Revision: 40 $ ($Date: 2021-04-19 21:47:30 +0200 (Mo, 19 Apr 2021) $)
 *
 *  @see de.frame4j
 *  @see de.frame4j.time.TimeHelper
 *  @see de.frame4j.util.App
 *  @see de.frame4j.util
 *  @see de.frame4j.text.TextHelper
 */
 // So far    V.33   (06.01.2009 13:28) :  new, alpha (JUnit handling)
 //           V02.16 (17.05.2005 07:54) :  jarUnsigned.
 //           V.o78+ (19.02.2007 15:44) :  work around to run within Eclipse
 //           V.133+ (06.01.2016) : FileHelper
 // Last change by $Author: albrecht $ at $Date: 2021-04-19 21:47:30 +0200 (Mo, 19 Apr 2021) $

@MinDoc(
    copyright = "Copyright 2009  A. Weinert",
    author    = "Albrecht Weinert",
    version   = "V.$Revision: 40 $",
    lastModified   = "$Date: 2021-04-19 21:47:30 +0200 (Mo, 19 Apr 2021) $",
 // lastModifiedBy = "$Author: albrecht $",
    usage = "java.exe de.frame4j.util.PackageTest or Eclipse/JUnit mechanisms",  
    purpose = "(JUnit) Tests for package de.frame4j.util"
) public class PackageTest {
   
   // This is a workaround only for Eclipse's wrong handling of JUnit
   static TimeZone cetZone = TimeZone.getTimeZone("CET");
   static TimeZone utcZone = TimeZone.getTimeZone("UTC");
           /// = TimeHelper.CET_ZONE as well as = TimeHelper.UTC_ZONE
           ///  both fail wrongly if JUnit run under Eclipse
           /// (reflection access blahBlah) 
           /// otherwise only runs from completely build and deployed jar
           /// (Well, one would like to run the tests before.)

/** Some tests for PropMap. <br /> */
   @Test public void testPropMap(){
       PropMap propMap = new PropMap(18);
       propMap.put("language", "it");
       propMap.setLanguage("dk");
       assertTrue("1: lang set", "dk".equals(propMap.value("language")));
//       assertTrue("must fail", false);
   } // testPropMap()

   
/** Some tests for FileHelper. <br /> */
   @Test public void  testFileHelper(){
       File df = FileHelper.getInstance("D:\\puseMuckel\\uhu.XcL");
       assertTrue("file i: type", df != null);
       assertTrue("file 0: type", FileHelper.getType(df).equals(".XcL"));
       assertTrue("file 1: type", FileHelper.isOfType(df, ";;;*.xcl"));
       assertTrue("file 2: type", FileHelper.isOfType(df, ";;;xc*"));
       assertTrue("file 3: type", FileHelper.isOfType(df, ";;;*.x*l;iii"));
       assertTrue("file 4: type", FileHelper.isOfType(df, ".xcl;"));
       assertFalse("file 5: type", FileHelper.isOfType(df, "xx;html;xml;java;txt;;"));
   } // testPropMap()

   
/** Some tests for Time[xy] and TimeHelper. <br /> */
   @Test public void  testTimeHelper(){
      
      assertTrue("Ti4: Fr 1 = week 0", TimeHelper.weekInYear(1, 5) == 0);
      assertTrue("Ti4: Su 3 = week 0", TimeHelper.weekInYear(3, 7) == 0);
      assertTrue("Ti4: Mo 4 = week 1", TimeHelper.weekInYear(4, 1) == 1);
      assertTrue("Ti4: We20 = week 3", TimeHelper.weekInYear(20, 3) == 3);
     
      
      assertTrue(TimeHelper.isLeapYear(1980));
      assertFalse(TimeHelper.isLeapYear(1981));
      assertTrue(TimeHelper.isLeapYear(2000));
      assertTrue(TimeHelper.isLeapYear(2400));
      assertTrue(TimeHelper.isLeapYear(1600));
      assertFalse(TimeHelper.isLeapYear(1900));
      assertFalse(TimeHelper.isLeapYear(1800));
      assertFalse("Ti5: 2009 leap year ", TimeHelper.isLeapYear(2009));
      if (failIntent) assertTrue("intentionally", false);

      Action act = Action.select(TimeHelper.TIME_CHOOSE, Action.ALL, "nOw", true);
      assertTrue("Ti6: select now ", act == TimeHelper.TIME_CHOOSE[10]);
      act = Action.select(TimeHelper.TIME_CHOOSE, null, "cEt", true);
      assertTrue("Ti7: select cet " + act, act == TimeHelper.TIME_CHOOSE[31]);
      act = Action.select(TimeHelper.TIME_CHOOSE, null, "MEZ", true);
      assertTrue("Ti8: select mez " + act, act == TimeHelper.TIME_CHOOSE[31]);

      long tiMi = TimeHelper.parse(" 02.02.09 noon CET ");
      assertTrue("Ti9: parse Feb2 " + tiMi, tiMi == 1233572400000L);

   //   final TimeRO ti = new TimeST(tiMi, cetZone);
      final String testCstring = "2009-02-02T12:00:00.000+01:00";
      String erg = TimeHelper.format("c", tiMi, AClock.CET_ZONE);
      assertTrue("Ti10: toString(c) " + erg, testCstring.equals(erg));
      // Time utcTime = new Time(utcZone);
      // utcTime.set(tiMi);
      // ISO 8601 full date
      String erg2 = TimeHelper.format("c", tiMi, AClock.UTC_ZONE);
      final String testUTCstring = "2009-02-02T11:00:00.000Z";
      assertTrue("Ti11: toString(c) " + erg2, testUTCstring.equals(erg2));

    //  TimeRO tiR = null;
   } //  testTimeHelper()

/** Some tests for TextHelper. <br /> */
   @Test public void  testTextHelper(){
      System.out.println("\n  ---  testTextHelper(  start ----\n");
      
      assertTrue("0: simpLC", 
          TextHelper.simpLowerC("ABCÄÖÜabcäöüÝ").equals("abcäöüabcäöüý") );

      String lang = TextHelper.checkLanguage("  It ", null);
      assertTrue("1: checkLa", "it".equals(lang));
      String regio = TextHelper.checkRegion("  \rfR \t \n ", "DK");
      assertTrue("2: checkRe","FR".equals(regio));
       
      assertTrue("3:  startsW", TextHelper.startsWith("MEZ", "MEZ", true));
      assertTrue("4:  startsW", TextHelper.startsWith("mez", "MEZ", true));
      assertFalse("5: startsW", TextHelper.startsWith("mez", "MEZ",false));
       
      Action act =  TimeHelper.TIME_CHOOSE[31];
       //new Action(5,  +60, new String[]{"CET", "MEZ", "BST",  
         //                   "WAT", "IST", "WEST" }); // West Afrika
       
     int selInd = act.selectedBy("CET", false);
     assertTrue("6: selectedBy CET CET " + selInd, selInd == 1);
     selInd = act.selectedBy("cEt", true);
     assertTrue("7: selectedBy CET CET " + selInd, selInd == 1);
       selInd = act.selectedBy("wes.", true);
       assertTrue("8: selectedBy CET CET " + selInd, selInd == -6);
       selInd = act.selectedBy("MEZ", false);
       assertTrue("9: selectedBy CET CET " + selInd, selInd == 2);
       selInd = act.selectedBy("MEZ", false);
       assertTrue("10: selectedBy CET CET " + selInd, selInd == 2);
       boolean wildE = TextHelper.wildEqual("*.txt", "uhu.tXt", true);
       assertTrue("11: wildEqual *.txt uhu.tXt ", wildE);
       wildE = TextHelper.wildEqual("*.part", "sTb5iYpM.msi.part", true);
       assertFalse("12: wildEqual *.part ...M.msi.part ", wildE);
       wildE = TextHelper.wildEqual("*.*.part", "sTb5iYpM.msi.part", true);
       assertTrue("13: wildEqual *.*.part ...M.msi.part ", wildE);
       
       wildE =  TextHelper.wildEqual("frame4j", "Frame4j", true);
       assertTrue("13b: wildEqual f..j F..j t ", wildE);
 
       wildE =  TextHelper.wildEqual("frame4j", "Frame4", true);
       assertFalse("13c: wildEqual f..j F..j t ", wildE);

       wildE =  TextHelper.wildEqual("frame4j", "frame4j2", true);
       assertFalse("13d: wildEqual f..j F..j t ", wildE);

       
       assertTrue("14: getPropertyText whitespace = default ",
         TextHelper.getPropertyText("  \t ", ComVar.AUTHOR) == ComVar.AUTHOR);
       assertTrue("15: getPropertyText    equals stripped ",
          TextHelper.getPropertyText("  2  ", null).equals("2"));
       assertTrue("16: getPropertyText whitespace = default2 trimmed ",
          TextHelper.getPropertyText("  \t ", "  2  ", null).equals("2"));

       assertFalse("18a: simpCharEq(A,b)", TextHelper.simpCharEqu('$','D'));
       assertFalse("18b: simpCharEq(A,b)", TextHelper.simpCharEqu('A','b'));
       assertTrue("18c: simpCharEq(A,a)", TextHelper.simpCharEqu('A','a'));
       assertTrue("18d: simpCharEq(b,B)", TextHelper.simpCharEqu('b','B'));
       assertTrue("18e: simpCharEq(Z,z)", TextHelper.simpCharEqu('Z','z'));
       assertFalse("18f: simpCharEq([,{)", TextHelper.simpCharEqu('[','{'));
       assertFalse("18g: simpCharEq(x/,x*)", TextHelper.simpCharEqu((char)0xF7,(char)0xD7));
       assertTrue("18h: simpCharEq(ä,Ä)", TextHelper.simpCharEqu('Ä','ä'));
       assertTrue("18i: simpCharEq(ä,Ä)", TextHelper.simpCharEqu('ä','Ä'));
       assertTrue("18j: simpCharEq(Ü,ü)", TextHelper.simpCharEqu('Ü','ü'));
       assertTrue("18k: simpCharEq(Ý,ý)", TextHelper.simpCharEqu('Ý','ý'));
       assertTrue("18l: simpCharEq(ä,Ä,t)", TextHelper.simpCharEqu('Ä','ä',true));
       assertTrue("18m: simpCharEq(ä,Ä,t)", TextHelper.simpCharEqu('ä','Ä',true));
       assertTrue("18n: simpCharEq(Ü,ü,t)", TextHelper.simpCharEqu('Ü','ü',true));
       assertTrue("18o: simpCharEq(Ý,ý,t)", TextHelper.simpCharEqu('Ý','ý',true));
       
       assertTrue("19e: compare", 
                               TextHelper.compare("ABCÄÖÜabcäöüÝ", "abcäöüabcäöüý", true) == 0);
       assertTrue("19l: compare", 
                               TextHelper.compare("ABCÄÖÜabcäöüÝ", "abcäöüabcäöüý", false) < 0);
       assertTrue("19E: compare", 
                               TextHelper.compare("ABCDEFGHIJ1234567890", "ABCDEFGHIJ1234567890", false) == 0);
       assertTrue("19L: compare", 
                               TextHelper.compare("ABCDEFGHIJ1234567890", "ABCDEFGHIJ123456789", false) > 0);
       assertTrue("19S: compare", 
                               TextHelper.compare("ABCDEFGHIJ123456789", "ABCDEFGHIJ1234567890", false) < 0);
       assertTrue("19SN: compare", 
                               TextHelper.compare(null, "ABCDEFGHIJ1234567890", false) < 0);
       assertTrue("19NN: compare", 
                               TextHelper.compare(null, null, false) == 0);

   } //   testTextHelper() 


/** Some tests for RK. <br /> */
   @Test public void  testRK(){
      System.out.println("\n  ---  testRK  start ----\n");

      CleverSSS rkW1 = RK.make("O", true, true);
      System.out.println("19d: " + rkW1.state() );

      //  0123456789x12   01
      int[] we = // TextHelper.where(" H a l l o ", "O", 0, true); // 9,11
         rkW1.where(" H a l l o ", 0); // 9,10
      int fIndex =  rkW1.indexOf(" H a l l o ", 0); 
      int eIndex =  rkW1.endIndexOf(" H a l l o ", 0); 
      assertTrue("19d: where( H a l l o , O,0,t) != null ", we != null);
      System.out.println("19d-f: where(H a l l o ,O,0,t) = {" + we[0] 
                                                              + ", " + we[1]);
      assertTrue("19e: where( H a l l o ,O,0,t) [0] = 9 ", we[0] == 9);
      assertTrue("19f: where( H a l l o ,O,0,t) [1] = 10 ", we[1] == 10);
      assertTrue("19e: where( H a l l o ,O,0,t) [0] = 9 ", fIndex == 9);
      assertTrue("19f: where( H a l l o ,O,0,t) [1] = 10 ", eIndex == 10);

      long lWe =  rkW1.lastWhereImpl(" H a l l o ", -1, 0); // 9,10
      System.out.println("19d-f: lWere(H a l l o ,O,-1,t) = " 
                                             +  CleverSSS.asPair(null, lWe));
      assertTrue("19e: lWhere( H a l l o ,O,-1,t) [0] = 9 ", 
                                                    lWe == pair2long(9, 10));

      CleverSSS rkW2 = RK.make("o", false, true);
                    //  0123456789x12
      we =  rkW2.where(" H a l l o  ", 0); // 9,10
      assertTrue("19g: where( H a l l o  , o,0 ,f) != null ", we != null);
      System.out.println("19g-h: where(H a l l o  ,o ,0,f) = {" 
                                                     + we[0] + ", " + we[1]);
      assertTrue("19h: where( H a l l o  ,o ,0,f) [0] = 9 ", we[0] == 9);
      assertTrue("19i: where( H a l l o  ,o ,0,f) [1] = 12 ", we[1] == 10);

      CleverSSS rkW3 =  RK.make("al L", true, true);
                   //  0123456789x1234
      we = rkW3.where(" H a l l o ", 0); //  3, 8
      assertTrue("19j: RK( al L,t,t) opti == true ", rkW3.optimisticOK);
      assertTrue("19j: RK( al L,t,t) ignWS == true ", rkW3.ignoreCase);
      assertTrue("19j: RK( al L,t,t) ignCa == true ", rkW3.ignoreWS);

      assertTrue("19k: where( H a l l o , al L,0,t) != null ", we != null);
      System.out.println("19a-c: where(H a l l o , al L,0,t) = {" + we[0]
                                                              + ", " + we[1]);
      assertTrue("19l: where( H a l l o , al L,0,t) [0] = 3 ", we[0] == 3);
      assertTrue("19m: where( H a l l o , al L,0,t) [1] = 8 ", we[1] == 8);

       int found = TextHelper.indexOf(" H a l l o ", "A l", 1, false);
       assertTrue("20a: indexOfOpt( H a l l o , A l,1,f) = -1 ", found == -1);

       found = TextHelper.indexOf(" H a l l o ", "A l", 1, true);
       assertTrue("20b: indexOfOpt( H a l l o , A l,1,t) = 3 ", found == 3);
 
       found = TextHelper.indexOf(" H a l l o ", "a l", 1, false);
       assertTrue("20c: indOf( H a l l o , a l,1,f) = 3 ", found == 3);
       
       found = TextHelper.indexOf(" H a l l o ", "a l", 3, false);
       assertTrue("20d: indOf( H a l l o , a l,3,f) = 3 ", found == 3);
       
       found = TextHelper.indexOf(" H a l l o ", " ", 1, true);
       assertTrue("20e: indOf( H a l l o , ,1,1) = 2 ", found == 2);

       found = TextHelper.indexOf(" H a l l o ", "o ", 1, false);
       assertTrue("20f: indOf( H a l l o ,o ,1,f) = 9 ", found == 9);

       found = TextHelper.indexOf("....aaaBB...", "aaBB", 1, false);
       assertTrue("20g: indOf(....aaaBB...,aaBB,1,f) = 5 ", found ==5);

       String test =  "    version   = V.$Revision: 40 $ " +
       		"  $Date: 2021-04-19 21:47:30 +0200 (Mo, 19 Apr 2021) $ " +
       		"  lastModifiedBy = $Author: albrecht $ usage = " +
       	 "		java.exe de.frame4j.util.PackageTest or Eclipse/JUnit mechanisms";  

       found = TextHelper.indexOf(test, "$Revision", 1, false);
       System.out.println("20h: find $Revison ,1, f) = " + found);
       assertFalse("20g: find $Revision ,1, f)  != -1 ", found == -1);
       
       found = TextHelper.indexOfOpt(" H a l l o ", "A l", 1, false);
       assertTrue("21a: indOpt( H a l l o , A l,1,f) = -1 ", found == -1);

       found = TextHelper.indexOfOpt(" H a l l o ", "A l", 1, true);
       assertTrue("21b: indOpt( H a l l o , A l,1,t) = 3 ", found == 3);
 
       found = TextHelper.indexOfOpt(" H a l l o ", "a l", 1, false);
       assertTrue("21c: indOpt( H a l l o , a l,1,f) = 3 ", found == 3);
       
       found = TextHelper.indexOfOpt(" H a l l o ", "a l", 3, false);
       assertTrue("21d: indOpt( H a l l o , a l,3,f) = 3 ", found == 3);
       
       found = TextHelper.indexOfOpt(" H a l l o ", " ", 1, true);
       assertTrue("21e: indOpt( H a l l o , ,1,1) = 2 ", found == 2);

       found = TextHelper.indexOfOpt(" H a l l o ", "o ", 1, false);
       assertTrue("21f: indOpt( H a l l o ,o ,1,f) = 9 ", found == 9);

       /// Hint: optimistic indexOf fails here as expected (see jacaDoc) 
       found = TextHelper.indexOfOpt("....aaaBB...", "aaBB", 1, false);
       assertTrue("21g: indOpt(....aaaBB...,aaBB,1,f) = 5 ", found == -1);
       
       
       
       found = TextHelper.equalsTil(".Hallo.", 1, "HaLLu", true);
       assertTrue("22a: equalsTil(.Hallo.,1,haLLu,t) = 4 ", found == 4);
       
       found = TextHelper.equalsTil(".Hallo.", 1, "HaLLu", false);
       System.out.println("22b: equalsTil(.Hallo.,1,haLLu,f) = " + found);

       assertTrue("22b: equalsTil(.Hallo.,1,haLLu,f) = 2 ", found == 2);
       
       
       CleverSSS rk1 = RK.make("A l", false);      
       found = rk1.indexOf(" H a l l o ", 1);
       System.out.println("23a: RK.indOf( H a l l o , A l,1,f) = " + found);
       assertTrue("23a: RK.indOf( H a l l o , A l,1,f) = -1 ", found == -1);

       CleverSSS rk2 = RK.make("A l", true);      
       found = rk2.indexOf(" H a l l o ", 1);
       assertTrue("23b: RK.indOf( H a l l o , A l,1,t) = 3 ", found == 3);
 
       CleverSSS rk0 = RK.make("a l", false); 
       found = rk0.indexOf(" H a l l o ", 1);
       assertTrue("23c: RK.indOf( H a l l o , a l,1,f) = 3 ", found == 3);
       
       CleverSSS rk3 = RK.make("a l", false);      
       found = rk3.indexOf(" H a l l o ",  3);
       assertTrue("23d: RK.indOf( H a l l o , a l,3,f) = 3 ", found == 3);
       
       CleverSSS rk4 = RK.make(" ", true);      
       found = rk4.indexOf(" H a l l o ", 1);
       assertTrue("23e: RK.indOf( H a l l o , ,1,1) = 2 ", found == 2);

       CleverSSS rk5 = RK.make("o ", false); 
                          //0123456789x
       found = rk5.indexOf(" H a l l o ", 1);
       System.out.println("23e: RK.indOf( H a l l o , o ,1,f,f) = " + found);
       
       assertTrue("23f: RK.indOf( H a l l o ,o ,1,f) = 9 ", found == 9);

       CleverSSS rk6 = RK.make("aaBB", false); 
       assertFalse("23g: RK(aaBB,f) opti == false ", rk6.optimisticOK);
       
                        //  0123456789x123456789v1234567
       found = rk6.indexOf("....aaaBB.......aaaBB...", 1);
       assertTrue("23g: RK.indOf(....aaaBB...,aaBB,1,f) = 5 ", found == 5);
 
                            //  0123456789x123456789v1234567
       lWe = rk6.lastWhereImpl("....aaaBB.......aaaBBBBBB...", 26, 17); // 17, 21
       System.out.println("23g: lWhere(...aaaBBBB , aaBB,25,f) = "
                                                      + asPair(null, lWe));
       assertTrue("23g: lWhere(...aaaBBBB , aaBB,25,f) = 17,21 ", 
                                                 lWe == pair2long(17, 21));

       lWe = rk6.lastWhereImpl("....aaaBB.......aaaBBBBBB...", 15,0); // 5, 9
       System.out.println("23g: lWhere(...aaaBBBB , aaBB,15,f) = " +
                                                        asPair(null, lWe));
       assertTrue("23g: lWhere(...aaaBBBB , aaBB,15,f) = 5, 9 ",
                                                   lWe == pair2long(5, 9));

       
       CleverSSS rk7 = RK.make(" l  o ", false, true);
       System.out.println("23h: rk = \"" + rk7 + "\", len= " + rk7.length()
                   + ", subLast = " + ((RK)rk7).subLast + ", hash = "
                        + rk7.hashCode());      
                         // 0123456789x
       found = rk7.indexOf(" H a l l o ", 2);
       System.out.println("23h: RK.indOf( H a l l o , l o,2,f,t) = " + found);
       assertTrue("23h: RK.indOf( H a l l o , l o,1,f,t) = 7 ", found == 7);
                  //   0123456789x
       we = rk7.where(" H a l l o ", 2); // 7,10
       assertTrue("23i: where( H a l l o  , o,0 ,f) != null ", we != null);
       System.out.println("23i: where( H a l l o , l o,2,f,t) = {" + we[0] + ", " + we[1]);
       assertTrue("23i: where( H a l l o  , o,0 ,f)[0] = 7 ", we[0] == 7);
       assertTrue("23i: where( H a l l o  , o,0 ,f)[1] = 10 ", we[1] == 10);

       we = rk7.where(" H a l l o ", 7); // 7,10
       System.out.println("23j: " + rk7.state() );

       assertTrue("23j: where( H a l l o  , o,0 ,f) != null ", we != null);
       System.out.println("23i: where( H a l l o , l o,7,f,t) = {" + we[0] + ", " + we[1]);
       assertTrue("23j: where( H a l l o  , o,7 ,f)[0] = 7 ", we[0] == 7);
       assertTrue("23j: where( H a l l o  , o,7 ,f)[1] = 10 ", we[1] == 10);

       CleverSSS rk8 = RK.make("    L    ", true, true);
       System.out.println("23k: " + rk8.state() );

       we = rk8.where(" H a l  L    o   ", 6); // 8,9
       System.out.println("23k: where( H a l  L    o , l ,6,t,t) = {" + we[0] + ", " + we[1]);
       assertTrue("23k: where( H a l  L    o , l ,6,t)[0] = 7 ", we[0] == 8);
       assertTrue("23k: where( H a l  L    o , l ,6,t)[1] = 10 ", we[1] == 9);

       for (int i = 0; i < 270; ++i) {
          /// if (i == 0xB5) continue; // µ M
          char c = (char) i;
          char tc = TextHelper.lowerC(c);
          char gc = Character.toLowerCase(c);
          assertTrue("24a: lowerCase test ["+ i +"] of " + c + tc + " ?= " + gc, tc == gc);
          tc = TextHelper.upperC(c);
          gc = Character.toUpperCase(c);
          assertTrue("24a: upperCase test ["+ i +"] of " + c + tc + " ?= " + gc + " =" + ((int)gc), tc == gc);
       }
 
       assertTrue("25a: areEqual(Hallo, hallo, true)", 
                               TextHelper.areEqual("Hallo", "hallo", true));
       assertFalse("25b: areEqual(Hallo, hallo, false)", 
                               TextHelper.areEqual("Hallo", "hallo", false));
       assertTrue("25c: areEqual(null, null, true)", 
                                      TextHelper.areEqual(null, null, true));
       assertTrue("25d: areEqual(,,false)", TextHelper
                 .areEqual(ComVar.EMPTY_STRING, new StringBuilder(), false));

  } //   testRK() 


/** Some tests for KMP. <br />
 *  <br />   
 */
   @Test public void  testKMP(){
      System.out.println("\n  ---  testKMP start ----\n");

      CleverSSS  rkW1 = KMP.make("O", true, true);
      System.out.println("19d: " + rkW1.state() );

      //  0123456789x12   01
      int[] we = // TextHelper.where(" H a l l o ", "O", 0, true); // 9,11
         rkW1.where(" H a l l o ", 0); // 9,10
      int fIndex =  rkW1.indexOf(" H a l l o ", 0); 
      int eIndex =  rkW1.endIndexOf(" H a l l o ", 0); 

      assertTrue("19d: where( H a l l o , O,0,t) != null ", we != null);
      System.out.println("19d-f: where(H a l l o ,O,0,t) = {" + we[0] + ", " + we[1]);
      
      
      assertTrue("19e: where( H a l l o ,O,0,t) [0] = 9 ", we[0] == 9);
      assertTrue("19f: where( H a l l o ,O,0,t) [1] = 10 ", we[1] == 10);
      assertTrue("19e: where( H a l l o ,O,0,t) [0] = 9 ", fIndex == 9);
      assertTrue("19f: where( H a l l o ,O,0,t) [1] = 10 ", eIndex == 10);

      long lWe =  rkW1.lastWhereImpl(" H a l l o ", -1, -1); // 9,10
      System.out.println("19d-f: lWere(H a l l o ,O,-1,t) = "
                                                      + asPair(null, lWe));
      assertTrue("19e: lWhere( H a l l o ,O,-1,t) [0] = 9, 10 ",
                                                   lWe == pair2long(9, 10));

      CleverSSS  rkW2 = KMP.make("o", false, true);
      System.out.println("19g: " + rkW2.state() );

                    //  0123456789x12
       we = rkW2.where(" H a l l o  ", 0); // 9,10
       assertTrue("19g: where( H a l l o  , o,0 ,f) != null ", we != null);
       System.out.println("19g-h: where(H a l l o  ,o ,0,f) = {" + we[0] + ", " + we[1]);
       assertTrue("19h: where( H a l l o  ,o ,0,f) [0] = 9 ", we[0] == 9);
       assertTrue("19i: where( H a l l o  ,o ,0,f) [1] = 12 ", we[1] == 10);

       
       CleverSSS  rk1 = KMP.make("A l", false); 
       System.out.println("23a: " + rk1.state() );

       int found = rk1.indexOf(" H a l l o ", 1);
       System.out.println("23a: KMP.indOf( H a l l o , A l,1,f) = " + found);
       assertTrue("23a: KMP.indOf( H a l l o , A l,1,f) = -1 ", found == -1);

       CleverSSS  rk2 = KMP.make("A l", true);  
       System.out.println("23b: " + rk2.state() );

       assertTrue("23b: KMP(A l,t,f) opti == true ", rk2.optimisticOK);
       assertFalse("23b: KMP( al L,t,t) ignWS == false ",rk2.ignoreWS);
       assertTrue("23b: KMP( al L,t,t) ignCa == true ", rk2.ignoreCase);
      
                   ///         A l
       found = rk2.indexOf(" H a l l o ", 1);
       System.out.println("23b: RK.indOf( H a l l o , A l,1,t) = 3 \""
                               + rk2 + "\" = " + found);

       assertTrue("23b: KMP.indOf( H a l l o , A l,1,t) = 3 ", found == 3);
 
       CleverSSS  rk0 = KMP.make("a l", false); 
       System.out.println("23c: " + rk0.state() );

       found = rk0.indexOf(" H a l l o ", 1);
       assertTrue("23c: KMP.indOf( H a l l o , a l,1,f) = 3 ", found == 3);
       
       CleverSSS  rk3 = KMP.make("a l", false);
       System.out.println("23d: " + rk3.state() );

       found = rk3.indexOf(" H a l l o ",  3);
       assertTrue("23d: KMP.indOf( H a l l o , a l,3,f) = 3 ", found == 3);
       
       CleverSSS  rk4 = KMP.make(" ", true);
       System.out.println("23e: " + rk4.state() );
       found = rk4.indexOf(" H a l l o ", 1);
       assertTrue("23e: KMP.indOf( H a l l o , ,1,1) = 2 ", found == 2);
       
       
       CleverSSS  rkW3 = KMP.make("al L", true, true);
       System.out.println("19j: " + rkW3.state() );

       assertTrue("19j: KMP( al L,t,t) opti == true ", rkW3.optimisticOK);
       assertTrue("19j: KMP( al L,t,t) ignWS == true ", rkW3.ignoreWS);
       assertTrue("19j: KMP( al L,t,t) ignCa == true ", rkW3.ignoreCase);

                     //  0123456789x1   01234
       we =  rkW3.where(" H a l l o ", 0); //  3, 8

       assertTrue("19k: where( H a l l o , al L,0,t) != null ", we != null);
       System.out.println("19a-c: where(H a l l o , al L,0,t) \""
                             + rkW3 + "\"  3, 8 = {" + we[0] + ", " + we[1]);

       assertTrue("19l: where( H a l l o , al L,0,t) [0] = 3 ", we[0] == 3);
       
       assertTrue("19m: where( H a l l o , al L,0,t) [1] = 8 ", we[1] == 8);
       
       lWe = rkW3.lastWhereImpl(" H a l l o ", 19, 3); //  3, 8
       System.out.println("19a-c: lWhere(H a l l o , al L,19,t) = "
                                                       + asPair(null, lWe));
       assertTrue("19l: lWhere( H a l l o , al L,19,t) [0] = 3, 8 ",
                                                    lWe == pair2long(3, 8));

       CleverSSS  rk5 = KMP.make("o ", false); 
       System.out.println("23e: " + rk5.state() );

                          //0123456789x
       found = rk5.indexOf(" H a l l o ", 1);
       System.out.println("23e: RK.indOf( H a l l o , o ,1,f,f) = " + found);
       
       assertTrue("23f: RK.indOf( H a l l o ,o ,1,f) = 9 ", found == 9);

       CleverSSS  rk6 = KMP.make("aaBB", false); 
       System.out.println("23g: " + rk6.state() );
       assertFalse("23g: KMP(aaBB,f) opti == false ", rk6.optimisticOK);
       
                        //  0123456789x123456789v1234567
       found = rk6.indexOf("....aaaBB.......aaaBB...", 1);
       System.out.println("23g: indOf(....aaBB...,aaBB,1,f) = 5 \""
                               + rk6  + "\" = " + found);
       assertTrue("23g: KMP.indOf(....aaBB...,aaBB,1,f) = 5 ", found == 5);
 
                            //  0123456789x123456789v1234567
       lWe = rk6.lastWhereImpl("....aaaBB.......aaaBBBBBB...", 26, 9); // 17, 21
       System.out.println("23g: lWhere(...aaaBBBB , aaBB,25,f) = " 
                                                    + asPair(null, lWe));
       assertTrue("23g: lWhere(...aaaBBBB , aaBB,25,f) [0] = 17, 21 ",
                                               lWe == pair2long(17, 21));
       
       lWe = rk6.lastWhereImpl("....aaaBB.......aaaBBBBBB...", 15, 5); // 5, 9
       System.out.println("23g: lWhere(...aaaBBBB , aaBB,15,f) = " 
                                                       + asPair(null, lWe));
       assertTrue("23g: lWhere(...aaaBBBB , aaBB,15,f) [0] = 5, 9",
                                                    lWe == pair2long(5, 9));
       
       CleverSSS  rk7 = KMP.make(" l  o ", false, true);
       System.out.println("23h: " + rk7.state() );

       System.out.println("23h: rk = \"" + rk7 + "\", len= " + rk7.length()
            + ", subLast = " + rk7.toString() + ", hash = " + rk7.hashCode());      
                         // 0123456789x
       found = rk7.indexOf(" H a l l o ", 2);
       System.out.println("23h: RK.indOf( H a l l o , l o,2,f,t) = " + found);
       assertTrue("23h: RK.indOf( H a l l o , l o,1,f,t) = 7 ", found == 7);
                  //   0123456789x
       we = rk7.where(" H a l l o ", 2); // 7,10
       assertTrue("23i: where( H a l l o  , o,0 ,f) != null ", we != null);
       System.out.println("23i: where( H a l l o , l o,2,f,t) = {" + we[0] + ", " + we[1]);
       assertTrue("23i: where( H a l l o  , o,0 ,f)[0] = 7 ", we[0] == 7);
       assertTrue("23i: where( H a l l o  , o,0 ,f)[1] = 10 ", we[1] == 10);

       we = rk7.where(" H a l l o ", 7); // 7,10
       assertTrue("23j: where( H a l l o  , o,0 ,f) != null ", we != null);
       System.out.println("23i: where( H a l l o , l o,7,f,t) = {" + we[0] + ", " + we[1]);
       assertTrue("23j: where( H a l l o  , o,7 ,f)[0] = 7 ", we[0] == 7);
       assertTrue("23j: where( H a l l o  , o,7 ,f)[1] = 10 ", we[1] == 10);

       CleverSSS  rk8 = KMP.make("    L    ", true, true);
       System.out.println("23k: " + rk8.state() );

       we = rk8.where(" H a l  L    o   ", 6); // 8,9
       System.out.println("23k: where( H a l  L    o , l ,6,t,t) = {" + we[0] + ", " + we[1]);
       assertTrue("23k: where( H a l  L    o , l ,6,t)[0] = 7 ", we[0] == 8);
       assertTrue("23k: where( H a l  L    o , l ,6,t)[1] = 10 ", we[1] == 9);
       
       
       //----
       
       
       CleverSSS cs9 = KMP.make("otto", false, false);
       System.out.println("23l (KPM): " + cs9.state() );

       //             0123456789x123456789v123
       String sequ = "abcottoabcottottozzzotto";
       final long[] therFnd = new long[12];
       final long[] therFnd3 = new long[3];
       final long[] therFnd4 = new long[4];
      
       int nOfF = cs9.allWhere(therFnd, sequ, 0, 0, true);  //  overlap
       System.out.println("23l (KPM): allWhere( otto,f,f (T)) # = " + nOfF
                                        + "\n " + asPairs(null, therFnd));
       assertTrue("23l: allWhere( otto,f,f (T)) # = 4 ", nOfF == 4);
       assertTrue("23l: allWhere( otto,f,f (T)) [4] == -1 ", therFnd[4] == -1L);
       assertTrue("23l: allWhere( otto,f,f (T))  4 spots ", 
                      therFnd[0] ==  ((7L << 32) |  3) 
                    && therFnd[1] == ((14L << 32) | 10 )
                    && therFnd[2] == ((17L << 32) | 13 ) 
                    && therFnd[3] == ((24L << 32) | 20 ));
      
       
       nOfF = cs9.allWhere(therFnd4, sequ, 0, 0, true);
       
       System.out.flush();

       assertTrue("23m: allWhere( otto,f,f (T)) # = 4 ", nOfF == 4);
       nOfF = cs9.allWhere(therFnd3, sequ, 0, 0, true);
       assertTrue("23m: allWhere( otto,f,f (T)) # = 3 (cut)", nOfF == 3);
      
       
       nOfF = cs9.allWhere(therFnd, sequ, 0, 0, false); // non overlap
       System.out.println("23l: allWhere( otto,f,f (F)) # = " + nOfF
                                          + "\n " +  asPairs(null, therFnd));
       assertTrue("23n: allWhere( otto,f,f (F)) # = 3 ", nOfF == 3);
       
       cs9 = KMP.make("o  ttO ", true, true);
       System.out.println("23n: " + cs9.state() );

       //      0123456789x123456789v123456789t1
       sequ = "abc O TT o  tT    o zzzottouiuiu";
       
       we =  cs9.where(sequ, 0); //  4, 10
       System.out.println("23n: where " + we[0] + ", " + we[1]);

     
       nOfF = cs9.allWhere(therFnd, sequ, 0, 0, true);  //  overlap
       System.out.println("23o: allWhere( otto,t,t (T)) # = " + nOfF
                                         + "\n " +  asPairs(null, therFnd));
       assertTrue("23o: allWhere( otto,t,t (T)) # = 3 ", nOfF == 3);
       assertTrue("23p: allWhere( otto,t,t (T)) [4] == -1 ", therFnd[3] == -1L);
       assertTrue("23q: allWhere( otto,t,t (T))  4 spots ", 
                      therFnd[0] ==  ((10L << 32) |  4) 
                    && therFnd[1] == ((19L << 32) |  9 )
                    && therFnd[2] == ((27L << 32) | 23 ));
      
       
       nOfF = cs9.allWhere(therFnd3, sequ, 0, 0, true);
       assertTrue("23r: allWhere( otto,t,t (T)) # = 3 ", nOfF == 3);
      
       
       nOfF = cs9.allWhere(therFnd, sequ, 0, 0, false); // non overlap
       System.out.println("23t: allWhere( otto,f,f (F)) # = " + nOfF
                               + "\n " +  asPairs(null, therFnd));
       assertTrue("23t: allWhere( otto,f,f (F)) # = 2 ", nOfF == 2);

       System.out.println("\n  ---  testKMP  end ----\n");
   } //   testKMP() 



/** Some tests for CleverSSS. <br /> */
   @Test public void  testCleverSSS(){
      System.out.println("\n  ---  testCleverSSS start ----\n");

      CleverSSS rkW1 = CleverSSS.makeSimple("O", true, true);
      System.out.println("19d: " + rkW1.state() );

      //  0123456789x12   01
      int[] we = // TextHelper.where(" H a l l o ", "O", 0, true); // 9,11
         rkW1.where(" H a l l o ", 0); // 9,10
      int fIndex =  rkW1.indexOf(" H a l l o ", 0); 
      int eIndex =  rkW1.endIndexOf(" H a l l o ", 0); 

      assertTrue("19d: where( H a l l o , O,0,t) != null ", we != null);
      System.out.println("19d-f: where(H a l l o ,O,0,t) = {" + we[0] + ", " + we[1]);
      
      
      assertTrue("19e: where( H a l l o ,O,0,t) [0] = 9 ", we[0] == 9);
      assertTrue("19f: where( H a l l o ,O,0,t) [1] = 10 ", we[1] == 10);
      assertTrue("19e: where( H a l l o ,O,0,t) [0] = 9 ", fIndex == 9);
       assertTrue("19f: where( H a l l o ,O,0,t) [1] = 10 ", eIndex == 10);
       
       long lWe =  rkW1.lastWhereImpl(" H a l l o ", -1, 0); // 9,10
       int i1 = (int)lWe;
       int i2 = (int)(lWe >>>32);
       System.out.println("19d-f: lWere(H a l l o ,O,-1,t) = {" + i1 
                                                            + ", " + i2);
       assertTrue("19e: lWhere( H a l l o ,O,-1,t) [0] = 9 ", i1 == 9);
       assertTrue("19f: lWhere( H a l l o ,O,-1,t) [1] = 10 ", i2 == 10);


       CleverSSS rkW2 = CleverSSS.makeSimple("o", false, true);
       System.out.println("19g: " + rkW2.state() );

                    //  0123456789x12 
       we = rkW2.where(" H a l l o  ", 0); // 9,10
       assertTrue("19g: where( H a l l o  , o,0 ,f) != null ", we != null);
       System.out.println("19g-h: where(H a l l o  ,o ,0,f) = {" + we[0] + ", " + we[1]);
       assertTrue("19h: where( H a l l o  ,o ,0,f) [0] = 9 ", we[0] == 9);
       assertTrue("19i: where( H a l l o  ,o ,0,f) [1] = 12 ", we[1] == 10);

       
       CleverSSS rk1 = CleverSSS.makeSimple("A l", false, false); 
       System.out.println("23a: " + rk1.state() );

       int found = rk1.indexOf(" H a l l o ", 1);
       System.out.println("23a: CleverSSS.indOf( H a l l o , A l,1,f) = " + found);
       assertTrue("23a: CleverSSS.indOf( H a l l o , A l,1,f) = -1 ", found == -1);

       CleverSSS rk2 = CleverSSS.makeSimple("A l", true, false);  
       System.out.println("23b: " + rk2.state() );

       assertTrue("23b: CleverSSS(A l,t,f) opti == true ", rk2.optimisticOK);
       assertFalse("23b: CleverSSS( al L,t,t) ignWS == false ",rk2.ignoreWS);
       assertTrue("23b: CleverSSS( al L,t,t) ignCa == true ", rk2.ignoreCase);
      
                   ///         A l
       found = rk2.indexOf(" H a l l o ", 1);
       System.out.println("23b: RK.indOf( H a l l o , A l,1,t) = 3 \""
                               + rk2 + "\" = " + found);

       assertTrue("23b: CleverSSS.indOf( H a l l o , A l,1,t) = 3 ", found == 3);
 
       CleverSSS rk0 = CleverSSS.makeSimple("a l", false, false); 
       System.out.println("23c: " + rk0.state() );

       found = rk0.indexOf(" H a l l o ", 1);
       assertTrue("23c: CleverSSS.indOf( H a l l o , a l,1,f) = 3 ", found == 3);
       
       CleverSSS rk3 = CleverSSS.makeSimple("a l", false, false); 
       System.out.println("23d: " + rk3.state() );

       found = rk3.indexOf(" H a l l o ",  3);
       assertTrue("23d: CleverSSS.indOf( H a l l o , a l,3,f) = 3 ", found == 3);
       
       CleverSSS rk4 = CleverSSS.makeSimple(" ", true, false); 
       System.out.println("23e: " + rk4.state() );
       found = rk4.indexOf(" H a l l o ", 1);
       assertTrue("23e: CleverSSS.indOf( H a l l o , ,1,1) = 2 ", found == 2);
       
       
       CleverSSS rkW3 = CleverSSS.makeSimple("al L", true, true);
       System.out.println("19j: " + rkW3.state() );

       assertTrue("19j: CleverSSS( al L,t,t) opti == true ", rkW3.optimisticOK);
       assertTrue("19j: CleverSSS( al L,t,t) ignWS == true ", rkW3.ignoreWS);
       assertTrue("19j: CleverSSS( al L,t,t) ignCa == true ", rkW3.ignoreCase);

                     //  0123456789x1   01234
       we =  rkW3.where(" H a l l o ", 0); //  3, 8

       assertTrue("19k: where( H a l l o , al L,0,t) != null ", we != null);
       System.out.println("19a-c: where(H a l l o , al L,0,t) \""
                             + rkW3 + "\"  3, 8 = {" + we[0] + ", " + we[1]);

       assertTrue("19l: where( H a l l o , al L,0,t) [0] = 3 ", we[0] == 3);
       
       assertTrue("19m: where( H a l l o , al L,0,t) [1] = 8 ", we[1] == 8);
       
      
       lWe = rkW3.lastWhereImpl(" H a l l o ", 19, 0); //  3, 8
       i1 = (int)lWe;
       i2 = (int)(lWe >>>32);

       System.out.println("19a-c: lWhere(H a l l o , al L,19,t) = {"
                                              + i1 + ", " + i2);
       assertTrue("19l: lWhere( H a l l o , al L,19,t) [0] = 3 ", i1 == 3);
       assertTrue("19m: lWhere( H a l l o , al L,19,t) [1] = 8 ", i2 == 8);

       
       CleverSSS rk5 = CleverSSS.makeSimple("o ", false, false); 
       System.out.println("23e: " + rk5.state() );

                          //0123456789x
       found = rk5.indexOf(" H a l l o ", 1);
       System.out.println("23e: RK.indOf( H a l l o , o ,1,f,f) = " + found);
       
       assertTrue("23f: RK.indOf( H a l l o ,o ,1,f) = 9 ", found == 9);

       CleverSSS rk6 = CleverSSS.makeSimple("aaBB", false, false); 
       System.out.println("23g: " + rk6.state() );
       assertFalse("23g: CleverSSS(aaBB,f) opti == false ", rk6.optimisticOK);
       
                        //  0123456789x123456789v1234567
       found = rk6.indexOf("....aaaBB.......aaaBB...", 1);
       System.out.println("23g: indOf(....aaBB...,aaBB,1,f) = 5 \""
                               + rk6  + "\" = " + found);
       assertTrue("23g: CleverSSS.indOf(....aaBB...,aaBB,1,f) = 5 ", found == 5);
 

                            //  0123456789x123456789v1234567
       lWe = rk6.lastWhereImpl("....aaaBB.......aaaBBBBBB...", 26, 0); 
       i1 = (int)lWe;
       i2 = (int)(lWe >>>32); // 17, 21
       System.out.println("23g: lWhere(...aaaBBBB , aaBB,25,f) = {" + i1
                               + ", " + i2);
       assertTrue("23g: lWhere(...aaaBBBB , aaBB,25,f) [0] = 17 ", i1 == 17);
       assertTrue("23g: lWhere(...aaaBBBB , aaBB,25,f) [1] = 21", i2 == 21);

       lWe = rk6.lastWhereImpl("....aaaBB.......aaaBBBBBB...", 15, 0); 
       i1 = (int)lWe;
       i2 = (int)(lWe >>>32); // 5, 9
       System.out.println("23g: lWhere(...aaaBBBB , aaBB,15,f) = {" + i1
                               + ", " + i2);
       assertTrue("23g: lWhere(...aaaBBBB , aaBB,15,f) [0] = 5 ", i1 == 5);
       assertTrue("23g: lWhere(...aaaBBBB , aaBB,15,f) [1] = 9 ", i2 == 9);

       
       CleverSSS rk7 = CleverSSS.makeSimple(" l  o ", false, true);
       System.out.println("23h: " + rk7.state() );

       System.out.println("23h: rk = \"" + rk7 + "\", len= " + rk7.length()
            + ", subLast = " + rk7.toString() + ", hash = " + rk7.hashCode());      
                         // 0123456789x
       found = rk7.indexOf(" H a l l o ", 2);
       System.out.println("23h: RK.indOf( H a l l o , l o,2,f,t) = " + found);
       assertTrue("23h: RK.indOf( H a l l o , l o,1,f,t) = 7 ", found == 7);
                  //   0123456789x
       we = rk7.where(" H a l l o ", 2); // 7,10
       assertTrue("23i: where( H a l l o  , o,0 ,f) != null ", we != null);
       System.out.println("23i: where( H a l l o , l o,2,f,t) = {" + we[0] + ", " + we[1]);
       assertTrue("23i: where( H a l l o  , o,0 ,f)[0] = 7 ", we[0] == 7);
       assertTrue("23i: where( H a l l o  , o,0 ,f)[1] = 10 ", we[1] == 10);

       we = rk7.where(" H a l l o ", 7); // 7,10
       assertTrue("23j: where( H a l l o  , o,0 ,f) != null ", we != null);
       System.out.println("23i: where( H a l l o , l o,7,f,t) = {" + we[0] + ", " + we[1]);
       assertTrue("23j: where( H a l l o  , o,7 ,f)[0] = 7 ", we[0] == 7);
       assertTrue("23j: where( H a l l o  , o,7 ,f)[1] = 10 ", we[1] == 10);

       CleverSSS rk8 = CleverSSS.makeSimple("    L    ", true, true);
       System.out.println("23k: " + rk8.state() );

       we = rk8.where(" H a l  L    o   ", 6); // 8,9
       System.out.println("23k: where( H a l  L    o , l ,6,t,t) = {" + we[0] + ", " + we[1]);
       assertTrue("23k: where( H a l  L    o , l ,6,t)[0] = 7 ", we[0] == 8);
       assertTrue("23k: where( H a l  L    o , l ,6,t)[1] = 10 ", we[1] == 9);
       
       CleverSSS cs9 = CleverSSS.makeSimple("otto", false, false);
       System.out.println("23l: " + cs9.state() );

       //             0123456789x123456789v123
       String sequ = "abcottoabcottottozzzotto";
       final long[] therFnd = new long[12];
       final long[] therFnd3 = new long[3];
       final long[] therFnd4 = new long[4];
      
       int nOfF = cs9.allWhere(therFnd, sequ, 0, 0, true);  //  overlap
       System.out.println("23l: allWhere( otto,f,f (T)) # = " + nOfF
                                         + "\n " +  asPairs(null, therFnd));
       assertTrue("23l: allWhere( otto,f,f (T)) # = 4 ", nOfF == 4);
       assertTrue("23l: allWhere( otto,f,f (T)) [4] == -1 ", therFnd[4] == -1L);
       assertTrue("23l: allWhere( otto,f,f (T))  4 spots ", 
                      therFnd[0] ==  ((7L << 32) |  3) 
                    && therFnd[1] == ((14L << 32) | 10 )
                    && therFnd[2] == ((17L << 32) | 13 ) 
                    && therFnd[3] == ((24L << 32) | 20 ));
      
       
       nOfF = cs9.allWhere(therFnd4, sequ, 0, 0, true);
       assertTrue("23m: allWhere( otto,f,f (T)) # = 4 ", nOfF == 4);
       nOfF = cs9.allWhere(therFnd3, sequ, 0, 0, true);
       assertTrue("23m: allWhere( otto,f,f (T)) # = 3 (cut)", nOfF == 3);
      
       
       nOfF = cs9.allWhere(therFnd, sequ, 0, 0, false); // non overlap
       System.out.println("23l: allWhere( otto,f,f (F)) # = " + nOfF
                                           + "\n " +  asPairs(null, therFnd));
       assertTrue("23n: allWhere( otto,f,f (F)) # = 3 ", nOfF == 3);
       
       cs9 = CleverSSS.makeSimple("o  ttO ", true, true);
       System.out.println("23n: " + cs9.state() );

       //      0123456789x123456789v123456789t1
       sequ = "abc O TT o  tT    o zzzottouiuiu";
       
       we =  cs9.where(sequ, 0); //  4, 10
       System.out.println("23n: where " + we[0] + ", " + we[1]);

     
       nOfF = cs9.allWhere(therFnd, sequ, 0, 0, true);  //  overlap
       System.out.println("23o: allWhere( otto,t,t (T)) # = " + nOfF
                                        + "\n " +  asPairs(null, therFnd));
       assertTrue("23o: allWhere( otto,t,t (T)) # = 3 ", nOfF == 3);
       assertTrue("23p: allWhere( otto,t,t (T)) [4] == -1 ", therFnd[3] == -1L);
       assertTrue("23q: allWhere( otto,t,t (T))  4 spots ", 
                      therFnd[0] ==  ((10L << 32) |  4) 
                    && therFnd[1] == ((19L << 32) |  9 )
                    && therFnd[2] == ((27L << 32) | 23 ));
      
       
       nOfF = cs9.allWhere(therFnd3, sequ, 0, 0, true);
       assertTrue("23r: allWhere( otto,t,t (T)) # = 3 ", nOfF == 3);
      
       
       nOfF = cs9.allWhere(therFnd, sequ, 0, 0, false); // non overlap
       System.out.println("23t: allWhere( otto,f,f (F)) # = " + nOfF
                                          + "\n " +  asPairs(null, therFnd));
       assertTrue("23t: allWhere( otto,f,f (F)) # = 2 ", nOfF == 2);

       System.out.println("\n  ---  testCleverSSS  end ----\n");
   } //   testCleverSSS() 

 
   
/** Let at least one &quot;test&quot; fail on intend. <br />
 *  <br />
 *  This is mainly a meta test for the test harness.<br />   
 */
   static boolean failIntent;

/** Static test launcher. <br />
 *  <br />
 *  @param args -fail switches on intentional test failures
 */
   public static void main(String[] args) {
      failIntent = args.length > 0 && args[0].equals("-fail");
      System.out.println("\n /// Test start with " 
            + (failIntent ? "all" : "no")+ " intentional test failures\n");

      
      Result result = org.junit.runner.JUnitCore.runClasses(PackageTest.class);
      int anzFail = result.getFailureCount();
      int anzRun = result.getRunCount();
      if (anzFail > 0) {
         List<Failure> list = result.getFailures();
         int i = 0;
         for (Failure f : list) {
            System.out.println("\n\n // failure " + ++i + " : "   + f );
         }
      }
      System.out.println("\n /// " + anzRun 
                              + " test(s) ended with " + anzFail + " failures \n");

   } // main(String[] )

} // class PackageTest (03.02.2009, 17.02.2010)
