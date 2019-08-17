package sun.nio.ch;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class SelectorImpl extends AbstractSelector {
  protected Set<SelectionKey> selectedKeys = new HashSet();
  
  protected HashSet<SelectionKey> keys = new HashSet();
  
  private Set<SelectionKey> publicKeys;
  
  private Set<SelectionKey> publicSelectedKeys;
  
  protected SelectorImpl(SelectorProvider paramSelectorProvider) {
    super(paramSelectorProvider);
    if (Util.atBugLevel("1.4")) {
      this.publicKeys = this.keys;
      this.publicSelectedKeys = this.selectedKeys;
    } else {
      this.publicKeys = Collections.unmodifiableSet(this.keys);
      this.publicSelectedKeys = Util.ungrowableSet(this.selectedKeys);
    } 
  }
  
  public Set<SelectionKey> keys() {
    if (!isOpen() && !Util.atBugLevel("1.4"))
      throw new ClosedSelectorException(); 
    return this.publicKeys;
  }
  
  public Set<SelectionKey> selectedKeys() {
    if (!isOpen() && !Util.atBugLevel("1.4"))
      throw new ClosedSelectorException(); 
    return this.publicSelectedKeys;
  }
  
  protected abstract int doSelect(long paramLong) throws IOException;
  
  private int lockAndDoSelect(long paramLong) throws IOException {
    synchronized (this) {
      if (!isOpen())
        throw new ClosedSelectorException(); 
      synchronized (this.publicKeys) {
        synchronized (this.publicSelectedKeys) {
          return doSelect(paramLong);
        } 
      } 
    } 
  }
  
  public int select(long paramLong) throws IOException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative timeout"); 
    return lockAndDoSelect((paramLong == 0L) ? -1L : paramLong);
  }
  
  public int select() throws IOException { return select(0L); }
  
  public int selectNow() throws IOException { return lockAndDoSelect(0L); }
  
  public void implCloseSelector() throws IOException {
    wakeup();
    synchronized (this) {
      synchronized (this.publicKeys) {
        synchronized (this.publicSelectedKeys) {
          implClose();
        } 
      } 
    } 
  }
  
  protected abstract void implClose() throws IOException;
  
  public void putEventOps(SelectionKeyImpl paramSelectionKeyImpl, int paramInt) {}
  
  protected final SelectionKey register(AbstractSelectableChannel paramAbstractSelectableChannel, int paramInt, Object paramObject) {
    if (!(paramAbstractSelectableChannel instanceof SelChImpl))
      throw new IllegalSelectorException(); 
    SelectionKeyImpl selectionKeyImpl = new SelectionKeyImpl((SelChImpl)paramAbstractSelectableChannel, this);
    selectionKeyImpl.attach(paramObject);
    synchronized (this.publicKeys) {
      implRegister(selectionKeyImpl);
    } 
    selectionKeyImpl.interestOps(paramInt);
    return selectionKeyImpl;
  }
  
  protected abstract void implRegister(SelectionKeyImpl paramSelectionKeyImpl);
  
  void processDeregisterQueue() throws IOException {
    Set set = cancelledKeys();
    synchronized (set) {
      if (!set.isEmpty()) {
        iterator = set.iterator();
        while (iterator.hasNext()) {
          SelectionKeyImpl selectionKeyImpl = (SelectionKeyImpl)iterator.next();
          try {
            implDereg(selectionKeyImpl);
          } catch (SocketException socketException) {
            throw new IOException("Error deregistering key", socketException);
          } finally {
            iterator.remove();
          } 
        } 
      } 
    } 
  }
  
  protected abstract void implDereg(SelectionKeyImpl paramSelectionKeyImpl);
  
  public abstract Selector wakeup();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\SelectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */