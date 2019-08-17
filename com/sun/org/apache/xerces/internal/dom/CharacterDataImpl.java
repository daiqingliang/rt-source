package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class CharacterDataImpl extends ChildNode {
  static final long serialVersionUID = 7931170150428474230L;
  
  protected String data;
  
  private static NodeList singletonNodeList = new NodeList() {
      public Node item(int param1Int) { return null; }
      
      public int getLength() { return 0; }
    };
  
  public CharacterDataImpl() {}
  
  protected CharacterDataImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) {
    super(paramCoreDocumentImpl);
    this.data = paramString;
  }
  
  public NodeList getChildNodes() { return singletonNodeList; }
  
  public String getNodeValue() {
    if (needsSyncData())
      synchronizeData(); 
    return this.data;
  }
  
  protected void setNodeValueInternal(String paramString) { setNodeValueInternal(paramString, false); }
  
  protected void setNodeValueInternal(String paramString, boolean paramBoolean) {
    CoreDocumentImpl coreDocumentImpl = ownerDocument();
    if (coreDocumentImpl.errorChecking && isReadOnly()) {
      String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str1);
    } 
    if (needsSyncData())
      synchronizeData(); 
    String str = this.data;
    coreDocumentImpl.modifyingCharacterData(this, paramBoolean);
    this.data = paramString;
    coreDocumentImpl.modifiedCharacterData(this, str, paramString, paramBoolean);
  }
  
  public void setNodeValue(String paramString) {
    setNodeValueInternal(paramString);
    ownerDocument().replacedText(this);
  }
  
  public String getData() {
    if (needsSyncData())
      synchronizeData(); 
    return this.data;
  }
  
  public int getLength() {
    if (needsSyncData())
      synchronizeData(); 
    return this.data.length();
  }
  
  public void appendData(String paramString) {
    if (isReadOnly()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str);
    } 
    if (paramString == null)
      return; 
    if (needsSyncData())
      synchronizeData(); 
    setNodeValue(this.data + paramString);
  }
  
  public void deleteData(int paramInt1, int paramInt2) throws DOMException { internalDeleteData(paramInt1, paramInt2, false); }
  
  void internalDeleteData(int paramInt1, int paramInt2, boolean paramBoolean) throws DOMException {
    CoreDocumentImpl coreDocumentImpl = ownerDocument();
    if (coreDocumentImpl.errorChecking) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (paramInt2 < 0) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
        throw new DOMException((short)1, str);
      } 
    } 
    if (needsSyncData())
      synchronizeData(); 
    int i = Math.max(this.data.length() - paramInt2 - paramInt1, 0);
    try {
      String str = this.data.substring(0, paramInt1) + ((i > 0) ? this.data.substring(paramInt1 + paramInt2, paramInt1 + paramInt2 + i) : "");
      setNodeValueInternal(str, paramBoolean);
      coreDocumentImpl.deletedText(this, paramInt1, paramInt2);
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
      throw new DOMException((short)1, str);
    } 
  }
  
  public void insertData(int paramInt, String paramString) throws DOMException { internalInsertData(paramInt, paramString, false); }
  
  void internalInsertData(int paramInt, String paramString, boolean paramBoolean) throws DOMException {
    CoreDocumentImpl coreDocumentImpl = ownerDocument();
    if (coreDocumentImpl.errorChecking && isReadOnly()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str);
    } 
    if (needsSyncData())
      synchronizeData(); 
    try {
      String str = (new StringBuffer(this.data)).insert(paramInt, paramString).toString();
      setNodeValueInternal(str, paramBoolean);
      coreDocumentImpl.insertedText(this, paramInt, paramString.length());
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
      throw new DOMException((short)1, str);
    } 
  }
  
  public void replaceData(int paramInt1, int paramInt2, String paramString) throws DOMException {
    CoreDocumentImpl coreDocumentImpl = ownerDocument();
    if (coreDocumentImpl.errorChecking && isReadOnly()) {
      String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str1);
    } 
    if (needsSyncData())
      synchronizeData(); 
    coreDocumentImpl.replacingData(this);
    String str = this.data;
    internalDeleteData(paramInt1, paramInt2, true);
    internalInsertData(paramInt1, paramString, true);
    coreDocumentImpl.replacedCharacterData(this, str, this.data);
  }
  
  public void setData(String paramString) { setNodeValue(paramString); }
  
  public String substringData(int paramInt1, int paramInt2) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    int i = this.data.length();
    if (paramInt2 < 0 || paramInt1 < 0 || paramInt1 > i - 1) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
      throw new DOMException((short)1, str);
    } 
    int j = Math.min(paramInt1 + paramInt2, i);
    return this.data.substring(paramInt1, j);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\CharacterDataImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */