<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/timeSeries">
		<html>
			<head>
				<META HTTP-EQUIV="Pragma" CONTENT="no-cache"/>
				<title>JTimeSeries Series Index</title>
				<style type="text/css"/>
			</head>
			<body>
				<xsl:apply-templates select="series"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="series">
		<xsl:value-of select="@seriesUrl"/><br/>
	</xsl:template>


</xsl:stylesheet>