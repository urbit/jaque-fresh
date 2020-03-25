package net.frodwith.jaque.runtime;

import java.util.Arrays;
import java.math.BigInteger;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.CompilerDirectives;

import gnu.math.MPN;

import org.bouncycastle.crypto.generators.UrbArgon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.ExitException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Various cryptographic operations on an Atom.
 */
public final class AtomCrypto {
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
                              .withMemoryAsKB(Atom.requireInt(memCost))
                              .withParallelism(Atom.requireInt(threads))
                              .withVersion(Atom.requireInt(version))
                              .build();

    UrbArgon2BytesGenerator generator = new UrbArgon2BytesGenerator();
    generator.init(params);

    byte[] outputBytes = new byte[Atom.requireInt(outputSize)];
    generator.generateBytes(msg, outputBytes);

    return Atom.fromByteArray(outputBytes, Atom.BIG_ENDIAN);
  }

  @TruffleBoundary
  public static Object aes_cbc(int mode, int keysize, Object key, Object iv, Object msg)
      throws ExitException {
    int len = HoonMath.met((byte)3, msg),
        out = (len % 16 == 0)
            ? len
            : len + 16 - (len % 16);
    byte[] ky = reverse(Atom.forceBytes(key, keysize)),
           iy = reverse(Atom.forceBytes(iv, 16)),
           my = reverse(Atom.forceBytes(msg, out));

    try {
      Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
      SecretKeySpec k = new SecretKeySpec(ky, "AES");
      c.init(mode, k, new IvParameterSpec(iy));

      return Atom.takeBytes(reverse(c.doFinal(my)), out);
    }
    catch (BadPaddingException e) {
      e.printStackTrace();
    }
    catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    catch (InvalidAlgorithmParameterException e) {
      e.printStackTrace();
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    catch (NoSuchPaddingException e) {
      e.printStackTrace();
    }
    throw new ExitException("aes_cbc failure");
  }

  @TruffleBoundary
  public static Object aes_ecb(int mode, int keysize, Object key, Object block)
      throws ExitException {
    byte[] ky = reverse(Atom.forceBytes(key, keysize)),
           by = reverse(Atom.forceBytes(block, 16));

    try {
      Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
      SecretKeySpec k = new SecretKeySpec(ky, "AES");
      c.init(mode, k);

      return Atom.takeBytes(reverse(c.doFinal(by)), 16);
    }
    catch (BadPaddingException e) {
      e.printStackTrace();
    }
    catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    catch (NoSuchPaddingException e) {
      e.printStackTrace();
    }
    throw new ExitException("aes_ecb failure");
  }

  /* IN-PLACE */
  private static byte[] reverse(byte[] a) {
    int i, j;
    byte b;
    for (i = 0, j = a.length - 1; j > i; ++i, --j) {
      b = a[i];
      a[i] = a[j];
      a[j] = b;
    }
    return a;
  }
}
