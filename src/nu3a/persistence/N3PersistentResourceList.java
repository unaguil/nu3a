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

package nu3a.persistence;

import java.util.Vector;

/**
 * Esta clase representa un vector de recursos persistentes
 */
public class N3PersistentResourceList {
	/**
	 * Recursos
	 */
	protected Vector resources;

	/**
	 * Crea una lista de recursos vacía.
	 */
	public N3PersistentResourceList() {
		resources = new Vector(0, 1);
	}

	/**
	 * Crea una lista de recursos del tamaño especificado, con todas sus
	 * posiciones a null.
	 * 
	 * @param size
	 *            Tamaño
	 */
	public N3PersistentResourceList(int size) {
		resources = new Vector(size, 0);
		for (int i = 0; i < size; i++)
			resources.add(null);
	}

	/**
	 * Añade un recurso al final de la lista.
	 * 
	 * @param resource
	 *            Recurso
	 */
	public void addResource(N3PersistentResource resource) {
		if (resource != null) {
			resource.getPersistentResources(this);
			if (resources.indexOf(resource) == -1)
				resources.add(resource);
		}
	}

	/**
	 * Establece el recurso como el recurso de la posición indicada.
	 * 
	 * @param resource
	 *            Recurso
	 * @param pos
	 *            Posición
	 */
	public void setResource(N3PersistentResource resource, int pos) {
		if (resource != null) {
			resources.setElementAt(resource, pos);
		}
	}

	/**
	 * Obtiene el i-ésimo recurso de la lista
	 * 
	 * @param i
	 *            Posición del recurso
	 * @return Recurso en la posición i
	 */
	public N3PersistentResource resourceAt(int i) {
		return (N3PersistentResource) resources.elementAt(i);
	}

	/**
	 * Obtiene el tamaño de la lista.
	 * 
	 * @return Tamaño
	 */
	public int size() {
		return resources.size();
	}

	/**
	 * Vacía la lista.
	 */
	public void clear() {
		resources.clear();
	}

	/**
	 * Indica la posición del recurso.
	 * 
	 * @param resource
	 *            Recurso
	 * @return Posción del recurso en la lista
	 */
	public int indexOf(N3PersistentResource resource) {
		return resources.indexOf(resource);
	}
}
