package javax.xml.namespace;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class QName implements Serializable {
  private static final long serialVersionUID;
  
  private static final long defaultSerialVersionUID = -9120448754896609940L;
  
  private static final long compatibleSerialVersionUID = 4418622981026545151L;
  
  private static boolean useDefaultSerialVersionUID = true;
  
  private final String namespaceURI;
  
  private final String localPart;
  
  private final String prefix;
  
  public QName(String paramString1, String paramString2) { this(paramString1, paramString2, ""); }
  
  public QName(String paramString1, String paramString2, String paramString3) {
    if (paramString1 == null) {
      this.namespaceURI = "";
    } else {
      this.namespaceURI = paramString1;
    } 
    if (paramString2 == null)
      throw new IllegalArgumentException("local part cannot be \"null\" when creating a QName"); 
    this.localPart = paramString2;
    if (paramString3 == null)
      throw new IllegalArgumentException("prefix cannot be \"null\" when creating a QName"); 
    this.prefix = paramString3;
  }
  
  public QName(String paramString) { this("", paramString, ""); }
  
  public String getNamespaceURI() { return this.namespaceURI; }
  
  public String getLocalPart() { return this.localPart; }
  
  public String getPrefix() { return this.prefix; }
  
  public final boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject == null || !(paramObject instanceof QName))
      return false; 
    QName qName = (QName)paramObject;
    return (this.localPart.equals(qName.localPart) && this.namespaceURI.equals(qName.namespaceURI));
  }
  
  public final int hashCode() { return this.namespaceURI.hashCode() ^ this.localPart.hashCode(); }
  
  public String toString() { return this.namespaceURI.equals("") ? this.localPart : ("{" + this.namespaceURI + "}" + this.localPart); }
  
  public static QName valueOf(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("cannot create QName from \"null\" or \"\" String"); 
    if (paramString.length() == 0)
      return new QName("", paramString, ""); 
    if (paramString.charAt(0) != '{')
      return new QName("", paramString, ""); 
    if (paramString.startsWith("{}"))
      throw new IllegalArgumentException("Namespace URI .equals(XMLConstants.NULL_NS_URI), .equals(\"\"), only the local part, \"" + paramString.substring(2 + "".length()) + "\", should be provided."); 
    int i = paramString.indexOf('}');
    if (i == -1)
      throw new IllegalArgumentException("cannot create QName from \"" + paramString + "\", missing closing \"}\""); 
    return new QName(paramString.substring(1, i), paramString.substring(i + 1), "");
  }
  
  static  {
    try {
      String str = (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() { return System.getProperty("com.sun.xml.namespace.QName.useCompatibleSerialVersionUID"); }
          });
      useDefaultSerialVersionUID = !(str != null && str.equals("1.0"));
    } catch (Exception exception) {
      useDefaultSerialVersionUID = true;
    } 
    if (useDefaultSerialVersionUID) {
      serialVersionUID = -9120448754896609940L;
    } else {
      serialVersionUID = 4418622981026545151L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\namespace\QName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */