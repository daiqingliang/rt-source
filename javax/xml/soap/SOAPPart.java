package javax.xml.soap;

import java.util.Iterator;
import javax.xml.transform.Source;
import org.w3c.dom.Document;

public abstract class SOAPPart implements Document, Node {
  public abstract SOAPEnvelope getEnvelope() throws SOAPException;
  
  public String getContentId() {
    String[] arrayOfString = getMimeHeader("Content-Id");
    return (arrayOfString != null && arrayOfString.length > 0) ? arrayOfString[0] : null;
  }
  
  public String getContentLocation() {
    String[] arrayOfString = getMimeHeader("Content-Location");
    return (arrayOfString != null && arrayOfString.length > 0) ? arrayOfString[0] : null;
  }
  
  public void setContentId(String paramString) { setMimeHeader("Content-Id", paramString); }
  
  public void setContentLocation(String paramString) { setMimeHeader("Content-Location", paramString); }
  
  public abstract void removeMimeHeader(String paramString);
  
  public abstract void removeAllMimeHeaders();
  
  public abstract String[] getMimeHeader(String paramString);
  
  public abstract void setMimeHeader(String paramString1, String paramString2);
  
  public abstract void addMimeHeader(String paramString1, String paramString2);
  
  public abstract Iterator getAllMimeHeaders();
  
  public abstract Iterator getMatchingMimeHeaders(String[] paramArrayOfString);
  
  public abstract Iterator getNonMatchingMimeHeaders(String[] paramArrayOfString);
  
  public abstract void setContent(Source paramSource) throws SOAPException;
  
  public abstract Source getContent() throws SOAPException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\SOAPPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */