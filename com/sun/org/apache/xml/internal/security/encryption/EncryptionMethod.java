package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

public interface EncryptionMethod {
  String getAlgorithm();
  
  int getKeySize();
  
  void setKeySize(int paramInt);
  
  byte[] getOAEPparams();
  
  void setOAEPparams(byte[] paramArrayOfByte);
  
  void setDigestAlgorithm(String paramString);
  
  String getDigestAlgorithm();
  
  void setMGFAlgorithm(String paramString);
  
  String getMGFAlgorithm();
  
  Iterator<Element> getEncryptionMethodInformation();
  
  void addEncryptionMethodInformation(Element paramElement);
  
  void removeEncryptionMethodInformation(Element paramElement);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\EncryptionMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */