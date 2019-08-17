package sun.security.krb5.internal.rcache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class AuthTime {
  final int ctime;
  
  final int cusec;
  
  final String client;
  
  final String server;
  
  public AuthTime(String paramString1, String paramString2, int paramInt1, int paramInt2) {
    this.ctime = paramInt1;
    this.cusec = paramInt2;
    this.client = paramString1;
    this.server = paramString2;
  }
  
  public String toString() { return String.format("%d/%06d/----/%s", new Object[] { Integer.valueOf(this.ctime), Integer.valueOf(this.cusec), this.client }); }
  
  private static String readStringWithLength(SeekableByteChannel paramSeekableByteChannel) throws IOException {
    ByteBuffer byteBuffer = ByteBuffer.allocate(4);
    byteBuffer.order(ByteOrder.nativeOrder());
    paramSeekableByteChannel.read(byteBuffer);
    byteBuffer.flip();
    int i = byteBuffer.getInt();
    if (i > 1024)
      throw new IOException("Invalid string length"); 
    byteBuffer = ByteBuffer.allocate(i);
    if (paramSeekableByteChannel.read(byteBuffer) != i)
      throw new IOException("Not enough string"); 
    byte[] arrayOfByte = byteBuffer.array();
    return (arrayOfByte[i - 1] == 0) ? new String(arrayOfByte, 0, i - 1, StandardCharsets.UTF_8) : new String(arrayOfByte, StandardCharsets.UTF_8);
  }
  
  public static AuthTime readFrom(SeekableByteChannel paramSeekableByteChannel) throws IOException {
    String str1 = readStringWithLength(paramSeekableByteChannel);
    String str2 = readStringWithLength(paramSeekableByteChannel);
    ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    paramSeekableByteChannel.read(byteBuffer);
    byteBuffer.order(ByteOrder.nativeOrder());
    int i = byteBuffer.getInt(0);
    int j = byteBuffer.getInt(4);
    if (str1.isEmpty()) {
      StringTokenizer stringTokenizer = new StringTokenizer(str2, " :");
      if (stringTokenizer.countTokens() != 6)
        throw new IOException("Incorrect rcache style"); 
      stringTokenizer.nextToken();
      String str = stringTokenizer.nextToken();
      stringTokenizer.nextToken();
      str1 = stringTokenizer.nextToken();
      stringTokenizer.nextToken();
      str2 = stringTokenizer.nextToken();
      return new AuthTimeWithHash(str1, str2, j, i, str);
    } 
    return new AuthTime(str1, str2, j, i);
  }
  
  protected byte[] encode0(String paramString1, String paramString2) {
    byte[] arrayOfByte1 = paramString1.getBytes(StandardCharsets.UTF_8);
    byte[] arrayOfByte2 = paramString2.getBytes(StandardCharsets.UTF_8);
    byte[] arrayOfByte3 = new byte[1];
    int i = 4 + arrayOfByte1.length + 1 + 4 + arrayOfByte2.length + 1 + 4 + 4;
    ByteBuffer byteBuffer = ByteBuffer.allocate(i).order(ByteOrder.nativeOrder());
    byteBuffer.putInt(arrayOfByte1.length + 1).put(arrayOfByte1).put(arrayOfByte3).putInt(arrayOfByte2.length + 1).put(arrayOfByte2).put(arrayOfByte3).putInt(this.cusec).putInt(this.ctime);
    return byteBuffer.array();
  }
  
  public byte[] encode(boolean paramBoolean) { return encode0(this.client, this.server); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\rcache\AuthTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */