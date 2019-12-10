/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_frodwith_jaque_Ed25519 */

#ifndef _Included_net_frodwith_jaque_Ed25519
#define _Included_net_frodwith_jaque_Ed25519
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_create_keypair
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_ed25519_1create_1keypair
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_key_exchange
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_ed25519_1key_1exchange
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_sign
 * Signature: ([B[B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_ed25519_1sign
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_verify
 * Signature: ([B[B[B)I
 */
JNIEXPORT jint JNICALL Java_net_frodwith_jaque_Ed25519_ed25519_1verify
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    point_add
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_point_1add
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    scalarmult
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_scalarmult
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    scalarmult_base
 * Signature: ([B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_scalarmult_1base
  (JNIEnv *, jclass, jbyteArray, jbyteArray);

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    add_scalarmult_scalarmult_base
 * Signature: ([B[B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_add_1scalarmult_1scalarmult_1base
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    add_double_scalarmult
 * Signature: ([B[B[B[B[B)V
 */
JNIEXPORT void JNICALL Java_net_frodwith_jaque_Ed25519_add_1double_1scalarmult
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray, jbyteArray, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif