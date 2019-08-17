package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;

final class FunctionAvailableCall extends FunctionCall {
  private Expression _arg;
  
  private String _nameOfFunct = null;
  
  private String _namespaceOfFunct = null;
  
  private boolean _isFunctionAvailable = false;
  
  public FunctionAvailableCall(QName paramQName, Vector paramVector) {
    super(paramQName, paramVector);
    this._arg = (Expression)paramVector.elementAt(0);
    this._type = null;
    if (this._arg instanceof LiteralExpr) {
      LiteralExpr literalExpr = (LiteralExpr)this._arg;
      this._namespaceOfFunct = literalExpr.getNamespace();
      this._nameOfFunct = literalExpr.getValue();
      if (!isInternalNamespace())
        this._isFunctionAvailable = hasMethods(); 
    } 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._type != null)
      return this._type; 
    if (this._arg instanceof LiteralExpr)
      return this._type = Type.Boolean; 
    ErrorMsg errorMsg = new ErrorMsg("NEED_LITERAL_ERR", "function-available", this);
    throw new TypeCheckError(errorMsg);
  }
  
  public Object evaluateAtCompileTime() { return getResult() ? Boolean.TRUE : Boolean.FALSE; }
  
  private boolean hasMethods() {
    LiteralExpr literalExpr = (LiteralExpr)this._arg;
    String str1 = getClassNameFromUri(this._namespaceOfFunct);
    String str2 = null;
    int i = this._nameOfFunct.indexOf(":");
    if (i > 0) {
      String str = this._nameOfFunct.substring(i + 1);
      int j = str.lastIndexOf('.');
      if (j > 0) {
        str2 = str.substring(j + 1);
        if (str1 != null && !str1.equals("")) {
          str1 = str1 + "." + str.substring(0, j);
        } else {
          str1 = str.substring(0, j);
        } 
      } else {
        str2 = str;
      } 
    } else {
      str2 = this._nameOfFunct;
    } 
    if (str1 == null || str2 == null)
      return false; 
    if (str2.indexOf('-') > 0)
      str2 = replaceDash(str2); 
    try {
      Class clazz = ObjectFactory.findProviderClass(str1, true);
      if (clazz == null)
        return false; 
      Method[] arrayOfMethod = clazz.getMethods();
      for (byte b = 0; b < arrayOfMethod.length; b++) {
        int j = arrayOfMethod[b].getModifiers();
        if (Modifier.isPublic(j) && Modifier.isStatic(j) && arrayOfMethod[b].getName().equals(str2))
          return true; 
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      return false;
    } 
    return false;
  }
  
  public boolean getResult() {
    if (this._nameOfFunct == null)
      return false; 
    if (isInternalNamespace()) {
      Parser parser = getParser();
      this._isFunctionAvailable = parser.functionSupported(Util.getLocalName(this._nameOfFunct));
    } 
    return this._isFunctionAvailable;
  }
  
  private boolean isInternalNamespace() { return (this._namespaceOfFunct == null || this._namespaceOfFunct.equals("") || this._namespaceOfFunct.equals("http://xml.apache.org/xalan/xsltc")); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    paramMethodGenerator.getInstructionList().append(new PUSH(constantPoolGen, getResult()));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FunctionAvailableCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */