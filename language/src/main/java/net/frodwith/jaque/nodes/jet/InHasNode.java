package net.frodwith.jaque.nodes.jet;

import java.util.ArrayDeque;
import java.util.Deque;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChildren({
  @NodeChild(value="a", type=SlotExpressionNode.class),
  @NodeChild(value="b", type=SlotExpressionNode.class),
})
public abstract class InHasNode extends SubjectNode {
  @Specialization
  protected Object has(Object a, Object b) {
    try {
      while (true) {
        if (Atom.isZero(a)) {
          return Atom.NO;
        }

        Cell aCell = Cell.require(a);
        if (Equality.equals(b, aCell.head)) {
          return Atom.YES;
        }

        Cell lrCell = Cell.require(aCell.tail);
        if (HoonMath.gor(b, aCell.head) == Atom.YES) {
          a = lrCell.head;
        } else {
          a = lrCell.tail;
        }
      }
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
