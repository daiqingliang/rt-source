package javax.sound.sampled;

import com.sun.media.sound.JDK13Services;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.spi.AudioFileWriter;
import javax.sound.sampled.spi.FormatConversionProvider;
import javax.sound.sampled.spi.MixerProvider;

public class AudioSystem {
  public static final int NOT_SPECIFIED = -1;
  
  public static Mixer.Info[] getMixerInfo() {
    List list = getMixerInfoList();
    return (Info[])list.toArray(new Mixer.Info[list.size()]);
  }
  
  public static Mixer getMixer(Mixer.Info paramInfo) {
    Object object = null;
    List list = getMixerProviders();
    byte b;
    for (b = 0; b < list.size(); b++) {
      try {
        return ((MixerProvider)list.get(b)).getMixer(paramInfo);
      } catch (IllegalArgumentException illegalArgumentException) {
      
      } catch (NullPointerException nullPointerException) {}
    } 
    if (paramInfo == null)
      for (b = 0; b < list.size(); b++) {
        try {
          MixerProvider mixerProvider = (MixerProvider)list.get(b);
          Mixer.Info[] arrayOfInfo = mixerProvider.getMixerInfo();
          byte b1 = 0;
          while (b1 < arrayOfInfo.length) {
            try {
              return mixerProvider.getMixer(arrayOfInfo[b1]);
            } catch (IllegalArgumentException illegalArgumentException) {
              b1++;
            } 
          } 
        } catch (IllegalArgumentException illegalArgumentException) {
        
        } catch (NullPointerException nullPointerException) {}
      }  
    throw new IllegalArgumentException("Mixer not supported: " + ((paramInfo != null) ? paramInfo.toString() : "null"));
  }
  
  public static Line.Info[] getSourceLineInfo(Line.Info paramInfo) {
    Vector vector = new Vector();
    Object object = null;
    Mixer.Info[] arrayOfInfo = getMixerInfo();
    for (byte b1 = 0; b1 < arrayOfInfo.length; b1++) {
      Mixer mixer = getMixer(arrayOfInfo[b1]);
      Line.Info[] arrayOfInfo2 = mixer.getSourceLineInfo(paramInfo);
      for (byte b = 0; b < arrayOfInfo2.length; b++)
        vector.addElement(arrayOfInfo2[b]); 
    } 
    Line.Info[] arrayOfInfo1 = new Line.Info[vector.size()];
    for (byte b2 = 0; b2 < arrayOfInfo1.length; b2++)
      arrayOfInfo1[b2] = (Line.Info)vector.get(b2); 
    return arrayOfInfo1;
  }
  
  public static Line.Info[] getTargetLineInfo(Line.Info paramInfo) {
    Vector vector = new Vector();
    Object object = null;
    Mixer.Info[] arrayOfInfo = getMixerInfo();
    for (byte b1 = 0; b1 < arrayOfInfo.length; b1++) {
      Mixer mixer = getMixer(arrayOfInfo[b1]);
      Line.Info[] arrayOfInfo2 = mixer.getTargetLineInfo(paramInfo);
      for (byte b = 0; b < arrayOfInfo2.length; b++)
        vector.addElement(arrayOfInfo2[b]); 
    } 
    Line.Info[] arrayOfInfo1 = new Line.Info[vector.size()];
    for (byte b2 = 0; b2 < arrayOfInfo1.length; b2++)
      arrayOfInfo1[b2] = (Line.Info)vector.get(b2); 
    return arrayOfInfo1;
  }
  
  public static boolean isLineSupported(Line.Info paramInfo) {
    Mixer.Info[] arrayOfInfo = getMixerInfo();
    for (byte b = 0; b < arrayOfInfo.length; b++) {
      if (arrayOfInfo[b] != null) {
        Mixer mixer = getMixer(arrayOfInfo[b]);
        if (mixer.isLineSupported(paramInfo))
          return true; 
      } 
    } 
    return false;
  }
  
  public static Line getLine(Line.Info paramInfo) throws LineUnavailableException {
    LineUnavailableException lineUnavailableException = null;
    List list = getMixerProviders();
    try {
      Mixer mixer = getDefaultMixer(list, paramInfo);
      if (mixer != null && mixer.isLineSupported(paramInfo))
        return mixer.getLine(paramInfo); 
    } catch (LineUnavailableException lineUnavailableException1) {
      lineUnavailableException = lineUnavailableException1;
    } catch (IllegalArgumentException illegalArgumentException) {}
    byte b;
    for (b = 0; b < list.size(); b++) {
      MixerProvider mixerProvider = (MixerProvider)list.get(b);
      Mixer.Info[] arrayOfInfo = mixerProvider.getMixerInfo();
      for (byte b1 = 0; b1 < arrayOfInfo.length; b1++) {
        try {
          Mixer mixer = mixerProvider.getMixer(arrayOfInfo[b1]);
          if (isAppropriateMixer(mixer, paramInfo, true))
            return mixer.getLine(paramInfo); 
        } catch (LineUnavailableException lineUnavailableException1) {
          lineUnavailableException = lineUnavailableException1;
        } catch (IllegalArgumentException illegalArgumentException) {}
      } 
    } 
    for (b = 0; b < list.size(); b++) {
      MixerProvider mixerProvider = (MixerProvider)list.get(b);
      Mixer.Info[] arrayOfInfo = mixerProvider.getMixerInfo();
      for (byte b1 = 0; b1 < arrayOfInfo.length; b1++) {
        try {
          Mixer mixer = mixerProvider.getMixer(arrayOfInfo[b1]);
          if (isAppropriateMixer(mixer, paramInfo, false))
            return mixer.getLine(paramInfo); 
        } catch (LineUnavailableException lineUnavailableException1) {
          lineUnavailableException = lineUnavailableException1;
        } catch (IllegalArgumentException illegalArgumentException) {}
      } 
    } 
    if (lineUnavailableException != null)
      throw lineUnavailableException; 
    throw new IllegalArgumentException("No line matching " + paramInfo.toString() + " is supported.");
  }
  
  public static Clip getClip() throws LineUnavailableException {
    AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, true);
    DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
    return (Clip)getLine(info);
  }
  
  public static Clip getClip(Mixer.Info paramInfo) throws LineUnavailableException {
    AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, true);
    DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
    Mixer mixer = getMixer(paramInfo);
    return (Clip)mixer.getLine(info);
  }
  
  public static SourceDataLine getSourceDataLine(AudioFormat paramAudioFormat) throws LineUnavailableException {
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, paramAudioFormat);
    return (SourceDataLine)getLine(info);
  }
  
  public static SourceDataLine getSourceDataLine(AudioFormat paramAudioFormat, Mixer.Info paramInfo) throws LineUnavailableException {
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, paramAudioFormat);
    Mixer mixer = getMixer(paramInfo);
    return (SourceDataLine)mixer.getLine(info);
  }
  
  public static TargetDataLine getTargetDataLine(AudioFormat paramAudioFormat) throws LineUnavailableException {
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, paramAudioFormat);
    return (TargetDataLine)getLine(info);
  }
  
  public static TargetDataLine getTargetDataLine(AudioFormat paramAudioFormat, Mixer.Info paramInfo) throws LineUnavailableException {
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, paramAudioFormat);
    Mixer mixer = getMixer(paramInfo);
    return (TargetDataLine)mixer.getLine(info);
  }
  
  public static AudioFormat.Encoding[] getTargetEncodings(AudioFormat.Encoding paramEncoding) {
    List list = getFormatConversionProviders();
    Vector vector = new Vector();
    AudioFormat.Encoding[] arrayOfEncoding = null;
    for (byte b = 0; b < list.size(); b++) {
      FormatConversionProvider formatConversionProvider = (FormatConversionProvider)list.get(b);
      if (formatConversionProvider.isSourceEncodingSupported(paramEncoding)) {
        arrayOfEncoding = formatConversionProvider.getTargetEncodings();
        for (byte b1 = 0; b1 < arrayOfEncoding.length; b1++)
          vector.addElement(arrayOfEncoding[b1]); 
      } 
    } 
    return (Encoding[])vector.toArray(new AudioFormat.Encoding[0]);
  }
  
  public static AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat) {
    List list = getFormatConversionProviders();
    Vector vector = new Vector();
    int i = 0;
    byte b1 = 0;
    Encoding[] arrayOfEncoding = null;
    for (byte b2 = 0; b2 < list.size(); b2++) {
      arrayOfEncoding = ((FormatConversionProvider)list.get(b2)).getTargetEncodings(paramAudioFormat);
      i += arrayOfEncoding.length;
      vector.addElement(arrayOfEncoding);
    } 
    AudioFormat.Encoding[] arrayOfEncoding1 = new AudioFormat.Encoding[i];
    for (byte b3 = 0; b3 < vector.size(); b3++) {
      arrayOfEncoding = (Encoding[])vector.get(b3);
      for (byte b = 0; b < arrayOfEncoding.length; b++)
        arrayOfEncoding1[b1++] = arrayOfEncoding[b]; 
    } 
    return arrayOfEncoding1;
  }
  
  public static boolean isConversionSupported(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat) {
    List list = getFormatConversionProviders();
    for (byte b = 0; b < list.size(); b++) {
      FormatConversionProvider formatConversionProvider = (FormatConversionProvider)list.get(b);
      if (formatConversionProvider.isConversionSupported(paramEncoding, paramAudioFormat))
        return true; 
    } 
    return false;
  }
  
  public static AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream) {
    List list = getFormatConversionProviders();
    for (byte b = 0; b < list.size(); b++) {
      FormatConversionProvider formatConversionProvider = (FormatConversionProvider)list.get(b);
      if (formatConversionProvider.isConversionSupported(paramEncoding, paramAudioInputStream.getFormat()))
        return formatConversionProvider.getAudioInputStream(paramEncoding, paramAudioInputStream); 
    } 
    throw new IllegalArgumentException("Unsupported conversion: " + paramEncoding + " from " + paramAudioInputStream.getFormat());
  }
  
  public static AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat) {
    List list = getFormatConversionProviders();
    Vector vector = new Vector();
    int i = 0;
    byte b1 = 0;
    AudioFormat[] arrayOfAudioFormat1 = null;
    for (byte b2 = 0; b2 < list.size(); b2++) {
      FormatConversionProvider formatConversionProvider = (FormatConversionProvider)list.get(b2);
      arrayOfAudioFormat1 = formatConversionProvider.getTargetFormats(paramEncoding, paramAudioFormat);
      i += arrayOfAudioFormat1.length;
      vector.addElement(arrayOfAudioFormat1);
    } 
    AudioFormat[] arrayOfAudioFormat2 = new AudioFormat[i];
    for (byte b3 = 0; b3 < vector.size(); b3++) {
      arrayOfAudioFormat1 = (AudioFormat[])vector.get(b3);
      for (byte b = 0; b < arrayOfAudioFormat1.length; b++)
        arrayOfAudioFormat2[b1++] = arrayOfAudioFormat1[b]; 
    } 
    return arrayOfAudioFormat2;
  }
  
  public static boolean isConversionSupported(AudioFormat paramAudioFormat1, AudioFormat paramAudioFormat2) {
    List list = getFormatConversionProviders();
    for (byte b = 0; b < list.size(); b++) {
      FormatConversionProvider formatConversionProvider = (FormatConversionProvider)list.get(b);
      if (formatConversionProvider.isConversionSupported(paramAudioFormat1, paramAudioFormat2))
        return true; 
    } 
    return false;
  }
  
  public static AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream) {
    if (paramAudioInputStream.getFormat().matches(paramAudioFormat))
      return paramAudioInputStream; 
    List list = getFormatConversionProviders();
    for (byte b = 0; b < list.size(); b++) {
      FormatConversionProvider formatConversionProvider = (FormatConversionProvider)list.get(b);
      if (formatConversionProvider.isConversionSupported(paramAudioFormat, paramAudioInputStream.getFormat()))
        return formatConversionProvider.getAudioInputStream(paramAudioFormat, paramAudioInputStream); 
    } 
    throw new IllegalArgumentException("Unsupported conversion: " + paramAudioFormat + " from " + paramAudioInputStream.getFormat());
  }
  
  public static AudioFileFormat getAudioFileFormat(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    List list = getAudioFileReaders();
    AudioFileFormat audioFileFormat = null;
    byte b = 0;
    while (b < list.size()) {
      AudioFileReader audioFileReader = (AudioFileReader)list.get(b);
      try {
        audioFileFormat = audioFileReader.getAudioFileFormat(paramInputStream);
        break;
      } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
        b++;
      } 
    } 
    if (audioFileFormat == null)
      throw new UnsupportedAudioFileException("file is not a supported file type"); 
    return audioFileFormat;
  }
  
  public static AudioFileFormat getAudioFileFormat(URL paramURL) throws UnsupportedAudioFileException, IOException {
    List list = getAudioFileReaders();
    AudioFileFormat audioFileFormat = null;
    byte b = 0;
    while (b < list.size()) {
      AudioFileReader audioFileReader = (AudioFileReader)list.get(b);
      try {
        audioFileFormat = audioFileReader.getAudioFileFormat(paramURL);
        break;
      } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
        b++;
      } 
    } 
    if (audioFileFormat == null)
      throw new UnsupportedAudioFileException("file is not a supported file type"); 
    return audioFileFormat;
  }
  
  public static AudioFileFormat getAudioFileFormat(File paramFile) throws UnsupportedAudioFileException, IOException {
    List list = getAudioFileReaders();
    AudioFileFormat audioFileFormat = null;
    byte b = 0;
    while (b < list.size()) {
      AudioFileReader audioFileReader = (AudioFileReader)list.get(b);
      try {
        audioFileFormat = audioFileReader.getAudioFileFormat(paramFile);
        break;
      } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
        b++;
      } 
    } 
    if (audioFileFormat == null)
      throw new UnsupportedAudioFileException("file is not a supported file type"); 
    return audioFileFormat;
  }
  
  public static AudioInputStream getAudioInputStream(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    List list = getAudioFileReaders();
    AudioInputStream audioInputStream = null;
    byte b = 0;
    while (b < list.size()) {
      AudioFileReader audioFileReader = (AudioFileReader)list.get(b);
      try {
        audioInputStream = audioFileReader.getAudioInputStream(paramInputStream);
        break;
      } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
        b++;
      } 
    } 
    if (audioInputStream == null)
      throw new UnsupportedAudioFileException("could not get audio input stream from input stream"); 
    return audioInputStream;
  }
  
  public static AudioInputStream getAudioInputStream(URL paramURL) throws UnsupportedAudioFileException, IOException {
    List list = getAudioFileReaders();
    AudioInputStream audioInputStream = null;
    byte b = 0;
    while (b < list.size()) {
      AudioFileReader audioFileReader = (AudioFileReader)list.get(b);
      try {
        audioInputStream = audioFileReader.getAudioInputStream(paramURL);
        break;
      } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
        b++;
      } 
    } 
    if (audioInputStream == null)
      throw new UnsupportedAudioFileException("could not get audio input stream from input URL"); 
    return audioInputStream;
  }
  
  public static AudioInputStream getAudioInputStream(File paramFile) throws UnsupportedAudioFileException, IOException {
    List list = getAudioFileReaders();
    AudioInputStream audioInputStream = null;
    byte b = 0;
    while (b < list.size()) {
      AudioFileReader audioFileReader = (AudioFileReader)list.get(b);
      try {
        audioInputStream = audioFileReader.getAudioInputStream(paramFile);
        break;
      } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
        b++;
      } 
    } 
    if (audioInputStream == null)
      throw new UnsupportedAudioFileException("could not get audio input stream from input file"); 
    return audioInputStream;
  }
  
  public static AudioFileFormat.Type[] getAudioFileTypes() {
    List list = getAudioFileWriters();
    HashSet hashSet = new HashSet();
    for (byte b = 0; b < list.size(); b++) {
      AudioFileWriter audioFileWriter = (AudioFileWriter)list.get(b);
      AudioFileFormat.Type[] arrayOfType = audioFileWriter.getAudioFileTypes();
      for (byte b1 = 0; b1 < arrayOfType.length; b1++)
        hashSet.add(arrayOfType[b1]); 
    } 
    return (Type[])hashSet.toArray(new AudioFileFormat.Type[0]);
  }
  
  public static boolean isFileTypeSupported(AudioFileFormat.Type paramType) {
    List list = getAudioFileWriters();
    for (byte b = 0; b < list.size(); b++) {
      AudioFileWriter audioFileWriter = (AudioFileWriter)list.get(b);
      if (audioFileWriter.isFileTypeSupported(paramType))
        return true; 
    } 
    return false;
  }
  
  public static AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream) {
    List list = getAudioFileWriters();
    HashSet hashSet = new HashSet();
    for (byte b = 0; b < list.size(); b++) {
      AudioFileWriter audioFileWriter = (AudioFileWriter)list.get(b);
      AudioFileFormat.Type[] arrayOfType = audioFileWriter.getAudioFileTypes(paramAudioInputStream);
      for (byte b1 = 0; b1 < arrayOfType.length; b1++)
        hashSet.add(arrayOfType[b1]); 
    } 
    return (Type[])hashSet.toArray(new AudioFileFormat.Type[0]);
  }
  
  public static boolean isFileTypeSupported(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream) {
    List list = getAudioFileWriters();
    for (byte b = 0; b < list.size(); b++) {
      AudioFileWriter audioFileWriter = (AudioFileWriter)list.get(b);
      if (audioFileWriter.isFileTypeSupported(paramType, paramAudioInputStream))
        return true; 
    } 
    return false;
  }
  
  public static int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream) throws IOException {
    List list = getAudioFileWriters();
    int i = 0;
    boolean bool = false;
    byte b = 0;
    while (b < list.size()) {
      AudioFileWriter audioFileWriter = (AudioFileWriter)list.get(b);
      try {
        i = audioFileWriter.write(paramAudioInputStream, paramType, paramOutputStream);
        bool = true;
        break;
      } catch (IllegalArgumentException illegalArgumentException) {
        b++;
      } 
    } 
    if (!bool)
      throw new IllegalArgumentException("could not write audio file: file type not supported: " + paramType); 
    return i;
  }
  
  public static int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile) throws IOException {
    List list = getAudioFileWriters();
    int i = 0;
    boolean bool = false;
    byte b = 0;
    while (b < list.size()) {
      AudioFileWriter audioFileWriter = (AudioFileWriter)list.get(b);
      try {
        i = audioFileWriter.write(paramAudioInputStream, paramType, paramFile);
        bool = true;
        break;
      } catch (IllegalArgumentException illegalArgumentException) {
        b++;
      } 
    } 
    if (!bool)
      throw new IllegalArgumentException("could not write audio file: file type not supported: " + paramType); 
    return i;
  }
  
  private static List getMixerProviders() { return getProviders(MixerProvider.class); }
  
  private static List getFormatConversionProviders() { return getProviders(FormatConversionProvider.class); }
  
  private static List getAudioFileReaders() { return getProviders(AudioFileReader.class); }
  
  private static List getAudioFileWriters() { return getProviders(AudioFileWriter.class); }
  
  private static Mixer getDefaultMixer(List paramList, Line.Info paramInfo) {
    Class clazz = paramInfo.getLineClass();
    String str1 = JDK13Services.getDefaultProviderClassName(clazz);
    String str2 = JDK13Services.getDefaultInstanceName(clazz);
    if (str1 != null) {
      MixerProvider mixerProvider = getNamedProvider(str1, paramList);
      if (mixerProvider != null)
        if (str2 != null) {
          Mixer mixer = getNamedMixer(str2, mixerProvider, paramInfo);
          if (mixer != null)
            return mixer; 
        } else {
          Mixer mixer = getFirstMixer(mixerProvider, paramInfo, false);
          if (mixer != null)
            return mixer; 
        }  
    } 
    if (str2 != null) {
      Mixer mixer = getNamedMixer(str2, paramList, paramInfo);
      if (mixer != null)
        return mixer; 
    } 
    return null;
  }
  
  private static MixerProvider getNamedProvider(String paramString, List paramList) {
    for (byte b = 0; b < paramList.size(); b++) {
      MixerProvider mixerProvider = (MixerProvider)paramList.get(b);
      if (mixerProvider.getClass().getName().equals(paramString))
        return mixerProvider; 
    } 
    return null;
  }
  
  private static Mixer getNamedMixer(String paramString, MixerProvider paramMixerProvider, Line.Info paramInfo) {
    Mixer.Info[] arrayOfInfo = paramMixerProvider.getMixerInfo();
    for (byte b = 0; b < arrayOfInfo.length; b++) {
      if (arrayOfInfo[b].getName().equals(paramString)) {
        Mixer mixer = paramMixerProvider.getMixer(arrayOfInfo[b]);
        if (isAppropriateMixer(mixer, paramInfo, false))
          return mixer; 
      } 
    } 
    return null;
  }
  
  private static Mixer getNamedMixer(String paramString, List paramList, Line.Info paramInfo) {
    for (byte b = 0; b < paramList.size(); b++) {
      MixerProvider mixerProvider = (MixerProvider)paramList.get(b);
      Mixer mixer = getNamedMixer(paramString, mixerProvider, paramInfo);
      if (mixer != null)
        return mixer; 
    } 
    return null;
  }
  
  private static Mixer getFirstMixer(MixerProvider paramMixerProvider, Line.Info paramInfo, boolean paramBoolean) {
    Mixer.Info[] arrayOfInfo = paramMixerProvider.getMixerInfo();
    for (byte b = 0; b < arrayOfInfo.length; b++) {
      Mixer mixer = paramMixerProvider.getMixer(arrayOfInfo[b]);
      if (isAppropriateMixer(mixer, paramInfo, paramBoolean))
        return mixer; 
    } 
    return null;
  }
  
  private static boolean isAppropriateMixer(Mixer paramMixer, Line.Info paramInfo, boolean paramBoolean) {
    if (!paramMixer.isLineSupported(paramInfo))
      return false; 
    Class clazz = paramInfo.getLineClass();
    if (paramBoolean && (SourceDataLine.class.isAssignableFrom(clazz) || Clip.class.isAssignableFrom(clazz))) {
      int i = paramMixer.getMaxLines(paramInfo);
      return (i == -1 || i > 1);
    } 
    return true;
  }
  
  private static List getMixerInfoList() {
    List list = getMixerProviders();
    return getMixerInfoList(list);
  }
  
  private static List getMixerInfoList(List paramList) {
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < paramList.size(); b++) {
      Info[] arrayOfInfo = (Info[])((MixerProvider)paramList.get(b)).getMixerInfo();
      for (byte b1 = 0; b1 < arrayOfInfo.length; b1++)
        arrayList.add(arrayOfInfo[b1]); 
    } 
    return arrayList;
  }
  
  private static List getProviders(Class paramClass) { return JDK13Services.getProviders(paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\AudioSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */