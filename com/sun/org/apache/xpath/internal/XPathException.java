package com.sun.org.apache.xpath.internal;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;

public class XPathException extends TransformerException {
  static final long serialVersionUID = 4263549717619045963L;
  
  Object m_styleNode = null;
  
  protected Exception m_exception;
  
  public Object getStylesheetNode() { return this.m_styleNode; }
  
  public void setStylesheetNode(Object paramObject) { this.m_styleNode = paramObject; }
  
  public XPathException(String paramString, ExpressionNode paramExpressionNode) {
    super(paramString);
    setLocator(paramExpressionNode);
    setStylesheetNode(getStylesheetNode(paramExpressionNode));
  }
  
  public XPathException(String paramString) { super(paramString); }
  
  public Node getStylesheetNode(ExpressionNode paramExpressionNode) {
    ExpressionNode expressionNode = getExpressionOwner(paramExpressionNode);
    return (null != expressionNode && expressionNode instanceof Node) ? (Node)expressionNode : null;
  }
  
  protected ExpressionNode getExpressionOwner(ExpressionNode paramExpressionNode) {
    ExpressionNode expressionNode;
    for (expressionNode = paramExpressionNode.exprGetParent(); null != expressionNode && expressionNode instanceof Expression; expressionNode = expressionNode.exprGetParent());
    return expressionNode;
  }
  
  public XPathException(String paramString, Object paramObject) {
    super(paramString);
    this.m_styleNode = paramObject;
  }
  
  public XPathException(String paramString, Node paramNode, Exception paramException) {
    super(paramString);
    this.m_styleNode = paramNode;
    this.m_exception = paramException;
  }
  
  public XPathException(String paramString, Exception paramException) {
    super(paramString);
    this.m_exception = paramException;
  }
  
  public void printStackTrace(PrintStream paramPrintStream) {
    if (paramPrintStream == null)
      paramPrintStream = System.err; 
    try {
      super.printStackTrace(paramPrintStream);
    } catch (Exception exception1) {}
    Exception exception = this.m_exception;
    for (byte b = 0; b < 10 && null != exception; b++) {
      paramPrintStream.println("---------");
      exception.printStackTrace(paramPrintStream);
      if (exception instanceof TransformerException) {
        TransformerException transformerException = (TransformerException)exception;
        Exception exception1 = exception;
        Throwable throwable = transformerException.getException();
        if (exception1 == throwable)
          break; 
      } else {
        exception = null;
      } 
    } 
  }
  
  public String getMessage() {
    String str = super.getMessage();
    for (Exception exception = this.m_exception; null != exception; exception = null) {
      String str1 = exception.getMessage();
      if (null != str1)
        str = str1; 
      if (exception instanceof TransformerException) {
        TransformerException transformerException = (TransformerException)exception;
        Exception exception1 = exception;
        Throwable throwable = transformerException.getException();
        if (exception1 == throwable)
          break; 
        continue;
      } 
    } 
    return (null != str) ? str : "";
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter) {
    if (paramPrintWriter == null)
      paramPrintWriter = new PrintWriter(System.err); 
    try {
      super.printStackTrace(paramPrintWriter);
    } catch (Exception exception) {}
    boolean bool = false;
    try {
      Throwable.class.getMethod("getCause", (Class[])null);
      bool = true;
    } catch (NoSuchMethodException noSuchMethodException) {}
    if (!bool) {
      Exception exception = this.m_exception;
      for (byte b = 0; b < 10 && null != exception; b++) {
        paramPrintWriter.println("---------");
        try {
          exception.printStackTrace(paramPrintWriter);
        } catch (Exception exception1) {
          paramPrintWriter.println("Could not print stack trace...");
        } 
        if (exception instanceof TransformerException) {
          TransformerException transformerException = (TransformerException)exception;
          Exception exception1 = exception;
          Throwable throwable = transformerException.getException();
          if (exception1 == throwable) {
            throwable = null;
            break;
          } 
        } else {
          exception = null;
        } 
      } 
    } 
  }
  
  public Throwable getException() { return this.m_exception; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\XPathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */