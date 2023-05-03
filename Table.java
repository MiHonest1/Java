import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;


import static javax.swing.JOptionPane.ERROR_MESSAGE;


public class Table implements Serializable {
    private static LinkedList<RecIntegral> info = new LinkedList<>();
    private LinkedList<String> values = new LinkedList<>();
    private JButton AddButton;
    private JButton DelButton;
    private JButton CalcButton;
    private JTextField Down;
    private JTextField Up;
    private JTextField Step;
    public JTable MyTable;
    public DefaultTableModel MyModel;
    private JPanel MyPanel;
    private JButton ClearButton;
    private JButton StuffButton;
    private JButton WriteButton;
    private JButton DownloadButton;
    private JButton WriteButtonSer;
    private JButton DownloadButtonSer;

    // Порт сервера, к которому собирается подключиться клиентский сокет
    public final static int SERVICE_PORT = 50001;
    public Table() {

        //Убираем возможность редактирования столбца с результатом
        MyTable.setModel(new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) { // Возвращает true, если ячейка в rowIndex и columnIndex доступна для редактирования
                return columnIndex != 3;
            }
        });

        // Чтобы добавить и удалить строки из таблицы, нужно использовать DefaultTableModel
        MyModel = (DefaultTableModel) MyTable.getModel();

        // создание столбцов
        MyModel.addColumn("Низ");
        MyModel.addColumn("Верх");
        MyModel.addColumn("Шаг");
        MyModel.addColumn("Результат");

        AddButton.addActionListener(new ActionListener() { // ActionListener хранит в себе метод, который активируется при нажатии кнопки
            @Override
            public void actionPerformed(ActionEvent e) { // actionPerformed - метод, который активируется при нажатии кнопки
                //Обработка исключений с помощью блоков try-catch:
                try {
                    if (Double.parseDouble(Step.getText()) == 0) {
                        throw new Exception1("Шаг");
                    }

                } catch (Exception1 ex) {
                    ex.Message1(MyPanel, ERROR_MESSAGE);
                    return;
                }


                try {
                    if (Double.parseDouble(Down.getText()) > 1000000 || Double.parseDouble(Down.getText()) < 0.000001) {
                        throw new Exception1("Нижний предел");
                    }
                    if (Double.parseDouble(Up.getText()) > 1000000 || Double.parseDouble(Up.getText()) < 0.000001) {
                        throw new Exception1("Верхний предел");
                    }
                    if (Double.parseDouble(Step.getText()) > 1000000 || Double.parseDouble(Step.getText()) < 0.000001) {
                        throw new Exception1("Шаг");
                    }

                } catch (Exception1 ex) {
                    ex.Message2(MyPanel, ERROR_MESSAGE);
                    return;
                }


                try {
                    if (Double.parseDouble(Down.getText()) > Double.parseDouble(Up.getText())) {
                        throw new Exception1("Пределы");
                    }

                } catch (Exception1 ex) {
                    ex.Message3(MyPanel, ERROR_MESSAGE);
                    return;
                }


                Double Up = Double.parseDouble(Table.this.Up.getText()); // getText возвращает текст, содержащийся в текстовом поле Up
                Double Down = Double.parseDouble(Table.this.Down.getText());
                Double Step = Double.parseDouble(Table.this.Step.getText());
                MyModel.addRow(new Double[]{Down, Up, Step}); // addRow добавляет строку
                info.add(new RecIntegral(Down, Up, Step, 0));//Работа с коллекцией

            }
        });

        CalcButton.addActionListener(new ActionListener() { // ActionListener хранит в себе метод, который активируется при нажатии кнопки
            @Override
            public void actionPerformed(ActionEvent e) { // actionPerformed - метод, который активируется при нажатии кнопки

                try {
                    //Создали экземпляр клиентского сокета
                    DatagramSocket clientSocket = new DatagramSocket();
                    //Получение IP-адреса сервера
                    InetAddress IPAddress = InetAddress.getByName("localhost");

                    //Создали соответствующие буферы
                    byte[] sendingDataBuffer = new byte[1024];
                    byte[] receivingDataBuffer = new byte[1024];

                    String data;
                    //UDP-пакеты
                    DatagramPacket sendingPacket;
                    DatagramPacket receivingPacket;


                    //записываем информацию из модела для передачи на сервер
                    for (int j = 0; j < 3; j++) {
                        data = new String(MyModel.getValueAt(0, j).toString());
                        sendingDataBuffer = data.getBytes();
                        //Создали UDP-пакет
                        sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddress, SERVICE_PORT);
                        //Отправили UDP-пакет серверу
                        clientSocket.send(sendingPacket);
                    }

                    //Получение обработанной инфы из сервера, занесение в таблицу
                    //Получили ответ от сервера
                    receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
                    clientSocket.receive(receivingPacket);
                    //Полученные данные занесли в таблицу
                    String receivedData = new String(receivingPacket.getData());
                    MyModel.setValueAt(Double.parseDouble(receivedData), 0, 3);
                    info.get(0).Res = Double.parseDouble(receivedData); //Работа с коллекцией

                    //Закрыли соединение с сервером через сокет
                    clientSocket.close();

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }


            }

        });

        DelButton.addActionListener(new ActionListener() { // ActionListener хранит в себе метод, который активируется при нажатии кнопки
            @Override
            public void actionPerformed(ActionEvent e) { // actionPerformed - метод, который активируется при нажатии кнопки
                int indexRow = MyTable.getSelectedRow(); // Метод getSelectedRow() возвращает индекс первой выбранной строки
                if (indexRow != -1) {
                    MyModel.removeRow(indexRow); // Метод removeRow() имеет параметр ind - индекс строки, которая будет удалена
                    info.remove(indexRow);//Работа с коллекцией
                }
            }
        });

        //Очистить таблицу
        ClearButton.addActionListener(new ActionListener() { // ActionListener хранит в себе метод, который активируется при нажатии кнопки
            @Override
            public void actionPerformed(ActionEvent e) { // actionPerformed - метод, который активируется при нажатии кнопки
                //Каждый раз, когда удаляем строку, количество строк будет меняться. Продолжаем цикл до тех пор, пока не останется строк
                while (MyModel.getRowCount() > 0) {
                    MyModel.removeRow(0);
                }
            }
        });

        //Заполнить таблицу данными из коллекции
        StuffButton.addActionListener(new ActionListener() { // ActionListener хранит в себе метод, который активируется при нажатии кнопки
            @Override
            public void actionPerformed(ActionEvent e) { // actionPerformed - метод, который активируется при нажатии кнопки
                while (MyModel.getRowCount() > 0) {
                    MyModel.removeRow(0);
                }
                //Заполнение таблицы данными из коллекции
                for (RecIntegral data : info) {
                    MyModel.addRow(new Object[]{data.Down1, data.Up1, data.Step1, data.Res});
                }

            }
        });

        //Загрузить в файл
        WriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser window = new JFileChooser(); //Создание JFileChooser с указанием директории пользователя по умолчанию
                window.setCurrentDirectory(new File(".")); //Установка директории по умолчанию
                window.setDialogTitle("Загрузка в файл"); //Заголовок окна
                window.setMultiSelectionEnabled(false); //Невозможность выбора сразу нескольких файлов
                window.setFileSelectionMode(JFileChooser.FILES_ONLY); //Выбор только файлов
                window.setSelectedFile(new File("file1.txt")); //Выбор файла по умолчанию
                window.showDialog(window, "Загрузить в файл"); //Открытие окна выбора файла с настроенным наименованием кнопки

                File MyFile = window.getSelectedFile(); //Чтение выделенного файла

                try {
                    FileWriter myWriter = new FileWriter(MyFile); //Создание объекта класса FileWriter для записи в файл на основе имени файла в файловой системе
                    BufferedWriter buf = new BufferedWriter(myWriter);

                    for (int i = 0; i < MyTable.getRowCount(); i++) {
                        for (int j = 0; j < MyTable.getColumnCount(); j++) {
                            buf.write(MyTable.getValueAt(i, j).toString() + " ");
                        }
                        buf.newLine(); //Метод newLine() использует собственное понятие разделителя строк платформы, определенное системным свойством line.separator
                    }

                    //Поток закрыт
                    buf.close();
                    myWriter.close();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        //Загрузить из файла
        DownloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                while (MyModel.getRowCount() > 0) {
                    MyModel.removeRow(0);
                }

                JFileChooser window = new JFileChooser(); //Создание JFileChooser с указанием директории пользователя по умолчанию
                window.setCurrentDirectory(new File(".")); //Установка директории по умолчанию
                window.setDialogTitle("Загрузка из файла"); //Заголовок окна
                window.setMultiSelectionEnabled(false); //Невозможность выбора сразу нескольких файлов
                window.setFileSelectionMode(JFileChooser.FILES_ONLY); //Выбор только файлов
                window.setSelectedFile(new File("file1.txt")); //Выбор файла по умолчанию
                window.showDialog(window, "Загрузить из файла"); //Открытие окна выбора файла с настроенным наименованием кнопки

                File MyFile = window.getSelectedFile(); //Чтение выделенного файла

                try {
                    FileReader myReader = new FileReader(MyFile); //Создание объекта класса FileReader для чтения из файла на основе имени файла в файловой системе
                    BufferedReader buf = new BufferedReader(myReader); //BufferedReader записывает текст в поток вывода символов, буферизуя символы, чтобы обеспечить эффективную запись отдельных символов, массивов и строк

                    Object[] lines = buf.lines().toArray(); //Метод lines() — метод, который возвращает поток строк, извлеченных из заданной многострочной строки

                    for (int i = 0; i < lines.length; i++) {
                        String[] row = lines[i].toString().split(" "); //Метод split разделяет строку на подстроки, используя разделитель, который определяется с помощью регулярного выражения
                        MyModel.addRow(row);
                    }

                    //Поток закрыт
                    buf.close();
                    myReader.close();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        //Сериализация
        WriteButtonSer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser window = new JFileChooser();
                window.setCurrentDirectory(new File(".")); //Создание JFileChooser с указанием директории пользователя по умолчанию
                window.setDialogTitle("Выберите файл для сериализации"); //Заголовок окна
                window.setMultiSelectionEnabled(false); //Невозможность выбора сразу нескольких файлов
                window.setFileSelectionMode(JFileChooser.FILES_ONLY); //Выбор только файлов
                window.setSelectedFile(new File("A.ser")); //Выбор файла по умолчанию
                window.showDialog(window, "Выбрать"); //Открытие окна выбора файла с настроенным наименованием кнопки

                File MyFile = window.getSelectedFile(); //Чтение выделенного файла

                for (int i = 0; i < MyTable.getRowCount(); i++) {
                    for (int j = 0; j < MyTable.getColumnCount(); j++) {
                        values.add(String.valueOf(MyTable.getValueAt(i, j)));
                    }
                }

                try {
                    //создаем 2 потока для сериализации объекта и сохранения его в файл
                    FileOutputStream outputStream = new FileOutputStream(MyFile);
                    ObjectOutputStream out = new ObjectOutputStream(outputStream);
                    // сохраняем values в файл
                    out.writeObject(values);
                    //закрываем поток и освобождаем ресурсы
                    outputStream.close();
                    out.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //Десериализация
        DownloadButtonSer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                while (MyModel.getRowCount() > 0) {
                    MyModel.removeRow(0);
                }

                JFileChooser window = new JFileChooser(); //Создание JFileChooser с указанием директории пользователя по умолчанию
                window.setCurrentDirectory(new File(".")); //Установка директории по умолчанию
                window.setDialogTitle("Выберите файл для десериализации"); //Заголовок окна
                window.setMultiSelectionEnabled(false); //Невозможность выбора сразу нескольких файлов
                window.setFileSelectionMode(JFileChooser.FILES_ONLY); //Выбор только файлов
                window.setSelectedFile(new File("A.ser")); //Выбор файла по умолчанию
                window.showDialog(window, "Выбрать"); //Открытие окна выбора файла с настроенным наименованием кнопки

                File MyFile = window.getSelectedFile(); //Чтение выделенного файла

                try {
                    //создаем 2 потока для десериализации объекта и выгрузки его из файла
                    FileInputStream inputStream = new FileInputStream(MyFile);
                    ObjectInputStream in = new ObjectInputStream(inputStream);

                    values = (LinkedList<String>) in.readObject(); //Метод readObject для чтения объекта из потока
                    //закрываем поток и освобождаем ресурсы
                    in.close();
                    inputStream.close();

                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                int i = 0;
                while (i < values.size()) {
                    MyModel.addRow(new Object[]{
                            values.get(i),
                            values.get(i + 1),
                            values.get(i + 2),
                            values.get(i + 3)
                    });
                    i = i + 4;
                }
            }
        });

    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Интегральная гипербола кибернетики"); // создаём окно с рамкой
        frame.setVisible(true); // показывает или скрывает это окно в зависимости от значения параметра
        frame.setContentPane(new Table().MyPanel);//задаёт содержимое окна с рамкой
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Устанавливает операцию при закрытии окна закончить работу приложения,
        // которая будет выполняться по умолчанию, когда пользователь инициирует «закрытие» этого окна
        frame.setSize(420,600); // размер окна
        frame.setLocation(120,100); // расположение окна

    }

}
