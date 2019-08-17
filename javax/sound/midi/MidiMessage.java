package javax.sound.midi;

public abstract class MidiMessage implements Cloneable {
  protected byte[] data;
  
  protected int length = 0;
  
  protected MidiMessage(byte[] paramArrayOfByte) {
    this.data = paramArrayOfByte;
    if (paramArrayOfByte != null)
      this.length = paramArrayOfByte.length; 
  }
  
  protected void setMessage(byte[] paramArrayOfByte, int paramInt) throws InvalidMidiDataException {
    if (paramInt < 0 || (paramInt > 0 && paramInt > paramArrayOfByte.length))
      throw new IndexOutOfBoundsException("length out of bounds: " + paramInt); 
    this.length = paramInt;
    if (this.data == null || this.data.length < this.length)
      this.data = new byte[this.length]; 
    System.arraycopy(paramArrayOfByte, 0, this.data, 0, paramInt);
  }
  
  public byte[] getMessage() {
    byte[] arrayOfByte = new byte[this.length];
    System.arraycopy(this.data, 0, arrayOfByte, 0, this.length);
    return arrayOfByte;
  }
  
  public int getStatus() { return (this.length > 0) ? (this.data[0] & 0xFF) : 0; }
  
  public int getLength() { return this.length; }
  
  public abstract Object clone();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\MidiMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */