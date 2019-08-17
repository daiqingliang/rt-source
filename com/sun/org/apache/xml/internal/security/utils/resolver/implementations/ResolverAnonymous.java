package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ResolverAnonymous extends ResourceResolverSpi {
  private InputStream inStream = null;
  
  public boolean engineIsThreadSafe() { return true; }
  
  public ResolverAnonymous(String paramString) throws FileNotFoundException, IOException { this.inStream = new FileInputStream(paramString); }
  
  public ResolverAnonymous(InputStream paramInputStream) { this.inStream = paramInputStream; }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext) { return new XMLSignatureInput(this.inStream); }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext) { return (paramResourceResolverContext.uriToResolve == null); }
  
  public String[] engineGetPropertyKeys() { return new String[0]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\resolver\implementations\ResolverAnonymous.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */