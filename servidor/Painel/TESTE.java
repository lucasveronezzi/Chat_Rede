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
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import java.awt.SystemColor;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import java.awt.Font;


public class TESTE extends JFrame {

	private JPanel contentPane;
	private JTextField txtDasd;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
				try {
					TESTE frame = new TESTE();
					frame.setVisible(true);
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
		
		JPanel panel =  new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(panel);
		panel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblNewLabel = new JLabel("New label");
		panel_1.add(lblNewLabel);
		
		
		
	}
	
}
