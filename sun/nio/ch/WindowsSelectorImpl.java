package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class WindowsSelectorImpl extends SelectorImpl {
  private final int INIT_CAP = 8;
  
  private static final int MAX_SELECTABLE_FDS = 1024;
  
  private SelectionKeyImpl[] channelArray = new SelectionKeyImpl[8];
  
  private PollArrayWrapper pollWrapper = new PollArrayWrapper(8);
  
  private int totalChannels = 1;
  
  private int threadsCount = 0;
  
  private final List<SelectThread> threads = new ArrayList();
  
  private final Pipe wakeupPipe = Pipe.open();
  
  private final int wakeupSourceFd = ((SelChImpl)this.wakeupPipe.source()).getFDVal();
  
  private final int wakeupSinkFd;
  
  private Object closeLock = new Object();
  
  private final FdMap fdMap = new FdMap(null);
  
  private final SubSelector subSelector = new SubSelector(null);
  
  private long timeout;
  
  private final Object interruptLock = new Object();
  
  private final StartLock startLock = new StartLock(null);
  
  private final FinishLock finishLock = new FinishLock(null);
  
  private long updateCount = 0L;
  
  WindowsSelectorImpl(SelectorProvider paramSelectorProvider) throws IOException {
    super(paramSelectorProvider);
    SinkChannelImpl sinkChannelImpl = (SinkChannelImpl)this.wakeupPipe.sink();
    sinkChannelImpl.sc.socket().setTcpNoDelay(true);
    this.wakeupSinkFd = sinkChannelImpl.getFDVal();
    this.pollWrapper.addWakeupSocket(this.wakeupSourceFd, 0);
  }
  
  protected int doSelect(long paramLong) throws IOException {
    if (this.channelArray == null)
      throw new ClosedSelectorException(); 
    this.timeout = paramLong;
    processDeregisterQueue();
    if (this.interruptTriggered) {
      resetWakeupSocket();
      return 0;
    } 
    adjustThreadsCount();
    this.finishLock.reset();
    this.startLock.startThreads();
    try {
      begin();
      try {
        this.subSelector.poll();
      } catch (IOException iOException) {
        this.finishLock.setException(iOException);
      } 
      if (this.threads.size() > 0)
        this.finishLock.waitForHelperThreads(); 
    } finally {
      end();
    } 
    this.finishLock.checkForException();
    processDeregisterQueue();
    int i = updateSelectedKeys();
    resetWakeupSocket();
    return i;
  }
  
  private void adjustThreadsCount() {
    if (this.threadsCount > this.threads.size()) {
      for (int i = this.threads.size(); i < this.threadsCount; i++) {
        SelectThread selectThread = new SelectThread(i, null);
        this.threads.add(selectThread);
        selectThread.setDaemon(true);
        selectThread.start();
      } 
    } else if (this.threadsCount < this.threads.size()) {
      for (int i = this.threads.size() - 1; i >= this.threadsCount; i--)
        ((SelectThread)this.threads.remove(i)).makeZombie(); 
    } 
  }
  
  private void setWakeupSocket() { setWakeupSocket0(this.wakeupSinkFd); }
  
  private native void setWakeupSocket0(int paramInt);
  
  private void resetWakeupSocket() {
    synchronized (this.interruptLock) {
      if (!this.interruptTriggered)
        return; 
      resetWakeupSocket0(this.wakeupSourceFd);
      this.interruptTriggered = false;
    } 
  }
  
  private native void resetWakeupSocket0(int paramInt);
  
  private native boolean discardUrgentData(int paramInt);
  
  private int updateSelectedKeys() {
    this.updateCount++;
    int i = 0;
    i += this.subSelector.processSelectedKeys(this.updateCount);
    for (SelectThread selectThread : this.threads)
      i += selectThread.subSelector.processSelectedKeys(this.updateCount); 
    return i;
  }
  
  protected void implClose() {
    synchronized (this.closeLock) {
      if (this.channelArray != null && this.pollWrapper != null) {
        synchronized (this.interruptLock) {
          this.interruptTriggered = true;
        } 
        this.wakeupPipe.sink().close();
        this.wakeupPipe.source().close();
        for (byte b = 1; b < this.totalChannels; b++) {
          if (b % 'Ѐ' != '\000') {
            deregister(this.channelArray[b]);
            SelectableChannel selectableChannel = this.channelArray[b].channel();
            if (!selectableChannel.isOpen() && !selectableChannel.isRegistered())
              ((SelChImpl)selectableChannel).kill(); 
          } 
        } 
        this.pollWrapper.free();
        this.pollWrapper = null;
        this.selectedKeys = null;
        this.channelArray = null;
        for (SelectThread selectThread : this.threads)
          selectThread.makeZombie(); 
        this.startLock.startThreads();
      } 
    } 
  }
  
  protected void implRegister(SelectionKeyImpl paramSelectionKeyImpl) {
    synchronized (this.closeLock) {
      if (this.pollWrapper == null)
        throw new ClosedSelectorException(); 
      growIfNeeded();
      this.channelArray[this.totalChannels] = paramSelectionKeyImpl;
      paramSelectionKeyImpl.setIndex(this.totalChannels);
      this.fdMap.put(paramSelectionKeyImpl);
      this.keys.add(paramSelectionKeyImpl);
      this.pollWrapper.addEntry(this.totalChannels, paramSelectionKeyImpl);
      this.totalChannels++;
    } 
  }
  
  private void growIfNeeded() {
    if (this.channelArray.length == this.totalChannels) {
      int i = this.totalChannels * 2;
      SelectionKeyImpl[] arrayOfSelectionKeyImpl = new SelectionKeyImpl[i];
      System.arraycopy(this.channelArray, 1, arrayOfSelectionKeyImpl, 1, this.totalChannels - 1);
      this.channelArray = arrayOfSelectionKeyImpl;
      this.pollWrapper.grow(i);
    } 
    if (this.totalChannels % 1024 == 0) {
      this.pollWrapper.addWakeupSocket(this.wakeupSourceFd, this.totalChannels);
      this.totalChannels++;
      this.threadsCount++;
    } 
  }
  
  protected void implDereg(SelectionKeyImpl paramSelectionKeyImpl) {
    int i = paramSelectionKeyImpl.getIndex();
    assert i >= 0;
    synchronized (this.closeLock) {
      if (i != this.totalChannels - 1) {
        SelectionKeyImpl selectionKeyImpl = this.channelArray[this.totalChannels - 1];
        this.channelArray[i] = selectionKeyImpl;
        selectionKeyImpl.setIndex(i);
        this.pollWrapper.replaceEntry(this.pollWrapper, this.totalChannels - 1, this.pollWrapper, i);
      } 
      paramSelectionKeyImpl.setIndex(-1);
    } 
    this.channelArray[this.totalChannels - 1] = null;
    this.totalChannels--;
    if (this.totalChannels != 1 && this.totalChannels % 1024 == 1) {
      this.totalChannels--;
      this.threadsCount--;
    } 
    this.fdMap.remove(paramSelectionKeyImpl);
    this.keys.remove(paramSelectionKeyImpl);
    this.selectedKeys.remove(paramSelectionKeyImpl);
    deregister(paramSelectionKeyImpl);
    SelectableChannel selectableChannel = paramSelectionKeyImpl.channel();
    if (!selectableChannel.isOpen() && !selectableChannel.isRegistered())
      ((SelChImpl)selectableChannel).kill(); 
  }
  
  public void putEventOps(SelectionKeyImpl paramSelectionKeyImpl, int paramInt) {
    synchronized (this.closeLock) {
      if (this.pollWrapper == null)
        throw new ClosedSelectorException(); 
      int i = paramSelectionKeyImpl.getIndex();
      if (i == -1)
        throw new CancelledKeyException(); 
      this.pollWrapper.putEventOps(i, paramInt);
    } 
  }
  
  public Selector wakeup() {
    synchronized (this.interruptLock) {
      if (!this.interruptTriggered) {
        setWakeupSocket();
        this.interruptTriggered = true;
      } 
    } 
    return this;
  }
  
  static  {
    IOUtil.load();
  }
  
  private static final class FdMap extends HashMap<Integer, MapEntry> {
    static final long serialVersionUID = 0L;
    
    private FdMap() {}
    
    private WindowsSelectorImpl.MapEntry get(int param1Int) { return (WindowsSelectorImpl.MapEntry)get(new Integer(param1Int)); }
    
    private WindowsSelectorImpl.MapEntry put(SelectionKeyImpl param1SelectionKeyImpl) { return (WindowsSelectorImpl.MapEntry)put(new Integer(param1SelectionKeyImpl.channel.getFDVal()), new WindowsSelectorImpl.MapEntry(param1SelectionKeyImpl)); }
    
    private WindowsSelectorImpl.MapEntry remove(SelectionKeyImpl param1SelectionKeyImpl) {
      Integer integer = new Integer(param1SelectionKeyImpl.channel.getFDVal());
      WindowsSelectorImpl.MapEntry mapEntry = (WindowsSelectorImpl.MapEntry)get(integer);
      return (mapEntry != null && mapEntry.ski.channel == param1SelectionKeyImpl.channel) ? (WindowsSelectorImpl.MapEntry)remove(integer) : null;
    }
  }
  
  private final class FinishLock {
    private int threadsToFinish;
    
    IOException exception = null;
    
    private FinishLock() {}
    
    private void reset() { this.threadsToFinish = WindowsSelectorImpl.this.threads.size(); }
    
    private void threadFinished() {
      if (this.threadsToFinish == WindowsSelectorImpl.this.threads.size())
        WindowsSelectorImpl.this.wakeup(); 
      this.threadsToFinish--;
      if (this.threadsToFinish == 0)
        notify(); 
    }
    
    private void waitForHelperThreads() {
      if (this.threadsToFinish == WindowsSelectorImpl.this.threads.size())
        WindowsSelectorImpl.this.wakeup(); 
      while (this.threadsToFinish != 0) {
        try {
          WindowsSelectorImpl.this.finishLock.wait();
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
        } 
      } 
    }
    
    private void setException(IOException param1IOException) { this.exception = param1IOException; }
    
    private void checkForException() {
      if (this.exception == null)
        return; 
      StringBuffer stringBuffer = new StringBuffer("An exception occurred during the execution of select(): \n");
      stringBuffer.append(this.exception);
      stringBuffer.append('\n');
      this.exception = null;
      throw new IOException(stringBuffer.toString());
    }
  }
  
  private static final class MapEntry {
    SelectionKeyImpl ski;
    
    long updateCount = 0L;
    
    long clearedCount = 0L;
    
    MapEntry(SelectionKeyImpl param1SelectionKeyImpl) { this.ski = param1SelectionKeyImpl; }
  }
  
  private final class SelectThread extends Thread {
    private final int index;
    
    final WindowsSelectorImpl.SubSelector subSelector;
    
    private long lastRun = 0L;
    
    private SelectThread(int param1Int) {
      this.index = param1Int;
      this.subSelector = new WindowsSelectorImpl.SubSelector(this$0, param1Int, null);
      this.lastRun = this$0.startLock.runsCounter;
    }
    
    void makeZombie() { this.zombie = true; }
    
    boolean isZombie() { return this.zombie; }
    
    public void run() {
      while (true) {
        if (WindowsSelectorImpl.this.startLock.waitForStart(this))
          return; 
        try {
          this.subSelector.poll(this.index);
        } catch (IOException iOException) {
          WindowsSelectorImpl.this.finishLock.setException(iOException);
        } 
        WindowsSelectorImpl.this.finishLock.threadFinished();
      } 
    }
  }
  
  private final class StartLock {
    private long runsCounter;
    
    private StartLock() {}
    
    private void startThreads() {
      this.runsCounter++;
      notifyAll();
    }
    
    private boolean waitForStart(WindowsSelectorImpl.SelectThread param1SelectThread) {
      while (this.runsCounter == param1SelectThread.lastRun) {
        try {
          WindowsSelectorImpl.this.startLock.wait();
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
        } 
      } 
      if (param1SelectThread.isZombie())
        return true; 
      param1SelectThread.lastRun = this.runsCounter;
      return false;
    }
  }
  
  private final class SubSelector {
    private final int pollArrayIndex = 0;
    
    private final int[] readFds = new int[1025];
    
    private final int[] writeFds = new int[1025];
    
    private final int[] exceptFds = new int[1025];
    
    private SubSelector() {}
    
    private SubSelector(int param1Int) {}
    
    private int poll() { return poll0(this.this$0.pollWrapper.pollArrayAddress, Math.min(WindowsSelectorImpl.this.totalChannels, 1024), this.readFds, this.writeFds, this.exceptFds, WindowsSelectorImpl.this.timeout); }
    
    private int poll(int param1Int) throws IOException { return poll0(this.this$0.pollWrapper.pollArrayAddress + (this.pollArrayIndex * PollArrayWrapper.SIZE_POLLFD), Math.min(1024, WindowsSelectorImpl.this.totalChannels - (param1Int + 1) * 1024), this.readFds, this.writeFds, this.exceptFds, WindowsSelectorImpl.this.timeout); }
    
    private native int poll0(long param1Long1, int param1Int, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2, int[] param1ArrayOfInt3, long param1Long2);
    
    private int processSelectedKeys(long param1Long) throws IOException {
      null = 0;
      null += processFDSet(param1Long, this.readFds, Net.POLLIN, false);
      null += processFDSet(param1Long, this.writeFds, Net.POLLCONN | Net.POLLOUT, false);
      return processFDSet(param1Long, this.exceptFds, Net.POLLIN | Net.POLLCONN | Net.POLLOUT, true);
    }
    
    private int processFDSet(long param1Long, int[] param1ArrayOfInt, int param1Int, boolean param1Boolean) {
      byte b1 = 0;
      for (byte b2 = 1; b2 <= param1ArrayOfInt[0]; b2++) {
        int i = param1ArrayOfInt[b2];
        if (i == WindowsSelectorImpl.this.wakeupSourceFd) {
          synchronized (WindowsSelectorImpl.this.interruptLock) {
            WindowsSelectorImpl.this.interruptTriggered = true;
          } 
        } else {
          WindowsSelectorImpl.MapEntry mapEntry = WindowsSelectorImpl.this.fdMap.get(i);
          if (mapEntry != null) {
            SelectionKeyImpl selectionKeyImpl = mapEntry.ski;
            if (!param1Boolean || !(selectionKeyImpl.channel() instanceof SocketChannelImpl) || !WindowsSelectorImpl.this.discardUrgentData(i))
              if (WindowsSelectorImpl.this.selectedKeys.contains(selectionKeyImpl)) {
                if (mapEntry.clearedCount != param1Long) {
                  if (selectionKeyImpl.channel.translateAndSetReadyOps(param1Int, selectionKeyImpl) && mapEntry.updateCount != param1Long) {
                    mapEntry.updateCount = param1Long;
                    b1++;
                  } 
                } else if (selectionKeyImpl.channel.translateAndUpdateReadyOps(param1Int, selectionKeyImpl) && mapEntry.updateCount != param1Long) {
                  mapEntry.updateCount = param1Long;
                  b1++;
                } 
                mapEntry.clearedCount = param1Long;
              } else {
                if (mapEntry.clearedCount != param1Long) {
                  selectionKeyImpl.channel.translateAndSetReadyOps(param1Int, selectionKeyImpl);
                  if ((selectionKeyImpl.nioReadyOps() & selectionKeyImpl.nioInterestOps()) != 0) {
                    WindowsSelectorImpl.this.selectedKeys.add(selectionKeyImpl);
                    mapEntry.updateCount = param1Long;
                    b1++;
                  } 
                } else {
                  selectionKeyImpl.channel.translateAndUpdateReadyOps(param1Int, selectionKeyImpl);
                  if ((selectionKeyImpl.nioReadyOps() & selectionKeyImpl.nioInterestOps()) != 0) {
                    WindowsSelectorImpl.this.selectedKeys.add(selectionKeyImpl);
                    mapEntry.updateCount = param1Long;
                    b1++;
                  } 
                } 
                mapEntry.clearedCount = param1Long;
              }  
          } 
        } 
      } 
      return b1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\WindowsSelectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */