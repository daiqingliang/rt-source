package sun.security.provider;

public final class SHA extends DigestBase {
  private int[] W = new int[80];
  
  private int[] state = new int[5];
  
  private static final int round1_kt = 1518500249;
  
  private static final int round2_kt = 1859775393;
  
  private static final int round3_kt = -1894007588;
  
  private static final int round4_kt = -899497514;
  
  public SHA() {
    super("SHA-1", 20, 64);
    implReset();
  }
  
  public Object clone() throws CloneNotSupportedException {
    SHA sHA = (SHA)super.clone();
    sHA.state = (int[])sHA.state.clone();
    sHA.W = new int[80];
    return sHA;
  }
  
  void implReset() {
    this.state[0] = 1732584193;
    this.state[1] = -271733879;
    this.state[2] = -1732584194;
    this.state[3] = 271733878;
    this.state[4] = -1009589776;
  }
  
  void implDigest(byte[] paramArrayOfByte, int paramInt) {
    long l = this.bytesProcessed << 3;
    int i = (int)this.bytesProcessed & 0x3F;
    int j = (i < 56) ? (56 - i) : (120 - i);
    engineUpdate(padding, 0, j);
    ByteArrayAccess.i2bBig4((int)(l >>> 32), this.buffer, 56);
    ByteArrayAccess.i2bBig4((int)l, this.buffer, 60);
    implCompress(this.buffer, 0);
    ByteArrayAccess.i2bBig(this.state, 0, paramArrayOfByte, paramInt, 20);
  }
  
  void implCompress(byte[] paramArrayOfByte, int paramInt) {
    ByteArrayAccess.b2iBig64(paramArrayOfByte, paramInt, this.W);
    int i;
    for (i = 16; i <= 79; i++) {
      int i1 = this.W[i - 3] ^ this.W[i - 8] ^ this.W[i - 14] ^ this.W[i - 16];
      this.W[i] = i1 << 1 | i1 >>> 31;
    } 
    i = this.state[0];
    int j = this.state[1];
    int k = this.state[2];
    int m = this.state[3];
    int n = this.state[4];
    byte b;
    for (b = 0; b < 20; b++) {
      int i1 = (i << 5 | i >>> 27) + (j & k | (j ^ 0xFFFFFFFF) & m) + n + this.W[b] + 1518500249;
      n = m;
      m = k;
      k = j << 30 | j >>> 2;
      j = i;
      i = i1;
    } 
    for (b = 20; b < 40; b++) {
      int i1 = (i << 5 | i >>> 27) + (j ^ k ^ m) + n + this.W[b] + 1859775393;
      n = m;
      m = k;
      k = j << 30 | j >>> 2;
      j = i;
      i = i1;
    } 
    for (b = 40; b < 60; b++) {
      int i1 = (i << 5 | i >>> 27) + (j & k | j & m | k & m) + n + this.W[b] + -1894007588;
      n = m;
      m = k;
      k = j << 30 | j >>> 2;
      j = i;
      i = i1;
    } 
    for (b = 60; b < 80; b++) {
      int i1 = (i << 5 | i >>> 27) + (j ^ k ^ m) + n + this.W[b] + -899497514;
      n = m;
      m = k;
      k = j << 30 | j >>> 2;
      j = i;
      i = i1;
    } 
    this.state[0] = this.state[0] + i;
    this.state[1] = this.state[1] + j;
    this.state[2] = this.state[2] + k;
    this.state[3] = this.state[3] + m;
    this.state[4] = this.state[4] + n;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\SHA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */