/*
 *	Copyright (c) 2003 Jorge García, Unai Aguilera
 *
 *	This file is part of Nu3A.
 *
 *   Nu3A is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   Nu3A is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Nu3A.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *	Authors: Jorge García <bardok@gmail.com>, Unai Aguilera <gkalgan@gmail.com>
 */

package nu3a.collision;

import java.util.Vector;

/**
 * Define los metodos que debe implementar un objeto que ademas de poder
 * colisionar pertenece una jerarquia de colisión. Esta jerarquía de colisión
 * permite realizar el test de colisión de forma más rápida ya que permite
 * evitar facilmente el test en aquellas ramas del arbol de escena que no
 * colisionan con el objeto indicado.
 */
public interface N3CollisionableHierarchy extends N3CollisionableVolume {
	/**
	 * Comprueba si los objetos que cuelgan de la rama colisionan con el objeto
	 * indicado.
	 * 
	 * @param c
	 *            Objeto con el que comprobar si hay colisión.
	 * @return Vector de objetos N3Collisionable que colisionan con el objeto
	 *         indicado. null si no hay ninguno.
	 */
	public Vector testRec(N3Collisionable c);
}
