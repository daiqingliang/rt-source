package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FuncPosition extends Function {
  static final long serialVersionUID = -9092846348197271582L;
  
  private boolean m_isTopLevel;
  
  public void postCompileStep(Compiler paramCompiler) { this.m_isTopLevel = (paramCompiler.getLocationPathDepth() == -1); }
  
  public int getPositionInContextNodeList(XPathContext paramXPathContext) {
    SubContextList subContextList = this.m_isTopLevel ? null : paramXPathContext.getSubContextList();
    if (null != subContextList)
      return subContextList.getProximityPosition(paramXPathContext); 
    DTMIterator dTMIterator = paramXPathContext.getContextNodeList();
    if (null != dTMIterator) {
      int i = dTMIterator.getCurrentNode();
      if (i == -1) {
        if (dTMIterator.getCurrentPos() == 0)
          return 0; 
        try {
          dTMIterator = dTMIterator.cloneWithReset();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
          throw new WrappedRuntimeException(cloneNotSupportedException);
        } 
        int j = paramXPathContext.getContextNode();
        do {
        
        } while (-1 != (i = dTMIterator.nextNode()) && i != j);
      } 
      return dTMIterator.getCurrentPos();
    } 
    return -1;
  }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    double d = getPositionInContextNodeList(paramXPathContext);
    return new XNumber(d);
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncPosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */