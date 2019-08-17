package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.BoolStack;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class DTMDefaultBase implements DTM {
  static final boolean JJK_DEBUG = false;
  
  public static final int ROOTNODE = 0;
  
  protected int m_size = 0;
  
  protected SuballocatedIntVector m_exptype;
  
  protected SuballocatedIntVector m_firstch;
  
  protected SuballocatedIntVector m_nextsib;
  
  protected SuballocatedIntVector m_prevsib;
  
  protected SuballocatedIntVector m_parent;
  
  protected Vector m_namespaceDeclSets = null;
  
  protected SuballocatedIntVector m_namespaceDeclSetElements = null;
  
  protected int[][][] m_elemIndexes;
  
  public static final int DEFAULT_BLOCKSIZE = 512;
  
  public static final int DEFAULT_NUMBLOCKS = 32;
  
  public static final int DEFAULT_NUMBLOCKS_SMALL = 4;
  
  protected static final int NOTPROCESSED = -2;
  
  public DTMManager m_mgr;
  
  protected DTMManagerDefault m_mgrDefault = null;
  
  protected SuballocatedIntVector m_dtmIdent;
  
  protected String m_documentBaseURI;
  
  protected DTMWSFilter m_wsfilter;
  
  protected boolean m_shouldStripWS = false;
  
  protected BoolStack m_shouldStripWhitespaceStack;
  
  protected XMLStringFactory m_xstrf;
  
  protected ExpandedNameTable m_expandedNameTable;
  
  protected boolean m_indexing;
  
  protected DTMAxisTraverser[] m_traversers;
  
  private Vector m_namespaceLists = null;
  
  public DTMDefaultBase(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean) { this(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean, 512, true, false); }
  
  public DTMDefaultBase(DTMManager paramDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3) {
    if (paramInt2 <= 64) {
      b = 4;
      this.m_dtmIdent = new SuballocatedIntVector(4, 1);
    } else {
      b = 32;
      this.m_dtmIdent = new SuballocatedIntVector(32);
    } 
    this.m_exptype = new SuballocatedIntVector(paramInt2, b);
    this.m_firstch = new SuballocatedIntVector(paramInt2, b);
    this.m_nextsib = new SuballocatedIntVector(paramInt2, b);
    this.m_parent = new SuballocatedIntVector(paramInt2, b);
    if (paramBoolean2)
      this.m_prevsib = new SuballocatedIntVector(paramInt2, b); 
    this.m_mgr = paramDTMManager;
    if (paramDTMManager instanceof DTMManagerDefault)
      this.m_mgrDefault = (DTMManagerDefault)paramDTMManager; 
    this.m_documentBaseURI = (null != paramSource) ? paramSource.getSystemId() : null;
    this.m_dtmIdent.setElementAt(paramInt1, 0);
    this.m_wsfilter = paramDTMWSFilter;
    this.m_xstrf = paramXMLStringFactory;
    this.m_indexing = paramBoolean1;
    if (paramBoolean1) {
      this.m_expandedNameTable = new ExpandedNameTable();
    } else {
      this.m_expandedNameTable = this.m_mgrDefault.getExpandedNameTable(this);
    } 
    if (null != paramDTMWSFilter) {
      this.m_shouldStripWhitespaceStack = new BoolStack();
      pushShouldStripWhitespace(false);
    } 
  }
  
  protected void ensureSizeOfIndex(int paramInt1, int paramInt2) {
    if (null == this.m_elemIndexes) {
      this.m_elemIndexes = new int[paramInt1 + 20][][];
    } else if (this.m_elemIndexes.length <= paramInt1) {
      int[][][] arrayOfInt2 = this.m_elemIndexes;
      this.m_elemIndexes = new int[paramInt1 + 20][][];
      System.arraycopy(arrayOfInt2, 0, this.m_elemIndexes, 0, arrayOfInt2.length);
    } 
    int[][] arrayOfInt = this.m_elemIndexes[paramInt1];
    if (null == arrayOfInt) {
      arrayOfInt = new int[paramInt2 + 100][];
      this.m_elemIndexes[paramInt1] = arrayOfInt;
    } else if (arrayOfInt.length <= paramInt2) {
      int[][] arrayOfInt2 = arrayOfInt;
      arrayOfInt = new int[paramInt2 + 100][];
      System.arraycopy(arrayOfInt2, 0, arrayOfInt, 0, arrayOfInt2.length);
      this.m_elemIndexes[paramInt1] = arrayOfInt;
    } 
    int[] arrayOfInt1 = arrayOfInt[paramInt2];
    if (null == arrayOfInt1) {
      arrayOfInt1 = new int[128];
      arrayOfInt[paramInt2] = arrayOfInt1;
      arrayOfInt1[0] = 1;
    } else if (arrayOfInt1.length <= arrayOfInt1[0] + 1) {
      int[] arrayOfInt2 = arrayOfInt1;
      arrayOfInt1 = new int[arrayOfInt1[0] + 1024];
      System.arraycopy(arrayOfInt2, 0, arrayOfInt1, 0, arrayOfInt2.length);
      arrayOfInt[paramInt2] = arrayOfInt1;
    } 
  }
  
  protected void indexNode(int paramInt1, int paramInt2) {
    ExpandedNameTable expandedNameTable = this.m_expandedNameTable;
    short s = expandedNameTable.getType(paramInt1);
    if (1 == s) {
      int i = expandedNameTable.getNamespaceID(paramInt1);
      int j = expandedNameTable.getLocalNameID(paramInt1);
      ensureSizeOfIndex(i, j);
      int[] arrayOfInt = this.m_elemIndexes[i][j];
      arrayOfInt[arrayOfInt[0]] = paramInt2;
      arrayOfInt[0] = arrayOfInt[0] + 1;
    } 
  }
  
  protected int findGTE(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt1;
    int j = paramInt1 + paramInt2 - 1;
    int k = j;
    while (i <= j) {
      int m = (i + j) / 2;
      int n = paramArrayOfInt[m];
      if (n > paramInt3) {
        j = m - 1;
        continue;
      } 
      if (n < paramInt3) {
        i = m + 1;
        continue;
      } 
      return m;
    } 
    return (i <= k && paramArrayOfInt[i] > paramInt3) ? i : -1;
  }
  
  int findElementFromIndex(int paramInt1, int paramInt2, int paramInt3) {
    int[][][] arrayOfInt = this.m_elemIndexes;
    if (null != arrayOfInt && paramInt1 < arrayOfInt.length) {
      int[][] arrayOfInt1 = arrayOfInt[paramInt1];
      if (null != arrayOfInt1 && paramInt2 < arrayOfInt1.length) {
        int[] arrayOfInt2 = arrayOfInt1[paramInt2];
        if (null != arrayOfInt2) {
          int i = findGTE(arrayOfInt2, 1, arrayOfInt2[0], paramInt3);
          if (i > -1)
            return arrayOfInt2[i]; 
        } 
      } 
    } 
    return -2;
  }
  
  protected abstract int getNextNodeIdentity(int paramInt);
  
  protected abstract boolean nextNode();
  
  protected abstract int getNumberOfNodes();
  
  protected short _type(int paramInt) {
    int i = _exptype(paramInt);
    return (-1 != i) ? this.m_expandedNameTable.getType(i) : -1;
  }
  
  protected int _exptype(int paramInt) {
    if (paramInt == -1)
      return -1; 
    while (paramInt >= this.m_size) {
      if (!nextNode() && paramInt >= this.m_size)
        return -1; 
    } 
    return this.m_exptype.elementAt(paramInt);
  }
  
  protected int _level(int paramInt) {
    while (paramInt >= this.m_size) {
      boolean bool = nextNode();
      if (!bool && paramInt >= this.m_size)
        return -1; 
    } 
    byte b;
    for (b = 0; -1 != (paramInt = _parent(paramInt)); b++);
    return b;
  }
  
  protected int _firstch(int paramInt) {
    int i = (paramInt >= this.m_size) ? -2 : this.m_firstch.elementAt(paramInt);
    while (i == -2) {
      boolean bool = nextNode();
      if (paramInt >= this.m_size && !bool)
        return -1; 
      i = this.m_firstch.elementAt(paramInt);
      if (i == -2 && !bool)
        return -1; 
    } 
    return i;
  }
  
  protected int _nextsib(int paramInt) {
    int i = (paramInt >= this.m_size) ? -2 : this.m_nextsib.elementAt(paramInt);
    while (i == -2) {
      boolean bool = nextNode();
      if (paramInt >= this.m_size && !bool)
        return -1; 
      i = this.m_nextsib.elementAt(paramInt);
      if (i == -2 && !bool)
        return -1; 
    } 
    return i;
  }
  
  protected int _prevsib(int paramInt) {
    if (paramInt < this.m_size)
      return this.m_prevsib.elementAt(paramInt); 
    do {
      boolean bool = nextNode();
      if (paramInt >= this.m_size && !bool)
        return -1; 
    } while (paramInt >= this.m_size);
    return this.m_prevsib.elementAt(paramInt);
  }
  
  protected int _parent(int paramInt) {
    if (paramInt < this.m_size)
      return this.m_parent.elementAt(paramInt); 
    do {
      boolean bool = nextNode();
      if (paramInt >= this.m_size && !bool)
        return -1; 
    } while (paramInt >= this.m_size);
    return this.m_parent.elementAt(paramInt);
  }
  
  public void dumpDTM(OutputStream paramOutputStream) {
    try {
      if (paramOutputStream == null) {
        File file = new File("DTMDump" + hashCode() + ".txt");
        System.err.println("Dumping... " + file.getAbsolutePath());
        paramOutputStream = new FileOutputStream(file);
      } 
      PrintStream printStream = new PrintStream(paramOutputStream);
      while (nextNode());
      int i = this.m_size;
      printStream.println("Total nodes: " + i);
      for (byte b = 0; b < i; b++) {
        String str;
        int j = makeNodeHandle(b);
        printStream.println("=========== index=" + b + " handle=" + j + " ===========");
        printStream.println("NodeName: " + getNodeName(j));
        printStream.println("NodeNameX: " + getNodeNameX(j));
        printStream.println("LocalName: " + getLocalName(j));
        printStream.println("NamespaceURI: " + getNamespaceURI(j));
        printStream.println("Prefix: " + getPrefix(j));
        int k = _exptype(b);
        printStream.println("Expanded Type ID: " + Integer.toHexString(k));
        short s = _type(b);
        switch (s) {
          case 2:
            str = "ATTRIBUTE_NODE";
            break;
          case 4:
            str = "CDATA_SECTION_NODE";
            break;
          case 8:
            str = "COMMENT_NODE";
            break;
          case 11:
            str = "DOCUMENT_FRAGMENT_NODE";
            break;
          case 9:
            str = "DOCUMENT_NODE";
            break;
          case 10:
            str = "DOCUMENT_NODE";
            break;
          case 1:
            str = "ELEMENT_NODE";
            break;
          case 6:
            str = "ENTITY_NODE";
            break;
          case 5:
            str = "ENTITY_REFERENCE_NODE";
            break;
          case 13:
            str = "NAMESPACE_NODE";
            break;
          case 12:
            str = "NOTATION_NODE";
            break;
          case -1:
            str = "NULL";
            break;
          case 7:
            str = "PROCESSING_INSTRUCTION_NODE";
            break;
          case 3:
            str = "TEXT_NODE";
            break;
          default:
            str = "Unknown!";
            break;
        } 
        printStream.println("Type: " + str);
        int m = _firstch(b);
        if (-1 == m) {
          printStream.println("First child: DTM.NULL");
        } else if (-2 == m) {
          printStream.println("First child: NOTPROCESSED");
        } else {
          printStream.println("First child: " + m);
        } 
        if (this.m_prevsib != null) {
          int i3 = _prevsib(b);
          if (-1 == i3) {
            printStream.println("Prev sibling: DTM.NULL");
          } else if (-2 == i3) {
            printStream.println("Prev sibling: NOTPROCESSED");
          } else {
            printStream.println("Prev sibling: " + i3);
          } 
        } 
        int n = _nextsib(b);
        if (-1 == n) {
          printStream.println("Next sibling: DTM.NULL");
        } else if (-2 == n) {
          printStream.println("Next sibling: NOTPROCESSED");
        } else {
          printStream.println("Next sibling: " + n);
        } 
        int i1 = _parent(b);
        if (-1 == i1) {
          printStream.println("Parent: DTM.NULL");
        } else if (-2 == i1) {
          printStream.println("Parent: NOTPROCESSED");
        } else {
          printStream.println("Parent: " + i1);
        } 
        int i2 = _level(b);
        printStream.println("Level: " + i2);
        printStream.println("Node Value: " + getNodeValue(j));
        printStream.println("String Value: " + getStringValue(j));
      } 
    } catch (IOException iOException) {
      iOException.printStackTrace(System.err);
      throw new RuntimeException(iOException.getMessage());
    } 
  }
  
  public String dumpNode(int paramInt) {
    if (paramInt == -1)
      return "[null]"; 
    switch (getNodeType(paramInt)) {
      case 2:
        str = "ATTR";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 4:
        str = "CDATA";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 8:
        str = "COMMENT";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 11:
        str = "DOC_FRAG";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 9:
        str = "DOC";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 10:
        str = "DOC_TYPE";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 1:
        str = "ELEMENT";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 6:
        str = "ENTITY";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 5:
        str = "ENT_REF";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 13:
        str = "NAMESPACE";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 12:
        str = "NOTATION";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case -1:
        str = "null";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 7:
        str = "PI";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
      case 3:
        str = "TEXT";
        return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
    } 
    String str = "Unknown!";
    return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
  }
  
  public void setFeature(String paramString, boolean paramBoolean) {}
  
  public boolean hasChildNodes(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    int j = _firstch(i);
    return (j != -1);
  }
  
  public final int makeNodeHandle(int paramInt) { return (-1 == paramInt) ? -1 : (this.m_dtmIdent.elementAt(paramInt >>> 16) + (paramInt & 0xFFFF)); }
  
  public final int makeNodeIdentity(int paramInt) {
    if (-1 == paramInt)
      return -1; 
    if (this.m_mgrDefault != null) {
      int j = paramInt >>> 16;
      return (this.m_mgrDefault.m_dtms[j] != this) ? -1 : (this.m_mgrDefault.m_dtm_offsets[j] | paramInt & 0xFFFF);
    } 
    int i = this.m_dtmIdent.indexOf(paramInt & 0xFFFF0000);
    return (i == -1) ? -1 : ((i << 16) + (paramInt & 0xFFFF));
  }
  
  public int getFirstChild(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    int j = _firstch(i);
    return makeNodeHandle(j);
  }
  
  public int getTypedFirstChild(int paramInt1, int paramInt2) {
    if (paramInt2 < 14) {
      for (int i = _firstch(makeNodeIdentity(paramInt1)); i != -1; i = _nextsib(i)) {
        int j = _exptype(i);
        if (j == paramInt2 || (j >= 14 && this.m_expandedNameTable.getType(j) == paramInt2))
          return makeNodeHandle(i); 
      } 
    } else {
      for (int i = _firstch(makeNodeIdentity(paramInt1)); i != -1; i = _nextsib(i)) {
        if (_exptype(i) == paramInt2)
          return makeNodeHandle(i); 
      } 
    } 
    return -1;
  }
  
  public int getLastChild(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    int j = _firstch(i);
    int k = -1;
    while (j != -1) {
      k = j;
      j = _nextsib(j);
    } 
    return makeNodeHandle(k);
  }
  
  public abstract int getAttributeNode(int paramInt, String paramString1, String paramString2);
  
  public int getFirstAttribute(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    return makeNodeHandle(getFirstAttributeIdentity(i));
  }
  
  protected int getFirstAttributeIdentity(int paramInt) {
    short s = _type(paramInt);
    if (1 == s)
      while (-1 != (paramInt = getNextNodeIdentity(paramInt))) {
        s = _type(paramInt);
        if (s == 2)
          return paramInt; 
        if (13 != s)
          break; 
      }  
    return -1;
  }
  
  protected int getTypedAttribute(int paramInt1, int paramInt2) {
    short s = getNodeType(paramInt1);
    if (1 == s) {
      int i = makeNodeIdentity(paramInt1);
      while (-1 != (i = getNextNodeIdentity(i))) {
        s = _type(i);
        if (s == 2) {
          if (_exptype(i) == paramInt2)
            return makeNodeHandle(i); 
          continue;
        } 
        if (13 != s)
          break; 
      } 
    } 
    return -1;
  }
  
  public int getNextSibling(int paramInt) { return (paramInt == -1) ? -1 : makeNodeHandle(_nextsib(makeNodeIdentity(paramInt))); }
  
  public int getTypedNextSibling(int paramInt1, int paramInt2) {
    if (paramInt1 == -1)
      return -1; 
    int i = makeNodeIdentity(paramInt1);
    int j;
    while ((i = _nextsib(i)) != -1 && (j = _exptype(i)) != paramInt2 && this.m_expandedNameTable.getType(j) != paramInt2);
    return (i == -1) ? -1 : makeNodeHandle(i);
  }
  
  public int getPreviousSibling(int paramInt) {
    if (paramInt == -1)
      return -1; 
    if (this.m_prevsib != null)
      return makeNodeHandle(_prevsib(makeNodeIdentity(paramInt))); 
    int i = makeNodeIdentity(paramInt);
    int j = _parent(i);
    int k = _firstch(j);
    int m = -1;
    while (k != i) {
      m = k;
      k = _nextsib(k);
    } 
    return makeNodeHandle(m);
  }
  
  public int getNextAttribute(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    return (_type(i) == 2) ? makeNodeHandle(getNextAttributeIdentity(i)) : -1;
  }
  
  protected int getNextAttributeIdentity(int paramInt) {
    while (-1 != (paramInt = getNextNodeIdentity(paramInt))) {
      short s = _type(paramInt);
      if (s == 2)
        return paramInt; 
      if (s != 13)
        break; 
    } 
    return -1;
  }
  
  protected void declareNamespaceInContext(int paramInt1, int paramInt2) {
    SuballocatedIntVector suballocatedIntVector = null;
    if (this.m_namespaceDeclSets == null) {
      this.m_namespaceDeclSetElements = new SuballocatedIntVector(32);
      this.m_namespaceDeclSetElements.addElement(paramInt1);
      this.m_namespaceDeclSets = new Vector();
      suballocatedIntVector = new SuballocatedIntVector(32);
      this.m_namespaceDeclSets.addElement(suballocatedIntVector);
    } else {
      int k = this.m_namespaceDeclSetElements.size() - 1;
      if (k >= 0 && paramInt1 == this.m_namespaceDeclSetElements.elementAt(k))
        suballocatedIntVector = (SuballocatedIntVector)this.m_namespaceDeclSets.elementAt(k); 
    } 
    if (suballocatedIntVector == null) {
      this.m_namespaceDeclSetElements.addElement(paramInt1);
      SuballocatedIntVector suballocatedIntVector1 = findNamespaceContext(_parent(paramInt1));
      if (suballocatedIntVector1 != null) {
        int k = suballocatedIntVector1.size();
        suballocatedIntVector = new SuballocatedIntVector(Math.max(Math.min(k + 16, 2048), 32));
        for (byte b = 0; b < k; b++)
          suballocatedIntVector.addElement(suballocatedIntVector1.elementAt(b)); 
      } else {
        suballocatedIntVector = new SuballocatedIntVector(32);
      } 
      this.m_namespaceDeclSets.addElement(suballocatedIntVector);
    } 
    int i = _exptype(paramInt2);
    for (int j = suballocatedIntVector.size() - 1; j >= 0; j--) {
      if (i == getExpandedTypeID(suballocatedIntVector.elementAt(j))) {
        suballocatedIntVector.setElementAt(makeNodeHandle(paramInt2), j);
        return;
      } 
    } 
    suballocatedIntVector.addElement(makeNodeHandle(paramInt2));
  }
  
  protected SuballocatedIntVector findNamespaceContext(int paramInt) {
    if (null != this.m_namespaceDeclSetElements) {
      int i = findInSortedSuballocatedIntVector(this.m_namespaceDeclSetElements, paramInt);
      if (i >= 0)
        return (SuballocatedIntVector)this.m_namespaceDeclSets.elementAt(i); 
      if (i == -1)
        return null; 
      i = -1 - i;
      int j = this.m_namespaceDeclSetElements.elementAt(--i);
      int k = _parent(paramInt);
      if (i == 0 && j < k) {
        int i1;
        int m = getDocumentRoot(makeNodeHandle(paramInt));
        int n = makeNodeIdentity(m);
        if (getNodeType(m) == 9) {
          int i2 = _firstch(n);
          i1 = (i2 != -1) ? i2 : n;
        } else {
          i1 = n;
        } 
        if (j == i1)
          return (SuballocatedIntVector)this.m_namespaceDeclSets.elementAt(i); 
      } 
      label35: while (i >= 0 && k > 0) {
        if (j == k)
          return (SuballocatedIntVector)this.m_namespaceDeclSets.elementAt(i); 
        if (j < k)
          while (true) {
            k = _parent(k);
            if (j >= k)
              continue label35; 
          }  
        if (i > 0)
          j = this.m_namespaceDeclSetElements.elementAt(--i); 
      } 
    } 
    return null;
  }
  
  protected int findInSortedSuballocatedIntVector(SuballocatedIntVector paramSuballocatedIntVector, int paramInt) {
    int i = 0;
    if (paramSuballocatedIntVector != null) {
      int j = 0;
      int k = paramSuballocatedIntVector.size() - 1;
      while (j <= k) {
        i = (j + k) / 2;
        int m = paramInt - paramSuballocatedIntVector.elementAt(i);
        if (m == 0)
          return i; 
        if (m < 0) {
          k = i - 1;
          continue;
        } 
        j = i + 1;
      } 
      if (j > i)
        i = j; 
    } 
    return -1 - i;
  }
  
  public int getFirstNamespaceNode(int paramInt, boolean paramBoolean) {
    if (paramBoolean) {
      int j = makeNodeIdentity(paramInt);
      if (_type(j) == 1) {
        SuballocatedIntVector suballocatedIntVector = findNamespaceContext(j);
        return (suballocatedIntVector == null || suballocatedIntVector.size() < 1) ? -1 : suballocatedIntVector.elementAt(0);
      } 
      return -1;
    } 
    int i = makeNodeIdentity(paramInt);
    if (_type(i) == 1) {
      while (-1 != (i = getNextNodeIdentity(i))) {
        short s = _type(i);
        if (s == 13)
          return makeNodeHandle(i); 
        if (2 != s)
          break; 
      } 
      return -1;
    } 
    return -1;
  }
  
  public int getNextNamespaceNode(int paramInt1, int paramInt2, boolean paramBoolean) {
    if (paramBoolean) {
      SuballocatedIntVector suballocatedIntVector = findNamespaceContext(makeNodeIdentity(paramInt1));
      if (suballocatedIntVector == null)
        return -1; 
      int j = 1 + suballocatedIntVector.indexOf(paramInt2);
      return (j <= 0 || j == suballocatedIntVector.size()) ? -1 : suballocatedIntVector.elementAt(j);
    } 
    int i = makeNodeIdentity(paramInt2);
    while (-1 != (i = getNextNodeIdentity(i))) {
      short s = _type(i);
      if (s == 13)
        return makeNodeHandle(i); 
      if (s != 2)
        break; 
    } 
    return -1;
  }
  
  public int getParent(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    return (i > 0) ? makeNodeHandle(_parent(i)) : -1;
  }
  
  public int getDocument() { return this.m_dtmIdent.elementAt(0); }
  
  public int getOwnerDocument(int paramInt) { return (9 == getNodeType(paramInt)) ? -1 : getDocumentRoot(paramInt); }
  
  public int getDocumentRoot(int paramInt) { return getManager().getDTM(paramInt).getDocument(); }
  
  public abstract XMLString getStringValue(int paramInt);
  
  public int getStringValueChunkCount(int paramInt) {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    return 0;
  }
  
  public char[] getStringValueChunk(int paramInt1, int paramInt2, int[] paramArrayOfInt) {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    return null;
  }
  
  public int getExpandedTypeID(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    return (i == -1) ? -1 : _exptype(i);
  }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt) {
    ExpandedNameTable expandedNameTable = this.m_expandedNameTable;
    return expandedNameTable.getExpandedTypeID(paramString1, paramString2, paramInt);
  }
  
  public String getLocalNameFromExpandedNameID(int paramInt) { return this.m_expandedNameTable.getLocalName(paramInt); }
  
  public String getNamespaceFromExpandedNameID(int paramInt) { return this.m_expandedNameTable.getNamespace(paramInt); }
  
  public int getNamespaceType(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    int j = _exptype(i);
    return this.m_expandedNameTable.getNamespaceID(j);
  }
  
  public abstract String getNodeName(int paramInt);
  
  public String getNodeNameX(int paramInt) {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    return null;
  }
  
  public abstract String getLocalName(int paramInt);
  
  public abstract String getPrefix(int paramInt);
  
  public abstract String getNamespaceURI(int paramInt);
  
  public abstract String getNodeValue(int paramInt);
  
  public short getNodeType(int paramInt) { return (paramInt == -1) ? -1 : this.m_expandedNameTable.getType(_exptype(makeNodeIdentity(paramInt))); }
  
  public short getLevel(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    return (short)(_level(i) + 1);
  }
  
  public int getNodeIdent(int paramInt) { return makeNodeIdentity(paramInt); }
  
  public int getNodeHandle(int paramInt) { return makeNodeHandle(paramInt); }
  
  public boolean isSupported(String paramString1, String paramString2) { return false; }
  
  public String getDocumentBaseURI() { return this.m_documentBaseURI; }
  
  public void setDocumentBaseURI(String paramString) { this.m_documentBaseURI = paramString; }
  
  public String getDocumentSystemIdentifier(int paramInt) { return this.m_documentBaseURI; }
  
  public String getDocumentEncoding(int paramInt) { return "UTF-8"; }
  
  public String getDocumentStandalone(int paramInt) { return null; }
  
  public String getDocumentVersion(int paramInt) { return null; }
  
  public boolean getDocumentAllDeclarationsProcessed() { return true; }
  
  public abstract String getDocumentTypeDeclarationSystemIdentifier();
  
  public abstract String getDocumentTypeDeclarationPublicIdentifier();
  
  public abstract int getElementById(String paramString);
  
  public abstract String getUnparsedEntityURI(String paramString);
  
  public boolean supportsPreStripping() { return true; }
  
  public boolean isNodeAfter(int paramInt1, int paramInt2) {
    int i = makeNodeIdentity(paramInt1);
    int j = makeNodeIdentity(paramInt2);
    return (i != -1 && j != -1 && i <= j);
  }
  
  public boolean isCharacterElementContentWhitespace(int paramInt) { return false; }
  
  public boolean isDocumentAllDeclarationsProcessed(int paramInt) { return true; }
  
  public abstract boolean isAttributeSpecified(int paramInt);
  
  public abstract void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean) throws SAXException;
  
  public abstract void dispatchToEvents(int paramInt, ContentHandler paramContentHandler) throws SAXException;
  
  public Node getNode(int paramInt) { return new DTMNodeProxy(this, paramInt); }
  
  public void appendChild(int paramInt, boolean paramBoolean1, boolean paramBoolean2) { error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null)); }
  
  public void appendTextChild(String paramString) { error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null)); }
  
  protected void error(String paramString) { throw new DTMException(paramString); }
  
  protected boolean getShouldStripWhitespace() { return this.m_shouldStripWS; }
  
  protected void pushShouldStripWhitespace(boolean paramBoolean) {
    this.m_shouldStripWS = paramBoolean;
    if (null != this.m_shouldStripWhitespaceStack)
      this.m_shouldStripWhitespaceStack.push(paramBoolean); 
  }
  
  protected void popShouldStripWhitespace() {
    if (null != this.m_shouldStripWhitespaceStack)
      this.m_shouldStripWS = this.m_shouldStripWhitespaceStack.popAndTop(); 
  }
  
  protected void setShouldStripWhitespace(boolean paramBoolean) {
    this.m_shouldStripWS = paramBoolean;
    if (null != this.m_shouldStripWhitespaceStack)
      this.m_shouldStripWhitespaceStack.setTop(paramBoolean); 
  }
  
  public void documentRegistration() {}
  
  public void documentRelease() {}
  
  public void migrateTo(DTMManager paramDTMManager) {
    this.m_mgr = paramDTMManager;
    if (paramDTMManager instanceof DTMManagerDefault)
      this.m_mgrDefault = (DTMManagerDefault)paramDTMManager; 
  }
  
  public DTMManager getManager() { return this.m_mgr; }
  
  public SuballocatedIntVector getDTMIDs() { return (this.m_mgr == null) ? null : this.m_dtmIdent; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMDefaultBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */