package client;


import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Element;
import serveur.element.Monstre;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Strategie d'un monstre.
 */
public class StrategieMonstre {

    /**
     * Console permettant d'ajouter une phrase et de recuperer le serveur
     * (l'arene).
     */
    protected Console console;

    /**
     * Cree un monstre, la console associe et sa strategie.
     *
     * @param ipArene   ip de communication avec l'arene
     * @param port      port de communication avec l'arene
     * @param ipConsole ip de la console du monstre
     * @param nbTours   nombre de tours pour ce monstre (si negatif, illimite)
     * @param position  position initiale du monstre dans l'arene
     * @param logger    gestionnaire de log
     */
    public StrategieMonstre(String ipArene, int port, String ipConsole,
                            int nbTours, Point position, LoggerProjet logger) {

        logger.info("Lanceur", "Creation de la console...");

        try {
            console = new Console(ipArene, port, ipConsole, this,
                    new Monstre(),
                    nbTours, position, logger);
            logger.info("Lanceur", "Creation de la console reussie");

        } catch (Exception e) {
            logger.info("Monstre", "Erreur lors de la creation de la console : \n" + e.toString());
            e.printStackTrace();
        }
    }

    // TODO etablir une strategie afin d'evoluer dans l'arene de combat
    // une proposition de strategie (simple) est donnee ci-dessous

    /**
     * Decrit la strategie.
     * Les methodes pour evoluer dans le jeu doivent etre les methodes RMI
     * de Arene et de ConsoleMonstre.
     *
     * @param voisins element voisins de cet element (elements qu'il voit)
     * @throws RemoteException
     */
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

        int refCible = Calculs.chercheElementProche(position, voisins);
        Element elemPlusProche = arene.elementFromRef(refCible);
        while (refCible != 0) {
            if (!((elemPlusProche instanceof Monstre) || (elemPlusProche instanceof Potion))) break;
            voisins.remove(refCible);
            refCible = Calculs.chercheElementProche(position, voisins);
            elemPlusProche = arene.elementFromRef(refCible);
        }

        if (refCible == 0) { // je n'ai pas de voisins, j'erre
            console.setPhrase("*En quête d'une proie*");
            arene.deplace(refRMI, 0);
        } else {
            int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

            if (distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
                // duel
                console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
                arene.lanceAttaque(refRMI, refCible);

            } else { // si voisins, mais plus eloignes
                // je vais vers le plus proche
                console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
                arene.deplace(refRMI, refCible);
            }
        }
    }


}
