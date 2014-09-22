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
import my.pr.sharepoint.client.SPView;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author PRando
 */
public class GetViewCollectionResponseHandler extends BaseSharePointSoapHandler {

    private SPSite parentSite = null;
    private ArrayList<SPView> views = null;

    public GetViewCollectionResponseHandler(SPSite parentSite) {
        this.parentSite = parentSite;
        views = new ArrayList<SPView>();
    }
    
    public void startDoc() throws SAXException {
        //System.out.println("start document   : ");
    }

    public void endDoc() throws SAXException {
        //System.out.println("end document     : ");
    }

    public void startElem(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //System.out.println("start element    : " + qName);
        
        if(qName.equalsIgnoreCase("View")) {
            SPView view = new SPView();
            String displayName = attributes.getValue("DisplayName");
            String name = attributes.getValue("Name");
            String url = attributes.getValue("Url");
            
            view.setName(name);
            view.setDisplayName(displayName);
            
            views.add(view);
            //parentSite.getListService().addList(id, name, title, description, Integer.valueOf(itemCount));
        }

        
    }

    public void endElem(String uri, String localName, String qName) throws SAXException {
        //System.out.println("end element      : " + qName);
    }

    public void chars(char ch[], int start, int length) throws SAXException {
        //System.out.println("start characters : " + new String(ch, start, length));
    }
    
    public SPView[] getViews() {
        return views.toArray(new SPView[views.size()]);
    }
 
}
