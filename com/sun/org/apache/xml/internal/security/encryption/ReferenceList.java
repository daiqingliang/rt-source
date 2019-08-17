package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;

public interface ReferenceList {
  public static final int DATA_REFERENCE = 1;
  
  public static final int KEY_REFERENCE = 2;
  
  void add(Reference paramReference);
  
  void remove(Reference paramReference);
  
  int size();
  
  boolean isEmpty();
  
  Iterator<Reference> getReferences();
  
  Reference newDataReference(String paramString);
  
  Reference newKeyReference(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\ReferenceList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */