#include "net_frodwith_jaque_Ed25519.h"

#include <stdio.h>
#include <stdlib.h>

#include "ed25519/ed25519.h"
#include "ed25519/ge.h"

#include "ge-additions/ge-additions.h"

/**
 * Helper method to get data out of jbyteArray. Returns false on bad input.
 */
int byteArrayToCharArray(JNIEnv* env, jbyteArray src, int assumedLen,
                         unsigned char* dest) {
  int len = (*env)->GetArrayLength(env, src);
  if (len != assumedLen)
    return 0;

  (*env)->GetByteArrayRegion(env, src, 0, len, (jbyte*)dest);
  return 1;
}

int charArrayToByteArray(JNIEnv* env, unsigned char* src, int assumedLen,
                         jbyteArray dest) {
  int len = (*env)->GetArrayLength(env, dest);
  if (len != assumedLen)
    return 0;

  (*env)->SetByteArrayRegion(env, dest, 0, len, (jbyte*)src);
  return 1;
}

//int copyCharArrayTo

/**
 * Throws an exception back to Java.
 */
void throwException(JNIEnv* env, const char* message) {
  jclass exceptionClass;
  const char className[] = "net/frodwith/jaque/Ed25519Exception";

  exceptionClass = (*env)->FindClass(env, className);
  if (exceptionClass == NULL) {
    // We have to throw an exception somehow; try the default one.
    exceptionClass = (*env)->FindClass(env, "java/lang/Exception");

    if (exceptionClass == NULL) {
      fprintf(stderr,
              "WARNING: FAILED TO THROW EXCEPTION WHILE TRYING TO THROW "
              "EXCEPTION!\r\n");
      return;
    }
  }

  (*env)->ThrowNew(env, exceptionClass, message);
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_create_keypair
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL
Java_net_frodwith_jaque_Ed25519_ed25519_1create_1keypair(
    JNIEnv* env,
    jclass thiz,
    jbyteArray jPublicKey,
    jbyteArray jPrivateKey,
    jbyteArray jSeed)
{
  // Local C version of the java byte arrays
  unsigned char publicKey[32];
  unsigned char privateKey[64];
  unsigned char seed[32];

  if (!byteArrayToCharArray(env, jSeed, 32, seed)) {
    throwException(env, "Seed size not equal to 32");
    return;
  }

  ed25519_create_keypair(publicKey, privateKey, seed);

  if (!charArrayToByteArray(env, publicKey, 32, jPublicKey)) {
    throwException(env, "Public key output size not 32");
    return;
  }

  if (!charArrayToByteArray(env, privateKey, 64, jPrivateKey)) {
    throwException(env, "Private key output size not 64");
    return;
  }
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_key_exchange
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL
Java_net_frodwith_jaque_Ed25519_ed25519_1key_1exchange(
    JNIEnv* env,
    jclass thiz,
    jbyteArray jSharedSecret,
    jbyteArray jPublicKey,
    jbyteArray jPrivateKey)
{
  unsigned char publicKey[32];
  unsigned char privateKey[64];

  if (!byteArrayToCharArray(env, jPublicKey, 32, publicKey)) {
    throwException(env, "Public key size not equal to 32");
    return;
  }

  if (!byteArrayToCharArray(env, jPrivateKey, 64, privateKey)) {
    throwException(env, "Private key size not equal to 64");
    return;
  }

  unsigned char shared[32] = {0};
  ed25519_key_exchange(shared, publicKey, privateKey);

  if (!charArrayToByteArray(env, shared, 32, jSharedSecret)) {
    throwException(env, "Shared secret output size not 32");
    return;
  }
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_sign
 * Signature: ([B[BJ[B[B)V
 */
JNIEXPORT void JNICALL
Java_net_frodwith_jaque_Ed25519_ed25519_1sign(
    JNIEnv* env,
    jclass thiz,
    jbyteArray jSignature,
    jbyteArray jMessage,
    jbyteArray jPublicKey,
    jbyteArray jPrivateKey)
{
  unsigned char publicKey[32];
  unsigned char privateKey[64];

  if (!byteArrayToCharArray(env, jPublicKey, 32, publicKey)) {
    throwException(env, "Public key size not equal to 32");
    return;
  }

  if (!byteArrayToCharArray(env, jPrivateKey, 64, privateKey)) {
    throwException(env, "Private key size not equal to 64");
    return;
  }

  size_t messageLen = (*env)->GetArrayLength(env, jMessage);
  unsigned char* message = (unsigned char*)malloc(messageLen);
  (*env)->GetByteArrayRegion(env, jMessage, 0, messageLen, (jbyte*)message);

  unsigned char signature[64] = {0};
  ed25519_sign(signature, message, messageLen, publicKey, privateKey);

  if (!charArrayToByteArray(env, signature, 64, jSignature)) {
    throwException(env, "Signature output size not 64");
    return;
  }

  free(message);
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_verify
 * Signature: ([B[BJ[B)I
 */
JNIEXPORT jint JNICALL
Java_net_frodwith_jaque_Ed25519_ed25519_1verify(
    JNIEnv* env,
    jclass thiz,
    jbyteArray jSignature,
    jbyteArray jMessage,
    jbyteArray jPublicKey)
{
  unsigned char publicKey[32];
  unsigned char signature[64];

  if (!byteArrayToCharArray(env, jPublicKey, 32, publicKey)) {
    throwException(env, "Public key size not equal to 32");
    return 0;
  }

  if (!byteArrayToCharArray(env, jSignature, 64, signature)) {
    throwException(env, "Signature size not equal to 64");
    return 0;
  }

  size_t messageLen = (*env)->GetArrayLength(env, jMessage);
  unsigned char* message = (unsigned char*)malloc(messageLen);
  (*env)->GetByteArrayRegion(env, jMessage, 0, messageLen, (jbyte*)message);

  // verified is a boolean, not a nock loobean
  int verified = ed25519_verify(signature, message, messageLen, publicKey);
  free(message);

  return verified;
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    point_add
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_point_1add(
    JNIEnv* env,
    jclass thiz,
    jbyteArray jOutput,
    jbyteArray jAPoint,
    jbyteArray jBPoint)
{
  fprintf(stderr, "\r+point-add:ed:crypto\r\n");

  unsigned char a_y[32];
  unsigned char b_y[32];

  if (!byteArrayToCharArray(env, jAPoint, 32, a_y)) {
    throwException(env, "a-point size not equal to 32");
    return;
  }

  if (!byteArrayToCharArray(env, jBPoint, 32, b_y)) {
    throwException(env, "b-point size not equal to 32");
    return;
  }

  ////////// Copied verbatim from ed_point_add.c
  //
  ge_p3 A;
  if (ge_frombytes_negate_vartime(&A, a_y) != 0) {
    throwException(env, "A not a valid @udpoint in +point-add:ed:crypto");
    return;
  }

  ge_p3 B;
  if (ge_frombytes_negate_vartime(&B, b_y) != 0) {
    throwException(env, "B not a valid @udpoint in +point-add:ed:crypto");
    return;
  }

  // Undo the negation from above. See add_scalar.c in the ed25519 distro.
  fe_neg(A.X, A.X);
  fe_neg(A.T, A.T);
  fe_neg(B.X, B.X);
  fe_neg(B.T, B.T);

  ge_cached b_cached;
  ge_p3_to_cached(&b_cached, &B);

  ge_p1p1 sum;
  ge_add(&sum, &A, &b_cached);

  ge_p3 result;
  ge_p1p1_to_p3(&result, &sum);

  unsigned char output_y[32];
  ge_p3_tobytes(output_y, &result);
  //
  //////////

  if (!charArrayToByteArray(env, output_y, 32, jOutput)) {
    throwException(env, "Output size not 32");
    return;
  }
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    scalarmult
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_scalarmult(
    JNIEnv* env,
    jclass thiz,
    jbyteArray jOutput,
    jbyteArray jAScalar,
    jbyteArray jAPoint)
{
  fprintf(stderr, "\r+scalarmult:ed:crypto\r\n");

  unsigned char a_y[32];
  unsigned char b_y[32];

  if (!byteArrayToCharArray(env, jAScalar, 32, a_y)) {
    throwException(env, "a-scalar size not equal to 32");
    return;
  }

  if (!byteArrayToCharArray(env, jAPoint, 32, b_y)) {
    throwException(env, "a-point size not equal to 32");
    return;
  }

  ////////// Copied verbatim from ed_scalarmult.c
  //
  ge_p3 B;
  if (ge_frombytes_negate_vartime(&B, b_y) != 0) {
    throwException(env, "B not a valid @udpoint in +scalarmult:ed:crypto");
    return;
  }

  // Undo the negation from above. See add_scalar.c in the ed25519 distro.
  fe_neg(B.X, B.X);
  fe_neg(B.T, B.T);

  ge_p3 result;
  ge_scalarmult(&result, a_y, &B);

  unsigned char output_y[32];
  ge_p3_tobytes(output_y, &result);
  //
  //////////

  if (!charArrayToByteArray(env, output_y, 32, jOutput)) {
    throwException(env, "Output size not 32");
    return;
  }
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    scalarmult_base
 * Signature: ([B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_scalarmult_1base(
    JNIEnv* env,
    jclass thiz,
    jbyteArray jOutput,
    jbyteArray jScalar)
{
  fprintf(stderr, "\r+scalarmult-base:ed:crypto\r\n");

  unsigned char scalar_y[32];
  if (!byteArrayToCharArray(env, jScalar, 32, scalar_y)) {
    throwException(env, "a-scalar size not equal to 32");
    return;
  }

  ////////// Copied verbatim from ed_scalarmult.c
  //
  ge_p3 R;
  ge_scalarmult_base(&R, scalar_y);

  unsigned char output_y[32];
  ge_p3_tobytes(output_y, &R);
  //
  //////////

  if (!charArrayToByteArray(env, output_y, 32, jOutput)) {
    throwException(env, "Output size not 32");
    return;
  }
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    add_scalarmult_scalarmult_base
 * Signature: ([B[B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_add_1scalarmult_1scalarmult_1base(
    JNIEnv* env,
    jclass thiz,
    jbyteArray jOutput,
    jbyteArray jAScalar,
    jbyteArray jAPoint,
    jbyteArray jBScalar)
{
  fprintf(stderr, "\r+add-scalarmult-scalarmult-base:ed:crypto\r\n");

  unsigned char a_y[32];
  unsigned char a_point_y[32];
  unsigned char b_y[32];

  if (!byteArrayToCharArray(env, jAScalar, 32, a_y)) {
    throwException(env, "a-scalar size not equal to 32");
    return;
  }

  if (!byteArrayToCharArray(env, jAPoint, 32, a_point_y)) {
    throwException(env, "a-point size not equal to 32");
    return;
  }

  if (!byteArrayToCharArray(env, jBScalar, 32, b_y)) {
    throwException(env, "b-scalar size not equal to 32");
    return;
  }

  ////////// Copied verbatim from ed_add_scalarmult_scalarmult_base.c
  //
  ge_p3 A;
  if (ge_frombytes_negate_vartime(&A, a_point_y) != 0) {
    throwException(
        env,
        "A not a valid @udpoint in +add-scalarmult-scalarmult-base:ed:crypto");
  }

  // Undo the negation from above. See add_scalar.c in the ed25519 distro.
  fe_neg(A.X, A.X);
  fe_neg(A.T, A.T);

  ge_p2 r;
  ge_double_scalarmult_vartime(&r, a_y, &A, b_y);

  unsigned char output_y[32];
  ge_tobytes(output_y, &r);
  //
  //////////

  if (!charArrayToByteArray(env, output_y, 32, jOutput)) {
    throwException(env, "Output size not 32");
    return;
  }
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    add_double_scalarmult
 * Signature: ([B[B[B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_add_1double_1scalarmult(
    JNIEnv* env,
    jclass thiz,
    jbyteArray jOutput,
    jbyteArray jAScalar,
    jbyteArray jAPoint,
    jbyteArray jBScalar,
    jbyteArray jBPoint)
{
  fprintf(stderr, "\r+add-double-scalarmult:ed:crypto\r\n");

  unsigned char a_y[32];
  unsigned char a_point_y[32];
  unsigned char b_y[32];
  unsigned char b_point_y[32];

  if (!byteArrayToCharArray(env, jAScalar, 32, a_y)) {
    throwException(env, "a-scalar size not equal to 32");
    return;
  }

  if (!byteArrayToCharArray(env, jAPoint, 32, a_point_y)) {
    throwException(env, "a-point size not equal to 32");
    return;
  }

  if (!byteArrayToCharArray(env, jBScalar, 32, b_y)) {
    throwException(env, "b-scalar size not equal to 32");
    return;
  }

  if (!byteArrayToCharArray(env, jBPoint, 32, b_point_y)) {
    throwException(env, "b-point size not equal to 32");
    return;
  }

  ////////// Copied verbatim from ed_add_double_scalarmult.c
  //
  ge_p3 A;
  if (ge_frombytes_negate_vartime(&A, a_point_y) != 0) {
    throwException(
        env,
        "A not a valid @udpoint in +add-double-scalarmult:ed:crypto");
    return;
  }

  ge_p3 B;
  if (ge_frombytes_negate_vartime(&B, b_point_y) != 0) {
    throwException(
        env,
        "B not a valid @udpoint in +add-double-scalarmult:ed:crypto");
    return;
  }

  // Undo the negation from above. See add_scalar.c in the ed25519 distro.
  fe_neg(A.X, A.X);
  fe_neg(A.T, A.T);
  fe_neg(B.X, B.X);
  fe_neg(B.T, B.T);

  // Perform the multiplications of a*A and b*B
  ge_p3 a_result, b_result;
  ge_scalarmult(&a_result, a_y, &A);
  ge_scalarmult(&b_result, b_y, &B);

  // Sum those two points
  ge_cached b_result_cached;
  ge_p3_to_cached(&b_result_cached, &b_result);
  ge_p1p1 sum;
  ge_add(&sum, &a_result, &b_result_cached);

  ge_p3 final_result;
  ge_p1p1_to_p3(&final_result, &sum);

  unsigned char output_y[32];
  ge_p3_tobytes(output_y, &final_result);
  //
  //////////

  if (!charArrayToByteArray(env, output_y, 32, jOutput)) {
    throwException(env, "Output size not 32");
    return;
  }
}