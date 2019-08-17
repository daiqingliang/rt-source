package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FilterExprWalker extends AxesWalker {
  static final long serialVersionUID = 5457182471424488375L;
  
  private Expression m_expr;
  
  private XNodeSet m_exprObj;
  
  private boolean m_mustHardReset = false;
  
  private boolean m_canDetachNodeset = true;
  
  public FilterExprWalker(WalkingIterator paramWalkingIterator) { super(paramWalkingIterator, 20); }
  
  public void init(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    super.init(paramCompiler, paramInt1, paramInt2);
    switch (paramInt2) {
      case 24:
      case 25:
        this.m_mustHardReset = true;
      case 22:
      case 23:
        this.m_expr = paramCompiler.compile(paramInt1);
        this.m_expr.exprSetParent(this);
        if (this.m_expr instanceof com.sun.org.apache.xpath.internal.operations.Variable)
          this.m_canDetachNodeset = false; 
        return;
    } 
    this.m_expr = paramCompiler.compile(paramInt1 + 2);
    this.m_expr.exprSetParent(this);
  }
  
  public void detach() {
    super.detach();
    if (this.m_canDetachNodeset)
      this.m_exprObj.detach(); 
    this.m_exprObj = null;
  }
  
  public void setRoot(int paramInt) {
    super.setRoot(paramInt);
    this.m_exprObj = FilterExprIteratorSimple.executeFilterExpr(paramInt, this.m_lpi.getXPathContext(), this.m_lpi.getPrefixResolver(), this.m_lpi.getIsTopLevel(), this.m_lpi.m_stackFrame, this.m_expr);
  }
  
  public Object clone() throws CloneNotSupportedException {
    FilterExprWalker filterExprWalker = (FilterExprWalker)super.clone();
    if (null != this.m_exprObj)
      filterExprWalker.m_exprObj = (XNodeSet)this.m_exprObj.clone(); 
    return filterExprWalker;
  }
  
  public short acceptNode(int paramInt) {
    try {
      if (getPredicateCount() > 0) {
        countProximityPosition(0);
        if (!executePredicates(paramInt, this.m_lpi.getXPathContext()))
          return 3; 
      } 
      return 1;
    } catch (TransformerException transformerException) {
      throw new RuntimeException(transformerException.getMessage());
    } 
  }
  
  public int getNextNode() { return (null != this.m_exprObj) ? this.m_exprObj.nextNode() : -1; }
  
  public int getLastPos(XPathContext paramXPathContext) { return this.m_exprObj.getLength(); }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    super.fixupVariables(paramVector, paramInt);
    this.m_expr.fixupVariables(paramVector, paramInt);
  }
  
  public Expression getInnerExpression() { return this.m_expr; }
  
  public void setInnerExpression(Expression paramExpression) {
    paramExpression.exprSetParent(this);
    this.m_expr = paramExpression;
  }
  
  public int getAnalysisBits() { return (null != this.m_expr && this.m_expr instanceof PathComponent) ? ((PathComponent)this.m_expr).getAnalysisBits() : 67108864; }
  
  public boolean isDocOrdered() { return this.m_exprObj.isDocOrdered(); }
  
  public int getAxis() { return this.m_exprObj.getAxis(); }
  
  public void callPredicateVisitors(XPathVisitor paramXPathVisitor) {
    this.m_expr.callVisitors(new filterExprOwner(), paramXPathVisitor);
    super.callPredicateVisitors(paramXPathVisitor);
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!super.deepEquals(paramExpression))
      return false; 
    FilterExprWalker filterExprWalker = (FilterExprWalker)paramExpression;
    return !!this.m_expr.deepEquals(filterExprWalker.m_expr);
  }
  
  class filterExprOwner implements ExpressionOwner {
    public Expression getExpression() { return FilterExprWalker.this.m_expr; }
    
    public void setExpression(Expression param1Expression) {
      param1Expression.exprSetParent(FilterExprWalker.this);
      FilterExprWalker.this.m_expr = param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\FilterExprWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */