package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import javax.xml.transform.Result;

public class TXWResult implements Result {
  private String systemId;
  
  private TypedXmlWriter writer;
  
  public TXWResult(TypedXmlWriter paramTypedXmlWriter) { this.writer = paramTypedXmlWriter; }
  
  public TypedXmlWriter getWriter() { return this.writer; }
  
  public void setWriter(TypedXmlWriter paramTypedXmlWriter) { this.writer = paramTypedXmlWriter; }
  
  public String getSystemId() { return this.systemId; }
  
  public void setSystemId(String paramString) { this.systemId = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\TXWResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */