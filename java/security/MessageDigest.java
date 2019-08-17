package java.security;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import sun.security.util.Debug;

public abstract class MessageDigest extends MessageDigestSpi {
  private static final Debug pdebug;
  
  private static final boolean skipDebug = ((pdebug = Debug.getInstance("provider", "Provider")).isOn("engine=") && !Debug.isOn("messagedigest"));
  
  private String algorithm;
  
  private static final int INITIAL = 0;
  
  private static final int IN_PROGRESS = 1;
  
  private int state = 0;
  
  private Provider provider;
  
  protected MessageDigest(String paramString) { this.algorithm = paramString; }
  
  public static MessageDigest getInstance(String paramString) throws NoSuchAlgorithmException {
    try {
      Delegate delegate;
      Object[] arrayOfObject = Security.getImpl(paramString, "MessageDigest", (String)null);
      if (arrayOfObject[0] instanceof MessageDigest) {
        delegate = (MessageDigest)arrayOfObject[0];
      } else {
        delegate = new Delegate((MessageDigestSpi)arrayOfObject[0], paramString);
      } 
      delegate.provider = (Provider)arrayOfObject[1];
      if (!skipDebug && pdebug != null)
        pdebug.println("MessageDigest." + paramString + " algorithm from: " + delegate.provider.getName()); 
      return delegate;
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new NoSuchAlgorithmException(paramString + " not found");
    } 
  }
  
  public static MessageDigest getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    if (paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException("missing provider"); 
    Object[] arrayOfObject = Security.getImpl(paramString1, "MessageDigest", paramString2);
    if (arrayOfObject[0] instanceof MessageDigest) {
      MessageDigest messageDigest = (MessageDigest)arrayOfObject[0];
      messageDigest.provider = (Provider)arrayOfObject[1];
      return messageDigest;
    } 
    Delegate delegate = new Delegate((MessageDigestSpi)arrayOfObject[0], paramString1);
    delegate.provider = (Provider)arrayOfObject[1];
    return delegate;
  }
  
  public static MessageDigest getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    if (paramProvider == null)
      throw new IllegalArgumentException("missing provider"); 
    Object[] arrayOfObject = Security.getImpl(paramString, "MessageDigest", paramProvider);
    if (arrayOfObject[0] instanceof MessageDigest) {
      MessageDigest messageDigest = (MessageDigest)arrayOfObject[0];
      messageDigest.provider = (Provider)arrayOfObject[1];
      return messageDigest;
    } 
    Delegate delegate = new Delegate((MessageDigestSpi)arrayOfObject[0], paramString);
    delegate.provider = (Provider)arrayOfObject[1];
    return delegate;
  }
  
  public final Provider getProvider() { return this.provider; }
  
  public void update(byte paramByte) {
    engineUpdate(paramByte);
    this.state = 1;
  }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("No input buffer given"); 
    if (paramArrayOfByte.length - paramInt1 < paramInt2)
      throw new IllegalArgumentException("Input buffer too short"); 
    engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
    this.state = 1;
  }
  
  public void update(byte[] paramArrayOfByte) {
    engineUpdate(paramArrayOfByte, 0, paramArrayOfByte.length);
    this.state = 1;
  }
  
  public final void update(ByteBuffer paramByteBuffer) {
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    engineUpdate(paramByteBuffer);
    this.state = 1;
  }
  
  public byte[] digest() {
    byte[] arrayOfByte = engineDigest();
    this.state = 0;
    return arrayOfByte;
  }
  
  public int digest(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws DigestException {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("No output buffer given"); 
    if (paramArrayOfByte.length - paramInt1 < paramInt2)
      throw new IllegalArgumentException("Output buffer too small for specified offset and length"); 
    int i = engineDigest(paramArrayOfByte, paramInt1, paramInt2);
    this.state = 0;
    return i;
  }
  
  public byte[] digest(byte[] paramArrayOfByte) {
    update(paramArrayOfByte);
    return digest();
  }
  
  public String toString() {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(byteArrayOutputStream);
    printStream.print(this.algorithm + " Message Digest from " + this.provider.getName() + ", ");
    switch (this.state) {
      case 0:
        printStream.print("<initialized>");
        break;
      case 1:
        printStream.print("<in progress>");
        break;
    } 
    printStream.println();
    return byteArrayOutputStream.toString();
  }
  
  public static boolean isEqual(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    if (paramArrayOfByte1 == paramArrayOfByte2)
      return true; 
    if (paramArrayOfByte1 == null || paramArrayOfByte2 == null)
      return false; 
    if (paramArrayOfByte1.length != paramArrayOfByte2.length)
      return false; 
    byte b = 0;
    for (byte b1 = 0; b1 < paramArrayOfByte1.length; b1++)
      b |= paramArrayOfByte1[b1] ^ paramArrayOfByte2[b1]; 
    return (b == 0);
  }
  
  public void reset() {
    engineReset();
    this.state = 0;
  }
  
  public final String getAlgorithm() { return this.algorithm; }
  
  public final int getDigestLength() {
    int i = engineGetDigestLength();
    if (i == 0)
      try {
        MessageDigest messageDigest = (MessageDigest)clone();
        byte[] arrayOfByte = messageDigest.digest();
        return arrayOfByte.length;
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        return i;
      }  
    return i;
  }
  
  public Object clone() throws CloneNotSupportedException {
    if (this instanceof Cloneable)
      return super.clone(); 
    throw new CloneNotSupportedException();
  }
  
  static class Delegate extends MessageDigest {
    private MessageDigestSpi digestSpi;
    
    public Delegate(MessageDigestSpi param1MessageDigestSpi, String param1String) {
      super(param1String);
      this.digestSpi = param1MessageDigestSpi;
    }
    
    public Object clone() throws CloneNotSupportedException {
      if (this.digestSpi instanceof Cloneable) {
        MessageDigestSpi messageDigestSpi = (MessageDigestSpi)this.digestSpi.clone();
        Delegate delegate = new Delegate(messageDigestSpi, this.algorithm);
        delegate.provider = this.provider;
        delegate.state = this.state;
        return delegate;
      } 
      throw new CloneNotSupportedException();
    }
    
    protected int engineGetDigestLength() { return this.digestSpi.engineGetDigestLength(); }
    
    protected void engineUpdate(byte param1Byte) { this.digestSpi.engineUpdate(param1Byte); }
    
    protected void engineUpdate(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) { this.digestSpi.engineUpdate(param1ArrayOfByte, param1Int1, param1Int2); }
    
    protected void engineUpdate(ByteBuffer param1ByteBuffer) { this.digestSpi.engineUpdate(param1ByteBuffer); }
    
    protected byte[] engineDigest() { return this.digestSpi.engineDigest(); }
    
    protected int engineDigest(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws DigestException { return this.digestSpi.engineDigest(param1ArrayOfByte, param1Int1, param1Int2); }
    
    protected void engineReset() { this.digestSpi.engineReset(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\MessageDigest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */