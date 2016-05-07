package Chat;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class Main extends JFrame{

	public static Main frame;
	public static void main (String[] args){
		try{
			UIManager.setLookAndFeel("org.jb2011.lnf.beautyeye.BeautyEyeLookAndFeelCross");
			UIManager.put("RootPane.setupButtonVisible", false);
			frame = new Main();
			frame.setVisible(true);
			
		}catch(ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public Main(){
		JFrame frame = this;
		
		JPanel panelIP = new JPanel();
		JPanel panelPorta = new JPanel();
		JPanel panelUser = new JPanel();
		JPanel panelConect = new JPanel();
		
		JLabel labelIP = new JLabel("IP: ");
		JLabel labelPorta = new JLabel("Porta: ");
		JLabel labelUser = new JLabel("Usuário: ");
		
		JTextField textIP = new JTextField("192.168.1.31");
		textIP.setColumns(10);
		JTextField textPorta = new JTextField("555");
		textPorta.setColumns(10);
		JTextField textUser = new JTextField();
		textUser.setColumns(10);
		
		JButton conect = new JButton("Conectar");
		panelConect.add(conect);
		conect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(textIP.getText().length() > 0 && 
						textPorta.getText().length() > 0 && 
							textUser.getText().length() > 0){
					if(!textUser.getText().contains("|") && !textUser.getText().contains("ARQUIVO")){
						Cliente client = new Cliente(textIP.getText(), textPorta.getText(), textUser.getText());
						if(client.conectado){
							frame.setVisible(false);
							new Janela_Chat(client);
						}
					}else{
						JOptionPane.showMessageDialog(null, "Favor não usar | no nome", "Atenção!", JOptionPane.WARNING_MESSAGE);
					}
				}else{
					JOptionPane.showMessageDialog(null, "Favor preencher todos os campos corretamente!", "Atenção!", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		panelIP.add(labelIP);
		panelPorta.add(labelPorta);
		panelUser.add(labelUser);
		
		panelIP.add(textIP);
		panelPorta.add(textPorta);
		panelUser.add(textUser);
		
		frame.add(panelIP);
		frame.add(panelPorta);
		frame.add(panelUser);
		frame.add(panelConect);
		
		setTitle("Iniciar Chat");
		setBounds(400,100,250,270);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
	}
}
