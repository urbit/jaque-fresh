package net.frodwith.jaque.nodes.jet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.data.Trel;
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
  @NodeChild(value="c", type=SlotExpressionNode.class)
})
public abstract class ByPutNode extends SubjectNode {
  @Specialization
  protected Object put(Object a, Object b, Object c) {
    try {
      return doPut(a, b, c);
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }

  // TODO: This could be made non-recursive now that we don't have to handle
  // the weird reference counting semantics of vere.
  private Object doPut(Object a, Object b, Object c) throws ExitException {
    if (Atom.isZero(a)) {
      return new Cell(new Cell(b, c), new Cell(0L, 0L));
    }

    Trel at = Trel.require(a);  // n, l, r -> p, q, r
    Cell n_a = Cell.require(at.p);
    if (Equality.equals(n_a.head, b)) {
      if (Equality.equals(n_a.tail, c)) {
        return a;
      } else {
        return new Cell(new Cell(b, c), new Cell(at.q, at.r));
      }
    }

    if (Atom.YES == HoonMath.gor(b, n_a.head)) {
      Object d = doPut(at.q, b, c);

      // head(head(d))
      Object h_h_d = Cell.require(Cell.require(d).head).head;
      if (Atom.YES == HoonMath.mor(n_a.head, h_h_d)) {
        return new Cell(n_a, new Cell(d, at.r));
      } else {
        Trel dt = Trel.require(d);
        return new Cell(dt.p, new Cell(dt.q, new Cell(n_a, new Cell(dt.r, at.r))));
      }
    } else {
      Object d = doPut(at.r, b, c);

      Object h_h_d = Cell.require(Cell.require(d).head).head;
      if (Atom.YES == HoonMath.mor(n_a.head, h_h_d)) {
        return new Cell(n_a, new Cell(at.q, d));
      } else {
        Trel dt = Trel.require(d);
        return new Cell(dt.p, new Cell(new Cell(n_a, new Cell(at.q, dt.q)), dt.r));
      }
    }
  }
}
