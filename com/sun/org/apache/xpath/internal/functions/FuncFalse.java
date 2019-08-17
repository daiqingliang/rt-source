package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FuncFalse extends Function {
  static final long serialVersionUID = 6150918062759769887L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException { return XBoolean.S_FALSE; }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncFalse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */