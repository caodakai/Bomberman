package concurrent;

import priv.cdk.bomberman.data.InputData;
import priv.ui.UserInterface;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Start extends JFrame {
    private static final int connNumber = 1;//模拟连接数

    public static volatile boolean stop = false;

    private static UserInterface userInterface;

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int post;
        try {
            Start start = new Start();
            int dataPort = 1;
            for(int i=0; i < connNumber; i++) {
                Socket so = new Socket(host, 2688);

                outString(so, "当前用户ip:" + InetAddress.getLocalHost().getHostAddress());

                String s = inString(so);//第一次连接，接收到可用端口

                so.close();

                System.out.println(s);

                String[] split = s.split("-");

                post = Integer.parseInt(split[0]);

                //连接一个可以交互的端口
                Socket socket = new Socket(host, post);

                dataPort = Integer.parseInt(split[1]);

                // '-' 后面的是名字
                outString(socket, InetAddress.getLocalHost().getHostAddress() + ":" + dataPort + "-cdk" /*+ InetAddress.getLocalHost().getHostAddress() + ":" + dataPort*/);

                System.out.println(inString(socket));

                //创建监听对象
                start.addSocket(socket);
            }
            //接收数据
            DatagramSocket datagramSocket = new DatagramSocket(dataPort);//监听本机1688端口,持续获取数据

            while (!stop) {
                byte[] buff = new byte[100000];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);

                datagramSocket.receive(packet);//接收

                ByteArrayInputStream byteArrayStram = new ByteArrayInputStream(buff);
                ObjectInputStream objectStream = new ObjectInputStream(byteArrayStram);

                userInterface.inputData = (InputData) objectStream.readObject();
                userInterface.repaint();

                objectStream.close();
                byteArrayStram.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Start(){
        userInterface = new UserInterface();

        this.add(userInterface);

        this.addKeyListener(userInterface);
        this.setSize(1800,1000);
        this.setLocation(100,50);
        this.setTitle("炸弹人");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void addSocket(Socket socket){
        userInterface.addSocket(socket);
    }

    public static String inString(Socket socket) throws UnsupportedEncodingException {
        byte[] bs=new byte[10240];
        int len=0;
        try {
            len=socket.getInputStream().read(bs);
        }catch(Exception e) {
            System.out.println("服务器关闭");
            stop = true;
        }

        return new String(bs, 0, len, "GBK");
    }

    public static void outString(Socket socket, String string){
        try {
            socket.getOutputStream().write(string.getBytes("GBK"));
        }catch (Exception e) {            //用户自动退出时发送异常
            System.out.println("服务器关闭");
            stop = true;
        }
    }
}
