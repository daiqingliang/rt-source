package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;

final class Param extends VariableBase {
  private boolean _isInSimpleNamedTemplate = false;
  
  public String toString() { return "param(" + this._name + ")"; }
  
  public Instruction setLoadInstruction(Instruction paramInstruction) {
    Instruction instruction = this._loadInstruction;
    this._loadInstruction = paramInstruction;
    return instruction;
  }
  
  public Instruction setStoreInstruction(Instruction paramInstruction) {
    Instruction instruction = this._storeInstruction;
    this._storeInstruction = paramInstruction;
    return instruction;
  }
  
  public void display(int paramInt) {
    indent(paramInt);
    System.out.println("param " + this._name);
    if (this._select != null) {
      indent(paramInt + 4);
      System.out.println("select " + this._select.toString());
    } 
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser) {
    super.parseContents(paramParser);
    SyntaxTreeNode syntaxTreeNode = getParent();
    if (syntaxTreeNode instanceof Stylesheet) {
      this._isLocal = false;
      Param param = paramParser.getSymbolTable().lookupParam(this._name);
      if (param != null) {
        int i = getImportPrecedence();
        int j = param.getImportPrecedence();
        if (i == j) {
          String str = this._name.toString();
          reportError(this, paramParser, "VARIABLE_REDEF_ERR", str);
        } else {
          if (j > i) {
            this._ignore = true;
            copyReferences(param);
            return;
          } 
          param.copyReferences(this);
          param.disable();
        } 
      } 
      ((Stylesheet)syntaxTreeNode).addParam(this);
      paramParser.getSymbolTable().addParam(this);
    } else if (syntaxTreeNode instanceof Template) {
      Template template = (Template)syntaxTreeNode;
      this._isLocal = true;
      template.addParameter(this);
      if (template.isSimpleNamedTemplate())
        this._isInSimpleNamedTemplate = true; 
    } 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._select != null) {
      this._type = this._select.typeCheck(paramSymbolTable);
      if (!(this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType) && !(this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType))
        this._select = new CastExpr(this._select, Type.Reference); 
    } else if (hasContents()) {
      typeCheckContents(paramSymbolTable);
    } 
    this._type = Type.Reference;
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._ignore)
      return; 
    this._ignore = true;
    String str1 = BasisLibrary.mapQNameToJavaName(this._name.toString());
    String str2 = this._type.toSignature();
    String str3 = this._type.getClassName();
    if (isLocal()) {
      if (this._isInSimpleNamedTemplate) {
        instructionList.append(loadInstruction());
        BranchHandle branchHandle = instructionList.append(new IFNONNULL(null));
        translateValue(paramClassGenerator, paramMethodGenerator);
        instructionList.append(storeInstruction());
        branchHandle.setTarget(instructionList.append(NOP));
        return;
      } 
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(new PUSH(constantPoolGen, str1));
      translateValue(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new PUSH(constantPoolGen, true));
      instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addParameter", "(Ljava/lang/String;Ljava/lang/Object;Z)Ljava/lang/Object;")));
      if (str3 != "")
        instructionList.append(new CHECKCAST(constantPoolGen.addClass(str3))); 
      this._type.translateUnBox(paramClassGenerator, paramMethodGenerator);
      if (this._refs.isEmpty()) {
        instructionList.append(this._type.POP());
        this._local = null;
      } else {
        this._local = paramMethodGenerator.addLocalVariable2(str1, this._type.toJCType(), instructionList.getEnd());
        instructionList.append(this._type.STORE(this._local.getIndex()));
      } 
    } else if (paramClassGenerator.containsField(str1) == null) {
      paramClassGenerator.addField(new Field(1, constantPoolGen.addUtf8(str1), constantPoolGen.addUtf8(str2), null, constantPoolGen.getConstantPool()));
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, str1));
      translateValue(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new PUSH(constantPoolGen, true));
      instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addParameter", "(Ljava/lang/String;Ljava/lang/Object;Z)Ljava/lang/Object;")));
      this._type.translateUnBox(paramClassGenerator, paramMethodGenerator);
      if (str3 != "")
        instructionList.append(new CHECKCAST(constantPoolGen.addClass(str3))); 
      instructionList.append(new PUTFIELD(constantPoolGen.addFieldref(paramClassGenerator.getClassName(), str1, str2)));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Param.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */