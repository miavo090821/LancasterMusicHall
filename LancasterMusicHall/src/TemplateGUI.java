import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Main class to run both Part 1 and Part 2
public class TemplateGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TemplateGUI::new);
    }

    public TemplateGUI() {
        JFrame frame = new JFrame("Template Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        frame.add(getTopPanel());
        frame.add(getMainContainer());
        frame.add(getBottomPanel());

        frame.setVisible(true);
    }

    /**
     * function to write the title
     * */
    private JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(142, 143, 255)); // colouring the background
        topPanel.setPreferredSize(new Dimension(500, 40));

        return topPanel;
    }

    /**
     * function to write the main section, split into four parts:
     *         1. Main Section 1
     *         2. Main Section 2
     *         3. Main Section 3
     *         4. Main Section 4
     * */
    private JPanel getMainContainer() {
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel section1 = getMainSection1();
        JPanel section2 = getMainSection2();
        JPanel section3 = getMainSection3();
        JPanel section4 = getMainSection4();

        mainContainer.add(section1);
        mainContainer.add(section2);
        mainContainer.add(section3);
        mainContainer.add(section4);

        return mainContainer;
    }


    /**
     * function to write section 1:
     * */
    private JPanel getMainSection1() {
        JPanel section1 = new JPanel();
        section1.setBackground(new Color(255, 137, 58)); // colouring the background
        section1.setPreferredSize(new Dimension(500, 30));

        return section1;
    }

    /**
     * function to write section 2
     * */
    private JPanel getMainSection2() {
        JPanel section2 = new JPanel();
        section2.setPreferredSize(new Dimension(500, 30));
        section2.setBackground(new Color(144, 255, 129)); // colouring the background

        return section2;
    }

    /**
     * function to write section 3
     * */
    private JPanel getMainSection3() {
        JPanel section3 = new JPanel();
        section3.setPreferredSize(new Dimension(500, 30));
        section3.setBackground(new Color(245, 210, 86)); // colouring the background

        return section3;
    }

    /**
     * function to write section 4
     * */
    private JPanel getMainSection4() {
        JPanel section4 = new JPanel();
        section4.setPreferredSize(new Dimension(500, 30));
        section4.setBackground(new Color(255, 166, 166)); // colouring the background

        return section4;
    }

    /**
     * Bottom panel has one part:
     *        1. Bottom Section
     * */
    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(255, 91, 255)); // colouring the background
        bottomPanel.setPreferredSize(new Dimension(500, 40));

        return bottomPanel;
    }
}



