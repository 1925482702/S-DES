# ��Ŀ�������

����Ŀȫ��ʹ�� **java+swing**�淶����ʵ�֣����ݰ������װ�DES�㷨��S-DES����ԭ��ʵ�֣���������������Կ�ļ��ܹ��̡�����������Կ�Ľ��ܹ��̡������������ĵ���Կ�ƽ���̣���������ơ����̵߳�Ӧ�õȡ�

## ����ն��
### ��һ�� ��������
��ӭ����<br>
#### ������Ŀ�����Լ�������ҳ��İ�����ڣ������ť���ɽ�����Ӧ����
![img.png](img.png)<br>

����/���ܽ���<br>
#### �����а�λ���Ŀ�ʮλ��Կ�򡢰�λ���Ŀ�
![img_1.png](img_1.png)<br>

#### �����λ���ģ�ʮλ��Կʱ��������ܰ�ť���������ɶ�Ӧ����
![img_2.png](img_2.png)![img_3.png](img_3.png)

#### �����λ���ģ�ʮλ��Կʱ��������ܰ�ť���������ɶ�Ӧ����
![img_4.png](img_4.png)![img_5.png](img_5.png)

#### ��������ڼ���λ������ȷ����ַǶ�������ʱ������
![img_6.png](img_6.png)![img_7.png](img_7.png)![img_8.png](img_8.png)![img_11.png](img_11.png)

### �ڶ��� �������
#### ʹ�õĲ��Զԣ�����00100100����Կ1101101101
����������ļ��ܽ��
![img_13.png](img_13.png)
��֤������ļ��ܽ��
![img_14.png](img_14.png)
���Կ������ܽ����һ�µ�

### ������ ��չ����
#### �����ڼ���ascll�룬���ַ��� "test"Ϊ������ͼ
![img_19.png](img_19.png)![img_17.png](img_17.png)
#### ����ʱͬ��
![img_18.png](img_18.png)![img_21.png](img_21.png)

### ���Ĺ� �����ƽ�
#### ����һ�������Ķԣ�ʹ�ñ����ƽ�ķ�ʽ�����Կ������ʹ���˶��̵߳ķ�ʽ�����ں������벿���ᵽ
![img_23.png](img_23.png)![img_24.png](img_24.png)

### ����� ��ղ���
#### �ɵ��Ĺؿ�֪������ͬһ�������Ķԣ���Կ���ܲ�ֹһ�ѡ������ڶ���ͬһ�����ģ���ͬ����Կ���ܵõ���������ͬ�����

# �����ֲ�
1 �㷨���<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;S-DES��һ�־���ķ��������㷨�����ڶ�8bits�������ݽ��м��ܺͽ��ܡ�����Ҫ���������׶Σ���Կ���ɺ����ݼ���/���ܡ�S-DES�㷨��һ�ֻ����ļ����㷨���������ڽ�����ѧ��Ŀ�ģ������ʺ����ڰ�ȫҪ��ϸߵ�Ӧ�á���ʵ��Ӧ���У�ͨ����ʹ�ø�ǿ��ļ����㷨����AES��Advanced Encryption Standard����<br>
* ��Կ����<br>
    * �û��ṩһ��10bits����Կ��K1 K2 K3 K4 K5 K6 K7 K8 K9 K10��<br>
    * ��Կ��������8bits������Կ��K1��K2
* ���ݼ���
    * ���ı���Ϊ����4bits�Ŀ飺L0��R0
    * ��L0��R0ִ�г�ʼ�û���IP��������
    * �������㣺������չ�����S-�������P-���û�����������
    * �������Ұ�飬���ٴν��е�������
    * ���յ����飨L4�����Ұ�飨R4�����ϲ����������ʼ�û���IP^-1������������8λ�����ġ�
* ���ݽ���
    * �����ݼ��ܹ������ƣ���ʹ������ԿK2�����򣩺�K1�������㡣<br>

2 ��Ҫ���<br>
�������
```
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

```
ͨ���û�����
```aidl
    // ͨ���û�����
    public static String permute(String inputStr, int[] table) {
        StringBuilder outputStr = new StringBuilder();
        for (int bitPosition : table) {
            outputStr.append(inputStr.charAt(bitPosition - 1));
        }
        return outputStr.toString();
    }
```
ѭ�����ƺ���
```aidl
   // ѭ�����ƺ���
    public static String ls(String key, int n) {
        String leftHalf = key.substring(0, 5);
        String rightHalf = key.substring(5);
        String shiftedLeft = leftHalf.substring(n) + leftHalf.substring(0, n);
        String shiftedRight = rightHalf.substring(n) + rightHalf.substring(0, n);
        return shiftedLeft + shiftedRight;
    }
```
����Կ����
```aidl
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
```
S-DES���ֺ���F
```aidl
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
```
���ܺ���
```aidl
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
```
���ܺ���
```aidl
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
```
ascll���ܼ�����
```aidl
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
```

ascll���ܼ�����
```aidl
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
```
3 GUI<br>
��welcomeGUIΪ��<br>
��������
```aidl
setTitle("��ӭ����");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
```
���ð�ť
```aidl
  JButton crackButton = new JButton("��Կ�ƽ�");
        JButton binaryButton = new JButton("��/����"); // �޸İ�ť����
        JButton AscllButton = new JButton("ASCLL");
        crackButton.setPreferredSize(new Dimension(150, 50));
        binaryButton.setPreferredSize(new Dimension(150, 50));
        AscllButton.setPreferredSize(new Dimension(150, 50));
         // ������ť��壬����Ӱ�ť
        JPanel buttonPanel = new JPanel(); // �����м��
        buttonPanel.add(crackButton);
        buttonPanel.add(binaryButton);
        buttonPanel.add(AscllButton);
```
��������������Ļ������
```aidl
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
```
����ҳ�棬��Ҫ�Ƿ��ػ�ӭ����İ�ť���
```aidl
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
```
4 ��չ-�����ƽ�<br>
ʵ�ֱ����ƽ⣬��������˶��̣߳����̣߳��ķ�ʽ�����ƽ���Կ��ͬʱ�����һ���ƽ�Ľ�����
```aidl
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
```

# �û�ָ��
1 ����<br>
1.1 ʲô��S-DES��
[S-DES����](https://blog.csdn.net/ftx456789/article/details/80514993) <br>
��ӭʹ��S-DES��Simplified Data Encryption Standard�����ܹ��ߡ�S-DES��Simplified Data Encryption Standard����һ���������ĶԳƼ����㷨��ּ�ڱ������ݵĻ����ԡ���������Ƕ��ʽϵͳ�ͽ�����;��

1.2 �û�ָ�ϸ���

���û�ָ�Ͻ��������˽����ʹ��S-DES�����ܺͽ������ݣ��Լ��˽��й�S-DES�Ļ���ԭ��Ͱ�ȫ�Կ��ǡ�

<br>2 S-DES����<br>
2.1 S-DES�Ĺ���ԭ��

S-DESʹ��Feistel����ṹ�����������ݷֳ����룬Ȼ�����һϵ�е��û�����������������������ġ�������̿���ͨ��������������ܡ�

2.2 ���ܺͽ���

���ܣ������λ���ĺ�ʮλ��Կ��ִ�м��ܲ����õ����ģ�

���ܣ������λ���ĺ���ȷ��ʮλ��Կ��ִ�н��ܲ����õ����ġ�

2.3 ��Կ����

��Կ������S-DES�йؼ���һ��������10bit��Կ��������8bit����Կ�����ڼ��ܺͽ��ܹ��̡�

2.4 GUI����
![img_25.png](img_25.png)
![img_27.png](img_27.png)
![img_28.png](img_28.png)
<br>3 ʹ��S-DES����<br>
<br>4 ʹ��S-DES����<br>