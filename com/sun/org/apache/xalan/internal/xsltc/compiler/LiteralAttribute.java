package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;

final class LiteralAttribute extends Instruction {
  private final String _name;
  
  private final AttributeValue _value;
  
  public LiteralAttribute(String paramString1, String paramString2, Parser paramParser, SyntaxTreeNode paramSyntaxTreeNode) {
    this._name = paramString1;
    setParent(paramSyntaxTreeNode);
    this._value = AttributeValue.create(this, paramString2, paramParser);
  }
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("LiteralAttribute name=" + this._name + " value=" + this._value);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._value.typeCheck(paramSymbolTable);
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  protected boolean contextDependent() { return this._value.contextDependent(); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(paramMethodGenerator.loadHandler());
    instructionList.append(new PUSH(constantPoolGen, this._name));
    this._value.translate(paramClassGenerator, paramMethodGenerator);
    SyntaxTreeNode syntaxTreeNode = getParent();
    if (syntaxTreeNode instanceof LiteralElement && ((LiteralElement)syntaxTreeNode).allAttributesUnique()) {
      byte b = 0;
      boolean bool = false;
      ElemDesc elemDesc = ((LiteralElement)syntaxTreeNode).getElemDesc();
      if (elemDesc != null)
        if (elemDesc.isAttrFlagSet(this._name, 4)) {
          b |= 0x2;
          bool = true;
        } else if (elemDesc.isAttrFlagSet(this._name, 2)) {
          b |= 0x4;
        }  
      if (this._value instanceof SimpleAttributeValue) {
        String str = ((SimpleAttributeValue)this._value).toString();
        if (!hasBadChars(str) && !bool)
          b |= 0x1; 
      } 
      instructionList.append(new PUSH(constantPoolGen, b));
      instructionList.append(paramMethodGenerator.uniqueAttribute());
    } else {
      instructionList.append(paramMethodGenerator.attribute());
    } 
  }
  
  private boolean hasBadChars(String paramString) {
    for (char c : paramString.toCharArray()) {
      if (c < ' ' || '~' < c || c == '<' || c == '>' || c == '&' || c == '"')
        return true; 
    } 
    return false;
  }
  
  public String getName() { return this._name; }
  
  public AttributeValue getValue() { return this._value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LiteralAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */