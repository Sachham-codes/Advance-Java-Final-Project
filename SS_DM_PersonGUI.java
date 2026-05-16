// Sachham Shrestha and Dimabh Singh Mohara 
// OCCC Advance Java final project (Person GUI) 

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class SS_DM_PersonGUI extends JFrame {

    // ── Data ────────────────────────────────────────────────────────────────
    private ArrayList<SS_DM_Person> personList = new ArrayList<>();
    private File    currentFile   = null;
    private boolean isDirty       = false;   
    //private boolean isConstructing = false;  

    // ── Menu items that must be toggled ─────────────────────────────────────
    private JMenuItem saveItem;
    private JMenuItem saveAsItem;

    // ── Main list ────────────────────────────────────────────────────────────
    private DefaultListModel<SS_DM_Person> listModel = new DefaultListModel<>();
    private JList<SS_DM_Person> personJList;
    private JLabel detailLabel;

    // ── Form panel (shown while constructing) ────────────────────────────────
    private JPanel    formPanel;
    private JComboBox<String> typeCombo;
    private JTextField fnField, lnField, extraField;
    private JLabel     extraLabel;
    // DOB spinner components (shown for OCCCPerson)
    private JPanel     dobPanel;
    private JSpinner   monthSpinner, daySpinner, yearSpinner;
    private JButton    confirmBtn, cancelBtn;

    // ── Constructor ──────────────────────────────────────────────────────────
    public SS_DM_PersonGUI() {
        setTitle("Person GUI — SS_DM");
        setSize(720, 520);
        setMinimumSize(new Dimension(600, 450));
        setLocationRelativeTo(null);

        // Override default close so we can prompt to save
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { exitApp(); }
        });

        buildMenuBar();
        buildMainPanel();
        setFormVisible(false);
        setVisible(true);
    }

     
    //  MENU BAR
     
    private void buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);

        // ── File ──
        JMenu fileMenu = new JMenu("File");
        bar.add(fileMenu);

        JMenuItem newItem    = new JMenuItem("New");
        JMenuItem openItem   = new JMenuItem("Open...");
        saveItem             = new JMenuItem("Save");
        saveAsItem           = new JMenuItem("Save As...");
        JMenuItem exitItem   = new JMenuItem("Exit");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        newItem  .addActionListener(e -> newList());
        openItem .addActionListener(e -> openFile());
        saveItem .addActionListener(e -> saveFile());
        saveAsItem.addActionListener(e -> saveAsFile());
        exitItem .addActionListener(e -> exitApp());

        // ── Help ──
        JMenu helpMenu = new JMenu("Help");
        bar.add(helpMenu);

        JMenuItem helpItem  = new JMenuItem("How to Use");
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);

        helpItem.addActionListener(e -> showHelp());
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "SS_DM Person GUI\nFinal Project — Java Swing\n\nManage Person, RegisteredPerson, and OCCCPerson records.",
                "About", JOptionPane.INFORMATION_MESSAGE));
    }

     
    //  MAIN PANEL
     
    private void buildMainPanel() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        // ── LEFT: list + action buttons ──────────────────────────────────
        JPanel leftPanel = new JPanel(new BorderLayout(4, 4));
        leftPanel.setPreferredSize(new Dimension(280, 0));

        personJList = new JList<>(listModel);
        personJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        personJList.setCellRenderer(new PersonCellRenderer());
        personJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetail();
        });

        JScrollPane scroll = new JScrollPane(personJList);
        scroll.setBorder(BorderFactory.createTitledBorder("People"));
        leftPanel.add(scroll, BorderLayout.CENTER);

        JPanel listBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        JButton addBtn    = new JButton("+ Add New");
        JButton deleteBtn = new JButton("✕ Delete");
        listBtns.add(addBtn);
        listBtns.add(deleteBtn);
        leftPanel.add(listBtns, BorderLayout.SOUTH);

        addBtn   .addActionListener(e -> startAddPerson());
        deleteBtn.addActionListener(e -> deletePerson());

        // ── RIGHT: detail + form ─────────────────────────────────────────
        JPanel rightPanel = new JPanel(new BorderLayout(4, 8));

        detailLabel = new JLabel("<html><i>Select a person to view details.</i></html>");
        detailLabel.setBorder(BorderFactory.createTitledBorder("Details"));
        detailLabel.setVerticalAlignment(SwingConstants.TOP);
        detailLabel.setPreferredSize(new Dimension(0, 80));
        rightPanel.add(detailLabel, BorderLayout.NORTH);

        formPanel = buildFormPanel();
        rightPanel.add(formPanel, BorderLayout.CENTER);

        // ── Assemble ─────────────────────────────────────────────────────
        root.add(leftPanel,  BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);
    }

     
    //  FORM PANEL (construction area)
     
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("New Person"));

        // Type selector
        JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typeRow.add(new JLabel("Type:"));
        typeCombo = new JComboBox<>(new String[]{"Person", "RegisteredPerson", "OCCCPerson"});
        typeCombo.addActionListener(e -> onTypeChanged());
        typeRow.add(typeCombo);
        panel.add(typeRow);

        // First name
        JPanel fnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fnRow.add(new JLabel("First Name:"));
        fnField = new JTextField(14);
        fnRow.add(fnField);
        panel.add(fnRow);

        // Last name
        JPanel lnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lnRow.add(new JLabel("Last Name: "));
        lnField = new JTextField(14);
        lnRow.add(lnField);
        panel.add(lnRow);

        // Extra field (gov ID for RegisteredPerson)
        JPanel extraRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        extraLabel = new JLabel("Gov't ID:  ");
        extraRow.add(extraLabel);
        extraField = new JTextField(14);
        extraRow.add(extraField);
        panel.add(extraRow);

        // DOB panel (OCCCPerson only)
        dobPanel = buildDobPanel();
        panel.add(dobPanel);

        // Confirm / Cancel
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        confirmBtn = new JButton("✔  Confirm");
        cancelBtn  = new JButton("✖  Cancel");
        confirmBtn.setBackground(new Color(60, 160, 60));
        confirmBtn.setForeground(Color.WHITE);
        cancelBtn .setBackground(new Color(200, 60, 60));
        cancelBtn .setForeground(Color.WHITE);
        btnRow.add(confirmBtn);
        btnRow.add(cancelBtn);
        panel.add(btnRow);

        confirmBtn.addActionListener(e -> confirmAdd());
        cancelBtn .addActionListener(e -> cancelAdd());

        return panel;
    }

    private JPanel buildDobPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Date of Birth:"));

        monthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        monthSpinner.setPreferredSize(new Dimension(52, 24));
        ((JSpinner.DefaultEditor) monthSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);

        daySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        daySpinner.setPreferredSize(new Dimension(52, 24));
        ((JSpinner.DefaultEditor) daySpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);

        yearSpinner = new JSpinner(new SpinnerNumberModel(2000, 1900, 2100, 1));
        yearSpinner.setPreferredSize(new Dimension(70, 24));
        ((JSpinner.DefaultEditor) yearSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "####"));

        // When month or year changes, clamp day to valid range
        ChangeListenerHelper clamp = () -> clampDay();
        monthSpinner.addChangeListener(e -> clamp.run());
        yearSpinner .addChangeListener(e -> clamp.run());

        panel.add(new JLabel("MM")); panel.add(monthSpinner);
        panel.add(new JLabel(" / DD")); panel.add(daySpinner);
        panel.add(new JLabel(" / YYYY")); panel.add(yearSpinner);
        return panel;
    }

    // Functional interface for the lambda above
    @FunctionalInterface interface ChangeListenerHelper { void run(); }

    private void clampDay() {
        int month = (Integer) monthSpinner.getValue();
        int year  = (Integer) yearSpinner.getValue();
        int max   = SS_DM_OCCCDate.daysInMonth(month, year);
        SpinnerNumberModel model = (SpinnerNumberModel) daySpinner.getModel();
        model.setMaximum(max);
        if ((Integer) daySpinner.getValue() > max) daySpinner.setValue(max);
    }

    private void onTypeChanged() {
        String type = (String) typeCombo.getSelectedItem();
        boolean isRegistered = "RegisteredPerson".equals(type);
        boolean isOCCC       = "OCCCPerson".equals(type);

        extraLabel.setText(isRegistered ? "Gov't ID:  " : "Extra:     ");
        extraField.setVisible(isRegistered);
        extraLabel.setVisible(isRegistered);
        dobPanel  .setVisible(isOCCC);
        formPanel.revalidate();
        formPanel.repaint();
    }

     
    //  ADD / CONFIRM / CANCEL
     
    private void startAddPerson() {
        fnField   .setText("");
        lnField   .setText("");
        extraField.setText("");
        typeCombo .setSelectedIndex(0);
        onTypeChanged();
        setFormVisible(true);
        setConstructing(true);
    }

    private void confirmAdd() {
        String fn   = fnField.getText().trim();
        String ln   = lnField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();

        if (fn.isEmpty() || ln.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First and last name are required.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SS_DM_Person person = null;

        if ("Person".equals(type)) {
            person = new SS_DM_Person(fn, ln);

        } else if ("RegisteredPerson".equals(type)) {
            String govID = extraField.getText().trim();
            if (govID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Government ID is required for a Registered Person.", "Missing Field", JOptionPane.WARNING_MESSAGE);
                return;
            }
            person = new SS_DM_RegisteredPerson(fn, ln, govID);

        } else if ("OCCCPerson".equals(type)) {
            int month = (Integer) monthSpinner.getValue();
            int day   = (Integer) daySpinner.getValue();
            int year  = (Integer) yearSpinner.getValue();
            try {
                SS_DM_OCCCDate dob = new SS_DM_OCCCDate(day, month, year);
                person = new SS_DM_OCCCPerson(fn, ln, dob);
            } catch (SS_DM_OCCCDateException ex) {
                // Clear spinners back to safe values and warn user
                monthSpinner.setValue(1);
                daySpinner  .setValue(1);
                yearSpinner .setValue(2000);
                JOptionPane.showMessageDialog(this, "Invalid Date!", "Date Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (person != null) {
            personList.add(person);
            listModel .addElement(person);
            personJList.setSelectedIndex(listModel.getSize() - 1);
            setFormVisible(false);
            setConstructing(false);
            markDirty();
        }
    }

    private void cancelAdd() {
        setFormVisible(false);
        setConstructing(false);
    }

     
    //  DELETE
     
    private void deletePerson() {
        int idx = personJList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Please select a person to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SS_DM_Person p = listModel.getElementAt(idx);
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete \"" + p + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            personList.remove(idx);
            listModel .remove(idx);
            detailLabel.setText("<html><i>Select a person to view details.</i></html>");
            markDirty();
        }
    }

     
    //  DETAIL DISPLAY
     
    private void showDetail() {
        int idx = personJList.getSelectedIndex();
        if (idx < 0) {
            detailLabel.setText("<html><i>Select a person to view details.</i></html>");
            return;
        }
        SS_DM_Person p = listModel.getElementAt(idx);
        String html = "<html><b>Type:</b> " + p.getTypeLabel()
                + "<br><b>Name:</b> " + p.getFirstName() + " " + p.getLastName();

        if (p instanceof SS_DM_RegisteredPerson) {
            html += "<br><b>Gov't ID:</b> " + ((SS_DM_RegisteredPerson) p).getGovernmentID();
        } else if (p instanceof SS_DM_OCCCPerson) {
            html += "<br><b>Date of Birth:</b> " + ((SS_DM_OCCCPerson) p).getDateOfBirth();
        }

        if (!p.getChildren().isEmpty()) {
            html += "<br><b>Children:</b> " + p.getChildren().size();
        }
        html += "</html>";
        detailLabel.setText(html);
    }

     
    //  FILE OPERATIONS
     
    private void newList() {
        if (!confirmDiscardChanges()) return;
        personList.clear();
        listModel .clear();
        currentFile = null;
        isDirty     = false;
        detailLabel.setText("<html><i>Select a person to view details.</i></html>");
        updateTitle();
    }

    private void openFile() {
        if (!confirmDiscardChanges()) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open Person File");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(chooser.getSelectedFile()))) {
                @SuppressWarnings("unchecked")
                ArrayList<SS_DM_Person> loaded = (ArrayList<SS_DM_Person>) ois.readObject();
                personList = loaded;
                refreshList();
                currentFile = chooser.getSelectedFile();
                isDirty     = false;
                updateTitle();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to open file:\n" + ex.getMessage(),
                        "Open Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        if (currentFile != null) {
            writeToFile(currentFile);
        } else {
            saveAsFile();
        }
    }

    private void saveAsFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Person File");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            // Auto-append .dat if no extension
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".dat");
            }
            currentFile = file;
            writeToFile(currentFile);
        }
    }

    private void writeToFile(File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(personList);
            isDirty = false;
            updateTitle();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save file:\n" + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

     
    //  EXIT
     
    private void exitApp() {
        if (isDirty) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes.\nSave before exit?",
                    "Save Before Exit?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) return;
            if (choice == JOptionPane.YES_OPTION) saveFile();
        }
        System.exit(0);
    }

     
    //  HELP
     
    private void showHelp() {
        String msg =
            "SS_DM Person GUI — Help\n\n" +
            "• File > New       : Start a fresh list (prompts to save if needed)\n" +
            "• File > Open...   : Load a saved file\n" +
            "• File > Save      : Save to the current file\n" +
            "• File > Save As...: Save to a new file\n" +
            "• File > Exit      : Quit (prompts to save if needed)\n\n" +
            "• '+ Add New'      : Opens the form to create a new Person\n" +
            "• '✕ Delete'       : Removes the selected person from the list\n\n" +
            "Person Types:\n" +
            "  Person            — First name, Last name\n" +
            "  RegisteredPerson  — + Government ID\n" +
            "  OCCCPerson        — + Date of Birth\n\n" +
            "Note: Save and Save As... are disabled while a person is being added.";
        JOptionPane.showMessageDialog(this, msg, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

     
    //  HELPERS
     
    /** Ask user if they want to save before discarding changes. Returns true = OK to proceed. */
    private boolean confirmDiscardChanges() {
        if (!isDirty) return true;
        int choice = JOptionPane.showConfirmDialog(this,
                "You have unsaved changes.\nSave before continuing?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) return false;
        if (choice == JOptionPane.YES_OPTION) saveFile();
        return true;
    }

    private void setConstructing(boolean constructing) {
        //isConstructing = constructing;
        saveItem  .setEnabled(!constructing);
        saveAsItem.setEnabled(!constructing);
    }

    private void setFormVisible(boolean visible) {
        formPanel.setVisible(visible);
        formPanel.getParent().revalidate();
        formPanel.getParent().repaint();
    }

    private void markDirty() {
        isDirty = true;
        updateTitle();
    }

    private void updateTitle() {
        String name = (currentFile != null) ? currentFile.getName() : "Untitled";
        setTitle("Person GUI — SS_DM  [" + name + "]" + (isDirty ? " *" : ""));
    }

    private void refreshList() {
        listModel.clear();
        for (SS_DM_Person p : personList) listModel.addElement(p);
        detailLabel.setText("<html><i>Select a person to view details.</i></html>");
    }

     
    //  CUSTOM LIST CELL RENDERER
     
    private static class PersonCellRenderer extends DefaultListCellRenderer {
        private static final Color COLOR_PERSON     = new Color(220, 235, 255);
        private static final Color COLOR_REGISTERED = new Color(220, 255, 220);
        private static final Color COLOR_OCCC       = new Color(255, 240, 210);

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof SS_DM_Person) {
                SS_DM_Person p = (SS_DM_Person) value;
                setText("<html><b>[" + p.getTypeLabel() + "]</b>  " + p.getFirstName() + " " + p.getLastName() + "</html>");
                if (!isSelected) {
                    if      (p instanceof SS_DM_OCCCPerson)       setBackground(COLOR_OCCC);
                    else if (p instanceof SS_DM_RegisteredPerson)  setBackground(COLOR_REGISTERED);
                    else                                            setBackground(COLOR_PERSON);
                }
                setBorder(new EmptyBorder(4, 6, 4, 6));
            }
            return this;
        }
    }

     
    //  MAIN
     
    public static void main(String[] args) {
        // Use system look and feel for a native appearance
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(SS_DM_PersonGUI::new);
    }
}
