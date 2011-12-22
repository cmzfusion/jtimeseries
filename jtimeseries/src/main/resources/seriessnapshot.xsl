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
                <h2>Snapshot of latest Timeseries Values</h2>
                <table>
                <tr><th>Series Path</th><th>datetime</th><th>Value</th></tr>
				<xsl:apply-templates select="series"/>
			    </table>
            </body>
		</html>
	</xsl:template>

	<xsl:template match="series">
        <tr>
            <td><xsl:value-of select="@parentPath"/>.<xsl:value-of select="@id"/></td>
            <td><xsl:value-of select="@datetime"/></td>
            <td align='right'><xsl:value-of select="@latestItemValue"/></td>
        </tr>
	</xsl:template>

</xsl:stylesheet>