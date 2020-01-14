package cn.edu.buaa.crypto.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

public class Base64Util {
	
	public static String file2String(String fileAddress) {
		String result = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(fileAddress));
			byte[] data = new byte[fileInputStream.available()];
			fileInputStream.read(data);
			fileInputStream.close();
			result = BASE64Encode(data);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static void String2File(String string, String fileAddress) {
		byte[] data = BASE64Decode(string);
		try {
			OutputStream outputStream = new FileOutputStream(new File(fileAddress));
			outputStream.write(data);
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//将 s 进行 BASE64 编码 
    public static String BASE64Encode(byte[] s) { 
        if (s == null) return null; 
        return (new BASE64Encoder()).encode(s);
    } 

    //将 BASE64 编码的字符串 s 进行解码 
    public static byte[] BASE64Decode(String s) { 
        if (s == null) return null; 
        BASE64Decoder decoder = new BASE64Decoder(); 
        try { 
            byte[] b = decoder.decodeBuffer(s); 
            return b; 
        } catch (Exception e) { 
            return null; 
        } 
    }
}
