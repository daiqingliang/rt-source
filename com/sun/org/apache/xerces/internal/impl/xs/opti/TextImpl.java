package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class TextImpl extends DefaultText {
  String fData = null;
  
  SchemaDOM fSchemaDOM = null;
  
  int fRow;
  
  int fCol;
  
  public TextImpl(StringBuffer paramStringBuffer, SchemaDOM paramSchemaDOM, int paramInt1, int paramInt2) {
    this.fData = paramStringBuffer.toString();
    this.fSchemaDOM = paramSchemaDOM;
    this.fRow = paramInt1;
    this.fCol = paramInt2;
    this.rawname = this.prefix = this.localpart = this.uri = null;
    this.nodeType = 3;
  }
  
  public Node getParentNode() { return this.fSchemaDOM.relations[this.fRow][0]; }
  
  public Node getPreviousSibling() { return (this.fCol == 1) ? null : this.fSchemaDOM.relations[this.fRow][this.fCol - 1]; }
  
  public Node getNextSibling() { return (this.fCol == this.fSchemaDOM.relations[this.fRow].length - 1) ? null : this.fSchemaDOM.relations[this.fRow][this.fCol + 1]; }
  
  public String getData() throws DOMException { return this.fData; }
  
  public int getLength() { return (this.fData == null) ? 0 : this.fData.length(); }
  
  public String substringData(int paramInt1, int paramInt2) throws DOMException {
    if (this.fData == null)
      return null; 
    if (paramInt2 < 0 || paramInt1 < 0 || paramInt1 > this.fData.length())
      throw new DOMException((short)1, "parameter error"); 
    return (paramInt1 + paramInt2 >= this.fData.length()) ? this.fData.substring(paramInt1) : this.fData.substring(paramInt1, paramInt1 + paramInt2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\TextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */