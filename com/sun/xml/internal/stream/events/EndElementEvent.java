package com.sun.xml.internal.stream.events;

import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;

public class EndElementEvent extends DummyEvent implements EndElement {
  List fNamespaces = null;
  
  QName fQName;
  
  public EndElementEvent() { init(); }
  
  protected void init() {
    setEventType(2);
    this.fNamespaces = new ArrayList();
  }
  
  public EndElementEvent(String paramString1, String paramString2, String paramString3) { this(new QName(paramString2, paramString3, paramString1)); }
  
  public EndElementEvent(QName paramQName) {
    this.fQName = paramQName;
    init();
  }
  
  public QName getName() { return this.fQName; }
  
  public void setName(QName paramQName) { this.fQName = paramQName; }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException {
    paramWriter.write("</");
    String str = this.fQName.getPrefix();
    if (str != null && str.length() > 0) {
      paramWriter.write(str);
      paramWriter.write(58);
    } 
    paramWriter.write(this.fQName.getLocalPart());
    paramWriter.write(62);
  }
  
  public Iterator getNamespaces() {
    if (this.fNamespaces != null)
      this.fNamespaces.iterator(); 
    return new ReadOnlyIterator();
  }
  
  void addNamespace(Namespace paramNamespace) {
    if (paramNamespace != null)
      this.fNamespaces.add(paramNamespace); 
  }
  
  public String toString() {
    null = "</" + nameAsString();
    return null + ">";
  }
  
  public String nameAsString() { return "".equals(this.fQName.getNamespaceURI()) ? this.fQName.getLocalPart() : ((this.fQName.getPrefix() != null) ? ("['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getPrefix() + ":" + this.fQName.getLocalPart()) : ("['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getLocalPart())); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\EndElementEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */