package sun.security.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManifestDigester {
  public static final String MF_MAIN_ATTRS = "Manifest-Main-Attributes";
  
  private byte[] rawBytes;
  
  private HashMap<String, Entry> entries;
  
  private boolean findSection(int paramInt, Position paramPosition) {
    int i = paramInt;
    int j = this.rawBytes.length;
    int k = paramInt;
    boolean bool = true;
    paramPosition.endOfFirstLine = -1;
    while (i < j) {
      byte b = this.rawBytes[i];
      switch (b) {
        case 13:
          if (paramPosition.endOfFirstLine == -1)
            paramPosition.endOfFirstLine = i - 1; 
          if (i < j && this.rawBytes[i + 1] == 10)
            i++; 
        case 10:
          if (paramPosition.endOfFirstLine == -1)
            paramPosition.endOfFirstLine = i - 1; 
          if (bool || i == j - 1) {
            if (i == j - 1) {
              paramPosition.endOfSection = i;
            } else {
              paramPosition.endOfSection = k;
            } 
            paramPosition.startOfNext = i + 1;
            return true;
          } 
          k = i;
          bool = true;
          break;
        default:
          bool = false;
          break;
      } 
      i++;
    } 
    return false;
  }
  
  public ManifestDigester(byte[] paramArrayOfByte) {
    this.rawBytes = paramArrayOfByte;
    this.entries = new HashMap();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Position position = new Position();
    if (!findSection(0, position))
      return; 
    this.entries.put("Manifest-Main-Attributes", (new Entry()).addSection(new Section(0, position.endOfSection + 1, position.startOfNext, this.rawBytes)));
    int i;
    for (i = position.startOfNext; findSection(i, position); i = position.startOfNext) {
      int j = position.endOfFirstLine - i + 1;
      int k = position.endOfSection - i + 1;
      int m = position.startOfNext - i;
      if (j > 6 && isNameAttr(paramArrayOfByte, i)) {
        StringBuilder stringBuilder = new StringBuilder(k);
        try {
          stringBuilder.append(new String(paramArrayOfByte, i + 6, j - 6, "UTF8"));
          int n = i + j;
          if (n - i < k)
            if (paramArrayOfByte[n] == 13) {
              n += 2;
            } else {
              n++;
            }  
          while (n - i < k && paramArrayOfByte[n++] == 32) {
            int i2;
            int i1 = n;
            while (n - i < k && paramArrayOfByte[n++] != 10);
            if (paramArrayOfByte[n - 1] != 10)
              return; 
            if (paramArrayOfByte[n - 2] == 13) {
              i2 = n - i1 - 2;
            } else {
              i2 = n - i1 - 1;
            } 
            stringBuilder.append(new String(paramArrayOfByte, i1, i2, "UTF8"));
          } 
          Entry entry = (Entry)this.entries.get(stringBuilder.toString());
          if (entry == null) {
            this.entries.put(stringBuilder.toString(), (new Entry()).addSection(new Section(i, k, m, this.rawBytes)));
          } else {
            entry.addSection(new Section(i, k, m, this.rawBytes));
          } 
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          throw new IllegalStateException("UTF8 not available on platform");
        } 
      } 
    } 
  }
  
  private boolean isNameAttr(byte[] paramArrayOfByte, int paramInt) { return ((paramArrayOfByte[paramInt] == 78 || paramArrayOfByte[paramInt] == 110) && (paramArrayOfByte[paramInt + 1] == 97 || paramArrayOfByte[paramInt + 1] == 65) && (paramArrayOfByte[paramInt + 2] == 109 || paramArrayOfByte[paramInt + 2] == 77) && (paramArrayOfByte[paramInt + 3] == 101 || paramArrayOfByte[paramInt + 3] == 69) && paramArrayOfByte[paramInt + 4] == 58 && paramArrayOfByte[paramInt + 5] == 32); }
  
  public Entry get(String paramString, boolean paramBoolean) {
    Entry entry = (Entry)this.entries.get(paramString);
    if (entry != null)
      entry.oldStyle = paramBoolean; 
    return entry;
  }
  
  public byte[] manifestDigest(MessageDigest paramMessageDigest) {
    paramMessageDigest.reset();
    paramMessageDigest.update(this.rawBytes, 0, this.rawBytes.length);
    return paramMessageDigest.digest();
  }
  
  public static class Entry {
    private List<ManifestDigester.Section> sections = new ArrayList();
    
    boolean oldStyle;
    
    private Entry addSection(ManifestDigester.Section param1Section) {
      this.sections.add(param1Section);
      return this;
    }
    
    public byte[] digest(MessageDigest param1MessageDigest) {
      param1MessageDigest.reset();
      for (ManifestDigester.Section section : this.sections) {
        if (this.oldStyle) {
          ManifestDigester.Section.doOldStyle(param1MessageDigest, section.rawBytes, section.offset, section.lengthWithBlankLine);
          continue;
        } 
        param1MessageDigest.update(section.rawBytes, section.offset, section.lengthWithBlankLine);
      } 
      return param1MessageDigest.digest();
    }
    
    public byte[] digestWorkaround(MessageDigest param1MessageDigest) {
      param1MessageDigest.reset();
      for (ManifestDigester.Section section : this.sections)
        param1MessageDigest.update(section.rawBytes, section.offset, section.length); 
      return param1MessageDigest.digest();
    }
  }
  
  static class Position {
    int endOfFirstLine;
    
    int endOfSection;
    
    int startOfNext;
  }
  
  private static class Section {
    int offset;
    
    int length;
    
    int lengthWithBlankLine;
    
    byte[] rawBytes;
    
    public Section(int param1Int1, int param1Int2, int param1Int3, byte[] param1ArrayOfByte) {
      this.offset = param1Int1;
      this.length = param1Int2;
      this.lengthWithBlankLine = param1Int3;
      this.rawBytes = param1ArrayOfByte;
    }
    
    private static void doOldStyle(MessageDigest param1MessageDigest, byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      int i = param1Int1;
      int j = param1Int1;
      int k = param1Int1 + param1Int2;
      byte b = -1;
      while (i < k) {
        if (param1ArrayOfByte[i] == 13 && b == 32) {
          param1MessageDigest.update(param1ArrayOfByte, j, i - j - 1);
          j = i;
        } 
        b = param1ArrayOfByte[i];
        i++;
      } 
      param1MessageDigest.update(param1ArrayOfByte, j, i - j);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\ManifestDigester.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */