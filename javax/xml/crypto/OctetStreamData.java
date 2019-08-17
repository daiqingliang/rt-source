package javax.xml.crypto;

import java.io.InputStream;

public class OctetStreamData implements Data {
  private InputStream octetStream;
  
  private String uri;
  
  private String mimeType;
  
  public OctetStreamData(InputStream paramInputStream) {
    if (paramInputStream == null)
      throw new NullPointerException("octetStream is null"); 
    this.octetStream = paramInputStream;
  }
  
  public OctetStreamData(InputStream paramInputStream, String paramString1, String paramString2) {
    if (paramInputStream == null)
      throw new NullPointerException("octetStream is null"); 
    this.octetStream = paramInputStream;
    this.uri = paramString1;
    this.mimeType = paramString2;
  }
  
  public InputStream getOctetStream() { return this.octetStream; }
  
  public String getURI() { return this.uri; }
  
  public String getMimeType() { return this.mimeType; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\OctetStreamData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */