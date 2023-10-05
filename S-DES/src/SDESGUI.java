import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
public class SDESGUI extends JFrame {
    public static String key = "1010101010";
    public static int[] p10Table = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    public static int[] p8Table = {6, 3, 7, 4, 8, 5, 10, 9};
    public static int[] p4Table = {2, 4, 3, 1};
    public static String p = "11110000";
    public static int[] ipTable = {2, 6, 3, 1, 4, 8, 5, 7};
    public static int[] epTable = {4, 1, 2, 3, 2, 3, 4, 1};
    public static int[] ipNiTable = {4, 1, 3, 5, 7, 2, 8, 6};
    public static int[][] sbox0 = {
            {1, 0, 3, 2},
            {3, 2, 1, 0},
            {0, 2, 1, 3},
            {3, 1, 0, 2}
    };
    public static int[][] sbox1 = {
            {0, 1, 2, 3},
            {2, 3, 1, 0},
            {3, 0, 1, 2},
            {2, 1, 0, 3}
    };

    private JTextField plaintextField, keyField, ciphertextField;
    private JButton returnButton; // ��ӷ��ذ�ť

    // ͨ���û�����
    public static String permute(String inputStr, int[] table) {
        StringBuilder outputStr = new StringBuilder();
        for (int bitPosition : table) {
            outputStr.append(inputStr.charAt(bitPosition - 1));
        }
        return outputStr.toString();
    }

    // ѭ�����ƺ���
    public static String ls(String key, int n) {
        String leftHalf = key.substring(0, 5);
        String rightHalf = key.substring(5);
        String shiftedLeft = leftHalf.substring(n) + leftHalf.substring(0, n);
        String shiftedRight = rightHalf.substring(n) + rightHalf.substring(0, n);
        return shiftedLeft + shiftedRight;
    }

    // ����Կ����
    public static String[] generateKey(String k, int[] p10Table, int[] p8Table) {
        // ִ�� P10 �û�
        String p10Key = permute(k, p10Table);
        // �Խ���������Ʋ����� P8 �û����õ� K1
        String k1 = permute(ls(p10Key, 1), p8Table);
        // �ٴζ���һ������������Ʋ����� P8 �û����õ� K2
        String k2 = permute(ls(ls(p10Key, 1), 1), p8Table);
        return new String[]{k1, k2};
    }

    // S-DES �� F ����
    public static String F(String rightHalf, String k, int[] epTable, int[][] sbox0, int[][] sbox1, int[] p4Table) {
        // ���Ұ벿�ֽ��� E/P ��չ�û�
        String expanded = permute(rightHalf, epTable);
        // �Խ���� K1 ����������
        int xored = Integer.parseInt(expanded, 2) ^ Integer.parseInt(k, 2);
        String xoredStr = String.format("%08d", Integer.parseInt(Integer.toBinaryString(xored)));
        // �������Ϊ���飬������ S-box �����滻
        String s0Input = xoredStr.substring(0, 4);
        String s1Input = xoredStr.substring(4);
        // ���� S �й������в���
        int s0Row = Integer.parseInt(s0Input.charAt(0) + "" + s0Input.charAt(3), 2);
        int s0Col = Integer.parseInt(s0Input.substring(1, 3), 2);
        int s1Row = Integer.parseInt(s1Input.charAt(0) + "" + s1Input.charAt(3), 2);
        int s1Col = Integer.parseInt(s1Input.substring(1, 3), 2);
        String s0Output = String.format("%2s", Integer.toBinaryString(sbox0[s0Row][s0Col])).replace(' ', '0');
        String s1Output = String.format("%2s", Integer.toBinaryString(sbox1[s1Row][s1Col])).replace(' ', '0');
        // ��������������� P4 �û��õ����ս��
        String sOutput = permute(s0Output + s1Output, p4Table);
        return sOutput;
    }

    // ���ܹ���
    public static String encrypt(String p, String k1, String k2, int[] ipTable, int[] epTable, int[] ipNiTable, int[][] sbox0, int[][] sbox1, int[] p4Table) {
        // ִ�г�ʼ�û�
        p = permute(p, ipTable);
        // �������� Feistel ����
        String l0 = p.substring(0, 4);
        String r0 = p.substring(4);
        String l1 = r0;
        // ��һ�ֵ� P4
        String fResult = F(r0, k1, epTable, sbox0, sbox1, p4Table);
        // p4 �� L0 ���
        String r1 = String.format("%4s", Integer.toBinaryString(Integer.parseInt(l0, 2) ^ Integer.parseInt(fResult, 2))).replace(' ', '0');
        // �ڶ��ֵ� P4
        fResult = F(r1, k2, epTable, sbox0, sbox1, p4Table);
        // p4 �� L1 ���
        String r2 = String.format("%4s", Integer.toBinaryString(Integer.parseInt(l1, 2) ^ Integer.parseInt(fResult, 2))).replace(' ', '0');
        // ���û������ؽ������� R2 �ұ� R1��
        return permute(r2 + r1, ipNiTable);
    }

    // ���ܹ���
    public static String decrypt(String c, String k1, String k2, int[] ipTable, int[] epTable, int[] ipNiTable, int[][] sbox0, int[][] sbox1, int[] p4Table) {
        // ִ�г�ʼ�û�
        c = permute(c, ipTable);
        // �������� Feistel ���ܣ�ע������Կ��ʹ��˳��
        String r2 = c.substring(0, 4);
        String l2 = c.substring(4);
        // ��һ�ֵ� P4
        String fResult = F(l2, k2, epTable, sbox0, sbox1, p4Table);
        // p4 �� R2 ���
        String l1 = String.format("%4s", Integer.toBinaryString(Integer.parseInt(r2, 2) ^ Integer.parseInt(fResult, 2))).replace(' ', '0');
        // �ڶ��ֵ� P4
        fResult = F(l1, k1, epTable, sbox0, sbox1, p4Table);
        // p4 �� R1 ���
        String r1 = String.format("%4s", Integer.toBinaryString(Integer.parseInt(l2, 2) ^ Integer.parseInt(fResult, 2))).replace(' ', '0');
        // ���û�����������
        return permute(r1 + l1, ipNiTable);
    }

    public SDESGUI() {

        // ��������Կ K1 �� K2
        String[] keys = generateKey(key, p10Table, p8Table);
        String k1 = keys[0];
        String k2 = keys[1];

        // �����Ľ��м���
        String ciphertext = encrypt(p, k1, k2, ipTable, epTable, ipNiTable, sbox0, sbox1, p4Table);

        // �����Ľ��н���
        String plaintext = decrypt(ciphertext, k1, k2, ipTable, epTable, ipNiTable, sbox0, sbox1, p4Table);

        // ���ô��ڱ���ʹ�С
        setTitle("S-DES ����/����");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // �������
        JLabel plaintextLabel = new JLabel("���ģ�8 λ��:");
        JLabel keyLabel = new JLabel("��Կ��10 λ��:");
        JLabel ciphertextLabel = new JLabel("���ģ�8 λ��:");

        Font labelFont = new Font(Font.DIALOG, Font.PLAIN, 18); // ���������С

        plaintextLabel.setFont(labelFont);
        keyLabel.setFont(labelFont);
        ciphertextLabel.setFont(labelFont);

        plaintextField = new JTextField(30);
        keyField = new JTextField(30);
        ciphertextField = new JTextField(30);

        Font textFieldFont = new Font(Font.DIALOG, Font.PLAIN, 20); // ���������С

        plaintextField.setFont(textFieldFont);
        keyField.setFont(textFieldFont);
        ciphertextField.setFont(textFieldFont);

        JButton encryptButton = new JButton("����");
        JButton decryptButton = new JButton("����");

        Font buttonFont = new Font(Font.DIALOG, Font.PLAIN, 16); // ���������С

        encryptButton.setFont(buttonFont);
        decryptButton.setFont(buttonFont);

        // ��������������
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(plaintextLabel, gbc);

        gbc.gridx = 1;
        add(plaintextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(keyLabel, gbc);

        gbc.gridx = 1;
        add(keyField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(ciphertextLabel, gbc);

        gbc.gridx = 1;
        add(ciphertextField, gbc);

        // ��Ӱ�ť
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // ������ť֮��ļ��
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // ��ӷ��ذ�ť
        returnButton = new JButton("����");
        returnButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(returnButton, gbc);

        // ���ذ�ť�ļ�����
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new WelcomeGUI().setVisible(true);
                    }
                });
            }
        });

        // ��ȡ��Ļ�ߴ�
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // ���㴰��λ��
        int windowWidth = getWidth(); // ��ȡ���ڿ��
        int windowHeight = getHeight(); // ��ȡ���ڸ߶�
        int x = (screenWidth - windowWidth) / 2; // ����x����
        int y = (screenHeight - windowHeight) / 2; // ����y����

        // ���ô���λ��
        setLocation(x, y);

        // ����¼�������
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String plaintext = plaintextField.getText();
                    String key = keyField.getText();

                    // ��������Ƿ�ֻ����0��1
                    if (!plaintext.matches("[01]+") || !key.matches("[01]+")) {
                        throw new IllegalArgumentException("������ֻ����0��1�Ķ��������֡�");
                    }

                    if (plaintext.length() != 8) {
                        throw new IllegalArgumentException("���ı���Ϊ8λ");
                    }
                    if (key.length() != 10) {
                        throw new IllegalArgumentException("��Կ����Ϊ10λ");
                    }

                    // ִ�м���
                    String[] keys = generateKey(key, p10Table, p8Table);
                    String k1 = keys[0];
                    String k2 = keys[1];
                    String ciphertext = encrypt(plaintext, k1, k2, ipTable, epTable, ipNiTable, sbox0, sbox1, p4Table);

                    // �������Ŀ������
                    ciphertextField.setText(ciphertext);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(SDESGUI.this, ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String ciphertext = ciphertextField.getText();
                    String key = keyField.getText();

                    // ��������Ƿ�ֻ����0��1
                    if (!ciphertext.matches("[01]+") || !key.matches("[01]+")) {
                        throw new IllegalArgumentException("������ֻ����0��1�Ķ��������֡�");
                    }

                    if (ciphertext.length() != 8) {
                        throw new IllegalArgumentException("���ı���Ϊ8λ");
                    }
                    if (key.length() != 10) {
                        throw new IllegalArgumentException("��Կ����Ϊ10λ");
                    }

                    // ִ�н���
                    String[] keys = generateKey(key, p10Table, p8Table);
                    String k1 = keys[0];
                    String k2 = keys[1];
                    String plaintext = decrypt(ciphertext, k1, k2, ipTable, epTable, ipNiTable, sbox0, sbox1, p4Table);

                    // �������Ŀ������
                    plaintextField.setText(plaintext);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(SDESGUI.this, ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}

class WelcomeGUI extends JFrame {

    public WelcomeGUI() {
        setTitle("��ӭ����");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // ���S-DES��������ǩ
        JLabel titleLabel = new JLabel("S-DES������");
        titleLabel.setFont(new Font("����", Font.PLAIN, 48));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JButton crackButton = new JButton("��Կ�ƽ�");
        JButton binaryButton = new JButton("��/����"); // �޸İ�ť����
        JButton AscllButton = new JButton("ASCLL");
        crackButton.setPreferredSize(new Dimension(150, 50));
        binaryButton.setPreferredSize(new Dimension(150, 50));
        AscllButton.setPreferredSize(new Dimension(150, 50));

        // ���ð�ť�����С
        crackButton.setFont(new Font("����", Font.PLAIN, 28)); // ���������С
        binaryButton.setFont(new Font("����", Font.PLAIN, 28)); // ���������С
        AscllButton.setFont(new Font("����", Font.PLAIN, 28)); // ���������С

        // ��Ӱ�ť������
        crackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new CrackGUI().setVisible(true);
                    }
                });
            }
        });

        // ��Ӱ�ť������
        binaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new SDESGUI().setVisible(true);
                    }
                });
            }
        });

        // ��Ӱ�ť������
        AscllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new AscllGUI().setVisible(true);
                    }
                });
            }
        });

        // ������ť��壬����Ӱ�ť
        JPanel buttonPanel = new JPanel(); // �����м��
        buttonPanel.add(crackButton);
        buttonPanel.add(binaryButton);
        buttonPanel.add(AscllButton);

        // ���ð�ť����ڴ�������λ��
        add(buttonPanel, BorderLayout.CENTER);

        // ��ȡ��Ļ�ߴ�
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // ���㴰��λ��
        int windowWidth = getWidth(); // ��ȡ���ڿ��
        int windowHeight = getHeight(); // ��ȡ���ڸ߶�
        int x = (screenWidth - windowWidth) / 2; // ����x����
        int y = (screenHeight - windowHeight) / 2; // ����y����

        // ���ô���λ��
        setLocation(x, y);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WelcomeGUI().setVisible(true);
            }
        });
    }
}

class CrackGUI extends JFrame {
    private JTextField plaintextField, ciphertextField;
    private JTextArea possibleKeysArea;
    private JButton crackButton;
    private JButton returnButton; // ��ӷ��ذ�ť
    private JProgressBar progressBar;

    public CrackGUI() {
        setTitle("S-DES ��Կ�ƽ�");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JLabel plaintextLabel = new JLabel("���ģ�8 λ��:");
        JLabel ciphertextLabel = new JLabel("���ģ�8 λ��:");
        JLabel possibleKeysLabel = new JLabel("���ܵ���Կ:");

        Font labelFont = new Font(Font.DIALOG, Font.PLAIN, 18);

        plaintextLabel.setFont(labelFont);
        ciphertextLabel.setFont(labelFont);
        possibleKeysLabel.setFont(labelFont);

        plaintextField = new JTextField(30);
        ciphertextField = new JTextField(30);
        possibleKeysArea = new JTextArea(10, 20);
        possibleKeysArea.setEditable(false);
        JScrollPane possibleKeysScrollPane = new JScrollPane(possibleKeysArea);

        Font textFieldFont = new Font(Font.DIALOG, Font.PLAIN, 20);

        plaintextField.setFont(textFieldFont);
        ciphertextField.setFont(textFieldFont);

        crackButton = new JButton("�ƽ�");
        crackButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        returnButton = new JButton("����"); // ��ӷ��ذ�ť
        returnButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(plaintextLabel, gbc);

        gbc.gridx = 1;
        add(plaintextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(ciphertextLabel, gbc);

        gbc.gridx = 1;
        add(ciphertextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(possibleKeysLabel, gbc);

        gbc.gridx = 1;
        add(possibleKeysScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(crackButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(progressBar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(returnButton, gbc); // �����ذ�ť��ӵ�����

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        int windowWidth = getWidth();
        int windowHeight = getHeight();
        int x = (screenWidth - windowWidth) / 2;
        int y = (screenHeight - windowHeight) / 2;

        setLocation(x, y);

        // ��Ӱ�ť���¼�������
        crackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    progressBar.setValue(0);
                    // ��ȡ��������ĺ�����
                    String plaintext = plaintextField.getText();
                    String ciphertext = ciphertextField.getText();

                    // ȷ����������ĺ������ǺϷ��Ķ���������
                    if (!plaintext.matches("[01]+") || !ciphertext.matches("[01]+")) {
                        throw new IllegalArgumentException("���ĺ�����������ֻ����0��1�Ķ��������֡�");
                    }

                    if (plaintext.length() != 8 || ciphertext.length() != 8) {
                        throw new IllegalArgumentException("���ĺ����ı���Ϊ8λ��");
                    }

                    progressBar.setIndeterminate(false);

                    // �����̳߳�
                    ExecutorService executor = Executors.newFixedThreadPool(4);

                    // �洢���ܵ���Կ
                    List<String> possibleKeys = Collections.synchronizedList(new ArrayList<>());

                    for (int i = 0; i < 1024; i++) {
                        final int index = i;
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                String key = String.format("%10s", Integer.toBinaryString(index)).replace(' ', '0');
                                String[] keys = SDESGUI.generateKey(key, SDESGUI.p10Table, SDESGUI.p8Table);
                                String k1 = keys[0];
                                String k2 = keys[1];

                                String guessedPlaintext = SDESGUI.decrypt(ciphertext, k1, k2, SDESGUI.ipTable, SDESGUI.epTable, SDESGUI.ipNiTable, SDESGUI.sbox0, SDESGUI.sbox1, SDESGUI.p4Table);

                                if (guessedPlaintext.equals(plaintext)) {
                                    possibleKeys.add(key);
                                }

                                int progress = (index * 100) / 1024;
                                progressBar.setValue(progress);
                            }
                        });
                    }

                    executor.shutdown();
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

                    progressBar.setValue(100);

                    // �����ܵ���Կ������ı���
                    StringBuilder keysStringBuilder = new StringBuilder();
                    for (String key : possibleKeys) {
                        keysStringBuilder.append(key).append("\n");
                    }
                    possibleKeysArea.setText(keysStringBuilder.toString());

                } catch (IllegalArgumentException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(CrackGUI.this, ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        returnButton.addActionListener(new ActionListener() { // ���ذ�ť�ļ�����
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new WelcomeGUI().setVisible(true);
                    }
                });
            }
        });
    }
}

class AscllGUI extends JFrame {
    private JTextField plaintextField, keyField, ciphertextField;
    private JButton encryptButton, decryptButton, returnButton;

    public AscllGUI() {
        setTitle("ASCLL ����/����");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // ��������򼰱�ǩ
        JLabel plaintextLabel = new JLabel("����:");
        plaintextLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        plaintextField = new JTextField(30);
        plaintextField.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        // ��Կ����򼰱�ǩ
        JLabel keyLabel = new JLabel("��Կ(10 λ��������):");
        keyLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        keyField = new JTextField(30);
        keyField.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        // ��������򼰱�ǩ
        JLabel ciphertextLabel = new JLabel("����:");
        ciphertextLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        ciphertextField = new JTextField(30);
        ciphertextField.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        // ���ܰ�ť
        encryptButton = new JButton("����");
        encryptButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        // ���ܰ�ť
        decryptButton = new JButton("����");
        decryptButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        // ���ذ�ť
        returnButton = new JButton("����");
        returnButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        // ���ò���
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // ��������������
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(plaintextLabel, gbc);

        gbc.gridx = 1;
        add(plaintextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(keyLabel, gbc);

        gbc.gridx = 1;
        add(keyField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(ciphertextLabel, gbc);

        gbc.gridx = 1;
        add(ciphertextField, gbc);

        // ��Ӱ�ť
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // ������ť֮��ļ��
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // ���ذ�ť
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(returnButton, gbc);

        // ���ô���λ��
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int windowWidth = getWidth();
        int windowHeight = getHeight();
        int x = (screenWidth - windowWidth) / 2;
        int y = (screenHeight - windowHeight) / 2;
        setLocation(x, y);

        // ���ܰ�ť�¼�������
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // ��ȡ����
                    String plaintext = plaintextField.getText();
                    String key = keyField.getText();

                    // ��������Ƿ���ASCLL�뷶Χ��
                    for (char c : plaintext.toCharArray()) {
                        if (c < 0 || c > 127) {
                            throw new IllegalArgumentException("���ı���ΪASCLL���ַ���");
                        }
                    }

                    // �����Կ�Ƿ�Ϊ10λ��������
                    if (!key.matches("[01]{10}")) {
                        throw new IllegalArgumentException("��Կ����Ϊ10λ����������");
                    }

                    // ���ܹ��̣���ASCLL�ַ�ת��Ϊ8λ������������м���
                    StringBuilder ciphertext = new StringBuilder();
                    for (char c : plaintext.toCharArray()) {
                        String binaryASCLL = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
                        String encrypted = SDESGUI.encrypt(binaryASCLL, key, key, SDESGUI.ipTable, SDESGUI.epTable, SDESGUI.ipNiTable, SDESGUI.sbox0, SDESGUI.sbox1, SDESGUI.p4Table);
                        int decimalValue = Integer.parseInt(encrypted, 2);
                        char asciiChar = (char) decimalValue;
                        ciphertext.append(asciiChar);
                    }

                    // ������ܽ��
                    ciphertextField.setText(ciphertext.toString());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AscllGUI.this, ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // ���ܰ�ť�¼�������
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // ��ȡ����
                    String ciphertext = ciphertextField.getText();
                    String key = keyField.getText();

                    // ���ܹ��̣���ASCLL�ַ�ת��Ϊ8λ������������н���
                    StringBuilder plaintext = new StringBuilder();
                    for (int i = 0; i < ciphertext.length(); i++) {
                        char ascllChar = ciphertext.charAt(i);
                        int ascllValue = (int) ascllChar;
                        String binaryASCLL = String.format("%8s", Integer.toBinaryString(ascllValue)).replace(' ', '0');
                        String decrypted = SDESGUI.decrypt(binaryASCLL, key, key, SDESGUI.ipTable, SDESGUI.epTable, SDESGUI.ipNiTable, SDESGUI.sbox0, SDESGUI.sbox1, SDESGUI.p4Table);
                        plaintext.append(decrypted);
                    }

                    // ������ܽ��
                    StringBuilder finalPlaintext = new StringBuilder();
                    for (int i = 0; i < plaintext.length(); i += 8) {
                        String binaryPlaintext = plaintext.substring(i, i + 8);
                        int intValue = Integer.parseInt(binaryPlaintext, 2);
                        char charValue = (char) intValue;
                        finalPlaintext.append(charValue);
                    }
                    plaintextField.setText(finalPlaintext.toString());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AscllGUI.this, ex.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // ���ذ�ť�¼�������
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new WelcomeGUI().setVisible(true);
                    }
                });
            }
        });
    }
}

