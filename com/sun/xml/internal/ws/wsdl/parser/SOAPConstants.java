package com.sun.xml.internal.ws.wsdl.parser;

import javax.xml.namespace.QName;

public interface SOAPConstants {
  public static final String URI_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
  
  public static final String URI_ENVELOPE12 = "http://www.w3.org/2003/05/soap-envelope";
  
  public static final String NS_WSDL_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
  
  public static final String NS_WSDL_SOAP12 = "http://schemas.xmlsoap.org/wsdl/soap12/";
  
  public static final String NS_SOAP_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
  
  public static final String URI_SOAP_TRANSPORT_HTTP = "http://schemas.xmlsoap.org/soap/http";
  
  public static final QName QNAME_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "address");
  
  public static final QName QNAME_SOAP12ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "address");
  
  public static final QName QNAME_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "binding");
  
  public static final QName QNAME_BODY = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "body");
  
  public static final QName QNAME_SOAP12BODY = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "body");
  
  public static final QName QNAME_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "fault");
  
  public static final QName QNAME_HEADER = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "header");
  
  public static final QName QNAME_SOAP12HEADER = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "header");
  
  public static final QName QNAME_HEADERFAULT = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "headerfault");
  
  public static final QName QNAME_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "operation");
  
  public static final QName QNAME_SOAP12OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "operation");
  
  public static final QName QNAME_MUSTUNDERSTAND = new QName("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstand");
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\SOAPConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */