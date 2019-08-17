package javax.xml.transform.stax;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public class StAXResult implements Result {
  public static final String FEATURE = "http://javax.xml.transform.stax.StAXResult/feature";
  
  private XMLEventWriter xmlEventWriter = null;
  
  private XMLStreamWriter xmlStreamWriter = null;
  
  private String systemId = null;
  
  public StAXResult(XMLEventWriter paramXMLEventWriter) {
    if (paramXMLEventWriter == null)
      throw new IllegalArgumentException("StAXResult(XMLEventWriter) with XMLEventWriter == null"); 
    this.xmlEventWriter = paramXMLEventWriter;
  }
  
  public StAXResult(XMLStreamWriter paramXMLStreamWriter) {
    if (paramXMLStreamWriter == null)
      throw new IllegalArgumentException("StAXResult(XMLStreamWriter) with XMLStreamWriter == null"); 
    this.xmlStreamWriter = paramXMLStreamWriter;
  }
  
  public XMLEventWriter getXMLEventWriter() { return this.xmlEventWriter; }
  
  public XMLStreamWriter getXMLStreamWriter() { return this.xmlStreamWriter; }
  
  public void setSystemId(String paramString) { throw new UnsupportedOperationException("StAXResult#setSystemId(systemId) cannot set the system identifier for a StAXResult"); }
  
  public String getSystemId() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\stax\StAXResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */