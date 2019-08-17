package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLEntityHandler {
  void startEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException;
  
  void endEntity(String paramString, Augmentations paramAugmentations) throws IOException, XNIException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLEntityHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */