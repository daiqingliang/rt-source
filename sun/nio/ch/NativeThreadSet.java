package sun.nio.ch;

class NativeThreadSet {
  private long[] elts;
  
  private int used = 0;
  
  private boolean waitingToEmpty;
  
  NativeThreadSet(int paramInt) { this.elts = new long[paramInt]; }
  
  int add() {
    long l = NativeThread.current();
    if (l == 0L)
      l = -1L; 
    synchronized (this) {
      int i = 0;
      if (this.used >= this.elts.length) {
        int k = this.elts.length;
        int m = k * 2;
        long[] arrayOfLong = new long[m];
        System.arraycopy(this.elts, 0, arrayOfLong, 0, k);
        this.elts = arrayOfLong;
        i = k;
      } 
      for (int j = i; j < this.elts.length; j++) {
        if (this.elts[j] == 0L) {
          this.elts[j] = l;
          this.used++;
          return j;
        } 
      } 
      assert false;
      return -1;
    } 
  }
  
  void remove(int paramInt) {
    synchronized (this) {
      this.elts[paramInt] = 0L;
      this.used--;
      if (this.used == 0 && this.waitingToEmpty)
        notifyAll(); 
    } 
  }
  
  void signalAndWait() {
    boolean bool = false;
    while (this.used > 0) {
      int i = this.used;
      int j = this.elts.length;
      for (b = 0; b < j; b++) {
        long l = this.elts[b];
        if (l != 0L) {
          if (l != -1L)
            NativeThread.signal(l); 
          if (--i == 0)
            break; 
        } 
      } 
      this.waitingToEmpty = true;
      try {
        wait(50L);
      } catch (InterruptedException b) {
        InterruptedException interruptedException;
        bool = true;
      } finally {
        this.waitingToEmpty = false;
      } 
    } 
    if (bool)
      Thread.currentThread().interrupt(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\NativeThreadSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */