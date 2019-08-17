package javax.sound.midi;

public class SysexMessage extends MidiMessage {
  public static final int SYSTEM_EXCLUSIVE = 240;
  
  public static final int SPECIAL_SYSTEM_EXCLUSIVE = 247;
  
  public SysexMessage() {
    this(new byte[2]);
    this.data[0] = -16;
    this.data[1] = -9;
  }
  
  public SysexMessage(byte[] paramArrayOfByte, int paramInt) throws InvalidMidiDataException {
    super(null);
    setMessage(paramArrayOfByte, paramInt);
  }
  
  public SysexMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2) throws InvalidMidiDataException {
    super(null);
    setMessage(paramInt1, paramArrayOfByte, paramInt2);
  }
  
  protected SysexMessage(byte[] paramArrayOfByte) { super(paramArrayOfByte); }
  
  public void setMessage(byte[] paramArrayOfByte, int paramInt) throws InvalidMidiDataException {
    byte b = paramArrayOfByte[0] & 0xFF;
    if (b != 240 && b != 247)
      throw new InvalidMidiDataException("Invalid status byte for sysex message: 0x" + Integer.toHexString(b)); 
    super.setMessage(paramArrayOfByte, paramInt);
  }
  
  public void setMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2) throws InvalidMidiDataException {
    if (paramInt1 != 240 && paramInt1 != 247)
      throw new InvalidMidiDataException("Invalid status byte for sysex message: 0x" + Integer.toHexString(paramInt1)); 
    if (paramInt2 < 0 || paramInt2 > paramArrayOfByte.length)
      throw new IndexOutOfBoundsException("length out of bounds: " + paramInt2); 
    this.length = paramInt2 + 1;
    if (this.data == null || this.data.length < this.length)
      this.data = new byte[this.length]; 
    this.data[0] = (byte)(paramInt1 & 0xFF);
    if (paramInt2 > 0)
      System.arraycopy(paramArrayOfByte, 0, this.data, 1, paramInt2); 
  }
  
  public byte[] getData() {
    byte[] arrayOfByte = new byte[this.length - 1];
    System.arraycopy(this.data, 1, arrayOfByte, 0, this.length - 1);
    return arrayOfByte;
  }
  
  public Object clone() {
    byte[] arrayOfByte = new byte[this.length];
    System.arraycopy(this.data, 0, arrayOfByte, 0, arrayOfByte.length);
    return new SysexMessage(arrayOfByte);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\SysexMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */