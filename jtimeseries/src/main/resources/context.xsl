<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/timeSeriesContext">
		<html>
			<head>
				<META HTTP-EQUIV="Pragma" CONTENT="no-cache"/>
				<title>JTimeSeries Context</title>
				<style type="text/css">
				#page {
				  margin-top: 10px;
				}

				#contextTree {
				  float: left;
				  width: 350px;
				}

				#context {
				  margin-left:  350px;
				}

				table.seriesTable {
					text-align: left;
					border-width: 0px 0px 0px 0px;
					border-spacing: 2px;
					border-style: solid solid solid solid;
					border-color: navy navy navy navy;
					border-collapse: separate;
					background-color: white;
				}
				table.seriesTable th {
					border-width: 0px 0px 0px 0px;
					padding: 2px 2px 2px 2px;
					border-style: inset inset inset inset;
					border-color: gray gray gray gray;
					background-color: white;
					-moz-border-radius: 0px 0px 0px 0px;
				}
				table.seriesTable td {
					border-width: 0px 0px 0px 0px;
					padding: 2px 2px 2px 2px;
					border-style: inset inset inset inset;
					border-color: gray gray gray gray;
					background-color: white;
					-moz-border-radius: 0px 0px 0px 0px;
				}

				</style>
			</head>

			<body>
			<p/>
			<div id="page">
				<div id="contextTree">			
					<xsl:apply-templates select="contextTree"/>
				</div>
				<div id="context">
					<xsl:apply-templates select="selectedContext"/>
				</div>
			</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="contextTree">
		<ul>
		<xsl:apply-templates select="context"/>
		</ul>
	</xsl:template>

	<xsl:template match="context">
		<li>
			<xsl:element name="a">
			<xsl:attribute name="href">
				<xsl:value-of select="@contextUrl"/>
			</xsl:attribute>
			<xsl:value-of select="@id"/>
			</xsl:element>
		</li>
		<xsl:if test="context">
			<ul>
				<xsl:apply-templates select="context"/>
			</ul>
		</xsl:if>
	</xsl:template>

	<xsl:template match="selectedContext">
		<p/>
		<h3><xsl:value-of select="@id"/></h3>
		<xsl:value-of select="@description"/>
		<xsl:apply-templates select="timeSeries"/>		
	</xsl:template>

	<xsl:template match="timeSeries">
		<xsl:apply-templates/>			
	</xsl:template>

	<xsl:template match="series">
		<p/>
		<table class="seriesTable">
		<tr><th><xsl:value-of select="@id"/></th></tr>
		<tr><td><font size='-1'><xsl:value-of select="@description"/></font></td></tr>
			<tr><th>
				<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="@seriesUrl"/>
				</xsl:attribute>
				Time Series Values
				</xsl:element>
			</th></tr>
		<td>
	    <xsl:element name="a">
		<xsl:attribute name="href">
			<xsl:value-of select="@chartImage"/>
			<xsl:text disable-output-escaping="yes">?width=1024&amp;height=768</xsl:text>
		</xsl:attribute>

			<xsl:element name="img">
				<xsl:attribute name="height">300</xsl:attribute>
				<xsl:attribute name="width">500</xsl:attribute>
				<xsl:attribute name="src">
					<xsl:value-of select="@chartImage"/>
					<xsl:text disable-output-escaping="yes">?width=500&amp;height=300</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="alt">image</xsl:attribute>
			</xsl:element>

		</xsl:element>
		</td>
		</table>
		<br/>
	</xsl:template>

</xsl:stylesheet>