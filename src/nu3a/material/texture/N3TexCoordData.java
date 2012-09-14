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

package nu3a.material.texture;

import java.util.Vector;

/**
 * Esta clase contiene información de coordenadas de mapeado.
 */
public class N3TexCoordData {
	/**
	 * Vector que contiene la informacion de mapeado.
	 */
	protected Vector texCoords;

	/**
	 * Posicion actual en la secuencia.
	 */
	protected int pos;

	/**
	 * Indica si al secuencia es ciclica.
	 */
	protected boolean loop;

	/**
	 * Referencia a la ultima coordenada de mapeado devuelta.
	 */
	protected N3TexCoord2D lastTexCoord;

	/**
	 * Constructor. Inicialmente la secuencia de coordenadas esta vacia.
	 */
	public N3TexCoordData() {
		texCoords = new Vector(0, 1);
		lastTexCoord = new N3TexCoord2D(0, 0);
		begin();
	}

	/**
	 * Permite añadir una coordenada de mapeado a la secuencia.
	 * 
	 * @param texCoord
	 *            Coordenada de mapeado.
	 */
	public void addTexCoord(N3TexCoord2D texCoord) {
		texCoords.add(texCoord);
	}

	/**
	 * Permite quitar un coordenada de la secuencia mediante su posicion en la
	 * misma.
	 * 
	 * @param i
	 *            Posicion de la coordenada a quitar.
	 * @return Indica si se ha eliminado correctamente el objeto de la
	 *         secuencia.
	 */
	public boolean removeTexCoord(int i) {
		if (i >= texCoords.size()) {
			texCoords.removeElementAt(i);
			return true;
		} else
			return false;
	}

	/**
	 * Permite quitar una coordenada de la secuencia a partir de la referencia
	 * al color.
	 * 
	 * @param i
	 *            Referencia de la coordenada a quitar.
	 * @return Indica si se ha eliminado correctamente el objeto de la
	 *         secuencia.
	 */
	public boolean removeColor(N3TexCoord2D c) {
		return texCoords.remove(c);
	}

	/**
	 * Permite obtener la coordenada situada en la posicion indicada.
	 * 
	 * @param i
	 *            Posición de la que obtener la coordenada.
	 */
	public N3TexCoord2D getTexCoord(int i) {
		if (i >= texCoords.size())
			return ((N3TexCoord2D) texCoords.elementAt(i));
		else
			return null;
	}

	/**
	 * Permite situar el iterador en la primera posicion de la secuencia.
	 */
	public void begin() {
		pos = 0;
	}

	/**
	 * Permite comprobar si la información de coordenadas esta vacia.
	 * 
	 * @return Si la información de coordenadas esta vacia.
	 */

	public boolean isEmpty() {
		return texCoords.isEmpty();
	}

	/**
	 * Permite saber si hay mas coordenadas en la secuencia.
	 * 
	 * @return Si hay mas coordendas en la secuencia.
	 */
	public boolean hasNext() {
		return (!texCoords.isEmpty() && pos < texCoords.size());
	}

	/**
	 * Permite obtener la siguiente coordenada de la secuencia. Si no hay mas
	 * coordenadas en la secuencia devuelve la ultima coordenada.
	 * 
	 * @return Siguiente color o el ultimo color de la secuencia de color.
	 */
	public N3TexCoord2D next() {
		if (hasNext()) {
			lastTexCoord = (N3TexCoord2D) texCoords.elementAt(pos++);
			return lastTexCoord;
		} else if (loop) {
			begin();
			lastTexCoord = (N3TexCoord2D) texCoords.elementAt(pos++);
			return lastTexCoord;
		}
		return lastTexCoord;
	}

	/*
	 * Permite especificar si la secuencia es cíclica o no.
	 * 
	 * @param loop Si la secuencia es cíclica o no.
	 */
	public void loop(boolean loop) {
		this.loop = loop;
	}

	/**
	 * Permite conocer si la secuencia es cíclica o no.
	 * 
	 * @return Si la secuencia es cíclica.
	 */
	public boolean isLoop() {
		return loop;
	}

	/**
	 * Vacía el contenido del contenedor.
	 */
	public void clear() {
		texCoords.clear();
	}

}
