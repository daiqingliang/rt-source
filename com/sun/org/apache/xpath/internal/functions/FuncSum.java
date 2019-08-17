package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncSum extends FunctionOneArg {
  static final long serialVersionUID = -2719049259574677519L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    DTMIterator dTMIterator = this.m_arg0.asIterator(paramXPathContext, paramXPathContext.getCurrentNode());
    double d = 0.0D;
    int i;
    while (-1 != (i = dTMIterator.nextNode())) {
      DTM dTM = dTMIterator.getDTM(i);
      XMLString xMLString = dTM.getStringValue(i);
      if (null != xMLString)
        d += xMLString.toDouble(); 
    } 
    dTMIterator.detach();
    return new XNumber(d);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncSum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */