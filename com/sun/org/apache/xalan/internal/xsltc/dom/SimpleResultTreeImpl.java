package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.serializer.EmptySerializer;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringDefault;
import java.util.Map;
import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class SimpleResultTreeImpl extends EmptySerializer implements DOM, DTM {
  private static final DTMAxisIterator EMPTY_ITERATOR = new DTMAxisIteratorBase() {
      public DTMAxisIterator reset() { return this; }
      
      public DTMAxisIterator setStartNode(int param1Int) { return this; }
      
      public int next() { return -1; }
      
      public void setMark() throws SAXException {}
      
      public void gotoMark() throws SAXException {}
      
      public int getLast() { return 0; }
      
      public int getPosition() { return 0; }
      
      public DTMAxisIterator cloneIterator() { return this; }
      
      public void setRestartable(boolean param1Boolean) {}
    };
  
  public static final int RTF_ROOT = 0;
  
  public static final int RTF_TEXT = 1;
  
  public static final int NUMBER_OF_NODES = 2;
  
  private static int _documentURIIndex = 0;
  
  private static final String EMPTY_STR = "";
  
  private String _text;
  
  protected String[] _textArray;
  
  protected XSLTCDTMManager _dtmManager;
  
  protected int _size = 0;
  
  private int _documentID;
  
  private BitArray _dontEscape = null;
  
  private boolean _escaping = true;
  
  public SimpleResultTreeImpl(XSLTCDTMManager paramXSLTCDTMManager, int paramInt) {
    this._dtmManager = paramXSLTCDTMManager;
    this._documentID = paramInt;
    this._textArray = new String[4];
  }
  
  public DTMManagerDefault getDTMManager() { return this._dtmManager; }
  
  public int getDocument() { return this._documentID; }
  
  public String getStringValue() { return this._text; }
  
  public DTMAxisIterator getIterator() { return new SingletonIterator(getDocument()); }
  
  public DTMAxisIterator getChildren(int paramInt) { return (new SimpleIterator()).setStartNode(paramInt); }
  
  public DTMAxisIterator getTypedChildren(int paramInt) { return new SimpleIterator(1, paramInt); }
  
  public DTMAxisIterator getAxisIterator(int paramInt) {
    switch (paramInt) {
      case 3:
      case 4:
        return new SimpleIterator(1);
      case 0:
      case 10:
        return new SimpleIterator(0);
      case 1:
        return (new SimpleIterator(0)).includeSelf();
      case 5:
        return (new SimpleIterator(1)).includeSelf();
      case 13:
        return new SingletonIterator();
    } 
    return EMPTY_ITERATOR;
  }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2) {
    switch (paramInt1) {
      case 3:
      case 4:
        return new SimpleIterator(1, paramInt2);
      case 0:
      case 10:
        return new SimpleIterator(0, paramInt2);
      case 1:
        return (new SimpleIterator(0, paramInt2)).includeSelf();
      case 5:
        return (new SimpleIterator(1, paramInt2)).includeSelf();
      case 13:
        return new SingletonIterator(paramInt2);
    } 
    return EMPTY_ITERATOR;
  }
  
  public DTMAxisIterator getNthDescendant(int paramInt1, int paramInt2, boolean paramBoolean) { return null; }
  
  public DTMAxisIterator getNamespaceAxisIterator(int paramInt1, int paramInt2) { return null; }
  
  public DTMAxisIterator getNodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean) { return null; }
  
  public DTMAxisIterator orderNodes(DTMAxisIterator paramDTMAxisIterator, int paramInt) { return paramDTMAxisIterator; }
  
  public String getNodeName(int paramInt) { return (getNodeIdent(paramInt) == 1) ? "#text" : ""; }
  
  public String getNodeNameX(int paramInt) { return ""; }
  
  public String getNamespaceName(int paramInt) { return ""; }
  
  public int getExpandedTypeID(int paramInt) {
    int i = getNodeIdent(paramInt);
    return (i == 1) ? 3 : ((i == 0) ? 0 : -1);
  }
  
  public int getNamespaceType(int paramInt) { return 0; }
  
  public int getParent(int paramInt) {
    int i = getNodeIdent(paramInt);
    return (i == 1) ? getNodeHandle(0) : -1;
  }
  
  public int getAttributeNode(int paramInt1, int paramInt2) { return -1; }
  
  public String getStringValueX(int paramInt) {
    int i = getNodeIdent(paramInt);
    return (i == 0 || i == 1) ? this._text : "";
  }
  
  public void copy(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException { characters(paramInt, paramSerializationHandler); }
  
  public void copy(DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler) throws TransletException {
    int i;
    while ((i = paramDTMAxisIterator.next()) != -1)
      copy(i, paramSerializationHandler); 
  }
  
  public String shallowCopy(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException {
    characters(paramInt, paramSerializationHandler);
    return null;
  }
  
  public boolean lessThan(int paramInt1, int paramInt2) { return (paramInt1 == -1) ? false : ((paramInt2 == -1) ? true : ((paramInt1 < paramInt2))); }
  
  public void characters(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException {
    int i = getNodeIdent(paramInt);
    if (i == 0 || i == 1) {
      boolean bool1 = false;
      boolean bool2 = false;
      try {
        for (byte b = 0; b < this._size; b++) {
          if (this._dontEscape != null) {
            bool1 = this._dontEscape.getBit(b);
            if (bool1)
              bool2 = paramSerializationHandler.setEscaping(false); 
          } 
          paramSerializationHandler.characters(this._textArray[b]);
          if (bool1)
            paramSerializationHandler.setEscaping(bool2); 
        } 
      } catch (SAXException sAXException) {
        throw new TransletException(sAXException);
      } 
    } 
  }
  
  public Node makeNode(int paramInt) { return null; }
  
  public Node makeNode(DTMAxisIterator paramDTMAxisIterator) { return null; }
  
  public NodeList makeNodeList(int paramInt) { return null; }
  
  public NodeList makeNodeList(DTMAxisIterator paramDTMAxisIterator) { return null; }
  
  public String getLanguage(int paramInt) { return null; }
  
  public int getSize() { return 2; }
  
  public String getDocumentURI(int paramInt) { return "simple_rtf" + _documentURIIndex++; }
  
  public void setFilter(StripFilter paramStripFilter) {}
  
  public void setupMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3) {}
  
  public boolean isElement(int paramInt) { return false; }
  
  public boolean isAttribute(int paramInt) { return false; }
  
  public String lookupNamespace(int paramInt, String paramString) throws TransletException { return null; }
  
  public int getNodeIdent(int paramInt) { return (paramInt != -1) ? (paramInt - this._documentID) : -1; }
  
  public int getNodeHandle(int paramInt) { return (paramInt != -1) ? (paramInt + this._documentID) : -1; }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2) { return null; }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2, boolean paramBoolean) { return null; }
  
  public SerializationHandler getOutputDomBuilder() { return this; }
  
  public int getNSType(int paramInt) { return 0; }
  
  public String getUnparsedEntityURI(String paramString) { return null; }
  
  public Map<String, Integer> getElementsWithIDs() { return null; }
  
  public void startDocument() throws SAXException {}
  
  public void endDocument() throws SAXException {
    if (this._size == 1) {
      this._text = this._textArray[0];
    } else {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < this._size; b++)
        stringBuffer.append(this._textArray[b]); 
      this._text = stringBuffer.toString();
    } 
  }
  
  public void characters(String paramString) throws SAXException {
    if (this._size >= this._textArray.length) {
      String[] arrayOfString = new String[this._textArray.length * 2];
      System.arraycopy(this._textArray, 0, arrayOfString, 0, this._textArray.length);
      this._textArray = arrayOfString;
    } 
    if (!this._escaping) {
      if (this._dontEscape == null)
        this._dontEscape = new BitArray(8); 
      if (this._size >= this._dontEscape.size())
        this._dontEscape.resize(this._dontEscape.size() * 2); 
      this._dontEscape.setBit(this._size);
    } 
    this._textArray[this._size++] = paramString;
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this._size >= this._textArray.length) {
      String[] arrayOfString = new String[this._textArray.length * 2];
      System.arraycopy(this._textArray, 0, arrayOfString, 0, this._textArray.length);
      this._textArray = arrayOfString;
    } 
    if (!this._escaping) {
      if (this._dontEscape == null)
        this._dontEscape = new BitArray(8); 
      if (this._size >= this._dontEscape.size())
        this._dontEscape.resize(this._dontEscape.size() * 2); 
      this._dontEscape.setBit(this._size);
    } 
    this._textArray[this._size++] = new String(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public boolean setEscaping(boolean paramBoolean) throws SAXException {
    boolean bool = this._escaping;
    this._escaping = paramBoolean;
    return bool;
  }
  
  public void setFeature(String paramString, boolean paramBoolean) {}
  
  public void setProperty(String paramString, Object paramObject) {}
  
  public DTMAxisTraverser getAxisTraverser(int paramInt) { return null; }
  
  public boolean hasChildNodes(int paramInt) { return (getNodeIdent(paramInt) == 0); }
  
  public int getFirstChild(int paramInt) {
    int i = getNodeIdent(paramInt);
    return (i == 0) ? getNodeHandle(1) : -1;
  }
  
  public int getLastChild(int paramInt) { return getFirstChild(paramInt); }
  
  public int getAttributeNode(int paramInt, String paramString1, String paramString2) { return -1; }
  
  public int getFirstAttribute(int paramInt) { return -1; }
  
  public int getFirstNamespaceNode(int paramInt, boolean paramBoolean) { return -1; }
  
  public int getNextSibling(int paramInt) { return -1; }
  
  public int getPreviousSibling(int paramInt) { return -1; }
  
  public int getNextAttribute(int paramInt) { return -1; }
  
  public int getNextNamespaceNode(int paramInt1, int paramInt2, boolean paramBoolean) { return -1; }
  
  public int getOwnerDocument(int paramInt) { return getDocument(); }
  
  public int getDocumentRoot(int paramInt) { return getDocument(); }
  
  public XMLString getStringValue(int paramInt) { return new XMLStringDefault(getStringValueX(paramInt)); }
  
  public int getStringValueChunkCount(int paramInt) { return 0; }
  
  public char[] getStringValueChunk(int paramInt1, int paramInt2, int[] paramArrayOfInt) { return null; }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt) { return -1; }
  
  public String getLocalNameFromExpandedNameID(int paramInt) { return ""; }
  
  public String getNamespaceFromExpandedNameID(int paramInt) { return ""; }
  
  public String getLocalName(int paramInt) { return ""; }
  
  public String getPrefix(int paramInt) { return null; }
  
  public String getNamespaceURI(int paramInt) { return ""; }
  
  public String getNodeValue(int paramInt) { return (getNodeIdent(paramInt) == 1) ? this._text : null; }
  
  public short getNodeType(int paramInt) {
    int i = getNodeIdent(paramInt);
    return (i == 1) ? 3 : ((i == 0) ? 0 : -1);
  }
  
  public short getLevel(int paramInt) {
    int i = getNodeIdent(paramInt);
    return (i == 1) ? 2 : ((i == 0) ? 1 : -1);
  }
  
  public boolean isSupported(String paramString1, String paramString2) { return false; }
  
  public String getDocumentBaseURI() { return ""; }
  
  public void setDocumentBaseURI(String paramString) throws SAXException {}
  
  public String getDocumentSystemIdentifier(int paramInt) { return null; }
  
  public String getDocumentEncoding(int paramInt) { return null; }
  
  public String getDocumentStandalone(int paramInt) { return null; }
  
  public String getDocumentVersion(int paramInt) { return null; }
  
  public boolean getDocumentAllDeclarationsProcessed() { return false; }
  
  public String getDocumentTypeDeclarationSystemIdentifier() { return null; }
  
  public String getDocumentTypeDeclarationPublicIdentifier() { return null; }
  
  public int getElementById(String paramString) { return -1; }
  
  public boolean supportsPreStripping() { return false; }
  
  public boolean isNodeAfter(int paramInt1, int paramInt2) { return lessThan(paramInt1, paramInt2); }
  
  public boolean isCharacterElementContentWhitespace(int paramInt) { return false; }
  
  public boolean isDocumentAllDeclarationsProcessed(int paramInt) { return false; }
  
  public boolean isAttributeSpecified(int paramInt) { return false; }
  
  public void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean) throws SAXException {}
  
  public void dispatchToEvents(int paramInt, ContentHandler paramContentHandler) throws SAXException {}
  
  public Node getNode(int paramInt) { return makeNode(paramInt); }
  
  public boolean needsTwoThreads() { return false; }
  
  public ContentHandler getContentHandler() { return null; }
  
  public LexicalHandler getLexicalHandler() { return null; }
  
  public EntityResolver getEntityResolver() { return null; }
  
  public DTDHandler getDTDHandler() { return null; }
  
  public ErrorHandler getErrorHandler() { return null; }
  
  public DeclHandler getDeclHandler() { return null; }
  
  public void appendChild(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {}
  
  public void appendTextChild(String paramString) throws SAXException {}
  
  public SourceLocator getSourceLocatorFor(int paramInt) { return null; }
  
  public void documentRegistration() throws SAXException {}
  
  public void documentRelease() throws SAXException {}
  
  public void migrateTo(DTMManager paramDTMManager) {}
  
  public void release() throws SAXException {
    if (this._documentID != 0) {
      this._dtmManager.release(this, true);
      this._documentID = 0;
    } 
  }
  
  public final class SimpleIterator extends DTMAxisIteratorBase {
    static final int DIRECTION_UP = 0;
    
    static final int DIRECTION_DOWN = 1;
    
    static final int NO_TYPE = -1;
    
    int _direction = 1;
    
    int _type = -1;
    
    int _currentNode;
    
    public SimpleIterator() {}
    
    public SimpleIterator(int param1Int) { this._direction = param1Int; }
    
    public SimpleIterator(int param1Int1, int param1Int2) {
      this._direction = param1Int1;
      this._type = param1Int2;
    }
    
    public int next() {
      if (this._direction == 1) {
        while (this._currentNode < 2) {
          if (this._type != -1) {
            if ((this._currentNode == 0 && this._type == 0) || (this._currentNode == 1 && this._type == 3))
              return returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode++)); 
            this._currentNode++;
            continue;
          } 
          return returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode++));
        } 
        return -1;
      } 
      while (this._currentNode >= 0) {
        if (this._type != -1) {
          if ((this._currentNode == 0 && this._type == 0) || (this._currentNode == 1 && this._type == 3))
            return returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode--)); 
          this._currentNode--;
          continue;
        } 
        return returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode--));
      } 
      return -1;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      int i = SimpleResultTreeImpl.this.getNodeIdent(param1Int);
      this._startNode = i;
      if (!this._includeSelf && i != -1)
        if (this._direction == 1) {
          i++;
        } else if (this._direction == 0) {
          i--;
        }  
      this._currentNode = i;
      return this;
    }
    
    public void setMark() throws SAXException { this._markedNode = this._currentNode; }
    
    public void gotoMark() throws SAXException { this._currentNode = this._markedNode; }
  }
  
  public final class SingletonIterator extends DTMAxisIteratorBase {
    static final int NO_TYPE = -1;
    
    int _type = -1;
    
    int _currentNode;
    
    public SingletonIterator() {}
    
    public SingletonIterator(int param1Int) { this._type = param1Int; }
    
    public void setMark() throws SAXException { this._markedNode = this._currentNode; }
    
    public void gotoMark() throws SAXException { this._currentNode = this._markedNode; }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      this._currentNode = this._startNode = SimpleResultTreeImpl.this.getNodeIdent(param1Int);
      return this;
    }
    
    public int next() {
      if (this._currentNode == -1)
        return -1; 
      this._currentNode = -1;
      if (this._type != -1) {
        if ((this._currentNode == 0 && this._type == 0) || (this._currentNode == 1 && this._type == 3))
          return SimpleResultTreeImpl.this.getNodeHandle(this._currentNode); 
      } else {
        return SimpleResultTreeImpl.this.getNodeHandle(this._currentNode);
      } 
      return -1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\SimpleResultTreeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */