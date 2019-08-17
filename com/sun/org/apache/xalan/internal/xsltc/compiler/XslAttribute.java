package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.List;

final class XslAttribute extends Instruction {
  private String _prefix;
  
  private AttributeValue _name;
  
  private AttributeValueTemplate _namespace = null;
  
  private boolean _ignore = false;
  
  private boolean _isLiteral = false;
  
  public AttributeValue getName() { return this._name; }
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("Attribute " + this._name);
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser) {
    boolean bool = false;
    SymbolTable symbolTable = paramParser.getSymbolTable();
    String str1 = getAttribute("name");
    String str2 = getAttribute("namespace");
    QName qName = paramParser.getQName(str1, false);
    String str3 = qName.getPrefix();
    if ((str3 != null && str3.equals("xmlns")) || str1.equals("xmlns")) {
      reportError(this, paramParser, "ILLEGAL_ATTR_NAME_ERR", str1);
      return;
    } 
    this._isLiteral = Util.isLiteral(str1);
    if (this._isLiteral && !XML11Char.isXML11ValidQName(str1)) {
      reportError(this, paramParser, "ILLEGAL_ATTR_NAME_ERR", str1);
      return;
    } 
    SyntaxTreeNode syntaxTreeNode = getParent();
    List list = syntaxTreeNode.getContents();
    for (byte b = 0; b < syntaxTreeNode.elementCount(); b++) {
      SyntaxTreeNode syntaxTreeNode1 = (SyntaxTreeNode)list.get(b);
      if (syntaxTreeNode1 == this)
        break; 
      if (!(syntaxTreeNode1 instanceof XslAttribute) && !(syntaxTreeNode1 instanceof UseAttributeSets) && !(syntaxTreeNode1 instanceof LiteralAttribute) && !(syntaxTreeNode1 instanceof Text) && !(syntaxTreeNode1 instanceof If) && !(syntaxTreeNode1 instanceof Choose) && !(syntaxTreeNode1 instanceof CopyOf) && !(syntaxTreeNode1 instanceof VariableBase))
        reportWarning(this, paramParser, "STRAY_ATTRIBUTE_ERR", str1); 
    } 
    if (str2 != null && str2 != "") {
      this._prefix = lookupPrefix(str2);
      this._namespace = new AttributeValueTemplate(str2, paramParser, this);
    } else if (str3 != null && str3 != "") {
      this._prefix = str3;
      str2 = lookupNamespace(str3);
      if (str2 != null)
        this._namespace = new AttributeValueTemplate(str2, paramParser, this); 
    } 
    if (this._namespace != null) {
      if (this._prefix == null || this._prefix == "") {
        if (str3 != null) {
          this._prefix = str3;
        } else {
          this._prefix = symbolTable.generateNamespacePrefix();
          bool = true;
        } 
      } else if (str3 != null && !str3.equals(this._prefix)) {
        this._prefix = str3;
      } 
      str1 = this._prefix + ":" + qName.getLocalPart();
      if (syntaxTreeNode instanceof LiteralElement && !bool)
        ((LiteralElement)syntaxTreeNode).registerNamespace(this._prefix, str2, symbolTable, false); 
    } 
    if (syntaxTreeNode instanceof LiteralElement)
      ((LiteralElement)syntaxTreeNode).addAttribute(this); 
    this._name = AttributeValue.create(this, str1, paramParser);
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (!this._ignore) {
      this._name.typeCheck(paramSymbolTable);
      if (this._namespace != null)
        this._namespace.typeCheck(paramSymbolTable); 
      typeCheckContents(paramSymbolTable);
    } 
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._ignore)
      return; 
    this._ignore = true;
    if (this._namespace != null) {
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(new PUSH(constantPoolGen, this._prefix));
      this._namespace.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(paramMethodGenerator.namespace());
    } 
    if (!this._isLiteral) {
      LocalVariableGen localVariableGen = paramMethodGenerator.addLocalVariable2("nameValue", Util.getJCRefType("Ljava/lang/String;"), null);
      this._name.translate(paramClassGenerator, paramMethodGenerator);
      localVariableGen.setStart(instructionList.append(new ASTORE(localVariableGen.getIndex())));
      instructionList.append(new ALOAD(localVariableGen.getIndex()));
      int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "checkAttribQName", "(Ljava/lang/String;)V");
      instructionList.append(new INVOKESTATIC(i));
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(DUP);
      localVariableGen.setEnd(instructionList.append(new ALOAD(localVariableGen.getIndex())));
    } else {
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(DUP);
      this._name.translate(paramClassGenerator, paramMethodGenerator);
    } 
    if (elementCount() == 1 && elementAt(0) instanceof Text) {
      instructionList.append(new PUSH(constantPoolGen, ((Text)elementAt(0)).getText()));
    } else {
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;")));
      instructionList.append(DUP);
      instructionList.append(paramMethodGenerator.storeHandler());
      translateContents(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;")));
    } 
    SyntaxTreeNode syntaxTreeNode = getParent();
    if (syntaxTreeNode instanceof LiteralElement && ((LiteralElement)syntaxTreeNode).allAttributesUnique()) {
      byte b = 0;
      ElemDesc elemDesc = ((LiteralElement)syntaxTreeNode).getElemDesc();
      if (elemDesc != null && this._name instanceof SimpleAttributeValue) {
        String str = ((SimpleAttributeValue)this._name).toString();
        if (elemDesc.isAttrFlagSet(str, 4)) {
          b |= 0x2;
        } else if (elemDesc.isAttrFlagSet(str, 2)) {
          b |= 0x4;
        } 
      } 
      instructionList.append(new PUSH(constantPoolGen, b));
      instructionList.append(paramMethodGenerator.uniqueAttribute());
    } else {
      instructionList.append(paramMethodGenerator.attribute());
    } 
    instructionList.append(paramMethodGenerator.storeHandler());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\XslAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */