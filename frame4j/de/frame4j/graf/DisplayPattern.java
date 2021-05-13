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

import de.frame4j.util.MinDoc;

/** <b>Plain patterns for a matrix display</b>. <br />
 *  <br />
 *  This class features the pattern for a 5 * 8 dot matrix display, like those
 *  used in the class. The character set is best described by
 *  &quot;ISO 8859-1 + a few Unicode specials and Greeks&quot;.
 *  Start this class as {@link #main(String[]) application} for a full display 
 *  list (as starlet/asterisk images).<br />
 *  <br />
 *  For the simplest possible display of printable characters without good
 *  small letters going below underline 5 * 8 dots it is sufficient.<br />
 *  <br style="clear:both;" />
 *  Every point's state is represented by a byte value; the
 *  meaning / code is:<br />
 *  <br />
 *  <table border="1" cellpadding="4" cellspacing="0" style="text-align:center;"
 *   summary="Colours">
 *  <tr><th>Value</th><th>Meaning</th><th>Colour</th></tr>
 *  <tr><td style="text-align:center;">0</td><td style="text-align:center;">Off</td>
 *         <td>background</td></tr>
 *  <tr><td style="text-align:center;">1</td><td style="text-align:center;">On</td>
 *         <td>foreground</td></tr>
 *  <tr><td style="text-align:center;">2..127</td><td style="text-align:center;">Not off</td>
 *         <td>Not used in this class.<br >
 *             application specific.<br />
 *             See {@link #get(char, byte)}</td></tr>
 *  </table><br style="clear:both;" />
 *  <br />
 *  Copyright 2000 - 2003, 2019 &nbsp; Albrecht Weinert<br />
 */
 // so far  V00.00 (08.01.2001) : new
 //         V00.01 (10.01.2001) : small i smaller, 128 characters
 //         V00.03 (09.05.2001) : new Characters, main()
 //         V02.00 (24.04.2003) : CVS Eclipse
 //         V.o75+ (13.02.2009) : Euro and root at 0x20ac 0x221a
 //         V.  12 (07.03.2019) : doc references to removed Matrix ... removed

@MinDoc(
   copyright = "Copyright 2000 - 2009, 2019  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 44 $",
   lastModified   = "$Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $",
   usage   = "start as Java application (-? for help)",  
   purpose = "supports application with patterns for a dot matrix display"
) public class DisplayPattern {

/** No objects. */
   private DisplayPattern(){}

/** Number of points, X direction, horizontal. <br />
 *  <br />
 *  Value: {@value}<br />
 */
   public static final int X_DOTS = 5;

/** Number of points, Y direction, vertical. <br />
 *  <br />
 *  Value: {@value}<br />
 */
   public static final int Y_DOTS = 8;

/** Constant for off (0) for &quot;readable&quot; array initialisers. <br>
 *  <br />
 *  Value: {@value}<br /> 
 */
   private static final byte µ = 0;

/** Constant for on (1) for &quot;readable&quot; array initialisers. <br>
 *  <br />
 *  Value: {@value}<br /> 
 */
   private static final byte X = 1;

// Special characters

/** Pattern for space. <br />
 *  <br />
 *  Value: length 0 (since V00.03, 13.06.01)
 */
   public static final byte[][] SPACE =  new byte[0][];

/** Pattern for Undefined. <br />
 *  <br />
 *  Effect is like blank.<br />
 *  <br />
 *  Value: one null (since  V00.03, 18.06.01)
 */
   public static final byte[][] UNDEF =  { null };

private static final byte[] _________ = null;    // empty line
//                          _,_,X,_,_ 
private static final byte[] ____X____ = {µ,µ,X}; // 1 Dot in the middle
//                          _,X,_,X,_ 
private static final byte[] __X___X__ = {µ,X,µ,X}; // 2 Dots distributed
//                          X,X,X,X,X
private static final byte[] X_X_X_X_X = {X,X,X,X,X}; // full line
//                         {_,X,X,X,X},
private static final byte[] __X_X_X_X = {µ,X,X,X,X}; // 
//                         {X,_,_,_,_},
private static final byte[] X________ = {X}; // 
//                         {_,X,X,X,_},
private static final byte[] __X_X_X__ = {µ,X,X,X}; // 
//                         {_,_,_,_,X},
private static final byte[] ________X = {µ,µ,µ,µ,X}; // 
//                         {X,X,X,X,_},
private static final byte[] X_X_X_X__ = {X,X,X,X}; // 
//                         {_,X,_,_,_} 
private static final byte[] __X______ = {µ,X}; // 1 Dot 2vl
//                         {_,_,_,X,_} 
private static final byte[] ______X__ = {µ,µ,µ,X}; // 1 Dot 2vr
//                         {X,_,_,_,X},
private static final byte[] X_______X = {X,µ,µ,µ,X}; // 2 Dots on sides
//                         {X,_,X,_,X},
private static final byte[] X___X___X = {X,µ,X,µ,X}; // 3 Dots sides, middle
//                         {X,X,_,_,_} 
private static final byte[] X_X______ = {X,X}; // 2 Dots left
//                         {_,X,X,_,_} 
private static final byte[] __X_X____ = {µ,X,X}; // 2 Dots 2vl,m
//                         {_,X,_,_,X} 
private static final byte[] __X_____X = {µ,X,µ,µ,X}; // 2 Dots 2vl,r 
//                         {_,X,_,X,X} 
private static final byte[] __X___X_X = {µ,X,µ,X,X}; // 3 Dots 2vl,2vr,r 
//                         {X,_,_,X,_} 
private static final byte[] X_____X__ = {X,µ,µ,X}; // 2 Dots l, 2vr 
//                         {X,_,X,_,_} 
private static final byte[] X___X____ = {X,µ,X}; // 2 Dots l,m
//                         {X,X,X,_,_} 
private static final byte[] X_X_X____ = {X,X,X}; // 3 Dots l
//                         {X,X,_,_,X} 
private static final byte[] X_X_____X = {X,X,µ,µ,X}; // 3 Dots ll, r
//                         {X,_,_,X,X} 
private static final byte[] X_____X_X = {X,µ,µ,X,X}; // 3 Dots l rr
//                         {_,_,X,X,X} 
private static final byte[] ____X_X_X = {µ,µ,X,X,X}; // 3 Dots rrr
//                         {_,_,X,_,X} 
private static final byte[] ____X___X = {µ,µ,X,µ,X}; // 2 Dots mi, r
//                         {_,_,_,X,X} 
private static final byte[] ______X_X = {µ,µ,µ,X,X}; // 2 Dots rr
//                         {_,_,X,X,_} 
private static final byte[] ____X_X__ = {µ,µ,X,X};   // 2 Dots m 2vr
//                         {X,X,_,X,X} 
private static final byte[] X_X___X_X = {X,X,µ,X,X}; // 4 Dots oh.mi
//                         {_,X,X,_,X} 
private static final byte[] __X_X___X = {µ,X,X,µ,X}; // 3 Dots 2vl,m, r
//                         {X,X,_,X,_} 
private static final byte[] X_X___X__ = {X,X,µ,X}; // 3 Dots l,l, 2vr
//                         {X,_,X,X,X} 
private static final byte[] X___X_X_X = {X,µ,X,X,X}; // 4 Dots oh. 2vl


//----------------------------------------------------------------------

/** Pattern for Exclamation. */
   static final byte[][] EXCL = 
         new byte[][]{
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       _________ ,
       ____X____    };


   static final byte[][] DQUOT =
         new byte[][]{
       __X___X__ ,
       __X___X__ ,
       __X___X__   };

   static final byte[][] HASH
     =  new byte[][]{
       __X___X__ ,
       __X___X__ ,
       X_X_X_X_X ,
       __X___X__ ,
       __X___X__ ,
       X_X_X_X_X ,
       __X___X__ ,
       __X___X__    };

   static final byte[][] DOLLAR = 
         new byte[][]{
       ____X____ ,
       __X_X_X_X ,
       X________ ,
       __X_X_X__ ,
       ________X ,
       X_X_X_X__ ,
       ____X____ ,
       ____X____    };

   static final byte[][] PERC = 
         new byte[][]{
       _________ ,
       X_X______ ,
       X_X_____X ,
       ______X__ ,
       ____X____ ,
       __X______ ,
       X_____X_X ,
       ______X_X    };

   static final byte[][] AMP = 
         new byte[][]{
       _________ ,
       __X_X____ ,
       X_____X__ ,
       X___X____ ,
       __X______ ,
       X___X___X ,
       X_____X__ ,
       __X_X___X    };

   static final byte[][] SQUOT = 
         new byte[][]{
       _________ ,
       __X_X_X__ ,
       ______X__ ,
       ______X__    };

   static final byte[][] OBRK = 
         new byte[][]{
       _________ ,
       ____X____ ,
       __X______ ,
       X________ ,
       X________ ,
       X________ ,
       __X______ ,
       ____X____    };

   static final byte[][] CBRK = 
         new byte[][]{
       _________ ,
       ____X____ ,
       ______X__ ,
       ________X ,
       ________X ,
       ________X ,
       ______X__ ,
       ____X____    };

   static final byte[][] STAR =  
         new byte[][]{
       _________ ,
       ____X____ ,
       X___X___X ,
       __X_X_X__ ,
       X___X___X ,
       ____X____   };

/** Pattern for + . */
   static final byte[][] PLUS = 
         new byte[][]{
    _________ ,
    _________ ,
    ____X____ ,
    ____X____ ,
    X_X_X_X_X ,
    ____X____ ,
    ____X____        };


   static final byte[][] COMMA =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       _________ ,
       __X_X____ ,
       __X_X____ ,
       ____X____ ,
       __X______    };

   static final byte[][] MINUS = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       _________ ,
       X_X_X_X_X    };


   static final byte[][] DOT = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       _________ ,
       _________ ,
       _________ ,
       __X_X____ ,
       __X_X____    };



   static final byte[][] SLASH =
         new byte[][]{
       _________ ,
       _________ ,
       ________X ,
       ______X__ ,
       ____X____ ,
       __X______ ,
       X________   };


   static final byte[][] M0 =
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X_____X_X ,
       X___X___X ,
       X_X_____X ,
       X_______X ,
       X_______X ,
       __X_X_X__    };

   static final byte[][] M1 =
         new byte[][]{
       ______X__ ,
       ____X_X__ ,
       __X___X__ ,
       ______X__ ,
       ______X__ ,
       ______X__ ,
       ______X__ ,
       __X_X_X_X    };

   static final byte[][] M2 =
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       ________X ,
       ______X__ ,
       ____X____ ,
       __X______ ,
       __X______ ,
       X_X_X_X_X    };

   static final byte[][] M3 =
         new byte[][]{
       X_X_X_X_X ,
       ________X ,
       ______X__ ,
       ____X____ ,
       ______X__ ,
       ________X ,
       X_______X ,
       __X_X_X__    };

   static final byte[][] M4 =
         new byte[][]{
       ______X__ ,
       ______X__ ,
       ____X_X__ ,
       __X___X__ ,
       X_____X__ ,
       X_X_X_X_X ,
       ______X__ ,
       ______X__    };

   static final byte[][] M5 =
         new byte[][]{
       X_X_X_X_X ,
       X________ ,
       X________ ,
       X_X_X_X__ ,
       ________X ,
       ________X ,
       ________X ,
       X_X_X_X__    };


   static final byte[][] M6 =
         new byte[][]{
       ______X_X ,
       ____X____ ,
       __X______ ,
       X________ ,
       X_X_X_X__ ,
       X_______X ,
       X_______X ,
       __X_X_X__    };

   static final byte[][] M7 =
         new byte[][]{
       X_X_X_X_X ,
       ________X ,
       ______X__ ,
       ____X____ ,
       __X______ ,
       __X______ ,
       __X______ ,
       __X______    };

   static final byte[][] M8 =
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       X_______X ,
       __X_X_X__    };

   static final byte[][] M9 =
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       __X_X_X_X ,
       ________X ,
       ______X__ ,
       ____X____ ,
       X_X______    };


   static final byte[][] COLON = 
         new byte[][]{
       _________ ,
       _________ ,
       __X_X____ ,
       __X_X____ ,
       _________ ,
       __X_X____ ,
       __X_X____   };

   static final byte[][] SEMICOLON = 
         new byte[][]{
       _________ ,
       _________ ,
       __X_X____ ,
       __X_X____ ,
       _________ ,
       __X_X____ ,
       ____X____ ,
       __X______    };


   static final byte[][] LT = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       ____X_X_X ,
       X________ ,
       ____X_X_X   };


   static final byte[][] EQUALS = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_X_X_X_X ,
       _________ ,
       X_X_X_X_X   };


   static final byte[][] GT =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_X_X____ ,
       ________X ,
       X_X_X____    };


   static final byte[][] QUEST = 
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       ________X ,
       ______X__ ,
       ____X____ ,
       ____X____ ,
       _________ ,
       ____X____    };


   static final byte[][] AT = 
         new byte[][]{
       _________ ,
       __X_X_X__ ,
       X_______X ,
       ________X ,
       __X_X___X ,
       X___X___X ,
       X___X___X ,
       __X_X_X__    };




   static final byte[][] A = 
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       X_X_X_X_X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X    };


   static final byte[][] B = 
         new byte[][]{
       X_X_X_X__ ,
       X_______X ,
       X_______X ,
       X_X_X_X__ ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_X_X_X__    };

   static final byte[][] C = 
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X________ ,
       X________ ,
       X________ ,
       X________ ,
       X_______X ,
       __X_X_X__    };


   static final byte[][] D = 
         new byte[][]{
       X_X_X____ ,
       X_____X__ ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_____X__ ,
       X_X_X____    };


   static final byte[][]  E =
         new byte[][]{
       X_X_X_X_X ,
       X________ ,
       X________ ,
       X_X_X_X__ ,
       X________ ,
       X________ ,
       X________ ,
       X_X_X_X_X    };



   static final byte[][]  F =
         new byte[][]{
       X_X_X_X_X ,
       X________ ,
       X________ ,
       X_X_X_X__ ,
       X________ ,
       X________ ,
       X________ ,
       X________    };



   static final byte[][]  G =
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X________ ,
       X________ ,
       X___X_X_X ,
       X_______X ,
       X_______X ,
       __X_X_X_X    };



   static final byte[][]  H =
         new byte[][]{
       X_______X ,
       X_______X ,
       X_______X ,
       X_X_X_X_X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X   };    


   static final byte[][]  I =
         new byte[][]{
       __X_X_X__ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       __X_X_X__   };          


   static final byte[][]  J =
         new byte[][]{
       ____X_X_X ,
       ______X__ ,
       ______X__ ,
       ______X__ ,
       ______X__ ,
       ______X__ ,
       X_____X__ ,
       __X_X____    };     


   static final byte[][]  K =
         new byte[][]{
       X_______X ,
       X_____X__ ,
       X___X____ ,
       X_X______ ,
       X_X______ ,
       X___X____ ,
       X_____X__ ,
       X_______X    };   


   static final byte[][]  L =
         new byte[][]{
       X________ ,
       X________ ,
       X________ ,
       X________ ,
       X________ ,
       X________ ,
       X________ ,
       X_X_X_X_X    };     


   static final byte[][]  M =
         new byte[][]{
       X_______X ,
       X_X___X_X ,
       X___X___X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X    };     


   static final byte[][]  N =
         new byte[][]{
       X_______X ,
       X_______X ,
       X_X_____X ,
       X___X___X ,
       X_____X_X ,
       X_______X ,
       X_______X ,
       X_______X    };      


   static final byte[][]  O =
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       __X_X_X__    };   


   static final byte[][]  P =
         new byte[][]{
       X_X_X_X__ ,
       X_______X ,
       X_______X ,
       X_X_X_X__ ,
       X________ ,
       X________ ,
       X________ ,
       X________    };   


   static final byte[][]  Q =
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X___X___X ,
       X_____X_X ,
       __X_X___X    };    


   static final byte[][]  R =
         new byte[][]{
       X_X_X_X__ ,
       X_______X ,
       X_______X ,
       X_X_X_X__ ,
       X_X______ ,
       X___X____ ,
       X_____X__ ,
       X_______X    };   


   static final byte[][]  S =
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X________ ,
       X_X_X_X__ ,
       ________X ,
       ________X ,
       X_______X ,
       __X_X_X__    };



   static final byte[][]  T =
         new byte[][]{
       X_X_X_X_X ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____    };   


   static final byte[][]  U =
         new byte[][]{
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       __X_X_X__    };   


   static final byte[][]  V =
         new byte[][]{
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       __X___X__ ,
       ____X____    }; 


   static final byte[][]  W =
         new byte[][]{

       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X___X___X ,
       X___X___X ,
       __X___X__    }; 


/** Pattern for capital letter X. */
   static final byte[][]  MX =
         new byte[][]{
       X_______X ,
       X_______X ,
       __X___X__ ,
       ____X____ ,
       ____X____ ,
       __X___X__ ,
       X_______X ,
       X_______X    };



   static final byte[][]  Y =
         new byte[][]{
       X_______X ,
       X_______X ,
       X_______X ,
       __X___X__ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____    };



   static final byte[][]  Z =
         new byte[][]{
       X_X_X_X_X ,
       ________X ,
       ______X__ ,
       ____X____ ,
       __X______ ,
       __X______ ,
       X________ ,
       X_X_X_X_X    };


   static final byte[][] OSQBRK= 
         new byte[][]{
       __X_X_X__ ,
       __X______ ,
       __X______ ,
       __X______ ,
       __X______ ,
       __X______ ,
       __X______ ,
       __X_X_X__    };


   static final byte[][] BACKSLASH =
         new byte[][]{
       _________ ,
       _________ ,
       X________ ,
       __X______ ,
       ____X____ ,
       ______X__ ,
       ________X   };

   static final byte[][] CSQBRK= 
         new byte[][]{
       __X_X_X__ ,
       ______X__ ,
       ______X__ ,
       ______X__ ,
       ______X__ ,
       ______X__ ,
       ______X__ ,
       __X_X_X__    };


   static final byte[][] GRAVE= 
         new byte[][]{
       _________ ,
       ____X____ ,
       __X___X__ ,
       X_______X     };


   static final byte[][] UNDERLINE = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       _________ ,
       _________ ,
       _________ ,
       _________ ,
       X_X_X_X_X    };



   static final byte[][] SQUOT2 =
         new byte[][]{
       __X______ ,
       ____X____ ,
       ______X__    };



   static final byte[][]  a =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       __X_X_X__ ,
       ________X ,
       __X_X_X_X ,
       X_______X ,
       __X_X_X_X    };



   static final byte[][]  b =
         new byte[][]{
       X________ ,
       X________ ,
       X________ ,
      {X,µ,X,X,µ},
       X_X_____X ,
       X_______X ,
       X_______X ,
       X_X_X_X__    };



   static final byte[][]  c =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       __X_X_X__ ,
       X________ ,
       X________ ,
       X_______X ,
       __X_X_X__    };



   static final byte[][]  d =
         new byte[][]{
       ________X ,
       ________X ,
       ________X ,
       __X_X___X ,
       X_____X_X ,
       X_______X ,
       X_______X ,
       __X_X_X_X    };



   static final byte[][]  e =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       __X_X_X__ ,
       X_______X ,
       X_X_X_X_X ,
       X________ ,
       __X_X_X__    };



   static final byte[][]  f =
         new byte[][]{
       ____X_X__ ,
       __X_____X ,
       __X______ ,
       X_X_X____ ,
       __X______ ,
       __X______ ,
       __X______ ,
       __X______    };



   static final byte[][]  g =
         new byte[][]{
       _________ ,
       _________ ,
       __X_X_X_X ,
       X_______X ,
       X_______X ,
       __X_X_X_X ,
       ________X ,
       __X_X_X__    };



   static final byte[][]  h =
         new byte[][]{
       X________ ,
       X________ ,
       X________ ,
      {X,µ,X,X,µ},
       X_X_____X ,
       X_______X ,
       X_______X ,
       X_______X    };



   static final byte[][]  i =
         new byte[][]{
       _________ ,
       ____X____ ,
       _________ ,
       __X_X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       __X_X_X__    };



   static final byte[][]  j =
         new byte[][]{
       ______X__ ,
       _________ ,
       ____X_X__ ,
       ______X__ ,
       ______X__ ,
       ______X__ ,
       X_____X__ ,
       __X_X____    };



   static final byte[][]  k =
         new byte[][]{
       _________ ,
       X________ ,
       X_______X ,
       X_____X__ ,
       X___X____ ,
       X_X______ ,
       X___X____ ,
       X_____X__    };



   static final byte[][]  l =
         new byte[][]{
       __X_X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       __X_X_X__    };



   static final byte[][]  m =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_X___X__ ,
       X___X___X ,
       X___X___X ,
       X_______X ,
       X_______X    };



   static final byte[][]  n =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
      {X,µ,X,X,µ},
       X_X_____X ,
       X_______X ,
       X_______X ,
       X_______X    };



   static final byte[][]  o =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       X_______X ,
       __X_X_X__    };



   static final byte[][]  p =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_X_X_X__ ,
       X_______X ,
       X_X_X_X__ ,
       X________ ,
       X________    };



   static final byte[][]  q =
         new byte[][]{
       _________ ,
       _________ ,
       ________X ,
       __X_X_X_X ,
       X_______X ,
       __X_X_X_X ,
       ________X ,
       ________X    };



   static final byte[][]  r =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
      {X,µ,X,X,µ},
       X_X_____X ,
       X________ ,
       X________ ,
       X________    };



   static final byte[][]  s =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       __X_X_X__ ,
       X________ ,
       __X_X_X__ ,
       ________X ,
       X_X_X_X__    };



   static final byte[][]  t =
         new byte[][]{
       _________ ,
       __X______ ,
       __X______ ,
       X_X_X____ ,
       __X______ ,
       __X______ ,
       __X_____X ,
       ____X_X__    };



   static final byte[][]  u =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_____X_X ,
       __X_X___X    };



   static final byte[][]  v =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_______X ,
       X_______X ,
       X_______X ,
       __X___X__ ,
       ____X____    };



   static final byte[][]  w =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_______X ,
       X_______X ,
       X___X___X ,
       X___X___X ,
       __X___X__    };



   static final byte[][]  x =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_______X ,
       __X___X__ ,
       ____X____ ,
       __X___X__ ,
       X_______X    };



   static final byte[][]  y =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_______X ,
       X_______X ,
       __X_X_X_X ,
       ________X ,
       __X_X_X__    };



   static final byte[][]  z =
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_X_X_X_X ,
       ______X__ ,
       ____X____ ,
       __X______ ,
       X_X_X_X_X    };

   static final byte[][] OCUBRK = 
         new byte[][]{
       _________ ,
       ______X__ ,
       ____X____ ,
       ____X____ ,
       X_X______ ,
       ____X____ ,
       ____X____ ,
       ______X__    };

   static final byte[][] VLINE =
         new byte[][]{
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____    };

   static final byte[][] CCUBRK =
         new byte[][]{
       _________ ,
       __X______ ,
       ____X____ ,
       ____X____ ,
       ______X_X ,
       ____X____ ,
       ____X____ ,
       __X______    };

   static final byte[][] AC =
         new byte[][]{
       _________ ,
       _________ ,
       __X______ ,
       X___X___X ,
       ______X__    };


 static final byte[][] BLOCK = 
         new byte[][]{
       X_X_X_X_X ,
       X_X_X_X_X ,
       X_X_X_X_X ,
       X_X_X_X_X ,
       X_X_X_X_X ,
       X_X_X_X_X ,
       X_X_X_X_X ,
       X_X_X_X_X        };


// End of  20H to 7E ---------------------------


   static final byte[][]  cent =
         new byte[][]{
       _________ ,
       ________X ,
       __X_X_X__ ,
       X_____X_X ,
       X___X____ ,
       X_X_____X ,
       __X_X_X__ ,
       X________    };


   static final byte[][]  POUND =
         new byte[][]{
       X_X_X____ ,
       X_____X__ ,
       __X______ ,
       X_X_X____ ,
       __X______ ,
       __X______ ,
       X________ ,
       X_X_X_X_X    };     



static final byte[][] RARROW =
         new byte[][]{
       _________ ,
       ____X____ ,
       ______X__ ,
       X_X_X_X_X ,
       ______X__ ,
       ____X____   };

static final byte[][] LARROW =
         new byte[][]{
       _________ ,
       ____X____ ,
       __X______ ,
       X_X_X_X_X ,
       __X______ ,
       ____X____    };


static final byte[][] alpha = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       __X_____X ,
       X___X___X ,
       X_____X__ ,
       X_____X__ ,
       __X_X___X        };

static final byte[][] ä = 
         new byte[][]{
       _________ ,
       __X___X__ ,
       _________ ,
       __X_X_X__ ,
       ________X ,
       __X_X_X_X ,
       X_______X ,
       __X_X_X_X    };


   static final byte[][] Ä = 
         new byte[][]{
       __X___X__ ,
       _________ ,
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       X_X_X_X_X ,
       X_______X ,
       X_______X    };


static final byte[][] betha = 
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X_X_X_X__ ,
       X_______X ,
       X_X_X_X__ ,
       X________ ,
       X________ ,
       X________        };

static final byte[][] epsilon = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       __X_X_X__ ,
       X________ ,
       __X_X____ ,
       X_______X ,
       __X_X_X__        };

static final byte[][] my = 
         new byte[][]{
       _________ ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_____X_X ,
      {X,X,X,µ,X},
       X________ ,
       X________        };

static final byte[][] sigma = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       __X_X_X_X ,
       X___X____ ,
       X_____X__ ,
       X_______X ,
       __X_X_X__        };

static final byte[][] ro = 
         new byte[][]{
       ____X_X__ ,
       __X_____X ,
       X_______X ,
       X_______X ,
       X_X_X_X__ ,
       X________ ,
       X________ ,
       X________        };


static final byte[][] ROOT =  // x221a
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       ____X_X_X ,
       ____X____ ,
       ____X____ ,
       X___X____ ,
       __X______        };


static final byte[][]  ö =
         new byte[][]{
       _________ ,
       __X___X__ ,
       _________ ,
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       X_______X ,
       __X_X_X__    };

static final byte[][]  Ö =
         new byte[][]{
       __X___X__ ,
       _________ ,
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       __X_X_X__    };


   static final byte[][]  GAMMA =
         new byte[][]{
       X_X_X_X_X ,
       X_______X ,
       X________ ,
       X________ ,
       X________ ,
       X________ ,
       X________ ,
       X________     };


   static final byte[][]  DELTA =
         new byte[][]{
       _________ ,
       _________ ,
       ____X____ ,
       __X___X__ ,
       __X___X__ ,
       X_______X ,
       X_______X ,
       X_X_X_X_X     };


   static final byte[][]  THETA =
         new byte[][]{
       __X_X_X__ ,
       X_______X ,
       X_X___X_X ,
       X_X_X_X_X ,
       X_X___X_X ,
       X_______X ,
       X_______X ,
       __X_X_X__    }; 

   static final byte[][]  PHI =
         new byte[][]{
       ____X____ ,
       __X_X_X__ ,
       X___X___X ,
       X___X___X ,
       X___X___X ,
       X___X___X ,
       __X_X_X__ ,
       ____X____   }; 


   static final byte[][]  LAMDA =
         new byte[][]{
       _________ ,
       _________ ,
       ____X____ ,
       __X___X__ ,
       __X___X__ ,
       X_______X ,
       X_______X ,
       X_______X     };
  


 static final byte[][] theta = 
         new byte[][]{
       _________ ,
       _________ ,
       __X_X_X__ ,
       X_______X ,
       X_X_X_X_X ,
       X_______X ,
       X_______X ,
       __X_X_X__        };

 static final byte[][] INFINITE = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       __X___X_X ,
       X___X___X ,
       X_X___X__       };

 static final byte[][] OMEGA = 
         new byte[][]{
       _________ ,
       _________ ,
       __X_X_X__ ,
       X_______X ,
       X_______X ,
       X_______X ,
       __X___X__ ,
       X_X___X_X        };

 static final byte[][]  ü =
         new byte[][]{
       _________ ,
       __X___X__ ,
       _________ ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_____X_X ,
       __X_X___X    };


   static final byte[][]  Ü =
         new byte[][]{
       __X___X__ ,
       _________ ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       X_______X ,
       __X_X_X__    };   



 static final byte[][] SIGMA = 
         new byte[][]{
       _________ ,
       X_X_X_X_X ,
       X________ ,
       __X______ ,
       ____X____ ,
       __X______ ,
       X________ ,
       X_X_X_X_X        };

 static final byte[][] pi = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       X_X_X_X_X ,
       __X___X__ ,
       __X___X__ ,
       __X___X__ ,
       X_____X_X        };

 static final byte[][] PI = 
         new byte[][]{
       _________ ,
       X_X_X_X_X ,
       __X_____X ,
       __X_____X ,
       __X_____X ,
       __X_____X ,
       __X_____X ,
       X_X_____X        };

   static final byte[][]  ESSZETT =
         new byte[][]{
       __X_X____ ,
       __X___X__ ,
       __X_X____ ,
       __X___X__ ,
       __X_____X ,
       __X___X_X ,
       __X______ ,
       __X______    };



   static final byte[][]  COPY =
         new byte[][]{
       _________ ,
       _________ ,
       __X_X_X__ ,
       X_X_____X ,
       X___X_X_X ,
       X_X_____X ,
       __X_X_X__    }; 

 
 static final byte[][] YEN = 
         new byte[][]{
       _________ ,
       X_______X ,
       __X___X__ ,
       X_X_X_X_X ,
       ____X____ ,
       X_X_X_X_X ,
       ____X____ ,
       ____X____        };
 

/** Pattern for inverted exclamation. <br /> */
   static final byte[][] IEXCL = 
         new byte[][]{
       ____X____ ,
       _________ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____    };

   static final byte[][] IQUEST = 
         new byte[][]{
       ____X____ ,
       _________ ,
       ____X____ ,
       ____X____ ,
       ______X__ ,
       X_______X ,
       ________X ,
       __X_X_X__    };

   static final byte[][] BVLIN =
         new byte[][]{
       ____X____ ,
       ____X____ ,
       ____X____ ,
       _________ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       ____X____    };


   static final byte[][]  iuml =
         new byte[][]{
       _________ ,
       X_____X__ ,
       _________ ,
       __X_X____ ,
       ____X____ ,
       ____X____ ,
       ____X____ ,
       __X_X_X__    };


   static final byte[][]  yuml =
         new byte[][]{
       _________ ,
       __X___X__ ,
       _________ ,
       X_______X ,
       X_______X ,
       __X_X_X_X ,
       ________X ,
       __X_X_X__    };




   static final byte[][] LCROSS =  // D7  
         new byte[][]{
       _________ ,
       _________ ,
       __X___X__ ,
       ____X____ ,
       __X___X__   };

   static final byte[][] ECROSS =  
         new byte[][]{
       _________ ,
       _________ ,
       __X_____X ,
       ____X_X__ ,
       __X_____X ,
       ____X_X__ ,
       __X_____X    };
   
   static final byte[][] EURO =   // 0x20ac;
      new byte[][]{
       _________ ,
       ____X_X_X ,
       __X______ ,
       X_X_X_X__ ,
       __X______ ,
       X_X_X____ ,
       __X_____X ,
       ____X_X__    };


   static final byte[][] grad =  
         new byte[][]{
       ____X_X_X ,
       ____X___X ,
       ____X___X ,
       ____X_X_X   };

   static final byte[][] hoch0 =  
         new byte[][]{
       ____X_X__ ,
       __X_____X ,
       __X_____X ,
       __X_____X ,
       ____X_X__   };

   static final byte[][] hoch1 =   
         new byte[][]{
       ________X ,
       ______X_X ,
       ____X___X ,
       ________X ,
       ________X   };

   static final byte[][] hoch2 =   
         new byte[][]{
       ____X_X__ ,
       ________X ,
       ______X__ ,
       ____X____ ,
       ____X_X_X   };

   static final byte[][] hoch3 =   
         new byte[][]{
       ____X_X_X ,
       ________X ,
       ______X__ ,
       ________X ,
       ____X_X__   };


   static final byte[][] MIDDOT = 
         new byte[][]{
       _________ ,
       _________ ,
       _________ ,
       __X_X____ ,
       __X_X____    };

   static final byte[][] DIV = 
         new byte[][]{
    _________ ,
    _________ ,
    _________ ,
    ____X____ ,
    X_X_X_X_X ,
    ____X____        };


   static final byte[][] SDASH = 
         new byte[][]{
    _________ ,
    _________ ,
    _________ ,
    _________ ,
    __X_X_X__       };


   static final byte[][] NOT = 
         new byte[][]{
    _________ ,
    _________ ,
    _________ ,
    _________ ,
    X_X_X_X_X ,
    ________X ,
    ________X        };


/** Pattern for +/- . <br /> */
   static final byte[][] PLUSMINUS = 
         new byte[][]{
    _________ ,
    ____X____ ,
    ____X____ ,
    X_X_X_X_X ,
    ____X____ ,
    ____X____ ,
    X_X_X_X_X        };

/** Pattern for OVERLINE. <br /> */
   static final byte[][] OVERLINE  = 
         new byte[][]{
    X_X_X_X_X        };


/** Pattern table [00..FF]. <br />*/
   static final byte[][][] MUST_TAB = {

// 00.1F ISO8859-1 control codes  (will be blank)
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, 
      UNDEF, SPACE, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, 
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, 
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, 

      SPACE, EXCL, DQUOT, HASH, DOLLAR, PERC, AMP, SQUOT,
      OBRK,  CBRK, STAR,  PLUS, COMMA, MINUS, DOT, SLASH,

      M0, M1, M2, M3, M4, M5, M6, M7, 
      M8, M9, COLON, SEMICOLON, LT, EQUALS, GT, QUEST,

      AT, 
      A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, 
          R, S, T, U, V, W, MX, Y, Z, 
          OSQBRK, BACKSLASH, CSQBRK, GRAVE, UNDERLINE,

      SQUOT2,  
      a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, 
          r, s, t, u, v, w, x, y, z, 
          OCUBRK, VLINE, CCUBRK, AC , BLOCK,

// 80..9F ISO8859-1 not used
      UNDEF,  UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, 
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, 
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, 
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, 

// A0..BF ISO8859-1 Space to wrong question mark (not complete)
      SPACE, IEXCL,  cent, POUND, ECROSS,  YEN, BVLIN, UNDEF, 
      UNDEF,  COPY, UNDEF, UNDEF,   NOT, SDASH, UNDEF, OVERLINE, 
       grad, PLUSMINUS, hoch2, hoch3,UNDEF, my, UNDEF, MIDDOT, 
      UNDEF, hoch1, hoch0, UNDEF, UNDEF, UNDEF, UNDEF, IQUEST, 

// C0..DF ISO8859-1 Aacc to ß (not complete)
      UNDEF, UNDEF, UNDEF, UNDEF,     Ä, UNDEF, UNDEF, UNDEF, 
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, 
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF,     Ö, LCROSS, 
      UNDEF, UNDEF, UNDEF, UNDEF,     Ü, UNDEF, UNDEF, ESSZETT, 

// E0..FF ISO8859-1 a acc to yUml (not complete)
      UNDEF, UNDEF, UNDEF, UNDEF,     ä, UNDEF, UNDEF, UNDEF, 
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF,  iuml, 
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF,     ö,   DIV, 
      UNDEF, UNDEF, UNDEF, UNDEF,     ü, UNDEF, UNDEF,  yuml 

   }; // MUST_TAB

   static final int MUST_TAB_LEN = MUST_TAB.length;


/** Pattern table [0390..03CF] . UNICODE Greek. <br /> */
   static final byte[][][] GREEK_TAB = {

// 0390..03AF Upper case Greek letters ()
      UNDEF,     A,     B, GAMMA, DELTA,     E,     Z,     H, 
      THETA,     I,     K, LAMDA,     M,     N, UNDEF,     O, 
         PI,     P, UNDEF, SIGMA,     T,     Y,   PHI,    MX, 
      UNDEF, OMEGA, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, 


// 03B0..03CF Lower case Greek letters ()
      UNDEF, UNDEF, betha, UNDEF, UNDEF, epsilon, UNDEF, UNDEF, 
      theta, UNDEF, UNDEF, UNDEF,    my, UNDEF, UNDEF, UNDEF, 
         pi,    ro, UNDEF, sigma, UNDEF, UNDEF, UNDEF, UNDEF, 
      UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF, UNDEF 

   }; // GREEK_TAB

   static final int GREEK_TAB_LEN = GREEK_TAB.length;
   static final int GREEK_TAB_BEG = 0x0390;
   static final int GREEK_TAB_END = GREEK_TAB_BEG + GREEK_TAB_LEN;


