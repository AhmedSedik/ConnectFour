package spielLogik;
import java.util.Scanner;

public class Console {


    public static void main(String[] args) {

       boolean isValid = false;

        while (!isValid) {
            int selection =0;
            System.out.println("Welcome. Please Select a Game to play:\n");
            System.out.println("1.Vier-Gewinnt\n2.Chomp");
            Scanner x = new Scanner(System.in);
            try {
                 selection= x.nextInt();
            } catch (Exception e) {
                x.next();

            }

            if (selection == 1) {
                isValid = true;
                VierGewinnt game1 = new VierGewinnt();
                System.out.println("  ____ ___  _   _ _   _ _____ ____ _____   _____ ___  _   _ ____  \n / ___/ _ \\| \\ | | \\ | | ____/ ___|_   _| |  ___/ _ \\| | | |  _ \\ \n" +
                        "| |  | | | |  \\| |  \\| |  _|| |     | |   | |_ | | | | | | | |_| |\n| |__| |_| | |\\  | |\\  | |__| |___  | |   |  _|| |_| | |_| |  _ | \n" +
                        " \\____\\___/|_| \\_|_| \\_|_____\\____| |_|   |_|   \\___/ \\___/|_| \\_\\\n\n" +
                        "Connect Four is a two-player connection game in which the players\nfirst choose a colour and then take turns dropping colored discs\nfrom the top into a seven-column, six-row grid. " +
                        "The pieces fall\nstraight down, occupying the next available space within the column.\nThe objective of the game is to connect four of one's own discs of\nthe same color next to each other vertically, horizontally, or\ndiagonally before your opponent.\n"); //Intro text.

                game1.durchgang();

            } else if (selection == 2) {
                isValid = true;
                Futtern game2 = new Futtern();
                game2.durchgang();
            } else {
                System.out.println("Wrong Entry! \n\n");
                isValid = false;

            }
        }




    }

}
