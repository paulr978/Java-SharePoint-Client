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
public class SPNTCreds {
    
    private String user = null;
    private String password = null;
    private String userDomain = null;
    private String hostDomain = null;
    
    public SPNTCreds() {
    
    }
    
   public SPNTCreds(String user, String password) {
        this(user, password, null, null);
    }
    
    public SPNTCreds(String user, String password, String domain) {
        this(user, password, domain, domain);
    }
    
    public SPNTCreds(String user, String password, String userDomain, String hostDomain) {
        setUser(user);
        setPassword(password);
        setUserDomain(userDomain);
        setHostDomain(hostDomain);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserDomain() {
        return userDomain;
    }

    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    public String getHostDomain() {
        return hostDomain;
    }

    public void setHostDomain(String hostDomain) {
        this.hostDomain = hostDomain;
    }
    
}
