/*
 * Program : Java SharePoint Client
 * Author: Paul Rando (paulr978@gmail.com)
 * GIT: https://github.com/paulr978/Java-SharePoint-Client
 * 
 */

package my.pr.sharepoint.client;

import java.io.File;
import java.io.FileOutputStream;
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
    private boolean downloaded = false;
    
    public SPAttachment(String fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }
    
    public SPAttachment(SPListRow row, String url) {
        this.row = row;
        this.url = url;
        this.fileName = this.url.substring(this.url.lastIndexOf("/") + 1);
    }
    
    public String getURL() {
        return url;
    }
    
    public File getFile(String name) throws IOException {
        if(!isDownloaded()) download();
        
        File file = new File(name);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
        return file;
    }
    
    public byte[] download() throws IOException {
        bytes = row.getList().getSharePointClient().getBytesFromUrl(url);
        downloaded = true;
        return bytes;
    }
    
    public boolean isDownloaded() {
        return downloaded;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getXmlFriendlyBytes() {
        return Base64.encodeBase64String(bytes);
    }
    
}
