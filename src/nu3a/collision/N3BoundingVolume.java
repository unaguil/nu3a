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
import nu3a.render.N3Render;

/**
 * Esta clase define un volumen de contenci�n generico.
 */
abstract public class N3BoundingVolume implements N3Collisionable {
	/**
	 * Transforma el volumen segun una matriz.
	 * 
	 * @param m
	 *            Matriz por la que transformar el volumen.
	 */
	abstract public void calcule(N3Matrix4D m);

	/**
	 * Copia los datos internos del volumen indicado.
	 * 
	 * @param bv
	 *            Volumen del que copiar los datos.
	 */
	abstract public void setData(N3BoundingVolume bv);

	/**
	 * A�ade al volumen actual el volumen indicado.
	 * 
	 * @param bv
	 *            Volumen a a�adir.
	 */
	abstract public void add(N3BoundingVolume bv);

	/**
	 * Permite comprobar si el volumen intersecciona con otro volumen.
	 * 
	 * @param bv
	 *            Volumen con el que realizar el test.
	 * @return Indica si el objeto colisiona.
	 */
	abstract public boolean test(N3BoundingVolume bv);

	/**
	 * Dibuja el volumen en el render especificado.
	 * 
	 * @param render
	 *            Render en el que dibujar el volumen.
	 */
	abstract public void draw(N3Render render);

	public void setCollisionable(boolean c) {
	}

	public boolean getCollisionable() {
		return true;
	}

	public boolean test(N3Collisionable c) {
		if (c instanceof N3BoundingVolume)
			((N3BoundingVolume) c).test(this);
		return c.test(this);
	}
}
