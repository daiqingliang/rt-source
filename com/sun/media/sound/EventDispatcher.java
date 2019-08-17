package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

final class EventDispatcher implements Runnable {
  private static final int AUTO_CLOSE_TIME = 5000;
  
  private final ArrayList eventQueue = new ArrayList();
  
  private Thread thread = null;
  
  private final ArrayList<ClipInfo> autoClosingClips = new ArrayList();
  
  private final ArrayList<LineMonitor> lineMonitors = new ArrayList();
  
  static final int LINE_MONITOR_TIME = 400;
  
  void start() {
    if (this.thread == null)
      this.thread = JSSecurityManager.createThread(this, "Java Sound Event Dispatcher", true, -1, true); 
  }
  
  void processEvent(EventInfo paramEventInfo) {
    int i = paramEventInfo.getListenerCount();
    if (paramEventInfo.getEvent() instanceof LineEvent) {
      LineEvent lineEvent = (LineEvent)paramEventInfo.getEvent();
      for (byte b = 0; b < i; b++) {
        try {
          ((LineListener)paramEventInfo.getListener(b)).update(lineEvent);
        } catch (Throwable throwable) {}
      } 
      return;
    } 
    if (paramEventInfo.getEvent() instanceof MetaMessage) {
      MetaMessage metaMessage = (MetaMessage)paramEventInfo.getEvent();
      for (byte b = 0; b < i; b++) {
        try {
          ((MetaEventListener)paramEventInfo.getListener(b)).meta(metaMessage);
        } catch (Throwable throwable) {}
      } 
      return;
    } 
    if (paramEventInfo.getEvent() instanceof ShortMessage) {
      ShortMessage shortMessage = (ShortMessage)paramEventInfo.getEvent();
      int j = shortMessage.getStatus();
      if ((j & 0xF0) == 176)
        for (byte b = 0; b < i; b++) {
          try {
            ((ControllerEventListener)paramEventInfo.getListener(b)).controlChange(shortMessage);
          } catch (Throwable throwable) {}
        }  
      return;
    } 
    Printer.err("Unknown event type: " + paramEventInfo.getEvent());
  }
  
  void dispatchEvents() {
    EventInfo eventInfo = null;
    synchronized (this) {
      try {
        if (this.eventQueue.size() == 0)
          if (this.autoClosingClips.size() > 0 || this.lineMonitors.size() > 0) {
            char c = 'ᎈ';
            if (this.lineMonitors.size() > 0)
              c = 'Ɛ'; 
            wait(c);
          } else {
            wait();
          }  
      } catch (InterruptedException interruptedException) {}
      if (this.eventQueue.size() > 0)
        eventInfo = (EventInfo)this.eventQueue.remove(0); 
    } 
    if (eventInfo != null) {
      processEvent(eventInfo);
    } else {
      if (this.autoClosingClips.size() > 0)
        closeAutoClosingClips(); 
      if (this.lineMonitors.size() > 0)
        monitorLines(); 
    } 
  }
  
  private void postEvent(EventInfo paramEventInfo) {
    this.eventQueue.add(paramEventInfo);
    notifyAll();
  }
  
  public void run() {
    while (true) {
      try {
        while (true)
          dispatchEvents(); 
        break;
      } catch (Throwable throwable) {}
    } 
  }
  
  void sendAudioEvents(Object paramObject, List paramList) {
    if (paramList == null || paramList.size() == 0)
      return; 
    start();
    EventInfo eventInfo = new EventInfo(paramObject, paramList);
    postEvent(eventInfo);
  }
  
  private void closeAutoClosingClips() {
    synchronized (this.autoClosingClips) {
      long l = System.currentTimeMillis();
      for (int i = this.autoClosingClips.size() - 1; i >= 0; i--) {
        ClipInfo clipInfo = (ClipInfo)this.autoClosingClips.get(i);
        if (clipInfo.isExpired(l)) {
          AutoClosingClip autoClosingClip = clipInfo.getClip();
          if (!autoClosingClip.isOpen() || !autoClosingClip.isAutoClosing()) {
            this.autoClosingClips.remove(i);
          } else if (!autoClosingClip.isRunning() && !autoClosingClip.isActive() && autoClosingClip.isAutoClosing()) {
            autoClosingClip.close();
          } 
        } 
      } 
    } 
  }
  
  private int getAutoClosingClipIndex(AutoClosingClip paramAutoClosingClip) {
    synchronized (this.autoClosingClips) {
      for (int i = this.autoClosingClips.size() - 1; i >= 0; i--) {
        if (paramAutoClosingClip.equals(((ClipInfo)this.autoClosingClips.get(i)).getClip()))
          return i; 
      } 
    } 
    return -1;
  }
  
  void autoClosingClipOpened(AutoClosingClip paramAutoClosingClip) {
    int i = 0;
    synchronized (this.autoClosingClips) {
      i = getAutoClosingClipIndex(paramAutoClosingClip);
      if (i == -1)
        this.autoClosingClips.add(new ClipInfo(paramAutoClosingClip)); 
    } 
    if (i == -1)
      synchronized (this) {
        notifyAll();
      }  
  }
  
  void autoClosingClipClosed(AutoClosingClip paramAutoClosingClip) {}
  
  private void monitorLines() {
    synchronized (this.lineMonitors) {
      for (byte b = 0; b < this.lineMonitors.size(); b++)
        ((LineMonitor)this.lineMonitors.get(b)).checkLine(); 
    } 
  }
  
  void addLineMonitor(LineMonitor paramLineMonitor) {
    synchronized (this.lineMonitors) {
      if (this.lineMonitors.indexOf(paramLineMonitor) >= 0)
        return; 
      this.lineMonitors.add(paramLineMonitor);
    } 
    synchronized (this) {
      notifyAll();
    } 
  }
  
  void removeLineMonitor(LineMonitor paramLineMonitor) {
    synchronized (this.lineMonitors) {
      if (this.lineMonitors.indexOf(paramLineMonitor) < 0)
        return; 
      this.lineMonitors.remove(paramLineMonitor);
    } 
  }
  
  private class ClipInfo {
    private final AutoClosingClip clip;
    
    private final long expiration;
    
    ClipInfo(AutoClosingClip param1AutoClosingClip) {
      this.clip = param1AutoClosingClip;
      this.expiration = System.currentTimeMillis() + 5000L;
    }
    
    AutoClosingClip getClip() { return this.clip; }
    
    boolean isExpired(long param1Long) { return (param1Long > this.expiration); }
  }
  
  private class EventInfo {
    private final Object event;
    
    private final Object[] listeners;
    
    EventInfo(Object param1Object, List param1List) {
      this.event = param1Object;
      this.listeners = param1List.toArray();
    }
    
    Object getEvent() { return this.event; }
    
    int getListenerCount() { return this.listeners.length; }
    
    Object getListener(int param1Int) { return this.listeners[param1Int]; }
  }
  
  static interface LineMonitor {
    void checkLine();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\EventDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */