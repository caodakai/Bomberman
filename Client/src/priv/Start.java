package priv;

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
    public static boolean stop = false;

    private static UserInterface userInterface;

    public static void main(String[] args) {
        String host = "192.168.1.46";
        int post;
        try {
            Socket so = new Socket(host, 2688);

            outString(so, "当前用户ip:" + InetAddress.getLocalHost().getHostAddress());

            String s = inString(so);//第一次连接，接收到可用端口

            so.close();

            System.out.println(s);

            String[] split = s.split("-");

            post = Integer.parseInt(split[0]);

            //连接一个可以交互的端口
            Socket socket = new Socket(host, post);

            int dataPort = Integer.parseInt(split[1]);

            // '-' 后面的是名字
            outString(socket, InetAddress.getLocalHost().getHostAddress() + ":" + dataPort + "-" + InetAddress.getLocalHost().getHostAddress() + ":" + dataPort);

            System.out.println(inString(socket));

            //创建监听对象
            new Start(socket);

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

    private Start(Socket socket){
        userInterface = new UserInterface(socket);

        this.add(userInterface);

        this.addKeyListener(userInterface);
        this.setSize(1300,1000);
        this.setLocation(300,50);
        this.setTitle("炸弹人");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static String inString(Socket socket) throws UnsupportedEncodingException {
        byte[] bs=new byte[10240];
        int len=0;
        try {
            len=socket.getInputStream().read(bs);		//读取数据,异常则说明客户端突然关闭,则将本端口号设置成可连接状态
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
