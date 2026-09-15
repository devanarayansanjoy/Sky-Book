import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class AutoCompleteTextField extends JTextField {
    private final List<String> dictionary;
    private final JPopupMenu popupMenu;

    public AutoCompleteTextField(int columns, List<String> dictionary) {
        super(columns);
        this.dictionary = dictionary;
        this.popupMenu = new JPopupMenu();

        getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> popupMenu.setVisible(false));
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popupMenu.setVisible(false);
                }
            }
        });
    }

    private void updateSuggestions() {
        SwingUtilities.invokeLater(() -> {
            String text = getText().trim();
            popupMenu.removeAll();
            if (text.isEmpty()) {
                popupMenu.setVisible(false);
                return;
            }

            List<String> matches = dictionary.stream()
                    .filter(word -> word.toLowerCase().startsWith(text.toLowerCase()))
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                popupMenu.setVisible(false);
                return;
            }

            for (String match : matches) {
                JMenuItem item = new JMenuItem(match);
                item.addActionListener(e -> {
                    setText(match);
                    popupMenu.setVisible(false);
                });
                popupMenu.add(item);
            }

            if (!popupMenu.isVisible() && isShowing()) {
                popupMenu.show(this, 0, getHeight());
            }
            
            requestFocusInWindow();
        });
    }
}
