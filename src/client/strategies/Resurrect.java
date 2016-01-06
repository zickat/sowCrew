/**
 * 
 */
package client.strategies;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.StrategiePersonnage;
import client.Voisins;
import logger.LoggerProjet;
import serveur.Arene;
import serveur.IArene;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.element.personnages.Necromancien;
import serveur.interaction.DuelLifeSteal;
import serveur.interaction.Interaction;
import serveur.interaction.Necromancie;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * @author Aym
 * Strategie du nécromancien
 */
public class Resurrect extends StrategiePersonnage {
	
	private int compteurMort =0;

	/**
	 * Cree la console associe et la strategie associe a un personnage
	 * @param ipArene ip de communication avec l'arene
	 * @param port port de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param Le personnage associe a la strategie
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position initiale du personnage dans l'arene
	 * @param logger gestionnaire de log
	 */
	public Resurrect(String ipArene, int port, String ipConsole, Personnage pers, int nbTours, Point position,
			LoggerProjet logger) {
		super(ipArene, port, ipConsole, pers, nbTours, position, logger);
	}

	/* (non-Javadoc)
	 * @see client.StrategiePersonnage#executeStrategie(java.util.HashMap)
	 */

	@Override
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		int i;
		IArene arene = console.getArene();
		i = arene.getPersonnagesMorts().size();
		// reference RMI de l'element courant
		int refRMI = 0;
		// position de l'element courant
		Point position = null;
		try {
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if(voisins.isEmpty()){
			console.setPhrase("Ouf je suis seul");
			if(i > compteurMort){
				arene.interagir(refRMI, refRMI, Necromancie.class);
				compteurMort++;
			}else{
				arene.deplace(refRMI, 0);
			}
		}else{
			int refCible = Calculs.chercheElementProche(position, voisins);
			Element elemPlusProche = arene.elementFromRef(refCible);
			//potion : c'est cool
			if(elemPlusProche  instanceof Potion){
			}else if(elemPlusProche.getGroupe() != this.console.getPersonnage().getGroupe()){
				//personnage : on l'evite
				console.setPhrase("Oh non un mechant !");
				Point echapatoir = Calculs.directionOpposee(position, arene.getPosition(refCible));
				i = arene.getPersonnagesMorts().size();
				if(i > compteurMort){
					arene.interagir(refRMI, refRMI, Necromancie.class);
					compteurMort++;
				}else{
					arene.deplace(refRMI, echapatoir);
				}
			}else{
				console.setPhrase("Ouf il n'y a que des potos");
				if(i > compteurMort){
					arene.interagir(refRMI, refRMI, Necromancie.class);
					compteurMort++;
				}else{
					arene.deplace(refRMI, 0);
				}
			}
		}
	}
}