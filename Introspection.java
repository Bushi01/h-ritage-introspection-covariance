import java.lang.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;

import java.util.*;
import java.util.Set;
import java.util.HashSet;

/*
*retourne la liste des methodes heritees et non redefinies localement en prenant en compte la covariance.
*/
public class Introspection{
    
    	public static Set<Method> getHeritees(String nomDeLaClasse) throws ClassNotFoundException {
		Set<Method> res = new HashSet<>();
		Set<Method> appui = new HashSet<>();//les methodes des classes des noeuds en dessous
		Set<Method> trop = new HashSet<>();//les classes redefinies

		try {
			// la classe donnee. La feuille du parcours
			Class cl = Class.forName(nomDeLaClasse);
			// variables qui acceuillent les sous et super classes
			Class sousCl = cl;
			Class superCl = cl.getSuperclass();

			while (superCl != null) {

				Method[] methodesSousCl = sousCl.getDeclaredMethods();
				Method[] methodesSuperCl = superCl.getDeclaredMethods();
				
				for (Method methode : methodesSousCl) {
					appui.add(methode);
				}

				// ajout de toutes les methodes des super-classes si non privees ou abstraites
				// dans res
				for (Method methodeSuperCl : methodesSuperCl) {
					
					// selection des methodes heritees de la super-classe
					int i = methodeSuperCl.getModifiers();

					if ((Modifier.isPublic(i) || Modifier.isProtected(i)) && !Modifier.isAbstract(i)) {
						res.add(methodeSuperCl);
					}

					// selection des methodes redefinies
					if (containsMethod(appui, methodeSuperCl)) {
						trop.add(methodeSuperCl);
					}

				}

				sousCl = superCl;
				superCl = superCl.getSuperclass();
			}

		} catch (ClassNotFoundException cnfe) {
		}

		res.removeAll(trop);// retrait methodes en trop
		
		return res;
	}

	public static boolean containsMethod(Set<Method> list, Method m) {
		// parcours de la liste
		for (Method methode : list) {
			// test du nom, test des parametres, test du type retour
			if (methode.getName().equals(m.getName()) && typeParam(methode, m) && typeRetourCov(methode, m)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * retourne true si les deux methodes ont les memes parametres
	 * 
	 * @return boolean
	 */
	public static boolean typeParam(Method mA, Method mB) {
		Class<?>[] pA = mA.getParameterTypes();// parametres classe A super-classe de B
		Class<?>[] pB = mB.getParameterTypes();// parametres classe B sous-classe de A
		// comparaison de nombre
		if (pA.length != pB.length) {
			return false;
		} // si pas le meme nombre de param alors false
			// comparaison des parametres
		for (int i = 0; i < pA.length; i++) {
			if (pA[i] != pB[i])
				return false; // si un param n'est pas commun alors false
		}
		return true;
	}

	/**
	 * retourne true si les deux methodes passees en parametre ont le meme type de
	 * retour ou sous-type. @ return boolean
	 */
	public static boolean typeRetourCov(Method mA, Method mB) {
		boolean res = false;
		Class<?> clA = mA.getReturnType();// type retour classe A super-classe de B
		Class<?> clB = mB.getReturnType();// type retour classe B sous-classe de A
		if (clA == clB) {
			return true;
		}
		// prise en compte de la covariance:
		while (clB != null) {
			clB = clB.getSuperclass(); // je remonte d'un noeud
			if (clA == clB) {
				return true;
			}
		}
		return res;
	}



    public static void main(String[] args) throws ClassNotFoundException{
        for(Method m : Introspection.getHeritees("java.util.AbstractCollection")){
        System.out.println(m);
        }

        //int i=1;
        //10 attendues ok
        /*for(Method m : Introspection.getHeritees("java.util.AbstractCollection")){
        System.out.println(i+" "+m);
        i++;
        }*/

        //19 attendues ok
        /*for(Method m : Introspection.getHeritees("java.util.AbstractSet")){
        System.out.println(i+" "+m);
        i++;
        }*/

        //57 attendues ok
        /*for(Method m : Introspection.getHeritees("java.util.Stack")){
        System.out.println(i+" "+m);
        i++;
        }*/

        //7 attendues ok
        /*for(Method m : Introspection.getHeritees("java.util.Vector")){
        System.out.println(i+" "+m);
        i++;
        }*/

    }
}