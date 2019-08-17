package javax.sound.midi;

import com.sun.media.sound.AutoConnectSequencer;
import com.sun.media.sound.JDK13Services;
import com.sun.media.sound.MidiDeviceReceiverEnvelope;
import com.sun.media.sound.MidiDeviceTransmitterEnvelope;
import com.sun.media.sound.ReferenceCountingDevice;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.sound.midi.spi.MidiDeviceProvider;
import javax.sound.midi.spi.MidiFileReader;
import javax.sound.midi.spi.MidiFileWriter;
import javax.sound.midi.spi.SoundbankReader;

public class MidiSystem {
  public static MidiDevice.Info[] getMidiDeviceInfo() {
    ArrayList arrayList = new ArrayList();
    List list = getMidiDeviceProviders();
    for (byte b = 0; b < list.size(); b++) {
      MidiDeviceProvider midiDeviceProvider = (MidiDeviceProvider)list.get(b);
      MidiDevice.Info[] arrayOfInfo = midiDeviceProvider.getDeviceInfo();
      for (byte b1 = 0; b1 < arrayOfInfo.length; b1++)
        arrayList.add(arrayOfInfo[b1]); 
    } 
    return (Info[])arrayList.toArray(new MidiDevice.Info[0]);
  }
  
  public static MidiDevice getMidiDevice(MidiDevice.Info paramInfo) throws MidiUnavailableException {
    List list = getMidiDeviceProviders();
    for (byte b = 0; b < list.size(); b++) {
      MidiDeviceProvider midiDeviceProvider = (MidiDeviceProvider)list.get(b);
      if (midiDeviceProvider.isDeviceSupported(paramInfo))
        return midiDeviceProvider.getDevice(paramInfo); 
    } 
    throw new IllegalArgumentException("Requested device not installed: " + paramInfo);
  }
  
  public static Receiver getReceiver() throws MidiUnavailableException {
    Receiver receiver;
    MidiDevice midiDevice = getDefaultDeviceWrapper(Receiver.class);
    if (midiDevice instanceof ReferenceCountingDevice) {
      receiver = ((ReferenceCountingDevice)midiDevice).getReceiverReferenceCounting();
    } else {
      receiver = midiDevice.getReceiver();
    } 
    if (!(receiver instanceof MidiDeviceReceiver))
      receiver = new MidiDeviceReceiverEnvelope(midiDevice, receiver); 
    return receiver;
  }
  
  public static Transmitter getTransmitter() throws MidiUnavailableException {
    Transmitter transmitter;
    MidiDevice midiDevice = getDefaultDeviceWrapper(Transmitter.class);
    if (midiDevice instanceof ReferenceCountingDevice) {
      transmitter = ((ReferenceCountingDevice)midiDevice).getTransmitterReferenceCounting();
    } else {
      transmitter = midiDevice.getTransmitter();
    } 
    if (!(transmitter instanceof MidiDeviceTransmitter))
      transmitter = new MidiDeviceTransmitterEnvelope(midiDevice, transmitter); 
    return transmitter;
  }
  
  public static Synthesizer getSynthesizer() throws MidiUnavailableException { return (Synthesizer)getDefaultDeviceWrapper(Synthesizer.class); }
  
  public static Sequencer getSequencer() throws MidiUnavailableException { return getSequencer(true); }
  
  public static Sequencer getSequencer(boolean paramBoolean) throws MidiUnavailableException {
    Sequencer sequencer = (Sequencer)getDefaultDeviceWrapper(Sequencer.class);
    if (paramBoolean) {
      receiver = null;
      MidiUnavailableException midiUnavailableException = null;
      try {
        synthesizer = getSynthesizer();
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
      } catch (MidiUnavailableException midiUnavailableException1) {
        if (midiUnavailableException1 instanceof MidiUnavailableException)
          midiUnavailableException = midiUnavailableException1; 
      } 
      if (receiver == null)
        try {
          receiver = getReceiver();
        } catch (Exception exception) {
          if (exception instanceof MidiUnavailableException)
            midiUnavailableException = (MidiUnavailableException)exception; 
        }  
      if (receiver != null) {
        sequencer.getTransmitter().setReceiver(receiver);
        if (sequencer instanceof AutoConnectSequencer)
          ((AutoConnectSequencer)sequencer).setAutoConnect(receiver); 
      } else {
        if (midiUnavailableException != null)
          throw midiUnavailableException; 
        throw new MidiUnavailableException("no receiver available");
      } 
    } 
    return sequencer;
  }
  
  public static Soundbank getSoundbank(InputStream paramInputStream) throws InvalidMidiDataException, IOException {
    SoundbankReader soundbankReader = null;
    Soundbank soundbank = null;
    List list = getSoundbankReaders();
    for (byte b = 0; b < list.size(); b++) {
      soundbankReader = (SoundbankReader)list.get(b);
      soundbank = soundbankReader.getSoundbank(paramInputStream);
      if (soundbank != null)
        return soundbank; 
    } 
    throw new InvalidMidiDataException("cannot get soundbank from stream");
  }
  
  public static Soundbank getSoundbank(URL paramURL) throws InvalidMidiDataException, IOException {
    SoundbankReader soundbankReader = null;
    Soundbank soundbank = null;
    List list = getSoundbankReaders();
    for (byte b = 0; b < list.size(); b++) {
      soundbankReader = (SoundbankReader)list.get(b);
      soundbank = soundbankReader.getSoundbank(paramURL);
      if (soundbank != null)
        return soundbank; 
    } 
    throw new InvalidMidiDataException("cannot get soundbank from stream");
  }
  
  public static Soundbank getSoundbank(File paramFile) throws InvalidMidiDataException, IOException {
    SoundbankReader soundbankReader = null;
    Soundbank soundbank = null;
    List list = getSoundbankReaders();
    for (byte b = 0; b < list.size(); b++) {
      soundbankReader = (SoundbankReader)list.get(b);
      soundbank = soundbankReader.getSoundbank(paramFile);
      if (soundbank != null)
        return soundbank; 
    } 
    throw new InvalidMidiDataException("cannot get soundbank from stream");
  }
  
  public static MidiFileFormat getMidiFileFormat(InputStream paramInputStream) throws InvalidMidiDataException, IOException {
    List list = getMidiFileReaders();
    MidiFileFormat midiFileFormat = null;
    byte b = 0;
    while (b < list.size()) {
      MidiFileReader midiFileReader = (MidiFileReader)list.get(b);
      try {
        midiFileFormat = midiFileReader.getMidiFileFormat(paramInputStream);
        break;
      } catch (InvalidMidiDataException invalidMidiDataException) {
        b++;
      } 
    } 
    if (midiFileFormat == null)
      throw new InvalidMidiDataException("input stream is not a supported file type"); 
    return midiFileFormat;
  }
  
  public static MidiFileFormat getMidiFileFormat(URL paramURL) throws InvalidMidiDataException, IOException {
    List list = getMidiFileReaders();
    MidiFileFormat midiFileFormat = null;
    byte b = 0;
    while (b < list.size()) {
      MidiFileReader midiFileReader = (MidiFileReader)list.get(b);
      try {
        midiFileFormat = midiFileReader.getMidiFileFormat(paramURL);
        break;
      } catch (InvalidMidiDataException invalidMidiDataException) {
        b++;
      } 
    } 
    if (midiFileFormat == null)
      throw new InvalidMidiDataException("url is not a supported file type"); 
    return midiFileFormat;
  }
  
  public static MidiFileFormat getMidiFileFormat(File paramFile) throws InvalidMidiDataException, IOException {
    List list = getMidiFileReaders();
    MidiFileFormat midiFileFormat = null;
    byte b = 0;
    while (b < list.size()) {
      MidiFileReader midiFileReader = (MidiFileReader)list.get(b);
      try {
        midiFileFormat = midiFileReader.getMidiFileFormat(paramFile);
        break;
      } catch (InvalidMidiDataException invalidMidiDataException) {
        b++;
      } 
    } 
    if (midiFileFormat == null)
      throw new InvalidMidiDataException("file is not a supported file type"); 
    return midiFileFormat;
  }
  
  public static Sequence getSequence(InputStream paramInputStream) throws InvalidMidiDataException, IOException {
    List list = getMidiFileReaders();
    Sequence sequence = null;
    byte b = 0;
    while (b < list.size()) {
      MidiFileReader midiFileReader = (MidiFileReader)list.get(b);
      try {
        sequence = midiFileReader.getSequence(paramInputStream);
        break;
      } catch (InvalidMidiDataException invalidMidiDataException) {
        b++;
      } 
    } 
    if (sequence == null)
      throw new InvalidMidiDataException("could not get sequence from input stream"); 
    return sequence;
  }
  
  public static Sequence getSequence(URL paramURL) throws InvalidMidiDataException, IOException {
    List list = getMidiFileReaders();
    Sequence sequence = null;
    byte b = 0;
    while (b < list.size()) {
      MidiFileReader midiFileReader = (MidiFileReader)list.get(b);
      try {
        sequence = midiFileReader.getSequence(paramURL);
        break;
      } catch (InvalidMidiDataException invalidMidiDataException) {
        b++;
      } 
    } 
    if (sequence == null)
      throw new InvalidMidiDataException("could not get sequence from URL"); 
    return sequence;
  }
  
  public static Sequence getSequence(File paramFile) throws InvalidMidiDataException, IOException {
    List list = getMidiFileReaders();
    Sequence sequence = null;
    byte b = 0;
    while (b < list.size()) {
      MidiFileReader midiFileReader = (MidiFileReader)list.get(b);
      try {
        sequence = midiFileReader.getSequence(paramFile);
        break;
      } catch (InvalidMidiDataException invalidMidiDataException) {
        b++;
      } 
    } 
    if (sequence == null)
      throw new InvalidMidiDataException("could not get sequence from file"); 
    return sequence;
  }
  
  public static int[] getMidiFileTypes() {
    List list = getMidiFileWriters();
    HashSet hashSet = new HashSet();
    for (byte b1 = 0; b1 < list.size(); b1++) {
      MidiFileWriter midiFileWriter = (MidiFileWriter)list.get(b1);
      int[] arrayOfInt1 = midiFileWriter.getMidiFileTypes();
      for (byte b = 0; b < arrayOfInt1.length; b++)
        hashSet.add(new Integer(arrayOfInt1[b])); 
    } 
    int[] arrayOfInt = new int[hashSet.size()];
    byte b2 = 0;
    for (Integer integer : hashSet)
      arrayOfInt[b2++] = integer.intValue(); 
    return arrayOfInt;
  }
  
  public static boolean isFileTypeSupported(int paramInt) {
    List list = getMidiFileWriters();
    for (byte b = 0; b < list.size(); b++) {
      MidiFileWriter midiFileWriter = (MidiFileWriter)list.get(b);
      if (midiFileWriter.isFileTypeSupported(paramInt))
        return true; 
    } 
    return false;
  }
  
  public static int[] getMidiFileTypes(Sequence paramSequence) {
    List list = getMidiFileWriters();
    HashSet hashSet = new HashSet();
    for (byte b1 = 0; b1 < list.size(); b1++) {
      MidiFileWriter midiFileWriter = (MidiFileWriter)list.get(b1);
      int[] arrayOfInt1 = midiFileWriter.getMidiFileTypes(paramSequence);
      for (byte b = 0; b < arrayOfInt1.length; b++)
        hashSet.add(new Integer(arrayOfInt1[b])); 
    } 
    int[] arrayOfInt = new int[hashSet.size()];
    byte b2 = 0;
    for (Integer integer : hashSet)
      arrayOfInt[b2++] = integer.intValue(); 
    return arrayOfInt;
  }
  
  public static boolean isFileTypeSupported(int paramInt, Sequence paramSequence) {
    List list = getMidiFileWriters();
    for (byte b = 0; b < list.size(); b++) {
      MidiFileWriter midiFileWriter = (MidiFileWriter)list.get(b);
      if (midiFileWriter.isFileTypeSupported(paramInt, paramSequence))
        return true; 
    } 
    return false;
  }
  
  public static int write(Sequence paramSequence, int paramInt, OutputStream paramOutputStream) throws IOException {
    List list = getMidiFileWriters();
    int i = -2;
    for (byte b = 0; b < list.size(); b++) {
      MidiFileWriter midiFileWriter = (MidiFileWriter)list.get(b);
      if (midiFileWriter.isFileTypeSupported(paramInt, paramSequence)) {
        i = midiFileWriter.write(paramSequence, paramInt, paramOutputStream);
        break;
      } 
    } 
    if (i == -2)
      throw new IllegalArgumentException("MIDI file type is not supported"); 
    return i;
  }
  
  public static int write(Sequence paramSequence, int paramInt, File paramFile) throws IOException {
    List list = getMidiFileWriters();
    int i = -2;
    for (byte b = 0; b < list.size(); b++) {
      MidiFileWriter midiFileWriter = (MidiFileWriter)list.get(b);
      if (midiFileWriter.isFileTypeSupported(paramInt, paramSequence)) {
        i = midiFileWriter.write(paramSequence, paramInt, paramFile);
        break;
      } 
    } 
    if (i == -2)
      throw new IllegalArgumentException("MIDI file type is not supported"); 
    return i;
  }
  
  private static List getMidiDeviceProviders() { return getProviders(MidiDeviceProvider.class); }
  
  private static List getSoundbankReaders() { return getProviders(SoundbankReader.class); }
  
  private static List getMidiFileWriters() { return getProviders(MidiFileWriter.class); }
  
  private static List getMidiFileReaders() { return getProviders(MidiFileReader.class); }
  
  private static MidiDevice getDefaultDeviceWrapper(Class paramClass) throws MidiUnavailableException {
    try {
      return getDefaultDevice(paramClass);
    } catch (IllegalArgumentException illegalArgumentException) {
      MidiUnavailableException midiUnavailableException = new MidiUnavailableException();
      midiUnavailableException.initCause(illegalArgumentException);
      throw midiUnavailableException;
    } 
  }
  
  private static MidiDevice getDefaultDevice(Class paramClass) throws MidiUnavailableException {
    List list = getMidiDeviceProviders();
    String str1 = JDK13Services.getDefaultProviderClassName(paramClass);
    String str2 = JDK13Services.getDefaultInstanceName(paramClass);
    if (str1 != null) {
      MidiDeviceProvider midiDeviceProvider = getNamedProvider(str1, list);
      if (midiDeviceProvider != null) {
        if (str2 != null) {
          MidiDevice midiDevice2 = getNamedDevice(str2, midiDeviceProvider, paramClass);
          if (midiDevice2 != null)
            return midiDevice2; 
        } 
        MidiDevice midiDevice1 = getFirstDevice(midiDeviceProvider, paramClass);
        if (midiDevice1 != null)
          return midiDevice1; 
      } 
    } 
    if (str2 != null) {
      MidiDevice midiDevice1 = getNamedDevice(str2, list, paramClass);
      if (midiDevice1 != null)
        return midiDevice1; 
    } 
    MidiDevice midiDevice = getFirstDevice(list, paramClass);
    if (midiDevice != null)
      return midiDevice; 
    throw new IllegalArgumentException("Requested device not installed");
  }
  
  private static MidiDeviceProvider getNamedProvider(String paramString, List paramList) {
    for (byte b = 0; b < paramList.size(); b++) {
      MidiDeviceProvider midiDeviceProvider = (MidiDeviceProvider)paramList.get(b);
      if (midiDeviceProvider.getClass().getName().equals(paramString))
        return midiDeviceProvider; 
    } 
    return null;
  }
  
  private static MidiDevice getNamedDevice(String paramString, MidiDeviceProvider paramMidiDeviceProvider, Class paramClass) {
    MidiDevice midiDevice = getNamedDevice(paramString, paramMidiDeviceProvider, paramClass, false, false);
    if (midiDevice != null)
      return midiDevice; 
    if (paramClass == Receiver.class) {
      midiDevice = getNamedDevice(paramString, paramMidiDeviceProvider, paramClass, true, false);
      if (midiDevice != null)
        return midiDevice; 
    } 
    return null;
  }
  
  private static MidiDevice getNamedDevice(String paramString, MidiDeviceProvider paramMidiDeviceProvider, Class paramClass, boolean paramBoolean1, boolean paramBoolean2) {
    MidiDevice.Info[] arrayOfInfo = paramMidiDeviceProvider.getDeviceInfo();
    for (byte b = 0; b < arrayOfInfo.length; b++) {
      if (arrayOfInfo[b].getName().equals(paramString)) {
        MidiDevice midiDevice = paramMidiDeviceProvider.getDevice(arrayOfInfo[b]);
        if (isAppropriateDevice(midiDevice, paramClass, paramBoolean1, paramBoolean2))
          return midiDevice; 
      } 
    } 
    return null;
  }
  
  private static MidiDevice getNamedDevice(String paramString, List paramList, Class paramClass) {
    MidiDevice midiDevice = getNamedDevice(paramString, paramList, paramClass, false, false);
    if (midiDevice != null)
      return midiDevice; 
    if (paramClass == Receiver.class) {
      midiDevice = getNamedDevice(paramString, paramList, paramClass, true, false);
      if (midiDevice != null)
        return midiDevice; 
    } 
    return null;
  }
  
  private static MidiDevice getNamedDevice(String paramString, List paramList, Class paramClass, boolean paramBoolean1, boolean paramBoolean2) {
    for (byte b = 0; b < paramList.size(); b++) {
      MidiDeviceProvider midiDeviceProvider = (MidiDeviceProvider)paramList.get(b);
      MidiDevice midiDevice = getNamedDevice(paramString, midiDeviceProvider, paramClass, paramBoolean1, paramBoolean2);
      if (midiDevice != null)
        return midiDevice; 
    } 
    return null;
  }
  
  private static MidiDevice getFirstDevice(MidiDeviceProvider paramMidiDeviceProvider, Class paramClass) {
    MidiDevice midiDevice = getFirstDevice(paramMidiDeviceProvider, paramClass, false, false);
    if (midiDevice != null)
      return midiDevice; 
    if (paramClass == Receiver.class) {
      midiDevice = getFirstDevice(paramMidiDeviceProvider, paramClass, true, false);
      if (midiDevice != null)
        return midiDevice; 
    } 
    return null;
  }
  
  private static MidiDevice getFirstDevice(MidiDeviceProvider paramMidiDeviceProvider, Class paramClass, boolean paramBoolean1, boolean paramBoolean2) {
    MidiDevice.Info[] arrayOfInfo = paramMidiDeviceProvider.getDeviceInfo();
    for (byte b = 0; b < arrayOfInfo.length; b++) {
      MidiDevice midiDevice = paramMidiDeviceProvider.getDevice(arrayOfInfo[b]);
      if (isAppropriateDevice(midiDevice, paramClass, paramBoolean1, paramBoolean2))
        return midiDevice; 
    } 
    return null;
  }
  
  private static MidiDevice getFirstDevice(List paramList, Class paramClass) {
    MidiDevice midiDevice = getFirstDevice(paramList, paramClass, false, false);
    if (midiDevice != null)
      return midiDevice; 
    if (paramClass == Receiver.class) {
      midiDevice = getFirstDevice(paramList, paramClass, true, false);
      if (midiDevice != null)
        return midiDevice; 
    } 
    return null;
  }
  
  private static MidiDevice getFirstDevice(List paramList, Class paramClass, boolean paramBoolean1, boolean paramBoolean2) {
    for (byte b = 0; b < paramList.size(); b++) {
      MidiDeviceProvider midiDeviceProvider = (MidiDeviceProvider)paramList.get(b);
      MidiDevice midiDevice = getFirstDevice(midiDeviceProvider, paramClass, paramBoolean1, paramBoolean2);
      if (midiDevice != null)
        return midiDevice; 
    } 
    return null;
  }
  
  private static boolean isAppropriateDevice(MidiDevice paramMidiDevice, Class paramClass, boolean paramBoolean1, boolean paramBoolean2) { return paramClass.isInstance(paramMidiDevice) ? true : ((((!(paramMidiDevice instanceof Sequencer) && !(paramMidiDevice instanceof Synthesizer)) || (paramMidiDevice instanceof Sequencer && paramBoolean2) || (paramMidiDevice instanceof Synthesizer && paramBoolean1)) && ((paramClass == Receiver.class && paramMidiDevice.getMaxReceivers() != 0) || (paramClass == Transmitter.class && paramMidiDevice.getMaxTransmitters() != 0)))); }
  
  private static List getProviders(Class paramClass) { return JDK13Services.getProviders(paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\MidiSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */