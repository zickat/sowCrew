package serveur.element.personnages;

import java.awt.Point;
import java.util.HashMap;

import client.StrategiePersonnage;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Personnage;

public class Squelette extends Personnage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Squelette(String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see serveur.element.Personnage#strategie(java.lang.String, int, java.lang.String, int, java.awt.Point, logger.LoggerProjet)
	 */
	@Override
	public void strategie(String ipArene, int port, String ipConsole, int nbTours, Point position,
			LoggerProjet logger) {
		new StrategiePersonnage(ipArene, port, ipConsole, this, nbTours, position, logger);
	}

}
