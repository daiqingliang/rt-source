package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import sun.corba.SharedSecrets;

public class StubIORImpl {
  private int hashCode;
  
  private byte[] typeData;
  
  private int[] profileTags;
  
  private byte[][] profileData;
  
  public StubIORImpl() {
    this.hashCode = 0;
    this.typeData = null;
    this.profileTags = null;
    this.profileData = (byte[][])null;
  }
  
  public String getRepositoryId() { return (this.typeData == null) ? null : new String(this.typeData); }
  
  public StubIORImpl(Object paramObject) {
    OutputStream outputStream = StubAdapter.getORB(paramObject).create_output_stream();
    outputStream.write_Object(paramObject);
    InputStream inputStream = outputStream.create_input_stream();
    int i = inputStream.read_long();
    this.typeData = new byte[i];
    inputStream.read_octet_array(this.typeData, 0, i);
    int j = inputStream.read_long();
    this.profileTags = new int[j];
    this.profileData = new byte[j][];
    for (byte b = 0; b < j; b++) {
      this.profileTags[b] = inputStream.read_long();
      this.profileData[b] = new byte[inputStream.read_long()];
      inputStream.read_octet_array(this.profileData[b], 0, this.profileData[b].length);
    } 
  }
  
  public Delegate getDelegate(ORB paramORB) {
    OutputStream outputStream = paramORB.create_output_stream();
    outputStream.write_long(this.typeData.length);
    outputStream.write_octet_array(this.typeData, 0, this.typeData.length);
    outputStream.write_long(this.profileTags.length);
    for (byte b = 0; b < this.profileTags.length; b++) {
      outputStream.write_long(this.profileTags[b]);
      outputStream.write_long(this.profileData[b].length);
      outputStream.write_octet_array(this.profileData[b], 0, this.profileData[b].length);
    } 
    InputStream inputStream = outputStream.create_input_stream();
    Object object = inputStream.read_Object();
    return StubAdapter.getDelegate(object);
  }
  
  public void doRead(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    int i = paramObjectInputStream.readInt();
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, byte[].class, i);
    this.typeData = new byte[i];
    paramObjectInputStream.readFully(this.typeData);
    int j = paramObjectInputStream.readInt();
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, int[].class, j);
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, byte[].class, j);
    this.profileTags = new int[j];
    this.profileData = new byte[j][];
    for (byte b = 0; b < j; b++) {
      this.profileTags[b] = paramObjectInputStream.readInt();
      int k = paramObjectInputStream.readInt();
      SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, byte[].class, k);
      this.profileData[b] = new byte[k];
      paramObjectInputStream.readFully(this.profileData[b]);
    } 
  }
  
  public void doWrite(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeInt(this.typeData.length);
    paramObjectOutputStream.write(this.typeData);
    paramObjectOutputStream.writeInt(this.profileTags.length);
    for (byte b = 0; b < this.profileTags.length; b++) {
      paramObjectOutputStream.writeInt(this.profileTags[b]);
      paramObjectOutputStream.writeInt(this.profileData[b].length);
      paramObjectOutputStream.write(this.profileData[b]);
    } 
  }
  
  public int hashCode() {
    if (this.hashCode == 0) {
      byte b;
      for (b = 0; b < this.typeData.length; b++)
        this.hashCode = this.hashCode * 37 + this.typeData[b]; 
      for (b = 0; b < this.profileTags.length; b++) {
        this.hashCode = this.hashCode * 37 + this.profileTags[b];
        for (byte b1 = 0; b1 < this.profileData[b].length; b1++)
          this.hashCode = this.hashCode * 37 + this.profileData[b][b1]; 
      } 
    } 
    return this.hashCode;
  }
  
  private boolean equalArrays(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    if (paramArrayOfInt1.length != paramArrayOfInt2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfInt1.length; b++) {
      if (paramArrayOfInt1[b] != paramArrayOfInt2[b])
        return false; 
    } 
    return true;
  }
  
  private boolean equalArrays(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    if (paramArrayOfByte1.length != paramArrayOfByte2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfByte1.length; b++) {
      if (paramArrayOfByte1[b] != paramArrayOfByte2[b])
        return false; 
    } 
    return true;
  }
  
  private boolean equalArrays(byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2) {
    if (paramArrayOfByte1.length != paramArrayOfByte2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfByte1.length; b++) {
      if (!equalArrays(paramArrayOfByte1[b], paramArrayOfByte2[b]))
        return false; 
    } 
    return true;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof StubIORImpl))
      return false; 
    StubIORImpl stubIORImpl = (StubIORImpl)paramObject;
    return (stubIORImpl.hashCode() != hashCode()) ? false : ((equalArrays(this.typeData, stubIORImpl.typeData) && equalArrays(this.profileTags, stubIORImpl.profileTags) && equalArrays(this.profileData, stubIORImpl.profileData)));
  }
  
  private void appendByteArray(StringBuffer paramStringBuffer, byte[] paramArrayOfByte) {
    for (byte b = 0; b < paramArrayOfByte.length; b++)
      paramStringBuffer.append(Integer.toHexString(paramArrayOfByte[b])); 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("SimpleIORImpl[");
    String str = new String(this.typeData);
    stringBuffer.append(str);
    for (byte b = 0; b < this.profileTags.length; b++) {
      stringBuffer.append(",(");
      stringBuffer.append(this.profileTags[b]);
      stringBuffer.append(")");
      appendByteArray(stringBuffer, this.profileData[b]);
    } 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\StubIORImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */