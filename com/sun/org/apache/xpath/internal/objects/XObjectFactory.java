package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.OneStepIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class XObjectFactory {
  public static XObject create(Object paramObject) {
    XObject xObject;
    if (paramObject instanceof XObject) {
      xObject = (XObject)paramObject;
    } else if (paramObject instanceof String) {
      xObject = new XString((String)paramObject);
    } else if (paramObject instanceof Boolean) {
      xObject = new XBoolean((Boolean)paramObject);
    } else if (paramObject instanceof Double) {
      xObject = new XNumber((Double)paramObject);
    } else {
      xObject = new XObject(paramObject);
    } 
    return xObject;
  }
  
  public static XObject create(Object paramObject, XPathContext paramXPathContext) {
    XObject xObject;
    if (paramObject instanceof XObject) {
      xObject = (XObject)paramObject;
    } else if (paramObject instanceof String) {
      xObject = new XString((String)paramObject);
    } else if (paramObject instanceof Boolean) {
      xObject = new XBoolean((Boolean)paramObject);
    } else if (paramObject instanceof Number) {
      xObject = new XNumber((Number)paramObject);
    } else if (paramObject instanceof DTM) {
      DTM dTM = (DTM)paramObject;
      try {
        int i = dTM.getDocument();
        DTMAxisIterator dTMAxisIterator = dTM.getAxisIterator(13);
        dTMAxisIterator.setStartNode(i);
        OneStepIterator oneStepIterator = new OneStepIterator(dTMAxisIterator, 13);
        oneStepIterator.setRoot(i, paramXPathContext);
        xObject = new XNodeSet(oneStepIterator);
      } catch (Exception exception) {
        throw new WrappedRuntimeException(exception);
      } 
    } else if (paramObject instanceof DTMAxisIterator) {
      DTMAxisIterator dTMAxisIterator = (DTMAxisIterator)paramObject;
      try {
        OneStepIterator oneStepIterator = new OneStepIterator(dTMAxisIterator, 13);
        oneStepIterator.setRoot(dTMAxisIterator.getStartNode(), paramXPathContext);
        xObject = new XNodeSet(oneStepIterator);
      } catch (Exception exception) {
        throw new WrappedRuntimeException(exception);
      } 
    } else if (paramObject instanceof DTMIterator) {
      xObject = new XNodeSet((DTMIterator)paramObject);
    } else if (paramObject instanceof Node) {
      xObject = new XNodeSetForDOM((Node)paramObject, paramXPathContext);
    } else if (paramObject instanceof NodeList) {
      xObject = new XNodeSetForDOM((NodeList)paramObject, paramXPathContext);
    } else if (paramObject instanceof NodeIterator) {
      xObject = new XNodeSetForDOM((NodeIterator)paramObject, paramXPathContext);
    } else {
      xObject = new XObject(paramObject);
    } 
    return xObject;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XObjectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */