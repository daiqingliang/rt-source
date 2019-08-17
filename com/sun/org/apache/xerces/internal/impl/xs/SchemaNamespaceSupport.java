package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;

public class SchemaNamespaceSupport extends NamespaceSupport {
  public SchemaNamespaceSupport() {}
  
  public SchemaNamespaceSupport(SchemaNamespaceSupport paramSchemaNamespaceSupport) {
    this.fNamespaceSize = paramSchemaNamespaceSupport.fNamespaceSize;
    if (this.fNamespace.length < this.fNamespaceSize)
      this.fNamespace = new String[this.fNamespaceSize]; 
    System.arraycopy(paramSchemaNamespaceSupport.fNamespace, 0, this.fNamespace, 0, this.fNamespaceSize);
    this.fCurrentContext = paramSchemaNamespaceSupport.fCurrentContext;
    if (this.fContext.length <= this.fCurrentContext)
      this.fContext = new int[this.fCurrentContext + 1]; 
    System.arraycopy(paramSchemaNamespaceSupport.fContext, 0, this.fContext, 0, this.fCurrentContext + 1);
  }
  
  public void setEffectiveContext(String[] paramArrayOfString) {
    if (paramArrayOfString == null || paramArrayOfString.length == 0)
      return; 
    pushContext();
    int i = this.fNamespaceSize + paramArrayOfString.length;
    if (this.fNamespace.length < i) {
      String[] arrayOfString = new String[i];
      System.arraycopy(this.fNamespace, 0, arrayOfString, 0, this.fNamespace.length);
      this.fNamespace = arrayOfString;
    } 
    System.arraycopy(paramArrayOfString, 0, this.fNamespace, this.fNamespaceSize, paramArrayOfString.length);
    this.fNamespaceSize = i;
  }
  
  public String[] getEffectiveLocalContext() {
    String[] arrayOfString = null;
    if (this.fCurrentContext >= 3) {
      int i = this.fContext[3];
      int j = this.fNamespaceSize - i;
      if (j > 0) {
        arrayOfString = new String[j];
        System.arraycopy(this.fNamespace, i, arrayOfString, 0, j);
      } 
    } 
    return arrayOfString;
  }
  
  public void makeGlobal() {
    if (this.fCurrentContext >= 3) {
      this.fCurrentContext = 3;
      this.fNamespaceSize = this.fContext[3];
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\SchemaNamespaceSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */