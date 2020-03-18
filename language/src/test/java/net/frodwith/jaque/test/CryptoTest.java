package net.frodwith.jaque.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import javax.crypto.Cipher;

import net.frodwith.jaque.runtime.AtomAes;
import net.frodwith.jaque.exception.ExitException;

import static net.frodwith.jaque.parser.CustomParser.simple;
import static net.frodwith.jaque.test.Util.assertNounEquals;


public class CryptoTest {
  @Test
  public void testAesEcba() throws ExitException {
    Object key = simple("0");
    Object in = simple("0");
    Object out = simple("136.792.598.789.324.718.765.670.228.683.992.083.246");

    assertNounEquals(out, AtomAes.aes_ecb(Cipher.ENCRYPT_MODE, 16, key, in));
    assertNounEquals(in, AtomAes.aes_ecb(Cipher.DECRYPT_MODE, 16, key, out));
  }

  @Test
  public void testAesCbca() throws ExitException {
    Object key = simple("0");
    Object iv = simple("1");
    Object in = simple("2");
    Object out = simple("329.096.428.771.127.816.754.617.083.181.581.189.600");

    assertNounEquals(out, AtomAes.aes_cbc(Cipher.ENCRYPT_MODE, 16, key, iv, in));
    assertNounEquals(in, AtomAes.aes_cbc(Cipher.DECRYPT_MODE, 16, key, iv, out));
  }
}
