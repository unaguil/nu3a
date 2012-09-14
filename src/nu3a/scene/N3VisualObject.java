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

import java.util.Vector;

import nu3a.collision.N3Collisionable;
import nu3a.geometry.N3GeometryData;
import nu3a.geometry.N3GeometryListener;
import nu3a.geometry.N3NormalData;
import nu3a.geometry.N3VertexData;
import nu3a.material.N3Material;
import nu3a.material.color.N3ColorData;
import nu3a.material.texture.N3TexCoordData;
import nu3a.names.N3NameManager;
import nu3a.names.exception.N3NameException;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.render.N3Render;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Clase abstracta padre de todos los tipos de objetos visuales de la escena. Un
 * objeto visual es la abstracci�n que representa un objeto que puede ser
 * mostrado en la escena, al que afectan las transformaciones de sus nodos
 * superiores en la jerarqu�a.
 * 
 * Un objeto visual se caracteriza por tener una geometr�a determinada.
 */
public abstract class N3VisualObject extends N3LeafNode implements
		N3GeometryListener {

	/**
	 * Indica si el objeto es o no visible.
	 */
	protected boolean visible = true;

	/**
	 * Indica si el volumen de contenci�n asociado al objeto es visible.
	 */
	protected boolean bvVisible = false;

	/**
	 * Objeto que contiene la geometr�a del objeto visual.
	 */
	protected N3GeometryData geometry;

	/**
	 * Objeto que contiene la geometr�a del objeto visual.
	 */
	protected boolean dirtyGeometry = false;

	/**
	 * Objeto que contiene la informaci�n de material del objeto visual.
	 */
	protected N3Material material;

	/**
	 * Informaci�n de vertices del objeto.
	 */
	protected N3VertexData vertexData;

	/**
	 * Informaci�n de normales del objeto.
	 */
	protected N3NormalData normalData;

	/**
	 * Informaci�n de color del objeto.
	 */
	protected N3ColorData colorData;

	/**
	 * Informaci�n de mapeado del objeto.
	 */
	protected N3TexCoordData texCoordData;

	/**
	 * /** Vector que contiene las luces de la escena que no afectan al objeto
	 * visual.
	 */
	protected Vector disabledLights;

	/**
	 * Constructor de la clase. Obtiene una instancia de un objeto visual
	 * gen�rico.
	 * 
	 * @param scene
	 *            Escena a la que pertenece el nodo
	 * @param g
	 *            Objeto que contiene la geometr�a del objeto visual
	 * @param name
	 *            Nombre del nodo
	 */
	protected N3VisualObject(N3Scene scene, N3GeometryData g, String name)
			throws N3NameException {
		super(scene, name);
		geometry = g;
		colorData = new N3ColorData();
		vertexData = new N3VertexData();
		normalData = new N3NormalData();
		texCoordData = new N3TexCoordData();
		disabledLights = new Vector(0, 1);
		geometry.addGeometryListener(this);
		scene.addVisualObject(this);
		processGeometry();
	}

	/**
	 * Constructor de la clase. Obtiene una instancia de un objeto visual
	 * gen�rico si geometria asociada.
	 * 
	 * @param scene
	 *            Escena a la que pertenece el nodo
	 * @param name
	 *            Nombre del nodo
	 */

	protected N3VisualObject(N3Scene scene, String name) throws N3NameException {
		super(scene, name);
		colorData = new N3ColorData();
		texCoordData = new N3TexCoordData();
		disabledLights = new Vector(0, 1);
		geometry = null;
		scene.addVisualObject(this);
	}

	/**
	 * Permite indicar si el objeto es o no visible
	 * 
	 * @param visible
	 *            Permite cambiar la visibilidad del objeto.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Permite comprobar si el objeto es o no visible.
	 * 
	 * @return Indica si el objeto es o no visible.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Permite indicar si el volumen asociado al objeto es visible o no.
	 * 
	 * @param visible
	 *            Indica si el volumen es visible o no.
	 */
	public void setBoundingVolumeVisible(boolean visible) {
		this.bvVisible = visible;
	}

	/**
	 * Permite comprobar si el volumen asociado al objeto es visible o no.
	 * 
	 * @return Indica si el volumen asociado al objeto es visible o no.
	 */
	public boolean isBoundingVolumeVisible() {
		return bvVisible;
	}

	/**
	 * Permite cambiar la geometria asociada con el objeto visual.
	 * 
	 * @param g
	 *            Geometria del objeto visual.
	 */
	public void setGeometry(N3GeometryData g) {
		if (geometry != null)
			geometry.removeGeometryListener(this);
		geometry = g;
		geometry.addGeometryListener(this);
	}

	/**
	 * Obtiene la geometr�a de la escena en la forma de un objeto de geometr�a
	 * gen�rico.
	 * 
	 * @return Objeto que contiene la geometr�a del objeto visual
	 */

	public N3GeometryData getGeometry() {
		return geometry;
	}

	/**
	 * Permite asignar un material al objeto visual.
	 * 
	 * @param material
	 *            Material a asignar al objeto visual.
	 */

	public void setMaterial(N3Material material) {
		this.material = material;
	}

	/**
	 * Obtiene el material del objeto visual.
	 * 
	 * @return Material del objeto visual.
	 */

	public N3Material getMaterial() {
		return material;
	}

	/**
	 * Elimina el nodo del �rbol de escena. Las clases que hereden de N3Node
	 * redefinir�n este m�todo para eliminar las referencias espec�ficas de su
	 * tipo.
	 */
	public void remove() {
		super.remove();
		scene.removeVisualObject(this);
	}

	/**
	 * Aplica la transformaci�n que el objeto ha heredado en el jerarquia de
	 * escena al espacio.
	 * 
	 * @param render
	 *            Render mediante el cual se renderiza la escena.
	 */

	protected void applyTransform(N3Render render) {
		render.setObjectTransformation(getAccMatrix());
	}

	/**
	 * Es el metodo para dibujar un objeto. Este m�todo primero llama al metodo
	 * appyTransform para aplicar la transformaci�n que tiene el objeto debido a
	 * su pertenencia a la escena, si el objeto tiene geometr�a deja su
	 * informaci�n geom�trica en vertexData, si tiene color deja esta
	 * informaci�n en colorData y si tiene informaci�n de mapeado la deja en
	 * texCoordData.
	 * 
	 * @param render
	 *            Render mediante el cual se renderiza la escena.
	 */
	public void draw(N3Render render) {
		if (bvVisible && bVolume != null) {
			render.resetCameraTransformation();
			bVolume.draw(render);
		}
		processGeometry();
		applyTransform(render);
		for (int i = 0; i < disabledLights.size(); i++) {
			int l = scene.getActiveLightNumber((N3Light) disabledLights
					.elementAt(i));
			if (l != -1)
				render.disableLight(l);
		}
		if (material != null) {
			material.renderMaterial(render);
		}
		if (geometry != null) {
			colorData.begin();
			vertexData.begin();
			normalData.begin();
		}
		for (int i = 0; i < disabledLights.size(); i++) {
			int l = scene.getActiveLightNumber((N3Light) disabledLights
					.elementAt(i));
			if (l != -1)
				render.enableLight(l);
		}
	}

	/**
	 * Establece que una determinada luz no va a afectar al objeto.
	 * 
	 * @param light
	 *            Luz de la escena
	 */
	public void disableLight(N3Light light) {
		disabledLights.add(light);
	}

	/**
	 * Establece que una determinada luz que no afectaba al objeto vuelve a
	 * afectarle.
	 * 
	 * @param light
	 *            Luz de la escena
	 */
	public void enableLight(N3Light light) {
		disabledLights.remove(light);
	}

	/**
	 * Procesa la geometr�a y la transforma a la representaci�n interna con la
	 * que el nodo manejar� los datos.
	 */
	protected abstract void processGeometry();

	/**
	 * Notifica al objeto que se ha cambiado su geometr�a.
	 */
	public void notifyGeometry() {
		dirtyGeometry = true;
	}

	// /Redefinici�n de la interfaz N3CollisionableVolume

	public void updateBV() {
		if (geometry != null) {
			geometry.calculeBV(getAccMatrix());
			bVolume.setData(geometry.getBoundingVolume());
		}
	}

	/**
	 * Comprueba si el objeto indicado colisiona a nivel de geometr�a con el
	 * objeto visual.
	 * 
	 * @param c
	 *            Objeto contra el que comprobar si hay colisi�n.
	 * @param all
	 *            Indica si el test se contin�a tras detectar la primera
	 *            collisi�n con un pol�gono.
	 */
	public boolean testGeometry(N3Collisionable c, boolean all) {
		if (geometry != null)
			return geometry.testGeometry(c, getAccMatrix(), all);
		else
			return false;
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
		resources.addResource(geometry);
		resources.addResource(material);
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = super.getXMLDescription(doc, resources);

		Element data = doc.createElement("geometry");
		data.setAttribute("index", "" + resources.indexOf(geometry));
		result.appendChild(data);

		data = doc.createElement("material");
		data.setAttribute("index", "" + resources.indexOf(material));
		result.appendChild(data);

		data = doc.createElement("visible");
		data.setAttribute("value", "" + visible);
		result.appendChild(data);

		data = doc.createElement("bvvisible");
		data.setAttribute("value", "" + bvVisible);
		result.appendChild(data);

		data = doc.createElement("disabledlights");
		for (int i = 0; i < disabledLights.size(); i++) {
			Element lightNode = doc.createElement("light");
			lightNode.setAttribute("name",
					((N3Light) disabledLights.elementAt(i)).getName());
			data.appendChild(lightNode);
		}
		result.appendChild(data);

		return result;
	}

	public void ressolveNames(Element info, N3NameManager manager,
			N3PersistentResourceList resources) {
		if (info.getElementsByTagName("light").getLength() > 0) {
			NodeList lights = info.getElementsByTagName("light");
			Element lightInfo;
			for (int i = 0; i < lights.getLength(); i++) {
				lightInfo = (Element) lights.item(i);
				N3Light l = (N3Light) manager.getNamedObject(lightInfo
						.getAttribute("name"));
				disableLight(l);
			}
		}
	}
}
