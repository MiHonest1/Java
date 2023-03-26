import java.awt.*;
import static javax.swing.JOptionPane.showMessageDialog;

//класс Exception1, унаследованный от класса Exception:
public class Exception1 extends Exception{

    private String title;

    public Exception1(String title) {
        this.title = title;
    }

    //Диалоговое окно с заголовком, cообщением и типом сообщения
    public void Message1(Component parent, int optionType) {
        showMessageDialog(parent, "Введён нулевой шаг", title, optionType);
    }
    public void Message2(Component parent, int optionType) {
        showMessageDialog(parent, "Выход за пределы диапазона (0.000001; 1000000)", title, optionType);
    }

    public void Message3(Component parent, int optionType) {
        showMessageDialog(parent, "Верхний предел не может быть меньше нижнего предела", title, optionType);
    }

}

