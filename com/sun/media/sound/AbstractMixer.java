package com.sun.media.sound;

import java.util.Vector;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

abstract class AbstractMixer extends AbstractLine implements Mixer {
  protected static final int PCM = 0;
  
  protected static final int ULAW = 1;
  
  protected static final int ALAW = 2;
  
  private final Mixer.Info mixerInfo;
  
  protected Line.Info[] sourceLineInfo;
  
  protected Line.Info[] targetLineInfo;
  
  private boolean started = false;
  
  private boolean manuallyOpened = false;
  
  private final Vector sourceLines = new Vector();
  
  private final Vector targetLines = new Vector();
  
  protected AbstractMixer(Mixer.Info paramInfo, Control[] paramArrayOfControl, Line.Info[] paramArrayOfInfo1, Line.Info[] paramArrayOfInfo2) {
    super(new Line.Info(Mixer.class), null, paramArrayOfControl);
    if (paramArrayOfControl == null)
      paramArrayOfControl = new Control[0]; 
    this.mixerInfo = paramInfo;
    this.sourceLineInfo = paramArrayOfInfo1;
    this.targetLineInfo = paramArrayOfInfo2;
  }
  
  public final Mixer.Info getMixerInfo() { return this.mixerInfo; }
  
  public final Line.Info[] getSourceLineInfo() {
    Line.Info[] arrayOfInfo = new Line.Info[this.sourceLineInfo.length];
    System.arraycopy(this.sourceLineInfo, 0, arrayOfInfo, 0, this.sourceLineInfo.length);
    return arrayOfInfo;
  }
  
  public final Line.Info[] getTargetLineInfo() {
    Line.Info[] arrayOfInfo = new Line.Info[this.targetLineInfo.length];
    System.arraycopy(this.targetLineInfo, 0, arrayOfInfo, 0, this.targetLineInfo.length);
    return arrayOfInfo;
  }
  
  public final Line.Info[] getSourceLineInfo(Line.Info paramInfo) {
    Vector vector = new Vector();
    byte b;
    for (b = 0; b < this.sourceLineInfo.length; b++) {
      if (paramInfo.matches(this.sourceLineInfo[b]))
        vector.addElement(this.sourceLineInfo[b]); 
    } 
    Line.Info[] arrayOfInfo = new Line.Info[vector.size()];
    for (b = 0; b < arrayOfInfo.length; b++)
      arrayOfInfo[b] = (Line.Info)vector.elementAt(b); 
    return arrayOfInfo;
  }
  
  public final Line.Info[] getTargetLineInfo(Line.Info paramInfo) {
    Vector vector = new Vector();
    byte b;
    for (b = 0; b < this.targetLineInfo.length; b++) {
      if (paramInfo.matches(this.targetLineInfo[b]))
        vector.addElement(this.targetLineInfo[b]); 
    } 
    Line.Info[] arrayOfInfo = new Line.Info[vector.size()];
    for (b = 0; b < arrayOfInfo.length; b++)
      arrayOfInfo[b] = (Line.Info)vector.elementAt(b); 
    return arrayOfInfo;
  }
  
  public final boolean isLineSupported(Line.Info paramInfo) {
    byte b;
    for (b = 0; b < this.sourceLineInfo.length; b++) {
      if (paramInfo.matches(this.sourceLineInfo[b]))
        return true; 
    } 
    for (b = 0; b < this.targetLineInfo.length; b++) {
      if (paramInfo.matches(this.targetLineInfo[b]))
        return true; 
    } 
    return false;
  }
  
  public abstract Line getLine(Line.Info paramInfo) throws LineUnavailableException;
  
  public abstract int getMaxLines(Line.Info paramInfo);
  
  protected abstract void implOpen() throws LineUnavailableException;
  
  protected abstract void implStart() throws LineUnavailableException;
  
  protected abstract void implStop() throws LineUnavailableException;
  
  protected abstract void implClose() throws LineUnavailableException;
  
  public final Line[] getSourceLines() {
    Line[] arrayOfLine;
    synchronized (this.sourceLines) {
      arrayOfLine = new Line[this.sourceLines.size()];
      for (byte b = 0; b < arrayOfLine.length; b++)
        arrayOfLine[b] = (Line)this.sourceLines.elementAt(b); 
    } 
    return arrayOfLine;
  }
  
  public final Line[] getTargetLines() {
    Line[] arrayOfLine;
    synchronized (this.targetLines) {
      arrayOfLine = new Line[this.targetLines.size()];
      for (byte b = 0; b < arrayOfLine.length; b++)
        arrayOfLine[b] = (Line)this.targetLines.elementAt(b); 
    } 
    return arrayOfLine;
  }
  
  public final void synchronize(Line[] paramArrayOfLine, boolean paramBoolean) { throw new IllegalArgumentException("Synchronization not supported by this mixer."); }
  
  public final void unsynchronize(Line[] paramArrayOfLine) { throw new IllegalArgumentException("Synchronization not supported by this mixer."); }
  
  public final boolean isSynchronizationSupported(Line[] paramArrayOfLine, boolean paramBoolean) { return false; }
  
  public final void open() throws LineUnavailableException { open(true); }
  
  final void open(boolean paramBoolean) throws LineUnavailableException {
    if (!isOpen()) {
      implOpen();
      setOpen(true);
      if (paramBoolean)
        this.manuallyOpened = true; 
    } 
  }
  
  final void open(Line paramLine) throws LineUnavailableException {
    if (equals(paramLine))
      return; 
    if (isSourceLine(paramLine.getLineInfo())) {
      if (!this.sourceLines.contains(paramLine)) {
        open(false);
        this.sourceLines.addElement(paramLine);
      } 
    } else if (isTargetLine(paramLine.getLineInfo()) && !this.targetLines.contains(paramLine)) {
      open(false);
      this.targetLines.addElement(paramLine);
    } 
  }
  
  final void close(Line paramLine) throws LineUnavailableException {
    if (equals(paramLine))
      return; 
    this.sourceLines.removeElement(paramLine);
    this.targetLines.removeElement(paramLine);
    if (this.sourceLines.isEmpty() && this.targetLines.isEmpty() && !this.manuallyOpened)
      close(); 
  }
  
  public final void close() throws LineUnavailableException {
    if (isOpen()) {
      Line[] arrayOfLine = getSourceLines();
      byte b;
      for (b = 0; b < arrayOfLine.length; b++)
        arrayOfLine[b].close(); 
      arrayOfLine = getTargetLines();
      for (b = 0; b < arrayOfLine.length; b++)
        arrayOfLine[b].close(); 
      implClose();
      setOpen(false);
    } 
    this.manuallyOpened = false;
  }
  
  final void start(Line paramLine) throws LineUnavailableException {
    if (equals(paramLine))
      return; 
    if (!this.started) {
      implStart();
      this.started = true;
    } 
  }
  
  final void stop(Line paramLine) throws LineUnavailableException {
    if (equals(paramLine))
      return; 
    Vector vector1 = (Vector)this.sourceLines.clone();
    for (byte b1 = 0; b1 < vector1.size(); b1++) {
      if (vector1.elementAt(b1) instanceof AbstractDataLine) {
        AbstractDataLine abstractDataLine = (AbstractDataLine)vector1.elementAt(b1);
        if (abstractDataLine.isStartedRunning() && !abstractDataLine.equals(paramLine))
          return; 
      } 
    } 
    Vector vector2 = (Vector)this.targetLines.clone();
    for (byte b2 = 0; b2 < vector2.size(); b2++) {
      if (vector2.elementAt(b2) instanceof AbstractDataLine) {
        AbstractDataLine abstractDataLine = (AbstractDataLine)vector2.elementAt(b2);
        if (abstractDataLine.isStartedRunning() && !abstractDataLine.equals(paramLine))
          return; 
      } 
    } 
    this.started = false;
    implStop();
  }
  
  final boolean isSourceLine(Line.Info paramInfo) {
    for (byte b = 0; b < this.sourceLineInfo.length; b++) {
      if (paramInfo.matches(this.sourceLineInfo[b]))
        return true; 
    } 
    return false;
  }
  
  final boolean isTargetLine(Line.Info paramInfo) {
    for (byte b = 0; b < this.targetLineInfo.length; b++) {
      if (paramInfo.matches(this.targetLineInfo[b]))
        return true; 
    } 
    return false;
  }
  
  final Line.Info getLineInfo(Line.Info paramInfo) {
    if (paramInfo == null)
      return null; 
    byte b;
    for (b = 0; b < this.sourceLineInfo.length; b++) {
      if (paramInfo.matches(this.sourceLineInfo[b]))
        return this.sourceLineInfo[b]; 
    } 
    for (b = 0; b < this.targetLineInfo.length; b++) {
      if (paramInfo.matches(this.targetLineInfo[b]))
        return this.targetLineInfo[b]; 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AbstractMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */