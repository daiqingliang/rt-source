package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class UnionPattern extends Expression {
  static final long serialVersionUID = -6670449967116905820L;
  
  private StepPattern[] m_patterns;
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    for (byte b = 0; b < this.m_patterns.length; b++)
      this.m_patterns[b].fixupVariables(paramVector, paramInt); 
  }
  
  public boolean canTraverseOutsideSubtree() {
    if (null != this.m_patterns) {
      int i = this.m_patterns.length;
      for (byte b = 0; b < i; b++) {
        if (this.m_patterns[b].canTraverseOutsideSubtree())
          return true; 
      } 
    } 
    return false;
  }
  
  public void setPatterns(StepPattern[] paramArrayOfStepPattern) {
    this.m_patterns = paramArrayOfStepPattern;
    if (null != paramArrayOfStepPattern)
      for (byte b = 0; b < paramArrayOfStepPattern.length; b++)
        paramArrayOfStepPattern[b].exprSetParent(this);  
  }
  
  public StepPattern[] getPatterns() { return this.m_patterns; }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    XObject xObject = null;
    int i = this.m_patterns.length;
    for (byte b = 0; b < i; b++) {
      XObject xObject1 = this.m_patterns[b].execute(paramXPathContext);
      if (xObject1 != NodeTest.SCORE_NONE)
        if (null == xObject) {
          xObject = xObject1;
        } else if (xObject1.num() > xObject.num()) {
          xObject = xObject1;
        }  
    } 
    if (null == xObject)
      xObject = NodeTest.SCORE_NONE; 
    return xObject;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) {
    paramXPathVisitor.visitUnionPattern(paramExpressionOwner, this);
    if (null != this.m_patterns) {
      int i = this.m_patterns.length;
      for (byte b = 0; b < i; b++)
        this.m_patterns[b].callVisitors(new UnionPathPartOwner(b), paramXPathVisitor); 
    } 
  }
  
  public boolean deepEquals(Expression paramExpression) {
    if (!isSameClass(paramExpression))
      return false; 
    UnionPattern unionPattern = (UnionPattern)paramExpression;
    if (null != this.m_patterns) {
      int i = this.m_patterns.length;
      if (null == unionPattern.m_patterns || unionPattern.m_patterns.length != i)
        return false; 
      for (byte b = 0; b < i; b++) {
        if (!this.m_patterns[b].deepEquals(unionPattern.m_patterns[b]))
          return false; 
      } 
    } else if (unionPattern.m_patterns != null) {
      return false;
    } 
    return true;
  }
  
  class UnionPathPartOwner implements ExpressionOwner {
    int m_index;
    
    UnionPathPartOwner(int param1Int) { this.m_index = param1Int; }
    
    public Expression getExpression() { return UnionPattern.this.m_patterns[this.m_index]; }
    
    public void setExpression(Expression param1Expression) {
      param1Expression.exprSetParent(UnionPattern.this);
      UnionPattern.this.m_patterns[this.m_index] = (StepPattern)param1Expression;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\patterns\UnionPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */