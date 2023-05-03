import java.io.*;
import java.net.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Server {
    //Серверный UDP-сокет запущен на этом порту
    public final static int SERVICE_PORT = 50001;
    public static void main (String[] args){

        try {
            //Создали новый экземпляр DatagramSocket, чтобы получать ответы от клиента
            DatagramSocket serverSocket = new DatagramSocket(SERVICE_PORT);

            //Создайте буферы для хранения отправляемых и получаемых данных
            byte[] receivingDataBuffer = new byte[1024];
            byte[] sendingDataBuffer = new byte[1024];

            //Создали экземпляр UDP-пакета для хранения клиентских данных с использованием буфера для полученных данных
            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);

            String receivedData; //данные клиента
            double[] func = new double[3]; //массив для хранения данных таблицы (min, max, step)

            String SendingMessage; //результат
            InetAddress senderAddress; //IP-адрес клиента
            int senderPort; //порт клиента
            DatagramPacket outputPacket; //UDP-пакет с данными для отправки клиенту


            double area = 0;
            for (int j = 0; j < 3; j++) {
                //Получение данных от клиента и сохранение их в inputPacket
                serverSocket.receive(inputPacket);
                //Занесение отправленных клиентом данных в массив func
                receivedData = new String(inputPacket.getData());
                func[j] = Double.parseDouble(receivedData);
            }
            double a = func[0];
            double b = func[1];
            double h = func[2];

            System.out.println("min (a) = " + a);
            System.out.println("max (b) = " + b);
            System.out.println("step (h) = " + h);

            Callable<Double> t1 = new Callable4(a, a + ((b - a) * 0.25), h);
            Callable<Double> t2 = new Callable4(a + ((b - a) * 0.25), a + ((b - a) * 0.5), h);
            Callable<Double> t3 = new Callable4(a + ((b - a) * 0.5), a + ((b - a) * 0.75), h);
            Callable<Double> t4 = new Callable4(a + ((b - a) * 0.75), b, h);

            FutureTask futureTask1 = new FutureTask(t1);
            FutureTask futureTask2 = new FutureTask(t2);
            FutureTask futureTask3 = new FutureTask(t3);
            FutureTask futureTask4 = new FutureTask(t4);

            new Thread(futureTask1).start();
            new Thread(futureTask2).start();
            new Thread(futureTask3).start();
            new Thread(futureTask4).start();

            try {
                System.out.println("Интервал 1: " + futureTask1.get());
                System.out.println("Интервал 2: " + futureTask2.get());
                System.out.println("Интервал 3: " + futureTask3.get());
                System.out.println("Интервал 4: " + futureTask4.get());
                area = (double) futureTask1.get() + (double) futureTask2.get() + (double) futureTask3.get() + (double) futureTask4.get();
                System.out.println("Итог: " + area);


                //отправка на клиент результатов
                SendingMessage = Double.toString(area); //перевод результата (area) в String
                sendingDataBuffer = SendingMessage.getBytes(); //результат преобразуем в байты
                senderAddress = inputPacket.getAddress(); //Получение IP-адреса клиента
                senderPort = inputPacket.getPort(); ///Получение порта клиента
                //Создание нового UDP-пакета с данными, чтобы отправить их клиенту
                outputPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, senderAddress, senderPort);
                serverSocket.send(outputPacket); //Отправка пакета (результата) клиенту

            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
            //Закрыли соединение сокетов
            serverSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
