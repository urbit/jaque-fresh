package net.frodwith.jaque.runtime;

import java.util.Arrays;
import java.math.BigInteger;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.CompilerDirectives;

import org.bouncycastle.crypto.generators.UrbArgon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.ExitException;

/**
 * Argon2 bindings on an Atom.
 */
public final class AtomArgon {
  @TruffleBoundary
  public static Object argon2(Object outputSize,
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
                              Object saltObj) throws ExitException {
    // flip endianness and render as byte arrays for argon2
    byte[] key = Atom.wordsToBytes(Atom.words(keyObj), Atom.requireInt(keyLen),
                                   Atom.BIG_ENDIAN);
    byte[] extra = Atom.wordsToBytes(Atom.words(extraObj), Atom.requireInt(extraLen),
                                     Atom.BIG_ENDIAN);
    byte[] msg = Atom.wordsToBytes(Atom.words(msgObj), Atom.requireInt(msgLen),
                                   Atom.BIG_ENDIAN);
    byte[] salt = Atom.wordsToBytes(Atom.words(saltObj), Atom.requireInt(saltLen),
                                    Atom.BIG_ENDIAN);

    int argonType = 0;
    int inType = Atom.requireInt(type);
    if (inType == 'd') {
      argonType = Argon2Parameters.ARGON2_d;
    } else if (inType == 'i') {
      argonType = Argon2Parameters.ARGON2_i;
    } else if (inType == 25_705) {  // id
      argonType = Argon2Parameters.ARGON2_id;
    } else if (inType == 'u') {
      argonType = UrbArgon2BytesGenerator.ARGON2_u;
    } else {
      throw new ExitException("unjetted argon2 variant");
    }

    Argon2Parameters params = new Argon2Parameters.Builder(argonType)
                              .withSalt(salt)
                              .withSecret(key)
                              .withAdditional(extra)
                              .withIterations(Atom.requireInt(timeCost))
                              .withMemoryAsKB(Atom.requireInt(memCost)) // or power of two?
                              .withParallelism(Atom.requireInt(threads))
                              .withVersion(Atom.requireInt(version))
                              .build();

    UrbArgon2BytesGenerator generator = new UrbArgon2BytesGenerator();
    generator.init(params);

    byte[] outputBytes = new byte[Atom.requireInt(outputSize)];
    generator.generateBytes(msg, outputBytes);

    return new Cell(3L, new Cell(outputSize, Atom.fromByteArray(outputBytes, Atom.BIG_ENDIAN)));
  }
}
