package sun.security.jgss.krb5;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSHeader;
import sun.security.jgss.GSSToken;

abstract class MessageToken extends Krb5Token {
  private static final int TOKEN_NO_CKSUM_SIZE = 16;
  
  private static final int FILLER = 65535;
  
  static final int SGN_ALG_DES_MAC_MD5 = 0;
  
  static final int SGN_ALG_DES_MAC = 512;
  
  static final int SGN_ALG_HMAC_SHA1_DES3_KD = 1024;
  
  static final int SEAL_ALG_NONE = 65535;
  
  static final int SEAL_ALG_DES = 0;
  
  static final int SEAL_ALG_DES3_KD = 512;
  
  static final int SEAL_ALG_ARCFOUR_HMAC = 4096;
  
  static final int SGN_ALG_HMAC_MD5_ARCFOUR = 4352;
  
  private static final int TOKEN_ID_POS = 0;
  
  private static final int SIGN_ALG_POS = 2;
  
  private static final int SEAL_ALG_POS = 4;
  
  private int seqNumber;
  
  private boolean confState = true;
  
  private boolean initiator = true;
  
  private int tokenId = 0;
  
  private GSSHeader gssHeader = null;
  
  private MessageTokenHeader tokenHeader = null;
  
  private byte[] checksum = null;
  
  private byte[] encSeqNumber = null;
  
  private byte[] seqNumberData = null;
  
  CipherHelper cipherHelper = null;
  
  MessageToken(int paramInt1, Krb5Context paramKrb5Context, byte[] paramArrayOfByte, int paramInt2, int paramInt3, MessageProp paramMessageProp) throws GSSException { this(paramInt1, paramKrb5Context, new ByteArrayInputStream(paramArrayOfByte, paramInt2, paramInt3), paramMessageProp); }
  
  MessageToken(int paramInt, Krb5Context paramKrb5Context, InputStream paramInputStream, MessageProp paramMessageProp) throws GSSException {
    init(paramInt, paramKrb5Context);
    try {
      this.gssHeader = new GSSHeader(paramInputStream);
      if (!this.gssHeader.getOid().equals(OID))
        throw new GSSException(10, -1, getTokenName(paramInt)); 
      if (!this.confState)
        paramMessageProp.setPrivacy(false); 
      this.tokenHeader = new MessageTokenHeader(paramInputStream, paramMessageProp);
      this.encSeqNumber = new byte[8];
      readFully(paramInputStream, this.encSeqNumber);
      this.checksum = new byte[this.cipherHelper.getChecksumLength()];
      readFully(paramInputStream, this.checksum);
    } catch (IOException iOException) {
      throw new GSSException(10, -1, getTokenName(paramInt) + ":" + iOException.getMessage());
    } 
  }
  
  public final GSSHeader getGSSHeader() { return this.gssHeader; }
  
  public final int getTokenId() { return this.tokenId; }
  
  public final byte[] getEncSeqNumber() { return this.encSeqNumber; }
  
  public final byte[] getChecksum() { return this.checksum; }
  
  public final boolean getConfState() { return this.confState; }
  
  public void genSignAndSeqNumber(MessageProp paramMessageProp, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3) throws GSSException {
    int i = paramMessageProp.getQOP();
    if (i != 0) {
      i = 0;
      paramMessageProp.setQOP(i);
    } 
    if (!this.confState)
      paramMessageProp.setPrivacy(false); 
    this.tokenHeader = new MessageTokenHeader(this.tokenId, paramMessageProp.getPrivacy(), i);
    this.checksum = getChecksum(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
    this.seqNumberData = new byte[8];
    if (this.cipherHelper.isArcFour()) {
      writeBigEndian(this.seqNumber, this.seqNumberData);
    } else {
      writeLittleEndian(this.seqNumber, this.seqNumberData);
    } 
    if (!this.initiator) {
      this.seqNumberData[4] = -1;
      this.seqNumberData[5] = -1;
      this.seqNumberData[6] = -1;
      this.seqNumberData[7] = -1;
    } 
    this.encSeqNumber = this.cipherHelper.encryptSeq(this.checksum, this.seqNumberData, 0, 8);
  }
  
  public final boolean verifySignAndSeqNumber(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3) throws GSSException {
    byte[] arrayOfByte = getChecksum(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
    if (MessageDigest.isEqual(this.checksum, arrayOfByte)) {
      this.seqNumberData = this.cipherHelper.decryptSeq(this.checksum, this.encSeqNumber, 0, 8);
      byte b = 0;
      if (this.initiator)
        b = -1; 
      if (this.seqNumberData[4] == b && this.seqNumberData[5] == b && this.seqNumberData[6] == b && this.seqNumberData[7] == b)
        return true; 
    } 
    return false;
  }
  
  public final int getSequenceNumber() {
    int i = 0;
    if (this.cipherHelper.isArcFour()) {
      i = readBigEndian(this.seqNumberData, 0, 4);
    } else {
      i = readLittleEndian(this.seqNumberData, 0, 4);
    } 
    return i;
  }
  
  private byte[] getChecksum(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3) throws GSSException {
    byte[] arrayOfByte1 = this.tokenHeader.getBytes();
    byte[] arrayOfByte2 = paramArrayOfByte1;
    byte[] arrayOfByte3 = arrayOfByte1;
    if (arrayOfByte2 != null) {
      arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
    } 
    return this.cipherHelper.calculateChecksum(this.tokenHeader.getSignAlg(), arrayOfByte3, paramArrayOfByte3, paramArrayOfByte2, paramInt1, paramInt2, this.tokenId);
  }
  
  MessageToken(int paramInt, Krb5Context paramKrb5Context) throws GSSException {
    init(paramInt, paramKrb5Context);
    this.seqNumber = paramKrb5Context.incrementMySequenceNumber();
  }
  
  private void init(int paramInt, Krb5Context paramKrb5Context) throws GSSException {
    this.tokenId = paramInt;
    this.confState = paramKrb5Context.getConfState();
    this.initiator = paramKrb5Context.isInitiator();
    this.cipherHelper = paramKrb5Context.getCipherHelper(null);
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException, GSSException {
    this.gssHeader = new GSSHeader(OID, getKrb5TokenSize());
    this.gssHeader.encode(paramOutputStream);
    this.tokenHeader.encode(paramOutputStream);
    paramOutputStream.write(this.encSeqNumber);
    paramOutputStream.write(this.checksum);
  }
  
  protected int getKrb5TokenSize() { return getTokenSize(); }
  
  protected final int getTokenSize() { return 16 + this.cipherHelper.getChecksumLength(); }
  
  protected static final int getTokenSize(CipherHelper paramCipherHelper) throws GSSException { return 16 + paramCipherHelper.getChecksumLength(); }
  
  protected abstract int getSealAlg(boolean paramBoolean, int paramInt) throws GSSException;
  
  protected int getSgnAlg(int paramInt) throws GSSException { return this.cipherHelper.getSgnAlg(); }
  
  class MessageTokenHeader {
    private int tokenId;
    
    private int signAlg;
    
    private int sealAlg;
    
    private byte[] bytes = new byte[8];
    
    public MessageTokenHeader(int param1Int1, boolean param1Boolean, int param1Int2) throws GSSException {
      this.tokenId = param1Int1;
      this.signAlg = this$0.getSgnAlg(param1Int2);
      this.sealAlg = this$0.getSealAlg(param1Boolean, param1Int2);
      this.bytes[0] = (byte)(param1Int1 >>> 8);
      this.bytes[1] = (byte)param1Int1;
      this.bytes[2] = (byte)(this.signAlg >>> 8);
      this.bytes[3] = (byte)this.signAlg;
      this.bytes[4] = (byte)(this.sealAlg >>> 8);
      this.bytes[5] = (byte)this.sealAlg;
      this.bytes[6] = -1;
      this.bytes[7] = -1;
    }
    
    public MessageTokenHeader(InputStream param1InputStream, MessageProp param1MessageProp) throws IOException {
      GSSToken.readFully(param1InputStream, this.bytes);
      this.tokenId = GSSToken.readInt(this.bytes, 0);
      this.signAlg = GSSToken.readInt(this.bytes, 2);
      this.sealAlg = GSSToken.readInt(this.bytes, 4);
      int i = GSSToken.readInt(this.bytes, 6);
      switch (this.sealAlg) {
        case 0:
        case 512:
        case 4096:
          param1MessageProp.setPrivacy(true);
          break;
        default:
          param1MessageProp.setPrivacy(false);
          break;
      } 
      param1MessageProp.setQOP(0);
    }
    
    public final void encode(OutputStream param1OutputStream) throws IOException, GSSException { param1OutputStream.write(this.bytes); }
    
    public final int getTokenId() { return this.tokenId; }
    
    public final int getSignAlg() { return this.signAlg; }
    
    public final int getSealAlg() { return this.sealAlg; }
    
    public final byte[] getBytes() { return this.bytes; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\MessageToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */