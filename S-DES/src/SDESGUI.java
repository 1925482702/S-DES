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
    private JButton returnButton; // 添加返回按钮

    // 通用置换函数
    public static String permute(String inputStr, int[] table) {
        StringBuilder outputStr = new StringBuilder();
        for (int bitPosition : table) {
            outputStr.append(inputStr.charAt(bitPosition - 1));
        }
        return outputStr.toString();
    }

    // 循环左移函数
    public static String ls(String key, int n) {
        String leftHalf = key.substring(0, 5);
        String rightHalf = key.substring(5);
        String shiftedLeft = leftHalf.substring(n) + leftHalf.substring(0, n);
        String shiftedRight = rightHalf.substring(n) + rightHalf.substring(0, n);
        return shiftedLeft + shiftedRight;
    }

    // 子密钥生成
    public static String[] generateKey(String k, int[] p10Table, int[] p8Table) {
        // 执行 P10 置换
        String p10Key = permute(k, p10Table);
        // 对结果进行左移操作和 P8 置换，得到 K1
        String k1 = permute(ls(p10Key, 1), p8Table);
        // 再次对上一步结果进行左移操作和 P8 置换，得到 K2
        String k2 = permute(ls(ls(p10Key, 1), 1), p8Table);
        return new String[]{k1, k2};
    }

    // S-DES 的 F 函数
    public static String F(String rightHalf, String k, int[] epTable, int[][] sbox0, int[][] sbox1, int[] p4Table) {
        // 对右半部分进行 E/P 扩展置换
        String expanded = permute(rightHalf, epTable);
        // 对结果与 K1 进行异或操作
        int xored = Integer.parseInt(expanded, 2) ^ Integer.parseInt(k, 2);
        String xoredStr = String.format("%08d", Integer.parseInt(Integer.toBinaryString(xored)));
        // 将结果分为两组，并根据 S-box 进行替换
        String s0Input = xoredStr.substring(0, 4);
        String s1Input = xoredStr.substring(4);
        // 根据 S 盒规则行列查找
        int s0Row = Integer.parseInt(s0Input.charAt(0) + "" + s0Input.charAt(3), 2);
        int s0Col = Integer.parseInt(s0Input.substring(1, 3), 2);
        int s1Row = Integer.parseInt(s1Input.charAt(0) + "" + s1Input.charAt(3), 2);
        int s1Col = Integer.parseInt(s1Input.substring(1, 3), 2);
        String s0Output = String.format("%2s", Integer.toBinaryString(sbox0[s0Row][s0Col])).replace(' ', '0');
        String s1Output = String.format("%2s", Integer.toBinaryString(sbox1[s1Row][s1Col])).replace(' ', '0');
        // 对两个输出串进行 P4 置换得到最终结果
        String sOutput = permute(s0Output + s1Output, p4Table);
        return sOutput;
    }

    // 加密过程
    public static String encrypt(String p, String k1, String k2, int[] ipTable, int[] epTable, int[] ipNiTable, int[][] sbox0, int[][] sbox1, int[] p4Table) {
        // 执行初始置换
        p = permute(p, ipTable);
        // 进行两轮 Feistel 加密
        String l0 = p.substring(0, 4);
        String r0 = p.substring(4);
        String l1 = r0;
        // 第一轮的 P4
        String fResult = F(r0, k1, epTable, sbox0, sbox1, p4Table);
        // p4 和 L0 异或
        String r1 = String.format("%4s", Integer.toBinaryString(Integer.parseInt(l0, 2) ^ Integer.parseInt(fResult, 2))).replace(' ', '0');
        // 第二轮的 P4
        fResult = F(r1, k2, epTable, sbox0, sbox1, p4Table);
        // p4 和 L1 异或
        String r2 = String.format("%4s", Integer.toBinaryString(Integer.parseInt(l1, 2) ^ Integer.parseInt(fResult, 2))).replace(' ', '0');
        // 逆置换并返回结果（左边 R2 右边 R1）
        return permute(r2 + r1, ipNiTable);
    }

    // 解密过程
    public static String decrypt(String c, String k1, String k2, int[] ipTable, int[] epTable, int[] ipNiTable, int[][] sbox0, int[][] sbox1, int[] p4Table) {
        // 执行初始置换
        c = permute(c, ipTable);
        // 进行两轮 Feistel 解密（注意子密钥的使用顺序）
        String r2 = c.substring(0, 4);
        String l2 = c.substring(4);
        // 第一轮的 P4
        String fResult = F(l2, k2, epTable, sbox0, sbox1, p4Table);
        // p4 和 R2 异或
        String l1 = String.format("%4s", Integer.toBinaryString(Integer.parseInt(r2, 2) ^ Integer.parseInt(fResult, 2))).replace(' ', '0');
        // 第二轮的 P4
        fResult = F(l1, k1, epTable, sbox0, sbox1, p4Table);
        // p4 和 R1 异或
        String r1 = String.format("%4s", Integer.toBinaryString(Integer.parseInt(l2, 2) ^ Integer.parseInt(fResult, 2))).replace(' ', '0');
        // 逆置换并返回明文
        return permute(r1 + l1, ipNiTable);
    }

    public SDESGUI() {

        // 生成子密钥 K1 和 K2
        String[] keys = generateKey(key, p10Table, p8Table);
        String k1 = keys[0];
        String k2 = keys[1];

        // 对明文进行加密
        String ciphertext = encrypt(p, k1, k2, ipTable, epTable, ipNiTable, sbox0, sbox1, p4Table);

        // 对密文进行解密
        String plaintext = decrypt(ciphertext, k1, k2, ipTable, epTable, ipNiTable, sbox0, sbox1, p4Table);

        // 设置窗口标题和大小
        setTitle("S-DES 加密/解密");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 创建组件
        JLabel plaintextLabel = new JLabel("明文（8 位）:");
        JLabel keyLabel = new JLabel("密钥（10 位）:");
        JLabel ciphertextLabel = new JLabel("密文（8 位）:");

        Font labelFont = new Font(Font.DIALOG, Font.PLAIN, 18); // 设置字体大小

        plaintextLabel.setFont(labelFont);
        keyLabel.setFont(labelFont);
        ciphertextLabel.setFont(labelFont);

        plaintextField = new JTextField(30);
        keyField = new JTextField(30);
        ciphertextField = new JTextField(30);

        Font textFieldFont = new Font(Font.DIALOG, Font.PLAIN, 20); // 设置字体大小

        plaintextField.setFont(textFieldFont);
        keyField.setFont(textFieldFont);
        ciphertextField.setFont(textFieldFont);

        JButton encryptButton = new JButton("加密");
        JButton decryptButton = new JButton("解密");

        Font buttonFont = new Font(Font.DIALOG, Font.PLAIN, 16); // 设置字体大小

        encryptButton.setFont(buttonFont);
        decryptButton.setFont(buttonFont);

        // 添加组件到窗口中
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

        // 添加按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // 调整按钮之间的间距
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // 添加返回按钮
        returnButton = new JButton("返回");
        returnButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(returnButton, gbc);

        // 返回按钮的监听器
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

        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // 计算窗口位置
        int windowWidth = getWidth(); // 获取窗口宽度
        int windowHeight = getHeight(); // 获取窗口高度
        int x = (screenWidth - windowWidth) / 2; // 计算x坐标
        int y = (screenHeight - windowHeight) / 2; // 计算y坐标

        // 设置窗口位置
        setLocation(x, y);

        // 添加事件监听器
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String plaintext = plaintextField.getText();
                    String key = keyField.getText();

                    // 检查输入是否只包含0和1
                    if (!plaintext.matches("[01]+") || !key.matches("[01]+")) {
                        throw new IllegalArgumentException("请输入只包含0和1的二进制数字。");
                    }

                    if (plaintext.length() != 8) {
                        throw new IllegalArgumentException("明文必须为8位");
                    }
                    if (key.length() != 10) {
                        throw new IllegalArgumentException("密钥必须为10位");
                    }

                    // 执行加密
                    String[] keys = generateKey(key, p10Table, p8Table);
                    String k1 = keys[0];
                    String k2 = keys[1];
                    String ciphertext = encrypt(plaintext, k1, k2, ipTable, epTable, ipNiTable, sbox0, sbox1, p4Table);

                    // 设置密文框的内容
                    ciphertextField.setText(ciphertext);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(SDESGUI.this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String ciphertext = ciphertextField.getText();
                    String key = keyField.getText();

                    // 检查输入是否只包含0和1
                    if (!ciphertext.matches("[01]+") || !key.matches("[01]+")) {
                        throw new IllegalArgumentException("请输入只包含0和1的二进制数字。");
                    }

                    if (ciphertext.length() != 8) {
                        throw new IllegalArgumentException("密文必须为8位");
                    }
                    if (key.length() != 10) {
                        throw new IllegalArgumentException("密钥必须为10位");
                    }

                    // 执行解密
                    String[] keys = generateKey(key, p10Table, p8Table);
                    String k1 = keys[0];
                    String k2 = keys[1];
                    String plaintext = decrypt(ciphertext, k1, k2, ipTable, epTable, ipNiTable, sbox0, sbox1, p4Table);

                    // 设置明文框的内容
                    plaintextField.setText(plaintext);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(SDESGUI.this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}

class WelcomeGUI extends JFrame {

    public WelcomeGUI() {
        setTitle("欢迎界面");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 添加S-DES加密器标签
        JLabel titleLabel = new JLabel("S-DES加密器");
        titleLabel.setFont(new Font("宋体", Font.PLAIN, 48));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JButton crackButton = new JButton("密钥破解");
        JButton binaryButton = new JButton("加/解密"); // 修改按钮文字
        JButton AscllButton = new JButton("ASCLL");
        crackButton.setPreferredSize(new Dimension(150, 50));
        binaryButton.setPreferredSize(new Dimension(150, 50));
        AscllButton.setPreferredSize(new Dimension(150, 50));

        // 设置按钮字体大小
        crackButton.setFont(new Font("宋体", Font.PLAIN, 28)); // 调整字体大小
        binaryButton.setFont(new Font("宋体", Font.PLAIN, 28)); // 调整字体大小
        AscllButton.setFont(new Font("宋体", Font.PLAIN, 28)); // 调整字体大小

        // 添加按钮监听器
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

        // 添加按钮监听器
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

        // 添加按钮监听器
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

        // 创建按钮面板，并添加按钮
        JPanel buttonPanel = new JPanel(); // 调整行间距
        buttonPanel.add(crackButton);
        buttonPanel.add(binaryButton);
        buttonPanel.add(AscllButton);

        // 设置按钮面板在窗口中央位置
        add(buttonPanel, BorderLayout.CENTER);

        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // 计算窗口位置
        int windowWidth = getWidth(); // 获取窗口宽度
        int windowHeight = getHeight(); // 获取窗口高度
        int x = (screenWidth - windowWidth) / 2; // 计算x坐标
        int y = (screenHeight - windowHeight) / 2; // 计算y坐标

        // 设置窗口位置
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
    private JButton returnButton; // 添加返回按钮
    private JProgressBar progressBar;

    public CrackGUI() {
        setTitle("S-DES 密钥破解");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JLabel plaintextLabel = new JLabel("明文（8 位）:");
        JLabel ciphertextLabel = new JLabel("密文（8 位）:");
        JLabel possibleKeysLabel = new JLabel("可能的密钥:");

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

        crackButton = new JButton("破解");
        crackButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        returnButton = new JButton("返回"); // 添加返回按钮
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
        add(returnButton, gbc); // 将返回按钮添加到界面

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        int windowWidth = getWidth();
        int windowHeight = getHeight();
        int x = (screenWidth - windowWidth) / 2;
        int y = (screenHeight - windowHeight) / 2;

        setLocation(x, y);

        // 添加按钮和事件监听器
        crackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    progressBar.setValue(0);
                    // 获取输入的明文和密文
                    String plaintext = plaintextField.getText();
                    String ciphertext = ciphertextField.getText();

                    // 确保输入的明文和密文是合法的二进制数字
                    if (!plaintext.matches("[01]+") || !ciphertext.matches("[01]+")) {
                        throw new IllegalArgumentException("明文和密文请输入只包含0和1的二进制数字。");
                    }

                    if (plaintext.length() != 8 || ciphertext.length() != 8) {
                        throw new IllegalArgumentException("明文和密文必须为8位。");
                    }

                    progressBar.setIndeterminate(false);

                    // 创建线程池
                    ExecutorService executor = Executors.newFixedThreadPool(4);

                    // 存储可能的密钥
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

                    // 将可能的密钥输出到文本框
                    StringBuilder keysStringBuilder = new StringBuilder();
                    for (String key : possibleKeys) {
                        keysStringBuilder.append(key).append("\n");
                    }
                    possibleKeysArea.setText(keysStringBuilder.toString());

                } catch (IllegalArgumentException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(CrackGUI.this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        returnButton.addActionListener(new ActionListener() { // 返回按钮的监听器
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
        setTitle("ASCLL 加密/解密");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 明文输入框及标签
        JLabel plaintextLabel = new JLabel("明文:");
        plaintextLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        plaintextField = new JTextField(30);
        plaintextField.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        // 密钥输入框及标签
        JLabel keyLabel = new JLabel("密钥(10 位二进制数):");
        keyLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        keyField = new JTextField(30);
        keyField.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        // 密文输出框及标签
        JLabel ciphertextLabel = new JLabel("密文:");
        ciphertextLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        ciphertextField = new JTextField(30);
        ciphertextField.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        // 加密按钮
        encryptButton = new JButton("加密");
        encryptButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        // 解密按钮
        decryptButton = new JButton("解密");
        decryptButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        // 返回按钮
        returnButton = new JButton("返回");
        returnButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        // 设置布局
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加组件到窗口中
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

        // 添加按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // 调整按钮之间的间距
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // 返回按钮
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(returnButton, gbc);

        // 设置窗口位置
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int windowWidth = getWidth();
        int windowHeight = getHeight();
        int x = (screenWidth - windowWidth) / 2;
        int y = (screenHeight - windowHeight) / 2;
        setLocation(x, y);

        // 加密按钮事件监听器
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 获取输入
                    String plaintext = plaintextField.getText();
                    String key = keyField.getText();

                    // 检查明文是否在ASCLL码范围内
                    for (char c : plaintext.toCharArray()) {
                        if (c < 0 || c > 127) {
                            throw new IllegalArgumentException("明文必须为ASCLL码字符。");
                        }
                    }

                    // 检查密钥是否为10位二进制数
                    if (!key.matches("[01]{10}")) {
                        throw new IllegalArgumentException("密钥必须为10位二进制数。");
                    }

                    // 加密过程，将ASCLL字符转化为8位二进制数后进行加密
                    StringBuilder ciphertext = new StringBuilder();
                    for (char c : plaintext.toCharArray()) {
                        String binaryASCLL = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
                        String encrypted = SDESGUI.encrypt(binaryASCLL, key, key, SDESGUI.ipTable, SDESGUI.epTable, SDESGUI.ipNiTable, SDESGUI.sbox0, SDESGUI.sbox1, SDESGUI.p4Table);
                        int decimalValue = Integer.parseInt(encrypted, 2);
                        char asciiChar = (char) decimalValue;
                        ciphertext.append(asciiChar);
                    }

                    // 输出加密结果
                    ciphertextField.setText(ciphertext.toString());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AscllGUI.this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 解密按钮事件监听器
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 获取输入
                    String ciphertext = ciphertextField.getText();
                    String key = keyField.getText();

                    // 解密过程，将ASCLL字符转化为8位二进制数后进行解密
                    StringBuilder plaintext = new StringBuilder();
                    for (int i = 0; i < ciphertext.length(); i++) {
                        char ascllChar = ciphertext.charAt(i);
                        int ascllValue = (int) ascllChar;
                        String binaryASCLL = String.format("%8s", Integer.toBinaryString(ascllValue)).replace(' ', '0');
                        String decrypted = SDESGUI.decrypt(binaryASCLL, key, key, SDESGUI.ipTable, SDESGUI.epTable, SDESGUI.ipNiTable, SDESGUI.sbox0, SDESGUI.sbox1, SDESGUI.p4Table);
                        plaintext.append(decrypted);
                    }

                    // 输出解密结果
                    StringBuilder finalPlaintext = new StringBuilder();
                    for (int i = 0; i < plaintext.length(); i += 8) {
                        String binaryPlaintext = plaintext.substring(i, i + 8);
                        int intValue = Integer.parseInt(binaryPlaintext, 2);
                        char charValue = (char) intValue;
                        finalPlaintext.append(charValue);
                    }
                    plaintextField.setText(finalPlaintext.toString());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AscllGUI.this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 返回按钮事件监听器
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

