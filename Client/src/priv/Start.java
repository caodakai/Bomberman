package priv;

import priv.cdk.bomberman.data.InputData;
import priv.cdk.bomberman.data.UDPData;
import priv.ui.UserInterface;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * cmd 命令运行 java -jar client.jar [member1] [member2]
 *
 * member1 : true 为游戏会员，游戏角色是无敌的
 * member2 : true 为超级管理员，可以使用更多的功能
 */
public class Start extends JFrame {
    public static volatile boolean stop = false;

    private UserInterface userInterface;

    public static void main(String[] args){
        Start start = new Start();
        start.start(args);
    }

    public void start(String[] args) {
        System.out.println("请输入服务器IP");
        String host = new Scanner(System.in).nextLine();
        String clientHost; // 客户端实际IP
        int post;
        try {
            Socket so = new Socket(host, 2688);

            clientHost = findIP(host);

            outString(so, "当前用户ip:" + clientHost);

            String s = inString(so);//第一次连接，接收到可用端口

            so.close();

            System.out.println(s);

            String[] split = s.split("-");

            post = Integer.parseInt(split[0]);

            //连接一个可以交互的端口
            Socket socket = new Socket(host, post);

            int dataPort = Integer.parseInt(split[1]);

            System.out.println("请输入姓名！");
            String name = new Scanner(System.in).nextLine();

            String member = getArgByIndex(0, args);

            UserInterface.member = getArgByIndex(1, args).equals("true");

            // '-' 后面的是名字
            outString(socket, clientHost + ":" + member.equals("true") + "+" + dataPort + "-" + name);

            System.out.println(inString(socket));

            //创建监听对象
            init(socket);

            //接收数据
            DatagramSocket datagramSocket = new DatagramSocket(dataPort);//监听本机1688端口,持续获取数据

            level1 : while (!stop) {
                int finalLength = -1;
                Map<Integer, byte[]> map =new TreeMap<>();
                Set<Integer> surplusIndex = null;
                long id = -1;
                boolean end = false;

                while (true) {
                    byte[] buff = new byte[25000];

                    DatagramPacket packet = new DatagramPacket(buff, buff.length);

                    datagramSocket.receive(packet);//接收
                    try (ByteArrayInputStream byteArrayStram = new ByteArrayInputStream(buff); ObjectInputStream objectStream = new ObjectInputStream(byteArrayStram)) {

                        Object object = objectStream.readObject();

                        if (object instanceof UDPData){
                            UDPData data = (UDPData) object;

                            if (id == -1){
                                id = data.getId();
                            }else if (id != data.getId()){
                                continue level1;
                            }

                            if (finalLength == -1){
                                if (data.getState() != UDPData.State.START && data.getFinalIndex() > 1) {
                                    continue level1;
                                }
                                finalLength = data.getFinalLength();
                            }

                            map.put(data.getIndex(), data.getBytes());

                            if (surplusIndex != null){
                                surplusIndex.remove(data.getIndex());

                                if (surplusIndex.isEmpty()){
                                    end = true;
                                    break;
                                }
                            }

                            if (data.getState() == UDPData.State.END){
                                boolean stop = true;
                                for (int i = 0;  i < data.getFinalIndex(); i++) {
                                    if (!map.containsKey(i)) {
                                        if (surplusIndex == null){
                                            surplusIndex = new HashSet<>();
                                        }
                                        surplusIndex.add(i);
                                        stop = false;
                                    }
                                }

                                if (stop) {
                                    end = true;
                                    break;
                                }
                            }
                        }

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }

                if (finalLength != -1 && end){
                    byte[] bytes = new byte[finalLength];

                    AtomicInteger length = new AtomicInteger();

                    map.forEach((key, value)->{
                        for (byte b : value) {
                            bytes[length.getAndIncrement()] = b;
                        }
                    });

                    try(ByteArrayInputStream byteArrayStram = new ByteArrayInputStream(bytes);
                        ObjectInputStream objectStream = new ObjectInputStream(byteArrayStram)) {

                        userInterface.inputData = (InputData) objectStream.readObject();
                    }
                    userInterface.repaint();
                }

                /*byte[] buff = new byte[100000];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);

                datagramSocket.receive(packet);//接收

                ByteArrayInputStream byteArrayStram = new ByteArrayInputStream(buff);
                ObjectInputStream objectStream = new ObjectInputStream(byteArrayStram);

                userInterface.inputData = (InputData) objectStream.readObject();
                userInterface.repaint();

                objectStream.close();
                byteArrayStram.close();*/
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void init(Socket socket){
        userInterface = new UserInterface(socket);

        this.add(userInterface);

        this.addKeyListener(userInterface);
        this.addFocusListener(userInterface);
        this.setSize(1800,1000);
        this.setLocation(100,50);
        this.setTitle("炸弹人");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static String inString(Socket socket) throws UnsupportedEncodingException {
        byte[] bs=new byte[1024];
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

    /**
     * 通过下标获取args的值
     * @param index 下标
     * @param args 数组
     * @return 返回输入的泛型值
     */
    private String getArgByIndex(int index, String[] args){
        if (args.length <= index){
            return "";
        }else {
            return args[index];
        }
    }

    /**
     * 获取和服务器同一网段的IP
     */
    private String findIP(String serverIP){
        String networkSegment = serverIP.substring(0, serverIP.lastIndexOf(".") + 1);

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        String ipAddress = inetAddress.getHostAddress();
                        if (ipAddress.startsWith(networkSegment)) {
                            return ipAddress;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("获取本地IP错误！" + e.getMessage());
        }

        return null;
    }
}
