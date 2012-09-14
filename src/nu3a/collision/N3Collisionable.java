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

/**
 * Define una interfaz que un objeto que puede colisionar debe implementar.
 */
public interface N3Collisionable {
	/**
	 * Comprueba si el objeto colisiona con otro objeto N3Collisionable.
	 * 
	 * @param c
	 *            Objeto con el que comprobar si hay colision.
	 * @return Indica si el objeto colisiona.
	 */
	public boolean test(N3Collisionable c);

	/**
	 * Permite indicar si el objeto puede colisionar con otros objetos.
	 * 
	 * @param c
	 *            Indica si el objeto puede colisionar o no.
	 */
	public void setCollisionable(boolean c);

	/**
	 * Indica si el objeto puede colisionar con otro objeto o no.
	 * 
	 * @return Si el objeto puede colisionar con otro objeto o no.
	 */
	public boolean getCollisionable();
}
