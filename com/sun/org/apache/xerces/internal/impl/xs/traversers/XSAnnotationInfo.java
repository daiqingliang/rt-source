package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.opti.ElementImpl;
import org.w3c.dom.Element;

final class XSAnnotationInfo {
  String fAnnotation;
  
  int fLine;
  
  int fColumn;
  
  int fCharOffset;
  
  XSAnnotationInfo next;
  
  XSAnnotationInfo(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    this.fAnnotation = paramString;
    this.fLine = paramInt1;
    this.fColumn = paramInt2;
    this.fCharOffset = paramInt3;
  }
  
  XSAnnotationInfo(String paramString, Element paramElement) {
    this.fAnnotation = paramString;
    if (paramElement instanceof ElementImpl) {
      ElementImpl elementImpl = (ElementImpl)paramElement;
      this.fLine = elementImpl.getLineNumber();
      this.fColumn = elementImpl.getColumnNumber();
      this.fCharOffset = elementImpl.getCharacterOffset();
    } else {
      this.fLine = -1;
      this.fColumn = -1;
      this.fCharOffset = -1;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSAnnotationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */