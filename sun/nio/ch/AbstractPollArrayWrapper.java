package sun.nio.ch;

public abstract class AbstractPollArrayWrapper {
  static final short SIZE_POLLFD = 8;
  
  static final short FD_OFFSET = 0;
  
  static final short EVENT_OFFSET = 4;
  
  static final short REVENT_OFFSET = 6;
  
  protected AllocatedNativeObject pollArray;
  
  protected int totalChannels = 0;
  
  protected long pollArrayAddress;
  
  int getEventOps(int paramInt) {
    int i = 8 * paramInt + 4;
    return this.pollArray.getShort(i);
  }
  
  int getReventOps(int paramInt) {
    int i = 8 * paramInt + 6;
    return this.pollArray.getShort(i);
  }
  
  int getDescriptor(int paramInt) {
    int i = 8 * paramInt + 0;
    return this.pollArray.getInt(i);
  }
  
  void putEventOps(int paramInt1, int paramInt2) {
    int i = 8 * paramInt1 + 4;
    this.pollArray.putShort(i, (short)paramInt2);
  }
  
  void putReventOps(int paramInt1, int paramInt2) {
    int i = 8 * paramInt1 + 6;
    this.pollArray.putShort(i, (short)paramInt2);
  }
  
  void putDescriptor(int paramInt1, int paramInt2) {
    int i = 8 * paramInt1 + 0;
    this.pollArray.putInt(i, paramInt2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\AbstractPollArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */