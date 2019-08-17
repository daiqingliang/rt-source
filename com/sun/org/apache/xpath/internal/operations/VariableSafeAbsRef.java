package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class VariableSafeAbsRef extends Variable {
  static final long serialVersionUID = -9174661990819967452L;
  
  public XObject execute(XPathContext paramXPathContext, boolean paramBoolean) throws TransformerException {
    XNodeSet xNodeSet = (XNodeSet)super.execute(paramXPathContext, paramBoolean);
    DTMManager dTMManager = paramXPathContext.getDTMManager();
    int i = paramXPathContext.getContextNode();
    if (dTMManager.getDTM(xNodeSet.getRoot()).getDocument() != dTMManager.getDTM(i).getDocument()) {
      Expression expression = (Expression)xNodeSet.getContainedIter();
      xNodeSet = (XNodeSet)expression.asIterator(paramXPathContext, i);
    } 
    return xNodeSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\VariableSafeAbsRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */