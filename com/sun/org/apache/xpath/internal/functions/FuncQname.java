package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncQname extends FunctionDef1Arg {
  static final long serialVersionUID = -1532307875532617380L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    XString xString;
    int i = getArg0AsNode(paramXPathContext);
    if (-1 != i) {
      DTM dTM = paramXPathContext.getDTM(i);
      String str = dTM.getNodeNameX(i);
      xString = (null == str) ? XString.EMPTYSTRING : new XString(str);
    } else {
      xString = XString.EMPTYSTRING;
    } 
    return xString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncQname.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */