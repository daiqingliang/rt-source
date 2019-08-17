package com.sun.org.apache.xalan.internal.xsltc.compiler;

public final class CompilerException extends Exception {
  static final long serialVersionUID = 1732939618562742663L;
  
  private String _msg;
  
  public CompilerException() {}
  
  public CompilerException(Exception paramException) {
    super(paramException.toString());
    this._msg = paramException.toString();
  }
  
  public CompilerException(String paramString) {
    super(paramString);
    this._msg = paramString;
  }
  
  public String getMessage() {
    int i = this._msg.indexOf(':');
    return (i > -1) ? this._msg.substring(i) : this._msg;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CompilerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */