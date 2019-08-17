package sun.print;

import java.util.ArrayList;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

class CustomMediaSizeName extends MediaSizeName {
  private static ArrayList customStringTable = new ArrayList();
  
  private static ArrayList customEnumTable = new ArrayList();
  
  private String choiceName;
  
  private MediaSizeName mediaName;
  
  private static final long serialVersionUID = 7412807582228043717L;
  
  private CustomMediaSizeName(int paramInt) { super(paramInt); }
  
  private static int nextValue(String paramString) {
    customStringTable.add(paramString);
    return customStringTable.size() - 1;
  }
  
  public CustomMediaSizeName(String paramString) {
    super(nextValue(paramString));
    customEnumTable.add(this);
    this.choiceName = null;
    this.mediaName = null;
  }
  
  public CustomMediaSizeName(String paramString1, String paramString2, float paramFloat1, float paramFloat2) {
    super(nextValue(paramString1));
    this.choiceName = paramString2;
    customEnumTable.add(this);
    this.mediaName = null;
    try {
      this.mediaName = MediaSize.findMedia(paramFloat1, paramFloat2, 25400);
    } catch (IllegalArgumentException illegalArgumentException) {}
    if (this.mediaName != null) {
      MediaSize mediaSize = MediaSize.getMediaSizeForName(this.mediaName);
      if (mediaSize == null) {
        this.mediaName = null;
      } else {
        float f1 = mediaSize.getX(25400);
        float f2 = mediaSize.getY(25400);
        float f3 = Math.abs(f1 - paramFloat1);
        float f4 = Math.abs(f2 - paramFloat2);
        if (f3 > 0.1D || f4 > 0.1D)
          this.mediaName = null; 
      } 
    } 
  }
  
  public String getChoiceName() { return this.choiceName; }
  
  public MediaSizeName getStandardMedia() { return this.mediaName; }
  
  public static MediaSizeName findMedia(Media[] paramArrayOfMedia, float paramFloat1, float paramFloat2, int paramInt) {
    if (paramFloat1 <= 0.0F || paramFloat2 <= 0.0F || paramInt < 1)
      throw new IllegalArgumentException("args must be +ve values"); 
    if (paramArrayOfMedia == null || paramArrayOfMedia.length == 0)
      throw new IllegalArgumentException("args must have valid array of media"); 
    byte b1 = 0;
    MediaSizeName[] arrayOfMediaSizeName = new MediaSizeName[paramArrayOfMedia.length];
    byte b2;
    for (b2 = 0; b2 < paramArrayOfMedia.length; b2++) {
      if (paramArrayOfMedia[b2] instanceof MediaSizeName)
        arrayOfMediaSizeName[b1++] = (MediaSizeName)paramArrayOfMedia[b2]; 
    } 
    if (b1 == 0)
      return null; 
    b2 = 0;
    double d = (paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2);
    float f1 = paramFloat1;
    float f2 = paramFloat2;
    for (byte b3 = 0; b3 < b1; b3++) {
      MediaSize mediaSize = MediaSize.getMediaSizeForName(arrayOfMediaSizeName[b3]);
      if (mediaSize != null) {
        float[] arrayOfFloat = mediaSize.getSize(paramInt);
        if (paramFloat1 == arrayOfFloat[0] && paramFloat2 == arrayOfFloat[1]) {
          b2 = b3;
          break;
        } 
        f1 = paramFloat1 - arrayOfFloat[0];
        f2 = paramFloat2 - arrayOfFloat[1];
        double d1 = (f1 * f1 + f2 * f2);
        if (d1 < d) {
          d = d1;
          b2 = b3;
        } 
      } 
    } 
    return arrayOfMediaSizeName[b2];
  }
  
  public Media[] getSuperEnumTable() { return (Media[])super.getEnumValueTable(); }
  
  protected String[] getStringTable() {
    String[] arrayOfString = new String[customStringTable.size()];
    return (String[])customStringTable.toArray(arrayOfString);
  }
  
  protected EnumSyntax[] getEnumValueTable() {
    MediaSizeName[] arrayOfMediaSizeName = new MediaSizeName[customEnumTable.size()];
    return (MediaSizeName[])customEnumTable.toArray(arrayOfMediaSizeName);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\CustomMediaSizeName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */