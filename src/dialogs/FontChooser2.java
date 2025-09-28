package dialogs;

import javax.swing.*;
import java.awt.*;

/**
 * A font selection dialog.
 * <p>
 * Note: can take a long time to start up on systems with (literally) hundreds
 * of fonts.
 * preview.
 *
 * @author Ian Darwin
 * @version $Id: FontChooser.java,v 1.19 2004/03/20 20:44:56 ian Exp $
 */
public class FontChooser2 extends JDialog
{
    /**
     * The index of the default size (e.g., 14 point == 4)
     */
    private static final int DEFAULT_SIZE = 4;
    /**
     * The font name chooser
     */
    private final List fontNameChoice;
    /**
     * The font size chooser
     */
    private final List fontSizeChoice;
    /**
     * The bold and italic choosers
     */
    private final Checkbox bold;

    // Working fields
    private final Checkbox italic;
    /**
     * The display area. Use a JLabel as the AWT label doesn't always honor
     * setFont() in a timely fashion :-)
     */
    private final JLabel previewArea;
    /**
     * Called from the action handlers to get the font info, build a font, and
     * set it.
     */
    int resultSize;
    /**
     * The font the user has chosen
     */
    private Font resultFont;

    /**
     * Construct a FontChooser -- Sets title and gets array of fonts on the
     * system. Builds a GUI to let the user choose one font at one size.
     */
    public FontChooser2(JDialog f)
    {
        super(f, "Font Chooser", true);

        Container cp = getContentPane();

        Panel top = new Panel();
        top.setLayout(new FlowLayout());

        fontNameChoice = new List(8);
        fontNameChoice.addItemListener(e -> previewFont());
        top.add(fontNameChoice);

        //Toolkit toolkit = Toolkit.getDefaultToolkit();
        // For JDK 1.1: returns about 10 names (Serif, SansSerif, etc.)
        // fontList = toolkit.getFontList();
        // For JDK 1.2: a much longer list; most of the names that come
        // with your OS (e.g., Arial), plus the Sun/Java ones (Lucida,
        // Lucida Bright, Lucida Sans...)
        /* The list of Fonts */
        String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        for (String aFontList : fontList)
        {
            fontNameChoice.add(aFontList);
        }
        fontNameChoice.select(0);

        fontSizeChoice = new List(8);
        fontSizeChoice.addItemListener(e -> previewFont());

        top.add(fontSizeChoice);

        /* The list of font sizes */
        String[] fontSizes = {"8", "10", "11", "12", "14", "16", "18",
                "20", "24", "30", "36", "40", "48", "60", "72"
        };

        for (String fontSize : fontSizes) {
            fontSizeChoice.add(fontSize);
        }

        fontSizeChoice.select(DEFAULT_SIZE);

        cp.add(top, BorderLayout.NORTH);

        Panel attrs = new Panel();
        top.add(attrs);
        attrs.setLayout(new GridLayout(0, 1));
        attrs.add(bold = new Checkbox("Bold", false));
        attrs.add(italic = new Checkbox("Italic", false));

        /* Display text */
        String displayText = "Qwerty Yuiop";
        previewArea = new JLabel(displayText, JLabel.CENTER);
        previewArea.setSize(200, 50);
        cp.add(previewArea, BorderLayout.CENTER);

        Panel bot = new Panel();

        JButton okButton = new JButton("Apply");
        bot.add(okButton);
        okButton.addActionListener(e -> {
            previewFont();
            dispose();
            setVisible(false);
        });

        cp.add(bot, BorderLayout.SOUTH);

        previewFont(); // ensure view is up to date!

        pack();
        setLocation(100, 100);
    }

    private void previewFont ()
    {
        String resultName = fontNameChoice.getSelectedItem();
        String resultSizeName = fontSizeChoice.getSelectedItem();
        resultSize = Integer.parseInt(resultSizeName);
        boolean isBold = bold.getState();
        boolean isItalic = italic.getState();
        int attrs = Font.PLAIN;
        if (isBold)
        {
            attrs |= Font.BOLD;
        }
        if (isItalic)
        {
            attrs |= Font.ITALIC;
        }
        resultFont = new Font(resultName, attrs, resultSize);
        previewArea.setFont(resultFont);
        pack(); // ensure Dialog is big enough.
    }

    /**
     * Retrieve the selected font, or null
     */
    public Font getSelectedFont ()
    {
        return resultFont;
    }

// --Commented out by Inspection START (3/2/2018 7:49 PM):
}