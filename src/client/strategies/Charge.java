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
import serveur.element.Personnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * @author val
 *
 */
public class Charge extends StrategiePersonnage {

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
	public Charge(String ipArene, int port, String ipConsole, Personnage pers, int nbTours, Point position,
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
		//arene.seCamouflerOuDeCamoufler(refRMI);
		Personnage p = (Personnage)arene.elementFromRef(refRMI);
		Voisins v = new Voisins(voisins, position, p);
		int refEnnemi = v.ennemisLePlusProche(arene); 
		if(refEnnemi == 0){
			v.init();
			int refAmis = v.amisLePlusProche(arene);
			if(refAmis == 0){
				arene.setPhrase(refRMI, "Quelqu'un a un Curly ?");
				arene.deplace(refRMI, 0);
			}else{
				int distance = Calculs.distanceChebyshev(position, arene.getPosition(refAmis));
				if(distance < Constantes.DISTANCE_MIN_INTERACTION){
					arene.setPhrase(refRMI, "Tu veut etre mon amis ? tu veut etre mon amis ? !");
				}else{
					Point destination = Calculs.meilleurPoint(arene.getPosition(refAmis), position, voisins);
					arene.setPhrase(refRMI, "Oh un amis la bas !");
					arene.teleporte(refRMI, destination);
				}
			}
		}else{
			int distance = Calculs.distanceChebyshev(position, arene.getPosition(refEnnemi));
			if(distance < Constantes.DISTANCE_MIN_INTERACTION){
				arene.setPhrase(refRMI, "Mon epee va faire couler votre sang !");
				arene.lanceAttaque(refRMI, refEnnemi);
			}else{
				Point destination = Calculs.meilleurPoint(arene.getPosition(refEnnemi), position, voisins);
				arene.setPhrase(refRMI, "Chargez "+ arene.elementFromRef(refEnnemi).getNom()+"!!");
				arene.teleporte(refRMI, destination);
			}
		}
	}

}
