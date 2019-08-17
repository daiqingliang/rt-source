package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class ExsltBase {
  protected static String toString(Node paramNode) {
    if (paramNode instanceof DTMNodeProxy)
      return ((DTMNodeProxy)paramNode).getStringValue(); 
    String str = paramNode.getNodeValue();
    if (str == null) {
      NodeList nodeList = paramNode.getChildNodes();
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < nodeList.getLength(); b++) {
        Node node = nodeList.item(b);
        stringBuffer.append(toString(node));
      } 
      return stringBuffer.toString();
    } 
    return str;
  }
  
  protected static double toNumber(Node paramNode) {
    double d = 0.0D;
    String str = toString(paramNode);
    try {
      d = Double.valueOf(str).doubleValue();
    } catch (NumberFormatException numberFormatException) {
      d = NaND;
    } 
    return d;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */