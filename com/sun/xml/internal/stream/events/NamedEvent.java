package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;

public class NamedEvent extends DummyEvent {
  private QName name;
  
  public NamedEvent() {}
  
  public NamedEvent(QName paramQName) { this.name = paramQName; }
  
  public NamedEvent(String paramString1, String paramString2, String paramString3) { this.name = new QName(paramString2, paramString3, paramString1); }
  
  public String getPrefix() { return this.name.getPrefix(); }
  
  public QName getName() { return this.name; }
  
  public void setName(QName paramQName) { this.name = paramQName; }
  
  public String nameAsString() { return "".equals(this.name.getNamespaceURI()) ? this.name.getLocalPart() : ((this.name.getPrefix() != null) ? ("['" + this.name.getNamespaceURI() + "']:" + getPrefix() + ":" + this.name.getLocalPart()) : ("['" + this.name.getNamespaceURI() + "']:" + this.name.getLocalPart())); }
  
  public String getNamespace() { return this.name.getNamespaceURI(); }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException { paramWriter.write(nameAsString()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\NamedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */