package Painel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JToggleButton;
import java.awt.Toolkit;

public class TESTE extends JFrame {

	private JPanel contentPane;
	private final Action action = new SwingAction();

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
		setBounds(100, 100, 600, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		JScrollPane scrollPane_2 = new JScrollPane();
		contentPane.add(scrollPane_2);
		
		JList list = new JList();
		list.setModel(new AbstractListModel() {
			String[] values = new String[] {"gdf", "gdfg"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		scrollPane_2.setViewportView(list);
		
		JPanel panel_1 = new JPanel();
		panel_1.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setPreferredSize(new Dimension(330, 300));
		contentPane.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\Lucas\\Documents\\eclipse\\ChatMulti_Client\\icon\\chatOn.jpg"));
		scrollPane.setViewportView(lblNewLabel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1);
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}
