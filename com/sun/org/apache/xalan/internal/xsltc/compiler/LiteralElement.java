package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.sun.org.apache.xml.internal.serializer.ToHTMLStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class LiteralElement extends Instruction {
  private String _name;
  
  private LiteralElement _literalElemParent = null;
  
  private List<SyntaxTreeNode> _attributeElements = null;
  
  private Map<String, String> _accessedPrefixes = null;
  
  private boolean _allAttributesUnique = false;
  
  public QName getName() { return this._qname; }
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("LiteralElement name = " + this._name);
    displayContents(paramInt + 4);
  }
  
  private String accessedNamespace(String paramString) {
    if (this._literalElemParent != null) {
      String str = this._literalElemParent.accessedNamespace(paramString);
      if (str != null)
        return str; 
    } 
    return (this._accessedPrefixes != null) ? (String)this._accessedPrefixes.get(paramString) : null;
  }
  
  public void registerNamespace(String paramString1, String paramString2, SymbolTable paramSymbolTable, boolean paramBoolean) {
    if (this._literalElemParent != null) {
      String str = this._literalElemParent.accessedNamespace(paramString1);
      if (str != null && str.equals(paramString2))
        return; 
    } 
    if (this._accessedPrefixes == null) {
      this._accessedPrefixes = new Hashtable();
    } else if (!paramBoolean) {
      String str = (String)this._accessedPrefixes.get(paramString1);
      if (str != null) {
        if (str.equals(paramString2))
          return; 
        paramString1 = paramSymbolTable.generateNamespacePrefix();
      } 
    } 
    if (!paramString1.equals("xml"))
      this._accessedPrefixes.put(paramString1, paramString2); 
  }
  
  private String translateQName(QName paramQName, SymbolTable paramSymbolTable) {
    String str1 = paramQName.getLocalPart();
    String str2 = paramQName.getPrefix();
    if (str2 == null) {
      str2 = "";
    } else if (str2.equals("xmlns")) {
      return "xmlns";
    } 
    String str3 = paramSymbolTable.lookupPrefixAlias(str2);
    if (str3 != null) {
      paramSymbolTable.excludeNamespaces(str2);
      str2 = str3;
    } 
    String str4 = lookupNamespace(str2);
    if (str4 == null)
      return str1; 
    registerNamespace(str2, str4, paramSymbolTable, false);
    return (str2 != "") ? (str2 + ":" + str1) : str1;
  }
  
  public void addAttribute(SyntaxTreeNode paramSyntaxTreeNode) {
    if (this._attributeElements == null)
      this._attributeElements = new ArrayList(2); 
    this._attributeElements.add(paramSyntaxTreeNode);
  }
  
  public void setFirstAttribute(SyntaxTreeNode paramSyntaxTreeNode) {
    if (this._attributeElements == null)
      this._attributeElements = new ArrayList(2); 
    this._attributeElements.add(0, paramSyntaxTreeNode);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._attributeElements != null)
      for (SyntaxTreeNode syntaxTreeNode : this._attributeElements)
        syntaxTreeNode.typeCheck(paramSymbolTable);  
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public Set<Map.Entry<String, String>> getNamespaceScope(SyntaxTreeNode paramSyntaxTreeNode) {
    HashMap hashMap = new HashMap();
    while (paramSyntaxTreeNode != null) {
      Map map = paramSyntaxTreeNode.getPrefixMapping();
      if (map != null)
        for (String str : map.keySet()) {
          if (!hashMap.containsKey(str))
            hashMap.put(str, map.get(str)); 
        }  
      paramSyntaxTreeNode = paramSyntaxTreeNode.getParent();
    } 
    return hashMap.entrySet();
  }
  
  public void parseContents(Parser paramParser) {
    SymbolTable symbolTable = paramParser.getSymbolTable();
    symbolTable.setCurrentNode(this);
    SyntaxTreeNode syntaxTreeNode = getParent();
    if (syntaxTreeNode != null && syntaxTreeNode instanceof LiteralElement)
      this._literalElemParent = (LiteralElement)syntaxTreeNode; 
    this._name = translateQName(this._qname, symbolTable);
    int i = this._attributes.getLength();
    for (byte b1 = 0; b1 < i; b1++) {
      QName qName = paramParser.getQName(this._attributes.getQName(b1));
      String str1 = qName.getNamespace();
      String str2 = this._attributes.getValue(b1);
      if (qName.equals(paramParser.getUseAttributeSets())) {
        if (!Util.isValidQNames(str2)) {
          ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str2, this);
          paramParser.reportError(3, errorMsg);
        } 
        setFirstAttribute(new UseAttributeSets(str2, paramParser));
      } else if (qName.equals(paramParser.getExtensionElementPrefixes())) {
        symbolTable.excludeNamespaces(str2);
      } else if (qName.equals(paramParser.getExcludeResultPrefixes())) {
        symbolTable.excludeNamespaces(str2);
      } else {
        String str = qName.getPrefix();
        if ((str == null || !str.equals("xmlns")) && (str != null || !qName.getLocalPart().equals("xmlns")) && (str1 == null || !str1.equals("http://www.w3.org/1999/XSL/Transform"))) {
          String str3 = translateQName(qName, symbolTable);
          LiteralAttribute literalAttribute = new LiteralAttribute(str3, str2, paramParser, this);
          addAttribute(literalAttribute);
          literalAttribute.setParent(this);
          literalAttribute.parseContents(paramParser);
        } 
      } 
    } 
    Set set = getNamespaceScope(this);
    for (Map.Entry entry : set) {
      String str = (String)entry.getKey();
      if (!str.equals("xml")) {
        String str1 = lookupNamespace(str);
        if (str1 != null && !symbolTable.isExcludedNamespace(str1))
          registerNamespace(str, str1, symbolTable, true); 
      } 
    } 
    parseChildren(paramParser);
    for (byte b2 = 0; b2 < i; b2++) {
      QName qName = paramParser.getQName(this._attributes.getQName(b2));
      String str = this._attributes.getValue(b2);
      if (qName.equals(paramParser.getExtensionElementPrefixes())) {
        symbolTable.unExcludeNamespaces(str);
      } else if (qName.equals(paramParser.getExcludeResultPrefixes())) {
        symbolTable.unExcludeNamespaces(str);
      } 
    } 
  }
  
  protected boolean contextDependent() { return dependentContents(); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    this._allAttributesUnique = checkAttributesUnique();
    instructionList.append(paramMethodGenerator.loadHandler());
    instructionList.append(new PUSH(constantPoolGen, this._name));
    instructionList.append(DUP2);
    instructionList.append(paramMethodGenerator.startElement());
    for (byte b = 0; b < elementCount(); b++) {
      SyntaxTreeNode syntaxTreeNode = elementAt(b);
      if (syntaxTreeNode instanceof Variable)
        syntaxTreeNode.translate(paramClassGenerator, paramMethodGenerator); 
    } 
    if (this._accessedPrefixes != null)
      for (Map.Entry entry : this._accessedPrefixes.entrySet()) {
        String str1 = (String)entry.getKey();
        String str2 = (String)entry.getValue();
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(new PUSH(constantPoolGen, str1));
        instructionList.append(new PUSH(constantPoolGen, str2));
        instructionList.append(paramMethodGenerator.namespace());
      }  
    if (this._attributeElements != null)
      for (SyntaxTreeNode syntaxTreeNode : this._attributeElements) {
        if (!(syntaxTreeNode instanceof XslAttribute))
          syntaxTreeNode.translate(paramClassGenerator, paramMethodGenerator); 
      }  
    translateContents(paramClassGenerator, paramMethodGenerator);
    instructionList.append(paramMethodGenerator.endElement());
  }
  
  private boolean isHTMLOutput() { return (getStylesheet().getOutputMethod() == 2); }
  
  public ElemDesc getElemDesc() { return isHTMLOutput() ? ToHTMLStream.getElemDesc(this._name) : null; }
  
  public boolean allAttributesUnique() { return this._allAttributesUnique; }
  
  private boolean checkAttributesUnique() {
    boolean bool = canProduceAttributeNodes(this, true);
    if (bool)
      return false; 
    if (this._attributeElements != null) {
      int i = this._attributeElements.size();
      HashMap hashMap = null;
      for (byte b = 0; b < i; b++) {
        SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)this._attributeElements.get(b);
        if (syntaxTreeNode instanceof UseAttributeSets)
          return false; 
        if (syntaxTreeNode instanceof XslAttribute) {
          if (hashMap == null) {
            hashMap = new HashMap();
            for (byte b1 = 0; b1 < b; b1++) {
              SyntaxTreeNode syntaxTreeNode1 = (SyntaxTreeNode)this._attributeElements.get(b1);
              if (syntaxTreeNode1 instanceof LiteralAttribute) {
                LiteralAttribute literalAttribute = (LiteralAttribute)syntaxTreeNode1;
                hashMap.put(literalAttribute.getName(), literalAttribute);
              } 
            } 
          } 
          XslAttribute xslAttribute = (XslAttribute)syntaxTreeNode;
          AttributeValue attributeValue = xslAttribute.getName();
          if (attributeValue instanceof AttributeValueTemplate)
            return false; 
          if (attributeValue instanceof SimpleAttributeValue) {
            SimpleAttributeValue simpleAttributeValue = (SimpleAttributeValue)attributeValue;
            String str = simpleAttributeValue.toString();
            if (str != null && hashMap.get(str) != null)
              return false; 
            if (str != null)
              hashMap.put(str, xslAttribute); 
          } 
        } 
      } 
    } 
    return true;
  }
  
  private boolean canProduceAttributeNodes(SyntaxTreeNode paramSyntaxTreeNode, boolean paramBoolean) {
    List list = paramSyntaxTreeNode.getContents();
    for (SyntaxTreeNode syntaxTreeNode : list) {
      if (syntaxTreeNode instanceof Text) {
        Text text = (Text)syntaxTreeNode;
        if (text.isIgnore())
          continue; 
        return false;
      } 
      if (syntaxTreeNode instanceof LiteralElement || syntaxTreeNode instanceof ValueOf || syntaxTreeNode instanceof XslElement || syntaxTreeNode instanceof Comment || syntaxTreeNode instanceof Number || syntaxTreeNode instanceof ProcessingInstruction)
        return false; 
      if (syntaxTreeNode instanceof XslAttribute) {
        if (paramBoolean)
          continue; 
        return true;
      } 
      if (syntaxTreeNode instanceof CallTemplate || syntaxTreeNode instanceof ApplyTemplates || syntaxTreeNode instanceof Copy || syntaxTreeNode instanceof CopyOf)
        return true; 
      if ((syntaxTreeNode instanceof If || syntaxTreeNode instanceof ForEach) && canProduceAttributeNodes(syntaxTreeNode, false))
        return true; 
      if (syntaxTreeNode instanceof Choose) {
        List list1 = syntaxTreeNode.getContents();
        for (SyntaxTreeNode syntaxTreeNode1 : list1) {
          if ((syntaxTreeNode1 instanceof When || syntaxTreeNode1 instanceof Otherwise) && canProduceAttributeNodes(syntaxTreeNode1, false))
            return true; 
        } 
      } 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LiteralElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */