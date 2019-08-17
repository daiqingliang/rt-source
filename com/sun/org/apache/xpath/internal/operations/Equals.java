package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Equals extends Operation {
  static final long serialVersionUID = -2658315633903426134L;
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2) throws TransformerException { return paramXObject1.equals(paramXObject2) ? XBoolean.S_TRUE : XBoolean.S_FALSE; }
  
  public boolean bool(XPathContext paramXPathContext) throws TransformerException {
    XObject xObject1 = this.m_left.execute(paramXPathContext, true);
    XObject xObject2 = this.m_right.execute(paramXPathContext, true);
    boolean bool = xObject1.equals(xObject2);
    xObject1.detach();
    xObject2.detach();
    return bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\Equals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */