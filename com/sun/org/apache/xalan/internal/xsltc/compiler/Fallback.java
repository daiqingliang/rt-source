package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Fallback extends Instruction {
  private boolean _active = false;
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return this._active ? typeCheckContents(paramSymbolTable) : Type.Void; }
  
  public void activate() { this._active = true; }
  
  public String toString() { return "fallback"; }
  
  public void parseContents(Parser paramParser) {
    if (this._active)
      parseChildren(paramParser); 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._active)
      translateContents(paramClassGenerator, paramMethodGenerator); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Fallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */