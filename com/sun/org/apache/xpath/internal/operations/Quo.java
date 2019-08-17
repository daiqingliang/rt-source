package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Quo extends Operation {
  static final long serialVersionUID = 693765299196169905L;
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2) throws TransformerException { return new XNumber((int)(paramXObject1.num() / paramXObject2.num())); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\Quo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */