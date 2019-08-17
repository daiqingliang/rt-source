package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncSubstring extends Function3Args {
  static final long serialVersionUID = -5996676095024715502L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    int j;
    XMLString xMLString2;
    XMLString xMLString1 = this.m_arg0.execute(paramXPathContext).xstr();
    double d = this.m_arg1.execute(paramXPathContext).num();
    int i = xMLString1.length();
    if (i <= 0)
      return XString.EMPTYSTRING; 
    if (Double.isNaN(d)) {
      d = -1000000.0D;
      j = 0;
    } else {
      d = Math.round(d);
      j = (d > 0.0D) ? ((int)d - 1) : 0;
    } 
    if (null != this.m_arg2) {
      double d1 = this.m_arg2.num(paramXPathContext);
      int k = (int)(Math.round(d1) + d) - 1;
      if (k < 0) {
        k = 0;
      } else if (k > i) {
        k = i;
      } 
      if (j > i)
        j = i; 
      xMLString2 = xMLString1.substring(j, k);
    } else {
      if (j > i)
        j = i; 
      xMLString2 = xMLString1.substring(j);
    } 
    return (XString)xMLString2;
  }
  
  public void checkNumberArgs(int paramInt) throws WrongNumberArgsException {
    if (paramInt < 2)
      reportWrongNumberArgs(); 
  }
  
  protected void reportWrongNumberArgs() { throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("ER_TWO_OR_THREE", null)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncSubstring.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */