/**
 * Choix de prendre une potion si elle est benefique
 */
package client.strategies;

import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.Potion;

/**
 * @author Solen
 *
 */
public class PrendrePotion {
	
	private Personnage personnage ;
	private Potion potion ;
	
	/**
	 * Constructeur
	 * @param personnage
	 * @param potion vue
	 */
	public PrendrePotion(Element personnage, Element potion) {
		this.personnage = (Personnage) personnage ;
		this.potion = (Potion) potion ;
	}

	/**
	 * Renvoie si le personnage doit prendre une potion vue
	 * @return boolean si potion benefique
	 */
	public boolean doitPrendrePotion() {
		if (personnage.getCaract(Caracteristique.VIE) + potion.getCaract(Caracteristique.VIE) <= 0 || (potion.getCaract(Caracteristique.VIE) > 0 && personnage.getCaract(Caracteristique.VIE) == 100))
			return false ;
		else
			return true ;
	}
	
	

}
