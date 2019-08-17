package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;

public final class DTMXRTreeFrag {
  private DTM m_dtm;
  
  private int m_dtmIdentity = -1;
  
  private XPathContext m_xctxt;
  
  public DTMXRTreeFrag(int paramInt, XPathContext paramXPathContext) {
    this.m_xctxt = paramXPathContext;
    this.m_dtmIdentity = paramInt;
    this.m_dtm = paramXPathContext.getDTM(paramInt);
  }
  
  public final void destruct() {
    this.m_dtm = null;
    this.m_xctxt = null;
  }
  
  final DTM getDTM() { return this.m_dtm; }
  
  public final int getDTMIdentity() { return this.m_dtmIdentity; }
  
  final XPathContext getXPathContext() { return this.m_xctxt; }
  
  public final int hashCode() { return this.m_dtmIdentity; }
  
  public final boolean equals(Object paramObject) { return (paramObject instanceof DTMXRTreeFrag) ? ((this.m_dtmIdentity == ((DTMXRTreeFrag)paramObject).getDTMIdentity())) : false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\DTMXRTreeFrag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */