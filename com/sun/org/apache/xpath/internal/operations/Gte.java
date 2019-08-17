package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Gte extends Operation {
  static final long serialVersionUID = 9142945909906680220L;
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2) throws TransformerException { return paramXObject1.greaterThanOrEqual(paramXObject2) ? XBoolean.S_TRUE : XBoolean.S_FALSE; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\Gte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */