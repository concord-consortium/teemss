<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">


<xsl:output method="xml" indent="yes"
  doctype-public="-//Concord.ORG//DTD LabBook Description//EN" 
  doctype-system="../../DTD/labbook.dtd"/>

<xsl:strip-space elements="*"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="project">
  <LABBOOK>
    <FOLDER ID="folder-ccprobe1" name="CCProbe">
      <xsl:copy-of select="document('ccprobe.xml')"/>
    </FOLDER>
    <FOLDER ID="{title}" name="{title}">
      <xsl:copy-of select="document('about-teemss.xml')"/>
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
  <SUPERNOTES ID="{../@name}-intro" name="Introduction">
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
  <SUPERNOTES ID="{../@name}-think" name="Thinking About the Question">
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
  <SUPERNOTES ID="{../@name}-materials" name="Materials">
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
  <SUPERNOTES ID="{../@name}-safety" name="Safety">
    <EMBOBJ object="teemss_titlebar.bmp"/>
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:value-of select="../title"/> Safety
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:apply-templates select="item"/>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="item">
  <SNPARAGRAPH>
    - <xsl:value-of select="normalize-space(.)"/>
  </SNPARAGRAPH>
  <SNPARAGRAPH/>
</xsl:template>

<xsl:template match="hints">
  <SUPERNOTES ID="{../@name}-hints" name="Technical Hints">
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

<xsl:template match="analysis" mode="investigate">
  <SUPERNOTES ID="{../@name}-analysis" name="Analysis">
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
  <xsl:element name="SUPERNOTES">
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
  </xsl:element>
</xsl:template>

<xsl:template match="trial" mode="response">
  <xsl:element name="FOLDER">
    <xsl:attribute name="ID">
      <xsl:value-of select="../@name"/>_trial_<xsl:number value="position()" format="I"/>_response</xsl:attribute>
    <xsl:attribute name="name">Trial <xsl:number value="position()" format="I"/> Responses      
    </xsl:attribute>
    <xsl:apply-templates select="steps" mode="response"/>
  </xsl:element>
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
    <xsl:apply-templates select="query-description"/>    
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
    <xsl:element name="EMBOBJ">
      <xsl:attribute name="object">
        <xsl:value-of select="ancestor::investigation/@name"/>_<xsl:value-of select="name(ancestor::*[../../investigation])"/>_<xsl:number level="any"/>_<xsl:number/>
      </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
  </xsl:element>
  <SNPARAGRAPH/>
</xsl:template>


<xsl:template match="query-response" mode="response">
  <xsl:element name="SUPERNOTES">
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
  </xsl:element>
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

<!--

<xsl:template match="datacollector-link">
  <xsl:call-template name="{@type}">
    <xsl:with-param name="sequencenumber">
      <xsl:number level="any"/>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>


  <xsl:variable name="type">
    <xsl:value-of select="@type"/>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="$type='dc-temperature1'"/>
      <xsl:call-template name="dc-temperature1">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-temperature2'"/>
      <xsl:call-template name="dc-temperature2">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-temperature3'"/>
      <xsl:call-template name="dc-temperature3">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-temperature4'"/>
      <xsl:call-template name="dc-temperature4">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-temperature5'"/>
      <xsl:call-template name="dc-temperature5">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-temperature6'"/>
      <xsl:call-template name="dc-temperature6">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-voltage-current-voltage'"/>
      <xsl:call-template name="dc-voltage-current-voltage">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-voltage-current-power'"/>
      <xsl:call-template name="dc-voltage-current-power">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-voltage-current-energy'"/>
      <xsl:call-template name="dc-voltage-current-energy">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-light-4000'"/>
      <xsl:call-template name="dc-light-4000">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-light-100000'"/>
      <xsl:call-template name="dc-light-100000">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-force1'"/>
      <xsl:call-template name="dc-force1">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-force2'"/>
      <xsl:call-template name="dc-force2">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-force3'"/>
      <xsl:call-template name="dc-force3">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-force4'"/>
      <xsl:call-template name="dc-force4">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-smartwheel-pos1'"/>
      <xsl:call-template name="dc-smartwheel-pos1">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-smartwheel-pos2'"/>
      <xsl:call-template name="dc-smartwheel-pos2">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-smartwheel-vel1'"/>
      <xsl:call-template name="dc-smartwheel-vel1">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-smartwheel-vel2'"/>
      <xsl:call-template name="dc-smartwheel-vel2">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$type='dc-smartwheel-vel3'"/>
      <xsl:call-template name="dc-smartwheel-vel3">
        <xsl:with-param name="sequencenumber">
          <xsl:number level="any"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
  </xsl:xhoose>
</xsl:template>
-->

<xsl:template match="ext-image-sequence">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="ext-image">
  <xsl:if test="preceding-sibling::node()[(self::text() and normalize-space(.)!='') or self::*]">
    <SNPARAGRAPH/>
  </xsl:if>
  <xsl:element name="EMBOBJ">
    <xsl:element name="IMAGE">
      <xsl:attribute name="ID">Image_<xsl:number level="any"/>       
      </xsl:attribute>
      <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
      <xsl:attribute name="url">../images/<xsl:value-of select="ancestor::unit/@name"/>/<xsl:value-of select="ancestor::investigation/@name"/>/<xsl:value-of select="@name"/>/PALM_TINY_<xsl:value-of select="@name"/>.bmp</xsl:attribute>
    </xsl:element>
  </xsl:element>
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


<xsl:template match="datacollector-link[@type='dc-temperature1']">
  <xsl:variable name="sequencenumber">
      <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>temperature1-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR"> 
      <xsl:attribute name="ID">
        <xsl:text>temperature1-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Temperature</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">temp1-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Temperature</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Mode</xsl:attribute>
          <xsl:attribute name="value">C</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">60</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">10</xsl:attribute>
          <xsl:attribute name="max">40</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">temp1-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='dc-temperature2']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>temperature2-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>temperature2-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Temperature</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">temp2-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Temperature</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Mode</xsl:attribute>
          <xsl:attribute name="value">C</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">120</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">10</xsl:attribute>
          <xsl:attribute name="max">40</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">temp2-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='dc-temperature3']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>temperature3-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>temperature3-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Temperature</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">temp3-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Temperature</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Mode</xsl:attribute>
          <xsl:attribute name="value">C</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">150</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">20</xsl:attribute>
          <xsl:attribute name="max">30</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">temp3-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='dc-temperature4']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>temperature4-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>temperature4-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Temperature</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">temp4-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Temperature</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Mode</xsl:attribute>
          <xsl:attribute name="value">C</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">60</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">20</xsl:attribute>
          <xsl:attribute name="max">35</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">temp4-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='dc-temperature5']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>temperature5-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>temperature5-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Temperature</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">temp5-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Temperature</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Mode</xsl:attribute>
          <xsl:attribute name="value">C</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">50</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">20</xsl:attribute>
          <xsl:attribute name="max">25</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">temp5-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='dc-temperature6']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>temperature6-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>temperature6-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Temperature</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">temp6-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Temperature</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Mode</xsl:attribute>
          <xsl:attribute name="value">C</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">50</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">20</xsl:attribute>
          <xsl:attribute name="max">50</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">temp6-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>




<xsl:template match="datacollector-link[@type='dc-voltage-current-voltage']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>voltage-current-voltage-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>

    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>voltage-current-voltage-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute name="name">Measure Voltage"</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">vi1-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">VoltageCurrent</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Speed</xsl:attribute>
          <xsl:attribute name="value">3 per second</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Version</xsl:attribute>
          <xsl:attribute name="value">2.0</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">2</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">40</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">3</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">-1</xsl:attribute>
          <xsl:attribute name="max">1</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">6</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">50</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Voltage</xsl:attribute>
            <xsl:attribute name="probe">vi1-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">1</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Current</xsl:attribute>
            <xsl:attribute name="probe">vi11-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">2</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Power</xsl:attribute>
            <xsl:attribute name="probe">vi1-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">3</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Energy</xsl:attribute>
            <xsl:attribute name="probe">vi11-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>


<xsl:template match="datacollector-link[@type='voltage-current-power']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>voltage-current-power-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>voltage-current-power-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Power"</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">vi2-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">VoltageCurrent</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
          <xsl:element name="PROP">
            <xsl:attribute name="name">Speed</xsl:attribute>
            <xsl:attribute name="value">3 per second</xsl:attribute>
          </xsl:element>
          <xsl:element name="PROP">
            <xsl:attribute name="name">Version</xsl:attribute>
            <xsl:attribute name="value">2.0</xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">2</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">40</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">-10</xsl:attribute>
          <xsl:attribute name="max">10</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">-1</xsl:attribute>
          <xsl:attribute name="max">1</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">6</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">50</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Voltage</xsl:attribute>
            <xsl:attribute name="probe">vi2-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">1</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Current</xsl:attribute>
            <xsl:attribute name="probe">vi2-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">2</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Power</xsl:attribute>
            <xsl:attribute name="probe">vi2-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">3</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Energy</xsl:attribute>
            <xsl:attribute name="probe">vi2-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='dc-voltage-current-energy']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>voltage-current-energy-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>voltage-current-energy-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Energy"</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">vi3-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">VoltageCurrent</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Speed</xsl:attribute>
          <xsl:attribute name="value">3 per second</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Version</xsl:attribute>
          <xsl:attribute name="value">2.0</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">3</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">40</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">-10</xsl:attribute>
          <xsl:attribute name="max">10</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">-1</xsl:attribute>
          <xsl:attribute name="max">1</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">6</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">50</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Voltage</xsl:attribute>
            <xsl:attribute name="probe">vi3-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">1</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Current</xsl:attribute>
            <xsl:attribute name="probe">vi3-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">2</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Power</xsl:attribute>
            <xsl:attribute name="probe">vi3-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">3</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Energy</xsl:attribute>
            <xsl:attribute name="probe">vi3-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>



<xsl:template match="datacollector-link[@type='light-4000']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>light-4000-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>light-4000-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Light, 4000 Lux</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">light-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Light</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Range</xsl:attribute>
          <xsl:attribute name="value">Dim Light</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Speed</xsl:attribute>
          <xsl:attribute name="value">3 per second</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">60</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">4000</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">light-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='light-100000']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>light-100000-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>light-100000-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Light, 125000 Lux</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">light3-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Light</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Range</xsl:attribute>
          <xsl:attribute name="value">Bright Light</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Speed</xsl:attribute>
          <xsl:attribute name="value">3 per second</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">60</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">125000</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">light3-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='force1']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>force1-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>force1-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Force"</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">f1-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Force</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Mode</xsl:attribute>
          <xsl:attribute name="value">End of Arm</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Range</xsl:attribute>
          <xsl:attribute name="value">+/- 20N</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Speed</xsl:attribute>
          <xsl:attribute name="value">3 per second</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">20</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">-0.1</xsl:attribute>
          <xsl:attribute name="max">0.6</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">f1-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>


<xsl:template match="datacollector-link[@type='force2']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>force2-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>force2-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Force"</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">f2-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Force</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Mode</xsl:attribute>
          <xsl:attribute name="value">End of Arm</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Range</xsl:attribute>
          <xsl:attribute name="value">+/- 20N</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Speed</xsl:attribute>
          <xsl:attribute name="value">400 per second</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">10</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">-20</xsl:attribute>
          <xsl:attribute name="max">0</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">f2-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>











<xsl:template match="datacollector-link[@type='force3']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>force3-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>force3-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Force"</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">f3-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Force</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Mode</xsl:attribute>
          <xsl:attribute name="value">End of Arm</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Range</xsl:attribute>
          <xsl:attribute name="value">+/- 20N</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Speed</xsl:attribute>
          <xsl:attribute name="value">400 per second</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">10</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">4</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">f3-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>











<xsl:template match="datacollector-link[@type='force4']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>force4-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>force4-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Force"</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">f4-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">Force</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Mode</xsl:attribute>
          <xsl:attribute name="value">End of Arm</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Range</xsl:attribute>
          <xsl:attribute name="value">+/- 20N</xsl:attribute>
        </xsl:element>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Speed</xsl:attribute>
          <xsl:attribute name="value">400 per second</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">10</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">-20</xsl:attribute>
          <xsl:attribute name="max">1</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:attribute name="datasource">f4-<xsl:number value="$sequencenumber"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>






<xsl:template match="datacollector-link[@type='smartwheel-pos1']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>smartwheel-pos1-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>smartwheel-pos1-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Position"</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">sw-pos1-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">SmartWheel</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
         <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">40</xsl:attribute>
        </xsl:element>
         <xsl:element name="YAXIS">
          <xsl:attribute name="min">-0.5</xsl:attribute>
          <xsl:attribute name="max">6</xsl:attribute>
        </xsl:element>
         <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Position</xsl:attribute>
            <xsl:attribute name="probe">sw-pos1-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>





<xsl:template match="datacollector-link[@type='smartwheel-pos2']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>smartwheel-pos2-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>smartwheel-pos2-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Position"</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">sw-pos2-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">SmartWheel</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
          <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">10</xsl:attribute>
        </xsl:element>
          <xsl:element name="YAXIS">
          <xsl:attribute name="min">-0.5</xsl:attribute>
          <xsl:attribute name="max">6</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Position</xsl:attribute>
            <xsl:attribute name="probe">sw-pos2-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='smartwheel-vel1']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>smartwheel-vel1-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>smartwheel-vel1-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Velocity</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">sw-vel1-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">SmartWheel</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
         <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">20</xsl:attribute>
        </xsl:element>
         <xsl:element name="YAXIS">
          <xsl:attribute name="min">-1</xsl:attribute>
          <xsl:attribute name="max">1</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Velocity</xsl:attribute>
            <xsl:attribute name="probe">sw-vel1-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='dc-smartwheel-vel2']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>smartwheel-vel2-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>smartwheel-vel2-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Velocity</xsl:attribute>
      <xsl:element name="PROBE">
        <xsl:attribute name="ID">sw-vel2-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">SmartWheel</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
        <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">10</xsl:attribute>
        </xsl:element>
        <xsl:element name="YAXIS">
          <xsl:attribute name="min">-1</xsl:attribute>
          <xsl:attribute name="max">1</xsl:attribute>
        </xsl:element>
        <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Velocity</xsl:attribute>
            <xsl:attribute name="probe">sw-vel2-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="datacollector-link[@type='dc-smartwheel-vel3']">
  <xsl:variable name="sequencenumber">
    <xsl:number level="any"/>
  </xsl:variable>
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object">
      <xsl:text>smartwheel-vel3-</xsl:text>
      <xsl:number value="$sequencenumber"/>
    </xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
    <xsl:element name="DATACOLLECTOR">
      <xsl:attribute name="ID">
        <xsl:text>smartwheel-vel3-</xsl:text>
        <xsl:number value="$sequencenumber"/>
      </xsl:attribute>
      <xsl:attribute  name="name">Measure Velocity</xsl:attribute>
       <xsl:element name="PROBE">
        <xsl:attribute name="ID">sw-vel3-<xsl:number value="$sequencenumber"/>
        </xsl:attribute>
        <xsl:attribute name="probe">SmartWheel</xsl:attribute>
        <xsl:element name="PROP">
          <xsl:attribute name="name">Port</xsl:attribute>
          <xsl:attribute name="value">A</xsl:attribute>
        </xsl:element>
      </xsl:element>
      <xsl:element name="GRAPH">
        <xsl:attribute name="current-line">0</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:element name="DATAFOLDER">
          <xsl:attribute name="object">
            <xsl:value-of select="ancestor::investigation/@name"/>
            <xsl:text>-saved-datasets</xsl:text>
          </xsl:attribute>
        </xsl:element>
         <xsl:element name="XAXIS">
          <xsl:attribute name="min">0</xsl:attribute>
          <xsl:attribute name="max">10</xsl:attribute>
        </xsl:element>
         <xsl:element name="YAXIS">
          <xsl:attribute name="min">-0.5</xsl:attribute>
          <xsl:attribute name="max">0.5</xsl:attribute>
        </xsl:element>
         <xsl:element name="LINE">
          <xsl:attribute name="xaxis">0</xsl:attribute>
          <xsl:attribute name="yaxis">0</xsl:attribute>
          <xsl:element name="INTPROBETRANS">
            <xsl:attribute name="quantity">Velocity</xsl:attribute>
            <xsl:attribute name="probe">sw-vel3-<xsl:number value="$sequencenumber"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:element>
</xsl:template>






</xsl:stylesheet>







