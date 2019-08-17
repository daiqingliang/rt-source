package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class ApplyImports extends Instruction {
  private QName _modeName;
  
  private int _precedence;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("ApplyTemplates");
    indent(paramInt + 4);
    if (this._modeName != null) {
      indent(paramInt + 4);
      Util.println("mode " + this._modeName);
    } 
  }
  
  public boolean hasWithParams() { return hasContents(); }
  
  private int getMinPrecedence(int paramInt) {
    Stylesheet stylesheet;
    for (stylesheet = getStylesheet(); stylesheet._includedFrom != null; stylesheet = stylesheet._includedFrom);
    return stylesheet.getMinimumDescendantPrecedence();
  }
  
  public void parseContents(Parser paramParser) {
    Stylesheet stylesheet = getStylesheet();
    stylesheet.setTemplateInlining(false);
    Template template = getTemplate();
    this._modeName = template.getModeName();
    this._precedence = template.getImportPrecedence();
    stylesheet = paramParser.getTopLevelStylesheet();
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    Stylesheet stylesheet = paramClassGenerator.getStylesheet();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = paramMethodGenerator.getLocalIndex("current");
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(paramMethodGenerator.loadIterator());
    instructionList.append(paramMethodGenerator.loadHandler());
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    if (stylesheet.hasLocalParams()) {
      instructionList.append(paramClassGenerator.loadTranslet());
      int n = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
      instructionList.append(new INVOKEVIRTUAL(n));
    } 
    int j = this._precedence;
    int k = getMinPrecedence(j);
    Mode mode = stylesheet.getMode(this._modeName);
    String str1 = mode.functionName(k, j);
    String str2 = paramClassGenerator.getStylesheet().getClassName();
    String str3 = paramClassGenerator.getApplyTemplatesSigForImport();
    int m = constantPoolGen.addMethodref(str2, str1, str3);
    instructionList.append(new INVOKEVIRTUAL(m));
    if (stylesheet.hasLocalParams()) {
      instructionList.append(paramClassGenerator.loadTranslet());
      int n = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
      instructionList.append(new INVOKEVIRTUAL(n));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ApplyImports.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */