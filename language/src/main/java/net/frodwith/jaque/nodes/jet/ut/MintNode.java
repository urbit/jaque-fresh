package net.frodwith.jaque.nodes.jet.ut;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.ExitException;

@NodeChildren({
    @NodeChild(value="cor", type=SlotNode.class),
    @NodeChild(value="gol", type=SlotNode.class),
    @NodeChild(value="gen", type=SlotNode.class),
    @NodeChild(value="vrf", type=SlotNode.class),
    @NodeChild(value="sut", type=SlotNode.class),
})
public abstract class MintNode extends DecapitatedJetNode {
  private final static long C3__MINT = 0x746e_696dL;

  @Specialization
  protected Object mint(Object cor,
                        Object gol,
                        Object gen,
                        Object vrf,
                        Object sut) {
    try {
      long cacheId = 141 + C3__MINT;
      Cell cacheKey = new Cell(vrf, new Cell(sut, new Cell(gol, gen)));
      return lookupOrExecute(cacheId, cacheKey, cor);
    } catch (ExitException e) {
      throw new NockException("failure running real mint", this);
    }
  }
}