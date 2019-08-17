package sun.security.x509;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class URIName implements GeneralNameInterface {
  private URI uri;
  
  private String host;
  
  private DNSName hostDNS;
  
  private IPAddressName hostIP;
  
  public URIName(DerValue paramDerValue) throws IOException { this(paramDerValue.getIA5String()); }
  
  public URIName(String paramString) throws IOException {
    try {
      this.uri = new URI(paramString);
    } catch (URISyntaxException uRISyntaxException) {
      throw new IOException("invalid URI name:" + paramString, uRISyntaxException);
    } 
    if (this.uri.getScheme() == null)
      throw new IOException("URI name must include scheme:" + paramString); 
    this.host = this.uri.getHost();
    if (this.host != null)
      if (this.host.charAt(0) == '[') {
        String str = this.host.substring(1, this.host.length() - 1);
        try {
          this.hostIP = new IPAddressName(str);
        } catch (IOException iOException) {
          throw new IOException("invalid URI name (host portion is not a valid IPv6 address):" + paramString);
        } 
      } else {
        try {
          this.hostDNS = new DNSName(this.host);
        } catch (IOException iOException) {
          try {
            this.hostIP = new IPAddressName(this.host);
          } catch (Exception exception) {
            throw new IOException("invalid URI name (host portion is not a valid DNS name, IPv4 address, or IPv6 address):" + paramString);
          } 
        } 
      }  
  }
  
  public static URIName nameConstraint(DerValue paramDerValue) throws IOException {
    URI uRI;
    String str = paramDerValue.getIA5String();
    try {
      uRI = new URI(str);
    } catch (URISyntaxException uRISyntaxException) {
      throw new IOException("invalid URI name constraint:" + str, uRISyntaxException);
    } 
    if (uRI.getScheme() == null) {
      String str1 = uRI.getSchemeSpecificPart();
      try {
        DNSName dNSName;
        if (str1.startsWith(".")) {
          dNSName = new DNSName(str1.substring(1));
        } else {
          dNSName = new DNSName(str1);
        } 
        return new URIName(uRI, str1, dNSName);
      } catch (IOException iOException) {
        throw new IOException("invalid URI name constraint:" + str, iOException);
      } 
    } 
    throw new IOException("invalid URI name constraint (should not include scheme):" + str);
  }
  
  URIName(URI paramURI, String paramString, DNSName paramDNSName) {
    this.uri = paramURI;
    this.host = paramString;
    this.hostDNS = paramDNSName;
  }
  
  public int getType() { return 6; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException { paramDerOutputStream.putIA5String(this.uri.toASCIIString()); }
  
  public String toString() { return "URIName: " + this.uri.toString(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof URIName))
      return false; 
    URIName uRIName = (URIName)paramObject;
    return this.uri.equals(uRIName.getURI());
  }
  
  public URI getURI() { return this.uri; }
  
  public String getName() { return this.uri.toString(); }
  
  public String getScheme() { return this.uri.getScheme(); }
  
  public String getHost() { return this.host; }
  
  public Object getHostObject() { return (this.hostIP != null) ? this.hostIP : this.hostDNS; }
  
  public int hashCode() { return this.uri.hashCode(); }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface) throws UnsupportedOperationException {
    int i;
    if (paramGeneralNameInterface == null) {
      i = -1;
    } else if (paramGeneralNameInterface.getType() != 6) {
      i = -1;
    } else {
      String str = ((URIName)paramGeneralNameInterface).getHost();
      if (str.equalsIgnoreCase(this.host)) {
        i = 0;
      } else {
        Object object = ((URIName)paramGeneralNameInterface).getHostObject();
        if (this.hostDNS == null || !(object instanceof DNSName)) {
          i = 3;
        } else {
          boolean bool1 = (this.host.charAt(0) == '.') ? 1 : 0;
          boolean bool2 = (str.charAt(0) == '.') ? 1 : 0;
          DNSName dNSName = (DNSName)object;
          i = this.hostDNS.constrains(dNSName);
          if (!bool1 && !bool2 && (i == 2 || i == 1))
            i = 3; 
          if (bool1 != bool2 && i == 0)
            if (bool1) {
              i = 2;
            } else {
              i = 1;
            }  
        } 
      } 
    } 
    return i;
  }
  
  public int subtreeDepth() {
    DNSName dNSName = null;
    try {
      dNSName = new DNSName(this.host);
    } catch (IOException iOException) {
      throw new UnsupportedOperationException(iOException.getMessage());
    } 
    return dNSName.subtreeDepth();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\URIName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */