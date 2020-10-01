package ruosha;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class Notepad {
	// 声明变量，不要问为什么是public，问就是好写
	public static JMenuItem open = new JMenuItem("打开"), save = new JMenuItem("保存"), exit = new JMenuItem("退出"),
			test = new JMenuItem("用默认程序打开"), close = new JMenuItem("关闭文件"), fileAtt = new JMenuItem("当前文件属性"),
			about = new JMenuItem("关于这个记事本");
	public static JMenu file = new JMenu("文件"), help = new JMenu("帮助");
	public static JFrame f = new JFrame("记事本");
	public static JMenuBar m = new JMenuBar();
	public static JPanel p = new JPanel();
	public static JTabbedPane t = new JTabbedPane();
	public static JTextArea[] text = { new JTextArea(), new JTextArea(), new JTextArea(), new JTextArea(),
			new JTextArea() };
	public static JScrollPane[] s = { new JScrollPane(), new JScrollPane(), new JScrollPane(), new JScrollPane(),
			new JScrollPane() };
	public static JFileChooser ch = new JFileChooser("d:/");
	public static File[] file1 = new File[5];
	public static BufferedWriter writer = null;
	public static JLabel l = new JLabel("编码：");
	public static String[] bm1 = new String[] { "GBK", "Unicode", "UTF-16BE", "UTF-8" };
	public static JComboBox bm = new JComboBox(bm1);
	public static ByteArrayOutputStream outputStream = null;
	public static String str, tell, filecode = "GBK";
	public static boolean openfile, savefile;
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	public static int i = 0;

	public static void main(String[] args) {
		p.setBounds(50, 50, 300, 60);
		p.setLayout(new FlowLayout());
		m.add(file);
		m.add(help);
		m.add(l);
		m.add(bm);
		file.add(open);
		file.add(save);
		file.add(exit);
		file.add(test);
		file.add(close);
		help.add(fileAtt);
		help.add(about);
		text[i].setLineWrap(true);
		t.add(p);
		t.remove(0);
		f.setJMenuBar(m);
		f.setSize(1000, 750);
		f.setLocation(240, 125);
		f.setLayout(null);
		f.setContentPane(t);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Thread t1 = new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (openfile) {
						openfile = false;
						int returnVal = ch.showOpenDialog(f);
						// 判断是否正常打开
						if (returnVal == 0) {
							file1[i] = ch.getSelectedFile();
							filecode = bm.getSelectedItem().toString();
							if (file1[i] != null) {
								System.out.println(returnVal);
								try {
									// 用数组是为了可以多个页面
									text[i].setText("");
									s[i] = new JScrollPane(text[i]);
									t.addTab(file1[i].getName(), s[i]);
									text[i].setLineWrap(true);
									FileInputStream fileInputStream = new FileInputStream(file1[i]);
									outputStream = new ByteArrayOutputStream();
									byte[] buffer = new byte[8192];
									int len = 0;
									while ((len = fileInputStream.read(buffer)) != -1) {
										outputStream.write(buffer, 0, len);
										text[i].append(new String(buffer, filecode));
									}
									fileInputStream.close();
									i++;
								} catch (FileNotFoundException e) {
								} catch (IOException e) {
								} finally {
									if (file1[4] != null) {
										if (null != outputStream)
											try {
												file1[i - 1] = null;
												outputStream.close();
											} catch (IOException e2) {
											}
									}
								}
							}
						}
					}
				}
			}

		};
		t1.start();
		Thread t2 = new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (savefile) {
						savefile = false;
						int returnVal = ch.showSaveDialog(f);
						// 如果是正常打开的话就保存
						if (returnVal == 0) {
							File file = ch.getSelectedFile();
							if (file != null) {
								// 缓冲保存文件
								try {
									if (!file.exists()) {
										file.createNewFile();
									}
									file.delete();
									writer = new BufferedWriter(
											new OutputStreamWriter(new FileOutputStream(file), filecode));
									writer.write(text[i - 1].getText());
									writer.close();
								} catch (IOException e1) {
								} finally {
									if (null != writer)
										try {
											writer.close();
										} catch (IOException e1) {
											e1.printStackTrace();
										}
								}
							}
						}
					}
				}
			}
		};
		t2.start();
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				say("尽量别打开视频等文件，会成乱码，有可能卡死,而且保存后就打不开了" + "\n如有任何bug请告诉若啥" + "\n by:ruosha", "", 1);
			}
		});
		fileAtt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (i != 0) {
					if (file1[i - 1] != null) {
						try {
							str = sdf.format(file1[i - 1].lastModified());
							say("文件大小：" + file1[i - 1].length() + "\n 文件位置：" + file1[i - 1].getPath() + "\n 修改时间：" + str
									+ "\n 是否为隐藏文件:" + file1[i - 1].isHidden() + "\n 文件是否可读：" + file1[i - 1].canRead()
									+ "\n 文件是否可写：" + file1[i - 1].canWrite() + "\n文件是否存在：" + file1[i - 1].exists(), "",
									0);
						} catch (Exception e1) {
						}
					}
				} else {
					say("未选择文件", "", 2);
				}
			}
		});
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (i != 0) {
					file1[i - 1] = null;
					text[i - 1].setText("");
					t.remove(s[i - 1]);
					i--;
				}
			}
		});
		test.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (i != 0) {
					if (file1[i - 1] != null) {
						try {
							java.awt.Desktop test1 = java.awt.Desktop.getDesktop();
							if (test1.isSupported(java.awt.Desktop.Action.BROWSE)) {
								File file = new File(file1[i - 1].getPath());
								test1.open(file);
							}
						} catch (java.lang.NullPointerException e1) {
						} catch (java.io.IOException e2) {
						}
					} else {
						say("未选择文件", "", 2);
					}
				}
			}
		});
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openfile = true;
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savefile = true;
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

	// 提示框模板
	public static void say(String text, String name, int id) {
		if (id == 0) {
			JOptionPane.showMessageDialog(f, text);
		} else if (id == 1) {
			JOptionPane.showMessageDialog(f, text, name, JOptionPane.WARNING_MESSAGE);
		} else if (id == 2) {
			JOptionPane.showMessageDialog(f, text, name, JOptionPane.ERROR_MESSAGE);
		} else if (id == 3) {
			tell = JOptionPane.showInputDialog(f, text);
		}
	}
}