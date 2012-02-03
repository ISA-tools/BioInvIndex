package uk.ac.ebi.bioinvindex.services.compound;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 21/09/2011
 *         Time: 20:39
 */
public class Image {

    private String url;
    private String caption;
    private String altTag;

    public Image(String url, String caption, String altTag) {
        this.url = url;
        this.caption = caption;
        this.altTag = altTag;
    }

    public String getUrl() {
        return url;
    }

    public String getCaption() {
        return caption;
    }

    public String getAltTag() {
        return altTag;
    }
}
