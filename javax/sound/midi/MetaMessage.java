package javax.sound.midi;

public class MetaMessage extends MidiMessage {
  public static final int META = 255;
  
  private int dataLength = 0;
  
  private static final long mask = 127L;
  
  public MetaMessage() { this(new byte[] { -1, 0 }); }
  
  public MetaMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2) throws InvalidMidiDataException {
    super(null);
    setMessage(paramInt1, paramArrayOfByte, paramInt2);
  }
  
  protected MetaMessage(byte[] paramArrayOfByte) {
    super(paramArrayOfByte);
    if (paramArrayOfByte.length >= 3) {
      this.dataLength = paramArrayOfByte.length - 3;
      for (byte b = 2; b < paramArrayOfByte.length && (paramArrayOfByte[b] & 0x80) != 0; b++)
        this.dataLength--; 
    } 
  }
  
  public void setMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2) throws InvalidMidiDataException {
    if (paramInt1 >= 128 || paramInt1 < 0)
      throw new InvalidMidiDataException("Invalid meta event with type " + paramInt1); 
    if ((paramInt2 > 0 && paramInt2 > paramArrayOfByte.length) || paramInt2 < 0)
      throw new InvalidMidiDataException("length out of bounds: " + paramInt2); 
    this.length = 2 + getVarIntLength(paramInt2) + paramInt2;
    this.dataLength = paramInt2;
    this.data = new byte[this.length];
    this.data[0] = -1;
    this.data[1] = (byte)paramInt1;
    writeVarInt(this.data, 2, paramInt2);
    if (paramInt2 > 0)
      System.arraycopy(paramArrayOfByte, 0, this.data, this.length - this.dataLength, this.dataLength); 
  }
  
  public int getType() { return (this.length >= 2) ? (this.data[1] & 0xFF) : 0; }
  
  public byte[] getData() {
    byte[] arrayOfByte = new byte[this.dataLength];
    System.arraycopy(this.data, this.length - this.dataLength, arrayOfByte, 0, this.dataLength);
    return arrayOfByte;
  }
  
  public Object clone() {
    byte[] arrayOfByte = new byte[this.length];
    System.arraycopy(this.data, 0, arrayOfByte, 0, arrayOfByte.length);
    return new MetaMessage(arrayOfByte);
  }
  
  private int getVarIntLength(long paramLong) {
    byte b = 0;
    do {
      paramLong >>= 7;
      b++;
    } while (paramLong > 0L);
    return b;
  }
  
  private void writeVarInt(byte[] paramArrayOfByte, int paramInt, long paramLong) {
    byte b;
    for (b = 63; b > 0 && (paramLong & 127L << b) == 0L; b -= 7);
    while (b > 0) {
      paramArrayOfByte[paramInt++] = (byte)(int)((paramLong & 127L << b) >> b | 0x80L);
      b -= 7;
    } 
    paramArrayOfByte[paramInt] = (byte)(int)(paramLong & 0x7FL);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\MetaMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */