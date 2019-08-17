package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.LocPathIterator;
import com.sun.org.apache.xpath.internal.axes.PredicatedNodeTest;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FuncCurrent extends Function {
  static final long serialVersionUID = 5715316804877715008L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    SubContextList subContextList = paramXPathContext.getCurrentNodeList();
    int i = -1;
    if (null != subContextList) {
      if (subContextList instanceof PredicatedNodeTest) {
        LocPathIterator locPathIterator = ((PredicatedNodeTest)subContextList).getLocPathIterator();
        i = locPathIterator.getCurrentContextNode();
      } else if (subContextList instanceof com.sun.org.apache.xpath.internal.patterns.StepPattern) {
        throw new RuntimeException(XSLMessages.createMessage("ER_PROCESSOR_ERROR", null));
      } 
    } else {
      i = paramXPathContext.getContextNode();
    } 
    return new XNodeSet(i, paramXPathContext.getDTMManager());
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncCurrent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */