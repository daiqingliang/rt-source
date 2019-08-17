package sun.nio.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractUserDefinedFileAttributeView implements UserDefinedFileAttributeView, DynamicFileAttributeView {
  protected void checkAccess(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    assert paramBoolean1 || paramBoolean2;
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      if (paramBoolean1)
        securityManager.checkRead(paramString); 
      if (paramBoolean2)
        securityManager.checkWrite(paramString); 
      securityManager.checkPermission(new RuntimePermission("accessUserDefinedAttributes"));
    } 
  }
  
  public final String name() { return "user"; }
  
  public final void setAttribute(String paramString, Object paramObject) throws IOException {
    ByteBuffer byteBuffer;
    if (paramObject instanceof byte[]) {
      byteBuffer = ByteBuffer.wrap((byte[])paramObject);
    } else {
      byteBuffer = (ByteBuffer)paramObject;
    } 
    write(paramString, byteBuffer);
  }
  
  public final Map<String, Object> readAttributes(String[] paramArrayOfString) throws IOException {
    List list = new ArrayList();
    for (String str : paramArrayOfString) {
      if (str.equals("*")) {
        list = list();
        break;
      } 
      if (str.length() == 0)
        throw new IllegalArgumentException(); 
      list.add(str);
    } 
    HashMap hashMap = new HashMap();
    for (String str : list) {
      int i = size(str);
      byte[] arrayOfByte1 = new byte[i];
      int j = read(str, ByteBuffer.wrap(arrayOfByte1));
      byte[] arrayOfByte2 = (j == i) ? arrayOfByte1 : Arrays.copyOf(arrayOfByte1, j);
      hashMap.put(str, arrayOfByte2);
    } 
    return hashMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\AbstractUserDefinedFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */