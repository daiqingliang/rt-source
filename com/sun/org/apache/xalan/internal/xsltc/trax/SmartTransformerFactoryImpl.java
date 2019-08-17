package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.XMLFilter;

public class SmartTransformerFactoryImpl extends SAXTransformerFactory {
  private static final String CLASS_NAME = "SmartTransformerFactoryImpl";
  
  private SAXTransformerFactory _xsltcFactory = null;
  
  private SAXTransformerFactory _xalanFactory = null;
  
  private SAXTransformerFactory _currFactory = null;
  
  private ErrorListener _errorlistener = null;
  
  private URIResolver _uriresolver = null;
  
  private boolean featureSecureProcessing = false;
  
  private void createXSLTCTransformerFactory() {
    this._xsltcFactory = new TransformerFactoryImpl();
    this._currFactory = this._xsltcFactory;
  }
  
  private void createXalanTransformerFactory() {
    String str = "com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.";
    try {
      Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl", true);
      this._xalanFactory = (SAXTransformerFactory)clazz.newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      System.err.println("com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.");
    } catch (InstantiationException instantiationException) {
      System.err.println("com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.");
    } catch (IllegalAccessException illegalAccessException) {
      System.err.println("com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.");
    } 
    this._currFactory = this._xalanFactory;
  }
  
  public void setErrorListener(ErrorListener paramErrorListener) throws IllegalArgumentException { this._errorlistener = paramErrorListener; }
  
  public ErrorListener getErrorListener() { return this._errorlistener; }
  
  public Object getAttribute(String paramString) throws IllegalArgumentException {
    if (paramString.equals("translet-name") || paramString.equals("debug")) {
      if (this._xsltcFactory == null)
        createXSLTCTransformerFactory(); 
      return this._xsltcFactory.getAttribute(paramString);
    } 
    if (this._xalanFactory == null)
      createXalanTransformerFactory(); 
    return this._xalanFactory.getAttribute(paramString);
  }
  
  public void setAttribute(String paramString, Object paramObject) throws IllegalArgumentException {
    if (paramString.equals("translet-name") || paramString.equals("debug")) {
      if (this._xsltcFactory == null)
        createXSLTCTransformerFactory(); 
      this._xsltcFactory.setAttribute(paramString, paramObject);
    } else {
      if (this._xalanFactory == null)
        createXalanTransformerFactory(); 
      this._xalanFactory.setAttribute(paramString, paramObject);
    } 
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws TransformerConfigurationException {
    if (paramString == null) {
      ErrorMsg errorMsg1 = new ErrorMsg("JAXP_SET_FEATURE_NULL_NAME");
      throw new NullPointerException(errorMsg1.toString());
    } 
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      this.featureSecureProcessing = paramBoolean;
      return;
    } 
    ErrorMsg errorMsg = new ErrorMsg("JAXP_UNSUPPORTED_FEATURE", paramString);
    throw new TransformerConfigurationException(errorMsg.toString());
  }
  
  public boolean getFeature(String paramString) {
    String[] arrayOfString = { "http://javax.xml.transform.dom.DOMSource/feature", "http://javax.xml.transform.dom.DOMResult/feature", "http://javax.xml.transform.sax.SAXSource/feature", "http://javax.xml.transform.sax.SAXResult/feature", "http://javax.xml.transform.stream.StreamSource/feature", "http://javax.xml.transform.stream.StreamResult/feature" };
    if (paramString == null) {
      ErrorMsg errorMsg = new ErrorMsg("JAXP_GET_FEATURE_NULL_NAME");
      throw new NullPointerException(errorMsg.toString());
    } 
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (paramString.equals(arrayOfString[b]))
        return true; 
    } 
    return paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing") ? this.featureSecureProcessing : 0;
  }
  
  public URIResolver getURIResolver() { return this._uriresolver; }
  
  public void setURIResolver(URIResolver paramURIResolver) { this._uriresolver = paramURIResolver; }
  
  public Source getAssociatedStylesheet(Source paramSource, String paramString1, String paramString2, String paramString3) throws TransformerConfigurationException {
    if (this._currFactory == null)
      createXSLTCTransformerFactory(); 
    return this._currFactory.getAssociatedStylesheet(paramSource, paramString1, paramString2, paramString3);
  }
  
  public Transformer newTransformer() throws TransformerConfigurationException {
    if (this._xalanFactory == null)
      createXalanTransformerFactory(); 
    if (this._errorlistener != null)
      this._xalanFactory.setErrorListener(this._errorlistener); 
    if (this._uriresolver != null)
      this._xalanFactory.setURIResolver(this._uriresolver); 
    this._currFactory = this._xalanFactory;
    return this._currFactory.newTransformer();
  }
  
  public Transformer newTransformer(Source paramSource) throws TransformerConfigurationException {
    if (this._xalanFactory == null)
      createXalanTransformerFactory(); 
    if (this._errorlistener != null)
      this._xalanFactory.setErrorListener(this._errorlistener); 
    if (this._uriresolver != null)
      this._xalanFactory.setURIResolver(this._uriresolver); 
    this._currFactory = this._xalanFactory;
    return this._currFactory.newTransformer(paramSource);
  }
  
  public Templates newTemplates(Source paramSource) throws TransformerConfigurationException {
    if (this._xsltcFactory == null)
      createXSLTCTransformerFactory(); 
    if (this._errorlistener != null)
      this._xsltcFactory.setErrorListener(this._errorlistener); 
    if (this._uriresolver != null)
      this._xsltcFactory.setURIResolver(this._uriresolver); 
    this._currFactory = this._xsltcFactory;
    return this._currFactory.newTemplates(paramSource);
  }
  
  public TemplatesHandler newTemplatesHandler() throws TransformerConfigurationException {
    if (this._xsltcFactory == null)
      createXSLTCTransformerFactory(); 
    if (this._errorlistener != null)
      this._xsltcFactory.setErrorListener(this._errorlistener); 
    if (this._uriresolver != null)
      this._xsltcFactory.setURIResolver(this._uriresolver); 
    return this._xsltcFactory.newTemplatesHandler();
  }
  
  public TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
    if (this._xalanFactory == null)
      createXalanTransformerFactory(); 
    if (this._errorlistener != null)
      this._xalanFactory.setErrorListener(this._errorlistener); 
    if (this._uriresolver != null)
      this._xalanFactory.setURIResolver(this._uriresolver); 
    return this._xalanFactory.newTransformerHandler();
  }
  
  public TransformerHandler newTransformerHandler(Source paramSource) throws TransformerConfigurationException {
    if (this._xalanFactory == null)
      createXalanTransformerFactory(); 
    if (this._errorlistener != null)
      this._xalanFactory.setErrorListener(this._errorlistener); 
    if (this._uriresolver != null)
      this._xalanFactory.setURIResolver(this._uriresolver); 
    return this._xalanFactory.newTransformerHandler(paramSource);
  }
  
  public TransformerHandler newTransformerHandler(Templates paramTemplates) throws TransformerConfigurationException {
    if (this._xsltcFactory == null)
      createXSLTCTransformerFactory(); 
    if (this._errorlistener != null)
      this._xsltcFactory.setErrorListener(this._errorlistener); 
    if (this._uriresolver != null)
      this._xsltcFactory.setURIResolver(this._uriresolver); 
    return this._xsltcFactory.newTransformerHandler(paramTemplates);
  }
  
  public XMLFilter newXMLFilter(Source paramSource) throws TransformerConfigurationException {
    if (this._xsltcFactory == null)
      createXSLTCTransformerFactory(); 
    if (this._errorlistener != null)
      this._xsltcFactory.setErrorListener(this._errorlistener); 
    if (this._uriresolver != null)
      this._xsltcFactory.setURIResolver(this._uriresolver); 
    Templates templates = this._xsltcFactory.newTemplates(paramSource);
    return (templates == null) ? null : newXMLFilter(templates);
  }
  
  public XMLFilter newXMLFilter(Templates paramTemplates) throws TransformerConfigurationException {
    try {
      return new TrAXFilter(paramTemplates);
    } catch (TransformerConfigurationException transformerConfigurationException) {
      if (this._xsltcFactory == null)
        createXSLTCTransformerFactory(); 
      ErrorListener errorListener = this._xsltcFactory.getErrorListener();
      if (errorListener != null)
        try {
          errorListener.fatalError(transformerConfigurationException);
          return null;
        } catch (TransformerException transformerException) {
          new TransformerConfigurationException(transformerException);
        }  
      throw transformerConfigurationException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\SmartTransformerFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */