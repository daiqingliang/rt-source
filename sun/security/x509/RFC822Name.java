package sun.security.x509;

import java.io.IOException;
import java.util.Locale;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class RFC822Name implements GeneralNameInterface {
  private String name;
  
  public RFC822Name(DerValue paramDerValue) throws IOException {
    this.name = paramDerValue.getIA5String();
    parseName(this.name);
  }
  
  public RFC822Name(String paramString) throws IOException {
    parseName(paramString);
    this.name = paramString;
  }
  
  public void parseName(String paramString) throws IOException {
    if (paramString == null || paramString.length() == 0)
      throw new IOException("RFC822Name may not be null or empty"); 
    String str = paramString.substring(paramString.indexOf('@') + 1);
    if (str.length() == 0)
      throw new IOException("RFC822Name may not end with @"); 
    if (str.startsWith(".") && str.length() == 1)
      throw new IOException("RFC822Name domain may not be just ."); 
  }
  
  public int getType() { return 1; }
  
  public String getName() { return this.name; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException { paramDerOutputStream.putIA5String(this.name); }
  
  public String toString() { return "RFC822Name: " + this.name; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof RFC822Name))
      return false; 
    RFC822Name rFC822Name = (RFC822Name)paramObject;
    return this.name.equalsIgnoreCase(rFC822Name.name);
  }
  
  public int hashCode() { return this.name.toUpperCase(Locale.ENGLISH).hashCode(); }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface) throws UnsupportedOperationException {
    byte b;
    if (paramGeneralNameInterface == null) {
      b = -1;
    } else if (paramGeneralNameInterface.getType() != 1) {
      b = -1;
    } else {
      String str1 = ((RFC822Name)paramGeneralNameInterface).getName().toLowerCase(Locale.ENGLISH);
      String str2 = this.name.toLowerCase(Locale.ENGLISH);
      if (str1.equals(str2)) {
        b = 0;
      } else if (str2.endsWith(str1)) {
        if (str1.indexOf('@') != -1) {
          b = 3;
        } else if (str1.startsWith(".")) {
          b = 2;
        } else {
          int i = str2.lastIndexOf(str1);
          if (str2.charAt(i - 1) == '@') {
            b = 2;
          } else {
            b = 3;
          } 
        } 
      } else if (str1.endsWith(str2)) {
        if (str2.indexOf('@') != -1) {
          b = 3;
        } else if (str2.startsWith(".")) {
          b = 1;
        } else {
          int i = str1.lastIndexOf(str2);
          if (str1.charAt(i - 1) == '@') {
            b = 1;
          } else {
            b = 3;
          } 
        } 
      } else {
        b = 3;
      } 
    } 
    return b;
  }
  
  public int subtreeDepth() {
    String str = this.name;
    byte b = 1;
    int i = str.lastIndexOf('@');
    if (i >= 0) {
      b++;
      str = str.substring(i + 1);
    } 
    while (str.lastIndexOf('.') >= 0) {
      str = str.substring(0, str.lastIndexOf('.'));
      b++;
    } 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\RFC822Name.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */