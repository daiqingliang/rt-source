package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {
  private Map<String, Object> attributes;
  
  private Map<String, Boolean> features;
  
  private Schema grammar;
  
  private boolean isXIncludeAware;
  
  private boolean fSecureProcess = true;
  
  public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
    if (this.grammar != null && this.attributes != null) {
      if (this.attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage"))
        throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "schema-already-specified", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage" })); 
      if (this.attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource"))
        throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "schema-already-specified", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaSource" })); 
    } 
    try {
      return new DocumentBuilderImpl(this, this.attributes, this.features, this.fSecureProcess);
    } catch (SAXException sAXException) {
      throw new ParserConfigurationException(sAXException.getMessage());
    } 
  }
  
  public void setAttribute(String paramString, Object paramObject) throws IllegalArgumentException {
    if (paramObject == null) {
      if (this.attributes != null)
        this.attributes.remove(paramString); 
      return;
    } 
    if (this.attributes == null)
      this.attributes = new HashMap(); 
    this.attributes.put(paramString, paramObject);
    try {
      new DocumentBuilderImpl(this, this.attributes, this.features);
    } catch (Exception exception) {
      this.attributes.remove(paramString);
      throw new IllegalArgumentException(exception.getMessage());
    } 
  }
  
  public Object getAttribute(String paramString) throws IllegalArgumentException {
    if (this.attributes != null) {
      Object object = this.attributes.get(paramString);
      if (object != null)
        return object; 
    } 
    DOMParser dOMParser = null;
    try {
      dOMParser = (new DocumentBuilderImpl(this, this.attributes, this.features)).getDOMParser();
      return dOMParser.getProperty(paramString);
    } catch (SAXException sAXException) {
      try {
        boolean bool = dOMParser.getFeature(paramString);
        return bool ? Boolean.TRUE : Boolean.FALSE;
      } catch (SAXException sAXException1) {
        throw new IllegalArgumentException(sAXException.getMessage());
      } 
    } 
  }
  
  public Schema getSchema() { return this.grammar; }
  
  public void setSchema(Schema paramSchema) { this.grammar = paramSchema; }
  
  public boolean isXIncludeAware() { return this.isXIncludeAware; }
  
  public void setXIncludeAware(boolean paramBoolean) { this.isXIncludeAware = paramBoolean; }
  
  public boolean getFeature(String paramString) throws ParserConfigurationException {
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
      return this.fSecureProcess; 
    if (this.features != null) {
      Boolean bool = (Boolean)this.features.get(paramString);
      if (bool != null)
        return bool.booleanValue(); 
    } 
    try {
      DOMParser dOMParser = (new DocumentBuilderImpl(this, this.attributes, this.features)).getDOMParser();
      return dOMParser.getFeature(paramString);
    } catch (SAXException sAXException) {
      throw new ParserConfigurationException(sAXException.getMessage());
    } 
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws ParserConfigurationException {
    if (this.features == null)
      this.features = new HashMap(); 
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      if (System.getSecurityManager() != null && !paramBoolean)
        throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null)); 
      this.fSecureProcess = paramBoolean;
      this.features.put(paramString, paramBoolean ? Boolean.TRUE : Boolean.FALSE);
      return;
    } 
    this.features.put(paramString, paramBoolean ? Boolean.TRUE : Boolean.FALSE);
    try {
      new DocumentBuilderImpl(this, this.attributes, this.features);
    } catch (SAXNotSupportedException sAXNotSupportedException) {
      this.features.remove(paramString);
      throw new ParserConfigurationException(sAXNotSupportedException.getMessage());
    } catch (SAXNotRecognizedException sAXNotRecognizedException) {
      this.features.remove(paramString);
      throw new ParserConfigurationException(sAXNotRecognizedException.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\DocumentBuilderFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */