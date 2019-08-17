package sun.security.x509;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.ObjectIdentifier;

class AVAKeyword {
  private static final Map<ObjectIdentifier, AVAKeyword> oidMap = new HashMap();
  
  private static final Map<String, AVAKeyword> keywordMap = new HashMap();
  
  private String keyword;
  
  private ObjectIdentifier oid;
  
  private boolean rfc1779Compliant;
  
  private boolean rfc2253Compliant;
  
  private AVAKeyword(String paramString, ObjectIdentifier paramObjectIdentifier, boolean paramBoolean1, boolean paramBoolean2) {
    this.keyword = paramString;
    this.oid = paramObjectIdentifier;
    this.rfc1779Compliant = paramBoolean1;
    this.rfc2253Compliant = paramBoolean2;
    oidMap.put(paramObjectIdentifier, this);
    keywordMap.put(paramString, this);
  }
  
  private boolean isCompliant(int paramInt) {
    switch (paramInt) {
      case 2:
        return this.rfc1779Compliant;
      case 3:
        return this.rfc2253Compliant;
      case 1:
        return true;
    } 
    throw new IllegalArgumentException("Invalid standard " + paramInt);
  }
  
  static ObjectIdentifier getOID(String paramString, int paramInt, Map<String, String> paramMap) throws IOException {
    paramString = paramString.toUpperCase(Locale.ENGLISH);
    if (paramInt == 3) {
      if (paramString.startsWith(" ") || paramString.endsWith(" "))
        throw new IOException("Invalid leading or trailing space in keyword \"" + paramString + "\""); 
    } else {
      paramString = paramString.trim();
    } 
    String str = (String)paramMap.get(paramString);
    if (str == null) {
      AVAKeyword aVAKeyword = (AVAKeyword)keywordMap.get(paramString);
      if (aVAKeyword != null && aVAKeyword.isCompliant(paramInt))
        return aVAKeyword.oid; 
    } else {
      return new ObjectIdentifier(str);
    } 
    if (paramInt == 1 && paramString.startsWith("OID."))
      paramString = paramString.substring(4); 
    boolean bool = false;
    if (paramString.length() != 0) {
      char c = paramString.charAt(0);
      if (c >= '0' && c <= '9')
        bool = true; 
    } 
    if (!bool)
      throw new IOException("Invalid keyword \"" + paramString + "\""); 
    return new ObjectIdentifier(paramString);
  }
  
  static String getKeyword(ObjectIdentifier paramObjectIdentifier, int paramInt) { return getKeyword(paramObjectIdentifier, paramInt, Collections.emptyMap()); }
  
  static String getKeyword(ObjectIdentifier paramObjectIdentifier, int paramInt, Map<String, String> paramMap) {
    String str1 = paramObjectIdentifier.toString();
    String str2 = (String)paramMap.get(str1);
    if (str2 == null) {
      AVAKeyword aVAKeyword = (AVAKeyword)oidMap.get(paramObjectIdentifier);
      if (aVAKeyword != null && aVAKeyword.isCompliant(paramInt))
        return aVAKeyword.keyword; 
    } else {
      if (str2.length() == 0)
        throw new IllegalArgumentException("keyword cannot be empty"); 
      str2 = str2.trim();
      char c = str2.charAt(0);
      if (c < 'A' || c > 'z' || (c > 'Z' && c < 'a'))
        throw new IllegalArgumentException("keyword does not start with letter"); 
      for (byte b = 1; b < str2.length(); b++) {
        c = str2.charAt(b);
        if ((c < 'A' || c > 'z' || (c > 'Z' && c < 'a')) && (c < '0' || c > '9') && c != '_')
          throw new IllegalArgumentException("keyword character is not a letter, digit, or underscore"); 
      } 
      return str2;
    } 
    return (paramInt == 3) ? str1 : ("OID." + str1);
  }
  
  static boolean hasKeyword(ObjectIdentifier paramObjectIdentifier, int paramInt) {
    AVAKeyword aVAKeyword = (AVAKeyword)oidMap.get(paramObjectIdentifier);
    return (aVAKeyword == null) ? false : aVAKeyword.isCompliant(paramInt);
  }
  
  static  {
    new AVAKeyword("CN", X500Name.commonName_oid, true, true);
    new AVAKeyword("C", X500Name.countryName_oid, true, true);
    new AVAKeyword("L", X500Name.localityName_oid, true, true);
    new AVAKeyword("S", X500Name.stateName_oid, false, false);
    new AVAKeyword("ST", X500Name.stateName_oid, true, true);
    new AVAKeyword("O", X500Name.orgName_oid, true, true);
    new AVAKeyword("OU", X500Name.orgUnitName_oid, true, true);
    new AVAKeyword("T", X500Name.title_oid, false, false);
    new AVAKeyword("IP", X500Name.ipAddress_oid, false, false);
    new AVAKeyword("STREET", X500Name.streetAddress_oid, true, true);
    new AVAKeyword("DC", X500Name.DOMAIN_COMPONENT_OID, false, true);
    new AVAKeyword("DNQUALIFIER", X500Name.DNQUALIFIER_OID, false, false);
    new AVAKeyword("DNQ", X500Name.DNQUALIFIER_OID, false, false);
    new AVAKeyword("SURNAME", X500Name.SURNAME_OID, false, false);
    new AVAKeyword("GIVENNAME", X500Name.GIVENNAME_OID, false, false);
    new AVAKeyword("INITIALS", X500Name.INITIALS_OID, false, false);
    new AVAKeyword("GENERATION", X500Name.GENERATIONQUALIFIER_OID, false, false);
    new AVAKeyword("EMAIL", PKCS9Attribute.EMAIL_ADDRESS_OID, false, false);
    new AVAKeyword("EMAILADDRESS", PKCS9Attribute.EMAIL_ADDRESS_OID, false, false);
    new AVAKeyword("UID", X500Name.userid_oid, false, true);
    new AVAKeyword("SERIALNUMBER", X500Name.SERIALNUMBER_OID, false, false);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\AVAKeyword.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */