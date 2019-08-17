package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Comment extends Instruction {
  public void parseContents(Parser paramParser) { parseChildren(paramParser); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    typeCheckContents(paramSymbolTable);
    return Type.String;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    Text text = null;
    if (elementCount() == 1) {
      SyntaxTreeNode syntaxTreeNode = elementAt(0);
      if (syntaxTreeNode instanceof Text)
        text = (Text)syntaxTreeNode; 
    } 
    if (text != null) {
      instructionList.append(paramMethodGenerator.loadHandler());
      if (text.canLoadAsArrayOffsetLength()) {
        text.loadAsArrayOffsetLength(paramClassGenerator, paramMethodGenerator);
        int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "([CII)V");
        instructionList.append(new INVOKEINTERFACE(i, 4));
      } else {
        instructionList.append(new PUSH(constantPoolGen, text.getText()));
        int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "(Ljava/lang/String;)V");
        instructionList.append(new INVOKEINTERFACE(i, 2));
      } 
    } else {
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(DUP);
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;")));
      instructionList.append(DUP);
      instructionList.append(paramMethodGenerator.storeHandler());
      translateContents(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;")));
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "(Ljava/lang/String;)V");
      instructionList.append(new INVOKEINTERFACE(i, 2));
      instructionList.append(paramMethodGenerator.storeHandler());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Comment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */