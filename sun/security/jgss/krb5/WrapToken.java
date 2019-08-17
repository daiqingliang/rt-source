package sun.security.jgss.krb5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSHeader;
import sun.security.krb5.Confounder;

class WrapToken extends MessageToken {
  static final int CONFOUNDER_SIZE = 8;
  
  static final byte[][] pads = { null, { 1 }, { 2, 2 }, { 3, 3, 3 }, { 4, 4, 4, 4 }, { 5, 5, 5, 5, 5 }, { 6, 6, 6, 6, 6, 6 }, { 7, 7, 7, 7, 7, 7, 7 }, { 8, 8, 8, 8, 8, 8, 8, 8 } };
  
  private boolean readTokenFromInputStream = true;
  
  private InputStream is = null;
  
  private byte[] tokenBytes = null;
  
  private int tokenOffset = 0;
  
  private int tokenLen = 0;
  
  private byte[] dataBytes = null;
  
  private int dataOffset = 0;
  
  private int dataLen = 0;
  
  private int dataSize = 0;
  
  byte[] confounder = null;
  
  byte[] padding = null;
  
  private boolean privacy = false;
  
  public WrapToken(Krb5Context paramKrb5Context, byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException {
    super(513, paramKrb5Context, paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
    this.readTokenFromInputStream = false;
    this.tokenBytes = paramArrayOfByte;
    this.tokenOffset = paramInt1;
    this.tokenLen = paramInt2;
    this.privacy = paramMessageProp.getPrivacy();
    this.dataSize = getGSSHeader().getMechTokenLength() - getKrb5TokenSize();
  }
  
  public WrapToken(Krb5Context paramKrb5Context, InputStream paramInputStream, MessageProp paramMessageProp) throws GSSException {
    super(513, paramKrb5Context, paramInputStream, paramMessageProp);
    this.is = paramInputStream;
    this.privacy = paramMessageProp.getPrivacy();
    this.dataSize = getGSSHeader().getMechTokenLength() - getTokenSize();
  }
  
  public byte[] getData() throws GSSException {
    byte[] arrayOfByte1 = new byte[this.dataSize];
    getData(arrayOfByte1, 0);
    byte[] arrayOfByte2 = new byte[this.dataSize - this.confounder.length - this.padding.length];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte2.length);
    return arrayOfByte2;
  }
  
  public int getData(byte[] paramArrayOfByte, int paramInt) throws GSSException {
    if (this.readTokenFromInputStream) {
      getDataFromStream(paramArrayOfByte, paramInt);
    } else {
      getDataFromBuffer(paramArrayOfByte, paramInt);
    } 
    return this.dataSize - this.confounder.length - this.padding.length;
  }
  
  private void getDataFromBuffer(byte[] paramArrayOfByte, int paramInt) throws GSSException {
    GSSHeader gSSHeader = getGSSHeader();
    int i = this.tokenOffset + gSSHeader.getLength() + getTokenSize();
    if (i + this.dataSize > this.tokenOffset + this.tokenLen)
      throw new GSSException(10, -1, "Insufficient data in " + getTokenName(getTokenId())); 
    this.confounder = new byte[8];
    if (this.privacy) {
      this.cipherHelper.decryptData(this, this.tokenBytes, i, this.dataSize, paramArrayOfByte, paramInt);
    } else {
      System.arraycopy(this.tokenBytes, i, this.confounder, 0, 8);
      byte b = this.tokenBytes[i + this.dataSize - 1];
      if (b < 0)
        b = 0; 
      if (b > 8)
        b %= 8; 
      this.padding = pads[b];
      System.arraycopy(this.tokenBytes, i + 8, paramArrayOfByte, paramInt, this.dataSize - 8 - b);
    } 
    if (!verifySignAndSeqNumber(this.confounder, paramArrayOfByte, paramInt, this.dataSize - 8 - this.padding.length, this.padding))
      throw new GSSException(6, -1, "Corrupt checksum or sequence number in Wrap token"); 
  }
  
  private void getDataFromStream(byte[] paramArrayOfByte, int paramInt) throws GSSException {
    GSSHeader gSSHeader = getGSSHeader();
    this.confounder = new byte[8];
    try {
      if (this.privacy) {
        this.cipherHelper.decryptData(this, this.is, this.dataSize, paramArrayOfByte, paramInt);
      } else {
        readFully(this.is, this.confounder);
        if (this.cipherHelper.isArcFour()) {
          this.padding = pads[1];
          readFully(this.is, paramArrayOfByte, paramInt, this.dataSize - 8 - 1);
        } else {
          int i = (this.dataSize - 8) / 8 - 1;
          int j = paramInt;
          for (byte b = 0; b < i; b++) {
            readFully(this.is, paramArrayOfByte, j, 8);
            j += 8;
          } 
          byte[] arrayOfByte = new byte[8];
          readFully(this.is, arrayOfByte);
          byte b1 = arrayOfByte[7];
          this.padding = pads[b1];
          System.arraycopy(arrayOfByte, 0, paramArrayOfByte, j, arrayOfByte.length - b1);
        } 
      } 
    } catch (IOException iOException) {
      throw new GSSException(10, -1, getTokenName(getTokenId()) + ": " + iOException.getMessage());
    } 
    if (!verifySignAndSeqNumber(this.confounder, paramArrayOfByte, paramInt, this.dataSize - 8 - this.padding.length, this.padding))
      throw new GSSException(6, -1, "Corrupt checksum or sequence number in Wrap token"); 
  }
  
  private byte[] getPadding(int paramInt) {
    int i = 0;
    if (this.cipherHelper.isArcFour()) {
      i = 1;
    } else {
      i = paramInt % 8;
      i = 8 - i;
    } 
    return pads[i];
  }
  
  public WrapToken(Krb5Context paramKrb5Context, MessageProp paramMessageProp, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GSSException {
    super(513, paramKrb5Context);
    this.confounder = Confounder.bytes(8);
    this.padding = getPadding(paramInt2);
    this.dataSize = this.confounder.length + paramInt2 + this.padding.length;
    this.dataBytes = paramArrayOfByte;
    this.dataOffset = paramInt1;
    this.dataLen = paramInt2;
    genSignAndSeqNumber(paramMessageProp, this.confounder, paramArrayOfByte, paramInt1, paramInt2, this.padding);
    if (!paramKrb5Context.getConfState())
      paramMessageProp.setPrivacy(false); 
    this.privacy = paramMessageProp.getPrivacy();
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException, GSSException {
    super.encode(paramOutputStream);
    if (!this.privacy) {
      paramOutputStream.write(this.confounder);
      paramOutputStream.write(this.dataBytes, this.dataOffset, this.dataLen);
      paramOutputStream.write(this.padding);
    } else {
      this.cipherHelper.encryptData(this, this.confounder, this.dataBytes, this.dataOffset, this.dataLen, this.padding, paramOutputStream);
    } 
  }
  
  public byte[] encode() throws GSSException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.dataSize + 50);
    encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public int encode(byte[] paramArrayOfByte, int paramInt) throws GSSException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    super.encode(byteArrayOutputStream);
    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt, arrayOfByte.length);
    paramInt += arrayOfByte.length;
    if (!this.privacy) {
      System.arraycopy(this.confounder, 0, paramArrayOfByte, paramInt, this.confounder.length);
      paramInt += this.confounder.length;
      System.arraycopy(this.dataBytes, this.dataOffset, paramArrayOfByte, paramInt, this.dataLen);
      paramInt += this.dataLen;
      System.arraycopy(this.padding, 0, paramArrayOfByte, paramInt, this.padding.length);
    } else {
      this.cipherHelper.encryptData(this, this.confounder, this.dataBytes, this.dataOffset, this.dataLen, this.padding, paramArrayOfByte, paramInt);
    } 
    return arrayOfByte.length + this.confounder.length + this.dataLen + this.padding.length;
  }
  
  protected int getKrb5TokenSize() throws GSSException { return getTokenSize() + this.dataSize; }
  
  protected int getSealAlg(boolean paramBoolean, int paramInt) throws GSSException { return !paramBoolean ? 65535 : this.cipherHelper.getSealAlg(); }
  
  static int getSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2, CipherHelper paramCipherHelper) throws GSSException { return GSSHeader.getMaxMechTokenSize(OID, paramInt2) - getTokenSize(paramCipherHelper) + 8 - 8; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\WrapToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */