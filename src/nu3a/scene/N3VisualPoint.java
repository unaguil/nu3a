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

package nu3a.scene;

import nu3a.geometry.N3GeometryData;
import nu3a.geometry.N3Point2D;
import nu3a.geometry.N3Polygon;
import nu3a.material.N3Material;
import nu3a.material.color.N3ColorRGBA;
import nu3a.math.N3Vector2D;
import nu3a.names.exception.N3NameException;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Clase que representa un objeto visual cuya geometr�a es un punto.
 */
public class N3VisualPoint extends N3VisualObject {
	/**
	 * Constructor de la clase. Crea un objeto visual con la geometr�a
	 * especificada por el objeto de geometr�a puntual indicado, para la escena
	 * que se especifica.
	 * 
	 * @param scene
	 *            Escena en la que se encuentra el objeto visual
	 * @param g
	 *            Objeto que indica la geometr�a del objeto visual
	 * @param name
	 *            Nombre del nodo
	 */
	public N3VisualPoint(N3Scene scene, N3GeometryData g, String name)
			throws N3NameException {
		super(scene, g, name);
	}

	/**
	 * Redefine el metodo draw de tal forma que dibuja un punto a partir del
	 * vertexData. La informaci�n de vertices,color y mapeado la obtiene desde
	 * la posici�n inicial de cada secuencia.
	 */

	public void draw(N3Render render) {
		super.draw(render);
		if (texCoordData != null && !texCoordData.isEmpty()) {
			texCoordData.begin();
			render.drawData(vertexData, render.N3_POINTS_DATA, colorData,
					texCoordData, normalData);
		} else {
			render.drawData(vertexData, render.N3_POINTS_DATA, colorData,
					normalData);
		}
	}

	protected void processGeometry() {
		if (dirtyGeometry) {
			N3Point2D p1 = null;
			N3Vector2D n1 = null;
			N3ColorRGBA c1 = null;
			vertexData.clear();
			normalData.clear();
			colorData.clear();
			for (int i = 0; i < geometry.polygonCount(); i++) {
				N3Polygon p = geometry.getPolygon(i);
				boolean pass = false;
				for (int j = 0; j < p.getSides(); j++) {
					p1 = p.getVertex(j);
					n1 = p.getNormal(j);
					c1 = p.getColor(j);
					vertexData.addVertex(p1);
					colorData.addColor(c1);
					normalData.addNormal(n1);
				}
			}
			dirtyGeometry = false;
		}
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
			N3SceneReader reader, N3Render render, N3Scene scene)
			throws Exception {
		String name = infoNode.getAttribute("name");
		Element data = (Element) infoNode.getElementsByTagName("geometry")
				.item(0);
		int index = Integer.parseInt(data.getAttribute("index"));
		N3GeometryData g = (N3GeometryData) resources.resourceAt(index);
		N3VisualPoint result = new N3VisualPoint(scene, g, name);

		data = (Element) infoNode.getElementsByTagName("collisionable").item(0);
		result.setCollisionable((new Boolean(data.getAttribute("value")))
				.booleanValue());

		data = (Element) infoNode.getElementsByTagName("material").item(0);
		index = Integer.parseInt(data.getAttribute("index"));
		if (index != -1) {
			N3Material m = (N3Material) resources.resourceAt(index);
			result.setMaterial(m);
		}

		return result;
	}
}
