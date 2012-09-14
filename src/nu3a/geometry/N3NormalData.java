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

package nu3a.geometry;

import java.util.Vector;

import nu3a.math.N3Vector2D;
import nu3a.math.N3Vector3D;

/**
 * Esta clase contiene informaci�n de normales de una forma gen�rica, y permite
 * su recorrido secuencial.
 */
public class N3NormalData {
	/**
	 * Vector de las normales.
	 */
	protected Vector normals;

	/**
	 * Posici�n el el vector de la siguiente normal a obtener con un acceso
	 * secuencial.
	 */
	protected int pos;

	/**
	 * �ltima normal obtenida.
	 */
	protected N3Vector2D lastNormal;

	/**
	 * Crea una instancia de la clase.
	 */
	public N3NormalData() {
		normals = new Vector(0, 1);
		lastNormal = new N3Vector3D(0, 0, 1);
		begin();
	}

	/**
	 * A�ade una normal al contenedor.
	 * 
	 * @param c
	 *            Normal a a�adir
	 */
	public void addNormal(N3Vector2D c) {
		normals.add(c);
	}

	/**
	 * Elimina una normal del contenedor.
	 * 
	 * @param i
	 *            �ndice de la normal a eliminar
	 * @return True si se ha eliminado; False en caso contrario
	 */
	public boolean removeNormal(int i) {
		if (i >= normals.size()) {
			normals.removeElementAt(i);
			return true;
		} else
			return false;
	}

	/**
	 * Elimina una normal del contenedor.
	 * 
	 * @param c
	 *            Normal a eliminar
	 * @return True si se ha eliminado; False en caso contrario
	 */
	public boolean removeNormal(N3Vector2D c) {
		return normals.remove(c);
	}

	public N3Vector2D getNormal(int i) {
		if (i >= normals.size())
			return ((N3Vector2D) normals.elementAt(i));
		else
			return null;
	}

	/**
	 * Posiciona al contenedor en el primer elemento.
	 */
	public void begin() {
		pos = 0;
	}

	/**
	 * Indica si el contenedor est� vac�o.
	 * 
	 * @return True si el contenedor est� vac�o; False en caso contrario
	 */
	public boolean isEmpty() {
		return normals.isEmpty();
	}

	/**
	 * Indica si se pueden devolver m�s objetos secuencialmente.
	 * 
	 * @return True si se pueden devolver m�s objetos; False en caso contrario.
	 */
	public boolean hasNext() {
		return (!normals.isEmpty() && pos < normals.size());
	}

	/**
	 * Obtiene el siguiente objeto del contenedor en orden secuencial.
	 * 
	 * @return Siguiente objeto del contenedor, o �ltimo, si no quedan m�s
	 */
	public N3Vector2D next() {
		if (hasNext()) {
			lastNormal = (N3Vector2D) normals.elementAt(pos++);
			return lastNormal;
		} else
			return lastNormal;
	}

	/**
	 * Vac�a el contenedor.
	 */
	public void clear() {
		normals.clear();
	}
};
