package java.nio.file.attribute;

import java.io.IOException;

public abstract class UserPrincipalLookupService {
  public abstract UserPrincipal lookupPrincipalByName(String paramString) throws IOException;
  
  public abstract GroupPrincipal lookupPrincipalByGroupName(String paramString) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\UserPrincipalLookupService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */