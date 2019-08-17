package com.sun.xml.internal.ws.wsdl.parser;

import javax.xml.namespace.QName;

public interface WSDLConstants {
  public static final String PREFIX_NS_WSDL = "wsdl";
  
  public static final String NS_XMLNS = "http://www.w3.org/2001/XMLSchema";
  
  public static final String NS_WSDL = "http://schemas.xmlsoap.org/wsdl/";
  
  public static final String NS_SOAP11_HTTP_BINDING = "http://schemas.xmlsoap.org/soap/http";
  
  public static final QName QNAME_SCHEMA = new QName("http://www.w3.org/2001/XMLSchema", "schema");
  
  public static final QName QNAME_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/", "binding");
  
  public static final QName QNAME_DEFINITIONS = new QName("http://schemas.xmlsoap.org/wsdl/", "definitions");
  
  public static final QName QNAME_DOCUMENTATION = new QName("http://schemas.xmlsoap.org/wsdl/", "documentation");
  
  public static final QName NS_SOAP_BINDING_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "address");
  
  public static final QName NS_SOAP_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "binding");
  
  public static final QName NS_SOAP12_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "binding");
  
  public static final QName NS_SOAP12_BINDING_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "address");
  
  public static final QName QNAME_IMPORT = new QName("http://schemas.xmlsoap.org/wsdl/", "import");
  
  public static final QName QNAME_MESSAGE = new QName("http://schemas.xmlsoap.org/wsdl/", "message");
  
  public static final QName QNAME_PART = new QName("http://schemas.xmlsoap.org/wsdl/", "part");
  
  public static final QName QNAME_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/", "operation");
  
  public static final QName QNAME_INPUT = new QName("http://schemas.xmlsoap.org/wsdl/", "input");
  
  public static final QName QNAME_OUTPUT = new QName("http://schemas.xmlsoap.org/wsdl/", "output");
  
  public static final QName QNAME_PORT = new QName("http://schemas.xmlsoap.org/wsdl/", "port");
  
  public static final QName QNAME_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/", "address");
  
  public static final QName QNAME_PORT_TYPE = new QName("http://schemas.xmlsoap.org/wsdl/", "portType");
  
  public static final QName QNAME_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/", "fault");
  
  public static final QName QNAME_SERVICE = new QName("http://schemas.xmlsoap.org/wsdl/", "service");
  
  public static final QName QNAME_TYPES = new QName("http://schemas.xmlsoap.org/wsdl/", "types");
  
  public static final String ATTR_TRANSPORT = "transport";
  
  public static final String ATTR_LOCATION = "location";
  
  public static final String ATTR_NAME = "name";
  
  public static final String ATTR_TNS = "targetNamespace";
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\WSDLConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */