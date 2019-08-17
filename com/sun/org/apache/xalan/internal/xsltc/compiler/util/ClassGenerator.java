package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;

public class ClassGenerator extends ClassGen {
  protected static final int TRANSLET_INDEX = 0;
  
  protected static int INVALID_INDEX = -1;
  
  private Stylesheet _stylesheet;
  
  private final Parser _parser;
  
  private final Instruction _aloadTranslet;
  
  private final String _domClass;
  
  private final String _domClassSig;
  
  private final String _applyTemplatesSig;
  
  private final String _applyTemplatesSigForImport;
  
  public ClassGenerator(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString, Stylesheet paramStylesheet) {
    super(paramString1, paramString2, paramString3, paramInt, paramArrayOfString);
    this._stylesheet = paramStylesheet;
    this._parser = paramStylesheet.getParser();
    this._aloadTranslet = new ALOAD(0);
    if (paramStylesheet.isMultiDocument()) {
      this._domClass = "com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM";
      this._domClassSig = "Lcom/sun/org/apache/xalan/internal/xsltc/dom/MultiDOM;";
    } else {
      this._domClass = "com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter";
      this._domClassSig = "Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;";
    } 
    this._applyTemplatesSig = "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
    this._applyTemplatesSigForImport = "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V";
  }
  
  public final Parser getParser() { return this._parser; }
  
  public final Stylesheet getStylesheet() { return this._stylesheet; }
  
  public final String getClassName() { return this._stylesheet.getClassName(); }
  
  public Instruction loadTranslet() { return this._aloadTranslet; }
  
  public final String getDOMClass() { return this._domClass; }
  
  public final String getDOMClassSig() { return this._domClassSig; }
  
  public final String getApplyTemplatesSig() { return this._applyTemplatesSig; }
  
  public final String getApplyTemplatesSigForImport() { return this._applyTemplatesSigForImport; }
  
  public boolean isExternal() { return false; }
  
  public void addMethod(MethodGenerator paramMethodGenerator) {
    Method[] arrayOfMethod = paramMethodGenerator.getGeneratedMethods(this);
    for (byte b = 0; b < arrayOfMethod.length; b++)
      addMethod(arrayOfMethod[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\ClassGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */