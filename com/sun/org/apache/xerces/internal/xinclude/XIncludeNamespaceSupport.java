package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;

public class XIncludeNamespaceSupport extends MultipleScopeNamespaceSupport {
  private boolean[] fValidContext = new boolean[8];
  
  public XIncludeNamespaceSupport() {}
  
  public XIncludeNamespaceSupport(NamespaceContext paramNamespaceContext) { super(paramNamespaceContext); }
  
  public void pushContext() {
    super.pushContext();
    if (this.fCurrentContext + 1 == this.fValidContext.length) {
      boolean[] arrayOfBoolean = new boolean[this.fValidContext.length * 2];
      System.arraycopy(this.fValidContext, 0, arrayOfBoolean, 0, this.fValidContext.length);
      this.fValidContext = arrayOfBoolean;
    } 
    this.fValidContext[this.fCurrentContext] = true;
  }
  
  public void setContextInvalid() { this.fValidContext[this.fCurrentContext] = false; }
  
  public String getURIFromIncludeParent(String paramString) {
    int i;
    for (i = this.fCurrentContext - 1; i > 0 && !this.fValidContext[i]; i--);
    return getURI(paramString, i);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XIncludeNamespaceSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */