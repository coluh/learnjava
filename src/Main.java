import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        GameBody gBody = new GameBody();
        gBody.init();
        gBody.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        boolean isPlaying = true;
        while (isPlaying) {
            gBody.play();
        }
    }
}