<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<!--
This stylesheet is originally from:  http://www-106.ibm.com/developerworks/xml/library/x-antxsl/
Updated April 29, 2004 by Jacob Kjome
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output
    method="html"
    indent="yes"
    omit-xml-declaration="yes"
    encoding="UTF-8"
    doctype-public="-//W3C//DTD XHTML 1.1//EN"
    doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"/>

<xsl:template match="/">
  <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-US">
    <xsl:comment>XSLT stylesheet used to transform this file:  ant2html.xsl</xsl:comment>
    <xsl:apply-templates select="project"/>
  </html>
</xsl:template>

<xsl:template match="project">
  <head>
    <title>Ant Project Source: <xsl:value-of select="@name"/></title>
    <style type="text/css">/*<![CDATA[*/
    html, body { font: 14px Arial, Helvetica, sans-serif; }
    caption { font-size: 16px; font-weight: bold; text-align: left; }
    tbody { border-bottom: 20px solid black; }
    th { text-align: right; white-space: nowrap; padding-left: 25px; }
    /*]]>*/</style>
  </head>
  <body bgcolor="#ffffff" marginheight="2" marginwidth="2" topmargin="2" leftmargin="2">
    <table border="1" cellspacing="0" cellpadding="2">
      <tr>
        <td valign="TOP" width="20%">
          <h2><a name="toc">Table of Contents</a></h2>
          <b><big><a href="#project">Project Attributes</a></big></b><br/><br/>

          <b><big><a href="#properties">Properties</a></big></b><br/>
<!--
          <xsl:for-each select="./property/@name">
            <xsl:sort/>
            <xsl:variable name="propName" select="."/>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
            <xsl:element name="a">
              <xsl:attribute name="href">
                <xsl:value-of select="concat('#property-',$propName)"/>
              </xsl:attribute>
              <xsl:value-of select="$propName"/>
            </xsl:element>
            <br/>
          </xsl:for-each>
-->

          <br/>
          <a name="toc-targets"/>
          <b><big><a href="#targets">Targets</a></big></b><br/>
          <xsl:for-each select="./target">
            <xsl:sort select="@name"/>
            <xsl:variable name="tarName" select="@name"/>
            <xsl:text disable-output-escaping="yes">&nbsp;&nbsp;&nbsp;</xsl:text>
            <xsl:element name="a">
              <xsl:attribute name="href">
                <xsl:value-of select="concat('#target-',$tarName)"/>
              </xsl:attribute>
              <xsl:value-of select="$tarName"/>
            </xsl:element>
            <br/>
          </xsl:for-each>
        </td>

        <td valign="top" width="80%">
          <!-- Begin project data -->
          <table border="0" cellspacing="0" cellpadding="5">
            <caption><a name="#project"/>Project Information</caption>
            <tbody>
              <tr>
                <th>Name:</th>
                <td>
                  <xsl:value-of select="@name"/>
                </td>
              </tr>
              <tr>
                <th>Base directory:</th>
                <td>
                  <xsl:choose>
                    <xsl:when test="@basedir='.'">
                      <i>current-working-directory</i>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="@basedir"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </td>
              </tr>
              <tr>
                <th>Default target:</th>
                <td>
                  <xsl:call-template name="formatTargetList">
                    <xsl:with-param name="targets" select="@default"/>
                  </xsl:call-template>
                </td>
              </tr>
            </tbody>
          </table>

          <hr/>

          <!-- Begin project data -->
          <table border="0" cellspacing="0" cellpadding="5">
            <caption><a name="properties"/><a href="#toc">Project Properties</a></caption>
            <tbody>
              <xsl:for-each select="./property/@name">
                <xsl:sort/>
                <tr>
                  <th>
                    <xsl:element name="a">
                      <xsl:attribute name="name">
                        <xsl:text>property-</xsl:text><xsl:value-of select="."/>
                      </xsl:attribute>
                    </xsl:element>
                    <xsl:value-of select="."/>:
                  </th>
                  <td>
                    <xsl:choose>
                      <xsl:when test="count(../@location) > 0">
                        <xsl:value-of select="../@location"/>
                      </xsl:when>
                      <xsl:when test="count(../@value) > 0">
                        <xsl:value-of select="../@value"/>
                      </xsl:when>
                    </xsl:choose>
                  </td>
                </tr>
              </xsl:for-each>
            </tbody>
          </table>

          <hr/>

          <!-- Begin project data -->
          <table border="0" cellspacing="0" cellpadding="5">
            <caption><a name="targets"/></caption>

            <xsl:for-each select="./target">
              <xsl:sort select="@name"/>
              <tbody>
              <tr>
                <th>
                  <xsl:element name="a">
                    <xsl:attribute name="name">
                      <xsl:text>target-</xsl:text><xsl:value-of select="@name"/>
                    </xsl:attribute>
                  </xsl:element>
                  Target:
                </th>
                <td>
                  <xsl:value-of select="@name"/>
                </td>
              </tr>

              <xsl:if test="count(./@description) > 0">
                <tr>
                  <th>Description:</th>
                  <td>
                    <xsl:value-of select="@description"/>
                  </td>
                </tr>
              </xsl:if>

              <xsl:if test="count(./@depends) > 0">
                <tr>
                  <th>Dependencies:</th>
                  <td>
                    <xsl:call-template name="formatTargetList">
                      <xsl:with-param name="targets" select="@depends"/>
                    </xsl:call-template>
                  </td>
                </tr>
              </xsl:if>

              <tr>
                <th valign="top">Tasks:</th>
                <td>
                  <xsl:choose>
                    <xsl:when test="count(child::node()) > 0">
                      <pre>
                      <xsl:apply-templates select="child::node()"/>
                      </pre>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>None</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </td>
              </tr>

              <tr>
                <td colspan="2">
                  <xsl:element name="a">
                    <xsl:attribute name="href">
                      <xsl:text>#toc-targets</xsl:text>
                    </xsl:attribute>
                    <xsl:text>Return to targets</xsl:text>
                  </xsl:element>
                </td>
              </tr>

              <tr>
                <td colspan="2">
                  <xsl:element name="a">
                    <xsl:attribute name="href">
                      <xsl:text>#toc</xsl:text>
                    </xsl:attribute>
                    <xsl:text>Return to table of contents</xsl:text>
                  </xsl:element>
                </td>
              </tr>

              <xsl:if test="position() &lt; last()">
                <tr><td colspan="2"><hr/></td></tr>
              </xsl:if>
              </tbody>
            </xsl:for-each>
          </table>
        </td>
      </tr>
    </table>
  </body>
</xsl:template>

  <!--
    =========================================================================
      Purpose:  Copy each node and attribute, exactly as found, to the output
                tree.
    =========================================================================
  -->
  <xsl:template match="node()" name="writeTask">
    <xsl:param    name="indent"   select="string('')"/>
    <xsl:variable name="nodeName" select="name(.)"/>
    <xsl:if test="count(@*) > 0">
      <xsl:value-of select="$indent"/>&lt;<xsl:value-of select="$nodeName"/>
      <xsl:for-each select="@*">
        <xsl:if test="position() > 0"><xsl:value-of select="'&#10;'"/></xsl:if>
        <xsl:value-of select="concat('    ', $indent, name(),'=&quot;',.,'&quot;')"/>
        <xsl:if test="position() = last()">
          <xsl:choose>
            <xsl:when test="count(../*) > 0">&gt;</xsl:when>
            <xsl:otherwise>/&gt;</xsl:otherwise>
          </xsl:choose>
        </xsl:if>
      </xsl:for-each>
      <xsl:value-of select="'&#10;'"/>
    </xsl:if>

    <xsl:for-each select="child::node()">
      <xsl:call-template name="writeTask">
        <xsl:with-param name="indent" select="concat($indent, '  ')"/>
      </xsl:call-template>
    </xsl:for-each>

    <!--xsl:value-of select="$indent"/--><xsl:if test="count(child::node()) > 0">&lt;/<xsl:value-of select="$nodeName"/>&gt;</xsl:if>

  </xsl:template>

  <!--
    =========================================================================
      Purpose:  Ignore comments imbedded into the text.
    =========================================================================
  -->
  <xsl:template match="comment()"/>

  <!--
    =========================================================================
      Purpose:  Format a list of target names as references.
    =========================================================================
  -->
  <xsl:template name="formatTargetList">
    <xsl:param    name="targets" select="string('')"/>

    <xsl:variable name="list"    select="normalize-space($targets)"/>
    <xsl:variable name="first"   select="normalize-space(substring-before($targets,','))"/>
    <xsl:variable name="rest"    select="normalize-space(substring-after($targets,','))"/>

    <xsl:if test="not($list = '')">
      <xsl:choose>
        <xsl:when test="contains($list, ',')">
          <xsl:element name="a">
            <xsl:attribute name="href">
              <xsl:value-of select="concat('#target-', $first)"/>
            </xsl:attribute>
            <xsl:value-of select="$first"/>
          </xsl:element>

          <xsl:text>, </xsl:text>

          <xsl:call-template name="formatTargetList">
            <xsl:with-param name="targets" select="$rest"/>
          </xsl:call-template>
        </xsl:when>

        <xsl:otherwise>
          <xsl:element name="a">
            <xsl:attribute name="href">
              <xsl:value-of select="concat('#target-', $list)"/>
            </xsl:attribute>
            <xsl:value-of select="$list"/>
          </xsl:element>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
