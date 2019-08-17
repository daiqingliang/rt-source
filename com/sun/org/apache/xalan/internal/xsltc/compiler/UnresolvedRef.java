package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class UnresolvedRef extends VariableRefBase {
  private QName _variableName = null;
  
  private VariableRefBase _ref = null;
  
  public UnresolvedRef(QName paramQName) { this._variableName = paramQName; }
  
  public QName getName() { return this._variableName; }
  
  private ErrorMsg reportError() {
    ErrorMsg errorMsg = new ErrorMsg("VARIABLE_UNDEF_ERR", this._variableName, this);
    getParser().reportError(3, errorMsg);
    return errorMsg;
  }
  
  private VariableRefBase resolve(Parser paramParser, SymbolTable paramSymbolTable) {
    VariableBase variableBase = paramParser.lookupVariable(this._variableName);
    if (variableBase == null)
      variableBase = (VariableBase)paramSymbolTable.lookupName(this._variableName); 
    if (variableBase == null) {
      reportError();
      return null;
    } 
    this._variable = variableBase;
    addParentDependency();
    return (variableBase instanceof Variable) ? new VariableRef((Variable)variableBase) : ((variableBase instanceof Param) ? new ParameterRef((Param)variableBase) : null);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._ref != null) {
      String str = this._variableName.toString();
      ErrorMsg errorMsg = new ErrorMsg("CIRCULAR_VARIABLE_ERR", str, this);
    } 
    if ((this._ref = resolve(getParser(), paramSymbolTable)) != null)
      return this._type = this._ref.typeCheck(paramSymbolTable); 
    throw new TypeCheckError(reportError());
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (this._ref != null) {
      this._ref.translate(paramClassGenerator, paramMethodGenerator);
    } else {
      reportError();
    } 
  }
  
  public String toString() { return "unresolved-ref()"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UnresolvedRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */