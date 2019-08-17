package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.BasicType;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.FieldGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.PUTSTATIC;
import com.sun.org.apache.bcel.internal.generic.TargetLostException;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.util.InstructionFinder;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public final class Stylesheet extends SyntaxTreeNode {
  private String _version;
  
  private QName _name;
  
  private String _systemId;
  
  private Stylesheet _parentStylesheet;
  
  private Vector _globals = new Vector();
  
  private Boolean _hasLocalParams = null;
  
  private String _className;
  
  private final Vector _templates = new Vector();
  
  private Vector _allValidTemplates = null;
  
  private int _nextModeSerial = 1;
  
  private final Map<String, Mode> _modes = new HashMap();
  
  private Mode _defaultMode;
  
  private final Map<String, String> _extensions = new HashMap();
  
  public Stylesheet _importedFrom = null;
  
  public Stylesheet _includedFrom = null;
  
  private Vector _includedStylesheets = null;
  
  private int _importPrecedence = 1;
  
  private int _minimumDescendantPrecedence = -1;
  
  private Map<String, Key> _keys = new HashMap();
  
  private SourceLoader _loader = null;
  
  private boolean _numberFormattingUsed = false;
  
  private boolean _simplified = false;
  
  private boolean _multiDocument = false;
  
  private boolean _callsNodeset = false;
  
  private boolean _hasIdCall = false;
  
  private boolean _templateInlining = false;
  
  private Output _lastOutputElement = null;
  
  private Properties _outputProperties = null;
  
  private int _outputMethod = 0;
  
  public static final int UNKNOWN_OUTPUT = 0;
  
  public static final int XML_OUTPUT = 1;
  
  public static final int HTML_OUTPUT = 2;
  
  public static final int TEXT_OUTPUT = 3;
  
  public int getOutputMethod() { return this._outputMethod; }
  
  private void checkOutputMethod() {
    if (this._lastOutputElement != null) {
      String str = this._lastOutputElement.getOutputMethod();
      if (str != null)
        if (str.equals("xml")) {
          this._outputMethod = 1;
        } else if (str.equals("html")) {
          this._outputMethod = 2;
        } else if (str.equals("text")) {
          this._outputMethod = 3;
        }  
    } 
  }
  
  public boolean getTemplateInlining() { return this._templateInlining; }
  
  public void setTemplateInlining(boolean paramBoolean) { this._templateInlining = paramBoolean; }
  
  public boolean isSimplified() { return this._simplified; }
  
  public void setSimplified() { this._simplified = true; }
  
  public void setHasIdCall(boolean paramBoolean) { this._hasIdCall = paramBoolean; }
  
  public void setOutputProperty(String paramString1, String paramString2) {
    if (this._outputProperties == null)
      this._outputProperties = new Properties(); 
    this._outputProperties.setProperty(paramString1, paramString2);
  }
  
  public void setOutputProperties(Properties paramProperties) { this._outputProperties = paramProperties; }
  
  public Properties getOutputProperties() { return this._outputProperties; }
  
  public Output getLastOutputElement() { return this._lastOutputElement; }
  
  public void setMultiDocument(boolean paramBoolean) { this._multiDocument = paramBoolean; }
  
  public boolean isMultiDocument() { return this._multiDocument; }
  
  public void setCallsNodeset(boolean paramBoolean) {
    if (paramBoolean)
      setMultiDocument(paramBoolean); 
    this._callsNodeset = paramBoolean;
  }
  
  public boolean callsNodeset() { return this._callsNodeset; }
  
  public void numberFormattingUsed() {
    this._numberFormattingUsed = true;
    Stylesheet stylesheet = getParentStylesheet();
    if (null != stylesheet)
      stylesheet.numberFormattingUsed(); 
  }
  
  public void setImportPrecedence(int paramInt) {
    this._importPrecedence = paramInt;
    Iterator iterator = elements();
    while (iterator.hasNext()) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)iterator.next();
      if (syntaxTreeNode instanceof Include) {
        Stylesheet stylesheet = ((Include)syntaxTreeNode).getIncludedStylesheet();
        if (stylesheet != null && stylesheet._includedFrom == this)
          stylesheet.setImportPrecedence(paramInt); 
      } 
    } 
    if (this._importedFrom != null) {
      if (this._importedFrom.getImportPrecedence() < paramInt) {
        Parser parser = getParser();
        int i = parser.getNextImportPrecedence();
        this._importedFrom.setImportPrecedence(i);
      } 
    } else if (this._includedFrom != null && this._includedFrom.getImportPrecedence() != paramInt) {
      this._includedFrom.setImportPrecedence(paramInt);
    } 
  }
  
  public int getImportPrecedence() { return this._importPrecedence; }
  
  public int getMinimumDescendantPrecedence() {
    if (this._minimumDescendantPrecedence == -1) {
      int i = getImportPrecedence();
      int j = (this._includedStylesheets != null) ? this._includedStylesheets.size() : 0;
      for (byte b = 0; b < j; b++) {
        int k = ((Stylesheet)this._includedStylesheets.elementAt(b)).getMinimumDescendantPrecedence();
        if (k < i)
          i = k; 
      } 
      this._minimumDescendantPrecedence = i;
    } 
    return this._minimumDescendantPrecedence;
  }
  
  public boolean checkForLoop(String paramString) { return (this._systemId != null && this._systemId.equals(paramString)) ? true : ((this._parentStylesheet != null) ? this._parentStylesheet.checkForLoop(paramString) : 0); }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    this._name = makeStylesheetName("__stylesheet_");
  }
  
  public void setParentStylesheet(Stylesheet paramStylesheet) { this._parentStylesheet = paramStylesheet; }
  
  public Stylesheet getParentStylesheet() { return this._parentStylesheet; }
  
  public void setImportingStylesheet(Stylesheet paramStylesheet) {
    this._importedFrom = paramStylesheet;
    paramStylesheet.addIncludedStylesheet(this);
  }
  
  public void setIncludingStylesheet(Stylesheet paramStylesheet) {
    this._includedFrom = paramStylesheet;
    paramStylesheet.addIncludedStylesheet(this);
  }
  
  public void addIncludedStylesheet(Stylesheet paramStylesheet) {
    if (this._includedStylesheets == null)
      this._includedStylesheets = new Vector(); 
    this._includedStylesheets.addElement(paramStylesheet);
  }
  
  public void setSystemId(String paramString) {
    if (paramString != null)
      this._systemId = SystemIDResolver.getAbsoluteURI(paramString); 
  }
  
  public String getSystemId() { return this._systemId; }
  
  public void setSourceLoader(SourceLoader paramSourceLoader) { this._loader = paramSourceLoader; }
  
  public SourceLoader getSourceLoader() { return this._loader; }
  
  private QName makeStylesheetName(String paramString) { return getParser().getQName(paramString + getXSLTC().nextStylesheetSerial()); }
  
  public boolean hasGlobals() { return (this._globals.size() > 0); }
  
  public boolean hasLocalParams() {
    if (this._hasLocalParams == null) {
      Vector vector = getAllValidTemplates();
      int i = vector.size();
      for (byte b = 0; b < i; b++) {
        Template template = (Template)vector.elementAt(b);
        if (template.hasParams()) {
          this._hasLocalParams = Boolean.TRUE;
          return true;
        } 
      } 
      this._hasLocalParams = Boolean.FALSE;
      return false;
    } 
    return this._hasLocalParams.booleanValue();
  }
  
  protected void addPrefixMapping(String paramString1, String paramString2) {
    if (paramString1.equals("") && paramString2.equals("http://www.w3.org/1999/xhtml"))
      return; 
    super.addPrefixMapping(paramString1, paramString2);
  }
  
  private void extensionURI(String paramString, SymbolTable paramSymbolTable) {
    if (paramString != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString);
      while (stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken();
        String str2 = lookupNamespace(str1);
        if (str2 != null)
          this._extensions.put(str2, str1); 
      } 
    } 
  }
  
  public boolean isExtension(String paramString) { return (this._extensions.get(paramString) != null); }
  
  public void declareExtensionPrefixes(Parser paramParser) {
    SymbolTable symbolTable = paramParser.getSymbolTable();
    String str = getAttribute("extension-element-prefixes");
    extensionURI(str, symbolTable);
  }
  
  public void parseContents(Parser paramParser) {
    SymbolTable symbolTable = paramParser.getSymbolTable();
    addPrefixMapping("xml", "http://www.w3.org/XML/1998/namespace");
    Stylesheet stylesheet = symbolTable.addStylesheet(this._name, this);
    if (stylesheet != null) {
      ErrorMsg errorMsg = new ErrorMsg("MULTIPLE_STYLESHEET_ERR", this);
      paramParser.reportError(3, errorMsg);
    } 
    if (this._simplified) {
      symbolTable.excludeURI("http://www.w3.org/1999/XSL/Transform");
      Template template = new Template();
      template.parseSimplified(this, paramParser);
    } else {
      parseOwnChildren(paramParser);
    } 
  }
  
  public final void parseOwnChildren(Parser paramParser) {
    SymbolTable symbolTable = paramParser.getSymbolTable();
    String str1 = getAttribute("exclude-result-prefixes");
    String str2 = getAttribute("extension-element-prefixes");
    symbolTable.pushExcludedNamespacesContext();
    symbolTable.excludeURI("http://www.w3.org/1999/XSL/Transform");
    symbolTable.excludeNamespaces(str1);
    symbolTable.excludeNamespaces(str2);
    List list = getContents();
    int i = list.size();
    byte b;
    for (b = 0; b < i; b++) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)list.get(b);
      if (syntaxTreeNode instanceof VariableBase || syntaxTreeNode instanceof NamespaceAlias) {
        paramParser.getSymbolTable().setCurrentNode(syntaxTreeNode);
        syntaxTreeNode.parseContents(paramParser);
      } 
    } 
    for (b = 0; b < i; b++) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)list.get(b);
      if (!(syntaxTreeNode instanceof VariableBase) && !(syntaxTreeNode instanceof NamespaceAlias)) {
        paramParser.getSymbolTable().setCurrentNode(syntaxTreeNode);
        syntaxTreeNode.parseContents(paramParser);
      } 
      if (!this._templateInlining && syntaxTreeNode instanceof Template) {
        Template template = (Template)syntaxTreeNode;
        String str = "template$dot$" + template.getPosition();
        template.setName(paramParser.getQName(str));
      } 
    } 
    symbolTable.popExcludedNamespacesContext();
  }
  
  public void processModes() {
    if (this._defaultMode == null)
      this._defaultMode = new Mode(null, this, ""); 
    this._defaultMode.processPatterns(this._keys);
    for (Mode mode : this._modes.values())
      mode.processPatterns(this._keys); 
  }
  
  private void compileModes(ClassGenerator paramClassGenerator) {
    this._defaultMode.compileApplyTemplates(paramClassGenerator);
    for (Mode mode : this._modes.values())
      mode.compileApplyTemplates(paramClassGenerator); 
  }
  
  public Mode getMode(QName paramQName) {
    if (paramQName == null) {
      if (this._defaultMode == null)
        this._defaultMode = new Mode(null, this, ""); 
      return this._defaultMode;
    } 
    Mode mode = (Mode)this._modes.get(paramQName.getStringRep());
    if (mode == null) {
      String str = Integer.toString(this._nextModeSerial++);
      this._modes.put(paramQName.getStringRep(), mode = new Mode(paramQName, this, str));
    } 
    return mode;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    int i = this._globals.size();
    for (byte b = 0; b < i; b++) {
      VariableBase variableBase = (VariableBase)this._globals.elementAt(b);
      variableBase.typeCheck(paramSymbolTable);
    } 
    return typeCheckContents(paramSymbolTable);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { translate(); }
  
  private void addDOMField(ClassGenerator paramClassGenerator) {
    FieldGen fieldGen = new FieldGen(1, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), "_dom", paramClassGenerator.getConstantPool());
    paramClassGenerator.addField(fieldGen.getField());
  }
  
  private void addStaticField(ClassGenerator paramClassGenerator, String paramString1, String paramString2) {
    FieldGen fieldGen = new FieldGen(12, Util.getJCRefType(paramString1), paramString2, paramClassGenerator.getConstantPool());
    paramClassGenerator.addField(fieldGen.getField());
  }
  
  public void translate() {
    this._className = getXSLTC().getClassName();
    ClassGenerator classGenerator = new ClassGenerator(this._className, "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "", 33, null, this);
    addDOMField(classGenerator);
    compileTransform(classGenerator);
    Iterator iterator = elements();
    while (iterator.hasNext()) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)iterator.next();
      if (syntaxTreeNode instanceof Template) {
        Template template = (Template)syntaxTreeNode;
        getMode(template.getModeName()).addTemplate(template);
        continue;
      } 
      if (syntaxTreeNode instanceof AttributeSet) {
        ((AttributeSet)syntaxTreeNode).translate(classGenerator, null);
        continue;
      } 
      if (syntaxTreeNode instanceof Output) {
        Output output = (Output)syntaxTreeNode;
        if (output.enabled())
          this._lastOutputElement = output; 
      } 
    } 
    checkOutputMethod();
    processModes();
    compileModes(classGenerator);
    compileStaticInitializer(classGenerator);
    compileConstructor(classGenerator, this._lastOutputElement);
    if (!getParser().errorsFound())
      getXSLTC().dumpClass(classGenerator.getJavaClass()); 
  }
  
  private void compileStaticInitializer(ClassGenerator paramClassGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = new InstructionList();
    MethodGenerator methodGenerator = new MethodGenerator(9, Type.VOID, null, null, "<clinit>", this._className, instructionList, constantPoolGen);
    addStaticField(paramClassGenerator, "[Ljava/lang/String;", "_sNamesArray");
    addStaticField(paramClassGenerator, "[Ljava/lang/String;", "_sUrisArray");
    addStaticField(paramClassGenerator, "[I", "_sTypesArray");
    addStaticField(paramClassGenerator, "[Ljava/lang/String;", "_sNamespaceArray");
    int i = getXSLTC().getCharacterDataCount();
    for (byte b1 = 0; b1 < i; b1++)
      addStaticField(paramClassGenerator, "[C", "_scharData" + b1); 
    Vector vector1 = getXSLTC().getNamesIndex();
    int j = vector1.size();
    String[] arrayOfString1 = new String[j];
    String[] arrayOfString2 = new String[j];
    int[] arrayOfInt = new int[j];
    int k;
    for (k = 0; k < j; k++) {
      String str = (String)vector1.elementAt(k);
      int i4;
      if ((i4 = str.lastIndexOf(':')) > -1)
        arrayOfString2[k] = str.substring(0, i4); 
      if (str.charAt(++i4) == '@') {
        arrayOfInt[k] = 2;
        i4++;
      } else if (str.charAt(i4) == '?') {
        arrayOfInt[k] = 13;
        i4++;
      } else {
        arrayOfInt[k] = 1;
      } 
      if (i4 == 0) {
        arrayOfString1[k] = str;
      } else {
        arrayOfString1[k] = str.substring(i4);
      } 
    } 
    methodGenerator.markChunkStart();
    instructionList.append(new PUSH(constantPoolGen, j));
    instructionList.append(new ANEWARRAY(constantPoolGen.addClass("java.lang.String")));
    k = constantPoolGen.addFieldref(this._className, "_sNamesArray", "[Ljava/lang/String;");
    instructionList.append(new PUTSTATIC(k));
    methodGenerator.markChunkEnd();
    int m;
    for (m = 0; m < j; m++) {
      String str = arrayOfString1[m];
      methodGenerator.markChunkStart();
      instructionList.append(new GETSTATIC(k));
      instructionList.append(new PUSH(constantPoolGen, m));
      instructionList.append(new PUSH(constantPoolGen, str));
      instructionList.append(AASTORE);
      methodGenerator.markChunkEnd();
    } 
    methodGenerator.markChunkStart();
    instructionList.append(new PUSH(constantPoolGen, j));
    instructionList.append(new ANEWARRAY(constantPoolGen.addClass("java.lang.String")));
    m = constantPoolGen.addFieldref(this._className, "_sUrisArray", "[Ljava/lang/String;");
    instructionList.append(new PUTSTATIC(m));
    methodGenerator.markChunkEnd();
    int n;
    for (n = 0; n < j; n++) {
      String str = arrayOfString2[n];
      methodGenerator.markChunkStart();
      instructionList.append(new GETSTATIC(m));
      instructionList.append(new PUSH(constantPoolGen, n));
      instructionList.append(new PUSH(constantPoolGen, str));
      instructionList.append(AASTORE);
      methodGenerator.markChunkEnd();
    } 
    methodGenerator.markChunkStart();
    instructionList.append(new PUSH(constantPoolGen, j));
    instructionList.append(new NEWARRAY(BasicType.INT));
    n = constantPoolGen.addFieldref(this._className, "_sTypesArray", "[I");
    instructionList.append(new PUTSTATIC(n));
    methodGenerator.markChunkEnd();
    for (byte b2 = 0; b2 < j; b2++) {
      int i4 = arrayOfInt[b2];
      methodGenerator.markChunkStart();
      instructionList.append(new GETSTATIC(n));
      instructionList.append(new PUSH(constantPoolGen, b2));
      instructionList.append(new PUSH(constantPoolGen, i4));
      instructionList.append(IASTORE);
    } 
    Vector vector2 = getXSLTC().getNamespaceIndex();
    methodGenerator.markChunkStart();
    instructionList.append(new PUSH(constantPoolGen, vector2.size()));
    instructionList.append(new ANEWARRAY(constantPoolGen.addClass("java.lang.String")));
    int i1 = constantPoolGen.addFieldref(this._className, "_sNamespaceArray", "[Ljava/lang/String;");
    instructionList.append(new PUTSTATIC(i1));
    methodGenerator.markChunkEnd();
    int i2;
    for (i2 = 0; i2 < vector2.size(); i2++) {
      String str = (String)vector2.elementAt(i2);
      methodGenerator.markChunkStart();
      instructionList.append(new GETSTATIC(i1));
      instructionList.append(new PUSH(constantPoolGen, i2));
      instructionList.append(new PUSH(constantPoolGen, str));
      instructionList.append(AASTORE);
      methodGenerator.markChunkEnd();
    } 
    i2 = getXSLTC().getCharacterDataCount();
    int i3 = constantPoolGen.addMethodref("java.lang.String", "toCharArray", "()[C");
    for (byte b3 = 0; b3 < i2; b3++) {
      methodGenerator.markChunkStart();
      instructionList.append(new PUSH(constantPoolGen, getXSLTC().getCharacterData(b3)));
      instructionList.append(new INVOKEVIRTUAL(i3));
      instructionList.append(new PUTSTATIC(constantPoolGen.addFieldref(this._className, "_scharData" + b3, "[C")));
      methodGenerator.markChunkEnd();
    } 
    instructionList.append(RETURN);
    paramClassGenerator.addMethod(methodGenerator);
  }
  
  private void compileConstructor(ClassGenerator paramClassGenerator, Output paramOutput) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = new InstructionList();
    MethodGenerator methodGenerator = new MethodGenerator(1, Type.VOID, null, null, "<init>", this._className, instructionList, constantPoolGen);
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "<init>", "()V")));
    methodGenerator.markChunkStart();
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETSTATIC(constantPoolGen.addFieldref(this._className, "_sNamesArray", "[Ljava/lang/String;")));
    instructionList.append(new PUTFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;")));
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETSTATIC(constantPoolGen.addFieldref(this._className, "_sUrisArray", "[Ljava/lang/String;")));
    instructionList.append(new PUTFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;")));
    methodGenerator.markChunkEnd();
    methodGenerator.markChunkStart();
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETSTATIC(constantPoolGen.addFieldref(this._className, "_sTypesArray", "[I")));
    instructionList.append(new PUTFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "typesArray", "[I")));
    methodGenerator.markChunkEnd();
    methodGenerator.markChunkStart();
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETSTATIC(constantPoolGen.addFieldref(this._className, "_sNamespaceArray", "[Ljava/lang/String;")));
    instructionList.append(new PUTFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;")));
    methodGenerator.markChunkEnd();
    methodGenerator.markChunkStart();
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new PUSH(constantPoolGen, 101));
    instructionList.append(new PUTFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "transletVersion", "I")));
    methodGenerator.markChunkEnd();
    if (this._hasIdCall) {
      methodGenerator.markChunkStart();
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(new PUSH(constantPoolGen, Boolean.TRUE));
      instructionList.append(new PUTFIELD(constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_hasIdCall", "Z")));
      methodGenerator.markChunkEnd();
    } 
    if (paramOutput != null) {
      methodGenerator.markChunkStart();
      paramOutput.translate(paramClassGenerator, methodGenerator);
      methodGenerator.markChunkEnd();
    } 
    if (this._numberFormattingUsed) {
      methodGenerator.markChunkStart();
      DecimalFormatting.translateDefaultDFS(paramClassGenerator, methodGenerator);
      methodGenerator.markChunkEnd();
    } 
    instructionList.append(RETURN);
    paramClassGenerator.addMethod(methodGenerator);
  }
  
  private String compileTopLevel(ClassGenerator paramClassGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    Type[] arrayOfType = { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;") };
    String[] arrayOfString = { "document", "iterator", "handler" };
    InstructionList instructionList = new InstructionList();
    MethodGenerator methodGenerator = new MethodGenerator(1, Type.VOID, arrayOfType, arrayOfString, "topLevel", this._className, instructionList, paramClassGenerator.getConstantPool());
    methodGenerator.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");
    LocalVariableGen localVariableGen = methodGenerator.addLocalVariable("current", Type.INT, null, null);
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "setFilter", "(Lcom/sun/org/apache/xalan/internal/xsltc/StripFilter;)V");
    int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    instructionList.append(methodGenerator.loadDOM());
    instructionList.append(new INVOKEINTERFACE(j, 1));
    instructionList.append(methodGenerator.nextNode());
    localVariableGen.setStart(instructionList.append(new ISTORE(localVariableGen.getIndex())));
    Vector vector1 = new Vector(this._globals);
    Iterator iterator = elements();
    while (iterator.hasNext()) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)iterator.next();
      if (syntaxTreeNode instanceof Key)
        vector1.add(syntaxTreeNode); 
    } 
    vector1 = resolveDependencies(vector1);
    int k = vector1.size();
    for (byte b = 0; b < k; b++) {
      TopLevelElement topLevelElement = (TopLevelElement)vector1.elementAt(b);
      topLevelElement.translate(paramClassGenerator, methodGenerator);
      if (topLevelElement instanceof Key) {
        Key key = (Key)topLevelElement;
        this._keys.put(key.getName(), key);
      } 
    } 
    Vector vector2 = new Vector();
    iterator = elements();
    while (iterator.hasNext()) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)iterator.next();
      if (syntaxTreeNode instanceof DecimalFormatting) {
        ((DecimalFormatting)syntaxTreeNode).translate(paramClassGenerator, methodGenerator);
        continue;
      } 
      if (syntaxTreeNode instanceof Whitespace)
        vector2.addAll(((Whitespace)syntaxTreeNode).getRules()); 
    } 
    if (vector2.size() > 0)
      Whitespace.translateRules(vector2, paramClassGenerator); 
    if (paramClassGenerator.containsMethod("stripSpace", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;II)Z") != null) {
      instructionList.append(methodGenerator.loadDOM());
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(new INVOKEINTERFACE(i, 2));
    } 
    instructionList.append(RETURN);
    paramClassGenerator.addMethod(methodGenerator);
    return "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
  }
  
  private Vector resolveDependencies(Vector paramVector) {
    Vector vector = new Vector();
    while (paramVector.size() > 0) {
      boolean bool = false;
      for (byte b = 0; b < paramVector.size(); b++) {
        TopLevelElement topLevelElement = (TopLevelElement)paramVector.elementAt(b);
        Vector vector1 = topLevelElement.getDependencies();
        if (vector1 == null || vector.containsAll(vector1)) {
          vector.addElement(topLevelElement);
          paramVector.remove(b);
          bool = true;
          continue;
        } 
      } 
      if (!bool) {
        ErrorMsg errorMsg = new ErrorMsg("CIRCULAR_VARIABLE_ERR", paramVector.toString(), this);
        getParser().reportError(3, errorMsg);
        return vector;
      } 
    } 
    return vector;
  }
  
  private String compileBuildKeys(ClassGenerator paramClassGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    Type[] arrayOfType = { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;"), Type.INT };
    String[] arrayOfString = { "document", "iterator", "handler", "current" };
    InstructionList instructionList = new InstructionList();
    MethodGenerator methodGenerator = new MethodGenerator(1, Type.VOID, arrayOfType, arrayOfString, "buildKeys", this._className, instructionList, paramClassGenerator.getConstantPool());
    methodGenerator.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");
    Iterator iterator = elements();
    while (iterator.hasNext()) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)iterator.next();
      if (syntaxTreeNode instanceof Key) {
        Key key = (Key)syntaxTreeNode;
        key.translate(paramClassGenerator, methodGenerator);
        this._keys.put(key.getName(), key);
      } 
    } 
    instructionList.append(RETURN);
    methodGenerator.stripAttributes(true);
    methodGenerator.setMaxLocals();
    methodGenerator.setMaxStack();
    methodGenerator.removeNOPs();
    paramClassGenerator.addMethod(methodGenerator.getMethod());
    return "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V";
  }
  
  private void compileTransform(ClassGenerator paramClassGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    Type[] arrayOfType = new Type[3];
    arrayOfType[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    arrayOfType[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    arrayOfType[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    String[] arrayOfString = new String[3];
    arrayOfString[0] = "document";
    arrayOfString[1] = "iterator";
    arrayOfString[2] = "handler";
    InstructionList instructionList = new InstructionList();
    MethodGenerator methodGenerator = new MethodGenerator(1, Type.VOID, arrayOfType, arrayOfString, "transform", this._className, instructionList, paramClassGenerator.getConstantPool());
    methodGenerator.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "resetPrefixIndex", "()V");
    instructionList.append(new INVOKESTATIC(i));
    LocalVariableGen localVariableGen = methodGenerator.addLocalVariable("current", Type.INT, null, null);
    String str1 = paramClassGenerator.getApplyTemplatesSig();
    int j = constantPoolGen.addMethodref(getClassName(), "applyTemplates", str1);
    int k = constantPoolGen.addFieldref(getClassName(), "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    instructionList.append(paramClassGenerator.loadTranslet());
    if (isMultiDocument()) {
      instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")));
      instructionList.append(DUP);
    } 
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(methodGenerator.loadDOM());
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "makeDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;")));
    if (isMultiDocument()) {
      int i2 = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
      instructionList.append(new INVOKESPECIAL(i2));
    } 
    instructionList.append(new PUTFIELD(k));
    int m = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    instructionList.append(methodGenerator.loadDOM());
    instructionList.append(new INVOKEINTERFACE(m, 1));
    instructionList.append(methodGenerator.nextNode());
    localVariableGen.setStart(instructionList.append(new ISTORE(localVariableGen.getIndex())));
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(methodGenerator.loadHandler());
    int n = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "transferOutputSettings", "(Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    instructionList.append(new INVOKEVIRTUAL(n));
    String str2 = compileBuildKeys(paramClassGenerator);
    int i1 = constantPoolGen.addMethodref(getClassName(), "buildKeys", str2);
    Iterator iterator = elements();
    if (this._globals.size() > 0 || iterator.hasNext()) {
      String str = compileTopLevel(paramClassGenerator);
      int i2 = constantPoolGen.addMethodref(getClassName(), "topLevel", str);
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(new GETFIELD(k));
      instructionList.append(methodGenerator.loadIterator());
      instructionList.append(methodGenerator.loadHandler());
      instructionList.append(new INVOKEVIRTUAL(i2));
    } 
    instructionList.append(methodGenerator.loadHandler());
    instructionList.append(methodGenerator.startDocument());
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETFIELD(k));
    instructionList.append(methodGenerator.loadIterator());
    instructionList.append(methodGenerator.loadHandler());
    instructionList.append(new INVOKEVIRTUAL(j));
    instructionList.append(methodGenerator.loadHandler());
    instructionList.append(methodGenerator.endDocument());
    instructionList.append(RETURN);
    paramClassGenerator.addMethod(methodGenerator);
  }
  
  private void peepHoleOptimization(MethodGenerator paramMethodGenerator) {
    String str = "`aload'`pop'`instruction'";
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    InstructionFinder instructionFinder = new InstructionFinder(instructionList);
    Iterator iterator = instructionFinder.search("`aload'`pop'`instruction'");
    while (iterator.hasNext()) {
      InstructionHandle[] arrayOfInstructionHandle = (InstructionHandle[])iterator.next();
      try {
        instructionList.delete(arrayOfInstructionHandle[0], arrayOfInstructionHandle[1]);
      } catch (TargetLostException targetLostException) {}
    } 
  }
  
  public int addParam(Param paramParam) {
    this._globals.addElement(paramParam);
    return this._globals.size() - 1;
  }
  
  public int addVariable(Variable paramVariable) {
    this._globals.addElement(paramVariable);
    return this._globals.size() - 1;
  }
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("Stylesheet");
    displayContents(paramInt + 4);
  }
  
  public String getNamespace(String paramString) { return lookupNamespace(paramString); }
  
  public String getClassName() { return this._className; }
  
  public Vector getTemplates() { return this._templates; }
  
  public Vector getAllValidTemplates() {
    if (this._includedStylesheets == null)
      return this._templates; 
    if (this._allValidTemplates == null) {
      Vector vector = new Vector();
      vector.addAll(this._templates);
      int i = this._includedStylesheets.size();
      for (byte b = 0; b < i; b++) {
        Stylesheet stylesheet = (Stylesheet)this._includedStylesheets.elementAt(b);
        vector.addAll(stylesheet.getAllValidTemplates());
      } 
      if (this._parentStylesheet != null)
        return vector; 
      this._allValidTemplates = vector;
    } 
    return this._allValidTemplates;
  }
  
  protected void addTemplate(Template paramTemplate) { this._templates.addElement(paramTemplate); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Stylesheet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */