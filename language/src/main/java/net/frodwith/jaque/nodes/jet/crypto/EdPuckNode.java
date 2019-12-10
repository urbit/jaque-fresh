package net.frodwith.jaque.nodes.jet.crypto;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

import net.frodwith.jaque.Ed25519;
import net.frodwith.jaque.Ed25519Exception;

@NodeChildren({
  @NodeChild(value="a", type=SlotNode.class)
})
public abstract class EdPuckNode extends SubjectNode {
  @Specialization
  protected Object puck(Object s) {
    try {
      byte[] seed = Atom.forceBytes(s, 32);
      byte[] publicKey = new byte[32];
      byte[] privateKey = new byte[64];
      Ed25519.ed25519_create_keypair(publicKey, privateKey, seed);
      return Atom.fromByteArray(publicKey, Atom.LITTLE_ENDIAN);
    } catch (Ed25519Exception e) {
      throw new NockException(e.getMessage(), this);
    } catch (ExitException e) {
      throw new NockException(e.getMessage(), this);
    }
  }
}