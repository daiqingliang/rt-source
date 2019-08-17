package sun.security.jgss.krb5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;

class MicToken extends MessageToken {
  public MicToken(Krb5Context paramKrb5Context, byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException { super(257, paramKrb5Context, paramArrayOfByte, paramInt1, paramInt2, paramMessageProp); }
  
  public MicToken(Krb5Context paramKrb5Context, InputStream paramInputStream, MessageProp paramMessageProp) throws GSSException { super(257, paramKrb5Context, paramInputStream, paramMessageProp); }
  
  public void verify(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GSSException {
    if (!verifySignAndSeqNumber(null, paramArrayOfByte, paramInt1, paramInt2, null))
      throw new GSSException(6, -1, "Corrupt checksum or sequence number in MIC token"); 
  }
  
  public void verify(InputStream paramInputStream) throws GSSException {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = new byte[paramInputStream.available()];
      paramInputStream.read(arrayOfByte);
    } catch (IOException iOException) {
      throw new GSSException(6, -1, "Corrupt checksum or sequence number in MIC token");
    } 
    verify(arrayOfByte, 0, arrayOfByte.length);
  }
  
  public MicToken(Krb5Context paramKrb5Context, MessageProp paramMessageProp, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GSSException {
    super(257, paramKrb5Context);
    if (paramMessageProp == null)
      paramMessageProp = new MessageProp(0, false); 
    genSignAndSeqNumber(paramMessageProp, null, paramArrayOfByte, paramInt1, paramInt2, null);
  }
  
  public MicToken(Krb5Context paramKrb5Context, MessageProp paramMessageProp, InputStream paramInputStream) throws GSSException, IOException {
    super(257, paramKrb5Context);
    byte[] arrayOfByte = new byte[paramInputStream.available()];
    paramInputStream.read(arrayOfByte);
    if (paramMessageProp == null)
      paramMessageProp = new MessageProp(0, false); 
    genSignAndSeqNumber(paramMessageProp, null, arrayOfByte, 0, arrayOfByte.length, null);
  }
  
  protected int getSealAlg(boolean paramBoolean, int paramInt) { return 65535; }
  
  public int encode(byte[] paramArrayOfByte, int paramInt) throws IOException, GSSException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    encode(byteArrayOutputStream);
    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  public byte[] encode() throws IOException, GSSException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(50);
    encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\MicToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */