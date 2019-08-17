package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import javax.xml.transform.TransformerException;

public abstract class BasicTestIterator extends LocPathIterator {
  static final long serialVersionUID = 3505378079378096623L;
  
  protected BasicTestIterator() {}
  
  protected BasicTestIterator(PrefixResolver paramPrefixResolver) { super(paramPrefixResolver); }
  
  protected BasicTestIterator(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    super(paramCompiler, paramInt1, paramInt2, false);
    int i = OpMap.getFirstChildPos(paramInt1);
    int j = paramCompiler.getWhatToShow(i);
    if (0 == (j & 0x1043) || j == -1) {
      initNodeTest(j);
    } else {
      initNodeTest(j, paramCompiler.getStepNS(i), paramCompiler.getStepLocalName(i));
    } 
    initPredicateInfo(paramCompiler, i);
  }
  
  protected BasicTestIterator(Compiler paramCompiler, int paramInt1, int paramInt2, boolean paramBoolean) throws TransformerException { super(paramCompiler, paramInt1, paramInt2, paramBoolean); }
  
  protected abstract int getNextNode();
  
  public int nextNode() {
    byte b;
    Object object;
    int i;
    if (this.m_foundLast) {
      this.m_lastFetched = -1;
      return -1;
    } 
    if (-1 == this.m_lastFetched)
      resetProximityPositions(); 
    if (-1 != this.m_stackFrame) {
      object = this.m_execContext.getVarStack();
      b = object.getStackFrame();
      object.setStackFrame(this.m_stackFrame);
    } else {
      object = null;
      b = 0;
    } 
    do {
      i = getNextNode();
    } while (-1 != i && 1 != acceptNode(i) && i != -1);
    if (-1 != i) {
      this.m_pos++;
      int j = i;
      if (-1 != this.m_stackFrame)
        object.setStackFrame(b); 
      return j;
    } 
    this.m_foundLast = true;
    byte b1 = -1;
    if (-1 != this.m_stackFrame)
      object.setStackFrame(b); 
    return b1;
  }
  
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {
    ChildTestIterator childTestIterator = (ChildTestIterator)super.cloneWithReset();
    childTestIterator.resetProximityPositions();
    return childTestIterator;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\BasicTestIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */