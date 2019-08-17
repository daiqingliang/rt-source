package com.sun.org.apache.xpath.internal;

import javax.xml.transform.SourceLocator;

public interface ExpressionNode extends SourceLocator {
  void exprSetParent(ExpressionNode paramExpressionNode);
  
  ExpressionNode exprGetParent();
  
  void exprAddChild(ExpressionNode paramExpressionNode, int paramInt);
  
  ExpressionNode exprGetChild(int paramInt);
  
  int exprGetNumChildren();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\ExpressionNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */