package java.security;

import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Locale;
import javax.crypto.spec.SecretKeySpec;

public class KeyRep implements Serializable {
  private static final long serialVersionUID = -4757683898830641853L;
  
  private static final String PKCS8 = "PKCS#8";
  
  private static final String X509 = "X.509";
  
  private static final String RAW = "RAW";
  
  private Type type;
  
  private String algorithm;
  
  private String format;
  
  private byte[] encoded;
  
  public KeyRep(Type paramType, String paramString1, String paramString2, byte[] paramArrayOfByte) {
    if (paramType == null || paramString1 == null || paramString2 == null || paramArrayOfByte == null)
      throw new NullPointerException("invalid null input(s)"); 
    this.type = paramType;
    this.algorithm = paramString1;
    this.format = paramString2.toUpperCase(Locale.ENGLISH);
    this.encoded = (byte[])paramArrayOfByte.clone();
  }
  
  protected Object readResolve() throws ObjectStreamException {
    try {
      if (this.type == Type.SECRET && "RAW".equals(this.format))
        return new SecretKeySpec(this.encoded, this.algorithm); 
      if (this.type == Type.PUBLIC && "X.509".equals(this.format)) {
        KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
        return keyFactory.generatePublic(new X509EncodedKeySpec(this.encoded));
      } 
      if (this.type == Type.PRIVATE && "PKCS#8".equals(this.format)) {
        KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(this.encoded));
      } 
      throw new NotSerializableException("unrecognized type/format combination: " + this.type + "/" + this.format);
    } catch (NotSerializableException notSerializableException) {
      throw notSerializableException;
    } catch (Exception exception) {
      NotSerializableException notSerializableException = new NotSerializableException("java.security.Key: [" + this.type + "] [" + this.algorithm + "] [" + this.format + "]");
      notSerializableException.initCause(exception);
      throw notSerializableException;
    } 
  }
  
  public enum Type {
    SECRET, PUBLIC, PRIVATE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\KeyRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */