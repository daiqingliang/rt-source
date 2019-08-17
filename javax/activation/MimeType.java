package javax.activation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Locale;

public class MimeType implements Externalizable {
  private String primaryType;
  
  private String subType;
  
  private MimeTypeParameterList parameters;
  
  private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
  
  public MimeType() {
    this.primaryType = "application";
    this.subType = "*";
    this.parameters = new MimeTypeParameterList();
  }
  
  public MimeType(String paramString) throws MimeTypeParseException { parse(paramString); }
  
  public MimeType(String paramString1, String paramString2) throws MimeTypeParseException {
    if (isValidToken(paramString1)) {
      this.primaryType = paramString1.toLowerCase(Locale.ENGLISH);
    } else {
      throw new MimeTypeParseException("Primary type is invalid.");
    } 
    if (isValidToken(paramString2)) {
      this.subType = paramString2.toLowerCase(Locale.ENGLISH);
    } else {
      throw new MimeTypeParseException("Sub type is invalid.");
    } 
    this.parameters = new MimeTypeParameterList();
  }
  
  private void parse(String paramString) throws MimeTypeParseException {
    int i = paramString.indexOf('/');
    int j = paramString.indexOf(';');
    if (i < 0 && j < 0)
      throw new MimeTypeParseException("Unable to find a sub type."); 
    if (i < 0 && j >= 0)
      throw new MimeTypeParseException("Unable to find a sub type."); 
    if (i >= 0 && j < 0) {
      this.primaryType = paramString.substring(0, i).trim().toLowerCase(Locale.ENGLISH);
      this.subType = paramString.substring(i + 1).trim().toLowerCase(Locale.ENGLISH);
      this.parameters = new MimeTypeParameterList();
    } else if (i < j) {
      this.primaryType = paramString.substring(0, i).trim().toLowerCase(Locale.ENGLISH);
      this.subType = paramString.substring(i + 1, j).trim().toLowerCase(Locale.ENGLISH);
      this.parameters = new MimeTypeParameterList(paramString.substring(j));
    } else {
      throw new MimeTypeParseException("Unable to find a sub type.");
    } 
    if (!isValidToken(this.primaryType))
      throw new MimeTypeParseException("Primary type is invalid."); 
    if (!isValidToken(this.subType))
      throw new MimeTypeParseException("Sub type is invalid."); 
  }
  
  public String getPrimaryType() { return this.primaryType; }
  
  public void setPrimaryType(String paramString) throws MimeTypeParseException {
    if (!isValidToken(this.primaryType))
      throw new MimeTypeParseException("Primary type is invalid."); 
    this.primaryType = paramString.toLowerCase(Locale.ENGLISH);
  }
  
  public String getSubType() { return this.subType; }
  
  public void setSubType(String paramString) throws MimeTypeParseException {
    if (!isValidToken(this.subType))
      throw new MimeTypeParseException("Sub type is invalid."); 
    this.subType = paramString.toLowerCase(Locale.ENGLISH);
  }
  
  public MimeTypeParameterList getParameters() { return this.parameters; }
  
  public String getParameter(String paramString) { return this.parameters.get(paramString); }
  
  public void setParameter(String paramString1, String paramString2) throws MimeTypeParseException { this.parameters.set(paramString1, paramString2); }
  
  public void removeParameter(String paramString) throws MimeTypeParseException { this.parameters.remove(paramString); }
  
  public String toString() { return getBaseType() + this.parameters.toString(); }
  
  public String getBaseType() { return this.primaryType + "/" + this.subType; }
  
  public boolean match(MimeType paramMimeType) { return (this.primaryType.equals(paramMimeType.getPrimaryType()) && (this.subType.equals("*") || paramMimeType.getSubType().equals("*") || this.subType.equals(paramMimeType.getSubType()))); }
  
  public boolean match(String paramString) throws MimeTypeParseException { return match(new MimeType(paramString)); }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException {
    paramObjectOutput.writeUTF(toString());
    paramObjectOutput.flush();
  }
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    try {
      parse(paramObjectInput.readUTF());
    } catch (MimeTypeParseException mimeTypeParseException) {
      throw new IOException(mimeTypeParseException.toString());
    } 
  }
  
  private static boolean isTokenChar(char paramChar) { return (paramChar > ' ' && paramChar < '' && "()<>@,;:/[]?=\\\"".indexOf(paramChar) < 0); }
  
  private boolean isValidToken(String paramString) throws MimeTypeParseException {
    int i = paramString.length();
    if (i > 0) {
      for (byte b = 0; b < i; b++) {
        char c = paramString.charAt(b);
        if (!isTokenChar(c))
          return false; 
      } 
      return true;
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\MimeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */