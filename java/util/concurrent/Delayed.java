package java.util.concurrent;

public interface Delayed extends Comparable<Delayed> {
  long getDelay(TimeUnit paramTimeUnit);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\Delayed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */