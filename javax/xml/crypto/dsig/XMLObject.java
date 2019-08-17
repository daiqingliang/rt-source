package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface XMLObject extends XMLStructure {
  public static final String TYPE = "http://www.w3.org/2000/09/xmldsig#Object";
  
  List getContent();
  
  String getId();
  
  String getMimeType();
  
  String getEncoding();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\XMLObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */