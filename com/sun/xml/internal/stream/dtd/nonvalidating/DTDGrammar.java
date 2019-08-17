package com.sun.xml.internal.stream.dtd.nonvalidating;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTDGrammar {
  public static final int TOP_LEVEL_SCOPE = -1;
  
  private static final int CHUNK_SHIFT = 8;
  
  private static final int CHUNK_SIZE = 256;
  
  private static final int CHUNK_MASK = 255;
  
  private static final int INITIAL_CHUNK_COUNT = 4;
  
  private static final short LIST_FLAG = 128;
  
  private static final short LIST_MASK = -129;
  
  private static final boolean DEBUG = false;
  
  protected XMLDTDSource fDTDSource = null;
  
  protected XMLDTDContentModelSource fDTDContentModelSource = null;
  
  protected int fCurrentElementIndex;
  
  protected int fCurrentAttributeIndex;
  
  protected boolean fReadingExternalDTD = false;
  
  private SymbolTable fSymbolTable;
  
  private ArrayList notationDecls = new ArrayList();
  
  private int fElementDeclCount = 0;
  
  private QName[][] fElementDeclName = new QName[4][];
  
  private short[][] fElementDeclType = new short[4][];
  
  private int[][] fElementDeclFirstAttributeDeclIndex = new int[4][];
  
  private int[][] fElementDeclLastAttributeDeclIndex = new int[4][];
  
  private int fAttributeDeclCount = 0;
  
  private QName[][] fAttributeDeclName = new QName[4][];
  
  private short[][] fAttributeDeclType = new short[4][];
  
  private String[][][] fAttributeDeclEnumeration = new String[4][][];
  
  private short[][] fAttributeDeclDefaultType = new short[4][];
  
  private String[][] fAttributeDeclDefaultValue = new String[4][];
  
  private String[][] fAttributeDeclNonNormalizedDefaultValue = new String[4][];
  
  private int[][] fAttributeDeclNextAttributeDeclIndex = new int[4][];
  
  private final Map<String, Integer> fElementIndexMap = new HashMap();
  
  private final QName fQName = new QName();
  
  protected XMLAttributeDecl fAttributeDecl = new XMLAttributeDecl();
  
  private XMLElementDecl fElementDecl = new XMLElementDecl();
  
  private XMLSimpleType fSimpleType = new XMLSimpleType();
  
  Map<String, XMLElementDecl> fElementDeclTab = new HashMap();
  
  public DTDGrammar(SymbolTable paramSymbolTable) { this.fSymbolTable = paramSymbolTable; }
  
  public int getAttributeDeclIndex(int paramInt, String paramString) {
    if (paramInt == -1)
      return -1; 
    for (int i = getFirstAttributeDeclIndex(paramInt); i != -1; i = getNextAttributeDeclIndex(i)) {
      getAttributeDecl(i, this.fAttributeDecl);
      if (this.fAttributeDecl.name.rawname == paramString || paramString.equals(this.fAttributeDecl.name.rawname))
        return i; 
    } 
    return -1;
  }
  
  public void startDTD(XMLLocator paramXMLLocator, Augmentations paramAugmentations) throws XNIException {}
  
  public void elementDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {
    XMLElementDecl xMLElementDecl1 = (XMLElementDecl)this.fElementDeclTab.get(paramString1);
    if (xMLElementDecl1 != null) {
      if (xMLElementDecl1.type == -1) {
        this.fCurrentElementIndex = getElementDeclIndex(paramString1);
      } else {
        return;
      } 
    } else {
      this.fCurrentElementIndex = createElementDecl();
    } 
    XMLElementDecl xMLElementDecl2 = new XMLElementDecl();
    QName qName = new QName(null, paramString1, paramString1, null);
    xMLElementDecl2.name.setValues(qName);
    xMLElementDecl2.scope = -1;
    if (paramString2.equals("EMPTY")) {
      xMLElementDecl2.type = 1;
    } else if (paramString2.equals("ANY")) {
      xMLElementDecl2.type = 0;
    } else if (paramString2.startsWith("(")) {
      if (paramString2.indexOf("#PCDATA") > 0) {
        xMLElementDecl2.type = 2;
      } else {
        xMLElementDecl2.type = 3;
      } 
    } 
    this.fElementDeclTab.put(paramString1, xMLElementDecl2);
    this.fElementDecl = xMLElementDecl2;
    setElementDecl(this.fCurrentElementIndex, this.fElementDecl);
    int i = this.fCurrentElementIndex >> 8;
    ensureElementDeclCapacity(i);
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, String paramString4, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException {
    if (paramString3 != XMLSymbols.fCDATASymbol && paramXMLString1 != null)
      normalizeDefaultAttrValue(paramXMLString1); 
    if (!this.fElementDeclTab.containsKey(paramString1)) {
      this.fCurrentElementIndex = createElementDecl();
      XMLElementDecl xMLElementDecl = new XMLElementDecl();
      xMLElementDecl.name.setValues(null, paramString1, paramString1, null);
      xMLElementDecl.scope = -1;
      this.fElementDeclTab.put(paramString1, xMLElementDecl);
      setElementDecl(this.fCurrentElementIndex, xMLElementDecl);
    } 
    int i = getElementDeclIndex(paramString1);
    if (getAttributeDeclIndex(i, paramString2) != -1)
      return; 
    this.fCurrentAttributeIndex = createAttributeDecl();
    this.fSimpleType.clear();
    if (paramString4 != null)
      if (paramString4.equals("#FIXED")) {
        this.fSimpleType;
        this.fSimpleType.defaultType = 1;
      } else if (paramString4.equals("#IMPLIED")) {
        this.fSimpleType;
        this.fSimpleType.defaultType = 0;
      } else if (paramString4.equals("#REQUIRED")) {
        this.fSimpleType;
        this.fSimpleType.defaultType = 2;
      }  
    this.fSimpleType.defaultValue = (paramXMLString1 != null) ? paramXMLString1.toString() : null;
    this.fSimpleType.nonNormalizedDefaultValue = (paramXMLString2 != null) ? paramXMLString2.toString() : null;
    this.fSimpleType.enumeration = paramArrayOfString;
    if (paramString3.equals("CDATA")) {
      this.fSimpleType.type = 0;
    } else if (paramString3.equals("ID")) {
      this.fSimpleType.type = 3;
    } else if (paramString3.startsWith("IDREF")) {
      this.fSimpleType.type = 4;
      if (paramString3.indexOf("S") > 0)
        this.fSimpleType.list = true; 
    } else if (paramString3.equals("ENTITIES")) {
      this.fSimpleType.type = 1;
      this.fSimpleType.list = true;
    } else if (paramString3.equals("ENTITY")) {
      this.fSimpleType.type = 1;
    } else if (paramString3.equals("NMTOKENS")) {
      this.fSimpleType.type = 5;
      this.fSimpleType.list = true;
    } else if (paramString3.equals("NMTOKEN")) {
      this.fSimpleType.type = 5;
    } else if (paramString3.startsWith("NOTATION")) {
      this.fSimpleType.type = 6;
    } else if (paramString3.startsWith("ENUMERATION")) {
      this.fSimpleType.type = 2;
    } else {
      System.err.println("!!! unknown attribute type " + paramString3);
    } 
    this.fQName.setValues(null, paramString2, paramString2, null);
    this.fAttributeDecl.setValues(this.fQName, this.fSimpleType, false);
    setAttributeDecl(i, this.fCurrentAttributeIndex, this.fAttributeDecl);
    int j = this.fCurrentAttributeIndex >> 8;
    ensureAttributeDeclCapacity(j);
  }
  
  public SymbolTable getSymbolTable() { return this.fSymbolTable; }
  
  public int getFirstElementDeclIndex() { return (this.fElementDeclCount >= 0) ? 0 : -1; }
  
  public int getNextElementDeclIndex(int paramInt) { return (paramInt < this.fElementDeclCount - 1) ? (paramInt + 1) : -1; }
  
  public int getElementDeclIndex(String paramString) {
    Integer integer = (Integer)this.fElementIndexMap.get(paramString);
    if (integer == null)
      integer = Integer.valueOf(-1); 
    return integer.intValue();
  }
  
  public int getElementDeclIndex(QName paramQName) { return getElementDeclIndex(paramQName.rawname); }
  
  public short getContentSpecType(int paramInt) {
    if (paramInt < 0 || paramInt >= this.fElementDeclCount)
      return -1; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return (this.fElementDeclType[i][j] == -1) ? -1 : (short)(this.fElementDeclType[i][j] & 0xFFFFFF7F);
  }
  
  public boolean getElementDecl(int paramInt, XMLElementDecl paramXMLElementDecl) {
    if (paramInt < 0 || paramInt >= this.fElementDeclCount)
      return false; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    paramXMLElementDecl.name.setValues(this.fElementDeclName[i][j]);
    if (this.fElementDeclType[i][j] == -1) {
      paramXMLElementDecl.type = -1;
      paramXMLElementDecl.simpleType.list = false;
    } else {
      paramXMLElementDecl.type = (short)(this.fElementDeclType[i][j] & 0xFFFFFF7F);
      paramXMLElementDecl.simpleType.list = ((this.fElementDeclType[i][j] & 0x80) != 0);
    } 
    paramXMLElementDecl.simpleType.defaultType = -1;
    paramXMLElementDecl.simpleType.defaultValue = null;
    return true;
  }
  
  public int getFirstAttributeDeclIndex(int paramInt) {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return this.fElementDeclFirstAttributeDeclIndex[i][j];
  }
  
  public int getNextAttributeDeclIndex(int paramInt) {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return this.fAttributeDeclNextAttributeDeclIndex[i][j];
  }
  
  public boolean getAttributeDecl(int paramInt, XMLAttributeDecl paramXMLAttributeDecl) {
    boolean bool;
    short s;
    if (paramInt < 0 || paramInt >= this.fAttributeDeclCount)
      return false; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    paramXMLAttributeDecl.name.setValues(this.fAttributeDeclName[i][j]);
    if (this.fAttributeDeclType[i][j] == -1) {
      s = -1;
      bool = false;
    } else {
      s = (short)(this.fAttributeDeclType[i][j] & 0xFFFFFF7F);
      bool = ((this.fAttributeDeclType[i][j] & 0x80) != 0);
    } 
    paramXMLAttributeDecl.simpleType.setValues(s, (this.fAttributeDeclName[i][j]).localpart, this.fAttributeDeclEnumeration[i][j], bool, this.fAttributeDeclDefaultType[i][j], this.fAttributeDeclDefaultValue[i][j], this.fAttributeDeclNonNormalizedDefaultValue[i][j]);
    return true;
  }
  
  public boolean isCDATAAttribute(QName paramQName1, QName paramQName2) {
    int i = getElementDeclIndex(paramQName1);
    return !(getAttributeDecl(i, this.fAttributeDecl) && this.fAttributeDecl.simpleType.type != 0);
  }
  
  public void printElements() {
    byte b = 0;
    XMLElementDecl xMLElementDecl = new XMLElementDecl();
    while (getElementDecl(b++, xMLElementDecl))
      System.out.println("element decl: " + xMLElementDecl.name + ", " + xMLElementDecl.name.rawname); 
  }
  
  public void printAttributes(int paramInt) {
    int i = getFirstAttributeDeclIndex(paramInt);
    System.out.print(paramInt);
    System.out.print(" [");
    while (i != -1) {
      System.out.print(' ');
      System.out.print(i);
      printAttribute(i);
      i = getNextAttributeDeclIndex(i);
      if (i != -1)
        System.out.print(","); 
    } 
    System.out.println(" ]");
  }
  
  protected int createElementDecl() {
    int i = this.fElementDeclCount >> 8;
    int j = this.fElementDeclCount & 0xFF;
    ensureElementDeclCapacity(i);
    this.fElementDeclName[i][j] = new QName();
    this.fElementDeclType[i][j] = -1;
    this.fElementDeclFirstAttributeDeclIndex[i][j] = -1;
    this.fElementDeclLastAttributeDeclIndex[i][j] = -1;
    return this.fElementDeclCount++;
  }
  
  protected void setElementDecl(int paramInt, XMLElementDecl paramXMLElementDecl) {
    if (paramInt < 0 || paramInt >= this.fElementDeclCount)
      return; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    int k = paramXMLElementDecl.scope;
    this.fElementDeclName[i][j].setValues(paramXMLElementDecl.name);
    this.fElementDeclType[i][j] = paramXMLElementDecl.type;
    if (paramXMLElementDecl.simpleType.list == true)
      this.fElementDeclType[i][j] = (short)(this.fElementDeclType[i][j] | 0x80); 
    this.fElementIndexMap.put(paramXMLElementDecl.name.rawname, Integer.valueOf(paramInt));
  }
  
  protected void setFirstAttributeDeclIndex(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 >= this.fElementDeclCount)
      return; 
    int i = paramInt1 >> 8;
    int j = paramInt1 & 0xFF;
    this.fElementDeclFirstAttributeDeclIndex[i][j] = paramInt2;
  }
  
  protected int createAttributeDecl() {
    int i = this.fAttributeDeclCount >> 8;
    int j = this.fAttributeDeclCount & 0xFF;
    ensureAttributeDeclCapacity(i);
    this.fAttributeDeclName[i][j] = new QName();
    this.fAttributeDeclType[i][j] = -1;
    this.fAttributeDeclEnumeration[i][j] = null;
    this.fAttributeDeclDefaultType[i][j] = 0;
    this.fAttributeDeclDefaultValue[i][j] = null;
    this.fAttributeDeclNonNormalizedDefaultValue[i][j] = null;
    this.fAttributeDeclNextAttributeDeclIndex[i][j] = -1;
    return this.fAttributeDeclCount++;
  }
  
  protected void setAttributeDecl(int paramInt1, int paramInt2, XMLAttributeDecl paramXMLAttributeDecl) {
    int i = paramInt2 >> 8;
    int j = paramInt2 & 0xFF;
    this.fAttributeDeclName[i][j].setValues(paramXMLAttributeDecl.name);
    this.fAttributeDeclType[i][j] = paramXMLAttributeDecl.simpleType.type;
    if (paramXMLAttributeDecl.simpleType.list)
      this.fAttributeDeclType[i][j] = (short)(this.fAttributeDeclType[i][j] | 0x80); 
    this.fAttributeDeclEnumeration[i][j] = paramXMLAttributeDecl.simpleType.enumeration;
    this.fAttributeDeclDefaultType[i][j] = paramXMLAttributeDecl.simpleType.defaultType;
    this.fAttributeDeclDefaultValue[i][j] = paramXMLAttributeDecl.simpleType.defaultValue;
    this.fAttributeDeclNonNormalizedDefaultValue[i][j] = paramXMLAttributeDecl.simpleType.nonNormalizedDefaultValue;
    int k = paramInt1 >> 8;
    int m = paramInt1 & 0xFF;
    int n;
    for (n = this.fElementDeclFirstAttributeDeclIndex[k][m]; n != -1 && n != paramInt2; n = this.fAttributeDeclNextAttributeDeclIndex[i][j]) {
      i = n >> 8;
      j = n & 0xFF;
    } 
    if (n == -1) {
      if (this.fElementDeclFirstAttributeDeclIndex[k][m] == -1) {
        this.fElementDeclFirstAttributeDeclIndex[k][m] = paramInt2;
      } else {
        n = this.fElementDeclLastAttributeDeclIndex[k][m];
        i = n >> 8;
        j = n & 0xFF;
        this.fAttributeDeclNextAttributeDeclIndex[i][j] = paramInt2;
      } 
      this.fElementDeclLastAttributeDeclIndex[k][m] = paramInt2;
    } 
  }
  
  public void notationDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {
    XMLNotationDecl xMLNotationDecl = new XMLNotationDecl();
    xMLNotationDecl.setValues(paramString, paramXMLResourceIdentifier.getPublicId(), paramXMLResourceIdentifier.getLiteralSystemId(), paramXMLResourceIdentifier.getBaseSystemId());
    this.notationDecls.add(xMLNotationDecl);
  }
  
  public List getNotationDecls() { return this.notationDecls; }
  
  private void printAttribute(int paramInt) {
    XMLAttributeDecl xMLAttributeDecl = new XMLAttributeDecl();
    if (getAttributeDecl(paramInt, xMLAttributeDecl)) {
      System.out.print(" { ");
      System.out.print(xMLAttributeDecl.name.localpart);
      System.out.print(" }");
    } 
  }
  
  private void ensureElementDeclCapacity(int paramInt) {
    if (paramInt >= this.fElementDeclName.length) {
      this.fElementDeclName = resize(this.fElementDeclName, this.fElementDeclName.length * 2);
      this.fElementDeclType = resize(this.fElementDeclType, this.fElementDeclType.length * 2);
      this.fElementDeclFirstAttributeDeclIndex = resize(this.fElementDeclFirstAttributeDeclIndex, this.fElementDeclFirstAttributeDeclIndex.length * 2);
      this.fElementDeclLastAttributeDeclIndex = resize(this.fElementDeclLastAttributeDeclIndex, this.fElementDeclLastAttributeDeclIndex.length * 2);
    } else if (this.fElementDeclName[paramInt] != null) {
      return;
    } 
    this.fElementDeclName[paramInt] = new QName[256];
    this.fElementDeclType[paramInt] = new short[256];
    this.fElementDeclFirstAttributeDeclIndex[paramInt] = new int[256];
    this.fElementDeclLastAttributeDeclIndex[paramInt] = new int[256];
  }
  
  private void ensureAttributeDeclCapacity(int paramInt) {
    if (paramInt >= this.fAttributeDeclName.length) {
      this.fAttributeDeclName = resize(this.fAttributeDeclName, this.fAttributeDeclName.length * 2);
      this.fAttributeDeclType = resize(this.fAttributeDeclType, this.fAttributeDeclType.length * 2);
      this.fAttributeDeclEnumeration = resize(this.fAttributeDeclEnumeration, this.fAttributeDeclEnumeration.length * 2);
      this.fAttributeDeclDefaultType = resize(this.fAttributeDeclDefaultType, this.fAttributeDeclDefaultType.length * 2);
      this.fAttributeDeclDefaultValue = resize(this.fAttributeDeclDefaultValue, this.fAttributeDeclDefaultValue.length * 2);
      this.fAttributeDeclNonNormalizedDefaultValue = resize(this.fAttributeDeclNonNormalizedDefaultValue, this.fAttributeDeclNonNormalizedDefaultValue.length * 2);
      this.fAttributeDeclNextAttributeDeclIndex = resize(this.fAttributeDeclNextAttributeDeclIndex, this.fAttributeDeclNextAttributeDeclIndex.length * 2);
    } else if (this.fAttributeDeclName[paramInt] != null) {
      return;
    } 
    this.fAttributeDeclName[paramInt] = new QName[256];
    this.fAttributeDeclType[paramInt] = new short[256];
    this.fAttributeDeclEnumeration[paramInt] = new String[256][];
    this.fAttributeDeclDefaultType[paramInt] = new short[256];
    this.fAttributeDeclDefaultValue[paramInt] = new String[256];
    this.fAttributeDeclNonNormalizedDefaultValue[paramInt] = new String[256];
    this.fAttributeDeclNextAttributeDeclIndex[paramInt] = new int[256];
  }
  
  private static short[][] resize(short[][] paramArrayOfShort, int paramInt) {
    short[][] arrayOfShort = new short[paramInt][];
    System.arraycopy(paramArrayOfShort, 0, arrayOfShort, 0, paramArrayOfShort.length);
    return arrayOfShort;
  }
  
  private static int[][] resize(int[][] paramArrayOfInt, int paramInt) {
    int[][] arrayOfInt = new int[paramInt][];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramArrayOfInt.length);
    return arrayOfInt;
  }
  
  private static QName[][] resize(QName[][] paramArrayOfQName, int paramInt) {
    QName[][] arrayOfQName = new QName[paramInt][];
    System.arraycopy(paramArrayOfQName, 0, arrayOfQName, 0, paramArrayOfQName.length);
    return arrayOfQName;
  }
  
  private static String[][] resize(String[][] paramArrayOfString, int paramInt) {
    String[][] arrayOfString = new String[paramInt][];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramArrayOfString.length);
    return arrayOfString;
  }
  
  private static String[][][] resize(String[][][] paramArrayOfString, int paramInt) {
    String[][][] arrayOfString = new String[paramInt][][];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramArrayOfString.length);
    return arrayOfString;
  }
  
  private boolean normalizeDefaultAttrValue(XMLString paramXMLString) {
    int i = paramXMLString.length;
    boolean bool = true;
    int j = paramXMLString.offset;
    int k = paramXMLString.offset + paramXMLString.length;
    for (int m = paramXMLString.offset; m < k; m++) {
      if (paramXMLString.ch[m] == ' ') {
        if (!bool) {
          paramXMLString.ch[j++] = ' ';
          bool = true;
        } 
      } else {
        if (j != m)
          paramXMLString.ch[j] = paramXMLString.ch[m]; 
        j++;
        bool = false;
      } 
    } 
    if (j != k) {
      if (bool)
        j--; 
      paramXMLString.length = j - paramXMLString.offset;
      return true;
    } 
    return false;
  }
  
  public void endDTD(Augmentations paramAugmentations) throws XNIException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\dtd\nonvalidating\DTDGrammar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */