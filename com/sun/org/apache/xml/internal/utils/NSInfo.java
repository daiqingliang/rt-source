package com.sun.org.apache.xml.internal.utils;

public class NSInfo {
  public String m_namespace;
  
  public boolean m_hasXMLNSAttrs;
  
  public boolean m_hasProcessedNS;
  
  public int m_ancestorHasXMLNSAttrs;
  
  public static final int ANCESTORXMLNSUNPROCESSED = 0;
  
  public static final int ANCESTORHASXMLNS = 1;
  
  public static final int ANCESTORNOXMLNS = 2;
  
  public NSInfo(boolean paramBoolean1, boolean paramBoolean2) {
    this.m_hasProcessedNS = paramBoolean1;
    this.m_hasXMLNSAttrs = paramBoolean2;
    this.m_namespace = null;
    this.m_ancestorHasXMLNSAttrs = 0;
  }
  
  public NSInfo(boolean paramBoolean1, boolean paramBoolean2, int paramInt) {
    this.m_hasProcessedNS = paramBoolean1;
    this.m_hasXMLNSAttrs = paramBoolean2;
    this.m_ancestorHasXMLNSAttrs = paramInt;
    this.m_namespace = null;
  }
  
  public NSInfo(String paramString, boolean paramBoolean) {
    this.m_hasProcessedNS = true;
    this.m_hasXMLNSAttrs = paramBoolean;
    this.m_namespace = paramString;
    this.m_ancestorHasXMLNSAttrs = 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\NSInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */