package sun.print;

import java.util.ArrayList;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaTray;

class CustomMediaTray extends MediaTray {
  private static ArrayList customStringTable = new ArrayList();
  
  private static ArrayList customEnumTable = new ArrayList();
  
  private String choiceName;
  
  private static final long serialVersionUID = 1019451298193987013L;
  
  private CustomMediaTray(int paramInt) { super(paramInt); }
  
  private static int nextValue(String paramString) {
    customStringTable.add(paramString);
    return customStringTable.size() - 1;
  }
  
  public CustomMediaTray(String paramString1, String paramString2) {
    super(nextValue(paramString1));
    this.choiceName = paramString2;
    customEnumTable.add(this);
  }
  
  public String getChoiceName() { return this.choiceName; }
  
  public Media[] getSuperEnumTable() { return (Media[])super.getEnumValueTable(); }
  
  protected String[] getStringTable() {
    String[] arrayOfString = new String[customStringTable.size()];
    return (String[])customStringTable.toArray(arrayOfString);
  }
  
  protected EnumSyntax[] getEnumValueTable() {
    MediaTray[] arrayOfMediaTray = new MediaTray[customEnumTable.size()];
    return (MediaTray[])customEnumTable.toArray(arrayOfMediaTray);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\CustomMediaTray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */