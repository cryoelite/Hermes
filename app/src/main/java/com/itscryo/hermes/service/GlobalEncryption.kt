package com.itscryo.hermes.service

import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class GlobalEncryption {

	private fun generateSecretKey(key: String): SecretKey {
		val keyByteArray = key.toByteArray()
		return SecretKeySpec(keyByteArray, 0, keyByteArray.size, "AES")
	}

	private fun getCipher(): Cipher {
		return Cipher.getInstance("AES/CBC/PKCS5PADDING");
	}

	 private fun encryptValue(value: String, key: String, iv: String): String {
		val plaintext: ByteArray = value.toByteArray()
		val cipher = getCipher()
		val ivParameterSpec = IvParameterSpec(iv.toByteArray())
		val secretKey= generateSecretKey(key)
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
		val ciphertext: ByteArray = cipher.doFinal(plaintext)
		return ciphertext.toString()
	}

	 private fun decryptValue(value: String, key: String, iv: String): String {
		val plaintext: ByteArray = value.toByteArray()
		val cipher = getCipher()
		val ivParameterSpec = IvParameterSpec(iv.toByteArray())
		val secretKey = generateSecretKey(key)
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
		val ciphertext: ByteArray = cipher.doFinal(plaintext)
		return ciphertext.toString()
	}

	fun encryptMessageID(userID: String, messageIDKey: String, messageIDIV: String): String {
		val time = GregorianCalendar().timeInMillis.toString()
		val messageIDText= "$userID time"
		return encryptValue(messageIDText, messageIDKey, messageIDIV)
	}

	fun decryptMessageID(messageID:String, messageIDKey: String, messageIDIV: String): String {
		return  decryptValue(messageID, messageIDKey, messageIDIV)
	}

	fun generateRandomString(): String {
		val rand= SecureRandom()
		val randVal= rand.nextLong()
		return randVal.toString()
	}
}