package com.sun.xml.internal.txw2;

class StartTag extends Content implements NamespaceResolver {
  private String uri;
  
  private final String localName;
  
  private Attribute firstAtt;
  
  private Attribute lastAtt;
  
  private ContainerElement owner;
  
  private NamespaceDecl firstNs;
  
  private NamespaceDecl lastNs;
  
  final Document document;
  
  public StartTag(ContainerElement paramContainerElement, String paramString1, String paramString2) {
    this(paramContainerElement.document, paramString1, paramString2);
    this.owner = paramContainerElement;
  }
  
  public StartTag(Document paramDocument, String paramString1, String paramString2) {
    assert paramString1 != null;
    assert paramString2 != null;
    this.uri = paramString1;
    this.localName = paramString2;
    this.document = paramDocument;
    addNamespaceDecl(paramString1, null, false);
  }
  
  public void addAttribute(String paramString1, String paramString2, Object paramObject) {
    checkWritable();
    Attribute attribute;
    for (attribute = this.firstAtt; attribute != null && !attribute.hasName(paramString1, paramString2); attribute = attribute.next);
    if (attribute == null) {
      attribute = new Attribute(paramString1, paramString2);
      if (this.lastAtt == null) {
        assert this.firstAtt == null;
        this.firstAtt = this.lastAtt = attribute;
      } else {
        assert this.firstAtt != null;
        this.lastAtt.next = attribute;
        this.lastAtt = attribute;
      } 
      if (paramString1.length() > 0)
        addNamespaceDecl(paramString1, null, true); 
    } 
    this.document.writeValue(paramObject, this, attribute.value);
  }
  
  public NamespaceDecl addNamespaceDecl(String paramString1, String paramString2, boolean paramBoolean) {
    checkWritable();
    if (paramString1 == null)
      throw new IllegalArgumentException(); 
    if (paramString1.length() == 0) {
      if (paramBoolean)
        throw new IllegalArgumentException("The empty namespace cannot have a non-empty prefix"); 
      if (paramString2 != null && paramString2.length() > 0)
        throw new IllegalArgumentException("The empty namespace can be only bound to the empty prefix"); 
      paramString2 = "";
    } 
    NamespaceDecl namespaceDecl;
    for (namespaceDecl = this.firstNs; namespaceDecl != null; namespaceDecl = namespaceDecl.next) {
      if (paramString1.equals(namespaceDecl.uri)) {
        if (paramString2 == null) {
          namespaceDecl.requirePrefix |= paramBoolean;
          return namespaceDecl;
        } 
        if (namespaceDecl.prefix == null) {
          namespaceDecl.prefix = paramString2;
          namespaceDecl.requirePrefix |= paramBoolean;
          return namespaceDecl;
        } 
        if (paramString2.equals(namespaceDecl.prefix)) {
          namespaceDecl.requirePrefix |= paramBoolean;
          return namespaceDecl;
        } 
      } 
      if (paramString2 != null && namespaceDecl.prefix != null && namespaceDecl.prefix.equals(paramString2))
        throw new IllegalArgumentException("Prefix '" + paramString2 + "' is already bound to '" + namespaceDecl.uri + '\''); 
    } 
    namespaceDecl = new NamespaceDecl(this.document.assignNewId(), paramString1, paramString2, paramBoolean);
    if (this.lastNs == null) {
      assert this.firstNs == null;
      this.firstNs = this.lastNs = namespaceDecl;
    } else {
      assert this.firstNs != null;
      this.lastNs.next = namespaceDecl;
      this.lastNs = namespaceDecl;
    } 
    return namespaceDecl;
  }
  
  private void checkWritable() {
    if (isWritten())
      throw new IllegalStateException("The start tag of " + this.localName + " has already been written. If you need out of order writing, see the TypedXmlWriter.block method"); 
  }
  
  boolean isWritten() { return (this.uri == null); }
  
  boolean isReadyToCommit() {
    if (this.owner != null && this.owner.isBlocked())
      return false; 
    for (Content content = getNext(); content != null; content = content.getNext()) {
      if (content.concludesPendingStartTag())
        return true; 
    } 
    return false;
  }
  
  public void written() {
    this.firstAtt = this.lastAtt = null;
    this.uri = null;
    if (this.owner != null) {
      assert this.owner.startTag == this;
      this.owner.startTag = null;
    } 
  }
  
  boolean concludesPendingStartTag() { return true; }
  
  void accept(ContentVisitor paramContentVisitor) { paramContentVisitor.onStartTag(this.uri, this.localName, this.firstAtt, this.firstNs); }
  
  public String getPrefix(String paramString) {
    NamespaceDecl namespaceDecl = addNamespaceDecl(paramString, null, false);
    return (namespaceDecl.prefix != null) ? namespaceDecl.prefix : namespaceDecl.dummyPrefix;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\StartTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */