package com.sun.xml.internal.ws.developer;

import com.sun.xml.internal.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

public class SerializationFeature extends WebServiceFeature {
  public static final String ID = "http://jax-ws.java.net/features/serialization";
  
  private final String encoding;
  
  public SerializationFeature() { this(""); }
  
  @FeatureConstructor({"encoding"})
  public SerializationFeature(String paramString) { this.encoding = paramString; }
  
  public String getID() { return "http://jax-ws.java.net/features/serialization"; }
  
  public String getEncoding() { return this.encoding; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\SerializationFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */