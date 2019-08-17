package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class FI_SAX_XML extends TransformInputOutput {
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream) throws Exception {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.transform(new FastInfosetSource(paramInputStream), new StreamResult(paramOutputStream));
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    FI_SAX_XML fI_SAX_XML = new FI_SAX_XML();
    fI_SAX_XML.parse(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\FI_SAX_XML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */