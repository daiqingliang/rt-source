package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncLocalPart extends FunctionDef1Arg {
  static final long serialVersionUID = 7591798770325814746L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    int i = getArg0AsNode(paramXPathContext);
    if (-1 == i)
      return XString.EMPTYSTRING; 
    DTM dTM = paramXPathContext.getDTM(i);
    String str = (i != -1) ? dTM.getLocalName(i) : "";
    return (str.startsWith("#") || str.equals("xmlns")) ? XString.EMPTYSTRING : new XString(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncLocalPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */