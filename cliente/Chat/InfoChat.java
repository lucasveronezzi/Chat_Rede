package Chat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;


public class InfoChat {
	public String textToolTip = "Participantes: ";
	public JLabel labMsgNLida = new JLabel();
	public boolean msgNaoLida = false;
	public ImageIcon icon;
	public int tipo;
	public List<Arquivo> arquivos;
	
	private String nome;
	private String ip;
	private JPanel pChatRecebe;
	private List<String> usuarios;
	private Color clEmitente = new Color(229, 240, 245);
	private Color clDestino =new Color(213, 231, 255);
	private Font FontHeader = new Font("Century Gothic", Font.BOLD, 12);
	private Font fontFile = new Font("Century Gothic", Font.BOLD, 11);
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm");
	private ImageIcon iconMsgNLIda = new ImageIcon("C:\\img\\chat.png");
	private ImageIcon iconDownload = new ImageIcon("C:\\Chat\\img\\icon-file.png");
	private int tmPanel = 0;
	
	public InfoChat(String nome, String ip, int tipo){
		this.nome = nome;
		this.ip = ip;	
		this.tipo = tipo;
		if(tipo == 1) usuarios = new ArrayList<String>();
		else arquivos = new ArrayList<Arquivo>();
		labMsgNLida.setIcon(iconMsgNLIda);
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
		
		JTextPane labelMsg = new JTextPane();
		labelMsg.setEditable(false);
		labelMsg.setContentType("text/html");
		labelMsg.setMargin(new Insets(5,10,5,15));
		msg = replaceEmoticon(msg);
		msg = "<p style=\"font-size: 11px;width: 255px; word-wrap: break-word;margin:0px\">"+msg.replace("\n", "<br>")+"</p>";
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
	public void addFileToChat(String path, String emitente, String opt, String tamanhoFile){
		JProgressBar bar = new JProgressBar();
		JButton buttonFile = new JButton();
		String nomeFile = new File(path).getName();
		Date date = new Date();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		arquivos.add(new Arquivo(path, tamanhoFile, bar, buttonFile));
		
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
		
		JPanel panelFile = new JPanel();
		buttonFile.setIcon(iconDownload);
		buttonFile.setPreferredSize(new Dimension(40,40));
		buttonFile.setOpaque(true);
		panelFile.add(buttonFile);
		
		JPanel panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(260,45));
		panelFile.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JLabel labelNomeFile = new JLabel("Arquivo: "+nomeFile);
		labelNomeFile.setFont(fontFile);
		labelNomeFile.setOpaque(true);
		panel_1.add(labelNomeFile);
		
		bar.setStringPainted(true);
		bar.setPreferredSize(new Dimension(100,20));
		panel_1.add(bar);

		if(opt.equals("recebendo")){
			buttonFile.setToolTipText("Baixar Arquivo");
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
	 class Arquivo{
		 public File arquivo;
		 public int tamanho;
		 public JButton button;
		 public JProgressBar barraProgresso;
		 
		 public Arquivo(String path, String tamanho, JProgressBar progresso, JButton button){
			 this.arquivo = new File(path);
			 this.tamanho = Integer.parseInt(tamanho);
			 this.barraProgresso = progresso;
			 this.button = button;
		 }
	 }
	 public String replaceEmoticon(String msg){
		 HashMap<String, String> smileys = new HashMap<String, String>();
		 String emoPath = "<img src=\"file:C:/Chat/img/emoticon/";
		 smileys.put(":\\)",emoPath+"e6.png\" >");
		 smileys.put(":\\(",emoPath+"e7.png\" >");
		 smileys.put(">.<",emoPath+"e3.png\" >");
		 smileys.put(":D",emoPath+"e4.png\" >");
		 smileys.put(":R",emoPath+"e2.png\" >");
		 smileys.put(":L",emoPath+"e1.png\" >");
		 smileys.put(":O",emoPath+"e5.png\" >");
		 for(Entry<String, String> smiley : smileys.entrySet())
			 msg = msg.replaceAll(smiley.getKey(), smiley.getValue());
		 return msg;
	 }
}