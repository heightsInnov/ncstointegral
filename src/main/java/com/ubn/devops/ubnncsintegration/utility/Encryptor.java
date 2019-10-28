/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubn.devops.ubnncsintegration.utility;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class Encryptor.
 *
 * @author lababatunde
 */
public class Encryptor {

	/** The Constant logger. */
	final static Logger logger = LoggerFactory.getLogger(Encryptor.class);

	/** The Constant KEY. */
	public static final String KEY = "TX234569";

	/** The cipher. */
	private BufferedBlockCipher cipher;

	/** The key. */
	private KeyParameter key;

	// Initialize the cryptographic engine.
	/**
	 * Instantiates a new encryptor.
	 *
	 * @param key the key
	 */
	// The key array should be at least 8 bytes long.
	public Encryptor(byte[] key) {
		cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESEngine()));
		this.key = new KeyParameter(key);
	}

	// Initialize the cryptographic engine.
	/**
	 * Instantiates a new encryptor.
	 *
	 * @param key the key
	 */
	// The string should be at least 8 chars long.
	public Encryptor(String key) {
		this(key.getBytes());
	}

	/**
	 * Call cipher.
	 *
	 * @param data the data
	 * @return the byte[]
	 * @throws CryptoException the crypto exception
	 */
	// Private routine that does the gritty work.
	private byte[] callCipher(byte[] data) throws CryptoException {
		int size = cipher.getOutputSize(data.length);
		byte[] result = new byte[size];
		int olen = cipher.processBytes(data, 0, data.length, result, 0);
		olen += cipher.doFinal(result, olen);

		if (olen < size) {
			byte[] tmp = new byte[olen];
			System.arraycopy(result, 0, tmp, 0, olen);
			result = tmp;
		}

		return result;
	}

	// Encrypt arbitrary byte array, returning the
	/**
	 * Encrypt.
	 *
	 * @param data the data
	 * @return the byte[]
	 * @throws CryptoException the crypto exception
	 */
	// encrypted data in a different byte array.
	public synchronized byte[] encrypt(byte[] data) throws CryptoException {
		if (data == null || data.length == 0) {
			return new byte[0];
		}

		cipher.init(true, key);
		return callCipher(data);
	}

	/**
	 * Encrypt string.
	 *
	 * @param data the data
	 * @return the byte[]
	 * @throws CryptoException the crypto exception
	 */
	// Encrypts a string.
	public byte[] encryptString(String data) throws CryptoException {
		if (data == null || data.length() == 0) {
			return new byte[0];
		}

		return encrypt(data.getBytes());
	}

	/**
	 * Encrypt string encoded.
	 *
	 * @param data the data
	 * @return the string
	 */
	public String encryptStringEncoded(String data) {
		String dataString = "";
		try {
			byte[] dataEncrypt = encryptString(data);
			dataString = new String(Base64.encode(dataEncrypt));

		} catch (CryptoException e) {
			logger.error("encryptStringEncoded==---->>>" + e);
		}
		return dataString;
	}

	/**
	 * Decrypt string encoded.
	 *
	 * @param data the data
	 * @return the string
	 */
	public String decryptStringEncoded(String data) {
		String decodedString = "";
		try {
			byte[] decodedData = Base64.decode(data);
			if (data == null || decodedData.length == 0) {
				return "";
			}
			decodedString = new String(decrypt(decodedData));
		} catch (CryptoException e) {
			logger.error("encryptStringEncoded==---->>>" + e);
		}
		return decodedString;
	}

	/**
	 * Decrypt.
	 *
	 * @param data the data
	 * @return the byte[]
	 * @throws CryptoException the crypto exception
	 */
	// Decrypts arbitrary data.
	public synchronized byte[] decrypt(byte[] data) throws CryptoException {
		if (data == null || data.length == 0) {
			return new byte[0];
		}

		cipher.init(false, key);
		return callCipher(data);
	}

	// Decrypts a string that was previously encoded
	/**
	 * Decrypt string.
	 *
	 * @param data the data
	 * @return the string
	 * @throws CryptoException the crypto exception
	 */
	// using encryptString.
	public String decryptString(byte[] data) throws CryptoException {
		if (data == null || data.length == 0) {
			return "";
		}

		return new String(decrypt(data));
	}

	public static void main(String[] args) { 
		
		
		  String data ="N9mke1aP3nsqPrg8Oje5PFbxY6/No0YIdHDLovXwXrWDWoW8WUfVxZ41zE2jezmsbKTTA3swBA7mYfXktmaHYZV8O5erB6pLUVBrBAlvVRBwdDNvhNysd2XMh/ehU/N4nvdH21ElH1ZsLuujnDMcIGfv1Ca1C4SNsOrisd4znS1Yb7Rvhb1S8to48POg5oXKKgElsHGzEml4lCo3J1o2vCZKhMApaJ8oWHXQ50hXg8qMAPYi39ErotV/wsrxpmeVzfILlLml2TbLbix21GSVAixpT6bL634oQvftEqcoCPsmzFzVnGY5+dT+dTBbxTT70xrhOJJcv9EOOm6hI7EQCiIRkc/0QFiAPkxPiiBK8Z0TqTXgb29rT1TH2HaNIkMvqdiU+K5eXGMQq9TjUHeLuGraUngp/P2p9fuxfHb5a7IcUuGlDaVKcp/vUHj10c9euf8oZsfMhwHx1gvW+G1CkZF196S8YEEaUTuDd9tf1tBnc/AzbfSL6vwJ4nmpr1t7eZ5oD5DqGoI5VTB4ZkNJqlSKLRaae76T0YcesDYRsf4GmwHInhmKhbaUl6Q8SbFgWb8Vk5Mva0nUTbgMaRbhRFcsZY9TGhAU";
		  Encryptor encryptor = new Encryptor(KEY); // String encrptedData =
		  //encryptor.encryptStringEncoded(data);
		  //ystem.out.println("Encrypted: "+encrptedData); String decryptedData =
		 // encryptor.decryptStringEncoded(data);
		  System.out.println(encryptor.decryptStringEncoded(data));
		 
	  }
	 
}
