<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template name="teemss-header-script">
<!-- START TOP NAV -->

<script LANGUAGE="JavaScript"><xsl:text>
</xsl:text>
<xsl:comment>hide this script from non-javascript-enabled browsers
if (document.images) {
Itopban_02_02 = new Image(48, 28);Itopban_02_02.src = 'images_nav/but-home.gif';
Itopban_02_02o = new Image(48, 28);Itopban_02_02o.src = 'images_nav/but-home_over.gif';
Itopban_02_02h = new Image(48, 28);Itopban_02_02h.src = 'images_nav/but-home_onClick.gif';
Itopban_02_03 = new Image(120, 28);Itopban_02_03.src = 'images_nav/but-investigations.gif';
Itopban_02_03o = new Image(120, 28);Itopban_02_03o.src = 'images_nav/but-investigations_over.gif';
Itopban_02_03h = new Image(120, 28);Itopban_02_03h.src = 'images_nav/but-investigations_onClick.gif';
Itopban_02_04 = new Image(42, 28);Itopban_02_04.src = 'images_nav/but-help.gif';
Itopban_02_04o = new Image(42, 28);Itopban_02_04o.src = 'images_nav/but-help_over.gif';
Itopban_02_04h = new Image(42, 28);Itopban_02_04h.src = 'images_nav/but-help_onClick.gif';
}
function di(id,name){
  if (document.images) {document.images[id].src=eval(name+".src"); }
}

// function that displays status bar message

function dm(msgStr) {
  document.returnValue = false;
  if (document.images) { 
     window.status = msgStr;
     document.returnValue = true;
  }
}
var showMsg = navigator.userAgent != "Mozilla/4.0 (compatible; MSIE 4.0; Mac_PowerPC)";
function dmim(msgStr) {
  document.returnValue = false;
  if (showMsg) { 
    window.status = msgStr;
    document.returnValue = true;
  }
}

// stop hiding
</xsl:comment>
</script>

<!-- END TOP NAV -->
</xsl:template>


<xsl:template name="teemss-header">
<!-- START IMAGE WITH TABLE -->

<table BORDER="0" CELLPADDING="0" CELLSPACING="0" WIDTH="550">
<tr><!-- spacing row, 0 height. -->
<td><img SRC="images_nav/topban_00.gif" WIDTH="91" HEIGHT="1" BORDER="0"/></td>
<td><img SRC="images_nav/topban_00.gif" WIDTH="48" HEIGHT="1" BORDER="0"/></td>
<td><img SRC="images_nav/topban_00.gif" WIDTH="60" HEIGHT="1" BORDER="0"/></td>
<td><img SRC="images_nav/topban_00.gif" WIDTH="42" HEIGHT="1" BORDER="0"/></td>
<td><img SRC="images_nav/topban_00.gif" WIDTH="309" HEIGHT="1" BORDER="0"/></td>
</tr>
<tr><!-- row 01 -->
<td ROWSPAN="2" COLSPAN="1"><img NAME="Ntopban_01_01" SRC="images_nav/butterfly.gif" WIDTH="91" HEIGHT="86" BORDER="0" ALT="Butterfly logo"/></td>
<td ROWSPAN="1" COLSPAN="2"><img NAME="Ntopban_01_02" SRC="images_nav/unit-none.gif" WIDTH="168
   " HEIGHT="58" BORDER="0" ALT=""/></td>

<td ROWSPAN="1" COLSPAN="1"><img NAME="Ntopban_01_05" SRC="images_nav/teemssbanner.gif" WIDTH="309" HEIGHT="58" BORDER="0" ALT="technology enhanced elementary and middle school science"/></td>
<td><img SRC="images_nav/topban_00.gif" WIDTH="1" HEIGHT="58" BORDER="0"/></td>
</tr>
<tr><!-- row 02 -->
<td ROWSPAN="1" COLSPAN="1"><a HREF="index.html" ONMOUSEOUT="di('Ntopban_02_02','Itopban_02_02');dm(''); return true;" ONMOUSEOVER="di('Ntopban_02_02','Itopban_02_02o');dm('Return to TEEMSS home page'); return true;" ONCLICK="di('Ntopban_02_02','Itopban_02_02h');return true;" ><img NAME="Ntopban_02_02" SRC="images_nav/but-home.gif" WIDTH="48" HEIGHT="28" BORDER="0" ALT="Home button"/></a></td>
<td ROWSPAN="1" COLSPAN="1"><a HREF="investigations.htm" ONMOUSEOUT="di('Ntopban_02_03','Itopban_02_03');dm(''); return true;" ONMOUSEOVER="di('Ntopban_02_03','Itopban_02_03o');dm('List of TEEMSS Investigations'); return true;" ONCLICK="di('Ntopban_02_03','Itopban_02_03h');return true;" ><img NAME="Ntopban_02_03" SRC="images_nav/but-investigations.gif" WIDTH="120" HEIGHT="28" BORDER="0" ALT="List of Investigations button"/></a></td>
<td ROWSPAN="1" COLSPAN="1"><a HREF="help.htm" ONMOUSEOUT="di('Ntopban_02_04','Itopban_02_04');dm(''); return true;" ONMOUSEOVER="di('Ntopban_02_04','Itopban_02_04o');dm('Need Help?'); return true;" ONCLICK="di('Ntopban_02_04','Itopban_02_04h');return true;" ><img NAME="Ntopban_02_04" SRC="images_nav/but-help.gif" WIDTH="42" HEIGHT="28" BORDER="0" ALT="Help button"/></a></td>

<td><img SRC="images_nav/topban_00.gif" WIDTH="1" HEIGHT="28" BORDER="0"/></td>
</tr>
</table>

<!-- END IMAGE WITH TABLE -->

</xsl:template>

</xsl:stylesheet>
