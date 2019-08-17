package javax.xml.xpath;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.xml.sax.InputSource;

public interface XPath {
  void reset();
  
  void setXPathVariableResolver(XPathVariableResolver paramXPathVariableResolver);
  
  XPathVariableResolver getXPathVariableResolver();
  
  void setXPathFunctionResolver(XPathFunctionResolver paramXPathFunctionResolver);
  
  XPathFunctionResolver getXPathFunctionResolver();
  
  void setNamespaceContext(NamespaceContext paramNamespaceContext);
  
  NamespaceContext getNamespaceContext();
  
  XPathExpression compile(String paramString) throws XPathExpressionException;
  
  Object evaluate(String paramString, Object paramObject, QName paramQName) throws XPathExpressionException;
  
  String evaluate(String paramString, Object paramObject) throws XPathExpressionException;
  
  Object evaluate(String paramString, InputSource paramInputSource, QName paramQName) throws XPathExpressionException;
  
  String evaluate(String paramString, InputSource paramInputSource) throws XPathExpressionException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\xpath\XPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */