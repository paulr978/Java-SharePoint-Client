/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.pr.sharepoint.client;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author PRando
 */
public class SPField extends SPObject {
    
    private String id = null;
    private String type = null;
    private String displayName = null;
    private String name = null;
    private boolean required = false;
    private boolean hidden = false;
    private boolean readOnly = false;
    
    
    public SPField() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
