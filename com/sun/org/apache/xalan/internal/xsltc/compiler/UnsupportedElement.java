package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.List;
import java.util.Vector;

final class UnsupportedElement extends SyntaxTreeNode {
  private Vector _fallbacks = null;
  
  private ErrorMsg _message = null;
  
  private boolean _isExtension = false;
  
  public UnsupportedElement(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    super(paramString1, paramString2, paramString3);
    this._isExtension = paramBoolean;
  }
  
  public void setErrorMessage(ErrorMsg paramErrorMsg) { this._message = paramErrorMsg; }
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("Unsupported element = " + this._qname.getNamespace() + ":" + this._qname.getLocalPart());
    displayContents(paramInt + 4);
  }
  
  private void processFallbacks(Parser paramParser) {
    List list = getContents();
    if (list != null) {
      int i = list.size();
      for (byte b = 0; b < i; b++) {
        SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)list.get(b);
        if (syntaxTreeNode instanceof Fallback) {
          Fallback fallback = (Fallback)syntaxTreeNode;
          fallback.activate();
          fallback.parseContents(paramParser);
          if (this._fallbacks == null)
            this._fallbacks = new Vector(); 
          this._fallbacks.addElement(syntaxTreeNode);
        } 
      } 
    } 
  }
  
  public void parseContents(Parser paramParser) { processFallbacks(paramParser); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._fallbacks != null) {
      int i = this._fallbacks.size();
      for (byte b = 0; b < i; b++) {
        Fallback fallback = (Fallback)this._fallbacks.elementAt(b);
        fallback.typeCheck(paramSymbolTable);
      } 
    } 
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (this._fallbacks != null) {
      int i = this._fallbacks.size();
      for (byte b = 0; b < i; b++) {
        Fallback fallback = (Fallback)this._fallbacks.elementAt(b);
        fallback.translate(paramClassGenerator, paramMethodGenerator);
      } 
    } else {
      ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unsupported_ElementF", "(Ljava/lang/String;Z)V");
      instructionList.append(new PUSH(constantPoolGen, getQName().toString()));
      instructionList.append(new PUSH(constantPoolGen, this._isExtension));
      instructionList.append(new INVOKESTATIC(i));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UnsupportedElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */