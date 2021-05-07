package com.cburch.LogisimFX.newgui.PrintFrame;

import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.comp.ComponentDrawContext;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.util.StringUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class PrintController extends AbstractController {

    private Stage stage;

    @FXML
    private Label CircuitsLbl;

    @FXML
    private ListView<String> CircuitLstVw;

    @FXML
    private Label HeaderLbl;

    @FXML
    private TextField HeaderTxtFld;

    @FXML
    private Button OkBtn;

    @FXML
    private Button CancleBtn;

    private PrinterJob job;
    private Project proj;

    private ObservableList<String> circuits = FXCollections.observableArrayList("1","2","1","1","1","1","1","1","1");

    @FXML
    public void initialize(){

        CircuitsLbl.textProperty().bind(LC.createStringBinding("labelCircuits"));

        HeaderLbl.textProperty().bind(LC.createStringBinding("labelHeader"));
        HeaderTxtFld.setText("%n (%p of %P)");

        OkBtn.setText("Ok");
        OkBtn.setOnAction(event -> {
            pageSetup(OkBtn,stage);
        });

        CancleBtn.setText("Cancel");
        CancleBtn.setOnAction(event -> {
            stage.close();
        });

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;
        stage.titleProperty().bind(LC.createStringBinding("printParmsTitle"));
        stage.setHeight(300);
        stage.setWidth(300);

        proj = project;

        setCircuitList(true);

        if(circuits.size()==0){
            DialogManager.CreateErrorDialog( LC.get("printEmptyCircuitsTitle"), LC.get("printEmptyCircuitsMessage"));
        }


    }



    public void setCircuitList(boolean includeEmpty) {

        MultipleSelectionModel<String> langsSelectionModel = CircuitLstVw.getSelectionModel();
        langsSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

/*
        LogisimFile file = proj.getLogisimFile();
        Circuit current = proj.getCurrentCircuit();

        boolean currentFound = false;

        for (Circuit circ : file.getCircuits()) {
            if (!includeEmpty || circ.getBounds() != Bounds.EMPTY_BOUNDS) {
                if (circ == current) currentFound = true;
                //circuits.add(circ);
            }
        }

*/
        CircuitLstVw.setItems(circuits);

        //if (currentFound) CircuitLstVw.getSelectionModel().select(current);

    }




    private void pageSetup(Node node, Stage owner) {

        // Create the PrinterJob
        job = PrinterJob.createPrinterJob();

        if (job == null)
        {
            return;
        }

        // Show the print setup dialog
        boolean proceed = job.showPageSetupDialog(owner);

        if (proceed)
        {
            printSetup(node,stage);
        }

    }

    private void printSetup(Node node, Stage owner){

        if (job == null)
        {
            return;
        }

        // Show the print setup dialog
        boolean proceed = job.showPrintDialog(owner);

        if (proceed)
        {
            print(job, node);
        }

    }

    private void print(PrinterJob job, Node node) {

        // Print the node
        boolean printed = job.printPage(node);

        if (printed)
        {
            job.endJob();
        }

    }




    public void doPrint(Project proj) {

        CircuitJList list = new CircuitJList(proj, true);
        Frame frame = proj.getFrame();

        /*
        if (list.getModel().getSize() == 0) {
            JOptionPane.showMessageDialog(proj.getFrame(),
                    lc.get("printEmptyCircuitsMessage"),
                    lc.get("printEmptyCircuitsTitle"),
                    JOptionPane.YES_NO_OPTION);
            return;
        }

         */

        ParmsPanel parmsPanel = new ParmsPanel(list);

        int action = JOptionPane.showConfirmDialog(frame,
                parmsPanel, Strings.get("printParmsTitle"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (action != JOptionPane.OK_OPTION) return;
        List<Circuit> circuits = list.getSelectedCircuits();
        if (circuits.isEmpty()) return;

        PageFormat format = new PageFormat();
        Printable print = new MyPrintable(proj, circuits,
                parmsPanel.getHeader(),
                parmsPanel.getRotateToFit(),
                parmsPanel.getPrinterView());

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(print, format);
        if (job.printDialog() == false) return;
        try {
            job.print();
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(proj.getFrame(),
                    StringUtil.format(Strings.get("printError"), e.toString()),
                    Strings.get("printErrorTitle"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ParmsPanel extends JPanel {
        JCheckBox rotateToFit;
        JCheckBox printerView;
        JTextField header;
        GridBagLayout gridbag;
        GridBagConstraints gbc;

        ParmsPanel(JList list) {
            // set up components
            rotateToFit = new JCheckBox();
            rotateToFit.setSelected(true);
            printerView = new JCheckBox();
            printerView.setSelected(true);
            header = new JTextField(20);
            header.setText("%n (%p of %P)");

            // set up panel
            gridbag = new GridBagLayout();
            gbc = new GridBagConstraints();
            setLayout(gridbag);

            // now add components into panel
            gbc.gridy = 0;
            gbc.gridx = GridBagConstraints.RELATIVE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(5, 0, 5, 0);
            gbc.fill = GridBagConstraints.NONE;
            addGb(new JLabel(Strings.get("labelCircuits") + " "));
            gbc.fill = GridBagConstraints.HORIZONTAL;
            addGb(new JScrollPane(list));
            gbc.fill = GridBagConstraints.NONE;

            gbc.gridy++;
            addGb(new JLabel(Strings.get("labelHeader") + " "));
            addGb(header);

            gbc.gridy++;
            addGb(new JLabel(Strings.get("labelRotateToFit") + " "));
            addGb(rotateToFit);

            gbc.gridy++;
            addGb(new JLabel(Strings.get("labelPrinterView") + " "));
            addGb(printerView);
        }

        private void addGb(JComponent comp) {
            gridbag.setConstraints(comp, gbc);
            add(comp);
        }

        boolean getRotateToFit() { return rotateToFit.isSelected(); }
        boolean getPrinterView() { return printerView.isSelected(); }
        String getHeader() { return header.getText(); }
    }

    private static class MyPrintable implements Printable {
        Project proj;
        List<Circuit> circuits;
        String header;
        boolean rotateToFit;
        boolean printerView;

        MyPrintable(Project proj, List<Circuit> circuits, String header,
                    boolean rotateToFit, boolean printerView) {
            this.proj = proj;
            this.circuits = circuits;
            this.header = header;
            this.rotateToFit = rotateToFit;
            this.printerView = printerView;
        }

        public int print(Graphics base, PageFormat format, int pageIndex) {
            if (pageIndex >= circuits.size()) return Printable.NO_SUCH_PAGE;

            Circuit circ = circuits.get(pageIndex);
            CircuitState circState = proj.getCircuitState(circ);
            Graphics g = base.create();
            Graphics2D g2 = g instanceof Graphics2D ? (Graphics2D) g : null;
            FontMetrics fm = g.getFontMetrics();
            String head = (header != null && !header.equals(""))
                    ? format(header, pageIndex + 1, circuits.size(),
                    circ.getName())
                    : null;
            int headHeight = (head == null ? 0 : fm.getHeight());

            // Compute image size
            double imWidth = format.getImageableWidth();
            double imHeight = format.getImageableHeight();

            // Correct coordinate system for page, including
            // translation and possible rotation.
            Bounds bds = circ.getBounds(g).expand(4);
            double scale = Math.min(imWidth / bds.getWidth(),
                    (imHeight - headHeight) / bds.getHeight());
            if (g2 != null) {
                g2.translate(format.getImageableX(), format.getImageableY());
                if (rotateToFit && scale < 1.0 / 1.1) {
                    double scale2 = Math.min(imHeight / bds.getWidth(),
                            (imWidth - headHeight) / bds.getHeight());
                    if (scale2 >= scale * 1.1) { // will rotate
                        scale = scale2;
                        if (imHeight > imWidth) { // portrait -> landscape
                            g2.translate(0, imHeight);
                            g2.rotate(-Math.PI / 2);
                        } else { // landscape -> portrait
                            g2.translate(imWidth, 0);
                            g2.rotate(Math.PI / 2);
                        }
                        double t = imHeight;
                        imHeight = imWidth;
                        imWidth = t;
                    }
                }
            }

            // Draw the header line if appropriate
            if (head != null) {
                g.drawString(head,
                        (int) Math.round((imWidth - fm.stringWidth(head)) / 2),
                        fm.getAscent());
                if (g2 != null) {
                    imHeight -= headHeight;
                    g2.translate(0, headHeight);
                }
            }

            // Now change coordinate system for circuit, including
            // translation and possible scaling
            if (g2 != null) {
                if (scale < 1.0) {
                    g2.scale(scale, scale);
                    imWidth /= scale;
                    imHeight /= scale;
                }
                double dx = Math.max(0.0, (imWidth - bds.getWidth()) / 2);
                g2.translate(-bds.getX() + dx, -bds.getY());
            }

            // Ensure that the circuit is eligible to be drawn
            Rectangle clip = g.getClipBounds();
            clip.add(bds.getX(), bds.getY());
            clip.add(bds.getX() + bds.getWidth(),
                    bds.getY() + bds.getHeight());
            g.setClip(clip);

            // And finally draw the circuit onto the page
            ComponentDrawContext context = new ComponentDrawContext(
                    proj.getFrame().getCanvas(), circ, circState,
                    base, g, printerView);
            Collection<Component> noComps = Collections.emptySet();
            circ.draw(context, noComps);
            g.dispose();
            return Printable.PAGE_EXISTS;
        }
    }

    private static String format(String header, int index, int max,
                                 String circName) {
        int mark = header.indexOf('%');
        if (mark < 0) return header;
        StringBuilder ret = new StringBuilder();
        int start = 0;
        for (; mark >= 0 && mark + 1 < header.length();
             start = mark + 2, mark = header.indexOf('%', start)) {
            ret.append(header.substring(start, mark));
            switch (header.charAt(mark + 1)) {
                case 'n': ret.append(circName); break;
                case 'p': ret.append("" + index); break;
                case 'P': ret.append("" + max); break;
                case '%': ret.append("%"); break;
                default:  ret.append("%" + header.charAt(mark + 1));
            }
        }
        if (start < header.length()) {
            ret.append(header.substring(start));
        }
        return ret.toString();
    }




    @Override
    public void onClose() {
        System.out.println("Print closed");
    }


}
