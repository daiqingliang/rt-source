package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Vector;

class TopLevelElement extends SyntaxTreeNode {
  protected Vector _dependencies = null;
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return typeCheckContents(paramSymbolTable); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ErrorMsg errorMsg = new ErrorMsg("NOT_IMPLEMENTED_ERR", getClass(), this);
    getParser().reportError(2, errorMsg);
  }
  
  public InstructionList compile(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    InstructionList instructionList2 = paramMethodGenerator.getInstructionList();
    InstructionList instructionList1;
    paramMethodGenerator.setInstructionList(instructionList1 = new InstructionList());
    translate(paramClassGenerator, paramMethodGenerator);
    paramMethodGenerator.setInstructionList(instructionList2);
    return instructionList1;
  }
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("TopLevelElement");
    displayContents(paramInt + 4);
  }
  
  public void addDependency(TopLevelElement paramTopLevelElement) {
    if (this._dependencies == null)
      this._dependencies = new Vector(); 
    if (!this._dependencies.contains(paramTopLevelElement))
      this._dependencies.addElement(paramTopLevelElement); 
  }
  
  public Vector getDependencies() { return this._dependencies; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\TopLevelElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */