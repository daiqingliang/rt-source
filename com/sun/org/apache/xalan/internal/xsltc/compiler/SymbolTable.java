package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

final class SymbolTable {
  private final Map<String, Stylesheet> _stylesheets = new HashMap();
  
  private final Map<String, Vector> _primops = new HashMap();
  
  private Map<String, VariableBase> _variables = null;
  
  private Map<String, Template> _templates = null;
  
  private Map<String, AttributeSet> _attributeSets = null;
  
  private Map<String, String> _aliases = null;
  
  private Map<String, Integer> _excludedURI = null;
  
  private Stack<Map<String, Integer>> _excludedURIStack = null;
  
  private Map<String, DecimalFormatting> _decimalFormats = null;
  
  private Map<String, Key> _keys = null;
  
  private int _nsCounter = 0;
  
  private SyntaxTreeNode _current = null;
  
  public DecimalFormatting getDecimalFormatting(QName paramQName) { return (this._decimalFormats == null) ? null : (DecimalFormatting)this._decimalFormats.get(paramQName.getStringRep()); }
  
  public void addDecimalFormatting(QName paramQName, DecimalFormatting paramDecimalFormatting) {
    if (this._decimalFormats == null)
      this._decimalFormats = new HashMap(); 
    this._decimalFormats.put(paramQName.getStringRep(), paramDecimalFormatting);
  }
  
  public Key getKey(QName paramQName) { return (this._keys == null) ? null : (Key)this._keys.get(paramQName.getStringRep()); }
  
  public void addKey(QName paramQName, Key paramKey) {
    if (this._keys == null)
      this._keys = new HashMap(); 
    this._keys.put(paramQName.getStringRep(), paramKey);
  }
  
  public Stylesheet addStylesheet(QName paramQName, Stylesheet paramStylesheet) { return (Stylesheet)this._stylesheets.put(paramQName.getStringRep(), paramStylesheet); }
  
  public Stylesheet lookupStylesheet(QName paramQName) { return (Stylesheet)this._stylesheets.get(paramQName.getStringRep()); }
  
  public Template addTemplate(Template paramTemplate) {
    QName qName = paramTemplate.getName();
    if (this._templates == null)
      this._templates = new HashMap(); 
    return (Template)this._templates.put(qName.getStringRep(), paramTemplate);
  }
  
  public Template lookupTemplate(QName paramQName) { return (this._templates == null) ? null : (Template)this._templates.get(paramQName.getStringRep()); }
  
  public Variable addVariable(Variable paramVariable) {
    if (this._variables == null)
      this._variables = new HashMap(); 
    String str = paramVariable.getName().getStringRep();
    return (Variable)this._variables.put(str, paramVariable);
  }
  
  public Param addParam(Param paramParam) {
    if (this._variables == null)
      this._variables = new HashMap(); 
    String str = paramParam.getName().getStringRep();
    return (Param)this._variables.put(str, paramParam);
  }
  
  public Variable lookupVariable(QName paramQName) {
    if (this._variables == null)
      return null; 
    String str = paramQName.getStringRep();
    VariableBase variableBase = (VariableBase)this._variables.get(str);
    return (variableBase instanceof Variable) ? (Variable)variableBase : null;
  }
  
  public Param lookupParam(QName paramQName) {
    if (this._variables == null)
      return null; 
    String str = paramQName.getStringRep();
    VariableBase variableBase = (VariableBase)this._variables.get(str);
    return (variableBase instanceof Param) ? (Param)variableBase : null;
  }
  
  public SyntaxTreeNode lookupName(QName paramQName) {
    if (this._variables == null)
      return null; 
    String str = paramQName.getStringRep();
    return (SyntaxTreeNode)this._variables.get(str);
  }
  
  public AttributeSet addAttributeSet(AttributeSet paramAttributeSet) {
    if (this._attributeSets == null)
      this._attributeSets = new HashMap(); 
    return (AttributeSet)this._attributeSets.put(paramAttributeSet.getName().getStringRep(), paramAttributeSet);
  }
  
  public AttributeSet lookupAttributeSet(QName paramQName) { return (this._attributeSets == null) ? null : (AttributeSet)this._attributeSets.get(paramQName.getStringRep()); }
  
  public void addPrimop(String paramString, MethodType paramMethodType) {
    Vector vector = (Vector)this._primops.get(paramString);
    if (vector == null)
      this._primops.put(paramString, vector = new Vector()); 
    vector.addElement(paramMethodType);
  }
  
  public Vector lookupPrimop(String paramString) { return (Vector)this._primops.get(paramString); }
  
  public String generateNamespacePrefix() { return "ns" + this._nsCounter++; }
  
  public void setCurrentNode(SyntaxTreeNode paramSyntaxTreeNode) { this._current = paramSyntaxTreeNode; }
  
  public String lookupNamespace(String paramString) { return (this._current == null) ? "" : this._current.lookupNamespace(paramString); }
  
  public void addPrefixAlias(String paramString1, String paramString2) {
    if (this._aliases == null)
      this._aliases = new HashMap(); 
    this._aliases.put(paramString1, paramString2);
  }
  
  public String lookupPrefixAlias(String paramString) { return (this._aliases == null) ? null : (String)this._aliases.get(paramString); }
  
  public void excludeURI(String paramString) {
    if (paramString == null)
      return; 
    if (this._excludedURI == null)
      this._excludedURI = new HashMap(); 
    Integer integer = (Integer)this._excludedURI.get(paramString);
    if (integer == null) {
      integer = Integer.valueOf(1);
    } else {
      integer = Integer.valueOf(integer.intValue() + 1);
    } 
    this._excludedURI.put(paramString, integer);
  }
  
  public void excludeNamespaces(String paramString) {
    if (paramString != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString);
      while (stringTokenizer.hasMoreTokens()) {
        String str2;
        String str1 = stringTokenizer.nextToken();
        if (str1.equals("#default")) {
          str2 = lookupNamespace("");
        } else {
          str2 = lookupNamespace(str1);
        } 
        if (str2 != null)
          excludeURI(str2); 
      } 
    } 
  }
  
  public boolean isExcludedNamespace(String paramString) {
    if (paramString != null && this._excludedURI != null) {
      Integer integer = (Integer)this._excludedURI.get(paramString);
      return (integer != null && integer.intValue() > 0);
    } 
    return false;
  }
  
  public void unExcludeNamespaces(String paramString) {
    if (this._excludedURI == null)
      return; 
    if (paramString != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString);
      while (stringTokenizer.hasMoreTokens()) {
        String str2;
        String str1 = stringTokenizer.nextToken();
        if (str1.equals("#default")) {
          str2 = lookupNamespace("");
        } else {
          str2 = lookupNamespace(str1);
        } 
        Integer integer = (Integer)this._excludedURI.get(str2);
        if (integer != null)
          this._excludedURI.put(str2, Integer.valueOf(integer.intValue() - 1)); 
      } 
    } 
  }
  
  public void pushExcludedNamespacesContext() {
    if (this._excludedURIStack == null)
      this._excludedURIStack = new Stack(); 
    this._excludedURIStack.push(this._excludedURI);
    this._excludedURI = null;
  }
  
  public void popExcludedNamespacesContext() {
    this._excludedURI = (Map)this._excludedURIStack.pop();
    if (this._excludedURIStack.isEmpty())
      this._excludedURIStack = null; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\SymbolTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */