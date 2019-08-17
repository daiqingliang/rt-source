package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class WalkingIterator extends LocPathIterator implements ExpressionOwner {
  static final long serialVersionUID = 9110225941815665906L;
  
  protected AxesWalker m_lastUsedWalker;
  
  protected AxesWalker m_firstWalker;
  
  WalkingIterator(Compiler paramCompiler, int paramInt1, int paramInt2, boolean paramBoolean) throws TransformerException {
    super(paramCompiler, paramInt1, paramInt2, paramBoolean);
    int i = OpMap.getFirstChildPos(paramInt1);
    if (paramBoolean) {
      this.m_firstWalker = WalkerFactory.loadWalkers(this, paramCompiler, i, 0);
      this.m_lastUsedWalker = this.m_firstWalker;
    } 
  }
  
  public WalkingIterator(PrefixResolver paramPrefixResolver) { super(paramPrefixResolver); }
  
  public int getAnalysisBits() {
    int i = 0;
    if (null != this.m_firstWalker)
      for (AxesWalker axesWalker = this.m_firstWalker; null != axesWalker; axesWalker = axesWalker.getNextWalker()) {
        int j = axesWalker.getAnalysisBits();
        i |= j;
      }  
    return i;
  }
  
  public Object clone() throws CloneNotSupportedException {
    WalkingIterator walkingIterator = (WalkingIterator)super.clone();
    if (null != this.m_firstWalker)
      walkingIterator.m_firstWalker = this.m_firstWalker.cloneDeep(walkingIterator, null); 
    return walkingIterator;
  }
  
  public void reset() {
    super.reset();
    if (null != this.m_firstWalker) {
      this.m_lastUsedWalker = this.m_firstWalker;
      this.m_firstWalker.setRoot(this.m_context);
    } 
  }
  
  public void setRoot(int paramInt, Object paramObject) {
    super.setRoot(paramInt, paramObject);
    if (null != this.m_firstWalker) {
      this.m_firstWalker.setRoot(paramInt);
      this.m_lastUsedWalker = this.m_firstWalker;
    } 
  }
  
  public int nextNode() {
    if (this.m_foundLast)
      return -1; 
    if (-1 == this.m_stackFrame)
      return returnNextNode(this.m_firstWalker.nextNode()); 
    VariableStack variableStack = this.m_execContext.getVarStack();
    int i = variableStack.getStackFrame();
    variableStack.setStackFrame(this.m_stackFrame);
    int j = returnNextNode(this.m_firstWalker.nextNode());
    variableStack.setStackFrame(i);
    return j;
  }
  
  public final AxesWalker getFirstWalker() { return this.m_firstWalker; }
  
  public final void setFirstWalker(AxesWalker paramAxesWalker) { this.m_firstWalker = paramAxesWalker; }
  
  public final void setLastUsedWalker(AxesWalker paramAxesWalker) { this.m_lastUsedWalker = paramAxesWalker; }
  
  public final AxesWalker getLastUsedWalker() { return this.m_lastUsedWalker; }
  
  public void detach() {
    if (this.m_allowDetach) {
      for (AxesWalker axesWalker = this.m_firstWalker; null != axesWalker; axesWalker = axesWalker.getNextWalker())
        axesWalker.detach(); 
      this.m_lastUsedWalker = null;
      super.detach();
    } 
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    this.m_predicateIndex = -1;
    for (AxesWalker axesWalker = this.m_firstWalker; null != axesWalker; axesWalker = axesWalker.getNextWalker())
      axesWalker.fixupVariables(paramVector, paramInt); 
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) {
    if (paramXPathVisitor.visitLocationPath(paramExpressionOwner, this) && null != this.m_firstWalker)
      this.m_firstWalker.callVisitors(this, paramXPathVisitor); 
  }
  
  public Expression getExpression() { return this.m_firstWalker; }
  
  public void setExpression(Expression paramExpression) {
    paramExpression.exprSetParent(this);
    this.m_firstWalker = (AxesWalker)paramExpression;
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!super.deepEquals(paramExpression))
      return false; 
    AxesWalker axesWalker1 = this.m_firstWalker;
    AxesWalker axesWalker2;
    for (axesWalker2 = ((WalkingIterator)paramExpression).m_firstWalker; null != axesWalker1 && null != axesWalker2; axesWalker2 = axesWalker2.getNextWalker()) {
      if (!axesWalker1.deepEquals(axesWalker2))
        return false; 
      axesWalker1 = axesWalker1.getNextWalker();
    } 
    return !(null != axesWalker1 || null != axesWalker2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\WalkingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */