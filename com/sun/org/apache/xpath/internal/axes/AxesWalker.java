package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class AxesWalker extends PredicatedNodeTest implements Cloneable, PathComponent, ExpressionOwner {
  static final long serialVersionUID = -2966031951306601247L;
  
  private DTM m_dtm;
  
  int m_root = -1;
  
  private int m_currentNode = -1;
  
  boolean m_isFresh;
  
  protected AxesWalker m_nextWalker;
  
  AxesWalker m_prevWalker;
  
  protected int m_axis = -1;
  
  protected DTMAxisTraverser m_traverser;
  
  public AxesWalker(LocPathIterator paramLocPathIterator, int paramInt) {
    super(paramLocPathIterator);
    this.m_axis = paramInt;
  }
  
  public final WalkingIterator wi() { return (WalkingIterator)this.m_lpi; }
  
  public void init(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException { initPredicateInfo(paramCompiler, paramInt1); }
  
  public Object clone() throws CloneNotSupportedException { return (AxesWalker)super.clone(); }
  
  AxesWalker cloneDeep(WalkingIterator paramWalkingIterator, Vector paramVector) throws CloneNotSupportedException {
    AxesWalker axesWalker = findClone(this, paramVector);
    if (null != axesWalker)
      return axesWalker; 
    axesWalker = (AxesWalker)clone();
    axesWalker.setLocPathIterator(paramWalkingIterator);
    if (null != paramVector) {
      paramVector.addElement(this);
      paramVector.addElement(axesWalker);
    } 
    if ((wi()).m_lastUsedWalker == this)
      paramWalkingIterator.m_lastUsedWalker = axesWalker; 
    if (null != this.m_nextWalker)
      axesWalker.m_nextWalker = this.m_nextWalker.cloneDeep(paramWalkingIterator, paramVector); 
    if (null != paramVector) {
      if (null != this.m_prevWalker)
        axesWalker.m_prevWalker = this.m_prevWalker.cloneDeep(paramWalkingIterator, paramVector); 
    } else if (null != this.m_nextWalker) {
      axesWalker.m_nextWalker.m_prevWalker = axesWalker;
    } 
    return axesWalker;
  }
  
  static AxesWalker findClone(AxesWalker paramAxesWalker, Vector paramVector) {
    if (null != paramVector) {
      int i = paramVector.size();
      for (byte b = 0; b < i; b += 2) {
        if (paramAxesWalker == paramVector.elementAt(b))
          return (AxesWalker)paramVector.elementAt(b + 1); 
      } 
    } 
    return null;
  }
  
  public void detach() {
    this.m_currentNode = -1;
    this.m_dtm = null;
    this.m_traverser = null;
    this.m_isFresh = true;
    this.m_root = -1;
  }
  
  public int getRoot() { return this.m_root; }
  
  public int getAnalysisBits() {
    int i = getAxis();
    return WalkerFactory.getAnalysisBitFromAxes(i);
  }
  
  public void setRoot(int paramInt) {
    XPathContext xPathContext = wi().getXPathContext();
    this.m_dtm = xPathContext.getDTM(paramInt);
    this.m_traverser = this.m_dtm.getAxisTraverser(this.m_axis);
    this.m_isFresh = true;
    this.m_foundLast = false;
    this.m_root = paramInt;
    this.m_currentNode = paramInt;
    if (-1 == paramInt)
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_SETTING_WALKER_ROOT_TO_NULL", null)); 
    resetProximityPositions();
  }
  
  public final int getCurrentNode() { return this.m_currentNode; }
  
  public void setNextWalker(AxesWalker paramAxesWalker) { this.m_nextWalker = paramAxesWalker; }
  
  public AxesWalker getNextWalker() { return this.m_nextWalker; }
  
  public void setPrevWalker(AxesWalker paramAxesWalker) { this.m_prevWalker = paramAxesWalker; }
  
  public AxesWalker getPrevWalker() { return this.m_prevWalker; }
  
  private int returnNextNode(int paramInt) { return paramInt; }
  
  protected int getNextNode() {
    if (this.m_foundLast)
      return -1; 
    if (this.m_isFresh) {
      this.m_currentNode = this.m_traverser.first(this.m_root);
      this.m_isFresh = false;
    } else if (-1 != this.m_currentNode) {
      this.m_currentNode = this.m_traverser.next(this.m_root, this.m_currentNode);
    } 
    if (-1 == this.m_currentNode)
      this.m_foundLast = true; 
    return this.m_currentNode;
  }
  
  public int nextNode() {
    int i = -1;
    AxesWalker axesWalker = wi().getLastUsedWalker();
    while (null != axesWalker) {
      i = axesWalker.getNextNode();
      if (-1 == i) {
        axesWalker = axesWalker.m_prevWalker;
        continue;
      } 
      if (axesWalker.acceptNode(i) != 1)
        continue; 
      if (null == axesWalker.m_nextWalker) {
        wi().setLastUsedWalker(axesWalker);
        break;
      } 
      AxesWalker axesWalker1 = axesWalker;
      axesWalker = axesWalker.m_nextWalker;
      axesWalker.setRoot(i);
      axesWalker.m_prevWalker = axesWalker1;
    } 
    return i;
  }
  
  public int getLastPos(XPathContext paramXPathContext) {
    AxesWalker axesWalker1;
    int i = getProximityPosition();
    try {
      axesWalker1 = (AxesWalker)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return -1;
    } 
    axesWalker1.setPredicateCount(this.m_predicateIndex);
    axesWalker1.setNextWalker(null);
    axesWalker1.setPrevWalker(null);
    walkingIterator = wi();
    axesWalker2 = walkingIterator.getLastUsedWalker();
    try {
      walkingIterator.setLastUsedWalker(axesWalker1);
      int j;
      while (-1 != (j = axesWalker1.nextNode()))
        i++; 
    } finally {
      walkingIterator.setLastUsedWalker(axesWalker2);
    } 
    return i;
  }
  
  public void setDefaultDTM(DTM paramDTM) { this.m_dtm = paramDTM; }
  
  public DTM getDTM(int paramInt) { return wi().getXPathContext().getDTM(paramInt); }
  
  public boolean isDocOrdered() { return true; }
  
  public int getAxis() { return this.m_axis; }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) {
    if (paramXPathVisitor.visitStep(paramExpressionOwner, this)) {
      callPredicateVisitors(paramXPathVisitor);
      if (null != this.m_nextWalker)
        this.m_nextWalker.callVisitors(this, paramXPathVisitor); 
    } 
  }
  
  public Expression getExpression() { return this.m_nextWalker; }
  
  public void setExpression(Expression paramExpression) {
    paramExpression.exprSetParent(this);
    this.m_nextWalker = (AxesWalker)paramExpression;
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!super.deepEquals(paramExpression))
      return false; 
    AxesWalker axesWalker = (AxesWalker)paramExpression;
    return !(this.m_axis != axesWalker.m_axis);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\AxesWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */