package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public abstract class Function extends Expression {
  static final long serialVersionUID = 6927661240854599768L;
  
  public void setArg(Expression paramExpression, int paramInt) throws WrongNumberArgsException { reportWrongNumberArgs(); }
  
  public void checkNumberArgs(int paramInt) throws WrongNumberArgsException {
    if (paramInt != 0)
      reportWrongNumberArgs(); 
  }
  
  protected void reportWrongNumberArgs() { throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("zero", null)); }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    System.out.println("Error! Function.execute should not be called!");
    return null;
  }
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor) {}
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) {
    if (paramXPathVisitor.visitFunction(paramExpressionOwner, this))
      callArgVisitors(paramXPathVisitor); 
  }
  
  public boolean deepEquals(Expression paramExpression) { return !!isSameClass(paramExpression); }
  
  public void postCompileStep(Compiler paramCompiler) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\Function.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */