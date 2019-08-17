package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSGrammarPool;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.LSInputList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSInput;

public final class XSLoaderImpl implements XSLoader, DOMConfiguration {
  private final XSGrammarPool fGrammarPool = new XSGrammarMerger();
  
  private final XMLSchemaLoader fSchemaLoader = new XMLSchemaLoader();
  
  public XSLoaderImpl() { this.fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool); }
  
  public DOMConfiguration getConfig() { return this; }
  
  public XSModel loadURIList(StringList paramStringList) {
    int i = paramStringList.getLength();
    try {
      this.fGrammarPool.clear();
      for (byte b = 0; b < i; b++)
        this.fSchemaLoader.loadGrammar(new XMLInputSource(null, paramStringList.item(b), null)); 
      return this.fGrammarPool.toXSModel();
    } catch (Exception exception) {
      this.fSchemaLoader.reportDOMFatalError(exception);
      return null;
    } 
  }
  
  public XSModel loadInputList(LSInputList paramLSInputList) {
    int i = paramLSInputList.getLength();
    try {
      this.fGrammarPool.clear();
      for (byte b = 0; b < i; b++)
        this.fSchemaLoader.loadGrammar(this.fSchemaLoader.dom2xmlInputSource(paramLSInputList.item(b))); 
      return this.fGrammarPool.toXSModel();
    } catch (Exception exception) {
      this.fSchemaLoader.reportDOMFatalError(exception);
      return null;
    } 
  }
  
  public XSModel loadURI(String paramString) {
    try {
      this.fGrammarPool.clear();
      return ((XSGrammar)this.fSchemaLoader.loadGrammar(new XMLInputSource(null, paramString, null))).toXSModel();
    } catch (Exception exception) {
      this.fSchemaLoader.reportDOMFatalError(exception);
      return null;
    } 
  }
  
  public XSModel load(LSInput paramLSInput) {
    try {
      this.fGrammarPool.clear();
      return ((XSGrammar)this.fSchemaLoader.loadGrammar(this.fSchemaLoader.dom2xmlInputSource(paramLSInput))).toXSModel();
    } catch (Exception exception) {
      this.fSchemaLoader.reportDOMFatalError(exception);
      return null;
    } 
  }
  
  public void setParameter(String paramString, Object paramObject) throws DOMException { this.fSchemaLoader.setParameter(paramString, paramObject); }
  
  public Object getParameter(String paramString) throws DOMException { return this.fSchemaLoader.getParameter(paramString); }
  
  public boolean canSetParameter(String paramString, Object paramObject) { return this.fSchemaLoader.canSetParameter(paramString, paramObject); }
  
  public DOMStringList getParameterNames() { return this.fSchemaLoader.getParameterNames(); }
  
  private static final class XSGrammarMerger extends XSGrammarPool {
    public void putGrammar(Grammar param1Grammar) {
      SchemaGrammar schemaGrammar = toSchemaGrammar(super.getGrammar(param1Grammar.getGrammarDescription()));
      if (schemaGrammar != null) {
        SchemaGrammar schemaGrammar1 = toSchemaGrammar(param1Grammar);
        if (schemaGrammar1 != null)
          mergeSchemaGrammars(schemaGrammar, schemaGrammar1); 
      } else {
        super.putGrammar(param1Grammar);
      } 
    }
    
    private SchemaGrammar toSchemaGrammar(Grammar param1Grammar) { return (param1Grammar instanceof SchemaGrammar) ? (SchemaGrammar)param1Grammar : null; }
    
    private void mergeSchemaGrammars(SchemaGrammar param1SchemaGrammar1, SchemaGrammar param1SchemaGrammar2) {
      XSNamedMap xSNamedMap = param1SchemaGrammar2.getComponents((short)2);
      int i = xSNamedMap.getLength();
      byte b1;
      for (b1 = 0; b1 < i; b1++) {
        XSElementDecl xSElementDecl = (XSElementDecl)xSNamedMap.item(b1);
        if (param1SchemaGrammar1.getGlobalElementDecl(xSElementDecl.getName()) == null)
          param1SchemaGrammar1.addGlobalElementDecl(xSElementDecl); 
      } 
      xSNamedMap = param1SchemaGrammar2.getComponents((short)1);
      i = xSNamedMap.getLength();
      for (b1 = 0; b1 < i; b1++) {
        XSAttributeDecl xSAttributeDecl = (XSAttributeDecl)xSNamedMap.item(b1);
        if (param1SchemaGrammar1.getGlobalAttributeDecl(xSAttributeDecl.getName()) == null)
          param1SchemaGrammar1.addGlobalAttributeDecl(xSAttributeDecl); 
      } 
      xSNamedMap = param1SchemaGrammar2.getComponents((short)3);
      i = xSNamedMap.getLength();
      for (b1 = 0; b1 < i; b1++) {
        XSTypeDefinition xSTypeDefinition = (XSTypeDefinition)xSNamedMap.item(b1);
        if (param1SchemaGrammar1.getGlobalTypeDecl(xSTypeDefinition.getName()) == null)
          param1SchemaGrammar1.addGlobalTypeDecl(xSTypeDefinition); 
      } 
      xSNamedMap = param1SchemaGrammar2.getComponents((short)5);
      i = xSNamedMap.getLength();
      for (b1 = 0; b1 < i; b1++) {
        XSAttributeGroupDecl xSAttributeGroupDecl = (XSAttributeGroupDecl)xSNamedMap.item(b1);
        if (param1SchemaGrammar1.getGlobalAttributeGroupDecl(xSAttributeGroupDecl.getName()) == null)
          param1SchemaGrammar1.addGlobalAttributeGroupDecl(xSAttributeGroupDecl); 
      } 
      xSNamedMap = param1SchemaGrammar2.getComponents((short)7);
      i = xSNamedMap.getLength();
      for (b1 = 0; b1 < i; b1++) {
        XSGroupDecl xSGroupDecl = (XSGroupDecl)xSNamedMap.item(b1);
        if (param1SchemaGrammar1.getGlobalGroupDecl(xSGroupDecl.getName()) == null)
          param1SchemaGrammar1.addGlobalGroupDecl(xSGroupDecl); 
      } 
      xSNamedMap = param1SchemaGrammar2.getComponents((short)11);
      i = xSNamedMap.getLength();
      for (b1 = 0; b1 < i; b1++) {
        XSNotationDecl xSNotationDecl = (XSNotationDecl)xSNamedMap.item(b1);
        if (param1SchemaGrammar1.getGlobalNotationDecl(xSNotationDecl.getName()) == null)
          param1SchemaGrammar1.addGlobalNotationDecl(xSNotationDecl); 
      } 
      XSObjectList xSObjectList = param1SchemaGrammar2.getAnnotations();
      i = xSObjectList.getLength();
      for (byte b2 = 0; b2 < i; b2++)
        param1SchemaGrammar1.addAnnotation((XSAnnotationImpl)xSObjectList.item(b2)); 
    }
    
    public boolean containsGrammar(XMLGrammarDescription param1XMLGrammarDescription) { return false; }
    
    public Grammar getGrammar(XMLGrammarDescription param1XMLGrammarDescription) { return null; }
    
    public Grammar retrieveGrammar(XMLGrammarDescription param1XMLGrammarDescription) { return null; }
    
    public Grammar[] retrieveInitialGrammarSet(String param1String) { return new Grammar[0]; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSLoaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */