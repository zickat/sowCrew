package lanceur;

import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class LanceurPerso {

	Personnage p;
	
	public LanceurPerso(Personnage p) {
		this.p = p;
	}
	
	public void lancer(){
		int nbTours = Constantes.NB_TOURS_PERSONNAGE_DEFAUT;
		int port = Constantes.PORT_DEFAUT;
		String ipArene = Constantes.IP_DEFAUT;
		LoggerProjet logger = null;
		try {
			logger = new LoggerProjet(true, "personnage_"+p.getNom()+p.getGroupe());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		try {
			String ipConsole = InetAddress.getLocalHost().getHostAddress();
			
			logger.info("Lanceur", "Creation du personnage...");
			
			// caracteristiques du personnage
			HashMap<Caracteristique, Integer> caracts = new HashMap<Caracteristique, Integer>();
			// seule la force n'a pas sa valeur par defaut (exemple)
			caracts.put(Caracteristique.FORCE, 
					Calculs.valeurCaracAleatoire(Caracteristique.FORCE)); 
			
			Point position = Calculs.positionAleatoireArene();
			p.strategie(ipArene, port, ipConsole, nbTours, position, logger);
			logger.info("Lanceur", "Creation du personnage reussie");
			
		} catch (Exception e) {
			logger.severe("Lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
	}

}
