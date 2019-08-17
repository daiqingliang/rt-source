package com.sun.org.apache.xml.internal.serializer;

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public final class AttributesImplSerializer extends AttributesImpl {
  private final Map<String, Integer> m_indexFromQName = new HashMap();
  
  private final StringBuffer m_buff = new StringBuffer();
  
  private static final int MAX = 12;
  
  private static final int MAXMinus1 = 11;
  
  public final int getIndex(String paramString) {
    int i;
    if (getLength() < 12)
      return super.getIndex(paramString); 
    Integer integer = (Integer)this.m_indexFromQName.get(paramString);
    if (integer == null) {
      i = -1;
    } else {
      i = integer.intValue();
    } 
    return i;
  }
  
  public final void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    int i = getLength();
    super.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5);
    if (i < 11)
      return; 
    if (i == 11) {
      switchOverToHash(12);
    } else {
      Integer integer = Integer.valueOf(i);
      this.m_indexFromQName.put(paramString3, integer);
      this.m_buff.setLength(0);
      this.m_buff.append('{').append(paramString1).append('}').append(paramString2);
      String str = this.m_buff.toString();
      this.m_indexFromQName.put(str, integer);
    } 
  }
  
  private void switchOverToHash(int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      String str1 = getQName(b);
      Integer integer = Integer.valueOf(b);
      this.m_indexFromQName.put(str1, integer);
      String str2 = getURI(b);
      String str3 = getLocalName(b);
      this.m_buff.setLength(0);
      this.m_buff.append('{').append(str2).append('}').append(str3);
      String str4 = this.m_buff.toString();
      this.m_indexFromQName.put(str4, integer);
    } 
  }
  
  public final void clear() {
    int i = getLength();
    super.clear();
    if (12 <= i)
      this.m_indexFromQName.clear(); 
  }
  
  public final void setAttributes(Attributes paramAttributes) {
    super.setAttributes(paramAttributes);
    int i = paramAttributes.getLength();
    if (12 <= i)
      switchOverToHash(i); 
  }
  
  public final int getIndex(String paramString1, String paramString2) {
    int i;
    if (getLength() < 12)
      return super.getIndex(paramString1, paramString2); 
    this.m_buff.setLength(0);
    this.m_buff.append('{').append(paramString1).append('}').append(paramString2);
    String str = this.m_buff.toString();
    Integer integer = (Integer)this.m_indexFromQName.get(str);
    if (integer == null) {
      i = -1;
    } else {
      i = integer.intValue();
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\AttributesImplSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */