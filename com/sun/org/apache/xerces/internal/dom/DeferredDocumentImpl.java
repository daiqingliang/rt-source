package com.sun.org.apache.xerces.internal.dom;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DeferredDocumentImpl extends DocumentImpl implements DeferredNode {
  static final long serialVersionUID = 5186323580749626857L;
  
  private static final boolean DEBUG_PRINT_REF_COUNTS = false;
  
  private static final boolean DEBUG_PRINT_TABLES = false;
  
  private static final boolean DEBUG_IDS = false;
  
  protected static final int CHUNK_SHIFT = 8;
  
  protected static final int CHUNK_SIZE = 256;
  
  protected static final int CHUNK_MASK = 255;
  
  protected static final int INITIAL_CHUNK_COUNT = 32;
  
  protected int fNodeCount = 0;
  
  protected int[][] fNodeType;
  
  protected Object[][] fNodeName;
  
  protected Object[][] fNodeValue;
  
  protected int[][] fNodeParent;
  
  protected int[][] fNodeLastChild;
  
  protected int[][] fNodePrevSib;
  
  protected Object[][] fNodeURI;
  
  protected int[][] fNodeExtra;
  
  protected int fIdCount;
  
  protected String[] fIdName;
  
  protected int[] fIdElement;
  
  protected boolean fNamespacesEnabled = false;
  
  private final StringBuilder fBufferStr = new StringBuilder();
  
  private final ArrayList fStrChunks = new ArrayList();
  
  private static final int[] INIT_ARRAY = new int[257];
  
  public DeferredDocumentImpl() { this(false); }
  
  public DeferredDocumentImpl(boolean paramBoolean) { this(paramBoolean, false); }
  
  public DeferredDocumentImpl(boolean paramBoolean1, boolean paramBoolean2) {
    super(paramBoolean2);
    needsSyncData(true);
    needsSyncChildren(true);
    this.fNamespacesEnabled = paramBoolean1;
  }
  
  public DOMImplementation getImplementation() { return DeferredDOMImplementationImpl.getDOMImplementation(); }
  
  boolean getNamespacesEnabled() { return this.fNamespacesEnabled; }
  
  void setNamespacesEnabled(boolean paramBoolean) { this.fNamespacesEnabled = paramBoolean; }
  
  public int createDeferredDocument() { return createNode((short)9); }
  
  public int createDeferredDocumentType(String paramString1, String paramString2, String paramString3) {
    int i = createNode((short)10);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkValue(this.fNodeName, paramString1, j, k);
    setChunkValue(this.fNodeValue, paramString2, j, k);
    setChunkValue(this.fNodeURI, paramString3, j, k);
    return i;
  }
  
  public void setInternalSubset(int paramInt, String paramString) {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    int k = createNode((short)10);
    int m = k >> 8;
    int n = k & 0xFF;
    setChunkIndex(this.fNodeExtra, k, i, j);
    setChunkValue(this.fNodeValue, paramString, m, n);
  }
  
  public int createDeferredNotation(String paramString1, String paramString2, String paramString3, String paramString4) {
    int i = createNode((short)12);
    int j = i >> 8;
    int k = i & 0xFF;
    int m = createNode((short)12);
    int n = m >> 8;
    int i1 = m & 0xFF;
    setChunkValue(this.fNodeName, paramString1, j, k);
    setChunkValue(this.fNodeValue, paramString2, j, k);
    setChunkValue(this.fNodeURI, paramString3, j, k);
    setChunkIndex(this.fNodeExtra, m, j, k);
    setChunkValue(this.fNodeName, paramString4, n, i1);
    return i;
  }
  
  public int createDeferredEntity(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    int i = createNode((short)6);
    int j = i >> 8;
    int k = i & 0xFF;
    int m = createNode((short)6);
    int n = m >> 8;
    int i1 = m & 0xFF;
    setChunkValue(this.fNodeName, paramString1, j, k);
    setChunkValue(this.fNodeValue, paramString2, j, k);
    setChunkValue(this.fNodeURI, paramString3, j, k);
    setChunkIndex(this.fNodeExtra, m, j, k);
    setChunkValue(this.fNodeName, paramString4, n, i1);
    setChunkValue(this.fNodeValue, null, n, i1);
    setChunkValue(this.fNodeURI, null, n, i1);
    int i2 = createNode((short)6);
    int i3 = i2 >> 8;
    int i4 = i2 & 0xFF;
    setChunkIndex(this.fNodeExtra, i2, n, i1);
    setChunkValue(this.fNodeName, paramString5, i3, i4);
    return i;
  }
  
  public String getDeferredEntityBaseURI(int paramInt) {
    if (paramInt != -1) {
      int i = getNodeExtra(paramInt, false);
      i = getNodeExtra(i, false);
      return getNodeName(i, false);
    } 
    return null;
  }
  
  public void setEntityInfo(int paramInt, String paramString1, String paramString2) {
    int i = getNodeExtra(paramInt, false);
    if (i != -1) {
      int j = i >> 8;
      int k = i & 0xFF;
      setChunkValue(this.fNodeValue, paramString1, j, k);
      setChunkValue(this.fNodeURI, paramString2, j, k);
    } 
  }
  
  public void setTypeInfo(int paramInt, Object paramObject) {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    setChunkValue(this.fNodeValue, paramObject, i, j);
  }
  
  public void setInputEncoding(int paramInt, String paramString) {
    int i = getNodeExtra(paramInt, false);
    int j = getNodeExtra(i, false);
    int k = j >> 8;
    int m = j & 0xFF;
    setChunkValue(this.fNodeValue, paramString, k, m);
  }
  
  public int createDeferredEntityReference(String paramString1, String paramString2) {
    int i = createNode((short)5);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkValue(this.fNodeName, paramString1, j, k);
    setChunkValue(this.fNodeValue, paramString2, j, k);
    return i;
  }
  
  public int createDeferredElement(String paramString1, String paramString2, Object paramObject) {
    int i = createNode((short)1);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkValue(this.fNodeName, paramString2, j, k);
    setChunkValue(this.fNodeURI, paramString1, j, k);
    setChunkValue(this.fNodeValue, paramObject, j, k);
    return i;
  }
  
  public int createDeferredElement(String paramString) { return createDeferredElement(null, paramString); }
  
  public int createDeferredElement(String paramString1, String paramString2) {
    int i = createNode((short)1);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkValue(this.fNodeName, paramString2, j, k);
    setChunkValue(this.fNodeURI, paramString1, j, k);
    return i;
  }
  
  public int setDeferredAttribute(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, Object paramObject) {
    int i = createDeferredAttribute(paramString1, paramString2, paramString3, paramBoolean1);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkIndex(this.fNodeParent, paramInt, j, k);
    int m = paramInt >> 8;
    int n = paramInt & 0xFF;
    int i1 = getChunkIndex(this.fNodeExtra, m, n);
    if (i1 != 0)
      setChunkIndex(this.fNodePrevSib, i1, j, k); 
    setChunkIndex(this.fNodeExtra, i, m, n);
    int i2 = getChunkIndex(this.fNodeExtra, j, k);
    if (paramBoolean2) {
      i2 |= 0x200;
      setChunkIndex(this.fNodeExtra, i2, j, k);
      String str = getChunkValue(this.fNodeValue, j, k);
      putIdentifier(str, paramInt);
    } 
    if (paramObject != null) {
      int i3 = createNode((short)20);
      int i4 = i3 >> 8;
      int i5 = i3 & 0xFF;
      setChunkIndex(this.fNodeLastChild, i3, j, k);
      setChunkValue(this.fNodeValue, paramObject, i4, i5);
    } 
    return i;
  }
  
  public int setDeferredAttribute(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    int i = createDeferredAttribute(paramString1, paramString2, paramString3, paramBoolean);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkIndex(this.fNodeParent, paramInt, j, k);
    int m = paramInt >> 8;
    int n = paramInt & 0xFF;
    int i1 = getChunkIndex(this.fNodeExtra, m, n);
    if (i1 != 0)
      setChunkIndex(this.fNodePrevSib, i1, j, k); 
    setChunkIndex(this.fNodeExtra, i, m, n);
    return i;
  }
  
  public int createDeferredAttribute(String paramString1, String paramString2, boolean paramBoolean) { return createDeferredAttribute(paramString1, null, paramString2, paramBoolean); }
  
  public int createDeferredAttribute(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    int i = createNode((short)2);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkValue(this.fNodeName, paramString1, j, k);
    setChunkValue(this.fNodeURI, paramString2, j, k);
    setChunkValue(this.fNodeValue, paramString3, j, k);
    byte b = paramBoolean ? 32 : 0;
    setChunkIndex(this.fNodeExtra, b, j, k);
    return i;
  }
  
  public int createDeferredElementDefinition(String paramString) {
    int i = createNode((short)21);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkValue(this.fNodeName, paramString, j, k);
    return i;
  }
  
  public int createDeferredTextNode(String paramString, boolean paramBoolean) {
    int i = createNode((short)3);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkValue(this.fNodeValue, paramString, j, k);
    setChunkIndex(this.fNodeExtra, paramBoolean ? 1 : 0, j, k);
    return i;
  }
  
  public int createDeferredCDATASection(String paramString) {
    int i = createNode((short)4);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkValue(this.fNodeValue, paramString, j, k);
    return i;
  }
  
  public int createDeferredProcessingInstruction(String paramString1, String paramString2) {
    int i = createNode((short)7);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkValue(this.fNodeName, paramString1, j, k);
    setChunkValue(this.fNodeValue, paramString2, j, k);
    return i;
  }
  
  public int createDeferredComment(String paramString) {
    int i = createNode((short)8);
    int j = i >> 8;
    int k = i & 0xFF;
    setChunkValue(this.fNodeValue, paramString, j, k);
    return i;
  }
  
  public int cloneNode(int paramInt, boolean paramBoolean) {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    int k = this.fNodeType[i][j];
    int m = createNode((short)k);
    int n = m >> 8;
    int i1 = m & 0xFF;
    setChunkValue(this.fNodeName, this.fNodeName[i][j], n, i1);
    setChunkValue(this.fNodeValue, this.fNodeValue[i][j], n, i1);
    setChunkValue(this.fNodeURI, this.fNodeURI[i][j], n, i1);
    int i2 = this.fNodeExtra[i][j];
    if (i2 != -1) {
      if (k != 2 && k != 3)
        i2 = cloneNode(i2, false); 
      setChunkIndex(this.fNodeExtra, i2, n, i1);
    } 
    if (paramBoolean) {
      int i3 = -1;
      int i4;
      for (i4 = getLastChild(paramInt, false); i4 != -1; i4 = getRealPrevSibling(i4, false)) {
        int i5 = cloneNode(i4, paramBoolean);
        insertBefore(m, i5, i3);
        i3 = i5;
      } 
    } 
    return m;
  }
  
  public void appendChild(int paramInt1, int paramInt2) {
    int i = paramInt1 >> 8;
    int j = paramInt1 & 0xFF;
    int k = paramInt2 >> 8;
    int m = paramInt2 & 0xFF;
    setChunkIndex(this.fNodeParent, paramInt1, k, m);
    int n = getChunkIndex(this.fNodeLastChild, i, j);
    setChunkIndex(this.fNodePrevSib, n, k, m);
    setChunkIndex(this.fNodeLastChild, paramInt2, i, j);
  }
  
  public int setAttributeNode(int paramInt1, int paramInt2) {
    int i = paramInt1 >> 8;
    int j = paramInt1 & 0xFF;
    int k = paramInt2 >> 8;
    int m = paramInt2 & 0xFF;
    String str = getChunkValue(this.fNodeName, k, m);
    int n = getChunkIndex(this.fNodeExtra, i, j);
    int i1 = -1;
    int i2 = -1;
    int i3 = -1;
    while (n != -1) {
      i2 = n >> 8;
      i3 = n & 0xFF;
      String str1 = getChunkValue(this.fNodeName, i2, i3);
      if (str1.equals(str))
        break; 
      i1 = n;
      n = getChunkIndex(this.fNodePrevSib, i2, i3);
    } 
    if (n != -1) {
      int i5 = getChunkIndex(this.fNodePrevSib, i2, i3);
      if (i1 == -1) {
        setChunkIndex(this.fNodeExtra, i5, i, j);
      } else {
        int i9 = i1 >> 8;
        int i10 = i1 & 0xFF;
        setChunkIndex(this.fNodePrevSib, i5, i9, i10);
      } 
      clearChunkIndex(this.fNodeType, i2, i3);
      clearChunkValue(this.fNodeName, i2, i3);
      clearChunkValue(this.fNodeValue, i2, i3);
      clearChunkIndex(this.fNodeParent, i2, i3);
      clearChunkIndex(this.fNodePrevSib, i2, i3);
      int i6 = clearChunkIndex(this.fNodeLastChild, i2, i3);
      int i7 = i6 >> 8;
      int i8 = i6 & 0xFF;
      clearChunkIndex(this.fNodeType, i7, i8);
      clearChunkValue(this.fNodeValue, i7, i8);
      clearChunkIndex(this.fNodeParent, i7, i8);
      clearChunkIndex(this.fNodeLastChild, i7, i8);
    } 
    int i4 = getChunkIndex(this.fNodeExtra, i, j);
    setChunkIndex(this.fNodeExtra, paramInt2, i, j);
    setChunkIndex(this.fNodePrevSib, i4, k, m);
    return n;
  }
  
  public void setIdAttributeNode(int paramInt1, int paramInt2) {
    int i = paramInt2 >> 8;
    int j = paramInt2 & 0xFF;
    int k = getChunkIndex(this.fNodeExtra, i, j);
    k |= 0x200;
    setChunkIndex(this.fNodeExtra, k, i, j);
    String str = getChunkValue(this.fNodeValue, i, j);
    putIdentifier(str, paramInt1);
  }
  
  public void setIdAttribute(int paramInt) {
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    int k = getChunkIndex(this.fNodeExtra, i, j);
    k |= 0x200;
    setChunkIndex(this.fNodeExtra, k, i, j);
  }
  
  public int insertBefore(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 == -1) {
      appendChild(paramInt1, paramInt2);
      return paramInt2;
    } 
    int i = paramInt2 >> 8;
    int j = paramInt2 & 0xFF;
    int k = paramInt3 >> 8;
    int m = paramInt3 & 0xFF;
    int n = getChunkIndex(this.fNodePrevSib, k, m);
    setChunkIndex(this.fNodePrevSib, paramInt2, k, m);
    setChunkIndex(this.fNodePrevSib, n, i, j);
    return paramInt2;
  }
  
  public void setAsLastChild(int paramInt1, int paramInt2) {
    int i = paramInt1 >> 8;
    int j = paramInt1 & 0xFF;
    setChunkIndex(this.fNodeLastChild, paramInt2, i, j);
  }
  
  public int getParentNode(int paramInt) { return getParentNode(paramInt, false); }
  
  public int getParentNode(int paramInt, boolean paramBoolean) {
    if (paramInt == -1)
      return -1; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return paramBoolean ? clearChunkIndex(this.fNodeParent, i, j) : getChunkIndex(this.fNodeParent, i, j);
  }
  
  public int getLastChild(int paramInt) { return getLastChild(paramInt, true); }
  
  public int getLastChild(int paramInt, boolean paramBoolean) {
    if (paramInt == -1)
      return -1; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return paramBoolean ? clearChunkIndex(this.fNodeLastChild, i, j) : getChunkIndex(this.fNodeLastChild, i, j);
  }
  
  public int getPrevSibling(int paramInt) { return getPrevSibling(paramInt, true); }
  
  public int getPrevSibling(int paramInt, boolean paramBoolean) {
    if (paramInt == -1)
      return -1; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    int k = getChunkIndex(this.fNodeType, i, j);
    if (k == 3) {
      do {
        paramInt = getChunkIndex(this.fNodePrevSib, i, j);
        if (paramInt == -1)
          break; 
        i = paramInt >> 8;
        j = paramInt & 0xFF;
        k = getChunkIndex(this.fNodeType, i, j);
      } while (k == 3);
    } else {
      paramInt = getChunkIndex(this.fNodePrevSib, i, j);
    } 
    return paramInt;
  }
  
  public int getRealPrevSibling(int paramInt) { return getRealPrevSibling(paramInt, true); }
  
  public int getRealPrevSibling(int paramInt, boolean paramBoolean) {
    if (paramInt == -1)
      return -1; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return paramBoolean ? clearChunkIndex(this.fNodePrevSib, i, j) : getChunkIndex(this.fNodePrevSib, i, j);
  }
  
  public int lookupElementDefinition(String paramString) {
    if (this.fNodeCount > 1) {
      int i = -1;
      int j = 0;
      int k = 0;
      int m;
      for (m = getChunkIndex(this.fNodeLastChild, j, k); m != -1; m = getChunkIndex(this.fNodePrevSib, j, k)) {
        j = m >> 8;
        k = m & 0xFF;
        if (getChunkIndex(this.fNodeType, j, k) == 10) {
          i = m;
          break;
        } 
      } 
      if (i == -1)
        return -1; 
      j = i >> 8;
      k = i & 0xFF;
      for (m = getChunkIndex(this.fNodeLastChild, j, k); m != -1; m = getChunkIndex(this.fNodePrevSib, j, k)) {
        j = m >> 8;
        k = m & 0xFF;
        if (getChunkIndex(this.fNodeType, j, k) == 21 && getChunkValue(this.fNodeName, j, k) == paramString)
          return m; 
      } 
    } 
    return -1;
  }
  
  public DeferredNode getNodeObject(int paramInt) {
    DeferredNotationImpl deferredNotationImpl;
    DeferredAttrImpl deferredAttrImpl;
    DeferredProcessingInstructionImpl deferredProcessingInstructionImpl;
    DeferredElementImpl deferredElementImpl;
    DeferredCDATASectionImpl deferredCDATASectionImpl;
    DeferredElementDefinitionImpl deferredElementDefinitionImpl;
    DeferredDocumentImpl deferredDocumentImpl;
    DeferredCommentImpl deferredCommentImpl;
    DeferredTextImpl deferredTextImpl;
    DeferredEntityReferenceImpl deferredEntityReferenceImpl;
    DeferredEntityImpl deferredEntityImpl;
    if (paramInt == -1)
      return null; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    int k = getChunkIndex(this.fNodeType, i, j);
    if (k != 3 && k != 4)
      clearChunkIndex(this.fNodeType, i, j); 
    DeferredAttrNSImpl deferredAttrNSImpl = null;
    switch (k) {
      case 2:
        if (this.fNamespacesEnabled) {
          deferredAttrNSImpl = new DeferredAttrNSImpl(this, paramInt);
          break;
        } 
        deferredAttrImpl = new DeferredAttrImpl(this, paramInt);
        break;
      case 4:
        deferredCDATASectionImpl = new DeferredCDATASectionImpl(this, paramInt);
        break;
      case 8:
        deferredCommentImpl = new DeferredCommentImpl(this, paramInt);
        break;
      case 9:
        deferredDocumentImpl = this;
        break;
      case 10:
        deferredElementImpl = new DeferredDocumentTypeImpl(this, paramInt);
        this.docType = (DocumentTypeImpl)deferredElementImpl;
        break;
      case 1:
        if (this.fNamespacesEnabled) {
          DeferredElementNSImpl deferredElementNSImpl = new DeferredElementNSImpl(this, paramInt);
        } else {
          deferredElementImpl = new DeferredElementImpl(this, paramInt);
        } 
        if (this.fIdElement != null) {
          int m;
          for (m = binarySearch(this.fIdElement, 0, this.fIdCount - 1, paramInt); m != -1; m = -1) {
            String str = this.fIdName[m];
            if (str != null) {
              putIdentifier0(str, (Element)deferredElementImpl);
              this.fIdName[m] = null;
            } 
            if (m + 1 < this.fIdCount && this.fIdElement[m + 1] == paramInt) {
              m++;
              continue;
            } 
          } 
        } 
        break;
      case 6:
        deferredEntityImpl = new DeferredEntityImpl(this, paramInt);
        break;
      case 5:
        deferredEntityReferenceImpl = new DeferredEntityReferenceImpl(this, paramInt);
        break;
      case 12:
        deferredNotationImpl = new DeferredNotationImpl(this, paramInt);
        break;
      case 7:
        deferredProcessingInstructionImpl = new DeferredProcessingInstructionImpl(this, paramInt);
        break;
      case 3:
        deferredTextImpl = new DeferredTextImpl(this, paramInt);
        break;
      case 21:
        deferredElementDefinitionImpl = new DeferredElementDefinitionImpl(this, paramInt);
        break;
      default:
        throw new IllegalArgumentException("type: " + k);
    } 
    if (deferredElementDefinitionImpl != null)
      return deferredElementDefinitionImpl; 
    throw new IllegalArgumentException();
  }
  
  public String getNodeName(int paramInt) { return getNodeName(paramInt, true); }
  
  public String getNodeName(int paramInt, boolean paramBoolean) {
    if (paramInt == -1)
      return null; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return paramBoolean ? clearChunkValue(this.fNodeName, i, j) : getChunkValue(this.fNodeName, i, j);
  }
  
  public String getNodeValueString(int paramInt) { return getNodeValueString(paramInt, true); }
  
  public String getNodeValueString(int paramInt, boolean paramBoolean) {
    if (paramInt == -1)
      return null; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    String str = paramBoolean ? clearChunkValue(this.fNodeValue, i, j) : getChunkValue(this.fNodeValue, i, j);
    if (str == null)
      return null; 
    int k = getChunkIndex(this.fNodeType, i, j);
    if (k == 3) {
      int m = getRealPrevSibling(paramInt);
      if (m != -1 && getNodeType(m, false) == 3) {
        this.fStrChunks.add(str);
        do {
          i = m >> 8;
          j = m & 0xFF;
          str = getChunkValue(this.fNodeValue, i, j);
          this.fStrChunks.add(str);
          m = getChunkIndex(this.fNodePrevSib, i, j);
        } while (m != -1 && getNodeType(m, false) == 3);
        int n = this.fStrChunks.size();
        for (int i1 = n - 1; i1 >= 0; i1--)
          this.fBufferStr.append((String)this.fStrChunks.get(i1)); 
        str = this.fBufferStr.toString();
        this.fStrChunks.clear();
        this.fBufferStr.setLength(0);
        return str;
      } 
    } else if (k == 4) {
      int m = getLastChild(paramInt, false);
      if (m != -1) {
        this.fBufferStr.append(str);
        while (m != -1) {
          i = m >> 8;
          j = m & 0xFF;
          str = getChunkValue(this.fNodeValue, i, j);
          this.fStrChunks.add(str);
          m = getChunkIndex(this.fNodePrevSib, i, j);
        } 
        for (int n = this.fStrChunks.size() - 1; n >= 0; n--)
          this.fBufferStr.append((String)this.fStrChunks.get(n)); 
        str = this.fBufferStr.toString();
        this.fStrChunks.clear();
        this.fBufferStr.setLength(0);
        return str;
      } 
    } 
    return str;
  }
  
  public String getNodeValue(int paramInt) { return getNodeValue(paramInt, true); }
  
  public Object getTypeInfo(int paramInt) {
    if (paramInt == -1)
      return null; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    Object object = (this.fNodeValue[i] != null) ? this.fNodeValue[i][j] : null;
    if (object != null) {
      this.fNodeValue[i][j] = null;
      RefCount refCount = (RefCount)this.fNodeValue[i][256];
      refCount.fCount--;
      if (refCount.fCount == 0)
        this.fNodeValue[i] = null; 
    } 
    return object;
  }
  
  public String getNodeValue(int paramInt, boolean paramBoolean) {
    if (paramInt == -1)
      return null; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return paramBoolean ? clearChunkValue(this.fNodeValue, i, j) : getChunkValue(this.fNodeValue, i, j);
  }
  
  public int getNodeExtra(int paramInt) { return getNodeExtra(paramInt, true); }
  
  public int getNodeExtra(int paramInt, boolean paramBoolean) {
    if (paramInt == -1)
      return -1; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return paramBoolean ? clearChunkIndex(this.fNodeExtra, i, j) : getChunkIndex(this.fNodeExtra, i, j);
  }
  
  public short getNodeType(int paramInt) { return getNodeType(paramInt, true); }
  
  public short getNodeType(int paramInt, boolean paramBoolean) {
    if (paramInt == -1)
      return -1; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return paramBoolean ? (short)clearChunkIndex(this.fNodeType, i, j) : (short)getChunkIndex(this.fNodeType, i, j);
  }
  
  public String getAttribute(int paramInt, String paramString) {
    if (paramInt == -1 || paramString == null)
      return null; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    int k;
    for (k = getChunkIndex(this.fNodeExtra, i, j); k != -1; k = getChunkIndex(this.fNodePrevSib, m, n)) {
      int m = k >> 8;
      int n = k & 0xFF;
      if (getChunkValue(this.fNodeName, m, n) == paramString)
        return getChunkValue(this.fNodeValue, m, n); 
    } 
    return null;
  }
  
  public String getNodeURI(int paramInt) { return getNodeURI(paramInt, true); }
  
  public String getNodeURI(int paramInt, boolean paramBoolean) {
    if (paramInt == -1)
      return null; 
    int i = paramInt >> 8;
    int j = paramInt & 0xFF;
    return paramBoolean ? clearChunkValue(this.fNodeURI, i, j) : getChunkValue(this.fNodeURI, i, j);
  }
  
  public void putIdentifier(String paramString, int paramInt) {
    if (this.fIdName == null) {
      this.fIdName = new String[64];
      this.fIdElement = new int[64];
    } 
    if (this.fIdCount == this.fIdName.length) {
      String[] arrayOfString = new String[this.fIdCount * 2];
      System.arraycopy(this.fIdName, 0, arrayOfString, 0, this.fIdCount);
      this.fIdName = arrayOfString;
      int[] arrayOfInt = new int[arrayOfString.length];
      System.arraycopy(this.fIdElement, 0, arrayOfInt, 0, this.fIdCount);
      this.fIdElement = arrayOfInt;
    } 
    this.fIdName[this.fIdCount] = paramString;
    this.fIdElement[this.fIdCount] = paramInt;
    this.fIdCount++;
  }
  
  public void print() {}
  
  public int getNodeIndex() { return 0; }
  
  protected void synchronizeData() {
    needsSyncData(false);
    if (this.fIdElement != null) {
      IntVector intVector = new IntVector();
      for (byte b = 0; b < this.fIdCount; b++) {
        int i = this.fIdElement[b];
        String str = this.fIdName[b];
        if (str != null) {
          intVector.removeAllElements();
          int j = i;
          do {
            intVector.addElement(j);
            int m = j >> 8;
            int n = j & 0xFF;
            j = getChunkIndex(this.fNodeParent, m, n);
          } while (j != -1);
          Node node = this;
          for (int k = intVector.size() - 2; k >= 0; k--) {
            j = intVector.elementAt(k);
            for (Node node1 = node.getLastChild(); node1 != null; node1 = node1.getPreviousSibling()) {
              if (node1 instanceof DeferredNode) {
                int m = ((DeferredNode)node1).getNodeIndex();
                if (m == j) {
                  node = node1;
                  break;
                } 
              } 
            } 
          } 
          Element element = (Element)node;
          putIdentifier0(str, element);
          this.fIdName[b] = null;
          while (b + true < this.fIdCount && this.fIdElement[b + true] == i) {
            str = this.fIdName[++b];
            if (str == null)
              continue; 
            putIdentifier0(str, element);
          } 
        } 
      } 
    } 
  }
  
  protected void synchronizeChildren() {
    if (needsSyncData()) {
      synchronizeData();
      if (!needsSyncChildren())
        return; 
    } 
    boolean bool = this.mutationEvents;
    this.mutationEvents = false;
    needsSyncChildren(false);
    getNodeType(0);
    ChildNode childNode1 = null;
    ChildNode childNode2 = null;
    int i;
    for (i = getLastChild(0); i != -1; i = getPrevSibling(i)) {
      ChildNode childNode = (ChildNode)getNodeObject(i);
      if (childNode2 == null) {
        childNode2 = childNode;
      } else {
        childNode1.previousSibling = childNode;
      } 
      childNode.ownerNode = this;
      childNode.isOwned(true);
      childNode.nextSibling = childNode1;
      childNode1 = childNode;
      short s = childNode.getNodeType();
      if (s == 1) {
        this.docElement = (ElementImpl)childNode;
      } else if (s == 10) {
        this.docType = (DocumentTypeImpl)childNode;
      } 
    } 
    if (childNode1 != null) {
      this.firstChild = childNode1;
      childNode1.isFirstChild(true);
      lastChild(childNode2);
    } 
    this.mutationEvents = bool;
  }
  
  protected final void synchronizeChildren(AttrImpl paramAttrImpl, int paramInt) {
    boolean bool = getMutationEvents();
    setMutationEvents(false);
    paramAttrImpl.needsSyncChildren(false);
    int i = getLastChild(paramInt);
    int j = getPrevSibling(i);
    if (j == -1) {
      paramAttrImpl.value = getNodeValueString(paramInt);
      paramAttrImpl.hasStringValue(true);
    } else {
      ChildNode childNode1 = null;
      ChildNode childNode2 = null;
      int k;
      for (k = i; k != -1; k = getPrevSibling(k)) {
        ChildNode childNode = (ChildNode)getNodeObject(k);
        if (childNode2 == null) {
          childNode2 = childNode;
        } else {
          childNode1.previousSibling = childNode;
        } 
        childNode.ownerNode = paramAttrImpl;
        childNode.isOwned(true);
        childNode.nextSibling = childNode1;
        childNode1 = childNode;
      } 
      if (childNode2 != null) {
        paramAttrImpl.value = childNode1;
        childNode1.isFirstChild(true);
        paramAttrImpl.lastChild(childNode2);
      } 
      paramAttrImpl.hasStringValue(false);
    } 
    setMutationEvents(bool);
  }
  
  protected final void synchronizeChildren(ParentNode paramParentNode, int paramInt) {
    boolean bool = getMutationEvents();
    setMutationEvents(false);
    paramParentNode.needsSyncChildren(false);
    ChildNode childNode1 = null;
    ChildNode childNode2 = null;
    int i;
    for (i = getLastChild(paramInt); i != -1; i = getPrevSibling(i)) {
      ChildNode childNode = (ChildNode)getNodeObject(i);
      if (childNode2 == null) {
        childNode2 = childNode;
      } else {
        childNode1.previousSibling = childNode;
      } 
      childNode.ownerNode = paramParentNode;
      childNode.isOwned(true);
      childNode.nextSibling = childNode1;
      childNode1 = childNode;
    } 
    if (childNode2 != null) {
      paramParentNode.firstChild = childNode1;
      childNode1.isFirstChild(true);
      paramParentNode.lastChild(childNode2);
    } 
    setMutationEvents(bool);
  }
  
  protected void ensureCapacity(int paramInt) {
    if (this.fNodeType == null) {
      this.fNodeType = new int[32][];
      this.fNodeName = new Object[32][];
      this.fNodeValue = new Object[32][];
      this.fNodeParent = new int[32][];
      this.fNodeLastChild = new int[32][];
      this.fNodePrevSib = new int[32][];
      this.fNodeURI = new Object[32][];
      this.fNodeExtra = new int[32][];
    } else if (this.fNodeType.length <= paramInt) {
      int i = paramInt * 2;
      int[][] arrayOfInt = new int[i][];
      System.arraycopy(this.fNodeType, 0, arrayOfInt, 0, paramInt);
      this.fNodeType = arrayOfInt;
      Object[][] arrayOfObject = new Object[i][];
      System.arraycopy(this.fNodeName, 0, arrayOfObject, 0, paramInt);
      this.fNodeName = arrayOfObject;
      arrayOfObject = new Object[i][];
      System.arraycopy(this.fNodeValue, 0, arrayOfObject, 0, paramInt);
      this.fNodeValue = arrayOfObject;
      arrayOfInt = new int[i][];
      System.arraycopy(this.fNodeParent, 0, arrayOfInt, 0, paramInt);
      this.fNodeParent = arrayOfInt;
      arrayOfInt = new int[i][];
      System.arraycopy(this.fNodeLastChild, 0, arrayOfInt, 0, paramInt);
      this.fNodeLastChild = arrayOfInt;
      arrayOfInt = new int[i][];
      System.arraycopy(this.fNodePrevSib, 0, arrayOfInt, 0, paramInt);
      this.fNodePrevSib = arrayOfInt;
      arrayOfObject = new Object[i][];
      System.arraycopy(this.fNodeURI, 0, arrayOfObject, 0, paramInt);
      this.fNodeURI = arrayOfObject;
      arrayOfInt = new int[i][];
      System.arraycopy(this.fNodeExtra, 0, arrayOfInt, 0, paramInt);
      this.fNodeExtra = arrayOfInt;
    } else if (this.fNodeType[paramInt] != null) {
      return;
    } 
    createChunk(this.fNodeType, paramInt);
    createChunk(this.fNodeName, paramInt);
    createChunk(this.fNodeValue, paramInt);
    createChunk(this.fNodeParent, paramInt);
    createChunk(this.fNodeLastChild, paramInt);
    createChunk(this.fNodePrevSib, paramInt);
    createChunk(this.fNodeURI, paramInt);
    createChunk(this.fNodeExtra, paramInt);
  }
  
  protected int createNode(short paramShort) {
    int i = this.fNodeCount >> 8;
    int j = this.fNodeCount & 0xFF;
    ensureCapacity(i);
    setChunkIndex(this.fNodeType, paramShort, i, j);
    return this.fNodeCount++;
  }
  
  protected static int binarySearch(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    while (paramInt1 <= paramInt2) {
      int i = paramInt1 + paramInt2 >>> 1;
      int j = paramArrayOfInt[i];
      if (j == paramInt3) {
        while (i > 0 && paramArrayOfInt[i - 1] == paramInt3)
          i--; 
        return i;
      } 
      if (j > paramInt3) {
        paramInt2 = i - 1;
        continue;
      } 
      paramInt1 = i + 1;
    } 
    return -1;
  }
  
  private final void createChunk(int[][] paramArrayOfInt, int paramInt) {
    paramArrayOfInt[paramInt] = new int[257];
    System.arraycopy(INIT_ARRAY, 0, paramArrayOfInt[paramInt], 0, 256);
  }
  
  private final void createChunk(Object[][] paramArrayOfObject, int paramInt) {
    paramArrayOfObject[paramInt] = new Object[257];
    paramArrayOfObject[paramInt][256] = new RefCount();
  }
  
  private final int setChunkIndex(int[][] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 == -1)
      return clearChunkIndex(paramArrayOfInt, paramInt2, paramInt3); 
    int[] arrayOfInt = paramArrayOfInt[paramInt2];
    if (arrayOfInt == null) {
      createChunk(paramArrayOfInt, paramInt2);
      arrayOfInt = paramArrayOfInt[paramInt2];
    } 
    int i = arrayOfInt[paramInt3];
    if (i == -1)
      arrayOfInt[256] = arrayOfInt[256] + 1; 
    arrayOfInt[paramInt3] = paramInt1;
    return i;
  }
  
  private final String setChunkValue(Object[][] paramArrayOfObject, Object paramObject, int paramInt1, int paramInt2) {
    if (paramObject == null)
      return clearChunkValue(paramArrayOfObject, paramInt1, paramInt2); 
    Object[] arrayOfObject = paramArrayOfObject[paramInt1];
    if (arrayOfObject == null) {
      createChunk(paramArrayOfObject, paramInt1);
      arrayOfObject = paramArrayOfObject[paramInt1];
    } 
    String str = (String)arrayOfObject[paramInt2];
    if (str == null) {
      RefCount refCount = (RefCount)arrayOfObject[256];
      refCount.fCount++;
    } 
    arrayOfObject[paramInt2] = paramObject;
    return str;
  }
  
  private final int getChunkIndex(int[][] paramArrayOfInt, int paramInt1, int paramInt2) { return (paramArrayOfInt[paramInt1] != null) ? paramArrayOfInt[paramInt1][paramInt2] : -1; }
  
  private final String getChunkValue(Object[][] paramArrayOfObject, int paramInt1, int paramInt2) { return (paramArrayOfObject[paramInt1] != null) ? (String)paramArrayOfObject[paramInt1][paramInt2] : null; }
  
  private final String getNodeValue(int paramInt1, int paramInt2) {
    Object object = this.fNodeValue[paramInt1][paramInt2];
    return (object == null) ? null : ((object instanceof String) ? (String)object : object.toString());
  }
  
  private final int clearChunkIndex(int[][] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = (paramArrayOfInt[paramInt1] != null) ? paramArrayOfInt[paramInt1][paramInt2] : -1;
    if (i != -1) {
      paramArrayOfInt[paramInt1][256] = paramArrayOfInt[paramInt1][256] - 1;
      paramArrayOfInt[paramInt1][paramInt2] = -1;
      if (paramArrayOfInt[paramInt1][256] == 0)
        paramArrayOfInt[paramInt1] = null; 
    } 
    return i;
  }
  
  private final String clearChunkValue(Object[][] paramArrayOfObject, int paramInt1, int paramInt2) {
    String str = (paramArrayOfObject[paramInt1] != null) ? (String)paramArrayOfObject[paramInt1][paramInt2] : null;
    if (str != null) {
      paramArrayOfObject[paramInt1][paramInt2] = null;
      RefCount refCount = (RefCount)paramArrayOfObject[paramInt1][256];
      refCount.fCount--;
      if (refCount.fCount == 0)
        paramArrayOfObject[paramInt1] = null; 
    } 
    return str;
  }
  
  private final void putIdentifier0(String paramString, Element paramElement) {
    if (this.identifiers == null)
      this.identifiers = new HashMap(); 
    this.identifiers.put(paramString, paramElement);
  }
  
  private static void print(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  static  {
    for (byte b = 0; b < 'Ā'; b++)
      INIT_ARRAY[b] = -1; 
  }
  
  static final class IntVector {
    private int[] data;
    
    private int size;
    
    public int size() { return this.size; }
    
    public int elementAt(int param1Int) { return this.data[param1Int]; }
    
    public void addElement(int param1Int) {
      ensureCapacity(this.size + 1);
      this.data[this.size++] = param1Int;
    }
    
    public void removeAllElements() { this.size = 0; }
    
    private void ensureCapacity(int param1Int) {
      if (this.data == null) {
        this.data = new int[param1Int + 15];
      } else if (param1Int > this.data.length) {
        int[] arrayOfInt = new int[param1Int + 15];
        System.arraycopy(this.data, 0, arrayOfInt, 0, this.data.length);
        this.data = arrayOfInt;
      } 
    }
  }
  
  static final class RefCount {
    int fCount;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredDocumentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */