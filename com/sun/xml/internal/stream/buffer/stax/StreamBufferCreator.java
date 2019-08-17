package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.stream.buffer.AbstractCreator;
import java.util.ArrayList;
import java.util.List;

abstract class StreamBufferCreator extends AbstractCreator {
  private boolean checkAttributeValue = false;
  
  protected List<String> attributeValuePrefixes = new ArrayList();
  
  protected void storeQualifiedName(int paramInt, String paramString1, String paramString2, String paramString3) {
    if (paramString2 != null && paramString2.length() > 0) {
      if (paramString1 != null && paramString1.length() > 0) {
        paramInt |= 0x1;
        storeStructureString(paramString1);
      } 
      paramInt |= 0x2;
      storeStructureString(paramString2);
    } 
    storeStructureString(paramString3);
    storeStructure(paramInt);
  }
  
  protected final void storeNamespaceAttribute(String paramString1, String paramString2) {
    byte b = 64;
    if (paramString1 != null && paramString1.length() > 0) {
      b |= 0x1;
      storeStructureString(paramString1);
    } 
    if (paramString2 != null && paramString2.length() > 0) {
      b |= 0x2;
      storeStructureString(paramString2);
    } 
    storeStructure(b);
  }
  
  protected final void storeAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    storeQualifiedName(48, paramString1, paramString2, paramString3);
    storeStructureString(paramString4);
    storeContentString(paramString5);
    if (this.checkAttributeValue && paramString5.indexOf("://") == -1) {
      int i = paramString5.indexOf(":");
      int j = paramString5.lastIndexOf(":");
      if (i != -1 && j == i) {
        String str = paramString5.substring(0, i);
        if (!this.attributeValuePrefixes.contains(str))
          this.attributeValuePrefixes.add(str); 
      } 
    } 
  }
  
  public final List getAttributeValuePrefixes() { return this.attributeValuePrefixes; }
  
  protected final void storeProcessingInstruction(String paramString1, String paramString2) {
    storeStructure(112);
    storeStructureString(paramString1);
    storeStructureString(paramString2);
  }
  
  public final boolean isCheckAttributeValue() { return this.checkAttributeValue; }
  
  public final void setCheckAttributeValue(boolean paramBoolean) { this.checkAttributeValue = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\stax\StreamBufferCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */