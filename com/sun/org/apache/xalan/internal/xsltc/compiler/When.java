package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class When extends Instruction {
  private Expression _test;
  
  private boolean _ignore = false;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("When");
    indent(paramInt + 4);
    System.out.print("test ");
    Util.println(this._test.toString());
    displayContents(paramInt + 4);
  }
  
  public Expression getTest() { return this._test; }
  
  public boolean ignore() { return this._ignore; }
  
  public void parseContents(Parser paramParser) {
    this._test = paramParser.parseExpression(this, "test", null);
    Object object = this._test.evaluateAtCompileTime();
    if (object != null && object instanceof Boolean)
      this._ignore = !((Boolean)object).booleanValue(); 
    parseChildren(paramParser);
    if (this._test.isDummy())
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "test"); 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (!(this._test.typeCheck(paramSymbolTable) instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType))
      this._test = new CastExpr(this._test, Type.Boolean); 
    if (!this._ignore)
      typeCheckContents(paramSymbolTable); 
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ErrorMsg errorMsg = new ErrorMsg("STRAY_WHEN_ERR", this);
    getParser().reportError(3, errorMsg);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\When.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */