package net.frodwith.jaque.nodes.jet.crypto;

import java.io.StringWriter;
import java.io.IOException;
import java.io.Serializable;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.nodes.op.SlamOpNode;
import net.frodwith.jaque.printer.SimpleAtomPrinter;

import com.google.common.io.BaseEncoding;

import net.frodwith.jaque.runtime.Murmug;

@NodeChildren({
  @NodeChild(value="hashGate", type=SlotExpressionNode.class),
  @NodeChild(value="inBoq", type=SlotExpressionNode.class),
  @NodeChild(value="out", type=SlotExpressionNode.class),
  @NodeChild(value="keyLen", type=SlotExpressionNode.class),
  @NodeChild(value="keyData", type=SlotExpressionNode.class),
  @NodeChild(value="msgLen", type=SlotExpressionNode.class),
  @NodeChild(value="msgData", type=SlotExpressionNode.class)
})
public abstract class HmacNode extends SubjectNode {
  private @Child SlamOpNode slamNode;

  public HmacNode(AstContext astContext) {
    this.slamNode = new SlamOpNode(astContext, false);
  }

  @Specialization
  protected Object hmac(Object hashGate, Object inBoq, Object out, Object keyLen, Object keyData,
                        Object msgLen, Object msgData) {
    try {
      return doHmac(hashGate, inBoq, out, keyLen, keyData, msgLen, msgData);
    } catch (ExitException e) {
      throw new NockException(e.getMessage(), this);
    }
  }

  @TruffleBoundary
  public Object doHmac(Object hashGate,  // haj=$-([@u @] @)
                       Object boq,       // boq=@u
                       Object out,       // out=@u
                       Object keyLen,    // wik / wid.(key=byts)
                       Object keyData,   // key / dat.(key=byts)
                       Object msgLen,    // wid / wid.(msg=byts)
                       Object msgData)   // dat / dat.(msg=byts)
      throws ExitException {
    // ensure key and message fit signaled lengths
    Object key = HoonMath.end((byte)3, keyLen, keyData);
    Object dat = HoonMath.end((byte)3, msgLen, msgData);

    // keys longer than block size are shortened by hashing
    if (Atom.compare(keyLen, boq) == 1) {
      key = slamNode.executeSlam(hashGate, new Cell(keyLen, key));
      keyLen = out;
    }

    // keys shorter than block size are right-padded
    if (Atom.compare(keyLen, boq) == -1) {
      key = HoonMath.lsh((byte)3, Atom.requireInt(HoonMath.sub(boq, keyLen)), key);
    }

    // pad key, inner and outer.
    int boqInt = Atom.requireInt(boq);
    int trail = boqInt % 4;
    int padwords = (boqInt / 4) + (trail == 0 ? 0 : 1);
    byte[] innpad = new byte[padwords * 4];
    byte[] outpad = new byte[padwords * 4];
    Arrays.fill(innpad, (byte)0x36);
    Arrays.fill(outpad, (byte)0x5c);
    if (trail > 0) {
      innpad[padwords-1] = (byte)(0x36363636 >> (8 * (4 - trail)));
      outpad[padwords-1] = (byte)(0x5c5c5c5c >> (8 * (4 - trail)));
    }

    Object innkey = HoonMath.mix(key, Atom.fromByteArray(innpad));
    Object outkey = HoonMath.mix(key, Atom.fromByteArray(outpad));

    // append inner padding to message, then hash
    Object innmsg = HoonMath.add(HoonMath.lsh((byte)3, Atom.requireInt(msgLen), innkey), dat);
    Object innmsgLen = HoonMath.add(msgLen, boq);
    Object innhaj = slamNode.executeSlam(hashGate, new Cell(innmsgLen, innmsg));

    // prepend outer padding to result, hash again
    Object outmsg = HoonMath.add(HoonMath.lsh((byte)3, Atom.requireInt(out), outkey), innhaj);
    Object outmsgLen = HoonMath.add(out, boq);
    Object outhaj = slamNode.executeSlam(hashGate, new Cell(outmsgLen, outmsg));

    return outhaj;
  }
}
