package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import jdk.xml.internal.JdkXmlFeatures;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;

public class XPathExpressionImpl implements XPathExpression {
  private XPathFunctionResolver functionResolver;
  
  private XPathVariableResolver variableResolver;
  
  private JAXPPrefixResolver prefixResolver;
  
  private XPath xpath;
  
  private boolean featureSecureProcessing = false;
  
  boolean overrideDefaultParser;
  
  private final JdkXmlFeatures featureManager;
  
  static DocumentBuilderFactory dbf = null;
  
  static DocumentBuilder db = null;
  
  static Document d = null;
  
  protected XPathExpressionImpl() { this(null, null, null, null, false, new JdkXmlFeatures(false)); }
  
  protected XPathExpressionImpl(XPath paramXPath, JAXPPrefixResolver paramJAXPPrefixResolver, XPathFunctionResolver paramXPathFunctionResolver, XPathVariableResolver paramXPathVariableResolver) { this(paramXPath, paramJAXPPrefixResolver, paramXPathFunctionResolver, paramXPathVariableResolver, false, new JdkXmlFeatures(false)); }
  
  protected XPathExpressionImpl(XPath paramXPath, JAXPPrefixResolver paramJAXPPrefixResolver, XPathFunctionResolver paramXPathFunctionResolver, XPathVariableResolver paramXPathVariableResolver, boolean paramBoolean, JdkXmlFeatures paramJdkXmlFeatures) {
    this.xpath = paramXPath;
    this.prefixResolver = paramJAXPPrefixResolver;
    this.functionResolver = paramXPathFunctionResolver;
    this.variableResolver = paramXPathVariableResolver;
    this.featureSecureProcessing = paramBoolean;
    this.featureManager = paramJdkXmlFeatures;
    this.overrideDefaultParser = paramJdkXmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
  }
  
  public void setXPath(XPath paramXPath) { this.xpath = paramXPath; }
  
  public Object eval(Object paramObject, QName paramQName) throws TransformerException {
    XObject xObject = eval(paramObject);
    return getResultAsType(xObject, paramQName);
  }
  
  private XObject eval(Object paramObject) throws TransformerException {
    XPathContext xPathContext = null;
    if (this.functionResolver != null) {
      JAXPExtensionsProvider jAXPExtensionsProvider = new JAXPExtensionsProvider(this.functionResolver, this.featureSecureProcessing, this.featureManager);
      xPathContext = new XPathContext(jAXPExtensionsProvider);
    } else {
      xPathContext = new XPathContext();
    } 
    xPathContext.setVarStack(new JAXPVariableStack(this.variableResolver));
    XObject xObject = null;
    Node node = (Node)paramObject;
    if (node == null) {
      xObject = this.xpath.execute(xPathContext, -1, this.prefixResolver);
    } else {
      xObject = this.xpath.execute(xPathContext, node, this.prefixResolver);
    } 
    return xObject;
  }
  
  public Object evaluate(Object paramObject, QName paramQName) throws TransformerException {
    if (paramQName == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
      throw new NullPointerException(str);
    } 
    if (!isSupported(paramQName)) {
      String str = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { paramQName.toString() });
      throw new IllegalArgumentException(str);
    } 
    try {
      return eval(paramObject, paramQName);
    } catch (NullPointerException nullPointerException) {
      throw new XPathExpressionException(nullPointerException);
    } catch (TransformerException transformerException) {
      Throwable throwable = transformerException.getException();
      if (throwable instanceof XPathFunctionException)
        throw (XPathFunctionException)throwable; 
      throw new XPathExpressionException(transformerException);
    } 
  }
  
  public String evaluate(Object paramObject) throws XPathExpressionException { return (String)evaluate(paramObject, XPathConstants.STRING); }
  
  public Object evaluate(InputSource paramInputSource, QName paramQName) throws XPathExpressionException {
    if (paramInputSource == null || paramQName == null) {
      String str = XSLMessages.createXPATHMessage("ER_SOURCE_RETURN_TYPE_CANNOT_BE_NULL", null);
      throw new NullPointerException(str);
    } 
    if (!isSupported(paramQName)) {
      String str = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { paramQName.toString() });
      throw new IllegalArgumentException(str);
    } 
    try {
      if (dbf == null)
        dbf = JdkXmlUtils.getDOMFactory(this.overrideDefaultParser); 
      db = dbf.newDocumentBuilder();
      Document document = db.parse(paramInputSource);
      return eval(document, paramQName);
    } catch (Exception exception) {
      throw new XPathExpressionException(exception);
    } 
  }
  
  public String evaluate(InputSource paramInputSource) throws XPathExpressionException { return (String)evaluate(paramInputSource, XPathConstants.STRING); }
  
  private boolean isSupported(QName paramQName) { return (paramQName.equals(XPathConstants.STRING) || paramQName.equals(XPathConstants.NUMBER) || paramQName.equals(XPathConstants.BOOLEAN) || paramQName.equals(XPathConstants.NODE) || paramQName.equals(XPathConstants.NODESET)); }
  
  private Object getResultAsType(XObject paramXObject, QName paramQName) throws TransformerException {
    if (paramQName.equals(XPathConstants.STRING))
      return paramXObject.str(); 
    if (paramQName.equals(XPathConstants.NUMBER))
      return new Double(paramXObject.num()); 
    if (paramQName.equals(XPathConstants.BOOLEAN))
      return new Boolean(paramXObject.bool()); 
    if (paramQName.equals(XPathConstants.NODESET))
      return paramXObject.nodelist(); 
    if (paramQName.equals(XPathConstants.NODE)) {
      NodeIterator nodeIterator = paramXObject.nodeset();
      return nodeIterator.nextNode();
    } 
    String str = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { paramQName.toString() });
    throw new IllegalArgumentException(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\XPathExpressionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */