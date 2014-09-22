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
import my.pr.sharepoint.client.SPAttachment;
import my.pr.sharepoint.client.SPListRow;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author PRando
 */
public class GetAttachmentCollectionResponseHandler extends BaseSharePointSoapHandler {

    private SPList list = null;
    private SPListRow row = null;
    
    private boolean attachmentTag = false;
    
    public GetAttachmentCollectionResponseHandler(SPList list, SPListRow row) {
        this.list = list;
        this.row = row;
    }
    
    public void startDoc() throws SAXException {
        //System.out.println("start document   : ");
    }

    public void endDoc() throws SAXException {
        //System.out.println("end document     : ");
    }

    public void startElem(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //System.out.println("start element    : " + qName);
        
        if(qName.equalsIgnoreCase("Attachment")) {
            attachmentTag = true;
        }

        
    }

    public void endElem(String uri, String localName, String qName) throws SAXException {
        //System.out.println("end element      : " + qName);
    }

    public void chars(char ch[], int start, int length) throws SAXException {
        //System.out.println("start characters : " + new String(ch, start, length));
        
        if(attachmentTag) {
            SPAttachment attachment = new SPAttachment(row, new String(ch, start, length));
            row.addAttachment(attachment);
            attachmentTag = false;
        }
    }

}
