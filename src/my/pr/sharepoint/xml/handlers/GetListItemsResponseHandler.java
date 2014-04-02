/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.xml.handlers;

//import com.kronos.kgs.analysis.iis.*;
import my.pr.sharepoint.client.SPField;
import my.pr.sharepoint.client.SPList;
import my.pr.sharepoint.client.SPListRow;
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
public class GetListItemsResponseHandler extends BaseSharePointSoapHandler {

    private SPList list = null;

    public GetListItemsResponseHandler(SPList list) {
        this.list = list;
    }

    
    public void startDoc() throws SAXException {
        //System.out.println("start document   : ");
    }

    public void endDoc() throws SAXException {
        //System.out.println("end document     : ");
    }

    public void startElem(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //System.out.println("start element    : " + qName);
        
        if(qName.equalsIgnoreCase("z:row")) {
            SPListRow row = list.getNewListRow();
            for(int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.getLocalName(i).replace("ows_", "");
                String value = attributes.getValue(i).replaceAll("[\\u00A0]", " ").trim();
                
                if(name.equalsIgnoreCase("ID")) {
                    row.setId(Integer.parseInt(value));
                }
                else {
                    row.addRowColumn(name, value);
                }
                
            }
            list.addRowToCache(row);
        }
        

        
    }

    public void endElem(String uri, String localName, String qName) throws SAXException {
        //System.out.println("end element      : " + qName);
    }

    public void chars(char ch[], int start, int length) throws SAXException {
        //System.out.println("start characters : " + new String(ch, start, length));
    }
    
    public SPList getList() {
        return list;
    }

}
