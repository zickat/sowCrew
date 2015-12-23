package client.strategies;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.StrategiePersonnage;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class Fuite extends StrategiePersonnage {

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
	public Fuite(String ipArene, int port, String ipConsole, Personnage pers, int nbTours, Point position,
			LoggerProjet logger) {
		super(ipArene, port, ipConsole, pers, nbTours, position, logger);
	}

	@Override
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		IArene arene = console.getArene();
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
			arene.deplace(refRMI, 0);
		}else{
			int refCible = Calculs.chercheElementProche(position, voisins);
			Element elemPlusProche = arene.elementFromRef(refCible);
			//potion : c'est cool
			if(elemPlusProche instanceof Potion){
				int distance = Calculs.distanceChebyshev(position, arene.getPosition(refCible));
				if(distance <= Constantes.DISTANCE_MIN_INTERACTION){
					// ramassage
					console.setPhrase("Je ramasse une potion");
					arene.ramassePotion(refRMI, refCible);
				}else{
					//j'y vais
					console.setPhrase("Je vais vers la potion " + elemPlusProche.getNom());
					arene.deplace(refRMI, refCible);
				}
			}else{
				//personnage : on l'evite
				console.setPhrase("Oh non un mechant !");
				Point echapatoir = Calculs.directionOpposee(position, arene.getPosition(refCible));
				arene.deplace(refRMI, echapatoir);
			}
		}
	}

}
