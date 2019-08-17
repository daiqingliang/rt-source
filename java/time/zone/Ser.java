package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
import java.time.ZoneOffset;

final class Ser implements Externalizable {
  private static final long serialVersionUID = -8885321777449118786L;
  
  static final byte ZRULES = 1;
  
  static final byte ZOT = 2;
  
  static final byte ZOTRULE = 3;
  
  private byte type;
  
  private Object object;
  
  public Ser() {}
  
  Ser(byte paramByte, Object paramObject) {
    this.type = paramByte;
    this.object = paramObject;
  }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException { writeInternal(this.type, this.object, paramObjectOutput); }
  
  static void write(Object paramObject, DataOutput paramDataOutput) throws IOException { writeInternal((byte)1, paramObject, paramDataOutput); }
  
  private static void writeInternal(byte paramByte, Object paramObject, DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeByte(paramByte);
    switch (paramByte) {
      case 1:
        ((ZoneRules)paramObject).writeExternal(paramDataOutput);
        return;
      case 2:
        ((ZoneOffsetTransition)paramObject).writeExternal(paramDataOutput);
        return;
      case 3:
        ((ZoneOffsetTransitionRule)paramObject).writeExternal(paramDataOutput);
        return;
    } 
    throw new InvalidClassException("Unknown serialized type");
  }
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    this.type = paramObjectInput.readByte();
    this.object = readInternal(this.type, paramObjectInput);
  }
  
  static Object read(DataInput paramDataInput) throws IOException, ClassNotFoundException {
    byte b = paramDataInput.readByte();
    return readInternal(b, paramDataInput);
  }
  
  private static Object readInternal(byte paramByte, DataInput paramDataInput) throws IOException, ClassNotFoundException {
    switch (paramByte) {
      case 1:
        return ZoneRules.readExternal(paramDataInput);
      case 2:
        return ZoneOffsetTransition.readExternal(paramDataInput);
      case 3:
        return ZoneOffsetTransitionRule.readExternal(paramDataInput);
    } 
    throw new StreamCorruptedException("Unknown serialized type");
  }
  
  private Object readResolve() { return this.object; }
  
  static void writeOffset(ZoneOffset paramZoneOffset, DataOutput paramDataOutput) throws IOException {
    int i = paramZoneOffset.getTotalSeconds();
    int j = (i % 900 == 0) ? (i / 900) : 127;
    paramDataOutput.writeByte(j);
    if (j == 127)
      paramDataOutput.writeInt(i); 
  }
  
  static ZoneOffset readOffset(DataInput paramDataInput) throws IOException {
    byte b = paramDataInput.readByte();
    return (b == Byte.MAX_VALUE) ? ZoneOffset.ofTotalSeconds(paramDataInput.readInt()) : ZoneOffset.ofTotalSeconds(b * 900);
  }
  
  static void writeEpochSec(long paramLong, DataOutput paramDataOutput) throws IOException {
    if (paramLong >= -4575744000L && paramLong < 10413792000L && paramLong % 900L == 0L) {
      int i = (int)((paramLong + 4575744000L) / 900L);
      paramDataOutput.writeByte(i >>> 16 & 0xFF);
      paramDataOutput.writeByte(i >>> 8 & 0xFF);
      paramDataOutput.writeByte(i & 0xFF);
    } else {
      paramDataOutput.writeByte(255);
      paramDataOutput.writeLong(paramLong);
    } 
  }
  
  static long readEpochSec(DataInput paramDataInput) throws IOException {
    byte b1 = paramDataInput.readByte() & 0xFF;
    if (b1 == 255)
      return paramDataInput.readLong(); 
    byte b2 = paramDataInput.readByte() & 0xFF;
    byte b3 = paramDataInput.readByte() & 0xFF;
    long l = ((b1 << 16) + (b2 << 8) + b3);
    return l * 900L - 4575744000L;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\zone\Ser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */