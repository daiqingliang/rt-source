package com.sun.media.sound;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public final class JDK13Services {
  private static final String PROPERTIES_FILENAME = "sound.properties";
  
  private static Properties properties;
  
  public static List<?> getProviders(Class<?> paramClass) {
    List list;
    if (!javax.sound.sampled.spi.MixerProvider.class.equals(paramClass) && !javax.sound.sampled.spi.FormatConversionProvider.class.equals(paramClass) && !javax.sound.sampled.spi.AudioFileReader.class.equals(paramClass) && !javax.sound.sampled.spi.AudioFileWriter.class.equals(paramClass) && !javax.sound.midi.spi.MidiDeviceProvider.class.equals(paramClass) && !javax.sound.midi.spi.SoundbankReader.class.equals(paramClass) && !javax.sound.midi.spi.MidiFileWriter.class.equals(paramClass) && !javax.sound.midi.spi.MidiFileReader.class.equals(paramClass)) {
      list = new ArrayList(0);
    } else {
      list = JSSecurityManager.getProviders(paramClass);
    } 
    return Collections.unmodifiableList(list);
  }
  
  public static String getDefaultProviderClassName(Class paramClass) {
    String str1 = null;
    String str2 = getDefaultProvider(paramClass);
    if (str2 != null) {
      int i = str2.indexOf('#');
      if (i != 0)
        if (i > 0) {
          str1 = str2.substring(0, i);
        } else {
          str1 = str2;
        }  
    } 
    return str1;
  }
  
  public static String getDefaultInstanceName(Class paramClass) {
    String str1 = null;
    String str2 = getDefaultProvider(paramClass);
    if (str2 != null) {
      int i = str2.indexOf('#');
      if (i >= 0 && i < str2.length() - 1)
        str1 = str2.substring(i + 1); 
    } 
    return str1;
  }
  
  private static String getDefaultProvider(Class paramClass) {
    if (!javax.sound.sampled.SourceDataLine.class.equals(paramClass) && !javax.sound.sampled.TargetDataLine.class.equals(paramClass) && !javax.sound.sampled.Clip.class.equals(paramClass) && !javax.sound.sampled.Port.class.equals(paramClass) && !javax.sound.midi.Receiver.class.equals(paramClass) && !javax.sound.midi.Transmitter.class.equals(paramClass) && !javax.sound.midi.Synthesizer.class.equals(paramClass) && !javax.sound.midi.Sequencer.class.equals(paramClass))
      return null; 
    String str1 = paramClass.getName();
    String str2 = (String)AccessController.doPrivileged(() -> System.getProperty(paramString));
    if (str2 == null)
      str2 = getProperties().getProperty(str1); 
    if ("".equals(str2))
      str2 = null; 
    return str2;
  }
  
  private static Properties getProperties() {
    if (properties == null) {
      properties = new Properties();
      JSSecurityManager.loadProperties(properties, "sound.properties");
    } 
    return properties;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\JDK13Services.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */