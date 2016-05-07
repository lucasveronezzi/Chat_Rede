package Painel;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sf.jcarrierpigeon.*;
import java.awt.Toolkit;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.SpringLayout;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import java.awt.SystemColor;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class TESTE extends JFrame {

	private JPanel contentPane;
	private JTextField txtDasd;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
				try {
					TESTE frame = new TESTE();
					frame.setVisible(true);
					NotificationQueue queue = new NotificationQueue();
					Notification note = new Notification(frame, WindowPosition.BOTTOMRIGHT, 25, 25, 10000);
					
					queue.add(note);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
	}

	/**
	 * Create the frame.
	 */
	public TESTE() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(TESTE.class.getResource("/org/jb2011/lnf/beautyeye/ch1_titlepane/imgs/ifi1.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 250);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("New menu");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("New menu item");
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JSeparator separator = new JSeparator();
		mnNewMenu.add(separator);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("New menu item");
		mnNewMenu.add(mntmNewMenuItem_2);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnNewButton = new JButton();
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setPreferredSize(new Dimension(20,20));
		btnNewButton.setIcon(new ImageIcon(TESTE.class.getResource("/org/jb2011/lnf/beautyeye/ch16_tree/imgs/treeDefaultOpen1.png")));
		getContentPane().add(btnNewButton);
		
		JComboBox comboBox = new JComboBox();
		getContentPane().add(comboBox);
		comboBox.addItem("teste");
		
	}
	
}
