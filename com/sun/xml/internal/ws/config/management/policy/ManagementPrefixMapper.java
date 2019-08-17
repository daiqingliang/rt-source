package com.sun.xml.internal.ws.config.management.policy;

import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.HashMap;
import java.util.Map;

public class ManagementPrefixMapper implements PrefixMapper {
  private static final Map<String, String> prefixMap = new HashMap();
  
  public Map<String, String> getPrefixMap() { return prefixMap; }
  
  static  {
    prefixMap.put("http://java.sun.com/xml/ns/metro/management", "sunman");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\config\management\policy\ManagementPrefixMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */