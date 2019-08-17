package java.security;

import java.net.URI;
import javax.security.auth.login.Configuration;

public class URIParameter implements Policy.Parameters, Configuration.Parameters {
  private URI uri;
  
  public URIParameter(URI paramURI) {
    if (paramURI == null)
      throw new NullPointerException("invalid null URI"); 
    this.uri = paramURI;
  }
  
  public URI getURI() { return this.uri; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\URIParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */