package java.io;

import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;

public final class FilePermission extends Permission implements Serializable {
  private static final int EXECUTE = 1;
  
  private static final int WRITE = 2;
  
  private static final int READ = 4;
  
  private static final int DELETE = 8;
  
  private static final int READLINK = 16;
  
  private static final int ALL = 31;
  
  private static final int NONE = 0;
  
  private int mask;
  
  private boolean directory;
  
  private boolean recursive;
  
  private String actions;
  
  private String cpath;
  
  private static final char RECURSIVE_CHAR = '-';
  
  private static final char WILD_CHAR = '*';
  
  private static final long serialVersionUID = 7930732926638008763L;
  
  private void init(int paramInt) {
    if ((paramInt & 0x1F) != paramInt)
      throw new IllegalArgumentException("invalid actions mask"); 
    if (paramInt == 0)
      throw new IllegalArgumentException("invalid actions mask"); 
    if ((this.cpath = getName()) == null)
      throw new NullPointerException("name can't be null"); 
    this.mask = paramInt;
    if (this.cpath.equals("<<ALL FILES>>")) {
      this.directory = true;
      this.recursive = true;
      this.cpath = "";
      return;
    } 
    this.cpath = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            try {
              String str = FilePermission.this.cpath;
              if (FilePermission.this.cpath.endsWith("*")) {
                str = str.substring(0, str.length() - 1) + "-";
                str = (new File(str)).getCanonicalPath();
                return str.substring(0, str.length() - 1) + "*";
              } 
              return (new File(str)).getCanonicalPath();
            } catch (IOException iOException) {
              return FilePermission.this.cpath;
            } 
          }
        });
    int i = this.cpath.length();
    char c = (i > 0) ? this.cpath.charAt(i - 1) : 0;
    if (c == '-' && this.cpath.charAt(i - 2) == File.separatorChar) {
      this.directory = true;
      this.recursive = true;
      this.cpath = this.cpath.substring(0, --i);
    } else if (c == '*' && this.cpath.charAt(i - 2) == File.separatorChar) {
      this.directory = true;
      this.cpath = this.cpath.substring(0, --i);
    } 
  }
  
  public FilePermission(String paramString1, String paramString2) {
    super(paramString1);
    init(getMask(paramString2));
  }
  
  FilePermission(String paramString, int paramInt) {
    super(paramString);
    init(paramInt);
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof FilePermission))
      return false; 
    FilePermission filePermission = (FilePermission)paramPermission;
    return ((this.mask & filePermission.mask) == filePermission.mask && impliesIgnoreMask(filePermission));
  }
  
  boolean impliesIgnoreMask(FilePermission paramFilePermission) {
    if (this.directory) {
      if (this.recursive)
        return paramFilePermission.directory ? ((paramFilePermission.cpath.length() >= this.cpath.length() && paramFilePermission.cpath.startsWith(this.cpath))) : ((paramFilePermission.cpath.length() > this.cpath.length() && paramFilePermission.cpath.startsWith(this.cpath))); 
      if (paramFilePermission.directory)
        return paramFilePermission.recursive ? false : this.cpath.equals(paramFilePermission.cpath); 
      int i = paramFilePermission.cpath.lastIndexOf(File.separatorChar);
      return (i == -1) ? false : ((this.cpath.length() == i + 1 && this.cpath.regionMatches(0, paramFilePermission.cpath, 0, i + 1)));
    } 
    return paramFilePermission.directory ? false : this.cpath.equals(paramFilePermission.cpath);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof FilePermission))
      return false; 
    FilePermission filePermission = (FilePermission)paramObject;
    return (this.mask == filePermission.mask && this.cpath.equals(filePermission.cpath) && this.directory == filePermission.directory && this.recursive == filePermission.recursive);
  }
  
  public int hashCode() { return 0; }
  
  private static int getMask(String paramString) {
    byte b = 0;
    if (paramString == null)
      return b; 
    if (paramString == "read")
      return 4; 
    if (paramString == "write")
      return 2; 
    if (paramString == "execute")
      return 1; 
    if (paramString == "delete")
      return 8; 
    if (paramString == "readlink")
      return 16; 
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length - 1;
    if (i < 0)
      return b; 
    while (i != -1) {
      int j;
      char c;
      while (i != -1 && ((c = arrayOfChar[i]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t'))
        i--; 
      if (i >= 3 && (arrayOfChar[i - 3] == 'r' || arrayOfChar[i - 3] == 'R') && (arrayOfChar[i - 2] == 'e' || arrayOfChar[i - 2] == 'E') && (arrayOfChar[i - 1] == 'a' || arrayOfChar[i - 1] == 'A') && (arrayOfChar[i] == 'd' || arrayOfChar[i] == 'D')) {
        j = 4;
        b |= 0x4;
      } else if (i >= 4 && (arrayOfChar[i - 4] == 'w' || arrayOfChar[i - 4] == 'W') && (arrayOfChar[i - 3] == 'r' || arrayOfChar[i - 3] == 'R') && (arrayOfChar[i - 2] == 'i' || arrayOfChar[i - 2] == 'I') && (arrayOfChar[i - 1] == 't' || arrayOfChar[i - 1] == 'T') && (arrayOfChar[i] == 'e' || arrayOfChar[i] == 'E')) {
        j = 5;
        b |= 0x2;
      } else if (i >= 6 && (arrayOfChar[i - 6] == 'e' || arrayOfChar[i - 6] == 'E') && (arrayOfChar[i - 5] == 'x' || arrayOfChar[i - 5] == 'X') && (arrayOfChar[i - 4] == 'e' || arrayOfChar[i - 4] == 'E') && (arrayOfChar[i - 3] == 'c' || arrayOfChar[i - 3] == 'C') && (arrayOfChar[i - 2] == 'u' || arrayOfChar[i - 2] == 'U') && (arrayOfChar[i - 1] == 't' || arrayOfChar[i - 1] == 'T') && (arrayOfChar[i] == 'e' || arrayOfChar[i] == 'E')) {
        j = 7;
        b |= 0x1;
      } else if (i >= 5 && (arrayOfChar[i - 5] == 'd' || arrayOfChar[i - 5] == 'D') && (arrayOfChar[i - 4] == 'e' || arrayOfChar[i - 4] == 'E') && (arrayOfChar[i - 3] == 'l' || arrayOfChar[i - 3] == 'L') && (arrayOfChar[i - 2] == 'e' || arrayOfChar[i - 2] == 'E') && (arrayOfChar[i - 1] == 't' || arrayOfChar[i - 1] == 'T') && (arrayOfChar[i] == 'e' || arrayOfChar[i] == 'E')) {
        j = 6;
        b |= 0x8;
      } else if (i >= 7 && (arrayOfChar[i - 7] == 'r' || arrayOfChar[i - 7] == 'R') && (arrayOfChar[i - 6] == 'e' || arrayOfChar[i - 6] == 'E') && (arrayOfChar[i - 5] == 'a' || arrayOfChar[i - 5] == 'A') && (arrayOfChar[i - 4] == 'd' || arrayOfChar[i - 4] == 'D') && (arrayOfChar[i - 3] == 'l' || arrayOfChar[i - 3] == 'L') && (arrayOfChar[i - 2] == 'i' || arrayOfChar[i - 2] == 'I') && (arrayOfChar[i - 1] == 'n' || arrayOfChar[i - 1] == 'N') && (arrayOfChar[i] == 'k' || arrayOfChar[i] == 'K')) {
        j = 8;
        b |= 0x10;
      } else {
        throw new IllegalArgumentException("invalid permission: " + paramString);
      } 
      boolean bool = false;
      while (i >= j && !bool) {
        switch (arrayOfChar[i - j]) {
          case ',':
            bool = true;
            break;
          case '\t':
          case '\n':
          case '\f':
          case '\r':
          case ' ':
            break;
          default:
            throw new IllegalArgumentException("invalid permission: " + paramString);
        } 
        i--;
      } 
      i -= j;
    } 
    return b;
  }
  
  int getMask() { return this.mask; }
  
  private static String getActions(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    boolean bool = false;
    if ((paramInt & 0x4) == 4) {
      bool = true;
      stringBuilder.append("read");
    } 
    if ((paramInt & 0x2) == 2) {
      if (bool) {
        stringBuilder.append(',');
      } else {
        bool = true;
      } 
      stringBuilder.append("write");
    } 
    if ((paramInt & true) == 1) {
      if (bool) {
        stringBuilder.append(',');
      } else {
        bool = true;
      } 
      stringBuilder.append("execute");
    } 
    if ((paramInt & 0x8) == 8) {
      if (bool) {
        stringBuilder.append(',');
      } else {
        bool = true;
      } 
      stringBuilder.append("delete");
    } 
    if ((paramInt & 0x10) == 16) {
      if (bool) {
        stringBuilder.append(',');
      } else {
        bool = true;
      } 
      stringBuilder.append("readlink");
    } 
    return stringBuilder.toString();
  }
  
  public String getActions() {
    if (this.actions == null)
      this.actions = getActions(this.mask); 
    return this.actions;
  }
  
  public PermissionCollection newPermissionCollection() { return new FilePermissionCollection(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.actions == null)
      getActions(); 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    init(getMask(this.actions));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\FilePermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */