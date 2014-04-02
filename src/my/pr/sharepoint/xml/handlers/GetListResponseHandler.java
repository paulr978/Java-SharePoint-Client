/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.xml.handlers;

//import com.kronos.kgs.analysis.iis.*;
import my.pr.sharepoint.client.SPField;
import my.pr.sharepoint.client.SPList;
import my.pr.sharepoint.client.SharePointClient;
import my.pr.sharepoint.client.SPSite;
import java.util.ArrayList;
import java.util.List;
import my.pr.utils.OBoolean;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author PRando
 */
public class GetListResponseHandler extends BaseSharePointSoapHandler {

    private SPSite parentSite = null;
    private SPList list = null;
    
    public GetListResponseHandler(SPSite parentSite) {
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
            list = new SPList(parentSite);
            String name = attributes.getValue("Name");
            String title = attributes.getValue("Title");
            String description = attributes.getValue("Description");
            int itemCount = Integer.parseInt(attributes.getValue("ItemCount"));
            
            list.setId(attributes.getValue("ID"));
            list.setName(name);
            list.setTitle(title);
            list.setDescription(description);
            list.setItemCount(itemCount);
        }
        
        if(qName.equalsIgnoreCase("Field")) {
            SPField field = new SPField();
            //<Field ID="{03e45e84-1992-4d42-9116-26f756012634}" RowOrdinal="0" Type="ContentTypeId" Sealed="TRUE" ReadOnly="TRUE" Hidden="TRUE" DisplayName="Content Type ID" Name="ContentTypeId" DisplaceOnUpgrade="TRUE" SourceID="http://schemas.microsoft.com/sharepoint/v3" StaticName="ContentTypeId" ColName="tp_ContentTypeId" FromBaseType="TRUE"/>
            field.setId(attributes.getValue("ID"));
            field.setType(attributes.getValue("ContentTypeId"));
            field.setDisplayName(attributes.getValue("DisplayName"));
            field.setName(attributes.getValue("Name"));
            field.setHidden(OBoolean.parseString(attributes.getValue("Hidden")));
            field.setReadOnly(OBoolean.parseString(attributes.getValue("ReadOnly")));
            field.setHidden(OBoolean.parseString(attributes.getValue("Hidden")));
            field.setRequired(OBoolean.parseString(attributes.getValue("Required")));
            
            list.addField(field);
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
