package javax.security.auth.kerberos;

import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public class KerberosKey implements SecretKey, Destroyable {
  private static final long serialVersionUID = -4625402278148246993L;
  
  private KerberosPrincipal principal;
  
  private int versionNum;
  
  private KeyImpl key;
  
  private boolean destroyed = false;
  
  public KerberosKey(KerberosPrincipal paramKerberosPrincipal, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    this.principal = paramKerberosPrincipal;
    this.versionNum = paramInt2;
    this.key = new KeyImpl(paramArrayOfByte, paramInt1);
  }
  
  public KerberosKey(KerberosPrincipal paramKerberosPrincipal, char[] paramArrayOfChar, String paramString) {
    this.principal = paramKerberosPrincipal;
    this.key = new KeyImpl(paramKerberosPrincipal, paramArrayOfChar, paramString);
  }
  
  public final KerberosPrincipal getPrincipal() {
    if (this.destroyed)
      throw new IllegalStateException("This key is no longer valid"); 
    return this.principal;
  }
  
  public final int getVersionNumber() {
    if (this.destroyed)
      throw new IllegalStateException("This key is no longer valid"); 
    return this.versionNum;
  }
  
  public final int getKeyType() {
    if (this.destroyed)
      throw new IllegalStateException("This key is no longer valid"); 
    return this.key.getKeyType();
  }
  
  public final String getAlgorithm() {
    if (this.destroyed)
      throw new IllegalStateException("This key is no longer valid"); 
    return this.key.getAlgorithm();
  }
  
  public final String getFormat() {
    if (this.destroyed)
      throw new IllegalStateException("This key is no longer valid"); 
    return this.key.getFormat();
  }
  
  public final byte[] getEncoded() {
    if (this.destroyed)
      throw new IllegalStateException("This key is no longer valid"); 
    return this.key.getEncoded();
  }
  
  public void destroy() throws DestroyFailedException {
    if (!this.destroyed) {
      this.key.destroy();
      this.principal = null;
      this.destroyed = true;
    } 
  }
  
  public boolean isDestroyed() { return this.destroyed; }
  
  public String toString() { return this.destroyed ? "Destroyed Principal" : ("Kerberos Principal " + this.principal.toString() + "Key Version " + this.versionNum + "key " + this.key.toString()); }
  
  public int hashCode() {
    int i = 17;
    if (isDestroyed())
      return i; 
    i = 37 * i + Arrays.hashCode(getEncoded());
    i = 37 * i + getKeyType();
    if (this.principal != null)
      i = 37 * i + this.principal.hashCode(); 
    return i * 37 + this.versionNum;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof KerberosKey))
      return false; 
    KerberosKey kerberosKey = (KerberosKey)paramObject;
    if (isDestroyed() || kerberosKey.isDestroyed())
      return false; 
    if (this.versionNum != kerberosKey.getVersionNumber() || getKeyType() != kerberosKey.getKeyType() || !Arrays.equals(getEncoded(), kerberosKey.getEncoded()))
      return false; 
    if (this.principal == null) {
      if (kerberosKey.getPrincipal() != null)
        return false; 
    } else if (!this.principal.equals(kerberosKey.getPrincipal())) {
      return false;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\kerberos\KerberosKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */