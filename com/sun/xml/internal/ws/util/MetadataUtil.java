package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MetadataUtil {
  public static Map<String, SDDocument> getMetadataClosure(@NotNull String paramString, @NotNull SDDocumentResolver paramSDDocumentResolver, boolean paramBoolean) {
    HashMap hashMap = new HashMap();
    HashSet hashSet = new HashSet();
    hashSet.add(paramString);
    while (!hashSet.isEmpty()) {
      Iterator iterator = hashSet.iterator();
      String str = (String)iterator.next();
      hashSet.remove(str);
      SDDocument sDDocument1 = paramSDDocumentResolver.resolve(str);
      SDDocument sDDocument2 = (SDDocument)hashMap.put(sDDocument1.getURL().toExternalForm(), sDDocument1);
      assert sDDocument2 == null;
      Set set = sDDocument1.getImports();
      if (!sDDocument1.isSchema() || !paramBoolean)
        for (String str1 : set) {
          if (hashMap.get(str1) == null)
            hashSet.add(str1); 
        }  
    } 
    return hashMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\MetadataUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */