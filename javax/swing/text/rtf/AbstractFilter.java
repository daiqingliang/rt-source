package javax.swing.text.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

abstract class AbstractFilter extends OutputStream {
  protected char[] translationTable = latin1TranslationTable;
  
  protected boolean[] specialsTable = noSpecialsTable;
  
  static final char[] latin1TranslationTable;
  
  static final boolean[] noSpecialsTable = new boolean[256];
  
  static final boolean[] allSpecialsTable;
  
  public void readFromStream(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = new byte[16384];
    while (true) {
      int i = paramInputStream.read(arrayOfByte);
      if (i < 0)
        break; 
      write(arrayOfByte, 0, i);
    } 
  }
  
  public void readFromReader(Reader paramReader) throws IOException {
    char[] arrayOfChar = new char[2048];
    while (true) {
      int i = paramReader.read(arrayOfChar);
      if (i < 0)
        break; 
      for (byte b = 0; b < i; b++)
        write(arrayOfChar[b]); 
    } 
  }
  
  public void write(int paramInt) throws IOException {
    if (paramInt < 0)
      paramInt += 256; 
    if (this.specialsTable[paramInt]) {
      writeSpecial(paramInt);
    } else {
      char c = this.translationTable[paramInt];
      if (c != '\000')
        write(c); 
    } 
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    StringBuilder stringBuilder = null;
    while (paramInt2 > 0) {
      short s = (short)paramArrayOfByte[paramInt1];
      if (s < 0)
        s = (short)(s + 256); 
      if (this.specialsTable[s]) {
        if (stringBuilder != null) {
          write(stringBuilder.toString());
          stringBuilder = null;
        } 
        writeSpecial(s);
      } else {
        char c = this.translationTable[s];
        if (c != '\000') {
          if (stringBuilder == null)
            stringBuilder = new StringBuilder(); 
          stringBuilder.append(c);
        } 
      } 
      paramInt2--;
      paramInt1++;
    } 
    if (stringBuilder != null)
      write(stringBuilder.toString()); 
  }
  
  public void write(String paramString) throws IOException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++)
      write(paramString.charAt(b)); 
  }
  
  protected abstract void write(char paramChar) throws IOException;
  
  protected abstract void writeSpecial(int paramInt) throws IOException;
  
  static  {
    byte b;
    for (b = 0; b < 'Ā'; b++)
      noSpecialsTable[b] = false; 
    allSpecialsTable = new boolean[256];
    for (b = 0; b < 'Ā'; b++)
      allSpecialsTable[b] = true; 
    latin1TranslationTable = new char[256];
    for (b = 0; b < 'Ā'; b++)
      latin1TranslationTable[b] = (char)b; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\rtf\AbstractFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */