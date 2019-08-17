package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent extends TransformInputOutput {
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString) throws Exception {
    if (!paramInputStream.markSupported())
      paramInputStream = new BufferedInputStream(paramInputStream); 
    paramInputStream.mark(4);
    boolean bool = Decoder.isFastInfosetDocument(paramInputStream);
    paramInputStream.reset();
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMResult dOMResult = new DOMResult();
    if (bool) {
      transformer.transform(new FastInfosetSource(paramInputStream), dOMResult);
    } else if (paramString != null) {
      SAXParser sAXParser = getParser();
      XMLReader xMLReader = sAXParser.getXMLReader();
      xMLReader.setEntityResolver(createRelativePathResolver(paramString));
      SAXSource sAXSource = new SAXSource(xMLReader, new InputSource(paramInputStream));
      transformer.transform(sAXSource, dOMResult);
    } else {
      transformer.transform(new StreamSource(paramInputStream), dOMResult);
    } 
    SAXEventSerializer sAXEventSerializer = new SAXEventSerializer(paramOutputStream);
    transformer.transform(new DOMSource(dOMResult.getNode()), new SAXResult(sAXEventSerializer));
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream) throws Exception { parse(paramInputStream, paramOutputStream, null); }
  
  private SAXParser getParser() {
    SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
    sAXParserFactory.setNamespaceAware(true);
    try {
      return sAXParserFactory.newSAXParser();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent fI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent = new FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent();
    fI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent.parse(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */