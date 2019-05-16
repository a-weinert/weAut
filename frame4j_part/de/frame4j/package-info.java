/**
<b>Frame4J &mdash; the Java framework</b> base package <br />
A comfortable framework for robust applications, real world tools
and more.<br />
Copyright <a href="#co">&copy;</a>  2016 &nbsp; Albrecht Weinert <br />

<h3><a name="be">Description</a></h3>

This  package {@link de.frame4j} is the base or root package of the framework
Frame4J. Frame4J has a decades long history keeping compatibility even back to
the weinertBib (packages  {@code de.a_weinert}) predecessor.<br />
<br />
Since 2015 (and about Rev. 110 old repository) some of the backward 
compatibility was given up<ul>
<li> Running on Java &lt; 8 may work but isn't checked any more. Further
     development of Frame4J is free to use 8-features.<br />
     On the other hand we still rely on the extension mechanism. Hence,
     don't go to far with Java 8 and not to 9.</li>
<li> Content in packages  {@code de.a_weinert} within Frame4J was dropped,
     moved out or to other {@link de.frame4j} packages. </li>     
<li> The explicit support of Servlets was dropped due to Frame4J's users 
     migrating from Tomcat to (pure) Apache or Node.js. Tomcat users will
     miss especially the methods put Servlet and request parameters to
     {@link de.frame4j.util.PropMap PropMap} and
     {@link de.frame4j.util.Prop Prop} objects.</li>
</ul>
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
by Michael Schierl's jpdftweak).</li>
<br />
<br />
<b>Frame4J's base package URLs</b><br />
According to Java rules, the base package's names correspond
to the domains / URLs
<table summary="format table 1" style="border:none;  margin: 4px 2px 4px 18px;">
<tr><td>&bull;&nbsp;</td><td><a href="https://frame4j.de" target="_top">frame4j.de/</a></td><td>Frame4J  (open source project),</td></tr>
<tr><td>&bull;</td><td><a href="http://a-weinert.de" target="_top">a-weinert.de/</a>  &nbsp;</td><td>Albrecht Weinert (home).</td></tr>
</table>

<br />
<b>Frame4J's web URLs</b>:
<table summary="format table 2" style="border:none; margin: 4px 2px 4px 18px;">
<tr><td>&bull;&nbsp;</td><td><a href="http://a-weinert.de/frame4j/index_en.html" 
 title="also via frame4j.de"
target="_top">a-weinert.de/frame4j/</a></td><td>Frame4J home page,</td></tr>
<tr><td>&bull;</td><td><a href="http://weinert-automation.de/software/frame4j/" 
 title="frame4j.de downloads"
target="_top">weinert-automation.de/software/frame4j/</a> &nbsp;</td><td>Frame4J downloads: binaries, sources and else,</td></tr>
<tr><td>&bull;</td><td><a href="https://ai2t.de/svn/frame4j/src/trunk" 
 title="Authentication required"
target="_top">ai2t.de/svn/frame4j/src/trunk</a></a></td><td> Frame4J SVN (https and authentication required).</td></tr>
</table>
</ul>Hint: Those URLs have no relation to package naming.<br />
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
<tr><td>&bull;</td><td>{@link de.frame4j}</td><td> base package and Frame4's tools.</td></tr>
 </table>


<h3><a name="ts">The tool set &mdash; support 
        for development and administration</a></h3>

This package <a href="#ts">de.frame4j</a> contains all tools and most 
applications that are part of
<a href="http://a-weinert.de/frame4j/" target="_top">Frame4J</a>.<br />
<ol>
<li><a name="ddt">Development and deployment tools</a>
<ul>
<li><a href="./SVNkeys.html">SVNkeys</a></li>
<li><a href="./SVNkeysFilter.html">SVNkeysFilter (for Doxygen, mainly)</a></li>
<li><a href="./FS.html">FS</a></li>
<li><a href="./FuR.html">FuR</a></li>
<li><a href="./Update.html">Update</a></li>
<li>{@link de.frame4j.PDFcompose PDFcompose}</a></li>
</ul></li>

<li><a name="att">Tools for administrative tasks</a>
<ul>
<li>{@link de.frame4j.Era Era}</li>
<li>{@link de.frame4j.Del Del}</li>
<li><a href="./UCopy.html">UCopy</a></li>
<li>{@link de.frame4j.io.ShowPorts ShowPorts}</li>
</ul></li>

<li><a name="mht">Miscellaneous and helper applications</a>
<ul>
<li><a href="./XMLio.html">XMLio</a></li>
<li><a href="./ShowProps.html">ShowProps</a></li>
<li><a href="./AskAlert.html">AskAlert</a></li>
<li><a href="./SendMail.html">SendMail</a></li>
<li><a href="./PKextr.html">PKextr</a></li>
<li><a href="./Exec.html">Exec</a></li>
</ul></li>


<li><a name="mht">Classes with (embedded) helper, test or demo application</a>
<ul>
<li>{@link de.frame4j.time.TimeHelper}</li>
<li>{@link de.frame4j.util.AppLangMap}</li>
</ul></li>

<li><a name="mht">Demonstrators (games)</a>
<ul>
<li><a href="./demos/CompDemo.html">ComplDemo</a></li>
<li><a href="./demos/TvH.html">TvH</a></li>
</ul></li></ol>

The scripts to build, document and deploy Frame4J use those tools too, mainly
from the first categories above. In that sense the framework is 
&quot;self building&quot;.<br />
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
<a href="http://a-weinert.de/frame4j/" target="_top">Frame4J</a> is made
available under the terms of the Eclipse Public License V1.0 which accompanies
this distribution, and is available at 
<a href="https://www.eclipse.org/legal/epl-2.0/">eclipse.org/legal/epl-v10.html</a>
or as <a href="./doc-files/epl.txt">text</a> within the source 
distribution.<br />

 @author   Albrecht Weinert
 @version  Revision $Revision: 17 $ ($Date: 2019-05-15 21:51:04 +0200 (Mi, 15 Mai 2019) $) 
*/
package de.frame4j;