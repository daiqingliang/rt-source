package javax.swing.filechooser;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public final class FileNameExtensionFilter extends FileFilter {
  private final String description;
  
  private final String[] extensions;
  
  private final String[] lowerCaseExtensions;
  
  public FileNameExtensionFilter(String paramString, String... paramVarArgs) {
    if (paramVarArgs == null || paramVarArgs.length == 0)
      throw new IllegalArgumentException("Extensions must be non-null and not empty"); 
    this.description = paramString;
    this.extensions = new String[paramVarArgs.length];
    this.lowerCaseExtensions = new String[paramVarArgs.length];
    for (byte b = 0; b < paramVarArgs.length; b++) {
      if (paramVarArgs[b] == null || paramVarArgs[b].length() == 0)
        throw new IllegalArgumentException("Each extension must be non-null and not empty"); 
      this.extensions[b] = paramVarArgs[b];
      this.lowerCaseExtensions[b] = paramVarArgs[b].toLowerCase(Locale.ENGLISH);
    } 
  }
  
  public boolean accept(File paramFile) {
    if (paramFile != null) {
      if (paramFile.isDirectory())
        return true; 
      String str = paramFile.getName();
      int i = str.lastIndexOf('.');
      if (i > 0 && i < str.length() - 1) {
        String str1 = str.substring(i + 1).toLowerCase(Locale.ENGLISH);
        for (String str2 : this.lowerCaseExtensions) {
          if (str1.equals(str2))
            return true; 
        } 
      } 
    } 
    return false;
  }
  
  public String getDescription() { return this.description; }
  
  public String[] getExtensions() {
    String[] arrayOfString = new String[this.extensions.length];
    System.arraycopy(this.extensions, 0, arrayOfString, 0, this.extensions.length);
    return arrayOfString;
  }
  
  public String toString() { return super.toString() + "[description=" + getDescription() + " extensions=" + Arrays.asList(getExtensions()) + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\filechooser\FileNameExtensionFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */