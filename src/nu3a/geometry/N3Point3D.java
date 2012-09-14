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
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;
import nu3a.scene.N3Scene;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Representa un punto de tres dimensiones, coordenadas (x,y,z).
 */
public class N3Point3D extends N3Point2D {
	/**
	 * Coordenada z del punto.
	 */
	public float z;

	/**
	 * Coordenada homogenea.
	 */
	public float w;

	/**
	 * Construye un punto 3D con las coordenadas (0,0,0);
	 */
	public N3Point3D() {
		x = 0;
		y = 0;
		z = 0;
		w = 1.0f;
	}

	/**
	 * Construye un punto 3D con las coordenadas especificadas.
	 */
	public N3Point3D(float x, float y, float z) {
		super(x, y);
		this.z = z;
	}

	/**
	 * Obtiene la representacion en forma de String.
	 */
	public String toString() {
		return ("(" + x + "," + y + "," + z + "," + w + ")");
	}

	/**
	 * Permite obtener el valor de la coordenada z del punto.
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Permite cambiar el valor de la coordenada z del punto.
	 */
	public void setZ(float z) {
		this.z = z;
	}

	public float getW() {
		return w;
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
		return new N3Point3D(x, y, z);
	}
}
