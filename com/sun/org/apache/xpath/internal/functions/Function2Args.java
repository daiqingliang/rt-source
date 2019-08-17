package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Vector;

public class Function2Args extends FunctionOneArg {
  static final long serialVersionUID = 5574294996842710641L;
  
  Expression m_arg1;
  
  public Expression getArg1() { return this.m_arg1; }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    super.fixupVariables(paramVector, paramInt);
    if (null != this.m_arg1)
      this.m_arg1.fixupVariables(paramVector, paramInt); 
  }
  
  public void setArg(Expression paramExpression, int paramInt) throws WrongNumberArgsException {
    if (paramInt == 0) {
      super.setArg(paramExpression, paramInt);
    } else if (1 == paramInt) {
      this.m_arg1 = paramExpression;
      paramExpression.exprSetParent(this);
    } else {
      reportWrongNumberArgs();
    } 
  }
  
  public void checkNumberArgs(int paramInt) throws WrongNumberArgsException {
    if (paramInt != 2)
      reportWrongNumberArgs(); 
  }
  
  protected void reportWrongNumberArgs() { throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("two", null)); }
  
  public boolean canTraverseOutsideSubtree() { return super.canTraverseOutsideSubtree() ? true : this.m_arg1.canTraverseOutsideSubtree(); }
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor) {
    super.callArgVisitors(paramXPathVisitor);
    if (null != this.m_arg1)
      this.m_arg1.callVisitors(new Arg1Owner(), paramXPathVisitor); 
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!super.deepEquals(paramExpression))
      return false; 
    if (null != this.m_arg1) {
      if (null == ((Function2Args)paramExpression).m_arg1)
        return false; 
      if (!this.m_arg1.deepEquals(((Function2Args)paramExpression).m_arg1))
        return false; 
    } else if (null != ((Function2Args)paramExpression).m_arg1) {
      return false;
    } 
    return true;
  }
  
  class Arg1Owner implements ExpressionOwner {
    public Expression getExpression() { return Function2Args.this.m_arg1; }
    
    public void setExpression(Expression param1Expression) {
      param1Expression.exprSetParent(Function2Args.this);
      Function2Args.this.m_arg1 = param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\Function2Args.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */