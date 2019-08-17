package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncLang extends FunctionOneArg {
  static final long serialVersionUID = -7868705139354872185L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    String str = this.m_arg0.execute(paramXPathContext).str();
    int i = paramXPathContext.getCurrentNode();
    boolean bool = false;
    DTM dTM = paramXPathContext.getDTM(i);
    while (-1 != i) {
      if (1 == dTM.getNodeType(i)) {
        int j = dTM.getAttributeNode(i, "http://www.w3.org/XML/1998/namespace", "lang");
        if (-1 != j) {
          String str1 = dTM.getNodeValue(j);
          if (str1.toLowerCase().startsWith(str.toLowerCase())) {
            int k = str.length();
            if (str1.length() == k || str1.charAt(k) == '-')
              bool = true; 
          } 
          break;
        } 
      } 
      i = dTM.getParent(i);
    } 
    return bool ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncLang.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */