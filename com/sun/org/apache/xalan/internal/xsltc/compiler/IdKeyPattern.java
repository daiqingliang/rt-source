package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

abstract class IdKeyPattern extends LocationPathPattern {
  protected RelativePathPattern _left = null;
  
  private String _index = null;
  
  private String _value = null;
  
  public IdKeyPattern(String paramString1, String paramString2) {
    this._index = paramString1;
    this._value = paramString2;
  }
  
  public String getIndexName() { return this._index; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return Type.NodeSet; }
  
  public boolean isWildcard() { return false; }
  
  public void setLeft(RelativePathPattern paramRelativePathPattern) { this._left = paramRelativePathPattern; }
  
  public StepPattern getKernelPattern() { return null; }
  
  public void reduceKernelPattern() {}
  
  public String toString() { return "id/keyPattern(" + this._index + ", " + this._value + ')'; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex;");
    int j = constantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "containsID", "(ILjava/lang/Object;)I");
    int k = constantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "containsKey", "(ILjava/lang/Object;)I");
    int m = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new PUSH(constantPoolGen, this._index));
    instructionList.append(new INVOKEVIRTUAL(i));
    instructionList.append(SWAP);
    instructionList.append(new PUSH(constantPoolGen, this._value));
    if (this instanceof IdPattern) {
      instructionList.append(new INVOKEVIRTUAL(j));
    } else {
      instructionList.append(new INVOKEVIRTUAL(k));
    } 
    this._trueList.add(instructionList.append(new IFNE(null)));
    this._falseList.add(instructionList.append(new GOTO(null)));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\IdKeyPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */