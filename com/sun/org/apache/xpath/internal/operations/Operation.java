package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class Operation extends Expression implements ExpressionOwner {
  static final long serialVersionUID = -3037139537171050430L;
  
  protected Expression m_left;
  
  protected Expression m_right;
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    this.m_left.fixupVariables(paramVector, paramInt);
    this.m_right.fixupVariables(paramVector, paramInt);
  }
  
  public boolean canTraverseOutsideSubtree() { return (null != this.m_left && this.m_left.canTraverseOutsideSubtree()) ? true : ((null != this.m_right && this.m_right.canTraverseOutsideSubtree())); }
  
  public void setLeftRight(Expression paramExpression1, Expression paramExpression2) {
    this.m_left = paramExpression1;
    this.m_right = paramExpression2;
    paramExpression1.exprSetParent(this);
    paramExpression2.exprSetParent(this);
  }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    XObject xObject1 = this.m_left.execute(paramXPathContext, true);
    XObject xObject2 = this.m_right.execute(paramXPathContext, true);
    XObject xObject3 = operate(xObject1, xObject2);
    xObject1.detach();
    xObject2.detach();
    return xObject3;
  }
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2) throws TransformerException { return null; }
  
  public Expression getLeftOperand() { return this.m_left; }
  
  public Expression getRightOperand() { return this.m_right; }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) {
    if (paramXPathVisitor.visitBinaryOperation(paramExpressionOwner, this)) {
      this.m_left.callVisitors(new LeftExprOwner(), paramXPathVisitor);
      this.m_right.callVisitors(this, paramXPathVisitor);
    } 
  }
  
  public Expression getExpression() { return this.m_right; }
  
  public void setExpression(Expression paramExpression) {
    paramExpression.exprSetParent(this);
    this.m_right = paramExpression;
  }
  
  public boolean deepEquals(Expression paramExpression) { return !isSameClass(paramExpression) ? false : (!this.m_left.deepEquals(((Operation)paramExpression).m_left) ? false : (!!this.m_right.deepEquals(((Operation)paramExpression).m_right))); }
  
  class LeftExprOwner implements ExpressionOwner {
    public Expression getExpression() { return Operation.this.m_left; }
    
    public void setExpression(Expression param1Expression) {
      param1Expression.exprSetParent(Operation.this);
      Operation.this.m_left = param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\Operation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */