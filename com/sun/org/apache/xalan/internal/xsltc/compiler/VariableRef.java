package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;

final class VariableRef extends VariableRefBase {
  public VariableRef(Variable paramVariable) { super(paramVariable); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._type.implementedAsMethod())
      return; 
    String str1 = this._variable.getEscapedName();
    String str2 = this._type.toSignature();
    if (this._variable.isLocal()) {
      if (paramClassGenerator.isExternal()) {
        Closure closure;
        for (closure = this._closure; closure != null && !closure.inInnerClass(); closure = closure.getParentClosure());
        if (closure != null) {
          instructionList.append(ALOAD_0);
          instructionList.append(new GETFIELD(constantPoolGen.addFieldref(closure.getInnerClassName(), str1, str2)));
        } else {
          instructionList.append(this._variable.loadInstruction());
        } 
      } else {
        instructionList.append(this._variable.loadInstruction());
      } 
    } else {
      String str = paramClassGenerator.getClassName();
      instructionList.append(paramClassGenerator.loadTranslet());
      if (paramClassGenerator.isExternal())
        instructionList.append(new CHECKCAST(constantPoolGen.addClass(str))); 
      instructionList.append(new GETFIELD(constantPoolGen.addFieldref(str, str1, str2)));
    } 
    if (this._variable.getType() instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType) {
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "cloneIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(new INVOKEINTERFACE(i, 1));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\VariableRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */