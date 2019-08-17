package com.sun.org.apache.xerces.internal.xni;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;

public interface XMLDTDContentModelHandler {
  public static final short SEPARATOR_CHOICE = 0;
  
  public static final short SEPARATOR_SEQUENCE = 1;
  
  public static final short OCCURS_ZERO_OR_ONE = 2;
  
  public static final short OCCURS_ZERO_OR_MORE = 3;
  
  public static final short OCCURS_ONE_OR_MORE = 4;
  
  void startContentModel(String paramString, Augmentations paramAugmentations) throws XNIException;
  
  void any(Augmentations paramAugmentations) throws XNIException;
  
  void empty(Augmentations paramAugmentations) throws XNIException;
  
  void startGroup(Augmentations paramAugmentations) throws XNIException;
  
  void pcdata(Augmentations paramAugmentations) throws XNIException;
  
  void element(String paramString, Augmentations paramAugmentations) throws XNIException;
  
  void separator(short paramShort, Augmentations paramAugmentations) throws XNIException;
  
  void occurrence(short paramShort, Augmentations paramAugmentations) throws XNIException;
  
  void endGroup(Augmentations paramAugmentations) throws XNIException;
  
  void endContentModel(Augmentations paramAugmentations) throws XNIException;
  
  void setDTDContentModelSource(XMLDTDContentModelSource paramXMLDTDContentModelSource);
  
  XMLDTDContentModelSource getDTDContentModelSource();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\XMLDTDContentModelHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */