/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.pr.sharepoint.testing;

import my.pr.sharepoint.client.SPList;
import my.pr.sharepoint.client.SPListRow;
import my.pr.sharepoint.client.SPNTCreds;
import my.pr.sharepoint.client.SPSite;
import my.pr.utils.OFile;

/**
 *
 * @author Paul
 */
public class Tester {
    
    
    public static void main(String[] args) throws Exception {
        test1();
    }
    
    
    public static void test1() throws Exception {
        SPNTCreds creds = new SPNTCreds("Administrator", "pyramid007", "server-a");
        SPSite custSite = new SPSite("http://server-a/Test1", creds);

        SPList testList = custSite.getListService().getList("Test List");
        
        SPListRow row = testList.getRow(1);
        //testList.addAttachment(row, "test3.txt", "Woohoo!!".getBytes());
        System.out.println(new OFile(testList.getAttachments(row)[2].download()).convertToString());
       
    }
}
