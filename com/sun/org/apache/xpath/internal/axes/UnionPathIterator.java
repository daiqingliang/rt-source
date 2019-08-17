package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class UnionPathIterator extends LocPathIterator implements Cloneable, DTMIterator, Serializable, PathComponent {
  static final long serialVersionUID = -3910351546843826781L;
  
  protected LocPathIterator[] m_exprs;
  
  protected DTMIterator[] m_iterators;
  
  public UnionPathIterator() {
    this.m_iterators = null;
    this.m_exprs = null;
  }
  
  public void setRoot(int paramInt, Object paramObject) {
    super.setRoot(paramInt, paramObject);
    try {
      if (null != this.m_exprs) {
        int i = this.m_exprs.length;
        DTMIterator[] arrayOfDTMIterator = new DTMIterator[i];
        for (byte b = 0; b < i; b++) {
          DTMIterator dTMIterator = this.m_exprs[b].asIterator(this.m_execContext, paramInt);
          arrayOfDTMIterator[b] = dTMIterator;
          dTMIterator.nextNode();
        } 
        this.m_iterators = arrayOfDTMIterator;
      } 
    } catch (Exception exception) {
      throw new WrappedRuntimeException(exception);
    } 
  }
  
  public void addIterator(DTMIterator paramDTMIterator) {
    if (null == this.m_iterators) {
      this.m_iterators = new DTMIterator[1];
      this.m_iterators[0] = paramDTMIterator;
    } else {
      DTMIterator[] arrayOfDTMIterator = this.m_iterators;
      int i = this.m_iterators.length;
      this.m_iterators = new DTMIterator[i + 1];
      System.arraycopy(arrayOfDTMIterator, 0, this.m_iterators, 0, i);
      this.m_iterators[i] = paramDTMIterator;
    } 
    paramDTMIterator.nextNode();
    if (paramDTMIterator instanceof Expression)
      ((Expression)paramDTMIterator).exprSetParent(this); 
  }
  
  public void detach() {
    if (this.m_allowDetach && null != this.m_iterators) {
      int i = this.m_iterators.length;
      for (byte b = 0; b < i; b++)
        this.m_iterators[b].detach(); 
      this.m_iterators = null;
    } 
  }
  
  public UnionPathIterator(Compiler paramCompiler, int paramInt) throws TransformerException {
    paramInt = OpMap.getFirstChildPos(paramInt);
    loadLocationPaths(paramCompiler, paramInt, 0);
  }
  
  public static LocPathIterator createUnionIterator(Compiler paramCompiler, int paramInt) throws TransformerException {
    UnionPathIterator unionPathIterator = new UnionPathIterator(paramCompiler, paramInt);
    int i = unionPathIterator.m_exprs.length;
    boolean bool = true;
    for (byte b = 0; b < i; b++) {
      LocPathIterator locPathIterator = unionPathIterator.m_exprs[b];
      if (locPathIterator.getAxis() != 3) {
        bool = false;
        break;
      } 
      if (HasPositionalPredChecker.check(locPathIterator)) {
        bool = false;
        break;
      } 
    } 
    if (bool) {
      UnionChildIterator unionChildIterator = new UnionChildIterator();
      for (byte b1 = 0; b1 < i; b1++) {
        LocPathIterator locPathIterator = unionPathIterator.m_exprs[b1];
        unionChildIterator.addNodeTest(locPathIterator);
      } 
      return unionChildIterator;
    } 
    return unionPathIterator;
  }
  
  public int getAnalysisBits() {
    int i = 0;
    if (this.m_exprs != null) {
      int j = this.m_exprs.length;
      for (byte b = 0; b < j; b++) {
        int k = this.m_exprs[b].getAnalysisBits();
        i |= k;
      } 
    } 
    return i;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, TransformerException {
    try {
      paramObjectInputStream.defaultReadObject();
      this.m_clones = new IteratorPool(this);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new TransformerException(classNotFoundException);
    } 
  }
  
  public Object clone() throws CloneNotSupportedException {
    UnionPathIterator unionPathIterator = (UnionPathIterator)super.clone();
    if (this.m_iterators != null) {
      int i = this.m_iterators.length;
      unionPathIterator.m_iterators = new DTMIterator[i];
      for (byte b = 0; b < i; b++)
        unionPathIterator.m_iterators[b] = (DTMIterator)this.m_iterators[b].clone(); 
    } 
    return unionPathIterator;
  }
  
  protected LocPathIterator createDTMIterator(Compiler paramCompiler, int paramInt) throws TransformerException { return (LocPathIterator)WalkerFactory.newDTMIterator(paramCompiler, paramInt, (paramCompiler.getLocationPathDepth() <= 0)); }
  
  protected void loadLocationPaths(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    int i = paramCompiler.getOp(paramInt1);
    if (i == 28) {
      loadLocationPaths(paramCompiler, paramCompiler.getNextOpPos(paramInt1), paramInt2 + 1);
      this.m_exprs[paramInt2] = createDTMIterator(paramCompiler, paramInt1);
      this.m_exprs[paramInt2].exprSetParent(this);
    } else {
      WalkingIterator walkingIterator;
      switch (i) {
        case 22:
        case 23:
        case 24:
        case 25:
          loadLocationPaths(paramCompiler, paramCompiler.getNextOpPos(paramInt1), paramInt2 + 1);
          walkingIterator = new WalkingIterator(paramCompiler.getNamespaceContext());
          walkingIterator.exprSetParent(this);
          if (paramCompiler.getLocationPathDepth() <= 0)
            walkingIterator.setIsTopLevel(true); 
          walkingIterator.m_firstWalker = new FilterExprWalker(walkingIterator);
          walkingIterator.m_firstWalker.init(paramCompiler, paramInt1, i);
          this.m_exprs[paramInt2] = walkingIterator;
          return;
      } 
      this.m_exprs = new LocPathIterator[paramInt2];
    } 
  }
  
  public int nextNode() {
    if (this.m_foundLast)
      return -1; 
    int i = -1;
    if (null != this.m_iterators) {
      int j = this.m_iterators.length;
      byte b1 = -1;
      for (byte b2 = 0; b2 < j; b2++) {
        int k = this.m_iterators[b2].getCurrentNode();
        if (-1 != k)
          if (-1 == i) {
            b1 = b2;
            i = k;
          } else if (k == i) {
            this.m_iterators[b2].nextNode();
          } else {
            DTM dTM = getDTM(k);
            if (dTM.isNodeAfter(k, i)) {
              b1 = b2;
              i = k;
            } 
          }  
      } 
      if (-1 != i) {
        this.m_iterators[b1].nextNode();
        incrementCurrentPos();
      } else {
        this.m_foundLast = true;
      } 
    } 
    this.m_lastFetched = i;
    return i;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    for (byte b = 0; b < this.m_exprs.length; b++)
      this.m_exprs[b].fixupVariables(paramVector, paramInt); 
  }
  
  public int getAxis() { return -1; }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) {
    if (paramXPathVisitor.visitUnionPath(paramExpressionOwner, this) && null != this.m_exprs) {
      int i = this.m_exprs.length;
      for (byte b = 0; b < i; b++)
        this.m_exprs[b].callVisitors(new iterOwner(b), paramXPathVisitor); 
    } 
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!super.deepEquals(paramExpression))
      return false; 
    UnionPathIterator unionPathIterator = (UnionPathIterator)paramExpression;
    if (null != this.m_exprs) {
      int i = this.m_exprs.length;
      if (null == unionPathIterator.m_exprs || unionPathIterator.m_exprs.length != i)
        return false; 
      for (byte b = 0; b < i; b++) {
        if (!this.m_exprs[b].deepEquals(unionPathIterator.m_exprs[b]))
          return false; 
      } 
    } else if (null != unionPathIterator.m_exprs) {
      return false;
    } 
    return true;
  }
  
  class iterOwner implements ExpressionOwner {
    int m_index;
    
    iterOwner(int param1Int) { this.m_index = param1Int; }
    
    public Expression getExpression() { return UnionPathIterator.this.m_exprs[this.m_index]; }
    
    public void setExpression(Expression param1Expression) {
      if (!(param1Expression instanceof LocPathIterator)) {
        WalkingIterator walkingIterator = new WalkingIterator(UnionPathIterator.this.getPrefixResolver());
        FilterExprWalker filterExprWalker = new FilterExprWalker(walkingIterator);
        walkingIterator.setFirstWalker(filterExprWalker);
        filterExprWalker.setInnerExpression(param1Expression);
        walkingIterator.exprSetParent(UnionPathIterator.this);
        filterExprWalker.exprSetParent(walkingIterator);
        param1Expression.exprSetParent(filterExprWalker);
        param1Expression = walkingIterator;
      } else {
        param1Expression.exprSetParent(UnionPathIterator.this);
      } 
      UnionPathIterator.this.m_exprs[this.m_index] = (LocPathIterator)param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\UnionPathIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */