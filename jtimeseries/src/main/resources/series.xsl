<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/timeSeries">
		<html>
			<head>
				<META HTTP-EQUIV="Pragma" CONTENT="no-cache"/>
				<title>JTimeSeries Time Series</title>
				<style type="text/css">
				table.seriesTable {
					text-align: left;
					border-width: 1px 1px 1px 1px;
					border-spacing: 2px;
					border-style: solid solid solid solid;
					border-color: blue blue blue blue;
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
					padding: 2px 10px 2px 10px;
					border-style: inset inset inset inset;
					border-color: gray gray gray gray;
					background-color: white;
					-moz-border-radius: 0px 0px 0px 0px;
				}
				</style>	
			</head>
			<body>
				<xsl:apply-templates select="series"/>
				<p/>
                <xsl:apply-templates select="summaryStats"/>
                <p/>
				<xsl:apply-templates select="seriesItems"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="series">
		<p/>
		<h3><xsl:value-of select="@id"/></h3>
		<h4><xsl:value-of select="@description"/></h4>
	</xsl:template>

    <xsl:template match="summaryStats">
		<table class='seriesTable'>
		<tr><th>Summary Stat</th><th>Value</th></tr>
		<xsl:apply-templates select="summaryStat"/>
		</table>
	</xsl:template>

    <xsl:template match="summaryStat">
		<tr>
		<td><xsl:value-of select="@name"/></td>
		<td><xsl:value-of select="@value"/></td>
		</tr>
	</xsl:template>

	<xsl:template match="seriesItems">
		<table class='seriesTable'>
		<tr><th>Timestamp</th><th>Datetime</th><th>value</th></tr>
		<xsl:apply-templates select="seriesItem"/>
		</table>
	</xsl:template>

	<xsl:template match="seriesItem">
		<tr>
		<td><xsl:value-of select="@timestamp"/></td>
		<td><xsl:value-of select="@datetime"/></td>
		<td><xsl:value-of select="@value"/></td>
		</tr>
	</xsl:template>

</xsl:stylesheet>