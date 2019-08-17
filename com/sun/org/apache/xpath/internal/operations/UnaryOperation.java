package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public abstract class UnaryOperation extends Expression implements ExpressionOwner {
  static final long serialVersionUID = 6536083808424286166L;
  
  protected Expression m_right;
  
  public void fixupVariables(Vector paramVector, int paramInt) { this.m_right.fixupVariables(paramVector, paramInt); }
  
  public boolean canTraverseOutsideSubtree() { return (null != this.m_right && this.m_right.canTraverseOutsideSubtree()); }
  
  public void setRight(Expression paramExpression) {
    this.m_right = paramExpression;
    paramExpression.exprSetParent(this);
  }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException { return operate(this.m_right.execute(paramXPathContext)); }
  
  public abstract XObject operate(XObject paramXObject) throws TransformerException;
  
  public Expression getOperand() { return this.m_right; }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) {
    if (paramXPathVisitor.visitUnaryOperation(paramExpressionOwner, this))
      this.m_right.callVisitors(this, paramXPathVisitor); 
  }
  
  public Expression getExpression() { return this.m_right; }
  
  public void setExpression(Expression paramExpression) {
    paramExpression.exprSetParent(this);
    this.m_right = paramExpression;
  }
  
  public boolean deepEquals(Expression paramExpression) { return !isSameClass(paramExpression) ? false : (!!this.m_right.deepEquals(((UnaryOperation)paramExpression).m_right)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\UnaryOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */