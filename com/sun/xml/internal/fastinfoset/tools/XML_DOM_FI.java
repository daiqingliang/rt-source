package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.dom.DOMDocumentSerializer;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class XML_DOM_FI extends TransformInputOutput {
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString) throws Exception {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    if (paramString != null)
      documentBuilder.setEntityResolver(createRelativePathResolver(paramString)); 
    Document document = documentBuilder.parse(paramInputStream);
    DOMDocumentSerializer dOMDocumentSerializer = new DOMDocumentSerializer();
    dOMDocumentSerializer.setOutputStream(paramOutputStream);
    dOMDocumentSerializer.serialize(document);
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream) throws Exception { parse(paramInputStream, paramOutputStream, null); }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    XML_DOM_FI xML_DOM_FI = new XML_DOM_FI();
    xML_DOM_FI.parse(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\XML_DOM_FI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */