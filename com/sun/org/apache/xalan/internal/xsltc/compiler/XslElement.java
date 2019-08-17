package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class XslElement extends Instruction {
  private String _prefix;
  
  private boolean _ignore = false;
  
  private boolean _isLiteralName = true;
  
  private AttributeValueTemplate _name;
  
  private AttributeValueTemplate _namespace;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("Element " + this._name);
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser) {
    SymbolTable symbolTable = paramParser.getSymbolTable();
    String str1 = getAttribute("name");
    if (str1 == "") {
      ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_ELEM_NAME_ERR", str1, this);
      paramParser.reportError(4, errorMsg);
      parseChildren(paramParser);
      this._ignore = true;
      return;
    } 
    String str2 = getAttribute("namespace");
    this._isLiteralName = Util.isLiteral(str1);
    if (this._isLiteralName) {
      if (!XML11Char.isXML11ValidQName(str1)) {
        ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_ELEM_NAME_ERR", str1, this);
        paramParser.reportError(4, errorMsg);
        parseChildren(paramParser);
        this._ignore = true;
        return;
      } 
      QName qName = paramParser.getQNameSafe(str1);
      String str4 = qName.getPrefix();
      String str5 = qName.getLocalPart();
      if (str4 == null)
        str4 = ""; 
      if (!hasAttribute("namespace")) {
        str2 = lookupNamespace(str4);
        if (str2 == null) {
          ErrorMsg errorMsg = new ErrorMsg("NAMESPACE_UNDEF_ERR", str4, this);
          paramParser.reportError(4, errorMsg);
          parseChildren(paramParser);
          this._ignore = true;
          return;
        } 
        this._prefix = str4;
        this._namespace = new AttributeValueTemplate(str2, paramParser, this);
      } else {
        if (str4 == "") {
          if (Util.isLiteral(str2)) {
            str4 = lookupPrefix(str2);
            if (str4 == null)
              str4 = symbolTable.generateNamespacePrefix(); 
          } 
          StringBuffer stringBuffer = new StringBuffer(str4);
          if (str4 != "")
            stringBuffer.append(':'); 
          str1 = stringBuffer.append(str5).toString();
        } 
        this._prefix = str4;
        this._namespace = new AttributeValueTemplate(str2, paramParser, this);
      } 
    } else {
      this._namespace = (str2 == "") ? null : new AttributeValueTemplate(str2, paramParser, this);
    } 
    this._name = new AttributeValueTemplate(str1, paramParser, this);
    String str3 = getAttribute("use-attribute-sets");
    if (str3.length() > 0) {
      if (!Util.isValidQNames(str3)) {
        ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str3, this);
        paramParser.reportError(3, errorMsg);
      } 
      setFirstElement(new UseAttributeSets(str3, paramParser));
    } 
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (!this._ignore) {
      this._name.typeCheck(paramSymbolTable);
      if (this._namespace != null)
        this._namespace.typeCheck(paramSymbolTable); 
    } 
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translateLiteral(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (!this._ignore) {
      instructionList.append(paramMethodGenerator.loadHandler());
      this._name.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(DUP2);
      instructionList.append(paramMethodGenerator.startElement());
      if (this._namespace != null) {
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(new PUSH(constantPoolGen, this._prefix));
        this._namespace.translate(paramClassGenerator, paramMethodGenerator);
        instructionList.append(paramMethodGenerator.namespace());
      } 
    } 
    translateContents(paramClassGenerator, paramMethodGenerator);
    if (!this._ignore)
      instructionList.append(paramMethodGenerator.endElement()); 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._isLiteralName) {
      translateLiteral(paramClassGenerator, paramMethodGenerator);
      return;
    } 
    if (!this._ignore) {
      LocalVariableGen localVariableGen = paramMethodGenerator.addLocalVariable2("nameValue", Util.getJCRefType("Ljava/lang/String;"), null);
      this._name.translate(paramClassGenerator, paramMethodGenerator);
      localVariableGen.setStart(instructionList.append(new ASTORE(localVariableGen.getIndex())));
      instructionList.append(new ALOAD(localVariableGen.getIndex()));
      int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "checkQName", "(Ljava/lang/String;)V");
      instructionList.append(new INVOKESTATIC(i));
      instructionList.append(paramMethodGenerator.loadHandler());
      localVariableGen.setEnd(instructionList.append(new ALOAD(localVariableGen.getIndex())));
      if (this._namespace != null) {
        this._namespace.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        instructionList.append(ACONST_NULL);
      } 
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(paramMethodGenerator.loadCurrentNode());
      instructionList.append(new INVOKESTATIC(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "startXslElement", "(Ljava/lang/String;Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)Ljava/lang/String;")));
    } 
    translateContents(paramClassGenerator, paramMethodGenerator);
    if (!this._ignore)
      instructionList.append(paramMethodGenerator.endElement()); 
  }
  
  public void translateContents(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    int i = elementCount();
    for (byte b = 0; b < i; b++) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)getContents().get(b);
      if (!this._ignore || !(syntaxTreeNode instanceof XslAttribute))
        syntaxTreeNode.translate(paramClassGenerator, paramMethodGenerator); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\XslElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */