package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncContains extends Function2Args {
  static final long serialVersionUID = 5084753781887919723L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    String str1 = this.m_arg0.execute(paramXPathContext).str();
    String str2 = this.m_arg1.execute(paramXPathContext).str();
    if (str1.length() == 0 && str2.length() == 0)
      return XBoolean.S_TRUE; 
    int i = str1.indexOf(str2);
    return (i > -1) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncContains.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */