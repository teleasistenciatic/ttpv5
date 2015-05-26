package com.local.android.teleasistenciaticplus.lib.cifrado;

import com.local.android.teleasistenciaticplus.lib.helper.AppLog;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Clase encargada de cifrar según el algoritmo AES de 128
 * https://www.youtube.com/watch?v=NHuibtoL_qk
 *
 */

public class Cifrado {

    static char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    //Initialization Vector (se pasa en cada mensaje, no es secreto)
    //gracias al vector de inicialización se reducen las posibilidades de hacer
    //ingeniería clásica de rotura de claves al evitar el XOR de resultados

    private String iv = "kxp5j29vn4d8e0sq"; //16 caracteres (16 x 8, 128bits)
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

    /////////////////////////////////////////////////
    private String SecretKey = "ef85jx92mc94ja9c";
    /////////////////////////////////////////////////

    /**
     * Contructor donde se declara el tipo de encriptación
     * debe de ser la misma que usará la aplicación en PHP (Rijndael)
     */
    public Cifrado() {
        ivspec = new IvParameterSpec(iv.getBytes());
        keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            AppLog.e("Cifrado","No hay tal algoritmo de cifrado",e);
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            AppLog.e("Cifrado","No hay tal padding",e);
        }
    }

    /**
     * Cifrado
     * @param text - texto para cifrar
     * @return - texto cifrado
     * @throws Exception
     */
    public String cifrar(String text) throws Exception {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        byte[] encrypted;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            throw new Exception("[cifrar] " + e.getMessage());
        }

        return bytesToHex(encrypted);
    }

    /**
     * Descifrado
     * @param code - cadena a encriptar
     * @return - cadena encriptada
     * @throws Exception
     */
    public String descifrar (String code) throws Exception {
        if (code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            decrypted = cipher.doFinal(hexToBytes(code));
            //Remove trailing zeroes
            if (decrypted.length > 0) {
                int trim = 0;
                for (int i = decrypted.length - 1; i >= 0; i--) if (decrypted[i] == 0) trim++;

                if (trim > 0) {
                    byte[] newArray = new byte[decrypted.length - trim];
                    System.arraycopy(decrypted, 0, newArray, 0, decrypted.length - trim);
                    decrypted = newArray;
                }
            }
        } catch (Exception e) {
            throw new Exception("[descifrar] " + e.getMessage());
        }
        return new String(decrypted);
    }

    /**
     * Convierte bytes a formato hexadecimal
     * @param buf array de bytes
     * @return una cadena
     */
    public String bytesToHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    /**
     * Convierte cadena hexadecimal a array binario
     * @param str cadena que se convertira en array binario
     * @return array de bytes
     */
    public byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }

    /**
     * Genera los rellenos para la matriz del algoritmo AES
     * @param source cadena
     * @return cadena procesada
     */
    private String padString(String source) {
        char paddingChar = 0;
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }

        return source;
    }
} // fin Cifrado