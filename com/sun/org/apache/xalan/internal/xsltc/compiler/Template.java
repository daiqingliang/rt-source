package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NamedMethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.List;
import java.util.Vector;

public final class Template extends TopLevelElement {
  private QName _name;
  
  private QName _mode;
  
  private Pattern _pattern;
  
  private double _priority;
  
  private int _position;
  
  private boolean _disabled = false;
  
  private boolean _compiled = false;
  
  private boolean _simplified = false;
  
  private boolean _isSimpleNamedTemplate = false;
  
  private Vector<Param> _parameters = new Vector();
  
  private Stylesheet _stylesheet = null;
  
  public boolean hasParams() { return (this._parameters.size() > 0); }
  
  public boolean isSimplified() { return this._simplified; }
  
  public void setSimplified() { this._simplified = true; }
  
  public boolean isSimpleNamedTemplate() { return this._isSimpleNamedTemplate; }
  
  public void addParameter(Param paramParam) { this._parameters.addElement(paramParam); }
  
  public Vector<Param> getParameters() { return this._parameters; }
  
  public void disable() { this._disabled = true; }
  
  public boolean disabled() { return this._disabled; }
  
  public double getPriority() { return this._priority; }
  
  public int getPosition() { return this._position; }
  
  public boolean isNamed() { return (this._name != null); }
  
  public Pattern getPattern() { return this._pattern; }
  
  public QName getName() { return this._name; }
  
  public void setName(QName paramQName) {
    if (this._name == null)
      this._name = paramQName; 
  }
  
  public QName getModeName() { return this._mode; }
  
  public int compareTo(Object paramObject) {
    Template template = (Template)paramObject;
    return (this._priority > template._priority) ? 1 : ((this._priority < template._priority) ? -1 : ((this._position > template._position) ? 1 : ((this._position < template._position) ? -1 : 0)));
  }
  
  public void display(int paramInt) {
    Util.println('\n');
    indent(paramInt);
    if (this._name != null) {
      indent(paramInt);
      Util.println("name = " + this._name);
    } else if (this._pattern != null) {
      indent(paramInt);
      Util.println("match = " + this._pattern.toString());
    } 
    if (this._mode != null) {
      indent(paramInt);
      Util.println("mode = " + this._mode);
    } 
    displayContents(paramInt + 4);
  }
  
  private boolean resolveNamedTemplates(Template paramTemplate, Parser paramParser) {
    if (paramTemplate == null)
      return true; 
    SymbolTable symbolTable = paramParser.getSymbolTable();
    int i = getImportPrecedence();
    int j = paramTemplate.getImportPrecedence();
    if (i > j) {
      paramTemplate.disable();
      return true;
    } 
    if (i < j) {
      symbolTable.addTemplate(paramTemplate);
      disable();
      return true;
    } 
    return false;
  }
  
  public Stylesheet getStylesheet() { return this._stylesheet; }
  
  public void parseContents(Parser paramParser) {
    String str1 = getAttribute("name");
    String str2 = getAttribute("mode");
    String str3 = getAttribute("match");
    String str4 = getAttribute("priority");
    this._stylesheet = super.getStylesheet();
    if (str1.length() > 0) {
      if (!XML11Char.isXML11ValidQName(str1)) {
        ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str1, this);
        paramParser.reportError(3, errorMsg);
      } 
      this._name = paramParser.getQNameIgnoreDefaultNs(str1);
    } 
    if (str2.length() > 0) {
      if (!XML11Char.isXML11ValidQName(str2)) {
        ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str2, this);
        paramParser.reportError(3, errorMsg);
      } 
      this._mode = paramParser.getQNameIgnoreDefaultNs(str2);
    } 
    if (str3.length() > 0)
      this._pattern = paramParser.parsePattern(this, "match", null); 
    if (str4.length() > 0) {
      this._priority = Double.parseDouble(str4);
    } else if (this._pattern != null) {
      this._priority = this._pattern.getPriority();
    } else {
      this._priority = NaND;
    } 
    this._position = paramParser.getTemplateIndex();
    if (this._name != null) {
      Template template = paramParser.getSymbolTable().addTemplate(this);
      if (!resolveNamedTemplates(template, paramParser)) {
        ErrorMsg errorMsg = new ErrorMsg("TEMPLATE_REDEF_ERR", this._name, this);
        paramParser.reportError(3, errorMsg);
      } 
      if (this._pattern == null && this._mode == null)
        this._isSimpleNamedTemplate = true; 
    } 
    if (this._parent instanceof Stylesheet)
      ((Stylesheet)this._parent).addTemplate(this); 
    paramParser.setTemplate(this);
    parseChildren(paramParser);
    paramParser.setTemplate(null);
  }
  
  public void parseSimplified(Stylesheet paramStylesheet, Parser paramParser) {
    this._stylesheet = paramStylesheet;
    setParent(paramStylesheet);
    this._name = null;
    this._mode = null;
    this._priority = NaND;
    this._pattern = paramParser.parsePattern(this, "/");
    List list = this._stylesheet.getContents();
    SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)list.get(0);
    if (syntaxTreeNode instanceof LiteralElement) {
      addElement(syntaxTreeNode);
      syntaxTreeNode.setParent(this);
      list.set(0, this);
      paramParser.setTemplate(this);
      syntaxTreeNode.parseContents(paramParser);
      paramParser.setTemplate(null);
    } 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._pattern != null)
      this._pattern.typeCheck(paramSymbolTable); 
    return typeCheckContents(paramSymbolTable);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (this._disabled)
      return; 
    String str = paramClassGenerator.getClassName();
    if (this._compiled && isNamed()) {
      String str1 = Util.escape(this._name.toString());
      instructionList.append(paramClassGenerator.loadTranslet());
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(paramMethodGenerator.loadIterator());
      instructionList.append(paramMethodGenerator.loadHandler());
      instructionList.append(paramMethodGenerator.loadCurrentNode());
      instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref(str, str1, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V")));
      return;
    } 
    if (this._compiled)
      return; 
    this._compiled = true;
    if (this._isSimpleNamedTemplate && paramMethodGenerator instanceof NamedMethodGenerator) {
      int i = this._parameters.size();
      NamedMethodGenerator namedMethodGenerator = (NamedMethodGenerator)paramMethodGenerator;
      for (byte b = 0; b < i; b++) {
        Param param = (Param)this._parameters.elementAt(b);
        param.setLoadInstruction(namedMethodGenerator.loadParameter(b));
        param.setStoreInstruction(namedMethodGenerator.storeParameter(b));
      } 
    } 
    translateContents(paramClassGenerator, paramMethodGenerator);
    instructionList.setPositions(true);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Template.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */