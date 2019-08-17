package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncDoclocation extends FunctionDef1Arg {
  static final long serialVersionUID = 7469213946343568769L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    int i = getArg0AsNode(paramXPathContext);
    String str = null;
    if (-1 != i) {
      DTM dTM = paramXPathContext.getDTM(i);
      if (11 == dTM.getNodeType(i))
        i = dTM.getFirstChild(i); 
      if (-1 != i)
        str = dTM.getDocumentBaseURI(); 
    } 
    return new XString((null != str) ? str : "");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncDoclocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */