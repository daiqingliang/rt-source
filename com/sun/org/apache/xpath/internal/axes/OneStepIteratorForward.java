package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import javax.xml.transform.TransformerException;

public class OneStepIteratorForward extends ChildTestIterator {
  static final long serialVersionUID = -1576936606178190566L;
  
  protected int m_axis = -1;
  
  OneStepIteratorForward(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    super(paramCompiler, paramInt1, paramInt2);
    int i = OpMap.getFirstChildPos(paramInt1);
    this.m_axis = WalkerFactory.getAxisFromStep(paramCompiler, i);
  }
  
  public OneStepIteratorForward(int paramInt) {
    super(null);
    this.m_axis = paramInt;
    byte b = -1;
    initNodeTest(b);
  }
  
  public void setRoot(int paramInt, Object paramObject) {
    super.setRoot(paramInt, paramObject);
    this.m_traverser = this.m_cdtm.getAxisTraverser(this.m_axis);
  }
  
  protected int getNextNode() {
    this.m_lastFetched = (-1 == this.m_lastFetched) ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched);
    return this.m_lastFetched;
  }
  
  public int getAxis() { return this.m_axis; }
  
  public boolean deepEquals(Expression paramExpression) { return !super.deepEquals(paramExpression) ? false : (!(this.m_axis != ((OneStepIteratorForward)paramExpression).m_axis)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\OneStepIteratorForward.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */