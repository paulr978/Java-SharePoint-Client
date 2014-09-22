/*
 * Program : Java SharePoint Client
 * Author: Paul Rando (paulr978@gmail.com)
 * GIT: https://github.com/paulr978/Java-SharePoint-Client
 * 
 */
package my.pr.sharepoint.xml.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author PRando
 */
public abstract class BaseSharePointSoapHandler extends DefaultHandler {
    
    protected abstract void startDoc() throws SAXException;
    protected abstract void endDoc() throws SAXException;
    protected abstract void startElem(String uri, String localName, String qName, Attributes attributes) throws SAXException;
    protected abstract void endElem(String uri, String localName, String qName) throws SAXException;
    protected abstract void chars(char ch[], int start, int length) throws SAXException;
    
    private boolean error = false;
    private String errorCode = null;
    private String errorText = null;
    
    private boolean startErrorCodeTag = false;
    private boolean startErrorTextTag = false;
    
    public BaseSharePointSoapHandler() {
    
    }
    
    public void startDocument() throws SAXException {
        //System.out.println("start document   : ");
        startDoc();
    }

    public void endDocument() throws SAXException {
        //System.out.println("end document     : ");
        endDoc();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //System.out.println("start element    : " + qName);
        
        if(qName.equalsIgnoreCase("ErrorCode")) {
            startErrorCodeTag = true;
        }
        
        if(qName.equalsIgnoreCase("ErrorText")) {
            startErrorTextTag = true;
        }
        
        if(qName.equalsIgnoreCase("errorstring")) {
            startErrorTextTag = true;
        }
        
        startElem(uri, localName, qName, attributes);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        //System.out.println("end element      : " + qName);
        
        endElem(uri, localName, qName);
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        //System.out.println("start characters : " + new String(ch, start, length));
        
        if(startErrorCodeTag) {
            errorCode = new String(ch, start, length);
            if(!errorCode.equalsIgnoreCase("0x00000000")) error = true;
            
            startErrorCodeTag = false;
        }
        
        if(startErrorTextTag) {
            errorText = new String(ch, start, length);
            startErrorTextTag = false;
        }
        
        if(startErrorTextTag) {
            errorText = new String(ch, start, length);
            startErrorTextTag = false;
        }
        
        chars(ch, start, length);
    }
    
    public boolean isError() {
        return error;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getErrorText() {
        return errorText;
    }
    
}
