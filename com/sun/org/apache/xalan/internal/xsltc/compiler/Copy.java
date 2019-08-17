package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Copy extends Instruction {
  private UseAttributeSets _useSets;
  
  public void parseContents(Parser paramParser) {
    String str = getAttribute("use-attribute-sets");
    if (str.length() > 0) {
      if (!Util.isValidQNames(str)) {
        ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str, this);
        paramParser.reportError(3, errorMsg);
      } 
      this._useSets = new UseAttributeSets(str, paramParser);
    } 
    parseChildren(paramParser);
  }
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("Copy");
    indent(paramInt + 4);
    displayContents(paramInt + 4);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._useSets != null)
      this._useSets.typeCheck(paramSymbolTable); 
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable2("name", Util.getJCRefType("Ljava/lang/String;"), null);
    LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable2("length", Util.getJCRefType("I"), null);
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    instructionList.append(paramMethodGenerator.loadHandler());
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "shallowCopy", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)Ljava/lang/String;");
    instructionList.append(new INVOKEINTERFACE(i, 3));
    instructionList.append(DUP);
    localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
    BranchHandle branchHandle1 = instructionList.append(new IFNULL(null));
    instructionList.append(new ALOAD(localVariableGen1.getIndex()));
    int j = constantPoolGen.addMethodref("java.lang.String", "length", "()I");
    instructionList.append(new INVOKEVIRTUAL(j));
    instructionList.append(DUP);
    localVariableGen2.setStart(instructionList.append(new ISTORE(localVariableGen2.getIndex())));
    BranchHandle branchHandle2 = instructionList.append(new IFEQ(null));
    if (this._useSets != null) {
      SyntaxTreeNode syntaxTreeNode = getParent();
      if (syntaxTreeNode instanceof LiteralElement || syntaxTreeNode instanceof LiteralElement) {
        this._useSets.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        instructionList.append(new ILOAD(localVariableGen2.getIndex()));
        BranchHandle branchHandle = instructionList.append(new IFEQ(null));
        this._useSets.translate(paramClassGenerator, paramMethodGenerator);
        branchHandle.setTarget(instructionList.append(NOP));
      } 
    } 
    branchHandle2.setTarget(instructionList.append(NOP));
    translateContents(paramClassGenerator, paramMethodGenerator);
    localVariableGen2.setEnd(instructionList.append(new ILOAD(localVariableGen2.getIndex())));
    BranchHandle branchHandle3 = instructionList.append(new IFEQ(null));
    instructionList.append(paramMethodGenerator.loadHandler());
    localVariableGen1.setEnd(instructionList.append(new ALOAD(localVariableGen1.getIndex())));
    instructionList.append(paramMethodGenerator.endElement());
    InstructionHandle instructionHandle = instructionList.append(NOP);
    branchHandle1.setTarget(instructionHandle);
    branchHandle3.setTarget(instructionHandle);
    paramMethodGenerator.removeLocalVariable(localVariableGen1);
    paramMethodGenerator.removeLocalVariable(localVariableGen2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Copy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */