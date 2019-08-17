package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Or extends Operation {
  static final long serialVersionUID = -644107191353853079L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    XObject xObject = this.m_left.execute(paramXPathContext);
    if (!xObject.bool()) {
      XObject xObject1 = this.m_right.execute(paramXPathContext);
      return xObject1.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    } 
    return XBoolean.S_TRUE;
  }
  
  public boolean bool(XPathContext paramXPathContext) throws TransformerException { return (this.m_left.bool(paramXPathContext) || this.m_right.bool(paramXPathContext)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\Or.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */