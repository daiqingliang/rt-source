package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class FI_StAX_SAX_Or_XML_SAX_SAXEvent extends TransformInputOutput {
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream) throws Exception {
    if (!paramInputStream.markSupported())
      paramInputStream = new BufferedInputStream(paramInputStream); 
    paramInputStream.mark(4);
    boolean bool = Decoder.isFastInfosetDocument(paramInputStream);
    paramInputStream.reset();
    if (bool) {
      StAXDocumentParser stAXDocumentParser = new StAXDocumentParser();
      stAXDocumentParser.setInputStream(paramInputStream);
      SAXEventSerializer sAXEventSerializer = new SAXEventSerializer(paramOutputStream);
      StAX2SAXReader stAX2SAXReader = new StAX2SAXReader(stAXDocumentParser, sAXEventSerializer);
      stAX2SAXReader.setLexicalHandler(sAXEventSerializer);
      stAX2SAXReader.adapt();
    } else {
      SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
      sAXParserFactory.setNamespaceAware(true);
      SAXParser sAXParser = sAXParserFactory.newSAXParser();
      SAXEventSerializer sAXEventSerializer = new SAXEventSerializer(paramOutputStream);
      sAXParser.setProperty("http://xml.org/sax/properties/lexical-handler", sAXEventSerializer);
      sAXParser.parse(paramInputStream, sAXEventSerializer);
    } 
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    FI_StAX_SAX_Or_XML_SAX_SAXEvent fI_StAX_SAX_Or_XML_SAX_SAXEvent = new FI_StAX_SAX_Or_XML_SAX_SAXEvent();
    fI_StAX_SAX_Or_XML_SAX_SAXEvent.parse(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\FI_StAX_SAX_Or_XML_SAX_SAXEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */