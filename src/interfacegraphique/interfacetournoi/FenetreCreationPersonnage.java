package interfacegraphique.interfacetournoi;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import interfacegraphique.IHM;
import interfacegraphique.interfacetournoi.exceptionSaisie.GroupeNonValideException;
import interfacegraphique.interfacetournoi.exceptionSaisie.NomNonValideException;
import lanceur.LanceurPerso;
import serveur.element.Caracteristique;
import serveur.element.Personnage;

import javax.swing.JFrame;
/**
 * 
 * @author benjamin
 *
 */
public class FenetreCreationPersonnage extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Textfield permettant d'entrer le nom du personnage
	 */
	private JTextField valueNom; 
	
	/**
	 * Textfield permettant d'entrer le groupe du personnage
	 */
	private JTextField valueGroupe;
	/**
	 * Bouton qui valide la creation d'un personnage
	 */
	private JButton b;
	
	private IHM test;
	/**
	 * Initialisation de la fenetre de creation du personnage
	 */
	/** 
	 * Personnage que l'on crée
	 */
	private Personnage p;
	
	/**
	 * Nom du personnage créé ( Berserker etc... )
	 */
	private String nomPersonnage;
	
	/**
	 * 
	 * @param component IHM à utiliser pour la creation de fenetres
	 * @param p	Personnage p à utiliser dans initComponents
	 * @param nom	Nom de la classe du Personnage
	 */
	public FenetreCreationPersonnage(IHM component, Personnage p, String nom){
		this.p = p;
		this.test = component;
		nomPersonnage = nom;
		initComponents();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int x = ((int) screenSize.getWidth() / 2 ) - (this.getWidth() / 2);
		int y = ((int) screenSize.getHeight() / 2 ) - (this.getHeight() / 2);
		Point point = new Point(x,y);
		this.setLocation(point);
		this.setVisible(true);
	}
	/** 
	 * Initialisation de la fenetre de creation de personnage
	 * @param p Personnage à remplir pour pouvoir le creer
	 */
	public void initComponents(){
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(310, 215));
        setResizable(true);
        setAlwaysOnTop(true);
        getContentPane().setLayout(new GridLayout(3,1, 0, 0)); 
        setTitle("Création de "+nomPersonnage);
        JPanel nomPerso = new JPanel();
        JLabel labelNom = new JLabel("Nom du personnage");
        nomPerso.add(labelNom);
        valueNom = new JTextField();
		valueNom.setPreferredSize(new Dimension(90,20));
		nomPerso.add(valueNom);
		getContentPane().add(nomPerso);
    	
		JPanel nomGroupe = new JPanel();
        JLabel labelGroupe = new JLabel("Nom-Numéro du Groupe");
        nomGroupe.add(labelGroupe);
        valueGroupe = new JTextField();
		valueGroupe.setPreferredSize(new Dimension(90,20));
		nomGroupe.add(valueGroupe);
		getContentPane().add(nomGroupe);

		JPanel validation = new JPanel();
    	b = new JButton ("Créer");
    	validation.add(b);
    	    	
    	b.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent event) {
    			lancePerso();
    		}
    	});
    	getContentPane().add(validation);
        pack();
        
	}
	/**
	 * Teste si les champs ne sont pas vides
	 */
	public void lancePerso (){
		List<String> erreurMessage = new ArrayList<String>();
		boolean validValues = true;
		String nom;
		String groupe;
		try {
			nom = getNom();
			
		} catch (NomNonValideException e) {
			validValues = false;
			erreurMessage.add("Le nom saisi est invalide.");
		}
		try {
			groupe = getGroupe();
		} catch ( GroupeNonValideException e){
			validValues = false;
			erreurMessage.add("Le groupe saisi est invalide.");
		}
		if (validValues) {
			p.setGroupe(valueGroupe.getText());
			p.setNom(valueNom.getText());
			new Thread(new LanceurPerso(p)).start();
			setVisible(false);
		} else {
			afficheMessageErreur(erreurMessage);
		}
	}
	
	/**
	 * Si un des champs est vide, cela affiche une erreur
	 * @param messages : liste de messages d'erreur
	 */
	private void afficheMessageErreur(List<String> messages) {
		String s = "<html><body><div width='300px' align='center'>";
		
		for (String msg : messages) {
			s += "<p>" + msg + "</p><br>";
		}
		
		JOptionPane.showMessageDialog(this, s, "Erreur de saisie", JOptionPane.ERROR_MESSAGE); 
	}
	
	/** 
	 * Teste la validité du nom et renvoie le nom uniquement s'il est valide
	 * @return
	 * @throws NomNonValideException
	 */
	public String getNom() throws NomNonValideException {
		String nom = valueNom.getText();
			
		if (nom.equals("")) {
				throw new NomNonValideException();
		}
			
	   return nom;
	}  
	/**
	 * Teste la validité du groupe et renvoie le groupe uniquement s'il est valide
	 * @return
	 * @throws GroupeNonValideException
	 */
	public String getGroupe() throws GroupeNonValideException {
		String groupe = valueGroupe.getText();
			
		if (groupe.equals("")) {
				throw new GroupeNonValideException();
		}
			
	   return groupe;
	} 
}
