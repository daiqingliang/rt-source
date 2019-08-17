package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Mult extends Operation {
  static final long serialVersionUID = -4956770147013414675L;
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2) throws TransformerException { return new XNumber(paramXObject1.num() * paramXObject2.num()); }
  
  public double num(XPathContext paramXPathContext) throws TransformerException { return this.m_left.num(paramXPathContext) * this.m_right.num(paramXPathContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\Mult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */