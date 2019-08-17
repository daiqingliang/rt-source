package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncRound extends FunctionOneArg {
  static final long serialVersionUID = -7970583902573826611L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    XObject xObject = this.m_arg0.execute(paramXPathContext);
    double d = xObject.num();
    return (d >= -0.5D && d < 0.0D) ? new XNumber(-0.0D) : ((d == 0.0D) ? new XNumber(d) : new XNumber(Math.floor(d + 0.5D)));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncRound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */