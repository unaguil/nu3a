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

import nu3a.collision.N3BoundingVolume;
import nu3a.collision.N3Collisionable;
import nu3a.names.exception.N3NameException;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;
import nu3a.scene.exception.N3AlreadyHasParentException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Clase nodo de agrupaci�n. Este tipo de nodos sirve como padre de otros nodos
 * de la jerarqu�a, los cuales se agrupan como hijos de �ste.
 */
public class N3GroupNode extends N3Node {
	/**
	 * Vector que contiene los nodos hijos del nodo de agrupaci�n en la escena.
	 */
	protected Vector children;

	/**
	 * Constructor de la clase. Crea un nodo de agrupaci�n para la escena
	 * especificada.
	 * 
	 * @param scene
	 *            Escena a la que pertenece el nodo.
	 * @param name
	 *            Nombre del nodo
	 */
	public N3GroupNode(N3Scene scene, String name) throws N3NameException {
		super(scene, name);
		children = new Vector(0, 1);
	}

	/**
	 * M�todo que a�ade un hijo al nodo al nodo de agrupaci�n, y establece al
	 * nodo como padre del nodo hijo.
	 * 
	 * @param node
	 *            Nodo hijo
	 */
	public void addChild(N3Node node) throws N3AlreadyHasParentException {
		node.setParent(this);
		children.add(node);
	}

	/**
	 * M�todo que quita un hijo al nodo de agrupaci�n, y establece al nodo hijo
	 * como un nodo sin padre.
	 * 
	 * @param node
	 *            Nodo hijo
	 */
	public void removeChild(N3Node node) {
		children.remove(node);
		node.removeParent();
	}

	/**
	 * M�todo que devuelve el conjunto de hijos del nodo.
	 * 
	 * @return Nodos hijos del nodo de agrupaci�n.
	 */

	public N3Node[] getChildren() {
		N3Node[] result = new N3Node[children.size()];
		return (N3Node[]) children.toArray(result);
	}

	/**
	 * Indica que la matriz de transformaci�n acumulada es inv�lida y hay que
	 * recalcularla, y notifica esta situaci�n a los nodos hijos, que se ven
	 * afectados por ello.
	 */
	protected void setDirty() {
		super.setDirty();
		for (int i = 0; (children != null) && (i < children.size()); i++)
			((N3Node) children.elementAt(i)).setDirty();
	}

	/**
	 * Elimina el nodo del �rbol de escena. Las clases que hereden de N3Node
	 * redefinir�n este m�todo para eliminar las referencias espec�ficas de su
	 * tipo.
	 */
	public void remove() {
		super.remove();
		for (int i = 0; (children != null) && (i < children.size()); i++)
			((N3Node) children.elementAt(i)).remove();
	}

	// / Implementaci�n interfaz N3Collisionable

	public Vector testRec(N3Collisionable c) {
		Vector collisions = new Vector(0, 1);
		if (test(c)) {
			for (int i = 0; i < children.size(); i++) {
				N3Node node = (N3Node) children.elementAt(i);
				Vector v = node.testRec(c);
				for (int j = 0; j < v.size(); j++)
					collisions.add(v.elementAt(j));
			}
		}
		return collisions;
	}

	/*
	 * public void calculeBV(N3Matrix4D m) { bVolume = new N3AABB(); }
	 */

	public void updateBV() {
		super.updateBV();
		boolean first = true;
		setCollisionable(false);
		for (int i = 0; i < children.size(); i++) {
			N3Node node = (N3Node) children.elementAt(i);
			node.updateBV();
			if (node.getCollisionable()) {
				N3BoundingVolume bv = node.getBoundingVolume();
				if (first) {
					bVolume.setData(bv);
					first = false;
				}
				bVolume.add(bv);
				setCollisionable(true);
			}
		}
		isDirty = false;
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
		N3GroupNode result = new N3GroupNode(scene, name);

		Element data = (Element) infoNode.getElementsByTagName("collisionable")
				.item(0);
		result.setCollisionable((new Boolean(data.getAttribute("value")))
				.booleanValue());
		return result;
	}
}
