/*
 * Program : Java SharePoint Client
 * Author: Paul Rando (paulr978@gmail.com)
 * GIT: https://github.com/paulr978/Java-SharePoint-Client
 * 
 */
package my.pr.sharepoint.client;

/**
 *
 * @author PRando
 */
public class SPException extends Exception {
    
    public SPException(String msg) {
        super(msg);
    }
    
    public SPException(String msg, Throwable t) {
        super(msg, t);
    }
    
    public SPException(String msg, Exception e) {
        super(msg, e);
    }
}
