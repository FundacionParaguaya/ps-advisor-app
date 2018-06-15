package org.fundacionparaguaya.adviserplatform.util;


import android.util.Base64;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by afeltes on 11/1/15.
 */
public final class SecurityUtils {

    private static final String LOG_TAG = SecurityUtils.class.getName();


    /**
     * El tipo de algoritmo utilizado para las encriptaciones.
     */
    protected static final String ALGORITHM = "Blowfish";
    private static final String ENCODING = "UTF8";
    //Clave por default estática para los encritpados y desencriptados
    //Esto debería actualizarse periódicamente, junto con todos los parámetros que se guarden
    //encriptados con este algoritmo
    // 16 bytes
    private static byte[] defaultKey = new byte[]{02, 32, 12, 42, 64, 55, 26, 72, 82, 29, 21, 94, 77, 99,
            90, 10};
        /*
         * *********************************************************
         */

    private SecurityUtils() {

    }

    public static void main(String[] args) {
        Log.d(LOG_TAG, SecurityUtils.encrypt("ALGUN PLAIN STRING"));
    }


    public static String generateRandomPassword() {
        Random a = new Random();
        a.setSeed(System.currentTimeMillis());
        return String.format("%06d", a.nextInt(999999));
    }

    /**
     * Se encripta un string con una clave.
     *
     * @param message El string a encriptar.
     * @param key     La clave en bytes con la que se quiere encriptar.
     * @return
     */
    public static String encryptarConPassword(String message, byte[] key) {
        String ret = null;
        try {
            Cipher c = Cipher.getInstance(ALGORITHM);
            SecretKeySpec k = new SecretKeySpec(key, ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, k);
            byte[] encrypted = c.doFinal(message.getBytes(ENCODING));
            ret = byteToBase64(encrypted);
        } catch (Exception e) {
            Log.e(LOG_TAG, "No se pudo encriptar la cadena: " + e.getMessage());
        }

        return ret;
    }


    public static String desencryptarConPassword(String encrypted, byte[] key, boolean quiet) {
        String ret = null;
        if (StringUtils.isNotEmpty(encrypted)) {
            ret = desencriptarConKeyByte(encrypted, key, quiet);
        }
        return ret;
    }

    /**
     * Desencripta una cadena con un password.
     *
     * @param encrypted La cadena encriptada y codificada en Base64
     * @param key       La clave en bytes que se utilizará para encriptar.
     * @param quiet     Si se imprimirá o no errores de encriptado. Se puso este
     *                  parámetro, para tener compatibilidad hacia atrás de las
     *                  páginas que ya se tenía con encriptado, cuándo se implement
     *                  en Setiembre de 2011, la encriptación para todos los URLs de
     *                  la aplicación.
     * @return
     */
    private static String desencriptarConKeyByte(String encrypted, byte[] key, boolean quiet) {
        String ret = null;
        try {
                        /* El valor encriptado convertido a byte */
            byte[] rawEnc = base64ToByte(encrypted);
            Cipher c = Cipher.getInstance(SecurityUtils.ALGORITHM);
            SecretKeySpec k = new SecretKeySpec(key, SecurityUtils.ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, k);
            byte[] raw = c.doFinal(rawEnc);
            ret = new String(raw, ENCODING);
        } catch (Exception e) {
            if (!quiet) {
                Log.e(LOG_TAG, "No se pudo desencriptar la cadena: " + encrypted);
                Log.d(LOG_TAG, "\tclave: " + new String(key));
            }
        }
        return ret;
    }


    public static String desencryptarConPassword(String encrypted, byte[] key) {
        return desencryptarConPassword(encrypted, key, false);
    }

    /**
     * From a byte[] returns a base 64 representation
     *
     * @param data byte[]
     * @return String
     * @throws IOException
     */
    public static String byteToBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * Encripta una cadena con el defaultKey
     *
     * @param message
     * @return
     */
    public static String encrypt(String message) {
        return encryptarConPassword(message, SecurityUtils.defaultKey);
    }

    /**
     * Desencripta una cadena que se encriptó con el defaultKey
     *
     * @param encrypted
     * @return
     */
    public static String decrypt(String encrypted) {
        return desencryptarConPassword(encrypted, SecurityUtils.defaultKey);
    }

    /**
     * From a base 64 representation, returns the corresponding byte[]
     *
     * @param data String The base64 representation
     * @return byte[]
     * @throws IOException
     */
    public static byte[] base64ToByte(String data) throws IOException {
        return Base64.decode(data, Base64.DEFAULT);
    }

}
