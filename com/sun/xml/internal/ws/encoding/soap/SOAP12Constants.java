package com.sun.xml.internal.ws.encoding.soap;

import javax.xml.namespace.QName;

public class SOAP12Constants {
  public static final String URI_ENVELOPE = "http://www.w3.org/2003/05/soap-envelope";
  
  public static final String URI_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
  
  public static final String URI_HTTP = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
  
  public static final String URI_SOAP_RPC = "http://www.w3.org/2002/06/soap-rpc";
  
  public static final QName QNAME_SOAP_RPC = new QName("http://www.w3.org/2002/06/soap-rpc", "rpc");
  
  public static final QName QNAME_SOAP_RESULT = new QName("http://www.w3.org/2002/06/soap-rpc", "result");
  
  public static final QName QNAME_SOAP_ENVELOPE = new QName("http://www.w3.org/2003/05/soap-envelope", "Envelope");
  
  public static final QName QNAME_SOAP_BODY = new QName("http://www.w3.org/2003/05/soap-envelope", "Body");
  
  public static final QName QNAME_SOAP_HEADER = new QName("http://www.w3.org/2003/05/soap-envelope", "Header");
  
  public static final QName QNAME_ENVELOPE_ENCODINGSTYLE = new QName("http://www.w3.org/2003/05/soap-envelope", "encodingStyle");
  
  public static final QName QNAME_SOAP_FAULT = new QName("http://www.w3.org/2003/05/soap-envelope", "Fault");
  
  public static final QName QNAME_MUSTUNDERSTAND = new QName("http://www.w3.org/2003/05/soap-envelope", "mustUnderstand");
  
  public static final QName QNAME_ROLE = new QName("http://www.w3.org/2003/05/soap-envelope", "role");
  
  public static final QName QNAME_NOT_UNDERSTOOD = new QName("http://www.w3.org/2003/05/soap-envelope", "NotUnderstood");
  
  public static final QName QNAME_FAULT_CODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Code");
  
  public static final QName QNAME_FAULT_SUBCODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode");
  
  public static final QName QNAME_FAULT_VALUE = new QName("http://www.w3.org/2003/05/soap-envelope", "Value");
  
  public static final QName QNAME_FAULT_REASON = new QName("http://www.w3.org/2003/05/soap-envelope", "Reason");
  
  public static final QName QNAME_FAULT_NODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Node");
  
  public static final QName QNAME_FAULT_ROLE = new QName("http://www.w3.org/2003/05/soap-envelope", "Role");
  
  public static final QName QNAME_FAULT_DETAIL = new QName("http://www.w3.org/2003/05/soap-envelope", "Detail");
  
  public static final QName QNAME_FAULT_REASON_TEXT = new QName("http://www.w3.org/2003/05/soap-envelope", "Text");
  
  public static final QName QNAME_UPGRADE = new QName("http://www.w3.org/2003/05/soap-envelope", "Upgrade");
  
  public static final QName QNAME_UPGRADE_SUPPORTED_ENVELOPE = new QName("http://www.w3.org/2003/05/soap-envelope", "SupportedEnvelope");
  
  public static final QName FAULT_CODE_MUST_UNDERSTAND = new QName("http://www.w3.org/2003/05/soap-envelope", "MustUnderstand");
  
  public static final QName FAULT_CODE_MISUNDERSTOOD = new QName("http://www.w3.org/2003/05/soap-envelope", "Misunderstood");
  
  public static final QName FAULT_CODE_VERSION_MISMATCH = new QName("http://www.w3.org/2003/05/soap-envelope", "VersionMismatch");
  
  public static final QName FAULT_CODE_DATA_ENCODING_UNKNOWN = new QName("http://www.w3.org/2003/05/soap-envelope", "DataEncodingUnknown");
  
  public static final QName FAULT_CODE_PROCEDURE_NOT_PRESENT = new QName("http://www.w3.org/2003/05/soap-envelope", "ProcedureNotPresent");
  
  public static final QName FAULT_CODE_BAD_ARGUMENTS = new QName("http://www.w3.org/2003/05/soap-envelope", "BadArguments");
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\soap\SOAP12Constants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */