package sun.security.x509;

import java.io.IOException;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class UniqueIdentity {
  private BitArray id;
  
  public UniqueIdentity(BitArray paramBitArray) { this.id = paramBitArray; }
  
  public UniqueIdentity(byte[] paramArrayOfByte) { this.id = new BitArray(paramArrayOfByte.length * 8, paramArrayOfByte); }
  
  public UniqueIdentity(DerInputStream paramDerInputStream) throws IOException {
    DerValue derValue = paramDerInputStream.getDerValue();
    this.id = derValue.getUnalignedBitString(true);
  }
  
  public UniqueIdentity(DerValue paramDerValue) throws IOException { this.id = paramDerValue.getUnalignedBitString(true); }
  
  public String toString() { return "UniqueIdentity:" + this.id.toString() + "\n"; }
  
  public void encode(DerOutputStream paramDerOutputStream, byte paramByte) throws IOException {
    byte[] arrayOfByte = this.id.toByteArray();
    int i = arrayOfByte.length * 8 - this.id.length();
    paramDerOutputStream.write(paramByte);
    paramDerOutputStream.putLength(arrayOfByte.length + 1);
    paramDerOutputStream.write(i);
    paramDerOutputStream.write(arrayOfByte);
  }
  
  public boolean[] getId() { return (this.id == null) ? null : this.id.toBooleanArray(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\UniqueIdentity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */