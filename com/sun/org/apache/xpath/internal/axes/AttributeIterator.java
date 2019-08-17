package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class AttributeIterator extends ChildTestIterator {
  static final long serialVersionUID = -8417986700712229686L;
  
  AttributeIterator(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException { super(paramCompiler, paramInt1, paramInt2); }
  
  protected int getNextNode() {
    this.m_lastFetched = (-1 == this.m_lastFetched) ? this.m_cdtm.getFirstAttribute(this.m_context) : this.m_cdtm.getNextAttribute(this.m_lastFetched);
    return this.m_lastFetched;
  }
  
  public int getAxis() { return 2; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\AttributeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */