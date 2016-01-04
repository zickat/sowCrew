package serveur.vuelement;

import java.awt.Color;
import java.awt.Point;

import serveur.element.Personnage;
import utilitaires.Constantes;

/**
 * Donnees que le serveur doit conserver sur chacun de ces clients personnages.
 */
public class VuePersonnage extends VueElement<Personnage> implements Comparable<VuePersonnage> {
	
	private static final long serialVersionUID = 6775104377685248116L;

	/**
	 * Adresse IP du client.
	 */
	private String adresseIp = Constantes.IP_DEFAUT;
	
	/**
	 * Vrai si ce personnage a execute une action ce tour-ci.
	 */
	private boolean actionExecutee;
	
	/**
	 * Nombre de tours que ce client peut passer sur l'arene.
	 * Si negatif, ce temps est illimite.
	 */
	private final int NB_TOURS;
	
	/**
	 * Numero de tour pour ce client.
	 */
	private int tour = 0;
	
	/**
	 * Numero du tour ou le personnage est mort. 
	 * Egal a -1 s'il est vivant.
	 */
	private int tourMort = -1;
	
	/**
	 * Dit si le personnage est visble ou non sur l'ihm
	 */
	private boolean visible = true;
	
	/**
	 * Cree une vue du personnage.
	 * @param adresseIp adresse IP de la console correspondant au personnage
	 * @param personnage personnage correspondant
	 * @param nbTours nombre de tours ou l'element est present sur 
	 * l'arene (si negatif, indefiniment)
	 * @param position position courante
	 * @param ref reference RMI
	 */
	public VuePersonnage(String adresseIp, Personnage personnage, int nbTours, 
			Point position, int ref) {
		
		super(personnage, position, ref);
		this.adresseIp = adresseIp;
		this.actionExecutee = false;
		this.NB_TOURS = nbTours;
	}
	
	/**
	 * Note que ce personnage a deja execute une action a ce tour.
	 */
	public void executeAction() {
		actionExecutee = true;
	}

	/**
	 * Termine le tour de ce personnage : decremente le nombre de tours restants
	 * et note qu'aucune action n'a ete executee. 
	 */
	public void termineTour() {
		actionExecutee = false;
		tour++;
	}
	
	/**
	 * Teste si ce personnage n'a pas termine son temps sur l'arene.
	 * @return vrai s'il reste des tours a ce personnage, faux sinon
	 */
	public boolean resteTours() {
		return NB_TOURS > 0 && tour < NB_TOURS;
	}

	/**
	 * Renvoie la couleur de l'element s'il est vivant, ou gris sinon.
	 * @return couleur de l'element
	 */
	@Override
	public Color getCouleur() {
		return element.estVivant()? super.getCouleur(): Constantes.COULEUR_MORTS;
	}

	public String getAdresseIp() {
		return adresseIp;
	}

	public boolean isActionExecutee() {
		return actionExecutee;
	}
	
	public int getTourMort() {
		return tourMort;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setTourMort(int tourMort) {
		this.tourMort = tourMort;
	}

	@Override
	public int compareTo(VuePersonnage vp2) {
		int res;
		
		Personnage e1 = this.getElement();
		Personnage e2 = vp2.getElement();
		
		if(e1.estVivant()) {
			if(e2.estVivant()) {
				// tous les deux vivants : reference RMI
				res = vp2.getRefRMI() - this.getRefRMI();
			} else {
				// vivant avant mort
				res = -1;
			}
		} else {
			if(e2.estVivant()) {
				// vivant avant mort
				res = 1;
			} else {
				// tous les deux morts : celui mort le plus tard avant
				res = vp2.getTourMort() - this.getTourMort();
			}
		}
		
		
		return res;
	}
}















