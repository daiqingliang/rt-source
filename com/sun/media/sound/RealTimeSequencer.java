package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

final class RealTimeSequencer extends AbstractMidiDevice implements Sequencer, AutoConnectSequencer {
  private static final boolean DEBUG_PUMP = false;
  
  private static final boolean DEBUG_PUMP_ALL = false;
  
  private static final Map<ThreadGroup, EventDispatcher> dispatchers = new WeakHashMap();
  
  static final RealTimeSequencerInfo info = new RealTimeSequencerInfo(null);
  
  private static final Sequencer.SyncMode[] masterSyncModes = { Sequencer.SyncMode.INTERNAL_CLOCK };
  
  private static final Sequencer.SyncMode[] slaveSyncModes = { Sequencer.SyncMode.NO_SYNC };
  
  private static final Sequencer.SyncMode masterSyncMode = Sequencer.SyncMode.INTERNAL_CLOCK;
  
  private static final Sequencer.SyncMode slaveSyncMode = Sequencer.SyncMode.NO_SYNC;
  
  private Sequence sequence = null;
  
  private double cacheTempoMPQ = -1.0D;
  
  private float cacheTempoFactor = -1.0F;
  
  private boolean[] trackMuted = null;
  
  private boolean[] trackSolo = null;
  
  private final MidiUtils.TempoCache tempoCache = new MidiUtils.TempoCache();
  
  private boolean running = false;
  
  private PlayThread playThread;
  
  private boolean recording = false;
  
  private final List recordingTracks = new ArrayList();
  
  private long loopStart = 0L;
  
  private long loopEnd = -1L;
  
  private int loopCount = 0;
  
  private final ArrayList metaEventListeners = new ArrayList();
  
  private final ArrayList controllerEventListeners = new ArrayList();
  
  private boolean autoConnect = false;
  
  private boolean doAutoConnectAtNextOpen = false;
  
  Receiver autoConnectedReceiver = null;
  
  RealTimeSequencer() throws MidiUnavailableException { super(info); }
  
  public void setSequence(Sequence paramSequence) throws InvalidMidiDataException {
    if (paramSequence != this.sequence) {
      if (this.sequence != null && paramSequence == null) {
        setCaches();
        stop();
        this.trackMuted = null;
        this.trackSolo = null;
        this.loopStart = 0L;
        this.loopEnd = -1L;
        this.loopCount = 0;
        if (getDataPump() != null) {
          getDataPump().setTickPos(0L);
          getDataPump().resetLoopCount();
        } 
      } 
      if (this.playThread != null)
        this.playThread.setSequence(paramSequence); 
      this.sequence = paramSequence;
      if (paramSequence != null) {
        this.tempoCache.refresh(paramSequence);
        setTickPosition(0L);
        propagateCaches();
      } 
    } else if (paramSequence != null) {
      this.tempoCache.refresh(paramSequence);
      if (this.playThread != null)
        this.playThread.setSequence(paramSequence); 
    } 
  }
  
  public void setSequence(InputStream paramInputStream) throws IOException, InvalidMidiDataException {
    if (paramInputStream == null) {
      setSequence((Sequence)null);
      return;
    } 
    Sequence sequence1 = MidiSystem.getSequence(paramInputStream);
    setSequence(sequence1);
  }
  
  public Sequence getSequence() { return this.sequence; }
  
  public void start() throws MidiUnavailableException {
    if (!isOpen())
      throw new IllegalStateException("sequencer not open"); 
    if (this.sequence == null)
      throw new IllegalStateException("sequence not set"); 
    if (this.running == true)
      return; 
    implStart();
  }
  
  public void stop() throws MidiUnavailableException {
    if (!isOpen())
      throw new IllegalStateException("sequencer not open"); 
    stopRecording();
    if (!this.running)
      return; 
    implStop();
  }
  
  public boolean isRunning() { return this.running; }
  
  public void startRecording() throws MidiUnavailableException {
    if (!isOpen())
      throw new IllegalStateException("Sequencer not open"); 
    start();
    this.recording = true;
  }
  
  public void stopRecording() throws MidiUnavailableException {
    if (!isOpen())
      throw new IllegalStateException("Sequencer not open"); 
    this.recording = false;
  }
  
  public boolean isRecording() { return this.recording; }
  
  public void recordEnable(Track paramTrack, int paramInt) {
    if (!findTrack(paramTrack))
      throw new IllegalArgumentException("Track does not exist in the current sequence"); 
    synchronized (this.recordingTracks) {
      RecordingTrack recordingTrack = RecordingTrack.get(this.recordingTracks, paramTrack);
      if (recordingTrack != null) {
        recordingTrack.channel = paramInt;
      } else {
        this.recordingTracks.add(new RecordingTrack(paramTrack, paramInt));
      } 
    } 
  }
  
  public void recordDisable(Track paramTrack) {
    synchronized (this.recordingTracks) {
      RecordingTrack recordingTrack = RecordingTrack.get(this.recordingTracks, paramTrack);
      if (recordingTrack != null)
        this.recordingTracks.remove(recordingTrack); 
    } 
  }
  
  private boolean findTrack(Track paramTrack) {
    boolean bool = false;
    if (this.sequence != null) {
      Track[] arrayOfTrack = this.sequence.getTracks();
      for (byte b = 0; b < arrayOfTrack.length; b++) {
        if (paramTrack == arrayOfTrack[b]) {
          bool = true;
          break;
        } 
      } 
    } 
    return bool;
  }
  
  public float getTempoInBPM() { return (float)MidiUtils.convertTempo(getTempoInMPQ()); }
  
  public void setTempoInBPM(float paramFloat) {
    if (paramFloat <= 0.0F)
      paramFloat = 1.0F; 
    setTempoInMPQ((float)MidiUtils.convertTempo(paramFloat));
  }
  
  public float getTempoInMPQ() { return needCaching() ? ((this.cacheTempoMPQ != -1.0D) ? (float)this.cacheTempoMPQ : ((this.sequence != null) ? this.tempoCache.getTempoMPQAt(getTickPosition()) : 500000.0F)) : getDataPump().getTempoMPQ(); }
  
  public void setTempoInMPQ(float paramFloat) {
    if (paramFloat <= 0.0F)
      paramFloat = 1.0F; 
    if (needCaching()) {
      this.cacheTempoMPQ = paramFloat;
    } else {
      getDataPump().setTempoMPQ(paramFloat);
      this.cacheTempoMPQ = -1.0D;
    } 
  }
  
  public void setTempoFactor(float paramFloat) {
    if (paramFloat <= 0.0F)
      return; 
    if (needCaching()) {
      this.cacheTempoFactor = paramFloat;
    } else {
      getDataPump().setTempoFactor(paramFloat);
      this.cacheTempoFactor = -1.0F;
    } 
  }
  
  public float getTempoFactor() { return needCaching() ? ((this.cacheTempoFactor != -1.0F) ? this.cacheTempoFactor : 1.0F) : getDataPump().getTempoFactor(); }
  
  public long getTickLength() { return (this.sequence == null) ? 0L : this.sequence.getTickLength(); }
  
  public long getTickPosition() { return (getDataPump() == null || this.sequence == null) ? 0L : getDataPump().getTickPos(); }
  
  public void setTickPosition(long paramLong) {
    if (paramLong < 0L)
      return; 
    if (getDataPump() == null) {
      if (paramLong != 0L);
    } else if (this.sequence == null) {
      if (paramLong != 0L);
    } else {
      getDataPump().setTickPos(paramLong);
    } 
  }
  
  public long getMicrosecondLength() { return (this.sequence == null) ? 0L : this.sequence.getMicrosecondLength(); }
  
  public long getMicrosecondPosition() {
    if (getDataPump() == null || this.sequence == null)
      return 0L; 
    synchronized (this.tempoCache) {
      return MidiUtils.tick2microsecond(this.sequence, getDataPump().getTickPos(), this.tempoCache);
    } 
  }
  
  public void setMicrosecondPosition(long paramLong) {
    if (paramLong < 0L)
      return; 
    if (getDataPump() == null) {
      if (paramLong != 0L);
    } else if (this.sequence == null) {
      if (paramLong != 0L);
    } else {
      synchronized (this.tempoCache) {
        setTickPosition(MidiUtils.microsecond2tick(this.sequence, paramLong, this.tempoCache));
      } 
    } 
  }
  
  public void setMasterSyncMode(Sequencer.SyncMode paramSyncMode) {}
  
  public Sequencer.SyncMode getMasterSyncMode() { return masterSyncMode; }
  
  public Sequencer.SyncMode[] getMasterSyncModes() {
    Sequencer.SyncMode[] arrayOfSyncMode = new Sequencer.SyncMode[masterSyncModes.length];
    System.arraycopy(masterSyncModes, 0, arrayOfSyncMode, 0, masterSyncModes.length);
    return arrayOfSyncMode;
  }
  
  public void setSlaveSyncMode(Sequencer.SyncMode paramSyncMode) {}
  
  public Sequencer.SyncMode getSlaveSyncMode() { return slaveSyncMode; }
  
  public Sequencer.SyncMode[] getSlaveSyncModes() {
    Sequencer.SyncMode[] arrayOfSyncMode = new Sequencer.SyncMode[slaveSyncModes.length];
    System.arraycopy(slaveSyncModes, 0, arrayOfSyncMode, 0, slaveSyncModes.length);
    return arrayOfSyncMode;
  }
  
  int getTrackCount() {
    Sequence sequence1 = getSequence();
    return (sequence1 != null) ? this.sequence.getTracks().length : 0;
  }
  
  public void setTrackMute(int paramInt, boolean paramBoolean) {
    int i = getTrackCount();
    if (paramInt < 0 || paramInt >= getTrackCount())
      return; 
    this.trackMuted = ensureBoolArraySize(this.trackMuted, i);
    this.trackMuted[paramInt] = paramBoolean;
    if (getDataPump() != null)
      getDataPump().muteSoloChanged(); 
  }
  
  public boolean getTrackMute(int paramInt) { return (paramInt < 0 || paramInt >= getTrackCount()) ? false : ((this.trackMuted == null || this.trackMuted.length <= paramInt) ? false : this.trackMuted[paramInt]); }
  
  public void setTrackSolo(int paramInt, boolean paramBoolean) {
    int i = getTrackCount();
    if (paramInt < 0 || paramInt >= getTrackCount())
      return; 
    this.trackSolo = ensureBoolArraySize(this.trackSolo, i);
    this.trackSolo[paramInt] = paramBoolean;
    if (getDataPump() != null)
      getDataPump().muteSoloChanged(); 
  }
  
  public boolean getTrackSolo(int paramInt) { return (paramInt < 0 || paramInt >= getTrackCount()) ? false : ((this.trackSolo == null || this.trackSolo.length <= paramInt) ? false : this.trackSolo[paramInt]); }
  
  public boolean addMetaEventListener(MetaEventListener paramMetaEventListener) {
    synchronized (this.metaEventListeners) {
      if (!this.metaEventListeners.contains(paramMetaEventListener))
        this.metaEventListeners.add(paramMetaEventListener); 
      return true;
    } 
  }
  
  public void removeMetaEventListener(MetaEventListener paramMetaEventListener) {
    synchronized (this.metaEventListeners) {
      int i = this.metaEventListeners.indexOf(paramMetaEventListener);
      if (i >= 0)
        this.metaEventListeners.remove(i); 
    } 
  }
  
  public int[] addControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt) {
    synchronized (this.controllerEventListeners) {
      ControllerListElement controllerListElement = null;
      boolean bool = false;
      for (byte b = 0; b < this.controllerEventListeners.size(); b++) {
        controllerListElement = (ControllerListElement)this.controllerEventListeners.get(b);
        if (controllerListElement.listener.equals(paramControllerEventListener)) {
          controllerListElement.addControllers(paramArrayOfInt);
          bool = true;
          break;
        } 
      } 
      if (!bool) {
        controllerListElement = new ControllerListElement(paramControllerEventListener, paramArrayOfInt, null);
        this.controllerEventListeners.add(controllerListElement);
      } 
      return controllerListElement.getControllers();
    } 
  }
  
  public int[] removeControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt) {
    synchronized (this.controllerEventListeners) {
      ControllerListElement controllerListElement = null;
      boolean bool = false;
      int i;
      for (i = 0; i < this.controllerEventListeners.size(); i++) {
        controllerListElement = (ControllerListElement)this.controllerEventListeners.get(i);
        if (controllerListElement.listener.equals(paramControllerEventListener)) {
          controllerListElement.removeControllers(paramArrayOfInt);
          bool = true;
          break;
        } 
      } 
      if (!bool)
        return new int[0]; 
      if (paramArrayOfInt == null) {
        i = this.controllerEventListeners.indexOf(controllerListElement);
        if (i >= 0)
          this.controllerEventListeners.remove(i); 
        return new int[0];
      } 
      return controllerListElement.getControllers();
    } 
  }
  
  public void setLoopStartPoint(long paramLong) {
    if (paramLong > getTickLength() || (this.loopEnd != -1L && paramLong > this.loopEnd) || paramLong < 0L)
      throw new IllegalArgumentException("invalid loop start point: " + paramLong); 
    this.loopStart = paramLong;
  }
  
  public long getLoopStartPoint() { return this.loopStart; }
  
  public void setLoopEndPoint(long paramLong) {
    if (paramLong > getTickLength() || (this.loopStart > paramLong && paramLong != -1L) || paramLong < -1L)
      throw new IllegalArgumentException("invalid loop end point: " + paramLong); 
    this.loopEnd = paramLong;
  }
  
  public long getLoopEndPoint() { return this.loopEnd; }
  
  public void setLoopCount(int paramInt) {
    if (paramInt != -1 && paramInt < 0)
      throw new IllegalArgumentException("illegal value for loop count: " + paramInt); 
    this.loopCount = paramInt;
    if (getDataPump() != null)
      getDataPump().resetLoopCount(); 
  }
  
  public int getLoopCount() { return this.loopCount; }
  
  protected void implOpen() throws MidiUnavailableException {
    this.playThread = new PlayThread();
    if (this.sequence != null)
      this.playThread.setSequence(this.sequence); 
    propagateCaches();
    if (this.doAutoConnectAtNextOpen)
      doAutoConnect(); 
  }
  
  private void doAutoConnect() throws MidiUnavailableException {
    receiver = null;
    try {
      synthesizer = MidiSystem.getSynthesizer();
      if (synthesizer instanceof ReferenceCountingDevice) {
        receiver = ((ReferenceCountingDevice)synthesizer).getReceiverReferenceCounting();
      } else {
        synthesizer.open();
        try {
          receiver = synthesizer.getReceiver();
        } finally {
          if (receiver == null)
            synthesizer.close(); 
        } 
      } 
    } catch (Exception exception) {}
    if (receiver == null)
      try {
        receiver = MidiSystem.getReceiver();
      } catch (Exception exception) {} 
    if (receiver != null) {
      this.autoConnectedReceiver = receiver;
      try {
        getTransmitter().setReceiver(receiver);
      } catch (Exception exception) {}
    } 
  }
  
  private void propagateCaches() throws MidiUnavailableException {
    if (this.sequence != null && isOpen()) {
      if (this.cacheTempoFactor != -1.0F)
        setTempoFactor(this.cacheTempoFactor); 
      if (this.cacheTempoMPQ == -1.0D) {
        setTempoInMPQ((new MidiUtils.TempoCache(this.sequence)).getTempoMPQAt(getTickPosition()));
      } else {
        setTempoInMPQ((float)this.cacheTempoMPQ);
      } 
    } 
  }
  
  private void setCaches() throws MidiUnavailableException {
    this.cacheTempoFactor = getTempoFactor();
    this.cacheTempoMPQ = getTempoInMPQ();
  }
  
  protected void implClose() throws MidiUnavailableException {
    if (this.playThread != null) {
      this.playThread.close();
      this.playThread = null;
    } 
    super.implClose();
    this.sequence = null;
    this.running = false;
    this.cacheTempoMPQ = -1.0D;
    this.cacheTempoFactor = -1.0F;
    this.trackMuted = null;
    this.trackSolo = null;
    this.loopStart = 0L;
    this.loopEnd = -1L;
    this.loopCount = 0;
    this.doAutoConnectAtNextOpen = this.autoConnect;
    if (this.autoConnectedReceiver != null) {
      try {
        this.autoConnectedReceiver.close();
      } catch (Exception exception) {}
      this.autoConnectedReceiver = null;
    } 
  }
  
  void implStart() throws MidiUnavailableException {
    if (this.playThread == null)
      return; 
    this.tempoCache.refresh(this.sequence);
    if (!this.running) {
      this.running = true;
      this.playThread.start();
    } 
  }
  
  void implStop() throws MidiUnavailableException {
    if (this.playThread == null)
      return; 
    this.recording = false;
    if (this.running) {
      this.running = false;
      this.playThread.stop();
    } 
  }
  
  private static EventDispatcher getEventDispatcher() {
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
  
  void sendMetaEvents(MidiMessage paramMidiMessage) {
    if (this.metaEventListeners.size() == 0)
      return; 
    getEventDispatcher().sendAudioEvents(paramMidiMessage, this.metaEventListeners);
  }
  
  void sendControllerEvents(MidiMessage paramMidiMessage) {
    int i = this.controllerEventListeners.size();
    if (i == 0)
      return; 
    if (!(paramMidiMessage instanceof ShortMessage))
      return; 
    ShortMessage shortMessage = (ShortMessage)paramMidiMessage;
    int j = shortMessage.getData1();
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < i; b++) {
      ControllerListElement controllerListElement = (ControllerListElement)this.controllerEventListeners.get(b);
      for (byte b1 = 0; b1 < controllerListElement.controllers.length; b1++) {
        if (controllerListElement.controllers[b1] == j) {
          arrayList.add(controllerListElement.listener);
          break;
        } 
      } 
    } 
    getEventDispatcher().sendAudioEvents(paramMidiMessage, arrayList);
  }
  
  private boolean needCaching() { return (!isOpen() || this.sequence == null || this.playThread == null); }
  
  private DataPump getDataPump() { return (this.playThread != null) ? this.playThread.getDataPump() : null; }
  
  private MidiUtils.TempoCache getTempoCache() { return this.tempoCache; }
  
  private static boolean[] ensureBoolArraySize(boolean[] paramArrayOfBoolean, int paramInt) {
    if (paramArrayOfBoolean == null)
      return new boolean[paramInt]; 
    if (paramArrayOfBoolean.length < paramInt) {
      boolean[] arrayOfBoolean = new boolean[paramInt];
      System.arraycopy(paramArrayOfBoolean, 0, arrayOfBoolean, 0, paramArrayOfBoolean.length);
      return arrayOfBoolean;
    } 
    return paramArrayOfBoolean;
  }
  
  protected boolean hasReceivers() { return true; }
  
  protected Receiver createReceiver() throws MidiUnavailableException { return new SequencerReceiver(); }
  
  protected boolean hasTransmitters() { return true; }
  
  protected Transmitter createTransmitter() throws MidiUnavailableException { return new SequencerTransmitter(null); }
  
  public void setAutoConnect(Receiver paramReceiver) {
    this.autoConnect = (paramReceiver != null);
    this.autoConnectedReceiver = paramReceiver;
  }
  
  private class ControllerListElement {
    int[] controllers;
    
    final ControllerEventListener listener;
    
    private ControllerListElement(ControllerEventListener param1ControllerEventListener, int[] param1ArrayOfInt) {
      this.listener = param1ControllerEventListener;
      if (param1ArrayOfInt == null) {
        param1ArrayOfInt = new int[128];
        for (byte b = 0; b < ''; b++)
          param1ArrayOfInt[b] = b; 
      } 
      this.controllers = param1ArrayOfInt;
    }
    
    private void addControllers(int[] param1ArrayOfInt) {
      if (param1ArrayOfInt == null) {
        this.controllers = new int[128];
        for (byte b = 0; b < ''; b++)
          this.controllers[b] = b; 
        return;
      } 
      int[] arrayOfInt1 = new int[this.controllers.length + param1ArrayOfInt.length];
      byte b1;
      for (b1 = 0; b1 < this.controllers.length; b1++)
        arrayOfInt1[b1] = this.controllers[b1]; 
      int i = this.controllers.length;
      for (b1 = 0; b1 < param1ArrayOfInt.length; b1++) {
        boolean bool = false;
        for (byte b = 0; b < this.controllers.length; b++) {
          if (param1ArrayOfInt[b1] == this.controllers[b]) {
            bool = true;
            break;
          } 
        } 
        if (!bool)
          arrayOfInt1[i++] = param1ArrayOfInt[b1]; 
      } 
      int[] arrayOfInt2 = new int[i];
      for (byte b2 = 0; b2 < i; b2++)
        arrayOfInt2[b2] = arrayOfInt1[b2]; 
      this.controllers = arrayOfInt2;
    }
    
    private void removeControllers(int[] param1ArrayOfInt) {
      if (param1ArrayOfInt == null) {
        this.controllers = new int[0];
      } else {
        int[] arrayOfInt1 = new int[this.controllers.length];
        byte b1 = 0;
        for (byte b2 = 0; b2 < this.controllers.length; b2++) {
          boolean bool = false;
          for (byte b = 0; b < param1ArrayOfInt.length; b++) {
            if (this.controllers[b2] == param1ArrayOfInt[b]) {
              bool = true;
              break;
            } 
          } 
          if (!bool)
            arrayOfInt1[b1++] = this.controllers[b2]; 
        } 
        int[] arrayOfInt2 = new int[b1];
        for (byte b3 = 0; b3 < b1; b3++)
          arrayOfInt2[b3] = arrayOfInt1[b3]; 
        this.controllers = arrayOfInt2;
      } 
    }
    
    private int[] getControllers() {
      if (this.controllers == null)
        return null; 
      int[] arrayOfInt = new int[this.controllers.length];
      for (byte b = 0; b < this.controllers.length; b++)
        arrayOfInt[b] = this.controllers[b]; 
      return arrayOfInt;
    }
  }
  
  private class DataPump {
    private float currTempo;
    
    private float tempoFactor;
    
    private float inverseTempoFactor;
    
    private long ignoreTempoEventAt;
    
    private int resolution;
    
    private float divisionType;
    
    private long checkPointMillis;
    
    private long checkPointTick;
    
    private int[] noteOnCache;
    
    private Track[] tracks;
    
    private boolean[] trackDisabled;
    
    private int[] trackReadPos;
    
    private long lastTick;
    
    private boolean needReindex = false;
    
    private int currLoopCounter = 0;
    
    DataPump() { init(); }
    
    void init() throws MidiUnavailableException {
      this.ignoreTempoEventAt = -1L;
      this.tempoFactor = 1.0F;
      this.inverseTempoFactor = 1.0F;
      this.noteOnCache = new int[128];
      this.tracks = null;
      this.trackDisabled = null;
    }
    
    void setTickPos(long param1Long) {
      long l = param1Long;
      this.lastTick = param1Long;
      if (RealTimeSequencer.this.running)
        notesOff(false); 
      if (RealTimeSequencer.this.running || param1Long > 0L) {
        chaseEvents(l, param1Long);
      } else {
        this.needReindex = true;
      } 
      if (!hasCachedTempo()) {
        setTempoMPQ(RealTimeSequencer.this.getTempoCache().getTempoMPQAt(this.lastTick, this.currTempo));
        this.ignoreTempoEventAt = -1L;
      } 
      this.checkPointMillis = 0L;
    }
    
    long getTickPos() { return this.lastTick; }
    
    boolean hasCachedTempo() {
      if (this.ignoreTempoEventAt != this.lastTick)
        this.ignoreTempoEventAt = -1L; 
      return (this.ignoreTempoEventAt >= 0L);
    }
    
    void setTempoMPQ(float param1Float) {
      if (param1Float > 0.0F && param1Float != this.currTempo) {
        this.ignoreTempoEventAt = this.lastTick;
        this.currTempo = param1Float;
        this.checkPointMillis = 0L;
      } 
    }
    
    float getTempoMPQ() { return this.currTempo; }
    
    void setTempoFactor(float param1Float) {
      if (param1Float > 0.0F && param1Float != this.tempoFactor) {
        this.tempoFactor = param1Float;
        this.inverseTempoFactor = 1.0F / param1Float;
        this.checkPointMillis = 0L;
      } 
    }
    
    float getTempoFactor() { return this.tempoFactor; }
    
    void muteSoloChanged() throws MidiUnavailableException {
      boolean[] arrayOfBoolean = makeDisabledArray();
      if (RealTimeSequencer.this.running)
        applyDisabledTracks(this.trackDisabled, arrayOfBoolean); 
      this.trackDisabled = arrayOfBoolean;
    }
    
    void setSequence(Sequence param1Sequence) throws InvalidMidiDataException {
      if (param1Sequence == null) {
        init();
        return;
      } 
      this.tracks = param1Sequence.getTracks();
      muteSoloChanged();
      this.resolution = param1Sequence.getResolution();
      this.divisionType = param1Sequence.getDivisionType();
      this.trackReadPos = new int[this.tracks.length];
      this.checkPointMillis = 0L;
      this.needReindex = true;
    }
    
    void resetLoopCount() throws MidiUnavailableException { this.currLoopCounter = RealTimeSequencer.this.loopCount; }
    
    void clearNoteOnCache() throws MidiUnavailableException {
      for (byte b = 0; b < ''; b++)
        this.noteOnCache[b] = 0; 
    }
    
    void notesOff(boolean param1Boolean) {
      byte b1 = 0;
      for (byte b2 = 0; b2 < 16; b2++) {
        int i = true << b2;
        for (byte b = 0; b < ''; b++) {
          if ((this.noteOnCache[b] & i) != 0) {
            this.noteOnCache[b] = this.noteOnCache[b] ^ i;
            RealTimeSequencer.this.getTransmitterList().sendMessage(0x90 | b2 | b << 8, -1L);
            b1++;
          } 
        } 
        RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | b2 | 0x7B00, -1L);
        RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | b2 | 0x4000, -1L);
        if (param1Boolean) {
          RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | b2 | 0x7900, -1L);
          b1++;
        } 
      } 
    }
    
    private boolean[] makeDisabledArray() {
      boolean[] arrayOfBoolean3;
      boolean[] arrayOfBoolean2;
      if (this.tracks == null)
        return null; 
      boolean[] arrayOfBoolean1 = new boolean[this.tracks.length];
      synchronized (RealTimeSequencer.this) {
        arrayOfBoolean3 = RealTimeSequencer.this.trackMuted;
        arrayOfBoolean2 = RealTimeSequencer.this.trackSolo;
      } 
      boolean bool = false;
      if (arrayOfBoolean2 != null)
        for (byte b = 0; b < arrayOfBoolean2.length; b++) {
          if (arrayOfBoolean2[b]) {
            bool = true;
            break;
          } 
        }  
      if (bool) {
        for (byte b = 0; b < arrayOfBoolean1.length; b++)
          arrayOfBoolean1[b] = (b >= arrayOfBoolean2.length || !arrayOfBoolean2[b]); 
      } else {
        for (byte b = 0; b < arrayOfBoolean1.length; b++)
          arrayOfBoolean1[b] = (arrayOfBoolean3 != null && b < arrayOfBoolean3.length && arrayOfBoolean3[b]); 
      } 
      return arrayOfBoolean1;
    }
    
    private void sendNoteOffIfOn(Track param1Track, long param1Long) {
      int i = param1Track.size();
      byte b = 0;
      try {
        for (byte b1 = 0; b1 < i; b1++) {
          MidiEvent midiEvent = param1Track.get(b1);
          if (midiEvent.getTick() > param1Long)
            break; 
          MidiMessage midiMessage = midiEvent.getMessage();
          int j = midiMessage.getStatus();
          int k = midiMessage.getLength();
          if (k == 3 && (j & 0xF0) == 144) {
            int m = -1;
            if (midiMessage instanceof ShortMessage) {
              ShortMessage shortMessage = (ShortMessage)midiMessage;
              if (shortMessage.getData2() > 0)
                m = shortMessage.getData1(); 
            } else {
              byte[] arrayOfByte = midiMessage.getMessage();
              if ((arrayOfByte[2] & 0x7F) > 0)
                m = arrayOfByte[1] & 0x7F; 
            } 
            if (m >= 0) {
              int n = 1 << (j & 0xF);
              if ((this.noteOnCache[m] & n) != 0) {
                RealTimeSequencer.this.getTransmitterList().sendMessage(j | m << 8, -1L);
                this.noteOnCache[m] = this.noteOnCache[m] & (0xFFFF ^ n);
                b++;
              } 
            } 
          } 
        } 
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
    }
    
    private void applyDisabledTracks(boolean[] param1ArrayOfBoolean1, boolean[] param1ArrayOfBoolean2) {
      byte[][] arrayOfByte = (byte[][])null;
      synchronized (RealTimeSequencer.this) {
        for (byte b = 0; b < param1ArrayOfBoolean2.length; b++) {
          if ((param1ArrayOfBoolean1 == null || b >= param1ArrayOfBoolean1.length || !param1ArrayOfBoolean1[b]) && param1ArrayOfBoolean2[b]) {
            if (this.tracks.length > b)
              sendNoteOffIfOn(this.tracks[b], this.lastTick); 
          } else if (param1ArrayOfBoolean1 != null && b < param1ArrayOfBoolean1.length && param1ArrayOfBoolean1[b] && !param1ArrayOfBoolean2[b]) {
            if (arrayOfByte == null)
              arrayOfByte = new byte[128][16]; 
            chaseTrackEvents(b, 0L, this.lastTick, true, arrayOfByte);
          } 
        } 
      } 
    }
    
    private void chaseTrackEvents(int param1Int, long param1Long1, long param1Long2, boolean param1Boolean, byte[][] param1ArrayOfByte) {
      if (param1Long1 > param1Long2)
        param1Long1 = 0L; 
      byte[] arrayOfByte = new byte[16];
      for (byte b1 = 0; b1 < 16; b1++) {
        arrayOfByte[b1] = -1;
        for (byte b = 0; b < ''; b++)
          param1ArrayOfByte[b][b1] = -1; 
      } 
      Track track = this.tracks[param1Int];
      int i = track.size();
      try {
        for (byte b = 0; b < i; b++) {
          MidiEvent midiEvent = track.get(b);
          if (midiEvent.getTick() >= param1Long2) {
            if (param1Boolean && param1Int < this.trackReadPos.length)
              this.trackReadPos[param1Int] = (b > 0) ? (b - 1) : 0; 
            break;
          } 
          MidiMessage midiMessage = midiEvent.getMessage();
          int j = midiMessage.getStatus();
          int k = midiMessage.getLength();
          if (k == 3 && (j & 0xF0) == 176)
            if (midiMessage instanceof ShortMessage) {
              ShortMessage shortMessage = (ShortMessage)midiMessage;
              param1ArrayOfByte[shortMessage.getData1() & 0x7F][j & 0xF] = (byte)shortMessage.getData2();
            } else {
              byte[] arrayOfByte1 = midiMessage.getMessage();
              param1ArrayOfByte[arrayOfByte1[1] & 0x7F][j & 0xF] = arrayOfByte1[2];
            }  
          if (k == 2 && (j & 0xF0) == 192)
            if (midiMessage instanceof ShortMessage) {
              ShortMessage shortMessage = (ShortMessage)midiMessage;
              arrayOfByte[j & 0xF] = (byte)shortMessage.getData1();
            } else {
              byte[] arrayOfByte1 = midiMessage.getMessage();
              arrayOfByte[j & 0xF] = arrayOfByte1[1];
            }  
        } 
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
      byte b2 = 0;
      for (byte b3 = 0; b3 < 16; b3++) {
        for (byte b = 0; b < ''; b++) {
          byte b4 = param1ArrayOfByte[b][b3];
          if (b4 >= 0) {
            char c = 0xB0 | b3 | b << 8 | b4 << 16;
            RealTimeSequencer.this.getTransmitterList().sendMessage(c, -1L);
            b2++;
          } 
        } 
        if (arrayOfByte[b3] >= 0)
          RealTimeSequencer.this.getTransmitterList().sendMessage(0xC0 | b3 | arrayOfByte[b3] << 8, -1L); 
        if (arrayOfByte[b3] >= 0 || param1Long1 == 0L || param1Long2 == 0L) {
          RealTimeSequencer.this.getTransmitterList().sendMessage(0xE0 | b3 | 0x400000, -1L);
          RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | b3 | 0x4000, -1L);
        } 
      } 
    }
    
    void chaseEvents(long param1Long1, long param1Long2) {
      byte[][] arrayOfByte = new byte[128][16];
      for (byte b = 0; b < this.tracks.length; b++) {
        if (this.trackDisabled == null || this.trackDisabled.length <= b || !this.trackDisabled[b])
          chaseTrackEvents(b, param1Long1, param1Long2, true, arrayOfByte); 
      } 
    }
    
    private long getCurrentTimeMillis() { return System.nanoTime() / 1000000L; }
    
    private long millis2tick(long param1Long) {
      if (this.divisionType != 0.0F) {
        double d = param1Long * this.tempoFactor * this.divisionType * this.resolution / 1000.0D;
        return (long)d;
      } 
      return MidiUtils.microsec2ticks(param1Long * 1000L, (this.currTempo * this.inverseTempoFactor), this.resolution);
    }
    
    private long tick2millis(long param1Long) {
      if (this.divisionType != 0.0F) {
        double d = param1Long * 1000.0D / this.tempoFactor * this.divisionType * this.resolution;
        return (long)d;
      } 
      return MidiUtils.ticks2microsec(param1Long, (this.currTempo * this.inverseTempoFactor), this.resolution) / 1000L;
    }
    
    private void ReindexTrack(int param1Int, long param1Long) {
      if (param1Int < this.trackReadPos.length && param1Int < this.tracks.length)
        this.trackReadPos[param1Int] = MidiUtils.tick2index(this.tracks[param1Int], param1Long); 
    }
    
    private boolean dispatchMessage(int param1Int, MidiEvent param1MidiEvent) {
      boolean bool = false;
      MidiMessage midiMessage = param1MidiEvent.getMessage();
      int i = midiMessage.getStatus();
      int j = midiMessage.getLength();
      if (i == 255 && j >= 2) {
        if (param1Int == 0) {
          int k = MidiUtils.getTempoMPQ(midiMessage);
          if (k > 0) {
            if (param1MidiEvent.getTick() != this.ignoreTempoEventAt) {
              setTempoMPQ(k);
              bool = true;
            } 
            this.ignoreTempoEventAt = -1L;
          } 
        } 
        RealTimeSequencer.this.sendMetaEvents(midiMessage);
      } else {
        int n;
        int m;
        ShortMessage shortMessage;
        int k;
        RealTimeSequencer.this.getTransmitterList().sendMessage(midiMessage, -1L);
        switch (i & 0xF0) {
          case 128:
            k = ((ShortMessage)midiMessage).getData1() & 0x7F;
            this.noteOnCache[k] = this.noteOnCache[k] & (0xFFFF ^ 1 << (i & 0xF));
            break;
          case 144:
            shortMessage = (ShortMessage)midiMessage;
            m = shortMessage.getData1() & 0x7F;
            n = shortMessage.getData2() & 0x7F;
            if (n > 0) {
              this.noteOnCache[m] = this.noteOnCache[m] | 1 << (i & 0xF);
              break;
            } 
            this.noteOnCache[m] = this.noteOnCache[m] & (0xFFFF ^ 1 << (i & 0xF));
            break;
          case 176:
            RealTimeSequencer.this.sendControllerEvents(midiMessage);
            break;
        } 
      } 
      return bool;
    }
    
    boolean pump() {
      long l2 = this.lastTick;
      boolean bool1 = false;
      boolean bool = false;
      boolean bool2 = false;
      long l1 = getCurrentTimeMillis();
      byte b = 0;
      do {
        bool1 = false;
        if (this.needReindex) {
          if (this.trackReadPos.length < this.tracks.length)
            this.trackReadPos = new int[this.tracks.length]; 
          for (byte b2 = 0; b2 < this.tracks.length; b2++)
            ReindexTrack(b2, l2); 
          this.needReindex = false;
          this.checkPointMillis = 0L;
        } 
        if (this.checkPointMillis == 0L) {
          l1 = getCurrentTimeMillis();
          this.checkPointMillis = l1;
          l2 = this.lastTick;
          this.checkPointTick = l2;
        } else {
          l2 = this.checkPointTick + millis2tick(l1 - this.checkPointMillis);
          if (RealTimeSequencer.this.loopEnd != -1L && ((RealTimeSequencer.this.loopCount > 0 && this.currLoopCounter > 0) || RealTimeSequencer.this.loopCount == -1) && this.lastTick <= RealTimeSequencer.this.loopEnd && l2 >= RealTimeSequencer.this.loopEnd) {
            l2 = RealTimeSequencer.this.loopEnd - 1L;
            bool = true;
          } 
          this.lastTick = l2;
        } 
        b = 0;
        for (byte b1 = 0; b1 < this.tracks.length; b1++) {
          try {
            boolean bool3 = this.trackDisabled[b1];
            Track track = this.tracks[b1];
            int i = this.trackReadPos[b1];
            int j = track.size();
            MidiEvent midiEvent;
            while (!bool1 && i < j && (midiEvent = track.get(i)).getTick() <= l2) {
              if (i == j - 1 && MidiUtils.isMetaEndOfTrack(midiEvent.getMessage())) {
                i = j;
                break;
              } 
              i++;
              if (!bool3 || (!b1 && MidiUtils.isMetaTempo(midiEvent.getMessage())))
                bool1 = dispatchMessage(b1, midiEvent); 
            } 
            if (i >= j)
              b++; 
            this.trackReadPos[b1] = i;
          } catch (Exception exception) {
            if (exception instanceof ArrayIndexOutOfBoundsException) {
              this.needReindex = true;
              bool1 = true;
            } 
          } 
          if (bool1)
            break; 
        } 
        bool2 = (b == this.tracks.length);
        if (!bool && (((RealTimeSequencer.this.loopCount <= 0 || this.currLoopCounter <= 0) && RealTimeSequencer.this.loopCount != -1) || bool1 || RealTimeSequencer.this.loopEnd != -1L || !bool2))
          continue; 
        long l3 = this.checkPointMillis;
        long l4 = RealTimeSequencer.this.loopEnd;
        if (l4 == -1L)
          l4 = this.lastTick; 
        if (RealTimeSequencer.this.loopCount != -1)
          this.currLoopCounter--; 
        setTickPos(RealTimeSequencer.this.loopStart);
        this.checkPointMillis = l3 + tick2millis(l4 - this.checkPointTick);
        this.checkPointTick = RealTimeSequencer.this.loopStart;
        this.needReindex = false;
        bool1 = false;
        bool = false;
        bool2 = false;
      } while (bool1);
      return bool2;
    }
  }
  
  final class PlayThread implements Runnable {
    private Thread thread;
    
    private final Object lock = new Object();
    
    boolean interrupted = false;
    
    boolean isPumping = false;
    
    private final RealTimeSequencer.DataPump dataPump = new RealTimeSequencer.DataPump(RealTimeSequencer.this);
    
    PlayThread() {
      byte b = 8;
      this.thread = JSSecurityManager.createThread(this, "Java Sound Sequencer", false, b, true);
    }
    
    RealTimeSequencer.DataPump getDataPump() { return this.dataPump; }
    
    void setSequence(Sequence param1Sequence) throws InvalidMidiDataException { this.dataPump.setSequence(param1Sequence); }
    
    void start() throws MidiUnavailableException {
      RealTimeSequencer.this.running = true;
      if (!this.dataPump.hasCachedTempo()) {
        long l = RealTimeSequencer.this.getTickPosition();
        this.dataPump.setTempoMPQ(RealTimeSequencer.this.tempoCache.getTempoMPQAt(l));
      } 
      this.dataPump.checkPointMillis = 0L;
      this.dataPump.clearNoteOnCache();
      this.dataPump.needReindex = true;
      this.dataPump.resetLoopCount();
      synchronized (this.lock) {
        this.lock.notifyAll();
      } 
    }
    
    void stop() throws MidiUnavailableException {
      playThreadImplStop();
      long l = System.nanoTime() / 1000000L;
      while (this.isPumping) {
        synchronized (this.lock) {
          try {
            this.lock.wait(2000L);
          } catch (InterruptedException interruptedException) {}
        } 
        if (System.nanoTime() / 1000000L - l > 1900L);
      } 
    }
    
    void playThreadImplStop() throws MidiUnavailableException {
      RealTimeSequencer.this.running = false;
      synchronized (this.lock) {
        this.lock.notifyAll();
      } 
    }
    
    void close() throws MidiUnavailableException {
      Thread thread1 = null;
      synchronized (this) {
        this.interrupted = true;
        thread1 = this.thread;
        this.thread = null;
      } 
      if (thread1 != null)
        synchronized (this.lock) {
          this.lock.notifyAll();
        }  
      if (thread1 != null)
        try {
          thread1.join(2000L);
        } catch (InterruptedException interruptedException) {} 
    }
    
    public void run() throws MidiUnavailableException {
      while (!this.interrupted) {
        boolean bool1 = false;
        boolean bool2 = RealTimeSequencer.this.running;
        this.isPumping = (!this.interrupted && RealTimeSequencer.this.running);
        while (!bool1 && !this.interrupted && RealTimeSequencer.this.running) {
          bool1 = this.dataPump.pump();
          try {
            Thread.sleep(1L);
          } catch (InterruptedException interruptedException) {}
        } 
        playThreadImplStop();
        if (bool2)
          this.dataPump.notesOff(true); 
        if (bool1) {
          this.dataPump.setTickPos(RealTimeSequencer.this.sequence.getTickLength());
          MetaMessage metaMessage = new MetaMessage();
          try {
            metaMessage.setMessage(47, new byte[0], 0);
          } catch (InvalidMidiDataException invalidMidiDataException) {}
          RealTimeSequencer.this.sendMetaEvents(metaMessage);
        } 
        synchronized (this.lock) {
          this.isPumping = false;
          this.lock.notifyAll();
          while (!RealTimeSequencer.this.running && !this.interrupted) {
            try {
              this.lock.wait();
            } catch (Exception exception) {}
          } 
        } 
      } 
    }
  }
  
  private static class RealTimeSequencerInfo extends MidiDevice.Info {
    private static final String name = "Real Time Sequencer";
    
    private static final String vendor = "Oracle Corporation";
    
    private static final String description = "Software sequencer";
    
    private static final String version = "Version 1.0";
    
    private RealTimeSequencerInfo() throws MidiUnavailableException { super("Real Time Sequencer", "Oracle Corporation", "Software sequencer", "Version 1.0"); }
  }
  
  static class RecordingTrack {
    private final Track track;
    
    private int channel;
    
    RecordingTrack(Track param1Track, int param1Int) {
      this.track = param1Track;
      this.channel = param1Int;
    }
    
    static RecordingTrack get(List param1List, Track param1Track) {
      synchronized (param1List) {
        int i = param1List.size();
        for (byte b = 0; b < i; b++) {
          RecordingTrack recordingTrack = (RecordingTrack)param1List.get(b);
          if (recordingTrack.track == param1Track)
            return recordingTrack; 
        } 
      } 
      return null;
    }
    
    static Track get(List param1List, int param1Int) {
      synchronized (param1List) {
        int i = param1List.size();
        for (byte b = 0; b < i; b++) {
          RecordingTrack recordingTrack = (RecordingTrack)param1List.get(b);
          if (recordingTrack.channel == param1Int || recordingTrack.channel == -1)
            return recordingTrack.track; 
        } 
      } 
      return null;
    }
  }
  
  final class SequencerReceiver extends AbstractMidiDevice.AbstractReceiver {
    SequencerReceiver() { super(RealTimeSequencer.this); }
    
    void implSend(MidiMessage param1MidiMessage, long param1Long) {
      if (RealTimeSequencer.this.recording) {
        long l = 0L;
        if (param1Long < 0L) {
          l = RealTimeSequencer.this.getTickPosition();
        } else {
          synchronized (RealTimeSequencer.this.tempoCache) {
            l = MidiUtils.microsecond2tick(RealTimeSequencer.this.sequence, param1Long, RealTimeSequencer.this.tempoCache);
          } 
        } 
        Track track = null;
        if (param1MidiMessage.getLength() > 1) {
          if (param1MidiMessage instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage)param1MidiMessage;
            if ((shortMessage.getStatus() & 0xF0) != 240)
              track = RealTimeSequencer.RecordingTrack.get(RealTimeSequencer.this.recordingTracks, shortMessage.getChannel()); 
          } else {
            track = RealTimeSequencer.RecordingTrack.get(RealTimeSequencer.this.recordingTracks, -1);
          } 
          if (track != null) {
            if (param1MidiMessage instanceof ShortMessage) {
              param1MidiMessage = new FastShortMessage((ShortMessage)param1MidiMessage);
            } else {
              param1MidiMessage = (MidiMessage)param1MidiMessage.clone();
            } 
            MidiEvent midiEvent = new MidiEvent(param1MidiMessage, l);
            track.add(midiEvent);
          } 
        } 
      } 
    }
  }
  
  private class SequencerTransmitter extends AbstractMidiDevice.BasicTransmitter {
    private SequencerTransmitter() { super(RealTimeSequencer.this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\RealTimeSequencer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */