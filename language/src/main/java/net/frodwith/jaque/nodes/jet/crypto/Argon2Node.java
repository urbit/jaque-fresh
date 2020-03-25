package net.frodwith.jaque.nodes.jet.crypto;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.AtomArgon;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChildren({
  @NodeChild(value="outputSize", type=SlotExpressionNode.class),
  @NodeChild(value="type", type=SlotExpressionNode.class),
  @NodeChild(value="version", type=SlotExpressionNode.class),
  @NodeChild(value="threads", type=SlotExpressionNode.class),
  @NodeChild(value="memCost", type=SlotExpressionNode.class),
  @NodeChild(value="timeCost", type=SlotExpressionNode.class),
  @NodeChild(value="keyLen", type=SlotExpressionNode.class),
  @NodeChild(value="keyObj", type=SlotExpressionNode.class),
  @NodeChild(value="extraLen", type=SlotExpressionNode.class),
  @NodeChild(value="extraObj", type=SlotExpressionNode.class),
//
  @NodeChild(value="msgLen", type=SlotExpressionNode.class),
  @NodeChild(value="msgObj", type=SlotExpressionNode.class),
  @NodeChild(value="saltLen", type=SlotExpressionNode.class),
  @NodeChild(value="saltObj", type=SlotExpressionNode.class)
})
public abstract class Argon2Node extends SubjectNode {
  @Specialization
  protected Object argon2(Object outputSize,
                          Object type,
                          Object version,
                          Object threads,
                          Object memCost,
                          Object timeCost,
                          Object keyLen,
                          Object keyObj,
                          Object extraLen,
                          Object extraObj,
                          //
                          Object msgLen,
                          Object msgObj,
                          Object saltLen,
                          Object saltObj) {
    try {
      System.err.println("argon2");
      return AtomArgon.argon2(outputSize, type, version, threads, memCost, timeCost, keyLen,
                              keyObj, extraLen, extraObj, msgLen, msgObj, saltLen, saltObj);
    } catch (ExitException e) {
      e.printStackTrace();
      throw new NockException(e.getMessage(), this);
    }
  }
}
