package sun.net.www.protocol.http;

import java.security.AccessController;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import sun.net.www.HeaderParser;
import sun.net.www.MessageHeader;
import sun.security.action.GetPropertyAction;

public class AuthenticationHeader {
  MessageHeader rsp;
  
  HeaderParser preferred;
  
  String preferred_r;
  
  private final HttpCallerInfo hci;
  
  boolean dontUseNegotiate = false;
  
  static String authPref = null;
  
  String hdrname;
  
  HashMap<String, SchemeMapValue> schemes;
  
  public String toString() { return "AuthenticationHeader: prefer " + this.preferred_r; }
  
  public AuthenticationHeader(String paramString, MessageHeader paramMessageHeader, HttpCallerInfo paramHttpCallerInfo, boolean paramBoolean) { this(paramString, paramMessageHeader, paramHttpCallerInfo, paramBoolean, Collections.emptySet()); }
  
  public AuthenticationHeader(String paramString, MessageHeader paramMessageHeader, HttpCallerInfo paramHttpCallerInfo, boolean paramBoolean, Set<String> paramSet) {
    this.hci = paramHttpCallerInfo;
    this.dontUseNegotiate = paramBoolean;
    this.rsp = paramMessageHeader;
    this.hdrname = paramString;
    this.schemes = new HashMap();
    parse(paramSet);
  }
  
  public HttpCallerInfo getHttpCallerInfo() { return this.hci; }
  
  private void parse(Set<String> paramSet) {
    Iterator iterator = this.rsp.multiValueIterator(this.hdrname);
    while (iterator.hasNext()) {
      String str = (String)iterator.next();
      HeaderParser headerParser = new HeaderParser(str);
      Iterator iterator1 = headerParser.keys();
      byte b1 = 0;
      byte b2 = -1;
      while (iterator1.hasNext()) {
        iterator1.next();
        if (headerParser.findValue(b1) == null) {
          if (b2 != -1) {
            HeaderParser headerParser1 = headerParser.subsequence(b2, b1);
            String str1 = headerParser1.findKey(0);
            if (!paramSet.contains(str1))
              this.schemes.put(str1, new SchemeMapValue(headerParser1, str)); 
          } 
          b2 = b1;
        } 
        b1++;
      } 
      if (b1 > b2) {
        HeaderParser headerParser1 = headerParser.subsequence(b2, b1);
        String str1 = headerParser1.findKey(0);
        if (!paramSet.contains(str1))
          this.schemes.put(str1, new SchemeMapValue(headerParser1, str)); 
      } 
    } 
    SchemeMapValue schemeMapValue = null;
    if (authPref == null || (schemeMapValue = (SchemeMapValue)this.schemes.get(authPref)) == null) {
      if (schemeMapValue == null && !this.dontUseNegotiate) {
        SchemeMapValue schemeMapValue1 = (SchemeMapValue)this.schemes.get("negotiate");
        if (schemeMapValue1 != null) {
          if (this.hci == null || !NegotiateAuthentication.isSupported(new HttpCallerInfo(this.hci, "Negotiate")))
            schemeMapValue1 = null; 
          schemeMapValue = schemeMapValue1;
        } 
      } 
      if (schemeMapValue == null && !this.dontUseNegotiate) {
        SchemeMapValue schemeMapValue1 = (SchemeMapValue)this.schemes.get("kerberos");
        if (schemeMapValue1 != null) {
          if (this.hci == null || !NegotiateAuthentication.isSupported(new HttpCallerInfo(this.hci, "Kerberos")))
            schemeMapValue1 = null; 
          schemeMapValue = schemeMapValue1;
        } 
      } 
      if (schemeMapValue == null && (schemeMapValue = (SchemeMapValue)this.schemes.get("digest")) == null && (!NTLMAuthenticationProxy.supported || (schemeMapValue = (SchemeMapValue)this.schemes.get("ntlm")) == null))
        schemeMapValue = (SchemeMapValue)this.schemes.get("basic"); 
    } else if (this.dontUseNegotiate && authPref.equals("negotiate")) {
      schemeMapValue = null;
    } 
    if (schemeMapValue != null) {
      this.preferred = schemeMapValue.parser;
      this.preferred_r = schemeMapValue.raw;
    } 
  }
  
  public HeaderParser headerParser() { return this.preferred; }
  
  public String scheme() { return (this.preferred != null) ? this.preferred.findKey(0) : null; }
  
  public String raw() { return this.preferred_r; }
  
  public boolean isPresent() { return (this.preferred != null); }
  
  static  {
    authPref = (String)AccessController.doPrivileged(new GetPropertyAction("http.auth.preference"));
    if (authPref != null) {
      authPref = authPref.toLowerCase();
      if (authPref.equals("spnego") || authPref.equals("kerberos"))
        authPref = "negotiate"; 
    } 
  }
  
  static class SchemeMapValue {
    String raw;
    
    HeaderParser parser;
    
    SchemeMapValue(HeaderParser param1HeaderParser, String param1String) {
      this.raw = param1String;
      this.parser = param1HeaderParser;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\AuthenticationHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */