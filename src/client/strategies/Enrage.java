package client.strategies;
import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.StrategiePersonnage;
import client.Voisins;
import logger.LoggerProjet;
import serveur.Arene;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.personnages.Berserker;
import serveur.element.personnages.Separator;
import serveur.interaction.Enrager;
import serveur.interaction.Soigner;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class Enrage extends StrategiePersonnage{

	private int lastVie;
	/**
	 * 
	 * @param ipArene
	 * @param port
	 * @param ipConsole
	 * @param pers
	 * @param nbTours
	 * @param position
	 * @param logger
	 */
	public Enrage(String ipArene, int port, String ipConsole, Personnage pers, int nbTours, Point position,
			LoggerProjet logger) {
		super(ipArene, port, ipConsole, pers, nbTours, position, logger);
		lastVie = pers.getCaract(Caracteristique.VIE);
		// TODO Auto-generated constructor stub
	}
	
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
		int newvie = moi.getCaract(Caracteristique.VIE);
		if ( newvie < lastVie){
			arene.interagir(refRMI,refRMI,Enrager.class);
		}
		lastVie = newvie;
		Voisins v = new Voisins(voisins, position,moi);
		if(!v.hasNext()){
			arene.setPhrase(refRMI, "Je me balade !");
			arene.deplace(refRMI, 0);
		}else{
			int ennemis = v.ennemisLePlusProche(arene);
			if(ennemis != 0){
				int distance = Calculs.distanceChebyshev(position, arene.getPosition(ennemis));
				Element autre = arene.elementFromRef(ennemis);
				if(distance <= Constantes.DISTANCE_MIN_INTERACTION){
					arene.setPhrase(refRMI, "Reste avec moi "+autre.getNom());
					arene.lanceAttaque(refRMI,ennemis);
					
				}else{
					arene.setPhrase(refRMI, "Je vous tout casser");
					arene.deplace(refRMI, ennemis);
				}
			}
		}
	}
	

}
