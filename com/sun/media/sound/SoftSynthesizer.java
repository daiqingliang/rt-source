package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Transmitter;
import javax.sound.midi.VoiceStatus;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public final class SoftSynthesizer implements AudioSynthesizer, ReferenceCountingDevice {
  static final String INFO_NAME = "Gervill";
  
  static final String INFO_VENDOR = "OpenJDK";
  
  static final String INFO_DESCRIPTION = "Software MIDI Synthesizer";
  
  static final String INFO_VERSION = "1.0";
  
  static final MidiDevice.Info info = new Info();
  
  private static SourceDataLine testline = null;
  
  private static Soundbank defaultSoundBank = null;
  
  WeakAudioStream weakstream = null;
  
  final Object control_mutex = this;
  
  int voiceIDCounter = 0;
  
  int voice_allocation_mode = 0;
  
  boolean load_default_soundbank = false;
  
  boolean reverb_light = true;
  
  boolean reverb_on = true;
  
  boolean chorus_on = true;
  
  boolean agc_on = true;
  
  SoftChannel[] channels;
  
  SoftChannelProxy[] external_channels = null;
  
  private boolean largemode = false;
  
  private int gmmode = 0;
  
  private int deviceid = 0;
  
  private AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
  
  private SourceDataLine sourceDataLine = null;
  
  private SoftAudioPusher pusher = null;
  
  private AudioInputStream pusher_stream = null;
  
  private float controlrate = 147.0F;
  
  private boolean open = false;
  
  private boolean implicitOpen = false;
  
  private String resamplerType = "linear";
  
  private SoftResampler resampler = new SoftLinearResampler();
  
  private int number_of_midi_channels = 16;
  
  private int maxpoly = 64;
  
  private long latency = 200000L;
  
  private boolean jitter_correction = false;
  
  private SoftMainMixer mainmixer;
  
  private SoftVoice[] voices;
  
  private Map<String, SoftTuning> tunings = new HashMap();
  
  private Map<String, SoftInstrument> inslist = new HashMap();
  
  private Map<String, ModelInstrument> loadedlist = new HashMap();
  
  private ArrayList<Receiver> recvslist = new ArrayList();
  
  private void getBuffers(ModelInstrument paramModelInstrument, List<ModelByteBuffer> paramList) {
    for (ModelPerformer modelPerformer : paramModelInstrument.getPerformers()) {
      if (modelPerformer.getOscillators() != null)
        for (ModelOscillator modelOscillator : modelPerformer.getOscillators()) {
          if (modelOscillator instanceof ModelByteBufferWavetable) {
            ModelByteBufferWavetable modelByteBufferWavetable = (ModelByteBufferWavetable)modelOscillator;
            ModelByteBuffer modelByteBuffer = modelByteBufferWavetable.getBuffer();
            if (modelByteBuffer != null)
              paramList.add(modelByteBuffer); 
            modelByteBuffer = modelByteBufferWavetable.get8BitExtensionBuffer();
            if (modelByteBuffer != null)
              paramList.add(modelByteBuffer); 
          } 
        }  
    } 
  }
  
  private boolean loadSamples(List<ModelInstrument> paramList) {
    if (this.largemode)
      return true; 
    ArrayList arrayList = new ArrayList();
    for (ModelInstrument modelInstrument : paramList)
      getBuffers(modelInstrument, arrayList); 
    try {
      ModelByteBuffer.loadAll(arrayList);
    } catch (IOException iOException) {
      return false;
    } 
    return true;
  }
  
  private boolean loadInstruments(List<ModelInstrument> paramList) {
    if (!isOpen())
      return false; 
    if (!loadSamples(paramList))
      return false; 
    synchronized (this.control_mutex) {
      if (this.channels != null)
        for (SoftChannel softChannel : this.channels) {
          softChannel.current_instrument = null;
          softChannel.current_director = null;
        }  
      for (Instrument instrument : paramList) {
        String str = patchToString(instrument.getPatch());
        SoftInstrument softInstrument = new SoftInstrument((ModelInstrument)instrument);
        this.inslist.put(str, softInstrument);
        this.loadedlist.put(str, (ModelInstrument)instrument);
      } 
    } 
    return true;
  }
  
  private void processPropertyInfo(Map<String, Object> paramMap) {
    AudioSynthesizerPropertyInfo[] arrayOfAudioSynthesizerPropertyInfo = getPropertyInfo(paramMap);
    String str = (String)(arrayOfAudioSynthesizerPropertyInfo[0]).value;
    if (str.equalsIgnoreCase("point")) {
      this.resampler = new SoftPointResampler();
      this.resamplerType = "point";
    } else if (str.equalsIgnoreCase("linear")) {
      this.resampler = new SoftLinearResampler2();
      this.resamplerType = "linear";
    } else if (str.equalsIgnoreCase("linear1")) {
      this.resampler = new SoftLinearResampler();
      this.resamplerType = "linear1";
    } else if (str.equalsIgnoreCase("linear2")) {
      this.resampler = new SoftLinearResampler2();
      this.resamplerType = "linear2";
    } else if (str.equalsIgnoreCase("cubic")) {
      this.resampler = new SoftCubicResampler();
      this.resamplerType = "cubic";
    } else if (str.equalsIgnoreCase("lanczos")) {
      this.resampler = new SoftLanczosResampler();
      this.resamplerType = "lanczos";
    } else if (str.equalsIgnoreCase("sinc")) {
      this.resampler = new SoftSincResampler();
      this.resamplerType = "sinc";
    } 
    setFormat((AudioFormat)(arrayOfAudioSynthesizerPropertyInfo[2]).value);
    this.controlrate = ((Float)(arrayOfAudioSynthesizerPropertyInfo[1]).value).floatValue();
    this.latency = ((Long)(arrayOfAudioSynthesizerPropertyInfo[3]).value).longValue();
    this.deviceid = ((Integer)(arrayOfAudioSynthesizerPropertyInfo[4]).value).intValue();
    this.maxpoly = ((Integer)(arrayOfAudioSynthesizerPropertyInfo[5]).value).intValue();
    this.reverb_on = ((Boolean)(arrayOfAudioSynthesizerPropertyInfo[6]).value).booleanValue();
    this.chorus_on = ((Boolean)(arrayOfAudioSynthesizerPropertyInfo[7]).value).booleanValue();
    this.agc_on = ((Boolean)(arrayOfAudioSynthesizerPropertyInfo[8]).value).booleanValue();
    this.largemode = ((Boolean)(arrayOfAudioSynthesizerPropertyInfo[9]).value).booleanValue();
    this.number_of_midi_channels = ((Integer)(arrayOfAudioSynthesizerPropertyInfo[10]).value).intValue();
    this.jitter_correction = ((Boolean)(arrayOfAudioSynthesizerPropertyInfo[11]).value).booleanValue();
    this.reverb_light = ((Boolean)(arrayOfAudioSynthesizerPropertyInfo[12]).value).booleanValue();
    this.load_default_soundbank = ((Boolean)(arrayOfAudioSynthesizerPropertyInfo[13]).value).booleanValue();
  }
  
  private String patchToString(Patch paramPatch) { return (paramPatch instanceof ModelPatch && ((ModelPatch)paramPatch).isPercussion()) ? ("p." + paramPatch.getProgram() + "." + paramPatch.getBank()) : (paramPatch.getProgram() + "." + paramPatch.getBank()); }
  
  private void setFormat(AudioFormat paramAudioFormat) {
    if (paramAudioFormat.getChannels() > 2)
      throw new IllegalArgumentException("Only mono and stereo audio supported."); 
    if (AudioFloatConverter.getConverter(paramAudioFormat) == null)
      throw new IllegalArgumentException("Audio format not supported."); 
    this.format = paramAudioFormat;
  }
  
  void removeReceiver(Receiver paramReceiver) {
    boolean bool = false;
    synchronized (this.control_mutex) {
      if (this.recvslist.remove(paramReceiver) && this.implicitOpen && this.recvslist.isEmpty())
        bool = true; 
    } 
    if (bool)
      close(); 
  }
  
  SoftMainMixer getMainMixer() { return !isOpen() ? null : this.mainmixer; }
  
  SoftInstrument findInstrument(int paramInt1, int paramInt2, int paramInt3) {
    String str;
    if (paramInt2 >> 7 == 120 || paramInt2 >> 7 == 121) {
      String str1;
      str = (SoftInstrument)this.inslist.get(paramInt1 + "." + paramInt2);
      if (str != null)
        return str; 
      if (paramInt2 >> 7 == 120) {
        str1 = "p.";
      } else {
        str1 = "";
      } 
      str = (SoftInstrument)this.inslist.get(str1 + paramInt1 + "." + ((paramInt2 & 0x80) << 7));
      if (str != null)
        return str; 
      str = (SoftInstrument)this.inslist.get(str1 + paramInt1 + "." + (paramInt2 & 0x80));
      if (str != null)
        return str; 
      str = (SoftInstrument)this.inslist.get(str1 + paramInt1 + ".0");
      if (str != null)
        return str; 
      str = (SoftInstrument)this.inslist.get(str1 + paramInt1 + "0.0");
      return (str != null) ? str : null;
    } 
    if (paramInt3 == 9) {
      str = "p.";
    } else {
      str = "";
    } 
    SoftInstrument softInstrument = (SoftInstrument)this.inslist.get(str + paramInt1 + "." + paramInt2);
    if (softInstrument != null)
      return softInstrument; 
    softInstrument = (SoftInstrument)this.inslist.get(str + paramInt1 + ".0");
    if (softInstrument != null)
      return softInstrument; 
    softInstrument = (SoftInstrument)this.inslist.get(str + "0.0");
    return (softInstrument != null) ? softInstrument : null;
  }
  
  int getVoiceAllocationMode() { return this.voice_allocation_mode; }
  
  int getGeneralMidiMode() { return this.gmmode; }
  
  void setGeneralMidiMode(int paramInt) { this.gmmode = paramInt; }
  
  int getDeviceID() { return this.deviceid; }
  
  float getControlRate() { return this.controlrate; }
  
  SoftVoice[] getVoices() { return this.voices; }
  
  SoftTuning getTuning(Patch paramPatch) {
    String str = patchToString(paramPatch);
    SoftTuning softTuning = (SoftTuning)this.tunings.get(str);
    if (softTuning == null) {
      softTuning = new SoftTuning(paramPatch);
      this.tunings.put(str, softTuning);
    } 
    return softTuning;
  }
  
  public long getLatency() {
    synchronized (this.control_mutex) {
      return this.latency;
    } 
  }
  
  public AudioFormat getFormat() {
    synchronized (this.control_mutex) {
      return this.format;
    } 
  }
  
  public int getMaxPolyphony() {
    synchronized (this.control_mutex) {
      return this.maxpoly;
    } 
  }
  
  public MidiChannel[] getChannels() {
    synchronized (this.control_mutex) {
      MidiChannel[] arrayOfMidiChannel;
      if (this.external_channels == null) {
        this.external_channels = new SoftChannelProxy[16];
        for (byte b1 = 0; b1 < this.external_channels.length; b1++)
          this.external_channels[b1] = new SoftChannelProxy(); 
      } 
      if (isOpen()) {
        arrayOfMidiChannel = new MidiChannel[this.channels.length];
      } else {
        arrayOfMidiChannel = new MidiChannel[16];
      } 
      for (byte b = 0; b < arrayOfMidiChannel.length; b++)
        arrayOfMidiChannel[b] = this.external_channels[b]; 
      return arrayOfMidiChannel;
    } 
  }
  
  public VoiceStatus[] getVoiceStatus() {
    if (!isOpen()) {
      VoiceStatus[] arrayOfVoiceStatus = new VoiceStatus[getMaxPolyphony()];
      for (byte b = 0; b < arrayOfVoiceStatus.length; b++) {
        VoiceStatus voiceStatus = new VoiceStatus();
        voiceStatus.active = false;
        voiceStatus.bank = 0;
        voiceStatus.channel = 0;
        voiceStatus.note = 0;
        voiceStatus.program = 0;
        voiceStatus.volume = 0;
        arrayOfVoiceStatus[b] = voiceStatus;
      } 
      return arrayOfVoiceStatus;
    } 
    synchronized (this.control_mutex) {
      VoiceStatus[] arrayOfVoiceStatus = new VoiceStatus[this.voices.length];
      for (byte b = 0; b < this.voices.length; b++) {
        SoftVoice softVoice = this.voices[b];
        VoiceStatus voiceStatus = new VoiceStatus();
        voiceStatus.active = softVoice.active;
        voiceStatus.bank = softVoice.bank;
        voiceStatus.channel = softVoice.channel;
        voiceStatus.note = softVoice.note;
        voiceStatus.program = softVoice.program;
        voiceStatus.volume = softVoice.volume;
        arrayOfVoiceStatus[b] = voiceStatus;
      } 
      return arrayOfVoiceStatus;
    } 
  }
  
  public boolean isSoundbankSupported(Soundbank paramSoundbank) {
    for (Instrument instrument : paramSoundbank.getInstruments()) {
      if (!(instrument instanceof ModelInstrument))
        return false; 
    } 
    return true;
  }
  
  public boolean loadInstrument(Instrument paramInstrument) {
    if (paramInstrument == null || !(paramInstrument instanceof ModelInstrument))
      throw new IllegalArgumentException("Unsupported instrument: " + paramInstrument); 
    ArrayList arrayList = new ArrayList();
    arrayList.add((ModelInstrument)paramInstrument);
    return loadInstruments(arrayList);
  }
  
  public void unloadInstrument(Instrument paramInstrument) {
    if (paramInstrument == null || !(paramInstrument instanceof ModelInstrument))
      throw new IllegalArgumentException("Unsupported instrument: " + paramInstrument); 
    if (!isOpen())
      return; 
    String str = patchToString(paramInstrument.getPatch());
    synchronized (this.control_mutex) {
      for (SoftChannel softChannel : this.channels)
        softChannel.current_instrument = null; 
      this.inslist.remove(str);
      this.loadedlist.remove(str);
      for (byte b = 0; b < this.channels.length; b++)
        this.channels[b].allSoundOff(); 
    } 
  }
  
  public boolean remapInstrument(Instrument paramInstrument1, Instrument paramInstrument2) {
    if (paramInstrument1 == null)
      throw new NullPointerException(); 
    if (paramInstrument2 == null)
      throw new NullPointerException(); 
    if (!(paramInstrument1 instanceof ModelInstrument))
      throw new IllegalArgumentException("Unsupported instrument: " + paramInstrument1.toString()); 
    if (!(paramInstrument2 instanceof ModelInstrument))
      throw new IllegalArgumentException("Unsupported instrument: " + paramInstrument2.toString()); 
    if (!isOpen())
      return false; 
    synchronized (this.control_mutex) {
      if (!this.loadedlist.containsValue(paramInstrument2))
        throw new IllegalArgumentException("Instrument to is not loaded."); 
      unloadInstrument(paramInstrument1);
      ModelMappedInstrument modelMappedInstrument = new ModelMappedInstrument((ModelInstrument)paramInstrument2, paramInstrument1.getPatch());
      return loadInstrument(modelMappedInstrument);
    } 
  }
  
  public Soundbank getDefaultSoundbank() {
    synchronized (SoftSynthesizer.class) {
      if (defaultSoundBank != null)
        return defaultSoundBank; 
      ArrayList arrayList = new ArrayList();
      arrayList.add(new PrivilegedAction<InputStream>() {
            public InputStream run() {
              File file1 = new File(System.getProperties().getProperty("java.home"));
              File file2 = new File(new File(file1, "lib"), "audio");
              if (file2.exists()) {
                File file = null;
                File[] arrayOfFile = file2.listFiles();
                if (arrayOfFile != null)
                  for (byte b = 0; b < arrayOfFile.length; b++) {
                    File file3 = arrayOfFile[b];
                    if (file3.isFile()) {
                      String str = file3.getName().toLowerCase();
                      if ((str.endsWith(".sf2") || str.endsWith(".dls")) && (file == null || file3.length() > file.length()))
                        file = file3; 
                    } 
                  }  
                if (file != null)
                  try {
                    return new FileInputStream(file);
                  } catch (IOException iOException) {} 
              } 
              return null;
            }
          });
      arrayList.add(new PrivilegedAction<InputStream>() {
            public InputStream run() {
              if (System.getProperties().getProperty("os.name").startsWith("Linux")) {
                File[] arrayOfFile = { new File("/usr/share/soundfonts/"), new File("/usr/local/share/soundfonts/"), new File("/usr/share/sounds/sf2/"), new File("/usr/local/share/sounds/sf2/") };
                for (File file : arrayOfFile) {
                  if (file.exists()) {
                    File file1 = new File(file, "default.sf2");
                    if (file1.exists())
                      try {
                        return new FileInputStream(file1);
                      } catch (IOException iOException) {} 
                  } 
                } 
              } 
              return null;
            }
          });
      arrayList.add(new PrivilegedAction<InputStream>() {
            public InputStream run() {
              if (System.getProperties().getProperty("os.name").startsWith("Windows")) {
                File file = new File(System.getenv("SystemRoot") + "\\system32\\drivers\\gm.dls");
                if (file.exists())
                  try {
                    return new FileInputStream(file);
                  } catch (IOException iOException) {} 
              } 
              return null;
            }
          });
      arrayList.add(new PrivilegedAction<InputStream>() {
            public InputStream run() {
              File file1 = new File(System.getProperty("user.home"), ".gervill");
              File file2 = new File(file1, "soundbank-emg.sf2");
              if (file2.exists())
                try {
                  return new FileInputStream(file2);
                } catch (IOException iOException) {} 
              return null;
            }
          });
      for (PrivilegedAction privilegedAction : arrayList) {
        try {
          Soundbank soundbank;
          inputStream = (InputStream)AccessController.doPrivileged(privilegedAction);
          if (inputStream == null)
            continue; 
          try {
            soundbank = MidiSystem.getSoundbank(new BufferedInputStream(inputStream));
          } finally {
            inputStream.close();
          } 
          if (soundbank != null) {
            defaultSoundBank = soundbank;
            return defaultSoundBank;
          } 
        } catch (Exception exception) {}
      } 
      try {
        defaultSoundBank = EmergencySoundbank.createSoundbank();
      } catch (Exception exception) {}
      if (defaultSoundBank != null) {
        OutputStream outputStream = (OutputStream)AccessController.doPrivileged(() -> {
              try {
                File file1 = new File(System.getProperty("user.home"), ".gervill");
                if (!file1.exists())
                  file1.mkdirs(); 
                File file2 = new File(file1, "soundbank-emg.sf2");
                return file2.exists() ? null : new FileOutputStream(file2);
              } catch (FileNotFoundException fileNotFoundException) {
                return null;
              } 
            });
        if (outputStream != null)
          try {
            ((SF2Soundbank)defaultSoundBank).save(outputStream);
            outputStream.close();
          } catch (IOException iOException) {} 
      } 
    } 
    return defaultSoundBank;
  }
  
  public Instrument[] getAvailableInstruments() {
    Soundbank soundbank = getDefaultSoundbank();
    if (soundbank == null)
      return new Instrument[0]; 
    Instrument[] arrayOfInstrument = soundbank.getInstruments();
    Arrays.sort(arrayOfInstrument, new ModelInstrumentComparator());
    return arrayOfInstrument;
  }
  
  public Instrument[] getLoadedInstruments() {
    if (!isOpen())
      return new Instrument[0]; 
    synchronized (this.control_mutex) {
      ModelInstrument[] arrayOfModelInstrument = new ModelInstrument[this.loadedlist.values().size()];
      this.loadedlist.values().toArray(arrayOfModelInstrument);
      Arrays.sort(arrayOfModelInstrument, new ModelInstrumentComparator());
      return arrayOfModelInstrument;
    } 
  }
  
  public boolean loadAllInstruments(Soundbank paramSoundbank) {
    ArrayList arrayList = new ArrayList();
    for (Instrument instrument : paramSoundbank.getInstruments()) {
      if (instrument == null || !(instrument instanceof ModelInstrument))
        throw new IllegalArgumentException("Unsupported instrument: " + instrument); 
      arrayList.add((ModelInstrument)instrument);
    } 
    return loadInstruments(arrayList);
  }
  
  public void unloadAllInstruments(Soundbank paramSoundbank) {
    if (paramSoundbank == null || !isSoundbankSupported(paramSoundbank))
      throw new IllegalArgumentException("Unsupported soundbank: " + paramSoundbank); 
    if (!isOpen())
      return; 
    for (Instrument instrument : paramSoundbank.getInstruments()) {
      if (instrument instanceof ModelInstrument)
        unloadInstrument(instrument); 
    } 
  }
  
  public boolean loadInstruments(Soundbank paramSoundbank, Patch[] paramArrayOfPatch) {
    ArrayList arrayList = new ArrayList();
    for (Patch patch : paramArrayOfPatch) {
      Instrument instrument = paramSoundbank.getInstrument(patch);
      if (instrument == null || !(instrument instanceof ModelInstrument))
        throw new IllegalArgumentException("Unsupported instrument: " + instrument); 
      arrayList.add((ModelInstrument)instrument);
    } 
    return loadInstruments(arrayList);
  }
  
  public void unloadInstruments(Soundbank paramSoundbank, Patch[] paramArrayOfPatch) {
    if (paramSoundbank == null || !isSoundbankSupported(paramSoundbank))
      throw new IllegalArgumentException("Unsupported soundbank: " + paramSoundbank); 
    if (!isOpen())
      return; 
    for (Patch patch : paramArrayOfPatch) {
      Instrument instrument = paramSoundbank.getInstrument(patch);
      if (instrument instanceof ModelInstrument)
        unloadInstrument(instrument); 
    } 
  }
  
  public MidiDevice.Info getDeviceInfo() { return info; }
  
  private Properties getStoredProperties() { return (Properties)AccessController.doPrivileged(() -> {
          Properties properties = new Properties();
          String str = "/com/sun/media/sound/softsynthesizer";
          try {
            Preferences preferences = Preferences.userRoot();
            if (preferences.nodeExists(str)) {
              Preferences preferences1 = preferences.node(str);
              String[] arrayOfString = preferences1.keys();
              for (String str1 : arrayOfString) {
                String str2 = preferences1.get(str1, null);
                if (str2 != null)
                  properties.setProperty(str1, str2); 
              } 
            } 
          } catch (BackingStoreException backingStoreException) {}
          return properties;
        }); }
  
  public AudioSynthesizerPropertyInfo[] getPropertyInfo(Map<String, Object> paramMap) {
    ArrayList arrayList = new ArrayList();
    boolean bool = (paramMap == null && this.open) ? 1 : 0;
    AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("interpolation", bool ? this.resamplerType : "linear");
    audioSynthesizerPropertyInfo.choices = new String[] { "linear", "linear1", "linear2", "cubic", "lanczos", "sinc", "point" };
    audioSynthesizerPropertyInfo.description = "Interpolation method";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("control rate", Float.valueOf(bool ? this.controlrate : 147.0F));
    audioSynthesizerPropertyInfo.description = "Control rate";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("format", bool ? this.format : new AudioFormat(44100.0F, 16, 2, true, false));
    audioSynthesizerPropertyInfo.description = "Default audio format";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("latency", Long.valueOf(bool ? this.latency : 120000L));
    audioSynthesizerPropertyInfo.description = "Default latency";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("device id", Integer.valueOf(bool ? this.deviceid : 0));
    audioSynthesizerPropertyInfo.description = "Device ID for SysEx Messages";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("max polyphony", Integer.valueOf(bool ? this.maxpoly : 64));
    audioSynthesizerPropertyInfo.description = "Maximum polyphony";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("reverb", Boolean.valueOf(bool ? this.reverb_on : 1));
    audioSynthesizerPropertyInfo.description = "Turn reverb effect on or off";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("chorus", Boolean.valueOf(bool ? this.chorus_on : 1));
    audioSynthesizerPropertyInfo.description = "Turn chorus effect on or off";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("auto gain control", Boolean.valueOf(bool ? this.agc_on : 1));
    audioSynthesizerPropertyInfo.description = "Turn auto gain control on or off";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("large mode", Boolean.valueOf(bool ? this.largemode : 0));
    audioSynthesizerPropertyInfo.description = "Turn large mode on or off.";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("midi channels", Integer.valueOf(bool ? this.channels.length : 16));
    audioSynthesizerPropertyInfo.description = "Number of midi channels.";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("jitter correction", Boolean.valueOf(bool ? this.jitter_correction : 1));
    audioSynthesizerPropertyInfo.description = "Turn jitter correction on or off.";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("light reverb", Boolean.valueOf(bool ? this.reverb_light : 1));
    audioSynthesizerPropertyInfo.description = "Turn light reverb mode on or off";
    arrayList.add(audioSynthesizerPropertyInfo);
    audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("load default soundbank", Boolean.valueOf(bool ? this.load_default_soundbank : 1));
    audioSynthesizerPropertyInfo.description = "Enabled/disable loading default soundbank";
    arrayList.add(audioSynthesizerPropertyInfo);
    AudioSynthesizerPropertyInfo[] arrayOfAudioSynthesizerPropertyInfo = (AudioSynthesizerPropertyInfo[])arrayList.toArray(new AudioSynthesizerPropertyInfo[arrayList.size()]);
    Properties properties = getStoredProperties();
    for (AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo1 : arrayOfAudioSynthesizerPropertyInfo) {
      Object object = (paramMap == null) ? null : paramMap.get(audioSynthesizerPropertyInfo1.name);
      object = (object != null) ? object : properties.getProperty(audioSynthesizerPropertyInfo1.name);
      if (object != null) {
        Class clazz = audioSynthesizerPropertyInfo1.valueClass;
        if (clazz.isInstance(object)) {
          audioSynthesizerPropertyInfo1.value = object;
        } else if (object instanceof String) {
          String str = (String)object;
          if (clazz == Boolean.class) {
            if (str.equalsIgnoreCase("true"))
              audioSynthesizerPropertyInfo1.value = Boolean.TRUE; 
            if (str.equalsIgnoreCase("false"))
              audioSynthesizerPropertyInfo1.value = Boolean.FALSE; 
          } else if (clazz == AudioFormat.class) {
            int i = 2;
            boolean bool1 = true;
            boolean bool2 = false;
            int j = 16;
            float f = 44100.0F;
            try {
              StringTokenizer stringTokenizer = new StringTokenizer(str, ", ");
              for (String str1 = ""; stringTokenizer.hasMoreTokens(); str1 = str2) {
                String str2 = stringTokenizer.nextToken().toLowerCase();
                if (str2.equals("mono"))
                  i = 1; 
                if (str2.startsWith("channel"))
                  i = Integer.parseInt(str1); 
                if (str2.contains("unsigned"))
                  bool1 = false; 
                if (str2.equals("big-endian"))
                  bool2 = true; 
                if (str2.equals("bit"))
                  j = Integer.parseInt(str1); 
                if (str2.equals("hz"))
                  f = Float.parseFloat(str1); 
              } 
              audioSynthesizerPropertyInfo1.value = new AudioFormat(f, j, i, bool1, bool2);
            } catch (NumberFormatException numberFormatException) {}
          } else {
            try {
              if (clazz == Byte.class) {
                audioSynthesizerPropertyInfo1.value = Byte.valueOf(str);
              } else if (clazz == Short.class) {
                audioSynthesizerPropertyInfo1.value = Short.valueOf(str);
              } else if (clazz == Integer.class) {
                audioSynthesizerPropertyInfo1.value = Integer.valueOf(str);
              } else if (clazz == Long.class) {
                audioSynthesizerPropertyInfo1.value = Long.valueOf(str);
              } else if (clazz == Float.class) {
                audioSynthesizerPropertyInfo1.value = Float.valueOf(str);
              } else if (clazz == Double.class) {
                audioSynthesizerPropertyInfo1.value = Double.valueOf(str);
              } 
            } catch (NumberFormatException numberFormatException) {}
          } 
        } else if (object instanceof Number) {
          Number number = (Number)object;
          if (clazz == Byte.class)
            audioSynthesizerPropertyInfo1.value = Byte.valueOf(number.byteValue()); 
          if (clazz == Short.class)
            audioSynthesizerPropertyInfo1.value = Short.valueOf(number.shortValue()); 
          if (clazz == Integer.class)
            audioSynthesizerPropertyInfo1.value = Integer.valueOf(number.intValue()); 
          if (clazz == Long.class)
            audioSynthesizerPropertyInfo1.value = Long.valueOf(number.longValue()); 
          if (clazz == Float.class)
            audioSynthesizerPropertyInfo1.value = Float.valueOf(number.floatValue()); 
          if (clazz == Double.class)
            audioSynthesizerPropertyInfo1.value = Double.valueOf(number.doubleValue()); 
        } 
      } 
    } 
    return arrayOfAudioSynthesizerPropertyInfo;
  }
  
  public void open() {
    if (isOpen()) {
      synchronized (this.control_mutex) {
        this.implicitOpen = false;
      } 
      return;
    } 
    open(null, null);
  }
  
  public void open(SourceDataLine paramSourceDataLine, Map<String, Object> paramMap) throws MidiUnavailableException {
    if (isOpen()) {
      synchronized (this.control_mutex) {
        this.implicitOpen = false;
      } 
      return;
    } 
    synchronized (this.control_mutex) {
      try {
        if (paramSourceDataLine != null)
          setFormat(paramSourceDataLine.getFormat()); 
        AudioInputStream audioInputStream = openStream(getFormat(), paramMap);
        this.weakstream = new WeakAudioStream(audioInputStream);
        audioInputStream = this.weakstream.getAudioInputStream();
        if (paramSourceDataLine == null)
          if (testline != null) {
            paramSourceDataLine = testline;
          } else {
            paramSourceDataLine = AudioSystem.getSourceDataLine(getFormat());
          }  
        double d = this.latency;
        if (!paramSourceDataLine.isOpen()) {
          int k = getFormat().getFrameSize() * (int)(getFormat().getFrameRate() * d / 1000000.0D);
          paramSourceDataLine.open(getFormat(), k);
          this.sourceDataLine = paramSourceDataLine;
        } 
        if (!paramSourceDataLine.isActive())
          paramSourceDataLine.start(); 
        int i = 512;
        try {
          i = audioInputStream.available();
        } catch (IOException iOException) {}
        int j = paramSourceDataLine.getBufferSize();
        j -= j % i;
        if (j < 3 * i)
          j = 3 * i; 
        if (this.jitter_correction) {
          audioInputStream = new SoftJitterCorrector(audioInputStream, j, i);
          if (this.weakstream != null)
            this.weakstream.jitter_stream = audioInputStream; 
        } 
        this.pusher = new SoftAudioPusher(paramSourceDataLine, audioInputStream, i);
        this.pusher_stream = audioInputStream;
        this.pusher.start();
        if (this.weakstream != null) {
          this.weakstream.pusher = this.pusher;
          this.weakstream.sourceDataLine = this.sourceDataLine;
        } 
      } catch (LineUnavailableException|SecurityException|IllegalArgumentException lineUnavailableException) {
        if (isOpen())
          close(); 
        MidiUnavailableException midiUnavailableException = new MidiUnavailableException("Can not open line");
        midiUnavailableException.initCause(lineUnavailableException);
        throw midiUnavailableException;
      } 
    } 
  }
  
  public AudioInputStream openStream(AudioFormat paramAudioFormat, Map<String, Object> paramMap) throws MidiUnavailableException {
    if (isOpen())
      throw new MidiUnavailableException("Synthesizer is already open"); 
    synchronized (this.control_mutex) {
      this.gmmode = 0;
      this.voice_allocation_mode = 0;
      processPropertyInfo(paramMap);
      this.open = true;
      this.implicitOpen = false;
      if (paramAudioFormat != null)
        setFormat(paramAudioFormat); 
      if (this.load_default_soundbank) {
        Soundbank soundbank = getDefaultSoundbank();
        if (soundbank != null)
          loadAllInstruments(soundbank); 
      } 
      this.voices = new SoftVoice[this.maxpoly];
      byte b;
      for (b = 0; b < this.maxpoly; b++)
        this.voices[b] = new SoftVoice(this); 
      this.mainmixer = new SoftMainMixer(this);
      this.channels = new SoftChannel[this.number_of_midi_channels];
      for (b = 0; b < this.channels.length; b++)
        this.channels[b] = new SoftChannel(this, b); 
      if (this.external_channels == null) {
        if (this.channels.length < 16) {
          this.external_channels = new SoftChannelProxy[16];
        } else {
          this.external_channels = new SoftChannelProxy[this.channels.length];
        } 
        for (b = 0; b < this.external_channels.length; b++)
          this.external_channels[b] = new SoftChannelProxy(); 
      } else if (this.channels.length > this.external_channels.length) {
        SoftChannelProxy[] arrayOfSoftChannelProxy = new SoftChannelProxy[this.channels.length];
        int i;
        for (i = 0; i < this.external_channels.length; i++)
          arrayOfSoftChannelProxy[i] = this.external_channels[i]; 
        for (i = this.external_channels.length; i < arrayOfSoftChannelProxy.length; i++)
          arrayOfSoftChannelProxy[i] = new SoftChannelProxy(); 
      } 
      for (b = 0; b < this.channels.length; b++)
        this.external_channels[b].setChannel(this.channels[b]); 
      for (SoftVoice softVoice : getVoices())
        softVoice.resampler = this.resampler.openStreamer(); 
      for (Receiver receiver : getReceivers()) {
        SoftReceiver softReceiver = (SoftReceiver)receiver;
        softReceiver.open = this.open;
        softReceiver.mainmixer = this.mainmixer;
        softReceiver.midimessages = this.mainmixer.midimessages;
      } 
      return this.mainmixer.getInputStream();
    } 
  }
  
  public void close() {
    if (!isOpen())
      return; 
    SoftAudioPusher softAudioPusher = null;
    AudioInputStream audioInputStream = null;
    synchronized (this.control_mutex) {
      if (this.pusher != null) {
        softAudioPusher = this.pusher;
        audioInputStream = this.pusher_stream;
        this.pusher = null;
        this.pusher_stream = null;
      } 
    } 
    if (softAudioPusher != null) {
      softAudioPusher.stop();
      try {
        audioInputStream.close();
      } catch (IOException iOException) {}
    } 
    synchronized (this.control_mutex) {
      if (this.mainmixer != null)
        this.mainmixer.close(); 
      this.open = false;
      this.implicitOpen = false;
      this.mainmixer = null;
      this.voices = null;
      this.channels = null;
      if (this.external_channels != null)
        for (byte b = 0; b < this.external_channels.length; b++)
          this.external_channels[b].setChannel(null);  
      if (this.sourceDataLine != null) {
        this.sourceDataLine.close();
        this.sourceDataLine = null;
      } 
      this.inslist.clear();
      this.loadedlist.clear();
      this.tunings.clear();
      while (this.recvslist.size() != 0)
        ((Receiver)this.recvslist.get(this.recvslist.size() - 1)).close(); 
    } 
  }
  
  public boolean isOpen() {
    synchronized (this.control_mutex) {
      return this.open;
    } 
  }
  
  public long getMicrosecondPosition() {
    if (!isOpen())
      return 0L; 
    synchronized (this.control_mutex) {
      return this.mainmixer.getMicrosecondPosition();
    } 
  }
  
  public int getMaxReceivers() { return -1; }
  
  public int getMaxTransmitters() { return 0; }
  
  public Receiver getReceiver() throws MidiUnavailableException {
    synchronized (this.control_mutex) {
      SoftReceiver softReceiver = new SoftReceiver(this);
      softReceiver.open = this.open;
      this.recvslist.add(softReceiver);
      return softReceiver;
    } 
  }
  
  public List<Receiver> getReceivers() {
    synchronized (this.control_mutex) {
      ArrayList arrayList = new ArrayList();
      arrayList.addAll(this.recvslist);
      return arrayList;
    } 
  }
  
  public Transmitter getTransmitter() throws MidiUnavailableException { throw new MidiUnavailableException("No transmitter available"); }
  
  public List<Transmitter> getTransmitters() { return new ArrayList(); }
  
  public Receiver getReceiverReferenceCounting() throws MidiUnavailableException {
    if (!isOpen()) {
      open();
      synchronized (this.control_mutex) {
        this.implicitOpen = true;
      } 
    } 
    return getReceiver();
  }
  
  public Transmitter getTransmitterReferenceCounting() throws MidiUnavailableException { throw new MidiUnavailableException("No transmitter available"); }
  
  private static class Info extends MidiDevice.Info {
    Info() { super("Gervill", "OpenJDK", "Software MIDI Synthesizer", "1.0"); }
  }
  
  protected static final class WeakAudioStream extends InputStream {
    public SoftAudioPusher pusher = null;
    
    public AudioInputStream jitter_stream = null;
    
    public SourceDataLine sourceDataLine = null;
    
    private int framesize = 0;
    
    private WeakReference<AudioInputStream> weak_stream_link;
    
    private AudioFloatConverter converter;
    
    private float[] silentbuffer = null;
    
    private int samplesize;
    
    public void setInputStream(AudioInputStream param1AudioInputStream) { this.stream = param1AudioInputStream; }
    
    public int available() {
      AudioInputStream audioInputStream = this.stream;
      return (audioInputStream != null) ? audioInputStream.available() : 0;
    }
    
    public int read() {
      byte[] arrayOfByte = new byte[1];
      return (read(arrayOfByte) == -1) ? -1 : (arrayOfByte[0] & 0xFF);
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      AudioInputStream audioInputStream = this.stream;
      if (audioInputStream != null)
        return audioInputStream.read(param1ArrayOfByte, param1Int1, param1Int2); 
      int i = param1Int2 / this.samplesize;
      if (this.silentbuffer == null || this.silentbuffer.length < i)
        this.silentbuffer = new float[i]; 
      this.converter.toByteArray(this.silentbuffer, i, param1ArrayOfByte, param1Int1);
      this.silent_samples += (param1Int2 / this.framesize);
      if (this.pusher != null && this.weak_stream_link.get() == null) {
        Runnable runnable = new Runnable() {
            SoftAudioPusher _pusher = SoftSynthesizer.WeakAudioStream.this.pusher;
            
            AudioInputStream _jitter_stream = SoftSynthesizer.WeakAudioStream.this.jitter_stream;
            
            SourceDataLine _sourceDataLine = SoftSynthesizer.WeakAudioStream.this.sourceDataLine;
            
            public void run() {
              this._pusher.stop();
              if (this._jitter_stream != null)
                try {
                  this._jitter_stream.close();
                } catch (IOException iOException) {
                  iOException.printStackTrace();
                }  
              if (this._sourceDataLine != null)
                this._sourceDataLine.close(); 
            }
          };
        this.pusher = null;
        this.jitter_stream = null;
        this.sourceDataLine = null;
        (new Thread(runnable)).start();
      } 
      return param1Int2;
    }
    
    public WeakAudioStream(AudioInputStream param1AudioInputStream) {
      this.stream = param1AudioInputStream;
      this.weak_stream_link = new WeakReference(param1AudioInputStream);
      this.converter = AudioFloatConverter.getConverter(param1AudioInputStream.getFormat());
      this.samplesize = param1AudioInputStream.getFormat().getFrameSize() / param1AudioInputStream.getFormat().getChannels();
      this.framesize = param1AudioInputStream.getFormat().getFrameSize();
    }
    
    public AudioInputStream getAudioInputStream() { return new AudioInputStream(this, this.stream.getFormat(), -1L); }
    
    public void close() {
      AudioInputStream audioInputStream = (AudioInputStream)this.weak_stream_link.get();
      if (audioInputStream != null)
        audioInputStream.close(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftSynthesizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */