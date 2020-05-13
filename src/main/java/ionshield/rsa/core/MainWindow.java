package ionshield.rsa.core;


import ionshield.rsa.utils.BasicPrimeGen;
import ionshield.rsa.utils.RNG;
import ionshield.rsa.utils.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {
    private JPanel rootPanel;
    private JTextArea log;
    
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton saveButton;
    private JTextField fileNameField;
    private JButton loadButton;
    private JTextArea log2;
    private JTextArea extra;
    private JTextField qField;
    private JButton generatePQButton;
    private JTextField pField;
    private JTextField nField;
    private JTextField eField;
    private JTextField dField;
    private JTextField bitField;
    private JButton calculateNButton;
    private JButton calculateEDButton;
    
    private static String TITLE = "Compressor-2";
    
    private int precision = 3;
    
    private char[] buf;
    private RSAEncryption cr = new RSAEncryption();
    
    private MainWindow() {
        initComponents();
    }
    
    private void initComponents() {
        
        generatePQButton.addActionListener(ev -> {
            try {
                int bits = Integer.parseInt(bitField.getText());
                RNG<BigInteger> rng = new BasicPrimeGen();
                
                int l = bits / 2;
                int r = bits - l;
                
                BigInteger p = rng.getInRange(new BigDecimal(BigInteger.ONE.shiftLeft(l - 1)).multiply(BigDecimal.valueOf(Math.sqrt(2))).toBigInteger(), BigInteger.ONE.shiftLeft(l).subtract(BigInteger.ONE));
                BigInteger q = rng.getInRange(new BigDecimal(BigInteger.ONE.shiftLeft(r - 1)).multiply(BigDecimal.valueOf(Math.sqrt(2))).toBigInteger(), BigInteger.ONE.shiftLeft(r).subtract(BigInteger.ONE));
                
                pField.setText(p.toString());
                qField.setText(q.toString());
                
            } catch(NumberFormatException e) {
                log2.append("Invalid number format" + System.lineSeparator());
            }
        });
        
        calculateNButton.addActionListener(ev -> {
            try {
                BigInteger p = new BigInteger(pField.getText());
                BigInteger q = new BigInteger(qField.getText());
                nField.setText(p.multiply(q).toString());
            } catch(NumberFormatException e) {
                log2.append("Invalid number format" + System.lineSeparator());
            }
        });
        
        calculateEDButton.addActionListener(ev -> {
            try {
                BigInteger p = new BigInteger(pField.getText());
                BigInteger q = new BigInteger(qField.getText());
                BigInteger n = new BigInteger(nField.getText());
    
                BigInteger pm = p.subtract(BigInteger.ONE);
                BigInteger qm = q.subtract(BigInteger.ONE);
                
                BigInteger l = (pm.multiply(qm)).divide(pm.gcd(qm));
                
                RNG<BigInteger> rng = new BasicPrimeGen();
                BigInteger e = rng.getInRange(BigInteger.ONE, l);
                while (e.gcd(l).compareTo(BigInteger.ONE) != 0)  {
                    e = rng.getInRange(BigInteger.ONE, l);
                }
                
                BigInteger d = Utils.inverseMod(e, l);
                
                eField.setText(e.toString());
                dField.setText(d.toString());
                
            } catch(NumberFormatException e) {
                log2.append("Invalid number format" + System.lineSeparator());
            }
        });
        
        loadButton.addActionListener(ev -> {
            loadFile();
            log2.setText("");
            StringBuilder sb = new StringBuilder();
            
            log.setText(new String(buf));
            log2.append("File size: " + buf.length + System.lineSeparator());
            //updateGraph();
        });
        
        encryptButton.addActionListener(ev -> {
            try {
                if (buf == null) {
                    throw new IllegalArgumentException("No file selected");
                }
                int prevSize = buf.length;
    
                BigInteger e = new BigInteger(eField.getText());
                BigInteger d = new BigInteger(dField.getText());
                BigInteger n = new BigInteger(nField.getText());
                int bitLength = Integer.parseInt(bitField.getText());
                
                cr.setE(e);
                cr.setD(d);
                cr.setN(n);
                cr.setBitLength(bitLength);
                
                buf = Utils.bytesToChars(cr.encrypt(Utils.charsToBytes(buf)));
                
                
                log.setText(new String(buf));
                log2.append("File size: " + buf.length + System.lineSeparator());
            }
            catch (IllegalArgumentException ex) {
                log2.append(ex.getMessage() + System.lineSeparator());
            }
        });
    
        decryptButton.addActionListener(ev -> {
            try {
                if (buf == null) {
                    throw new IllegalArgumentException("No file selected");
                }
                int prevSize = buf.length;
    
                BigInteger e = new BigInteger(eField.getText());
                BigInteger d = new BigInteger(dField.getText());
                BigInteger n = new BigInteger(nField.getText());
                int bitLength = Integer.parseInt(bitField.getText());
    
                cr.setE(e);
                cr.setD(d);
                cr.setN(n);
                cr.setBitLength(bitLength);
            
                buf = Utils.bytesToChars(cr.decrypt(Utils.charsToBytes(buf)));
                
                log.setText(new String(buf));
                log2.append("File size: " + buf.length + System.lineSeparator());
            }
            catch (IllegalArgumentException ex) {
                log2.append(ex.getMessage() + System.lineSeparator());
            }
        });
        
        saveButton.addActionListener(e -> saveFile());
    }
    
    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "txt",  "txt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(rootPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(file));
    
                char[] buf = new char[(int)file.length()];
                reader.read(buf);
                
                this.buf = buf;
                fileNameField.setText(file.getName());
                reader.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void saveFile() {
        if (buf == null || buf.length == 0) return;
        JFileChooser chooser = new JFileChooser();
        /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "IMG & TXT", "img", "txt");
        chooser.setFileFilter(filter);*/
        int returnVal = chooser.showSaveDialog(rootPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(buf);
                
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    /*private void updateGraph() {
    
        try {
            image = Utils.readImage(lines);
            graph.setImage(image);
        } catch (IllegalArgumentException e) {
            log2.append(e.getMessage() + System.lineSeparator());
        }
        
        graph.repaint();
    }*/
    
    
    public static void main(String[] args) {
        JFrame frame = new JFrame(TITLE);
        MainWindow gui = new MainWindow();
        frame.setContentPane(gui.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
