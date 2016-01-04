/**
 * 
 */
package serveur.element.personnages;

import java.awt.Point;
import java.util.HashMap;

import client.strategies.Camouflage;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Personnage;

/**
 * @author val
 *
 */
public class Voleur extends Personnage {

	
	private static final long serialVersionUID = 1L;

	/**
	 * @param nom
	 * @param groupe
	 */
	public Voleur(String nom, String groupe) {
		super(nom, groupe, new HashMap<Caracteristique, Integer>());
	}

	@Override
	public void strategie(String ipArene, int port, String ipConsole, int nbTours, Point position,
			LoggerProjet logger) {
		new Camouflage(ipArene, port, ipConsole, this, nbTours, position, logger);
	}

}
