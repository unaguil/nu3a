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

import nu3a.light.N3LightData;
import nu3a.math.N3Vector3D;
import nu3a.names.exception.N3NameException;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Clase que representa una luz gen�rica con sus componentes especular,
 * ambiental y difusa.
 */
public class N3Light extends N3LeafNode {

	/**
	 * Utilizados para pasar al render la informaci�n que necesita
	 */
	private float[] pos = { 0, 0, 0, 0 };
	private float[] dir = new float[3];

	/**
	 * Objeto que guarda las caracter�sticas de la luz.
	 */
	N3LightData internalLight;

	/**
	 * Crea una instancia de un nodo de luz para el objeto de datos de luz
	 * especificado.
	 * 
	 * @param data
	 *            Datos de la luz
	 * @param scene
	 *            Escena a la que pertenece el nodo.
	 * @param name
	 *            Nombre del nodo
	 */
	public N3Light(N3LightData data, N3Scene scene, String name)
			throws N3NameException {
		super(scene, name);
		scene.addLight(this);
		internalLight = data;
	}

	/**
	 * Elimina el nodo del �rbol de escena. Las clases que hereden de N3Node
	 * redefinir�n este m�todo para eliminar las referencias espec�ficas de su
	 * tipo.
	 */
	public void remove() {
		super.remove();
		scene.removeLight(this);
	}

	/**
	 * Establece los valores de la luz en el objeto de renderizado.
	 * 
	 * @param render
	 *            Objeto de renderizado.
	 * @param n
	 *            Posici�n que ocupa la luz en el vector de luces activas de la
	 *            escena
	 */
	public void setRenderValues(N3Render render, int n) {
		render.enableLight(n);
		/* Renderizar las propiedades del color */
		render.setObjectTransformation(getAccMatrix());
		render.setLightParam(render.N3_AMBIENT, internalLight.getAmbiental()
				.getColorArray(), n);
		render.setLightParam(render.N3_DIFFUSE, internalLight.getDiffuse()
				.getColorArray(), n);
		render.setLightParam(render.N3_SPECULAR, internalLight.getSpecular()
				.getColorArray(), n);
		render.setLightParam(render.N3_CONSTANT_ATTENUATION,
				internalLight.getConstantAttenuation(), n);
		render.setLightParam(render.N3_LINEAR_ATTENUATION,
				internalLight.getLinearAttenuation(), n);
		render.setLightParam(render.N3_QUADRATIC_ATTENUATION,
				internalLight.getQuadraticAttenuation(), n);

		N3Vector3D direction = internalLight.getDirection();
		this.dir[0] = direction.x;
		this.dir[1] = direction.y;
		this.dir[2] = direction.z;

		if (internalLight.getPositional()) {
			pos[0] = 0;
			pos[1] = 0;
			pos[2] = 0;
			pos[3] = 1.0f;
		} else {
			pos[0] = dir[0];
			pos[1] = dir[1];
			pos[2] = dir[2];
			pos[0] = 0;
			dir[0] = 0;
			dir[1] = 0;
			dir[2] = -1.0f;
		}

		render.setLightParam(render.N3_POSITION, pos, n);
		render.setLightParam(render.N3_SPOT_DIRECTION, dir, n);
		render.setLightParam(render.N3_SPOT_CUTOFF, internalLight.getAngle(), n);
		render.setLightParam(render.N3_SPOT_EXPONENT,
				internalLight.getSpotExponent(), n);
	}

	/**
	 * Obtiene el objeto con los datos de la luz.
	 * 
	 * @return Objeto de datos de la luz
	 */
	public N3LightData getLightData() {
		return internalLight;
	}

	/**
	 * Establece el objeto con los datos de la luz.
	 * 
	 * @param data
	 *            Objeto de datos de la luz
	 */
	public void setLightData(N3LightData data) {
		internalLight = data;
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
		resources.addResource(internalLight);
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = super.getXMLDescription(doc, resources);

		Element data = doc.createElement("internal");
		data.setAttribute("index", "" + resources.indexOf(internalLight));
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
			N3SceneReader reader, N3Render render, N3Scene scene)
			throws Exception {
		String name = infoNode.getAttribute("name");
		Element data = (Element) infoNode.getElementsByTagName("internal")
				.item(0);
		int index = Integer.parseInt(data.getAttribute("index"));
		N3LightData l = (N3LightData) resources.resourceAt(index);
		N3Light result = new N3Light(l, scene, name);

		data = (Element) infoNode.getElementsByTagName("collisionable").item(0);
		result.setCollisionable((new Boolean(data.getAttribute("value")))
				.booleanValue());

		return result;
	}
}
