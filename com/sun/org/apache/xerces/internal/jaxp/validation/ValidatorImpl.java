package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.validation.Validator;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

final class ValidatorImpl extends Validator implements PSVIProvider {
  private XMLSchemaValidatorComponentManager fComponentManager;
  
  private ValidatorHandlerImpl fSAXValidatorHelper;
  
  private DOMValidatorHelper fDOMValidatorHelper;
  
  private StreamValidatorHelper fStreamValidatorHelper;
  
  private StAXValidatorHelper fStaxValidatorHelper;
  
  private boolean fConfigurationChanged = false;
  
  private boolean fErrorHandlerChanged = false;
  
  private boolean fResourceResolverChanged = false;
  
  private static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";
  
  public ValidatorImpl(XSGrammarPoolContainer paramXSGrammarPoolContainer) {
    this.fComponentManager = new XMLSchemaValidatorComponentManager(paramXSGrammarPoolContainer);
    setErrorHandler(null);
    setResourceResolver(null);
  }
  
  public void validate(Source paramSource, Result paramResult) throws SAXException, IOException {
    if (paramSource instanceof javax.xml.transform.sax.SAXSource) {
      if (this.fSAXValidatorHelper == null)
        this.fSAXValidatorHelper = new ValidatorHandlerImpl(this.fComponentManager); 
      this.fSAXValidatorHelper.validate(paramSource, paramResult);
    } else if (paramSource instanceof javax.xml.transform.dom.DOMSource) {
      if (this.fDOMValidatorHelper == null)
        this.fDOMValidatorHelper = new DOMValidatorHelper(this.fComponentManager); 
      this.fDOMValidatorHelper.validate(paramSource, paramResult);
    } else if (paramSource instanceof javax.xml.transform.stream.StreamSource) {
      if (this.fStreamValidatorHelper == null)
        this.fStreamValidatorHelper = new StreamValidatorHelper(this.fComponentManager); 
      this.fStreamValidatorHelper.validate(paramSource, paramResult);
    } else if (paramSource instanceof javax.xml.transform.stax.StAXSource) {
      if (this.fStaxValidatorHelper == null)
        this.fStaxValidatorHelper = new StAXValidatorHelper(this.fComponentManager); 
      this.fStaxValidatorHelper.validate(paramSource, paramResult);
    } else {
      if (paramSource == null)
        throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceParameterNull", null)); 
      throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceNotAccepted", new Object[] { paramSource.getClass().getName() }));
    } 
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) {
    this.fErrorHandlerChanged = (paramErrorHandler != null);
    this.fComponentManager.setErrorHandler(paramErrorHandler);
  }
  
  public ErrorHandler getErrorHandler() { return this.fComponentManager.getErrorHandler(); }
  
  public void setResourceResolver(LSResourceResolver paramLSResourceResolver) {
    this.fResourceResolverChanged = (paramLSResourceResolver != null);
    this.fComponentManager.setResourceResolver(paramLSResourceResolver);
  }
  
  public LSResourceResolver getResourceResolver() { return this.fComponentManager.getResourceResolver(); }
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    try {
      return this.fComponentManager.getFeature(paramString);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str1 = xMLConfigurationException.getIdentifier();
      String str2 = (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED) ? "feature-not-recognized" : "feature-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    } 
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    try {
      this.fComponentManager.setFeature(paramString, paramBoolean);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str2;
      String str1 = xMLConfigurationException.getIdentifier();
      if (xMLConfigurationException.getType() == Status.NOT_ALLOWED)
        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "jaxp-secureprocessing-feature", null)); 
      if (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
        str2 = "feature-not-recognized";
      } else {
        str2 = "feature-not-supported";
      } 
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    } 
    this.fConfigurationChanged = true;
  }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    if ("http://apache.org/xml/properties/dom/current-element-node".equals(paramString))
      return (this.fDOMValidatorHelper != null) ? this.fDOMValidatorHelper.getCurrentElement() : null; 
    try {
      return this.fComponentManager.getProperty(paramString);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str1 = xMLConfigurationException.getIdentifier();
      String str2 = (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED) ? "property-not-recognized" : "property-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    } 
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    try {
      this.fComponentManager.setProperty(paramString, paramObject);
    } catch (XMLConfigurationException xMLConfigurationException) {
      String str1 = xMLConfigurationException.getIdentifier();
      String str2 = (xMLConfigurationException.getType() == Status.NOT_RECOGNIZED) ? "property-not-recognized" : "property-not-supported";
      throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), str2, new Object[] { str1 }));
    } 
    this.fConfigurationChanged = true;
  }
  
  public void reset() {
    if (this.fConfigurationChanged) {
      this.fComponentManager.restoreInitialState();
      setErrorHandler(null);
      setResourceResolver(null);
      this.fConfigurationChanged = false;
      this.fErrorHandlerChanged = false;
      this.fResourceResolverChanged = false;
    } else {
      if (this.fErrorHandlerChanged) {
        setErrorHandler(null);
        this.fErrorHandlerChanged = false;
      } 
      if (this.fResourceResolverChanged) {
        setResourceResolver(null);
        this.fResourceResolverChanged = false;
      } 
    } 
  }
  
  public ElementPSVI getElementPSVI() { return (this.fSAXValidatorHelper != null) ? this.fSAXValidatorHelper.getElementPSVI() : null; }
  
  public AttributePSVI getAttributePSVI(int paramInt) { return (this.fSAXValidatorHelper != null) ? this.fSAXValidatorHelper.getAttributePSVI(paramInt) : null; }
  
  public AttributePSVI getAttributePSVIByName(String paramString1, String paramString2) { return (this.fSAXValidatorHelper != null) ? this.fSAXValidatorHelper.getAttributePSVIByName(paramString1, paramString2) : null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\ValidatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */