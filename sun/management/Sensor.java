package sun.management;

import java.lang.management.MemoryUsage;

public abstract class Sensor {
  private Object lock;
  
  private String name;
  
  private long count;
  
  private boolean on;
  
  public Sensor(String paramString) {
    this.name = paramString;
    this.count = 0L;
    this.on = false;
    this.lock = new Object();
  }
  
  public String getName() { return this.name; }
  
  public long getCount() {
    synchronized (this.lock) {
      return this.count;
    } 
  }
  
  public boolean isOn() {
    synchronized (this.lock) {
      return this.on;
    } 
  }
  
  public void trigger() {
    synchronized (this.lock) {
      this.on = true;
      this.count++;
    } 
    triggerAction();
  }
  
  public void trigger(int paramInt) {
    synchronized (this.lock) {
      this.on = true;
      this.count += paramInt;
    } 
    triggerAction();
  }
  
  public void trigger(int paramInt, MemoryUsage paramMemoryUsage) {
    synchronized (this.lock) {
      this.on = true;
      this.count += paramInt;
    } 
    triggerAction(paramMemoryUsage);
  }
  
  public void clear() {
    synchronized (this.lock) {
      this.on = false;
    } 
    clearAction();
  }
  
  public void clear(int paramInt) {
    synchronized (this.lock) {
      this.on = false;
      this.count += paramInt;
    } 
    clearAction();
  }
  
  public String toString() { return "Sensor - " + getName() + (isOn() ? " on " : " off ") + " count = " + getCount(); }
  
  abstract void triggerAction();
  
  abstract void triggerAction(MemoryUsage paramMemoryUsage);
  
  abstract void clearAction();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\Sensor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */