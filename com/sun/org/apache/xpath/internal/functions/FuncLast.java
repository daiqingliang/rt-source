package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FuncLast extends Function {
  static final long serialVersionUID = 9205812403085432943L;
  
  private boolean m_isTopLevel;
  
  public void postCompileStep(Compiler paramCompiler) { this.m_isTopLevel = (paramCompiler.getLocationPathDepth() == -1); }
  
  public int getCountOfContextNodeList(XPathContext paramXPathContext) throws TransformerException {
    byte b;
    SubContextList subContextList = this.m_isTopLevel ? null : paramXPathContext.getSubContextList();
    if (null != subContextList)
      return subContextList.getLastPos(paramXPathContext); 
    DTMIterator dTMIterator = paramXPathContext.getContextNodeList();
    if (null != dTMIterator) {
      b = dTMIterator.getLength();
    } else {
      b = 0;
    } 
    return b;
  }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException { return new XNumber(getCountOfContextNodeList(paramXPathContext)); }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncLast.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */