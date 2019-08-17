package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractAclFileAttributeView implements AclFileAttributeView, DynamicFileAttributeView {
  private static final String OWNER_NAME = "owner";
  
  private static final String ACL_NAME = "acl";
  
  public final String name() { return "acl"; }
  
  public final void setAttribute(String paramString, Object paramObject) throws IOException {
    if (paramString.equals("owner")) {
      setOwner((UserPrincipal)paramObject);
      return;
    } 
    if (paramString.equals("acl")) {
      setAcl((List)paramObject);
      return;
    } 
    throw new IllegalArgumentException("'" + name() + ":" + paramString + "' not recognized");
  }
  
  public final Map<String, Object> readAttributes(String[] paramArrayOfString) throws IOException {
    boolean bool1 = false;
    boolean bool2 = false;
    for (String str : paramArrayOfString) {
      if (str.equals("*")) {
        bool2 = true;
        bool1 = true;
      } else if (str.equals("acl")) {
        bool1 = true;
      } else if (str.equals("owner")) {
        bool2 = true;
      } else {
        throw new IllegalArgumentException("'" + name() + ":" + str + "' not recognized");
      } 
    } 
    HashMap hashMap = new HashMap(2);
    if (bool1)
      hashMap.put("acl", getAcl()); 
    if (bool2)
      hashMap.put("owner", getOwner()); 
    return Collections.unmodifiableMap(hashMap);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\AbstractAclFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */