package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.FunctionTable;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncExtFunctionAvailable extends FunctionOneArg {
  static final long serialVersionUID = 5118814314918592241L;
  
  private FunctionTable m_functionTable = null;
  
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
    if (str1.equals("http://www.w3.org/1999/XSL/Transform"))
      try {
        if (null == this.m_functionTable)
          this.m_functionTable = new FunctionTable(); 
        return this.m_functionTable.functionAvailable(str2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
      } catch (Exception exception) {
        return XBoolean.S_FALSE;
      }  
    ExtensionsProvider extensionsProvider = (ExtensionsProvider)paramXPathContext.getOwnerObject();
    return extensionsProvider.functionAvailable(str1, str2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
  
  public void setFunctionTable(FunctionTable paramFunctionTable) { this.m_functionTable = paramFunctionTable; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncExtFunctionAvailable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */