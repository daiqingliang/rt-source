package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncUnparsedEntityURI extends FunctionOneArg {
  static final long serialVersionUID = 845309759097448178L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    String str1 = this.m_arg0.execute(paramXPathContext).str();
    int i = paramXPathContext.getCurrentNode();
    DTM dTM = paramXPathContext.getDTM(i);
    int j = dTM.getDocument();
    String str2 = dTM.getUnparsedEntityURI(str1);
    return new XString(str2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncUnparsedEntityURI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */