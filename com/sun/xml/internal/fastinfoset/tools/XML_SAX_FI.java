package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.sax.SAXDocumentSerializer;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XML_SAX_FI extends TransformInputOutput {
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString) throws Exception {
    SAXParser sAXParser = getParser();
    SAXDocumentSerializer sAXDocumentSerializer = getSerializer(paramOutputStream);
    XMLReader xMLReader = sAXParser.getXMLReader();
    xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", sAXDocumentSerializer);
    xMLReader.setContentHandler(sAXDocumentSerializer);
    if (paramString != null)
      xMLReader.setEntityResolver(createRelativePathResolver(paramString)); 
    xMLReader.parse(new InputSource(paramInputStream));
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream) throws Exception { parse(paramInputStream, paramOutputStream, null); }
  
  public void convert(Reader paramReader, OutputStream paramOutputStream) throws Exception {
    InputSource inputSource = new InputSource(paramReader);
    SAXParser sAXParser = getParser();
    SAXDocumentSerializer sAXDocumentSerializer = getSerializer(paramOutputStream);
    sAXParser.setProperty("http://xml.org/sax/properties/lexical-handler", sAXDocumentSerializer);
    sAXParser.parse(inputSource, sAXDocumentSerializer);
  }
  
  private SAXParser getParser() {
    SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
    sAXParserFactory.setNamespaceAware(true);
    try {
      return sAXParserFactory.newSAXParser();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  private SAXDocumentSerializer getSerializer(OutputStream paramOutputStream) {
    SAXDocumentSerializer sAXDocumentSerializer = new SAXDocumentSerializer();
    sAXDocumentSerializer.setOutputStream(paramOutputStream);
    return sAXDocumentSerializer;
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    XML_SAX_FI xML_SAX_FI = new XML_SAX_FI();
    xML_SAX_FI.parse(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\XML_SAX_FI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */