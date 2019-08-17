package com.sun.xml.internal.stream.dtd;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLAttributeDecl;

public class DTDGrammarUtil {
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  private static final boolean DEBUG_ATTRIBUTES = false;
  
  private static final boolean DEBUG_ELEMENT_CHILDREN = false;
  
  protected DTDGrammar fDTDGrammar = null;
  
  protected boolean fNamespaces;
  
  protected SymbolTable fSymbolTable = null;
  
  private int fCurrentElementIndex = -1;
  
  private int fCurrentContentSpecType = -1;
  
  private boolean[] fElementContentState = new boolean[8];
  
  private int fElementDepth = -1;
  
  private boolean fInElementContent = false;
  
  private XMLAttributeDecl fTempAttDecl = new XMLAttributeDecl();
  
  private QName fTempQName = new QName();
  
  private StringBuffer fBuffer = new StringBuffer();
  
  private NamespaceContext fNamespaceContext = null;
  
  public DTDGrammarUtil(SymbolTable paramSymbolTable) { this.fSymbolTable = paramSymbolTable; }
  
  public DTDGrammarUtil(DTDGrammar paramDTDGrammar, SymbolTable paramSymbolTable) {
    this.fDTDGrammar = paramDTDGrammar;
    this.fSymbolTable = paramSymbolTable;
  }
  
  public DTDGrammarUtil(DTDGrammar paramDTDGrammar, SymbolTable paramSymbolTable, NamespaceContext paramNamespaceContext) {
    this.fDTDGrammar = paramDTDGrammar;
    this.fSymbolTable = paramSymbolTable;
    this.fNamespaceContext = paramNamespaceContext;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager) throws XMLConfigurationException {
    this.fDTDGrammar = null;
    this.fInElementContent = false;
    this.fCurrentElementIndex = -1;
    this.fCurrentContentSpecType = -1;
    this.fNamespaces = paramXMLComponentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    this.fSymbolTable = (SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fElementDepth = -1;
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes) throws XNIException { handleStartElement(paramQName, paramXMLAttributes); }
  
  public void endElement(QName paramQName) throws XNIException { handleEndElement(paramQName); }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {}
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {}
  
  public void addDTDDefaultAttrs(QName paramQName, XMLAttributes paramXMLAttributes) throws XNIException {
    int i = this.fDTDGrammar.getElementDeclIndex(paramQName);
    if (i == -1 || this.fDTDGrammar == null)
      return; 
    int j;
    for (j = this.fDTDGrammar.getFirstAttributeDeclIndex(i); j != -1; j = this.fDTDGrammar.getNextAttributeDeclIndex(j)) {
      this.fDTDGrammar.getAttributeDecl(j, this.fTempAttDecl);
      String str1 = this.fTempAttDecl.name.prefix;
      String str2 = this.fTempAttDecl.name.localpart;
      String str3 = this.fTempAttDecl.name.rawname;
      String str4 = getAttributeTypeName(this.fTempAttDecl);
      short s = this.fTempAttDecl.simpleType.defaultType;
      String str5 = null;
      if (this.fTempAttDecl.simpleType.defaultValue != null)
        str5 = this.fTempAttDecl.simpleType.defaultValue; 
      boolean bool1 = false;
      boolean bool2 = (s == 2) ? 1 : 0;
      boolean bool3 = (str4 == XMLSymbols.fCDATASymbol) ? 1 : 0;
      if (!bool3 || bool2 || str5 != null)
        if (this.fNamespaceContext != null && str3.startsWith("xmlns")) {
          String str = "";
          int m = str3.indexOf(':');
          if (m != -1) {
            str = str3.substring(0, m);
          } else {
            str = str3;
          } 
          str = this.fSymbolTable.addSymbol(str);
          if (!((NamespaceSupport)this.fNamespaceContext).containsPrefixInCurrentContext(str))
            this.fNamespaceContext.declarePrefix(str, str5); 
          bool1 = true;
        } else {
          int m = paramXMLAttributes.getLength();
          for (byte b1 = 0; b1 < m; b1++) {
            if (paramXMLAttributes.getQName(b1) == str3) {
              bool1 = true;
              break;
            } 
          } 
        }  
      if (!bool1 && str5 != null) {
        if (this.fNamespaces) {
          int n = str3.indexOf(':');
          if (n != -1) {
            str1 = str3.substring(0, n);
            str1 = this.fSymbolTable.addSymbol(str1);
            str2 = str3.substring(n + 1);
            str2 = this.fSymbolTable.addSymbol(str2);
          } 
        } 
        this.fTempQName.setValues(str1, str2, str3, this.fTempAttDecl.name.uri);
        int m = paramXMLAttributes.addAttribute(this.fTempQName, str4, str5);
      } 
    } 
    int k = paramXMLAttributes.getLength();
    for (byte b = 0; b < k; b++) {
      String str = paramXMLAttributes.getQName(b);
      boolean bool = false;
      int m;
      for (m = this.fDTDGrammar.getFirstAttributeDeclIndex(i); m != -1; m = this.fDTDGrammar.getNextAttributeDeclIndex(m)) {
        this.fDTDGrammar.getAttributeDecl(m, this.fTempAttDecl);
        if (this.fTempAttDecl.name.rawname == str) {
          bool = true;
          break;
        } 
      } 
      if (bool) {
        String str1 = getAttributeTypeName(this.fTempAttDecl);
        paramXMLAttributes.setType(b, str1);
        boolean bool1 = false;
        if (paramXMLAttributes.isSpecified(b) && str1 != XMLSymbols.fCDATASymbol)
          bool1 = normalizeAttrValue(paramXMLAttributes, b); 
      } 
    } 
  }
  
  private boolean normalizeAttrValue(XMLAttributes paramXMLAttributes, int paramInt) {
    boolean bool1 = true;
    boolean bool2 = false;
    boolean bool3 = false;
    byte b1 = 0;
    byte b2 = 0;
    String str1 = paramXMLAttributes.getValue(paramInt);
    char[] arrayOfChar = new char[str1.length()];
    this.fBuffer.setLength(0);
    str1.getChars(0, str1.length(), arrayOfChar, 0);
    for (byte b3 = 0; b3 < arrayOfChar.length; b3++) {
      if (arrayOfChar[b3] == ' ') {
        if (bool3) {
          bool2 = true;
          bool3 = false;
        } 
        if (bool2 && !bool1) {
          bool2 = false;
          this.fBuffer.append(arrayOfChar[b3]);
          b1++;
        } else if (bool1 || !bool2) {
          b2++;
        } 
      } else {
        bool3 = true;
        bool2 = false;
        bool1 = false;
        this.fBuffer.append(arrayOfChar[b3]);
        b1++;
      } 
    } 
    if (b1 > 0 && this.fBuffer.charAt(b1 - 1) == ' ')
      this.fBuffer.setLength(b1 - 1); 
    String str2 = this.fBuffer.toString();
    paramXMLAttributes.setValue(paramInt, str2);
    return !str1.equals(str2);
  }
  
  private String getAttributeTypeName(XMLAttributeDecl paramXMLAttributeDecl) {
    byte b;
    StringBuffer stringBuffer;
    switch (paramXMLAttributeDecl.simpleType.type) {
      case 1:
        return paramXMLAttributeDecl.simpleType.list ? XMLSymbols.fENTITIESSymbol : XMLSymbols.fENTITYSymbol;
      case 2:
        stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        for (b = 0; b < paramXMLAttributeDecl.simpleType.enumeration.length; b++) {
          if (b)
            stringBuffer.append("|"); 
          stringBuffer.append(paramXMLAttributeDecl.simpleType.enumeration[b]);
        } 
        stringBuffer.append(')');
        return this.fSymbolTable.addSymbol(stringBuffer.toString());
      case 3:
        return XMLSymbols.fIDSymbol;
      case 4:
        return paramXMLAttributeDecl.simpleType.list ? XMLSymbols.fIDREFSSymbol : XMLSymbols.fIDREFSymbol;
      case 5:
        return paramXMLAttributeDecl.simpleType.list ? XMLSymbols.fNMTOKENSSymbol : XMLSymbols.fNMTOKENSymbol;
      case 6:
        return XMLSymbols.fNOTATIONSymbol;
    } 
    return XMLSymbols.fCDATASymbol;
  }
  
  private void ensureStackCapacity(int paramInt) {
    if (paramInt == this.fElementContentState.length) {
      boolean[] arrayOfBoolean = new boolean[paramInt * 2];
      System.arraycopy(this.fElementContentState, 0, arrayOfBoolean, 0, paramInt);
      this.fElementContentState = arrayOfBoolean;
    } 
  }
  
  protected void handleStartElement(QName paramQName, XMLAttributes paramXMLAttributes) throws XNIException {
    if (this.fDTDGrammar == null) {
      this.fCurrentElementIndex = -1;
      this.fCurrentContentSpecType = -1;
      this.fInElementContent = false;
      return;
    } 
    this.fCurrentElementIndex = this.fDTDGrammar.getElementDeclIndex(paramQName);
    this.fCurrentContentSpecType = this.fDTDGrammar.getContentSpecType(this.fCurrentElementIndex);
    addDTDDefaultAttrs(paramQName, paramXMLAttributes);
    this.fInElementContent = (this.fCurrentContentSpecType == 3);
    this.fElementDepth++;
    ensureStackCapacity(this.fElementDepth);
    this.fElementContentState[this.fElementDepth] = this.fInElementContent;
  }
  
  protected void handleEndElement(QName paramQName) throws XNIException {
    if (this.fDTDGrammar == null)
      return; 
    this.fElementDepth--;
    if (this.fElementDepth < -1)
      throw new RuntimeException("FWK008 Element stack underflow"); 
    if (this.fElementDepth < 0) {
      this.fCurrentElementIndex = -1;
      this.fCurrentContentSpecType = -1;
      this.fInElementContent = false;
      return;
    } 
    this.fInElementContent = this.fElementContentState[this.fElementDepth];
  }
  
  public boolean isInElementContent() { return this.fInElementContent; }
  
  public boolean isIgnorableWhiteSpace(XMLString paramXMLString) {
    if (isInElementContent()) {
      for (int i = paramXMLString.offset; i < paramXMLString.offset + paramXMLString.length; i++) {
        if (!XMLChar.isSpace(paramXMLString.ch[i]))
          return false; 
      } 
      return true;
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\dtd\DTDGrammarUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */