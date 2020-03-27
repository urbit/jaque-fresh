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
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.runtime.Lists;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.nodes.op.SlamOpNode;

@NodeChildren({
  @NodeChild(value="a", type=SlotExpressionNode.class),
  @NodeChild(value="b", type=SlotExpressionNode.class),
})
public abstract class TurnNode extends SubjectNode {
  private @Child SlamOpNode slamNode;

  public TurnNode(AstContext astContext) {
    this.slamNode = new SlamOpNode(astContext, false);
  }

  @Specialization
  protected Object turn(Object list, Object gate) {
    Deque<Object> s = new ArrayDeque<Object>();
    for ( Object i : new List(list) ) {
      s.push(i);
    }
    Object r = 0L;
    while ( !s.isEmpty() ) {
      r = new Cell(this.slamNode.executeSlam(gate, s.pop()), r);
    }
    return r;
  }
}
