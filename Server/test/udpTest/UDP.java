package udpTest;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * udp 每次传输有大小上限，当前方法准备将字节转换成多次发送
 */
public class UDP {
    public int clientPort = 2222;

    public static void main(String[] args) {
        UDP udp = new UDP();

        new Thread(()->{
            try {
                InetAddress address = InetAddress.getByName("127.0.0.1");
                DatagramPacket finalPacket = new DatagramPacket(new byte[0], 0, address, udp.clientPort);

                Set<String> players = new HashSet<>();

                int a = 8000;
                while (a > 0){
                    players.add(a-- + "");
                }

                DatagramSocket socket = new DatagramSocket();

                udp.server(finalPacket, players, socket);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(()->{
            try {
                DatagramSocket datagramSocket = new DatagramSocket(udp.clientPort);//监听本机1688端口,持续获取数据
                udp.client(datagramSocket);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void server(DatagramPacket finalPacket, Set<String> players, DatagramSocket socket) throws IOException, InterruptedException {
        while (true) {
            try(ByteArrayOutputStream stream = new ByteArrayOutputStream(); ObjectOutputStream output = new ObjectOutputStream(stream)){
                output.writeObject(players);

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
                    UdpData udpData = new UdpData(id, sendIndex, sendData, finalIndex, finalLength, sendIndex == 0 ? UdpData.State.START : UdpData.State.SEND);

                    send(udpData, finalPacket, socket);

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

                UdpData udpData = new UdpData(id, sendIndex, sendData, finalIndex, finalLength, UdpData.State.END);

                send(udpData, finalPacket, socket);
            }

            Thread.sleep(100);
        }

    }

    public void send(Object object, DatagramPacket finalPacket, DatagramSocket finalSocket) throws IOException {
        try(ByteArrayOutputStream stream = new ByteArrayOutputStream(); ObjectOutputStream output = new ObjectOutputStream(stream)){
            output.writeObject(object);

            finalPacket.setData(stream.toByteArray());//填充DatagramPacket
            finalSocket.send(finalPacket);//发送

            output.flush();
        }
    }

    public void client(DatagramSocket datagramSocket) throws IOException, ClassNotFoundException {

        level1 : while (true) {
            int finalLength = -1;
            Map<Integer, byte[]> map =new TreeMap<>();
            long id = -1;
            boolean end = false;

            while (true) {
                byte[] buff = new byte[25000];

                DatagramPacket packet = new DatagramPacket(buff, buff.length);

                datagramSocket.receive(packet);//接收
                try (ByteArrayInputStream byteArrayStram = new ByteArrayInputStream(buff); ObjectInputStream objectStream = new ObjectInputStream(byteArrayStram)) {

                    Object object = objectStream.readObject();

                    if (object instanceof UdpData){
                        UdpData data = (UdpData) object;

                        if (id == -1){
                            id = data.getId();
                        }else if (id != data.getId()){
                            continue level1;
                        }

                        if (finalLength == -1){
                            if (data.getState() != UdpData.State.START && data.getFinalIndex() > 1) {
                                continue level1;
                            }
                            finalLength = data.getFinalLength();
                        }

                        map.put(data.getIndex(), data.getBytes());

                        if (data.getState() == UdpData.State.END){

                            int last = -1;
                            boolean stop = true;
                            for (Integer integer : map.keySet()) {
                                if (++last != integer){
                                    stop = false;
                                    break;
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

                ByteArrayInputStream byteArrayStram = new ByteArrayInputStream(bytes);
                ObjectInputStream objectStream = new ObjectInputStream(byteArrayStram);

                Object object = objectStream.readObject();

                System.out.println(object);
            }
        }
    }
}

class UdpData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;

    private final int index;

    private final byte[] bytes;

    private final int finalIndex;

    private final int finalLength;

    private final State state;

    public UdpData(long id, int index, byte[] bytes, int finalIndex, int finalLength, State state) {
        this.id = id;
        this.index = index;
        this.bytes = bytes;
        this.finalIndex = finalIndex;
        this.finalLength = finalLength;
        this.state = state;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getFinalIndex() {
        return finalIndex;
    }

    public int getFinalLength() {
        return finalLength;
    }

    public State getState() {
        return state;
    }

    public enum State implements Serializable {
        START,SEND,END
    }
}

