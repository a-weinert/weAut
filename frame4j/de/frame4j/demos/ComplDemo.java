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
package de.frame4j.demos;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import de.frame4j.graf.WeAutLogo;
import de.frame4j.math.Complex;


/** <b>Demonstration of complex arithmetics</b>. <br />
 *  <br />
 *  The operations +, -, *, /, exp(), log() on complex numbers as well as many
 *  other operations can be demonstrated graphically.<br />
 *  <br />
 *  After Rev. 159 in a previous 2016 repository, ComplDemo is no longer an
 *  Applet due to Oracle's decision to kill Applets. It will still run as a 
 *  stand-alone application as long as Frame4J (frame4j.jar) is installed in
 *  Java &lt;=8. It can be made available by WebStart. Start as application:
 *<pre>                java de.frame4j.demos.ComplDemo
 * or              javaw de.frame4j.demos.ComplDemo </pre>
 *  <br />  
 *  <br />
 *  <a href="../package-summary.html#co">&copy;</a>
 *  Copyright 1999 - 2005, 2016 &nbsp; Albrecht Weinert <br /> 
 *  <br />
 *  @see de.frame4j.math.Complex
 *  @see de.frame4j.math.CFun
 *  @see java.beans.Beans
 *
 *  @author   Albrecht Weinert
 *  @version  $Revision: 40 $ ($Date: 2021-04-19 21:47:30 +0200 (Mo, 19 Apr 2021) $)
 */
 // so far:  V00.00 (17.08.1999) :  Applet only
 //          V00.01 (15.12.1999) :  application, too
 //          V00.02 (17.12.1999) :  Vector "arrow", exp not binary
 //          V01.01 (18.05.2000) :  Complex new and CFun
 //          V01.04 (03.05.2001) :  +,-,* using now Complex class too
 //          V02.23 (13.06.2005) :  /**, paint sequence by length
 //          V.125  (14.04.2015) :  moved to ..demos, application as Panel
 //          V.134+ (08.03.2016) :  Applet possibilities reduced (in comments)
 //          V.159+ (16.08.2016) :  Applet variant finally killed, since Java9

public class ComplDemo extends Panel implements MouseMotionListener,
                           MouseListener,  ActionListener,  ItemListener, 
                                               ComponentListener, Runnable {  

/** Title for use as graphical application. <br /> */
   public static final String TITLE     = 
                          " Demo: Complex arithmetics    V.$Revision: 40 $"; 

/** Information: author. <br /> */
   public static final String AUTHOR    = "Author:   Albrecht Weinert"; 

/** Information: class file name. <br /> */
   public static final String CLASS_F   = "Class:  ComplDemo.class";

/** Informations: version and date. <br /> */
   public static final String VERSION   = 
                   "Version: V.$Revision: 40 $ (14.12.2009)";

/** Information: copyright hint. <br /> */
   public static final String COPYRIGHT = 
                  "Copyright (c) 1999 - 2009, 2015   Albrecht Weinert";

   static final int    CHOICEGAP = 30;

   private Thread anim;
   private boolean arithChanged;

   int width, height;       // Frame size
   int scrWidth, scrHeight; // Picture size 
   int y00, x00;            // Pixel coordinates of 0,0 relative to leUp
   int unityPix, piPix;     // 1.0 and PI in pixels

   Image imgBg;  // background picture (Coordinates, axis denominations) 
   Choice opChooser;
   Button arDisplay;
   int    arInd;

   String  opString  = " + ";
   boolean dragOp2   = false;
   int     opIndex   = 1;

   static final int     LAST_OP_BIN =  4; // see init() opChooser.add

/** Last operator respectively function index. <br />
 *  <br />
 *  The operations are numbered from 1: + to 19: exp() .<br />
 *  <br />
 *  @see #selectOp
 */
   static public final int LAST_OP  = 19; // see init() opChooser.add

/** A circle of radius pi will be shown. <br />
 *  <br />
 *  default: true
 */
   public boolean showPi    = true;

/**  A circle of radius 1 will be shown.  <br />
 *  <br />
 *  default: true
 */
   public boolean showUnit  = true;
   

/** Constructor as Panel respectively Application. <br /> */     
   public ComplDemo(boolean showPi, boolean showUnit){
      this.showPi = showPi;
      this.showUnit = showUnit;
      init();
   } // constructor as Panel

/** One time initialisations. <br />
 *  <br />
 *  @see #main(String[])
 */   
   public void init(){
      imgInit(); // make the background picture

      setBackground (new Color(0x0F0,0x0F0,0x0F0));
      Font fieldFont = new Font ("Monospaced", Font.PLAIN,  11);
      arDisplay = new Button(
              " (1.00 +j1.00)   +   (-1.00 + j1.00)  =  (0.00 +j2.00) ");
      arDisplay.setFont(fieldFont);
      arDisplay.setBackground(Color.white);
      arDisplay.addActionListener(this);
      add (arDisplay);

      opChooser = new Choice();
      opChooser.setFont(fieldFont); 
      opChooser.add(" op ");  //  0
      opChooser.add(" + ");   //  1
      opChooser.add(" - ");   //  2
      opChooser.add(" * ");   //  3
      opChooser.add(" / ");   //  4 (4 = last binary operator)
      opChooser.add("exp");   //  5  
      opChooser.add(" re ");  //  6 
      opChooser.add(" im ");  //  7
      opChooser.add("conj");  //  8
      opChooser.add("neg");   //  9
      opChooser.add("abs");   // 10
      opChooser.add("arg");   // 11
      opChooser.add("norm");  // 12
      opChooser.add("sin");   // 13
      opChooser.add("cos");   // 14
      opChooser.add("sinh");  // 15
      opChooser.add("cosh");  // 16
      opChooser.add("tan");   // 17
      opChooser.add("tanh");  // 18
      opChooser.add("log");   // 19  (19 = last unary operator)
      ///  lastOp = 19;
      opChooser.add(" \\/ ");   // > lastOp
      opChooser.select(0); 
      opChooser.addItemListener(this);

      add(opChooser);
      addMouseMotionListener(this);
      addMouseListener(this);
      addComponentListener(this);
      anim = new Thread(this);
      anim.setDaemon(true);
      anim.start();
   } // init()

/** Working method of the background thread. <br /> */
   @Override public void run(){
      long browswait = 400;
      while (anim != null) {
         try {
            Thread.sleep(279);
         } catch (Exception e){}
            if (arithChanged) showArith();
         try {
            Thread.sleep(browswait); // wait for int
         } catch (Exception e){
            browswait = 30000; // workaround for some browsers
            // e.g. Netscape 4.07 doesn't interrupt Threads (Why ???)
         }
      } // (in)finite loop
   } // run()

/** Initialising the background picture. <br />
 *  <br />
 *  Hint: In run as application this method must be called conditionally on
 *  several spots.
 */
   public void imgInit(){  // Initialising the background picture

      Font axisFont   = new Font ("Serif", Font.PLAIN,  13 );
      Dimension panelDim = getSize();
      width = panelDim.width;
      height= panelDim.height;
      x00 = width/2;
      y00 = height/2 + CHOICEGAP/2;

      scrWidth  = width  - 10;
      scrHeight = height - CHOICEGAP - 10;
      
      int xHalfAx = scrWidth  / 2 - 6;
      int yHalfAx = scrHeight / 2 - 6;
      int smallHalfAx = xHalfAx > yHalfAx ? yHalfAx : xHalfAx;
      unityPix = smallHalfAx / 4  - 2;
      piPix = unityPix - 48;
      if (piPix > 2) unityPix -= piPix / 2;
       
      double xRange  =  (double)xHalfAx / (double)unityPix;
      int    xiRange =  (int) xRange;
      double yRange  =  (double)yHalfAx / (double)unityPix;
      int    yiRange =  (int) yRange;
      piPix   =  (int) (Math.PI * unityPix);
      initOps();
      
      Image imgPBg = createImage(scrWidth, scrHeight);
      if (imgPBg == null) return; 
      Graphics g = imgPBg.getGraphics();
      g.setColor(Color.white);
      g.fillRect(0,0,scrWidth,scrHeight);
      g.setColor(Color.black);
      g.drawRect(0,0,scrWidth-1,scrHeight-1);
      g.translate(scrWidth/2, scrHeight/2);
      g.setColor ( Color.yellow);
      if (showPi)  g.drawOval (-piPix,-piPix,2*piPix,2*piPix);

      for (int i =  -xiRange ; i <= xiRange ; ++i) {
        int xt = i * unityPix;
        if (i!=0) g.drawLine (xt, -yHalfAx, xt, yHalfAx);
      }

      for (int i =  -yiRange ; i <= yiRange ; ++i) {
        int yt = i * unityPix;
        if (i!=0)  g.drawLine (-xHalfAx, yt , xHalfAx-1, yt);
      }
      g.setColor ( Color.black);
      g.drawLine (  0, -yHalfAx,  0,  yHalfAx);  // y-Axis
      g.drawLine (  0, -yHalfAx,  2,6-yHalfAx);  // arrow
      g.drawLine (  0, -yHalfAx, -2,6-yHalfAx);  // arrow
      g.drawLine (-xHalfAx,  0 , xHalfAx-1, 0);  // x-Axis
      g.drawLine (xHalfAx-6, 2 , xHalfAx-1, 0);  // arrow
      g.drawLine (xHalfAx-6,-2 , xHalfAx-1, 0);  // arrow
      if (showUnit)
         g.drawOval (-unityPix,-unityPix,2*unityPix,2*unityPix);
      g.setFont(axisFont);
      g.drawString("1",   unityPix+7,    12);
      g.drawString("Pi",  piPix+5,       14);
      g.drawString("re",  xHalfAx-12,    12);
      g.drawString("im",  12,            12 - yHalfAx );

      g.setColor ( Color.blue);
      g.drawString("op1", 12, yHalfAx-4);
      g.setColor ( Color.green);
      g.drawString("op2", 40, yHalfAx-4);
      g.setColor ( Color.red);
      g.drawString("result", 68, yHalfAx-4);

      imgBg = imgPBg;
      imgPBg = null;
      g = null;   
   } // imgInit

//---------------------------------------------------------------------

   static final byte UNKNOWN    = 0; // normally for result only
   static final byte CONSISTENT = 1;
   static final byte PIX_CHANGE = 2;
   static final byte CPL_CHANGE = 4;

//=====================================================================   
   
/** Wrapper for Complex with pixel coordinates and comparison. <br />
 *  <br />
 *  An object of this class wraps a {@link Complex} object, adapts it to the
 *  interface {@link Comparable} and adds pixel coordinates and painting in
 *  a specified colour<br />
 *  <br />
 *  Hint: A {@link de.frame4j.demos.ComplDemo ComplDemo} object makes just 
 *  three such wrappers: two for operands and one for the result.<br />
 *  <br />
 *  @version see enclosing class
 *  @author Albrecht Weinert  
 */    
   public class ComplexWrapper implements Comparable<ComplexWrapper> {
      
/** The complex number wrapped. <br />
 *  <br />
 *  Must never be null. <br />
 */      
      public Complex cp;

/** Painting colour for the complex vector. <br />
 *  <br />
 *  Must never be null. <br />
 */      
      public Color color;
      
/** Pixel coordinates. <br /> */
      public int xp, yp;
      
/** Pixel norm. <br />
 *  <br />
 *  I.e the square of the length. <br />
 */  
       public int lp;
      
/** State. <br />
 *  <br />
 *  Consistency between {@link #cp} and ({@link #xp}, {@link #yp}}.<br /> 
 */      
      public byte stat;
      
/** Do not paint. <br />
 *  <br />
 *  If true, {@link #doThePaint(Graphics)} does nothing. <br />
 */      
      public boolean noShow;

/** Make a wrapper. <br />
 *  <br />
 *  @param color the colour for painting the vector (never null!).
 */      
      public ComplexWrapper(Color color){
         this.color = color;
         this.cp = Complex.ZERO;
      } // ComplexWrapper(Color)
      
/** Set the Complex. <br /> 
 *  <br />
 *  @param cp the complex number (object) to be wrapped
 * */
      public void set(Complex cp){
         if (this.cp == cp) return;
         this.cp = cp;
         stat = CPL_CHANGE;
      } // set(Complex) 

/** Set by real and imaginary part. <br /> 
 *  <br />
 *  @param re the real part of the complex number (object) to be wrapped
 *  @param im its imaginary part
 */
      public void set(double re, double im){  set(Complex.make(re, im)); }


/** Set by pixel coordinates  x, y. <br /> 
 *  <br />
 *  @param xp the x-coordinate in pixel units
 *  @param yp the y-coordinate 
 */
      public void set(int xp, int yp){
         if (this.xp == xp && this.yp == yp) return;
         this.xp = xp;
         this.yp = yp;
         stat = PIX_CHANGE;
      } // set(2*int)

      
/** Make pixel and complex values consistent. <br /> */
      public void consist(){
         if (stat == CONSISTENT) return;
         if (stat == CPL_CHANGE) { // Complex geändert
            stat = CONSISTENT;
            switch (cp.h) {
               case 0 : // NaN NaN is shown as index finger 0 gezeigt
               case 9 : // 0 
                  yp = xp = lp = 0;
                  return;
               case 1 : // INF0
                  xp = width; // longer than picture
                  yp = 0;
                  lp = 90000;
                  return;
               case 2 : // INF45
                  xp = width; // longer than picture
                  yp = -width; // longer than picture
                  lp = 99000;
                  return;
               case 3 : // INF90
                  xp = 0;
                  yp = -width; // longer than picture
                  lp = 90000;
                  return;
               case 4 : // INF135
                  xp = -width; // longer than picture
                  yp = -width; // longer than picture
                  lp = 99000;
                  return;
               case 5 : // INF180
                  xp = -width; // longer than picture
                  yp = 0;
                  lp = 90000;
                  return;
               case 6 : // INF225
                  xp = -width; // longer than picture
                  yp = -width; // longer than picture
                  lp = 99000;
                  return;
               case 7 : // INF270
                  xp = 0;
                  yp = lp = width; // longer than picture
                  lp = 90000;
                  return;
               case 8 : // INF315
                  xp = width; // longer than picture
                  yp = width; // longer than picture
                  break;
               case 10 : // ONE
                  xp = unityPix; 
                  yp = 0;
                  lp = unityPix * unityPix;
                  return;
               case 11 : // I, J
                  xp = 0; 
                  yp = -unityPix;
                  lp = unityPix * unityPix;
                  return;
               case 12 : // M_ONE
                  xp = -unityPix; 
                  yp = 0;
                  lp = unityPix * unityPix;
                  return;
               case 13 : // M_I, M_J
                  xp = 0; 
                  yp = unityPix;
                  lp = unityPix * unityPix;
                  return;
               default: // endlich, nicht 0, nicht NaN
                  xp = (int)(cp.re * unityPix);
                  yp = -(int)(cp.im * unityPix);
                  break;
            } // switch hash code
            lp = xp * xp + yp * yp;
            return; // Complex geändert
         } // Complex changed
         // Pixel changed
         cp = Complex.make((float)xp / unityPix, -(float)yp / unityPix);
         lp = xp * xp + yp * yp;
         stat = CONSISTENT;
      } // consist()
         
      
/** Compare the absolute values. <br />
 *  <br />
 *  {@link Comparable} implementation.<br />
 *  @param other the other wrapped number to compare with (not null!)
 *  @return 0: equal; -: this ({@link #lp}) smaller than other's; +: greater
 */ 
      @Override public int compareTo(final ComplexWrapper other){
         if (other == this) return 0;
         if (other.stat != CONSISTENT) other.consist();
         if (this.stat != CONSISTENT) this.consist();
         return this.lp - other.lp;
      } //  compareTo(Object other)
      

/** Paint as vector. <br />
 *  <br />
 *  This method requires g.translate(); that means the Zero of  {@code g}
 *  must be the coordinates origin in the complex plane.<br />
 *  <br />
 *  If g is null or {@link #noShow} is true, nothing will be done.<br />
 *  <br />
 *  @param g the graphic environment to paint on
 */
      public void doThePaint(Graphics g){
         if (noShow || g == null) return;
         g.setColor(color);
         int x = xp;
         int y = yp;
         g.drawLine (0, 0,  x,     y);
         g.drawLine (x, y,  ++x, ++y);
         g.drawLine (x, y,  x,  y-=2);
         g.drawLine (x, y,  x-=2,  y);
         g.drawLine (x, y,  x,  y+=2);
         g.drawLine (x, y,  x+=2,  y);
      } // doThePaint(Graphics)
      
      
   } // Wrapper class  =================================================

   
/** Operand 1 . <br /> */
   ComplexWrapper op1 = new ComplexWrapper(Color.blue);

/** Operand 2 . <br /> */
   ComplexWrapper op2 = new ComplexWrapper(Color.green);

/** The result . <br /> */
   ComplexWrapper erg = new ComplexWrapper(Color.red);
    
/** All the three (operands and result) combined. <br /> */
   ComplexWrapper[]  all3 = new ComplexWrapper[]{op1, op2, erg};
      

/** Set the operands to visible default values. <br />
 *  <br />
 *  op1 = (1, -1i) <br />
 *  op2 = (-1, -1i)
 */
   public void initOps(){
      op1.set( unityPix, -unityPix);
      op2.set(-unityPix, -unityPix);
      erg.set(0,         -unityPix -unityPix);
      erg.stat = UNKNOWN;
   } // initOps()

   
/** Make the operands and result consistent (pixel to Complex). <br />
 *  <br />
 *  This method makes the operands consistent after changes of the Complex
 *  or of the pixels. If the state is undefined a change of pixels is assumed
 *  for the operands.<br />
 *  <br />
 */ 
   public void updateOps(){
      if (op1.stat != CONSISTENT) {
        op1.consist();
        erg.stat = UNKNOWN;
      }
      if (op2.stat != CONSISTENT) {
        op2.consist();
        erg.stat = UNKNOWN;
      }
      if (erg.stat == CPL_CHANGE) { // Complex has changed
        erg.consist();
      }
   } //  updateOps()    
      
//----------------------------------------------------------------------
       
/** Paints itself (avoiding flicker). <br /> 
 *  <br />
 *  @param g the graphic environment to paint on
 */
   @Override public void update(Graphics g){  
        paint(g);  // no clear necessary  (Anti flicker)
   } // update(Graphics


/** Pint the picture. <br /> 
 *  <br />
 *  @param g the graphic environment to paint on
 */
   @Override public void paint(Graphics g){
      if (imgBg == null) imgInit();
      if (imgBg != null) g.drawImage(imgBg, 
                                  5 , CHOICEGAP+5, scrWidth, scrHeight, this);       
      g.setClip(5+3, CHOICEGAP+5+3, scrWidth-6, scrHeight-6 );
      g.translate(x00, y00);

      Arrays.sort(all3); // paint the largest first (13.06.2005)
      all3[0].doThePaint(g);
      all3[1].doThePaint(g);
      all3[2].doThePaint(g);
    } // paint(Graphics) 

//---  Mouse events  ------------------------

/** Dragging the mouse. <br />
 *  <br />
 *  By dragging the mouse with left button pressed the caught operand's 
 *  vector will be dragged and the painting, calculation and text display
 *  updated.<br />
 *  @param e the mouse event (ignored)
 */ 
   @Override public void mouseDragged(MouseEvent e){
      int x = e.getX() - x00;
      int y = e.getY() - y00;
      if (dragOp2) {
         op2.set(x, y);
      } else {
         op1.set(x, y);
      }
      makeArith();
   } // mouseDragged(MouseEvent)


/** Pressing the mouse button. <br />
 *  <br />
 *  By pressing the (left) mouse button the operand nearer to the spot will
 *  be caught. (If an unary operation is selected, there is only one
 *  operand and hence no need to choose.)<br >
 *  <br />
 *  @see #mouseDragged
 *  @param e the mouse event (forwarded to {@link #mouseDragged(MouseEvent)})
 */ 
   @Override public void mousePressed(MouseEvent e){
      if (op2.noShow) {
         dragOp2 = false;
         mouseDragged(e);
         return;
      }
      int x = e.getX() - x00;
      int y = e.getY() - y00;
      int dif1 = Math.abs(op1.xp - x) + Math.abs(op1.yp - y);      
      int dif2 = Math.abs(op2.xp - x) + Math.abs(op2.yp - y);
      dragOp2 = dif2 < dif1;   // a bit simplistic choice but sufficient 
      mouseDragged(e);
   } // mousePressed(MouseEvent)

   @Override public void mouseMoved(MouseEvent e){ }
   @Override public void mouseClicked(MouseEvent e){ }
   @Override public void mouseEntered(MouseEvent e){ }
   @Override public void mouseExited(MouseEvent e){ }
   @Override public void mouseReleased(MouseEvent e){ }

//---  Component events --- 

/** Resize the frame if run as application. <br /> 
 * <br />
 *  @param e the component event (ignored)
 */
   @Override public void componentResized(ComponentEvent e) { // size changes.
      imgBg = null; // Hintergrundbild ungültig      
   } // componentResized(ComponentEvent)

   @Override public void componentHidden(ComponentEvent e){} // > invisible.
   @Override public void componentMoved (ComponentEvent e){} // position chg.
   @Override public void componentShown (ComponentEvent e){} // made visible.   

//---  Item- events ---------------

/** Item event. <br />
 *  <br />
 *  This leads to the choice of an (other) arithmetic operation by using 
 *  {@link #selectOp(int)}.<br />
 */
   @Override public void itemStateChanged(ItemEvent e) {
      selectOp( opChooser.getSelectedIndex());
   }  // itemStateChanged(ItemEvent)

/** Choosing another arithmetic operation. <br />
 *  <br />
 *  This method changes the arithmetic operation to be demonstrated.<br />
 *  <br />
 *  For parameter values &lt;= 0 nothing happens.<br />
 *  <br />
 *  For  parameter values &gt; {@link #LAST_OP} the operands will be 
 *  initialised to default values by {@link #initOps()}.<br />
 *  <br />
 *  For other values this applies:<br />
 *  1: +; 2: -; 3: *; 4: /; 5: exp; 6: re; 7: im
 *  8: conj; 9: neg; 10: abs; 11: arg; 12: norm (abs*abs); 13: sin;
 *  14: cos; 15: sinh; 16: cosh; 17: tan; 18: tanh; 19: log.
 *  @param ts the complex operator to be demonstrated next (see list above)
 */
   public void selectOp(int ts){
      if (ts <= 0) return;
      if (ts > LAST_OP) {
         initOps();
      } else  if (ts != opIndex) { // opChanged
        opChooser.select(ts); 
        opString  = opChooser.getSelectedItem().trim();
        opIndex   = ts;
        op2.noShow = ts > LAST_OP_BIN;
      } // opChanged
      makeArith();
      //  opChooser.select(0);
   }  // selectOp(int)


/** Do the arithmetic operation. <br /> */
   public void makeArith(){
      updateOps(); 
      switch (opIndex) {
        case  1: // Add
           erg.set(op1.cp.plus(op2.cp));
           break;
        case  2: // Subtraktion
           erg.set(op1.cp.minus(op2.cp));
           break;
        case  6: // Realteil
           erg.set(op1.cp.re, 0);
           break;
        case  7: // Im-teil
           erg.set(op1.cp.im, 0);
           break;
        case  8: // konj-kompl
           erg.set(op1.cp.conjugate());
           break;
        case  9: // neg
           erg.set(op1.cp.negative());
           break;
        case  3: // mul
           erg.set(op1.cp.times(op2.cp));
           break;
        case  10: // abs
           erg.set(op1.cp.abs(), 0);
           break;
        case  11: // arg
           erg.set(op1.cp.argument(), 0);
           break;
        case  12: // norm
           double tmp = op1.cp.abs();
           erg.set(tmp * tmp, 0);
           break;
        case  4: // div
           erg.set(op1.cp.divide(op2.cp));
           break;
        case  5: // exp
            erg.set(op1.cp.exp());
            break;
        case  13: // sin
            erg.set(op1.cp.sin());
            break;
        case  14: // cos
            erg.set(op1.cp.cos());
            break;
        case  15: // sinh
            erg.set(op1.cp.sinh());
            break;
        case  16: // cosh
            erg.set(op1.cp.cosh());
            break;
        case  17: // tan
            erg.set(op1.cp.tan());
            break;
        case  18: // tanh
            erg.set(op1.cp.tanh());
            break;
        case  19: // log
            erg.set(op1.cp.log());
            break;
        default:
            erg.set(Complex.NaN);
            break;
      } // switch op
      erg.stat = CPL_CHANGE;  
      updateOps();
      repaint(50);
      if (! arithChanged && anim != null) {
         arithChanged = true;
         anim.interrupt();
      }
   } // makeArith()

/** Display  &quot;op1 op op2 = erg&quot;. <br /> */
   public void showArith(){
      arithChanged = false;
      StringBuilder dest = new StringBuilder(100);
      if (op2.noShow)   dest.append(opString);
          dest.append("(");
          dest.append(op1.cp.toString(5));
          dest.append(")");

      if (! op2.noShow) { 
          dest.append(" ");
          dest.append(opString);
          dest.append(" (");
          dest.append(op2.cp.toString(5));
          dest.append(")");
      } 

      dest.append(" = " );
      dest.append(erg.cp.toString(5));

      arDisplay.setLabel(dest.toString());
      arInd = -1;
   } // showArith()


   @Override public void actionPerformed(ActionEvent e){
      switch (++arInd) { 
         case 0:
             arDisplay.setLabel(CLASS_F);
             break;
         case 1:  
             arDisplay.setLabel(COPYRIGHT);
             break;
         case 3:  
             arDisplay.setLabel(AUTHOR);
             break;
         case 2:  
             arDisplay.setLabel(VERSION);
             break;
         case 4:  
             arDisplay.setLabel("arg : angle (-Pi...+Pi)");
             break;
         case 5:  
             arDisplay.setLabel("abs : length");
             break;
         case 6:  
             arDisplay.setLabel("norm: length*length");
             break;
         default:
             arithChanged = true;
             anim.interrupt(); // sets arInd = -1
      }
   } // actionPerformed

/** Size preferred. <br />
 *  <br />
 *  Returns (500, 328).
 */ 
   @Override public Dimension getPreferredSize(){
     return new Dimension(500, 328);
  } // getPreferredSize()

/** Minimal size. <br />
 *  <br />
 *  Returns (480, 228).
 */ 
   @Override public Dimension getMinimumSize(){
     return new Dimension(480, 228);
  } // getMinimumSize()

   
/** Start the graphical Demo as Java application. <br />
 *  <br />
 *  The demo panel {@link de.frame4j.demos.ComplDemo ComplDemo} will be
 *  embedded into a window of initial size 520 * 338.
 *  @param args not relevant
 */ 
   public static void main(String[] args){
      System.out.println(AUTHOR + "\n" + CLASS_F + "\n" + VERSION);
      ComplDemo   coDe = new ComplDemo(true, true);
      final Frame frame = new Frame(TITLE);
      coDe.setSize(new Dimension(520, 338));
      //    coDe.imgInit(); // again
      frame.add("Center", coDe);
    //  coDe.start();
      frame.setSize(coDe.getSize());
      frame.setIconImage(WeAutLogo.getIcon());
      
      frame.addWindowListener( new WindowAdapter() { // ===  ano. inner ====
         @Override public void windowClosing(WindowEvent e){
            frame.setVisible(false);
            frame.dispose();
            System.exit(0);  // is / was (?) indispensable here
         }
      }); //                                           ===
      frame.setVisible(true);
   } // main (String[])
   
}  // ComplDemo (07.02.2002, 31.01.2010, 14.04.2015, 16.08.2016)
