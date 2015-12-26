/**
 * 
 */
package serveur.element.personnages;

import java.awt.Point;
import java.util.HashMap;

import client.strategies.Heal;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Personnage;

/**
 * @author val
 *
 */
public class Soigneur extends Personnage {

	private static final long serialVersionUID = 1L;

	/**
	 * Cree un soigneur avec un nom et un groupe.
	 * @param nom du personnage
	 * @param groupe d'etudiants du personnage
	 * @param caracts caracteristiques du personnage
	 */
	public Soigneur(String nom, String groupe) {
		super(nom, groupe, new HashMap<Caracteristique, Integer>());
		incrementeCaract(Caracteristique.FORCE, 30);
	}

	/* (non-Javadoc)
	 * @see serveur.element.Personnage#strategie(java.lang.String, int, java.lang.String, int, java.awt.Point, logger.LoggerProjet)
	 */
	@Override
	public void strategie(String ipArene, int port, String ipConsole, int nbTours, Point position,
			LoggerProjet logger) {
		new Heal(ipArene, port, ipConsole, this, nbTours, position, logger);
	}

}
