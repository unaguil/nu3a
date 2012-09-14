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

import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Representa un punto de dos dimensiones, coordenadas (x,y).
 */
public class N3Point2D implements N3PersistentResource {
	/**
	 * Coordenada x del punto.
	 */
	public float x;
	/**
	 * Coordenada y del punto.
	 */
	public float y;

	/**
	 * Construye un punto 2D con las coordenadas (0,0);
	 */
	public N3Point2D() {
		x = 0;
		y = 0;
	}

	/**
	 * Construye un punto 2D con las coordenadas especificadas.
	 * 
	 * @param x
	 *            Coordenada x del punto
	 * @param y
	 *            Coordenada y del punto
	 */
	public N3Point2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Obtiene la representacion en forma de String.
	 * 
	 * @return Representacion en forma de String
	 */
	public String toString() {
		return ("(" + x + "," + y + ")");
	}

	/**
	 * Permite cambiar el valor de la coordenada x del punto.
	 * 
	 * @param x
	 *            Coordenada x del punto
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Permite cambiar el valor de la coordenada y del punto.
	 * 
	 * @param y
	 *            Coordenada y del punto
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Permite obtener el valor de la coordenada x del punto.
	 * 
	 * @return Coordenada x del punto
	 */
	public float getX() {
		return x;
	}

	/**
	 * Permite obtener el valor de la coordenada y del punto.
	 * 
	 * @return Coordenada y del punto
	 */
	public float getY() {
		return y;
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = doc.createElement("resource");
		result.setAttribute("class", getClass().getName());
		Element data = doc.createElement("data");
		data.setAttribute("x", "" + x);
		data.setAttribute("y", "" + y);
		result.appendChild(data);

		return result;
	}
}
