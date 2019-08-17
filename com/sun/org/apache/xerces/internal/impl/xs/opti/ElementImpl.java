package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementImpl extends DefaultElement {
  SchemaDOM schemaDOM;
  
  Attr[] attrs;
  
  int row = -1;
  
  int col = -1;
  
  int parentRow = -1;
  
  int line;
  
  int column;
  
  int charOffset;
  
  String fAnnotation;
  
  String fSyntheticAnnotation;
  
  public ElementImpl(int paramInt1, int paramInt2, int paramInt3) {
    this.nodeType = 1;
    this.line = paramInt1;
    this.column = paramInt2;
    this.charOffset = paramInt3;
  }
  
  public ElementImpl(int paramInt1, int paramInt2) { this(paramInt1, paramInt2, -1); }
  
  public ElementImpl(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3) {
    super(paramString1, paramString2, paramString3, paramString4, (short)1);
    this.line = paramInt1;
    this.column = paramInt2;
    this.charOffset = paramInt3;
  }
  
  public ElementImpl(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2) { this(paramString1, paramString2, paramString3, paramString4, paramInt1, paramInt2, -1); }
  
  public Document getOwnerDocument() { return this.schemaDOM; }
  
  public Node getParentNode() { return this.schemaDOM.relations[this.row][0]; }
  
  public boolean hasChildNodes() { return !(this.parentRow == -1); }
  
  public Node getFirstChild() { return (this.parentRow == -1) ? null : this.schemaDOM.relations[this.parentRow][1]; }
  
  public Node getLastChild() {
    if (this.parentRow == -1)
      return null; 
    byte b;
    for (b = 1; b < this.schemaDOM.relations[this.parentRow].length; b++) {
      if (this.schemaDOM.relations[this.parentRow][b] == null)
        return this.schemaDOM.relations[this.parentRow][b - true]; 
    } 
    if (b == 1)
      b++; 
    return this.schemaDOM.relations[this.parentRow][b - 1];
  }
  
  public Node getPreviousSibling() { return (this.col == 1) ? null : this.schemaDOM.relations[this.row][this.col - 1]; }
  
  public Node getNextSibling() { return (this.col == this.schemaDOM.relations[this.row].length - 1) ? null : this.schemaDOM.relations[this.row][this.col + 1]; }
  
  public NamedNodeMap getAttributes() { return new NamedNodeMapImpl(this.attrs); }
  
  public boolean hasAttributes() { return !(this.attrs.length == 0); }
  
  public String getTagName() { return this.rawname; }
  
  public String getAttribute(String paramString) {
    for (byte b = 0; b < this.attrs.length; b++) {
      if (this.attrs[b].getName().equals(paramString))
        return this.attrs[b].getValue(); 
    } 
    return "";
  }
  
  public Attr getAttributeNode(String paramString) {
    for (byte b = 0; b < this.attrs.length; b++) {
      if (this.attrs[b].getName().equals(paramString))
        return this.attrs[b]; 
    } 
    return null;
  }
  
  public String getAttributeNS(String paramString1, String paramString2) {
    for (byte b = 0; b < this.attrs.length; b++) {
      if (this.attrs[b].getLocalName().equals(paramString2) && nsEquals(this.attrs[b].getNamespaceURI(), paramString1))
        return this.attrs[b].getValue(); 
    } 
    return "";
  }
  
  public Attr getAttributeNodeNS(String paramString1, String paramString2) {
    for (byte b = 0; b < this.attrs.length; b++) {
      if (this.attrs[b].getName().equals(paramString2) && nsEquals(this.attrs[b].getNamespaceURI(), paramString1))
        return this.attrs[b]; 
    } 
    return null;
  }
  
  public boolean hasAttribute(String paramString) {
    for (byte b = 0; b < this.attrs.length; b++) {
      if (this.attrs[b].getName().equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public boolean hasAttributeNS(String paramString1, String paramString2) {
    for (byte b = 0; b < this.attrs.length; b++) {
      if (this.attrs[b].getName().equals(paramString2) && nsEquals(this.attrs[b].getNamespaceURI(), paramString1))
        return true; 
    } 
    return false;
  }
  
  public void setAttribute(String paramString1, String paramString2) {
    for (byte b = 0; b < this.attrs.length; b++) {
      if (this.attrs[b].getName().equals(paramString1)) {
        this.attrs[b].setValue(paramString2);
        return;
      } 
    } 
  }
  
  public int getLineNumber() { return this.line; }
  
  public int getColumnNumber() { return this.column; }
  
  public int getCharacterOffset() { return this.charOffset; }
  
  public String getAnnotation() { return this.fAnnotation; }
  
  public String getSyntheticAnnotation() { return this.fSyntheticAnnotation; }
  
  private static boolean nsEquals(String paramString1, String paramString2) { return (paramString1 == null) ? ((paramString2 == null)) : paramString1.equals(paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\ElementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */