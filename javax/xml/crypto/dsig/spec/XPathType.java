package javax.xml.crypto.dsig.spec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class XPathType {
  private final String expression;
  
  private final Filter filter;
  
  private Map<String, String> nsMap;
  
  public XPathType(String paramString, Filter paramFilter) {
    if (paramString == null)
      throw new NullPointerException("expression cannot be null"); 
    if (paramFilter == null)
      throw new NullPointerException("filter cannot be null"); 
    this.expression = paramString;
    this.filter = paramFilter;
    this.nsMap = Collections.emptyMap();
  }
  
  public XPathType(String paramString, Filter paramFilter, Map paramMap) {
    this(paramString, paramFilter);
    if (paramMap == null)
      throw new NullPointerException("namespaceMap cannot be null"); 
    HashMap hashMap1 = new HashMap(paramMap);
    for (Map.Entry entry : hashMap1.entrySet()) {
      if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String))
        throw new ClassCastException("not a String"); 
    } 
    HashMap hashMap2 = hashMap1;
    this.nsMap = Collections.unmodifiableMap(hashMap2);
  }
  
  public String getExpression() { return this.expression; }
  
  public Filter getFilter() { return this.filter; }
  
  public Map getNamespaceMap() { return this.nsMap; }
  
  public static class Filter {
    private final String operation;
    
    public static final Filter INTERSECT = new Filter("intersect");
    
    public static final Filter SUBTRACT = new Filter("subtract");
    
    public static final Filter UNION = new Filter("union");
    
    private Filter(String param1String) { this.operation = param1String; }
    
    public String toString() { return this.operation; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\spec\XPathType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */