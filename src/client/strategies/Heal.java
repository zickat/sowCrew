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
import serveur.IArene;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.interaction.Soigner;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * @author val
 * Strategie du soigneur
 */
public class Heal extends StrategiePersonnage {

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
	public Heal(String ipArene, int port, String ipConsole, Personnage pers, int nbTours, Point position,
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
		if(!v.hasNext()){
			arene.setPhrase(refRMI, "Je me balade !");
			arene.deplace(refRMI, 0);
		}else{
			int amis = v.amisLePlusProche(arene);
			if(amis != 0){
				int distance = Calculs.distanceChebyshev(position, arene.getPosition(amis));
				Element autre = arene.elementFromRef(amis);
				if(distance <= Constantes.DISTANCE_MIN_INTERACTION){
					arene.setPhrase(refRMI, "Reste avec moi "+autre.getNom());
					//arene.soigner(refRMI, amis);
					arene.interagir(refRMI, amis, Soigner.class);
				}else{
					arene.setPhrase(refRMI, "Viens la, mon amis que je te soigne !");
					arene.deplace(refRMI, amis);
				}
			}
		}
	}
	
	

}
