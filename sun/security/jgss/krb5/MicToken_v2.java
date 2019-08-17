package sun.security.jgss.krb5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;

class MicToken_v2 extends MessageToken_v2 {
  public MicToken_v2(Krb5Context paramKrb5Context, byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp) throws GSSException { super(1028, paramKrb5Context, paramArrayOfByte, paramInt1, paramInt2, paramMessageProp); }
  
  public MicToken_v2(Krb5Context paramKrb5Context, InputStream paramInputStream, MessageProp paramMessageProp) throws GSSException { super(1028, paramKrb5Context, paramInputStream, paramMessageProp); }
  
  public void verify(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GSSException {
    if (!verifySign(paramArrayOfByte, paramInt1, paramInt2))
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
  
  public MicToken_v2(Krb5Context paramKrb5Context, MessageProp paramMessageProp, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws GSSException {
    super(1028, paramKrb5Context);
    if (paramMessageProp == null)
      paramMessageProp = new MessageProp(0, false); 
    genSignAndSeqNumber(paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public MicToken_v2(Krb5Context paramKrb5Context, MessageProp paramMessageProp, InputStream paramInputStream) throws GSSException, IOException {
    super(1028, paramKrb5Context);
    byte[] arrayOfByte = new byte[paramInputStream.available()];
    paramInputStream.read(arrayOfByte);
    if (paramMessageProp == null)
      paramMessageProp = new MessageProp(0, false); 
    genSignAndSeqNumber(paramMessageProp, arrayOfByte, 0, arrayOfByte.length);
  }
  
  public byte[] encode() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(50);
    encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public int encode(byte[] paramArrayOfByte, int paramInt) throws IOException {
    byte[] arrayOfByte = encode();
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    encodeHeader(paramOutputStream);
    paramOutputStream.write(this.checksum);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\MicToken_v2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */