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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Comparator;

import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;
import de.frame4j.time.TimeHelper;

/** <b>Support for file handling</b>. <br />
 *  <br />
 *  This class provides some operations and comfort for the handling of
 *  {@link File}s to simplify applications that have to handle files. (And
 *  which don't have to?)<br />
 *  <br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 1998 - 2009, 2013  &nbsp; Albrecht Weinert. 
 *  @author   Albrecht Weinert
 *  @version  $Revision: 38 $ ($Date: 2021-04-16 19:38:01 +0200 (Fr, 16 Apr 2021) $)
 *  @see FileCriteria
 *  @see FileService
 */
 // so far    V.134+ (05.01.2016) :  new
@MinDoc(
   copyright = "Copyright  2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 38 $",
   lastModified   = "$Date: 2021-04-16 19:38:01 +0200 (Fr, 16 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use en lieu de direct java.io.File",  
   purpose = "a file with some extra abilities"
) public final class FileHelper {
   
/** A extensive File comparator. <br /> 
 *  <br />
 *  @see #compare(File, File)
 */   
   static public Comparator<File> comparator = new Comparator<File>(){
      @Override public int compare(File d1, File d2) {
         return FileHelper.compare(d1, d2);
      }
    };
   
   private FileHelper(){} // no Objects; no javadoc
   
    
/** Compare two files. <br />
 *  <br />
 *  This method implements the interface 
 *  {@link java.util.Comparator java.util.Comparator}. This method compares 
 *  file properties according to their ordering not file content.<br />
 *  <br />
 *  The comparing sequence for the attributes is<ol>
 *  <li>name (case sensitive according to platform settings)</li>
 *  <li>length</li>
 *  <li>modification date</li>
 *  <li>path name (parent directory)<br />
 *  </ol>
 *  The first found non equal comparing &quot;wins&quot;, as its value is 
 *  returned. 0 would only be returned if all compared properties are 
 *  equal.<br />
 *  <br />
 *  This method defines a total ordering that is not consistent to 
 *  {@link File File}.{@link File#equals(Object) equals()}.<br />
 *  <br />
 *  @see ComVar#OS
 *  @see #sortFNC
 */
   public static final int compare(final File d1, final File d2){
            if (d1 == d2) return 0; // 04.09.2013
            String n1 = d1.getName();
            String n2 = d2.getName();
            int ret = TextHelper.compare(n1, n2, !sortFNC);      
            if (ret != 0) return ret;
            if (d1.exists()) {
               if (!d2.exists()) return 1000;
               long l1 = d1.length();
               long l2 = d2.length();
               if (l1 != l2) 
                  return (l1 > l2) ? 1001 : -1001;
               l1 = d1.lastModified();
               l2 = d2.lastModified();
               if (l1 != l2) 
                 return (l1 > l2) ? 1002 : -1002;
            } else {
               if (d2.exists()) return -1000;
            } 
            n1 = d1.getParent();
            n2 = d2.getParent();
            return TextHelper.compare(n1, n2, !sortFNC);  
         } // public int compare(2*File)

   
/** Array of no Files. */
   public static final File[] NO_FileX = new File[0];

/** Case of file names is relevant for sorting. <br />
 *  <br />
 *  default: {@link ComVar#NOT_WINDOWS}; true, 
 *  if the platform is definitely not Windows.<br />
*/
   public static boolean sortFNC = ComVar.NOT_WINDOWS;

//---------------------------------------------------------------------------

/** Construct a complete path. <br />
 *  <br />
 *  If both {@code basePath} and {@code name} are null, null is 
 *  returned.<br />
 *  <br />
 *  A file name is made specified by the path (parameter {@code basePath}) 
 *  and {@code name}. These may be absolute or relative, containing relative
 *  path expressions like  ..\ or .\ . The returned name will be an 
 *  unambiguous and complete &mdash; &quot;canonical&quot; &mdash;) pathname.
 *  A missing separator (\ bei DOS/Windows) at the end of {@code basePath} 
 *  will be supplied. {@code basePath} if given always denotes 
 *  directory.)<br />
 *  <br />
 *  &quot;Canonical&quot; means a complete and free of all relativity and 
 *  relative expressions according to the rules of the used file system.<br /> 
 *  <br > 
 *  Example: D:\otto\emil\oha.txt on Windows would be the canonical form of
 *  D:\otto\hugo\..\emil/marzan\..\oha.txt.<br />
 *  <br />
 *  If the given path is relative, the current actual path is 
 *  prepended.<br />
 *  <br />
 *  Implementation hint: This method contains (predecessor's) code to fight
 *  Java's class call miss-interpretation of X:oha, X:  and uhu\oha on Windows.
 *  There are strong hints that at least in Java 6 those problems are past. 
 *  {@link File#getCanonicalPath()} on Windows now seems to have the same 
 *  effect as the (quite older) code here. Be it not for the Exceptions 
 *  threatening in File's method {@link File#getCanonicalPath()} (IO and 
 *  Security for just a bit file name syntax handling?) the old code here
 *  could be removed.<br />
 *  <br />
 *  @param  basePath the path (directory)  (or null)
 *  @param  name     the file name
 *  @return a complete name
 *  @see    #getInstance(String)   getInstance()
 */
   public static final String getCompleteFilePath(String basePath, 
                                                         final String name){
      int bpl = basePath == null ? 0 : basePath.length();
      if (bpl == 0) { // no basePath spec
         if (name == null || name.isEmpty()) return null;
         basePath = ComVar.EMPTY_STRING;
      }  else {     // with basePath spec
         basePath = TextHelper.makeFName(basePath, null); // 9.9.2003
         if ( (bpl >= 2) && (basePath.charAt(1) == ':')  && 
                                 (  ( (bpl >= 3) && 
                                    (basePath.charAt(2) != ComVar.FS) ) ||
                                    ( bpl == 2)  
                                 )                               ) { 
            // no path relative specification like X: or X:oha (not X:\oha)
            if (ComVar.UD != null && Character.toUpperCase(basePath.charAt(0))
                                    ==  Character.toUpperCase(ComVar.UD.charAt(0))) { 
               String javaActPath = ComVar.UD.endsWith(ComVar.FSS) 
               ? ComVar.UD : ComVar.UD + ComVar.FSS;
               basePath = javaActPath +    basePath.substring(2,bpl);   
            }  else
               basePath = basePath.charAt(0) + ":" + ComVar.FSS + 
               basePath.substring(2,bpl);   
         }  // X:oha-Problem
         if (! basePath.endsWith(ComVar.FSS))
            basePath += ComVar.FSS;
      } //  with basePath spec
      if (name != null) 
         basePath += TextHelper.makeFName(name, null); 
       try {  // if not yet canonical try to -> canonical
          File ret = new File(basePath);
          basePath =  ret.getCanonicalPath();
      } catch (Exception e) {} // do nothing; return basepath as wa
      return basePath; 
   } //  getCompleteFilePath(2*String)

/** A FileVisitor default implementation &mdash; for asking. <br />
 *  <br />
 *  This is a (default) implementation of the interface {@link FileVisitor}
 *  by an object of that inetrface's embedded class 
 *  {@link FileVisitor.AskConsImpl} with default settings. It is to ask on the
 *  console by nationalised text before writing on existing files.<br />
 *  <br />
 *  Value: new FileVisitor.AskConsImpl(false, null, null, null)<br />
 *  <br />
 *  @since  V01.02 (08.12.2001)
 */
   public static final FileVisitor ASK_OVERWRITE = 
                       new FileVisitor.AskConsImpl(false, null, null, null);


/** Default setting for procedure to ask on file changes. <br />
 *  <br />
 *  This static variable defines the (default) behaviour of some methods of 
 *  this class before changing existing files respectively opening them (for 
 *  writing).<br />
 *  <br />
 *  The default setting of this static variable is the constant 
 *  {@link #ASK_OVERWRITE}. It may be changed.<br />
 *  <br />
 *  A graphical or javaw-started application must and other may prefer an
 *  asking by a Window. For that purpose use an instance of 
 *  {@link FileVisitor.AskGrafImpl}.<br />
 *  <br />
 *  @since  V01.02 (08.12.2001)
 *  @see #makeOS  makeOS()
 *  @see #ASK_OVERWRITE
 */
   public volatile static FileVisitor askOverwrite = ASK_OVERWRITE;
// this approach is stupid: modify a static here to steer other objects
// todo change asap. otoh it worked 20 years   

/** Factory by pathname (String) and path relative name (String). <br />
 *  <br />
 *  A File object is made. It will be tried, to canonicalise the basePath.
 *  This means also trying to make relative paths absolute.<br />
 *  (The method {@link #getCompleteFilePath(String, String)
 *  getFileWithCompletePath()} will be applied to basePath.)<br />
 *  <br />
 *  @param basePath The name of a  directory either absolute
 *              or relative to the applications actual path
 *  @param name  The name of a file or directory relative to path. 
 *  @return A fitting File object or null
 */
  static public File getInstance(final String basePath, final String  name){
     if (name == null || name.isEmpty()) { // no name
        return getInstance(basePath);
     } // no name
     String theDir  = getCompleteFilePath(basePath, null);
     return new File(theDir, TextHelper.makeFName(name, null)); 
   } // getInstance(String, String)


/** Factory by pathname (String). <br />
 *  <br />
 *  A File object is made. It will be tried, to canonicalise the basePath.
 *  These means also trying to make relative paths absolute.<br />
 *  (The method {@link #getCompleteFilePath(String, String)
 *  getFileWithCompletePath()} will be applied to basePath.)<br />
 *  <br />
 *  @param path The name of a file or directory either absolute
 *              or relative to the applications actual path
 *  @return A fitting File object or null
 */
  static public File getInstance(final String path) {
      String theDir  = getCompleteFilePath(null, path);
      if (theDir == null) return null;
      return new File(theDir);
   } // getInstance(String)

//--------------------------------------------------------------------------
  
  
/** Extracting the file type (extension) from a given file name. <br />
 *  <br />
 *  The name's extension is returned including (!) the leading dot (.).<br />
 *  <br />
 *  null is returned if the {@code name} contains no dot (.) before the
 *  last character. <br />
 *  If the the name (part) before the dot is empty, the name itself is 
 *  considered as type.<br />
 *  <br />
 *  Implementation hint: In Frame4J's predecessor the analogous method of
 *  class &quot;Datei&quot; returned the extension in upper case. This 
 *  (quite bad) idea was dropped for Frame4J.<br />
 *  <br />
 *  @param name a &quot;pure&quot; name without any paths or drive letters
 *  @return the extension or null
 *  @see    #getType(File)
 */
   public static String getType(final String name){
     int nl;
     if (name == null || (nl = name.length()) < 2) return null;
     final int pp = name.lastIndexOf('.');
     if (pp < 0 || pp == nl - 1) return null;
     return pp == 0 ?  name : name.substring(pp); 
   } // getType(String)

/** Fetching the file type, meaning the name's extension. <br />
 *  <br />
 *  The name's extension is returned including (!) the leading dot (.).<br />
 *  <br />
 *  null is returned if the name (part) contains no 
 *  &quot;inner dot&quot;.<br />
 *  This includes the case of the name (part) being itself empty. If the 
 *  File object was not constructed &quot;canonically&quot; it is
 *  possible that the file name wrongly is part of the (parent) path. Then, of
 *  course, (not only) this method fails.<br />
 *  <br />
 *  Implementation hint: In Frame4J's predecessor the analogous method of
 *  class &quot;Datei&quot; returned the extension in upper case. This 
 *  (quite bad) idea was dropped for Frame4J.<br />
 *  <br />
 *  @return the extension or null
 *  @see    #isOfType(File, CharSequence) isOfType()
 *  @see    #getCompleteFilePath(String, String) getCompleteFilePath()
 */
   public static String getType(final File f){ 
      if (f == null) return null;
      return getType(f.getName()); 
   } // getType(File)


/** Check the file type (i.e. the name's extension). <br />
 *  <br />
 *  This file's type will be compared with the type(s) specified by the 
 *  parameter {@code types}. The matching allows for semicolon separated 
 *  lists as well as wildCards (* ?) in {@code types} and is not case
 *  sensitive.<br />
 *  Example: &quot;.htm; HTML &quot;. <br />
 *  <br />
 *  Is {@code types} null, true is returned.<br />
 *  <br />
 *  If {@code types} has an empty entry (like the second one in 
 *  &quot;.htm;&nbsp;&nbsp;;.exe&quot;), than (and only than) a file name 
 *  with no extension (&quot;otto&quot;) will get a true.<br />
 *  <br />
 *  Hints:<ol>
 *  <li> blanks in types are ignored; a missing start dot (.) will be added, 
 *       and a leading first asterisk (*) before a dot removed.</li>
 *  <li> Wildcards (? *) on else places within a type are OK.</li>
 *  <li> If the File object was not constructed 
 *       &quot;canonically&quot; the file name part may wrongly be empty.
 *       Than (not only) this method fails.</li></ol><br />
 *  <br />
 *  Implementation hint: In Frame4J's predecessor the equivalent method
 *  Datei.isOfType() in fact used &quot;pathSeparator&quot; instead of 
 *  the semicolon (;  in above examples). That (bad) idea is dropped 
 *  here, as the Linux pathSeparator (:) has nothing to do with a type
 *  list in the above sense. Anyway, that concept of file type is foreign to 
 *  Linux. <br />
 *  <br />
 *  @param types the extensions asked for 
 *  @param f the file to get the type from
 *  @return true, if a fit was found
 *  @see    #getType getType()
 *  @see    #getInstance(String) getInstance()
 *  @see    #getCompleteFilePath(String, String) getCompleteFilePath()
 *  @see #wildEqual(File, java.lang.String) wildEqual()
 *  @see TextHelper#wildEqual(CharSequence, CharSequence, boolean)
 */
   static public boolean isOfType(File f, CharSequence types){
     if (f == null) return false;
     String type = TextHelper.trimUq(types, null);
     if (type == null) return true;
     int tL = type.length();
     if (type.charAt(tL-1) != ';') { /// pathSeparatorChar) {
        type = type + ';'; /// pathSeparator;
        ++tL;
     }

     String actType;
     int colonPos;
     final String myType = getType(f);
     for (int startPos = 0;      //  v was pathSeparatorChar
           (colonPos = type.indexOf(';', startPos)) >= 0;
                                            startPos = colonPos + 1) { //for
        if (colonPos == 0) continue; // types start ";gdjgaDKJHGAk
        if (colonPos == startPos) { // really empty: two consecutive ;;
           if (myType == null) return true;
           continue;
        } // really empty: two consecutive ;;
        actType = type.substring (startPos, colonPos).trim();
        int actTL = actType.length();
        if (actTL > 0 &&  actType.charAt(0) == '*'){
           actType = actType.substring(1);
           --actTL;
        }
       if (actTL == 0) { // empty type entry in list
          if (myType == null) return true;
          continue;
       } // empty type entry in list
       if (actType.charAt(0) == '.') {
           if (actTL == 1) continue; 
       } else
          actType = '.' + actType;
       if (actType.equals(myType) || 
           TextHelper.wildEqual(actType, myType, true) ) return true;
     } // for
     return false;
   } // isOfType(File, CharSequence)


/** Ask if other file or directory may be part of this file. <br />
 *  <br />
 *  The method returns true if the path ({@link File#getPath()}) of the 
 *  {@code otherFile} provided contains  {@code thisFile}'s path really 
 *  as a part.<br />
 *  <br />
 *  This would mean {@code otherFile} being a subdirectory of 
 *  {@code thisFile}  or directly or indirectly contained. It must be 
 *  &quot;real containment&quot;; just same paths return false.<br />
 *  <br />
 *  Existence of both files or their type (directory/file) will not be 
 *  considered.<br />
 *  <br />
 *  @param otherFile possibly contained in {@code thisFile}
 *  @return true, if contained directly or indirectly 
 */
   public static boolean contains(File thisFile, File otherFile){
     if (otherFile == null || thisFile == null) return false;
     String tn = thisFile.getPath();
     String on = otherFile.getPath();  /// on should be longer 
     if (tn.length() >= on.length()) return false; 
     if (!on.startsWith(tn)) return false;
     return on.charAt(tn.length()) == ComVar.FS;
   } // contains(2*File ) 



/** Compare the name with a pattern with optionally wildcards. <br />
 *  <br />
 *  The file f's name is compared to the pattern {@code wildname},
 *  that may as well contain wildcard characters (? and *).<br />
 *  <br />
 *  The call is equivalent to: <br />
 *  &nbsp; &nbsp; TextHelper.wildEqual(wildName, dieseDatei.getName(),
 *         false)<br />
 *  <br />
 *  @param wildName the pattern, may include wildcards
 *  @return         true if matches (according to wildEqual() rules)
 *  @see TextHelper#wildEqual(CharSequence, CharSequence, boolean)
 *  @see #isOfType(File, CharSequence) 
 */
   public static boolean wildEqual(final File f, final String wildName) {
      if (f == null) return false;
     return TextHelper.wildEqual(wildName, f.getName(), false);
   } // wildEqual(File, String)

//---------------------------------------------------------------------------


/** Output a test description of this file. <br />
 *  <br />
 *  A multi-line description of this object is output to parameter out.<br />
 *  <br />
 *  @param  out the destination for the description
 *  @return true, if this file  exists
  *  @see    #listLine listLine()
 */
   public static boolean testDescribe(final File f, final PrintWriter out){
      if (f == null) return false;
      final boolean exi = f.exists();
      final String nam = f.getName();
      final boolean isFi = f.isFile();
      out.println ("Name  : " + nam);
      out.println ("Parent: " + f.getParent());
      String maP, mcP;
      try {
         mcP =  f.getCanonicalPath();
      } catch (Exception e) {mcP = " not available"; }
      out.print   ("Path  : " + (maP = f.getPath()));
      if (f.isAbsolute())  out.print (" (abs.)");
      else  out.print ("\nAPath : " + (maP = f.getAbsolutePath()) );

      if (maP.equals(mcP)) out.println (" (canonical)");
      else 
         out.println ("\nCaPath: " + mcP);
      out.print   ("It is a ");
      if (!exi)   out.print ("non existent (or hidden) ");
      else               out.print ("existent ");
      if (f.isDirectory()) { 
          out.println ("directory");
      } // isDirectory
      else if (isFi) { 
          out.println ("file");
      } // isFile
      else { 
          out.println ("unknown type");
      } // no File or Directory          

      if (isFi) {
         out.print (nam + " " + f.length() + " Bytes ");
         out.print(TimeHelper.formatDIN(f.lastModified())); 
         if (f.canRead()) out.print("r");
         if (f.canWrite()) out.print("w");
         out.println ();
      } // isFile
      return exi;
   } // testDescribe(File, PrintWriter)


/** Generate a line for a file list. <br />
 *  <br />
 *  One descriptive line containing name, length, modification date and
 *  attributes will be made and returned.<br />
 *  <br />
 *  An tabular layout will be tried, as well as keeping the length below 78
 *  characters. If enough space remains the directory path is appended in
 *  braces (behind the attributes).<br />
 *  <br /> 
 *  @return the line
 *  @see    #testDescribe testDescribe()
 *  @see    #infoLine(File)
 */
   public static String listLine(final File f){
      if (f == null) return ComVar.EMPTY_STRING;
      StringBuilder dest = new StringBuilder(89);
      String parent = f.getParent();
      String name   = f.getName();
      final boolean exi = f.exists();
      final boolean isFi = f.isFile();
      final boolean isDir = f.isDirectory();
      if (name == null || name.isEmpty()) {
          name = parent;
          parent = null;
          if (name == null || name.isEmpty())
             name = f.getPath();
      }
      if (isDir && (parent != null)) {
         dest.append(parent);
         if (!parent.endsWith(ComVar.FSS))
             dest.append(ComVar.FS);
      }
      dest.append(name);
      if (isDir && !name.endsWith(ComVar.FSS)) 
         dest.append(ComVar.FS);

      if (!(exi || isFi || isDir)) {
         do { dest.append(' '); } while (dest.length() < 21);
         dest.append("non existent (or hidden) unknown type");
      } // non existing or hidden          
      else  { // existing
         String dl = isDir ? " " :
                       f.isFile() ? (f.length() + " ") : " ";
         do { dest.append(' '); } 
              while ((dest.length()+ dl.length()) < 31);
         dest.append(dl);
         dest.append(TimeHelper.formatDIN(f.lastModified())).append(' ');
         if ( !exi ) dest.append("(sh)");
         if (isFi) { // file
            if (f.canWrite()) {
               if (f.canRead())dest.append('r');  
               dest.append('w');
            } else
               dest.append("r/o");
         } /* file */ else {
            if (isDir) dest.append("dir");
         }  
      } //existing
      if (!isDir && 
           (parent != null) && (dest.length() + parent.length()) < 75) {
         while ((dest.length() + parent.length()) < 75) 
            dest.append(' ');
         dest.append(" (" + parent + ")");
      }
      return new String(dest);
   } // listLine(File)


/** Generate a file denomination [Path\]Name. <br />
 *  <br />
 *  The call is equivalent to
 *  {@link #pathName(File, StringBuilder, boolean, boolean, String)
 *     pathName(dest, noParent, quoteSpace, null)}.<br />
 */
   public static StringBuilder pathName(final File f, 
                                  final StringBuilder dest, 
                           final boolean noParent, final boolean quoteSpace){
       return pathName(f, dest, noParent, quoteSpace, null);
   } // pathName(File, StringBuilder, 2*boolean)
   
/** Generate a file denomination [Path\]Name. <br />
 *  <br />
 *  A text with either path and name or only name plus a prefix 
 *  &quot;... \ &quot; will be made.<br />
 *  <br /> 
 *  <br />
 *  @param  dest the StringBuilder to append to; if null it is made with
 *          starting capacity 89
 *  @param  noParent   true: omit directory information
 *  @param  quoteSpace true: If name contains spaces, surround by &quot;
 *  @param  relateTo   if the parent directory starts with relateTo this
 *                   starting part will be omitted
 *  @return dest
 *  @see    #testDescribe   testDescribe()
 *  @see    #listLine       listLine()
 */
   public static StringBuilder pathName(final File f, StringBuilder dest, 
                    final boolean noParent, boolean quoteSpace,
                    String relateTo){
      if (dest == null)  dest = new StringBuilder(89);
      if (f == null) return dest;
      String parent = f.getParent();
      if (relateTo != null && parent != null) {
         if (!parent.endsWith(ComVar.FSS))
            parent = parent + ComVar.FSS;

         if (TextHelper.startsWith(parent, relateTo, false )) {
            final int rltL = relateTo.length();
            final int parL = parent.length();
            if (parL == rltL) {
               parent = null;
            } else {
               parent = parent.substring(rltL);
            }
         }
      }
      String name  = f.getName();
      boolean isDir = f.isDirectory();
      if (name == null || name.isEmpty()) {
          name = parent;
          parent = null;
          if (name == null || name.isEmpty()) name = f.getPath();
      }
      
      quoteSpace = quoteSpace  
          && (name.indexOf(' ') >= 0  
              || (!noParent && parent != null && parent.indexOf(' ') >= 0) );

      if (noParent) {
         dest.append("... \\ ");
         if (quoteSpace) dest.append('\"');
      } else  {
         if (quoteSpace) dest.append('\"');
         if (parent != null ) {
            dest.append(parent);
            if (!parent.endsWith(ComVar.FSS))
                dest.append(ComVar.FS);
        }
      }
        
      dest.append(name);
      if (isDir && !name.endsWith(ComVar.FSS)) 
         dest.append(ComVar.FS);
      if (quoteSpace) dest.append('\"');
      return dest;
   } // pathName(File, StringBuilder, 2*boolean, String)


/** Generate a line for a file list. <br />
 *  <br />
 *  The call is equivalent to
 *  {@link #infoLine(File, StringBuilder, boolean, boolean)
 *            infoLine(f, null, false, false)}.<br />
 *  <br />
 *  @return the generated line
 *  @see    #testDescribe  testDescribe()
 *  @see    #listLine      listLine()
 */
   public static String infoLine(final File f){
      return infoLine(f, null, false, false, null).toString();
   } // infoLine(File) 


/** Generate a line for a file list. <br />
 *  <br />
 *  The call is equivalent to
 *  {@link #infoLine(File, StringBuilder, boolean, boolean, String) 
 *         infoLine(f, dest, noParent, quoteSpace, null)}.
 */
   public static StringBuilder infoLine(final File f, 
                        final StringBuilder dest, 
                           final boolean noParent, final boolean quoteSpace){
       return infoLine(f, dest, noParent, quoteSpace, null);
   } // infoLine(File, StringBuilder, 2*boolean)

/** Generate a line for a file list. <br />
 *  <br />
 *  <br />
 *  A descriptive line containing name, length, modification date and
 *  attributes will be made.<br />
 *  <br />
 *  Between name and the other denominations is at least one space.<br />
 *  <br />
 *  This method favours similar and parsable informations over tabular layout
 *  and beauty to the human reader. For the latter use 
 *  {@link #listLine(File)}.<br />
 *  <br />
 *  @param  noParent   true: generate without parent directory information
 *  @param  quoteSpace true: If name contains spaces, surround by &quot;
 *  @param  relateTo   if the parent directory starts with relateTo this
 *                   starting part will be omitted
 *  @return the line
 *  @see    #testDescribe(File, PrintWriter)   testDescribe()
 *  @see    #listLine(File)
 */
   public static StringBuilder infoLine(final File f, StringBuilder bastel, 
                    final boolean noParent, final boolean quoteSpace, 
                                                    final String relateTo) {
      bastel = pathName(f, bastel, noParent, quoteSpace, relateTo);
      if (f == null) return bastel;
      final boolean exi = f.exists();
      final boolean isFi = f.isFile();
      final boolean isDir = f.isDirectory();

      int startComm = 42;
      int endLen    = 55;
      if (noParent || relateTo != null) {
         endLen    -= 12;
         startComm -= 12;
      }  
      do {
         bastel.append(' ');
      } while (bastel.length() < startComm);
 
      if (!(exi || isFi || isDir)) { // non existing or hidden      
         bastel.append("non existent or unknown type");
      }  else  { // existing
         String dl = isDir ? " " :
                       isFi ? (f.length() + " ") : " ";
         do { bastel.append(' '); } 
              while ((bastel.length() + dl.length()) < endLen); 
         bastel.append(dl);
         bastel.append(TimeHelper.formatDIN(f.lastModified())).append(' ');
         if (f.isHidden()) bastel.append('h');
         if (! exi) bastel.append("(sh)");
         if (isFi) { // file
            if (f.canWrite()) {
               if (f.canRead())bastel.append('r');  
               bastel.append('w');
            } else
               bastel.append("r/o");
         } /* file */ else {
            if (isDir) bastel.append("dir");
         }  
      } //existing
      return bastel;
   } // infoLine(File, StringBuilder, 2*boolean, String)

//---------------------------------------------------------------------------

/** Make an output connected to the File given. <br />
 *  <br />
 *  This method generates and returns an object of the inner class
 *  {link OS} and hence {@link FileOutputStream} plus a 
 *  {@link PrintWriter} ({@code pw}) contained.<br />
 *  <br />
 *  Parameter mode specifies how to treat an existing file pointed to by the
 *  File object given as f; see {@link OutMode} for the possible
 *  behaviours. null is treated as 
 *  {@link OutMode}.{@link OutMode#ASK ASK}.<br />
 *  <br />
 *  @param mode  how to handle existing files 
 *  @param outEncoding the PrintWriter's encoding; null or defaults to
 *               platform's file encoding
 *  @return      An {@link OS} for this File 
 *               or null if impossible or forbidden 
 *  @see #ASK_OVERWRITE
 *  @see #askOverwrite
 *  @see FileVisitor
 *  @see FileVisitor.AskConsImpl
 *  @see FileVisitor.AskGrafImpl
 */
   public static OS makeOS(final File f, OutMode mode, 
                                             final Charset outEncoding){
      if (f == null) return null;
       /// ans: 0 : create, 3 : append, 6: overwr, else: ask 
      if (f.exists()) { 
         if (mode == OutMode.NO_ACTION) return null; // no change existing
         if (mode == null) mode = OutMode.ASK;      // default ask
         if (mode == OutMode.ASK && askOverwrite != null) { // do ask
            int ans = askOverwrite.visit(f);
            if (ans <= 0)  return null;  // asking go a NO
            if (ans == 3) mode = OutMode.APPEND;
         } // do ask
      }   else {  // file exists not yet 
         String par = f.getParent();
         if (par != null) {
            File dir = new File(par);
            if (!dir.exists()) dir.mkdirs();
         }
      } // file exists not yet  
      
      try {
    	   return new FileHelper.OS(f, mode == OutMode.APPEND, outEncoding);
      }  catch (IOException e){
         return null;
      }
   } //  makeOS(File, OutMode, CharSequence)


/** Make an output connected to the File. <br />
 *  <br />
 *  The call is equivalent to
 *  {@link #makeOS(File, OutMode, Charset)  makeOS(f, mode, null)}.<br />
 */
   public static final OS makeOS(final File f, final OutMode mode){ 
      return makeOS(f, mode, null); 
   } //  makeOS(File, OutMode)
   
//-----------------------------------------------------------------------


/** Write into a file. <br />
 *  <br />
 *  The (remaining or recorded) content of the Input object supplied will be
 *  written respectively appended into the given File.<br />
 *  <br />
 *  If the file (behind) exists the behaviour is governed by {@code outMode}
 *  like described for {@link #makeOS(File, OutMode) makeOS()}).<br />
 *  <br />
 *  The {@link OutMode} {@link OutMode#UPDATE UPDATE} means: If the 
 *  {@link Input} object represents a file or an URL and if the given File
 *  already exists it is overwritten if the given File's last modification is
 *  older than that of the {@link Input}.<br />
 *  <br />
 *  If Input allows to determine the time of the last modification that is
 *  set for the given File after successful copying. (Otherwise it would be
 *  the actual time, as this method modified the file.)<br />
 *  <br />
 *  @param ein     the input
 *  @param outMode the mode
 *  @return number of bytes copied / written into this file;
 *                 -1 : no input or no age difference in update mode or no out;
 *                 &lt; 0 if errors; -3 = IOException  
 *  @see #makeOS  makeOS()
 *  @see Input
 */
   public static int copyFrom(final Input ein, File out, OutMode outMode){
      if (ein == null || out == null) return -1;
      final long date = ein.lastModified();
      if (out.exists()) { // File exists: care for outMode
          isItUpdate: if (outMode == OutMode.UPDATE) {
              if (date != 0L && date <= out.lastModified()) 
                return -1; // this File modification not older
              outMode = OutMode.OVERWRITE; // update -> Overwrite
           }
      } // File exists: care for outMode   

     if ((ein.dataFile != null) && 
          (out.equals(ein.dataFile)))  return -5;  // no copy to self

      OS pos = makeOS(out, outMode, null);
      if (pos == null) return -2;
      
      int gesLen = ein.copyTo(pos);
      pos.close();
      if (gesLen >= 0 && date > 0L) {     // Inputs date is known
             out.setLastModified(date); // so set 
      }
      return  gesLen;     
    }  // copyFrom(Input, File, String)

//-----------------------------------------------------------------------


/** Compare two  file names (only). <br />
 *  <br />
 *  This method returns true, if the other object is a File and just 
 *  the names (not the paths) are equal. The comparison is or is not case
 *  sensitive according to the platform.<br />
 *  <br />
 *  @see #sortFNC
 *  @see #sameDir sameDir
 */
   public static boolean sameName(final File f, final Object o){
      if (f == null) return false;
      if (!(o instanceof File)) return false;
      final String n2 = ((File)o).getName();
      final String n1 = f.getName();
      return TextHelper.areEqual(n1, n2, !sortFNC);
   } // sameName(File, Object)


/** Compare two file paths (only). <br />
 *  <br />
 *  This method returns true, if the other object is a (Data)File and just 
 *  the paths (parent directories; not the names) are equal. The comparison 
 *  is or is not case sensitive according to the platform.<br />
 *  <br />
 *  <br />
 *  @see #sortFNC
 *  @see #sameName sameName
 */
   public static boolean sameDir(final File f, Object o){
      if (f == null) return false;
      if (!(o instanceof File)) return false;
      String n2 = ((File)o).getParent();
      String n1 = f.getParent();
      if (n1 == null) { /// should not happen
         return n2 == null;
      }
      return TextHelper.areEqual(n1, n2, !sortFNC);
   } // sameDir(File, Object)


//========================================================================

/** <b>Embedded class OS (OutputStream)</b> of FileHelper. <br />
 *  <br />
 *  A successfully constructed object of this class is a FileOutputStream
 *  for the set File object and it features a connected PrintWriter
 *  ({@link #pw}).<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 1997 - 2000, 2016 &nbsp;  Albrecht Weinert <br /> 
 */
 // so far    V00.03 (10:13 13.04.98)  :  innere Klasse OS, neu
 //           V00.04 (01.12.00 16:45)  :  /**

   @MinDoc(
      copyright = "Copyright  1997 - 2000, 2016 A. Weinert",
      author    = "Albrecht Weinert",
      version   = "like enclosing class FileHelper",
      lastModified   = "like enclosing class FileHelper",
      lastModifiedBy = "like enclosing class FileHelper",
      usage   = "use for output to a File",  
      purpose = "simplifies greatly the output to a file"
   ) public static class  OS  extends FileOutputStream {

/** The output PrintWriter. <br />
 *  <br />
 *  Will be set on construction and set null on {@link #close()}.<br />
 */
      public volatile PrintWriter pw;
      
/** The file. */
      public final File f;
      
/** Connected to a file in append mode. <br />
 *  <br />
 *  Set true (finally) on construction if the destination was connected to
 *  in append mode.<br />      
 */
      public final boolean append;


/** The output Writer. <br />
 *  <br />
 *  Will be set on construction and set null on {@link #close()}.<br />
 */
      public volatile BufferedWriter osw;

/** The output for the (set) File.  <br />
 *  <br />
 *  @param append true: do append instead of overwrite
 *  @exception IOException like FileOutputStream(..)
 */
      public OS(final File f, final boolean append) throws IOException {
         super(f.getPath(), append);
         this.f = f;
         this.append = append;
         pw = new PrintWriter(this);
      }  // OS(File, boolean)

/** The output for the (set) File.  <br />
 *  <br />
 *  @param append true: do append instead of overwrite
 *  @param outEncoding character encoding for the PrintWriter
 *  @exception IOException like FileOutputStream(..)
 */
      public OS(final File f, final boolean append, 
                         final Charset outEncoding) throws IOException {
         super(f.getPath(), append);
         this.f = f;
         this.append = append;
         // String outEnc = TextHelper.trimUq(outEncoding, null);
         osw = outEncoding == null 
                 ? new BufferedWriter(
            		     new OutputStreamWriter(this, ComVar.FILE_ENCODING))
                 : new BufferedWriter(
            		     new OutputStreamWriter(this, outEncoding));        
         pw = new PrintWriter(osw, false);
      } // OS(File, boolean, CharSequence)

/** The output for the (set) File, overwriting. <br />
 *  <br />
 *  @exception IOException like FileOutputStream(..)
 */
      public OS(final File f) throws IOException {
         super(f, false);
         this.f = f;
         pw = new PrintWriter(this);
         this.append = false;
      } // OS(File) 

/** Closing the output (file / stream). <br /> */ 
      @Override synchronized public void close(){
         BufferedWriter oldOsw = osw;
         pw  = null;
         osw = null;
         if (oldOsw  != null) try {
               oldOsw.flush();
               oldOsw.close();
               return;
         } catch (Exception e) {} // ignore
         try {super.close(); } catch (Exception e) {}  
      } // close()

   } // embedded class FileHelper.OS (05.01.2016)  ================

} // class FileHelper (05.01.2016)
