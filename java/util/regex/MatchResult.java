package java.util.regex;

public interface MatchResult {
  int start();
  
  int start(int paramInt);
  
  int end();
  
  int end(int paramInt);
  
  String group();
  
  String group(int paramInt);
  
  int groupCount();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\regex\MatchResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */