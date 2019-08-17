package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.AttributeSetMethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.Iterator;
import java.util.List;

final class AttributeSet extends TopLevelElement {
  private static final String AttributeSetPrefix = "$as$";
  
  private QName _name;
  
  private UseAttributeSets _useSets;
  
  private AttributeSet _mergeSet;
  
  private String _method;
  
  private boolean _ignore = false;
  
  public QName getName() { return this._name; }
  
  public String getMethodName() { return this._method; }
  
  public void ignore() { this._ignore = true; }
  
  public void parseContents(Parser paramParser) {
    String str1 = getAttribute("name");
    if (!XML11Char.isXML11ValidQName(str1)) {
      ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str1, this);
      paramParser.reportError(3, errorMsg);
    } 
    this._name = paramParser.getQNameIgnoreDefaultNs(str1);
    if (this._name == null || this._name.equals("")) {
      ErrorMsg errorMsg = new ErrorMsg("UNNAMED_ATTRIBSET_ERR", this);
      paramParser.reportError(3, errorMsg);
    } 
    String str2 = getAttribute("use-attribute-sets");
    if (str2.length() > 0) {
      if (!Util.isValidQNames(str2)) {
        ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str2, this);
        paramParser.reportError(3, errorMsg);
      } 
      this._useSets = new UseAttributeSets(str2, paramParser);
    } 
    List list = getContents();
    int i = list.size();
    for (byte b = 0; b < i; b++) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)list.get(b);
      if (syntaxTreeNode instanceof XslAttribute) {
        paramParser.getSymbolTable().setCurrentNode(syntaxTreeNode);
        syntaxTreeNode.parseContents(paramParser);
      } else if (!(syntaxTreeNode instanceof Text)) {
        ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_CHILD_ERR", this);
        paramParser.reportError(3, errorMsg);
      } 
    } 
    paramParser.getSymbolTable().setCurrentNode(this);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._ignore)
      return Type.Void; 
    this._mergeSet = paramSymbolTable.addAttributeSet(this);
    this._method = "$as$" + getXSLTC().nextAttributeSetSerial();
    if (this._useSets != null)
      this._useSets.typeCheck(paramSymbolTable); 
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (this._ignore)
      return; 
    paramMethodGenerator = new AttributeSetMethodGenerator(this._method, paramClassGenerator);
    if (this._mergeSet != null) {
      ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList instructionList1 = paramMethodGenerator.getInstructionList();
      String str = this._mergeSet.getMethodName();
      instructionList1.append(paramClassGenerator.loadTranslet());
      instructionList1.append(paramMethodGenerator.loadDOM());
      instructionList1.append(paramMethodGenerator.loadIterator());
      instructionList1.append(paramMethodGenerator.loadHandler());
      instructionList1.append(paramMethodGenerator.loadCurrentNode());
      int i = constantPoolGen.addMethodref(paramClassGenerator.getClassName(), str, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V");
      instructionList1.append(new INVOKESPECIAL(i));
    } 
    if (this._useSets != null)
      this._useSets.translate(paramClassGenerator, paramMethodGenerator); 
    Iterator iterator = elements();
    while (iterator.hasNext()) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)iterator.next();
      if (syntaxTreeNode instanceof XslAttribute) {
        XslAttribute xslAttribute = (XslAttribute)syntaxTreeNode;
        xslAttribute.translate(paramClassGenerator, paramMethodGenerator);
      } 
    } 
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(RETURN);
    paramClassGenerator.addMethod(paramMethodGenerator);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("attribute-set: ");
    Iterator iterator = elements();
    while (iterator.hasNext()) {
      XslAttribute xslAttribute = (XslAttribute)iterator.next();
      stringBuffer.append(xslAttribute);
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */