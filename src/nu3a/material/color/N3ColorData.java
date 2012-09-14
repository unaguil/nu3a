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

package nu3a.material.color;

import java.util.Vector;

/**
 * Esta clase define la informaci�n de color de un objeto visual. Los colores
 * son almacenados en la secuencia en la que son introducidos. Cuando un objeto
 * visual que contiene una referencia a esta clase es renderizado se asignan
 * colores a sus vertices en orden empezando por el primer color de la
 * secuencia. Si un objeto tiene mas vertices que colores asignados los vertices
 * restantes se renderizan con el ultimo color de la secuencia.
 */
public class N3ColorData {
	/**
	 * Vector que contiene la informacion de color.
	 */
	protected Vector colors;

	/**
	 * Posici�n actual en la secuencia.
	 */
	protected int pos;

	/**
	 * Indica si al secuencia es ciclica.
	 */
	protected boolean loop;

	/**
	 * Referencia al ultimo color devuelto.
	 */
	protected N3ColorRGBA lastColor;

	/**
	 * Constructor. Inicialmente la secuencia de colores esta vacia.
	 */
	public N3ColorData() {
		colors = new Vector(0, 1);
		lastColor = new N3ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f);
		loop = false;
		begin();
	}

	/**
	 * Permite a�adir un color a la secuencia de colores.
	 * 
	 * @param c
	 *            Color a a�adir.
	 */
	public void addColor(N3ColorRGBA c) {
		colors.add(c);
	}

	/**
	 * Permite quitar un color de la secuencia de colores mediante su posicion
	 * en la misma.
	 * 
	 * @param i
	 *            Posicion del color a quitar.
	 * @return Indica si se ha eliminado correctamente el objeto de la
	 *         secuencia.
	 */
	public boolean removeColor(int i) {
		if (i >= colors.size()) {
			colors.removeElementAt(i);
			return true;
		} else
			return false;
	}

	/**
	 * Permite quitar un color de la secuencia de colores a partir de la
	 * referencia al color.
	 * 
	 * @param i
	 *            Referencia del color a quitar.
	 * @return Indica si se ha eliminado correctamente el objeto de la
	 *         secuencia.
	 */
	public boolean removeColor(N3ColorRGBA c) {
		return colors.remove(c);
	}

	/**
	 * Permite obtener el color contenido en la posici�n indicada.
	 * 
	 * @param i
	 *            Posici�n de la que obtener el color.
	 */
	public N3ColorRGBA getColor(int i) {
		if (i >= colors.size())
			return ((N3ColorRGBA) colors.elementAt(i));
		else
			return null;
	}

	/**
	 * Permite situar el iterador que recorrer la informaci�n de color desde el
	 * principio de la misma.
	 */
	public void begin() {
		pos = 0;
	}

	/**
	 * Permite comprobar si la informaci�n de color esta vacia.
	 * 
	 * @return Si la informaci�n de color esta vacia.
	 */

	public boolean isEmpty() {
		return colors.isEmpty();
	}

	/**
	 * Permite saber si hay mas colores en la informaci�n de color. Si la
	 * secuencia es c�clica no tiene utilidad.
	 * 
	 * @return Si hay mas colores en la secuencia.
	 */
	public boolean hasNext() {
		return ((!colors.isEmpty() && pos < colors.size()));
	}

	/**
	 * Permite obtener el siguiente color de la informaci�n de color. Si no hay
	 * mas colores en el informaci�n de color next() siempre devuelve el ultimo
	 * color de la secuencia.
	 * 
	 * @return Siguiente color o el ultimo color de la secuencia de color.
	 */
	public N3ColorRGBA next() {
		if (hasNext()) {
			lastColor = (N3ColorRGBA) colors.elementAt(pos++);
			return lastColor;
		} else {
			if (loop) {
				begin();
				lastColor = (N3ColorRGBA) colors.elementAt(pos++);
				return lastColor;
			}
			return lastColor;
		}
	}

	/*
	 * Permite especificar si la secuencia es c�clica o no.
	 * 
	 * @param loop Si la secuencia es c�clica o no.
	 */
	public void loop(boolean loop) {
		this.loop = loop;
	}

	/**
	 * Permite conocer si la secuencia es c�clica o no.
	 * 
	 * @return Si la secuencia es c�clica.
	 */
	public boolean isLoop() {
		return loop;
	}

	/**
	 * Vac�a el contenedor.
	 */
	public void clear() {
		colors.clear();
	}
};
