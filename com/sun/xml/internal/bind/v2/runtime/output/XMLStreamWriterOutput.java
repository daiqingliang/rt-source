package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.marshaller.NoEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.SAXException;

public class XMLStreamWriterOutput extends XmlOutputAbstractImpl {
  private final XMLStreamWriter out;
  
  private final CharacterEscapeHandler escapeHandler;
  
  private final XmlStreamOutWriterAdapter writerWrapper;
  
  protected final char[] buf = new char[256];
  
  private static final Class FI_STAX_WRITER_CLASS = initFIStAXWriterClass();
  
  private static final Constructor<? extends XmlOutput> FI_OUTPUT_CTOR = initFastInfosetOutputClass();
  
  private static final Class STAXEX_WRITER_CLASS = initStAXExWriterClass();
  
  private static final Constructor<? extends XmlOutput> STAXEX_OUTPUT_CTOR = initStAXExOutputClass();
  
  public static XmlOutput create(XMLStreamWriter paramXMLStreamWriter, JAXBContextImpl paramJAXBContextImpl, CharacterEscapeHandler paramCharacterEscapeHandler) {
    Class clazz = paramXMLStreamWriter.getClass();
    if (clazz == FI_STAX_WRITER_CLASS)
      try {
        return (XmlOutput)FI_OUTPUT_CTOR.newInstance(new Object[] { paramXMLStreamWriter, paramJAXBContextImpl });
      } catch (Exception exception) {} 
    if (STAXEX_WRITER_CLASS != null && STAXEX_WRITER_CLASS.isAssignableFrom(clazz))
      try {
        return (XmlOutput)STAXEX_OUTPUT_CTOR.newInstance(new Object[] { paramXMLStreamWriter });
      } catch (Exception exception) {} 
    CharacterEscapeHandler characterEscapeHandler = (paramCharacterEscapeHandler != null) ? paramCharacterEscapeHandler : NoEscapeHandler.theInstance;
    return new XMLStreamWriterOutput(paramXMLStreamWriter, characterEscapeHandler);
  }
  
  protected XMLStreamWriterOutput(XMLStreamWriter paramXMLStreamWriter, CharacterEscapeHandler paramCharacterEscapeHandler) {
    this.out = paramXMLStreamWriter;
    this.escapeHandler = paramCharacterEscapeHandler;
    this.writerWrapper = new XmlStreamOutWriterAdapter(paramXMLStreamWriter, null);
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl) throws IOException, SAXException, XMLStreamException {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    if (!paramBoolean)
      this.out.writeStartDocument(); 
  }
  
  public void endDocument(boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    if (!paramBoolean) {
      this.out.writeEndDocument();
      this.out.flush();
    } 
    super.endDocument(paramBoolean);
  }
  
  public void beginStartTag(int paramInt, String paramString) throws IOException, XMLStreamException {
    this.out.writeStartElement(this.nsContext.getPrefix(paramInt), paramString, this.nsContext.getNamespaceURI(paramInt));
    NamespaceContextImpl.Element element = this.nsContext.getCurrent();
    if (element.count() > 0)
      for (int i = element.count() - 1; i >= 0; i--) {
        String str = element.getNsUri(i);
        if (str.length() != 0 || element.getBase() != 1)
          this.out.writeNamespace(element.getPrefix(i), str); 
      }  
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2) throws IOException, XMLStreamException {
    if (paramInt == -1) {
      this.out.writeAttribute(paramString1, paramString2);
    } else {
      this.out.writeAttribute(this.nsContext.getPrefix(paramInt), this.nsContext.getNamespaceURI(paramInt), paramString1, paramString2);
    } 
  }
  
  public void endStartTag() throws IOException, SAXException {}
  
  public void endTag(int paramInt, String paramString) throws IOException, XMLStreamException { this.out.writeEndElement(); }
  
  public void text(String paramString, boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    if (paramBoolean)
      this.out.writeCharacters(" "); 
    this.escapeHandler.escape(paramString.toCharArray(), 0, paramString.length(), false, this.writerWrapper);
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    if (paramBoolean)
      this.out.writeCharacters(" "); 
    int i = paramPcdata.length();
    if (i < this.buf.length) {
      paramPcdata.writeTo(this.buf, 0);
      this.out.writeCharacters(this.buf, 0, i);
    } else {
      this.out.writeCharacters(paramPcdata.toString());
    } 
  }
  
  private static Class initFIStAXWriterClass() {
    try {
      Class clazz1;
      Class clazz2 = (clazz1 = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.stax.LowLevelFastInfosetStreamWriter")).forName("com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer");
      return clazz1.isAssignableFrom(clazz2) ? clazz2 : null;
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  private static Constructor<? extends XmlOutput> initFastInfosetOutputClass() {
    try {
      if (FI_STAX_WRITER_CLASS == null)
        return null; 
      Class clazz = Class.forName("com.sun.xml.internal.bind.v2.runtime.output.FastInfosetStreamWriterOutput");
      return clazz.getConstructor(new Class[] { FI_STAX_WRITER_CLASS, JAXBContextImpl.class });
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  private static Class initStAXExWriterClass() {
    try {
      return Class.forName("com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx");
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  private static Constructor<? extends XmlOutput> initStAXExOutputClass() {
    try {
      Class clazz = Class.forName("com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput");
      return clazz.getConstructor(new Class[] { STAXEX_WRITER_CLASS });
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  private static final class XmlStreamOutWriterAdapter extends Writer {
    private final XMLStreamWriter writer;
    
    private XmlStreamOutWriterAdapter(XMLStreamWriter param1XMLStreamWriter) { this.writer = param1XMLStreamWriter; }
    
    public void write(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws IOException {
      try {
        this.writer.writeCharacters(param1ArrayOfChar, param1Int1, param1Int2);
      } catch (XMLStreamException xMLStreamException) {
        throw new IOException("Error writing XML stream", xMLStreamException);
      } 
    }
    
    public void writeEntityRef(String param1String) throws XMLStreamException { this.writer.writeEntityRef(param1String); }
    
    public void flush() throws IOException, SAXException {
      try {
        this.writer.flush();
      } catch (XMLStreamException xMLStreamException) {
        throw new IOException("Error flushing XML stream", xMLStreamException);
      } 
    }
    
    public void close() throws IOException, SAXException {
      try {
        this.writer.close();
      } catch (XMLStreamException xMLStreamException) {
        throw new IOException("Error closing XML stream", xMLStreamException);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\XMLStreamWriterOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */