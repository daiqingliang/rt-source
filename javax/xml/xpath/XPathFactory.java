package javax.xml.xpath;

public abstract class XPathFactory {
  public static final String DEFAULT_PROPERTY_NAME = "javax.xml.xpath.XPathFactory";
  
  public static final String DEFAULT_OBJECT_MODEL_URI = "http://java.sun.com/jaxp/xpath/dom";
  
  private static SecuritySupport ss = new SecuritySupport();
  
  public static XPathFactory newInstance() {
    try {
      return newInstance("http://java.sun.com/jaxp/xpath/dom");
    } catch (XPathFactoryConfigurationException xPathFactoryConfigurationException) {
      throw new RuntimeException("XPathFactory#newInstance() failed to create an XPathFactory for the default object model: http://java.sun.com/jaxp/xpath/dom with the XPathFactoryConfigurationException: " + xPathFactoryConfigurationException.toString());
    } 
  }
  
  public static XPathFactory newInstance(String paramString) throws XPathFactoryConfigurationException {
    if (paramString == null)
      throw new NullPointerException("XPathFactory#newInstance(String uri) cannot be called with uri == null"); 
    if (paramString.length() == 0)
      throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\""); 
    ClassLoader classLoader = ss.getContextClassLoader();
    if (classLoader == null)
      classLoader = XPathFactory.class.getClassLoader(); 
    XPathFactory xPathFactory = (new XPathFactoryFinder(classLoader)).newFactory(paramString);
    if (xPathFactory == null)
      throw new XPathFactoryConfigurationException("No XPathFactory implementation found for the object model: " + paramString); 
    return xPathFactory;
  }
  
  public static XPathFactory newInstance(String paramString1, String paramString2, ClassLoader paramClassLoader) throws XPathFactoryConfigurationException {
    ClassLoader classLoader = paramClassLoader;
    if (paramString1 == null)
      throw new NullPointerException("XPathFactory#newInstance(String uri) cannot be called with uri == null"); 
    if (paramString1.length() == 0)
      throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\""); 
    if (classLoader == null)
      classLoader = ss.getContextClassLoader(); 
    XPathFactory xPathFactory = (new XPathFactoryFinder(classLoader)).createInstance(paramString2);
    if (xPathFactory == null)
      throw new XPathFactoryConfigurationException("No XPathFactory implementation found for the object model: " + paramString1); 
    if (xPathFactory.isObjectModelSupported(paramString1))
      return xPathFactory; 
    throw new XPathFactoryConfigurationException("Factory " + paramString2 + " doesn't support given " + paramString1 + " object model");
  }
  
  public abstract boolean isObjectModelSupported(String paramString);
  
  public abstract void setFeature(String paramString, boolean paramBoolean) throws XPathFactoryConfigurationException;
  
  public abstract boolean getFeature(String paramString);
  
  public abstract void setXPathVariableResolver(XPathVariableResolver paramXPathVariableResolver);
  
  public abstract void setXPathFunctionResolver(XPathFunctionResolver paramXPathFunctionResolver);
  
  public abstract XPath newXPath();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\xpath\XPathFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */