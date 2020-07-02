import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextEditor extends JFrame {
    File selectedFile;
    JTextArea textArea = new JTextArea();
    JTextField filenameField = new JTextField("No file");
    JLabel lineCount = new JLabel(textArea.getLineCount() + " L");

    {
        textArea.setName("TextArea");
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setLineWrap(true);

        textArea.getInputMap().put(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK),
            "save");

        textArea.getInputMap().put(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_MASK),
            "save");

        textArea.getInputMap().put(
            KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK),
            "load");

        textArea.getInputMap().put(
            KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.META_MASK),
            "load");

        textArea.getActionMap().put("save",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    saveTextAreaContents();
                }
            });

        textArea.getActionMap().put("load",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    loadFile();
                }
            });

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                if (!filenameField.getText().matches("^(\\* )(.+)")) {
                    filenameField.setText("* " + filenameField.getText());
                }

                lineCount.setText(textArea.getLineCount() + " L");
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if (!filenameField.getText().matches("^(\\* )(.+)")) {
                    filenameField.setText("* " + filenameField.getText());
                }

                lineCount.setText(textArea.getLineCount() + " L");
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                if (!filenameField.getText().matches("^(\\* )(.+)")) {
                    filenameField.setText("* " + filenameField.getText());
                }

                lineCount.setText(textArea.getLineCount() + " L");
            }
        });

        filenameField.setName("FilenameField");
        filenameField.setEditable(false);
        filenameField.setCaretPosition(0);
        filenameField.setHorizontalAlignment(JTextField.CENTER);
        filenameField.setBackground(new Color(255, 255, 255, 0));
        filenameField.setOpaque(false);
        filenameField.setBorder(BorderFactory.createEmptyBorder());
    }

    private void saveTextAreaContents() {
        if (selectedFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showSaveDialog(getContentPane());
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                filenameField.setText(selectedFile.getName());
            } else {
                return;
            }
        }

        try {
            Files.writeString(Path.of(selectedFile.getAbsolutePath()), textArea.getText());
            filenameField.setText(filenameField.getText().replaceAll("^\\* ", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(getContentPane());
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filenameField.setText(selectedFile.getName());

            try {
                textArea.setText(Files.readString(Path.of(selectedFile.getAbsolutePath())));
                filenameField.setText(filenameField.getText().replaceAll("^\\* ", ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Box buildUpperBox() {
        Box upperBox = Box.createHorizontalBox();
        upperBox.setName("UpperBox");
        upperBox.setOpaque(true);
        upperBox.setBackground(Color.gray);

        JPanel upperPanel = new JPanel();
        upperBox.setName("UpperPanel");
        upperPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 50));
        upperPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        upperPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton saveButton = new JButton("Save");
        saveButton.setName("SaveButton");
        saveButton.addActionListener(actionEvent -> saveTextAreaContents());

        JButton loadButton = new JButton("Load");
        loadButton.setName("LoadButton");
        loadButton.addActionListener(actionEvent -> loadFile());

        upperPanel.add(filenameField);
        upperBox.add(saveButton);
        upperBox.add(loadButton);

        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS));
        upperBox.add(upperPanel);
        return upperBox;
    }

    private Box buildCenterBox() {
        Box centerBox = Box.createHorizontalBox();
        centerBox.setName("CenterBox");

        JScrollPane scrollableTextArea = new JScrollPane(textArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollableTextArea.setName("ScrollPane");

        centerBox.add(scrollableTextArea);
        return centerBox;
    }

    private Box buildLowerBox() {
        Box lowerBox = Box.createHorizontalBox();
        lowerBox.setName("LowerBox");
        lowerBox.setOpaque(true);
        lowerBox.setBackground(Color.gray);

        JPanel lowerPanel = new JPanel();
        lowerPanel.setName("LowerPanel");
        lowerPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 50));
        lowerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        lowerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lowerPanel.add(lineCount);
        lowerBox.add(lowerPanel);
        return lowerBox;
    }

    public TextEditor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setTitle("TextEditor");
        setLocationRelativeTo(null);

        add(buildUpperBox());
        add(buildCenterBox());
        add(buildLowerBox());

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setVisible(true);
    }
}
