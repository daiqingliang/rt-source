package com.sun.xml.internal.messaging.saaj.soap.name;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import org.w3c.dom.Element;

public class NameImpl implements Name {
  public static final String XML_NAMESPACE_PREFIX = "xml";
  
  public static final String XML_SCHEMA_NAMESPACE_PREFIX = "xs";
  
  public static final String SOAP_ENVELOPE_PREFIX = "SOAP-ENV";
  
  public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
  
  public static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
  
  public static final String SOAP12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";
  
  public static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
  
  protected String uri = "";
  
  protected String localName = "";
  
  protected String prefix = "";
  
  private String qualifiedName = null;
  
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.name", "com.sun.xml.internal.messaging.saaj.soap.name.LocalStrings");
  
  public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();
  
  protected NameImpl(String paramString) { this.localName = (paramString == null) ? "" : paramString; }
  
  protected NameImpl(String paramString1, String paramString2, String paramString3) {
    this.uri = (paramString3 == null) ? "" : paramString3;
    this.localName = (paramString1 == null) ? "" : paramString1;
    this.prefix = (paramString2 == null) ? "" : paramString2;
    if (this.prefix.equals("xmlns") && this.uri.equals(""))
      this.uri = XMLNS_URI; 
    if (this.uri.equals(XMLNS_URI) && this.prefix.equals(""))
      this.prefix = "xmlns"; 
  }
  
  public static Name convertToName(QName paramQName) { return new NameImpl(paramQName.getLocalPart(), paramQName.getPrefix(), paramQName.getNamespaceURI()); }
  
  public static QName convertToQName(Name paramName) { return new QName(paramName.getURI(), paramName.getLocalName(), paramName.getPrefix()); }
  
  public static NameImpl createFromUnqualifiedName(String paramString) { return new NameImpl(paramString); }
  
  public static Name createFromTagName(String paramString) { return createFromTagAndUri(paramString, ""); }
  
  public static Name createFromQualifiedName(String paramString1, String paramString2) { return createFromTagAndUri(paramString1, paramString2); }
  
  protected static Name createFromTagAndUri(String paramString1, String paramString2) {
    if (paramString1 == null) {
      log.severe("SAAJ0201.name.not.created.from.null.tag");
      throw new IllegalArgumentException("Cannot create a name from a null tag.");
    } 
    int i = paramString1.indexOf(':');
    return (i < 0) ? new NameImpl(paramString1, "", paramString2) : new NameImpl(paramString1.substring(i + 1), paramString1.substring(0, i), paramString2);
  }
  
  protected static int getPrefixSeparatorIndex(String paramString) {
    int i = paramString.indexOf(':');
    if (i < 0) {
      log.log(Level.SEVERE, "SAAJ0202.name.invalid.arg.format", new String[] { paramString });
      throw new IllegalArgumentException("Argument \"" + paramString + "\" must be of the form: \"prefix:localName\"");
    } 
    return i;
  }
  
  public static String getPrefixFromQualifiedName(String paramString) { return paramString.substring(0, getPrefixSeparatorIndex(paramString)); }
  
  public static String getLocalNameFromQualifiedName(String paramString) { return paramString.substring(getPrefixSeparatorIndex(paramString) + 1); }
  
  public static String getPrefixFromTagName(String paramString) { return isQualified(paramString) ? getPrefixFromQualifiedName(paramString) : ""; }
  
  public static String getLocalNameFromTagName(String paramString) { return isQualified(paramString) ? getLocalNameFromQualifiedName(paramString) : paramString; }
  
  public static boolean isQualified(String paramString) { return (paramString.indexOf(':') >= 0); }
  
  public static NameImpl create(String paramString1, String paramString2, String paramString3) {
    if (paramString2 == null)
      paramString2 = ""; 
    if (paramString3 == null)
      paramString3 = ""; 
    if (paramString1 == null)
      paramString1 = ""; 
    if (!paramString3.equals("") && !paramString1.equals("")) {
      if (paramString3.equals("http://schemas.xmlsoap.org/soap/envelope/"))
        return paramString1.equalsIgnoreCase("Envelope") ? createEnvelope1_1Name(paramString2) : (paramString1.equalsIgnoreCase("Header") ? createHeader1_1Name(paramString2) : (paramString1.equalsIgnoreCase("Body") ? createBody1_1Name(paramString2) : (paramString1.equalsIgnoreCase("Fault") ? createFault1_1Name(paramString2) : new SOAP1_1Name(paramString1, paramString2)))); 
      if (paramString3.equals("http://www.w3.org/2003/05/soap-envelope"))
        return paramString1.equalsIgnoreCase("Envelope") ? createEnvelope1_2Name(paramString2) : (paramString1.equalsIgnoreCase("Header") ? createHeader1_2Name(paramString2) : (paramString1.equalsIgnoreCase("Body") ? createBody1_2Name(paramString2) : ((paramString1.equals("Fault") || paramString1.equals("Reason") || paramString1.equals("Detail")) ? createFault1_2Name(paramString1, paramString2) : ((paramString1.equals("Code") || paramString1.equals("Subcode")) ? createCodeSubcode1_2Name(paramString2, paramString1) : new SOAP1_2Name(paramString1, paramString2))))); 
    } 
    return new NameImpl(paramString1, paramString2, paramString3);
  }
  
  public static String createQName(String paramString1, String paramString2) { return (paramString1 == null || paramString1.equals("")) ? paramString2 : (paramString1 + ":" + paramString2); }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Name))
      return false; 
    Name name = (Name)paramObject;
    return !this.uri.equals(name.getURI()) ? false : (!!this.localName.equals(name.getLocalName()));
  }
  
  public int hashCode() { return this.localName.hashCode(); }
  
  public String getLocalName() { return this.localName; }
  
  public String getPrefix() { return this.prefix; }
  
  public String getURI() { return this.uri; }
  
  public String getQualifiedName() {
    if (this.qualifiedName == null)
      if (this.prefix != null && this.prefix.length() > 0) {
        this.qualifiedName = this.prefix + ":" + this.localName;
      } else {
        this.qualifiedName = this.localName;
      }  
    return this.qualifiedName;
  }
  
  public static NameImpl createEnvelope1_1Name(String paramString) { return new Envelope1_1Name(paramString); }
  
  public static NameImpl createEnvelope1_2Name(String paramString) { return new Envelope1_2Name(paramString); }
  
  public static NameImpl createHeader1_1Name(String paramString) { return new Header1_1Name(paramString); }
  
  public static NameImpl createHeader1_2Name(String paramString) { return new Header1_2Name(paramString); }
  
  public static NameImpl createBody1_1Name(String paramString) { return new Body1_1Name(paramString); }
  
  public static NameImpl createBody1_2Name(String paramString) { return new Body1_2Name(paramString); }
  
  public static NameImpl createFault1_1Name(String paramString) { return new Fault1_1Name(paramString); }
  
  public static NameImpl createNotUnderstood1_2Name(String paramString) { return new NotUnderstood1_2Name(paramString); }
  
  public static NameImpl createUpgrade1_2Name(String paramString) { return new Upgrade1_2Name(paramString); }
  
  public static NameImpl createSupportedEnvelope1_2Name(String paramString) { return new SupportedEnvelope1_2Name(paramString); }
  
  public static NameImpl createFault1_2Name(String paramString1, String paramString2) { return new Fault1_2Name(paramString1, paramString2); }
  
  public static NameImpl createCodeSubcode1_2Name(String paramString1, String paramString2) { return new CodeSubcode1_2Name(paramString2, paramString1); }
  
  public static NameImpl createDetail1_1Name() { return new Detail1_1Name(); }
  
  public static NameImpl createDetail1_1Name(String paramString) { return new Detail1_1Name(paramString); }
  
  public static NameImpl createFaultElement1_1Name(String paramString) { return new FaultElement1_1Name(paramString); }
  
  public static NameImpl createFaultElement1_1Name(String paramString1, String paramString2) { return new FaultElement1_1Name(paramString1, paramString2); }
  
  public static NameImpl createSOAP11Name(String paramString) { return new SOAP1_1Name(paramString, null); }
  
  public static NameImpl createSOAP12Name(String paramString) { return new SOAP1_2Name(paramString, null); }
  
  public static NameImpl createSOAP12Name(String paramString1, String paramString2) { return new SOAP1_2Name(paramString1, paramString2); }
  
  public static NameImpl createXmlName(String paramString) { return new NameImpl(paramString, "xml", "http://www.w3.org/XML/1998/namespace"); }
  
  public static Name copyElementName(Element paramElement) {
    String str1 = paramElement.getLocalName();
    String str2 = paramElement.getPrefix();
    String str3 = paramElement.getNamespaceURI();
    return create(str1, str2, str3);
  }
  
  static class Body1_1Name extends SOAP1_1Name {
    Body1_1Name(String param1String) { super("Body", param1String); }
  }
  
  static class Body1_2Name extends SOAP1_2Name {
    Body1_2Name(String param1String) { super("Body", param1String); }
  }
  
  static class CodeSubcode1_2Name extends SOAP1_2Name {
    CodeSubcode1_2Name(String param1String1, String param1String2) { super(param1String1, param1String2); }
  }
  
  static class Detail1_1Name extends NameImpl {
    Detail1_1Name() { super("detail"); }
    
    Detail1_1Name(String param1String) { super("detail", param1String, ""); }
  }
  
  static class Envelope1_1Name extends SOAP1_1Name {
    Envelope1_1Name(String param1String) { super("Envelope", param1String); }
  }
  
  static class Envelope1_2Name extends SOAP1_2Name {
    Envelope1_2Name(String param1String) { super("Envelope", param1String); }
  }
  
  static class Fault1_1Name extends NameImpl {
    Fault1_1Name(String param1String) { super("Fault", (param1String == null || param1String.equals("")) ? "SOAP-ENV" : param1String, "http://schemas.xmlsoap.org/soap/envelope/"); }
  }
  
  static class Fault1_2Name extends NameImpl {
    Fault1_2Name(String param1String1, String param1String2) { super((param1String1 == null || param1String1.equals("")) ? "Fault" : param1String1, (param1String2 == null || param1String2.equals("")) ? "env" : param1String2, "http://www.w3.org/2003/05/soap-envelope"); }
  }
  
  static class FaultElement1_1Name extends NameImpl {
    FaultElement1_1Name(String param1String) { super(param1String); }
    
    FaultElement1_1Name(String param1String1, String param1String2) { super(param1String1, param1String2, ""); }
  }
  
  static class Header1_1Name extends SOAP1_1Name {
    Header1_1Name(String param1String) { super("Header", param1String); }
  }
  
  static class Header1_2Name extends SOAP1_2Name {
    Header1_2Name(String param1String) { super("Header", param1String); }
  }
  
  static class NotUnderstood1_2Name extends NameImpl {
    NotUnderstood1_2Name(String param1String) { super("NotUnderstood", (param1String == null || param1String.equals("")) ? "env" : param1String, "http://www.w3.org/2003/05/soap-envelope"); }
  }
  
  static class SOAP1_1Name extends NameImpl {
    SOAP1_1Name(String param1String1, String param1String2) { super(param1String1, (param1String2 == null || param1String2.equals("")) ? "SOAP-ENV" : param1String2, "http://schemas.xmlsoap.org/soap/envelope/"); }
  }
  
  static class SOAP1_2Name extends NameImpl {
    SOAP1_2Name(String param1String1, String param1String2) { super(param1String1, (param1String2 == null || param1String2.equals("")) ? "env" : param1String2, "http://www.w3.org/2003/05/soap-envelope"); }
  }
  
  static class SupportedEnvelope1_2Name extends NameImpl {
    SupportedEnvelope1_2Name(String param1String) { super("SupportedEnvelope", (param1String == null || param1String.equals("")) ? "env" : param1String, "http://www.w3.org/2003/05/soap-envelope"); }
  }
  
  static class Upgrade1_2Name extends NameImpl {
    Upgrade1_2Name(String param1String) { super("Upgrade", (param1String == null || param1String.equals("")) ? "env" : param1String, "http://www.w3.org/2003/05/soap-envelope"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\name\NameImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */