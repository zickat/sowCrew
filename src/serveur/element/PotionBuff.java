/**
 * 
 */
package serveur.element;

/**
 * @author valen
 *
 */
public class PotionBuff extends Potion {

	private static final long serialVersionUID = -3083767195774998877L;

	private DotHot dh;
	
	/**
	 * @param nom
	 * @param groupe
	 * @param caracts
	 */
	public PotionBuff(String nom, String groupe, DotHot dh) {
		super(nom, groupe, Caracteristique.mapCaracteristiquesNul());
		this.dh = dh;
	}

	public DotHot getDotHot() {
		return dh;
	}

}
