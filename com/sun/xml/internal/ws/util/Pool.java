package com.sun.xml.internal.ws.util;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public abstract class Pool<T> extends Object {
  public final T take() {
    Object object = getQueue().poll();
    return (object == null) ? (T)create() : (T)object;
  }
  
  private ConcurrentLinkedQueue<T> getQueue() {
    WeakReference weakReference = this.queue;
    if (weakReference != null) {
      ConcurrentLinkedQueue concurrentLinkedQueue1 = (ConcurrentLinkedQueue)weakReference.get();
      if (concurrentLinkedQueue1 != null)
        return concurrentLinkedQueue1; 
    } 
    ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
    this.queue = new WeakReference(concurrentLinkedQueue);
    return concurrentLinkedQueue;
  }
  
  public final void recycle(T paramT) { getQueue().offer(paramT); }
  
  protected abstract T create();
  
  public static final class Marshaller extends Pool<Marshaller> {
    private final JAXBContext context;
    
    public Marshaller(JAXBContext param1JAXBContext) { this.context = param1JAXBContext; }
    
    protected Marshaller create() {
      try {
        return this.context.createMarshaller();
      } catch (JAXBException jAXBException) {
        throw new AssertionError(jAXBException);
      } 
    }
  }
  
  public static final class TubePool extends Pool<Tube> {
    private final Tube master;
    
    public TubePool(Tube param1Tube) {
      this.master = param1Tube;
      recycle(param1Tube);
    }
    
    protected Tube create() { return TubeCloner.clone(this.master); }
    
    @Deprecated
    public final Tube takeMaster() { return this.master; }
  }
  
  public static final class Unmarshaller extends Pool<Unmarshaller> {
    private final JAXBContext context;
    
    public Unmarshaller(JAXBContext param1JAXBContext) { this.context = param1JAXBContext; }
    
    protected Unmarshaller create() {
      try {
        return this.context.createUnmarshaller();
      } catch (JAXBException jAXBException) {
        throw new AssertionError(jAXBException);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\Pool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */