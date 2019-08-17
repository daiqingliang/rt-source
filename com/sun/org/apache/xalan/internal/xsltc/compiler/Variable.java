package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DCONST;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Variable extends VariableBase {
  public int getIndex() { return (this._local != null) ? this._local.getIndex() : -1; }
  
  public void parseContents(Parser paramParser) {
    super.parseContents(paramParser);
    SyntaxTreeNode syntaxTreeNode = getParent();
    if (syntaxTreeNode instanceof Stylesheet) {
      this._isLocal = false;
      Variable variable = paramParser.getSymbolTable().lookupVariable(this._name);
      if (variable != null) {
        int i = getImportPrecedence();
        int j = variable.getImportPrecedence();
        if (i == j) {
          String str = this._name.toString();
          reportError(this, paramParser, "VARIABLE_REDEF_ERR", str);
        } else {
          if (j > i) {
            this._ignore = true;
            copyReferences(variable);
            return;
          } 
          variable.copyReferences(this);
          variable.disable();
        } 
      } 
      ((Stylesheet)syntaxTreeNode).addVariable(this);
      paramParser.getSymbolTable().addVariable(this);
    } else {
      this._isLocal = true;
    } 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._select != null) {
      this._type = this._select.typeCheck(paramSymbolTable);
    } else if (hasContents()) {
      typeCheckContents(paramSymbolTable);
      this._type = Type.ResultTree;
    } else {
      this._type = Type.Reference;
    } 
    return Type.Void;
  }
  
  public void initialize(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (isLocal() && !this._refs.isEmpty()) {
      if (this._local == null)
        this._local = paramMethodGenerator.addLocalVariable2(getEscapedName(), this._type.toJCType(), null); 
      if (this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType || this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType || this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType) {
        instructionList.append(new ICONST(0));
      } else if (this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType) {
        instructionList.append(new DCONST(0.0D));
      } else {
        instructionList.append(new ACONST_NULL());
      } 
      this._local.setStart(instructionList.append(this._type.STORE(this._local.getIndex())));
    } 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._refs.isEmpty())
      this._ignore = true; 
    if (this._ignore)
      return; 
    this._ignore = true;
    String str = getEscapedName();
    if (isLocal()) {
      translateValue(paramClassGenerator, paramMethodGenerator);
      boolean bool = (this._local == null) ? 1 : 0;
      if (bool)
        mapRegister(paramMethodGenerator); 
      InstructionHandle instructionHandle = instructionList.append(this._type.STORE(this._local.getIndex()));
      if (bool)
        this._local.setStart(instructionHandle); 
    } else {
      String str1 = this._type.toSignature();
      if (paramClassGenerator.containsField(str) == null) {
        paramClassGenerator.addField(new Field(1, constantPoolGen.addUtf8(str), constantPoolGen.addUtf8(str1), null, constantPoolGen.getConstantPool()));
        instructionList.append(paramClassGenerator.loadTranslet());
        translateValue(paramClassGenerator, paramMethodGenerator);
        instructionList.append(new PUTFIELD(constantPoolGen.addFieldref(paramClassGenerator.getClassName(), str, str1)));
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Variable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */