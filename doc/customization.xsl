<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:import href="docbook.xsl"/>

<xsl:template match="table">
  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>
  <xsl:variable name="prop-columns"
    select=".//colspec[contains(@colwidth, '*')]"/>

  <fo:block>
    <xsl:attribute name="span">
      <xsl:choose>
        <xsl:when test="@pgwide=1">all</xsl:when>
        <xsl:otherwise>none</xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>

<!--
    <fo:table-and-caption id="{$id}"
                          xsl:use-attribute-sets="formal.object.properties"
                          keep-together.within-column="1">
      <fo:table-caption>
-->

        <fo:block xsl:use-attribute-sets="formal.title.properties">
          <xsl:apply-templates select="." mode="object.title.markup"/>
        </fo:block>

<!--
      </fo:table-caption>
-->

      <fo:table>
        <xsl:call-template name="table.frame"/>
        <xsl:if test="count($prop-columns) != 0">
          <xsl:attribute name="table-layout">fixed</xsl:attribute>
        </xsl:if>
        <xsl:apply-templates select="tgroup"/>
      </fo:table>

<!--
    </fo:table-and-caption>
-->

  </fo:block>
</xsl:template>



<xsl:template match="tocentry|tocfront|tocback">
  <fo:block text-align-last="justify"
            end-indent="2pc">
<!--        last-line-end-indent="-2pc">   -->
    <fo:inline keep-with-next.within-line="always">
      <xsl:choose>
        <xsl:when test="@linkend">
          <fo:basic-link internal-destination="{@linkend}">
            <xsl:apply-templates/>
          </fo:basic-link>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </fo:inline>

    <xsl:choose>
      <xsl:when test="@linkend">
        <fo:inline keep-together.within-line="always">
          <xsl:text> </xsl:text>
          <fo:leader leader-pattern="dots"
                     keep-with-next.within-line="always"/>
          <xsl:text> </xsl:text>
          <fo:basic-link internal-destination="{@linkend}">
            <xsl:choose>
              <xsl:when test="@pagenum">
                <xsl:value-of select="@pagenum"/>
              </xsl:when>
              <xsl:otherwise>
                <fo:page-number-citation ref-id="{@linkend}"/>
              </xsl:otherwise>
            </xsl:choose>
          </fo:basic-link>
        </fo:inline>
      </xsl:when>
      <xsl:when test="@pagenum">
        <fo:inline keep-together.within-line="always">
          <xsl:text> </xsl:text>
          <fo:leader leader-pattern="dots"
                     keep-with-next.within-line="always"/>
          <xsl:text> </xsl:text>
          <xsl:value-of select="@pagenum"/>
        </fo:inline>
      </xsl:when>
      <xsl:otherwise>
        <!-- just the leaders, what else can I do? -->
        <fo:inline keep-together.within-line="always">
          <xsl:text> </xsl:text>
          <fo:leader leader-pattern="space"
                     keep-with-next.within-line="always"/>
        </fo:inline>
      </xsl:otherwise>
    </xsl:choose>
  </fo:block>
</xsl:template>

<!-- from autotoc.xsl -->


<xsl:template name="toc.line">
  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>

  <xsl:variable name="label">
    <xsl:apply-templates select="." mode="label.markup"/>
  </xsl:variable>

  <fo:block text-align-last="justify"
            end-indent="{$toc.indent.width}pt">
<!--        last-line-end-indent="-{$toc.indent.width}pt">  -->
    <fo:inline keep-with-next.within-line="always">
      <xsl:if test="$label != ''">
        <xsl:copy-of select="$label"/>
        <xsl:value-of select="$autotoc.label.separator"/>
      </xsl:if>
      <xsl:apply-templates select="." mode="title.markup"/>
    </fo:inline>
    <fo:inline keep-together.within-line="always">
      <xsl:text> </xsl:text>
      <fo:leader leader-pattern="dots"
                 keep-with-next.within-line="always"/>
      <xsl:text> </xsl:text>
      <fo:basic-link internal-destination="{$id}">
<!--                     xsl:use-attribute-sets="xref.properties">-->
        <fo:page-number-citation ref-id="{$id}"/>
      </fo:basic-link>
    </fo:inline>
  </fo:block>
</xsl:template>


</xsl:stylesheet>
