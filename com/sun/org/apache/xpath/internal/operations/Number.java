package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Number extends UnaryOperation {
  static final long serialVersionUID = 7196954482871619765L;
  
  public XObject operate(XObject paramXObject) throws TransformerException { return (2 == paramXObject.getType()) ? paramXObject : new XNumber(paramXObject.num()); }
  
  public double num(XPathContext paramXPathContext) throws TransformerException { return this.m_right.num(paramXPathContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\Number.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */