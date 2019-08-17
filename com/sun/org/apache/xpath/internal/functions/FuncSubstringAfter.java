package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncSubstringAfter extends Function2Args {
  static final long serialVersionUID = -8119731889862512194L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    XMLString xMLString1 = this.m_arg0.execute(paramXPathContext).xstr();
    XMLString xMLString2 = this.m_arg1.execute(paramXPathContext).xstr();
    int i = xMLString1.indexOf(xMLString2);
    return (-1 == i) ? XString.EMPTYSTRING : (XString)xMLString1.substring(i + xMLString2.length());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncSubstringAfter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */