package java.util.stream;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;
import java.util.function.Supplier;

final class DistinctOps {
  static <T> ReferencePipeline<T, T> makeRef(AbstractPipeline<?, T, ?> paramAbstractPipeline) { return new ReferencePipeline.StatefulOp<T, T>(paramAbstractPipeline, StreamShape.REFERENCE, StreamOpFlag.IS_DISTINCT | StreamOpFlag.NOT_SIZED) {
        <P_IN> Node<T> reduce(PipelineHelper<T> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) {
          TerminalOp terminalOp = ReduceOps.makeRef(java.util.LinkedHashSet::new, HashSet::add, AbstractCollection::addAll);
          return Nodes.node((Collection)terminalOp.evaluateParallel(param1PipelineHelper, param1Spliterator));
        }
        
        <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<T[]> param1IntFunction) {
          if (StreamOpFlag.DISTINCT.isKnown(param1PipelineHelper.getStreamAndOpFlags()))
            return param1PipelineHelper.evaluate(param1Spliterator, false, param1IntFunction); 
          if (StreamOpFlag.ORDERED.isKnown(param1PipelineHelper.getStreamAndOpFlags()))
            return reduce(param1PipelineHelper, param1Spliterator); 
          AtomicBoolean atomicBoolean = new AtomicBoolean(false);
          ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
          TerminalOp terminalOp = ForEachOps.makeRef(param1Object -> {
                if (param1Object == null) {
                  param1AtomicBoolean.set(true);
                } else {
                  param1ConcurrentHashMap.putIfAbsent(param1Object, Boolean.TRUE);
                } 
              }false);
          terminalOp.evaluateParallel(param1PipelineHelper, param1Spliterator);
          HashSet hashSet = concurrentHashMap.keySet();
          if (atomicBoolean.get()) {
            hashSet = new HashSet(hashSet);
            hashSet.add(null);
          } 
          return Nodes.node(hashSet);
        }
        
        <P_IN> Spliterator<T> opEvaluateParallelLazy(PipelineHelper<T> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) { return StreamOpFlag.DISTINCT.isKnown(param1PipelineHelper.getStreamAndOpFlags()) ? param1PipelineHelper.wrapSpliterator(param1Spliterator) : (StreamOpFlag.ORDERED.isKnown(param1PipelineHelper.getStreamAndOpFlags()) ? reduce(param1PipelineHelper, param1Spliterator).spliterator() : new StreamSpliterators.DistinctSpliterator(param1PipelineHelper.wrapSpliterator(param1Spliterator))); }
        
        Sink<T> opWrapSink(int param1Int, Sink<T> param1Sink) {
          Objects.requireNonNull(param1Sink);
          return StreamOpFlag.DISTINCT.isKnown(param1Int) ? param1Sink : (StreamOpFlag.SORTED.isKnown(param1Int) ? new Sink.ChainedReference<T, T>(param1Sink) {
              boolean seenNull;
              
              T lastSeen;
              
              public void begin(long param2Long) {
                this.seenNull = false;
                this.lastSeen = null;
                this.downstream.begin(-1L);
              }
              
              public void end() {
                this.seenNull = false;
                this.lastSeen = null;
                this.downstream.end();
              }
              
              public void accept(T param2T) {
                if (param2T == null) {
                  if (!this.seenNull) {
                    this.seenNull = true;
                    this.downstream.accept(this.lastSeen = null);
                  } 
                } else if (this.lastSeen == null || !param2T.equals(this.lastSeen)) {
                  this.downstream.accept(this.lastSeen = param2T);
                } 
              }
            } : new Sink.ChainedReference<T, T>(param1Sink) {
              Set<T> seen;
              
              public void begin(long param2Long) {
                this.seen = new HashSet();
                this.downstream.begin(-1L);
              }
              
              public void end() {
                this.seen = null;
                this.downstream.end();
              }
              
              public void accept(T param2T) {
                if (!this.seen.contains(param2T)) {
                  this.seen.add(param2T);
                  this.downstream.accept(param2T);
                } 
              }
            });
        }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\DistinctOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */