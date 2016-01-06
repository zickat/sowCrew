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
import serveur.interaction.DuelLifeSteal;
import serveur.interaction.Interaction;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * @author Aym	
 * Stratégie de la liche
 */
public class LifeSteal extends StrategiePersonnage {

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
	public LifeSteal(String ipArene, int port, String ipConsole, Personnage pers, int nbTours, Point position,
			LoggerProjet logger) {
		super(ipArene, port, ipConsole, pers, nbTours, position, logger);
	}

	/* (non-Javadoc)
	 * @see client.StrategiePersonnage#executeStrategie(java.util.HashMap)
	 */
	@Override
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		IArene arene = console.getArene();
		int refRMI = 0;
		Point position = null;
		
		try {
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		Personnage moi = (Personnage) arene.elementFromConsole(console);
		Voisins v = new Voisins(voisins, position,moi);
		int ennemis = v.ennemisLePlusProche(arene);
		if(ennemis ==0){
			arene.setPhrase(refRMI, "Je me balade !");
			arene.deplace(refRMI, 0);
		}else{
				int distance = Calculs.distanceChebyshev(position, arene.getPosition(ennemis));
				Element autre = arene.elementFromRef(ennemis);
				if(distance <= Constantes.DISTANCE_MIN_INTERACTION){
					VuePersonnage maVue = (VuePersonnage) arene.vueFromRef(refRMI);
					VuePersonnage vueEnnemis = (VuePersonnage) arene.vueFromRef(ennemis);
					arene.setPhrase(refRMI, "Viens te battre "+autre.getNom());
					arene.interagir(refRMI, ennemis, DuelLifeSteal.class);
				}else{
					arene.setPhrase(refRMI, "Viens là que j'aspire ta vie ! !");
					arene.deplace(refRMI, ennemis);
				}
		}
	}
}
