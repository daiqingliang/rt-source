package sun.java2d.pipe;

public interface SpanIterator {
  void getPathBox(int[] paramArrayOfInt);
  
  void intersectClipBox(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  boolean nextSpan(int[] paramArrayOfInt);
  
  void skipDownTo(int paramInt);
  
  long getNativeIterator();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\SpanIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */