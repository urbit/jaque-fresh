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
})
public abstract class InPutNode extends SubjectNode {
  @Specialization
  protected Object put(Object a, Object b) {
    try {
      return doPut(a, b);
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }

  // TODO: This could be made non-recursive now that we don't have to handle
  // the weird reference counting semantics of vere.
  private Object doPut(Object a, Object b) throws ExitException {
    if (Atom.isZero(a)) {
      return new Cell(b, new Cell(0L, 0L));
    }

    Trel at = Trel.require(a);  // n, l, r -> p, q, r
    if (Equality.equals(at.p, b)) {
      return a;
    }

    if (Atom.YES == HoonMath.gor(b, at.p)) {
      Object c = doPut(at.q, b);

      if (Atom.YES == HoonMath.mor(at.p, Cell.require(c).head)) {
        return new Cell(at.p, new Cell(c, at.r));
      } else {
        Trel ct = Trel.require(c);
        return new Cell(ct.p, new Cell(ct.q, new Cell(at.p, new Cell(ct.r, at.r))));
      }
    } else {
      Object c = doPut(at.r, b);

      if (Atom.YES == HoonMath.mor(at.p, Cell.require(c).head)) {
        return new Cell(at.p, new Cell(at.q, c));
      } else {
        Trel ct = Trel.require(c);
        return new Cell(ct.p, new Cell(new Cell(at.p, new Cell(at.q, ct.q)), ct.r));
      }
    }
  }
}
