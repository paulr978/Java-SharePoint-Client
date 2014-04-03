/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.pr.sharepoint.client;

import java.io.IOException;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Paul
 */
public class SPAttachment extends SPObject {
    
    private SPListRow row = null;
    private String url = null;
    private String fileName = null;
    private byte[] bytes = null;
    
    public SPAttachment(String fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }
    
    public SPAttachment(SPListRow row, String url) {
        this.row = row;
        this.url = url;
        this.fileName = this.url.substring(this.url.lastIndexOf("/") + 1);
    }
    
    public byte[] download() throws IOException {
        return row.getList().getSharePointClient().getBytesFromUrl(url);
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getXmlFriendlyBytes() {
        return Base64.encodeBase64URLSafeString(bytes);
    }
    
}
