package sun.security.krb5.internal.util;

import java.io.IOException;
import java.util.Arrays;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;

public class KerberosFlags {
  BitArray bits;
  
  protected static final int BITS_PER_UNIT = 8;
  
  public KerberosFlags(int paramInt) throws IllegalArgumentException { this.bits = new BitArray(paramInt); }
  
  public KerberosFlags(int paramInt, byte[] paramArrayOfByte) throws IllegalArgumentException {
    this.bits = new BitArray(paramInt, paramArrayOfByte);
    if (paramInt != 32)
      this.bits = new BitArray(Arrays.copyOf(this.bits.toBooleanArray(), 32)); 
  }
  
  public KerberosFlags(boolean[] paramArrayOfBoolean) { this.bits = new BitArray((paramArrayOfBoolean.length == 32) ? paramArrayOfBoolean : Arrays.copyOf(paramArrayOfBoolean, 32)); }
  
  public void set(int paramInt, boolean paramBoolean) { this.bits.set(paramInt, paramBoolean); }
  
  public boolean get(int paramInt) { return this.bits.get(paramInt); }
  
  public boolean[] toBooleanArray() { return this.bits.toBooleanArray(); }
  
  public byte[] asn1Encode() throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putUnalignedBitString(this.bits);
    return derOutputStream.toByteArray();
  }
  
  public String toString() { return this.bits.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\interna\\util\KerberosFlags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */