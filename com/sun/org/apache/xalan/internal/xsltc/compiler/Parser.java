package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.java_cup.internal.runtime.Symbol;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

public class Parser implements Constants, ContentHandler {
  private static final String XSL = "xsl";
  
  private static final String TRANSLET = "translet";
  
  private Locator _locator = null;
  
  private XSLTC _xsltc;
  
  private XPathParser _xpathParser;
  
  private ArrayList<ErrorMsg> _errors;
  
  private ArrayList<ErrorMsg> _warnings;
  
  private Map<String, String> _instructionClasses;
  
  private Map<String, String[]> _instructionAttrs;
  
  private Map<String, QName> _qNames;
  
  private Map<String, Map<String, QName>> _namespaces;
  
  private QName _useAttributeSets;
  
  private QName _excludeResultPrefixes;
  
  private QName _extensionElementPrefixes;
  
  private Map<String, Object> _variableScope;
  
  private Stylesheet _currentStylesheet;
  
  private SymbolTable _symbolTable;
  
  private Output _output;
  
  private Template _template;
  
  private boolean _rootNamespaceDef;
  
  private SyntaxTreeNode _root;
  
  private String _target;
  
  private int _currentImportPrecedence;
  
  private boolean _overrideDefaultParser;
  
  private String _PImedia = null;
  
  private String _PItitle = null;
  
  private String _PIcharset = null;
  
  private int _templateIndex = 0;
  
  private boolean versionIsOne = true;
  
  private Stack<SyntaxTreeNode> _parentStack = null;
  
  private Map<String, String> _prefixMapping = null;
  
  public Parser(XSLTC paramXSLTC, boolean paramBoolean) {
    this._xsltc = paramXSLTC;
    this._overrideDefaultParser = paramBoolean;
  }
  
  public void init() {
    this._qNames = new HashMap(512);
    this._namespaces = new HashMap();
    this._instructionClasses = new HashMap();
    this._instructionAttrs = new HashMap();
    this._variableScope = new HashMap();
    this._template = null;
    this._errors = new ArrayList();
    this._warnings = new ArrayList();
    this._symbolTable = new SymbolTable();
    this._xpathParser = new XPathParser(this);
    this._currentStylesheet = null;
    this._output = null;
    this._root = null;
    this._rootNamespaceDef = false;
    this._currentImportPrecedence = 1;
    initStdClasses();
    initInstructionAttrs();
    initExtClasses();
    initSymbolTable();
    this._useAttributeSets = getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "use-attribute-sets");
    this._excludeResultPrefixes = getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "exclude-result-prefixes");
    this._extensionElementPrefixes = getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "extension-element-prefixes");
  }
  
  public void setOutput(Output paramOutput) {
    if (this._output != null) {
      if (this._output.getImportPrecedence() <= paramOutput.getImportPrecedence()) {
        paramOutput.mergeOutput(this._output);
        this._output.disable();
        this._output = paramOutput;
      } else {
        paramOutput.disable();
      } 
    } else {
      this._output = paramOutput;
    } 
  }
  
  public Output getOutput() { return this._output; }
  
  public Properties getOutputProperties() { return getTopLevelStylesheet().getOutputProperties(); }
  
  public void addVariable(Variable paramVariable) { addVariableOrParam(paramVariable); }
  
  public void addParameter(Param paramParam) { addVariableOrParam(paramParam); }
  
  private void addVariableOrParam(VariableBase paramVariableBase) {
    Object object = this._variableScope.get(paramVariableBase.getName().getStringRep());
    if (object != null) {
      if (object instanceof Stack) {
        Stack stack = (Stack)object;
        stack.push(paramVariableBase);
      } else if (object instanceof VariableBase) {
        Stack stack = new Stack();
        stack.push((VariableBase)object);
        stack.push(paramVariableBase);
        this._variableScope.put(paramVariableBase.getName().getStringRep(), stack);
      } 
    } else {
      this._variableScope.put(paramVariableBase.getName().getStringRep(), paramVariableBase);
    } 
  }
  
  public void removeVariable(QName paramQName) {
    Object object = this._variableScope.get(paramQName.getStringRep());
    if (object instanceof Stack) {
      Stack stack = (Stack)object;
      if (!stack.isEmpty())
        stack.pop(); 
      if (!stack.isEmpty())
        return; 
    } 
    this._variableScope.remove(paramQName.getStringRep());
  }
  
  public VariableBase lookupVariable(QName paramQName) {
    Object object = this._variableScope.get(paramQName.getStringRep());
    if (object instanceof VariableBase)
      return (VariableBase)object; 
    if (object instanceof Stack) {
      Stack stack = (Stack)object;
      return (VariableBase)stack.peek();
    } 
    return null;
  }
  
  public void setXSLTC(XSLTC paramXSLTC) { this._xsltc = paramXSLTC; }
  
  public XSLTC getXSLTC() { return this._xsltc; }
  
  public int getCurrentImportPrecedence() { return this._currentImportPrecedence; }
  
  public int getNextImportPrecedence() { return ++this._currentImportPrecedence; }
  
  public void setCurrentStylesheet(Stylesheet paramStylesheet) { this._currentStylesheet = paramStylesheet; }
  
  public Stylesheet getCurrentStylesheet() { return this._currentStylesheet; }
  
  public Stylesheet getTopLevelStylesheet() { return this._xsltc.getStylesheet(); }
  
  public QName getQNameSafe(String paramString) {
    int i = paramString.lastIndexOf(':');
    if (i != -1) {
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      String str3 = null;
      if (!str1.equals("xmlns")) {
        str3 = this._symbolTable.lookupNamespace(str1);
        if (str3 == null)
          str3 = ""; 
      } 
      return getQName(str3, str1, str2);
    } 
    String str = paramString.equals("xmlns") ? null : this._symbolTable.lookupNamespace("");
    return getQName(str, null, paramString);
  }
  
  public QName getQName(String paramString) { return getQName(paramString, true, false); }
  
  public QName getQNameIgnoreDefaultNs(String paramString) { return getQName(paramString, true, true); }
  
  public QName getQName(String paramString, boolean paramBoolean) { return getQName(paramString, paramBoolean, false); }
  
  private QName getQName(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    int i = paramString.lastIndexOf(':');
    if (i != -1) {
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      String str3 = null;
      if (!str1.equals("xmlns")) {
        str3 = this._symbolTable.lookupNamespace(str1);
        if (str3 == null && paramBoolean1) {
          int j = getLineNumber();
          ErrorMsg errorMsg = new ErrorMsg("NAMESPACE_UNDEF_ERR", j, str1);
          reportError(3, errorMsg);
        } 
      } 
      return getQName(str3, str1, str2);
    } 
    if (paramString.equals("xmlns"))
      paramBoolean2 = true; 
    String str = paramBoolean2 ? null : this._symbolTable.lookupNamespace("");
    return getQName(str, null, paramString);
  }
  
  public QName getQName(String paramString1, String paramString2, String paramString3) {
    if (paramString1 == null || paramString1.equals("")) {
      QName qName1 = (QName)this._qNames.get(paramString3);
      if (qName1 == null) {
        qName1 = new QName(null, paramString2, paramString3);
        this._qNames.put(paramString3, qName1);
      } 
      return qName1;
    } 
    Map map = (Map)this._namespaces.get(paramString1);
    String str = (paramString2 == null || paramString2.length() == 0) ? paramString3 : (paramString2 + ':' + paramString3);
    if (map == null) {
      QName qName1 = new QName(paramString1, paramString2, paramString3);
      this._namespaces.put(paramString1, map = new HashMap());
      map.put(str, qName1);
      return qName1;
    } 
    QName qName = (QName)map.get(str);
    if (qName == null) {
      qName = new QName(paramString1, paramString2, paramString3);
      map.put(str, qName);
    } 
    return qName;
  }
  
  public QName getQName(String paramString1, String paramString2) { return getQName(paramString1 + paramString2); }
  
  public QName getQName(QName paramQName1, QName paramQName2) { return getQName(paramQName1.toString() + paramQName2.toString()); }
  
  public QName getUseAttributeSets() { return this._useAttributeSets; }
  
  public QName getExtensionElementPrefixes() { return this._extensionElementPrefixes; }
  
  public QName getExcludeResultPrefixes() { return this._excludeResultPrefixes; }
  
  public Stylesheet makeStylesheet(SyntaxTreeNode paramSyntaxTreeNode) throws CompilerException {
    try {
      Stylesheet stylesheet;
      if (paramSyntaxTreeNode instanceof Stylesheet) {
        stylesheet = (Stylesheet)paramSyntaxTreeNode;
      } else {
        stylesheet = new Stylesheet();
        stylesheet.setSimplified();
        stylesheet.addElement(paramSyntaxTreeNode);
        stylesheet.setAttributes((AttributesImpl)paramSyntaxTreeNode.getAttributes());
        if (paramSyntaxTreeNode.lookupNamespace("") == null)
          paramSyntaxTreeNode.addPrefixMapping("", ""); 
      } 
      stylesheet.setParser(this);
      return stylesheet;
    } catch (ClassCastException classCastException) {
      ErrorMsg errorMsg = new ErrorMsg("NOT_STYLESHEET_ERR", paramSyntaxTreeNode);
      throw new CompilerException(errorMsg.toString());
    } 
  }
  
  public void createAST(Stylesheet paramStylesheet) {
    try {
      if (paramStylesheet != null) {
        paramStylesheet.parseContents(this);
        Iterator iterator = paramStylesheet.elements();
        while (iterator.hasNext()) {
          SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)iterator.next();
          if (syntaxTreeNode instanceof Text) {
            int i = getLineNumber();
            ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_TEXT_NODE_ERR", i, null);
            reportError(3, errorMsg);
          } 
        } 
        if (!errorsFound())
          paramStylesheet.typeCheck(this._symbolTable); 
      } 
    } catch (TypeCheckError typeCheckError) {
      reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", typeCheckError));
    } 
  }
  
  public SyntaxTreeNode parse(XMLReader paramXMLReader, InputSource paramInputSource) {
    try {
      paramXMLReader.setContentHandler(this);
      paramXMLReader.parse(paramInputSource);
      return getStylesheet(this._root);
    } catch (IOException iOException) {
      if (this._xsltc.debug())
        iOException.printStackTrace(); 
      reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", iOException));
    } catch (SAXException sAXException) {
      Exception exception = sAXException.getException();
      if (this._xsltc.debug()) {
        sAXException.printStackTrace();
        if (exception != null)
          exception.printStackTrace(); 
      } 
      reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", sAXException));
    } catch (CompilerException compilerException) {
      if (this._xsltc.debug())
        compilerException.printStackTrace(); 
      reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", compilerException));
    } catch (Exception exception) {
      if (this._xsltc.debug())
        exception.printStackTrace(); 
      reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", exception));
    } 
    return null;
  }
  
  public SyntaxTreeNode parse(InputSource paramInputSource) {
    XMLReader xMLReader = JdkXmlUtils.getXMLReader(this._overrideDefaultParser, this._xsltc.isSecureProcessing());
    JdkXmlUtils.setXMLReaderPropertyIfSupport(xMLReader, "http://javax.xml.XMLConstants/property/accessExternalDTD", this._xsltc.getProperty("http://javax.xml.XMLConstants/property/accessExternalDTD"), true);
    String str = "";
    try {
      XMLSecurityManager xMLSecurityManager = (XMLSecurityManager)this._xsltc.getProperty("http://apache.org/xml/properties/security-manager");
      for (XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
        str = limit.apiProperty();
        xMLReader.setProperty(str, xMLSecurityManager.getLimitValueAsString(limit));
      } 
      if (xMLSecurityManager.printEntityCountInfo()) {
        str = "http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo";
        xMLReader.setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
      } 
    } catch (SAXException sAXException) {
      XMLSecurityManager.printWarning(xMLReader.getClass().getName(), str, sAXException);
    } 
    return parse(xMLReader, paramInputSource);
  }
  
  public SyntaxTreeNode getDocumentRoot() { return this._root; }
  
  protected void setPIParameters(String paramString1, String paramString2, String paramString3) {
    this._PImedia = paramString1;
    this._PItitle = paramString2;
    this._PIcharset = paramString3;
  }
  
  private SyntaxTreeNode getStylesheet(SyntaxTreeNode paramSyntaxTreeNode) throws CompilerException {
    if (this._target == null) {
      if (!this._rootNamespaceDef) {
        ErrorMsg errorMsg = new ErrorMsg("MISSING_XSLT_URI_ERR");
        throw new CompilerException(errorMsg.toString());
      } 
      return paramSyntaxTreeNode;
    } 
    if (this._target.charAt(0) == '#') {
      SyntaxTreeNode syntaxTreeNode = findStylesheet(paramSyntaxTreeNode, this._target.substring(1));
      if (syntaxTreeNode == null) {
        ErrorMsg errorMsg = new ErrorMsg("MISSING_XSLT_TARGET_ERR", this._target, paramSyntaxTreeNode);
        throw new CompilerException(errorMsg.toString());
      } 
      return syntaxTreeNode;
    } 
    try {
      String str1 = this._target;
      if (str1.indexOf(":") == -1)
        str1 = "file:" + str1; 
      str1 = SystemIDResolver.getAbsoluteURI(str1);
      String str2 = SecuritySupport.checkAccess(str1, (String)this._xsltc.getProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet"), "all");
      if (str2 != null) {
        ErrorMsg errorMsg = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(this._target), str2, paramSyntaxTreeNode);
        throw new CompilerException(errorMsg.toString());
      } 
    } catch (IOException iOException) {
      throw new CompilerException(iOException);
    } 
    return loadExternalStylesheet(this._target);
  }
  
  private SyntaxTreeNode findStylesheet(SyntaxTreeNode paramSyntaxTreeNode, String paramString) {
    if (paramSyntaxTreeNode == null)
      return null; 
    if (paramSyntaxTreeNode instanceof Stylesheet) {
      String str = paramSyntaxTreeNode.getAttribute("id");
      if (str.equals(paramString))
        return paramSyntaxTreeNode; 
    } 
    List list = paramSyntaxTreeNode.getContents();
    if (list != null) {
      int i = list.size();
      for (byte b = 0; b < i; b++) {
        SyntaxTreeNode syntaxTreeNode1 = (SyntaxTreeNode)list.get(b);
        SyntaxTreeNode syntaxTreeNode2 = findStylesheet(syntaxTreeNode1, paramString);
        if (syntaxTreeNode2 != null)
          return syntaxTreeNode2; 
      } 
    } 
    return null;
  }
  
  private SyntaxTreeNode loadExternalStylesheet(String paramString) throws CompilerException {
    InputSource inputSource;
    if ((new File(paramString)).exists()) {
      inputSource = new InputSource("file:" + paramString);
    } else {
      inputSource = new InputSource(paramString);
    } 
    return parse(inputSource);
  }
  
  private void initAttrTable(String paramString, String[] paramArrayOfString) { this._instructionAttrs.put(getQName("http://www.w3.org/1999/XSL/Transform", "xsl", paramString).getStringRep(), paramArrayOfString); }
  
  private void initInstructionAttrs() {
    initAttrTable("template", new String[] { "match", "name", "priority", "mode" });
    initAttrTable("stylesheet", new String[] { "id", "version", "extension-element-prefixes", "exclude-result-prefixes" });
    initAttrTable("transform", new String[] { "id", "version", "extension-element-prefixes", "exclude-result-prefixes" });
    initAttrTable("text", new String[] { "disable-output-escaping" });
    initAttrTable("if", new String[] { "test" });
    initAttrTable("choose", new String[0]);
    initAttrTable("when", new String[] { "test" });
    initAttrTable("otherwise", new String[0]);
    initAttrTable("for-each", new String[] { "select" });
    initAttrTable("message", new String[] { "terminate" });
    initAttrTable("number", new String[] { "level", "count", "from", "value", "format", "lang", "letter-value", "grouping-separator", "grouping-size" });
    initAttrTable("comment", new String[0]);
    initAttrTable("copy", new String[] { "use-attribute-sets" });
    initAttrTable("copy-of", new String[] { "select" });
    initAttrTable("param", new String[] { "name", "select" });
    initAttrTable("with-param", new String[] { "name", "select" });
    initAttrTable("variable", new String[] { "name", "select" });
    initAttrTable("output", new String[] { "method", "version", "encoding", "omit-xml-declaration", "standalone", "doctype-public", "doctype-system", "cdata-section-elements", "indent", "media-type" });
    initAttrTable("sort", new String[] { "select", "order", "case-order", "lang", "data-type" });
    initAttrTable("key", new String[] { "name", "match", "use" });
    initAttrTable("fallback", new String[0]);
    initAttrTable("attribute", new String[] { "name", "namespace" });
    initAttrTable("attribute-set", new String[] { "name", "use-attribute-sets" });
    initAttrTable("value-of", new String[] { "select", "disable-output-escaping" });
    initAttrTable("element", new String[] { "name", "namespace", "use-attribute-sets" });
    initAttrTable("call-template", new String[] { "name" });
    initAttrTable("apply-templates", new String[] { "select", "mode" });
    initAttrTable("apply-imports", new String[0]);
    initAttrTable("decimal-format", new String[] { 
          "name", "decimal-separator", "grouping-separator", "infinity", "minus-sign", "NaN", "percent", "per-mille", "zero-digit", "digit", 
          "pattern-separator" });
    initAttrTable("import", new String[] { "href" });
    initAttrTable("include", new String[] { "href" });
    initAttrTable("strip-space", new String[] { "elements" });
    initAttrTable("preserve-space", new String[] { "elements" });
    initAttrTable("processing-instruction", new String[] { "name" });
    initAttrTable("namespace-alias", new String[] { "stylesheet-prefix", "result-prefix" });
  }
  
  private void initStdClasses() {
    initStdClass("template", "Template");
    initStdClass("stylesheet", "Stylesheet");
    initStdClass("transform", "Stylesheet");
    initStdClass("text", "Text");
    initStdClass("if", "If");
    initStdClass("choose", "Choose");
    initStdClass("when", "When");
    initStdClass("otherwise", "Otherwise");
    initStdClass("for-each", "ForEach");
    initStdClass("message", "Message");
    initStdClass("number", "Number");
    initStdClass("comment", "Comment");
    initStdClass("copy", "Copy");
    initStdClass("copy-of", "CopyOf");
    initStdClass("param", "Param");
    initStdClass("with-param", "WithParam");
    initStdClass("variable", "Variable");
    initStdClass("output", "Output");
    initStdClass("sort", "Sort");
    initStdClass("key", "Key");
    initStdClass("fallback", "Fallback");
    initStdClass("attribute", "XslAttribute");
    initStdClass("attribute-set", "AttributeSet");
    initStdClass("value-of", "ValueOf");
    initStdClass("element", "XslElement");
    initStdClass("call-template", "CallTemplate");
    initStdClass("apply-templates", "ApplyTemplates");
    initStdClass("apply-imports", "ApplyImports");
    initStdClass("decimal-format", "DecimalFormatting");
    initStdClass("import", "Import");
    initStdClass("include", "Include");
    initStdClass("strip-space", "Whitespace");
    initStdClass("preserve-space", "Whitespace");
    initStdClass("processing-instruction", "ProcessingInstruction");
    initStdClass("namespace-alias", "NamespaceAlias");
  }
  
  private void initStdClass(String paramString1, String paramString2) { this._instructionClasses.put(getQName("http://www.w3.org/1999/XSL/Transform", "xsl", paramString1).getStringRep(), "com.sun.org.apache.xalan.internal.xsltc.compiler." + paramString2); }
  
  public boolean elementSupported(String paramString1, String paramString2) { return (this._instructionClasses.get(getQName(paramString1, "xsl", paramString2).getStringRep()) != null); }
  
  public boolean functionSupported(String paramString) { return (this._symbolTable.lookupPrimop(paramString) != null); }
  
  private void initExtClasses() {
    initExtClass("output", "TransletOutput");
    initExtClass("http://xml.apache.org/xalan/redirect", "write", "TransletOutput");
  }
  
  private void initExtClass(String paramString1, String paramString2) { this._instructionClasses.put(getQName("http://xml.apache.org/xalan/xsltc", "translet", paramString1).getStringRep(), "com.sun.org.apache.xalan.internal.xsltc.compiler." + paramString2); }
  
  private void initExtClass(String paramString1, String paramString2, String paramString3) { this._instructionClasses.put(getQName(paramString1, "translet", paramString2).getStringRep(), "com.sun.org.apache.xalan.internal.xsltc.compiler." + paramString3); }
  
  private void initSymbolTable() {
    MethodType methodType1 = new MethodType(Type.Int, Type.Void);
    MethodType methodType2 = new MethodType(Type.Int, Type.Real);
    MethodType methodType3 = new MethodType(Type.Int, Type.String);
    MethodType methodType4 = new MethodType(Type.Int, Type.NodeSet);
    MethodType methodType5 = new MethodType(Type.Real, Type.Int);
    MethodType methodType6 = new MethodType(Type.Real, Type.Void);
    MethodType methodType7 = new MethodType(Type.Real, Type.Real);
    MethodType methodType8 = new MethodType(Type.Real, Type.NodeSet);
    MethodType methodType9 = new MethodType(Type.Real, Type.Reference);
    MethodType methodType10 = new MethodType(Type.Int, Type.Int);
    MethodType methodType11 = new MethodType(Type.NodeSet, Type.Reference);
    MethodType methodType12 = new MethodType(Type.NodeSet, Type.Void);
    MethodType methodType13 = new MethodType(Type.NodeSet, Type.String);
    MethodType methodType14 = new MethodType(Type.NodeSet, Type.NodeSet);
    MethodType methodType15 = new MethodType(Type.Node, Type.Void);
    MethodType methodType16 = new MethodType(Type.String, Type.Void);
    MethodType methodType17 = new MethodType(Type.String, Type.String);
    MethodType methodType18 = new MethodType(Type.String, Type.Node);
    MethodType methodType19 = new MethodType(Type.String, Type.NodeSet);
    MethodType methodType20 = new MethodType(Type.String, Type.Reference);
    MethodType methodType21 = new MethodType(Type.Boolean, Type.Reference);
    MethodType methodType22 = new MethodType(Type.Boolean, Type.Void);
    MethodType methodType23 = new MethodType(Type.Boolean, Type.Boolean);
    MethodType methodType24 = new MethodType(Type.Boolean, Type.String);
    MethodType methodType25 = new MethodType(Type.NodeSet, Type.Object);
    MethodType methodType26 = new MethodType(Type.Real, Type.Real, Type.Real);
    MethodType methodType27 = new MethodType(Type.Int, Type.Int, Type.Int);
    MethodType methodType28 = new MethodType(Type.Boolean, Type.Real, Type.Real);
    MethodType methodType29 = new MethodType(Type.Boolean, Type.Int, Type.Int);
    MethodType methodType30 = new MethodType(Type.String, Type.String, Type.String);
    MethodType methodType31 = new MethodType(Type.String, Type.Real, Type.String);
    MethodType methodType32 = new MethodType(Type.String, Type.String, Type.Real);
    MethodType methodType33 = new MethodType(Type.Reference, Type.String, Type.Reference);
    MethodType methodType34 = new MethodType(Type.NodeSet, Type.String, Type.String);
    MethodType methodType35 = new MethodType(Type.NodeSet, Type.String, Type.NodeSet);
    MethodType methodType36 = new MethodType(Type.Boolean, Type.Boolean, Type.Boolean);
    MethodType methodType37 = new MethodType(Type.Boolean, Type.String, Type.String);
    MethodType methodType38 = new MethodType(Type.String, Type.String, Type.NodeSet);
    MethodType methodType39 = new MethodType(Type.String, Type.Real, Type.String, Type.String);
    MethodType methodType40 = new MethodType(Type.String, Type.String, Type.Real, Type.Real);
    MethodType methodType41 = new MethodType(Type.String, Type.String, Type.String, Type.String);
    this._symbolTable.addPrimop("current", methodType15);
    this._symbolTable.addPrimop("last", methodType1);
    this._symbolTable.addPrimop("position", methodType1);
    this._symbolTable.addPrimop("true", methodType22);
    this._symbolTable.addPrimop("false", methodType22);
    this._symbolTable.addPrimop("not", methodType23);
    this._symbolTable.addPrimop("name", methodType16);
    this._symbolTable.addPrimop("name", methodType18);
    this._symbolTable.addPrimop("generate-id", methodType16);
    this._symbolTable.addPrimop("generate-id", methodType18);
    this._symbolTable.addPrimop("ceiling", methodType7);
    this._symbolTable.addPrimop("floor", methodType7);
    this._symbolTable.addPrimop("round", methodType7);
    this._symbolTable.addPrimop("contains", methodType37);
    this._symbolTable.addPrimop("number", methodType9);
    this._symbolTable.addPrimop("number", methodType6);
    this._symbolTable.addPrimop("boolean", methodType21);
    this._symbolTable.addPrimop("string", methodType20);
    this._symbolTable.addPrimop("string", methodType16);
    this._symbolTable.addPrimop("translate", methodType41);
    this._symbolTable.addPrimop("string-length", methodType1);
    this._symbolTable.addPrimop("string-length", methodType3);
    this._symbolTable.addPrimop("starts-with", methodType37);
    this._symbolTable.addPrimop("format-number", methodType31);
    this._symbolTable.addPrimop("format-number", methodType39);
    this._symbolTable.addPrimop("unparsed-entity-uri", methodType17);
    this._symbolTable.addPrimop("key", methodType34);
    this._symbolTable.addPrimop("key", methodType35);
    this._symbolTable.addPrimop("id", methodType13);
    this._symbolTable.addPrimop("id", methodType14);
    this._symbolTable.addPrimop("namespace-uri", methodType16);
    this._symbolTable.addPrimop("function-available", methodType24);
    this._symbolTable.addPrimop("element-available", methodType24);
    this._symbolTable.addPrimop("document", methodType13);
    this._symbolTable.addPrimop("document", methodType12);
    this._symbolTable.addPrimop("count", methodType4);
    this._symbolTable.addPrimop("sum", methodType8);
    this._symbolTable.addPrimop("local-name", methodType16);
    this._symbolTable.addPrimop("local-name", methodType19);
    this._symbolTable.addPrimop("namespace-uri", methodType16);
    this._symbolTable.addPrimop("namespace-uri", methodType19);
    this._symbolTable.addPrimop("substring", methodType32);
    this._symbolTable.addPrimop("substring", methodType40);
    this._symbolTable.addPrimop("substring-after", methodType30);
    this._symbolTable.addPrimop("substring-before", methodType30);
    this._symbolTable.addPrimop("normalize-space", methodType16);
    this._symbolTable.addPrimop("normalize-space", methodType17);
    this._symbolTable.addPrimop("system-property", methodType17);
    this._symbolTable.addPrimop("nodeset", methodType11);
    this._symbolTable.addPrimop("objectType", methodType20);
    this._symbolTable.addPrimop("cast", methodType33);
    this._symbolTable.addPrimop("+", methodType26);
    this._symbolTable.addPrimop("-", methodType26);
    this._symbolTable.addPrimop("*", methodType26);
    this._symbolTable.addPrimop("/", methodType26);
    this._symbolTable.addPrimop("%", methodType26);
    this._symbolTable.addPrimop("+", methodType27);
    this._symbolTable.addPrimop("-", methodType27);
    this._symbolTable.addPrimop("*", methodType27);
    this._symbolTable.addPrimop("<", methodType28);
    this._symbolTable.addPrimop("<=", methodType28);
    this._symbolTable.addPrimop(">", methodType28);
    this._symbolTable.addPrimop(">=", methodType28);
    this._symbolTable.addPrimop("<", methodType29);
    this._symbolTable.addPrimop("<=", methodType29);
    this._symbolTable.addPrimop(">", methodType29);
    this._symbolTable.addPrimop(">=", methodType29);
    this._symbolTable.addPrimop("<", methodType36);
    this._symbolTable.addPrimop("<=", methodType36);
    this._symbolTable.addPrimop(">", methodType36);
    this._symbolTable.addPrimop(">=", methodType36);
    this._symbolTable.addPrimop("or", methodType36);
    this._symbolTable.addPrimop("and", methodType36);
    this._symbolTable.addPrimop("u-", methodType7);
    this._symbolTable.addPrimop("u-", methodType10);
  }
  
  public SymbolTable getSymbolTable() { return this._symbolTable; }
  
  public Template getTemplate() { return this._template; }
  
  public void setTemplate(Template paramTemplate) { this._template = paramTemplate; }
  
  public int getTemplateIndex() { return this._templateIndex++; }
  
  public SyntaxTreeNode makeInstance(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) {
    SyntaxTreeNode syntaxTreeNode = null;
    QName qName = getQName(paramString1, paramString2, paramString3);
    String str = (String)this._instructionClasses.get(qName.getStringRep());
    if (str != null) {
      try {
        Class clazz = ObjectFactory.findProviderClass(str, true);
        syntaxTreeNode = (SyntaxTreeNode)clazz.newInstance();
        syntaxTreeNode.setQName(qName);
        syntaxTreeNode.setParser(this);
        if (this._locator != null)
          syntaxTreeNode.setLineNumber(getLineNumber()); 
        if (syntaxTreeNode instanceof Stylesheet)
          this._xsltc.setStylesheet((Stylesheet)syntaxTreeNode); 
        checkForSuperfluousAttributes(syntaxTreeNode, paramAttributes);
      } catch (ClassNotFoundException classNotFoundException) {
        ErrorMsg errorMsg = new ErrorMsg("CLASS_NOT_FOUND_ERR", syntaxTreeNode);
        reportError(3, errorMsg);
      } catch (Exception exception) {
        ErrorMsg errorMsg = new ErrorMsg("INTERNAL_ERR", exception.getMessage(), syntaxTreeNode);
        reportError(2, errorMsg);
      } 
    } else {
      if (paramString1 != null)
        if (paramString1.equals("http://www.w3.org/1999/XSL/Transform")) {
          syntaxTreeNode = new UnsupportedElement(paramString1, paramString2, paramString3, false);
          UnsupportedElement unsupportedElement = (UnsupportedElement)syntaxTreeNode;
          ErrorMsg errorMsg = new ErrorMsg("UNSUPPORTED_XSL_ERR", getLineNumber(), paramString3);
          unsupportedElement.setErrorMessage(errorMsg);
          if (this.versionIsOne)
            reportError(1, errorMsg); 
        } else if (paramString1.equals("http://xml.apache.org/xalan/xsltc")) {
          syntaxTreeNode = new UnsupportedElement(paramString1, paramString2, paramString3, true);
          UnsupportedElement unsupportedElement = (UnsupportedElement)syntaxTreeNode;
          ErrorMsg errorMsg = new ErrorMsg("UNSUPPORTED_EXT_ERR", getLineNumber(), paramString3);
          unsupportedElement.setErrorMessage(errorMsg);
        } else {
          Stylesheet stylesheet = this._xsltc.getStylesheet();
          if (stylesheet != null && stylesheet.isExtension(paramString1) && stylesheet != this._parentStack.peek()) {
            syntaxTreeNode = new UnsupportedElement(paramString1, paramString2, paramString3, true);
            UnsupportedElement unsupportedElement = (UnsupportedElement)syntaxTreeNode;
            ErrorMsg errorMsg = new ErrorMsg("UNSUPPORTED_EXT_ERR", getLineNumber(), paramString2 + ":" + paramString3);
            unsupportedElement.setErrorMessage(errorMsg);
          } 
        }  
      if (syntaxTreeNode == null) {
        syntaxTreeNode = new LiteralElement();
        syntaxTreeNode.setLineNumber(getLineNumber());
      } 
    } 
    if (syntaxTreeNode != null && syntaxTreeNode instanceof LiteralElement)
      ((LiteralElement)syntaxTreeNode).setQName(qName); 
    return syntaxTreeNode;
  }
  
  private void checkForSuperfluousAttributes(SyntaxTreeNode paramSyntaxTreeNode, Attributes paramAttributes) {
    QName qName = paramSyntaxTreeNode.getQName();
    boolean bool = paramSyntaxTreeNode instanceof Stylesheet;
    String[] arrayOfString = (String[])this._instructionAttrs.get(qName.getStringRep());
    if (this.versionIsOne && arrayOfString != null) {
      int i = paramAttributes.getLength();
      for (byte b = 0; b < i; b++) {
        String str = paramAttributes.getQName(b);
        if (bool && str.equals("version"))
          this.versionIsOne = paramAttributes.getValue(b).equals("1.0"); 
        if (!str.startsWith("xml") && str.indexOf(':') <= 0) {
          byte b1;
          for (b1 = 0; b1 < arrayOfString.length && !str.equalsIgnoreCase(arrayOfString[b1]); b1++);
          if (b1 == arrayOfString.length) {
            ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_ATTRIBUTE_ERR", str, paramSyntaxTreeNode);
            errorMsg.setWarningError(true);
            reportError(4, errorMsg);
          } 
        } 
      } 
    } 
  }
  
  public Expression parseExpression(SyntaxTreeNode paramSyntaxTreeNode, String paramString) { return (Expression)parseTopLevel(paramSyntaxTreeNode, "<EXPRESSION>" + paramString, null); }
  
  public Expression parseExpression(SyntaxTreeNode paramSyntaxTreeNode, String paramString1, String paramString2) {
    String str = paramSyntaxTreeNode.getAttribute(paramString1);
    if (str.length() == 0 && paramString2 != null)
      str = paramString2; 
    return (Expression)parseTopLevel(paramSyntaxTreeNode, "<EXPRESSION>" + str, str);
  }
  
  public Pattern parsePattern(SyntaxTreeNode paramSyntaxTreeNode, String paramString) { return (Pattern)parseTopLevel(paramSyntaxTreeNode, "<PATTERN>" + paramString, paramString); }
  
  public Pattern parsePattern(SyntaxTreeNode paramSyntaxTreeNode, String paramString1, String paramString2) {
    String str = paramSyntaxTreeNode.getAttribute(paramString1);
    if (str.length() == 0 && paramString2 != null)
      str = paramString2; 
    return (Pattern)parseTopLevel(paramSyntaxTreeNode, "<PATTERN>" + str, str);
  }
  
  private SyntaxTreeNode parseTopLevel(SyntaxTreeNode paramSyntaxTreeNode, String paramString1, String paramString2) {
    int i = getLineNumber();
    try {
      this._xpathParser.setScanner(new XPathLexer(new StringReader(paramString1)));
      Symbol symbol = this._xpathParser.parse(paramString2, i);
      if (symbol != null) {
        SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)symbol.value;
        if (syntaxTreeNode != null) {
          syntaxTreeNode.setParser(this);
          syntaxTreeNode.setParent(paramSyntaxTreeNode);
          syntaxTreeNode.setLineNumber(i);
          return syntaxTreeNode;
        } 
      } 
      reportError(3, new ErrorMsg("XPATH_PARSER_ERR", paramString2, paramSyntaxTreeNode));
    } catch (Exception exception) {
      if (this._xsltc.debug())
        exception.printStackTrace(); 
      reportError(3, new ErrorMsg("XPATH_PARSER_ERR", paramString2, paramSyntaxTreeNode));
    } 
    SyntaxTreeNode.Dummy.setParser(this);
    return SyntaxTreeNode.Dummy;
  }
  
  public boolean errorsFound() { return (this._errors.size() > 0); }
  
  public void printErrors() {
    int i = this._errors.size();
    if (i > 0) {
      System.err.println(new ErrorMsg("COMPILER_ERROR_KEY"));
      for (byte b = 0; b < i; b++)
        System.err.println("  " + this._errors.get(b)); 
    } 
  }
  
  public void printWarnings() {
    int i = this._warnings.size();
    if (i > 0) {
      System.err.println(new ErrorMsg("COMPILER_WARNING_KEY"));
      for (byte b = 0; b < i; b++)
        System.err.println("  " + this._warnings.get(b)); 
    } 
  }
  
  public void reportError(int paramInt, ErrorMsg paramErrorMsg) {
    switch (paramInt) {
      case 0:
        this._errors.add(paramErrorMsg);
        break;
      case 1:
        this._errors.add(paramErrorMsg);
        break;
      case 2:
        this._errors.add(paramErrorMsg);
        break;
      case 3:
        this._errors.add(paramErrorMsg);
        break;
      case 4:
        this._warnings.add(paramErrorMsg);
        break;
    } 
  }
  
  public ArrayList<ErrorMsg> getErrors() { return this._errors; }
  
  public ArrayList<ErrorMsg> getWarnings() { return this._warnings; }
  
  public void startDocument() {
    this._root = null;
    this._target = null;
    this._prefixMapping = null;
    this._parentStack = new Stack();
  }
  
  public void endDocument() {}
  
  public void startPrefixMapping(String paramString1, String paramString2) {
    if (this._prefixMapping == null)
      this._prefixMapping = new HashMap(); 
    this._prefixMapping.put(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString) {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    int i = paramString3.lastIndexOf(':');
    String str = (i == -1) ? null : paramString3.substring(0, i);
    SyntaxTreeNode syntaxTreeNode = makeInstance(paramString1, str, paramString2, paramAttributes);
    if (syntaxTreeNode == null) {
      ErrorMsg errorMsg = new ErrorMsg("ELEMENT_PARSE_ERR", str + ':' + paramString2);
      throw new SAXException(errorMsg.toString());
    } 
    if (this._root == null) {
      if (this._prefixMapping == null || !this._prefixMapping.containsValue("http://www.w3.org/1999/XSL/Transform")) {
        this._rootNamespaceDef = false;
      } else {
        this._rootNamespaceDef = true;
      } 
      this._root = syntaxTreeNode;
    } else {
      SyntaxTreeNode syntaxTreeNode1 = (SyntaxTreeNode)this._parentStack.peek();
      syntaxTreeNode1.addElement(syntaxTreeNode);
      syntaxTreeNode.setParent(syntaxTreeNode1);
    } 
    syntaxTreeNode.setAttributes(new AttributesImpl(paramAttributes));
    syntaxTreeNode.setPrefixMapping(this._prefixMapping);
    if (syntaxTreeNode instanceof Stylesheet) {
      getSymbolTable().setCurrentNode(syntaxTreeNode);
      ((Stylesheet)syntaxTreeNode).declareExtensionPrefixes(this);
    } 
    this._prefixMapping = null;
    this._parentStack.push(syntaxTreeNode);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) { this._parentStack.pop(); }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    String str = new String(paramArrayOfChar, paramInt1, paramInt2);
    SyntaxTreeNode syntaxTreeNode1 = (SyntaxTreeNode)this._parentStack.peek();
    if (str.length() == 0)
      return; 
    if (syntaxTreeNode1 instanceof Text) {
      ((Text)syntaxTreeNode1).setText(str);
      return;
    } 
    if (syntaxTreeNode1 instanceof Stylesheet)
      return; 
    SyntaxTreeNode syntaxTreeNode2 = syntaxTreeNode1.lastChild();
    if (syntaxTreeNode2 != null && syntaxTreeNode2 instanceof Text) {
      Text text = (Text)syntaxTreeNode2;
      if (!text.isTextElement() && (paramInt2 > 1 || paramArrayOfChar[0] < 'Ā')) {
        text.setText(str);
        return;
      } 
    } 
    syntaxTreeNode1.addElement(new Text(str));
  }
  
  private String getTokenValue(String paramString) {
    int i = paramString.indexOf('"');
    int j = paramString.lastIndexOf('"');
    return paramString.substring(i + 1, j);
  }
  
  public void processingInstruction(String paramString1, String paramString2) {
    if (this._target == null && paramString1.equals("xml-stylesheet")) {
      String str1 = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
      StringTokenizer stringTokenizer = new StringTokenizer(paramString2);
      while (stringTokenizer.hasMoreElements()) {
        String str = (String)stringTokenizer.nextElement();
        if (str.startsWith("href")) {
          str1 = getTokenValue(str);
          continue;
        } 
        if (str.startsWith("media")) {
          str2 = getTokenValue(str);
          continue;
        } 
        if (str.startsWith("title")) {
          str3 = getTokenValue(str);
          continue;
        } 
        if (str.startsWith("charset"))
          str4 = getTokenValue(str); 
      } 
      if ((this._PImedia == null || this._PImedia.equals(str2)) && (this._PItitle == null || this._PImedia.equals(str3)) && (this._PIcharset == null || this._PImedia.equals(str4)))
        this._target = str1; 
    } 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {}
  
  public void skippedEntity(String paramString) {}
  
  public void setDocumentLocator(Locator paramLocator) { this._locator = paramLocator; }
  
  private int getLineNumber() {
    int i = 0;
    if (this._locator != null)
      i = this._locator.getLineNumber(); 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */