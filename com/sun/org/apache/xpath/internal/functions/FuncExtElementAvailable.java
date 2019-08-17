package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncExtElementAvailable extends FunctionOneArg {
  static final long serialVersionUID = -472533699257968546L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    String str2;
    String str1;
    String str3 = this.m_arg0.execute(paramXPathContext).str();
    int i = str3.indexOf(':');
    if (i < 0) {
      String str = "";
      str1 = "http://www.w3.org/1999/XSL/Transform";
      str2 = str3;
    } else {
      String str = str3.substring(0, i);
      str1 = paramXPathContext.getNamespaceContext().getNamespaceForPrefix(str);
      if (null == str1)
        return XBoolean.S_FALSE; 
      str2 = str3.substring(i + 1);
    } 
    if (str1.equals("http://www.w3.org/1999/XSL/Transform") || str1.equals("http://xml.apache.org/xalan"))
      return XBoolean.S_FALSE; 
    ExtensionsProvider extensionsProvider = (ExtensionsProvider)paramXPathContext.getOwnerObject();
    return extensionsProvider.elementAvailable(str1, str2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncExtElementAvailable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */