package com.sun.org.apache.xalan.internal.xsltc.compiler;

final class ArgumentList {
  private final Expression _arg;
  
  private final ArgumentList _rest;
  
  public ArgumentList(Expression paramExpression, ArgumentList paramArgumentList) {
    this._arg = paramExpression;
    this._rest = paramArgumentList;
  }
  
  public String toString() { return (this._rest == null) ? this._arg.toString() : (this._arg.toString() + ", " + this._rest.toString()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ArgumentList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */