package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNull;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FuncExtFunction extends Function {
  static final long serialVersionUID = 5196115554693708718L;
  
  String m_namespace;
  
  String m_extensionName;
  
  Object m_methodKey;
  
  Vector m_argVec = new Vector();
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    if (null != this.m_argVec) {
      int i = this.m_argVec.size();
      for (byte b = 0; b < i; b++) {
        Expression expression = (Expression)this.m_argVec.elementAt(b);
        expression.fixupVariables(paramVector, paramInt);
      } 
    } 
  }
  
  public String getNamespace() { return this.m_namespace; }
  
  public String getFunctionName() { return this.m_extensionName; }
  
  public Object getMethodKey() { return this.m_methodKey; }
  
  public Expression getArg(int paramInt) { return (paramInt >= 0 && paramInt < this.m_argVec.size()) ? (Expression)this.m_argVec.elementAt(paramInt) : null; }
  
  public int getArgCount() { return this.m_argVec.size(); }
  
  public FuncExtFunction(String paramString1, String paramString2, Object paramObject) {
    this.m_namespace = paramString1;
    this.m_extensionName = paramString2;
    this.m_methodKey = paramObject;
  }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    XNull xNull;
    if (paramXPathContext.isSecureProcessing())
      throw new TransformerException(XPATHMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { toString() })); 
    Vector vector = new Vector();
    int i = this.m_argVec.size();
    for (byte b = 0; b < i; b++) {
      Expression expression = (Expression)this.m_argVec.elementAt(b);
      XObject xObject = expression.execute(paramXPathContext);
      xObject.allowDetachToRelease(false);
      vector.addElement(xObject);
    } 
    ExtensionsProvider extensionsProvider = (ExtensionsProvider)paramXPathContext.getOwnerObject();
    Object object = extensionsProvider.extFunction(this, vector);
    if (null != object) {
      xNull = XObject.create(object, paramXPathContext);
    } else {
      xNull = new XNull();
    } 
    return xNull;
  }
  
  public void setArg(Expression paramExpression, int paramInt) throws WrongNumberArgsException {
    this.m_argVec.addElement(paramExpression);
    paramExpression.exprSetParent(this);
  }
  
  public void checkNumberArgs(int paramInt) throws WrongNumberArgsException {}
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor) {
    for (byte b = 0; b < this.m_argVec.size(); b++) {
      Expression expression = (Expression)this.m_argVec.elementAt(b);
      expression.callVisitors(new ArgExtOwner(expression), paramXPathVisitor);
    } 
  }
  
  public void exprSetParent(ExpressionNode paramExpressionNode) {
    super.exprSetParent(paramExpressionNode);
    int i = this.m_argVec.size();
    for (byte b = 0; b < i; b++) {
      Expression expression = (Expression)this.m_argVec.elementAt(b);
      expression.exprSetParent(paramExpressionNode);
    } 
  }
  
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
    String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { "Programmer's assertion:  the method FunctionMultiArgs.reportWrongNumberArgs() should never be called." });
    throw new RuntimeException(str);
  }
  
  public String toString() { return (this.m_namespace != null && this.m_namespace.length() > 0) ? ("{" + this.m_namespace + "}" + this.m_extensionName) : this.m_extensionName; }
  
  class ArgExtOwner implements ExpressionOwner {
    Expression m_exp;
    
    ArgExtOwner(Expression param1Expression) { this.m_exp = param1Expression; }
    
    public Expression getExpression() { return this.m_exp; }
    
    public void setExpression(Expression param1Expression) {
      param1Expression.exprSetParent(FuncExtFunction.this);
      this.m_exp = param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncExtFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */