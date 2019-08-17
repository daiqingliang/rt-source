package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class And extends Operation {
  static final long serialVersionUID = 392330077126534022L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    XObject xObject = this.m_left.execute(paramXPathContext);
    if (xObject.bool()) {
      XObject xObject1 = this.m_right.execute(paramXPathContext);
      return xObject1.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    } 
    return XBoolean.S_FALSE;
  }
  
  public boolean bool(XPathContext paramXPathContext) throws TransformerException { return (this.m_left.bool(paramXPathContext) && this.m_right.bool(paramXPathContext)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\And.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */