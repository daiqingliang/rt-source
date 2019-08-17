package javax.sound.midi;

public class ShortMessage extends MidiMessage {
  public static final int MIDI_TIME_CODE = 241;
  
  public static final int SONG_POSITION_POINTER = 242;
  
  public static final int SONG_SELECT = 243;
  
  public static final int TUNE_REQUEST = 246;
  
  public static final int END_OF_EXCLUSIVE = 247;
  
  public static final int TIMING_CLOCK = 248;
  
  public static final int START = 250;
  
  public static final int CONTINUE = 251;
  
  public static final int STOP = 252;
  
  public static final int ACTIVE_SENSING = 254;
  
  public static final int SYSTEM_RESET = 255;
  
  public static final int NOTE_OFF = 128;
  
  public static final int NOTE_ON = 144;
  
  public static final int POLY_PRESSURE = 160;
  
  public static final int CONTROL_CHANGE = 176;
  
  public static final int PROGRAM_CHANGE = 192;
  
  public static final int CHANNEL_PRESSURE = 208;
  
  public static final int PITCH_BEND = 224;
  
  public ShortMessage() {
    this(new byte[3]);
    this.data[0] = -112;
    this.data[1] = 64;
    this.data[2] = Byte.MAX_VALUE;
    this.length = 3;
  }
  
  public ShortMessage(int paramInt) throws InvalidMidiDataException {
    super(null);
    setMessage(paramInt);
  }
  
  public ShortMessage(int paramInt1, int paramInt2, int paramInt3) throws InvalidMidiDataException {
    super(null);
    setMessage(paramInt1, paramInt2, paramInt3);
  }
  
  public ShortMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws InvalidMidiDataException {
    super(null);
    setMessage(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected ShortMessage(byte[] paramArrayOfByte) { super(paramArrayOfByte); }
  
  public void setMessage(int paramInt) throws InvalidMidiDataException {
    int i = getDataLength(paramInt);
    if (i != 0)
      throw new InvalidMidiDataException("Status byte; " + paramInt + " requires " + i + " data bytes"); 
    setMessage(paramInt, 0, 0);
  }
  
  public void setMessage(int paramInt1, int paramInt2, int paramInt3) throws InvalidMidiDataException {
    int i = getDataLength(paramInt1);
    if (i > 0) {
      if (paramInt2 < 0 || paramInt2 > 127)
        throw new InvalidMidiDataException("data1 out of range: " + paramInt2); 
      if (i > 1 && (paramInt3 < 0 || paramInt3 > 127))
        throw new InvalidMidiDataException("data2 out of range: " + paramInt3); 
    } 
    this.length = i + 1;
    if (this.data == null || this.data.length < this.length)
      this.data = new byte[3]; 
    this.data[0] = (byte)(paramInt1 & 0xFF);
    if (this.length > 1) {
      this.data[1] = (byte)(paramInt2 & 0xFF);
      if (this.length > 2)
        this.data[2] = (byte)(paramInt3 & 0xFF); 
    } 
  }
  
  public void setMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws InvalidMidiDataException {
    if (paramInt1 >= 240 || paramInt1 < 128)
      throw new InvalidMidiDataException("command out of range: 0x" + Integer.toHexString(paramInt1)); 
    if ((paramInt2 & 0xFFFFFFF0) != 0)
      throw new InvalidMidiDataException("channel out of range: " + paramInt2); 
    setMessage(paramInt1 & 0xF0 | paramInt2 & 0xF, paramInt3, paramInt4);
  }
  
  public int getChannel() { return getStatus() & 0xF; }
  
  public int getCommand() { return getStatus() & 0xF0; }
  
  public int getData1() { return (this.length > 1) ? (this.data[1] & 0xFF) : 0; }
  
  public int getData2() { return (this.length > 2) ? (this.data[2] & 0xFF) : 0; }
  
  public Object clone() {
    byte[] arrayOfByte = new byte[this.length];
    System.arraycopy(this.data, 0, arrayOfByte, 0, arrayOfByte.length);
    return new ShortMessage(arrayOfByte);
  }
  
  protected final int getDataLength(int paramInt) throws InvalidMidiDataException {
    switch (paramInt) {
      case 246:
      case 247:
      case 248:
      case 249:
      case 250:
      case 251:
      case 252:
      case 253:
      case 254:
      case 255:
        return 0;
      case 241:
      case 243:
        return 1;
      case 242:
        return 2;
    } 
    switch (paramInt & 0xF0) {
      case 128:
      case 144:
      case 160:
      case 176:
      case 224:
        return 2;
      case 192:
      case 208:
        return 1;
    } 
    throw new InvalidMidiDataException("Invalid status byte: " + paramInt);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\ShortMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */