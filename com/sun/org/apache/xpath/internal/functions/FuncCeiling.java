package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncCeiling extends FunctionOneArg {
  static final long serialVersionUID = -1275988936390464739L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException { return new XNumber(Math.ceil(this.m_arg0.execute(paramXPathContext).num())); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncCeiling.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */