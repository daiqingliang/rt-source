package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import org.xml.sax.SAXException;

public class XMLEventWriterOutput extends XmlOutputAbstractImpl {
  private final XMLEventWriter out;
  
  private final XMLEventFactory ef;
  
  private final Characters sp;
  
  public XMLEventWriterOutput(XMLEventWriter paramXMLEventWriter) {
    this.out = paramXMLEventWriter;
    this.ef = XMLEventFactory.newInstance();
    this.sp = this.ef.createCharacters(" ");
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl) throws IOException, SAXException, XMLStreamException {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    if (!paramBoolean)
      this.out.add(this.ef.createStartDocument()); 
  }
  
  public void endDocument(boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    if (!paramBoolean) {
      this.out.add(this.ef.createEndDocument());
      this.out.flush();
    } 
    super.endDocument(paramBoolean);
  }
  
  public void beginStartTag(int paramInt, String paramString) throws IOException, XMLStreamException {
    this.out.add(this.ef.createStartElement(this.nsContext.getPrefix(paramInt), this.nsContext.getNamespaceURI(paramInt), paramString));
    NamespaceContextImpl.Element element = this.nsContext.getCurrent();
    if (element.count() > 0)
      for (int i = element.count() - 1; i >= 0; i--) {
        String str = element.getNsUri(i);
        if (str.length() != 0 || element.getBase() != 1)
          this.out.add(this.ef.createNamespace(element.getPrefix(i), str)); 
      }  
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2) throws IOException, XMLStreamException {
    Attribute attribute;
    if (paramInt == -1) {
      attribute = this.ef.createAttribute(paramString1, paramString2);
    } else {
      attribute = this.ef.createAttribute(this.nsContext.getPrefix(paramInt), this.nsContext.getNamespaceURI(paramInt), paramString1, paramString2);
    } 
    this.out.add(attribute);
  }
  
  public void endStartTag() throws IOException, SAXException {}
  
  public void endTag(int paramInt, String paramString) throws IOException, XMLStreamException { this.out.add(this.ef.createEndElement(this.nsContext.getPrefix(paramInt), this.nsContext.getNamespaceURI(paramInt), paramString)); }
  
  public void text(String paramString, boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    if (paramBoolean)
      this.out.add(this.sp); 
    this.out.add(this.ef.createCharacters(paramString));
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean) throws IOException, SAXException, XMLStreamException { text(paramPcdata.toString(), paramBoolean); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\XMLEventWriterOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */