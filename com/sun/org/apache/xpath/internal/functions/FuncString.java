package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncString extends FunctionDef1Arg {
  static final long serialVersionUID = -2206677149497712883L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException { return (XString)getArg0AsString(paramXPathContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */