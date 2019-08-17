package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class MTOMXmlOutput extends XmlOutputAbstractImpl {
  private final XmlOutput next;
  
  private String nsUri;
  
  private String localName;
  
  public MTOMXmlOutput(XmlOutput paramXmlOutput) { this.next = paramXmlOutput; }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl) throws IOException, SAXException, XMLStreamException {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    this.next.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
  }
  
  public void endDocument(boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    this.next.endDocument(paramBoolean);
    super.endDocument(paramBoolean);
  }
  
  public void beginStartTag(Name paramName) throws IOException, XMLStreamException {
    this.next.beginStartTag(paramName);
    this.nsUri = paramName.nsUri;
    this.localName = paramName.localName;
  }
  
  public void beginStartTag(int paramInt, String paramString) throws IOException, XMLStreamException {
    this.next.beginStartTag(paramInt, paramString);
    this.nsUri = this.nsContext.getNamespaceURI(paramInt);
    this.localName = paramString;
  }
  
  public void attribute(Name paramName, String paramString) throws IOException, XMLStreamException { this.next.attribute(paramName, paramString); }
  
  public void attribute(int paramInt, String paramString1, String paramString2) throws IOException, XMLStreamException { this.next.attribute(paramInt, paramString1, paramString2); }
  
  public void endStartTag() throws IOException, SAXException { this.next.endStartTag(); }
  
  public void endTag(Name paramName) throws IOException, XMLStreamException { this.next.endTag(paramName); }
  
  public void endTag(int paramInt, String paramString) throws IOException, XMLStreamException { this.next.endTag(paramInt, paramString); }
  
  public void text(String paramString, boolean paramBoolean) throws IOException, SAXException, XMLStreamException { this.next.text(paramString, paramBoolean); }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    if (paramPcdata instanceof Base64Data && !this.serializer.getInlineBinaryFlag()) {
      String str;
      Base64Data base64Data = (Base64Data)paramPcdata;
      if (base64Data.hasData()) {
        str = this.serializer.attachmentMarshaller.addMtomAttachment(base64Data.get(), 0, base64Data.getDataLen(), base64Data.getMimeType(), this.nsUri, this.localName);
      } else {
        str = this.serializer.attachmentMarshaller.addMtomAttachment(base64Data.getDataHandler(), this.nsUri, this.localName);
      } 
      if (str != null) {
        this.nsContext.getCurrent().push();
        int i = this.nsContext.declareNsUri("http://www.w3.org/2004/08/xop/include", "xop", false);
        beginStartTag(i, "Include");
        attribute(-1, "href", str);
        endStartTag();
        endTag(i, "Include");
        this.nsContext.getCurrent().pop();
        return;
      } 
    } 
    this.next.text(paramPcdata, paramBoolean);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\MTOMXmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */