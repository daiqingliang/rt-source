package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Objects;

class VariableRefBase extends Expression {
  protected VariableBase _variable;
  
  protected Closure _closure = null;
  
  public VariableRefBase(VariableBase paramVariableBase) {
    this._variable = paramVariableBase;
    paramVariableBase.addReference(this);
  }
  
  public VariableRefBase() { this._variable = null; }
  
  public VariableBase getVariable() { return this._variable; }
  
  public void addParentDependency() {
    SyntaxTreeNode syntaxTreeNode = this;
    while (syntaxTreeNode != null && !(syntaxTreeNode instanceof TopLevelElement))
      syntaxTreeNode = syntaxTreeNode.getParent(); 
    TopLevelElement topLevelElement = (TopLevelElement)syntaxTreeNode;
    if (topLevelElement != null) {
      VariableBase variableBase = this._variable;
      if (this._variable._ignore)
        if (this._variable instanceof Variable) {
          variableBase = topLevelElement.getSymbolTable().lookupVariable(this._variable._name);
        } else if (this._variable instanceof Param) {
          variableBase = topLevelElement.getSymbolTable().lookupParam(this._variable._name);
        }  
      topLevelElement.addDependency(variableBase);
    } 
  }
  
  public boolean equals(Object paramObject) { return (paramObject == this || (paramObject instanceof VariableRefBase && this._variable == ((VariableRefBase)paramObject)._variable)); }
  
  public int hashCode() { return Objects.hashCode(this._variable); }
  
  public String toString() { return "variable-ref(" + this._variable.getName() + '/' + this._variable.getType() + ')'; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._type != null)
      return this._type; 
    if (this._variable.isLocal()) {
      SyntaxTreeNode syntaxTreeNode = getParent();
      do {
        if (syntaxTreeNode instanceof Closure) {
          this._closure = (Closure)syntaxTreeNode;
          break;
        } 
        if (syntaxTreeNode instanceof TopLevelElement)
          break; 
        syntaxTreeNode = syntaxTreeNode.getParent();
      } while (syntaxTreeNode != null);
      if (this._closure != null)
        this._closure.addVariable(this); 
    } 
    this._type = this._variable.getType();
    if (this._type == null) {
      this._variable.typeCheck(paramSymbolTable);
      this._type = this._variable.getType();
    } 
    addParentDependency();
    return this._type;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\VariableRefBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */