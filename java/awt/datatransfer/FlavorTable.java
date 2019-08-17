package java.awt.datatransfer;

import java.util.List;

public interface FlavorTable extends FlavorMap {
  List<String> getNativesForFlavor(DataFlavor paramDataFlavor);
  
  List<DataFlavor> getFlavorsForNative(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\datatransfer\FlavorTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */