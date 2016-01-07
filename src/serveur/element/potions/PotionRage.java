/**
 * Potion sacrifiant de la vie pour de la force
 */

package serveur.element.potions;

import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.element.Potion;

/**
 * @author Solen
 *
 */

public class PotionRage extends Potion {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur d'une potion de rage avec le groupe le groupe qui l'a envoyee
	 * @param groupe groupe d'etudiants de la potion
	 */
	public PotionRage(String groupe) {
		super("Potion de rage", groupe, new HashMap<Caracteristique, Integer>()) ;
		caracts.put(Caracteristique.VIE, -30) ;
		caracts.put(Caracteristique.FORCE, 50) ;
		caracts.put(Caracteristique.INITIATIVE, 0) ;
		caracts.put(Caracteristique.VITESSE, 0) ;
	}

}
