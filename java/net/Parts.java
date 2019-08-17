package java.net;

class Parts {
  String path;
  
  String query;
  
  String ref;
  
  Parts(String paramString) {
    int i = paramString.indexOf('#');
    this.ref = (i < 0) ? null : paramString.substring(i + 1);
    paramString = (i < 0) ? paramString : paramString.substring(0, i);
    int j = paramString.lastIndexOf('?');
    if (j != -1) {
      this.query = paramString.substring(j + 1);
      this.path = paramString.substring(0, j);
    } else {
      this.path = paramString;
    } 
  }
  
  String getPath() { return this.path; }
  
  String getQuery() { return this.query; }
  
  String getRef() { return this.ref; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\Parts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */