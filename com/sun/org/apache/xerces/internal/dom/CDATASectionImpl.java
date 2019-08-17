package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.CDATASection;

public class CDATASectionImpl extends TextImpl implements CDATASection {
  static final long serialVersionUID = 2372071297878177780L;
  
  public CDATASectionImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) { super(paramCoreDocumentImpl, paramString); }
  
  public short getNodeType() { return 4; }
  
  public String getNodeName() { return "#cdata-section"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\CDATASectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */