package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class FloorCall extends FunctionCall {
  public FloorCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    argument().translate(paramClassGenerator, paramMethodGenerator);
    paramMethodGenerator.getInstructionList().append(new INVOKESTATIC(paramClassGenerator.getConstantPool().addMethodref("java.lang.Math", "floor", "(D)D")));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FloorCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */