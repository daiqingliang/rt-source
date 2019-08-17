package com.sun.org.apache.xml.internal.serializer;

final class ElemContext {
  final int m_currentElemDepth;
  
  ElemDesc m_elementDesc = null;
  
  String m_elementLocalName = null;
  
  String m_elementName = null;
  
  String m_elementURI = null;
  
  boolean m_isCdataSection;
  
  boolean m_isRaw = false;
  
  private ElemContext m_next;
  
  final ElemContext m_prev = this;
  
  boolean m_startTagOpen = false;
  
  ElemContext() { this.m_currentElemDepth = 0; }
  
  private ElemContext(ElemContext paramElemContext) { paramElemContext.m_currentElemDepth++; }
  
  final ElemContext pop() { return this.m_prev; }
  
  final ElemContext push() {
    ElemContext elemContext = this.m_next;
    if (elemContext == null) {
      elemContext = new ElemContext(this);
      this.m_next = elemContext;
    } 
    elemContext.m_startTagOpen = true;
    return elemContext;
  }
  
  final ElemContext push(String paramString1, String paramString2, String paramString3) {
    ElemContext elemContext = this.m_next;
    if (elemContext == null) {
      elemContext = new ElemContext(this);
      this.m_next = elemContext;
    } 
    elemContext.m_elementName = paramString3;
    elemContext.m_elementLocalName = paramString2;
    elemContext.m_elementURI = paramString1;
    elemContext.m_isCdataSection = false;
    elemContext.m_startTagOpen = true;
    return elemContext;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\ElemContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */