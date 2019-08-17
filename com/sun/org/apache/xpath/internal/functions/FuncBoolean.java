package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncBoolean extends FunctionOneArg {
  static final long serialVersionUID = 4328660760070034592L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException { return this.m_arg0.execute(paramXPathContext).bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */