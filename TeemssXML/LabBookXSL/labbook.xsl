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
      <xsl:copy-of select="document('datacollectors.xml')"/>
    </FOLDER>
    <FOLDER ID="{title}" name="{title}">
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
    <xsl:apply-templates select="steps"/>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="analysis" mode="response">
  <xsl:element name="FOLDER">
    <xsl:attribute name="ID">
      <xsl:value-of select="../@name"/>_analysis_response</xsl:attribute>
    <xsl:attribute name="name">Analysis Responses</xsl:attribute>
<!--    <xsl:apply-templates select="steps" mode="response"/>    -->
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
    <xsl:apply-templates/>
    <SNPARAGRAPH/>
  </xsl:element>
</xsl:template>

<xsl:template match="trial" mode="response">
  <xsl:element name="FOLDER">
    <xsl:attribute name="ID">
      <xsl:value-of select="../@name"/>_trial_<xsl:number value="position()" format="I"/>_response</xsl:attribute>
    <xsl:attribute name="name">Trial <xsl:number value="position()" format="I"/> Responses      
    </xsl:attribute>
    <xsl:apply-templates select="query-response" mode="response"/>
  </xsl:element>
</xsl:template>

<xsl:template match="instructions">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="instruction">
  <xsl:if test="position()=3">---aircart-insert-part2---</xsl:if>
  <xsl:if test="position()=5">---aircart-insert-part3---</xsl:if>
  <xsl:if test="position()=7">---aircart-insert-part4---</xsl:if>
  <SNPARAGRAPH linkcolor="0000FF"><xsl:value-of select="@title"/></SNPARAGRAPH>
  <SNPARAGRAPH/>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="steps">
  <xsl:apply-templates select="step | query-response" mode="step"/>
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

<!--

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
  </xsl:element>

  <xsl:if test="$format_depth='1'"><SNPARAGRAPH/></xsl:if>
  <xsl:if test="position()=last()">
    <xsl:if test="$format_depth!='1'">
      <SNPARAGRAPH/>
    </xsl:if>
  </xsl:if>
  <xsl:apply-templates select="text()[position()!=1]|*"/>
</xsl:template>

-->

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
      <xsl:value-of select="name(ancestor::investigation)"/><xsl:text> </xsl:text><xsl:value-of select="name(ancestor::*[../../investigation])"/> <xsl:number value="position()"/>
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
    <xsl:value-of select="normalize-space(.) "/>
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
      <xsl:value-of select="normalize-space(.) "/>
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
    <SNPARAGRAPH>
      <xsl:value-of select="normalize-space(.) "/>
    </SNPARAGRAPH>
</xsl:template>

<xsl:template match="datacollector-link">
  <xsl:element name="EMBOBJ">
    <xsl:attribute name="object"><xsl:value-of select="@type"/></xsl:attribute>
    <xsl:attribute name="link">true</xsl:attribute>
    <xsl:attribute name="linkcolor">FF0000</xsl:attribute>
  </xsl:element>
</xsl:template>


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
      <xsl:attribute name="name">PALM_TINY_<xsl:value-of select="../@name"/>.bmp</xsl:attribute>
      <xsl:attribute name="url">../images/<xsl:value-of select="ancestor::unit/@name"/>/<xsl:value-of select="ancestor::investigation/@name"/>/<xsl:value-of select="@name"/>/PALM_TINY_<xsl:value-of select="@name"/>.bmp</xsl:attribute>
    </xsl:element>
  </xsl:element>
</xsl:template>

<!-- /node()[position()=1 and name(.)='ext-image'] -->

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

</xsl:stylesheet>

