package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResolverLocalFilesystem extends ResourceResolverSpi {
  private static final int FILE_URI_LENGTH = "file:/".length();
  
  private static Logger log = Logger.getLogger(ResolverLocalFilesystem.class.getName());
  
  public boolean engineIsThreadSafe() { return true; }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext) throws ResourceResolverException {
    try {
      URI uRI = getNewURI(paramResourceResolverContext.uriToResolve, paramResourceResolverContext.baseUri);
      String str = translateUriToFilename(uRI.toString());
      FileInputStream fileInputStream = new FileInputStream(str);
      XMLSignatureInput xMLSignatureInput = new XMLSignatureInput(fileInputStream);
      xMLSignatureInput.setSourceURI(uRI.toString());
      return xMLSignatureInput;
    } catch (Exception exception) {
      throw new ResourceResolverException("generic.EmptyMessage", exception, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
    } 
  }
  
  private static String translateUriToFilename(String paramString) {
    String str = paramString.substring(FILE_URI_LENGTH);
    if (str.indexOf("%20") > -1) {
      int i = 0;
      int j = 0;
      StringBuilder stringBuilder = new StringBuilder(str.length());
      do {
        j = str.indexOf("%20", i);
        if (j == -1) {
          stringBuilder.append(str.substring(i));
        } else {
          stringBuilder.append(str.substring(i, j));
          stringBuilder.append(' ');
          i = j + 3;
        } 
      } while (j != -1);
      str = stringBuilder.toString();
    } 
    return (str.charAt(1) == ':') ? str : ("/" + str);
  }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext) {
    if (paramResourceResolverContext.uriToResolve == null)
      return false; 
    if (paramResourceResolverContext.uriToResolve.equals("") || paramResourceResolverContext.uriToResolve.charAt(0) == '#' || paramResourceResolverContext.uriToResolve.startsWith("http:"))
      return false; 
    try {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I was asked whether I can resolve " + paramResourceResolverContext.uriToResolve); 
      if (paramResourceResolverContext.uriToResolve.startsWith("file:") || paramResourceResolverContext.baseUri.startsWith("file:")) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "I state that I can resolve " + paramResourceResolverContext.uriToResolve); 
        return true;
      } 
    } catch (Exception exception) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, exception.getMessage(), exception); 
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "But I can't"); 
    return false;
  }
  
  private static URI getNewURI(String paramString1, String paramString2) throws URISyntaxException {
    URI uRI = null;
    if (paramString2 == null || "".equals(paramString2)) {
      uRI = new URI(paramString1);
    } else {
      uRI = (new URI(paramString2)).resolve(paramString1);
    } 
    return (uRI.getFragment() != null) ? new URI(uRI.getScheme(), uRI.getSchemeSpecificPart(), null) : uRI;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\resolver\implementations\ResolverLocalFilesystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */