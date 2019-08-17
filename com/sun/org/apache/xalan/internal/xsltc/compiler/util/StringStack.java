package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.Stack;

public final class StringStack extends Stack {
  static final long serialVersionUID = -1506910875640317898L;
  
  public String peekString() { return (String)peek(); }
  
  public String popString() { return (String)pop(); }
  
  public String pushString(String paramString) { return (String)push(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\StringStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */