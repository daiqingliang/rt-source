package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMAny;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMBinOp;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMLeaf;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMUniOp;
import com.sun.org.apache.xerces.internal.impl.dtd.models.ContentModelValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.models.DFAContentModel;
import com.sun.org.apache.xerces.internal.impl.dtd.models.MixedContentModel;
import com.sun.org.apache.xerces.internal.impl.dtd.models.SimpleContentModel;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DTDGrammar implements XMLDTDHandler, XMLDTDContentModelHandler, EntityState, Grammar {
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
  
  protected XMLDTDDescription fGrammarDescription = null;
  
  private int fElementDeclCount = 0;
  
  private QName[][] fElementDeclName = new QName[4][];
  
  private short[][] fElementDeclType = new short[4][];
  
  private int[][] fElementDeclContentSpecIndex = new int[4][];
  
  private ContentModelValidator[][] fElementDeclContentModelValidator = new ContentModelValidator[4][];
  
  private int[][] fElementDeclFirstAttributeDeclIndex = new int[4][];
  
  private int[][] fElementDeclLastAttributeDeclIndex = new int[4][];
  
  private int fAttributeDeclCount = 0;
  
  private QName[][] fAttributeDeclName = new QName[4][];
  
  private boolean fIsImmutable = false;
  
  private short[][] fAttributeDeclType = new short[4][];
  
  private String[][][] fAttributeDeclEnumeration = new String[4][][];
  
  private short[][] fAttributeDeclDefaultType = new short[4][];
  
  private DatatypeValidator[][] fAttributeDeclDatatypeValidator = new DatatypeValidator[4][];
  
  private String[][] fAttributeDeclDefaultValue = new String[4][];
  
  private String[][] fAttributeDeclNonNormalizedDefaultValue = new String[4][];
  
  private int[][] fAttributeDeclNextAttributeDeclIndex = new int[4][];
  
  private int fContentSpecCount = 0;
  
  private short[][] fContentSpecType = new short[4][];
  
  private Object[][] fContentSpecValue = new Object[4][];
  
  private Object[][] fContentSpecOtherValue = new Object[4][];
  
  private int fEntityCount = 0;
  
  private String[][] fEntityName = new String[4][];
  
  private String[][] fEntityValue = new String[4][];
  
  private String[][] fEntityPublicId = new String[4][];
  
  private String[][] fEntitySystemId = new String[4][];
  
  private String[][] fEntityBaseSystemId = new String[4][];
  
  private String[][] fEntityNotation = new String[4][];
  
  private byte[][] fEntityIsPE = new byte[4][];
  
  private byte[][] fEntityInExternal = new byte[4][];
  
  private int fNotationCount = 0;
  
  private String[][] fNotationName = new String[4][];
  
  private String[][] fNotationPublicId = new String[4][];
  
  private String[][] fNotationSystemId = new String[4][];
  
  private String[][] fNotationBaseSystemId = new String[4][];
  
  private final Map<String, Integer> fElementIndexMap = new HashMap();
  
  private final Map<String, Integer> fEntityIndexMap = new HashMap();
  
  private final Map<String, Integer> fNotationIndexMap = new HashMap();
  
  private boolean fMixed;
  
  private final QName fQName = new QName();
  
  private final QName fQName2 = new QName();
  
  protected final XMLAttributeDecl fAttributeDecl = new XMLAttributeDecl();
  
  private int fLeafCount = 0;
  
  private int fEpsilonIndex = -1;
  
  private XMLElementDecl fElementDecl = new XMLElementDecl();
  
  private XMLEntityDecl fEntityDecl = new XMLEntityDecl();
  
  private XMLSimpleType fSimpleType = new XMLSimpleType();
  
  private XMLContentSpec fContentSpec = new XMLContentSpec();
  
  Map<String, XMLElementDecl> fElementDeclTab = new HashMap();
  
  private short[] fOpStack = null;
  
  private int[] fNodeIndexStack = null;
  
  private int[] fPrevNodeIndexStack = null;
  
  private int fDepth = 0;
  
  private boolean[] fPEntityStack = new boolean[4];
  
  private int fPEDepth = 0;
  
  private int[][] fElementDeclIsExternal = new int[4][];
  
  private int[][] fAttributeDeclIsExternal = new int[4][];
  
  int valueIndex = -1;
  
  int prevNodeIndex = -1;
  
  int nodeIndex = -1;
  
  public DTDGrammar(SymbolTable paramSymbolTable, XMLDTDDescription paramXMLDTDDescription) {
    this.fSymbolTable = paramSymbolTable;
    this.fGrammarDescription = paramXMLDTDDescription;
  }
  
  public XMLGrammarDescription getGrammarDescription() { return this.fGrammarDescription; }
  
  public boolean getElementDeclIsExternal(int paramInt) {
    if (paramInt < 0)
      return false; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return (this.fElementDeclIsExternal[i][j] != 0);
  }
  
  public boolean getAttributeDeclIsExternal(int paramInt) {
    if (paramInt < 0)
      return false; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return (this.fAttributeDeclIsExternal[i][j] != 0);
  }
  
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
  
  public void startDTD(XMLLocator paramXMLLocator, Augmentations paramAugmentations) throws XNIException {
    this.fOpStack = null;
    this.fNodeIndexStack = null;
    this.fPrevNodeIndexStack = null;
  }
  
  public void startParameterEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fPEDepth == this.fPEntityStack.length) {
      boolean[] arrayOfBoolean = new boolean[this.fPEntityStack.length * 2];
      System.arraycopy(this.fPEntityStack, 0, arrayOfBoolean, 0, this.fPEntityStack.length);
      this.fPEntityStack = arrayOfBoolean;
    } 
    this.fPEntityStack[this.fPEDepth] = this.fReadingExternalDTD;
    this.fPEDepth++;
  }
  
  public void startExternalSubset(XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException { this.fReadingExternalDTD = true; }
  
  public void endParameterEntity(String paramString, Augmentations paramAugmentations) throws XNIException {
    this.fPEDepth--;
    this.fReadingExternalDTD = this.fPEntityStack[this.fPEDepth];
  }
  
  public void endExternalSubset(Augmentations paramAugmentations) throws XNIException { this.fReadingExternalDTD = false; }
  
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
    this.fQName.setValues(null, paramString1, paramString1, null);
    xMLElementDecl2.name.setValues(this.fQName);
    xMLElementDecl2.contentModelValidator = null;
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
    addContentSpecToElement(xMLElementDecl2);
    setElementDecl(this.fCurrentElementIndex, this.fElementDecl);
    int i = this.fCurrentElementIndex >> 8;
    int j = this.fCurrentElementIndex & 0xFF;
    ensureElementDeclCapacity(i);
    this.fElementDeclIsExternal[i][j] = (this.fReadingExternalDTD || this.fPEDepth > 0) ? 1 : 0;
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, String paramString4, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException {
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
        this.fSimpleType.defaultType = 1;
      } else if (paramString4.equals("#IMPLIED")) {
        this.fSimpleType.defaultType = 0;
      } else if (paramString4.equals("#REQUIRED")) {
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
    int k = this.fCurrentAttributeIndex & 0xFF;
    ensureAttributeDeclCapacity(j);
    this.fAttributeDeclIsExternal[j][k] = (this.fReadingExternalDTD || this.fPEDepth > 0) ? 1 : 0;
  }
  
  public void internalEntityDecl(String paramString, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException {
    int i = getEntityDeclIndex(paramString);
    if (i == -1) {
      i = createEntityDecl();
      boolean bool1 = paramString.startsWith("%");
      boolean bool2 = (this.fReadingExternalDTD || this.fPEDepth > 0);
      XMLEntityDecl xMLEntityDecl = new XMLEntityDecl();
      xMLEntityDecl.setValues(paramString, null, null, null, null, paramXMLString1.toString(), bool1, bool2);
      setEntityDecl(i, xMLEntityDecl);
    } 
  }
  
  public void externalEntityDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {
    int i = getEntityDeclIndex(paramString);
    if (i == -1) {
      i = createEntityDecl();
      boolean bool1 = paramString.startsWith("%");
      boolean bool2 = (this.fReadingExternalDTD || this.fPEDepth > 0);
      XMLEntityDecl xMLEntityDecl = new XMLEntityDecl();
      xMLEntityDecl.setValues(paramString, paramXMLResourceIdentifier.getPublicId(), paramXMLResourceIdentifier.getLiteralSystemId(), paramXMLResourceIdentifier.getBaseSystemId(), null, null, bool1, bool2);
      setEntityDecl(i, xMLEntityDecl);
    } 
  }
  
  public void unparsedEntityDecl(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    XMLEntityDecl xMLEntityDecl = new XMLEntityDecl();
    boolean bool1 = paramString1.startsWith("%");
    boolean bool2 = (this.fReadingExternalDTD || this.fPEDepth > 0);
    xMLEntityDecl.setValues(paramString1, paramXMLResourceIdentifier.getPublicId(), paramXMLResourceIdentifier.getLiteralSystemId(), paramXMLResourceIdentifier.getBaseSystemId(), paramString2, null, bool1, bool2);
    int i = getEntityDeclIndex(paramString1);
    if (i == -1) {
      i = createEntityDecl();
      setEntityDecl(i, xMLEntityDecl);
    } 
  }
  
  public void notationDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {
    XMLNotationDecl xMLNotationDecl = new XMLNotationDecl();
    xMLNotationDecl.setValues(paramString, paramXMLResourceIdentifier.getPublicId(), paramXMLResourceIdentifier.getLiteralSystemId(), paramXMLResourceIdentifier.getBaseSystemId());
    int i = getNotationDeclIndex(paramString);
    if (i == -1) {
      i = createNotationDecl();
      setNotationDecl(i, xMLNotationDecl);
    } 
  }
  
  public void endDTD(Augmentations paramAugmentations) throws XNIException {
    this.fIsImmutable = true;
    if (this.fGrammarDescription.getRootName() == null) {
      char c = Character.MIN_VALUE;
      String str = null;
      int i = this.fElementDeclCount;
      ArrayList arrayList = new ArrayList(i);
      for (byte b = 0; b < i; b++) {
        byte b1 = b >> 8;
        c = b & 0xFF;
        str = (this.fElementDeclName[b1][c]).rawname;
        arrayList.add(str);
      } 
      this.fGrammarDescription.setPossibleRoots(arrayList);
    } 
  }
  
  public void setDTDSource(XMLDTDSource paramXMLDTDSource) { this.fDTDSource = paramXMLDTDSource; }
  
  public XMLDTDSource getDTDSource() { return this.fDTDSource; }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void startAttlist(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void endAttlist(Augmentations paramAugmentations) throws XNIException {}
  
  public void startConditional(short paramShort, Augmentations paramAugmentations) throws XNIException {}
  
  public void ignoredCharacters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void endConditional(Augmentations paramAugmentations) throws XNIException {}
  
  public void setDTDContentModelSource(XMLDTDContentModelSource paramXMLDTDContentModelSource) { this.fDTDContentModelSource = paramXMLDTDContentModelSource; }
  
  public XMLDTDContentModelSource getDTDContentModelSource() { return this.fDTDContentModelSource; }
  
  public void startContentModel(String paramString, Augmentations paramAugmentations) throws XNIException {
    XMLElementDecl xMLElementDecl = (XMLElementDecl)this.fElementDeclTab.get(paramString);
    if (xMLElementDecl != null)
      this.fElementDecl = xMLElementDecl; 
    this.fDepth = 0;
    initializeContentModelStack();
  }
  
  public void startGroup(Augmentations paramAugmentations) throws XNIException {
    this.fDepth++;
    initializeContentModelStack();
    this.fMixed = false;
  }
  
  public void pcdata(Augmentations paramAugmentations) throws XNIException { this.fMixed = true; }
  
  public void element(String paramString, Augmentations paramAugmentations) throws XNIException {
    if (this.fMixed) {
      if (this.fNodeIndexStack[this.fDepth] == -1) {
        this.fNodeIndexStack[this.fDepth] = addUniqueLeafNode(paramString);
      } else {
        this.fNodeIndexStack[this.fDepth] = addContentSpecNode((short)4, this.fNodeIndexStack[this.fDepth], addUniqueLeafNode(paramString));
      } 
    } else {
      this.fNodeIndexStack[this.fDepth] = addContentSpecNode((short)0, paramString);
    } 
  }
  
  public void separator(short paramShort, Augmentations paramAugmentations) throws XNIException {
    if (!this.fMixed)
      if (this.fOpStack[this.fDepth] != 5 && paramShort == 0) {
        if (this.fPrevNodeIndexStack[this.fDepth] != -1)
          this.fNodeIndexStack[this.fDepth] = addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]); 
        this.fPrevNodeIndexStack[this.fDepth] = this.fNodeIndexStack[this.fDepth];
        this.fOpStack[this.fDepth] = 4;
      } else if (this.fOpStack[this.fDepth] != 4 && paramShort == 1) {
        if (this.fPrevNodeIndexStack[this.fDepth] != -1)
          this.fNodeIndexStack[this.fDepth] = addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]); 
        this.fPrevNodeIndexStack[this.fDepth] = this.fNodeIndexStack[this.fDepth];
        this.fOpStack[this.fDepth] = 5;
      }  
  }
  
  public void occurrence(short paramShort, Augmentations paramAugmentations) throws XNIException {
    if (!this.fMixed)
      if (paramShort == 2) {
        this.fNodeIndexStack[this.fDepth] = addContentSpecNode((short)1, this.fNodeIndexStack[this.fDepth], -1);
      } else if (paramShort == 3) {
        this.fNodeIndexStack[this.fDepth] = addContentSpecNode((short)2, this.fNodeIndexStack[this.fDepth], -1);
      } else if (paramShort == 4) {
        this.fNodeIndexStack[this.fDepth] = addContentSpecNode((short)3, this.fNodeIndexStack[this.fDepth], -1);
      }  
  }
  
  public void endGroup(Augmentations paramAugmentations) throws XNIException {
    if (!this.fMixed) {
      if (this.fPrevNodeIndexStack[this.fDepth] != -1)
        this.fNodeIndexStack[this.fDepth] = addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]); 
      int i = this.fNodeIndexStack[this.fDepth--];
      this.fNodeIndexStack[this.fDepth] = i;
    } 
  }
  
  public void any(Augmentations paramAugmentations) throws XNIException {}
  
  public void empty(Augmentations paramAugmentations) throws XNIException {}
  
  public void endContentModel(Augmentations paramAugmentations) throws XNIException {}
  
  public boolean isNamespaceAware() { return false; }
  
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
    if (paramXMLElementDecl.type == 3 || paramXMLElementDecl.type == 2)
      paramXMLElementDecl.contentModelValidator = getElementContentModelValidator(paramInt); 
    paramXMLElementDecl.simpleType.datatypeValidator = null;
    paramXMLElementDecl.simpleType.defaultType = -1;
    paramXMLElementDecl.simpleType.defaultValue = null;
    return true;
  }
  
  QName getElementDeclName(int paramInt) {
    if (paramInt < 0 || paramInt >= this.fElementDeclCount)
      return null; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return this.fElementDeclName[i][j];
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
    paramXMLAttributeDecl.simpleType.setValues(s, (this.fAttributeDeclName[i][j]).localpart, this.fAttributeDeclEnumeration[i][j], bool, this.fAttributeDeclDefaultType[i][j], this.fAttributeDeclDefaultValue[i][j], this.fAttributeDeclNonNormalizedDefaultValue[i][j], this.fAttributeDeclDatatypeValidator[i][j]);
    return true;
  }
  
  public boolean isCDATAAttribute(QName paramQName1, QName paramQName2) {
    int i = getElementDeclIndex(paramQName1);
    return !(getAttributeDecl(i, this.fAttributeDecl) && this.fAttributeDecl.simpleType.type != 0);
  }
  
  public int getEntityDeclIndex(String paramString) { return (paramString == null || this.fEntityIndexMap.get(paramString) == null) ? -1 : ((Integer)this.fEntityIndexMap.get(paramString)).intValue(); }
  
  public boolean getEntityDecl(int paramInt, XMLEntityDecl paramXMLEntityDecl) {
    if (paramInt < 0 || paramInt >= this.fEntityCount)
      return false; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    paramXMLEntityDecl.setValues(this.fEntityName[i][j], this.fEntityPublicId[i][j], this.fEntitySystemId[i][j], this.fEntityBaseSystemId[i][j], this.fEntityNotation[i][j], this.fEntityValue[i][j], !(this.fEntityIsPE[i][j] == 0), !(this.fEntityInExternal[i][j] == 0));
    return true;
  }
  
  public int getNotationDeclIndex(String paramString) { return (paramString == null || this.fNotationIndexMap.get(paramString) == null) ? -1 : ((Integer)this.fNotationIndexMap.get(paramString)).intValue(); }
  
  public boolean getNotationDecl(int paramInt, XMLNotationDecl paramXMLNotationDecl) {
    if (paramInt < 0 || paramInt >= this.fNotationCount)
      return false; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    paramXMLNotationDecl.setValues(this.fNotationName[i][j], this.fNotationPublicId[i][j], this.fNotationSystemId[i][j], this.fNotationBaseSystemId[i][j]);
    return true;
  }
  
  public boolean getContentSpec(int paramInt, XMLContentSpec paramXMLContentSpec) {
    if (paramInt < 0 || paramInt >= this.fContentSpecCount)
      return false; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    paramXMLContentSpec.type = this.fContentSpecType[i][j];
    paramXMLContentSpec.value = this.fContentSpecValue[i][j];
    paramXMLContentSpec.otherValue = this.fContentSpecOtherValue[i][j];
    return true;
  }
  
  public int getContentSpecIndex(int paramInt) {
    if (paramInt < 0 || paramInt >= this.fElementDeclCount)
      return -1; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return this.fElementDeclContentSpecIndex[i][j];
  }
  
  public String getContentSpecAsString(int paramInt) {
    if (paramInt < 0 || paramInt >= this.fElementDeclCount)
      return null; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    int k = this.fElementDeclContentSpecIndex[i][j];
    XMLContentSpec xMLContentSpec = new XMLContentSpec();
    if (getContentSpec(k, xMLContentSpec)) {
      short s2;
      StringBuffer stringBuffer = new StringBuffer();
      short s1 = xMLContentSpec.type & 0xF;
      switch (s1) {
        case 0:
          stringBuffer.append('(');
          if (xMLContentSpec.value == null && xMLContentSpec.otherValue == null) {
            stringBuffer.append("#PCDATA");
          } else {
            stringBuffer.append(xMLContentSpec.value);
          } 
          stringBuffer.append(')');
          return stringBuffer.toString();
        case 1:
          getContentSpec((int[])xMLContentSpec.value[0], xMLContentSpec);
          s2 = xMLContentSpec.type;
          if (s2 == 0) {
            stringBuffer.append('(');
            stringBuffer.append(xMLContentSpec.value);
            stringBuffer.append(')');
          } else if (s2 == 3 || s2 == 2 || s2 == 1) {
            stringBuffer.append('(');
            appendContentSpec(xMLContentSpec, stringBuffer, true, s1);
            stringBuffer.append(')');
          } else {
            appendContentSpec(xMLContentSpec, stringBuffer, true, s1);
          } 
          stringBuffer.append('?');
          return stringBuffer.toString();
        case 2:
          getContentSpec((int[])xMLContentSpec.value[0], xMLContentSpec);
          s2 = xMLContentSpec.type;
          if (s2 == 0) {
            stringBuffer.append('(');
            if (xMLContentSpec.value == null && xMLContentSpec.otherValue == null) {
              stringBuffer.append("#PCDATA");
            } else if (xMLContentSpec.otherValue != null) {
              stringBuffer.append("##any:uri=").append(xMLContentSpec.otherValue);
            } else if (xMLContentSpec.value == null) {
              stringBuffer.append("##any");
            } else {
              appendContentSpec(xMLContentSpec, stringBuffer, true, s1);
            } 
            stringBuffer.append(')');
          } else if (s2 == 3 || s2 == 2 || s2 == 1) {
            stringBuffer.append('(');
            appendContentSpec(xMLContentSpec, stringBuffer, true, s1);
            stringBuffer.append(')');
          } else {
            appendContentSpec(xMLContentSpec, stringBuffer, true, s1);
          } 
          stringBuffer.append('*');
          return stringBuffer.toString();
        case 3:
          getContentSpec((int[])xMLContentSpec.value[0], xMLContentSpec);
          s2 = xMLContentSpec.type;
          if (s2 == 0) {
            stringBuffer.append('(');
            if (xMLContentSpec.value == null && xMLContentSpec.otherValue == null) {
              stringBuffer.append("#PCDATA");
            } else if (xMLContentSpec.otherValue != null) {
              stringBuffer.append("##any:uri=").append(xMLContentSpec.otherValue);
            } else if (xMLContentSpec.value == null) {
              stringBuffer.append("##any");
            } else {
              stringBuffer.append(xMLContentSpec.value);
            } 
            stringBuffer.append(')');
          } else if (s2 == 3 || s2 == 2 || s2 == 1) {
            stringBuffer.append('(');
            appendContentSpec(xMLContentSpec, stringBuffer, true, s1);
            stringBuffer.append(')');
          } else {
            appendContentSpec(xMLContentSpec, stringBuffer, true, s1);
          } 
          stringBuffer.append('+');
          return stringBuffer.toString();
        case 4:
        case 5:
          appendContentSpec(xMLContentSpec, stringBuffer, true, s1);
          return stringBuffer.toString();
        case 6:
          stringBuffer.append("##any");
          if (xMLContentSpec.otherValue != null) {
            stringBuffer.append(":uri=");
            stringBuffer.append(xMLContentSpec.otherValue);
          } 
          return stringBuffer.toString();
        case 7:
          stringBuffer.append("##other:uri=");
          stringBuffer.append(xMLContentSpec.otherValue);
          return stringBuffer.toString();
        case 8:
          stringBuffer.append("##local");
          return stringBuffer.toString();
      } 
      stringBuffer.append("???");
      return stringBuffer.toString();
    } 
    return null;
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
  
  protected void addContentSpecToElement(XMLElementDecl paramXMLElementDecl) {
    if ((this.fDepth == 0 || (this.fDepth == 1 && paramXMLElementDecl.type == 2)) && this.fNodeIndexStack != null) {
      if (paramXMLElementDecl.type == 2) {
        int i = addUniqueLeafNode(null);
        if (this.fNodeIndexStack[0] == -1) {
          this.fNodeIndexStack[0] = i;
        } else {
          this.fNodeIndexStack[0] = addContentSpecNode((short)4, i, this.fNodeIndexStack[0]);
        } 
      } 
      setContentSpecIndex(this.fCurrentElementIndex, this.fNodeIndexStack[this.fDepth]);
    } 
  }
  
  protected ContentModelValidator getElementContentModelValidator(int paramInt) {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    ContentModelValidator contentModelValidator = this.fElementDeclContentModelValidator[i][j];
    if (contentModelValidator != null)
      return contentModelValidator; 
    short s = this.fElementDeclType[i][j];
    if (s == 4)
      return null; 
    int k = this.fElementDeclContentSpecIndex[i][j];
    XMLContentSpec xMLContentSpec = new XMLContentSpec();
    getContentSpec(k, xMLContentSpec);
    if (s == 2) {
      ChildrenList childrenList = new ChildrenList();
      contentSpecTree(k, xMLContentSpec, childrenList);
      contentModelValidator = new MixedContentModel(childrenList.qname, childrenList.type, 0, childrenList.length, false);
    } else if (s == 3) {
      contentModelValidator = createChildModel(k);
    } else {
      throw new RuntimeException("Unknown content type for a element decl in getElementContentModelValidator() in AbstractDTDGrammar class");
    } 
    this.fElementDeclContentModelValidator[i][j] = contentModelValidator;
    return contentModelValidator;
  }
  
  protected int createElementDecl() {
    int i = this.fElementDeclCount >> 8;
    int j = this.fElementDeclCount & 0xFF;
    ensureElementDeclCapacity(i);
    this.fElementDeclName[i][j] = new QName();
    this.fElementDeclType[i][j] = -1;
    this.fElementDeclContentModelValidator[i][j] = null;
    this.fElementDeclFirstAttributeDeclIndex[i][j] = -1;
    this.fElementDeclLastAttributeDeclIndex[i][j] = -1;
    return this.fElementDeclCount++;
  }
  
  protected void setElementDecl(int paramInt, XMLElementDecl paramXMLElementDecl) {
    if (paramInt < 0 || paramInt >= this.fElementDeclCount)
      return; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    this.fElementDeclName[i][j].setValues(paramXMLElementDecl.name);
    this.fElementDeclType[i][j] = paramXMLElementDecl.type;
    this.fElementDeclContentModelValidator[i][j] = paramXMLElementDecl.contentModelValidator;
    if (paramXMLElementDecl.simpleType.list == true)
      this.fElementDeclType[i][j] = (short)(this.fElementDeclType[i][j] | 0x80); 
    this.fElementIndexMap.put(paramXMLElementDecl.name.rawname, Integer.valueOf(paramInt));
  }
  
  protected void putElementNameMapping(QName paramQName, int paramInt1, int paramInt2) {}
  
  protected void setFirstAttributeDeclIndex(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 >= this.fElementDeclCount)
      return; 
    int i = paramInt1 >> 8;
    int j = paramInt1 & 0xFF;
    this.fElementDeclFirstAttributeDeclIndex[i][j] = paramInt2;
  }
  
  protected void setContentSpecIndex(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 >= this.fElementDeclCount)
      return; 
    int i = paramInt1 >> 8;
    int j = paramInt1 & 0xFF;
    this.fElementDeclContentSpecIndex[i][j] = paramInt2;
  }
  
  protected int createAttributeDecl() {
    int i = this.fAttributeDeclCount >> 8;
    int j = this.fAttributeDeclCount & 0xFF;
    ensureAttributeDeclCapacity(i);
    this.fAttributeDeclName[i][j] = new QName();
    this.fAttributeDeclType[i][j] = -1;
    this.fAttributeDeclDatatypeValidator[i][j] = null;
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
    this.fAttributeDeclDatatypeValidator[i][j] = paramXMLAttributeDecl.simpleType.datatypeValidator;
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
  
  protected int createContentSpec() {
    int i = this.fContentSpecCount >> 8;
    int j = this.fContentSpecCount & 0xFF;
    ensureContentSpecCapacity(i);
    this.fContentSpecType[i][j] = -1;
    this.fContentSpecValue[i][j] = null;
    this.fContentSpecOtherValue[i][j] = null;
    return this.fContentSpecCount++;
  }
  
  protected void setContentSpec(int paramInt, XMLContentSpec paramXMLContentSpec) {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    this.fContentSpecType[i][j] = paramXMLContentSpec.type;
    this.fContentSpecValue[i][j] = paramXMLContentSpec.value;
    this.fContentSpecOtherValue[i][j] = paramXMLContentSpec.otherValue;
  }
  
  protected int createEntityDecl() {
    int i = this.fEntityCount >> 8;
    int j = this.fEntityCount & 0xFF;
    ensureEntityDeclCapacity(i);
    this.fEntityIsPE[i][j] = 0;
    this.fEntityInExternal[i][j] = 0;
    return this.fEntityCount++;
  }
  
  protected void setEntityDecl(int paramInt, XMLEntityDecl paramXMLEntityDecl) {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    this.fEntityName[i][j] = paramXMLEntityDecl.name;
    this.fEntityValue[i][j] = paramXMLEntityDecl.value;
    this.fEntityPublicId[i][j] = paramXMLEntityDecl.publicId;
    this.fEntitySystemId[i][j] = paramXMLEntityDecl.systemId;
    this.fEntityBaseSystemId[i][j] = paramXMLEntityDecl.baseSystemId;
    this.fEntityNotation[i][j] = paramXMLEntityDecl.notation;
    this.fEntityIsPE[i][j] = paramXMLEntityDecl.isPE ? 1 : 0;
    this.fEntityInExternal[i][j] = paramXMLEntityDecl.inExternal ? 1 : 0;
    this.fEntityIndexMap.put(paramXMLEntityDecl.name, Integer.valueOf(paramInt));
  }
  
  protected int createNotationDecl() {
    int i = this.fNotationCount >> 8;
    ensureNotationDeclCapacity(i);
    return this.fNotationCount++;
  }
  
  protected void setNotationDecl(int paramInt, XMLNotationDecl paramXMLNotationDecl) {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    this.fNotationName[i][j] = paramXMLNotationDecl.name;
    this.fNotationPublicId[i][j] = paramXMLNotationDecl.publicId;
    this.fNotationSystemId[i][j] = paramXMLNotationDecl.systemId;
    this.fNotationBaseSystemId[i][j] = paramXMLNotationDecl.baseSystemId;
    this.fNotationIndexMap.put(paramXMLNotationDecl.name, Integer.valueOf(paramInt));
  }
  
  protected int addContentSpecNode(short paramShort, String paramString) {
    int i = createContentSpec();
    this.fContentSpec.setValues(paramShort, paramString, null);
    setContentSpec(i, this.fContentSpec);
    return i;
  }
  
  protected int addUniqueLeafNode(String paramString) {
    int i = createContentSpec();
    this.fContentSpec.setValues((short)0, paramString, null);
    setContentSpec(i, this.fContentSpec);
    return i;
  }
  
  protected int addContentSpecNode(short paramShort, int paramInt1, int paramInt2) {
    int i = createContentSpec();
    int[] arrayOfInt1 = new int[1];
    int[] arrayOfInt2 = new int[1];
    arrayOfInt1[0] = paramInt1;
    arrayOfInt2[0] = paramInt2;
    this.fContentSpec.setValues(paramShort, arrayOfInt1, arrayOfInt2);
    setContentSpec(i, this.fContentSpec);
    return i;
  }
  
  protected void initializeContentModelStack() {
    if (this.fOpStack == null) {
      this.fOpStack = new short[8];
      this.fNodeIndexStack = new int[8];
      this.fPrevNodeIndexStack = new int[8];
    } else if (this.fDepth == this.fOpStack.length) {
      short[] arrayOfShort = new short[this.fDepth * 2];
      System.arraycopy(this.fOpStack, 0, arrayOfShort, 0, this.fDepth);
      this.fOpStack = arrayOfShort;
      int[] arrayOfInt = new int[this.fDepth * 2];
      System.arraycopy(this.fNodeIndexStack, 0, arrayOfInt, 0, this.fDepth);
      this.fNodeIndexStack = arrayOfInt;
      arrayOfInt = new int[this.fDepth * 2];
      System.arraycopy(this.fPrevNodeIndexStack, 0, arrayOfInt, 0, this.fDepth);
      this.fPrevNodeIndexStack = arrayOfInt;
    } 
    this.fOpStack[this.fDepth] = -1;
    this.fNodeIndexStack[this.fDepth] = -1;
    this.fPrevNodeIndexStack[this.fDepth] = -1;
  }
  
  boolean isImmutable() { return this.fIsImmutable; }
  
  private void appendContentSpec(XMLContentSpec paramXMLContentSpec, StringBuffer paramStringBuffer, boolean paramBoolean, int paramInt) {
    int i;
    short s2;
    short s1 = paramXMLContentSpec.type & 0xF;
    switch (s1) {
      case 0:
        if (paramXMLContentSpec.value == null && paramXMLContentSpec.otherValue == null) {
          paramStringBuffer.append("#PCDATA");
        } else if (paramXMLContentSpec.value == null && paramXMLContentSpec.otherValue != null) {
          paramStringBuffer.append("##any:uri=").append(paramXMLContentSpec.otherValue);
        } else if (paramXMLContentSpec.value == null) {
          paramStringBuffer.append("##any");
        } else {
          paramStringBuffer.append(paramXMLContentSpec.value);
        } 
        return;
      case 1:
        if (paramInt == 3 || paramInt == 2 || paramInt == 1) {
          getContentSpec((int[])paramXMLContentSpec.value[0], paramXMLContentSpec);
          paramStringBuffer.append('(');
          appendContentSpec(paramXMLContentSpec, paramStringBuffer, true, s1);
          paramStringBuffer.append(')');
        } else {
          getContentSpec((int[])paramXMLContentSpec.value[0], paramXMLContentSpec);
          appendContentSpec(paramXMLContentSpec, paramStringBuffer, true, s1);
        } 
        paramStringBuffer.append('?');
        return;
      case 2:
        if (paramInt == 3 || paramInt == 2 || paramInt == 1) {
          getContentSpec((int[])paramXMLContentSpec.value[0], paramXMLContentSpec);
          paramStringBuffer.append('(');
          appendContentSpec(paramXMLContentSpec, paramStringBuffer, true, s1);
          paramStringBuffer.append(')');
        } else {
          getContentSpec((int[])paramXMLContentSpec.value[0], paramXMLContentSpec);
          appendContentSpec(paramXMLContentSpec, paramStringBuffer, true, s1);
        } 
        paramStringBuffer.append('*');
        return;
      case 3:
        if (paramInt == 3 || paramInt == 2 || paramInt == 1) {
          paramStringBuffer.append('(');
          getContentSpec((int[])paramXMLContentSpec.value[0], paramXMLContentSpec);
          appendContentSpec(paramXMLContentSpec, paramStringBuffer, true, s1);
          paramStringBuffer.append(')');
        } else {
          getContentSpec((int[])paramXMLContentSpec.value[0], paramXMLContentSpec);
          appendContentSpec(paramXMLContentSpec, paramStringBuffer, true, s1);
        } 
        paramStringBuffer.append('+');
        return;
      case 4:
      case 5:
        if (paramBoolean)
          paramStringBuffer.append('('); 
        s2 = paramXMLContentSpec.type;
        i = (int[])paramXMLContentSpec.otherValue[0];
        getContentSpec((int[])paramXMLContentSpec.value[0], paramXMLContentSpec);
        appendContentSpec(paramXMLContentSpec, paramStringBuffer, (paramXMLContentSpec.type != s2), s1);
        if (s2 == 4) {
          paramStringBuffer.append('|');
        } else {
          paramStringBuffer.append(',');
        } 
        getContentSpec(i, paramXMLContentSpec);
        appendContentSpec(paramXMLContentSpec, paramStringBuffer, true, s1);
        if (paramBoolean)
          paramStringBuffer.append(')'); 
        return;
      case 6:
        paramStringBuffer.append("##any");
        if (paramXMLContentSpec.otherValue != null) {
          paramStringBuffer.append(":uri=");
          paramStringBuffer.append(paramXMLContentSpec.otherValue);
        } 
        return;
      case 7:
        paramStringBuffer.append("##other:uri=");
        paramStringBuffer.append(paramXMLContentSpec.otherValue);
        return;
      case 8:
        paramStringBuffer.append("##local");
        return;
    } 
    paramStringBuffer.append("???");
  }
  
  private void printAttribute(int paramInt) {
    XMLAttributeDecl xMLAttributeDecl = new XMLAttributeDecl();
    if (getAttributeDecl(paramInt, xMLAttributeDecl)) {
      System.out.print(" { ");
      System.out.print(xMLAttributeDecl.name.localpart);
      System.out.print(" }");
    } 
  }
  
  private ContentModelValidator createChildModel(int paramInt) {
    XMLContentSpec xMLContentSpec = new XMLContentSpec();
    getContentSpec(paramInt, xMLContentSpec);
    if ((xMLContentSpec.type & 0xF) != 6 && (xMLContentSpec.type & 0xF) != 7 && (xMLContentSpec.type & 0xF) != 8) {
      if (xMLContentSpec.type == 0) {
        if (xMLContentSpec.value == null && xMLContentSpec.otherValue == null)
          throw new RuntimeException("ImplementationMessages.VAL_NPCD"); 
        this.fQName.setValues(null, (String)xMLContentSpec.value, (String)xMLContentSpec.value, (String)xMLContentSpec.otherValue);
        return new SimpleContentModel(xMLContentSpec.type, this.fQName, null);
      } 
      if (xMLContentSpec.type == 4 || xMLContentSpec.type == 5) {
        XMLContentSpec xMLContentSpec1 = new XMLContentSpec();
        XMLContentSpec xMLContentSpec2 = new XMLContentSpec();
        getContentSpec((int[])xMLContentSpec.value[0], xMLContentSpec1);
        getContentSpec((int[])xMLContentSpec.otherValue[0], xMLContentSpec2);
        if (xMLContentSpec1.type == 0 && xMLContentSpec2.type == 0) {
          this.fQName.setValues(null, (String)xMLContentSpec1.value, (String)xMLContentSpec1.value, (String)xMLContentSpec1.otherValue);
          this.fQName2.setValues(null, (String)xMLContentSpec2.value, (String)xMLContentSpec2.value, (String)xMLContentSpec2.otherValue);
          return new SimpleContentModel(xMLContentSpec.type, this.fQName, this.fQName2);
        } 
      } else if (xMLContentSpec.type == 1 || xMLContentSpec.type == 2 || xMLContentSpec.type == 3) {
        XMLContentSpec xMLContentSpec1 = new XMLContentSpec();
        getContentSpec((int[])xMLContentSpec.value[0], xMLContentSpec1);
        if (xMLContentSpec1.type == 0) {
          this.fQName.setValues(null, (String)xMLContentSpec1.value, (String)xMLContentSpec1.value, (String)xMLContentSpec1.otherValue);
          return new SimpleContentModel(xMLContentSpec.type, this.fQName, null);
        } 
      } else {
        throw new RuntimeException("ImplementationMessages.VAL_CST");
      } 
    } 
    this.fLeafCount = 0;
    this.fLeafCount = 0;
    CMNode cMNode = buildSyntaxTree(paramInt, xMLContentSpec);
    return new DFAContentModel(cMNode, this.fLeafCount, false);
  }
  
  private final CMNode buildSyntaxTree(int paramInt, XMLContentSpec paramXMLContentSpec) {
    CMUniOp cMUniOp = null;
    getContentSpec(paramInt, paramXMLContentSpec);
    if ((paramXMLContentSpec.type & 0xF) == 6) {
      cMUniOp = new CMAny(paramXMLContentSpec.type, (String)paramXMLContentSpec.otherValue, this.fLeafCount++);
    } else if ((paramXMLContentSpec.type & 0xF) == 7) {
      cMUniOp = new CMAny(paramXMLContentSpec.type, (String)paramXMLContentSpec.otherValue, this.fLeafCount++);
    } else if ((paramXMLContentSpec.type & 0xF) == 8) {
      cMUniOp = new CMAny(paramXMLContentSpec.type, null, this.fLeafCount++);
    } else if (paramXMLContentSpec.type == 0) {
      this.fQName.setValues(null, (String)paramXMLContentSpec.value, (String)paramXMLContentSpec.value, (String)paramXMLContentSpec.otherValue);
      CMLeaf cMLeaf = new CMLeaf(this.fQName, this.fLeafCount++);
    } else {
      int i = (int[])paramXMLContentSpec.value[0];
      int j = (int[])paramXMLContentSpec.otherValue[0];
      if (paramXMLContentSpec.type == 4 || paramXMLContentSpec.type == 5) {
        CMBinOp cMBinOp = new CMBinOp(paramXMLContentSpec.type, buildSyntaxTree(i, paramXMLContentSpec), buildSyntaxTree(j, paramXMLContentSpec));
      } else if (paramXMLContentSpec.type == 2) {
        CMUniOp cMUniOp1 = new CMUniOp(paramXMLContentSpec.type, buildSyntaxTree(i, paramXMLContentSpec));
      } else if (paramXMLContentSpec.type == 2 || paramXMLContentSpec.type == 1 || paramXMLContentSpec.type == 3) {
        cMUniOp = new CMUniOp(paramXMLContentSpec.type, buildSyntaxTree(i, paramXMLContentSpec));
      } else {
        throw new RuntimeException("ImplementationMessages.VAL_CST");
      } 
    } 
    return cMUniOp;
  }
  
  private void contentSpecTree(int paramInt, XMLContentSpec paramXMLContentSpec, ChildrenList paramChildrenList) {
    getContentSpec(paramInt, paramXMLContentSpec);
    if (paramXMLContentSpec.type == 0 || (paramXMLContentSpec.type & 0xF) == 6 || (paramXMLContentSpec.type & 0xF) == 8 || (paramXMLContentSpec.type & 0xF) == 7) {
      if (paramChildrenList.length == paramChildrenList.qname.length) {
        QName[] arrayOfQName = new QName[paramChildrenList.length * 2];
        System.arraycopy(paramChildrenList.qname, 0, arrayOfQName, 0, paramChildrenList.length);
        paramChildrenList.qname = arrayOfQName;
        int[] arrayOfInt = new int[paramChildrenList.length * 2];
        System.arraycopy(paramChildrenList.type, 0, arrayOfInt, 0, paramChildrenList.length);
        paramChildrenList.type = arrayOfInt;
      } 
      paramChildrenList.qname[paramChildrenList.length] = new QName(null, (String)paramXMLContentSpec.value, (String)paramXMLContentSpec.value, (String)paramXMLContentSpec.otherValue);
      paramChildrenList.type[paramChildrenList.length] = paramXMLContentSpec.type;
      paramChildrenList.length++;
      return;
    } 
    int i = (paramXMLContentSpec.value != null) ? (int[])paramXMLContentSpec.value[0] : -1;
    int j = -1;
    if (paramXMLContentSpec.otherValue != null) {
      j = (int[])paramXMLContentSpec.otherValue[0];
    } else {
      return;
    } 
    if (paramXMLContentSpec.type == 4 || paramXMLContentSpec.type == 5) {
      contentSpecTree(i, paramXMLContentSpec, paramChildrenList);
      contentSpecTree(j, paramXMLContentSpec, paramChildrenList);
      return;
    } 
    if (paramXMLContentSpec.type == 1 || paramXMLContentSpec.type == 2 || paramXMLContentSpec.type == 3) {
      contentSpecTree(i, paramXMLContentSpec, paramChildrenList);
      return;
    } 
    throw new RuntimeException("Invalid content spec type seen in contentSpecTree() method of AbstractDTDGrammar class : " + paramXMLContentSpec.type);
  }
  
  private void ensureElementDeclCapacity(int paramInt) {
    if (paramInt >= this.fElementDeclName.length) {
      this.fElementDeclIsExternal = resize(this.fElementDeclIsExternal, this.fElementDeclIsExternal.length * 2);
      this.fElementDeclName = resize(this.fElementDeclName, this.fElementDeclName.length * 2);
      this.fElementDeclType = resize(this.fElementDeclType, this.fElementDeclType.length * 2);
      this.fElementDeclContentModelValidator = resize(this.fElementDeclContentModelValidator, this.fElementDeclContentModelValidator.length * 2);
      this.fElementDeclContentSpecIndex = resize(this.fElementDeclContentSpecIndex, this.fElementDeclContentSpecIndex.length * 2);
      this.fElementDeclFirstAttributeDeclIndex = resize(this.fElementDeclFirstAttributeDeclIndex, this.fElementDeclFirstAttributeDeclIndex.length * 2);
      this.fElementDeclLastAttributeDeclIndex = resize(this.fElementDeclLastAttributeDeclIndex, this.fElementDeclLastAttributeDeclIndex.length * 2);
    } else if (this.fElementDeclName[paramInt] != null) {
      return;
    } 
    this.fElementDeclIsExternal[paramInt] = new int[256];
    this.fElementDeclName[paramInt] = new QName[256];
    this.fElementDeclType[paramInt] = new short[256];
    this.fElementDeclContentModelValidator[paramInt] = new ContentModelValidator[256];
    this.fElementDeclContentSpecIndex[paramInt] = new int[256];
    this.fElementDeclFirstAttributeDeclIndex[paramInt] = new int[256];
    this.fElementDeclLastAttributeDeclIndex[paramInt] = new int[256];
  }
  
  private void ensureAttributeDeclCapacity(int paramInt) {
    if (paramInt >= this.fAttributeDeclName.length) {
      this.fAttributeDeclIsExternal = resize(this.fAttributeDeclIsExternal, this.fAttributeDeclIsExternal.length * 2);
      this.fAttributeDeclName = resize(this.fAttributeDeclName, this.fAttributeDeclName.length * 2);
      this.fAttributeDeclType = resize(this.fAttributeDeclType, this.fAttributeDeclType.length * 2);
      this.fAttributeDeclEnumeration = resize(this.fAttributeDeclEnumeration, this.fAttributeDeclEnumeration.length * 2);
      this.fAttributeDeclDefaultType = resize(this.fAttributeDeclDefaultType, this.fAttributeDeclDefaultType.length * 2);
      this.fAttributeDeclDatatypeValidator = resize(this.fAttributeDeclDatatypeValidator, this.fAttributeDeclDatatypeValidator.length * 2);
      this.fAttributeDeclDefaultValue = resize(this.fAttributeDeclDefaultValue, this.fAttributeDeclDefaultValue.length * 2);
      this.fAttributeDeclNonNormalizedDefaultValue = resize(this.fAttributeDeclNonNormalizedDefaultValue, this.fAttributeDeclNonNormalizedDefaultValue.length * 2);
      this.fAttributeDeclNextAttributeDeclIndex = resize(this.fAttributeDeclNextAttributeDeclIndex, this.fAttributeDeclNextAttributeDeclIndex.length * 2);
    } else if (this.fAttributeDeclName[paramInt] != null) {
      return;
    } 
    this.fAttributeDeclIsExternal[paramInt] = new int[256];
    this.fAttributeDeclName[paramInt] = new QName[256];
    this.fAttributeDeclType[paramInt] = new short[256];
    this.fAttributeDeclEnumeration[paramInt] = new String[256][];
    this.fAttributeDeclDefaultType[paramInt] = new short[256];
    this.fAttributeDeclDatatypeValidator[paramInt] = new DatatypeValidator[256];
    this.fAttributeDeclDefaultValue[paramInt] = new String[256];
    this.fAttributeDeclNonNormalizedDefaultValue[paramInt] = new String[256];
    this.fAttributeDeclNextAttributeDeclIndex[paramInt] = new int[256];
  }
  
  private void ensureEntityDeclCapacity(int paramInt) {
    if (paramInt >= this.fEntityName.length) {
      this.fEntityName = resize(this.fEntityName, this.fEntityName.length * 2);
      this.fEntityValue = resize(this.fEntityValue, this.fEntityValue.length * 2);
      this.fEntityPublicId = resize(this.fEntityPublicId, this.fEntityPublicId.length * 2);
      this.fEntitySystemId = resize(this.fEntitySystemId, this.fEntitySystemId.length * 2);
      this.fEntityBaseSystemId = resize(this.fEntityBaseSystemId, this.fEntityBaseSystemId.length * 2);
      this.fEntityNotation = resize(this.fEntityNotation, this.fEntityNotation.length * 2);
      this.fEntityIsPE = resize(this.fEntityIsPE, this.fEntityIsPE.length * 2);
      this.fEntityInExternal = resize(this.fEntityInExternal, this.fEntityInExternal.length * 2);
    } else if (this.fEntityName[paramInt] != null) {
      return;
    } 
    this.fEntityName[paramInt] = new String[256];
    this.fEntityValue[paramInt] = new String[256];
    this.fEntityPublicId[paramInt] = new String[256];
    this.fEntitySystemId[paramInt] = new String[256];
    this.fEntityBaseSystemId[paramInt] = new String[256];
    this.fEntityNotation[paramInt] = new String[256];
    this.fEntityIsPE[paramInt] = new byte[256];
    this.fEntityInExternal[paramInt] = new byte[256];
  }
  
  private void ensureNotationDeclCapacity(int paramInt) {
    if (paramInt >= this.fNotationName.length) {
      this.fNotationName = resize(this.fNotationName, this.fNotationName.length * 2);
      this.fNotationPublicId = resize(this.fNotationPublicId, this.fNotationPublicId.length * 2);
      this.fNotationSystemId = resize(this.fNotationSystemId, this.fNotationSystemId.length * 2);
      this.fNotationBaseSystemId = resize(this.fNotationBaseSystemId, this.fNotationBaseSystemId.length * 2);
    } else if (this.fNotationName[paramInt] != null) {
      return;
    } 
    this.fNotationName[paramInt] = new String[256];
    this.fNotationPublicId[paramInt] = new String[256];
    this.fNotationSystemId[paramInt] = new String[256];
    this.fNotationBaseSystemId[paramInt] = new String[256];
  }
  
  private void ensureContentSpecCapacity(int paramInt) {
    if (paramInt >= this.fContentSpecType.length) {
      this.fContentSpecType = resize(this.fContentSpecType, this.fContentSpecType.length * 2);
      this.fContentSpecValue = resize(this.fContentSpecValue, this.fContentSpecValue.length * 2);
      this.fContentSpecOtherValue = resize(this.fContentSpecOtherValue, this.fContentSpecOtherValue.length * 2);
    } else if (this.fContentSpecType[paramInt] != null) {
      return;
    } 
    this.fContentSpecType[paramInt] = new short[256];
    this.fContentSpecValue[paramInt] = new Object[256];
    this.fContentSpecOtherValue[paramInt] = new Object[256];
  }
  
  private static byte[][] resize(byte[][] paramArrayOfByte, int paramInt) {
    byte[][] arrayOfByte = new byte[paramInt][];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
    return arrayOfByte;
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
  
  private static DatatypeValidator[][] resize(DatatypeValidator[][] paramArrayOfDatatypeValidator, int paramInt) {
    DatatypeValidator[][] arrayOfDatatypeValidator = new DatatypeValidator[paramInt][];
    System.arraycopy(paramArrayOfDatatypeValidator, 0, arrayOfDatatypeValidator, 0, paramArrayOfDatatypeValidator.length);
    return arrayOfDatatypeValidator;
  }
  
  private static ContentModelValidator[][] resize(ContentModelValidator[][] paramArrayOfContentModelValidator, int paramInt) {
    ContentModelValidator[][] arrayOfContentModelValidator = new ContentModelValidator[paramInt][];
    System.arraycopy(paramArrayOfContentModelValidator, 0, arrayOfContentModelValidator, 0, paramArrayOfContentModelValidator.length);
    return arrayOfContentModelValidator;
  }
  
  private static Object[][] resize(Object[][] paramArrayOfObject, int paramInt) {
    Object[][] arrayOfObject = new Object[paramInt][];
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, paramArrayOfObject.length);
    return arrayOfObject;
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
  
  public boolean isEntityDeclared(String paramString) { return (getEntityDeclIndex(paramString) != -1); }
  
  public boolean isEntityUnparsed(String paramString) {
    int i = getEntityDeclIndex(paramString);
    if (i > -1) {
      int j = i >> 8;
      int k = i & 0xFF;
      return (this.fEntityNotation[j][k] != null);
    } 
    return false;
  }
  
  private static class ChildrenList {
    public int length = 0;
    
    public QName[] qname = new QName[2];
    
    public int[] type = new int[2];
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\DTDGrammar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */