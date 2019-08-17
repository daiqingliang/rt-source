package java.util.zip;

class ZStreamRef {
  ZStreamRef(long paramLong) { this.address = paramLong; }
  
  long address() { return this.address; }
  
  void clear() { this.address = 0L; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\ZStreamRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */