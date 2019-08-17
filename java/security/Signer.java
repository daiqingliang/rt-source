package java.security;

@Deprecated
public abstract class Signer extends Identity {
  private static final long serialVersionUID = -1763464102261361480L;
  
  private PrivateKey privateKey;
  
  protected Signer() {}
  
  public Signer(String paramString) { super(paramString); }
  
  public Signer(String paramString, IdentityScope paramIdentityScope) throws KeyManagementException { super(paramString, paramIdentityScope); }
  
  public PrivateKey getPrivateKey() {
    check("getSignerPrivateKey");
    return this.privateKey;
  }
  
  public final void setKeyPair(KeyPair paramKeyPair) throws InvalidParameterException, KeyException {
    check("setSignerKeyPair");
    final PublicKey pub = paramKeyPair.getPublic();
    PrivateKey privateKey1 = paramKeyPair.getPrivate();
    if (publicKey == null || privateKey1 == null)
      throw new InvalidParameterException(); 
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws KeyManagementException {
              Signer.this.setPublicKey(pub);
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (KeyManagementException)privilegedActionException.getException();
    } 
    this.privateKey = privateKey1;
  }
  
  String printKeys() {
    String str = "";
    PublicKey publicKey = getPublicKey();
    if (publicKey != null && this.privateKey != null) {
      str = "\tpublic and private keys initialized";
    } else {
      str = "\tno keys";
    } 
    return str;
  }
  
  public String toString() { return "[Signer]" + super.toString(); }
  
  private static void check(String paramString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSecurityAccess(paramString); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Signer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */