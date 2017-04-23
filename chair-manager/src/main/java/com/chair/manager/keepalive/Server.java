package com.chair.manager.keepalive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class Server implements ApplicationListener<ApplicationEvent> {

	private int port;
	private volatile boolean running = false;
	private long receiveTimeDelay = Constant.RECEIVE_TIME_DELAY;
	private ConcurrentHashMap<Class, ServerObjectAction> actionMapping = new ConcurrentHashMap<Class, ServerObjectAction>();
	private ConcurrentHashMap<String, Socket> ipMapping = new ConcurrentHashMap<String, Socket>();
	private Thread connWatchDog;

	public Server() {
	}

	/**
	 * 构造函数
	 * 
	 * @param port
	 */
	public Server(int port) {
		this.port = port;
	}

	public void start() {
		if (running)
			return;
		running = true;
		connWatchDog = new Thread(new ConnWatchDog());
		connWatchDog.start();
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		if (running)
			running = false;
		if (connWatchDog != null)
			connWatchDog.stop();
	}

	//测试
	public static void main(String[] args) {
		int port = Constant.PORT;
		System.out.println("----服务器启动--端口---" + port);
		Server server = new Server(port);
		server.start();
		try {
			Thread.sleep(10*1000);
			server.new SocketAction().send("192.168.1.176","---服务端呼叫客户端，收到请回答---");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*------------------------------------------------------------------------------------------*/
	/**
	 * 添加接收对象的处理对象。
	 * 
	 * @param cls
	 *            待处理的对象，其所属的类。
	 * @param action
	 *            处理过程对象。
	 */
	public void addActionMap(Class<?> cls, ServerObjectAction action) {
		actionMapping.put(cls, action);
	}

	class ConnWatchDog implements Runnable {
		public void run() {
			try {
				ServerSocket ss = new ServerSocket(port, 5);
				while (running) {
					Socket s = ss.accept();
					new Thread(new SocketAction(s)).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
				Server.this.stop();
			}

		}
	}

	class SocketAction implements Runnable {
		Socket s;
		boolean run = true;
		long lastReceiveTime = System.currentTimeMillis();
		
		
		public SocketAction() {
		}
		public SocketAction(Socket s) {
			this.s = s;
		}

		public void run() {
			while (running && run) {
				if (System.currentTimeMillis() - lastReceiveTime > receiveTimeDelay) {
					overThis();
				} else {
					try {
						lastReceiveTime = System.currentTimeMillis();
						ipMapping.put(s.getInetAddress().toString().replace("/", ""), s);// 以k-v保存ip对应的socket对象
						receive(); // 接收消息
						response(); // 响应消息
					} catch (Exception e) {
						e.printStackTrace();
						overThis();
					}
				}
			}
		}
		
		/**
		 * 发送消息
		 * @param toClientIP 客户端IP
		 * @param toMessage	消息内容
		 * @throws IOException
		 */
		public void send(String toClientIP, String toMessage) throws IOException {
			Socket clientSocket = ipMapping.get(toClientIP);
			DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
			dos.writeUTF(toMessage);
			dos.flush();
		}

		/**
		 * 响应消息
		 */
		private void response() throws IOException {
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//			String sendMsg = "响应成功";
			String sendMsg = "*QB001";
			dos.writeUTF(sendMsg);
			dos.flush();
		}

		/**
		 * 接收消息
		 * 
		 * @throws IOException
		 */
		private void receive() throws IOException {
			String clientIP = s.getInetAddress().toString().replace("/", "");
			s.setKeepAlive(true);// 设置长连接
			DataInputStream dis = new DataInputStream(s.getInputStream());
			String reciverMsg = dis.readUTF();
			System.out.println("---接收客户端消息-" + clientIP + "---" + reciverMsg);
		}

		/**
		 * 客户端断开链接
		 */
		private void overThis() {
			if (run)
				run = false;
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("关闭：" + s.getRemoteSocketAddress());
		}

	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event.getSource() instanceof XmlWebApplicationContext) {
			if (((XmlWebApplicationContext) event.getSource()).getDisplayName().equals("Root WebApplicationContext")) {
				int port = Constant.PORT;
				System.err.println("----spring初始化Socket服务器启动，建立长连接--端口---" + port);
				Server server = new Server(port);
				server.start();
			}
		}

	}

}
