package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResolverDirectHTTP extends ResourceResolverSpi {
  private static Logger log = Logger.getLogger(ResolverDirectHTTP.class.getName());
  
  private static final String[] properties = { "http.proxy.host", "http.proxy.port", "http.proxy.username", "http.proxy.password", "http.basic.username", "http.basic.password" };
  
  private static final int HttpProxyHost = 0;
  
  private static final int HttpProxyPort = 1;
  
  private static final int HttpProxyUser = 2;
  
  private static final int HttpProxyPass = 3;
  
  private static final int HttpBasicUser = 4;
  
  private static final int HttpBasicPass = 5;
  
  public boolean engineIsThreadSafe() { return true; }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext) throws ResourceResolverException {
    try {
      URI uRI = getNewURI(paramResourceResolverContext.uriToResolve, paramResourceResolverContext.baseUri);
      URL uRL = uRI.toURL();
      URLConnection uRLConnection = openConnection(uRL);
      String str1 = uRLConnection.getHeaderField("WWW-Authenticate");
      if (str1 != null && str1.startsWith("Basic")) {
        String str3 = engineGetProperty(properties[4]);
        String str4 = engineGetProperty(properties[5]);
        if (str3 != null && str4 != null) {
          uRLConnection = openConnection(uRL);
          String str5 = str3 + ":" + str4;
          String str6 = Base64.encode(str5.getBytes("ISO-8859-1"));
          uRLConnection.setRequestProperty("Authorization", "Basic " + str6);
        } 
      } 
      String str2 = uRLConnection.getHeaderField("Content-Type");
      InputStream inputStream = uRLConnection.getInputStream();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] arrayOfByte = new byte[4096];
      int i = 0;
      int j;
      for (j = 0; (i = inputStream.read(arrayOfByte)) >= 0; j += i)
        byteArrayOutputStream.write(arrayOfByte, 0, i); 
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Fetched " + j + " bytes from URI " + uRI.toString()); 
      XMLSignatureInput xMLSignatureInput = new XMLSignatureInput(byteArrayOutputStream.toByteArray());
      xMLSignatureInput.setSourceURI(uRI.toString());
      xMLSignatureInput.setMIMEType(str2);
      return xMLSignatureInput;
    } catch (URISyntaxException uRISyntaxException) {
      throw new ResourceResolverException("generic.EmptyMessage", uRISyntaxException, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
    } catch (MalformedURLException malformedURLException) {
      throw new ResourceResolverException("generic.EmptyMessage", malformedURLException, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
    } catch (IOException iOException) {
      throw new ResourceResolverException("generic.EmptyMessage", iOException, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ResourceResolverException("generic.EmptyMessage", illegalArgumentException, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
    } 
  }
  
  private URLConnection openConnection(URL paramURL) throws IOException {
    URLConnection uRLConnection;
    String str1 = engineGetProperty(properties[0]);
    String str2 = engineGetProperty(properties[1]);
    String str3 = engineGetProperty(properties[2]);
    String str4 = engineGetProperty(properties[3]);
    Proxy proxy = null;
    if (str1 != null && str2 != null) {
      int i = Integer.parseInt(str2);
      proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(str1, i));
    } 
    if (proxy != null) {
      uRLConnection = paramURL.openConnection(proxy);
      if (str3 != null && str4 != null) {
        String str5 = str3 + ":" + str4;
        String str6 = "Basic " + Base64.encode(str5.getBytes("ISO-8859-1"));
        uRLConnection.setRequestProperty("Proxy-Authorization", str6);
      } 
    } else {
      uRLConnection = paramURL.openConnection();
    } 
    return uRLConnection;
  }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext) {
    if (paramResourceResolverContext.uriToResolve == null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "quick fail, uri == null"); 
      return false;
    } 
    if (paramResourceResolverContext.uriToResolve.equals("") || paramResourceResolverContext.uriToResolve.charAt(0) == '#') {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "quick fail for empty URIs and local ones"); 
      return false;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I was asked whether I can resolve " + paramResourceResolverContext.uriToResolve); 
    if (paramResourceResolverContext.uriToResolve.startsWith("http:") || (paramResourceResolverContext.baseUri != null && paramResourceResolverContext.baseUri.startsWith("http:"))) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I state that I can resolve " + paramResourceResolverContext.uriToResolve); 
      return true;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I state that I can't resolve " + paramResourceResolverContext.uriToResolve); 
    return false;
  }
  
  public String[] engineGetPropertyKeys() { return (String[])properties.clone(); }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\resolver\implementations\ResolverDirectHTTP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */