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

package nu3a.scene;

import nu3a.names.exception.N3NameException;

/**
 * Clase abstracta que representa la base de los objetos de la escena que no
 * tendr�n nodos hijos, es decir, las hojas del �rbol de escena.
 */
public abstract class N3LeafNode extends N3Node {
	/**
	 * Crea una instancia del nodo hoja para la escena especificada.
	 * 
	 * @param scene
	 *            Escena a la que pertenece el nodo.
	 * @param name
	 *            Nombre del nodo
	 */
	protected N3LeafNode(N3Scene scene, String name) throws N3NameException {
		super(scene, name);
	}
}
