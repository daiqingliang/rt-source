package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.axes.PathComponent;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class Variable extends Expression implements PathComponent {
  static final long serialVersionUID = -4334975375609297049L;
  
  private boolean m_fixUpWasCalled = false;
  
  protected QName m_qname;
  
  protected int m_index;
  
  protected boolean m_isGlobal = false;
  
  static final java.lang.String PSUEDOVARNAMESPACE = "http://xml.apache.org/xalan/psuedovar";
  
  public void setIndex(int paramInt) { this.m_index = paramInt; }
  
  public int getIndex() { return this.m_index; }
  
  public void setIsGlobal(boolean paramBoolean) { this.m_isGlobal = paramBoolean; }
  
  public boolean getGlobal() { return this.m_isGlobal; }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    this.m_fixUpWasCalled = true;
    int i = paramVector.size();
    for (int j = paramVector.size() - 1; j >= 0; j--) {
      QName qName = (QName)paramVector.elementAt(j);
      if (qName.equals(this.m_qname)) {
        if (j < paramInt) {
          this.m_isGlobal = true;
          this.m_index = j;
        } else {
          this.m_index = j - paramInt;
        } 
        return;
      } 
    } 
    java.lang.String str = XSLMessages.createXPATHMessage("ER_COULD_NOT_FIND_VAR", new Object[] { this.m_qname.toString() });
    TransformerException transformerException = new TransformerException(str, this);
    throw new WrappedRuntimeException(transformerException);
  }
  
  public void setQName(QName paramQName) { this.m_qname = paramQName; }
  
  public QName getQName() { return this.m_qname; }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException { return execute(paramXPathContext, false); }
  
  public XObject execute(XPathContext paramXPathContext, boolean paramBoolean) throws TransformerException {
    XObject xObject;
    PrefixResolver prefixResolver = paramXPathContext.getNamespaceContext();
    if (this.m_fixUpWasCalled) {
      if (this.m_isGlobal) {
        xObject = paramXPathContext.getVarStack().getGlobalVariable(paramXPathContext, this.m_index, paramBoolean);
      } else {
        xObject = paramXPathContext.getVarStack().getLocalVariable(paramXPathContext, this.m_index, paramBoolean);
      } 
    } else {
      xObject = paramXPathContext.getVarStack().getVariableOrParam(paramXPathContext, this.m_qname);
    } 
    if (null == xObject) {
      warn(paramXPathContext, "WG_ILLEGAL_VARIABLE_REFERENCE", new Object[] { this.m_qname.getLocalPart() });
      xObject = new XNodeSet(paramXPathContext.getDTMManager());
    } 
    return xObject;
  }
  
  public boolean isStableNumber() { return true; }
  
  public int getAnalysisBits() { return 67108864; }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) { paramXPathVisitor.visitVariableRef(paramExpressionOwner, this); }
  
  public boolean deepEquals(Expression paramExpression) { return !isSameClass(paramExpression) ? false : (!!this.m_qname.equals(((Variable)paramExpression).m_qname)); }
  
  public boolean isPsuedoVarRef() {
    java.lang.String str = this.m_qname.getNamespaceURI();
    return (null != str && str.equals("http://xml.apache.org/xalan/psuedovar") && this.m_qname.getLocalName().startsWith("#"));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\operations\Variable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */