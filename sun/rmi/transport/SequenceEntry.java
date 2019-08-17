package sun.rmi.transport;

class SequenceEntry {
  long sequenceNum;
  
  boolean keep;
  
  SequenceEntry(long paramLong) {
    this.sequenceNum = paramLong;
    this.keep = false;
  }
  
  void retain(long paramLong) {
    this.sequenceNum = paramLong;
    this.keep = true;
  }
  
  void update(long paramLong) { this.sequenceNum = paramLong; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\SequenceEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */