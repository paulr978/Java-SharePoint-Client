/*
 * Program : Java SharePoint Client
 * Author: Paul Rando (paulr978@gmail.com)
 * GIT: https://github.com/paulr978/Java-SharePoint-Client
 * 
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
    private SPUserService userService = null;
    private SPSite parentSite = null;
    private String title = null;
    private String url = null;
    private String origUrl = null;
    private String description = null;
    private String language = null;
    private String theme = null;

    public SPSite(String url, String user, String password, String userDomain, String hostDomain) throws IOException {
        this(url, new SPNTCreds(user, password, userDomain, hostDomain), false);
    }
    
    public SPSite(String url, String user, String password, String userDomain, String hostDomain, boolean debug) throws IOException {
        this(url, new SPNTCreds(user, password, userDomain, hostDomain), debug);
    }
    
    
    
    public SPSite(String url, SPNTCreds creds) throws IOException {
        this(url, creds, false);
    }

    public SPSite(String url, SPNTCreds creds, boolean debug) throws IOException {
        this(url, creds, debug, 0, 0);
    }
    
    public SPSite(String url, SPNTCreds creds, boolean debug, int commRetryCount, long commRetrySleepInterval) throws IOException {
        //Fix URL Spaces
        origUrl = url;
        url = url.replace(" ", "%20");
        
        this.client = new SharePointClient(url, creds, debug, commRetryCount, commRetrySleepInterval);
        this.client.setOrigUrl(origUrl);
        this.ntCreds = creds;

        subSites = new HashMap<String, SimpleSite>();
        listService = new SPListService(this);
        userService = new SPUserService(this);

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
    
    public SPUserService getUserService() {
        return userService;
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

}
