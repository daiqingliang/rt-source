package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import jdk.xml.internal.JdkXmlFeatures;

public class XPathFactoryImpl extends XPathFactory {
  private static final String CLASS_NAME = "XPathFactoryImpl";
  
  private XPathFunctionResolver xPathFunctionResolver = null;
  
  private XPathVariableResolver xPathVariableResolver = null;
  
  private boolean _isNotSecureProcessing = true;
  
  private boolean _isSecureMode = false;
  
  private final JdkXmlFeatures _featureManager;
  
  public XPathFactoryImpl() {
    if (System.getSecurityManager() != null) {
      this._isSecureMode = true;
      this._isNotSecureProcessing = false;
    } 
    this._featureManager = new JdkXmlFeatures(!this._isNotSecureProcessing);
  }
  
  public boolean isObjectModelSupported(String paramString) {
    if (paramString == null) {
      String str = XSLMessages.createXPATHMessage("ER_OBJECT_MODEL_NULL", new Object[] { getClass().getName() });
      throw new NullPointerException(str);
    } 
    if (paramString.length() == 0) {
      String str = XSLMessages.createXPATHMessage("ER_OBJECT_MODEL_EMPTY", new Object[] { getClass().getName() });
      throw new IllegalArgumentException(str);
    } 
    return paramString.equals("http://java.sun.com/jaxp/xpath/dom");
  }
  
  public XPath newXPath() { return new XPathImpl(this.xPathVariableResolver, this.xPathFunctionResolver, !this._isNotSecureProcessing, this._featureManager); }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XPathFactoryConfigurationException {
    if (paramString == null) {
      String str1 = XSLMessages.createXPATHMessage("ER_FEATURE_NAME_NULL", new Object[] { "XPathFactoryImpl", new Boolean(paramBoolean) });
      throw new NullPointerException(str1);
    } 
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      if (this._isSecureMode && !paramBoolean) {
        String str1 = XSLMessages.createXPATHMessage("ER_SECUREPROCESSING_FEATURE", new Object[] { paramString, "XPathFactoryImpl", new Boolean(paramBoolean) });
        throw new XPathFactoryConfigurationException(str1);
      } 
      this._isNotSecureProcessing = !paramBoolean;
      if (paramBoolean && this._featureManager != null)
        this._featureManager.setFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION, JdkXmlFeatures.State.FSP, false); 
      return;
    } 
    if (paramString.equals("http://www.oracle.com/feature/use-service-mechanism") && this._isSecureMode)
      return; 
    if (this._featureManager != null && this._featureManager.setFeature(paramString, JdkXmlFeatures.State.APIPROPERTY, Boolean.valueOf(paramBoolean)))
      return; 
    String str = XSLMessages.createXPATHMessage("ER_FEATURE_UNKNOWN", new Object[] { paramString, "XPathFactoryImpl", Boolean.valueOf(paramBoolean) });
    throw new XPathFactoryConfigurationException(str);
  }
  
  public boolean getFeature(String paramString) {
    if (paramString == null) {
      String str1 = XSLMessages.createXPATHMessage("ER_GETTING_NULL_FEATURE", new Object[] { "XPathFactoryImpl" });
      throw new NullPointerException(str1);
    } 
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
      return !this._isNotSecureProcessing; 
    int i = this._featureManager.getIndex(paramString);
    if (i > -1)
      return this._featureManager.getFeature(i); 
    String str = XSLMessages.createXPATHMessage("ER_GETTING_UNKNOWN_FEATURE", new Object[] { paramString, "XPathFactoryImpl" });
    throw new XPathFactoryConfigurationException(str);
  }
  
  public void setXPathFunctionResolver(XPathFunctionResolver paramXPathFunctionResolver) {
    if (paramXPathFunctionResolver == null) {
      String str = XSLMessages.createXPATHMessage("ER_NULL_XPATH_FUNCTION_RESOLVER", new Object[] { "XPathFactoryImpl" });
      throw new NullPointerException(str);
    } 
    this.xPathFunctionResolver = paramXPathFunctionResolver;
  }
  
  public void setXPathVariableResolver(XPathVariableResolver paramXPathVariableResolver) {
    if (paramXPathVariableResolver == null) {
      String str = XSLMessages.createXPATHMessage("ER_NULL_XPATH_VARIABLE_RESOLVER", new Object[] { "XPathFactoryImpl" });
      throw new NullPointerException(str);
    } 
    this.xPathVariableResolver = paramXPathVariableResolver;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\XPathFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */