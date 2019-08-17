package sun.security.x509;

import java.io.IOException;
import java.util.Locale;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class DNSName implements GeneralNameInterface {
  private String name;
  
  private static final String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  
  private static final String digitsAndHyphen = "0123456789-";
  
  private static final String alphaDigitsAndHyphen = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-";
  
  public DNSName(DerValue paramDerValue) throws IOException { this.name = paramDerValue.getIA5String(); }
  
  public DNSName(String paramString) throws IOException {
    if (paramString == null || paramString.length() == 0)
      throw new IOException("DNS name must not be null"); 
    if (paramString.indexOf(' ') != -1)
      throw new IOException("DNS names or NameConstraints with blank components are not permitted"); 
    if (paramString.charAt(0) == '.' || paramString.charAt(paramString.length() - 1) == '.')
      throw new IOException("DNS names or NameConstraints may not begin or end with a ."); 
    for (int i = 0; i < paramString.length(); i = j + 1) {
      int j = paramString.indexOf('.', i);
      if (j < 0)
        j = paramString.length(); 
      if (j - i < 1)
        throw new IOException("DNSName SubjectAltNames with empty components are not permitted"); 
      if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".indexOf(paramString.charAt(i)) < 0)
        throw new IOException("DNSName components must begin with a letter"); 
      for (int k = i + 1; k < j; k++) {
        char c = paramString.charAt(k);
        if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-".indexOf(c) < 0)
          throw new IOException("DNSName components must consist of letters, digits, and hyphens"); 
      } 
    } 
    this.name = paramString;
  }
  
  public int getType() { return 2; }
  
  public String getName() { return this.name; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException { paramDerOutputStream.putIA5String(this.name); }
  
  public String toString() { return "DNSName: " + this.name; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof DNSName))
      return false; 
    DNSName dNSName = (DNSName)paramObject;
    return this.name.equalsIgnoreCase(dNSName.name);
  }
  
  public int hashCode() { return this.name.toUpperCase(Locale.ENGLISH).hashCode(); }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface) throws UnsupportedOperationException {
    byte b;
    if (paramGeneralNameInterface == null) {
      b = -1;
    } else if (paramGeneralNameInterface.getType() != 2) {
      b = -1;
    } else {
      String str1 = ((DNSName)paramGeneralNameInterface).getName().toLowerCase(Locale.ENGLISH);
      String str2 = this.name.toLowerCase(Locale.ENGLISH);
      if (str1.equals(str2)) {
        b = 0;
      } else if (str2.endsWith(str1)) {
        int i = str2.lastIndexOf(str1);
        if (str2.charAt(i - 1) == '.') {
          b = 2;
        } else {
          b = 3;
        } 
      } else if (str1.endsWith(str2)) {
        int i = str1.lastIndexOf(str2);
        if (str1.charAt(i - 1) == '.') {
          b = 1;
        } else {
          b = 3;
        } 
      } else {
        b = 3;
      } 
    } 
    return b;
  }
  
  public int subtreeDepth() {
    byte b = 1;
    for (int i = this.name.indexOf('.'); i >= 0; i = this.name.indexOf('.', i + 1))
      b++; 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\DNSName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */