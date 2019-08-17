package sun.text.bidi;

public class BidiRun {
  int start;
  
  int limit;
  
  int insertRemove;
  
  byte level;
  
  BidiRun() { this(0, 0, (byte)0); }
  
  BidiRun(int paramInt1, int paramInt2, byte paramByte) {
    this.start = paramInt1;
    this.limit = paramInt2;
    this.level = paramByte;
  }
  
  void copyFrom(BidiRun paramBidiRun) {
    this.start = paramBidiRun.start;
    this.limit = paramBidiRun.limit;
    this.level = paramBidiRun.level;
    this.insertRemove = paramBidiRun.insertRemove;
  }
  
  public byte getEmbeddingLevel() { return this.level; }
  
  boolean isEvenRun() { return ((this.level & true) == 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\bidi\BidiRun.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */