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
})
public abstract class InTapNode extends SubjectNode {
  @Specialization
  protected Object tap(Object a) {
    try {
      if (Atom.isZero(a)) {
        return 0L;
      }

      Stack<Object> nodeStack = new Stack<Object>();
      Deque<Object> outputs = new ArrayDeque<Object>();

      nodeStack.push(a);

      while (nodeStack.empty() == false) {
        Trel node = Trel.require(nodeStack.pop());
        outputs.push(node.p);
        if (!Atom.isZero(node.r))
          nodeStack.push(node.r);
        if (!Atom.isZero(node.q))
          nodeStack.push(node.q);
      }

      Object r = 0L;
      while ( !outputs.isEmpty() ) {
        r = new Cell(outputs.pop(), r);
      }
      return r;
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
