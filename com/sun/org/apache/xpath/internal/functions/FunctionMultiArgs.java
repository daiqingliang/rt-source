package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Vector;

public class FunctionMultiArgs extends Function3Args {
  static final long serialVersionUID = 7117257746138417181L;
  
  Expression[] m_args;
  
  public Expression[] getArgs() { return this.m_args; }
  
  public void setArg(Expression paramExpression, int paramInt) throws WrongNumberArgsException {
    if (paramInt < 3) {
      super.setArg(paramExpression, paramInt);
    } else {
      if (null == this.m_args) {
        this.m_args = new Expression[1];
        this.m_args[0] = paramExpression;
      } else {
        Expression[] arrayOfExpression = new Expression[this.m_args.length + 1];
        System.arraycopy(this.m_args, 0, arrayOfExpression, 0, this.m_args.length);
        arrayOfExpression[this.m_args.length] = paramExpression;
        this.m_args = arrayOfExpression;
      } 
      paramExpression.exprSetParent(this);
    } 
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    super.fixupVariables(paramVector, paramInt);
    if (null != this.m_args)
      for (byte b = 0; b < this.m_args.length; b++)
        this.m_args[b].fixupVariables(paramVector, paramInt);  
  }
  
  public void checkNumberArgs(int paramInt) throws WrongNumberArgsException {}
  
  protected void reportWrongNumberArgs() {
    String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { "Programmer's assertion:  the method FunctionMultiArgs.reportWrongNumberArgs() should never be called." });
    throw new RuntimeException(str);
  }
  
  public boolean canTraverseOutsideSubtree() {
    if (super.canTraverseOutsideSubtree())
      return true; 
    int i = this.m_args.length;
    for (byte b = 0; b < i; b++) {
      if (this.m_args[b].canTraverseOutsideSubtree())
        return true; 
    } 
    return false;
  }
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor) {
    super.callArgVisitors(paramXPathVisitor);
    if (null != this.m_args) {
      int i = this.m_args.length;
      for (byte b = 0; b < i; b++)
        this.m_args[b].callVisitors(new ArgMultiOwner(b), paramXPathVisitor); 
    } 
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!super.deepEquals(paramExpression))
      return false; 
    FunctionMultiArgs functionMultiArgs = (FunctionMultiArgs)paramExpression;
    if (null != this.m_args) {
      int i = this.m_args.length;
      if (null == functionMultiArgs || functionMultiArgs.m_args.length != i)
        return false; 
      for (byte b = 0; b < i; b++) {
        if (!this.m_args[b].deepEquals(functionMultiArgs.m_args[b]))
          return false; 
      } 
    } else if (null != functionMultiArgs.m_args) {
      return false;
    } 
    return true;
  }
  
  class ArgMultiOwner implements ExpressionOwner {
    int m_argIndex;
    
    ArgMultiOwner(int param1Int) { this.m_argIndex = param1Int; }
    
    public Expression getExpression() { return FunctionMultiArgs.this.m_args[this.m_argIndex]; }
    
    public void setExpression(Expression param1Expression) {
      param1Expression.exprSetParent(FunctionMultiArgs.this);
      FunctionMultiArgs.this.m_args[this.m_argIndex] = param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FunctionMultiArgs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */