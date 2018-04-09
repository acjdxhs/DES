import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEW;


public class DES {
	// 初始置换
	private int[] IP ;
	// 逆置换
	private int[] invIP;
	// 密码，字符串模式
	private String password;
	// 密钥
	private BitArray key;
	// 十六轮的子密钥
	private BitArray[] subKeys;
	// 置换选择1
	private int[] RS1;
	// 置换选择2
	private int[] RS2;
	// 循环左移表
	private int[] moveTable;
	// 扩充置换表
	private int[] expendTable;
	// 置换函数P
	private int[] P;
	// 代换选择S盒
	private int[][][] S;
	
	public DES() {
		// 默认密钥
		this("secret");
	}
	
	public DES(String key) {
		init();
		password = key;
		generateKey(key);
	}

	public void init() {
		IP = new int[] {58,50,42,34,26,18,10,2,
						60,52,44,36,28,20,12,4,
						62,54,46,38,30,22,14,6,
						64,56,48,40,32,24,16,8,
						57,49,41,33,25,17,9 ,1,
						59,51,43,35,27,19,11,3,
						61,53,45,37,29,21,13,5,
						63,55,47,39,31,23,15,7};
		
		invIP = calInvIP(IP);
		
		RS1 = new int[] {57,49,41,33,25,17,9 ,
						 1 ,58,50,42,34,26,18,
						 10,2 ,59,51,43,35,27,
						 19,11,3 ,60,52,44,36,
						 63,55,47,39,31,23,15,
						 7 ,62,54,46,38,30,22,
						 14,6 ,61,53,45,37,29,
						 21,13,5 ,28,20,12,4 };
		RS2 = new int[] {14,17,11,24,1 ,5 ,3 ,28,
						 15,6 ,21,10,23,19,12,4 ,
						 26,8 ,16,7 ,27,20,13,2 ,
						 41,52,31,37,47,55,30,40,
						 51,45,33,48,44,49,39,56,
						 34,53,46,42,50,36,29,32};
		
		moveTable = new int[] {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
		
		expendTable = new int[] {32,1 ,2 ,3 ,4 ,5 ,
								 4 ,5 ,6 ,7 ,8 ,9 ,
								 8 ,9 ,10,11,12,13,
								 12,13,14,15,16,17,
								 16,17,18,19,20,21,
								 20,21,22,23,24,25,
								 24,25,26,27,28,29,
								 28,29,30,31,32,1 };
		
		P = new int[] {16,7 ,20,21,29,12,28,17,
					   1 ,15,23,26,5 ,18,31,10,
					   2 ,8 ,24,14,32,27,3 ,9 ,
					   19,13,30,6 ,22,11,4 ,25};
		
		S = new int [][][] {new int[][] {
								new int[] {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
								new int[] {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
								new int[] {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
								new int[] {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}
							},
							new int[][] {
								new int[] {15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
								new int[] {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
								new int[] {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
								new int[] {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}
							},
							new int[][] {
								new int[] {10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
								new int[] {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
								new int[] {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
								new int[] {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}
							},
							new int[][] {
								new int[] {7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
								new int[] {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
								new int[] {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
								new int[] {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}
							},
							new int[][] {
								new int[] {2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
								new int[] {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
								new int[] {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
								new int[] {11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}
							},
							new int[][] {
								new int[] {12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
								new int[] {10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
								new int[] {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
								new int[] {4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}
							},
							new int[][] {
								new int[] {4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
								new int[] {13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
								new int[] {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
								new int[] {6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}
							},
							new int[][] {
								new int[] {13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
								new int[] {1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
								new int[] {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
								new int[] {2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}
							}};

	}
	
	// 根据String产生64位密码并产生计算十六轮所用到的子密钥
	void generateKey(String key) {
		byte[] bs = key.getBytes();
		byte[] tmp = new byte[8];
		for(int i=0; i<bs.length && i<8; i++) {
			tmp[i] = bs[i];
		}
		this.key = new BitArray(tmp);
		generateSubKeys();
	}
	
	void generateSubKeys() {
		subKeys = new BitArray[16];
		// 加密过程中的中间态密钥
		BitArray k = (BitArray) key.clone();
		// 64位密钥置换选择为56位
		k = replace(k, RS1);
		// 将密钥拆分为左右两部分
		List<BitArray> rK = k.split();
		BitArray lKey = rK.get(0);
		BitArray rKey = rK.get(1);
		for (int i=0; i<16; i++) {
			// 将左右key循环左移
			lKey.move(moveTable[i]);
			rKey.move(i);
			// 合并并进行置换选择2
			BitArray tmp = new BitArray(48);
			tmp.union(lKey, rKey);
			tmp = replace(tmp, RS2);
			subKeys[i] = tmp;
		}
	}
	
	// 根据初始置换计算逆置换
	int[] calInvIP(int[] IP) {
		int[] invIP = new int[IP.length];
		for(int i=0; i<IP.length; i++) {
			invIP[IP[i]-1] = i + 1;
		}
		return invIP;
	}
	
	public String encryp(String message) {
		try {
			return blockAndEncrypOrDecryp(message, 1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String decryp(String message) {
		try {
			return blockAndEncrypOrDecryp(message, 0);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 将输入文本切分为块并进行加密解密操作，message为文本，tag为1表示加密，否则表示解密
	public String blockAndEncrypOrDecryp (String message, int tag) throws UnsupportedEncodingException {
		// 如果是解密，需要解码，因为加密所得到的密文是由Base64编码而成
		byte[] msgBytes;
		if (tag == 1) {
			msgBytes = message.getBytes("UTF-8");
			//System.out.println("要加密的块：\n");
		} else {
			msgBytes = Base64.getDecoder().decode(message.getBytes());
			//System.out.println("要解密的块：\n");
		}
		//System.out.println(new BitArray(msgBytes).toString());
		
		byte[] tmp = new byte[8];
		// BitArray[] arrays = new BitArray[(int) Math.ceil(msgBytes.length/8.0)];
		byte[] result = new byte[(int) (Math.ceil(msgBytes.length/8.0)*8)];
		for(int i=0; i<msgBytes.length; i++) {
			tmp[i%8] = msgBytes[i];
			if((i+1)%8 == 0 || i == msgBytes.length-1) {
				BitArray b = new BitArray(tmp);
				b = encrypOrDecryp(b, tag);
				for(int j=0; j<b.getBytes().length;j++) {
					result[(i/8)*8 + j] = b.getBytes()[j];
				}
				tmp = new byte[8];
			}
		}
		if (tag == 1) {
			return Base64.getEncoder().encodeToString(result);
		} else {
			return new String(result);
		}
	}
	
	// 加密或者解密，message为64位明文，tag为1表示加密，否则表示解密
	public BitArray encrypOrDecryp (BitArray msg, int tag) {
		// 如果是加密操作，则顺序使用子密钥，否则逆序使用。决定是否是顺序此处采用Math.abs(i-num)进行
		int num;
		if (tag == 1) {
			num = 0;
		} else {
			num = 15;
		}
		// 明文的初始置换
		msg = replace(msg, IP);
		
		// 将要加密部分分为左右两个部分
		List<BitArray> rM = msg.split();
		BitArray lMsg = rM.get(0);
		BitArray rMsg = rM.get(1);
		// 进行16轮加密
		for (int i=0; i<16; i++) {
			BitArray tmp = (BitArray) rMsg.clone();
			rMsg = turn(lMsg, rMsg, subKeys[Math.abs(i-num)]);
			lMsg = tmp;
		}
		// 32位互换
		BitArray tmp = lMsg;
		lMsg = rMsg;
		rMsg = tmp;
		msg.union(lMsg, rMsg);
		msg = replace(msg, invIP);
		if (tag == 1) {
			//System.out.println("加密后的块：\n");
		} else{
			//System.out.println("解密后的块：\n");
		}
		//System.out.println(msg.toString());
		return msg;
	}
	
	// 置换，注意置换得到的结果跟置换规则有关，即可以改变原始信息长度
	BitArray replace(BitArray msg, int[] rule) {
		BitArray tmp = new BitArray(rule.length);
		
		for(int i=0; i<rule.length; i++) {
			boolean b = msg.get(rule[i] - 1);
			if(b) tmp.set(i);
		}
		return tmp;
	}
	
	// 第i轮循环左移
	void move(BitArray k, int i) {
		k.move(moveTable[i]);
	}
	
	// 代换/选择
	BitArray select(BitArray msg) {
		BitArray[] b = msg.splitForSelect();
		int[] result = new int[b.length];
		for(int i=0; i<b.length; i++) {
			BitArray tmp = b[i];
			int row = (tmp.get(0)?2:0) + (tmp.get(5)?1:0);
			int col = (tmp.get(1)?8:0) + (tmp.get(2)?4:0) + (tmp.get(3)?2:0) + (tmp.get(4)?1:0);
			result[i] = S[i][row][col];
		}
		BitArray r = new BitArray(b.length * 4);
		r.unionForSelect(result);
		return r;
	}
	
	// 每轮加密过程
	BitArray turn(BitArray lMsg, BitArray rMsg, BitArray k) {
		// 扩产/置换
		rMsg = replace(rMsg, expendTable);
		// 与密钥异或
		rMsg = rMsg.xor(k);
		// 代换/选择
		rMsg = select(rMsg);
		// 置换
		rMsg = replace(rMsg, P);
		// 左右部分异或
		rMsg = rMsg.xor(lMsg);
		return rMsg;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
		generateKey(password);
	}
}
