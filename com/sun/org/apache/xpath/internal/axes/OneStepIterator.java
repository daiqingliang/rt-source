package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import javax.xml.transform.TransformerException;

public class OneStepIterator extends ChildTestIterator {
  static final long serialVersionUID = 4623710779664998283L;
  
  protected int m_axis = -1;
  
  protected DTMAxisIterator m_iterator;
  
  OneStepIterator(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    super(paramCompiler, paramInt1, paramInt2);
    int i = OpMap.getFirstChildPos(paramInt1);
    this.m_axis = WalkerFactory.getAxisFromStep(paramCompiler, i);
  }
  
  public OneStepIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt) throws TransformerException {
    super(null);
    this.m_iterator = paramDTMAxisIterator;
    this.m_axis = paramInt;
    byte b = -1;
    initNodeTest(b);
  }
  
  public void setRoot(int paramInt, Object paramObject) {
    super.setRoot(paramInt, paramObject);
    if (this.m_axis > -1)
      this.m_iterator = this.m_cdtm.getAxisIterator(this.m_axis); 
    this.m_iterator.setStartNode(this.m_context);
  }
  
  public void detach() {
    if (this.m_allowDetach) {
      if (this.m_axis > -1)
        this.m_iterator = null; 
      super.detach();
    } 
  }
  
  protected int getNextNode() { return this.m_lastFetched = this.m_iterator.next(); }
  
  public Object clone() throws CloneNotSupportedException {
    OneStepIterator oneStepIterator = (OneStepIterator)super.clone();
    if (this.m_iterator != null)
      oneStepIterator.m_iterator = this.m_iterator.cloneIterator(); 
    return oneStepIterator;
  }
  
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {
    OneStepIterator oneStepIterator = (OneStepIterator)super.cloneWithReset();
    oneStepIterator.m_iterator = this.m_iterator;
    return oneStepIterator;
  }
  
  public boolean isReverseAxes() { return this.m_iterator.isReverse(); }
  
  protected int getProximityPosition(int paramInt) {
    if (!isReverseAxes())
      return super.getProximityPosition(paramInt); 
    if (paramInt < 0)
      return -1; 
    if (this.m_proximityPositions[paramInt] <= 0) {
      xPathContext = getXPathContext();
      try {
        OneStepIterator oneStepIterator = (OneStepIterator)clone();
        int i = getRoot();
        xPathContext.pushCurrentNode(i);
        oneStepIterator.setRoot(i, xPathContext);
        oneStepIterator.m_predCount = paramInt;
        int j;
        int k;
        for (j = 1; -1 != (k = oneStepIterator.nextNode()); j++);
        this.m_proximityPositions[paramInt] = this.m_proximityPositions[paramInt] + j;
      } catch (CloneNotSupportedException cloneNotSupportedException) {
      
      } finally {
        xPathContext.popCurrentNode();
      } 
    } 
    return this.m_proximityPositions[paramInt];
  }
  
  public int getLength() {
    if (!isReverseAxes())
      return super.getLength(); 
    boolean bool = (this == this.m_execContext.getSubContextList()) ? 1 : 0;
    int i = getPredicateCount();
    if (-1 != this.m_length && bool && this.m_predicateIndex < 1)
      return this.m_length; 
    byte b = 0;
    xPathContext = getXPathContext();
    try {
      OneStepIterator oneStepIterator = (OneStepIterator)cloneWithReset();
      int j = getRoot();
      xPathContext.pushCurrentNode(j);
      oneStepIterator.setRoot(j, xPathContext);
      oneStepIterator.m_predCount = this.m_predicateIndex;
      int k;
      while (-1 != (k = oneStepIterator.nextNode()))
        b++; 
    } catch (CloneNotSupportedException cloneNotSupportedException) {
    
    } finally {
      xPathContext.popCurrentNode();
    } 
    if (bool && this.m_predicateIndex < 1)
      this.m_length = b; 
    return b;
  }
  
  protected void countProximityPosition(int paramInt) {
    if (!isReverseAxes()) {
      super.countProximityPosition(paramInt);
    } else if (paramInt < this.m_proximityPositions.length) {
      this.m_proximityPositions[paramInt] = this.m_proximityPositions[paramInt] - 1;
    } 
  }
  
  public void reset() {
    super.reset();
    if (null != this.m_iterator)
      this.m_iterator.reset(); 
  }
  
  public int getAxis() { return this.m_axis; }
  
  public boolean deepEquals(Expression paramExpression) { return !super.deepEquals(paramExpression) ? false : (!(this.m_axis != ((OneStepIterator)paramExpression).m_axis)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\OneStepIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */