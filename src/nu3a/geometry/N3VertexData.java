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

/**
 * Esta clase contiene informaci�n de vertices de una forma generica, y permite
 * su recorrido secuencial.
 */
public class N3VertexData {
	/**
	 * Vector de los v�rtices.
	 */
	protected Vector vertexes;

	/**
	 * Posici�n el el vector del siguiente vector a obtener con un acceso
	 * secuencial.
	 */
	protected int pos;

	/**
	 * �ltimo v�rtice obtenido.
	 */
	protected N3Point2D lastVertex;

	/**
	 * Crea una instancia de la clase.
	 */
	public N3VertexData() {
		vertexes = new Vector(0, 1);
		begin();
	}

	/**
	 * A�ade un v�rtice al contenedor.
	 * 
	 * @param c
	 *            V�rtice a a�adir
	 */
	public void addVertex(N3Point2D c) {
		vertexes.add(c);
	}

	/**
	 * Elimina un v�rtice del contenedor.
	 * 
	 * @param i
	 *            �ndice del v�rtice a eliminar
	 * @return True si se ha eliminado; False en caso contrario
	 */
	public boolean removeVertex(int i) {
		if (i >= vertexes.size()) {
			vertexes.removeElementAt(i);
			return true;
		} else
			return false;
	}

	/**
	 * Elimina un v�rtice del contenedor.
	 * 
	 * @param c
	 *            V�rtice a eliminar
	 * @return True si se ha eliminado; False en caso contrario
	 */
	public boolean removeVertex(N3Point2D c) {
		return vertexes.remove(c);
	}

	/**
	 * Obtiene el v�rtice en la posici�n indicada.
	 * 
	 * @param i
	 *            �ndice del v�rtice
	 * @return V�rtice i-�simo
	 */
	public N3Point2D getVertex(int i) {
		if (i >= vertexes.size())
			return ((N3Point2D) vertexes.elementAt(i));
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
		return vertexes.isEmpty();
	}

	/**
	 * Indica si se pueden devolver m�s objetos secuencialmente.
	 * 
	 * @return True si se pueden devolver m�s objetos; False en caso contrario.
	 */
	public boolean hasNext() {
		return (!vertexes.isEmpty() && pos < vertexes.size());
	}

	/**
	 * Obtiene el siguiente objeto del contenedor en orden secuencial.
	 * 
	 * @return Siguiente objeto del contenedor, o �ltimo, si no quedan m�s
	 */
	public N3Point2D next() {
		if (hasNext()) {
			lastVertex = (N3Point2D) vertexes.elementAt(pos++);
			return lastVertex;
		} else
			return lastVertex;
	}

	/**
	 * Vac�a el contenedor.
	 */
	public void clear() {
		vertexes.clear();
	}
};
