package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.functions.Function;

public class HasPositionalPredChecker extends XPathVisitor {
  private boolean m_hasPositionalPred = false;
  
  private int m_predDepth = 0;
  
  public static boolean check(LocPathIterator paramLocPathIterator) {
    HasPositionalPredChecker hasPositionalPredChecker = new HasPositionalPredChecker();
    paramLocPathIterator.callVisitors(null, hasPositionalPredChecker);
    return hasPositionalPredChecker.m_hasPositionalPred;
  }
  
  public boolean visitFunction(ExpressionOwner paramExpressionOwner, Function paramFunction) {
    if (paramFunction instanceof com.sun.org.apache.xpath.internal.functions.FuncPosition || paramFunction instanceof com.sun.org.apache.xpath.internal.functions.FuncLast)
      this.m_hasPositionalPred = true; 
    return true;
  }
  
  public boolean visitPredicate(ExpressionOwner paramExpressionOwner, Expression paramExpression) {
    this.m_predDepth++;
    if (this.m_predDepth == 1)
      if (paramExpression instanceof com.sun.org.apache.xpath.internal.operations.Variable || paramExpression instanceof com.sun.org.apache.xpath.internal.objects.XNumber || paramExpression instanceof com.sun.org.apache.xpath.internal.operations.Div || paramExpression instanceof com.sun.org.apache.xpath.internal.operations.Plus || paramExpression instanceof com.sun.org.apache.xpath.internal.operations.Minus || paramExpression instanceof com.sun.org.apache.xpath.internal.operations.Mod || paramExpression instanceof com.sun.org.apache.xpath.internal.operations.Quo || paramExpression instanceof com.sun.org.apache.xpath.internal.operations.Mult || paramExpression instanceof com.sun.org.apache.xpath.internal.operations.Number || paramExpression instanceof Function) {
        this.m_hasPositionalPred = true;
      } else {
        paramExpression.callVisitors(paramExpressionOwner, this);
      }  
    this.m_predDepth--;
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\HasPositionalPredChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */