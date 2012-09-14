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

package nu3a.names.exception;

/**
 * Excepci�n que se lanza al intentar a�adir un padre a un nodo que ya lo tiene.
 */

public class N3NameException extends Exception {
	/**
	 * Constructor de la clase.
	 * 
	 * @param message
	 *            Mensaje que indica el tipo de excepci�n producida.
	 */
	public N3NameException(String message) {
		super(message);
	}
}