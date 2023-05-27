package com.maan.eway.auth.token;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Service;

@Service
public class EncryDecryService {

private static final String FORMAT = "ISO-8859-1";
	
	public static String encrypt(String unencryptedString) throws Exception {

        String encryptedString = null;
        CryptoConfig.getCipher().init(Cipher.ENCRYPT_MODE, CryptoConfig.getKey());
        byte[] plainText = unencryptedString.getBytes(FORMAT);
        byte[] encryptedText = CryptoConfig.getCipher().doFinal(plainText);
        encryptedString = DatatypeConverter.printBase64Binary(encryptedText);
        return encryptedString;
        
	
	}
	
	public static String decrypt(String encryptedString)  throws Exception {

        String decryptedText = null;
        CryptoConfig.getCipher().init(Cipher.DECRYPT_MODE, CryptoConfig.getKey());
        byte[] encryptedText = DatatypeConverter.parseBase64Binary(encryptedString.replace(" ", "+"));
        byte[] plainText = CryptoConfig.getCipher().doFinal(encryptedText);
        decryptedText = new String(plainText);

        return decryptedText;
    }
	
	public static void main(String []args ) throws Exception {
		EncryDecryService s =new EncryDecryService();
		String a="{\r\n"
				+ "\"LoginId\": \"Issuer11\",\r\n"
				+ "\"UserType\": \"Issuer\",\r\n"
				+ "\"SubUserType\": \"high\",\r\n"
				+ "\"CustomerRefNo\":\"Cust-00007\",\r\n"
				+ "\"PageType\": \"Home/existingQuotes/customerSelection/customerDetails/excess-discount\",\r\n"
				+ "\"BranchCode\":\"01\",\r\n"
				+ "\"QuoteNo\":null,\r\n"
				+ "\"PolicyNo\": null,\r\n"
				+ "\"RefNo\": \"DOM-03604\"\r\n"
				+ "}";
		System.out.println(s.encrypt(a)); 
		System.out.println(s.decrypt(s.encrypt(a)));
	}
}
