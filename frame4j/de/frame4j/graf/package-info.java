/**
<b>Support for graphical applications</b> <br />
Images, image information and handling, dialogs and patterns. <br />
Copyright <a href="#co">&copy;</a> 1997 - 2003, 2919 Albrecht Weinert <br />

<h3><a name="be">Description</a></h3>
In this package &nbsp;{@code de.frame4j.graf}&nbsp; the framework's graphical
support classes are pooled.<br />
<br />
{@link de.frame4j.graf.ImageInfo ImageInfo} wraps and simplifies getting of 
informations and some operations on a picture.<br />
<br />
{@link de.frame4j.graf.AskDialog AskDialog} gives graphical and non graphical
applications often needed user interactions. Non reactive users may get a 
time out. This allows for default reactions in unobserved (batch) modes. The
application {@link de.frame4j.AskAlert AskAlert} exploits this for scripts
like Windows' .bat or .cmd.<br />
<br />
{@link de.frame4j.graf.GrafHelper GrafHelper} encompasses some static values
and helper methods.<br />
<br />
{@link de.frame4j.graf.DisplayPattern DisplaPattern} servers dot matrix
displays (like the well known LCD or LED devices) that can be used in
graphical applications. (Operations on those displays may sometimes be
cheaper than graphical text output operations, but that's not the 
reason d'être.)<br />
<br />
{@link de.frame4j.graf.WeAutLogo WeAutLogo} brings two depictions of 
weinert-automation's logo.<br />
<br />
The Interface {@link de.frame4j.graf.Paintable Paintable} promises what it's
name says.<br /> 
<br />
<br />
<h3>Terms of use, <a name="co" id="co">copyright</a></h3>
Copyright 1997 - 2003, 2019 &nbsp; Albrecht Weinert.<br />
<br />
Please find <a href="../package-summary.html#co">here</a> the terms of use 
for the 
<a href="../package-summary.html#package_description">framework</a> 
 <a href="../package-summary.html#be">Frame4J</a>.<br />
 
 @see <a href="#be">Package description</a>
 @see <a href="#co">Terms of use<br /></a>
 @see de.frame4j
 @author   Albrecht Weinert
 @version  Revision $Revision: 12 $ ($Date: 2019-03-07 18:30:51 +0100 (Do, 07 Mrz 2019) $) 
*/
package de.frame4j.graf;