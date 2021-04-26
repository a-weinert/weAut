package de.frame4j.demos;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import de.frame4j.graf.Tableau;
import de.frame4j.graf.GrafVal;
import de.frame4j.graf.WeAutLogo;
import de.frame4j.graf.Paintable;

/**  <b>Towers of Hanoi &mdash; A Java game</b>. <br />
 *  <br />
 *  A TvH object is a {@link de.frame4j.graf.Tableau} and, hence, also a demo 
 *  for this {@link de.frame4j.graf.Tableau class}. It features the well known
 *  sorted stack moving game with two to ten disks. You can play by rule as
 *  well as wrongly. The recursive approach to the solution may well be 
 *  demonstrated by hand or be performed automatically.<br />
 *  <br />
 *  From 2002 until 2018 TvH was used in German University lectures to
 *  demonstrate recursive algorithms, O(f(n)) notation and Java programming.
 *  Hence, all comments, except this introductory part, have been and will be
 *  kept in German language. <br />
 *  <br />
 *  <br />
 * <b>Die Türme von Hanoi mit Bildhintergrund &mdash; Ein Java-Spiel</b>. <br />
 *  <br />
 *  Ein TvH-Objekt ist (per Erbe) ein {@link de.frame4j.graf.Tableau}, das
 *  das Stapelumsortierspiel Türme von Hanoi mit 2 bis 10 Turmscheiben und
 *  drei Bedienknöpfen darstellt. Das Spiel kann von Hand regelgerecht und
 *  auch regelwidrig gespielt werden. Sein (rekursiver) Lösungsansatz kann 
 *  vorgeführt werden und auch als automatischer Ablauf gezeigt werden.<br />
 *  <br />
 *  TvH beruht auf dem Framework {@link de.frame4j} 
 *  ({@link de.frame4j Frame4J}) und insbesondere auf dem Paket
 *  {@link de.frame4j.graf}.<br />
 *  <br />
 *  Copyright &copy; 1998 - 2004 &nbsp; Albrecht Weinert
 *  @author   Albrecht Weinert
 *  @version  $Revision: 39 $ ($Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $)
 *  @see      de.frame4j.graf
 */
 // Bisher    V00.00 10.10.1998 : neu
 //           V00.01 14.10.1998 : Klasse Bild-Änderung, dragged
 //           V00.04 04.06.2000 : aWeinertBib (de.a_weinert)
 //           V00.10 02.01.2002 : Anker-buttons, Bild-Erbe
 //           V02.20 05.07.2004 : kleine Änderung an Bild
 //           V02.25 27.03.2008 : comment typos
 //           V.o29+ 30.03.2015 : to jevaLearn project;  Tableau
 //           V.o14  08.03.2019 : weAutLogo, put to frame4j demos

public class TvH extends Tableau
                             implements Tableau.Listener, Runnable, GrafVal {

/** Name und Version. */
   public static final String headerLine = 
                             "  Türme von Hanoi / Towers of Hanoi";

   
//========================================== Innere Klasse Scheibe ======

/** <b>Die Darstellung der Turmscheiben</b>. <br />
 *  <br />
 *  Gemäß den Anforderungen von {@link de.frame4j.graf.Tableau}
 *  implementiert diese Klasse {@link de.frame4j.graf.Paintable}.
 */
   protected class Scheibe implements Paintable {
 
/** Die (eine!) Methode zum Malen des Elements im Bild.  */
      @Override public void paint(final Graphics g, final int x, final int y,
                                          final int width, final int height){
         g.setColor(rt);
         g.fillRoundRect(x, y, width, height, 18, 8);
         g.setColor(sw);
         g.drawRoundRect(x, y, width, height, 18, 8);
         g.drawArc(x - 2,          y - 10,   20, 10,   -90, -135);
         g.drawArc(x + width - 18, y - 10,   20, 10,   -90,  135);
         g.setColor(ws);
         g.drawArc(x - 1,          y - 11,   20, 10,   -90, -135);
         g.drawArc(x + width - 19, y - 11,   20, 10,   -90,  135);
      }  // paint

   } // class Scheibe
   
//==================== Innere Klasse (Stange) anonymous singleton  ==========

/** Die Darstellung der Turmstange. */
   Paintable stange = new Paintable(){

/** Die Malmethode der Stange im Bild. */
      @Override public void paint(final Graphics g, final int x, final int y,
                              final int width, final int height){
         int xMi = x + width/2;
         int stapI = 0;
         if (xMi < w3) stapI = 1;
         else  if (xMi > 2*w3) stapI = 2;
         g.setColor(sw);
         g.drawLine(xMi + 1, height - yStep/3,   xMi + 1, y);
         g.drawOval(x + 1, y - 1, width, height);
         String anz = Integer.toString(hoehen[stapI]);
         g.drawString(anz, xMi + width + 1, y + 2 * height + 1);
         g.setColor(ws);
         g.drawLine(xMi, height - yStep / 3,   xMi, y);
         g.drawString(anz, xMi + width, y + 2 * height);
         g.fillOval(x, y, width, height);
      }  // paint

   };  // ano. class implements Stange

//==================== Innere Klasse Rahmen  ==============================

/** <b>(Hilfs-) Rahmen zum Einbetten eines TvH-Objekts</b>. */
  public class Rahmen extends Frame {

/** Einziger Konstruktor. */
   public Rahmen(){
      super(headerLine); // .replace("$Revision:", ".").replace(" $", " "));
      final long  ctN = System.currentTimeMillis();
      final boolean oddMinute = ((ctN / 60000) & 1) == 1;
      setIconImage(oddMinute ? WeAutLogo.getIcon() :  WeAutLogo.getIcon2());
      add(TvH.this);
      pack();
      TvH.this.init(6);
      setVisible(true);

      addWindowListener( new WindowAdapter() {
         @Override public void windowClosing (WindowEvent event){
            setVisible(false);
            dispose();
            System.exit(0);
         }
      }); 
     } // Rahmen()


/** Überschriebene Update-Methode für den Rahmen. <br />
 *  <br />
 *  Sie verzichtet auf das für diese Anwendung unnötige Löschen des 
 *  Hintergrunds und verhindert so die Flackerei. 
 *  {@link de.frame4j.graf.Tableau} implementiert Doppelpufferung, sodass 
 *  auch auf dem Spielfeld (bild) nichts flackert.
 */
   @Override public void update(Graphics g){ paint(g); }

  } // Rahmen

//=======================================================================

   Font buttonFont = new Font("SansSerif", Font.PLAIN, 13);

/** Default-Name der Datei für das Hintergrundbild. <br />
 *  <br />
 *  Wert: .../Peking.gif; Ein pagodenartiges Gebäude in Peking, <br />
 *  passend in Motiv und Größe (auch wenn Peking nicht Hanoi ist).<br />
 *  Foto (Copyright 1981):  Annette Weinert  <br />
 *  <br />
 *  @see #img
 */
   public static final String BG_FILE = "de/frame4j/demos/Peking.gif"; 

   static final int OR = 29; // margin  ^
   static final int LRR = 5; // margin | |
   static final int UR = 5;  // margin  _

   static final int Y_STEP = 38; // geplante/gewünschte Mindesthöhe
   final int       w2;       // Bildbreite/2
   final int       w3;       // ca. Bildbreite/3

/** Breite der größten Scheibe. */
   public final int breite; 

/** Maximale Scheibenanzahl. */
   public final int maxAnz;

/** Größe des Bilds. <br />
 *  <br />
 *  Ist gleich {@link #img img.dim}
 */
   public final Dimension dim;
   
   private static boolean usePekingPhoto; // (c) Annette Weinert

/** Konstruktor mit Namen der Bildhintergrunddatei. <br />
 *  <br />
 *  Wird kein Dateiname angegeben, wird {@link #BG_FILE} genommen.
 *  Die Datei muss existieren und in ihrer Größe passen.
 */
   public TvH(String bgFile){
      super(si, bgFile, TvH.class, OR, UR, LRR,LRR);
      usePekingPhoto = bgFile == BG_FILE;
      this.dim = img.getSize();
      maxAnz = (dim.height - UR - OR) / Y_STEP;
      w2 = dim.width / 2;
      w3 = (dim.width - LRR - LRR) / 3;
      breite = (dim.width/4 + w3)/2;  // größte Breite
 
      buttEl = new Element[]{
        new Element(hgr, sw, buttonFont, "Spielen"),
        new Element(hgr, sw, buttonFont, "xxx Scheiben  - 1"),
        new Element(hgr, sw, buttonFont, "xxx Scheiben  + 1") };

      buttAn = new Anchor[]{
        new Anchor(buttEl[0], 
               new Point(w2 - breite /2 + 10, 5) , breite-20, 19  ),
        new Anchor(buttEl[1],
                       new Point(LRR, 5)         , breite+14, 19  ),
        new Anchor(buttEl[2],
            new Point(dim.width - LRR - breite -14, 5) , breite+14, 19  ) };

       addListener(this); 
       setMotion(true);
   } // TvH(String)


/** Einmaliges Ändern des geladenen und geränderten Bilds. <br />
 *  <br />
 *  Es wird ein Copyrighthinweis am unteren rechten Rand hinzugefügt.<br />
 */
   @Override public void retouche(Graphics g, int w, int h){
      g.setFont(buttonFont);
      String copRigNot = usePekingPhoto 
                      ? "Foto, Copyright (c) 1981 Annette Weinert"
                      : "TvH, Copyright (c) 2002, Albrecht Weinert";
      g.setColor(nbl);      
      g.drawString(copRigNot, w - 229, h - 7);
      g.setColor(hgr); 
      g.drawString(copRigNot, w - 230, h - 8);
   } // retouche(Graphics..) 

/** Die drei Bedienknöpfe. */
   protected Element[] buttEl;    // 0 : Spielen,  1: - 2 : +
   
/** Die Verankerung der drei Bedienknöpfe. */
   protected Anchor[]  buttAn;    //

   Scheibe[]   scheibe;
   Anchor[]    anker;    // die Scheiben
   Anchor[]    stangen = new Anchor[3];
   Anchor[][]  stapel =  new Anchor[3][];
   int[]       hoehen = {0, 0, 0};
   Point[]     platz;    // aktuelle Plätze
   Point[]     lPlatz;   // linker Stapelplatz
   int total;
   int[] xStange;    // x f. li, mi, re Stapel
   int yBotom;       // y f. Unterkante Stapel
   int yStep;        // Scheibenhöhe = size.height
   Dimension size;   // Abmessung der größten Scheibe
   Dimension topKugel= new Dimension (8, 8);
   int sizeStep;     // Durchmesserschritt von Scheibe zu Scheibe

   Anchor[] inDerLuft;
   int      anzInLuft;

   boolean autoPlay;
   boolean noMove   = true;

/** Minimale Scheibenanzahl (2). */
   public final int MIN_ANZ = 2;

//---------------------------------------------------------------

   public Anchor removeFrom(int stapInd){
      if ((stapInd <0) || (stapInd > 2) || (hoehen[stapInd] <=0)) return null;
      noMove = false;
      hoehen[stapInd]--;
      Anchor ret = stapel[stapInd][hoehen[stapInd]];
      stapel[stapInd][hoehen[stapInd]] = null;
      ret.moveLoc(stapInd==2 ? -5 : +5, -5);
      return ret;
   } // removeFrom(int)

   public void putTo(int stapInd, Anchor a){
      if ((stapInd < 0) || (stapInd > 2) || (a == null)) return;
      int lage = hoehen[stapInd]++;
      int breite = a.getDim().width;
      a.setLoc(xStange[stapInd] - breite / 2, yBotom - lage * yStep);
      stapel[stapInd][lage] = a;
   } // putTo(int, Anchor)

   public void fromTo(int stapFrom, int stapTo){
      Anchor aa = removeFrom (stapFrom);
      repaint();
      try { Thread.sleep(850); } catch (Exception e) { }    
      putTo(stapTo, aa);
      repaint();
      try { Thread.sleep(450); } catch (Exception e) { }    
   } // fromTo(2*int)

   void umbau(int aktAnz, int aktQuell, int aktHilf, int aktZiel){
      if (aktAnz > 1)
         umbau(aktAnz-1, aktQuell, aktZiel , aktHilf);  // Rekursion
      if (!autoPlay) return;
      fromTo(aktQuell, aktZiel);    //  Umbau einer Scheibe 
      if (!autoPlay) return;
      if (aktAnz > 1)
         umbau (aktAnz-1, aktHilf,  aktQuell, aktZiel);  // Rekursion
   } // umbau(4*int)       

   @Override  public void run(){
      if (!autoPlay) return;
      umbau (total,  1, 0, 2);
      autoPlay = false;
      knakM();
      repaint();
   } // run()

//---------------------------------------------------------------------------

/** Initialisieren des Spiels. <br />
 *  <br />
 *  Das Spiel wird ggf. angehalten und mit wTotal Scheiben neu
 *  initialisiert. wTotal wird auf {@link #MIN_ANZ} .. {@link #maxAnz}
 *  begrenzt.
 */
   public void init(int wTotal){
      autoPlay = false;
      total = wTotal > maxAnz ? maxAnz : wTotal;
      if (total < MIN_ANZ) total = MIN_ANZ;
      yStep = (dim.height - UR - OR) /(total+1);
      yBotom = dim.height - ((yStep-2) / 2) - yStep;
      size = new Dimension (breite, yStep);
      sizeStep = (breite - breite/5) / total;
      // [0] = Mitte = Hilfsstapel, [1]=Links=Quellstapel, [2]=Rechts=Ziel
      xStange = new int[]{w2,  w2 - w3,  w2 + w3};
      clearAnchor();
      addAnchor(buttAn);

      for (int i=0; i<3; ++i) {
         stapel[i] = new Anchor[total];
         stangen[i] = new Anchor(stange, 
             new Point (xStange[i]-2, 2 + OR), topKugel);
      }
      addAnchor(stangen);

      platz   = new Point[total]; // die aktuellen Plätze der Scheiben
      lPlatz  = new Point[total]; // die linken Ausgangsplätze
      scheibe = new Scheibe[total];
      anker   = new Anchor[total]; // die Scheiben

      inDerLuft= new Anchor[total];
      anzInLuft = 0;
      
      int aktBreite =  breite; 
      for (int i = 0; i < total; ++i) {
        lPlatz[i] = new Point(xStange[1]-(aktBreite/2),yBotom-i*yStep);
        platz [i] = (Point)lPlatz[i].clone();
        scheibe[i] = new Scheibe();
        anker[i] = new Anchor(scheibe[i], platz[i], 
           new Dimension(aktBreite,yStep));
        stapel[1][i] = anker[i];
        stapel[0][i] = null;
        stapel[2][i] = null;
        aktBreite -= sizeStep;
      } // for erzeugen
      addAnchorReverse(anker);
      hoehen[1] = total; 
      hoehen[0] = hoehen[2] = 0;
      buttEl[1].setText(total + " Scheiben" + (total > MIN_ANZ ? " -1" : ""));
      buttEl[2].setText(total + " Scheiben" + (total < maxAnz ? " +1" : ""));
      autoPlay = false;
      noMove = true;
   } // init(int) 

//---------------------------------------------------------------------------

/** Neu aufstellen bzw.&nbsp;starten bzw.&nbsp;stoppen. */
  public void nss(){
      if (autoPlay) { // Stop
         autoPlay = false;
      } else if (noMove) {  // Spielen
         autoPlay = true;
         new Thread(this).start();
      } else {     // neu Geben
            for (int i = 0; i < anker.length; ++i) {
               anker[i].getLoc().x = lPlatz[i].x;
               anker[i].getLoc().y = lPlatz[i].y;
               stapel[1][i] = anker[i];
               stapel[0][i] = null;
               stapel[2][i] = null;
               noMove = true;
            } 
         anzInLuft = 0;
         hoehen[1] = total;
         hoehen[0] = hoehen[2] = 0;
      }
      knakM();
  } // nss()

/** Text mittlerer Knopf aktualisieren und malen. */
  public void knakM(){
      buttEl[0].setText(autoPlay ? "Stop" : noMove ? "Spielen" : "Neu");
      repaint();
  } //  knakM()


/** Eine Scheibe weniger. */
   public void minus(){
      autoPlay = false;
      buttEl[2].setBgColor(hgr);
      buttEl[0].setBgColor(hgr);
      buttEl[1].setBgColor(gn);
      init(total-1); 
      knakM();
   } // minus()

/** Eine Scheibe mehr. */
  public void plus(){
      autoPlay = false;
      buttEl[1].setBgColor(hgr);
      buttEl[2].setBgColor(gn);
      buttEl[0].setBgColor(hgr);
      init(total+1); 
      knakM();
  } // plus()

   int cMx, cMy, lMx, lMy;
   int yDist;

/** Ausführungsmethode für Bild-Ereignisse (Maus). */
   @Override public void bildBedient(Anchor a, MouseEvent m){
      boolean mPr = m.getID() == MouseEvent.MOUSE_PRESSED;
      if (a == buttAn[0]) { // mittlerer Knopf
         if (mPr) nss();
         return;
      } // mittlerer Knopf
      if (a == buttAn[1]) { // linker Knopf
         if (mPr) minus();
         return;
      } // linker Knopf
      if (a == buttAn[2]) { // rechter Knopf
         if (mPr) plus();
         return;
      } // rechter Knopf
      int stapI = 0;
      lMx = cMx;
      lMy = cMy;
      cMx = m.getX();   
      cMy = m.getY();   
      if (cMx < w3) stapI = 1;
      else  if (cMx > 2*w3) stapI = 2;

      if (mPr) {
         if (a != null) { // Element gedrückt
            autoPlay = false;
            Anchor aa = null;
            if (anzInLuft == 0) do {
               inDerLuft [anzInLuft++] = aa = removeFrom(stapI); 
            } while (aa != a  &&  a != stangen[stapI]);
            if (a == stangen[stapI] && aa != null) 
              yDist = cMy - inDerLuft[0].getLoc().y; // f. Stabhochsprung
            else yDist = 0;                    
         } 
      }  else if (m.getID() == MouseEvent.MOUSE_RELEASED) {
         autoPlay = false;
         while (anzInLuft > 0) {
            putTo(stapI, inDerLuft[--anzInLuft]);
         }
      } else if (m.getID() == MouseEvent.MOUSE_DRAGGED) {
         for (int i = 0; i < anzInLuft; ++i) {
           int dy = cMy - lMy;
           if (yDist < -15) { 
                dy -= 15;
                yDist += 15; 
           }
           inDerLuft[i].moveLoc(cMx-lMx, dy);
         }
      }
      knakM();
   } // bildBedient(Anchor, MouseEvent)


/** Startmethode der Anwendung. <br />
 *  <br />
 *  Ein optionaler Parameter ist der Name einer Bilddatei. Sie dient dann als 
 *  Hintergrund und gibt die Größe des Fensters vor. <br />
 *  <br />
 *  Start: java de.frame4j.demos TvH [backgroundimagefile]
 *  
 *  @param args optional [0]=alternative background image-file; default: peking.gif
 */
   public static void main(String[] args){
      usePekingPhoto = args.length == 0;
      try {
         TvH tvH = new TvH(usePekingPhoto ? BG_FILE : args[0]);
         tvH.new Rahmen();
      } catch (Exception e) {
         System.out.println("\nTvH-Start Fehler :" + e 
                  + "\n\nStart:  java[w] TvH [Bilddatei.gif,jpg,png]");
         System.exit(99);
      }
   } // main(String[])

} // class TvH (2003, 2004, 2015, 2016, 2019, 24.03.2021)
