package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class ChildIterator extends LocPathIterator {
  static final long serialVersionUID = -6935428015142993583L;
  
  ChildIterator(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    super(paramCompiler, paramInt1, paramInt2, false);
    initNodeTest(-1);
  }
  
  public int asNode(XPathContext paramXPathContext) throws TransformerException {
    int i = paramXPathContext.getCurrentNode();
    DTM dTM = paramXPathContext.getDTM(i);
    return dTM.getFirstChild(i);
  }
  
  public int nextNode() {
    if (this.m_foundLast)
      return -1; 
    int i = (-1 == this.m_lastFetched) ? this.m_cdtm.getFirstChild(this.m_context) : this.m_cdtm.getNextSibling(this.m_lastFetched);
    this.m_lastFetched = i;
    if (-1 != i) {
      this.m_pos++;
      return i;
    } 
    this.m_foundLast = true;
    return -1;
  }
  
  public int getAxis() { return 3; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\ChildIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */