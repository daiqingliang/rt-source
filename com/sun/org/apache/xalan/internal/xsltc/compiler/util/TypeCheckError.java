package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;

public class TypeCheckError extends Exception {
  static final long serialVersionUID = 3246224233917854640L;
  
  ErrorMsg _error = null;
  
  SyntaxTreeNode _node = null;
  
  public TypeCheckError(SyntaxTreeNode paramSyntaxTreeNode) { this._node = paramSyntaxTreeNode; }
  
  public TypeCheckError(ErrorMsg paramErrorMsg) { this._error = paramErrorMsg; }
  
  public TypeCheckError(String paramString, Object paramObject) { this._error = new ErrorMsg(paramString, paramObject); }
  
  public TypeCheckError(String paramString, Object paramObject1, Object paramObject2) { this._error = new ErrorMsg(paramString, paramObject1, paramObject2); }
  
  public ErrorMsg getErrorMsg() { return this._error; }
  
  public String getMessage() { return toString(); }
  
  public String toString() {
    if (this._error == null)
      if (this._node != null) {
        this._error = new ErrorMsg("TYPE_CHECK_ERR", this._node.toString());
      } else {
        this._error = new ErrorMsg("TYPE_CHECK_UNK_LOC_ERR");
      }  
    return this._error.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\TypeCheckError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */