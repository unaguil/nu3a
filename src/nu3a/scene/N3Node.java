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

import nu3a.collision.N3AABB;
import nu3a.collision.N3BoundingVolume;
import nu3a.collision.N3Collisionable;
import nu3a.collision.N3CollisionableHierarchy;
import nu3a.collision.N3CollisionableVolume;
import nu3a.geometry.N3Point3D;
import nu3a.math.N3Matrix4D;
import nu3a.names.N3NameManager;
import nu3a.names.N3NamedObject;
import nu3a.names.exception.N3NameException;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.scene.exception.N3AlreadyHasParentException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Clase padre de los nodos de la escena.
 * 
 * Si no es el nodo ra�z deber� tener un nodo padre. El nodo ra�z de la escena
 * tendr� como padre un nodo nulo (null).
 */
public abstract class N3Node implements N3NamedObject,
		N3CollisionableHierarchy, N3PersistentResource {

	/**
	 * Matriz de transformaci�n que se aplica a este nodo y a sus hijos. Es
	 * resultado de la multiplicaci�n de las matrices de transformaci�n de los
	 * nodos de transformaci�n superiores en la jerarqu�a.
	 */
	protected N3Matrix4D accMatrix;

	/**
	 * Variable que indica si hay que recalcular la matriz de transformaci�n
	 * acumulada.
	 */
	protected boolean isDirty;

	/**
	 * Escena. Es la escena a la que pertenece el nodo. Un nodo s�lo puede
	 * pertenecer a una escena.
	 */
	protected N3Scene scene;

	/**
	 * Nodo padre. Es el nodo inmediatamente superior en la jerarqu�a de la
	 * escena.
	 */
	protected N3GroupNode parent;

	/**
	 * Nombre del nodo
	 */
	protected String name;

	/**
	 * Volumen de contenci�n.
	 */
	protected N3BoundingVolume bVolume;

	/**
	 * Indica si el se debe comprobar si el volumen asociado colisiona.
	 */
	protected boolean collisionable;

	/**
	 * Constructor de la clase.
	 * 
	 * @param scene
	 *            Escena a la que pertenece el nodo.
	 * @param s
	 *            Nombre del nodo
	 */
	protected N3Node(N3Scene scene, String n) throws N3NameException {
		parent = null;
		this.scene = scene;
		setDirty();
		name = n;
		scene.addNamedObject(this);
		bVolume = new N3AABB();
		collisionable = false;
	}

	/**
	 * Establece el nombre del nodo. Ha de ser �nico para toda la escena.
	 * 
	 * @param n
	 *            Nombre del nodo
	 */
	public void setName(String n) throws N3NameException {
		if (!n.equals(name)) {
			scene.removeNamedObject(this);
			name = n;
			scene.addNamedObject(this);
		}
	}

	public void setNameManager(N3NameManager manager) {
	}

	/**
	 * Obtiene el nombre del nodo
	 * 
	 * @return Nombre del nodo
	 */
	public String getName() {
		return name;
	}

	/**
	 * Establece el padre del nodo. Se llamar� autom�ticamente al a�adir este
	 * nodo como hijo a un N3GroupNode.
	 * 
	 * Lanza una excepci�n del tipo N3AlreadyHasParentException en el caso de
	 * que el nodo ya tenga asignado un nodo padre.
	 * 
	 * @param node
	 *            Nodo padre
	 */
	protected void setParent(N3GroupNode node)
			throws N3AlreadyHasParentException {
		if (parent != null)
			throw new N3AlreadyHasParentException(
					"This node has a parent already.");
		parent = node;
		setDirty();
	}

	/**
	 * Devuelve una referencia a la escena .
	 * 
	 * @return Referencia a la escena.
	 */
	public N3Scene getScene() {
		return scene;
	}

	/**
	 * Devuelve una referencia al nodo padre.
	 * 
	 * @return Referencia al nodo padre.
	 */
	public N3GroupNode getParent() {
		return parent;
	}

	/**
	 * Elimina la referencia al padre. Este m�todo se invoca por el m�todo
	 * removeChild del padre.
	 */
	protected void removeParent() {
		parent = null;
	}

	/**
	 * Indica que la matriz de transformaci�n acumulada es inv�lida y hay que
	 * recalcularla.
	 */
	protected void setDirty() {
		isDirty = true;
	}

	/**
	 * Obtiene la matriz acumulada de transformaciones para el nodo.
	 * 
	 * @return Matriz acumulada de transformaciones
	 */
	public N3Matrix4D getAccMatrix() {
		if (isDirty) {
			if (parent == null) {
				accMatrix = new N3Matrix4D();
			} else {
				accMatrix = parent.getAccMatrix();
			}
			isDirty = false;
		}
		return accMatrix;
	}

	/**
	 * Actualiza el nodo si se ha producido algun cambio.
	 */

	/**
	 * Elimina el nodo del �rbol de escena. Las clases que hereden de N3Node
	 * redefinir�n este m�todo para eliminar las referencias espec�ficas de su
	 * tipo.
	 */
	public void remove() {
		N3Camera c;
		scene.removeNamedObject(this);
		if (parent == null)
			parent.removeChild(this);
		for (int i = 0; i < scene.getCameraCount(); i++) {
			c = scene.getCamera(i);
			if (c.getTarget() == this)
				c.setTarget(null, null);
		}
	}

	/**
	 * Indica si la subjerarqu�a a la que pertenece el nodo ha sido ya a�adida a
	 * la jerarqu�a de la escena a la que pertenece, es decir, si recorriendo
	 * los nodos de hijo a padre, empezando por el nodo indicado, se puede
	 * llegar hasta el nodo ra�z.
	 * 
	 * @return true en el caso de que el nodo pertenezca a una subjerarqu�a
	 *         insertada en la jerarqu�a de la escena <br>
	 *         false en el caso de que el nodo no pertenezca a una subjerarqu�a
	 *         insertada en la jerarqu�a de la escena
	 */
	public boolean inScene() {
		if (this == scene.getHierarchyRoot())
			return true;
		else if (parent == null)
			return false;
		else
			return parent.inScene();
	}

	/**
	 * Permite obtener la posici�n del nodo en el espacio.
	 * 
	 * @return Posici�n del nodo en el espacio.
	 */

	public N3Point3D getPosition() {
		return getAccMatrix().getPosition();
	}

	// // Implementaci�n de la interfaz N3CollisionableHierarchy

	public void updateBV() {
		getAccMatrix();
		calculeBV(accMatrix);
	}

	public boolean test(N3Collisionable c) {
		if (c instanceof N3CollisionableVolume) {
			N3BoundingVolume bv = ((N3CollisionableVolume) c)
					.getBoundingVolume();
			return ((bVolume != null) && (bv != null) && bVolume.test(bv));
		} else
			return ((bVolume != null) && bVolume.test(c));
	}

	public Vector testRec(N3Collisionable c) {
		Vector collisions = new Vector(0, 1);
		if (test(c))
			collisions.add(this);
		return collisions;
	}

	public void setCollisionable(boolean c) {
		collisionable = c;
	}

	public boolean getCollisionable() {
		return collisionable;
	}

	public N3BoundingVolume getBoundingVolume() {
		return bVolume;
	}

	public void calculeBV(N3Matrix4D m) {
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = doc.createElement("node");
		result.setAttribute("class", getClass().getName());
		result.setAttribute("name", name);
		result.setAttribute("parentname", parent.name);

		Element data = doc.createElement("collisionable");
		data.setAttribute("value", "" + collisionable);
		result.appendChild(data);

		return result;
	}

	/**
	 * Resuelve los nombres que hayan podido quedar sin resolver en el proceso
	 * de carga.
	 * 
	 * @param info
	 *            Datos del nodo
	 * @param manager
	 *            Gestor de nombres
	 * @param resources
	 *            Lista de recursos
	 */
	public void ressolveNames(Element info, N3NameManager manager,
			N3PersistentResourceList resources) {
	}
}
