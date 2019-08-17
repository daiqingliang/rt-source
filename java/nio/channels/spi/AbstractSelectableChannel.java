package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public abstract class AbstractSelectableChannel extends SelectableChannel {
  private final SelectorProvider provider;
  
  private SelectionKey[] keys = null;
  
  private int keyCount = 0;
  
  private final Object keyLock = new Object();
  
  private final Object regLock = new Object();
  
  boolean blocking = true;
  
  protected AbstractSelectableChannel(SelectorProvider paramSelectorProvider) { this.provider = paramSelectorProvider; }
  
  public final SelectorProvider provider() { return this.provider; }
  
  private void addKey(SelectionKey paramSelectionKey) {
    assert Thread.holdsLock(this.keyLock);
    int i = 0;
    if (this.keys != null && this.keyCount < this.keys.length) {
      for (i = 0; i < this.keys.length && this.keys[i] != null; i++);
    } else if (this.keys == null) {
      this.keys = new SelectionKey[3];
    } else {
      int j = this.keys.length * 2;
      SelectionKey[] arrayOfSelectionKey = new SelectionKey[j];
      for (i = 0; i < this.keys.length; i++)
        arrayOfSelectionKey[i] = this.keys[i]; 
      this.keys = arrayOfSelectionKey;
      i = this.keyCount;
    } 
    this.keys[i] = paramSelectionKey;
    this.keyCount++;
  }
  
  private SelectionKey findKey(Selector paramSelector) {
    synchronized (this.keyLock) {
      if (this.keys == null)
        return null; 
      for (byte b = 0; b < this.keys.length; b++) {
        if (this.keys[b] != null && this.keys[b].selector() == paramSelector)
          return this.keys[b]; 
      } 
      return null;
    } 
  }
  
  void removeKey(SelectionKey paramSelectionKey) {
    synchronized (this.keyLock) {
      for (byte b = 0; b < this.keys.length; b++) {
        if (this.keys[b] == paramSelectionKey) {
          this.keys[b] = null;
          this.keyCount--;
        } 
      } 
      ((AbstractSelectionKey)paramSelectionKey).invalidate();
    } 
  }
  
  private boolean haveValidKeys() {
    synchronized (this.keyLock) {
      if (this.keyCount == 0)
        return false; 
      for (byte b = 0; b < this.keys.length; b++) {
        if (this.keys[b] != null && this.keys[b].isValid())
          return true; 
      } 
      return false;
    } 
  }
  
  public final boolean isRegistered() {
    synchronized (this.keyLock) {
      return (this.keyCount != 0);
    } 
  }
  
  public final SelectionKey keyFor(Selector paramSelector) { return findKey(paramSelector); }
  
  public final SelectionKey register(Selector paramSelector, int paramInt, Object paramObject) throws ClosedChannelException {
    synchronized (this.regLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if ((paramInt & (validOps() ^ 0xFFFFFFFF)) != 0)
        throw new IllegalArgumentException(); 
      if (this.blocking)
        throw new IllegalBlockingModeException(); 
      SelectionKey selectionKey = findKey(paramSelector);
      if (selectionKey != null) {
        selectionKey.interestOps(paramInt);
        selectionKey.attach(paramObject);
      } 
      if (selectionKey == null)
        synchronized (this.keyLock) {
          if (!isOpen())
            throw new ClosedChannelException(); 
          selectionKey = ((AbstractSelector)paramSelector).register(this, paramInt, paramObject);
          addKey(selectionKey);
        }  
      return selectionKey;
    } 
  }
  
  protected final void implCloseChannel() throws IOException {
    implCloseSelectableChannel();
    synchronized (this.keyLock) {
      boolean bool = (this.keys == null) ? 0 : this.keys.length;
      for (byte b = 0; b < bool; b++) {
        SelectionKey selectionKey = this.keys[b];
        if (selectionKey != null)
          selectionKey.cancel(); 
      } 
    } 
  }
  
  protected abstract void implCloseSelectableChannel() throws IOException;
  
  public final boolean isBlocking() {
    synchronized (this.regLock) {
      return this.blocking;
    } 
  }
  
  public final Object blockingLock() { return this.regLock; }
  
  public final SelectableChannel configureBlocking(boolean paramBoolean) throws IOException {
    synchronized (this.regLock) {
      if (!isOpen())
        throw new ClosedChannelException(); 
      if (this.blocking == paramBoolean)
        return this; 
      if (paramBoolean && haveValidKeys())
        throw new IllegalBlockingModeException(); 
      implConfigureBlocking(paramBoolean);
      this.blocking = paramBoolean;
    } 
    return this;
  }
  
  protected abstract void implConfigureBlocking(boolean paramBoolean) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\spi\AbstractSelectableChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */