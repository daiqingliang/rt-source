package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncTranslate extends Function3Args {
  static final long serialVersionUID = -1672834340026116482L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    String str1 = this.m_arg0.execute(paramXPathContext).str();
    String str2 = this.m_arg1.execute(paramXPathContext).str();
    String str3 = this.m_arg2.execute(paramXPathContext).str();
    int i = str1.length();
    int j = str3.length();
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < i; b++) {
      char c = str1.charAt(b);
      int k = str2.indexOf(c);
      if (k < 0) {
        stringBuffer.append(c);
      } else if (k < j) {
        stringBuffer.append(str3.charAt(k));
      } 
    } 
    return new XString(stringBuffer.toString());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncTranslate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */