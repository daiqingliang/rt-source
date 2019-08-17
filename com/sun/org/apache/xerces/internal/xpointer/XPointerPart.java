package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public interface XPointerPart {
  public static final int EVENT_ELEMENT_START = 0;
  
  public static final int EVENT_ELEMENT_END = 1;
  
  public static final int EVENT_ELEMENT_EMPTY = 2;
  
  void parseXPointer(String paramString) throws XNIException;
  
  boolean resolveXPointer(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations, int paramInt) throws XNIException;
  
  boolean isFragmentResolved() throws XNIException;
  
  boolean isChildFragmentResolved() throws XNIException;
  
  String getSchemeName();
  
  String getSchemeData();
  
  void setSchemeName(String paramString) throws XNIException;
  
  void setSchemeData(String paramString) throws XNIException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xpointer\XPointerPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */