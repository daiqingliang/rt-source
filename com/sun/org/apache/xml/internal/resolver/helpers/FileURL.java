package com.sun.org.apache.xml.internal.resolver.helpers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class FileURL {
  public static URL makeURL(String paramString) throws MalformedURLException {
    File file = new File(paramString);
    return file.toURI().toURL();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\helpers\FileURL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */