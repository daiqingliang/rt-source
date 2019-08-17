package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.Vector;

class VariableBase extends TopLevelElement {
  protected QName _name;
  
  protected String _escapedName;
  
  protected Type _type;
  
  protected boolean _isLocal;
  
  protected LocalVariableGen _local;
  
  protected Instruction _loadInstruction;
  
  protected Instruction _storeInstruction;
  
  protected Expression _select;
  
  protected String select;
  
  protected Vector<VariableRefBase> _refs = new Vector(2);
  
  protected boolean _ignore = false;
  
  public void disable() { this._ignore = true; }
  
  public void addReference(VariableRefBase paramVariableRefBase) { this._refs.addElement(paramVariableRefBase); }
  
  public void copyReferences(VariableBase paramVariableBase) {
    int i = this._refs.size();
    for (byte b = 0; b < i; b++)
      paramVariableBase.addReference((VariableRefBase)this._refs.get(b)); 
  }
  
  public void mapRegister(MethodGenerator paramMethodGenerator) {
    if (this._local == null) {
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      String str = getEscapedName();
      Type type = this._type.toJCType();
      this._local = paramMethodGenerator.addLocalVariable2(str, type, instructionList.getEnd());
    } 
  }
  
  public void unmapRegister(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (this._local != null) {
      if (this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
        ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
        InstructionList instructionList = paramMethodGenerator.getInstructionList();
        if (paramClassGenerator.getStylesheet().callsNodeset() && paramClassGenerator.getDOMClass().equals("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")) {
          int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "removeDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;)V");
          instructionList.append(paramMethodGenerator.loadDOM());
          instructionList.append(new CHECKCAST(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")));
          instructionList.append(loadInstruction());
          instructionList.append(new CHECKCAST(constantPoolGen.addClass("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter")));
          instructionList.append(new INVOKEVIRTUAL(j));
        } 
        int i = constantPoolGen.addInterfaceMethodref("com/sun/org/apache/xalan/internal/xsltc/DOM", "release", "()V");
        instructionList.append(loadInstruction());
        instructionList.append(new INVOKEINTERFACE(i, 1));
      } 
      this._local.setEnd(paramMethodGenerator.getInstructionList().getEnd());
      paramMethodGenerator.removeLocalVariable(this._local);
      this._refs = null;
      this._local = null;
    } 
  }
  
  public Instruction loadInstruction() {
    if (this._loadInstruction == null)
      this._loadInstruction = this._type.LOAD(this._local.getIndex()); 
    return this._loadInstruction;
  }
  
  public Instruction storeInstruction() {
    if (this._storeInstruction == null)
      this._storeInstruction = this._type.STORE(this._local.getIndex()); 
    return this._storeInstruction;
  }
  
  public Expression getExpression() { return this._select; }
  
  public String toString() { return "variable(" + this._name + ")"; }
  
  public void display(int paramInt) {
    indent(paramInt);
    System.out.println("Variable " + this._name);
    if (this._select != null) {
      indent(paramInt + 4);
      System.out.println("select " + this._select.toString());
    } 
    displayContents(paramInt + 4);
  }
  
  public Type getType() { return this._type; }
  
  public QName getName() { return this._name; }
  
  public String getEscapedName() { return this._escapedName; }
  
  public void setName(QName paramQName) {
    this._name = paramQName;
    this._escapedName = Util.escape(paramQName.getStringRep());
  }
  
  public boolean isLocal() { return this._isLocal; }
  
  public void parseContents(Parser paramParser) {
    String str = getAttribute("name");
    if (str.length() > 0) {
      if (!XML11Char.isXML11ValidQName(str)) {
        ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str, this);
        paramParser.reportError(3, errorMsg);
      } 
      setName(paramParser.getQNameIgnoreDefaultNs(str));
    } else {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "name");
    } 
    VariableBase variableBase = paramParser.lookupVariable(this._name);
    if (variableBase != null && variableBase.getParent() == getParent())
      reportError(this, paramParser, "VARIABLE_REDEF_ERR", str); 
    this.select = getAttribute("select");
    if (this.select.length() > 0) {
      this._select = getParser().parseExpression(this, "select", null);
      if (this._select.isDummy()) {
        reportError(this, paramParser, "REQUIRED_ATTR_ERR", "select");
        return;
      } 
    } 
    parseChildren(paramParser);
  }
  
  public void translateValue(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (this._select != null) {
      this._select.translate(paramClassGenerator, paramMethodGenerator);
      if (this._select.getType() instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType) {
        ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
        InstructionList instructionList = paramMethodGenerator.getInstructionList();
        int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CachedNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
        instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CachedNodeListIterator")));
        instructionList.append(DUP_X1);
        instructionList.append(SWAP);
        instructionList.append(new INVOKESPECIAL(i));
      } 
      this._select.startIterator(paramClassGenerator, paramMethodGenerator);
    } else if (hasContents()) {
      compileResultTree(paramClassGenerator, paramMethodGenerator);
    } else {
      ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      instructionList.append(new PUSH(constantPoolGen, ""));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\VariableBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */