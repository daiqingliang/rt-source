package sun.security.jgss.krb5;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSToken;

abstract class MessageToken_v2 extends Krb5Token {
  protected static final int TOKEN_HEADER_SIZE = 16;
  
  private static final int TOKEN_ID_POS = 0;
  
  private static final int TOKEN_FLAG_POS = 2;
  
  private static final int TOKEN_EC_POS = 4;
  
  private static final int TOKEN_RRC_POS = 6;
  
  protected static final int CONFOUNDER_SIZE = 16;
  
  static final int KG_USAGE_ACCEPTOR_SEAL = 22;
  
  static final int KG_USAGE_ACCEPTOR_SIGN = 23;
  
  static final int KG_USAGE_INITIATOR_SEAL = 24;
  
  static final int KG_USAGE_INITIATOR_SIGN = 25;
  
  private static final int FLAG_SENDER_IS_ACCEPTOR = 1;
  
  private static final int FLAG_WRAP_CONFIDENTIAL = 2;
  
  private static final int FLAG_ACCEPTOR_SUBKEY = 4;
  
  private static final int FILLER = 255;
  
  private MessageTokenHeader tokenHeader = null;
  
  private int tokenId = 0;
  
  private int seqNumber;
  
  protected byte[] tokenData;
  
  protected int tokenDataLen;
  
  private int key_usage = 0;
  
  private int ec = 0;
  
  private int rrc = 0;
  
  byte[] checksum = null;
  
  private boolean confState = true;
  
  private boolean initiator = true;
  
  private boolean have_acceptor_subkey = false;
  
  CipherHelper cipherHelper = null;
  
  MessageToken_v2(int paramInt1, Krb5Context paramKrb5Context, byte[] paramArrayOfByte, int paramInt2, int paramInt3, MessageProp paramMessageProp) throws GSSException { this(paramInt1, paramKrb5Context, new ByteArrayInputStream(paramArrayOfByte, paramInt2, paramInt3), paramMessageProp); }
  
  MessageToken_v2(int paramInt, Krb5Context paramKrb5Context, InputStream paramInputStream, MessageProp paramMessageProp) throws GSSException {
    init(paramInt, paramKrb5Context);
    try {
      if (!this.confState)
        paramMessageProp.setPrivacy(false); 
      this.tokenHeader = new MessageTokenHeader(paramInputStream, paramMessageProp, paramInt);
      if (paramInt == 1284) {
        this.key_usage = !this.initiator ? 24 : 22;
      } else if (paramInt == 1028) {
        this.key_usage = !this.initiator ? 25 : 23;
      } 
      int i = 0;
      if (paramInt == 1284 && paramMessageProp.getPrivacy()) {
        i = 32 + this.cipherHelper.getChecksumLength();
      } else {
        i = this.cipherHelper.getChecksumLength();
      } 
      if (paramInt == 1028) {
        this.tokenDataLen = i;
        this.tokenData = new byte[i];
        readFully(paramInputStream, this.tokenData);
      } else {
        this.tokenDataLen = paramInputStream.available();
        if (this.tokenDataLen >= i) {
          this.tokenData = new byte[this.tokenDataLen];
          readFully(paramInputStream, this.tokenData);
        } else {
          byte[] arrayOfByte = new byte[i];
          readFully(paramInputStream, arrayOfByte);
          int j = paramInputStream.available();
          this.tokenDataLen = i + j;
          this.tokenData = Arrays.copyOf(arrayOfByte, this.tokenDataLen);
          readFully(paramInputStream, this.tokenData, i, j);
        } 
      } 
      if (paramInt == 1284)
        rotate(); 
      if (paramInt == 1028 || (paramInt == 1284 && !paramMessageProp.getPrivacy())) {
        int j = this.cipherHelper.getChecksumLength();
        this.checksum = new byte[j];
        System.arraycopy(this.tokenData, this.tokenDataLen - j, this.checksum, 0, j);
        if (paramInt == 1284 && !paramMessageProp.getPrivacy() && j != this.ec)
          throw new GSSException(10, -1, getTokenName(paramInt) + ":EC incorrect!"); 
      } 
    } catch (IOException iOException) {
      throw new GSSException(10, -1, getTokenName(paramInt) + ":" + iOException.getMessage());
    } 
  }
  
  public final int getTokenId() { return this.tokenId; }
  
  public final int getKeyUsage() { return this.key_usage; }
  
  public final boolean getConfState() { return this.confState; }
  
  public void genSignAndSeqNumber(MessageProp paramMessageProp, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GSSException {
    int i = paramMessageProp.getQOP();
    if (i != 0) {
      i = 0;
      paramMessageProp.setQOP(i);
    } 
    if (!this.confState)
      paramMessageProp.setPrivacy(false); 
    this.tokenHeader = new MessageTokenHeader(this.tokenId, paramMessageProp.getPrivacy());
    if (this.tokenId == 1284) {
      this.key_usage = this.initiator ? 24 : 22;
    } else if (this.tokenId == 1028) {
      this.key_usage = this.initiator ? 25 : 23;
    } 
    if (this.tokenId == 1028 || (!paramMessageProp.getPrivacy() && this.tokenId == 1284))
      this.checksum = getChecksum(paramArrayOfByte, paramInt1, paramInt2); 
    if (!paramMessageProp.getPrivacy() && this.tokenId == 1284) {
      byte[] arrayOfByte = this.tokenHeader.getBytes();
      arrayOfByte[4] = (byte)(this.checksum.length >>> 8);
      arrayOfByte[5] = (byte)this.checksum.length;
    } 
  }
  
  public final boolean verifySign(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GSSException {
    byte[] arrayOfByte = getChecksum(paramArrayOfByte, paramInt1, paramInt2);
    return MessageDigest.isEqual(this.checksum, arrayOfByte);
  }
  
  private void rotate() {
    if (this.rrc % this.tokenDataLen != 0) {
      this.rrc %= this.tokenDataLen;
      byte[] arrayOfByte = new byte[this.tokenDataLen];
      System.arraycopy(this.tokenData, this.rrc, arrayOfByte, 0, this.tokenDataLen - this.rrc);
      System.arraycopy(this.tokenData, 0, arrayOfByte, this.tokenDataLen - this.rrc, this.rrc);
      this.tokenData = arrayOfByte;
    } 
  }
  
  public final int getSequenceNumber() { return this.seqNumber; }
  
  byte[] getChecksum(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GSSException {
    byte[] arrayOfByte = this.tokenHeader.getBytes();
    byte b = arrayOfByte[2] & 0x2;
    if (b == 0 && this.tokenId == 1284) {
      arrayOfByte[4] = 0;
      arrayOfByte[5] = 0;
      arrayOfByte[6] = 0;
      arrayOfByte[7] = 0;
    } 
    return this.cipherHelper.calculateChecksum(arrayOfByte, paramArrayOfByte, paramInt1, paramInt2, this.key_usage);
  }
  
  MessageToken_v2(int paramInt, Krb5Context paramKrb5Context) throws GSSException {
    init(paramInt, paramKrb5Context);
    this.seqNumber = paramKrb5Context.incrementMySequenceNumber();
  }
  
  private void init(int paramInt, Krb5Context paramKrb5Context) throws GSSException {
    this.tokenId = paramInt;
    this.confState = paramKrb5Context.getConfState();
    this.initiator = paramKrb5Context.isInitiator();
    this.have_acceptor_subkey = (paramKrb5Context.getKeySrc() == 2);
    this.cipherHelper = paramKrb5Context.getCipherHelper(null);
  }
  
  protected void encodeHeader(OutputStream paramOutputStream) throws IOException { this.tokenHeader.encode(paramOutputStream); }
  
  public abstract void encode(OutputStream paramOutputStream) throws IOException;
  
  protected final byte[] getTokenHeader() { return this.tokenHeader.getBytes(); }
  
  class MessageTokenHeader {
    private int tokenId;
    
    private byte[] bytes = new byte[16];
    
    public MessageTokenHeader(int param1Int, boolean param1Boolean) throws GSSException {
      this.tokenId = param1Int;
      this.bytes[0] = (byte)(param1Int >>> 8);
      this.bytes[1] = (byte)param1Int;
      byte b = 0;
      b = (this$0.initiator ? 0 : 1) | ((param1Boolean && param1Int != 1028) ? 2 : 0) | (this$0.have_acceptor_subkey ? 4 : 0);
      this.bytes[2] = (byte)b;
      this.bytes[3] = -1;
      if (param1Int == 1284) {
        this.bytes[4] = 0;
        this.bytes[5] = 0;
        this.bytes[6] = 0;
        this.bytes[7] = 0;
      } else if (param1Int == 1028) {
        for (byte b1 = 4; b1 < 8; b1++)
          this.bytes[b1] = -1; 
      } 
      GSSToken.writeBigEndian(this$0.seqNumber, this.bytes, 12);
    }
    
    public MessageTokenHeader(InputStream param1InputStream, MessageProp param1MessageProp, int param1Int) throws IOException, GSSException {
      GSSToken.readFully(param1InputStream, this.bytes, 0, 16);
      this.tokenId = GSSToken.readInt(this.bytes, 0);
      if (this.tokenId != param1Int)
        throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Defective Token ID!"); 
      boolean bool = this$0.initiator ? 1 : 0;
      byte b1 = this.bytes[2] & true;
      if (b1 != bool)
        throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Acceptor Flag Error!"); 
      byte b2 = this.bytes[2] & 0x2;
      if (b2 == 2 && this.tokenId == 1284) {
        param1MessageProp.setPrivacy(true);
      } else {
        param1MessageProp.setPrivacy(false);
      } 
      if (this.tokenId == 1284) {
        if ((this.bytes[3] & 0xFF) != 255)
          throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Defective Token Filler!"); 
        this$0.ec = GSSToken.readBigEndian(this.bytes, 4, 2);
        this$0.rrc = GSSToken.readBigEndian(this.bytes, 6, 2);
      } else if (this.tokenId == 1028) {
        for (byte b = 3; b < 8; b++) {
          if ((this.bytes[b] & 0xFF) != 255)
            throw new GSSException(10, -1, Krb5Token.getTokenName(this.tokenId) + ":Defective Token Filler!"); 
        } 
      } 
      param1MessageProp.setQOP(0);
      this$0.seqNumber = GSSToken.readBigEndian(this.bytes, 0, 8);
    }
    
    public final void encode(OutputStream param1OutputStream) throws IOException { param1OutputStream.write(this.bytes); }
    
    public final int getTokenId() { return this.tokenId; }
    
    public final byte[] getBytes() { return this.bytes; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\MessageToken_v2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */