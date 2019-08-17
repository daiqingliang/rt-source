package java.util.stream;

abstract class AbstractSpinedBuffer {
  public static final int MIN_CHUNK_POWER = 4;
  
  public static final int MIN_CHUNK_SIZE = 16;
  
  public static final int MAX_CHUNK_POWER = 30;
  
  public static final int MIN_SPINE_SIZE = 8;
  
  protected final int initialChunkPower;
  
  protected int elementIndex;
  
  protected int spineIndex;
  
  protected long[] priorElementCount;
  
  protected AbstractSpinedBuffer() { this.initialChunkPower = 4; }
  
  protected AbstractSpinedBuffer(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt); 
    this.initialChunkPower = Math.max(4, 32 - Integer.numberOfLeadingZeros(paramInt - 1));
  }
  
  public boolean isEmpty() { return (this.spineIndex == 0 && this.elementIndex == 0); }
  
  public long count() { return (this.spineIndex == 0) ? this.elementIndex : (this.priorElementCount[this.spineIndex] + this.elementIndex); }
  
  protected int chunkSize(int paramInt) {
    int i = (paramInt == 0 || paramInt == 1) ? this.initialChunkPower : Math.min(this.initialChunkPower + paramInt - 1, 30);
    return 1 << i;
  }
  
  public abstract void clear();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\AbstractSpinedBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */