package com.sun.org.apache.xml.internal.dtm;

import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import javax.xml.transform.Source;
import org.w3c.dom.Node;

public abstract class DTMManager {
  protected XMLStringFactory m_xsf = null;
  
  private boolean _overrideDefaultParser;
  
  public boolean m_incremental = false;
  
  public boolean m_source_location = false;
  
  public static final int IDENT_DTM_NODE_BITS = 16;
  
  public static final int IDENT_NODE_DEFAULT = 65535;
  
  public static final int IDENT_DTM_DEFAULT = -65536;
  
  public static final int IDENT_MAX_DTMS = 65536;
  
  public XMLStringFactory getXMLStringFactory() { return this.m_xsf; }
  
  public void setXMLStringFactory(XMLStringFactory paramXMLStringFactory) { this.m_xsf = paramXMLStringFactory; }
  
  public static DTMManager newInstance(XMLStringFactory paramXMLStringFactory) throws DTMException {
    DTMManagerDefault dTMManagerDefault = new DTMManagerDefault();
    dTMManagerDefault.setXMLStringFactory(paramXMLStringFactory);
    return dTMManagerDefault;
  }
  
  public abstract DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3);
  
  public abstract DTM getDTM(int paramInt);
  
  public abstract int getDTMHandleFromNode(Node paramNode);
  
  public abstract DTM createDocumentFragment();
  
  public abstract boolean release(DTM paramDTM, boolean paramBoolean);
  
  public abstract DTMIterator createDTMIterator(Object paramObject, int paramInt);
  
  public abstract DTMIterator createDTMIterator(String paramString, PrefixResolver paramPrefixResolver);
  
  public abstract DTMIterator createDTMIterator(int paramInt, DTMFilter paramDTMFilter, boolean paramBoolean);
  
  public abstract DTMIterator createDTMIterator(int paramInt);
  
  public boolean getIncremental() { return this.m_incremental; }
  
  public void setIncremental(boolean paramBoolean) { this.m_incremental = paramBoolean; }
  
  public boolean getSource_location() { return this.m_source_location; }
  
  public void setSource_location(boolean paramBoolean) { this.m_source_location = paramBoolean; }
  
  public boolean overrideDefaultParser() { return this._overrideDefaultParser; }
  
  public void setOverrideDefaultParser(boolean paramBoolean) { this._overrideDefaultParser = paramBoolean; }
  
  public abstract int getDTMIdentity(DTM paramDTM);
  
  public int getDTMIdentityMask() { return -65536; }
  
  public int getNodeIdentityMask() { return 65535; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTMManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */