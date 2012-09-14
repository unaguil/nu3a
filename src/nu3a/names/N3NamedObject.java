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
 * Esta interfaz determina los métodos que debe de cumplir un objeto con nombre,
 * para que pueda ser añadido a un gestor de nombres, y posteriormente
 * recuperado a partir de su nombre.
 */
public interface N3NamedObject {
	/**
	 * Establece el gestor de nombres para el objeto.
	 * 
	 * @param nm
	 *            Gestor de Nombres
	 */
	public void setNameManager(N3NameManager nm);

	/**
	 * Establece el nombre del objeto. Ha de ser único para el gestor de nombres
	 * del objeto.
	 * 
	 * @param name
	 *            Nombre del objeto
	 */
	public void setName(String name) throws N3NameException;

	/**
	 * Obtiene el nombre del objeto.
	 * 
	 * @return Nombre del objeto
	 */
	public String getName();
}
