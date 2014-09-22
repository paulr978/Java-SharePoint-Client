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
public class SPFieldInformation {
    
    private String internalName = null;
    private String displayName = null;
    private String value = null;
    private String type = null;
    
    public SPFieldInformation(String internalName, String displayName, String value, String type) {
        setInternalName(internalName);
        setDisplayName(displayName);
        setValue(value);
        setType(type);
    }
    
    public SPFieldInformation(String internalName, String displayName, String value) {
        this();
        setInternalName(internalName);
        setDisplayName(displayName);
        setValue(value);
    }
    
    public SPFieldInformation() {
        type = "Text";
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
