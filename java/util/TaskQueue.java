package java.util;

class TaskQueue {
  private TimerTask[] queue = new TimerTask[128];
  
  private int size = 0;
  
  int size() { return this.size; }
  
  void add(TimerTask paramTimerTask) {
    if (this.size + 1 == this.queue.length)
      this.queue = (TimerTask[])Arrays.copyOf(this.queue, 2 * this.queue.length); 
    this.queue[++this.size] = paramTimerTask;
    fixUp(this.size);
  }
  
  TimerTask getMin() { return this.queue[1]; }
  
  TimerTask get(int paramInt) { return this.queue[paramInt]; }
  
  void removeMin() {
    this.queue[1] = this.queue[this.size];
    this.queue[this.size--] = null;
    fixDown(1);
  }
  
  void quickRemove(int paramInt) {
    assert paramInt <= this.size;
    this.queue[paramInt] = this.queue[this.size];
    this.queue[this.size--] = null;
  }
  
  void rescheduleMin(long paramLong) {
    (this.queue[1]).nextExecutionTime = paramLong;
    fixDown(1);
  }
  
  boolean isEmpty() { return (this.size == 0); }
  
  void clear() {
    for (byte b = 1; b <= this.size; b++)
      this.queue[b] = null; 
    this.size = 0;
  }
  
  private void fixUp(int paramInt) {
    while (paramInt > 1) {
      int i = paramInt >> 1;
      if ((this.queue[i]).nextExecutionTime <= (this.queue[paramInt]).nextExecutionTime)
        break; 
      TimerTask timerTask = this.queue[i];
      this.queue[i] = this.queue[paramInt];
      this.queue[paramInt] = timerTask;
      paramInt = i;
    } 
  }
  
  private void fixDown(int paramInt) {
    int i;
    while ((i = paramInt << 1) <= this.size && i > 0) {
      if (i < this.size && (this.queue[i]).nextExecutionTime > (this.queue[i + 1]).nextExecutionTime)
        i++; 
      if ((this.queue[paramInt]).nextExecutionTime <= (this.queue[i]).nextExecutionTime)
        break; 
      TimerTask timerTask = this.queue[i];
      this.queue[i] = this.queue[paramInt];
      this.queue[paramInt] = timerTask;
      paramInt = i;
    } 
  }
  
  void heapify() {
    for (int i = this.size / 2; i >= 1; i--)
      fixDown(i); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\TaskQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */