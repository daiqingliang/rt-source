package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import javax.xml.transform.TransformerException;

public class MatchPatternIterator extends LocPathIterator {
  static final long serialVersionUID = -5201153767396296474L;
  
  protected StepPattern m_pattern;
  
  protected int m_superAxis = -1;
  
  protected DTMAxisTraverser m_traverser;
  
  private static final boolean DEBUG = false;
  
  MatchPatternIterator(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    super(paramCompiler, paramInt1, paramInt2, false);
    int i = OpMap.getFirstChildPos(paramInt1);
    this.m_pattern = WalkerFactory.loadSteps(this, paramCompiler, i, 0);
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    if (0 != (paramInt2 & 0x28000000))
      bool1 = true; 
    if (0 != (paramInt2 & 0x5D86000))
      bool2 = true; 
    if (0 != (paramInt2 & 0x70000))
      bool3 = true; 
    if (0 != (paramInt2 & 0x208000))
      bool4 = true; 
    if (bool1 || bool2) {
      if (bool4) {
        this.m_superAxis = 16;
      } else {
        this.m_superAxis = 17;
      } 
    } else if (bool3) {
      if (bool4) {
        this.m_superAxis = 14;
      } else {
        this.m_superAxis = 5;
      } 
    } else {
      this.m_superAxis = 16;
    } 
  }
  
  public void setRoot(int paramInt, Object paramObject) {
    super.setRoot(paramInt, paramObject);
    this.m_traverser = this.m_cdtm.getAxisTraverser(this.m_superAxis);
  }
  
  public void detach() {
    if (this.m_allowDetach) {
      this.m_traverser = null;
      super.detach();
    } 
  }
  
  protected int getNextNode() {
    this.m_lastFetched = (-1 == this.m_lastFetched) ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched);
    return this.m_lastFetched;
  }
  
  public int nextNode() {
    byte b;
    Object object;
    int i;
    if (this.m_foundLast)
      return -1; 
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
    } while (-1 != i && 1 != acceptNode(i, this.m_execContext) && i != -1);
    if (-1 != i) {
      incrementCurrentPos();
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
  
  public short acceptNode(int paramInt, XPathContext paramXPathContext) {
    try {
      paramXPathContext.pushCurrentNode(paramInt);
      paramXPathContext.pushIteratorRoot(this.m_context);
      XObject xObject = this.m_pattern.execute(paramXPathContext);
      return (xObject == NodeTest.SCORE_NONE) ? 3 : 1;
    } catch (TransformerException transformerException) {
      throw new RuntimeException(transformerException.getMessage());
    } finally {
      paramXPathContext.popCurrentNode();
      paramXPathContext.popIteratorRoot();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\MatchPatternIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */