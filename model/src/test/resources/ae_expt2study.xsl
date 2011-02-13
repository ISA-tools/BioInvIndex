<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="experiments">
        <studies>
            <xsl:apply-templates/>
        </studies>
    </xsl:template>

    <xsl:template match="experiment">
        <study id="{@id}"
               acc="{@accnum}"
               DESCRIPTION="{substring(DESCRIPTION[not(starts-with(text(),'(Generated'))], 1, 254)}"
               TITLE="{substring(@name,1,254)}"
               ><xsl:if test="@releasedate!=''"><xsl:attribute name="releaseDate"><xsl:VALUE-of select="@releasedate"/></xsl:attribute></xsl:if>
        </study>
    </xsl:template>
</xsl:stylesheet>