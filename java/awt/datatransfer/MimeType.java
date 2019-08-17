package java.awt.datatransfer;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Locale;

class MimeType implements Externalizable, Cloneable {
  static final long serialVersionUID = -6568722458793895906L;
  
  private String primaryType;
  
  private String subType;
  
  private MimeTypeParameterList parameters;
  
  private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";
  
  public MimeType() {}
  
  public MimeType(String paramString) throws MimeTypeParseException { parse(paramString); }
  
  public MimeType(String paramString1, String paramString2) throws MimeTypeParseException { this(paramString1, paramString2, new MimeTypeParameterList()); }
  
  public MimeType(String paramString1, String paramString2, MimeTypeParameterList paramMimeTypeParameterList) throws MimeTypeParseException {
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
    this.parameters = (MimeTypeParameterList)paramMimeTypeParameterList.clone();
  }
  
  public int hashCode() {
    null = 0;
    null += this.primaryType.hashCode();
    null += this.subType.hashCode();
    return this.parameters.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof MimeType))
      return false; 
    MimeType mimeType = (MimeType)paramObject;
    return (this.primaryType.equals(mimeType.primaryType) && this.subType.equals(mimeType.subType) && this.parameters.equals(mimeType.parameters));
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
  
  public String getSubType() { return this.subType; }
  
  public MimeTypeParameterList getParameters() { return (MimeTypeParameterList)this.parameters.clone(); }
  
  public String getParameter(String paramString) { return this.parameters.get(paramString); }
  
  public void setParameter(String paramString1, String paramString2) throws MimeTypeParseException { this.parameters.set(paramString1, paramString2); }
  
  public void removeParameter(String paramString) throws MimeTypeParseException { this.parameters.remove(paramString); }
  
  public String toString() { return getBaseType() + this.parameters.toString(); }
  
  public String getBaseType() { return this.primaryType + "/" + this.subType; }
  
  public boolean match(MimeType paramMimeType) { return (paramMimeType == null) ? false : ((this.primaryType.equals(paramMimeType.getPrimaryType()) && (this.subType.equals("*") || paramMimeType.getSubType().equals("*") || this.subType.equals(paramMimeType.getSubType())))); }
  
  public boolean match(String paramString) throws MimeTypeParseException { return (paramString == null) ? false : match(new MimeType(paramString)); }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException {
    String str = toString();
    if (str.length() <= 65535) {
      paramObjectOutput.writeUTF(str);
    } else {
      paramObjectOutput.writeByte(0);
      paramObjectOutput.writeByte(0);
      paramObjectOutput.writeInt(str.length());
      paramObjectOutput.write(str.getBytes());
    } 
  }
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    String str = paramObjectInput.readUTF();
    if (str == null || str.length() == 0) {
      byte[] arrayOfByte = new byte[paramObjectInput.readInt()];
      paramObjectInput.readFully(arrayOfByte);
      str = new String(arrayOfByte);
    } 
    try {
      parse(str);
    } catch (MimeTypeParseException mimeTypeParseException) {
      throw new IOException(mimeTypeParseException.toString());
    } 
  }
  
  public Object clone() {
    MimeType mimeType = null;
    try {
      mimeType = (MimeType)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    mimeType.parameters = (MimeTypeParameterList)this.parameters.clone();
    return mimeType;
  }
  
  private static boolean isTokenChar(char paramChar) { return (paramChar > ' ' && paramChar < '' && "()<>@,;:\\\"/[]?=".indexOf(paramChar) < 0); }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\datatransfer\MimeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */