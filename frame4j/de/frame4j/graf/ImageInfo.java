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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import de.frame4j.io.FileHelper;
import de.frame4j.io.Input;
import de.frame4j.io.OutMode;
// import de.frame4j.util.AppHelper;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.util.Prop;
import de.frame4j.text.TextHelper;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Serializable;

import static de.frame4j.util.ComVar.*;

/** <b>An image with additional information and abilities</b>. <br />
 *  <br />
 *  An object of this class wraps a picture ({@link #img}, 
 *  {@link #getImg getImg()}) and features additional information and
 *  operations on it. The picture contained in and ImageInfo object is either 
 *  provided or made as a {@link java.awt.Image java.awt.Image} or preferably
 *  as the more versatile 
 *  {@link  java.awt.image.BufferedImage java.awt.image.BufferedImage}.<br />
 *  <br />
 *  Around this wrapped picture this class features some possibilities, that 
 *  otherwise, especially pre JDK1.4, would be very circumstantial, like:<ul>
 *  <li>simply getting of an (image) file's image size,</li>
 *  <li>serialising<br />
 *      (n.b.: AWT images are not serialisable),</li>
 *  <li>simplified &quot;painting&quot; for / into a file,<br />
 *      etc. pp..</li></ul>
 *  <br />
 *  ImageInfo simply gets you the image dimensions (image height and width)
 *  for a .png-, .gif- or .jpg file. For the very frequent use case (file
 *  listings by web services and so on) of pure getting the dimension there 
 *  is an optimised static helper method 
 *  {@link #imageSize imageSize(File)} to be used only if no further 
 *  informations or operations on the picture are required.<br />
 *  <br />
 *  Objects of this class are serialisable &mdash; including the wrapped 
 *  picture. This is a great help for factored (automation) applications that
 *  include pictures as backgrounds faceplates and so on. Those (if wrapped
 *  in an {@link ImageInfo}) can now be treated (serialised / de-serialised) 
 *  as all other function blocks.
 *  ({@link java.awt.Image Image} is not serialisable by itself.).<br />
 *  <br />
 *  Factoring {@link ImageInfo} with different sources for the picture are
 *  comfortably supported.<br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 1998 - 2003, 2004, 2008 &nbsp; Albrecht Weinert<br /> 
 *  <br />
 */
 // so far    V00.00 (15:16 28.04.2000) :  ex weinertBib
 //           V00.01 (12:46 11.06.2000) :  Serializable
 //           V00.04 (04.01.2002 17:55) :  buffered, 1.4, multiple trials
 //           V00.10 (04.01.2002 17:55) :  de, extensions, paint ...
 //           V00.11 (08.08.2002 20:30) :  load as stream
 //           V02.09 (30.10.2003 14:14) :  get from Component
 //           V02.22 (08.09.2005 09:39) :  toString()
 //           V02.25 (07.03.2008 09:32) :  ex prae - Java 2
 //           V02.26 (19.03.2008 08:33) :  Paintable
 //           V.o52+ (12.02.2009 19:21) :  ported to Frame4J
 //           V.o95+ (13.03.2009 14:35) :  imgPixels volatile ! (not tr typo)
 //           V.121+ (04.01.2010 14:13) :  some spelling
 //           V.139+ (06.01.2016) : FileHelper
//           V.  17 14.03.2019 : getAsResourceStream
@MinDoc(
   copyright = "Copyright 1998 - 2004, 2008, 2009, 20016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 38 $",
   lastModified   = "$Date: 2021-04-16 19:38:01 +0200 (Fr, 16 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use instead of Image and for images",  
   purpose = "additional operations and information on images"
) public class ImageInfo implements Serializable,  Paintable, Cloneable {


/** Version number for serialising. <br /> */
   static final long serialVersionUID = 260153007300201L;
//                                      magic /Id./maMi


//---------   static ---------------------------------

/** Determine the dimension of a .gif, .jpg or .png picture. <br />
 *  <br />
 *  This method determines the size of the picture contained in a 
 *  {@link File file}. The file content should be one of the image formats
 *  supplied by the underlying JDK / JRE installation. As standard
 *  these are .gif., .jpg and .png. Additionally the file extension should
 *  (with .gif must) comply with the image's type.<br />
 *  <br />
 *  An GIF image's size (in a .gif file) is read directly from the file 
 *  &mdash; saving thus throw away helper object and time.<br />
 *  For all other cases an (internal) ImageInfo object will be produced and
 *  asked for the wrapped picture's size.<br />
 *  <br />
 *  If the file is non existing or contains no (readable) image of platform
 *  supplied type, this will be signalled by returning the negative dimension
 *  (-1,-1).<br />
 *  <br />
 *  Hint 1: For all pictures not known to be gif and saying so by file name
 *  (extension) or in the case of needing the wrapping ImageInfo object
 *  anyway using this method makes no sense. Then make the ImageInfo object,
 *  check the success and ask it for the dimension.<br />
 *  <br />
 *  Hint 2: Corrupt .jpg files crashed some JVMs without warning (and hence
 *  without exceptions). Hopefully this concerns only the bugged 1.4.2x. If 
 *  that happens it's not this class' fault.<br />
 *  <br />
 *  Hint 3: Sometimes asynchronous (and uncontrollable) helper threads for
 *  .jpg or .png processing throw or report exceptions while this method 
 *  nevertheless returned the correct result.<br />
 *  <br />
 *  @param  fi   the image file
 *  @return      the size;  (-1,-1), if not to be determined
 */
   @SuppressWarnings("resource")  // input is from buffer here
   public static Dimension imageSize(final File fi) {
      final Dimension ret = new Dimension(-1, -1); // like NONE_DIM
      if ( fi == null || !fi.exists() || fi.isDirectory() || !fi.isFile() )
         return ret;
 
      if (FileHelper.getType(fi).equals(".GIF"))  { //  GIF-Bild
         byte[] buff = new byte[128];
         int br = 0;
         try {br = new Input(fi).readStartOfContent(buff);
         } catch (IOException e) {}     
         if (br > 10) { // width in [7 6] height in [9 8] / Pixel 
            ret.width  =  buff[7] * 256 + (buff[6]<0?buff[6]+256:buff[6]) ;
            ret.height =  buff[9] * 256 + (buff[8]<0?buff[8]+256:buff[8]) ;
         }
         return ret;
      } 

      // GIF or other 
         ImageInfo ii = ImageInfo.getInstance(fi.getPath());
         return ii == null ? ret : ii.getSize();
   }  // Dimension imageSize(File)


//--------------------------------------------


/** The Image. <br />
 *  <br />
 *  Implementation hint:<br>
 *  This variable is transient a (awt.){@link java.awt.Image Image} itself
 *  is not serialisable.<br />
 */
   protected transient volatile Image img;

/** The Image. <br />
 *  <br />
 *  This method returns the picture wrapped in this ImageInfo or null.<br />
 *  Remark: Former (Frame4J predecessor) versions of this class took the 
 *  burden of prae Java2 circumbendibus. Since these dropped, null should 
 *  be no more possible here.<br />
 *  <br />
 *  The returned image might be a {@link java.awt.Image java.awt.Image} or
 *  preferably / most often a versatile
 *  {@link  java.awt.image.BufferedImage java.awt.image.BufferedImage}.<br />
 *  @return the image wrapped
 */
   public final Image getImg(){ return  img; }


/** The picture's dimension. <br />
 *  <br />
 *  In case of no image (or not yet loaded) the dimension is
 *  (-1, -1).<br />
 *  <br />
 */
   protected volatile int w = -1, h = -1;

/** The picture's file name (if given). */
   String fName;

/** The picture's file name (if given). <br />
 *  <br />
 *  @return the image's filename (if known) 
 */
   public final String getFName(){ return fName; }


/** The picture as pixel array. <br />
 *  <br />
 *  In this array (generated accordingly on first request) the whole picture
 *  is supplied as simple pixel array.<br />
 *  <br />  
 *  @see #getImgPixels
 */
   protected volatile int[] imgPixels;

   
/** State as text. <br /> 
 *  <br /> 
 */   
   @Override public String toString(){
      StringBuilder dest = new StringBuilder(98);
      dest.append(" de.frame4j.graf.ImageInfo ");
      if (fName != null) dest.append(fName).append(" (");
      if (w == -1) {
         dest.append("  ?  *  ?  ) ");
      } else {
         dest.append(w).append(" * ").append(h).append(") ");
      }
      return dest.toString();
   } // toString() 

   
/** Copy this object. <br /> 
 *  <br /> 
 *  This method makes a deep copy. <br />
 */   
   @Override public synchronized Object clone(){
      ImageInfo ret = null;
      try {
         ret = (ImageInfo) super.clone();
      } catch (Exception ex) { // should not happen
         ret  = new ImageInfo();
         ret.fName = this.fName;
         ret.w = this.w;
         ret.h = this.h;
         ret.leM = this.leM;
         ret.riM = this.riM;
         ret.upM = this.upM;
         ret.loM = this.loM;
         ret.img = this.img;
         ret.imgPixels = this.imgPixels;
      } // repair not for inheritors
      
      if (ret.imgPixels == null) {
         ret.imgPixels = this.getImgPixels();
         if (ret.imgPixels != null) {
            ret.imgPixels = ret.imgPixels.clone();
            ret.img = new BufferedImage(ret.w, ret.h, 
                                              BufferedImage.TYPE_INT_ARGB);
            ((BufferedImage)ret.img).setRGB(0, 0, 
                                    ret.w, ret.h, ret.imgPixels, 0, ret.w);
         }  else {
            ret.img = null;
         }
      }
      return ret;
   }

//--- Generate / construct ------------------------------------------------
   
/** No public Constructor. <br />
 *  <br />
 *  Objects are made by static factory methods or by de-serialising. <br />
 */
   protected ImageInfo(){}


/** Make an ImageInfo object by a file. <br />
 *  <br />
 *  This method may produce an ImageInfo object even when the file delivers / 
 *  contains no image. The &quote;picture's&quot; size is then (-1,-1).<br />
 *  <br />
 *  null is returned if no readable / interpretable file content is 
 *  supplied.<br />
 *  @param imgFile the file
 */
   static public ImageInfo getInstance(File imgFile){
      if (imgFile == null) return null;
      String name = imgFile.getPath(); 
      ImageInfo ret = null; 
      try {
         ret = getInstance(new Input(imgFile), name);
      } catch (Exception ex){ } // ignore return null
      return ret;
   } // getInstance(File)


/** Make an ImageInfo object by a  file name. <br />
 *  <br />
 *  This method makes an ImageInfo object if fName designates an existing and
 *  readable file containing an image of JVM / platform supported format.<br />
 *  Otherwise null is returned.<br />
 *  <br />
 *  @param fName the file's name
 */
   static public ImageInfo getInstance(String fName){
      ImageInfo ret =  getInstance(fName, null, (Prop)null);
      return ret == null || ret.getWidth() < 0 ? null : ret;
   } // getInstance(String

/** Make an (buffered) Image by a file name. <br />
 *  <br />
 *  This method makes a helper / throw away ImageInfo object by the named
 *  file if feasible and just returns the wrapped image.<br />
 *  All other cases / failings return null.<br />
 *  <br />
 *  @param fName the file's name
 */
   static public BufferedImage getImg(String fName){
      ImageInfo ret =  getInstance(fName, null, (Prop)null);
      return ret == null || ret.getWidth() < 0 ? null 
           : (BufferedImage)ret.img;
   } //  getImg(String 


/** Make an ImageInfo object by a named file or class resource. <br />
 *  <br />
 *  Multiple ways are tried to produce an ImageInfo object fitting the 
 *  supplied name. First trial is a image file of just the name. Second
 *  try is the same but with the added suffix {@code ext} (if there and not
 *  yet suffix of {@code fName}).<br />
 *  <br />
 *  If {@code prop} is not null
 *  {@link Prop#getAsStream(String, String) Prop.getAsStream()} will be used
 *  in trials to load the picture as resource.<br />
 *  <br />
 *  If all fails null is returned.<br />
 *  <br />
 *  @param fName the name of the (image) file
 */
   static public ImageInfo getInstance(String fName,
                           final String ext, final Class<?> klass){
    //     System.out.println("   ///   TEST   getInstance(" + fName 
    //                                 + ", " + ext   + ", " + klass + ")");
      String lastMadeFName = fName = TextHelper.makeFName(fName, null);
      if (fName == null) return null;
      String fNameO = fName;
      ImageInfo ret = null;
      while (true) { 
         try {
            ret = getInstance(new FileInputStream(fName), fName);
         } catch (Exception ex){}
         if (ret != null) return ret;
         if (ext == null) break;
         int l1 = fName.length();
         fName = TextHelper.makeFName(fName, ext);
         if (fName != null ) lastMadeFName = fName;
         ///  ext = null; // break after 2. trial
         if (l1 != fName.length()) continue; // 2. trial with  .ext
         break;
      }
      if (klass != null) {
         try {
            ret = getInstance(Input.getAsResourceStream(fNameO, klass),
                                                              lastMadeFName);
         } catch (Exception ex){} // ignore return null
      }
      return ret;
   } // getInstance(String,String,Prop)


/** Make an ImageInfo object by a named file or resource. <br />
 *  <br />
 *  Multiple ways are tried to produce an ImageInfo object fitting the 
 *  supplied name. First trial is a image file of just the name. Second
 *  try is the same but with the added suffix {@code ext} (if there and not
 *  yet suffix of {@code fName}).<br />
 *  <br />
 *  If {@code prop} is not null
 *  {@link Prop#getAsStream(String, String) Prop.getAsStream()} will be used
 *  in trials to load the picture as resource.<br />
 *  <br />
 *  If all fails null is returned.<br />
 *  <br />
 *  @param fName the name of the (image) file
 */
   static public ImageInfo getInstance(String fName,
                           final String ext, final Prop prop){

      String lastMadeFName = fName = TextHelper.makeFName(fName, null);
      if (fName == null) return null;
      String fNameO = fName;
      ImageInfo ret = null;
      while (true) { 
         try {
            ret = getInstance(new FileInputStream(fName), fName);
         } catch (Exception ex){}
         if (ret != null) return ret;
         if (ext == null) break;
         int l1 = fName.length();
         fName = TextHelper.makeFName(fName, ext);
         if (fName != null ) lastMadeFName = fName;
         ///  ext = null; // break after 2. trial
         if (l1 != fName.length()) continue; // 2. trial with  .ext
         break;
      }
      if (prop != null) {
         try {
            ret = getInstance(prop.getAsStream(fNameO, ext), lastMadeFName);
         } catch (Exception ex){} // ignore return null
      }
      return ret;
   } // getInstance(String,String,Prop)



/** Make an ImageInfo object by a stream. <br />
 *  <br />
 *  This method tries to read a picture from the stream supplied.<br />
 *  In the case of success an ImageInfo object wrapping the picture as 
 *  BufferedImage is returned. The stream supplied will not be closed by this
 *  method.<br />
 *  All failings return null.<br />
 *  <br />
 *  @param ein   the stream
 *  @param fName name for the returned object
 *  @see Prop#getAsStream(String, String) Prop.getAsStream()
 *  @see #getInstance(String)
 */
   static public ImageInfo getInstance(final InputStream ein, String fName){
      if (ein == null) return null;
      try {
         BufferedImage img  = ImageIO.read(ein);
         ImageInfo ret = new ImageInfo();
         ret.img = img;
         ret.w = img.getWidth();
         ret.h = img.getHeight();
         ret.fName = fName;
         return ret;
      } catch (Exception ex) { return null;}
   } // getInstance(InputStream)


/** Make an ImageInfo object by a graphical component. <br />
 *  <br />
 *  This method generates an ImageInfo object from an AWT or Swing component.
 *  This component must be ready to show up directly or by its embedding
 *  container. In other words: it is already visible or would be without
 *  any problems if comp.setVisible(true) would be called.<br />
 *  <br />
 *  null will be returned if no component was supplied (null) or it is not
 *  (ready to be) visible or its dimensions are 0 * 0 (a form of 
 *  invisibility).<br />
 *  <br />
 *  This method allows the storing of the actual state of GUIs or parts of it
 *  à la Windows' alt-print.<br />
 *  <br />
 *  The wrapped picture ({@link #getImg()}) is a 
 *  {@link java.awt.image.BufferedImage BufferedImage} of kind
 *  {@link java.awt.image.BufferedImage}.TYPE_INT_ARGB
 *  (4 Byte alpha RGB).<br />
 *  <br />
 *  @param comp the graphical component to catch; it has to be in a 
 *              renderable state.
 */
   static public ImageInfo getInstance(final Component comp){
      if (comp == null) return null;
      int w = comp.getWidth();
      int h = comp.getHeight();
      if (w <= 0 || h <= 0) return null;
      ImageInfo ret  = new ImageInfo();
      ret.w  = w;
      ret.h  = h;
      ret.img =  new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      Graphics grI2 = ret.img.getGraphics();
      comp.paint(grI2);
      return ret;
   } //  getInstance(Component )


/** Make an ImageInfo object, empty or from memory content. <br />
 *  <br />
 *  This method makes an ImageInfo object from memory, i.e. stored pixels and 
 *  dimension.<br />
 *  <br>
 *  null will be returned on incorrect dimension (&lt;0, &lt;0) or supplied
 *  size not fitting the pixel array.<br />
 *  <br />
 *  If imgPixels is null, an empty image of supplied size is made. Non null
 *  imgPixels have to fit in size (as said) and are used as picture 
 *  content.<br />
 *  <br />
 *  The wrapped picture ({@link #getImg()}) is a 
 *  {@link java.awt.image.BufferedImage BufferedImage} of kind
 *  {@link java.awt.image.BufferedImage}.TYPE_INT_ARGB
 *  (4 Byte alpha RGB).<br />
 *  <br />
 *  @param imgPixels the pixel array (attention used / stored in the object 
 *                                 made as supplied reference; no copy made)
 *  @param w width
 *  @param h height
 */
   static public ImageInfo getInstance(final int[]imgPixels, 
                                                 final int w, final int h){
      if (w <= 0 || h <= 0) return null;
      if (imgPixels != null && imgPixels.length != w * h) return null;
      ImageInfo ret  = new ImageInfo();
      ret.w  = w;
      ret.h  = h;
      ret.imgPixels  = imgPixels;
      ret.img =  new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); 
      if (imgPixels != null)  
         ((BufferedImage)ret.img).setRGB(0, 0, w, h,  imgPixels, 0, w);
      return ret;
   } //  getInstance(int[], int, int)


/** The image's width. <br /> */
   public final int getWidth(){ return this.w; }
   
/** The image's height. <br /> */
   public final int getHeight(){ return this.h; }
   
/** The image's dimension. <br />
 *  <br />
 *  This method creates a new {@link Dimension} object for every call and is
 *  only useful in cases where such an object is really required.<br />
 *  <br />
 *  Otherwise use {@link #getWidth()} and {@link ImageInfo#getHeight()}.<br />
 */
   public Dimension getSize(){
      return new Dimension(w, h);
   } // getSize() 


/** The image as pixel array. <br />
 *  <br />
 *  A pixel array will be produced and initialised if not yet made (on the
 *  first call). The made array is returned as reference (no copy made).<br />
 *  The returned arrays size is width * height (may be 0).<br />
 *  If no (readable / grabbable) picture is wrapped, null is returned.<br />
 *  <br />
 *  @return the pixel array (reference; no copy!, )
 */
   public final int[] getImgPixels(){
      if (imgPixels != null) return imgPixels;
      if (w < 0 || h < 0) return null;
      imgPixels = new int[w * h];
      if (img instanceof BufferedImage) {
        ((BufferedImage) img).getRGB(0, 0, // startx/y
                             w, h,   // w, h
                             imgPixels, 0,  // dest array & offset
                             w);
        return imgPixels;
     }
     PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, imgPixels, 0, w);
     try {
        pg.grabPixels();
     } catch (InterruptedException e) {
        imgPixels = null;
     }
     if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
        imgPixels = null;
     }
     return imgPixels; 
   } // getImgPixels()

/** Green + blue difference to other picture. <br />
 *  <br />
 *  If this or the other ImageInfo object contains no picture, or if they are
 *  the same or their sizes (in number of pixels) are different -1 is 
 *  returned.<br />
 *  In all other (here sensible cases) the (absolute) differences of the
 *  (8 bit) green and blue pixel values are added.<br />
 *  <br />
 *  The (at first sight) idiosyncratic, but comparably simple algorithm 
 *  provided surprisingly good motion detection in many cases (stationary
 *  in door camera, simple floating threshold).<br />
 */
   public int gbDif(ImageInfo other){
      if (other == null || other == this) return -1;
      int[] aktPix = getImgPixels();
      if (aktPix == null) return -1;
      int len = aktPix.length;
      if (len < 1) return -1;
      int[] lstPix = other.getImgPixels();
      if (lstPix == null) return -1;
      if (lstPix.length != len) return -1;
      
      int sum = 0;
      for (int i=0; i < len; ++i ) {
         int pixA = aktPix[i];
         int pixL = lstPix[i];
         int difB = pixA & 0xFF - pixL & 0xFF;
         sum += difB > 0 ? difB : -difB;
         pixA >>= 8;
         pixL >>= 8;
         difB = pixA & 0xFF - pixL & 0xFF;
         sum += difB > 0 ? difB : -difB;                 
      } 
      return sum;
   } // gbDif(ImageInfo)
   
//---  Paint it  ----------------------------------------

/** A Graphics object. <br />
 *  <br />
 *  This method returns a Graphics object for the contained picture 
 *  ({@link #getImg()}), that provides a possibility to paint (on) it. It is 
 *  only returned if wrapped image is a BufferedImage. Otherwise  null is 
 *  returned.<br />
 *  <br />
 *  The Graphics object returned will be of the extended type Graphics2D and
 *  can be used as such.<br />
 *  <br />
 *  @see #surrondImage surrondImage(..)
 *  @see #scaleImage(Color, int, int)
 */
   public Graphics getGraphics(){
      if (!(img instanceof BufferedImage)) return null;
      return img.getGraphics();
   } // getGraphics() 

/** The left margin. <br />
 *  <br />
 *  This variable just hold the margin  set by the last call of 
 *  {@link #surrondImage surrondImage()}.<br />
 *  <br />
 *  This variable {@link #leM} as well as 
 *  {@link #riM}, {@link #upM} and {@link #loM} are for informal reading
 *  only. It's value has no effect in this class.<br />
 *  <br />
 *  default / start value: 0
 */
    public volatile int leM;

/** The right margin. <br />
 *  <br />
 *  @see #leM
 *  @see #surrondImage surrondImage()
 */
    public volatile int riM;

/** The upper margin. <br />
 *  <br />
 *  @see #leM
 *  @see #surrondImage surrondImage()
 */
    public volatile int upM;

/** The lower margin. <br />
 *  <br />
 *  @see #leM
 *  @see #surrondImage surrondImage()
 */
    public volatile  int loM;

 
/** Surround the picture with margins. <br />
 *  <br />
 *  The picture ({@link #getImg() img}) will be surrounded / enlarged by the 
 *  margins specified. The 3 * 8 bit colour model is used for the 
 *  operation.<br />
 *  <br >
 *  If no wrapped picture is there (not even an empty one) nothing happens and
 *  null is returned.<br />
 *  <br />
 *  If one of the margins is out of the legal 0 .. 400 range, all margins are
 *  taken as 0; see also {@link 
 *  GrafHelper#surrondImage(Image, Color, int, int, int, int) 
 *    GrafHelper.surrondImage()}.<br />
 *  <br />
 *  After this method the wrapped image is of type BufferedImage (or ist is 
 *  null). A call {@code surrondImage(null, -1,...)} will just change the 
 *  type of the wrapped image to BufferedImage, if possible and not yet 
 *  so.<br />
 *  <br />
 *  Multiple calls to this method consider the enlarged picture as rimless 
 *  starting point. The margin set by the last call are stored in 
 *  {@link #upM}, {@link #loM}, {@link #leM} and  {@link #riM}.<br />
 *  <br />
 *  @param bgColor background respectively margin colour 
 *                  (default {@link GrafVal#ws}).
 *  @param upM upper margin in pixel (0..400).
 *  @param loM leM, riM the same for lower left and right.
 *  @return image enlarged by margins
 *  @see #scaleImage(Color, int, int)
 */ 
   public BufferedImage surrondImage(final Color bgColor,
                                    int upM, int loM, int leM, int riM) {
      if (img == null || w < 0 || h < 0) return null;

      if (upM < 0 || loM < 0 || leM < 0 || riM < 0 
          ||  upM > 400 || loM > 400 | leM > 400 || riM > 400) {
         loM = riM = leM = upM = 0;
         this.loM = this.riM = this.leM = this.upM = 0;
         if (img instanceof BufferedImage) return (BufferedImage)img;
      } else { // all margins in range
         w += leM + riM;
         h += upM + loM; 
         imgPixels = null;
         this.loM = loM;
         this.riM = riM;
         this.leM = leM;
         this.upM = upM;
      }
      return (BufferedImage)(img =  GrafHelper.surrondImage(img,
                                           bgColor, upM, loM, leM, riM));
   } // surrondImage(Color


/** Scaling an image. <br />
 *  <br />
 *  The wrapped picture ({@link #getImg() img}) will be brought to the 
 *  specified size. The 3 * 8 bit colour model is used for the 
 *  operation.<br />
 *  <br >
 *  If no wrapped picture is there (not even an empty one) nothing happens and
 *  null is returned.<br />
 *  <br />
 *  If one of the defined new dimensions is &lt; 1, nothing is done and 
 *  null is returned.<br />
 *  <br />
 *  If actual and destined sizes are equal and if the wrapped picture is 
 *  already of type  {@link BufferedImage} it is returned as is.<br /> 
 *  <br />
 *  @param bgColor background colour (if not null)
 *  @param newHeight target height
 *  @param newWidth  target width
 *  @return image 
 *  @see #surrondImage(Color, int, int, int, int)
 */ 
   public BufferedImage scaleImage(final Color bgColor,
                                   final int newWidth, final int newHeight) {
      if (newWidth <= 0 || newHeight <= 0) return null;
      if (img == null || w < 0 || h < 0) return null;
      boolean noScale = w == newWidth && h == newHeight;
      if (noScale  && img instanceof BufferedImage) 
         return (BufferedImage) img;
      img = GrafHelper.scaleImage(img, bgColor, newWidth, newHeight);
      w = newWidth;
      h = newHeight;
      return (BufferedImage) img;
    } // surrondImage(Image


//---   Store  / fetch  ----------------------------------------


/** Writing the image to an output stream. <br />
 *  <br />
 *  This method only works if the picture wrapped is a BufferedImage; see also
 *  {@link #surrondImage surrondImage()},
 *  {@link #scaleImage(Color, int, int)}.<br />
 *  <br />
 *  The formats supported for writing as standard are png und jpg. gif is
 *  usually supported for reading only (who knows why).<br />
 *  <br />
 *  @param  formatName informal name of the image file / stream  format
 *          (default png)
 *  @param output the stream to write the image to
 *  @return true on success
 *  @throws IOException at problems with output
 */
   public boolean writeImage(final CharSequence formatName,
                              final OutputStream output)
                                                        throws IOException {
      if (output == null || !(img instanceof RenderedImage)) return false;
      final String tmpForm = TextHelper.trimUq(formatName, "png"); 
      return ImageIO.write((RenderedImage)img, tmpForm, output);
   } // writeImage(String

/** The formats supported for writing. */
    static volatile String[] wfn;

/** The formats supported for writing. <br />
 *  <br />
 *  This method returns (a copy) of a non empty array of format names 
 *  available (on the platform) for image writing.<br />
 *  <br />
 *  If no formats can be determined (which should not be the case) the empty
 *  array ({@link ComVar#NO_STRINGS ComVar.NO_STRINGS}) is returned.<br />
 *  <br />
 *  @return a list (array) of image formats supported
 *  @see #writeImage writeImage()
 */
    public static String[] getWriterFormatNames() {
       if (wfn != null) {
          if (wfn.length == 0) return NO_STRINGS;
          return wfn.clone(); // defensive copy (since 04.03.2009)
       }
       String[] tmp = null;
       synchronized (NO_STRINGS) {
          if (wfn != null) {
             if (wfn.length == 0) return NO_STRINGS;
             return wfn.clone(); // defensive copy (since 04.03.2009)
          }
          try {
             tmp = ImageIO.getWriterFormatNames(); // this is expensive
          } catch (Exception e) {}
          if (tmp == null || tmp.length == 0) return wfn = NO_STRINGS;
          wfn = tmp.clone();
       } // sync
       return tmp;
    } // getWriterFormatNames()

/** Writing the image into a File. <br />
 *  <br />
 *  This method only works if the picture wrapped is a BufferedImage; see also
 *  {@link #surrondImage surrondImage()},
 *  {@link #scaleImage(Color, int, int)}.<br />
 *  <br />
 *  The format to be used will be determined by the extension of the 
 *  {@link File} supplied. That will be overwritten with the picture 
 *  content. The file will be closed after successful writing.<br />
 *  <br />
 *  @param imgFile the file to write the image to
 *  @return true on success
 *  @throws IOException in case of file writing problems
 */
   public boolean writeImage(File imgFile) throws IOException {
      if (imgFile == null || !(img instanceof RenderedImage)) return false;
      String formatName = FileHelper.getType(imgFile);
      if (formatName == null || formatName.length() < 3)
         formatName = "png";
      else {
         formatName = formatName.substring(1);
         if (wfn == null) getWriterFormatNames();
         boolean found = false;
         for (int i = 0; !found && i < wfn.length; ++i) { 
            found = TextHelper.areEqual(formatName, wfn[i], true);
            if (found) break;
         }  // for 
         if (!found) formatName = "png";  
      }
      OutputStream os = FileHelper.makeOS(imgFile, OutMode.OVERWRITE);
      boolean ret =  ImageIO.write((RenderedImage) img, formatName, os);
      os.close();
      return ret;
   } // writeImage(File) 


/** Writing the image into a file named fileName. <br />
 *  <br />
 *  The call is equivalent to<br /> &nbsp;
 *   writeImage({@link FileHelper FileHelper}.getInstance(fileName))
 *  <br />
 *  @param fileName the file to write the image to
 *  @return true if successful
 *  @throws IOException in case of file output problems
 */
   public boolean writeImage(String fileName)  throws IOException  {
      if (fileName == null || fileName.isEmpty()
          ||  !(img instanceof RenderedImage)) return false;
      return writeImage(FileHelper.getInstance(fileName));    
   }  // writeImage(String)


//------------------  Serialising ------------------------------------

/** Writing the image. <br /> */
   private void writeObject(ObjectOutputStream oos) throws IOException {
      getImgPixels();   // make the pixels if not yet done
      oos.defaultWriteObject();       
   } // writeObject(ObjectOutputStream

/** Reading the image. <br /> */
   private void readObject(ObjectInputStream ois) 
                     throws IOException, ClassNotFoundException {
      ois.defaultReadObject();       
      img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      if (imgPixels != null) {
         ((BufferedImage)img).setRGB(0, 0, w, h, imgPixels, 0, w);
      }
   } // readObject(ObjectInputStream)

//----------------------------------------------

/** The contained image as graphical component. <br />
 *  <br />
 *  A object of this class ImgDisplayer is an embedded {@link JComponent},
 *  that contains just the enclosing {@link ImageInfo}objects image at the 
 *  time of the ImgDisplayer's construction.<br />
 *  <br />
 */   
   @MinDoc(
      copyright = "see embedding class ImageInfo",
      author    = "Albrecht Weinert",
      version   = "see embedding class ImageInfo",
      lastModified   = "see embedding class ImageInfo",
      lastModifiedBy = "see embedding class ImageInfo",
      usage   = "construct and use",  
      purpose = "the image as graphical component"
   ) public final class ImgDisplayer extends JComponent {

      final Image im;

 /** Making the paintable component. <br />
  *  <br />
  *  For the picture wrapped by the enclosing {@link ImageInfo} object a 
  *  {@link JComponent} will be made, displaying just that picture.<br />
  */ 
      public ImgDisplayer() {
         this.im = ImageInfo.this.getImg();
         setDoubleBuffered(true);
      } // Constructor

/** Paint the image. <br />
 *  <br />
 *  The picture is painted in full size using white as background. <br />
 */
      @Override protected void paintComponent(final Graphics gr) {
          gr.drawImage(im, 0, 0, GrafVal.ws, null);
      } // paintComponent(Graphics)

   } // class ImgDisplayer      ========================================= 
   

/** The paint method. <br />
 *  <br />
 *  This method implementing the interface {@link Paintable} orders this 
 *  {@link ImageInfo} object to paint itself to a given location and a 
 *  given size.<br />
 *  See the interface's
 *  {@link Paintable#paint(Graphics, Point, Dimension) contract}.<br />
 *  <br />
 *  @param g The notorious {@link java.awt.Graphics} object (that can
 *           nowadays be expected to be a {@link java.awt.Graphics2D}).
 *  @param x y The left upper corner of where to paint
 *  @param width height The size of an surrounding rectangle to paint within
 */
   
   @Override public void paint(final Graphics g, int x, int y, 
                                                int width, int height){
      if (g == null || img == null) return;
      if (width < 1 || height < 1){
         g.drawImage(img, x, y, null);
         return;
      }
      g.drawImage(img, x, y, width, height,  null);
   } // paint(Graphics, ..

} // class ImageInfo (30.10.2003, 04.03.2008, 19.03.2008, 04.03.2009)
