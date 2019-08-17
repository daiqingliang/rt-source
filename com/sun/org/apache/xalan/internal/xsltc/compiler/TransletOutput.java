package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class TransletOutput extends Instruction {
  private Expression _filename;
  
  private boolean _append;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("TransletOutput: " + this._filename);
  }
  
  public void parseContents(Parser paramParser) {
    String str1 = getAttribute("file");
    String str2 = getAttribute("append");
    if (str1 == null || str1.equals(""))
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "file"); 
    this._filename = AttributeValue.create(this, str1, paramParser);
    if (str2 != null && (str2.toLowerCase().equals("yes") || str2.toLowerCase().equals("true"))) {
      this._append = true;
    } else {
      this._append = false;
    } 
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type = this._filename.typeCheck(paramSymbolTable);
    if (!(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType))
      this._filename = new CastExpr(this._filename, Type.String); 
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    boolean bool = paramClassGenerator.getParser().getXSLTC().isSecureProcessing();
    if (bool) {
      int k = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unallowed_extension_elementF", "(Ljava/lang/String;)V");
      instructionList.append(new PUSH(constantPoolGen, "redirect"));
      instructionList.append(new INVOKESTATIC(k));
      return;
    } 
    instructionList.append(paramMethodGenerator.loadHandler());
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "openOutputHandler", "(Ljava/lang/String;Z)Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "closeOutputHandler", "(Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    instructionList.append(paramClassGenerator.loadTranslet());
    this._filename.translate(paramClassGenerator, paramMethodGenerator);
    instructionList.append(new PUSH(constantPoolGen, this._append));
    instructionList.append(new INVOKEVIRTUAL(i));
    instructionList.append(paramMethodGenerator.storeHandler());
    translateContents(paramClassGenerator, paramMethodGenerator);
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(paramMethodGenerator.loadHandler());
    instructionList.append(new INVOKEVIRTUAL(j));
    instructionList.append(paramMethodGenerator.storeHandler());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\TransletOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */