package Chat;

import javax.swing.ImageIcon;
import javax.swing.JTextArea;

public class InfoChat {
	private String nome;
	private String ip;
	private JTextArea areaTextRecebe;
	public ImageIcon icon;
	
	public InfoChat(String nome, String ip){
		this.nome = nome;
		this.ip = ip;	
		
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
		icon = new ImageIcon("C:\\Users\\Lucas\\Documents\\eclipse\\ChatMulti_Client\\icon\\chatOn.jpg");
	}
	public void setIconOff(){
		icon = new ImageIcon("C:\\Users\\Lucas\\Documents\\eclipse\\ChatMulti_Client\\icon\\chatOff.jpg");
	}
}
