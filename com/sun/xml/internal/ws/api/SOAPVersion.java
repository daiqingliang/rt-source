package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public static enum SOAPVersion {
  SOAP_11("http://schemas.xmlsoap.org/wsdl/soap/http", "http://schemas.xmlsoap.org/soap/envelope/", "text/xml", "http://schemas.xmlsoap.org/soap/actor/next", "actor", "SOAP 1.1 Protocol", new QName("http://schemas.xmlsoap.org/soap/envelope/", "MustUnderstand"), "Client", "Server", Collections.singleton("http://schemas.xmlsoap.org/soap/actor/next")),
  SOAP_12("http://www.w3.org/2003/05/soap/bindings/HTTP/", "http://www.w3.org/2003/05/soap-envelope", "application/soap+xml", "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver", "role", "SOAP 1.2 Protocol", new QName("http://www.w3.org/2003/05/soap-envelope", "MustUnderstand"), "Sender", "Receiver", new HashSet(Arrays.asList(new String[] { "http://www.w3.org/2003/05/soap-envelope/role/next", "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver" })));
  
  public final String httpBindingId;
  
  public final String nsUri;
  
  public final String contentType;
  
  public final QName faultCodeMustUnderstand;
  
  public final MessageFactory saajMessageFactory;
  
  public final SOAPFactory saajSoapFactory;
  
  private final String saajFactoryString;
  
  public final String implicitRole;
  
  public final Set<String> implicitRoleSet;
  
  public final Set<String> requiredRoles;
  
  public final String roleAttributeName;
  
  public final QName faultCodeClient;
  
  public final QName faultCodeServer;
  
  SOAPVersion(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, QName paramQName, String paramString6, String paramString7, Set<String> paramSet1, Set paramSet2) {
    this.httpBindingId = paramString1;
    this.nsUri = paramString2;
    this.contentType = paramString3;
    this.implicitRole = paramString4;
    this.implicitRoleSet = Collections.singleton(paramString4);
    this.roleAttributeName = paramString5;
    this.saajFactoryString = paramQName;
    try {
      this.saajMessageFactory = MessageFactory.newInstance(paramQName);
      this.saajSoapFactory = SOAPFactory.newInstance(paramQName);
    } catch (SOAPException sOAPException) {
      throw new Error(sOAPException);
    } catch (NoSuchMethodError noSuchMethodError) {
      LinkageError linkageError = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
      linkageError.initCause(noSuchMethodError);
      throw linkageError;
    } 
    this.faultCodeMustUnderstand = paramString6;
    this.requiredRoles = paramSet2;
    this.faultCodeClient = new QName(paramString2, paramString7);
    this.faultCodeServer = new QName(paramString2, paramSet1);
  }
  
  public SOAPFactory getSOAPFactory() {
    try {
      return SAAJFactory.getSOAPFactory(this.saajFactoryString);
    } catch (SOAPException sOAPException) {
      throw new Error(sOAPException);
    } catch (NoSuchMethodError noSuchMethodError) {
      LinkageError linkageError = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
      linkageError.initCause(noSuchMethodError);
      throw linkageError;
    } 
  }
  
  public MessageFactory getMessageFactory() {
    try {
      return SAAJFactory.getMessageFactory(this.saajFactoryString);
    } catch (SOAPException sOAPException) {
      throw new Error(sOAPException);
    } catch (NoSuchMethodError noSuchMethodError) {
      LinkageError linkageError = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
      linkageError.initCause(noSuchMethodError);
      throw linkageError;
    } 
  }
  
  public String toString() { return this.httpBindingId; }
  
  public static SOAPVersion fromHttpBinding(String paramString) { return (paramString == null) ? SOAP_11 : (paramString.equals(SOAP_12.httpBindingId) ? SOAP_12 : SOAP_11); }
  
  public static SOAPVersion fromNsUri(String paramString) { return paramString.equals(SOAP_12.nsUri) ? SOAP_12 : SOAP_11; }
  
  public static SOAPVersion from(EnvelopeStyleFeature paramEnvelopeStyleFeature) {
    EnvelopeStyle.Style[] arrayOfStyle = paramEnvelopeStyleFeature.getStyles();
    if (arrayOfStyle.length != 1)
      throw new IllegalArgumentException("The EnvelopingFeature must has exactly one Enveloping.Style"); 
    return from(arrayOfStyle[0]);
  }
  
  public static SOAPVersion from(EnvelopeStyle.Style paramStyle) {
    switch (paramStyle) {
      case SOAP11:
        return SOAP_11;
      case SOAP12:
        return SOAP_12;
    } 
    return SOAP_11;
  }
  
  public EnvelopeStyleFeature toFeature() { return SOAP_11.equals(this) ? new EnvelopeStyleFeature(new EnvelopeStyle.Style[] { EnvelopeStyle.Style.SOAP11 }) : new EnvelopeStyleFeature(new EnvelopeStyle.Style[] { EnvelopeStyle.Style.SOAP12 }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\SOAPVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */