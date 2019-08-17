package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class ConcatCall extends FunctionCall {
  public ConcatCall(QName paramQName, Vector paramVector) { super(paramQName, paramVector); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    for (byte b = 0; b < argumentCount(); b++) {
      Expression expression = argument(b);
      if (!expression.typeCheck(paramSymbolTable).identicalTo(Type.String))
        setArgument(b, new CastExpr(expression, Type.String)); 
    } 
    return this._type = Type.String;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = argumentCount();
    switch (i) {
      case 0:
        instructionList.append(new PUSH(constantPoolGen, ""));
        return;
      case 1:
        argument().translate(paramClassGenerator, paramMethodGenerator);
        return;
    } 
    int j = constantPoolGen.addMethodref("java.lang.StringBuffer", "<init>", "()V");
    INVOKEVIRTUAL iNVOKEVIRTUAL = new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.lang.StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;"));
    int k = constantPoolGen.addMethodref("java.lang.StringBuffer", "toString", "()Ljava/lang/String;");
    instructionList.append(new NEW(constantPoolGen.addClass("java.lang.StringBuffer")));
    instructionList.append(DUP);
    instructionList.append(new INVOKESPECIAL(j));
    for (byte b = 0; b < i; b++) {
      argument(b).translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(iNVOKEVIRTUAL);
    } 
    instructionList.append(new INVOKEVIRTUAL(k));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ConcatCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */