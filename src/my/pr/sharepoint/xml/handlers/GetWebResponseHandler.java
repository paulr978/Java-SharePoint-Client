/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.xml.handlers;

//import com.kronos.kgs.analysis.iis.*;
import my.pr.sharepoint.client.SPSite;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author PRando
 */
public class GetWebResponseHandler extends BaseSharePointSoapHandler {

    private SPSite site = null;
    
    public GetWebResponseHandler(SPSite site) {
        this.site = site;
    }
    
    public void startDoc() throws SAXException {
        //System.out.println("start document   : ");
    }

    public void endDoc() throws SAXException {
        //System.out.println("end document     : ");
    }

    public void startElem(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //System.out.println("start element    : " + qName);
        
        if(qName.equalsIgnoreCase("Web")) {
            site.setTitle(attributes.getValue("Title"));
            site.setUrl(attributes.getValue("Url"));
            site.setDescription(attributes.getValue("Description"));
            site.setLanguage(attributes.getValue("Language"));
            site.setTheme(attributes.getValue("Theme"));
        }

        
    }

    public void endElem(String uri, String localName, String qName) throws SAXException {
        //System.out.println("end element      : " + qName);
    }

    public void chars(char ch[], int start, int length) throws SAXException {
        //System.out.println("start characters : " + new String(ch, start, length));
    }
    
    public SPSite getSite() {
        return site;
    }

}
