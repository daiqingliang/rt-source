package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Message extends Instruction {
  private boolean _terminate = false;
  
  public void parseContents(Parser paramParser) {
    String str = getAttribute("terminate");
    if (str != null)
      this._terminate = str.equals("yes"); 
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    SyntaxTreeNode syntaxTreeNode;
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(paramClassGenerator.loadTranslet());
    switch (elementCount()) {
      case 0:
        instructionList.append(new PUSH(constantPoolGen, ""));
        break;
      case 1:
        syntaxTreeNode = elementAt(0);
        if (syntaxTreeNode instanceof Text) {
          instructionList.append(new PUSH(constantPoolGen, ((Text)syntaxTreeNode).getText()));
          break;
        } 
      default:
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xml.internal.serializer.ToXMLStream")));
        instructionList.append(paramMethodGenerator.storeHandler());
        instructionList.append(new NEW(constantPoolGen.addClass("java.io.StringWriter")));
        instructionList.append(DUP);
        instructionList.append(DUP);
        instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref("java.io.StringWriter", "<init>", "()V")));
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref("com.sun.org.apache.xml.internal.serializer.ToXMLStream", "<init>", "()V")));
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(SWAP);
        instructionList.append(new INVOKEINTERFACE(constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setWriter", "(Ljava/io/Writer;)V"), 2));
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(new PUSH(constantPoolGen, "UTF-8"));
        instructionList.append(new INVOKEINTERFACE(constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setEncoding", "(Ljava/lang/String;)V"), 2));
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(ICONST_1);
        instructionList.append(new INVOKEINTERFACE(constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setOmitXMLDeclaration", "(Z)V"), 2));
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(new INVOKEINTERFACE(constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V"), 1));
        translateContents(paramClassGenerator, paramMethodGenerator);
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(new INVOKEINTERFACE(constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V"), 1));
        instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.io.StringWriter", "toString", "()Ljava/lang/String;")));
        instructionList.append(SWAP);
        instructionList.append(paramMethodGenerator.storeHandler());
        break;
    } 
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "displayMessage", "(Ljava/lang/String;)V")));
    if (this._terminate == true) {
      int i = constantPoolGen.addMethodref("java.lang.RuntimeException", "<init>", "(Ljava/lang/String;)V");
      instructionList.append(new NEW(constantPoolGen.addClass("java.lang.RuntimeException")));
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, "Termination forced by an xsl:message instruction"));
      instructionList.append(new INVOKESPECIAL(i));
      instructionList.append(ATHROW);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */