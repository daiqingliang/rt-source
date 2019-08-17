package com.sun.org.apache.xml.internal.security.utils;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JDKXPathAPI implements XPathAPI {
  private XPathFactory xpf;
  
  private String xpathStr;
  
  private XPathExpression xpathExpression;
  
  public NodeList selectNodeList(Node paramNode1, Node paramNode2, String paramString, Node paramNode3) throws TransformerException {
    if (!paramString.equals(this.xpathStr) || this.xpathExpression == null) {
      if (this.xpf == null) {
        this.xpf = XPathFactory.newInstance();
        try {
          this.xpf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
        } catch (XPathFactoryConfigurationException xPathFactoryConfigurationException) {
          throw new TransformerException("empty", xPathFactoryConfigurationException);
        } 
      } 
      XPath xPath = this.xpf.newXPath();
      xPath.setNamespaceContext(new DOMNamespaceContext(paramNode3));
      this.xpathStr = paramString;
      try {
        this.xpathExpression = xPath.compile(this.xpathStr);
      } catch (XPathExpressionException xPathExpressionException) {
        throw new TransformerException("empty", xPathExpressionException);
      } 
    } 
    try {
      return (NodeList)this.xpathExpression.evaluate(paramNode1, XPathConstants.NODESET);
    } catch (XPathExpressionException xPathExpressionException) {
      throw new TransformerException("empty", xPathExpressionException);
    } 
  }
  
  public boolean evaluate(Node paramNode1, Node paramNode2, String paramString, Node paramNode3) throws TransformerException {
    if (!paramString.equals(this.xpathStr) || this.xpathExpression == null) {
      if (this.xpf == null) {
        this.xpf = XPathFactory.newInstance();
        try {
          this.xpf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
        } catch (XPathFactoryConfigurationException xPathFactoryConfigurationException) {
          throw new TransformerException("empty", xPathFactoryConfigurationException);
        } 
      } 
      XPath xPath = this.xpf.newXPath();
      xPath.setNamespaceContext(new DOMNamespaceContext(paramNode3));
      this.xpathStr = paramString;
      try {
        this.xpathExpression = xPath.compile(this.xpathStr);
      } catch (XPathExpressionException xPathExpressionException) {
        throw new TransformerException("empty", xPathExpressionException);
      } 
    } 
    try {
      Boolean bool = (Boolean)this.xpathExpression.evaluate(paramNode1, XPathConstants.BOOLEAN);
      return bool.booleanValue();
    } catch (XPathExpressionException xPathExpressionException) {
      throw new TransformerException("empty", xPathExpressionException);
    } 
  }
  
  public void clear() {
    this.xpathStr = null;
    this.xpathExpression = null;
    this.xpf = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\JDKXPathAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */