package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Bool extends UnaryOperation {
  static final long serialVersionUID = 44705375321914635L;
  
  public XObject operate(XObject paramXObject) throws TransformerException { return (1 == paramXObject.getType()) ? paramXObject : (paramXObject.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE); }
  
  public boolean bool(XPathContext paramXPathContext) throws TransformerException { return this.m_right.bool(paramXPathContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\Bool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */