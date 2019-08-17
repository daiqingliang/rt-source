package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextWrapper implements NamespaceContext {
  private NamespaceContext fNamespaceContext;
  
  public NamespaceContextWrapper(NamespaceSupport paramNamespaceSupport) { this.fNamespaceContext = paramNamespaceSupport; }
  
  public String getNamespaceURI(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Prefix can't be null"); 
    return this.fNamespaceContext.getURI(paramString.intern());
  }
  
  public String getPrefix(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("URI can't be null."); 
    return this.fNamespaceContext.getPrefix(paramString.intern());
  }
  
  public Iterator getPrefixes(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("URI can't be null."); 
    Vector vector = ((NamespaceSupport)this.fNamespaceContext).getPrefixes(paramString.intern());
    return vector.iterator();
  }
  
  public NamespaceContext getNamespaceContext() { return this.fNamespaceContext; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\NamespaceContextWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */