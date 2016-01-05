/**
 * 
 */
package serveur.element;

import java.util.HashMap;

/**
 * @author valen
 * Permet de gerer les DOT (Degat on time) et les HOT (Heal on time)
 */
public class DotHot extends Element {

	private static final long serialVersionUID = 1L;
	
	private int nbTours;
	
	/**
	 * @param nom Nom du DotHot
	 * @param caracts ses caracteristiques
	 * @param nbTour le nombre de tour ou il sera actif
	 */
	public DotHot(String nom, HashMap<Caracteristique, Integer> caracts, int nbTours) {
		super(nom, "BuffHot", caracts);
		this.nbTours = nbTours;
	}

	public int getNbTours() {
		return nbTours;
	}
	
	public void decrementerNbTour(){
		nbTours--;
	}

}
