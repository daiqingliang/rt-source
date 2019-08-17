package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import java.util.Vector;

public class FilterExprIterator extends BasicTestIterator {
  static final long serialVersionUID = 2552176105165737614L;
  
  private Expression m_expr;
  
  private XNodeSet m_exprObj;
  
  private boolean m_mustHardReset = false;
  
  private boolean m_canDetachNodeset = true;
  
  public FilterExprIterator() { super(null); }
  
  public FilterExprIterator(Expression paramExpression) {
    super(null);
    this.m_expr = paramExpression;
  }
  
  public void setRoot(int paramInt, Object paramObject) {
    super.setRoot(paramInt, paramObject);
    this.m_exprObj = FilterExprIteratorSimple.executeFilterExpr(paramInt, this.m_execContext, getPrefixResolver(), getIsTopLevel(), this.m_stackFrame, this.m_expr);
  }
  
  protected int getNextNode() {
    if (null != this.m_exprObj) {
      this.m_lastFetched = this.m_exprObj.nextNode();
    } else {
      this.m_lastFetched = -1;
    } 
    return this.m_lastFetched;
  }
  
  public void detach() {
    super.detach();
    this.m_exprObj.detach();
    this.m_exprObj = null;
  }
  
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
  
  public void callPredicateVisitors(XPathVisitor paramXPathVisitor) {
    this.m_expr.callVisitors(new filterExprOwner(), paramXPathVisitor);
    super.callPredicateVisitors(paramXPathVisitor);
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!super.deepEquals(paramExpression))
      return false; 
    FilterExprIterator filterExprIterator = (FilterExprIterator)paramExpression;
    return !!this.m_expr.deepEquals(filterExprIterator.m_expr);
  }
  
  class filterExprOwner implements ExpressionOwner {
    public Expression getExpression() { return FilterExprIterator.this.m_expr; }
    
    public void setExpression(Expression param1Expression) {
      param1Expression.exprSetParent(FilterExprIterator.this);
      FilterExprIterator.this.m_expr = param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\FilterExprIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */