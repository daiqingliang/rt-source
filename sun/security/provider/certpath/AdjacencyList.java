package sun.security.provider.certpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AdjacencyList {
  private ArrayList<BuildStep> mStepList = new ArrayList();
  
  private List<List<Vertex>> mOrigList;
  
  public AdjacencyList(List<List<Vertex>> paramList) {
    this.mOrigList = paramList;
    buildList(paramList, 0, null);
  }
  
  public Iterator<BuildStep> iterator() { return Collections.unmodifiableList(this.mStepList).iterator(); }
  
  private boolean buildList(List<List<Vertex>> paramList, int paramInt, BuildStep paramBuildStep) {
    List list = (List)paramList.get(paramInt);
    boolean bool1 = true;
    boolean bool2 = true;
    for (Vertex vertex : list) {
      if (vertex.getIndex() != -1) {
        if (((List)paramList.get(vertex.getIndex())).size() != 0)
          bool1 = false; 
      } else if (vertex.getThrowable() == null) {
        bool2 = false;
      } 
      this.mStepList.add(new BuildStep(vertex, 1));
    } 
    if (bool1) {
      if (bool2) {
        if (paramBuildStep == null) {
          this.mStepList.add(new BuildStep(null, 4));
        } else {
          this.mStepList.add(new BuildStep(paramBuildStep.getVertex(), 2));
        } 
        return false;
      } 
      ArrayList arrayList = new ArrayList();
      for (Vertex vertex : list) {
        if (vertex.getThrowable() == null)
          arrayList.add(vertex); 
      } 
      if (arrayList.size() == 1) {
        this.mStepList.add(new BuildStep((Vertex)arrayList.get(0), 5));
      } else {
        this.mStepList.add(new BuildStep((Vertex)arrayList.get(0), 5));
      } 
      return true;
    } 
    boolean bool = false;
    for (Vertex vertex : list) {
      if (vertex.getIndex() != -1 && ((List)paramList.get(vertex.getIndex())).size() != 0) {
        BuildStep buildStep = new BuildStep(vertex, 3);
        this.mStepList.add(buildStep);
        bool = buildList(paramList, vertex.getIndex(), buildStep);
      } 
    } 
    if (bool)
      return true; 
    if (paramBuildStep == null) {
      this.mStepList.add(new BuildStep(null, 4));
    } else {
      this.mStepList.add(new BuildStep(paramBuildStep.getVertex(), 2));
    } 
    return false;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("[\n");
    byte b = 0;
    for (List list : this.mOrigList) {
      stringBuilder.append("LinkedList[").append(b++).append("]:\n");
      for (Vertex vertex : list)
        stringBuilder.append(vertex.toString()).append("\n"); 
    } 
    stringBuilder.append("]\n");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\AdjacencyList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */