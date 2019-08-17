package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class SAXParserFactoryImpl extends SAXParserFactory {
  private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
  
  private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
  
  private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
  
  private Map<String, Boolean> features;
  
  private Schema grammar;
  
  private boolean isXIncludeAware;
  
  private boolean fSecureProcess = true;
  
  public SAXParser newSAXParser() throws ParserConfigurationException {
    SAXParserImpl sAXParserImpl;
    try {
      sAXParserImpl = new SAXParserImpl(this, this.features, this.fSecureProcess);
    } catch (SAXException sAXException) {
      throw new ParserConfigurationException(sAXException.getMessage());
    } 
    return sAXParserImpl;
  }
  
  private SAXParserImpl newSAXParserImpl() throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
    SAXParserImpl sAXParserImpl;
    try {
      sAXParserImpl = new SAXParserImpl(this, this.features);
    } catch (SAXNotSupportedException sAXNotSupportedException) {
      throw sAXNotSupportedException;
    } catch (SAXNotRecognizedException sAXNotRecognizedException) {
      throw sAXNotRecognizedException;
    } catch (SAXException sAXException) {
      throw new ParserConfigurationException(sAXException.getMessage());
    } 
    return sAXParserImpl;
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      if (System.getSecurityManager() != null && !paramBoolean)
        throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null)); 
      this.fSecureProcess = paramBoolean;
      putInFeatures(paramString, paramBoolean);
      return;
    } 
    putInFeatures(paramString, paramBoolean);
    try {
      newSAXParserImpl();
    } catch (SAXNotSupportedException sAXNotSupportedException) {
      this.features.remove(paramString);
      throw sAXNotSupportedException;
    } catch (SAXNotRecognizedException sAXNotRecognizedException) {
      this.features.remove(paramString);
      throw sAXNotRecognizedException;
    } 
  }
  
  public boolean getFeature(String paramString) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    return paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing") ? this.fSecureProcess : newSAXParserImpl().getXMLReader().getFeature(paramString);
  }
  
  public Schema getSchema() { return this.grammar; }
  
  public void setSchema(Schema paramSchema) { this.grammar = paramSchema; }
  
  public boolean isXIncludeAware() { return getFromFeatures("http://apache.org/xml/features/xinclude"); }
  
  public void setXIncludeAware(boolean paramBoolean) { putInFeatures("http://apache.org/xml/features/xinclude", paramBoolean); }
  
  public void setValidating(boolean paramBoolean) { putInFeatures("http://xml.org/sax/features/validation", paramBoolean); }
  
  public boolean isValidating() { return getFromFeatures("http://xml.org/sax/features/validation"); }
  
  private void putInFeatures(String paramString, boolean paramBoolean) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
    if (this.features == null)
      this.features = new HashMap(); 
    this.features.put(paramString, paramBoolean ? Boolean.TRUE : Boolean.FALSE);
  }
  
  private boolean getFromFeatures(String paramString) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
    if (this.features == null)
      return false; 
    Boolean bool = (Boolean)this.features.get(paramString);
    return (bool == null) ? false : bool.booleanValue();
  }
  
  public boolean isNamespaceAware() { return getFromFeatures("http://xml.org/sax/features/namespaces"); }
  
  public void setNamespaceAware(boolean paramBoolean) { putInFeatures("http://xml.org/sax/features/namespaces", paramBoolean); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\SAXParserFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */