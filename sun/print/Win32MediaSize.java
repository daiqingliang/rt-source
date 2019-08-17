package sun.print;

import java.util.ArrayList;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

class Win32MediaSize extends MediaSizeName {
  private static ArrayList winStringTable = new ArrayList();
  
  private static ArrayList winEnumTable = new ArrayList();
  
  private static MediaSize[] predefMedia;
  
  private int dmPaperID;
  
  private Win32MediaSize(int paramInt) { super(paramInt); }
  
  private static int nextValue(String paramString) {
    winStringTable.add(paramString);
    return winStringTable.size() - 1;
  }
  
  public static Win32MediaSize findMediaName(String paramString) {
    int i = winStringTable.indexOf(paramString);
    return (i != -1) ? (Win32MediaSize)winEnumTable.get(i) : null;
  }
  
  public static MediaSize[] getPredefMedia() { return predefMedia; }
  
  public Win32MediaSize(String paramString, int paramInt) {
    super(nextValue(paramString));
    this.dmPaperID = paramInt;
    winEnumTable.add(this);
  }
  
  private MediaSizeName[] getSuperEnumTable() { return (MediaSizeName[])super.getEnumValueTable(); }
  
  int getDMPaper() { return this.dmPaperID; }
  
  protected String[] getStringTable() {
    String[] arrayOfString = new String[winStringTable.size()];
    return (String[])winStringTable.toArray(arrayOfString);
  }
  
  protected EnumSyntax[] getEnumValueTable() {
    MediaSizeName[] arrayOfMediaSizeName = new MediaSizeName[winEnumTable.size()];
    return (MediaSizeName[])winEnumTable.toArray(arrayOfMediaSizeName);
  }
  
  static  {
    Win32MediaSize win32MediaSize = new Win32MediaSize(-1);
    MediaSizeName[] arrayOfMediaSizeName = win32MediaSize.getSuperEnumTable();
    if (arrayOfMediaSizeName != null) {
      predefMedia = new MediaSize[arrayOfMediaSizeName.length];
      for (byte b = 0; b < arrayOfMediaSizeName.length; b++)
        predefMedia[b] = MediaSize.getMediaSizeForName(arrayOfMediaSizeName[b]); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\Win32MediaSize.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */