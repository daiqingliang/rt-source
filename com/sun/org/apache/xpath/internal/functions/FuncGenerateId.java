package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncGenerateId extends FunctionDef1Arg {
  static final long serialVersionUID = 973544842091724273L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    int i = getArg0AsNode(paramXPathContext);
    return (-1 != i) ? new XString("N" + Integer.toHexString(i).toUpperCase()) : XString.EMPTYSTRING;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncGenerateId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */