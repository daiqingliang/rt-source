package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.sax.SAXDocumentParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FI_SAX_Or_XML_SAX_SAXEvent extends TransformInputOutput {
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString) throws Exception {
    if (!paramInputStream.markSupported())
      paramInputStream = new BufferedInputStream(paramInputStream); 
    paramInputStream.mark(4);
    boolean bool = Decoder.isFastInfosetDocument(paramInputStream);
    paramInputStream.reset();
    if (bool) {
      SAXDocumentParser sAXDocumentParser = new SAXDocumentParser();
      SAXEventSerializer sAXEventSerializer = new SAXEventSerializer(paramOutputStream);
      sAXDocumentParser.setContentHandler(sAXEventSerializer);
      sAXDocumentParser.setProperty("http://xml.org/sax/properties/lexical-handler", sAXEventSerializer);
      sAXDocumentParser.parse(paramInputStream);
    } else {
      SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
      sAXParserFactory.setNamespaceAware(true);
      SAXParser sAXParser = sAXParserFactory.newSAXParser();
      SAXEventSerializer sAXEventSerializer = new SAXEventSerializer(paramOutputStream);
      XMLReader xMLReader = sAXParser.getXMLReader();
      xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", sAXEventSerializer);
      xMLReader.setContentHandler(sAXEventSerializer);
      if (paramString != null)
        xMLReader.setEntityResolver(createRelativePathResolver(paramString)); 
      xMLReader.parse(new InputSource(paramInputStream));
    } 
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream) throws Exception { parse(paramInputStream, paramOutputStream, null); }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    FI_SAX_Or_XML_SAX_SAXEvent fI_SAX_Or_XML_SAX_SAXEvent = new FI_SAX_Or_XML_SAX_SAXEvent();
    fI_SAX_Or_XML_SAX_SAXEvent.parse(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\FI_SAX_Or_XML_SAX_SAXEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */