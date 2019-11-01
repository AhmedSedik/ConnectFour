import java.util.Scanner;

public class Console {


    public static void main(String[] args) {

        System.out.println("Welcome. Please Select Game:\n");
        System.out.println("1.Vier-Gewinnt\n2.Chomp");
        Scanner x = new Scanner(System.in);
        int selection = x.nextInt();
        if (selection ==1) {
            vierGewinnt game1 = new vierGewinnt();
            game1.durchgang();

        }



    }
}
