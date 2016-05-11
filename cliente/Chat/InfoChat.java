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
	private Font FontHeader = new Font("Century Gothic", Font.BOLD, 13);
	private Font fontFile = new Font("Century Gothic", Font.BOLD, 11);
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm");
	private ImageIcon iconMsgNLIda = new ImageIcon("C:\\Chat\\img\\chat.png");
	private ImageIcon iconDownload = new ImageIcon("C:\\Chat\\img\\icon-file.png");
	
	private String lastEmitente = "";
	private String lastData = "";
	
	public InfoChat(String nome, String ip, int tipo){
		this.nome = nome;
		this.ip = ip;	
		this.tipo = tipo;
		if(tipo == 1) usuarios = new ArrayList<String>();
		arquivos = new ArrayList<Arquivo>();
		labMsgNLida.setIcon(iconMsgNLIda);
		labMsgNLida.setVisible(false);
		pChatRecebe = new JPanel();
		pChatRecebe.setBackground(Color.WHITE);
		pChatRecebe.setOpaque(true);
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
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		Date date = new Date();
		
		if(!emitente.equals(lastEmitente) || !lastData.equals(dateFormat.format(date))){
			JPanel panelHeader = new JPanel();
			panelHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
			
			JLabel labelNome = new JLabel(emitente+": ");
			labelNome.setFont(FontHeader);
			labelNome.setOpaque(true);
			labelNome.setPreferredSize(new Dimension(390,20));
			JLabel labelHora = new JLabel("["+dateFormat.format(date)+"]");
			labelHora.setFont(FontHeader);
			labelHora.setOpaque(true);
			labelHora.setPreferredSize(new Dimension(40,20));
			panelHeader.add(labelNome);
			panelHeader.add(labelHora);
			panel.add(panelHeader);
			lastEmitente = emitente;
			lastData = dateFormat.format(date);
		}
		
		JTextPane labelMsg = new JTextPane();
		labelMsg.setEditable(false);
		labelMsg.setContentType("text/html");
		labelMsg.setMargin(new Insets(5,30,5,35));
		msg = replaceEmoticon(msg);
		msg = "<p style=\"font-size: 11px;width: 298px; word-wrap: break-word;margin:0px\">"+msg.replace("\n", "<br>")+"</p>";
		labelMsg.setText(msg);
		
		if(emitente.equals("Eu"))
			labelMsg.setBackground(clEmitente);
		else
			labelMsg.setBackground(clDestino);
		
		panel.add(labelMsg);
		pChatRecebe.add(panel);
		pChatRecebe.revalidate();
		labelMsg.scrollRectToVisible(labelMsg.getBounds());
		panel.revalidate();
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
		labelNome.setPreferredSize(new Dimension(392,20));
		JLabel labelHora = new JLabel("["+dateFormat.format(date)+"]");
		labelHora.setFont(FontHeader);
		labelHora.setOpaque(true);
		labelHora.setPreferredSize(new Dimension(40,20));
		
		JPanel panelFile = new JPanel();
		buttonFile.setIcon(iconDownload);
		buttonFile.setPreferredSize(new Dimension(40,40));
		buttonFile.setOpaque(true);
		panelFile.add(buttonFile);
		
		JPanel panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(320,45));
		panelFile.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JLabel labelNomeFile = new JLabel("Arquivo: "+nomeFile);
		labelNomeFile.setFont(fontFile);
		labelNomeFile.setOpaque(true);
		panel_1.add(labelNomeFile);
		
		bar.setStringPainted(true);
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
		 smileys.put(":L",emoPath+"e1.png\" >");
		 smileys.put(":R",emoPath+"e2.png\" >");
		 smileys.put(">.<",emoPath+"e3.png\" >");
		 smileys.put(":D",emoPath+"e4.png\" >");
		 smileys.put(":O",emoPath+"e5.png\" >");
		 smileys.put(":\\)",emoPath+"e6.png\" >");
		 smileys.put(":\\(",emoPath+"e7.png\" >");
		 smileys.put("sniff",emoPath+"e8.png\" >");
		 smileys.put("/sono",emoPath+"e9.gif\" >");
		 smileys.put("eq%",emoPath+"e10.gif\" >");
		 smileys.put("pirata",emoPath+"e11.gif\" >");
		 smileys.put("o.O",emoPath+"e12.gif\" >");
		 smileys.put("/medo",emoPath+"e13.png\" >");
		 smileys.put("/raiva",emoPath+"e14.png\" >");
		 smileys.put("/facepalm",emoPath+"e15.gif\" >");
		 smileys.put("emo16",emoPath+"e16.png\" >");
		 smileys.put("emo17",emoPath+"e17.png\" >");
		 smileys.put("emo18",emoPath+"e18.gif\" >");
		 smileys.put("emo19",emoPath+"e19.gif\" >");
		 smileys.put("emo20",emoPath+"e20.gif\" >");
		 smileys.put("emo21",emoPath+"e21.png\" >");
		 smileys.put("emo22",emoPath+"e22.png\" >");
		 smileys.put("emo23",emoPath+"e23.gif\" >");
		 smileys.put("emo24",emoPath+"e24.gif\" >");
		 smileys.put("emo25",emoPath+"e25.png\" >");
		 
		 for(Entry<String, String> smiley : smileys.entrySet())
			 msg = msg.replaceAll(smiley.getKey(), smiley.getValue());
		 return msg;
	 }
}