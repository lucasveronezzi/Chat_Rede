package Chat;

import javax.swing.ImageIcon;
import javax.swing.JTextArea;

import Painel.TESTE;

public class InfoChat {
	private String nome;
	private String ip;
	public int tipo;
	private JTextArea areaTextRecebe;
	public ImageIcon icon;
	public boolean msgNaoLida = false;
	
	public InfoChat(String nome, String ip, int tipo){
		this.nome = nome;
		this.ip = ip;	
		this.tipo = tipo;
		areaTextRecebe = new JTextArea();
		areaTextRecebe.setLineWrap(true);
		areaTextRecebe.setWrapStyleWord(true);
		areaTextRecebe.setEditable(false);
	}
	public String getNome(){
		return this.nome;
	}
	public String getIp(){
		return this.ip;
	}
	public JTextArea getText(){
		return this.areaTextRecebe;
	}
	public void setIconON(){
		if(tipo == 0)
			icon = new ImageIcon("C:\\Users\\Lucas\\Documents\\eclipse\\Chat_Rede\\img\\user-icon-on.png");
		else
			icon = new ImageIcon("C:\\Users\\Lucas\\Documents\\eclipse\\Chat_Rede\\img\\user-group-icon.png");
	}
	public void setIconOff(){
		icon = new ImageIcon("C:\\Users\\Lucas\\Documents\\eclipse\\Chat_Rede\\img\\user-icon-off.png");
	}
}
