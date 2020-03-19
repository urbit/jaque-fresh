package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChildren({
  @NodeChild(value="a", type=SlotExpressionNode.class),
  @NodeChild(value="b", type=SlotExpressionNode.class)
})
public abstract class ReapNode extends SubjectNode {
  @TruffleBoundary
  private Object doReap(Object numTimes, Object toReplicate)  throws ExitException {
    Object count = 0L;
    Object ret = 0L;

    while (Atom.compare(numTimes, count) != 0) {
      ret = new Cell(toReplicate, ret);
      count = HoonMath.increment(count);
    }

    return ret;
  }

  @Specialization
  protected Object reap(Object a, Object b) {
    try {
      return doReap(needAtom(a), b);
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
