package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xpath.internal.axes.LocPathIterator;
import com.sun.org.apache.xpath.internal.axes.UnionPathIterator;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.operations.Operation;
import com.sun.org.apache.xpath.internal.operations.UnaryOperation;
import com.sun.org.apache.xpath.internal.operations.Variable;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import com.sun.org.apache.xpath.internal.patterns.UnionPattern;

public class XPathVisitor {
  public boolean visitLocationPath(ExpressionOwner paramExpressionOwner, LocPathIterator paramLocPathIterator) { return true; }
  
  public boolean visitUnionPath(ExpressionOwner paramExpressionOwner, UnionPathIterator paramUnionPathIterator) { return true; }
  
  public boolean visitStep(ExpressionOwner paramExpressionOwner, NodeTest paramNodeTest) { return true; }
  
  public boolean visitPredicate(ExpressionOwner paramExpressionOwner, Expression paramExpression) { return true; }
  
  public boolean visitBinaryOperation(ExpressionOwner paramExpressionOwner, Operation paramOperation) { return true; }
  
  public boolean visitUnaryOperation(ExpressionOwner paramExpressionOwner, UnaryOperation paramUnaryOperation) { return true; }
  
  public boolean visitVariableRef(ExpressionOwner paramExpressionOwner, Variable paramVariable) { return true; }
  
  public boolean visitFunction(ExpressionOwner paramExpressionOwner, Function paramFunction) { return true; }
  
  public boolean visitMatchPattern(ExpressionOwner paramExpressionOwner, StepPattern paramStepPattern) { return true; }
  
  public boolean visitUnionPattern(ExpressionOwner paramExpressionOwner, UnionPattern paramUnionPattern) { return true; }
  
  public boolean visitStringLiteral(ExpressionOwner paramExpressionOwner, XString paramXString) { return true; }
  
  public boolean visitNumberLiteral(ExpressionOwner paramExpressionOwner, XNumber paramXNumber) { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\XPathVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */