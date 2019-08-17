package java.security;

import java.io.Serializable;

public final class KeyPair implements Serializable {
  private static final long serialVersionUID = -7565189502268009837L;
  
  private PrivateKey privateKey;
  
  private PublicKey publicKey;
  
  public KeyPair(PublicKey paramPublicKey, PrivateKey paramPrivateKey) {
    this.publicKey = paramPublicKey;
    this.privateKey = paramPrivateKey;
  }
  
  public PublicKey getPublic() { return this.publicKey; }
  
  public PrivateKey getPrivate() { return this.privateKey; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\KeyPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */