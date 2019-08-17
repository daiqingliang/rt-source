package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public final class NameBuilder {
  private Map<String, Integer> uriIndexMap = new HashMap();
  
  private Set<String> nonDefaultableNsUris = new HashSet();
  
  private Map<String, Integer> localNameIndexMap = new HashMap();
  
  private QNameMap<Integer> elementQNameIndexMap = new QNameMap();
  
  private QNameMap<Integer> attributeQNameIndexMap = new QNameMap();
  
  public Name createElementName(QName paramQName) { return createElementName(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  public Name createElementName(String paramString1, String paramString2) { return createName(paramString1, paramString2, false, this.elementQNameIndexMap); }
  
  public Name createAttributeName(QName paramQName) { return createAttributeName(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  public Name createAttributeName(String paramString1, String paramString2) {
    assert paramString1.intern() == paramString1;
    assert paramString2.intern() == paramString2;
    if (paramString1.length() == 0)
      return new Name(allocIndex(this.attributeQNameIndexMap, "", paramString2), -1, paramString1, allocIndex(this.localNameIndexMap, paramString2), paramString2, true); 
    this.nonDefaultableNsUris.add(paramString1);
    return createName(paramString1, paramString2, true, this.attributeQNameIndexMap);
  }
  
  private Name createName(String paramString1, String paramString2, boolean paramBoolean, QNameMap<Integer> paramQNameMap) {
    assert paramString1.intern() == paramString1;
    assert paramString2.intern() == paramString2;
    return new Name(allocIndex(paramQNameMap, paramString1, paramString2), allocIndex(this.uriIndexMap, paramString1), paramString1, allocIndex(this.localNameIndexMap, paramString2), paramString2, paramBoolean);
  }
  
  private int allocIndex(Map<String, Integer> paramMap, String paramString) {
    Integer integer = (Integer)paramMap.get(paramString);
    if (integer == null) {
      integer = Integer.valueOf(paramMap.size());
      paramMap.put(paramString, integer);
    } 
    return integer.intValue();
  }
  
  private int allocIndex(QNameMap<Integer> paramQNameMap, String paramString1, String paramString2) {
    Integer integer = (Integer)paramQNameMap.get(paramString1, paramString2);
    if (integer == null) {
      integer = Integer.valueOf(paramQNameMap.size());
      paramQNameMap.put(paramString1, paramString2, integer);
    } 
    return integer.intValue();
  }
  
  public NameList conclude() {
    boolean[] arrayOfBoolean = new boolean[this.uriIndexMap.size()];
    for (Map.Entry entry : this.uriIndexMap.entrySet())
      arrayOfBoolean[((Integer)entry.getValue()).intValue()] = this.nonDefaultableNsUris.contains(entry.getKey()); 
    NameList nameList = new NameList(list(this.uriIndexMap), arrayOfBoolean, list(this.localNameIndexMap), this.elementQNameIndexMap.size(), this.attributeQNameIndexMap.size());
    this.uriIndexMap = null;
    this.localNameIndexMap = null;
    return nameList;
  }
  
  private String[] list(Map<String, Integer> paramMap) {
    String[] arrayOfString = new String[paramMap.size()];
    for (Map.Entry entry : paramMap.entrySet())
      arrayOfString[((Integer)entry.getValue()).intValue()] = (String)entry.getKey(); 
    return arrayOfString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\NameBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */