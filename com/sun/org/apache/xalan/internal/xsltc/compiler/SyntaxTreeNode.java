package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.BasicType;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DUP_X1;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public abstract class SyntaxTreeNode implements Constants {
  private Parser _parser;
  
  protected SyntaxTreeNode _parent;
  
  private Stylesheet _stylesheet;
  
  private Template _template;
  
  private final List<SyntaxTreeNode> _contents = new ArrayList(2);
  
  protected QName _qname;
  
  private int _line = 0;
  
  protected AttributesImpl _attributes = null;
  
  private Map<String, String> _prefixMapping = null;
  
  protected static final SyntaxTreeNode Dummy = new AbsolutePathPattern(null);
  
  protected static final int IndentIncrement = 4;
  
  private static final char[] _spaces = "                                                       ".toCharArray();
  
  public SyntaxTreeNode() { this._qname = null; }
  
  public SyntaxTreeNode(int paramInt) { this._qname = null; }
  
  public SyntaxTreeNode(String paramString1, String paramString2, String paramString3) { setQName(paramString1, paramString2, paramString3); }
  
  protected final void setLineNumber(int paramInt) { this._line = paramInt; }
  
  public final int getLineNumber() {
    if (this._line > 0)
      return this._line; 
    SyntaxTreeNode syntaxTreeNode = getParent();
    return (syntaxTreeNode != null) ? syntaxTreeNode.getLineNumber() : 0;
  }
  
  protected void setQName(QName paramQName) { this._qname = paramQName; }
  
  protected void setQName(String paramString1, String paramString2, String paramString3) { this._qname = new QName(paramString1, paramString2, paramString3); }
  
  protected QName getQName() { return this._qname; }
  
  protected void setAttributes(AttributesImpl paramAttributesImpl) { this._attributes = paramAttributesImpl; }
  
  protected String getAttribute(String paramString) {
    if (this._attributes == null)
      return ""; 
    String str = this._attributes.getValue(paramString);
    return (str == null || str.equals("")) ? "" : str;
  }
  
  protected String getAttribute(String paramString1, String paramString2) { return getAttribute(paramString1 + ':' + paramString2); }
  
  protected boolean hasAttribute(String paramString) { return (this._attributes != null && this._attributes.getValue(paramString) != null); }
  
  protected void addAttribute(String paramString1, String paramString2) {
    int i = this._attributes.getIndex(paramString1);
    if (i != -1) {
      this._attributes.setAttribute(i, "", Util.getLocalName(paramString1), paramString1, "CDATA", paramString2);
    } else {
      this._attributes.addAttribute("", Util.getLocalName(paramString1), paramString1, "CDATA", paramString2);
    } 
  }
  
  protected Attributes getAttributes() { return this._attributes; }
  
  protected void setPrefixMapping(Map<String, String> paramMap) { this._prefixMapping = paramMap; }
  
  protected Map<String, String> getPrefixMapping() { return this._prefixMapping; }
  
  protected void addPrefixMapping(String paramString1, String paramString2) {
    if (this._prefixMapping == null)
      this._prefixMapping = new HashMap(); 
    this._prefixMapping.put(paramString1, paramString2);
  }
  
  protected String lookupNamespace(String paramString) {
    String str = null;
    if (this._prefixMapping != null)
      str = (String)this._prefixMapping.get(paramString); 
    if (str == null && this._parent != null) {
      str = this._parent.lookupNamespace(paramString);
      if (paramString == "" && str == null)
        str = ""; 
    } 
    return str;
  }
  
  protected String lookupPrefix(String paramString) {
    String str = null;
    if (this._prefixMapping != null && this._prefixMapping.containsValue(paramString)) {
      for (Map.Entry entry : this._prefixMapping.entrySet()) {
        str = (String)entry.getKey();
        String str1 = (String)entry.getValue();
        if (str1.equals(paramString))
          return str; 
      } 
    } else if (this._parent != null) {
      str = this._parent.lookupPrefix(paramString);
      if (paramString == "" && str == null)
        str = ""; 
    } 
    return str;
  }
  
  protected void setParser(Parser paramParser) { this._parser = paramParser; }
  
  public final Parser getParser() { return this._parser; }
  
  protected void setParent(SyntaxTreeNode paramSyntaxTreeNode) {
    if (this._parent == null)
      this._parent = paramSyntaxTreeNode; 
  }
  
  protected final SyntaxTreeNode getParent() { return this._parent; }
  
  protected final boolean isDummy() { return (this == Dummy); }
  
  protected int getImportPrecedence() {
    Stylesheet stylesheet = getStylesheet();
    return (stylesheet == null) ? Integer.MIN_VALUE : stylesheet.getImportPrecedence();
  }
  
  public Stylesheet getStylesheet() {
    if (this._stylesheet == null) {
      SyntaxTreeNode syntaxTreeNode;
      for (syntaxTreeNode = this; syntaxTreeNode != null; syntaxTreeNode = syntaxTreeNode.getParent()) {
        if (syntaxTreeNode instanceof Stylesheet)
          return (Stylesheet)syntaxTreeNode; 
      } 
      this._stylesheet = (Stylesheet)syntaxTreeNode;
    } 
    return this._stylesheet;
  }
  
  protected Template getTemplate() {
    if (this._template == null) {
      SyntaxTreeNode syntaxTreeNode;
      for (syntaxTreeNode = this; syntaxTreeNode != null && !(syntaxTreeNode instanceof Template); syntaxTreeNode = syntaxTreeNode.getParent());
      this._template = (Template)syntaxTreeNode;
    } 
    return this._template;
  }
  
  protected final XSLTC getXSLTC() { return this._parser.getXSLTC(); }
  
  protected final SymbolTable getSymbolTable() { return (this._parser == null) ? null : this._parser.getSymbolTable(); }
  
  public void parseContents(Parser paramParser) { parseChildren(paramParser); }
  
  protected final void parseChildren(Parser paramParser) {
    ArrayList arrayList = null;
    for (SyntaxTreeNode syntaxTreeNode : this._contents) {
      paramParser.getSymbolTable().setCurrentNode(syntaxTreeNode);
      syntaxTreeNode.parseContents(paramParser);
      QName qName = updateScope(paramParser, syntaxTreeNode);
      if (qName != null) {
        if (arrayList == null)
          arrayList = new ArrayList(2); 
        arrayList.add(qName);
      } 
    } 
    paramParser.getSymbolTable().setCurrentNode(this);
    if (arrayList != null)
      for (QName qName : arrayList)
        paramParser.removeVariable(qName);  
  }
  
  protected QName updateScope(Parser paramParser, SyntaxTreeNode paramSyntaxTreeNode) {
    if (paramSyntaxTreeNode instanceof Variable) {
      Variable variable = (Variable)paramSyntaxTreeNode;
      paramParser.addVariable(variable);
      return variable.getName();
    } 
    if (paramSyntaxTreeNode instanceof Param) {
      Param param = (Param)paramSyntaxTreeNode;
      paramParser.addParameter(param);
      return param.getName();
    } 
    return null;
  }
  
  public abstract Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError;
  
  protected Type typeCheckContents(SymbolTable paramSymbolTable) throws TypeCheckError {
    for (SyntaxTreeNode syntaxTreeNode : this._contents)
      syntaxTreeNode.typeCheck(paramSymbolTable); 
    return Type.Void;
  }
  
  public abstract void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator);
  
  protected void translateContents(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    int i = elementCount();
    for (SyntaxTreeNode syntaxTreeNode : this._contents) {
      paramMethodGenerator.markChunkStart();
      syntaxTreeNode.translate(paramClassGenerator, paramMethodGenerator);
      paramMethodGenerator.markChunkEnd();
    } 
    for (byte b = 0; b < i; b++) {
      if (this._contents.get(b) instanceof VariableBase) {
        VariableBase variableBase = (VariableBase)this._contents.get(b);
        variableBase.unmapRegister(paramClassGenerator, paramMethodGenerator);
      } 
    } 
  }
  
  private boolean isSimpleRTF(SyntaxTreeNode paramSyntaxTreeNode) {
    List list = paramSyntaxTreeNode.getContents();
    for (SyntaxTreeNode syntaxTreeNode : list) {
      if (!isTextElement(syntaxTreeNode, false))
        return false; 
    } 
    return true;
  }
  
  private boolean isAdaptiveRTF(SyntaxTreeNode paramSyntaxTreeNode) {
    List list = paramSyntaxTreeNode.getContents();
    for (SyntaxTreeNode syntaxTreeNode : list) {
      if (!isTextElement(syntaxTreeNode, true))
        return false; 
    } 
    return true;
  }
  
  private boolean isTextElement(SyntaxTreeNode paramSyntaxTreeNode, boolean paramBoolean) {
    if (paramSyntaxTreeNode instanceof ValueOf || paramSyntaxTreeNode instanceof Number || paramSyntaxTreeNode instanceof Text)
      return true; 
    if (paramSyntaxTreeNode instanceof If)
      return paramBoolean ? isAdaptiveRTF(paramSyntaxTreeNode) : isSimpleRTF(paramSyntaxTreeNode); 
    if (paramSyntaxTreeNode instanceof Choose) {
      List list = paramSyntaxTreeNode.getContents();
      for (SyntaxTreeNode syntaxTreeNode : list) {
        if (syntaxTreeNode instanceof Text || ((syntaxTreeNode instanceof When || syntaxTreeNode instanceof Otherwise) && ((paramBoolean && isAdaptiveRTF(syntaxTreeNode)) || (!paramBoolean && isSimpleRTF(syntaxTreeNode)))))
          continue; 
        return false;
      } 
      return true;
    } 
    return (paramBoolean && (paramSyntaxTreeNode instanceof CallTemplate || paramSyntaxTreeNode instanceof ApplyTemplates));
  }
  
  protected void compileResultTree(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    Stylesheet stylesheet = paramClassGenerator.getStylesheet();
    boolean bool1 = isSimpleRTF(this);
    boolean bool2 = false;
    if (!bool1)
      bool2 = isAdaptiveRTF(this); 
    byte b = bool1 ? 0 : (bool2 ? 1 : 2);
    instructionList.append(paramMethodGenerator.loadHandler());
    String str = paramClassGenerator.getDOMClass();
    instructionList.append(paramMethodGenerator.loadDOM());
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getResultTreeFrag", "(IIZ)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    instructionList.append(new PUSH(constantPoolGen, 32));
    instructionList.append(new PUSH(constantPoolGen, b));
    instructionList.append(new PUSH(constantPoolGen, stylesheet.callsNodeset()));
    instructionList.append(new INVOKEINTERFACE(i, 4));
    instructionList.append(DUP);
    i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getOutputDomBuilder", "()Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    instructionList.append(new INVOKEINTERFACE(i, 1));
    instructionList.append(DUP);
    instructionList.append(paramMethodGenerator.storeHandler());
    instructionList.append(paramMethodGenerator.startDocument());
    translateContents(paramClassGenerator, paramMethodGenerator);
    instructionList.append(paramMethodGenerator.loadHandler());
    instructionList.append(paramMethodGenerator.endDocument());
    if (stylesheet.callsNodeset() && !str.equals("com/sun/org/apache/xalan/internal/xsltc/DOM")) {
      i = constantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;[Ljava/lang/String;[Ljava/lang/String;[I[Ljava/lang/String;)V");
      instructionList.append(new NEW(constantPoolGen.addClass("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter")));
      instructionList.append(new DUP_X1());
      instructionList.append(SWAP);
      if (!stylesheet.callsNodeset()) {
        instructionList.append(new ICONST(0));
        instructionList.append(new ANEWARRAY(constantPoolGen.addClass("java.lang.String")));
        instructionList.append(DUP);
        instructionList.append(DUP);
        instructionList.append(new ICONST(0));
        instructionList.append(new NEWARRAY(BasicType.INT));
        instructionList.append(SWAP);
        instructionList.append(new INVOKESPECIAL(i));
      } else {
        instructionList.append(ALOAD_0);
        instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;")));
        instructionList.append(ALOAD_0);
        instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;")));
        instructionList.append(ALOAD_0);
        instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "typesArray", "[I")));
        instructionList.append(ALOAD_0);
        instructionList.append(new GETFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;")));
        instructionList.append(new INVOKESPECIAL(i));
        instructionList.append(DUP);
        instructionList.append(paramMethodGenerator.loadDOM());
        instructionList.append(new CHECKCAST(constantPoolGen.addClass(paramClassGenerator.getDOMClass())));
        instructionList.append(SWAP);
        i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "addDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;)I");
        instructionList.append(new INVOKEVIRTUAL(i));
        instructionList.append(POP);
      } 
    } 
    instructionList.append(SWAP);
    instructionList.append(paramMethodGenerator.storeHandler());
  }
  
  protected boolean contextDependent() { return true; }
  
  protected boolean dependentContents() {
    for (SyntaxTreeNode syntaxTreeNode : this._contents) {
      if (syntaxTreeNode.contextDependent())
        return true; 
    } 
    return false;
  }
  
  protected final void addElement(SyntaxTreeNode paramSyntaxTreeNode) {
    this._contents.add(paramSyntaxTreeNode);
    paramSyntaxTreeNode.setParent(this);
  }
  
  protected final void setFirstElement(SyntaxTreeNode paramSyntaxTreeNode) {
    this._contents.add(0, paramSyntaxTreeNode);
    paramSyntaxTreeNode.setParent(this);
  }
  
  protected final void removeElement(SyntaxTreeNode paramSyntaxTreeNode) {
    this._contents.remove(paramSyntaxTreeNode);
    paramSyntaxTreeNode.setParent(null);
  }
  
  protected final List<SyntaxTreeNode> getContents() { return this._contents; }
  
  protected final boolean hasContents() { return (elementCount() > 0); }
  
  protected final int elementCount() { return this._contents.size(); }
  
  protected final Iterator<SyntaxTreeNode> elements() { return this._contents.iterator(); }
  
  protected final SyntaxTreeNode elementAt(int paramInt) { return (SyntaxTreeNode)this._contents.get(paramInt); }
  
  protected final SyntaxTreeNode lastChild() { return this._contents.isEmpty() ? null : (SyntaxTreeNode)this._contents.get(this._contents.size() - 1); }
  
  public void display(int paramInt) { displayContents(paramInt); }
  
  protected void displayContents(int paramInt) {
    for (SyntaxTreeNode syntaxTreeNode : this._contents)
      syntaxTreeNode.display(paramInt); 
  }
  
  protected final void indent(int paramInt) { System.out.print(new String(_spaces, 0, paramInt)); }
  
  protected void reportError(SyntaxTreeNode paramSyntaxTreeNode, Parser paramParser, String paramString1, String paramString2) {
    ErrorMsg errorMsg = new ErrorMsg(paramString1, paramString2, paramSyntaxTreeNode);
    paramParser.reportError(3, errorMsg);
  }
  
  protected void reportWarning(SyntaxTreeNode paramSyntaxTreeNode, Parser paramParser, String paramString1, String paramString2) {
    ErrorMsg errorMsg = new ErrorMsg(paramString1, paramString2, paramSyntaxTreeNode);
    paramParser.reportError(4, errorMsg);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\SyntaxTreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */