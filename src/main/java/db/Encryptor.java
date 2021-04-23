package db;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor
{
  public static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength)
  {
    try
    {
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
      PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
      SecretKey keyTmp = keyFactory.generateSecret(keySpec);
      return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }

  public static SecretKeySpec createDefaultSecretKey()
  {
    try
    {
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
      PBEKeySpec keySpec = new PBEKeySpec("password".toCharArray(), "12345678".getBytes(), 4000, 128);
      SecretKey keyTmp = keyFactory.generateSecret(keySpec);
      return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }

  public static String encrypt(String property, SecretKeySpec key)
  {
    try
    {
      Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      pbeCipher.init(Cipher.ENCRYPT_MODE, key);
      AlgorithmParameters parameters = pbeCipher.getParameters();
      IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
      byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
      byte[] iv = ivParameterSpec.getIV();
      return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }
    catch (Exception e)
    {
      return null;
    }
  }

  public static String base64Encode(byte[] bytes)
  {
    return Base64.getEncoder().encodeToString(bytes);
  }

  public static String decrypt(String string, SecretKeySpec key)
  {
    try
    {
      String iv = string.split(":")[0];
      String property = string.split(":")[1];
      Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
      return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }

  private static byte[] base64Decode(String property)
  {
    return Base64.getDecoder().decode(property);
  }
}
