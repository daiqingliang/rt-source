package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncNamespace extends FunctionDef1Arg {
  static final long serialVersionUID = -4695674566722321237L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    String str;
    int i = getArg0AsNode(paramXPathContext);
    if (i != -1) {
      DTM dTM = paramXPathContext.getDTM(i);
      short s = dTM.getNodeType(i);
      if (s == 1) {
        str = dTM.getNamespaceURI(i);
      } else if (s == 2) {
        str = dTM.getNodeName(i);
        if (str.startsWith("xmlns:") || str.equals("xmlns"))
          return XString.EMPTYSTRING; 
        str = dTM.getNamespaceURI(i);
      } else {
        return XString.EMPTYSTRING;
      } 
    } else {
      return XString.EMPTYSTRING;
    } 
    return (null == str) ? XString.EMPTYSTRING : new XString(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncNamespace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */