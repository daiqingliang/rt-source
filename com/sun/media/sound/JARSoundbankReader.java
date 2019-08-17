package com.sun.media.sound;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.spi.SoundbankReader;
import sun.reflect.misc.ReflectUtil;

public final class JARSoundbankReader extends SoundbankReader {
  private static boolean isZIP(URL paramURL) {
    boolean bool = false;
    try {
      inputStream = paramURL.openStream();
      try {
        byte[] arrayOfByte = new byte[4];
        bool = (inputStream.read(arrayOfByte) == 4);
        if (bool)
          bool = (arrayOfByte[0] == 80 && arrayOfByte[1] == 75 && arrayOfByte[2] == 3 && arrayOfByte[3] == 4); 
      } finally {
        inputStream.close();
      } 
    } catch (IOException iOException) {}
    return bool;
  }
  
  public Soundbank getSoundbank(URL paramURL) throws InvalidMidiDataException, IOException {
    if (!isZIP(paramURL))
      return null; 
    ArrayList arrayList = new ArrayList();
    URLClassLoader uRLClassLoader = URLClassLoader.newInstance(new URL[] { paramURL });
    inputStream = uRLClassLoader.getResourceAsStream("META-INF/services/javax.sound.midi.Soundbank");
    if (inputStream == null)
      return null; 
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
        if (!str.startsWith("#"))
          try {
            Class clazz = Class.forName(str.trim(), false, uRLClassLoader);
            if (Soundbank.class.isAssignableFrom(clazz)) {
              Object object = ReflectUtil.newInstance(clazz);
              arrayList.add((Soundbank)object);
            } 
          } catch (ClassNotFoundException classNotFoundException) {
          
          } catch (InstantiationException instantiationException) {
          
          } catch (IllegalAccessException illegalAccessException) {} 
      } 
    } finally {
      inputStream.close();
    } 
    if (arrayList.size() == 0)
      return null; 
    if (arrayList.size() == 1)
      return (Soundbank)arrayList.get(0); 
    SimpleSoundbank simpleSoundbank = new SimpleSoundbank();
    for (Soundbank soundbank : arrayList)
      simpleSoundbank.addAllInstruments(soundbank); 
    return simpleSoundbank;
  }
  
  public Soundbank getSoundbank(InputStream paramInputStream) throws InvalidMidiDataException, IOException { return null; }
  
  public Soundbank getSoundbank(File paramFile) throws InvalidMidiDataException, IOException { return getSoundbank(paramFile.toURI().toURL()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\JARSoundbankReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */