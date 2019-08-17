package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class SelfIteratorNoPredicate extends LocPathIterator {
  static final long serialVersionUID = -4226887905279814201L;
  
  SelfIteratorNoPredicate(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException { super(paramCompiler, paramInt1, paramInt2, false); }
  
  public SelfIteratorNoPredicate() throws TransformerException { super(null); }
  
  public int nextNode() {
    if (this.m_foundLast)
      return -1; 
    DTM dTM = this.m_cdtm;
    int i = (-1 == this.m_lastFetched) ? this.m_context : -1;
    this.m_lastFetched = i;
    if (-1 != i) {
      this.m_pos++;
      return i;
    } 
    this.m_foundLast = true;
    return -1;
  }
  
  public int asNode(XPathContext paramXPathContext) throws TransformerException { return paramXPathContext.getCurrentNode(); }
  
  public int getLastPos(XPathContext paramXPathContext) throws TransformerException { return 1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\SelfIteratorNoPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */