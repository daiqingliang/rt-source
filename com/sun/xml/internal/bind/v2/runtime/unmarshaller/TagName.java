package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.Name;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;

public abstract class TagName {
  public String uri;
  
  public String local;
  
  public Attributes atts;
  
  public final boolean matches(String paramString1, String paramString2) { return (this.uri == paramString1 && this.local == paramString2); }
  
  public final boolean matches(Name paramName) { return (this.local == paramName.localName && this.uri == paramName.nsUri); }
  
  public String toString() { return '{' + this.uri + '}' + this.local; }
  
  public abstract String getQname();
  
  public String getPrefix() {
    String str = getQname();
    int i = str.indexOf(':');
    return (i < 0) ? "" : str.substring(0, i);
  }
  
  public QName createQName() { return new QName(this.uri, this.local, getPrefix()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\TagName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */