package serveur;

import interfaceGraphique.view.VuePersonnage;

import java.awt.Point;

import serveur.element.PersonnageServeur;

/**
 * Toutes les donnees que le serveur doit conserver sur chacun de ces clients
 */
public class ClientPersonnage extends ClientElement {


	/**
	 * adresse ip du client
	 */
	private String ipAddress = "localhost";
	
	/**
	 * booleen specifiant si une action a ete executee ce tour-ci
	 */
	private boolean actionExecutee;
	
	private int tourSonne;
	
	/**
	 * Time To Live
	 */
	private int TTL;
	/**
	 * Compteur permettant de savoir le nombre de tour restant avant increment
	 */
	
	public ClientPersonnage(String ipAddress, PersonnageServeur pers, int TTL, Point position, int ref) {
		super(pers, position, ref);
		this.ipAddress = ipAddress;
		this.actionExecutee = false;
		this.tourSonne = 0;
		this.TTL = TTL;
	}

	/* *******************
	 * Accesseurs
	 * *******************/
	public String getIpAddress() {
		return ipAddress;
	}

	public PersonnageServeur getPersServeur() {
		return (PersonnageServeur) getElement();
	}
	
	public void setPersServeur(PersonnageServeur pers) {
		this.elem = pers;
	}

	public VuePersonnage getVue() {
		PersonnageServeur perso = getPersServeur();
		VuePersonnage vp = new VuePersonnage(
				getRef(), getPosition(), perso.getNom(), 
				perso.getGroupe(), perso.getCaracts(), getColor(), 
				getPhrase(), perso.getEquipe(), perso.getLeader());
		
		return vp;
	}
	
	public boolean isActionExecutee() {
		return actionExecutee;
	}
	
	/**
	 * Une action vient d'etre executee, on fait en sorte que plus aucune autre action
	 * ne sera executee avant la fin de ce tour
	 */
	public void actionExecutee() {
		actionExecutee = true;
	}

	public int getTTL() {
		return TTL;
	}
	
	/**
	 * Action a faire a la fin de ce tour
	 */
	public void finTour() {
		TTL--;
		actionExecutee = false;
		decrTourSonne();
	}


	
	private void decrTourSonne () {
		if (tourSonne > 0) tourSonne--;
	}
	
	public void sonne() {
		tourSonne++;
	}
	
	public int getTourSonne() {
		return tourSonne;
	}
}