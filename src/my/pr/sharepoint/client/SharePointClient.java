/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.client;

import my.pr.sharepoint.xml.handlers.GetListCollectionResponseHandler;
import my.pr.sharepoint.xml.handlers.GetListItemResponseHandler;
import my.pr.sharepoint.xml.handlers.GetListItemsResponseHandler;
import my.pr.sharepoint.xml.handlers.GetListResponseHandler;
import my.pr.sharepoint.xml.handlers.GetWebCollectionResponseHandler;
import my.pr.sharepoint.xml.handlers.GetWebResponseHandler;
import java.io.IOException;
import java.util.List;
import my.pr.connectivity.*;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author PRando
 */
public class SharePointClient extends PrHttpClient {

    public static final String WEBS_URL = "/_vti_bin/Webs.asmx";
    public static final String LISTS_URL = "/_vti_bin/Lists.asmx";
    private String url = null;
    private SPNTCreds creds = null;
    private static final int TIMEOUT = 60000;

    public SharePointClient(String sharePointUrl, SPNTCreds creds) {
        super(sharePointUrl, TIMEOUT, TIMEOUT);
        this.creds = creds;

        this.setNTCredentials(creds.getUser(), creds.getPassword(), creds.getUserDomain(), creds.getHostDomain());
        this.url = sharePointUrl;
    }

    public SPNTCreds getNTCredentials() {
        return creds;
    }

    private PrHttpResponse processSoapRequest(String webServiceUrl, String actionXml) throws IOException {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        xml.append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
        xml.append("<soap12:Body>");
        xml.append(actionXml);
        xml.append("</soap12:Body>");
        xml.append("</soap12:Envelope>");

        PrHttpResponse response = processPostSoap(webServiceUrl, xml.toString());
        response.printReport();

        return response;
    }

    public SPSite getCurrentSite() throws IOException {
        SPSite site = new SPSite(this);
        return getCurrentSite(site);
    }

    public SPSite getCurrentSite(SPSite site) throws IOException {
        String actionXml = "<GetWeb xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><webUrl>" + "." + "</webUrl></GetWeb>";
        PrHttpResponse response = processSoapRequest(WEBS_URL, actionXml);

        if (response.getStatusCode() == 200) {
            GetWebResponseHandler getWebHandler = new GetWebResponseHandler(site);
            PrXMLUtils.parseString(response.getStringContent(), getWebHandler);

            return site;
        } else {
            throw new IOException("Error Processing getSite()");
        }
    }

    public SPSite getSite(String url) throws IOException {
        String actionXml = "<GetWeb xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><webUrl>" + url + "</webUrl></GetWeb>";
        PrHttpResponse response = processSoapRequest(WEBS_URL, actionXml);

        if (response.getStatusCode() == 200) {
            SPSite site = new SPSite(url, creds);
            GetWebResponseHandler getWebHandler = new GetWebResponseHandler(site);
            PrXMLUtils.parseString(response.getStringContent(), getWebHandler);
            return site;
        } else {
            throw new IOException("Error Processing getSite()");
        }

    }

    public void getSiteCollection(SPSite site) throws IOException {
        String actionXml = "<GetWebCollection xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\" />";
        PrHttpResponse response = processSoapRequest(WEBS_URL, actionXml);

        if (response.getStatusCode() == 200) {

            GetWebCollectionResponseHandler getWebCollectionHandler = new GetWebCollectionResponseHandler(site);
            PrXMLUtils.parseString(response.getStringContent(), getWebCollectionHandler);

        } else {
            throw new IOException("Error Processing getSiteCollection()");
        }
    }

    public SPList getList(String name, SPSite site) throws IOException {
        String actionXml = "<GetList xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + name + "</listName></GetList>";
        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml);

        if (response.getStatusCode() == 200) {
            GetListResponseHandler getListHandler = new GetListResponseHandler(site);
            PrXMLUtils.parseString(response.getStringContent(), getListHandler);
            return getListHandler.getList();

        } else {
            throw new IOException("Error Processing getList()");
        }
    }

    public void getListCollection(SPSite site) throws IOException {
        String actionXml = "<GetListCollection xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\" />";
        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml);
        if (response.getStatusCode() == 200) {

            GetListCollectionResponseHandler getListCollectionHandler = new GetListCollectionResponseHandler(site);
            PrXMLUtils.parseString(response.getStringContent(), getListCollectionHandler);

        } else {
            throw new IOException("Error Processing getListCollection()");
        }
    }

    public void getListItems(SPList list, int rowLimit) throws IOException {
        String actionXml = "<GetListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName><rowLimit>" + rowLimit + "</rowLimit></GetListItems>";
        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml);
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            throw new IOException("Error Processing getListItems()");
        }
    }

    

    public void updateListItem(SPList list, SPListRow row) throws SPException, IOException {
        if (row.getId() == -1) {
            throw new SPException("Unable to Update Record, ID is not set.");
        }

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<UpdateListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName><updates>");
        actionXml.append("<Batch OnError=\"Continue\" ListVersion=\"1\"><Method ID=\"1\" Cmd=\"Update\">");

        actionXml.append("<Field Name=\"ID\">" + row.getId() + "</Field>");
        for (String column : row.getColumnNames()) {
            String value = row.getRowColumn(column);
            actionXml.append("<Field Name=\"" + column + "\">" + StringEscapeUtils.escapeXml(value) + "</Field>");
        }

        actionXml.append("</Method></Batch></updates></UpdateListItems>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            throw new IOException("Error Processing updateListItem()");
        }
    }

    public void deleteListItem(SPList list, SPListRow row) throws IOException {
        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<UpdateListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName><updates>");
        actionXml.append("<Batch OnError=\"Continue\" ListVersion=\"1\"><Method ID=\"1\" Cmd=\"Delete\">");

        actionXml.append("<Field Name=\"ID\">" + row.getId() + "</Field>");

        actionXml.append("</Method></Batch></updates></UpdateListItems>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            throw new IOException("Error Processing insertListItem()");
        }
    }

    public void deleteListItems(SPList list, List<SPListRow> rows) throws IOException {
        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<UpdateListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName><updates>");
        actionXml.append("<Batch OnError=\"Continue\" ListVersion=\"1\">");

        int count = 1;
        for (SPListRow row : rows) {
            actionXml.append("<Method ID=\"" + count++ + "\" Cmd=\"Delete\"><Field Name='ID'>" + row.getId() + "</Field></Method>");
        }

        actionXml.append("</Batch></updates></UpdateListItems>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {
            //GetListItemResponseHandler getListItemHandler = new GetListItemResponseHandler(list, row);
            //PrXMLUtils.parseString(response.getStringContent(), getListItemHandler);
            //return getListItemHandler.getRow();
        } else {
            throw new IOException("Error Processing deleteListItems()");
        }
    }

    public SPListRow insertListItem(SPList list, SPListRow row) throws SPException, IOException {
        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<UpdateListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName><updates>");
        actionXml.append("<Batch OnError=\"Continue\" ListVersion=\"1\"><Method ID=\"1\" Cmd=\"New\">");

        for (String column : row.getColumnNames()) {
            String value = row.getRowColumn(column);

            //value = value.replace("&", "&amp;");
            value = StringEscapeUtils.escapeXml(value);

            actionXml.append("<Field Name=\"" + column + "\">" + value + "</Field>");
        }

        actionXml.append("</Method></Batch></updates></UpdateListItems>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemResponseHandler getListItemHandler = new GetListItemResponseHandler(list, row);
            PrXMLUtils.parseString(response.getStringContent(), getListItemHandler);
            
            if(getListItemHandler.isError()) {
                throw new SPException("Error Occured During Insert! - " + getListItemHandler.getErrorText());
            }
            
            return getListItemHandler.getRow();
        } else {
            throw new IOException("Error Processing insertListItem()");
        }
    }

    public void updateListDescription(SPList list) throws IOException {
        String actionXml = "<UpdateList xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\">"
                + "<listName>" + list.getId() + "</listName>"
                + "<Batch OnError=\"Continue\" ListVersion=\"1\"><Method ID=\"1\" Cmd=\"Update\">"
                + "<List><ListProperties Description=\"" + StringEscapeUtils.escapeXml(list.getDescription()) + "\" /></List>"
                + "</Method></Batch>"
                + "</UpdateList>";

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml);
        if (response.getStatusCode() == 200) {
            //GetListCollectionResponseHandler getListCollectionHandler = new GetListCollectionResponseHandler(site);
            //PrXMLUtils.parseString(response.getStringContent(), getListCollectionHandler);
        } else {
            throw new IOException("Error Processing updateListDescription()");
        }
    }

    public static void main(String[] args) throws IOException {

    }
}
