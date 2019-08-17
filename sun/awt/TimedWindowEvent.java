package sun.awt;

import java.awt.Window;
import java.awt.event.WindowEvent;

public class TimedWindowEvent extends WindowEvent {
  private long time;
  
  public long getWhen() { return this.time; }
  
  public TimedWindowEvent(Window paramWindow1, int paramInt, Window paramWindow2, long paramLong) {
    super(paramWindow1, paramInt, paramWindow2);
    this.time = paramLong;
  }
  
  public TimedWindowEvent(Window paramWindow1, int paramInt1, Window paramWindow2, int paramInt2, int paramInt3, long paramLong) {
    super(paramWindow1, paramInt1, paramWindow2, paramInt2, paramInt3);
    this.time = paramLong;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\TimedWindowEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */