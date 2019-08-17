package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import javax.xml.ws.WebServiceException;

public final class EndpointAddress {
  @Nullable
  private URL url;
  
  private final URI uri;
  
  private final String stringForm;
  
  private Proxy proxy;
  
  public EndpointAddress(URI paramURI) {
    this.uri = paramURI;
    this.stringForm = paramURI.toString();
    try {
      initURL();
      this.proxy = chooseProxy();
    } catch (MalformedURLException malformedURLException) {}
  }
  
  public EndpointAddress(String paramString) throws URISyntaxException {
    this.uri = new URI(paramString);
    this.stringForm = paramString;
    try {
      initURL();
      this.proxy = chooseProxy();
    } catch (MalformedURLException malformedURLException) {}
  }
  
  private void initURL() throws MalformedURLException {
    String str = this.uri.getScheme();
    if (str == null) {
      this.url = new URL(this.uri.toString());
      return;
    } 
    str = str.toLowerCase();
    if ("http".equals(str) || "https".equals(str)) {
      this.url = new URL(this.uri.toASCIIString());
    } else {
      this.url = this.uri.toURL();
    } 
  }
  
  public static EndpointAddress create(String paramString) {
    try {
      return new EndpointAddress(paramString);
    } catch (URISyntaxException uRISyntaxException) {
      throw new WebServiceException("Illegal endpoint address: " + paramString, uRISyntaxException);
    } 
  }
  
  private Proxy chooseProxy() {
    ProxySelector proxySelector = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction<ProxySelector>() {
          public ProxySelector run() { return ProxySelector.getDefault(); }
        });
    if (proxySelector == null)
      return Proxy.NO_PROXY; 
    if (!proxySelector.getClass().getName().equals("sun.net.spi.DefaultProxySelector"))
      return null; 
    Iterator iterator = proxySelector.select(this.uri).iterator();
    return iterator.hasNext() ? (Proxy)iterator.next() : Proxy.NO_PROXY;
  }
  
  public URL getURL() { return this.url; }
  
  public URI getURI() { return this.uri; }
  
  public URLConnection openConnection() throws IOException {
    if (this.url == null)
      throw new WebServiceException("URI=" + this.uri + " doesn't have the corresponding URL"); 
    if (this.proxy != null && !this.dontUseProxyMethod)
      try {
        return this.url.openConnection(this.proxy);
      } catch (UnsupportedOperationException unsupportedOperationException) {
        this.dontUseProxyMethod = true;
      }  
    return this.url.openConnection();
  }
  
  public String toString() { return this.stringForm; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\EndpointAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */