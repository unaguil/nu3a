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

package nu3a.names;

import nu3a.names.exception.N3NameException;

/**
 * Esta interfaz determina los métodos que debe de cumplir cualquier clase que
 * gestione los nombres de una escena. Los objetos con nombre son objetos que
 * poseen un nombre único dentro del gestor, y pueden ser obtenidos a partir de
 * éste.
 */
public interface N3NameManager {
	/**
	 * Indica si existe algún objeto con el nombre especificado.
	 * 
	 * @param name
	 *            Nombre
	 * @return True si existe algún objeto con ese nombre; False en caso
	 *         contrario
	 */
	public boolean checkNamedObject(String name);

	/**
	 * Añade un objeto con nombre al gestor.
	 * 
	 * @param obj
	 *            Objeto a añadir
	 */
	public void addNamedObject(N3NamedObject obj) throws N3NameException;

	/**
	 * Elimina un objeto con nombre del gestor.
	 * 
	 * @param obj
	 *            Objeto a eliminar
	 */
	public void removeNamedObject(N3NamedObject obj);

	/**
	 * Obtiene un objeto según su nombre.
	 * 
	 * @param name
	 *            Nombre del objeto a obtener
	 * @return Objeto
	 */
	public N3NamedObject getNamedObject(String name);
}
