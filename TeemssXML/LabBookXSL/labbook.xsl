<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">


<xsl:output method="xml" indent="yes"
  doctype-public="-//Concord.ORG//DTD LabBook Description//EN" 
  doctype-system="../../DTD/labbook.dtd"/>

<xsl:strip-space element=*/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="project">
  <LABBOOK>
    <xsl:copy-of select="document('ccprobe.xml')"/>
    <FOLDER ID="{title}" name="{title}">
    <xsl:apply-templates select="unit"/>
    </FOLDER>
  </LABBOOK>
</xsl:template>

<xsl:template match="unit">
  <FOLDER ID="{@name}" name="{title}">
    <xsl:apply-templates select="investigation" mode="investigate"/>
    <FOLDER ID="{@name}-response" name="Responses">
      <xsl:apply-templates select="investigation" mode="response"/>
    </FOLDER>
  </FOLDER>
</xsl:template>

<xsl:template match="investigation" mode="investigate">
  <FOLDER ID="{@name}" name="{title}" view="paging">
    <xsl:apply-templates select="intro"/>
    <xsl:apply-templates select="think"/>
    <xsl:apply-templates select="materials"/>
    <xsl:apply-templates select="safety"/>
    <xsl:apply-templates select="trial" mode="investigate"/>
    <xsl:apply-templates select="hints"/>
    <xsl:apply-templates select="analysis" mode="investigate"/>
  </FOLDER>
</xsl:template>

<xsl:template match="investigation" mode="response">
  <FOLDER ID="{@name}-response" name="{title} Responses" view="paging">
    <xsl:apply-templates select="trial" mode="response"/>
    <xsl:apply-templates select="analysis" mode="response"/>
  </FOLDER>
</xsl:template>

<xsl:template match="intro">
  <SUPERNOTES ID="{../@name}-intro" name="Introduction">
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

<xsl:template match="think">
  <SUPERNOTES ID="{../@name}-think" name="Thinking About the Question">
    <SNPARAGRAPH linkcolor="0000FF">
      Thinking About the Question
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <SNPARAGRAPH linkcolor="000F0F">
      <xsl:value-of select="normalize-space(../question)"/>
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:for-each select="p">
      <SNPARAGRAPH>
        <xsl:value-of select="normalize-space(.)"/>
      </SNPARAGRAPH>
      <SNPARAGRAPH/>
    </xsl:for-each>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="materials">
  <SUPERNOTES ID="{../@name}-materials" name="Materials">
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
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:value-of select="../title"/> Analysis
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
    <xsl:apply-templates/>
  </SUPERNOTES>
</xsl:template>

<xsl:template match="analysis" mode="response">
  <SUPERNOTES ID="{../@name}-analysis-response" name="Analysis">
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:value-of select="../title"/> Analysis
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
  </SUPERNOTES>
</xsl:template>


<xsl:template match="trial" mode="investigate">
  <xsl:element name="SUPERNOTES">
    <xsl:attribute name="ID">
      <xsl:value-of select="../@name"/>_trial_<xsl:number value="position()" format="I"/>
    </xsl:attribute>
    <xsl:attribute name="name">Trial <xsl:number value="position()" format="I"/>      
    </xsl:attribute>
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
      <xsl:value-of select="../@name"/>_trial_<xsl:number value="position()" format="I"/>_response
    </xsl:attribute>
    <xsl:attribute name="name">Trial <xsl:number value="position()" format="I"/> Responses      
    </xsl:attribute>
    <xsl:apply-templates select="query-response" mode="response"/>
  </xsl:element>
</xsl:template>

<xsl:template match="instructions">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="instruction">
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
  <SNPARAGRAPH linkcolor="FF0000">
    click here to answer
  </SNPARAGRAPH>
  <SNPARAGRAPH/>
</xsl:template>

<xsl:template match="query-response" mode="response">
  <xsl:element name="SUPERNOTES">
    <xsl:attribute name="ID">
      <xsl:value-of select="../@name"/>_trial_<xsl:number value="position()" format="I"/>_response
    </xsl:attribute>

    <xsl:attribute name="name">Trial <xsl:number value="position()" format="I"/> Responses      
    </xsl:attribute>
    <SNPARAGRAPH linkcolor="0000FF">
      <xsl:apply-templates select="query-description"/>
    </SNPARAGRAPH>
    <SNPARAGRAPH/>
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

<xsl:template match="query" mode="paragraph">
    <xsl:value-of select="normalize-space(.)"/>
    <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="query" mode="list">
    <SNPARAGRAPH>
      <xsl:value-of select="normalize-space(.) "/>
    </SNPARAGRAPH>
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