/** Get the patter by character (code). <br />
 *  <br />
 *  Returned is a &quot;two dimensional&quot; 8 * 5 (or shorter; see hint)
 *  array of byte for the character<br />
 *  <br />
 *  As far / as good as displayable as 5 * 8 dots, pattern for character codes 
 *  00 to FF (according to Unicode  or ISO8859-1 in this range) are available
 *  as well as some Greek letters (Unicode 0390 to 03CF) and some extra
 *  characters as the Euro sign (not quite good in 5 * 8), for example.<br />
 *  <br />
 *  Unimplemented or non displayable characters (as control characters) return
 *  {@link #UNDEF}, that as {@link #SPACE} would be displayed as blank, 
 *  according the substitution rules..<br />
 *  <br />
 *  <u>Substitution rules</u><br />
 *  <br />
 *  <ol><li>Lines that end in one or more zeros ((byte) 0) for off may be
 *      shortened.</li>
 *  <li>Instead of a line, containing just five zeros ((byte) 0) may stand
 *      a null (byte[])null).</li>
 *  <li>If the pattern ends in one or more such empty or blank lines, the
 *     line array (first index) might be shortened accordingly, down to
 *     length zero but not be (byte[][])null.</li></ol>
 *  
 *  Warning: This method returns references to the internal pattern arrays of
 *  type byte[] and byte[][]. Defensive copies would be an immense waste in 
 *  the highly dynamic display applications. Do not modify!.<br />
 *  If modifications are necessary (or possible) use {@link #get(char, byte)}
 *  instead, which returns a new (full) 5 * 8 byte[][] dot array
 *  every time.<br />
 *  <br />
 *  @param c  the character (Unicode number)
 *  @return the pattern or {@link #UNDEF}
 */
   public static byte[][] get(char c){
      if (c < MUST_TAB_LEN) 
         return MUST_TAB[c]; 
      if (c >=  GREEK_TAB_BEG && c < GREEK_TAB_END) 
         return GREEK_TAB[c-GREEK_TAB_BEG];
      if (c == 0x20ac) return EURO; 
      if (c == 0x221a) return  ROOT;
      return UNDEF;
   } // get(char)


/** Fetch the pattern for a character. <br />
 *  <br />
 *  Returned is a new &quot;two dimensional&quot; 8 * 5 byte array for the 
 *  character c.<br />
 *  <br />
 *  Matrix dots to be on do display the given character, are set to the 
 *  {@code onValue} if that is in the range 2..127 or 1. Off dots get the 
 *  value 0.<br />
 *  <br />
 *  Hint: This method returns (every time) a new array. This may be modified
 *  opposed to the array returned by {@link #get(char c)}, which has quite
 *  similar results as {@link #get(char, byte) get(c, 1)}. Both methods
 *  have the same parameter range for c.<br />
 *  <br />
 */
   public static byte[][] get(final char c, byte onValue){
      if (onValue < 2) onValue = 1;
      byte[][] ret = new byte[8][5];
      byte[][] must = get(c);
      if (must == null) return ret;

      byte[] line = null;
      byte[] retL = null;

      liLoop: for (int i = 0; i < must.length; ++i) { 
         line = must[i];
         if (line == null) continue liLoop;
         retL = ret[i]; 
         for (int k = 0; k < line.length; ++k) {
             if (line[k] == 1) retL[k] = onValue;
         } // colLopp
      } // liLoop
     return ret;
  } // byte[][] get(char c, byte anWert)


/** Output all defined character pattern. <br />
 *  <br />
 *  The output is just on normal output (System.out) and may only by &gt; 
 *  written to a File.<br />
 */
  static public void main(String[] a){
     int mc = 0;
     mu = 8;
     StringBuilder hdl = new StringBuilder(2); // to suppress warning 
     StringBuilder[] lin = new StringBuilder[8];
     loop: while(true) {
        if (mc == 0 && mu == 0) break;
        if (mc  > 0 ||  mu == 0) {
           System.out.println("\n" + hdl + "\n");
           for (int z = 0; z < 8; ++z)
              System.out.println( lin[z] );
           System.out.println();
           if (mu == 0) break loop;
           mc = 0;
        }
        if (mc == 0) {
           hdl = new StringBuilder(88);
           for (int z = 0; z < 8; ++z)
              lin[z] = new StringBuilder(88);
        }     
        muLoop: for (; mu !=0 ; nextMu()) {
           byte[][] must = get(mu);
           if (must == UNDEF) continue muLoop;
           while (hdl.length() < mc * 13 + 3) hdl.append(' ');
           hdl.append("Pattern " + Integer.toHexString(mu));
           must = get(mu, (byte)1);
           for (int z = 0; z < 8; ++z) {
              lin[z].append("    ");
              for (int s = 0; s < 5; ++s) {
                 lin[z].append(must[z][s] == 0 ? " " : "*");
              }
              lin[z].append("   .");           
           }
           ++mc;
           if (mc > 5) { nextMu(); break muLoop; }  
        } // muLoop
     } // loop
  } // main(String[])
  
  // enumerator over   normal tab 8...  ;  Euro ;   Greek tab ...
  static char mu;
  private static void nextMu(){
     // fix or switch points
     if (mu == 0) return;
     if (mu == 0x20ac) {
        mu = 0x221a; // Euro to Square root
        return;
     }
     if (mu == 0x221a) {
        mu = 0; // root to end
        return;
     }
     // assume within range and check end of ranges
     ++mu;
     if (mu == GREEK_TAB_END) {
        mu = 0x20ac; // EURO;
        return;
     } 
     if (mu == MUST_TAB_LEN) {
        mu = GREEK_TAB_BEG; // base table to Greek
        return;
     }
   }
} // class DisplayPattern (DisplayMuster 12.10.2003) (12.02.2009)
