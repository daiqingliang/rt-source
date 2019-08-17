package javax.xml.crypto.dsig.spec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class XPathFilterParameterSpec implements TransformParameterSpec {
  private String xPath;
  
  private Map<String, String> nsMap;
  
  public XPathFilterParameterSpec(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.xPath = paramString;
    this.nsMap = Collections.emptyMap();
  }
  
  public XPathFilterParameterSpec(String paramString, Map paramMap) {
    if (paramString == null || paramMap == null)
      throw new NullPointerException(); 
    this.xPath = paramString;
    HashMap hashMap1 = new HashMap(paramMap);
    for (Map.Entry entry : hashMap1.entrySet()) {
      if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String))
        throw new ClassCastException("not a String"); 
    } 
    HashMap hashMap2 = hashMap1;
    this.nsMap = Collections.unmodifiableMap(hashMap2);
  }
  
  public String getXPath() { return this.xPath; }
  
  public Map getNamespaceMap() { return this.nsMap; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\spec\XPathFilterParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */