/**
 * Potion régénérant la vie au maximum et mettant l'initiative à 0
 */

package serveur.element.potions;

import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.element.Potion;

/**
 * @author Solen
 *
 */

public class PotionVie extends Potion {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur d'une potion de vie avec le groupe le groupe qui l'a envoyee
	 * @param groupe groupe d'etudiants de la potion
	 */
	public PotionVie(String groupe) {
		super("Potion de vie", groupe, new HashMap<Caracteristique, Integer>()) ;
		caracts.put(Caracteristique.VIE, 100) ;
		caracts.put(Caracteristique.FORCE, 0) ;
		caracts.put(Caracteristique.INITIATIVE, -100) ;
		caracts.put(Caracteristique.VITESSE, 0) ;
	}

}
