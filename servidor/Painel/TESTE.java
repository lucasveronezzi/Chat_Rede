package Painel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import Chat.InfoChat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SpringLayout;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;


public class TESTE extends JFrame {

	private JPanel contentPane;
	private JTextField txtDasd;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;

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
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		JLabel lblGdfgdf = new JLabel("Nome do Grupo");
		springLayout.putConstraint(SpringLayout.NORTH, lblGdfgdf, 21, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblGdfgdf, -170, SpringLayout.EAST, getContentPane());
		getContentPane().add(lblGdfgdf);
		
		textField_2 = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textField_2, -3, SpringLayout.NORTH, lblGdfgdf);
		springLayout.putConstraint(SpringLayout.WEST, textField_2, 6, SpringLayout.EAST, lblGdfgdf);
		getContentPane().add(textField_2);
		textField_2.setColumns(10);
		
		String[] data = {"one", "two", "three", "four"};
		String[] data2 = {"one", "two", "three", "four"};
		
		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 31, SpringLayout.NORTH, lblGdfgdf);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, lblGdfgdf);
		scrollPane.setBorder(new TitledBorder(new LineBorder(null), "Todos"));
		getContentPane().add(scrollPane);
		scrollPane.setPreferredSize(new Dimension(60,100));
		JList listChat = new JList(data);
		scrollPane.setViewportView(listChat);
		
		JButton btnAdicionar = new JButton("Adicionar");
		btnAdicionar.setIcon(null);
		springLayout.putConstraint(SpringLayout.SOUTH, btnAdicionar, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnAdicionar, 0, SpringLayout.EAST, lblGdfgdf);
		getContentPane().add(btnAdicionar);
		
		JButton btnNewButton = new JButton("Cancelar");
		springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 0, SpringLayout.NORTH, btnAdicionar);
		springLayout.putConstraint(SpringLayout.EAST, btnNewButton, -42, SpringLayout.EAST, getContentPane());
		getContentPane().add(btnNewButton);
		
		JLabel lblTesteeee = new JLabel("testeeee");
		springLayout.putConstraint(SpringLayout.NORTH, lblTesteeee, 50, SpringLayout.SOUTH, textField_2);
		springLayout.putConstraint(SpringLayout.WEST, lblTesteeee, 0, SpringLayout.WEST, btnNewButton);
		springLayout.putConstraint(SpringLayout.SOUTH, lblTesteeee, 81, SpringLayout.SOUTH, textField_2);
		springLayout.putConstraint(SpringLayout.EAST, lblTesteeee, 0, SpringLayout.EAST, btnNewButton);
		getContentPane().add(lblTesteeee);
		
		JSeparator separator = new JSeparator();
		getContentPane().add(separator);
		
		
		
	}
}
