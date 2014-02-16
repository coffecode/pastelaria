package codecoffe.restaurantes.licenca;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;

/**
 * @author JavaDigest
 * 
 */
public class EncryptionUtil {

  /**
   * String to hold name of the encryption algorithm.
   */
  public static final String ALGORITHM = "RSA";

  /**
   * String to hold the name of the private key file.
   */
  public static final String PRIVATE_KEY_FILE = "C:/keys/private.key";

  /**
   * String to hold name of the public key file.
   */
  public static final String PUBLIC_KEY_FILE = "C:/keys/public.key";

  /**
   * Generate key which contains a pair of private and public key using 1024
   * bytes. Store the set of keys in Prvate.key and Public.key files.
   * 
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static void generateKey() {
    try {
      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
      keyGen.initialize(1024);
      final KeyPair key = keyGen.generateKeyPair();

      File privateKeyFile = new File(PRIVATE_KEY_FILE);
      File publicKeyFile = new File(PUBLIC_KEY_FILE);

      // Create files to store public and private key
      if (privateKeyFile.getParentFile() != null) {
        privateKeyFile.getParentFile().mkdirs();
      }
      privateKeyFile.createNewFile();

      if (publicKeyFile.getParentFile() != null) {
        publicKeyFile.getParentFile().mkdirs();
      }
      publicKeyFile.createNewFile();

      // Saving the Public key in a file
      ObjectOutputStream publicKeyOS = new ObjectOutputStream(
          new FileOutputStream(publicKeyFile));
      publicKeyOS.writeObject(key.getPublic());
      publicKeyOS.close();

      // Saving the Private key in a file
      ObjectOutputStream privateKeyOS = new ObjectOutputStream(
          new FileOutputStream(privateKeyFile));
      privateKeyOS.writeObject(key.getPrivate());
      privateKeyOS.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * The method checks if the pair of public and private key has been generated.
   * 
   * @return flag indicating if the pair of keys were generated.
   */
  public static boolean areKeysPresent() {

    File privateKey = new File(PRIVATE_KEY_FILE);
    File publicKey = new File(PUBLIC_KEY_FILE);

    if (privateKey.exists() && publicKey.exists()) {
      return true;
    }
    return false;
  }

  /**
   * Encrypt the plain text using public key.
   * 
   * @param text
   *          : original plain text
   * @param privateKey
   *          :The public key
   * @return Encrypted text
   * @throws java.lang.Exception
   */
  public static byte[] encrypt(String text, PrivateKey privateKey) {
    byte[] cipherText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // encrypt the plain text using the public key
      cipher.init(Cipher.ENCRYPT_MODE, privateKey);
      cipherText = cipher.doFinal(text.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cipherText;
  }

  /**
   * Decrypt text using private key.
   * 
   * @param text
   *          :encrypted text
   * @param publicKey
   *          :The private key
   * @return plain text
   * @throws java.lang.Exception
   */
  public static String decrypt(byte[] text, PublicKey publicKey) {
    byte[] dectyptedText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);

      // decrypt the text using the private key
      cipher.init(Cipher.DECRYPT_MODE, publicKey);
      dectyptedText = cipher.doFinal(text);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return new String(dectyptedText);
  }

  /**
   * Test the EncryptionUtil
   */
  public static void main(String[] args) {

    try {

     /* // Check if the pair of keys are present else generate those.
      if (!areKeysPresent()) {
        // Method generates a pair of keys using the RSA algorithm and stores it
        // in their respective files
        generateKey();
      }*/
    	
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();
        
        @SuppressWarnings("unused")
		PrivateKey privTeste = key.getPrivate();
        @SuppressWarnings("unused")
		PublicKey pubTeste = key.getPublic();
      
      final String chaveParticular = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJJA/"
      		+ "KhqqcydeNxeKdnMmvh7y+Aw+NglfTBByFj+Ba8+BTQarWY+9IlBSoBXQk5s94b/"
      		+ "QnBtA4YcG8IC8RAP+alo1MmCPxoOJBap6xk0OZYa99rj+SgdwDejqnXwL9KTjZnZ"
      		+ "zjYdzDtgUwfla0Nxo2uuTIYzG+W7AsY58YL37CJnAgMBAAECgYAybP2wA7avucum"
      		+ "D0FWutjju6s7jSb/P02+ia/OHydOmI+qu1f13NbrS/un7G4MJD3j6ba6lJuWbbKU"
      		+ "XfdHlnp5i33PHVCZ1XeYnolSto7jIyHmTKISuRj193j/1ElUt/TZ0s5hjnmZvBLV"
      		+ "rbyl0oOuPZcSTFyqYdoY7D/pDMJZAQJBAOJJuKHa9tXmzQ16N9ZutLHf9DTlS+Bz"
      		+ "GCOjaEIXuv/P35L6APkU57jwz2MBitHkRqTA8U48cuDU1MLH1n/QTGECQQCldQ6q"
      		+ "d1yov0fRzaGyPyWLqLhuXA8M0Jm3mU0/DW5YlWuEr3Nme1LdTiuh+RyPJLF2bc8i"
      		+ "PqzrzXnBM9sAv6PHAkAnr9JH4MVFwpLX+EVuwD/EqoiD9msWVFk5duFEJjFPyD9n"
      		+ "4ZzmEnqJfwKAVLbqFOUm5AfuNr/XGryt5KqEBB5BAkA7CzHHnT7ArTnY4dOxAx3H"
      		+ "39ao80sfnJUuUpOhS460J+YtfFrnGF0ywkGJ1Jbfcg2uIHOJWeplX3byfinJ85lj"
      		+ "AkA9q/RxhT4xFPRsUVEoWBgre8OWEzHjllCbU30CKI+bQJHv20JXBobLD3euTE5T"
      		+ "cpg2nQZI1nDQx4LTjHblnYk6";
      
      BASE64Decoder decoder = new BASE64Decoder();
      byte[] sigBytes = decoder.decodeBuffer(chaveParticular);
      PrivateKey privKey2 = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(sigBytes));   
      
      final String chavePublica = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSQPyoaqnM"
      		+ "nXjcXinZzJr4e8vgMPjYJX0wQchY/gWvPgU0Gq1mPvSJQU"
      		+ "qAV0JObPeG/0JwbQOGHBvCAvEQD/mpaNTJgj8aDiQWqesZN"
      		+ "DmWGvfa4/koHcA3o6p18C/Sk42Z2c42Hcw7Y"
      		+ "FMH5WtDcaNrrkyGMxvluwLGOfGC9+wiZwIDAQAB";
      
      BASE64Decoder decoder2 = new BASE64Decoder();
      byte[] sigBytes2 = decoder2.decodeBuffer(chavePublica);
      PublicKey pubKey2 = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(sigBytes2));

      String originalText = "aisdjaosdjajsdoijasodjosajdoiajodasijdoasjdjaodjiasoidjoasjdoijasodjasdji";      
      byte[] cipherText2 = encrypt(originalText, privKey2);         
      String plainText = decrypt(cipherText2, pubKey2);
      
      /*byte[] publicKeyBytes = privateKey.getEncoded();

      //Convert Public key to String
      BASE64Encoder encoder = new BASE64Encoder();
      String pubKeyStr = encoder.encode(publicKeyBytes);  
      System.out.println("Key Private: " + pubKeyStr);*/

      // Printing the Original, Encrypted and Decrypted Text
      System.out.println("Original Text: " + originalText);
      System.out.println("Encrypted Text: " +cipherText2.toString());
      System.out.println("Decrypted Text: " + plainText);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
