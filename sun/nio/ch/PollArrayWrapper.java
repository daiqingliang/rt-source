package sun.nio.ch;

class PollArrayWrapper {
  private AllocatedNativeObject pollArray;
  
  long pollArrayAddress;
  
  private static final short FD_OFFSET = 0;
  
  private static final short EVENT_OFFSET = 4;
  
  static short SIZE_POLLFD = 8;
  
  private int size;
  
  PollArrayWrapper(int paramInt) {
    int i = paramInt * SIZE_POLLFD;
    this.pollArray = new AllocatedNativeObject(i, true);
    this.pollArrayAddress = this.pollArray.address();
    this.size = paramInt;
  }
  
  void addEntry(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) { putDescriptor(paramInt, paramSelectionKeyImpl.channel.getFDVal()); }
  
  void replaceEntry(PollArrayWrapper paramPollArrayWrapper1, int paramInt1, PollArrayWrapper paramPollArrayWrapper2, int paramInt2) {
    paramPollArrayWrapper2.putDescriptor(paramInt2, paramPollArrayWrapper1.getDescriptor(paramInt1));
    paramPollArrayWrapper2.putEventOps(paramInt2, paramPollArrayWrapper1.getEventOps(paramInt1));
  }
  
  void grow(int paramInt) {
    PollArrayWrapper pollArrayWrapper = new PollArrayWrapper(paramInt);
    for (byte b = 0; b < this.size; b++)
      replaceEntry(this, b, pollArrayWrapper, b); 
    this.pollArray.free();
    this.pollArray = pollArrayWrapper.pollArray;
    this.size = pollArrayWrapper.size;
    this.pollArrayAddress = this.pollArray.address();
  }
  
  void free() { this.pollArray.free(); }
  
  void putDescriptor(int paramInt1, int paramInt2) { this.pollArray.putInt(SIZE_POLLFD * paramInt1 + 0, paramInt2); }
  
  void putEventOps(int paramInt1, int paramInt2) { this.pollArray.putShort(SIZE_POLLFD * paramInt1 + 4, (short)paramInt2); }
  
  int getEventOps(int paramInt) { return this.pollArray.getShort(SIZE_POLLFD * paramInt + 4); }
  
  int getDescriptor(int paramInt) { return this.pollArray.getInt(SIZE_POLLFD * paramInt + 0); }
  
  void addWakeupSocket(int paramInt1, int paramInt2) {
    putDescriptor(paramInt2, paramInt1);
    putEventOps(paramInt2, Net.POLLIN);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\PollArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */