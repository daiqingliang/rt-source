package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.encoding.SOAPBindingCodec;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public abstract class BindingID {
  public static final SOAPHTTPImpl X_SOAP12_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/", true);
  
  public static final SOAPHTTPImpl SOAP12_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://www.w3.org/2003/05/soap/bindings/HTTP/", true);
  
  public static final SOAPHTTPImpl SOAP11_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_11, "http://schemas.xmlsoap.org/wsdl/soap/http", true);
  
  public static final SOAPHTTPImpl SOAP12_HTTP_MTOM = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true", true, true);
  
  public static final SOAPHTTPImpl SOAP11_HTTP_MTOM = new SOAPHTTPImpl(SOAPVersion.SOAP_11, "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true", true, true);
  
  public static final BindingID XML_HTTP = new Impl(SOAPVersion.SOAP_11, "http://www.w3.org/2004/08/wsdl/http", false) {
      public Codec createEncoder(WSBinding param1WSBinding) { return new XMLHTTPBindingCodec(param1WSBinding.getFeatures()); }
    };
  
  private static final BindingID REST_HTTP = new Impl(SOAPVersion.SOAP_11, "http://jax-ws.dev.java.net/rest", true) {
      public Codec createEncoder(WSBinding param1WSBinding) { return new XMLHTTPBindingCodec(param1WSBinding.getFeatures()); }
    };
  
  @NotNull
  public final WSBinding createBinding() { return BindingImpl.create(this); }
  
  @NotNull
  public String getTransport() { return "http://schemas.xmlsoap.org/soap/http"; }
  
  @NotNull
  public final WSBinding createBinding(WebServiceFeature... paramVarArgs) { return BindingImpl.create(this, paramVarArgs); }
  
  @NotNull
  public final WSBinding createBinding(WSFeatureList paramWSFeatureList) { return createBinding(paramWSFeatureList.toArray()); }
  
  public abstract SOAPVersion getSOAPVersion();
  
  @NotNull
  public abstract Codec createEncoder(@NotNull WSBinding paramWSBinding);
  
  public abstract String toString();
  
  public WebServiceFeatureList createBuiltinFeatureList() { return new WebServiceFeatureList(); }
  
  public boolean canGenerateWSDL() { return false; }
  
  public String getParameter(String paramString1, String paramString2) { return paramString2; }
  
  public boolean equals(Object paramObject) { return !(paramObject instanceof BindingID) ? false : toString().equals(paramObject.toString()); }
  
  public int hashCode() { return toString().hashCode(); }
  
  @NotNull
  public static BindingID parse(String paramString) {
    if (paramString.equals(XML_HTTP.toString()))
      return XML_HTTP; 
    if (paramString.equals(REST_HTTP.toString()))
      return REST_HTTP; 
    if (belongsTo(paramString, SOAP11_HTTP.toString()))
      return customize(paramString, SOAP11_HTTP); 
    if (belongsTo(paramString, SOAP12_HTTP.toString()))
      return customize(paramString, SOAP12_HTTP); 
    if (belongsTo(paramString, "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/"))
      return customize(paramString, X_SOAP12_HTTP); 
    for (BindingIDFactory bindingIDFactory : ServiceFinder.find(BindingIDFactory.class)) {
      BindingID bindingID = bindingIDFactory.parse(paramString);
      if (bindingID != null)
        return bindingID; 
    } 
    throw new WebServiceException("Wrong binding ID: " + paramString);
  }
  
  private static boolean belongsTo(String paramString1, String paramString2) { return (paramString1.equals(paramString2) || paramString1.startsWith(paramString2 + '?')); }
  
  private static SOAPHTTPImpl customize(String paramString, SOAPHTTPImpl paramSOAPHTTPImpl) {
    if (paramString.equals(paramSOAPHTTPImpl.toString()))
      return paramSOAPHTTPImpl; 
    SOAPHTTPImpl sOAPHTTPImpl = new SOAPHTTPImpl(paramSOAPHTTPImpl.getSOAPVersion(), paramString, paramSOAPHTTPImpl.canGenerateWSDL());
    try {
      if (paramString.indexOf('?') == -1)
        return sOAPHTTPImpl; 
      String str = URLDecoder.decode(paramString.substring(paramString.indexOf('?') + 1), "UTF-8");
      for (String str1 : str.split("&")) {
        int i = str1.indexOf('=');
        if (i < 0)
          throw new WebServiceException("Malformed binding ID (no '=' in " + str1 + ")"); 
        sOAPHTTPImpl.parameters.put(str1.substring(0, i), str1.substring(i + 1));
      } 
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new AssertionError(unsupportedEncodingException);
    } 
    return sOAPHTTPImpl;
  }
  
  @NotNull
  public static BindingID parse(Class<?> paramClass) {
    BindingType bindingType = (BindingType)paramClass.getAnnotation(BindingType.class);
    if (bindingType != null) {
      String str = bindingType.value();
      if (str.length() > 0)
        return parse(str); 
    } 
    return SOAP11_HTTP;
  }
  
  private static abstract class Impl extends BindingID {
    final SOAPVersion version;
    
    private final String lexical;
    
    private final boolean canGenerateWSDL;
    
    public Impl(SOAPVersion param1SOAPVersion, String param1String, boolean param1Boolean) {
      this.version = param1SOAPVersion;
      this.lexical = param1String;
      this.canGenerateWSDL = param1Boolean;
    }
    
    public SOAPVersion getSOAPVersion() { return this.version; }
    
    public String toString() { return this.lexical; }
    
    @Deprecated
    public boolean canGenerateWSDL() { return this.canGenerateWSDL; }
  }
  
  private static final class SOAPHTTPImpl extends Impl implements Cloneable {
    Map<String, String> parameters = new HashMap();
    
    static final String MTOM_PARAM = "mtom";
    
    public SOAPHTTPImpl(SOAPVersion param1SOAPVersion, String param1String, boolean param1Boolean) { super(param1SOAPVersion, param1String, param1Boolean); }
    
    public SOAPHTTPImpl(SOAPVersion param1SOAPVersion, String param1String, boolean param1Boolean1, boolean param1Boolean2) {
      this(param1SOAPVersion, param1String, param1Boolean1);
      String str = param1Boolean2 ? "true" : "false";
      this.parameters.put("mtom", str);
    }
    
    @NotNull
    public Codec createEncoder(WSBinding param1WSBinding) { return new SOAPBindingCodec(param1WSBinding.getFeatures()); }
    
    private Boolean isMTOMEnabled() {
      String str = (String)this.parameters.get("mtom");
      return (str == null) ? null : Boolean.valueOf(str);
    }
    
    public WebServiceFeatureList createBuiltinFeatureList() {
      WebServiceFeatureList webServiceFeatureList = super.createBuiltinFeatureList();
      Boolean bool = isMTOMEnabled();
      if (bool != null)
        webServiceFeatureList.add(new MTOMFeature(bool.booleanValue())); 
      return webServiceFeatureList;
    }
    
    public String getParameter(String param1String1, String param1String2) { return (this.parameters.get(param1String1) == null) ? super.getParameter(param1String1, param1String2) : (String)this.parameters.get(param1String1); }
    
    public SOAPHTTPImpl clone() throws CloneNotSupportedException { return (SOAPHTTPImpl)super.clone(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\BindingID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */