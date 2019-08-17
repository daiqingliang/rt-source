package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

public interface RevalidationHandler extends XMLDocumentFilter {
  boolean characterData(String paramString, Augmentations paramAugmentations);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\RevalidationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */