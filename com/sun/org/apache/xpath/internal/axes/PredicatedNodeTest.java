package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public abstract class PredicatedNodeTest extends NodeTest implements SubContextList {
  static final long serialVersionUID = -6193530757296377351L;
  
  protected int m_predCount = -1;
  
  protected boolean m_foundLast = false;
  
  protected LocPathIterator m_lpi;
  
  int m_predicateIndex = -1;
  
  private Expression[] m_predicates;
  
  protected int[] m_proximityPositions;
  
  static final boolean DEBUG_PREDICATECOUNTING = false;
  
  PredicatedNodeTest(LocPathIterator paramLocPathIterator) { this.m_lpi = paramLocPathIterator; }
  
  PredicatedNodeTest() {}
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, TransformerException {
    try {
      paramObjectInputStream.defaultReadObject();
      this.m_predicateIndex = -1;
      this.m_predCount = -1;
      resetProximityPositions();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new TransformerException(classNotFoundException);
    } 
  }
  
  public Object clone() throws CloneNotSupportedException {
    PredicatedNodeTest predicatedNodeTest = (PredicatedNodeTest)super.clone();
    if (null != this.m_proximityPositions && this.m_proximityPositions == predicatedNodeTest.m_proximityPositions) {
      predicatedNodeTest.m_proximityPositions = new int[this.m_proximityPositions.length];
      System.arraycopy(this.m_proximityPositions, 0, predicatedNodeTest.m_proximityPositions, 0, this.m_proximityPositions.length);
    } 
    if (predicatedNodeTest.m_lpi == this)
      predicatedNodeTest.m_lpi = (LocPathIterator)predicatedNodeTest; 
    return predicatedNodeTest;
  }
  
  public int getPredicateCount() { return (-1 == this.m_predCount) ? ((null == this.m_predicates) ? 0 : this.m_predicates.length) : this.m_predCount; }
  
  public void setPredicateCount(int paramInt) {
    if (paramInt > 0) {
      Expression[] arrayOfExpression = new Expression[paramInt];
      for (byte b = 0; b < paramInt; b++)
        arrayOfExpression[b] = this.m_predicates[b]; 
      this.m_predicates = arrayOfExpression;
    } else {
      this.m_predicates = null;
    } 
  }
  
  protected void initPredicateInfo(Compiler paramCompiler, int paramInt) throws TransformerException {
    int i = paramCompiler.getFirstPredicateOpPos(paramInt);
    if (i > 0) {
      this.m_predicates = paramCompiler.getCompiledPredicates(i);
      if (null != this.m_predicates)
        for (byte b = 0; b < this.m_predicates.length; b++)
          this.m_predicates[b].exprSetParent(this);  
    } 
  }
  
  public Expression getPredicate(int paramInt) { return this.m_predicates[paramInt]; }
  
  public int getProximityPosition() { return getProximityPosition(this.m_predicateIndex); }
  
  public int getProximityPosition(XPathContext paramXPathContext) { return getProximityPosition(); }
  
  public abstract int getLastPos(XPathContext paramXPathContext);
  
  protected int getProximityPosition(int paramInt) { return (paramInt >= 0) ? this.m_proximityPositions[paramInt] : 0; }
  
  public void resetProximityPositions() {
    int i = getPredicateCount();
    if (i > 0) {
      if (null == this.m_proximityPositions)
        this.m_proximityPositions = new int[i]; 
      for (byte b = 0; b < i; b++) {
        try {
          initProximityPosition(b);
        } catch (Exception exception) {
          throw new WrappedRuntimeException(exception);
        } 
      } 
    } 
  }
  
  public void initProximityPosition(int paramInt) { this.m_proximityPositions[paramInt] = 0; }
  
  protected void countProximityPosition(int paramInt) {
    int[] arrayOfInt = this.m_proximityPositions;
    if (null != arrayOfInt && paramInt < arrayOfInt.length)
      arrayOfInt[paramInt] = arrayOfInt[paramInt] + 1; 
  }
  
  public boolean isReverseAxes() { return false; }
  
  public int getPredicateIndex() { return this.m_predicateIndex; }
  
  boolean executePredicates(int paramInt, XPathContext paramXPathContext) throws TransformerException {
    int i = getPredicateCount();
    if (i == 0)
      return true; 
    PrefixResolver prefixResolver = paramXPathContext.getNamespaceContext();
    try {
      this.m_predicateIndex = 0;
      paramXPathContext.pushSubContextList(this);
      paramXPathContext.pushNamespaceContext(this.m_lpi.getPrefixResolver());
      paramXPathContext.pushCurrentNode(paramInt);
      for (byte b = 0; b < i; b++) {
        XObject xObject = this.m_predicates[b].execute(paramXPathContext);
        if (2 == xObject.getType()) {
          int j = getProximityPosition(this.m_predicateIndex);
          int k = (int)xObject.num();
          if (j != k)
            return false; 
          if (this.m_predicates[b].isStableNumber() && b == i - 1)
            this.m_foundLast = true; 
        } else if (!xObject.bool()) {
          return false;
        } 
        countProximityPosition(++this.m_predicateIndex);
      } 
    } finally {
      paramXPathContext.popCurrentNode();
      paramXPathContext.popNamespaceContext();
      paramXPathContext.popSubContextList();
      this.m_predicateIndex = -1;
    } 
    return true;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    super.fixupVariables(paramVector, paramInt);
    int i = getPredicateCount();
    for (byte b = 0; b < i; b++)
      this.m_predicates[b].fixupVariables(paramVector, paramInt); 
  }
  
  protected String nodeToString(int paramInt) {
    if (-1 != paramInt) {
      DTM dTM = this.m_lpi.getXPathContext().getDTM(paramInt);
      return dTM.getNodeName(paramInt) + "{" + (paramInt + 1) + "}";
    } 
    return "null";
  }
  
  public short acceptNode(int paramInt) {
    xPathContext = this.m_lpi.getXPathContext();
    try {
      xPathContext.pushCurrentNode(paramInt);
      XObject xObject = execute(xPathContext, paramInt);
      if (xObject != NodeTest.SCORE_NONE) {
        if (getPredicateCount() > 0) {
          countProximityPosition(0);
          if (!executePredicates(paramInt, xPathContext))
            return 3; 
        } 
        return 1;
      } 
    } catch (TransformerException transformerException) {
      throw new RuntimeException(transformerException.getMessage());
    } finally {
      xPathContext.popCurrentNode();
    } 
    return 3;
  }
  
  public LocPathIterator getLocPathIterator() { return this.m_lpi; }
  
  public void setLocPathIterator(LocPathIterator paramLocPathIterator) {
    this.m_lpi = paramLocPathIterator;
    if (this != paramLocPathIterator)
      paramLocPathIterator.exprSetParent(this); 
  }
  
  public boolean canTraverseOutsideSubtree() {
    int i = getPredicateCount();
    for (byte b = 0; b < i; b++) {
      if (getPredicate(b).canTraverseOutsideSubtree())
        return true; 
    } 
    return false;
  }
  
  public void callPredicateVisitors(XPathVisitor paramXPathVisitor) {
    if (null != this.m_predicates) {
      int i = this.m_predicates.length;
      for (byte b = 0; b < i; b++) {
        PredOwner predOwner = new PredOwner(b);
        if (paramXPathVisitor.visitPredicate(predOwner, this.m_predicates[b]))
          this.m_predicates[b].callVisitors(predOwner, paramXPathVisitor); 
      } 
    } 
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!super.deepEquals(paramExpression))
      return false; 
    PredicatedNodeTest predicatedNodeTest = (PredicatedNodeTest)paramExpression;
    if (null != this.m_predicates) {
      int i = this.m_predicates.length;
      if (null == predicatedNodeTest.m_predicates || predicatedNodeTest.m_predicates.length != i)
        return false; 
      for (byte b = 0; b < i; b++) {
        if (!this.m_predicates[b].deepEquals(predicatedNodeTest.m_predicates[b]))
          return false; 
      } 
    } else if (null != predicatedNodeTest.m_predicates) {
      return false;
    } 
    return true;
  }
  
  class PredOwner implements ExpressionOwner {
    int m_index;
    
    PredOwner(int param1Int) { this.m_index = param1Int; }
    
    public Expression getExpression() { return PredicatedNodeTest.this.m_predicates[this.m_index]; }
    
    public void setExpression(Expression param1Expression) {
      param1Expression.exprSetParent(PredicatedNodeTest.this);
      PredicatedNodeTest.this.m_predicates[this.m_index] = param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\PredicatedNodeTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */