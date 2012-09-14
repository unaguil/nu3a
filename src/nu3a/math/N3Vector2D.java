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

package nu3a.math;

import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Representa vectores de dos dimensiones.
 */
public class N3Vector2D implements N3PersistentResource {
	/**
	 * Componente x del vector.
	 */
	public float x;

	/**
	 * Componente y del vector.
	 */
	public float y;

	/**
	 * Construye un vector 2D de componentes (0,0).
	 */
	public N3Vector2D() {
		x = 0;
		y = 0;
	}

	/**
	 * Construye un vector 2D con las componentes indicadas como parametros.
	 * 
	 * @param x
	 *            Componente x del vector
	 * @param y
	 *            Componente y del vector
	 */
	public N3Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Representa el vector 2D mediante un objeto String.
	 * 
	 * @return Representacion en forma de String
	 */
	public String toString() {
		return ("(" + x + "," + y + ")");
	}

	/**
	 * Suma al vector actual el vector 2D especificado.
	 * 
	 * @param v
	 *            Vector a a�adir
	 */
	public void add(N3Vector2D v) {
		x += v.x;
		y += v.y;
	}

	/**
	 * Resta al vector actual el vector 2D especificado.
	 * 
	 * @param v
	 *            Vector a restar
	 */
	public void substract(N3Vector2D v) {
		x -= v.x;
		y -= v.y;
	}

	/**
	 * Escala el vector mediante el factor especificado.
	 * 
	 * @param s
	 *            Valor de escalado del vector
	 */
	public void scale(float s) {
		x *= s;
		y *= s;
	}

	/**
	 * Niega el vector.
	 */
	public void neg() {
		x = -x;
		y = -y;
	}

	/**
	 * Calcula la longitud del vector.
	 * 
	 * @return Longitud del vector
	 */
	public float length() {
		return ((float) Math.sqrt(x * x + y * y));
	}

	/**
	 * Normaliza el vector.
	 */
	public void normalize() {
		float l = length();
		x /= l;
		y /= l;
	}

	/**
	 * Calcula el vector 2D normalizado.
	 * 
	 * @param v
	 *            Vector a normalizar
	 * @return Vector normalizado
	 */
	public static N3Vector2D normalize(N3Vector2D v) {
		N3Vector2D res = new N3Vector2D();
		float l = v.length();
		res.x = v.x / l;
		res.y = v.y / l;
		return res;
	}

	/**
	 * Calcula el vector negado.
	 * 
	 * @param v
	 *            Vector a negar
	 * @return Vector negado
	 */
	public static N3Vector2D neg(N3Vector2D v) {
		N3Vector2D res = new N3Vector2D();
		res.x = -v.x;
		res.y = -v.y;
		return res;
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
