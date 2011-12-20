<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/timeSeries">
		<html>
			<head>
				<META HTTP-EQUIV="Pragma" CONTENT="no-cache"/>
				<title>JTimeSeries Series Snapshot</title>
				<style type="text/css"/>
			</head>
			<body>
                <table>
                <tr><th>Series Path</th><th>Timestamp</th><th>Value</th></tr>
				<xsl:apply-templates select="series"/>
			    </table>
            </body>
		</html>
	</xsl:template>

	<xsl:template match="series">
        <tr>
            <td><xsl:value-of select="@parentPath"/>.<xsl:value-of select="@id"/></td>
            <td><xsl:value-of select="@latestItemTimestamp"/></td>
            <td align='right'><xsl:value-of select="@latestItemValue"/></td>
        </tr>
	</xsl:template>

</xsl:stylesheet>