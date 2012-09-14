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

import nu3a.math.N3Matrix4D;

/**
 * Define los metodos que debe implementar un objeto que esta contenido en un
 * volumen de colisión. Los volúmenes de colisión permite contener geometrías
 * complejas dentro de una geometría más simple.
 */
public interface N3CollisionableVolume extends N3Collisionable {
	/**
	 * Permite obtener el volumen de contención asociado al objeto.
	 * 
	 * @return Volumen asociado con el objeto.
	 */
	public N3BoundingVolume getBoundingVolume();

	/**
	 * Indica al objeto que implementa la interfaz que calcule su Bounding
	 * Volume.
	 * 
	 * @param m
	 *            Matriz con la que calcular el volumen
	 */
	public void calculeBV(N3Matrix4D m);
}
