package com.sun.xml.internal.ws.developer;

import java.lang.reflect.Constructor;
import java.net.CookieHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public final class HttpConfigFeature extends WebServiceFeature {
  public static final String ID = "http://jax-ws.java.net/features/http-config";
  
  private static final Constructor cookieManagerConstructor;
  
  private static final Object cookiePolicy;
  
  private final CookieHandler cookieJar;
  
  public HttpConfigFeature() { this(getInternalCookieHandler()); }
  
  public HttpConfigFeature(CookieHandler paramCookieHandler) { this.cookieJar = paramCookieHandler; }
  
  private static CookieHandler getInternalCookieHandler() {
    try {
      return (CookieHandler)cookieManagerConstructor.newInstance(new Object[] { null, cookiePolicy });
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
  }
  
  public String getID() { return "http://jax-ws.java.net/features/http-config"; }
  
  public CookieHandler getCookieHandler() { return this.cookieJar; }
  
  static  {
    Object object;
    Constructor constructor;
    try {
      Class clazz1;
      Class clazz2;
      constructor = (clazz2 = (clazz1 = Class.forName("java.net.CookiePolicy")).forName("java.net.CookieStore")).forName("java.net.CookieManager").getConstructor(new Class[] { clazz2, clazz1 });
      object = clazz1.getField("ACCEPT_ALL").get(null);
    } catch (Exception exception) {
      try {
        Class clazz1;
        Class clazz2;
        constructor = (clazz2 = (clazz1 = Class.forName("com.sun.xml.internal.ws.transport.http.client.CookiePolicy")).forName("com.sun.xml.internal.ws.transport.http.client.CookieStore")).forName("com.sun.xml.internal.ws.transport.http.client.CookieManager").getConstructor(new Class[] { clazz2, clazz1 });
        object = clazz1.getField("ACCEPT_ALL").get(null);
      } catch (Exception exception1) {
        throw new WebServiceException(exception1);
      } 
    } 
    cookieManagerConstructor = constructor;
    cookiePolicy = object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\HttpConfigFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */