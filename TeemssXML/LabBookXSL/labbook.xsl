<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:output method="xml" indent="yes"
  doctype-public="-//Concord.ORG//DTD LabBook Description//EN" 
  doctype-system="../../XML2LabBook/labbook.dtd"/>

<xsl:include href="about-teemss.xml"/>

<xsl:strip-space elements="*"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="project">
  <LABBOOK>
    <xsl:copy-of select="document('../../CCProbe/CCProbeXML/ccprobe.xml')"/>
    <FOLDER ID="{title}" name="{title}">
      <xsl:call-template name="about-teemss">
        <xsl:with-param name="varDateModified">
          <xsl:value-of select="project-info"/>
        </xsl:with-param>
      </xsl:call-template>
      <xsl:apply-templates select="unit"/>
    </FOLDER>
  </LABBOOK>
</xsl:template>

<xsl:template match="unit">
  <FOLDER ID="{@name}" name="{title}">
    <xsl:apply-templates select="investigation" mode="investigate"/>
    <FOLDER ID="{@name}_response" name="Responses">
      <xsl:apply-templates select="investigation" mode="response"/>
    </FOLDER>
  </FOLDER>
</xsl:template>

<xsl:template match="investigation" mode="investigate">
  <FOLDER ID="{@name}" name="{title}" view="paging">
    <xsl:apply-templates select="intro"/>
    <xsl:apply-templates select="think" mode="investigate"/>
    <xsl:apply-templates select="materials"/>
    <xsl:apply-templates select="safety"/>
    <xsl:apply-templates select="trial" mode="investigate"/>
    <xsl:apply-templates select="hints"/>
    <xsl:apply-templates select="analysis" mode="investigate"/>
    <xsl:apply-templates select="further"/>
    <FOLDER ID="{@name}-saved-datasets" name="Saved Data Sets">
    </FOLDER>
  </FOLDER>
</xsl:template>

<xsl:template match="investigation" mode="response">
  <FOLDER ID="{@name}-response" name="{title} Responses" view="paging">
    <xsl:apply-templates select="think" mode="response"/>
    <xsl:apply-templates select="trial" mode="response"/>
    <xsl:apply-templates select="analysis" mode="response"/>
  </FOLDER>
</xsl:template>

<xsl:template match="intro">
  <SUPERNOTES ID="{../@name}-intro" name="Introduction" locked="true">
    <EMBOBJ object="teemss_titlebar.bmp"/>
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:value-of select="../title"/> Introduction
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <SNPARAGRAPH linkcolor="0000FF">
      Discovery Question:
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <SNPARAGRAPH linkcolor="000F0F">
      <xsl:value-of select="normalize-space(../question)"/>
    </SNPARAGRAPH>
    <xsl:apply-templates/>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="think" mode="investigate">
  <SUPERNOTES ID="{../@name}-think" name="Thinking About the Question" locked="true">
    <EMBOBJ object="teemss_titlebar.bmp"/>
    <SNPARAGRAPH linkcolor="0000FF">
      Thinking About the Question
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <SNPARAGRAPH linkcolor="000F0F">
      <xsl:value-of select="normalize-space(../question)"/>
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:apply-templates/>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="think" mode="response">
  <xsl:element name="FOLDER">
    <xsl:attribute name="ID">
      <xsl:value-of select="../@name"/>_think_response</xsl:attribute>
    <xsl:attribute name="name">Think About Responses </xsl:attribute>
    <xsl:apply-templates select="query-response" mode="response"/>
  </xsl:element>
</xsl:template>

<xsl:template match="materials">
  <SUPERNOTES ID="{../@name}-materials" name="Materials" locked="true">
    <EMBOBJ object="teemss_titlebar.bmp"/>
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:value-of select="../title"/> Materials
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:for-each select="item">
      <SNPARAGRAPH>
        - <xsl:value-of select="normalize-space(.)"/>
      </SNPARAGRAPH>
    </xsl:for-each>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="safety">
  <SUPERNOTES ID="{../@name}-safety" name="Safety" locked="true">
    <EMBOBJ object="teemss_titlebar.bmp"/>
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:value-of select="../title"/> Safety
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:apply-templates select="item"/>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="item">
  <INDENT size="1" first-line-offset="-1">
    <xsl:text>- </xsl:text>
    <xsl:apply-templates mode="item"/>
    <BR/>
    <SNPARAGRAPH/>
  </INDENT>
</xsl:template>

<xsl:template match="p" mode="item">
  <SNPARAGRAPH/>
  <BR/>
  <xsl:value-of select="(.)"/>
  <BR/>
</xsl:template>

<!-- <xsl:template match="text()[normalize-space(.)!='']" mode="item"> -->

<xsl:template match="text" mode="item">
  <xsl:value-of select="(.)"/>
</xsl:template>



<xsl:template match="hints">
  <SUPERNOTES ID="{../@name}-hints" name="Technical Hints" locked="true">
    <EMBOBJ object="teemss_titlebar.bmp"/>
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:value-of select="../title"/> Technical Hints
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:for-each select="hint">
      <SNPARAGRAPH>
        - <xsl:value-of select="title"/>
      </SNPARAGRAPH>
    </xsl:for-each>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="further">
  <SUPERNOTES ID="{../@name}-further" name="Further Investigations" locked="true">
    <EMBOBJ object="teemss_titlebar.bmp"/>
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:value-of select="../title"/>: Further Investigations
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:apply-templates/>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="analysis" mode="investigate">
  <SUPERNOTES ID="{../@name}-analysis" name="Analysis" locked="true">
    <EMBOBJ object="teemss_titlebar.bmp"/>
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:value-of select="../title"/> Analysis
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:apply-templates select="steps" mode="investigate"/>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="analysis" mode="response">
  <xsl:element name="FOLDER">
    <xsl:attribute name="ID">
      <xsl:value-of select="../@name"/>_analysis_response</xsl:attribute>
    <xsl:attribute name="name">Analysis Responses</xsl:attribute>
    <xsl:apply-templates select="steps" mode="response"/>
  </xsl:element>
</xsl:template>

<xsl:template match="trial" mode="investigate">
  <SUPERNOTES locked="true">
    <xsl:attribute name="ID">
      <xsl:value-of select="../@name"/>_trial_<xsl:number value="position()" format="I"/>
    </xsl:attribute>
    <xsl:attribute name="name">Trial <xsl:number value="position()" format="I"/>      
    </xsl:attribute>
    <EMBOBJ object="teemss_titlebar.bmp"/>
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:value-of select="normalize-space(@title)"/>
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:for-each select="*|text()">
      <xsl:choose>
        <xsl:when test="name()='steps'">
          <xsl:apply-templates select="." mode="investigate"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <SNPARAGRAPH/>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="trial" mode="response">
  <FOLDER>
    <xsl:attribute name="ID">
      <xsl:value-of select="../@name"/>_trial_<xsl:number value="position()" format="I"/>_response</xsl:attribute>
    <xsl:attribute name="name">Trial <xsl:number value="position()" format="I"/> Responses      
    </xsl:attribute>
    <xsl:apply-templates select="steps" mode="response"/>
  </FOLDER>
</xsl:template>

<xsl:template match="instructions">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="instruction">
  <xsl:if test="position()=2">---aircart-insert-part2---</xsl:if>
  <xsl:if test="position()=3">---aircart-insert-part3---</xsl:if>
  <xsl:if test="position()=4">---aircart-insert-part4---</xsl:if>
  <xsl:if test="position()=5">---aircart-insert-part5---</xsl:if>
  <xsl:if test="position()=6">---aircart-insert-part6---</xsl:if>
  <xsl:if test="position()=7">---aircart-insert-part7---</xsl:if>
  <xsl:if test="position()=8">---aircart-insert-part8---</xsl:if>
  <xsl:if test="position()=9">---aircart-insert-part9---</xsl:if>
  <SNPARAGRAPH linkcolor="0000FF"><xsl:value-of select="@title"/></SNPARAGRAPH>
  <SNPARAGRAPH/>
  <xsl:for-each select="*|text()">
    <xsl:choose>
      <xsl:when test="name()='steps'">
        <xsl:apply-templates select="." mode="investigate"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template match="steps">
  <xsl:apply-templates select="step | query-response" mode="step"/>
</xsl:template>

<xsl:template match="steps" mode="investigate">
  <xsl:apply-templates select="step | query-response" mode="step"/>
</xsl:template>

<xsl:template match="steps" mode="response">
  <xsl:apply-templates select="query-response" mode="response"/>
</xsl:template>

<xsl:template match="step" mode="step">
  <xsl:variable name="format_depth">
    <xsl:value-of select="count(ancestor::steps)"/>
  </xsl:variable>
  <xsl:variable name="format_label">
    <xsl:choose>
      <xsl:when test="$format_depth ='1'">1</xsl:when>
      <xsl:when test="$format_depth ='2'">a</xsl:when>
      <xsl:when test="$format_depth ='3'">i</xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:element name="SNPARAGRAPH">
    <xsl:if test="$format_depth='2'">
      <xsl:attribute name="indent">2</xsl:attribute>
    </xsl:if>
    <xsl:if test="$format_depth='3'">
      <xsl:attribute name="indent">4</xsl:attribute>
    </xsl:if>
    <xsl:number value="position()" format="{$format_label}"/>. <xsl:value-of select="normalize-space(text()[position()=1])"/>
  </xsl:element>
  <xsl:if test="$format_depth='1'"><SNPARAGRAPH/></xsl:if>
  <xsl:if test="position()=last()">
    <xsl:if test="$format_depth!='1'">
      <SNPARAGRAPH/>
    </xsl:if>
  </xsl:if>
  <xsl:apply-templates select="text()[position()!=1]|*"/>
</xsl:template>


<xsl:template match="query-response" mode="step">
  <xsl:variable name="format_depth">
    <xsl:value-of select="count(ancestor::steps)"/>
  </xsl:variable>
  <xsl:variable name="format_label">
    <xsl:choose>
      <xsl:when test="$format_depth ='1'">1</xsl:when>
      <xsl:when test="$format_depth ='2'">a</xsl:when>
      <xsl:when test="$format_depth ='3'">i</xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:element name="SNPARAGRAPH">
    <xsl:if test="$format_depth='2'">
      <xsl:attribute name="indent">2</xsl:attribute>
    </xsl:if>
    <xsl:if test="$format_depth='3'">
      <xsl:attribute name="indent">4</xsl:attribute>
    </xsl:if>
    <xsl:number value="position()" format="{$format_label}"/>. <xsl:value-of select="normalize-space(text()[position()=1])"/>
    <xsl:apply-templates select="query-description|datacollector-link"/>    
    <xsl:if test="@layout='paragraph'">
      <xsl:apply-templates select="querys" mode="paragraph"/>
    </xsl:if>
  </xsl:element>
  <xsl:if test="@layout='list'">
    <SNPARAGRAPH/>
    <xsl:apply-templates select="querys" mode="list"/>
  </xsl:if>
  <xsl:if test="$format_depth='1'"><SNPARAGRAPH/></xsl:if>
  <xsl:if test="position()=last()">
    <xsl:if test="$format_depth!='1'">
      <SNPARAGRAPH/>
    </xsl:if>
  </xsl:if>
    <xsl:element name="EMBOBJ">
      <xsl:attribute name="object">
        <xsl:value-of select="ancestor::investigation/@name"/>_<xsl:value-of select="name(ancestor::*[../../investigation])"/>_<xsl:number level="any"/>_<xsl:number/>
      </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
  </xsl:element>  
</xsl:template>


<xsl:template match="query-response">
  <xsl:choose>
    <xsl:when test="@layout='paragraph'">
      <SNPARAGRAPH>
      <xsl:apply-templates select="query-description"/>
      <xsl:apply-templates select="querys" mode="paragraph"/>
      </SNPARAGRAPH>
    </xsl:when>
    <xsl:when test="@layout='list'">
      <SNPARAGRAPH>
      <xsl:apply-templates select="query-description"/>
      </SNPARAGRAPH>
      <SNPARAGRAPH/>
      <xsl:apply-templates select="querys" mode="list"/>
    </xsl:when>
  </xsl:choose>
  <SNPARAGRAPH/>
    <EMBOBJ link="true" linkcolor="FF0000">
      <xsl:attribute name="object">
        <xsl:value-of select="ancestor::investigation/@name"/>_<xsl:value-of select="name(ancestor::*[../../investigation])"/>_<xsl:number level="any"/>_<xsl:number/>
      </xsl:attribute>
    </EMBOBJ>
  <SNPARAGRAPH/>
</xsl:template>


<xsl:template match="query-response" mode="response">
  <SUPERNOTES locked="true">
    <xsl:attribute name="ID">
      <xsl:value-of select="ancestor::investigation/@name"/>_<xsl:value-of select="name(ancestor::*[../../investigation])"/>_<xsl:number level="any"/>_<xsl:number value="position()"/>
    </xsl:attribute>
    <xsl:attribute name="name">
<xsl:text>Question </xsl:text>
<xsl:call-template name="query-response-position"/>
 <xsl:text>. </xsl:text>

<!--
<xsl:value-of select="name(ancestor::*[../../investigation])"/><xsl:text> </xsl:text>

<xsl:number value="position()"/>
-->

<xsl:value-of select="@type"/>
<xsl:text> </xsl:text>
<xsl:value-of select="@title"/>

    </xsl:attribute>
    <EMBOBJ object="teemss_titlebar.bmp"/>
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:apply-templates select="query-description"/>
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:apply-templates select="querys" mode="response"/>
    <SNPARAGRAPH/>
  </SUPERNOTES>
</xsl:template>


<xsl:template match="query-description">
    <xsl:value-of select="normalize-space(.) "/><xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="querys" mode="paragraph">
  <xsl:apply-templates select="query" mode="paragraph"/>
</xsl:template>

<xsl:template match="querys" mode="list">
  <xsl:apply-templates select="query" mode="list"/>
</xsl:template>

<xsl:template match="querys" mode="response">
  <xsl:for-each select="query">
    <SNPARAGRAPH>
      <xsl:call-template name="query-response-position"/><xsl:text>.</xsl:text>
      <xsl:number value="position()" format="1"/><xsl:text> </xsl:text>
      <xsl:value-of select="normalize-space(.)"/>
    </SNPARAGRAPH>
    <xsl:if test="@drawing-response='true'">
      <SNPARAGRAPH/>
        <xsl:element name="EMBOBJ">
          <xsl:attribute name="w">140</xsl:attribute>
          <xsl:attribute name="h">100</xsl:attribute>
          <xsl:element name="DRAWING">
            <xsl:attribute name="ID">query_drawing_<xsl:number level="any"/></xsl:attribute>
            <xsl:attribute name="name"><xsl:value-of select="ancestor::investigation/@title"/> <xsl:value-of select="name(ancestor::*[../../investigation])"/> <xsl:number value="position()"/></xsl:attribute>
          </xsl:element>
        </xsl:element>
      <SNPARAGRAPH/>
    </xsl:if>
    <xsl:if test="@note-response='true'">
      <SNPARAGRAPH/>
        <xsl:element name="EMBOBJ">
          <xsl:attribute name="w">140</xsl:attribute>
          <xsl:attribute name="h">60</xsl:attribute>
          <xsl:element name="NOTES">
            <xsl:attribute name="ID">query_note_<xsl:number level="any"/></xsl:attribute>
            <xsl:attribute name="name">query_note_<xsl:number level="any"/></xsl:attribute>
          </xsl:element>
        </xsl:element>
      <SNPARAGRAPH/>
    </xsl:if>
    <SNPARAGRAPH>
    </SNPARAGRAPH>
  </xsl:for-each>
</xsl:template>


<xsl:template match="query" mode="paragraph">
    <xsl:value-of select="normalize-space(.)"/>
    <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="query" mode="list">
  <xsl:variable name="format_depth">
    <xsl:value-of select="count(ancestor::steps)"/>
  </xsl:variable>
  <xsl:variable name="format_label">
    <xsl:choose>
      <xsl:when test="$format_depth ='1'">a</xsl:when>
      <xsl:when test="$format_depth ='2'">i</xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:element name="SNPARAGRAPH">
    <xsl:if test="$format_depth='1'">
      <xsl:attribute name="indent">2</xsl:attribute>
    </xsl:if>
    <xsl:if test="$format_depth='2'">
      <xsl:attribute name="indent">4</xsl:attribute>
    </xsl:if>
    <xsl:number value="position()" format="{$format_label}"/>. <xsl:value-of select="normalize-space(text()[position()=1])"/>
  </xsl:element>
</xsl:template>

<xsl:template name="query-response-position">
  <xsl:variable name="section_id">
    <xsl:value-of select="generate-id(ancestor::*[../../investigation])"/>
  </xsl:variable>
  <xsl:number level="any" 
  count="query-response[generate-id(ancestor::*[../../investigation]) = $section_id]"/>
</xsl:template>

<xsl:template match="ext-image-sequence">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="ext-image">
  <xsl:if test="preceding-sibling::node()[(self::text() and normalize-space(.)!='') or self::*]">
    <SNPARAGRAPH/>
  </xsl:if>
  <EMBOBJ>
    <IMAGE name="@name" locked="true">
      <xsl:attribute name="ID">Image_<xsl:number level="any"/>       
      </xsl:attribute>
      <xsl:attribute name="url">../images/<xsl:value-of select="ancestor::unit/@name"/>/<xsl:value-of select="ancestor::investigation/@name"/>/<xsl:value-of select="@name"/>/PALM_TINY_<xsl:value-of select="@name"/>.bmp</xsl:attribute>
    </IMAGE>
  </EMBOBJ>
</xsl:template>


<xsl:template name="level_label">
  <xsl:variable name="depth">
    <xsl:value-of select="count(ancestor::steps)"/>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="$depth ='1'">1</xsl:when>
    <xsl:when test="$depth ='2'">a</xsl:when>
    <xsl:when test="$depth >='3'">i</xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="text()[normalize-space(.)!='']">
  <SNPARAGRAPH>
    <xsl:value-of select="normalize-space(.)"/>
  </SNPARAGRAPH>
  <SNPARAGRAPH/>
</xsl:template>

<!-- Code added to parse datacollectors.xml
     For every datacollector-link this copies the 
     DATACOLLECTOR that has a name equal to the link's type
     as it copies if replaces:
      DATACOLLECTOR/@ID,PROBE/@ID,LINE/@datasource,INTPROBETRANS/@probe
     with itself plus a number unique to the instanceof of 
     datacollector-link 
-->
<xsl:template match="datacollector-link">
  <EMBOBJ link="true" linkcolor="FF0000">
	<xsl:call-template name="datacollector-ref">
   	  <xsl:with-param name="type"><xsl:value-of select="@type"/>
	  </xsl:with-param>
    </xsl:call-template>
   </EMBOBJ>
</xsl:template>

<xsl:template name="datacollector-ref">
  <xsl:param name="type"/>
  <xsl:apply-templates 
    select="document('datacollectors.xml')/dc-list/DATACOLLECTOR[@ID=$type]"
    mode="dc-copy">
     <xsl:with-param name="sequencenumber"><xsl:number level="any"/>
     </xsl:with-param>
     <xsl:with-param name="data-folder-id">
        <xsl:value-of select="ancestor::investigation/@name"/>-saved-datasets
     </xsl:with-param>
   </xsl:apply-templates>
</xsl:template>

<xsl:template match="*|@*" mode="dc-copy">
   <xsl:param name="sequencenumber"/>
   <xsl:param name="data-folder-id"/>
   <xsl:copy>
     <xsl:apply-templates select="*|@*" mode="dc-copy">
        <xsl:with-param name="sequencenumber">
          <xsl:value-of select="$sequencenumber"/>
        </xsl:with-param>
        <xsl:with-param name="data-folder-id">
          <xsl:value-of select="$data-folder-id"/>
        </xsl:with-param>
     </xsl:apply-templates>
   </xsl:copy>
</xsl:template>


<xsl:template 
  match="DATACOLLECTOR/@ID | PROBE/@ID | LINE/@datasource | INTPROBETRANS/@probe" 
  mode="dc-copy">
   <xsl:param name="sequencenumber"/>
<xsl:attribute name="{name(.)}">
<xsl:value-of select="."/>-<xsl:value-of select="$sequencenumber"/>
</xsl:attribute>
</xsl:template>

<xsl:template match="GRAPH" mode="dc-copy">
   <xsl:param name="sequencenumber"/>
   <xsl:param name="data-folder-id"/>
   <xsl:copy>
     <xsl:apply-templates select="@*" mode="dc-copy">
        <xsl:with-param name="sequencenumber">
          <xsl:value-of select="$sequencenumber"/>
        </xsl:with-param>
	 </xsl:apply-templates>
     <DATAFOLDER object="{normalize-space($data-folder-id)}"/>
     <xsl:apply-templates select="*" mode="dc-copy">
        <xsl:with-param name="sequencenumber">
          <xsl:value-of select="$sequencenumber"/>
        </xsl:with-param>
     </xsl:apply-templates>
   </xsl:copy>
</xsl:template>


</xsl:stylesheet>







