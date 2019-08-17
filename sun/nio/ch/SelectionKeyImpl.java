package sun.nio.ch;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectionKey;

public class SelectionKeyImpl extends AbstractSelectionKey {
  final SelChImpl channel;
  
  public final SelectorImpl selector;
  
  private int index;
  
  private int readyOps;
  
  SelectionKeyImpl(SelChImpl paramSelChImpl, SelectorImpl paramSelectorImpl) {
    this.channel = paramSelChImpl;
    this.selector = paramSelectorImpl;
  }
  
  public SelectableChannel channel() { return (SelectableChannel)this.channel; }
  
  public Selector selector() { return this.selector; }
  
  int getIndex() { return this.index; }
  
  void setIndex(int paramInt) { this.index = paramInt; }
  
  private void ensureValid() {
    if (!isValid())
      throw new CancelledKeyException(); 
  }
  
  public int interestOps() {
    ensureValid();
    return this.interestOps;
  }
  
  public SelectionKey interestOps(int paramInt) {
    ensureValid();
    return nioInterestOps(paramInt);
  }
  
  public int readyOps() {
    ensureValid();
    return this.readyOps;
  }
  
  public void nioReadyOps(int paramInt) { this.readyOps = paramInt; }
  
  public int nioReadyOps() { return this.readyOps; }
  
  public SelectionKey nioInterestOps(int paramInt) {
    if ((paramInt & (channel().validOps() ^ 0xFFFFFFFF)) != 0)
      throw new IllegalArgumentException(); 
    this.channel.translateAndSetInterestOps(paramInt, this);
    this.interestOps = paramInt;
    return this;
  }
  
  public int nioInterestOps() { return this.interestOps; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\SelectionKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */