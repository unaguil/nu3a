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

import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;
import nu3a.scene.N3Scene;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Esta clase representa coordenadas de mapeado de la forma u,v.
 */
public class N3TexCoord2D implements N3PersistentResource {
	/**
	 * Coordenada u de mapeado.
	 */
	public float u;

	/**
	 * Coordenada v de mapeado.
	 */
	public float v;

	/**
	 * Construye una coordenada de mapeado.
	 * 
	 * @param Componente
	 *            u de la coordenada de mapeado.
	 * @param Componente
	 *            v de la coordenada de mapeado.
	 */
	public N3TexCoord2D(float u, float v) {
		this.u = u;
		this.v = v;
	}

	/**
	 * Obtiene la representaci�n en forma de String del objeto.
	 * 
	 * @return String que representa el objeto.
	 */
	public String toString() {
		String res = "";
		res += "u: " + u + ",v: " + v;
		return res;
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = doc.createElement("resource");
		result.setAttribute("class", getClass().getName());
		Element data = doc.createElement("data");
		data.setAttribute("u", "" + u);
		data.setAttribute("v", "" + v);
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
		float u = Float.parseFloat(data.getAttribute("u"));
		float v = Float.parseFloat(data.getAttribute("v"));
		return new N3TexCoord2D(u, v);
	}
}
