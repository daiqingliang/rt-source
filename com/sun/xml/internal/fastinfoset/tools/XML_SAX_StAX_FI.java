package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XML_SAX_StAX_FI extends TransformInputOutput {
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString) throws Exception {
    StAXDocumentSerializer stAXDocumentSerializer = new StAXDocumentSerializer();
    stAXDocumentSerializer.setOutputStream(paramOutputStream);
    SAX2StAXWriter sAX2StAXWriter = new SAX2StAXWriter(stAXDocumentSerializer);
    SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
    sAXParserFactory.setNamespaceAware(true);
    SAXParser sAXParser = sAXParserFactory.newSAXParser();
    XMLReader xMLReader = sAXParser.getXMLReader();
    xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", sAX2StAXWriter);
    xMLReader.setContentHandler(sAX2StAXWriter);
    if (paramString != null)
      xMLReader.setEntityResolver(createRelativePathResolver(paramString)); 
    xMLReader.parse(new InputSource(paramInputStream));
    paramInputStream.close();
    paramOutputStream.close();
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream) throws Exception { parse(paramInputStream, paramOutputStream, null); }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    XML_SAX_StAX_FI xML_SAX_StAX_FI = new XML_SAX_StAX_FI();
    xML_SAX_StAX_FI.parse(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\XML_SAX_StAX_FI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */