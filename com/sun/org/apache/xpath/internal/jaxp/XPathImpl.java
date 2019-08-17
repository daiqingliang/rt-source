package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.IOException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
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
import org.xml.sax.SAXException;

public class XPathImpl implements XPath {
  private XPathVariableResolver variableResolver;
  
  private XPathFunctionResolver functionResolver;
  
  private XPathVariableResolver origVariableResolver;
  
  private XPathFunctionResolver origFunctionResolver;
  
  private NamespaceContext namespaceContext = null;
  
  private JAXPPrefixResolver prefixResolver;
  
  private boolean featureSecureProcessing = false;
  
  private boolean overrideDefaultParser = true;
  
  private final JdkXmlFeatures featureManager;
  
  private static Document d = null;
  
  XPathImpl(XPathVariableResolver paramXPathVariableResolver, XPathFunctionResolver paramXPathFunctionResolver) { this(paramXPathVariableResolver, paramXPathFunctionResolver, false, new JdkXmlFeatures(false)); }
  
  XPathImpl(XPathVariableResolver paramXPathVariableResolver, XPathFunctionResolver paramXPathFunctionResolver, boolean paramBoolean, JdkXmlFeatures paramJdkXmlFeatures) {
    this.origVariableResolver = this.variableResolver = paramXPathVariableResolver;
    this.origFunctionResolver = this.functionResolver = paramXPathFunctionResolver;
    this.featureSecureProcessing = paramBoolean;
    this.featureManager = paramJdkXmlFeatures;
    this.overrideDefaultParser = paramJdkXmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
  }
  
  public void setXPathVariableResolver(XPathVariableResolver paramXPathVariableResolver) {
    if (paramXPathVariableResolver == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPathVariableResolver" });
      throw new NullPointerException(str);
    } 
    this.variableResolver = paramXPathVariableResolver;
  }
  
  public XPathVariableResolver getXPathVariableResolver() { return this.variableResolver; }
  
  public void setXPathFunctionResolver(XPathFunctionResolver paramXPathFunctionResolver) {
    if (paramXPathFunctionResolver == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPathFunctionResolver" });
      throw new NullPointerException(str);
    } 
    this.functionResolver = paramXPathFunctionResolver;
  }
  
  public XPathFunctionResolver getXPathFunctionResolver() { return this.functionResolver; }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) {
    if (paramNamespaceContext == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "NamespaceContext" });
      throw new NullPointerException(str);
    } 
    this.namespaceContext = paramNamespaceContext;
    this.prefixResolver = new JAXPPrefixResolver(paramNamespaceContext);
  }
  
  public NamespaceContext getNamespaceContext() { return this.namespaceContext; }
  
  private DocumentBuilder getParser() {
    try {
      DocumentBuilderFactory documentBuilderFactory = JdkXmlUtils.getDOMFactory(this.overrideDefaultParser);
      return documentBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException parserConfigurationException) {
      throw new Error(parserConfigurationException);
    } 
  }
  
  private XObject eval(String paramString, Object paramObject) throws TransformerException {
    XPath xPath = new XPath(paramString, null, this.prefixResolver, 0);
    XPathContext xPathContext = null;
    if (this.functionResolver != null) {
      JAXPExtensionsProvider jAXPExtensionsProvider = new JAXPExtensionsProvider(this.functionResolver, this.featureSecureProcessing, this.featureManager);
      xPathContext = new XPathContext(jAXPExtensionsProvider);
    } else {
      xPathContext = new XPathContext();
    } 
    XObject xObject = null;
    xPathContext.setVarStack(new JAXPVariableStack(this.variableResolver));
    if (paramObject instanceof Node) {
      xObject = xPath.execute(xPathContext, (Node)paramObject, this.prefixResolver);
    } else {
      xObject = xPath.execute(xPathContext, -1, this.prefixResolver);
    } 
    return xObject;
  }
  
  public Object evaluate(String paramString, Object paramObject, QName paramQName) throws XPathExpressionException {
    if (paramString == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
      throw new NullPointerException(str);
    } 
    if (paramQName == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
      throw new NullPointerException(str);
    } 
    if (!isSupported(paramQName)) {
      String str = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { paramQName.toString() });
      throw new IllegalArgumentException(str);
    } 
    try {
      XObject xObject = eval(paramString, paramObject);
      return getResultAsType(xObject, paramQName);
    } catch (NullPointerException nullPointerException) {
      throw new XPathExpressionException(nullPointerException);
    } catch (TransformerException transformerException) {
      Throwable throwable = transformerException.getException();
      if (throwable instanceof XPathFunctionException)
        throw (XPathFunctionException)throwable; 
      throw new XPathExpressionException(transformerException);
    } 
  }
  
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
  
  public String evaluate(String paramString, Object paramObject) throws XPathExpressionException { return (String)evaluate(paramString, paramObject, XPathConstants.STRING); }
  
  public XPathExpression compile(String paramString) throws XPathExpressionException {
    if (paramString == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
      throw new NullPointerException(str);
    } 
    try {
      XPath xPath = new XPath(paramString, null, this.prefixResolver, 0);
      return new XPathExpressionImpl(xPath, this.prefixResolver, this.functionResolver, this.variableResolver, this.featureSecureProcessing, this.featureManager);
    } catch (TransformerException transformerException) {
      throw new XPathExpressionException(transformerException);
    } 
  }
  
  public Object evaluate(String paramString, InputSource paramInputSource, QName paramQName) throws XPathExpressionException {
    if (paramInputSource == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "source" });
      throw new NullPointerException(str);
    } 
    if (paramString == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
      throw new NullPointerException(str);
    } 
    if (paramQName == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
      throw new NullPointerException(str);
    } 
    if (!isSupported(paramQName)) {
      String str = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { paramQName.toString() });
      throw new IllegalArgumentException(str);
    } 
    try {
      Document document = getParser().parse(paramInputSource);
      XObject xObject = eval(paramString, document);
      return getResultAsType(xObject, paramQName);
    } catch (SAXException sAXException) {
      throw new XPathExpressionException(sAXException);
    } catch (IOException iOException) {
      throw new XPathExpressionException(iOException);
    } catch (TransformerException transformerException) {
      Throwable throwable = transformerException.getException();
      if (throwable instanceof XPathFunctionException)
        throw (XPathFunctionException)throwable; 
      throw new XPathExpressionException(transformerException);
    } 
  }
  
  public String evaluate(String paramString, InputSource paramInputSource) throws XPathExpressionException { return (String)evaluate(paramString, paramInputSource, XPathConstants.STRING); }
  
  public void reset() {
    this.variableResolver = this.origVariableResolver;
    this.functionResolver = this.origFunctionResolver;
    this.namespaceContext = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\XPathImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */