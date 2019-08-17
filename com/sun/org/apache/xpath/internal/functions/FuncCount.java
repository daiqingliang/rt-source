package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncCount extends FunctionOneArg {
  static final long serialVersionUID = -7116225100474153751L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    DTMIterator dTMIterator = this.m_arg0.asIterator(paramXPathContext, paramXPathContext.getCurrentNode());
    int i = dTMIterator.getLength();
    dTMIterator.detach();
    return new XNumber(i);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncCount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */