package com.example.groupchat;
import android.os.Environment;
import java.io.*;
import java.net.Socket;
import java.util.Date;

import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.CPABEMHOOAddress;

//客户端 ：文件上传下载接口
public class Client extends Socket{
	//private final String SERVER_IP="127.0.0.1";
	private final String SERVER_IP="10.112.159.87";
	private final int SERVER_PORT;
	private Socket client;
	private FileInputStream fis;
	private DataOutputStream dos;
	private DataInputStream dis;
	private FileOutputStream fos;
	BufferedWriter bw;



	//创建客户端，并指定接收的服务端IP和端口号
	public Client(int port) throws IOException{
		this.SERVER_PORT = port;
		this.client = new Socket(SERVER_IP,SERVER_PORT);
		System.out.println("成功连接服务端..."+ SERVER_IP + "端口号：" + SERVER_PORT);
	}

	//向服务端传输文件(上传Zip)
	public String uploadFile(String sendFileName) throws IOException {
		String sdFile = Environment.getExternalStorageDirectory()+ File.separator + "GroupChat" + File.separator + "ciperZip" + File.separator;
		File file = new File(sdFile + sendFileName);
		String url = "";
		try {
			fis = new FileInputStream(file);

			//接收server发送的tag(server中存储文件的路径)
			dos = new DataOutputStream(client.getOutputStream());//client.getOutputStream()返回此套接字的输出流
			// 开始传输文件
			System.out.println("======== 开始传输文件 ========");
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
				dos.write(bytes, 0, length);
				dos.flush();
			}
			System.out.println("======== 文件上传传输成功 ========");
			client.shutdownOutput();

			dis = new DataInputStream(client.getInputStream());
			byte[] fileUrl = new byte[10];
			dis.read(fileUrl, 0, 10);
			url = new String(fileUrl);
			System.out.println("接收到的文件的url:"+ url);
			return url;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("客户端文件传输异常");
		}finally {
			fis.close();
			dis.close();
			dos.close();
		}
		return url;
	}

	//向服务端传输文件(sender上传特征)
	public String uploadFeature(String sendFileName) throws IOException {
		String sdFile = Environment.getExternalStorageDirectory()+ File.separator + "GroupChat" + File.separator + "CKKS"+ File.separator;
		File file = new File(sdFile + sendFileName);
		String url = "";
		try {
			fis = new FileInputStream(file);
			dos = new DataOutputStream(client.getOutputStream());//client.getOutputStream()返回此套接字的输出流
			//文件名、大小等属性
			byte[] byteArrayFileName = sendFileName.getBytes();
			System.out.println("发送的文件名长度：" + byteArrayFileName.length);
			dos.write(byteArrayFileName, 0, byteArrayFileName.length);

			// 开始传输文件
			System.out.println("======== 开始传输文件 ========");
			byte[] bytes = new byte[1024];
			int length = 0;

			while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
				dos.write(bytes, 0, length);
				dos.flush();
			}

			System.out.println("======== 文件上传传输成功 ========");
			return url;

		}catch(IOException e){
			e.printStackTrace();
			System.out.println("客户端文件传输异常");
		}finally{
			fis.close();
			dos.close();
		}
		return url;
	}

	//向服务端传输文件(上传註冊用戶的加密特征)
	public String uploadRegisterFeature(String sendFileName) throws IOException {
		String sdFile = CPABEMHOOAddress.RegisterFaceAddress;
		File file = new File(sdFile + sendFileName);
		String url = "";
		try {
			fis = new FileInputStream(file);
			dos = new DataOutputStream(client.getOutputStream());//client.getOutputStream()返回此套接字的输出流
			//文件名、大小等属性
			byte[] byteArrayFileName = sendFileName.getBytes();
			System.out.println("发送的文件名长度：" + byteArrayFileName.length);
			dos.write(byteArrayFileName, 0, byteArrayFileName.length);


			// 开始传输文件
			System.out.println("======== 开始传输文件 ========");
			byte[] bytes = new byte[1024];
			int length = 0;

			while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
				dos.write(bytes, 0, length);
				dos.flush();
			}

			System.out.println("======== 文件上传传输成功 ========");
			return url;

		}catch(IOException e){
			e.printStackTrace();
			System.out.println("客户端文件传输异常");
		}finally{
			fis.close();
			dos.close();
		}
		return url;
	}

	//向服务端传输文件(上传cti.txt)
	public String uploadCTi(String dir, String sendFileName) throws IOException {
		String sdFile = Environment.getExternalStorageDirectory()+ File.separator + "GroupChat" + File.separator + "rcvZip"+ File.separator;
		File file = new File(sdFile + dir + File.separator+ sendFileName);
		String url = "";
		try {
			fis = new FileInputStream(file);
			dos = new DataOutputStream(client.getOutputStream());//client.getOutputStream()返回此套接字的输出流
			//文件名等属性
			byte[] byteArrayFileName = sendFileName.getBytes();
			//int filenameLen = byteArrayFileName.length;
			//dos.write(filenameLen);
			System.out.println("发送的文件名长度：" + byteArrayFileName.length);
			dos.write(byteArrayFileName, 0, byteArrayFileName.length);
			//dos.writeChar('\0');
			//dos.flush();

			// 开始传输文件
			System.out.println("======== 开始传输文件 ========");
			byte[] bytes = new byte[1024];
			int length = 0;

			while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
				dos.write(bytes, 0, length);
				dos.flush();
			}

			System.out.println("======== 文件上传传输成功 ========");
			return url;

		}catch(IOException e){
			e.printStackTrace();
			System.out.println("客户端文件传输异常");
		}finally{
			fis.close();
			dos.close();
		}
		return url;
	}

	//上传更新密钥MHOO_UK.txt
	public String uploadUK(String sendFileName) throws IOException {
		File file = new File(CPABEMHOOAddress.encryptedAddress+ sendFileName);
		String url = "";
		try {
			fis = new FileInputStream(file);
			dos = new DataOutputStream(client.getOutputStream());//client.getOutputStream()返回此套接字的输出流
			//文件名等属性
			byte[] byteArrayFileName = sendFileName.getBytes();
			//int filenameLen = byteArrayFileName.length;
			//dos.write(filenameLen);
			System.out.println("发送的文件名长度：" + byteArrayFileName.length);
			dos.write(byteArrayFileName, 0, byteArrayFileName.length);
			//dos.writeChar('\0');
			//dos.flush();

			// 开始传输文件
			System.out.println("======== 开始传输文件 ========");
			byte[] bytes = new byte[1024];
			int length = 0;

			while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
				dos.write(bytes, 0, length);
				dos.flush();
			}
			System.out.println("======== 文件上传传输成功 ========");

			client.shutdownOutput();

			dis = new DataInputStream(client.getInputStream());
			byte[] fileUrl = new byte[10];
			dis.read(fileUrl, 0, 10);
			url = new String(fileUrl);
			System.out.println("接收到的文件的url:"+ url);
			return url;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("客户端文件传输异常");
		}finally{
			fis.close();
			dis.close();
			dos.close();
		}
		return url;
	}

	//in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
	//out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));


	public String downloadFile(String recvFileName) throws IOException {
		String name = "";
		try {
			//输出流
			dos = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
			//发送请求下载的文件
			byte[] byteArrayFileName = recvFileName.getBytes();
			System.out.println("请求的文件名长度：" + byteArrayFileName.length);
			dos.write(byteArrayFileName, 0, byteArrayFileName.length);
			dos.writeChar('\0');
			dos.flush();

			byte[] inputByte = null;
			int length = 0;
			dis = new DataInputStream(client.getInputStream());
			//接收到文件后要写入的文件路径
			name = (new Date()).getTime()+"";
			String createtime = name +".zip";
			String sdFile = Environment.getExternalStorageDirectory()+ File.separator + "GroupChat" + File.separator + "rcvZip";
			File photoZipFile = new File(sdFile, createtime); // 在android的sdcard上创建文件对象

			fos = new FileOutputStream(photoZipFile);
			inputByte = new byte[1024];
			System.out.println("开始接收数据...");
			while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
				//System.out.println(length);
				fos.write(inputByte, 0, length);
				fos.flush();
			}
			System.out.println("完成接收");
			System.out.println("======== 文件下载成功 ========");
			return name;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("客户端文件传输异常");
		}finally{
			fos.close();
			dos.close();
			dis.close();
		}
		return name;
	}

//	public static void main(String[] args) {
//		try {
//			Client client1 = new Client(8120); // 启动客户端连接
//			client1.uploadFile("E:\\Program\\eclipse-workplace\\Client\\Files\\photo.zip"); // 上传文件
//
////			Client client2 = new Client(8121); // 启动客户端连接
////			client2.downloadFile("photo.zip"); //下载文件
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
 