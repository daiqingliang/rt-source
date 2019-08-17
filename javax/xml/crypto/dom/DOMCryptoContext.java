package javax.xml.crypto.dom;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;

public class DOMCryptoContext implements XMLCryptoContext {
  private HashMap<String, String> nsMap = new HashMap();
  
  private HashMap<String, Element> idMap = new HashMap();
  
  private HashMap<Object, Object> objMap = new HashMap();
  
  private String baseURI;
  
  private KeySelector ks;
  
  private URIDereferencer dereferencer;
  
  private HashMap<String, Object> propMap = new HashMap();
  
  private String defaultPrefix;
  
  public String getNamespacePrefix(String paramString1, String paramString2) {
    if (paramString1 == null)
      throw new NullPointerException("namespaceURI cannot be null"); 
    String str = (String)this.nsMap.get(paramString1);
    return (str != null) ? str : paramString2;
  }
  
  public String putNamespacePrefix(String paramString1, String paramString2) {
    if (paramString1 == null)
      throw new NullPointerException("namespaceURI is null"); 
    return (String)this.nsMap.put(paramString1, paramString2);
  }
  
  public String getDefaultNamespacePrefix() { return this.defaultPrefix; }
  
  public void setDefaultNamespacePrefix(String paramString) { this.defaultPrefix = paramString; }
  
  public String getBaseURI() { return this.baseURI; }
  
  public void setBaseURI(String paramString) {
    if (paramString != null)
      URI.create(paramString); 
    this.baseURI = paramString;
  }
  
  public URIDereferencer getURIDereferencer() { return this.dereferencer; }
  
  public void setURIDereferencer(URIDereferencer paramURIDereferencer) { this.dereferencer = paramURIDereferencer; }
  
  public Object getProperty(String paramString) {
    if (paramString == null)
      throw new NullPointerException("name is null"); 
    return this.propMap.get(paramString);
  }
  
  public Object setProperty(String paramString, Object paramObject) {
    if (paramString == null)
      throw new NullPointerException("name is null"); 
    return this.propMap.put(paramString, paramObject);
  }
  
  public KeySelector getKeySelector() { return this.ks; }
  
  public void setKeySelector(KeySelector paramKeySelector) { this.ks = paramKeySelector; }
  
  public Element getElementById(String paramString) {
    if (paramString == null)
      throw new NullPointerException("idValue is null"); 
    return (Element)this.idMap.get(paramString);
  }
  
  public void setIdAttributeNS(Element paramElement, String paramString1, String paramString2) {
    if (paramElement == null)
      throw new NullPointerException("element is null"); 
    if (paramString2 == null)
      throw new NullPointerException("localName is null"); 
    String str = paramElement.getAttributeNS(paramString1, paramString2);
    if (str == null || str.length() == 0)
      throw new IllegalArgumentException(paramString2 + " is not an attribute"); 
    this.idMap.put(str, paramElement);
  }
  
  public Iterator iterator() { return Collections.unmodifiableMap(this.idMap).entrySet().iterator(); }
  
  public Object get(Object paramObject) { return this.objMap.get(paramObject); }
  
  public Object put(Object paramObject1, Object paramObject2) { return this.objMap.put(paramObject1, paramObject2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dom\DOMCryptoContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */