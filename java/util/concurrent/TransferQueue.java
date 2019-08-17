package java.util.concurrent;

public interface TransferQueue<E> extends BlockingQueue<E> {
  boolean tryTransfer(E paramE);
  
  void transfer(E paramE) throws InterruptedException;
  
  boolean tryTransfer(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  boolean hasWaitingConsumer();
  
  int getWaitingConsumerCount();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\TransferQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */