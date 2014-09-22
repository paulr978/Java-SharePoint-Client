/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.client;

import java.io.File;
import my.pr.sharepoint.xml.handlers.GetListCollectionResponseHandler;
import my.pr.sharepoint.xml.handlers.GetListItemResponseHandler;
import my.pr.sharepoint.xml.handlers.GetListItemsResponseHandler;
import my.pr.sharepoint.xml.handlers.GetListResponseHandler;
import my.pr.sharepoint.xml.handlers.GetWebCollectionResponseHandler;
import my.pr.sharepoint.xml.handlers.GetWebResponseHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import my.pr.connectivity.*;
import my.pr.sharepoint.xml.handlers.GetAttachmentCollectionResponseHandler;
import my.pr.sharepoint.xml.handlers.GetUserCollectionResponseHandler;
import my.pr.sharepoint.xml.handlers.GetViewCollectionResponseHandler;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author PRando
 */
public class SharePointClient extends PrHttpClient {

    public static final String WEBS_URL = "/_vti_bin/Webs.asmx";
    public static final String LISTS_URL = "/_vti_bin/Lists.asmx";
    public static final String VIEWS_URL = "/_vti_bin/Views.asmx";
    public static final String COPY_URL = "/_vti_bin/Copy.asmx";
    public static final String DWS_URL = "/_vti_bin/dws.asmx";
    public static final String USERGROUP_URL = "/_vti_bin/usergroup.asmx";
    private String url = null;
    private String origUrl = null;
    private SPNTCreds creds = null;
    private static final int TIMEOUT = 60000;
    private boolean debug = false;
    private int retryCount = -1;
    private long retrySleepInterval = -1;

    public SharePointClient(String sharePointUrl, SPNTCreds creds) {
        this(sharePointUrl, creds, false, 0, 0);
    }

    public SharePointClient(String sharePointUrl, SPNTCreds creds, boolean debug) {
        this(sharePointUrl, creds, debug, 0, 0);
    }

    public SharePointClient(String sharePointUrl, SPNTCreds creds, boolean debug, int retryCount, long retrySleepInterval) {
        super(sharePointUrl, TIMEOUT, TIMEOUT, debug);
        this.creds = creds;

        System.out.println("Initializing SharePointClient... " + sharePointUrl);
        //this.setNTCredentials(creds.getUser(), creds.getPassword());
        this.setNTCredentials(creds.getUser(), creds.getPassword(), creds.getUserDomain(), creds.getHostDomain());
        this.url = sharePointUrl;
        this.debug = debug;
        this.retryCount = retryCount;
        this.retrySleepInterval = retrySleepInterval;
        
    }

    public SPNTCreds getNTCredentials() {
        return creds;
    }

    public String getUrl() {
        return url;
    }
    
    public void setOrigUrl(String origUrl) {
        this.origUrl = origUrl;
    }
    
    private PrHttpResponse processSoapRequest(String webServiceUrl, String actionXml) throws IOException {
        return processSoapRequest(webServiceUrl, actionXml, new DefaultRetryProcessor());
    }

    private PrHttpResponse processSoapRequest(String webServiceUrl, String actionXml, SPCommRetryProcessor retryProcessor) throws IOException {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        xml.append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
        xml.append("<soap12:Body>");
        xml.append(actionXml);
        xml.append("</soap12:Body>");
        xml.append("</soap12:Envelope>");

        //System.out.println("POST: " + xml.toString());
        
        PrHttpResponse response = null;
        try {
            response = processPostSoap(webServiceUrl, xml.toString());
            if (response == null) {
                throw new Exception("Null Response Received");
            } else if (response.getStatusCode() == -1) {
                throw new Exception("No Status Code Received");
            } else if (response.getStatusCode() != 200) {
                throw new Exception("Invalid Status Code Received: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(response != null) {
                System.out.println("Result Returned (caused exception) Code: " + response.getStatusCode());
                if(response.getStringContent() != null) {
                    System.out.println("Data: " + response.getStringContent());
                }
            }
            System.out.println("Error Occured During SharePoint Comm.  Retrying: " + retryCount + " Times and sleeping(ms) " + retrySleepInterval + " in between each try.");

            boolean successful = false;
            int attempt = 0;

            loop:
            while (!successful && attempt < retryCount) {
               
                try {
                    //System.out.println("Sleeping...");
                    Thread.sleep(retrySleepInterval);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                
                attempt++;
                System.out.println("Retrying Again...  Attempt: " + attempt);
                try {
                    response = processPostSoap(webServiceUrl, xml.toString());
                    if (response == null) {
                    } else if (response.getStatusCode() == -1) {
                    } else if (response.getStatusCode() != 200) {
                    } else if (response.getStatusCode() == 200) {
                        successful = true;
                        System.out.println("Finally Successful!  Continuing On...");
                        break loop;
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }


            }

            if(!successful)
                throw new IOException("Comm Error");
        }


        if (debug) {
            response.printReport();
        }

        return response;
    }
    
    private PrHttpResponse processSoapRequestNoRetry(String webServiceUrl, String actionXml) throws IOException {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        xml.append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
        xml.append("<soap12:Body>");
        xml.append(actionXml);
        xml.append("</soap12:Body>");
        xml.append("</soap12:Envelope>");

        //System.out.println("POST: " + xml.toString());
        
        PrHttpResponse response = null;
        try {
            response = processPostSoap(webServiceUrl, xml.toString());
            if (response == null) {
                throw new Exception("Null Response Received");
            } else if (response.getStatusCode() == -1) {
                throw new Exception("No Status Code Received");
            } else if (response.getStatusCode() != 200) {
                throw new Exception("Invalid Status Code Received: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(response != null) {
                System.out.println("Result Returned (caused exception) Code: " + response.getStatusCode());
                if(response.getStringContent() != null) {
                    System.out.println("Data: " + response.getStringContent());
                }
            }
            throw new IOException("Comm Error");
        }


        if (debug) {
            response.printReport();
        }

        return response;
    }
    
    

    /*
     private PrHttpResponse processBytesSoapRequest(String webServiceUrl, String actionXml) throws IOException {
     StringBuffer xml = new StringBuffer();
     xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
     xml.append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
     xml.append("<soap12:Body>");
     xml.append(actionXml);
     xml.append("</soap12:Body>");
     xml.append("</soap12:Envelope>");

     PrHttpResponse response = processPostSoap(webServiceUrl, xml.toString().getBytes());
     if(debug)
     response.printReport();

     return response;
     }
     */
    public SPSite getCurrentSite() throws IOException {
        SPSite site = new SPSite(this);
        return getCurrentSite(site);
    }

    public byte[] getBytesFromUrl(String url) throws IOException {
        PrHttpResponse response = this.processGetRequest(url);
        return response.getContentBytes();

    }

    public SPSite getCurrentSite(SPSite site) throws IOException {
        String actionXml = "<GetWeb xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><webUrl>" + "." + "</webUrl></GetWeb>";
        PrHttpResponse response = processSoapRequest(WEBS_URL, actionXml);

        if (response.getStatusCode() == 200) {
            GetWebResponseHandler getWebHandler = new GetWebResponseHandler(site);
            PrXMLUtils.parseString(response.getStringContent(), getWebHandler);

            return site;
        } else {
            throw new IOException("Response Code Received: " + response.getStatusCode() + ".  Error Processing getSite()");
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

    /*
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
     */
    
    public SPList getList(String name, SPSite site) throws IOException {
        return getList(name, null, site);
    }

    
    public SPList getList(String name, SPView view, SPSite site) throws IOException {
        String actionXml = "<GetList xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + name + "</listName>";
        //if(view != null) actionXml += "<viewName>" + view + "</viewName>";
        actionXml += "</GetList>";
        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml);

        if (response.getStatusCode() == 200) {
            GetListResponseHandler getListHandler = null;
            if (view != null) {
                getListHandler = new GetListResponseHandler(site, view);
            } else {
                getListHandler = new GetListResponseHandler(site);
            }

            PrXMLUtils.parseString(response.getStringContent(), getListHandler);
            return getListHandler.getList();

        } else {
            throw new IOException("Error Processing getList()");
        }
    }
     
    

    public void getUserCollection(SPSite site) throws IOException {
        String actionXml = "<GetAllUserCollectionFromWeb xmlns=\"http://schemas.microsoft.com/sharepoint/soap/directory/\" />";
        PrHttpResponse response = processSoapRequest(USERGROUP_URL, actionXml);
        if (response.getStatusCode() == 200) {

            GetUserCollectionResponseHandler getUserCollectionHandler = new GetUserCollectionResponseHandler(site);
            PrXMLUtils.parseString(response.getStringContent(), getUserCollectionHandler);

        } else {
            throw new IOException("Error Processing getUserCollection()");
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

    public SPView[] getViewCollection(String name, SPSite site) throws IOException {
        String actionXml = "<GetViewCollection xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\" ><listName>" + name + "</listName></GetViewCollection>";
        PrHttpResponse response = processSoapRequest(VIEWS_URL, actionXml);
        if (response.getStatusCode() == 200) {

            GetViewCollectionResponseHandler getViewCollectionHandler = new GetViewCollectionResponseHandler(site);
            PrXMLUtils.parseString(response.getStringContent(), getViewCollectionHandler);
            return getViewCollectionHandler.getViews();
        } else {
            throw new IOException("Error Processing getViewCollection()");
        }
    }

    /*
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
     */
    
    public void getListItems(SPList list, int rowLimit, String folder) throws IOException {
        getListItems(list, null, rowLimit, folder);
    }
    
    public void getListItems(SPList list, SPView view, int rowLimit) throws IOException {
        getListItems(list, view, rowLimit, null);
    }
    
    public void getListItems(SPList list, int rowLimit) throws IOException {
        getListItems(list, null, rowLimit, null);
    }

    public void getListItems(SPList list, SPView view, int rowLimit, String folder) throws IOException {
        String actionXml = "<GetListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName>";
        if (view != null) {
            actionXml += "<viewName>" + view.getName() + "</viewName>";
        }
        
        if(folder != null) {
            actionXml += "<queryOptions><QueryOptions>";
            actionXml += "<Folder>" + origUrl + "/" + list.getTitle() + "/" + folder + "</Folder>";
            actionXml += "</QueryOptions></queryOptions>";
        }
        
        actionXml += "<rowLimit>" + rowLimit + "</rowLimit></GetListItems>";
        
        if(debug) System.out.println("POST: " + actionXml);
        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml);
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            throw new IOException("Error Processing getListItems()");
        }
    }

    public void getListItemById(SPList list, SPView view, int id, int rowLimit) throws IOException {
        String actionXml = "<GetListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName>";
        if (view != null) {
            actionXml += "<viewName>" + view.getName() + "</viewName>";
        }
        actionXml += "<query><Query><Where><Eq><FieldRef Name = \"ID\"/><Value Type = \"Text\">" + id + "</Value></Eq></Where></Query></query>";
        actionXml += "<rowLimit>" + rowLimit + "</rowLimit></GetListItems>";
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

        if(debug) System.out.println("POST: " + actionXml);
        
        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            throw new IOException("Error Processing updateListItem()");
        }
    }

    public void unlinkDocument(SPList list, SPListRow row) throws SPException, IOException {
        if (row.getId() == -1) {
            throw new SPException("Unable to Update Record, ID is not set.");
        }

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<UpdateListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName><updates>");
        actionXml.append("<Batch OnError=\"Continue\" ListVersion=\"1\"><Method ID=\"1\" Cmd=\"Update\">");

        actionXml.append("<Field Name=\"ID\">" + row.getId() + "</Field>");
        actionXml.append("<Field Name='MetaInfo' Property='_CopySource'></Field>");

        actionXml.append("</Method></Batch></updates></UpdateListItems>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            throw new IOException("Error Processing unlinkDocument()");
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

        if (debug) {
            System.out.println("ActionXML: " + actionXml.toString());
        }

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemResponseHandler getListItemHandler = new GetListItemResponseHandler(list, row);
            PrXMLUtils.parseString(response.getStringContent(), getListItemHandler);

            if (getListItemHandler.isError()) {
                throw new SPException("Error Occured During Insert! - " + getListItemHandler.getErrorText());
            }

            return getListItemHandler.getRow();
        } else {
            throw new IOException("Error Processing insertListItem()");
        }
    }

    public SPListRow insertListItem(SPList list, SPListRow row, ArrayList<String> cs) throws SPException, IOException {
        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<UpdateListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName><updates>");
        actionXml.append("<Batch OnError=\"Continue\" ListVersion=\"1\"><Method ID=\"1\" Cmd=\"New\">");


        loop:
        for (String column : row.getColumnNames()) {
            boolean found = false;
            for (String c : cs) {
                if (c.equalsIgnoreCase(column)) {
                    found = true;
                }
            }
            if (!found) {
                break loop;
            }

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

            if (getListItemHandler.isError()) {
                throw new SPException("Error Occured During Insert! - " + getListItemHandler.getErrorText());
            }

            return getListItemHandler.getRow();
        } else {
            throw new IOException("Error Processing insertListItem()");
        }
    }

    public void createListFolder(SPList list, String rootFolder, String newFolder) throws SPException, IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<UpdateListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName><updates>");
        actionXml.append("<Batch OnError=\"Continue\" ListVersion=\"1\" PreCalc=\"TRUE\" RootFolder=\"" + StringEscapeUtils.escapeXml(rootFolder) + "\"><Method ID=\"1\" Cmd=\"New\">");
        //actionXml.append("<Batch OnError=\"Continue\" ListVersion=\"1\" PreCalc=\"TRUE\"><Method ID=\"1\" Cmd=\"New\">");
        actionXml.append("<Field Name='ID'>New</Field><Field Name='FSObjType'>1</Field><Field Name='BaseName'>" + StringEscapeUtils.escapeXml(newFolder) + "</Field>");

        actionXml.append("</Method></Batch></updates></UpdateListItems>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {
        } else {
            throw new IOException("Error Processing createListFolder()");
        }
    }

    public void putFile(SPList list, SPListRow row, SPAttachment attachment) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<AddAttachment xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\">");
        actionXml.append("<listName>" + list.getId() + "</listName>");
        actionXml.append("<listItemID>" + row.getId() + "</listItemID>");
        actionXml.append("<fileName>" + attachment.getFileName() + "</fileName>");

        if (!attachment.isDownloaded()) {
            attachment.download();
        }
        actionXml.append("<attachment>" + attachment.getXmlFriendlyBytes() + "</attachment>");
        actionXml.append("</AddAttachment>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            //System.out.println("Error: " + response.getStringContent());
            throw new IOException("Error Processing addAttachment()");
        }
    }

    public void addEmptyAttachment(SPList list, SPListRow row) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<AddAttachment xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\">");
        actionXml.append("<listName>" + list.getId() + "</listName>");
        actionXml.append("<listItemID>" + row.getId() + "</listItemID>");
        actionXml.append("<fileName>" + "empty.txt" + "</fileName>");

        //if(!attachment.isDownloaded()) attachment.download();
        actionXml.append("<attachment>abc</attachment>");
        actionXml.append("</AddAttachment>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            //System.out.println("Error: " + response.getStringContent());
            throw new IOException("Error Processing addAttachment()");
        }
    }

    public void addAttachment(SPList list, SPListRow row, File file) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<AddAttachment xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\">");
        actionXml.append("<listName>" + list.getId() + "</listName>");
        actionXml.append("<listItemID>" + row.getId() + "</listItemID>");
        actionXml.append("<fileName>" + file.getName() + "</fileName>");

        actionXml.append("<attachment>" + Base64.encodeBase64String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))) + "</attachment>");
        actionXml.append("</AddAttachment>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            //System.out.println("Error: " + response.getStringContent());
            throw new IOException("Error Processing addAttachment()");
        }
    }

    public void addAttachment(SPList list, SPListRow row, SPAttachment attachment) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<AddAttachment xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\">");
        actionXml.append("<listName>" + list.getId() + "</listName>");
        actionXml.append("<listItemID>" + row.getId() + "</listItemID>");
        actionXml.append("<fileName>" + attachment.getFileName() + "</fileName>");

        if (!attachment.isDownloaded()) {
            attachment.download();
        }
        actionXml.append("<attachment>" + attachment.getXmlFriendlyBytes() + "</attachment>");
        actionXml.append("</AddAttachment>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            //System.out.println("Error: " + response.getStringContent());
            throw new IOException("Error Processing addAttachment()");
        }
    }

    public void deleteAttachment(SPList list, SPListRow row, String url) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<DeleteAttachment xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\">");
        actionXml.append("<listName>" + list.getId() + "</listName>");
        actionXml.append("<listItemID>" + row.getId() + "</listItemID>");
        actionXml.append("<url>" + url + "</url>");
        actionXml.append("</DeleteAttachment>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            //System.out.println("Error: " + response.getStringContent());
            throw new IOException("Error Processing deleteAttachment()");
        }
    }

    public void deleteAttachment(SPList list, SPListRow row, SPAttachment attachment) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<DeleteAttachment xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\">");
        actionXml.append("<listName>" + list.getId() + "</listName>");
        actionXml.append("<listItemID>" + row.getId() + "</listItemID>");
        actionXml.append("<url>" + attachment.getURL() + "</url>");
        actionXml.append("</DeleteAttachment>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            //System.out.println("Error: " + response.getStringContent());
            throw new IOException("Error Processing deleteAttachment()");
        }
    }

    public void getAttachments(SPList list, SPListRow row) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<GetAttachmentCollection xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\">");
        actionXml.append("<listName>" + list.getId() + "</listName>");
        actionXml.append("<listItemID>" + row.getId() + "</listItemID>");
        actionXml.append("</GetAttachmentCollection>");

        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {

            GetAttachmentCollectionResponseHandler getAttachmentsHandler = new GetAttachmentCollectionResponseHandler(list, row);
            PrXMLUtils.parseString(response.getStringContent(), getAttachmentsHandler);

        } else {
            throw new IOException("Error Processing getAttachments()");
        }
    }

    public void copyLocalFile(String srcUrl, String desUrl) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<CopyIntoItemsLocal xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\">");
        actionXml.append("<SourceUrl>" + srcUrl + "</SourceUrl>");
        actionXml.append("<DestinationUrls><string>" + desUrl + "</string></DestinationUrls>");
        actionXml.append("</CopyIntoItemsLocal>");

        PrHttpResponse response = processSoapRequest(COPY_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {
            //GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            //PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);
        } else {
            System.out.println("Error: " + response.getStringContent());
            throw new IOException("Error Processing copyLocalFile()");
        }
    }

    public void getListItemByDoc(SPList list, String docName) throws IOException {
        String actionXml = "<GetListItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><listName>" + list.getId() + "</listName>";
        //if(view != null) actionXml += "<viewName>" + view.getName() + "</viewName>";
        actionXml += "<query><Query><Where><Eq><FieldRef Name = \"FileLeafRef\"/><Value Type = \"Text\">" + docName + "</Value></Eq></Where></Query></query>";
        actionXml += "<rowLimit>1</rowLimit></GetListItems>";
        PrHttpResponse response = processSoapRequest(LISTS_URL, actionXml);
        if (response.getStatusCode() == 200) {

            GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);

        } else {
            throw new IOException("Error Processing getListItems()");
        }
    }

    public void uploadFile(SPFieldInformation[] fields, File file, String desUrl) throws IOException, SPException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<CopyIntoItems xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\">");
        actionXml.append("<SourceUrl>" + file.getAbsolutePath() + "</SourceUrl>");
        actionXml.append("<DestinationUrls><string>" + desUrl.replace(" ", "%20") + "</string></DestinationUrls>");
        //actionXml.append("<listName>" + list.getId() + "</listName>");
        //actionXml.append("<listItemID>" + row.getId() + "</listItemID>");
        //actionXml.append("<fileName>" + attachment.getFileName() + "</fileName>");
        //actionXml.append("<Fields></Fields>");
        //if(!attachment.isDownloaded()) attachment.download();
        actionXml.append("<Fields>");
        for (SPFieldInformation field : fields) {
            String value = StringEscapeUtils.escapeXml(field.getValue());
            actionXml.append("<FieldInformation Type=\"" + field.getType() + "\" DisplayName=\"" + field.getDisplayName() + "\" InternalName=\"" + field.getInternalName() + "\" Value=\"" + StringEscapeUtils.escapeXml(field.getValue()) + "\"/>");
        }

        actionXml.append("</Fields>");
        //actionXml.append("<Stream>" + "abc" + "</Stream>");

        actionXml.append("<Stream>" + Base64.encodeBase64String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))) + "</Stream>");
        actionXml.append("</CopyIntoItems>");

        PrHttpResponse response = processSoapRequest(COPY_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {
            //GetListItemsResponseHandler getListItemsHandler = new GetListItemsResponseHandler(list);
            //PrXMLUtils.parseString(response.getStringContent(), getListItemsHandler);
        } else {
            System.out.println(response.getStatusCode());
            System.out.println("Error: " + response.getStringContent());
            throw new IOException("Error Processing copyFile()");
        }
    }

    /**
     *
     * @param uri ("Test%20SLA%20Reporting%20Library/455")
     * @throws IOException
     */
    public void createFolder(String uri) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<CreateFolder xmlns=\"http://schemas.microsoft.com/sharepoint/soap/dws/\">");
        actionXml.append("<url>" + uri + "</url>");
        actionXml.append("</CreateFolder>");

        PrHttpResponse response = processSoapRequest(DWS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {
            //GetAttachmentCollectionResponseHandler getAttachmentsHandler = new GetAttachmentCollectionResponseHandler(list, row);
            //PrXMLUtils.parseString(response.getStringContent(), getAttachmentsHandler);
        } else {
            throw new IOException("Error Processing createFolder()");
        }
    }

    public void getDocRepositoryItemMetaData(String doc) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<GetDwsMetaData xmlns=\"http://schemas.microsoft.com/sharepoint/soap/dws/\">");
        actionXml.append("<document>" + doc + "</document>");
        actionXml.append("<minimal>" + "false" + "</minimal>");
        actionXml.append("</GetDwsMetaData>");

        PrHttpResponse response = processSoapRequest(DWS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {
            //GetAttachmentCollectionResponseHandler getAttachmentsHandler = new GetAttachmentCollectionResponseHandler(list, row);
            //PrXMLUtils.parseString(response.getStringContent(), getAttachmentsHandler);
        } else {
            throw new IOException("Error Processing getDocRepositoryData()");
        }
    }

    public void getDocRepositoryData(String doc) throws IOException {

        StringBuffer actionXml = new StringBuffer();
        actionXml.append("<GetDwsData xmlns=\"http://schemas.microsoft.com/sharepoint/soap/dws/\">");
        actionXml.append("<document>" + doc + "</document>");
        actionXml.append("</GetDwsData>");

        PrHttpResponse response = processSoapRequest(DWS_URL, actionXml.toString());
        if (response.getStatusCode() == 200) {
            //GetAttachmentCollectionResponseHandler getAttachmentsHandler = new GetAttachmentCollectionResponseHandler(list, row);
            //PrXMLUtils.parseString(response.getStringContent(), getAttachmentsHandler);
        } else {
            throw new IOException("Error Processing getDocRepositoryData()");
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

    public static void main(String[] args) throws Exception {



    }
}
