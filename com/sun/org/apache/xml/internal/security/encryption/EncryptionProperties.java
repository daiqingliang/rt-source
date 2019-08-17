package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;

public interface EncryptionProperties {
  String getId();
  
  void setId(String paramString);
  
  Iterator<EncryptionProperty> getEncryptionProperties();
  
  void addEncryptionProperty(EncryptionProperty paramEncryptionProperty);
  
  void removeEncryptionProperty(EncryptionProperty paramEncryptionProperty);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\EncryptionProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */