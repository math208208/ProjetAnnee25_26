package test.unitaire;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;

import jeu.EtatJeu;
import jeu.Jeu;
import jeu.environnement.Manoir;
import jeu.environnement.Zone;
import jeu.joueur.Joueur;

public final class TestSupport {

	private TestSupport() {
	}

	public static Jeu jeuPret(String pseudo) {
		Jeu jeu = new Jeu();
		Manoir manoir = new Manoir();
		setField(jeu, "joueur", new Joueur(pseudo));
		setField(jeu, "manoir", manoir);
		setField(jeu, "zoneCourante", manoir.getZoneDepart());
		setField(jeu, "historiqueZones", new ArrayDeque<Zone>());
		setField(jeu, "etatJeu", EtatJeu.EN_COURS);
		setField(jeu, "eclairageActif", false);
		setField(jeu, "chemineActif", false);
		return jeu;
	}

	public static void setField(Object cible, String nomChamp, Object valeur) {
		try {
			Field champ = trouverChamp(cible.getClass(), nomChamp);
			champ.setAccessible(true);
			champ.set(cible, valeur);
		} catch (ReflectiveOperationException e) {
			throw new AssertionError("Impossible de modifier le champ " + nomChamp, e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getField(Object cible, String nomChamp, Class<T> type) {
		try {
			Field champ = trouverChamp(cible.getClass(), nomChamp);
			champ.setAccessible(true);
			return (T) champ.get(cible);
		} catch (ReflectiveOperationException e) {
			throw new AssertionError("Impossible de lire le champ " + nomChamp, e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokePrivate(Object cible, String nomMethode, Class<?>[] typesParametres,
			Object... arguments) {
		try {
			Method methode = trouverMethode(cible.getClass(), nomMethode, typesParametres);
			methode.setAccessible(true);
			return (T) methode.invoke(cible, arguments);
		} catch (ReflectiveOperationException e) {
			throw new AssertionError("Impossible d'appeler la methode " + nomMethode, e);
		}
	}

	private static Field trouverChamp(Class<?> type, String nomChamp) throws NoSuchFieldException {
		Class<?> courant = type;
		while (courant != null) {
			try {
				return courant.getDeclaredField(nomChamp);
			} catch (NoSuchFieldException e) {
				courant = courant.getSuperclass();
			}
		}
		throw new NoSuchFieldException(nomChamp);
	}

	private static Method trouverMethode(Class<?> type, String nomMethode, Class<?>[] typesParametres)
			throws NoSuchMethodException {
		Class<?> courant = type;
		while (courant != null) {
			try {
				return courant.getDeclaredMethod(nomMethode, typesParametres);
			} catch (NoSuchMethodException e) {
				courant = courant.getSuperclass();
			}
		}
		throw new NoSuchMethodException(nomMethode);
	}
}
