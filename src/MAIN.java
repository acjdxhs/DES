import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.BitSet;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class MAIN {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String password = "info sec";
		System.out.println("密码为：" + password);
		DES des = new DES(password);
		
		// 加密
		System.out.println("加密");
		String text = "hello world, this is my des algorithm";
		System.out.println("原文：" + text);
		String clipher = des.encryp(text);
		System.out.println("密文：" + clipher);
		
		// 解密
		String plain = des.decryp(clipher);
		System.out.println("解密");
		System.out.println("密码为" + des.getPassword() + "时：" + plain);
		des.setPassword("anfo sec");
		plain = des.decryp(clipher);
		System.out.println("密码为" + des.getPassword() + "时：" + plain);
		des.setPassword("infi sec");
		plain = des.decryp(clipher);
		System.out.println("密码为" + des.getPassword() + "时：" + plain);
		des.setPassword("info sec");
		plain = des.decryp(clipher);
		System.out.println("密码为" + des.getPassword() + "时：" + plain);
	}
	
}
