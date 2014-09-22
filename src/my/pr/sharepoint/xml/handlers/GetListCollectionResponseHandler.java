/*
 * Program : Java SharePoint Client
 * Author: Paul Rando (paulr978@gmail.com)
 * GIT: https://github.com/paulr978/Java-SharePoint-Client
 * 
 */
package my.pr.sharepoint.xml.handlers;

//import com.kronos.kgs.analysis.iis.*;
import my.pr.sharepoint.client.SPList;
import my.pr.sharepoint.client.SharePointClient;
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
public class GetListCollectionResponseHandler extends BaseSharePointSoapHandler {

    private SPSite parentSite = null;

    public GetListCollectionResponseHandler(SPSite parentSite) {
        this.parentSite = parentSite;
    }
    
    public void startDoc() throws SAXException {
        //System.out.println("start document   : ");
    }

    public void endDoc() throws SAXException {
        //System.out.println("end document     : ");
    }

    public void startElem(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //System.out.println("start element    : " + qName);
        
        if(qName.equalsIgnoreCase("List")) {
            String id = attributes.getValue("ID");
            String name = attributes.getValue("Name");
            String title = attributes.getValue("Title");
            String description = attributes.getValue("Description");
            String itemCount = attributes.getValue("ItemCount");
            
            parentSite.getListService().addList(id, name, title, description, Integer.valueOf(itemCount));
        }

        
    }

    public void endElem(String uri, String localName, String qName) throws SAXException {
        //System.out.println("end element      : " + qName);
    }

    public void chars(char ch[], int start, int length) throws SAXException {
        //System.out.println("start characters : " + new String(ch, start, length));
    }

}
