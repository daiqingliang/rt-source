package com.sun.media.sound;

import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

abstract class AbstractLine implements Line {
  protected final Line.Info info;
  
  protected Control[] controls;
  
  AbstractMixer mixer;
  
  private boolean open = false;
  
  private final Vector listeners = new Vector();
  
  private static final Map<ThreadGroup, EventDispatcher> dispatchers = new WeakHashMap();
  
  protected AbstractLine(Line.Info paramInfo, AbstractMixer paramAbstractMixer, Control[] paramArrayOfControl) {
    if (paramArrayOfControl == null)
      paramArrayOfControl = new Control[0]; 
    this.info = paramInfo;
    this.mixer = paramAbstractMixer;
    this.controls = paramArrayOfControl;
  }
  
  public final Line.Info getLineInfo() { return this.info; }
  
  public final boolean isOpen() { return this.open; }
  
  public final void addLineListener(LineListener paramLineListener) {
    synchronized (this.listeners) {
      if (!this.listeners.contains(paramLineListener))
        this.listeners.addElement(paramLineListener); 
    } 
  }
  
  public final void removeLineListener(LineListener paramLineListener) { this.listeners.removeElement(paramLineListener); }
  
  public final Control[] getControls() {
    Control[] arrayOfControl = new Control[this.controls.length];
    for (byte b = 0; b < this.controls.length; b++)
      arrayOfControl[b] = this.controls[b]; 
    return arrayOfControl;
  }
  
  public final boolean isControlSupported(Control.Type paramType) {
    if (paramType == null)
      return false; 
    for (byte b = 0; b < this.controls.length; b++) {
      if (paramType == this.controls[b].getType())
        return true; 
    } 
    return false;
  }
  
  public final Control getControl(Control.Type paramType) {
    if (paramType != null)
      for (byte b = 0; b < this.controls.length; b++) {
        if (paramType == this.controls[b].getType())
          return this.controls[b]; 
      }  
    throw new IllegalArgumentException("Unsupported control type: " + paramType);
  }
  
  final void setOpen(boolean paramBoolean) {
    boolean bool = false;
    long l = getLongFramePosition();
    synchronized (this) {
      if (this.open != paramBoolean) {
        this.open = paramBoolean;
        bool = true;
      } 
    } 
    if (bool)
      if (paramBoolean) {
        sendEvents(new LineEvent(this, LineEvent.Type.OPEN, l));
      } else {
        sendEvents(new LineEvent(this, LineEvent.Type.CLOSE, l));
      }  
  }
  
  final void sendEvents(LineEvent paramLineEvent) { getEventDispatcher().sendAudioEvents(paramLineEvent, this.listeners); }
  
  public final int getFramePosition() { return (int)getLongFramePosition(); }
  
  public long getLongFramePosition() { return -1L; }
  
  final AbstractMixer getMixer() { return this.mixer; }
  
  final EventDispatcher getEventDispatcher() {
    ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
    synchronized (dispatchers) {
      EventDispatcher eventDispatcher = (EventDispatcher)dispatchers.get(threadGroup);
      if (eventDispatcher == null) {
        eventDispatcher = new EventDispatcher();
        dispatchers.put(threadGroup, eventDispatcher);
        eventDispatcher.start();
      } 
      return eventDispatcher;
    } 
  }
  
  public abstract void open() throws LineUnavailableException;
  
  public abstract void close() throws LineUnavailableException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AbstractLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */