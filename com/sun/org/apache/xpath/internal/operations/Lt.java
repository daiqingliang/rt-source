package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Lt extends Operation {
  static final long serialVersionUID = 3388420509289359422L;
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2) throws TransformerException { return paramXObject1.lessThan(paramXObject2) ? XBoolean.S_TRUE : XBoolean.S_FALSE; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\Lt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */