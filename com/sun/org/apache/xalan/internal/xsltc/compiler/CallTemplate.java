package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.Vector;

final class CallTemplate extends Instruction {
  private QName _name;
  
  private SyntaxTreeNode[] _parameters = null;
  
  private Template _calleeTemplate = null;
  
  public void display(int paramInt) {
    indent(paramInt);
    System.out.print("CallTemplate");
    Util.println(" name " + this._name);
    displayContents(paramInt + 4);
  }
  
  public boolean hasWithParams() { return (elementCount() > 0); }
  
  public void parseContents(Parser paramParser) {
    String str = getAttribute("name");
    if (str.length() > 0) {
      if (!XML11Char.isXML11ValidQName(str)) {
        ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str, this);
        paramParser.reportError(3, errorMsg);
      } 
      this._name = paramParser.getQNameIgnoreDefaultNs(str);
    } else {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "name");
    } 
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Template template = paramSymbolTable.lookupTemplate(this._name);
    if (template != null) {
      typeCheckContents(paramSymbolTable);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("TEMPLATE_UNDEF_ERR", this._name, this);
      throw new TypeCheckError(errorMsg);
    } 
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    Stylesheet stylesheet = paramClassGenerator.getStylesheet();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (stylesheet.hasLocalParams() || hasContents()) {
      this._calleeTemplate = getCalleeTemplate();
      if (this._calleeTemplate != null) {
        buildParameterList();
      } else {
        int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
        instructionList.append(paramClassGenerator.loadTranslet());
        instructionList.append(new INVOKEVIRTUAL(i));
        translateContents(paramClassGenerator, paramMethodGenerator);
      } 
    } 
    String str1 = stylesheet.getClassName();
    String str2 = Util.escape(this._name.toString());
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(paramMethodGenerator.loadIterator());
    instructionList.append(paramMethodGenerator.loadHandler());
    instructionList.append(paramMethodGenerator.loadCurrentNode());
    StringBuffer stringBuffer = new StringBuffer("(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I");
    if (this._calleeTemplate != null) {
      int i = this._parameters.length;
      for (byte b = 0; b < i; b++) {
        SyntaxTreeNode syntaxTreeNode = this._parameters[b];
        stringBuffer.append("Ljava/lang/Object;");
        if (syntaxTreeNode instanceof Param) {
          instructionList.append(ACONST_NULL);
        } else {
          syntaxTreeNode.translate(paramClassGenerator, paramMethodGenerator);
        } 
      } 
    } 
    stringBuffer.append(")V");
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref(str1, str2, stringBuffer.toString())));
    if (this._parameters != null)
      for (byte b = 0; b < this._parameters.length; b++) {
        if (this._parameters[b] instanceof WithParam)
          ((WithParam)this._parameters[b]).releaseResultTree(paramClassGenerator, paramMethodGenerator); 
      }  
    if (this._calleeTemplate == null && (stylesheet.hasLocalParams() || hasContents())) {
      int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(new INVOKEVIRTUAL(i));
    } 
  }
  
  public Template getCalleeTemplate() {
    Template template = getXSLTC().getParser().getSymbolTable().lookupTemplate(this._name);
    return template.isSimpleNamedTemplate() ? template : null;
  }
  
  private void buildParameterList() {
    Vector vector = this._calleeTemplate.getParameters();
    int i = vector.size();
    this._parameters = new SyntaxTreeNode[i];
    int j;
    for (j = 0; j < i; j++)
      this._parameters[j] = (SyntaxTreeNode)vector.elementAt(j); 
    j = elementCount();
    for (byte b = 0; b < j; b++) {
      SyntaxTreeNode syntaxTreeNode = elementAt(b);
      if (syntaxTreeNode instanceof WithParam) {
        WithParam withParam = (WithParam)syntaxTreeNode;
        QName qName = withParam.getName();
        for (byte b1 = 0; b1 < i; b1++) {
          SyntaxTreeNode syntaxTreeNode1 = this._parameters[b1];
          if (syntaxTreeNode1 instanceof Param && ((Param)syntaxTreeNode1).getName().equals(qName)) {
            withParam.setDoParameterOptimization(true);
            this._parameters[b1] = withParam;
            break;
          } 
          if (syntaxTreeNode1 instanceof WithParam && ((WithParam)syntaxTreeNode1).getName().equals(qName)) {
            withParam.setDoParameterOptimization(true);
            this._parameters[b1] = withParam;
            break;
          } 
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CallTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */