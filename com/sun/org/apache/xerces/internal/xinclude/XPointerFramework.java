package com.sun.org.apache.xerces.internal.xinclude;

import java.util.Stack;

public class XPointerFramework {
  XPointerSchema[] fXPointerSchema;
  
  String[] fSchemaPointerName;
  
  String[] fSchemaPointerURI;
  
  String fSchemaPointer;
  
  String fCurrentSchemaPointer;
  
  Stack fSchemaNotAvailable;
  
  int fCountSchemaName = 0;
  
  int schemaLength = 0;
  
  XPointerSchema fDefaultXPointerSchema;
  
  public XPointerFramework() { this(null); }
  
  public XPointerFramework(XPointerSchema[] paramArrayOfXPointerSchema) {
    this.fXPointerSchema = paramArrayOfXPointerSchema;
    this.fSchemaNotAvailable = new Stack();
  }
  
  public void reset() {
    this.fXPointerSchema = null;
    this.fXPointerSchema = null;
    this.fCountSchemaName = 0;
    this.schemaLength = 0;
    this.fSchemaPointerName = null;
    this.fSchemaPointerURI = null;
    this.fDefaultXPointerSchema = null;
    this.fCurrentSchemaPointer = null;
  }
  
  public void setXPointerSchema(XPointerSchema[] paramArrayOfXPointerSchema) { this.fXPointerSchema = paramArrayOfXPointerSchema; }
  
  public void setSchemaPointer(String paramString) { this.fSchemaPointer = paramString; }
  
  public XPointerSchema getNextXPointerSchema() {
    int i = this.fCountSchemaName;
    if (this.fSchemaPointerName == null)
      getSchemaNames(); 
    if (this.fDefaultXPointerSchema == null)
      getDefaultSchema(); 
    if (this.fDefaultXPointerSchema.getXpointerSchemaName().equalsIgnoreCase(this.fSchemaPointerName[i])) {
      this.fDefaultXPointerSchema.reset();
      this.fDefaultXPointerSchema.setXPointerSchemaPointer(this.fSchemaPointerURI[i]);
      this.fCountSchemaName = ++i;
      return getDefaultSchema();
    } 
    if (this.fXPointerSchema == null) {
      this.fCountSchemaName = ++i;
      return null;
    } 
    int j = this.fXPointerSchema.length;
    while (this.fSchemaPointerName[i] != null) {
      for (byte b = 0; b < j; b++) {
        if (this.fSchemaPointerName[i].equalsIgnoreCase(this.fXPointerSchema[b].getXpointerSchemaName())) {
          this.fXPointerSchema[b].setXPointerSchemaPointer(this.fSchemaPointerURI[i]);
          this.fCountSchemaName = ++i;
          return this.fXPointerSchema[b];
        } 
      } 
      if (this.fSchemaNotAvailable == null)
        this.fSchemaNotAvailable = new Stack(); 
      this.fSchemaNotAvailable.push(this.fSchemaPointerName[i]);
      i++;
    } 
    return null;
  }
  
  public XPointerSchema getDefaultSchema() {
    if (this.fDefaultXPointerSchema == null)
      this.fDefaultXPointerSchema = new XPointerElementHandler(); 
    return this.fDefaultXPointerSchema;
  }
  
  public void getSchemaNames() {
    byte b1 = 0;
    int i = 0;
    int j = 0;
    byte b2 = 0;
    byte b3 = 0;
    int k = this.fSchemaPointer.length();
    this.fSchemaPointerName = new String[5];
    this.fSchemaPointerURI = new String[5];
    i = this.fSchemaPointer.indexOf('(');
    if (i <= 0)
      return; 
    this.fSchemaPointerName[b2++] = this.fSchemaPointer.substring(0, i++).trim();
    j = i;
    String str = null;
    b1++;
    while (i < k) {
      char c = this.fSchemaPointer.charAt(i);
      if (c == '(')
        b1++; 
      if (c == ')')
        b1--; 
      if (b1 == 0) {
        str = this.fSchemaPointer.substring(j, i).trim();
        this.fSchemaPointerURI[b3++] = getEscapedURI(str);
        j = i;
        if ((i = this.fSchemaPointer.indexOf('(', j)) != -1) {
          this.fSchemaPointerName[b2++] = this.fSchemaPointer.substring(j + 1, i).trim();
          b1++;
          j = i + 1;
        } else {
          i = j;
        } 
      } 
      i++;
    } 
    this.schemaLength = b3 - 1;
  }
  
  public String getEscapedURI(String paramString) { return paramString; }
  
  public int getSchemaCount() { return this.schemaLength; }
  
  public int getCurrentPointer() { return this.fCountSchemaName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XPointerFramework.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */