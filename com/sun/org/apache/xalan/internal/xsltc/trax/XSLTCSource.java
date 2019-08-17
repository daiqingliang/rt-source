package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;

public final class XSLTCSource implements Source {
  private String _systemId = null;
  
  private Source _source = null;
  
  private ThreadLocal _dom = new ThreadLocal();
  
  public XSLTCSource(String paramString) { this._systemId = paramString; }
  
  public XSLTCSource(Source paramSource) { this._source = paramSource; }
  
  public void setSystemId(String paramString) {
    this._systemId = paramString;
    if (this._source != null)
      this._source.setSystemId(paramString); 
  }
  
  public String getSystemId() { return (this._source != null) ? this._source.getSystemId() : this._systemId; }
  
  protected DOM getDOM(XSLTCDTMManager paramXSLTCDTMManager, AbstractTranslet paramAbstractTranslet) throws SAXException {
    SAXImpl sAXImpl = (SAXImpl)this._dom.get();
    if (sAXImpl != null) {
      if (paramXSLTCDTMManager != null)
        sAXImpl.migrateTo(paramXSLTCDTMManager); 
    } else {
      Source source = this._source;
      if (source == null)
        if (this._systemId != null && this._systemId.length() > 0) {
          source = new StreamSource(this._systemId);
        } else {
          ErrorMsg errorMsg = new ErrorMsg("XSLTC_SOURCE_ERR");
          throw new SAXException(errorMsg.toString());
        }  
      DOMWSFilter dOMWSFilter = null;
      if (paramAbstractTranslet != null && paramAbstractTranslet instanceof com.sun.org.apache.xalan.internal.xsltc.StripFilter)
        dOMWSFilter = new DOMWSFilter(paramAbstractTranslet); 
      boolean bool = (paramAbstractTranslet != null) ? paramAbstractTranslet.hasIdCall() : 0;
      if (paramXSLTCDTMManager == null)
        paramXSLTCDTMManager = XSLTCDTMManager.newInstance(); 
      sAXImpl = (SAXImpl)paramXSLTCDTMManager.getDTM(source, true, dOMWSFilter, false, false, bool);
      String str = getSystemId();
      if (str != null)
        sAXImpl.setDocumentURI(str); 
      this._dom.set(sAXImpl);
    } 
    return sAXImpl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\XSLTCSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */