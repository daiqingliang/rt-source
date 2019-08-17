package javax.xml.xpath;

import javax.xml.namespace.QName;
import org.xml.sax.InputSource;

public interface XPathExpression {
  Object evaluate(Object paramObject, QName paramQName) throws XPathExpressionException;
  
  String evaluate(Object paramObject) throws XPathExpressionException;
  
  Object evaluate(InputSource paramInputSource, QName paramQName) throws XPathExpressionException;
  
  String evaluate(InputSource paramInputSource) throws XPathExpressionException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\xpath\XPathExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */