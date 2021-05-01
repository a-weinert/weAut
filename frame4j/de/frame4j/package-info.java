/**
<b>Frame4J &mdash; the Java framework</b> the base package.<br />

Copyright <a href="#co">&copy;</a> 2000 - 2016, 2021 &nbsp;
 <a href="https://a-weinert.de/index_en.html" 
  title="Prof. Dr.-Ing. A. Weinert, Bochum">Albrecht Weinert</a><br />

<h3><a name="be">Frame4J &mdash; the Java framework</a></h3>
<br />
<b>Frame4J</b> is a comfortable framework for robust applications,
real world tools, a rich set of library functions and more.<br />
It has a decades long history of successful usage in useful tools and in
server applications running 24/7, i.e permanently. One goal was and is keeping
compatibility &mdash; for long times even back to the weinertBib
(packages de.a_weinert) predecessor.<br />
But, being a good principle, compatibility to far back cannot always hold.

<h3><a name="rh">Revision history</a></h3>

Since 2015 some of the backward compatibility to &lt; 8 was given up<ul>
<li> Running on Java &lt; 8 may work but is neither checked nor supported
     any more. Further development of Frame4J is free to use 
     all Java 8 features.<br />
     On the other hand we still rely on the extension mechanism. Hence,
     all Frame4J development and standard use (from Raspberry Pi to Windows
     servers) will be on Java 8. It can be run as is on Java &gt; 9 or also 
     build there (tested thoroughly with 11). But going to Java 9 or beyond
     is not recommended es we loose valuable features (only partly mended
     by the {@link de.frame4j.Exec Exec} approach.</li>
<li> Content in packages {@code de.a_weinert} within Frame4J was dropped,
     moved out or to other {@link de.frame4j} packages in 2015. 2019
     support for IO related (process control on Pi) applications 
     added in package {@link de.weAut}.</li>     
<li> The explicit support of Servlets was dropped due to Frame4J's users 
     migrating from Tomcat to (pure) Apache or Node.js. Tomcat users will
     miss especially the methods put Servlet and request parameters to
     {@link de.frame4j.util.PropMap PropMap} and
     {@link de.frame4j.util.Prop Prop} objects.</li>
</ul>

<h3><a name="be">Revision history</a></h3>
Revision 118 (old repo) before February 22 2015 is the last one before that 
streamlining.<br />  
<br />
Since August 2016 (Revision &gt;=177) all pre Java8 compatibility was 
willfully broken, mainly by using Java 8 timing classes as far as feasible. In
preparation to Java 9 and beyond all reference to and support for Applets was
dropped.<br />
<br />
In September 2016 we had a jump in SVN revision numbering from ~160 backwards
to ~4. (SVN server [sic!] bugs forced relaunches of repositories.)<br />
And we updated to the last non-commercial itext jars (the same as used
by Michael Schierl's jpdftweak). In between (March 2021) we dropped the use
of itext and removed the  tool de.frame4j.PDFcompose &mdash; the command
line tool pdftk (C:\\util\\pdftk.exe 29.07.2013 8.890.393) is a better and
more comfortable substitute.
<br />
<br />
<b>Frame4J's base package URLs</b><br />
According to Java rules, the base package's names correspond
to domains / URLs
<table summary="format table 1" style="border:none;  margin: 4px 2px 4px 18px;">
<tr><td>&bull;&nbsp;</td><td><a href="https://frame4j.de" target="_top">frame4j.de/</a>
  </td><td>de.frame4j</td><td>Frame4J  (open source project),</td></tr>
<tr><td>&bull;</td><td><a href="https://a-weinert.de" target="_top">a-weinert.de/</a> 
  &nbsp;</td><td>(no package) &nbsp; </td><td>Albrecht Weinert (home).</td></tr>
<tr><td>&bull;</td><td><a href="https://weinert-automation.de" target="_top">weinert-automation.de/</a> 
  &nbsp; </td><td>(no package) &nbsp; </td><td>weinert-automation (home).</td></tr>
<tr><td>&bull;</td><td><a href="https://weinert-automation.de/index_en.html" target="_top">weAut.de/</a> 
  &nbsp;</td><td>{@link de.weAut}</td><td>short synonym URL.</td></tr>  
</table>
Searching here you will find information, the repository and (this) javadoc
documentation.<br />
<br />
<b>Frame4J's main packages</b>: <br />
The framework's library classes are distributed over some packages:
<table summary="format table 3" style="border:none;  margin: 4px 2px 4px 18px;">
<tr><td>&bull;&nbsp;</td><td>{@link de.frame4j.util}&nbsp;</td><td>base and helper classes,</td></tr>
<tr><td>&bull;&nbsp;</td><td>{@link de.frame4j.text}&nbsp;</td><td>text helper,</td></tr>
<tr><td>&bull;&nbsp;</td><td>{@link de.frame4j.time}&nbsp;</td><td>time handling,</td></tr>
<tr><td>&bull;</td><td>{@link de.frame4j.io}</td><td>I/O support,</td></tr>
<tr><td>&bull;</td><td>{@link de.frame4j.xml}</td><td>xml support,</td></tr>
<tr><td>&bull;</td><td>{@link de.frame4j.net}</td><td>communications, JMX, LDAP,</td></tr>
<tr><td>&bull;</td><td>{@link de.frame4j.math}</td><td> functions and complex numbers,</td></tr>
<tr><td>&bull;</td><td>{@link de.frame4j.graf}</td><td> graphical helper,</td></tr>
<tr><td>&bull;</td><td>{@link de.frame4j.demos}</td><td> demonstrator programmes,</td></tr>
<tr><td>&bull;</td><td>{@link de.frame4j}</td><td> base package and Frame4's tools,</td></tr>
<tr><td>&bull;</td><td>{@link de.weAut}</td><td>process I/O support,</td></tr>
<tr><td>&bull;</td><td>{@link de.weAut.demos}</td><td> I/O demonstrators.</td></tr>
 </table>


<h3><a name="ts">The tool set &mdash; support 
        for development and administration</a></h3>

This package <a href="#ts">de.frame4j</a> contains all tools and most 
applications that are part of
<a href="https://frame4j.de/index_en.html" target="_top">Frame4J</a>.<br />
<ol>
<li><a name="ddt">Development and deployment tools</a>
<ul>
<li>{@link de.frame4j.SVNkeys SVNkeys} &nbsp; &nbsp;  beautify SVN tags for human reader</li>
<li>{@link de.frame4j.SVNkeysFilter SVNkeysFilter} SVNKeys as doxygen filter</li>
<li>{@link de.frame4j.FS FS} &nbsp; &nbsp; &nbsp; &nbsp; FS search files and file doublets</li>
<li>{@link de.frame4j.FuR} &nbsp; &nbsp; &nbsp;&nbsp;
   FuR search and replace text (pattern) in files</li>
<li>{@link de.frame4j.Update Update} &nbsp; update files</li>
</ul></li>

<li><a name="att">Tools for administrative tasks</a>
<ul>
<li>{@link de.frame4j.Era Era} &nbsp; &nbsp; &nbsp; &nbsp; delete files</li>
<li>{@link de.frame4j.Del Del} &nbsp; &nbsp; &nbsp; &nbsp; delete files</li>
<li>{@link de.frame4j.UCopy UCopy} &nbsp; &nbsp; get file content by file or URL</li>
<li>{@link de.frame4j.io.ShowPorts ShowPorts} list (serial) ports</li>
</ul></li>

<li><a name="mht">Miscellaneous and helper applications</a>
<ul>
<li>{@link de.frame4j.XMLio} &nbsp; &nbsp; XMLio XML &rarr; DOM &rarr; XML (check and transform)</li>
<li>{@link de.frame4j.ShowProps ShowProps} show system and JDK/JRE properties</li>
<li>{@link de.frame4j.AskAlert AskAlert} &nbsp; pause a script, ask with timeout<br />
    {@link de.frame4j.AskAlert AskAlert} &nbsp; without parameters: display Frame4J version</li>
<li>{@link de.frame4j.SendMail SendMail}</li>
<li>{@link de.frame4j.PKextr PKextr} &nbsp;&nbsp; get keys (public and private) from keystore</li>
<li>{@link de.frame4j.Exec Exec} &nbsp; &nbsp; &nbsp;&nbsp; start a Frame4J application (first parameter)<br />
    {@link de.frame4j.Exec Exec} &nbsp; &nbsp; &nbsp;&nbsp; no prameter starts {@link de.frame4j.AskAlert AskAlert}</li>
<li>{@link de.weAut.TestOnPi TestOnPi} test IO operations/devices on a Pi</li>
</ul></li>


<li><a name="mht">Classes with (embedded) helper, test or demo application</a>
<ul>
<li>{@link de.frame4j.time.TimeHelper}</li>
<li>{@link de.frame4j.util.AppLangMap}</li>
</ul></li>

<li><a name="mht">Demonstrators (games)</a>
<ul>
<li>{@link de.frame4j.demos.ComplDemo ComplDemo} &nbsp; complex arithmetic</li>
<li>{@link de.frame4j.demos.TvH TvH } &nbsp; &nbsp; &nbsp; &nbsp; towers of Hanoi</li>
<li>{@link  de.weAut.demos.RdGnPiGpioDBlink RdGnPiGpioDBlink}</li>
<li>{@link  de.weAut.demos.JustNotFLock JustNotFLock}</li>
</ul></li></ol>

The scripts to build, document and deploy Frame4J use those tools too, mainly
from the first categories above. In that sense the framework is 
&quot;self building&quot;.

Hint: For having all tools handy, make Frame4J an installed extension.<br />
We are trying to develop a replacement for installed extensions, before them
been broken by Java9.<br />
The tool <a href="./Exec.html">Exec</a> is able to start all frame4j tools.
Being made frame4j's Main-Class (in the manifest) allows to start them all 
via -jar frame4.jar. This is just partly repairing the missing installed 
extensions as all tools using iText, comm, mail and other extensions including
JNI .ddl or .so will fail on Java&gt;8.<br />    
<br />
<br />
<h3>Conditions of use / license, <a name="co" id="co">Copyright</a></h3>
Copyright 2015 Albrecht Weinert, Bochum, Germany (a-weinert.de) <br />
All rights reserved. <br />
<br />
<a href="https://frame4j.de/index_en.html" target="_top">Frame4J</a> is made
available under the terms of the Eclipse Public License V1.0 which accompanies
this distribution, and is available at 
<a href="https://www.eclipse.org/legal/epl-2.0/">eclipse.org/legal/epl-v10.html</a>
or as <a href="./doc-files/epl.txt">text</a> within the source 
distribution.<br />

 @author   Albrecht Weinert
 @version  Revision $Revision: 42 $ ($Date: 2021-05-01 18:54:54 +0200 (Sa, 01 Mai 2021) $) 
*/
package de.frame4j;