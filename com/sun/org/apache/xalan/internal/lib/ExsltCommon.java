package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import com.sun.org.apache.xpath.internal.NodeSet;

public class ExsltCommon {
  public static String objectType(Object paramObject) {
    if (paramObject instanceof String)
      return "string"; 
    if (paramObject instanceof Boolean)
      return "boolean"; 
    if (paramObject instanceof Number)
      return "number"; 
    if (paramObject instanceof DTMNodeIterator) {
      DTMIterator dTMIterator = ((DTMNodeIterator)paramObject).getDTMIterator();
      return (dTMIterator instanceof com.sun.org.apache.xpath.internal.axes.RTFIterator) ? "RTF" : "node-set";
    } 
    return "unknown";
  }
  
  public static NodeSet nodeSet(ExpressionContext paramExpressionContext, Object paramObject) { return Extensions.nodeset(paramExpressionContext, paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltCommon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */