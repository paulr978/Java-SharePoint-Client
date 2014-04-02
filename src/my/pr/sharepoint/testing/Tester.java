/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.testing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import my.pr.sharepoint.client.SPField;
import my.pr.sharepoint.client.SPList;
import my.pr.sharepoint.client.SPListRow;
import my.pr.sharepoint.client.SPListService;
import my.pr.sharepoint.client.SPNTCreds;
import my.pr.sharepoint.client.SPSite;

/**
 *
 * @author PRando
 */
public class Tester {

    public static void main(String[] args) throws Exception {


        //findDuplicates();
        //updateCCM();
        //updateCustomersOverview();

        //ovrList.updateListDescription("<span style=\"color: red;\">Currently Updating!</span>");
        //ovrList.updateListDescription("Updating!!!");



        //test1();
        //stageProductStatuses();
        //updateProductOfferings();
        //updateCustomers();
        //traverseCloud();
        
        //stageProductStatuses();
        
        //fixOrderDates();
        //fixNonStdContracts();
        //fixLiveDates();
        
        
        //checkBuildSite();
        //checkFinanceDashboard();
        
        //fixNewCustomers();
        checkFinanceDashboard();
        
        //checkUnknownAgainstLiveBO2();
        
        //fixCustomerDates();

        //validateLiveBO2();
        
        //checkBuildMaster();
        
        //checkLiveBO2();
        
        //findDuplicates();
        //checkCustomerGeneral();
        
        
        
        //checkDecommissions0303();
        //updateDecommissions0303();
        
        //exportFullCustomerList();
        
        
        
        //checkCustomerGeneral();
        
        /*
         SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
         SPSite referenceSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);
         SPSite customerSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
         SPList dest = customerSite.getListService().getList("Cloud Customers");
        
         //for(String cName : dest.getFieldNames()) {
         //    System.out.println("Dest Field Name: " + cName);
         //}
        
         SPList cMaster = referenceSite.getListService().getList("TestCustomerImportList");
         Iterator<Integer> iRows = cMaster.getRows().keySet().iterator();
         loop:while(iRows.hasNext()) {
         int rowId = iRows.next();
         SPListRow row = cMaster.getRows().get(rowId);
            
         SPListRow destRow = new SPListRow();
         destRow.addRowColumn("Solution_x0020_Id", row.getRowColumn("Import_x0020_Solution_x0020_Id"));
         destRow.addRowColumn("Customer_x0020_Name", row.getRowColumn("Import_x0020_Customer_x0020_Name"));
         dest.insertNewRecord(destRow);
         break loop;
         }
         */
    }

    private static void stageCloudCustomers() throws Exception {
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




    }

    private static void updateProductOfferings() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);

        SPList cusList = custSite.getListService().getList("Cloud Customers");
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");

        int count = 0;
        loop:
        for (SPListRow row : prdAssignList.getRows().values()) {

            SPListRow cloneRow = row.cloneNewRow();
            cloneRow.addRowColumn("Non-Standard Contract Language", "No");
            prdAssignList.updateRecord(cloneRow);

            System.out.println(count);
            count++;
            //if (count >= 3) {
            //break loop;
            //}

        }


    }
    
    
    private static void traverseCloud() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite cloudSite = new SPSite("https://kch.kronos.com/sites/Cloud", creds);
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference Data", creds);
        
        SPList list = refSite.getListService().getList("Cloud Lists");
        
        
        StringBuffer buffer = new StringBuffer();
        
        System.out.println("Site: " + cloudSite.getTitle());
        buffer.append("Site: " + cloudSite.getTitle() + "\n");
        
        if(cloudSite.getListService().getListsCount() > 0) {
                SPListService.SimpleList[] sLists = cloudSite.getListService().getSimpleLists();
                for(SPListService.SimpleList sList : sLists) {
                    System.out.println("      -> " + sList.getTitle() + ", Item Count: " + sList.getItemCount());
                    buffer.append("      -> " + sList.getTitle() + ", Item Count: " + sList.getItemCount() + "\n");
                }
        }

        traverseSite(buffer, cloudSite);
        
        System.out.println("OUTPUT: ");
        System.out.println(buffer.toString());
    }
    
    private static void traverseSite(StringBuffer buffer, SPSite site) throws Exception {
        SPSite.SimpleSite[] sites = site.getSimpleSubSites();       
        for(SPSite.SimpleSite sSite : sites) {
            SPSite subSite = site.getSubSite(sSite);
            System.out.println("Site: " + subSite.getTitle());
            buffer.append("Site: " + subSite.getTitle() + "\n");
            if(subSite.getListService().getListsCount() > 0) {
                SPListService.SimpleList[] sLists = subSite.getListService().getSimpleLists();
                for(SPListService.SimpleList sList : sLists) {
                    System.out.println("      -> " + sList.getTitle() + ", Item Count: " + sList.getItemCount());
                    buffer.append("      -> " + sList.getTitle() + ", Item Count: " + sList.getItemCount() + "\n");
                }
            }
            traverseSite(buffer, subSite);
        }
    }

    private static void updateCustomers() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);
        //SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);

        SPList basList = refSite.getListService().getList("Base Cloud Customers");
        SPList cusList = custSite.getListService().getList("Cloud Customers");
        //SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");

        int count = 0;
        loop:
        for (SPListRow row : cusList.getRows().values()) {

            SPListRow basRow = basList.getLookupColumn("Solution_x0020_Id", row.getRowCascadedLookupColumn("Solution_x0020_Id"));

            if (basRow != null) {
                SPListRow cloneRow = row.cloneNewRow();
                cloneRow.addRowColumn("Overview_x0020_Link", basRow.getId() + ";#" + basRow.getRowColumn("Lists_x0020_Overview_x0020_Link"));
                //cloneRow.addRowColumn("Non-Standard Contract Language", "No");
                cusList.updateRecord(cloneRow);

                System.out.println(count);
                count++;
                //if (count >= 3) {
                    //break loop;
                //}
            }


        }


    }

    private static void stageProductOfferings() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);

        SPList masList = refSite.getListService().getList("TestMasterList");
        SPList cusList = custSite.getListService().getList("Cloud Customers");
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");

        SPList prdList = refSite.getListService().getList("Product Offerings");
        SPList dcList = refSite.getListService().getList("Data Centers");
        for (SPField f : dcList.getFields()) {
            System.out.println("DC Field: " + f.getName());
        }
        SPList solList = refSite.getListService().getList("Solution Offerings");

        int count = 0;
        loop:
        for (SPListRow masListRow : masList.getRows().values()) {
            String recordId = String.valueOf(masListRow.getId());
            String solutionId = masListRow.getRowColumn("SID");
            //String customerName = masListRow.getRowColumn("Solution_x0020_Owner_x0020_Name");
            String dc = masListRow.getRowColumn("Hosting_Center");
            String product = masListRow.getRowColumn("Product");
            String solution = masListRow.getRowColumn("Offering");


            SPListRow cusListRow = cusList.getLookupColumn("Solution_x0020_Id", solutionId);
            SPListRow dcListRow = dcList.getLookupColumn("Name", dc);
            SPListRow prdListRow = prdList.getLookupColumn("Name", product);
            SPListRow solListRow = solList.getLookupColumn("Solution_x0020_Offering", solution);

            if (dcListRow != null && prdListRow != null && solListRow != null) {
                System.out.println("RecordId: " + recordId);

                String cSolutionId = cusListRow.getRowColumn("Solution_x0020_Id");
                String cCustomerName = cusListRow.getRowColumn("Customer_x0020_Name");
                String cProduct = prdListRow.getId() + ";#" + prdListRow.getRowColumn("Name");
                String cDc = dcListRow.getId() + ";#" + dcListRow.getRowColumn("Name");
                String cSol = solListRow.getId() + ";#" + solListRow.getRowColumn("Solution_x0020_Offering");

                SPListRow newPrdAssignRow = prdAssignList.getNewListRow();
                newPrdAssignRow.addRowColumn("Solution_x0020_Id", cSolutionId);
                newPrdAssignRow.addRowColumn("Customer_x0020_Name", cCustomerName);
                newPrdAssignRow.addRowColumn("Product_x0020_Offering", cProduct);
                newPrdAssignRow.addRowColumn("Data_x0020_Center", cDc);
                newPrdAssignRow.addRowColumn("Solution_x0020_Type", cSol);
                newPrdAssignRow.addRowColumn("Effective_x0020_Date", "1/1/1900");

                prdAssignList.insertNewRecord(newPrdAssignRow);


                count++;


                //if (count >= 2) {
                //break loop;
                //}

            }

        }




    }
    
    private static void updateProductStatuses() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);

        SPList prdAssignList = refSite.getListService().getList("Product Offering Assignments Sync");
        SPList prdStatusList = prdSite.getListService().getList("Product Assignment Status History");
    
        
        int count = 0;
        loop:
        for (SPListRow prdStatusRow : prdStatusList.getRows().values()) {
            
            String cSolutionId = prdStatusRow.getRowColumn("Solution_x0020_Id");
            //String cDc = prdStatusRow.getRowColumn("Data_x0020_Center");
            
            Map map = new HashMap();
            map.put("Solution_x0020_Id", cSolutionId);
            //map.put("Data_x0020_Center", cDc);
            SPListRow prdAssignRow = prdAssignList.getFirstRowContainingColumnValues(map);
        
            count++;
            if (count >= 3) {
                break loop;
            }
        }
        
    }
    
        private static void fixLiveDates() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Assignment Status History");
        SPList buildList = testSite.getListService().getList("OrderDates_LiveDates");
        
        int count = 0;
        loop:
        for (SPListRow prdListRow : prdAssignList.getRows().values()) {
            
            String solutionId = prdListRow.getRowCascadedLookupColumn("Solution Id");
            String pIdentifier = prdListRow.getRowCascadedLookupColumn("Product Identifier");
            String dc = "BO3";
            if(pIdentifier.contains("BO2")) dc = "BO2";
            
            Map map = new HashMap();
            map.put("LinkTitle", solutionId);
            map.put("Data Center", dc);
            SPListRow buildRow = buildList.getFirstRowContainingColumnValues(map);
            
            if(buildRow == null) continue loop;
            
            if(!buildRow.containsColumnName("Actual Go-live Date")) continue loop;
            
            String effectiveDate = buildRow.getRowColumn("Actual Go-live Date");
            
            if(effectiveDate == null) continue loop;
            if(effectiveDate.equalsIgnoreCase("")) continue loop;
            if(effectiveDate.contains("1900-01-01")) continue loop;
            if(effectiveDate.contains("2099-12-31")) continue loop;
            
            SPListRow update = prdListRow.cloneNewRow();
            update.addRowColumn("Status Type", "2;#Live");
            update.addRowColumn("Effective Date", effectiveDate);
            prdAssignList.updateRecord(update);
            
            count++;
            //if (count >= 15) {
                //break loop;
            //}
            
        }
        
    }
    
    
        
    private static void fixCustomerDates() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Assignment Status History");

        int count = 0;
        loop:
        for (SPListRow prdListRow : prdAssignList.getRows().values()) {
            
            String solutionId = prdListRow.getRowColumn("Solution Id");
            String customerName = prdListRow.getRowColumn("Customer Name");
            //String dc = prdListRow.getRowColumn("Data Center");
            
            SPListRow update = prdListRow.cloneNewRow();
            update.addRowColumn("Test", "1");
            prdAssignList.updateRecord(update);
            
            count++;
            System.out.println("Count: " + count + ", Updating Solution Id: " + solutionId + ", Customer Name: " + customerName);
            //if (count >= 13) {
            //    break loop;
            //}
            
        }
        
    }        
        
        
        
    
    private static void fixOrderDates() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");
        SPList buildList = testSite.getListService().getList("OrderDates_LiveDates");
        
        int count = 0;
        loop:
        for (SPListRow prdListRow : prdAssignList.getRows().values()) {
            
            String solutionId = prdListRow.getRowCascadedLookupColumn("Solution Id");
            String dc = prdListRow.getRowCascadedLookupColumn("Data Center");
            
            Map map = new HashMap();
            map.put("LinkTitle", solutionId);
            map.put("Data Center", dc);
            SPListRow buildRow = buildList.getFirstRowContainingColumnValues(map);
            
            if(buildRow == null) continue loop;
            
            String effectiveDate = buildRow.getRowColumn("Order Date");
            
            SPListRow update = prdListRow.cloneNewRow();
            update.addRowColumn("Effective Date", effectiveDate);
            prdAssignList.updateRecord(update);
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
            
        }
        
    }
            
    
    
    private static void checkUnknownAgainstLiveBO2() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = testSite.getListService().getList("Live_BO2");
        prdAssignList.getRows();
        
        SPList buildList = testSite.getListService().getList("UnknownBO2");
        
        int count = 0;
        loop:
        for (SPListRow buildListRow : buildList.getRows().values()) {
            
            String solutionId = null;
            String customerName = null;
            String dc = null;
            
            if(buildListRow.containsColumnName("Solution Id")) {
                solutionId = buildListRow.getRowColumn("Solution Id");
                customerName = buildListRow.getRowColumn("Customer Name");
                dc = buildListRow.getRowColumn("DC");
            }
            else {
                continue loop;
            }

            
            Map map = new HashMap();
            map.put("Column1", solutionId);
            map.put("Column3", dc);
            SPListRow buildRow = prdAssignList.getFirstRowContainingColumnValues(map);
            
            if(buildRow == null) {
                //buffer.append("Missing Customer: " + solutionId + ", Name: " + customerName + ", DC: " + dc + "\n");
                buffer.append(solutionId + ",  " + dc + ", " + customerName + "\n");
                System.out.println("Customer Missing!!! - " + solutionId);
            
            }
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            
        }
        
        System.out.println("Missing Report: ");
        System.out.println(buffer.toString());
        
    }
     
    
    
    private static void validateLiveBO2() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList liveBO2List = testSite.getListService().getList("Live_BO2");
        
        
        SPList prdAssignList = prdSite.getListService().getList("Customer Product Offering Status Dates");
        SPList bo2DecommissionsList = testSite.getListService().getList("DecommissionsList331");
        SPList master0219 = testSite.getListService().getList("CustomerMaster0219");
        SPList finance0228 = testSite.getListService().getList("FinanceDashboard0228");
        SPList buildTracker = testSite.getListService().getList("BuildMaster0228");
        //SPList buildList = testSite.getListService().getList("UnknownBO2");
        
        
        buffer.append("Solution Id\tData Center\tCustomer Name\tExists in New Master\tIn Decommission List\tMaster as of 0219\tFinance Dashboard 0228\tBuild Master 0228");
        buffer.append("\n");
        
        int count = 0;
        loop:
        for (SPListRow buildListRow : liveBO2List.getRows().values()) {
            
            String solutionId = null;
            String customerName = null;
            String dc = "BO2";
            
            if(buildListRow.containsColumnName("LinkTitle")) {
                solutionId = buildListRow.getRowColumn("LinkTitle");
                customerName = buildListRow.getRowColumn("Column2");
            }
            else {
                continue loop;
            }

            
            
            
            buffer.append(solutionId + "\t" + dc + "\t" + customerName + "\t");
            
            Map map = new HashMap();
            map.put("Solution_x0020_Id", solutionId);
            map.put("Data_x0020_Center", dc);
            //map.put("Current Status", "Live");
            SPListRow row = prdAssignList.getFirstRowContainingColumnValues(map);
            
            if(row == null) {
                //System.out.println("Unable to find entry: " + solutionId);
                buffer.append("False\t");
            }
            else {
                prdAssignList.markRow(row);
                buffer.append("True\t");
            }
            
            map = new HashMap();
            map.put("LinkTitle", solutionId);
            row = bo2DecommissionsList.getFirstRowContainingColumnValues(map);
            
            if(row == null) {
                buffer.append("False\t");
            }
            else {
                bo2DecommissionsList.markRow(row);
                buffer.append("True\t");
            }
            
            map = new HashMap();
            map.put("SID", solutionId);
            map.put("Hosting_Center", "BO2");
            row = master0219.getFirstRowContainingColumnValues(map);
            
            if(row == null) {
                buffer.append("False\t");
            }
            else {
                master0219.markRow(row);
                buffer.append("True\t");
            }
            
            map = new HashMap();
            map.put("LinkTitle", solutionId);
            map.put("DC", "BO2");
            row = finance0228.getFirstRowContainingColumnValues(map);
            
            if(row == null) {
                buffer.append("False\t");
            }
            else {
                finance0228.markRow(row);
                buffer.append("True\t");
            }       
            
            map = new HashMap();
            map.put("LinkTitle", solutionId);
            map.put("Data Center", "BO2");
            row = buildTracker.getFirstRowContainingColumnValues(map);
            
            if(row == null) {
                buffer.append("False\t");
            }
            else {
                buildTracker.markRow(row);
                buffer.append("True\t");
            }     
            
            
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            buffer.append("\n");
            
        }
        
        System.out.println("Missing Report: ");
        System.out.println(buffer.toString());
        
        System.out.println();
        System.out.println(prdAssignList.getUnMarkedRows().size());
        System.out.println(bo2DecommissionsList.getUnMarkedRows().size());
        System.out.println(master0219.getUnMarkedRows().size());
        System.out.println(finance0228.getUnMarkedRows().size());
        System.out.println(buildTracker.getUnMarkedRows().size());
        
    }
     

    
    private static void checkLiveBO2() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
         
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList liveBO2List = testSite.getListService().getList("Live_BO2");
        
        
        SPList prdAssignList = prdSite.getListService().getList("Customer Product Offering Status Dates");
        
        
        buffer.append("Solution Id\tData Center\tCustomer Name\tExists in New Master\tIn Decommission List\tMaster as of 0219\tFinance Dashboard 0228\tBuild Master 0228");
        buffer.append("\n");
        
        int count = 0;
        loop:
        for (SPListRow buildListRow : liveBO2List.getRows().values()) {
            
            String solutionId = null;
            String customerName = null;
            String dc = "BO2";
            
            if(buildListRow.containsColumnName("LinkTitle")) {
                solutionId = buildListRow.getRowColumn("LinkTitle");
                customerName = buildListRow.getRowColumn("Column2");
            }
            else {
                continue loop;
            }

            
            
            Map map = new HashMap();
            map.put("Solution_x0020_Id", solutionId);
            map.put("Data_x0020_Center", dc);
            //map.put("Current Status", "Live");
            SPListRow row = prdAssignList.getFirstRowContainingColumnValues(map);
            
            if(row == null) {
                buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\n");
            }
            else {
                continue loop;
            }
            
            
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            //buffer.append("\n");
            
        }
        
        System.out.println("Missing Report: ");
        System.out.println(buffer.toString());
        

    }
    
    
    
    
    private static void fixNewCustomers() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
         
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        //SPList liveBO2List = testSite.getListService().getList("Live_BO2");
        
        
        SPList statusList = prdSite.getListService().getList("Customer Product Offering Status Dates");
        SPList buildMaster = testSite.getListService().getList("BuildMaster0304");
        SPList prdStatusList = prdSite.getListService().getList("Product Assignment Status History");
        
        
        
        buffer.append("Solution Id\tData Center\tCustomer Name\tExists in New Master\tIn Decommission List\tMaster as of 0219\tFinance Dashboard 0228\tBuild Master 0228");
        buffer.append("\n");
        
        int count = 0;
        loop:
        for (SPListRow statusListRow : statusList.getRows().values()) {
            
            String solutionId = null;
            String customerName = null;
            String dc = null;
            
            if(statusListRow.containsColumnName("Solution Id")) {
                solutionId = statusListRow.getRowColumn("Solution Id");
                customerName = statusListRow.getRowColumn("Customer Name");
                dc = statusListRow.getRowColumn("Data Center");
            }
            else {
                continue loop;
            }

            if(statusListRow.containsColumnName("Current Status")) {
                //System.out.println("Current Status: " + statusListRow.getRowColumn("Current Status"));
                continue loop;
            }
            //else {
            
            //}
            
            /*
            Map map = new HashMap();
            map.put("LinkTitle", solutionId);
            map.put("Data_x0020_Center", dc);
            //map.put("Current Status", "Live");
            SPListRow row = buildMaster.getFirstRowContainingColumnValues(map);
            
            if(row == null) {
                //buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\n");
            }
            */
            
            Map map = new HashMap();
            map.put("LinkTitle", solutionId);
            map.put("Data_x0020_Center", dc);
            //map.put("Current Status", "Live");
            SPListRow row = buildMaster.getFirstRowContainingColumnValues(map);
            
            if(row == null) {
                //buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\n");
            }
            else {
                
                String buildSolutionId = row.getRowColumn("LinkTitle");
                String buildDc = row.getRowColumn("Data_x0020_Center");
                String buildCustomerName = row.getRowColumn("Customer Name");
                
                String orderDate = "";
                if(row.containsColumnName("Order_x0020_Date")) 
                    orderDate = row.getRowColumn("Order_x0020_Date");
                
                String proposedGoLive = "";
                if(row.containsColumnName("Proposed Go-Live Date"))
                    proposedGoLive = row.getRowColumn("Proposed Go-Live Date");
                
                String actualGoLive = "";
                if(row.containsColumnName("Actual Go-live Date"))
                    actualGoLive = row.getRowColumn("Actual Go-live Date");
                
                buffer.append(buildSolutionId + "\t" + customerName + "\t" + buildCustomerName + "\t" + buildDc + "\t" 
                        + orderDate + "\t" + proposedGoLive + "\t" + actualGoLive + "\n");
                //continue loop;
            }
            
            
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            //buffer.append("\n");
            
        }
        
        System.out.println("Report: ");
        System.out.println(buffer.toString());
        

    }
        
    
    
    
    
    
    private static void exportFullCustomerList() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList liveBO2List = testSite.getListService().getList("Live_BO2");
        
        
        SPList prdAssignList = prdSite.getListService().getList("Customer Product Offering Status Dates");
        SPList bo2DecommissionsList = testSite.getListService().getList("DecommissionsList331");
        SPList master0219 = testSite.getListService().getList("CustomerMaster0219");
        SPList finance0228 = testSite.getListService().getList("FinanceDashboard0228");
        SPList buildTracker = testSite.getListService().getList("BuildMaster0228");
        //SPList buildList = testSite.getListService().getList("UnknownBO2");
        
        
        buffer.append("Solution Id\tData Center\tCustomer Name\tExists in New Master\tIn Decommission List\tMaster as of 0219\tFinance Dashboard 0228\tBuild Master 0228");
        buffer.append("\n");
        
        int count = 0;
        loop:
        for (SPListRow buildListRow : liveBO2List.getRows().values()) {
            
            String solutionId = buildListRow.getRowColumn("LinkTitle");
            String customerName = buildListRow.getRowColumn("Column2");
            String dc = buildListRow.getRowColumn("Column3");
            
            buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\t");
            buffer.append("\n");
        }
            
        for(SPListRow row : bo2DecommissionsList.getRows().values()) {
            
            String solutionId = row.getRowColumn("LinkTitle");
            String customerName = row.getRowColumn("Customer");
            String dc = "BO2";
            
            buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\t");
            buffer.append("\n");
        }
        
        for(SPListRow row : master0219.getRows().values()) {
            
            String solutionId = row.getRowColumn("SID");
            String customerName = row.getRowColumn("Solution_Owner_Name");
            String dc = row.getRowColumn("Hosting_Center");
            
            buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\t");
            buffer.append("\n");
        }

        for(SPListRow row : finance0228.getRows().values()) {
            
            String solutionId = "";
            if(row.containsColumnName("LinkTitle")) {
                solutionId = row.getRowColumn("LinkTitle");
            }
            
            String customerName = row.getRowColumn("Customer_x0020_Name");
            String dc = row.getRowColumn("DC");
            
            buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\t");
            buffer.append("\n");
        }
           
        for(SPListRow row : buildTracker.getRows().values()) {
            
            String solutionId = "";
            if(row.containsColumnName("LinkTitle")) {
                solutionId = row.getRowColumn("LinkTitle");
            }
            
            String customerName = row.getRowColumn("Customer Name");
            String dc = row.getRowColumn("Data Center");
            
            buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\t");
            buffer.append("\n");
        }            
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            buffer.append("\n");
            
            System.out.println(buffer.toString());
       
    }    
    
    
    
    private static void checkBuildSite() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");
        prdAssignList.getRows();
        
        SPList buildList = testSite.getListService().getList("BuildMaster0228");
        
        int count = 0;
        loop:
        for (SPListRow buildListRow : buildList.getRows().values()) {
            
            String solutionId = null;
            String customerName = null;
            String dc = null;
            
            if(buildListRow.containsColumnName("LinkTitle")) {
                solutionId = buildListRow.getRowColumn("LinkTitle");
                customerName = buildListRow.getRowColumn("Customer Name");
                dc = buildListRow.getRowColumn("Data Center");
            }
            else {
                continue loop;
            }

            
            Map map = new HashMap();
            map.put("Solution Id", solutionId);
            map.put("Data Center", dc);
            SPListRow buildRow = prdAssignList.getFirstRowContainingColumnValues(map);
            
            if(buildRow == null) {
                //buffer.append("Missing Customer: " + solutionId + ", Name: " + customerName + ", DC: " + dc + "\n");
                buffer.append(solutionId + ",  " + dc + ", " + customerName + "\n");
                System.out.println("Customer Missing!!! - " + solutionId);
            
            }
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            
        }
        
        System.out.println("Missing Report: ");
        System.out.println(buffer.toString());
        
    }
           
    private static void checkCustomerGeneral() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");
        prdAssignList.getRows();
        
        SPList buildList = testSite.getListService().getList("CustomerGeneral0302A");
        
        int count = 0;
        loop:
        for (SPListRow buildListRow : buildList.getRows().values()) {
            
            String solutionId = null;
            String customerName = null;
            String dc = null;
            
            if(buildListRow.containsColumnName("LinkTitle")) {
                solutionId = buildListRow.getRowColumn("LinkTitle");
                customerName = buildListRow.getRowColumn("Client_x0020_Name");
            }
            else {
                continue loop;
            }
            if(buildListRow.containsColumnName("Hosting Center")) {
                dc = buildListRow.getRowColumn("Hosting Center");
                
                if(dc.equalsIgnoreCase("Self Hosted") || dc.trim().equalsIgnoreCase("")) continue loop;
            }
            else {
                continue loop;
            }

            
            Map map = new HashMap();
            //map.put("Solution Id", solutionId);
            map.put("Customer Name", customerName);
            //map.put("Data Center", dc);
            SPListRow buildRow = prdAssignList.getFirstRowContainingColumnValues(map);
            
            if(buildRow == null) {
                //buffer.append("Missing Customer: " + solutionId + ", Name: " + customerName + ", DC: " + dc + "\n");
                buffer.append(solutionId + "\t" + customerName + "\t" + dc);
                //System.out.println("Customer Missing!!! - " + solutionId);
                buffer.append("\n");
            }
            
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            
        }
        
        System.out.println("Missing Report: ");
        System.out.println(buffer.toString());
        
    }
              
    
    private static void checkFinanceDashboard() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");
        prdAssignList.getRows();
        
        SPList buildList = testSite.getListService().getList("FinanceDashboard0228");
        
        int count = 0;
        loop:
        for (SPListRow buildListRow : buildList.getRows().values()) {
            
            String solutionId = null;
            String customerName = null;
            String dc = null;

            if(buildListRow.containsColumnName("LinkTitle")) {
                solutionId = buildListRow.getRowColumn("LinkTitle");
                customerName = buildListRow.getRowColumn("Customer Name");
                dc = buildListRow.getRowColumn("DC");
            }
            else {
                continue loop;
            }

            
            Map map = new HashMap();
            map.put("Solution Id", solutionId);
            map.put("Data Center", dc);
            SPListRow buildRow = prdAssignList.getFirstRowContainingColumnValues(map);
            
            if(buildRow == null) {
                //buffer.append("Missing Customer: " + solutionId + ", Name: " + customerName + ", DC: " + dc + "\n");
                buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\n");
                //System.out.println("Customer Missing!!! - " + solutionId);
            
            }
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            
        }
        
        System.out.println("Missing Report: ");
        System.out.println(buffer.toString());
        
    }
    
    
    
    
    private static void checkDecommissions0303() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");
        prdAssignList.getRows();
        
        SPList decomList = testSite.getListService().getList("Decommissions0303");
        
        int count = 0;
        loop:
        for (SPListRow decomListRow : decomList.getRows().values()) {
            
            String solutionId = null;
            String customerName = null;
            String dc = "BO2";

            if(decomListRow.containsColumnName("LinkTitle")) {
                solutionId = decomListRow.getRowColumn("LinkTitle");
                customerName = decomListRow.getRowColumn("Customer");
                //dc = decomListRow.getRowColumn("DC");
            }
            else {
                continue loop;
            }

            
            Map map = new HashMap();
            map.put("Solution Id", solutionId);
            //map.put("Data Center", dc);
            SPListRow buildRow = prdAssignList.getFirstRowContainingColumnValues(map);
            
            if(buildRow == null) {
                //buffer.append("Missing Customer: " + solutionId + ", Name: " + customerName + ", DC: " + dc + "\n");
                buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\n");
                //System.out.println("Customer Missing!!! - " + solutionId);
            
            }
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            
        }
        
        System.out.println("Missing Report: ");
        System.out.println(buffer.toString());
        
    }
        
    
    
    private static void updateDecommissions0303() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");
        prdAssignList.getRows();
        
        SPList prdStatusList = prdSite.getListService().getList("Product Assignment Status History");
        prdStatusList.getRows();
        
        SPList decomList = testSite.getListService().getList("Decommissions0303");
        
        int count = 0;
        loop:
        for (SPListRow decomListRow : decomList.getRows().values()) {
            
            String solutionId = null;
            String customerName = null;
            String dc = "BO2";

            if(decomListRow.containsColumnName("LinkTitle")) {
                solutionId = decomListRow.getRowColumn("LinkTitle");
                customerName = decomListRow.getRowColumn("Customer");
                //dc = decomListRow.getRowColumn("DC");
            }
            else {
                continue loop;
            }

            
            Map map = new HashMap();
            map.put("Solution Id", solutionId);
            //map.put("Data Center", dc);
            SPListRow buildRow = prdAssignList.getFirstRowContainingColumnValues(map);
            
            if(buildRow != null) {
                //buffer.append("Missing Customer: " + solutionId + ", Name: " + customerName + ", DC: " + dc + "\n");
                buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\n");
                //System.out.println("Customer Missing!!! - " + solutionId);
                
                SPListRow[] statusRows = prdStatusList.getRowsByContainingSingleColumn("Solution Id", solutionId);
                for(SPListRow status : statusRows) {
                     buffer.append("   --> " + status.getRowColumn("Product Identifier") + " - " + status.getRowColumn("Status Type") + " - " + status.getRowColumn("Effective Date") + "\n");
                }
            
            }
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            
        }
        
        System.out.println("Missing Report: ");
        System.out.println(buffer.toString());
        
    }
    
    
    
    
    private static void checkBuildMaster() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");
        prdAssignList.getRows();
        
        buffer.append("SolutionId\tCustomer Name\tDataCenter\tProject Status\tProposed Go-Live\tActual Go-Live\n");
        
        SPList buildList = testSite.getListService().getList("BuildMaster0228");
        
        int count = 0;
        loop:
        for (SPListRow buildListRow : buildList.getRows().values()) {
            
            String solutionId = null;
            String customerName = null;
            String dc = null;

            if(buildListRow.containsColumnName("LinkTitle")) {
                solutionId = buildListRow.getRowColumn("LinkTitle");
                customerName = buildListRow.getRowColumn("Customer_x0020_Name");
                dc = buildListRow.getRowColumn("Data_x0020_Center");
            }
            else {
                continue loop;
            }

            if(buildListRow.containsColumnName("Project_x0020_Status")) {
                if(!buildListRow.getRowColumn("Project_x0020_Status").equalsIgnoreCase("Completed") && !buildListRow.getRowColumn("Project_x0020_Status").equalsIgnoreCase("Pro Services")) {
                    //continue loop;
                }
            }
            else {
                //continue loop;
            }
            
            Map map = new HashMap();
            map.put("Solution Id", solutionId);
            map.put("Data Center", dc);
            SPListRow buildRow = prdAssignList.getFirstRowContainingColumnValues(map);
            
            if(buildRow == null) {
                buffer.append(solutionId + "\t" + customerName + "\t" + dc + "\t" + buildListRow.getRowColumn("Project_x0020_Status") + "\t");
                
                String dates = "";
                if(buildListRow.containsColumnName("Actual Go-live Date")) {
                    buffer.append(buildListRow.getRowColumn("Actual go-live Date"));
                }
                buffer.append("\t");
                
                if(buildListRow.containsColumnName("Proposed Go-Live Date")) {
                    buffer.append(buildListRow.getRowColumn("Proposed Go-Live Date"));
                }
                buffer.append("\t");
                //System.out.println("Customer Missing!!! - " + solutionId);
                buffer.append("\n");
            }
            
    
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
           
            
        }
        
        System.out.println("Missing Report: ");
        System.out.println(buffer.toString());
        
    }    
    
    
    
    
    
    private static void fixNonStdContracts() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        
        //SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);
        
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");
        //SPList buildList = testSite.getListService().getList("OrderDates_LiveDates");
        
        int count = 0;
        loop:
        for (SPListRow prdListRow : prdAssignList.getRows().values()) {
            
            String solutionId = prdListRow.getRowCascadedLookupColumn("Solution Id");
            String dc = prdListRow.getRowCascadedLookupColumn("Data Center");
            
            /*
            Map map = new HashMap();
            map.put("LinkTitle", solutionId);
            map.put("Data Center", dc);
            SPListRow buildRow = buildList.getFirstRowContainingColumnValues(map);
            
            if(buildRow == null) continue loop;
            
            String effectiveDate = buildRow.getRowColumn("Order Date");
            
            SPListRow update = prdListRow.cloneNewRow();
            update.addRowColumn("Effective Date", effectiveDate);
            prdAssignList.updateRecord(update);
            
            */ 
            
            SPListRow update = prdListRow.cloneNewRow();
            update.addRowColumn("Non-Standard Contract Language", "Unknown");
            prdAssignList.updateRecord(update);
            
            count++;
            //if (count >= 1) {
            //    break loop;
            //}
            
        }
        
    }

    
    

    private static void stageProductStatuses() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);

        SPList masList = refSite.getListService().getList("TestMasterList");
        SPList cusList = custSite.getListService().getList("Cloud Customers");
        SPList prdAssignList = refSite.getListService().getList("Product Offering Assignments Sync");
        SPList prdStatusList = prdSite.getListService().getList("Product Assignment Status History");
        SPList statusList = refSite.getListService().getList("Status Types");
        for (String cName : prdStatusList.getFieldNames()) {
            System.out.println("statusField Name: " + cName);
        }

        int count = 0;
        loop:
        for (SPListRow masListRow : masList.getRows().values()) {
            String recordId = String.valueOf(masListRow.getId());
            String solutionId = masListRow.getRowColumn("SID");
            //String customerName = masListRow.getRowColumn("Solution_x0020_Owner_x0020_Name");
            String dc = masListRow.getRowColumn("Hosting_Center");
            String product = masListRow.getRowColumn("Product");
            String solution = masListRow.getRowColumn("Offering");

            String status = masListRow.getRowColumn("Live");
            if (status.equalsIgnoreCase("1")) {
                status = "Live";
            } else if (status.equalsIgnoreCase("0")) {
                status = "Not Live";
            }

            SPListRow cusRow = cusList.getLookupColumn("Solution_x0020_Id", solutionId);

            Map map = new HashMap();
            map.put("Solution_x0020_Id", solutionId);
            map.put("Data_x0020_Center", dc);
            SPListRow prdAssignRow = prdAssignList.getFirstRowContainingColumnValues(map);
            //SPListRow prdAssignRow = prdAssignList.getLookupColumn("Solution_x0020_Id", solutionId);


            SPListRow cusListRow = cusList.getLookupColumn("Solution_x0020_Id", solutionId);
            SPListRow statusRow = statusList.getLookupColumn("Customer_x0020_Status_x0020_Type", status);

            if (cusRow != null && prdAssignRow != null && cusListRow != null & statusRow != null) {
                System.out.println("RecordId: " + recordId);

                //String cSolutionId = cusListRow.getRowColumn("Solution_x0020_Id");
                //String cCustomerName = cusListRow.getRowColumn("Customer_x0020_Name");
                String cSolutionId = cusRow.getRowColumn("Solution_x0020_Id");
                String cCustomerName = cusRow.getRowColumn("Customer_x0020_Name");

                String cProductIdentifier = prdAssignRow.getId() + ";#" + prdAssignRow.getRowColumn("Product_x0020_Identifier");
                String cStatus = statusRow.getId() + ";#" + statusRow.getRowColumn("Customer_x0020_Status_x0020_Type");

                SPListRow newPrdStatusRow = prdStatusList.getNewListRow();
                newPrdStatusRow.addRowColumn("Solution_x0020_Id", cSolutionId);
                newPrdStatusRow.addRowColumn("Customer_x0020_Name", cCustomerName);
                newPrdStatusRow.addRowColumn("Product_x0020_Identifier", cProductIdentifier);
                newPrdStatusRow.addRowColumn("Status_x0020_Type", cStatus);
                //newPrdStatusRow.addRowColumn("Effective_x0020_Date", "1/1/1900 12:00:00");

                prdStatusList.insertNewRecord(newPrdStatusRow);


                count++;


                //if (count >= 3) {
                //break loop;
                //}

            }

        }




    }

    private static void findDuplicates() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite custSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);

        //SPList masList = custSite.getListService().getList("Customer Master 1");
        //SPList baseCustomers = refSite.getListService().getList("Base Cloud Customers");
        //SPList cloudCustomers = custSite.getListService().getList("Cloud Customers");

        SPList prodOffering = refSite.getListService().getList("Base Cloud Customers");
        SPListRow[] dups = prodOffering.getDuplicateRows("Solution Id", true);
        for (SPListRow dup : dups) {
            //cloudCustomers.deleteRecord(dup);
            System.out.println("Deleting Duplicate: " + dup.getRowColumn("Solution Id") + " " + dup.getRowColumn("Customer Name"));
        }


    }

    private static void updateCustomersOverview() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        SPSite cusSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite ccmSite = new SPSite("https://kch.kronos.com/sites/Cloud/Organizations/CCM", creds);

        SPList ovrList = refSite.getListService().getList("Customer Overview");
        //ovrList.updateListDescription("<span style=\"color: red;\">Currently Updating!</span>");
        //ovrList.updateListDescription("Updating!!!");

        SPList basList = refSite.getListService().getList("Base Cloud Customers");
        SPList cusList = cusSite.getListService().getList("Cloud Customers");
        SPList prdAssignList = prdSite.getListService().getList("Product Offering Assignments");
        SPList prdStatusList = prdSite.getListService().getList("Customer Product Assignment Status History");
        SPList ccmList = ccmSite.getListService().getList("Cloud Customer Managers (CCM)");


        int count = 0;
        loop:
        for (SPListRow row : basList.getRows().values()) {
            String id = String.valueOf(row.getId());
            String solutionId = row.getRowColumn("Solution Id");
            String customerName = row.getRowColumn("Customer Name");
            String portfolioLink = row.getRowColumn("Portfolio Link");

            SPListRow cusRow = cusList.getLookupColumn("Solution Id", solutionId);
            String vertical = cusRow.getRowColumn("Vertical");
            String country = cusRow.getRowColumn("Country");

            String prdOfferings = "";
            SPListRow[] prdAssignRows = prdAssignList.getRowsByContainingSingleColumn("Solution Id", solutionId);
            for (SPListRow prdAssignRow : prdAssignRows) {
                String offering = prdAssignRow.getRowColumn("Product Offering");
                prdOfferings += offering.substring(offering.indexOf(";#") + 2) + "\n";
            }

            SPListRow ovrRow = ovrList.getNewListRow();
            ovrRow.addRowColumn("Solution Id", solutionId);
            ovrRow.addRowColumn("Customer Name", customerName);
            ovrRow.addRowColumn("Vertical", vertical.substring(vertical.indexOf(";#") + 2));
            ovrRow.addRowColumn("Country", country.substring(country.indexOf(";#") + 2));
            ovrRow.addRowColumn("Product Offerings", prdOfferings);
            ovrList.insertNewRecord(ovrRow);

            count++;
            if (count >= 3) {
                break loop;
            }

        }
    }

    private static void updateCCM() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite refSite = new SPSite("https://kch.kronos.com/sites/Cloud/Reference%20Data", creds);
        SPSite prdSite = new SPSite("https://kch.kronos.com/sites/Cloud/Product%20Offerings", creds);
        SPSite cusSite = new SPSite("https://kch.kronos.com/sites/Cloud/Customers", creds);
        SPSite ccmSite = new SPSite("https://kch.kronos.com/sites/Cloud/Organizations/CCM", creds);

        //SPList ovrList = refSite.getListService().getList("Customer Overview");
        //ovrList.updateListDescription("<span style=\"color: red;\">Currently Updating!</span>");
        //ovrList.updateListDescription("Updating!!!");

        SPList masList = refSite.getListService().getList("TestMasterList");

        SPList basList = refSite.getListService().getList("Base Cloud Customers");
        SPList cusList = cusSite.getListService().getList("Cloud Customers");


        SPList ccmList = refSite.getListService().getList("Cloud Customer Managers (CCM)");
        SPList ccmAssignList = refSite.getListService().getList("CCM Assignments");


        int count = 0;
        loop:
        for (SPListRow row : basList.getRows().values()) {
            String id = String.valueOf(row.getId());
            String solutionId = row.getRowColumn("Solution Id");
            String customerName = row.getRowColumn("Customer Name");

            SPListRow masRow = masList.getLookupColumn("SID", solutionId);


            if (masRow != null) {
                if (masRow.getRowColumn("Solution_Owner_Name").equalsIgnoreCase(customerName)) {
                    String ccmName = masRow.getRowColumn("CCM_Name");
                    SPListRow ccmRow = ccmList.getLookupColumn("Full Name", ccmName);

                    if (ccmRow != null && masRow != null) {
                        String designation = "1;#Primary";
                        solutionId = row.getId() + ";#" + solutionId;
                        customerName = row.getId() + ";#" + customerName;
                        ccmName = ccmRow.getId() + ";#" + ccmName;

                        SPListRow ccmAssignRow = ccmAssignList.getNewListRow();
                        ccmAssignRow.addRowColumn("Solution Id", solutionId);
                        ccmAssignRow.addRowColumn("Customer Name", customerName);
                        ccmAssignRow.addRowColumn("CCM Assignment", ccmName);
                        ccmAssignRow.addRowColumn("Designation", designation);

                        ccmAssignList.insertNewRecord(ccmAssignRow);

                        count++;
                        System.out.println(count);
                        //if (count >= 1) {
                        //    break loop;
                        //}
                    }
                }
            }

        }
    }

    private static void test1() throws Exception {
        SPNTCreds creds = new SPNTCreds("prando", "pyramid007Kronos5", "corporate_nt");
        SPSite testSite = new SPSite("https://kch.kronos.com/sites/Cloud/Testing", creds);

        SPList srcList = testSite.getListService().getList("Source List");
        SPList desList = testSite.getListService().getList("Destination List");

        for (SPListRow row : srcList.getRows().values()) {
            String value = row.getRowColumn("Solution Id");
            SPListRow desRow = desList.getNewListRow();
            desRow.addRowColumn("Solution Id", value);
            desList.insertNewRecord(desRow);
        }
    }
}
