import java.util.Dictionary;
import java.util.Map;
import java.util.Scanner;

public class Main
{
    private static final Scanner sc = new Scanner(System.in);
    private static Dictionary dictLatin;
    private static Dictionary dictNumbers;

    public static void main(String[] args)
    {
        dictLatin = new Dictionary("", "Latin dictionary (4 el");
        dictNumbers = new Dictionary("", "Словарь цифр(5 el");
        dictLatin.load();
        dictNumbers.load();
        //...
    }
    private static void viewBoth()
    {

    }

    private static void operateOnDict()
    {

    }
    private static void printMap()
    {

    }
    private static int readInt()
    {

    }
}
