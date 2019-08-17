package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Vector;

public class FunctionOneArg extends Function implements ExpressionOwner {
  static final long serialVersionUID = -5180174180765609758L;
  
  Expression m_arg0;
  
  public Expression getArg0() { return this.m_arg0; }
  
  public void setArg(Expression paramExpression, int paramInt) throws WrongNumberArgsException {
    if (0 == paramInt) {
      this.m_arg0 = paramExpression;
      paramExpression.exprSetParent(this);
    } else {
      reportWrongNumberArgs();
    } 
  }
  
  public void checkNumberArgs(int paramInt) throws WrongNumberArgsException {
    if (paramInt != 1)
      reportWrongNumberArgs(); 
  }
  
  protected void reportWrongNumberArgs() { throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("one", null)); }
  
  public boolean canTraverseOutsideSubtree() { return this.m_arg0.canTraverseOutsideSubtree(); }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    if (null != this.m_arg0)
      this.m_arg0.fixupVariables(paramVector, paramInt); 
  }
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor) {
    if (null != this.m_arg0)
      this.m_arg0.callVisitors(this, paramXPathVisitor); 
  }
  
  public Expression getExpression() { return this.m_arg0; }
  
  public void setExpression(Expression paramExpression) {
    paramExpression.exprSetParent(this);
    this.m_arg0 = paramExpression;
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!super.deepEquals(paramExpression))
      return false; 
    if (null != this.m_arg0) {
      if (null == ((FunctionOneArg)paramExpression).m_arg0)
        return false; 
      if (!this.m_arg0.deepEquals(((FunctionOneArg)paramExpression).m_arg0))
        return false; 
    } else if (null != ((FunctionOneArg)paramExpression).m_arg0) {
      return false;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FunctionOneArg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */