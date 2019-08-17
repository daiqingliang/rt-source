package sun.management.counter.perf;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class PerfDataEntry {
  private String name;
  
  private int entryStart;
  
  private int entryLength;
  
  private int vectorLength;
  
  private PerfDataType dataType;
  
  private int flags;
  
  private Units unit;
  
  private Variability variability;
  
  private int dataOffset;
  
  private int dataSize;
  
  private ByteBuffer data;
  
  PerfDataEntry(ByteBuffer paramByteBuffer) {
    this.entryStart = paramByteBuffer.position();
    this.entryLength = paramByteBuffer.getInt();
    if (this.entryLength <= 0 || this.entryLength > paramByteBuffer.limit())
      throw new InstrumentationException("Invalid entry length:  entryLength = " + this.entryLength); 
    if (this.entryStart + this.entryLength > paramByteBuffer.limit())
      throw new InstrumentationException("Entry extends beyond end of buffer:  entryStart = " + this.entryStart + " entryLength = " + this.entryLength + " buffer limit = " + paramByteBuffer.limit()); 
    paramByteBuffer.position(this.entryStart + 4);
    int i = paramByteBuffer.getInt();
    if (this.entryStart + i > paramByteBuffer.limit())
      throw new InstrumentationException("Invalid name offset:  entryStart = " + this.entryStart + " nameOffset = " + i + " buffer limit = " + paramByteBuffer.limit()); 
    paramByteBuffer.position(this.entryStart + 8);
    this.vectorLength = paramByteBuffer.getInt();
    paramByteBuffer.position(this.entryStart + 12);
    this.dataType = PerfDataType.toPerfDataType(paramByteBuffer.get());
    paramByteBuffer.position(this.entryStart + 13);
    this.flags = paramByteBuffer.get();
    paramByteBuffer.position(this.entryStart + 14);
    this.unit = Units.toUnits(paramByteBuffer.get());
    paramByteBuffer.position(this.entryStart + 15);
    this.variability = Variability.toVariability(paramByteBuffer.get());
    paramByteBuffer.position(this.entryStart + 16);
    this.dataOffset = paramByteBuffer.getInt();
    paramByteBuffer.position(this.entryStart + i);
    byte b1;
    byte b;
    for (b1 = 0; (b = paramByteBuffer.get()) != 0; b1++);
    byte[] arrayOfByte = new byte[b1];
    paramByteBuffer.position(this.entryStart + i);
    for (b2 = 0; b2 < b1; b2++)
      arrayOfByte[b2] = paramByteBuffer.get(); 
    try {
      this.name = new String(arrayOfByte, "UTF-8");
    } catch (UnsupportedEncodingException b2) {
      UnsupportedEncodingException unsupportedEncodingException;
      throw new InternalError(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
    } 
    if (this.variability == Variability.INVALID)
      throw new InstrumentationException("Invalid variability attribute: name = " + this.name); 
    if (this.unit == Units.INVALID)
      throw new InstrumentationException("Invalid units attribute:  name = " + this.name); 
    if (this.vectorLength > 0) {
      this.dataSize = this.vectorLength * this.dataType.size();
    } else {
      this.dataSize = this.dataType.size();
    } 
    if (this.entryStart + this.dataOffset + this.dataSize > paramByteBuffer.limit())
      throw new InstrumentationException("Data extends beyond end of buffer:  entryStart = " + this.entryStart + " dataOffset = " + this.dataOffset + " dataSize = " + this.dataSize + " buffer limit = " + paramByteBuffer.limit()); 
    paramByteBuffer.position(this.entryStart + this.dataOffset);
    this.data = paramByteBuffer.slice();
    this.data.order(paramByteBuffer.order());
    this.data.limit(this.dataSize);
  }
  
  public int size() { return this.entryLength; }
  
  public String name() { return this.name; }
  
  public PerfDataType type() { return this.dataType; }
  
  public Units units() { return this.unit; }
  
  public int flags() { return this.flags; }
  
  public int vectorLength() { return this.vectorLength; }
  
  public Variability variability() { return this.variability; }
  
  public ByteBuffer byteData() {
    this.data.position(0);
    assert this.data.remaining() == vectorLength();
    return this.data.duplicate();
  }
  
  public LongBuffer longData() { return this.data.asLongBuffer(); }
  
  private class EntryFieldOffset {
    private static final int SIZEOF_BYTE = 1;
    
    private static final int SIZEOF_INT = 4;
    
    private static final int SIZEOF_LONG = 8;
    
    private static final int ENTRY_LENGTH_SIZE = 4;
    
    private static final int NAME_OFFSET_SIZE = 4;
    
    private static final int VECTOR_LENGTH_SIZE = 4;
    
    private static final int DATA_TYPE_SIZE = 1;
    
    private static final int FLAGS_SIZE = 1;
    
    private static final int DATA_UNIT_SIZE = 1;
    
    private static final int DATA_VAR_SIZE = 1;
    
    private static final int DATA_OFFSET_SIZE = 4;
    
    static final int ENTRY_LENGTH = 0;
    
    static final int NAME_OFFSET = 4;
    
    static final int VECTOR_LENGTH = 8;
    
    static final int DATA_TYPE = 12;
    
    static final int FLAGS = 13;
    
    static final int DATA_UNIT = 14;
    
    static final int DATA_VAR = 15;
    
    static final int DATA_OFFSET = 16;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\counter\perf\PerfDataEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */