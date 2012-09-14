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

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import nu3a.collision.N3Collisionable;
import nu3a.geometry.N3Point3D;
import nu3a.material.color.N3ColorRGBA;
import nu3a.math.N3Vector3D;
import nu3a.names.N3NameManager;
import nu3a.names.N3NamedObject;
import nu3a.names.exception.N3NameException;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Punto de acceso al �rbol de escena. Contiene la jerarqu�a de objetos y
 * transformaciones que componen la escena, junto con accesos directos a las
 * luces, c�maras y objetos visuales de la escena.
 */
public class N3Scene implements N3NameManager, N3PersistentResource {
	/**
	 * Nodo ra�z de la escena.
	 */
	protected N3GroupNode root;

	/**
	 * Vector de luces de la escena.
	 */
	protected Vector lights;

	/**
	 * Vector de luces activas de la escena.
	 */
	protected Vector activeLights;

	/**
	 * N�mero m�ximo de luces activas de la escena. Esta propiedad deber�a de
	 * coincidir con el n�mero m�ximo de luces que soporta el render a utilizar,
	 * o ser menor que �ste.
	 */
	protected int activeLightCount;

	/**
	 * Luz ambiental de la escena
	 */
	protected N3ColorRGBA ambientalLight;

	/**
	 * Vector de c�maras de la escena.
	 */
	protected Vector cameras;

	/**
	 * Vector de objetos visuales de la escena.
	 */
	protected Vector objects;

	/**
	 * C�mara a partir de la cual se visualizar� la escena
	 */
	protected N3Camera selectedCamera;

	/**
	 * Tabla de acceso r�pido a los nodos a trav�s de su nombre.
	 */
	protected Hashtable names;

	/**
	 * Crea una instancia de la escena, y su nodo ra�z.
	 */
	public N3Scene() {
		names = new Hashtable();
		lights = new Vector(0, 1);
		cameras = new Vector(0, 1);
		objects = new Vector(0, 1);
		activeLights = new Vector(0, 1);
		activeLightCount = 0;
		selectedCamera = null;
		ambientalLight = new N3ColorRGBA(0.2f, 0.2f, 0.2f);
		try {
			root = new N3GroupNode(this, "root");
		} catch (N3NameException e) {
		}
	}

	/**
	 * Devuelve una referencia al nodo ra�z de la escena.
	 * 
	 * @return Nodo ra�z de la escena
	 */
	public N3GroupNode getHierarchyRoot() {
		return root;
	}

	/**
	 * A�ade una luz a la escena.
	 * 
	 * @param l
	 *            Luz a a�adir
	 */
	protected void addLight(N3Light l) {
		lights.add(l);
	}

	/**
	 * Devuelve el n�mero de luces presentes en la escena
	 * 
	 * @return N�mero de luces en la escena
	 */
	public int getLightCount() {
		return lights.size();
	}

	/**
	 * Devuelve la n-�sima luz de la escena, d�nde n est� comprendido entre 0 y
	 * el n�mero de luces de la escena menos 1.
	 * 
	 * @param n
	 *            Posici�n de la luz a obtener del vector de luces
	 * @return N-�sima luz de la escena
	 */
	public N3Light getLight(int n) {
		return (N3Light) lights.elementAt(n);
	}

	/**
	 * Elimina una luz de la escena.
	 * 
	 * @param l
	 *            Luz a eliminar
	 */
	protected void removeLight(N3Light l) {
		/*
		 * Llamamos al enableLight en todos los objetos visuales para que quiten
		 * la luz de su vector de luces desactivadas
		 */
		for (int i = 0; i < objects.size(); i++)
			((N3VisualObject) objects.elementAt(i)).enableLight(l);
		lights.remove(l);
	}

	/**
	 * A�ade una c�mara a la escena.
	 * 
	 * @param c
	 *            C�mara a a�adir
	 */
	protected void addCamera(N3Camera c) {
		cameras.add(c);
	}

	/**
	 * Devuelve el n�mero de c�maras presentes en la escena
	 * 
	 * @return N�mero de c�maras en la escena
	 */
	public int getCameraCount() {
		return cameras.size();
	}

	/**
	 * Devuelve la n-�sima c�mara de la escena, d�nde n est� comprendido entre 0
	 * y el n�mero de c�maras de la escena menos 1.
	 * 
	 * @param n
	 *            Posici�n de la c�mara a obtener del vector de c�maras
	 * @return N-�sima c�mara de la escena
	 */
	public N3Camera getCamera(int n) {
		return (N3Camera) cameras.elementAt(n);
	}

	/**
	 * Elimina una c�mara de la escena.
	 * 
	 * @param c
	 *            C�mara a eliminar
	 */
	protected void removeCamera(N3Camera c) {
		cameras.remove(c);
	}

	/**
	 * A�ade un objeto visual a la escena.
	 * 
	 * @param o
	 *            objeto visual a a�adir
	 */
	protected void addVisualObject(N3VisualObject o) {
		objects.add(o);
	}

	/**
	 * Devuelve el n�mero de objetos visuales presentes en la escena
	 * 
	 * @return N�mero de objetos visuales en la escena
	 */
	public int getVisualObjectCount() {
		return objects.size();
	}

	/**
	 * Devuelve el n-�simo objeto visual de la escena, d�nde n est� comprendido
	 * entre 0 y el n�mero de objetos visuales de la escena menos 1.
	 * 
	 * @param n
	 *            Posici�n del objeto visual a obtener del vector de objetos
	 *            visuales
	 * @return N-�simo objeto visual de la escena
	 */
	public N3VisualObject getVisualObject(int n) {
		return (N3VisualObject) objects.elementAt(n);
	}

	/**
	 * Elimina un objeto visual de la escena.
	 * 
	 * @param o
	 *            Objeto visual a eliminar
	 */
	protected void removeVisualObject(N3VisualObject o) {
		objects.remove(o);
	}

	/**
	 * Establece la c�mara activa de la escena. Esta es la c�mara mediante la
	 * cual se dibujar� toda la escena.
	 * 
	 * @param c
	 *            C�mara que define la visi�n del espectador.
	 */
	public boolean setActiveCamera(N3Camera c) {
		int pos = cameras.indexOf(c);
		if (pos != -1) {
			selectedCamera = c;
			return true;
		} else {
			selectedCamera = null;
			return false;
		}
	}

	/**
	 * Obtiene la c�mara activa de la escena. Esta es la c�mara mediante la cual
	 * se dibujar� toda la escena.
	 * 
	 * @return C�mara que define la visi�n del espectador.
	 */

	public N3Camera getActiveCamera() {
		return selectedCamera;
	}

	/**
	 * Indica si existe el objeto en la tabla de acceso r�pido de la escena, a
	 * partir de su nombre.
	 * 
	 * @param node
	 *            Nodo de la escena.
	 * @return true si existe en la escena alg�n nodo con ese nombre.
	 */
	public boolean checkNamedObject(String name) {
		return (names.get(name) != null);
	}

	/**
	 * A�ade una referencia del objeto a la tabla de acceso r�pido de la escena,
	 * a partir de su nombre.
	 * 
	 * @param node
	 *            Nodo de la escena.
	 */
	public void addNamedObject(N3NamedObject obj) throws N3NameException {
		if (names.get(obj.getName()) != null)
			throw new N3NameException("Name must be unique: " + obj.getName());
		names.put(obj.getName(), obj);
	}

	/**
	 * Elimina la referencia del objeto a la tabla de acceso r�pido de la
	 * escena, a partir de su nombre.
	 * 
	 * @param node
	 *            Nodo de la escena.
	 */
	public void removeNamedObject(N3NamedObject obj) {
		names.remove(obj.getName());
	}

	/**
	 * Obtiene una referencia a un nodo a partir de su nombre.
	 * 
	 * @param name
	 *            Nombre del nodo
	 * @return Referencia al nodo con el nombre especificado
	 */
	public N3NamedObject getNamedObject(String name) {
		return (N3NamedObject) names.get(name);
	}

	/**
	 * Renderiza la escena usando el render indicado.
	 * 
	 * @param render
	 *            Render con el que renderizar la escena.
	 */
	public void render(N3Render render) {
		if (render.beginDraw()) {
			int i;
			if (selectedCamera != null) {
				N3Camera c = getActiveCamera();
				render.setProjectionMode();
				render.loadMatrix(c.getProjectionMatrix());
				render.setModelViewMode();
				render.setCameraTransformation(c.getCameraTransformation());
				if (c.getCameraData().getViewport() != null)
					render.setViewport(c.getCameraData().getViewport());
			}
			if (render.isLighting()) {
				render.setAmbientLightValue(ambientalLight);
				for (i = 0; i < activeLights.size(); i++)
					((N3Light) activeLights.elementAt(i)).setRenderValues(
							render, i);
				for (; i < render.getMaxLights(); i++) {
					render.disableLight(i);
				}
			}
			N3VisualObject obj;
			render.setClearColor(new N3ColorRGBA(0, 0, 0, 1.0f));
			render.clear();
			for (i = 0; i < objects.size(); i++) {
				obj = (N3VisualObject) objects.elementAt(i);
				if (obj.inScene() && obj.isVisible())
					obj.draw(render);
			}
			render.endDraw();
		}
	}

	/**
	 * Establece una luz como activa en la escena.
	 * 
	 * @param light
	 *            luz que se va a activar.
	 * @return Devuelve true si la luz se ha podido establecer como activa,
	 *         false en el caso de que ya se haya llegado al n�mero m�ximo de
	 *         luces activas, en cuyo caso no se habr� a�adido.
	 */
	public boolean addActiveLight(N3Light light) {
		if ((activeLights.size() < activeLightCount)
				&& (lights.indexOf(light) != -1)) {
			activeLights.add(light);
			return true;
		} else
			return false;
	}

	/**
	 * Desactiva una luz de la escena.
	 * 
	 * @param light
	 *            Luz a desactivar
	 */
	public void removeActiveLight(N3Light light) {
		activeLights.remove(lights);
	}

	/**
	 * Establece el n�mero m�ximo de las luces de la escena. Este n�mero debe de
	 * ser menor o igual al n�mero m�ximo de luces soportadas por el render en
	 * el que se vaya a dibujar la escena.
	 * 
	 * @param n
	 *            N�mero m�ximo de luces activas en la escena.
	 */
	public void setActiveLightCount(int n) {
		activeLightCount = n;
	}

	/**
	 * Obtiene la posici�n que ocupa una luz dentro del vector de luces activas.
	 * Devuelve -1 en el caso de que no sea una luz activa.
	 * 
	 * @param light
	 *            Luz a comprobar
	 * @return Posici�n de la luz dentro del vector de luces activas. Si la luz
	 *         no est� activa devuelve -1
	 */
	public int getActiveLightNumber(N3Light light) {
		return activeLights.indexOf(light);
	}

	/**
	 * Actualiza los volumenes de contenci�n de la escena.
	 */
	public void updateBV() {
		root.updateBV();
	}

	/**
	 * Permite comprobar si un N3Collisionable colisiona con algun objeto visual
	 * de la escena.Si el objeto a comprobar esta en la escena tambien ser�
	 * devuelte en el vector resultado.
	 * 
	 * @param c
	 *            Objeto a comprobar si colisiona con algun objeto de la escena.
	 * @return Objetos visuales contra los que colisiona el objeto indicado.
	 */
	public Vector test(N3Collisionable c) {
		return root.testRec(c);
	}

	private class DistanceSorter implements Comparator {
		N3Point3D p;

		public DistanceSorter(N3Point3D p) {
			this.p = p;
		}

		public int compare(Object o1, Object o2) {
			N3Point3D node1Pos = ((N3Node) o1).getPosition();
			N3Point3D node2Pos = ((N3Node) o2).getPosition();
			N3Vector3D node1Vector = new N3Vector3D(node1Pos.x - p.x,
					node1Pos.y - p.y, node1Pos.z - p.z);
			N3Vector3D node2Vector = new N3Vector3D(node2Pos.x - p.x,
					node2Pos.y - p.y, node2Pos.z - p.z);
			float node1Dist = node1Vector.length();
			float node2Dist = node2Vector.length();
			if (node1Dist < node2Dist)
				return -1;
			if (node1Dist > node2Dist)
				return 1;
			return 0;

		}

		public boolean equals(Object obj) {
			return (obj instanceof DistanceSorter);
		}
	}

	/**
	 * Permite ordenar un vector de N3Node segun su distancia a un punto, de
	 * menor distancia a mayor.
	 * 
	 * @param nodes
	 *            Nodos a ordenar
	 * @param p
	 *            Punto con respecto a cual ordenar.
	 */
	public void Zorder(Vector nodes, N3Point3D p) {
		List l = (List) nodes;
		DistanceSorter sorter = new DistanceSorter(p);
		java.util.Collections.sort(l, sorter);
	}

	/**
	 * Obtiene el n�mero de luces activas.
	 * 
	 * @return N�mero de luces activas
	 */
	public int getActiveLights() {
		return activeLights.size();
	}

	/**
	 * Obtiene la i-�sima luz activa
	 * 
	 * @param i
	 *            �ndice de la luz activa
	 * @return i-�sima luz activa
	 */
	public N3Light getActiveLight(int i) {
		return (N3Light) activeLights.elementAt(i);
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
		for (int i = 0; i < cameras.size(); i++)
			((N3Camera) cameras.elementAt(i)).getPersistentResources(resources);

		for (int i = 0; i < lights.size(); i++)
			((N3Light) lights.elementAt(i)).getPersistentResources(resources);

		for (int i = 0; i < objects.size(); i++)
			((N3VisualObject) objects.elementAt(i))
					.getPersistentResources(resources);
	}

	protected void getNodeInformation(N3Node node, Element sceneData,
			Document doc, N3PersistentResourceList resources) {
		sceneData.appendChild(node.getXMLDescription(doc, resources));
		if (node instanceof N3GroupNode) {
			N3Node[] nodes = ((N3GroupNode) node).getChildren();
			for (int i = 0; i < nodes.length; i++) {
				getNodeInformation(nodes[i], sceneData, doc, resources);
			}
		}
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element sceneRoot = doc.createElement("scene");
		Element sceneData;
		Element lightData;
		sceneRoot.setAttribute("rootnodename", root.getName());

		sceneData = doc.createElement("nodes");
		N3Node[] nodes = root.getChildren();
		for (int i = 0; i < nodes.length; i++) {
			getNodeInformation(nodes[i], sceneData, doc, resources);
		}
		sceneRoot.appendChild(sceneData);

		sceneData = doc.createElement("activelights");
		for (int i = 0; i < getActiveLights(); i++) {
			N3Light l = getActiveLight(i);
			lightData = doc.createElement("light");
			lightData.setAttribute("name", l.getName());
			sceneData.appendChild(lightData);
		}
		sceneRoot.appendChild(sceneData);

		sceneData = doc.createElement("activecamera");
		sceneData.setAttribute("camera", selectedCamera.getName());
		sceneRoot.appendChild(sceneData);

		return sceneRoot;
	}

	/**
	 * Carga los nodos de la escena a partir del documento DOM especificado.
	 * 
	 * @param document
	 *            Documento DOM con la informaci�n de la escena
	 * @param resources
	 *            Lista de recursos de la escena
	 * @param render
	 *            Render para el que se carga la escena
	 * @param reader
	 *            Lector de escena
	 */
	public void selfConfigure(Document document,
			N3PersistentResourceList resources, N3Render render,
			N3SceneReader reader) throws Exception {
		Element sceneData = (Element) document.getElementsByTagName("scene")
				.item(0);
		Element nodeInfo;
		String type;
		Class c;
		Method m;
		Object[] pars;
		N3Node node;
		N3GroupNode parent;
		Class[] parTypes = new Class[] { Class.forName("org.w3c.dom.Element"),
				Class.forName("org.w3c.dom.NodeList"), resources.getClass(),
				Class.forName("nu3a.persistence.N3SceneReader"),
				Class.forName("nu3a.render.N3Render"),
				Class.forName("nu3a.scene.N3Scene") };

		root.setName(sceneData.getAttribute("rootnodename"));

		NodeList hierarchy = sceneData.getElementsByTagName("node");
		for (int i = 0; i < hierarchy.getLength(); i++) {
			nodeInfo = (Element) hierarchy.item(i);
			type = nodeInfo.getAttribute("class");
			c = Class.forName(type);
			m = c.getMethod("loadInstance", parTypes);
			pars = new Object[] { nodeInfo, hierarchy, resources, reader,
					render, this };
			node = (N3Node) m.invoke(null, pars);
			parent = (N3GroupNode) getNamedObject(nodeInfo
					.getAttribute("parentname"));
			parent.addChild(node);
		}
		setActiveLightCount(render.getMaxLights());

		NodeList activeLights = sceneData.getElementsByTagName("light");
		N3Light active;
		for (int i = 0; i < activeLights.getLength(); i++) {
			nodeInfo = (Element) activeLights.item(i);
			active = (N3Light) getNamedObject(nodeInfo.getAttribute("name"));
			addActiveLight(active);
		}

		if (sceneData.getElementsByTagName("activecamera").getLength() > 0) {
			nodeInfo = (Element) sceneData.getElementsByTagName("activecamera")
					.item(0);
			N3Camera acam = (N3Camera) getNamedObject(nodeInfo
					.getAttribute("camera"));
			setActiveCamera(acam);
		}

		for (int i = 0; i < hierarchy.getLength(); i++) {
			nodeInfo = (Element) hierarchy.item(i);
			node = (N3Node) getNamedObject(nodeInfo.getAttribute("name"));
			node.ressolveNames(nodeInfo, this, resources);
		}
	}

	/**
	 * Establece el color por defecto de la luz ambiental de la escena.
	 * 
	 * @param c
	 *            Color de la luz ambiental
	 */
	public void setAmbientalLight(N3ColorRGBA c) {
		ambientalLight = c;
	}
}
