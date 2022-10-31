package priv.cdk;

import priv.cdk.bomberman.UserInterface;
import priv.cdk.bomberman.data.InputData;
import priv.cdk.bomberman.data.UDPData;
import priv.cdk.bomberman.game.Game;
import priv.cdk.bomberman.server.PlayerData;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 启动程序
 */
public class StartingProcedure extends JFrame {
    public static final int PORT = 2688;
    public static final int SEND_PORT = 3688;

    public static int maxPlayerNumber = 1;//最多多少人加入游戏

    public AtomicBoolean playerIsMax = new AtomicBoolean(false);//人数已达上限
    public final AtomicBoolean gameStop = new AtomicBoolean(false);//游戏关闭  最终指令

    //设置面板
    private Game game;

    private final PlayerData[] playerData = new PlayerData[maxPlayerNumber];

    public static void main(String[] args) {
        System.out.println("请输入一个房间的人数");
        maxPlayerNumber = new Scanner(System.in).nextInt();
        System.out.println("服务器启动");

        int port = PORT;
        int sendPort = SEND_PORT;

        do {
            try {
                if (port == SEND_PORT){
                    System.out.println("端口已经全部被使用");
                    break;
                }
                new StartingProcedure(port, sendPort);

                System.out.println("端口[" + port + "," + (port + maxPlayerNumber) + "][" + sendPort + "," + (sendPort + maxPlayerNumber) + "]已经全部被连接");

                port += maxPlayerNumber + 1;
                sendPort += maxPlayerNumber + 1;
            }catch (Exception e){
                e.printStackTrace();
                break;
            }
        }while (true);
    }

    public StartingProcedure(int port, int sendPort){
        AtomicInteger maxNumber = new AtomicInteger(0);

        //打开PORT的端口，接收客户端第一次连接 ，返回一个可交互的端口
        try(ServerSocket ss = new ServerSocket(PORT)) {
            do {
                Socket s = ss.accept();
                if(playerIsMax.get() || gameStop.get()){
                    break;
                }
                System.out.println(inString(s));

                int i = maxNumber.get();//第几个用户连接

                PlayerData playerDatum = new PlayerData(maxNumber.get());//主要交互对象

                int interactionPort = port + maxNumber.addAndGet(1);//使用一个交互端口

                int dataPort = sendPort + maxNumber.get();

                playerDatum.setPort(interactionPort);//保存客户端端口
                playerDatum.setDataPort(dataPort);//保存客户端接收数据的端口

                this.playerData[i] = playerDatum;

                outString(s, interactionPort + "-" + dataPort);

                createConnect(i);
            } while (maxNumber.get() < maxPlayerNumber);

            playerIsMax.set(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建连接
     *
     * 当所有连接正常后，进行数据交互，用来监听玩家按键盘.
     *
     */
    public void createConnect(int i){
        PlayerData playerDatum = playerData[i];

        playerDatum.setThread(new Thread(()->{
            try (ServerSocket ss = new ServerSocket(playerDatum.getPort()); Socket s = ss.accept()) { //等待连接

                boolean first = true;

                while (!gameStop.get()) {
                    String data = inString(s);

                    if(gameStop.get()){
                        System.out.println(playerDatum.getPlayerName() + "退出！");
                        break;
                    }

                    if (first) {
                        String cHost = data.substring(0, data.indexOf(":"));
                        String cPost = data.substring(data.indexOf(":") + 1, data.indexOf("-"));
                        String playName = data.substring(data.indexOf("-") + 1);

                        playerDatum.setPlayerName(playName);
                        playerDatum.setIp(cHost);
                        playerDatum.setAddress(InetAddress.getByName(cHost));

                        if (i == maxPlayerNumber - 1) {//第一次回应，并且玩家已达上限

                            String[] players = new String[playerData.length];
                            for (PlayerData datum : playerData) {
                                players[datum.getNumber()] = datum.getPlayerName();
                            }
                            game = new Game(players);

                            for (PlayerData datum : playerData) {

                                datum.setGame(game);
                                datum.setUserInterface(new UserInterface(game));

                                createGame(datum.getNumber());
                            }
                        }

                        first = false;

                    } else {
//                        System.out.println(data);
                        if (data.equals("")) {
                            System.out.println(playerDatum.getPlayerName() + "退出！");
                            break;
                        }
                        playerDatum.getUserInterface().keyPressed(i, Integer.parseInt(data));
                    }

                    outString(s, "接收成功！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //运行完毕，关闭输出数据的线程

            if (playerDatum.getTimer() != null) {
                playerDatum.getTimer().cancel();
                playerDatum.setTimer(null);
            }

            playerDatum.setThread(null);

            if(playerDatum.getGame() != null) {
                playerDatum.getGame().gameStop();
            }

            gameStop.set(true);

            if(playerIsMax.compareAndSet(false, true)) {
                try {
                    new Socket("127.0.0.1", 2688);
                } catch (IOException e) {
//                e.printStackTrace();
                    System.out.println("中断等待其它玩家连接！");
                }
            }
        }));

        playerDatum.getThread().start();
    }





    /**
     * 创建传输游戏数据对象 ，不需要互动
     */
    public void createGame(int pNumber){
        PlayerData playerDatum = playerData[pNumber];

        InputData data = new InputData(pNumber, playerDatum.getGame());

        //持续发送数据
        DatagramPacket finalPacket = new DatagramPacket(new byte[0], 0, playerDatum.getAddress(), playerDatum.getDataPort());

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        DatagramSocket finalSocket = socket;

        playerDatum.setTimer(new Timer());
        playerDatum.getTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                data.reload(playerDatum.getGame());

                try(ByteArrayOutputStream stream = new ByteArrayOutputStream(); ObjectOutputStream output = new ObjectOutputStream(stream)) {
                    output.writeObject(data);

                    byte[] bytes = stream.toByteArray();

                    int byteLength = 20000;
                    int sendLength = 0;
                    int sendIndex = 0;
                    int length = (bytes.length/byteLength) * byteLength;
                    int finalIndex = (bytes.length/byteLength) + (bytes.length % byteLength == 0 ? 0 : 1 );
                    int finalLength = bytes.length;
                    long id = System.currentTimeMillis() + new Random().nextInt(1000000);

                    while (sendLength < length){
                        byte[] sendData = new byte[byteLength];

                        for (int j = 0; j < byteLength; sendLength++, j++) {
                            sendData[j] = bytes[sendLength];
                        }
                        UDPData udpData = new UDPData(id, sendIndex, sendData, finalIndex, finalLength, sendIndex == 0 ? UDPData.State.START : UDPData.State.SEND);

                        send(udpData, finalPacket, finalSocket);

                        sendIndex++;
                    }

                    int endLength = bytes.length % byteLength;

                    byte[] sendData;
                    if (endLength != 0){
                        sendData = new byte[endLength];

                        for (int j = 0; j < endLength; sendLength++, j++) {
                            sendData[j] = bytes[sendLength];
                        }
                    }else{
                        sendData = null;
                    }

                    UDPData udpData = new UDPData(id, sendIndex, sendData, finalIndex, finalLength, UDPData.State.END);

                    send(udpData, finalPacket, finalSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*//对象->对象流->字节数组流->字节数组
                try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream(); ObjectOutputStream objectStream = new ObjectOutputStream(byteArrayStream)) {
                    objectStream.writeObject(data);
//                    objectStream.reset();//重复数据重置

                    byte[] arr = byteArrayStream.toByteArray();

                    finalPacket.setData(arr);//填充DatagramPacket

                    finalSocket.send(finalPacket);//发送

                    objectStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }, 0, 5);
    }

    public void send(Object object, DatagramPacket finalPacket, DatagramSocket finalSocket) throws IOException {
        try(ByteArrayOutputStream stream = new ByteArrayOutputStream(); ObjectOutputStream output = new ObjectOutputStream(stream)){
            output.writeObject(object);

            finalPacket.setData(stream.toByteArray());//填充DatagramPacket
            finalSocket.send(finalPacket);//发送

            output.flush();
        }
    }

    public static String inString(Socket socket) throws UnsupportedEncodingException {
        byte[] bs=new byte[10240];
        int len=0;
        try {
            len=socket.getInputStream().read(bs);		//读取数据,异常则说明客户端突然关闭,则将本端口号设置成可连接状态
        }catch(Exception e) {
            System.out.println("客户端已关闭");
        }

        if(len > 0) {
            return new String(bs, 0, len, "GBK");
        }else{
            return "";
        }
    }

    public static void outString(Socket socket, String string){
        try {
            socket.getOutputStream().write(string.getBytes("GBK"));
        }catch (Exception e) {            //用户自动退出时发送异常
            System.out.println("服务器已关闭");
        }
    }
}
