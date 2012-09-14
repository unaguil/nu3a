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

package nu3a.util;

/**
 * Interfaz que especifica que un objeto puede avisar de sus cambios a otros.
 */
public interface Observable {
	/**
	 * Añade un objeto para notificarle algún evento.
	 * 
	 * @param Objeto
	 */
	public void registryObserver(Observer o);

	/**
	 * Elimina un objeto para dejar de notificarle algún evento.
	 * 
	 * @param Objeto
	 */
	public void removeObserver(Observer o);

	/**
	 * Avisa a todos los objetos de que se ha producido el evento.
	 */
	public void notifyObservers();
}
