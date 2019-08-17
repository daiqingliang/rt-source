package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.Serializable;
import java.util.Vector;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class Expression implements Serializable, ExpressionNode, XPathVisitable {
  static final long serialVersionUID = 565665869777906902L;
  
  private ExpressionNode m_parent;
  
  public boolean canTraverseOutsideSubtree() { return false; }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt) throws TransformerException { return execute(paramXPathContext); }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt1, DTM paramDTM, int paramInt2) throws TransformerException { return execute(paramXPathContext); }
  
  public abstract XObject execute(XPathContext paramXPathContext) throws TransformerException;
  
  public XObject execute(XPathContext paramXPathContext, boolean paramBoolean) throws TransformerException { return execute(paramXPathContext); }
  
  public double num(XPathContext paramXPathContext) throws TransformerException { return execute(paramXPathContext).num(); }
  
  public boolean bool(XPathContext paramXPathContext) throws TransformerException { return execute(paramXPathContext).bool(); }
  
  public XMLString xstr(XPathContext paramXPathContext) throws TransformerException { return execute(paramXPathContext).xstr(); }
  
  public boolean isNodesetExpr() { return false; }
  
  public int asNode(XPathContext paramXPathContext) throws TransformerException {
    DTMIterator dTMIterator = execute(paramXPathContext).iter();
    return dTMIterator.nextNode();
  }
  
  public DTMIterator asIterator(XPathContext paramXPathContext, int paramInt) throws TransformerException {
    try {
      paramXPathContext.pushCurrentNodeAndExpression(paramInt, paramInt);
      return execute(paramXPathContext).iter();
    } finally {
      paramXPathContext.popCurrentNodeAndExpression();
    } 
  }
  
  public DTMIterator asIteratorRaw(XPathContext paramXPathContext, int paramInt) throws TransformerException {
    try {
      paramXPathContext.pushCurrentNodeAndExpression(paramInt, paramInt);
      XNodeSet xNodeSet = (XNodeSet)execute(paramXPathContext);
      return xNodeSet.iterRaw();
    } finally {
      paramXPathContext.popCurrentNodeAndExpression();
    } 
  }
  
  public void executeCharsToContentHandler(XPathContext paramXPathContext, ContentHandler paramContentHandler) throws TransformerException, SAXException {
    XObject xObject = execute(paramXPathContext);
    xObject.dispatchCharactersEvents(paramContentHandler);
    xObject.detach();
  }
  
  public boolean isStableNumber() { return false; }
  
  public abstract void fixupVariables(Vector paramVector, int paramInt);
  
  public abstract boolean deepEquals(Expression paramExpression);
  
  protected final boolean isSameClass(Expression paramExpression) { return (null == paramExpression) ? false : ((getClass() == paramExpression.getClass())); }
  
  public void warn(XPathContext paramXPathContext, String paramString, Object[] paramArrayOfObject) throws TransformerException {
    String str = XSLMessages.createXPATHWarning(paramString, paramArrayOfObject);
    if (null != paramXPathContext) {
      ErrorListener errorListener = paramXPathContext.getErrorListener();
      errorListener.warning(new TransformerException(str, paramXPathContext.getSAXLocator()));
    } 
  }
  
  public void assertion(boolean paramBoolean, String paramString) {
    if (!paramBoolean) {
      String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { paramString });
      throw new RuntimeException(str);
    } 
  }
  
  public void error(XPathContext paramXPathContext, String paramString, Object[] paramArrayOfObject) throws TransformerException {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    if (null != paramXPathContext) {
      ErrorListener errorListener = paramXPathContext.getErrorListener();
      TransformerException transformerException = new TransformerException(str, this);
      errorListener.fatalError(transformerException);
    } 
  }
  
  public ExpressionNode getExpressionOwner() {
    ExpressionNode expressionNode;
    for (expressionNode = exprGetParent(); null != expressionNode && expressionNode instanceof Expression; expressionNode = expressionNode.exprGetParent());
    return expressionNode;
  }
  
  public void exprSetParent(ExpressionNode paramExpressionNode) {
    assertion((paramExpressionNode != this), "Can not parent an expression to itself!");
    this.m_parent = paramExpressionNode;
  }
  
  public ExpressionNode exprGetParent() { return this.m_parent; }
  
  public void exprAddChild(ExpressionNode paramExpressionNode, int paramInt) { assertion(false, "exprAddChild method not implemented!"); }
  
  public ExpressionNode exprGetChild(int paramInt) { return null; }
  
  public int exprGetNumChildren() { return 0; }
  
  public String getPublicId() { return (null == this.m_parent) ? null : this.m_parent.getPublicId(); }
  
  public String getSystemId() { return (null == this.m_parent) ? null : this.m_parent.getSystemId(); }
  
  public int getLineNumber() { return (null == this.m_parent) ? 0 : this.m_parent.getLineNumber(); }
  
  public int getColumnNumber() { return (null == this.m_parent) ? 0 : this.m_parent.getColumnNumber(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\Expression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */