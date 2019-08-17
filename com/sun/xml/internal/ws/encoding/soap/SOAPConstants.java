package com.sun.xml.internal.ws.encoding.soap;

import javax.xml.namespace.QName;

public class SOAPConstants {
  public static final String URI_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
  
  public static final String URI_HTTP = "http://schemas.xmlsoap.org/soap/http";
  
  public static final String URI_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
  
  public static final String NS_WSDL_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
  
  public static final QName QNAME_ENVELOPE_ENCODINGSTYLE = new QName("http://schemas.xmlsoap.org/soap/envelope/", "encodingStyle");
  
  public static final QName QNAME_SOAP_ENVELOPE = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
  
  public static final QName QNAME_SOAP_HEADER = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Header");
  
  public static final QName QNAME_MUSTUNDERSTAND = new QName("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstand");
  
  public static final QName QNAME_ROLE = new QName("http://schemas.xmlsoap.org/soap/envelope/", "actor");
  
  public static final QName QNAME_SOAP_BODY = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body");
  
  public static final QName QNAME_SOAP_FAULT = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
  
  public static final QName QNAME_SOAP_FAULT_CODE = new QName("", "faultcode");
  
  public static final QName QNAME_SOAP_FAULT_STRING = new QName("", "faultstring");
  
  public static final QName QNAME_SOAP_FAULT_ACTOR = new QName("", "faultactor");
  
  public static final QName QNAME_SOAP_FAULT_DETAIL = new QName("", "detail");
  
  public static final QName FAULT_CODE_MUST_UNDERSTAND = new QName("http://schemas.xmlsoap.org/soap/envelope/", "MustUnderstand");
  
  public static final QName FAULT_CODE_VERSION_MISMATCH = new QName("http://schemas.xmlsoap.org/soap/envelope/", "VersionMismatch");
  
  public static final QName FAULT_CODE_DATA_ENCODING_UNKNOWN = new QName("http://schemas.xmlsoap.org/soap/envelope/", "DataEncodingUnknown");
  
  public static final QName FAULT_CODE_PROCEDURE_NOT_PRESENT = new QName("http://schemas.xmlsoap.org/soap/envelope/", "ProcedureNotPresent");
  
  public static final QName FAULT_CODE_BAD_ARGUMENTS = new QName("http://schemas.xmlsoap.org/soap/envelope/", "BadArguments");
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\soap\SOAPConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */