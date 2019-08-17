package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class ChildTestIterator extends BasicTestIterator {
  static final long serialVersionUID = -7936835957960705722L;
  
  protected DTMAxisTraverser m_traverser;
  
  ChildTestIterator(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException { super(paramCompiler, paramInt1, paramInt2); }
  
  public ChildTestIterator(DTMAxisTraverser paramDTMAxisTraverser) {
    super(null);
    this.m_traverser = paramDTMAxisTraverser;
  }
  
  protected int getNextNode() {
    this.m_lastFetched = (-1 == this.m_lastFetched) ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched);
    return this.m_lastFetched;
  }
  
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {
    ChildTestIterator childTestIterator = (ChildTestIterator)super.cloneWithReset();
    childTestIterator.m_traverser = this.m_traverser;
    return childTestIterator;
  }
  
  public void setRoot(int paramInt, Object paramObject) {
    super.setRoot(paramInt, paramObject);
    this.m_traverser = this.m_cdtm.getAxisTraverser(3);
  }
  
  public int getAxis() { return 3; }
  
  public void detach() {
    if (this.m_allowDetach) {
      this.m_traverser = null;
      super.detach();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\ChildTestIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */