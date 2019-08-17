package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FilterExprIteratorSimple extends LocPathIterator {
  static final long serialVersionUID = -6978977187025375579L;
  
  private Expression m_expr;
  
  private XNodeSet m_exprObj;
  
  private boolean m_mustHardReset = false;
  
  private boolean m_canDetachNodeset = true;
  
  public FilterExprIteratorSimple() { super(null); }
  
  public FilterExprIteratorSimple(Expression paramExpression) {
    super(null);
    this.m_expr = paramExpression;
  }
  
  public void setRoot(int paramInt, Object paramObject) {
    super.setRoot(paramInt, paramObject);
    this.m_exprObj = executeFilterExpr(paramInt, this.m_execContext, getPrefixResolver(), getIsTopLevel(), this.m_stackFrame, this.m_expr);
  }
  
  public static XNodeSet executeFilterExpr(int paramInt1, XPathContext paramXPathContext, PrefixResolver paramPrefixResolver, boolean paramBoolean, int paramInt2, Expression paramExpression) throws WrappedRuntimeException {
    prefixResolver = paramXPathContext.getNamespaceContext();
    XNodeSet xNodeSet = null;
    try {
      paramXPathContext.pushCurrentNode(paramInt1);
      paramXPathContext.setNamespaceContext(paramPrefixResolver);
      if (paramBoolean) {
        VariableStack variableStack = paramXPathContext.getVarStack();
        int i = variableStack.getStackFrame();
        variableStack.setStackFrame(paramInt2);
        xNodeSet = (XNodeSet)paramExpression.execute(paramXPathContext);
        xNodeSet.setShouldCacheNodes(true);
        variableStack.setStackFrame(i);
      } else {
        xNodeSet = (XNodeSet)paramExpression.execute(paramXPathContext);
      } 
    } catch (TransformerException transformerException) {
      throw new WrappedRuntimeException(transformerException);
    } finally {
      paramXPathContext.popCurrentNode();
      paramXPathContext.setNamespaceContext(prefixResolver);
    } 
    return xNodeSet;
  }
  
  public int nextNode() {
    byte b;
    if (this.m_foundLast)
      return -1; 
    if (null != this.m_exprObj) {
      this.m_lastFetched = b = this.m_exprObj.nextNode();
    } else {
      this.m_lastFetched = b = -1;
    } 
    if (-1 != b) {
      this.m_pos++;
      return b;
    } 
    this.m_foundLast = true;
    return -1;
  }
  
  public void detach() {
    if (this.m_allowDetach) {
      super.detach();
      this.m_exprObj.detach();
      this.m_exprObj = null;
    } 
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
    FilterExprIteratorSimple filterExprIteratorSimple = (FilterExprIteratorSimple)paramExpression;
    return !!this.m_expr.deepEquals(filterExprIteratorSimple.m_expr);
  }
  
  public int getAxis() { return (null != this.m_exprObj) ? this.m_exprObj.getAxis() : 20; }
  
  class filterExprOwner implements ExpressionOwner {
    public Expression getExpression() { return FilterExprIteratorSimple.this.m_expr; }
    
    public void setExpression(Expression param1Expression) {
      param1Expression.exprSetParent(FilterExprIteratorSimple.this);
      FilterExprIteratorSimple.this.m_expr = param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\FilterExprIteratorSimple.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */