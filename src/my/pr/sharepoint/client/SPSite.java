/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author PRando
 */
public class SPSite extends SPObject {

    private SharePointClient client = null;
    private SPNTCreds ntCreds = null;
    private Map<String, SimpleSite> subSites = null;
    private SPListService listService = null;
    private SPSite parentSite = null;
    private String title = null;
    private String url = null;
    private String description = null;
    private String language = null;
    private String theme = null;

    public SPSite(String url, String user, String password, String userDomain, String hostDomain) throws IOException {
        this(url, new SPNTCreds(user, password, userDomain, hostDomain));
    }

    public SPSite(String url, SPNTCreds creds) throws IOException {
        //Fix URL Spaces
        url = url.replace(" ", "%20");
        
        this.client = new SharePointClient(url, creds);
        this.ntCreds = creds;

        subSites = new HashMap<String, SimpleSite>();
        listService = new SPListService(this);

        client.getCurrentSite(this);
        loadSubSites();
        loadLists();
    }

    public SPSite(SharePointClient client) throws IOException {
        this.client = client;
        this.ntCreds = client.getNTCredentials();

        subSites = new HashMap<String, SimpleSite>();
        listService = new SPListService(this);

        client.getCurrentSite(this);
        loadSubSites();
        loadLists();
    }

    public SPListService getListService() {
        return listService;
    }

    private void loadSubSites() throws IOException {
        client.getSiteCollection(this);
    }

    private void loadLists() throws IOException {
        client.getListCollection(this);
    }

    private void setParentSite(SPSite site) {
        this.parentSite = site;
    }

    public boolean hasParentSite() {
        if (parentSite != null) {
            return true;
        }
        return false;
    }
    
    public SPSite getParentSite() {
        return parentSite;
    }

    public int getSubSitesCount() {
        return subSites.size();
    }

    public SimpleSite[] getSimpleSubSites() {
        return subSites.values().toArray(new SimpleSite[subSites.size()]);
    }

    public void addSubSite(String title, String description) {
        subSites.put(title, new SimpleSite(title, description));
    }
    
    public SPSite getSubSite(SimpleSite site) throws Exception {
        return getSubSite(site.getTitle());
    }

    public SPSite getSubSite(String name) throws Exception {
        SimpleSite simpleSite = null;
        if (subSites.containsKey(name)) {
            simpleSite = subSites.get(name);
            SPSite site = new SPSite(simpleSite.getUrl(), ntCreds);
            site.setParentSite(this);
            return site;
        }
        throw new Exception("Sub Site Not Found!");
    }

    public SharePointClient getClient() {
        return client;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getEncodedUrl() {
        return StringEscapeUtils.escapeHtml4(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public class SimpleSite {

        private String title = null;
        private String url = null;

        public SimpleSite() {
        }

        public SimpleSite(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static void main(String[] args) throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);

        SPList masList = custSite.getListService().getList("Customer Master 1");
        SPList tstList = refSite.getListService().getList("TestCustomerImportList");

        SPList cusList = custSite.getListService().getList("Cloud Customers");
        SPList verList = refSite.getListService().getList("Verticals");
        SPList ctyList = refSite.getListService().getList("Countries");
        //SPList baseList = refSite.getListService().getList("Base Cloud Customers");

        int count = 0;
        loop:
        for (SPListRow tstListRow : tstList.getRows().values()) {
            String recordId = tstListRow.getRowColumn("Record_x0020_Id");
            String solutionId = tstListRow.getRowColumn("Solution_x0020_Id");
            String customerName = tstListRow.getRowColumn("Customer_x0020_Name");
            String cSolutionId = tstListRow.getRowColumn("Import_x0020_Solution_x0020_Id");
            String cCustomerName = tstListRow.getRowColumn("Import_x0020_Customer_x0020_Name");

            SPListRow masListRow = masList.getLookupColumn("Solution_x0020_Id", solutionId);

            if (masListRow != null) {
                System.out.println("RecordId: " + recordId);
                String country = masListRow.getRowColumn("Country");
                String vertical = masListRow.getRowColumn("Vertical");

                SPListRow countryRow = ctyList.getLookupColumn("Code", country);
                SPListRow verticalRow = verList.getLookupColumn("Vertical_x0020_Name", vertical);
                String cCountry = countryRow.getId() + ";#" + country;
                String cVertical = verticalRow.getId() + ";#" + vertical;

                String cPLink = recordId + ";#" + "http://kap-us-krs/reportserver?/KGS%20Reports/Cloud/CloudCustomerPortfolio&SolutionId=" + solutionId;

                SPListRow newCusRow = cusList.getNewListRow();
                newCusRow.addRowColumn("Solution_x0020_Id", cSolutionId);
                newCusRow.addRowColumn("Customer_x0020_Name", cCustomerName);
                newCusRow.addRowColumn("Vertical", cVertical);
                newCusRow.addRowColumn("Country", cCountry);
                newCusRow.addRowColumn("Portfolio_x0020_Link", cPLink);

                cusList.insertNewRecord(newCusRow);

                
                count++;
                
                /*
                if (count >= 20) {
                    break loop;
                }
                */ 
            }

        }



        //System.out.println("Length: " + src.getFirstRows(1).length);


        //System.out.println("Sites Count: " + site.getSubSitesCount());
        //System.out.println("Lists Count: " + site.getListsCount());

        //System.out.println("Row Count: " + site.getListService().getList("Cloud Customers").getItemCount());
        //SPList cloudCustomers = site.getListService().getList("Cloud Customers");
        //SPList testList2 = site.getListService().getList("Test List 2");

        //for(String fName : testList2.getFieldNames()) {
        //System.out.println("Field Name: " + fName);
        //}

        //SPListRow row = new SPListRow();
        //row.addRowColumn("C_x0020_Solution_x0020_Id", "3;#6000071");
        //testList2.deleteRecords(row);
        /*
         SPListRow row = new SPListRow();
         row.addRowColumn("Customer_x0020_Name", "2;#Super Customer");
         cloudCustomers.insertNewRecord(row);
         System.out.println("!!!!!!!!!!!!! DELETE ROW " + row.getId());
         cloudCustomers.deleteRecord(row);
         */

        /*
         cloudCustomers.refreshData();
         LinkedHashMap<Integer, SPListRow> rows = cloudCustomers.getRows();
         Iterator<Integer> rowIds = rows.keySet().iterator();
         while(rowIds.hasNext()) {
         int rowId = rowIds.next();
         SPListRow row = rows.get(rowId);
         System.out.println("RowId: " + rowId + ", " + row.getRowColumn("Customer_x0020_Name"));
         }
         */

    }
}
