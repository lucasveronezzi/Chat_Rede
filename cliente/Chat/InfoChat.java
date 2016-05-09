package Chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;

public class InfoChat {
	private String nome;
	private String ip;
	public String textToolTip = "Participantes: ";
	public ImageIcon icon;
	public JButton buttonFile;
	public JProgressBar progressBar;
	private JPanel pChatRecebe;
	public JLabel labMsgNLida=new JLabel();
	private List<String> usuarios;
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm");
	public boolean msgNaoLida = false;
	private int tmPanel = 0;
	public int tipo;
	private Color clEmitente = new Color(239, 243, 255);
	private Color clDestino =new Color(229, 247, 253);
	private Font fontMsg = new Font("Verdana", Font.PLAIN, 12); 
	private Font FontHeader = new Font("Calibri Light", Font.BOLD, 14);
	private Font fontFile = new Font("Centaur", Font.BOLD, 13);
	
	public InfoChat(String nome, String ip, int tipo){
		this.nome = nome;
		this.ip = ip;	
		this.tipo = tipo;
		if(tipo == 1) usuarios = new ArrayList<String>();
		labMsgNLida.setIcon(new ImageIcon("img\\chat.png"));
		labMsgNLida.setVisible(false);
		pChatRecebe = new JPanel();
		pChatRecebe.setLayout(new WrapLayout());
	}
	public String getNome(){
		return this.nome;
	}
	public String getIp(){
		return this.ip;
	}
	public JPanel getPanelChat(){
		return pChatRecebe;
	}
	public void setIconON(){
		if(tipo == 0)
			icon = new ImageIcon("img\\user-icon-on.png");
		else
			icon = new ImageIcon("img\\user-group-icon.png");
	}
	public void setIconOff(){
		icon = new ImageIcon("img\\user-icon-off.png");
	}
	public void addUserToGrupo(String user){
		usuarios.add(user);
		textToolTip = textToolTip +user+", ";
	}
	public List<String> getUsuarios(){
		return usuarios;
	}
	public void delUser(String user){
		usuarios.remove(user);
		textToolTip = "Participantes: ";
		for(int x=0; usuarios.size() > x;x++){
			textToolTip = textToolTip +usuarios.get(x)+", ";
		}
	}
	public void addMsgToChat(String msg, String emitente){
		Date date = new Date();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel panelHeader = new JPanel();
		panelHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		
		JLabel labelNome = new JLabel(emitente+": ");
		labelNome.setFont(FontHeader);
		labelNome.setOpaque(true);
		labelNome.setPreferredSize(new Dimension(310,20));
		JLabel labelHora = new JLabel("["+dateFormat.format(date)+"]");
		labelHora.setFont(FontHeader);
		labelHora.setOpaque(true);
		labelHora.setPreferredSize(new Dimension(60,20));
		
		JTextArea labelMsg = new JTextArea();
		labelMsg.setFont(fontMsg);
		labelMsg.setEditable(false);
		labelMsg.setLineWrap(true);
		labelMsg.setWrapStyleWord(true);
		
		labelMsg.setMargin(new Insets(10,25,10,40));
		labelMsg.setText(msg);
		
		if(emitente.equals("Eu"))
			labelMsg.setBackground(clEmitente);
		else
			labelMsg.setBackground(clDestino);

		panelHeader.add(labelNome);
		panelHeader.add(labelHora);
		panel.add(panelHeader);
		panel.add(labelMsg);
		
		pChatRecebe.add(panel);
		pChatRecebe.revalidate();
		labelMsg.scrollRectToVisible(labelMsg.getBounds());
		panel.revalidate();
		tmPanel = tmPanel + panel.getHeight();
	}
	public void addFileToChat(String nomeFile, String emitente, String opt){
		Date date = new Date();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel panelHeader = new JPanel();
		panelHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		
		JLabel labelNome = new JLabel(emitente+": ");
		labelNome.setFont(FontHeader);
		labelNome.setOpaque(true);
		labelNome.setPreferredSize(new Dimension(315,20));
		
		JLabel labelHora = new JLabel("["+dateFormat.format(date)+"]");
		labelHora.setFont(FontHeader);
		labelHora.setOpaque(true);
		
		JPanel panelFile = new JPanel();
		buttonFile = new JButton();
		buttonFile.setIcon(new ImageIcon("img\\icon-file.png"));
		panelFile.add(buttonFile);
		
		JPanel panel_1 = new JPanel();
		panelFile.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JLabel labelNomeFile = new JLabel("Arquivo: "+nomeFile);
		labelNomeFile.setFont(fontFile);
		labelNomeFile.setOpaque(true);
		panel_1.add(labelNomeFile);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		panel_1.add(progressBar);

		if(opt.equals("recebendo")){
			labelNomeFile.setBackground(clDestino);
			panel_1.setBackground(clDestino);
			panelFile.setBackground(clDestino);
		}else{
			labelNomeFile.setBackground(clEmitente);
			panel_1.setBackground(clEmitente);
			panelFile.setBackground(clEmitente);
		}
			
		panelHeader.add(labelNome);
		panelHeader.add(labelHora);
		panel.add(panelHeader);
		panel.add(panelFile);
		
		pChatRecebe.add(panel);
		pChatRecebe.revalidate();
		panelFile.scrollRectToVisible(panelFile.getBounds());
		panel.revalidate();
		tmPanel = tmPanel + panel.getHeight();
	}
}