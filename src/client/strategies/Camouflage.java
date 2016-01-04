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
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * @author valen
 *
 */
public class Camouflage extends StrategiePersonnage {

	Boolean aDejaAttaque = false;
	
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
	public Camouflage(String ipArene, int port, String ipConsole, Personnage pers, int nbTours, Point position,
			LoggerProjet logger) {
		super(ipArene, port, ipConsole, pers, nbTours, position, logger);
	}

	@Override
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		IArene arene = console.getArene();
		int refRMI = 0;
		Point position = null;
		try {
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch 
		(RemoteException e) {
			e.printStackTrace();
		}
		VuePersonnage vp = (VuePersonnage) arene.vueFromRef(refRMI);
		Personnage p = (Personnage)arene.elementFromRef(refRMI);
		if(!aDejaAttaque && vp.isVisible()){
			arene.seCamouflerOuDeCamoufler(refRMI);
		}
		Voisins v = new Voisins(voisins, position, p);
		int refEnnemi = v.ennemisLePlusProche(arene); 
		if(refEnnemi == 0){
			if(vp.isVisible()){
				aDejaAttaque = false;
			}
			arene.setPhrase(refRMI, "Je me la coule douche pas d'ennemis en vue !");
			arene.deplace(refRMI, 0);
		}else{
			int distance = Calculs.distanceChebyshev(position, arene.getPosition(refEnnemi));
			if(distance < Constantes.DISTANCE_MIN_INTERACTION){
				if(!vp.isVisible()){
					aDejaAttaque = true;
					arene.seCamouflerOuDeCamoufler(refRMI);
				}
				arene.setPhrase(refRMI, "Mon epee va faire couler votre sang !");
				arene.lanceAttaque(refRMI, refEnnemi);
			}else{
				Point destination = Calculs.meilleurPoint(arene.getPosition(refEnnemi), position, voisins);
				arene.setPhrase(refRMI, "Chragez "+ arene.elementFromRef(refEnnemi).getNom()+"!!");
				arene.teleporte(refRMI, destination);
			}
		}
	}

}
