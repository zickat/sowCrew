/**
 * 
 */
package serveur.element.personnages;

import java.awt.Point;
import java.util.HashMap;

import client.strategies.Separation;
import lanceur.LanceurPerso;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Personnage;

/**
 * @author val
 * Ce personnage se separe en deux quand il est attaqué
 */
public class Separator extends Personnage {

	private static final long serialVersionUID = 8603081762987081002L;

	/**
	 * @param nom Nom du personnage
	 * @param groupe Groupe du personnage
	 * @param caracts caracteristiques du personnage
	 */
	public Separator(String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts);
	}
	
	/**
	 * @param nom Nom du personnage
	 * @param groupe Groupe du personnage
	 */
	public Separator(String nom, String groupe) {
		super(nom, groupe, new HashMap<Caracteristique, Integer>());
	}
	
	public void seSeparer(Point nouveauSeparator){
		incrementeCaract(Caracteristique.VIE, -getCaract(Caracteristique.VIE)/2);
		incrementeCaract(Caracteristique.FORCE, -getCaract(Caracteristique.FORCE)/2);
		incrementeCaract(Caracteristique.VIE, -getCaract(Caracteristique.INITIATIVE)/2);
		//System.err.println("Je me separe !!");
		new Thread(new LanceurPerso(new Separator(nom, groupe, caracts),nouveauSeparator)).start();
	}

	/* (non-Javadoc)
	 * @see serveur.element.Personnage#strategie(java.lang.String, int, java.lang.String, int, java.awt.Point, logger.LoggerProjet)
	 */
	@Override
	public void strategie(String ipArene, int port, String ipConsole, int nbTours, Point position,
			LoggerProjet logger) {
		new Separation(ipArene, port, ipConsole, this, nbTours, position, logger);
	}
	
	

}
