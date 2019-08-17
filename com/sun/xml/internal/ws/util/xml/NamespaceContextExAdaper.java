package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextExAdaper implements NamespaceContextEx {
  private final NamespaceContext nsContext;
  
  public NamespaceContextExAdaper(NamespaceContext paramNamespaceContext) { this.nsContext = paramNamespaceContext; }
  
  public Iterator<NamespaceContextEx.Binding> iterator() { throw new UnsupportedOperationException(); }
  
  public String getNamespaceURI(String paramString) { return this.nsContext.getNamespaceURI(paramString); }
  
  public String getPrefix(String paramString) { return this.nsContext.getPrefix(paramString); }
  
  public Iterator getPrefixes(String paramString) { return this.nsContext.getPrefixes(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\NamespaceContextExAdaper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */