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
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;
import nu3a.scene.N3Scene;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Representa vectores de tres dimensiones.
 */
public class N3Vector3D extends N3Vector2D {
	/**
	 * Componente z del vector.
	 */
	public float z;

	/**
	 * Construye un vector 3D de componentes (0,0,0).
	 */
	public N3Vector3D() {
		x = 0;
		y = 0;
		z = 0;
	}

	/**
	 * Construye un vector 3D con las componentes indicadas como parametros.
	 * 
	 * @param x
	 *            Componente x del vector
	 * @param y
	 *            Componente y del vector
	 * @param z
	 *            Componente z del vector
	 */
	public N3Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Crea una copia del vector.
	 * 
	 * @return Copia del vector
	 */
	public N3Vector3D copy() {
		return new N3Vector3D(x, y, z);
	}

	/**
	 * Obtiene la representacion en forma de String
	 * 
	 * @return Representacion en forma de String
	 */
	public String toString() {
		return ("(" + x + "," + y + "," + z + ")");
	}

	/**
	 * Suma al vector actual el vector 3D especificado.
	 */
	public void add(N3Vector3D v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	/**
	 * Resta al vector actual el vector 3D especificado.
	 * 
	 * @param v
	 *            Vector a restar
	 */
	public void substract(N3Vector3D v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}

	public void scale(float s) {
		x *= s;
		y *= s;
		z *= s;
	}

	public void neg() {
		x = -x;
		y = -y;
		z = -z;
	}

	/**
	 * Calcula el producto escalar con el vector especificado.
	 * 
	 * @param v
	 *            Vector por el que calcular el producto escalar
	 */
	public float dotProduct(N3Vector3D v) {
		return (x * v.x + y * v.y + z * v.z);
	}

	/**
	 * Calcula el producto vectorial con el vector especificado.
	 * 
	 * @param v
	 *            Vector por el que calcular el producto vectorial
	 */
	public void crossProduct(N3Vector3D v) {
		float nx, ny, nz;
		nx = (y * v.z) - (z * v.y);
		ny = (z * v.x) - (x * v.z);
		nz = (x * v.y) - (y * v.x);
		x = nx;
		y = ny;
		z = nz;
	}

	/**
	 * Calcula el producto vectorial de los vectores especificados.
	 * 
	 * @param v1
	 *            ,v2 Vectores con los que calcular el producto vectorial
	 * @return Nuevo vector producto vectorial
	 */
	public static N3Vector3D crossProduct(N3Vector3D v1, N3Vector3D v2) {
		N3Vector3D res = new N3Vector3D();
		res.x = (v1.y * v2.z) - (v1.z * v2.y);
		res.y = (v1.z * v2.x) - (v1.x * v2.z);
		res.z = (v1.x * v2.y) - (v1.y * v2.x);
		return res;
	}

	public float length() {
		return ((float) Math.sqrt(x * x + y * y + z * z));
	}

	public void normalize() {
		float l = length();
		x /= l;
		y /= l;
		z /= l;
	}

	/**
	 * Calcula el vector 3D normalizado.
	 * 
	 * @param v
	 *            Vector a normalizar
	 * @return Vector normalizado
	 */
	public static N3Vector3D normalize(N3Vector3D v) {
		N3Vector3D res = new N3Vector3D();
		float l = v.length();
		res.x = v.x / l;
		res.y = v.y / l;
		res.z = v.z / l;
		return res;
	}

	/**
	 * Calcula el vector negado.
	 * 
	 * @param v
	 *            Vector a negar
	 * @return Vector negado
	 */
	public static N3Vector3D neg(N3Vector3D v) {
		N3Vector3D res = new N3Vector3D();
		res.x = -v.x;
		res.y = -v.y;
		res.z = -v.z;
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
		data.setAttribute("z", "" + z);
		result.appendChild(data);

		return result;
	}

	/**
	 * Devuelve una instancia de la clase, a partir de los par�metros, y de la
	 * descripci�n XML en infoNode.
	 * 
	 * @param infoNode
	 *            Descripci�n XML de la instacia a crear
	 * @param nodes
	 *            Rama XML con las descripciones de los recursos de la escena
	 * @param resources
	 *            Lista de recursos de la escena
	 * @param reader
	 *            Instancia capaz de crear recursos que a�n no se han creado
	 * @param render
	 *            Render para el que se est� creando la escena
	 * @param scene
	 *            Escena que se est� creando
	 * @return Instancia de la clase con la informaci�n especificada
	 */
	public static N3PersistentResource loadInstance(Element infoNode,
			NodeList nodes, N3PersistentResourceList resources,
			N3SceneReader reader, N3Render render, N3Scene scene) {
		Element data = (Element) infoNode.getElementsByTagName("data").item(0);
		float x = Float.parseFloat(data.getAttribute("x"));
		float y = Float.parseFloat(data.getAttribute("y"));
		float z = Float.parseFloat(data.getAttribute("z"));
		return new N3Vector3D(x, y, z);
	}
}
