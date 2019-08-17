package java.awt.datatransfer;

import java.util.Map;

public interface FlavorMap {
  Map<DataFlavor, String> getNativesForFlavors(DataFlavor[] paramArrayOfDataFlavor);
  
  Map<String, DataFlavor> getFlavorsForNatives(String[] paramArrayOfString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\datatransfer\FlavorMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */