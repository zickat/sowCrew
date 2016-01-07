/**
 * 
 */
package lanceur;

import java.awt.Point;
import java.io.IOException;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * @author Solen
 *
 */
public class LanceurPotion implements Runnable {
	
	/**
	 * La potion a lancer
	 */
	private Potion p ;
	
	/**
	 * La position de depart de la potion
	 */
	private Point depart = null ;

	/**
	 * Cree un lanceur avec juste une potion
	 * @param p la potion a lancer
	 */
	public LanceurPotion(Potion p) {
		this.p = p;
	}
	
	/**
	 * Cree un lanceur avec la potion et la position
	 * @param p
	 * @param depart
	 */
	public LanceurPotion(Potion p, Point depart) {
		this.p = p;
		this.depart = depart;
	}
	
	
	public void lancer() {
		// init des arguments
		int port = Constantes.PORT_DEFAUT;
		String ipArene = Constantes.IP_DEFAUT;
		
		// creation du logger
		LoggerProjet logger = null;
		try {
			logger = new LoggerProjet(true, "potion_"+p.getNom()+p.getGroupe());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
		// lancement de la potion
		try {
			IArene arene = (IArene) java.rmi.Naming.lookup(Constantes.nomRMI(ipArene, port, "Arene"));
			logger.info("Lanceur", "Lancement de la potion sur le serveur...");
			
			// ajout de la potion
			if(depart == null)
				depart = Calculs.positionAleatoireArene();
			arene.ajoutePotion(p,depart);
			logger.info("Lanceur", "Lancement de la potion reussi");
			
		} catch (Exception e) {
			logger.severe("Lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
	}

	@Override
	public void run() {
		lancer() ;
	}

}
