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

import java.awt.Rectangle;

import nu3a.camera.N3CameraData;
import nu3a.camera.exception.N3InvalidCameraValuesException;
import nu3a.collision.N3Ray;
import nu3a.geometry.N3Point2D;
import nu3a.geometry.N3Point3D;
import nu3a.math.N3Matrix4D;
import nu3a.math.N3Vector3D;
import nu3a.names.N3NameManager;
import nu3a.names.exception.N3NameException;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * La clase N3SpotLight implementa la funcionalidad de una c�mara. Para esta
 * analog�a se define el concepto de visualizaci�n a trav�s de una c�mara como
 * el vol�men de la escena que va a proyectarse en el plano de visualizaci�n.
 * Para ello una c�mara define un �ngulo de apertura, junto con la distancia
 * m�nima y m�xima a la c�mara dentro de la cual se encuentra el vol�men de
 * visualizaci�n.
 * 
 * La posici�n de la c�mara y su direcci�n vendr�n dadas por las
 * transformaciones de sus padres. Una c�mara que cuelgue directamente del nodo
 * ra�z mirar� en la direcci�n de Z negativo, y se encontrar� en la posici�n
 * (0,0,0) del eje de coordenadas del universo de la escena. Bibliograf�a: - The
 * Red Book - Chapter 3: Projection Transformations - The Red Book - Appendix G:
 * Transformation Matrices - OpenGL� Sample Implementation (Silicon Graphics).
 * URL: <http://oss.sgi.com/projects/ogl-sample/> - MESA Source Code. URL:
 * <http://www.mesa.org>
 */
public class N3Camera extends N3LeafNode {

	/**
	 * Matriz de proyecci�n en de la c�mara
	 */
	protected N3Matrix4D matrix;

	/**
	 * Nodo objetivo. Si este valor no es null, la c�mara siempre estar� mirando
	 * a este nodo, independientemente de la rotaci�n a la que la c�mara est�
	 * sometida.
	 */
	protected N3Node target;

	/**
	 * Indica d�nde est� la parte de "arriba" de la escena. Se utiliza en el
	 * caso de que la c�mara tenga un objetivo.
	 */
	protected N3Vector3D top;

	/**
	 * Matriz temporal para calcular la matriz acumulada en el caso de que la
	 * c�mara tenga un nodo objetivo.
	 */
	protected N3Matrix4D targetMatrix;

	/**
	 * Variable que indica si hay que recalcular la matriz de la c�mara.
	 */
	protected boolean dirty;

	/**
	 * Objeto que guarda los datos relativos al objetivo de la c�mara.
	 */
	N3CameraData internalCamera;

	/**
	 * Almacena la matriz invertida de la camara
	 */
	protected N3Matrix4D inverseTransform;

	/**
	 * Construye una instancia de una c�mara con el �ngulo de apertura, la
	 * relaci�n altura/anchura, y las distancias m�nima y m�xima a la c�mara
	 * especificadas.
	 * 
	 * @param v
	 *            Viewport de la camara.
	 * @param a
	 *            �ngulo de apertura
	 * @param wh
	 *            Relaci�n de anchura entre altura del vol�men
	 * @param zN
	 *            Distancia m�nima a la c�mara
	 * @param zF
	 *            Distancia m�xima a la c�mara
	 * @param scene
	 *            Escena a la que pertenece el nodo.
	 * @param name
	 *            Nombre del nodo
	 */
	public N3Camera(Rectangle v, float a, float wh, float zN, float zF,
			N3Scene scene, String name) throws N3InvalidCameraValuesException,
			N3NameException {
		super(scene, name);
		internalCamera = new N3CameraData(v, a, wh, zN, zF);
		target = null;
		matrix = new N3Matrix4D();
		targetMatrix = new N3Matrix4D();
		inverseTransform = new N3Matrix4D();
		internalCamera.addCamera(this);
		scene.addCamera(this);
	}

	/**
	 * Construye una instancia de una c�mara con el objeto de datos de c�mara
	 * especificado.
	 * 
	 * @param data
	 *            Datos de la c�mara
	 * @param scene
	 *            Escena a la que pertenece el nodo
	 * @param name
	 *            Nombre del nodo
	 */
	public N3Camera(N3CameraData data, N3Scene scene, String name)
			throws N3InvalidCameraValuesException, N3NameException {
		super(scene, name);
		internalCamera = data;
		target = null;
		matrix = new N3Matrix4D();
		targetMatrix = new N3Matrix4D();
		inverseTransform = new N3Matrix4D();
		internalCamera.addCamera(this);
		scene.addCamera(this);
	}

	/**
	 * Recalcula la matriz de proyecci�n de la c�mara. Esta funci�n se llama
	 * autom�ticamente cada vez que cambia alguno de los valores que interviene
	 * en su definici�n. Esta matriz esta transpuesta para directamente
	 * utilizarla como coordenadas de visualizacion.
	 */
	protected void redoProjectionMatrix() {
		if (dirty || isDirty) {
			float zNear, zFar, left, right, bottom, top, x, y, a, b, c, d;
			float[] m;

			left = internalCamera.left();
			right = internalCamera.right();
			top = internalCamera.top();
			bottom = internalCamera.bottom();
			zNear = internalCamera.getZNear();
			zFar = internalCamera.getZFar();

			x = (2.0F * zNear) / (right - left);
			y = (2.0F * zNear) / (top - bottom);
			a = (right + left) / (right - left);
			b = (top + bottom) / (top - bottom);
			c = -(zFar + zNear) / (zFar - zNear);
			d = -(2.0F * zFar * zNear) / (zFar - zNear); /* error? */

			matrix.zero();
			m = matrix.getMatrix();

			m[0] = x;
			m[8] = a;
			m[5] = y;
			m[9] = b;
			m[10] = c;
			m[14] = d;
			m[11] = -1.0f;
			dirty = false;
		}
	}

	/**
	 * Obtiene la matriz de proyecci�n asociada a esta c�mara.
	 * 
	 * @return Matriz de proyecci�n asociada a la c�mara.
	 */
	public N3Matrix4D getProjectionMatrix() {
		redoProjectionMatrix();
		return matrix;
	}

	/**
	 * Elimina el nodo del �rbol de escena. Las clases que hereden de N3Node
	 * redefinir�n este m�todo para eliminar las referencias espec�ficas de su
	 * tipo.
	 */
	public void remove() {
		super.remove();
		internalCamera.removeCamera(this);
		scene.removeCamera(this);
	}

	/**
	 * Establece el nodo objetivo de la c�mara. Si es valor es diferente de
	 * null, la c�mara siempre se encontrar� mirando hacia dicho nodo.
	 * 
	 * @param node
	 *            Nodo objetivo de la c�mara
	 * @param t
	 *            Vector que indica cu�l es la parte de "arriba" de la escena
	 */
	public void setTarget(N3Node node, N3Vector3D t) {
		target = node;
		top = t;
	}

	/**
	 * Obtiene el nodo objetivo de la c�mara. Si es valor es diferente de null,
	 * la c�mara siempre se encontrar� mirando hacia dicho nodo.
	 * 
	 * @return Nodo objetivo de la c�mara
	 */
	public N3Node getTarget() {
		return target;
	}

	/**
	 * Establece el vector que indica cu�l es la parte de "arriba" de la escena.
	 * 
	 * @param t
	 *            Vector que indica cu�l es la parte de "arriba" de la escena
	 */
	public void setTop(N3Vector3D t) {
		top = t;
	}

	/**
	 * Obtiene el vector que indica cu�l es la parte de "arriba" de la escena.
	 * 
	 * @return Vector que indica cu�l es la parte de "arriba" de la escena
	 */
	public N3Vector3D getTop() {
		return top;
	}

	/**
	 * Calcula la matriz acumulada de la c�mara teniendo en cuenta su posici�n,
	 * la posici�n del nodo objetivo, y el vector que indica d�nde se encuentra
	 * la parte superior de la escena.
	 */
	protected void cameraLookAt(N3Vector3D eye, N3Vector3D center,
			N3Vector3D top) {
		N3Vector3D x, y, z;
		float[] m;

		z = new N3Vector3D(eye.x - center.x, eye.y - center.y, eye.z - center.z);
		z.normalize();

		y = new N3Vector3D(top.x, top.y, top.z);
		x = N3Vector3D.crossProduct(y, z);
		y = N3Vector3D.crossProduct(z, x);

		x.normalize();
		y.normalize();

		m = targetMatrix.getMatrix();

		m[0] = x.x;
		m[1] = x.y;
		m[2] = x.z;
		m[3] = 0.0f;
		m[4] = y.x;
		m[5] = y.y;
		m[6] = y.z;
		m[7] = 0.0f;
		m[8] = z.x;
		m[9] = z.y;
		m[10] = z.z;
		m[11] = 0.0f;
		m[12] = eye.x;
		m[13] = eye.y;
		m[14] = eye.z;
		m[15] = 1.0f;
	}

	/**
	 * Obtiene la matriz acumulada de transformaciones para el nodo.
	 * 
	 * @return Matriz acumulada de transformaciones
	 */
	public N3Matrix4D getAccMatrix() {
		super.getAccMatrix();
		if (target != null) {
			N3Matrix4D targetM = target.getAccMatrix();
			float[] tmpMatrix = accMatrix.getMatrix();
			// Las coordenadas de posici�n est�n abajo, no en la derecha...
			// mierda de traspuestas...
			N3Vector3D eye = new N3Vector3D(tmpMatrix[12], tmpMatrix[13],
					tmpMatrix[14]);
			tmpMatrix = targetM.getMatrix();
			N3Vector3D center = new N3Vector3D(tmpMatrix[12], tmpMatrix[13],
					tmpMatrix[14]);
			targetMatrix.identity();
			cameraLookAt(eye, center, top);
			accMatrix = targetMatrix;
		}
		return accMatrix;
	}

	/**
	 * Obtiene la transformaci�n de la camara, es la inversa de la matriz
	 * acumulada.
	 * 
	 * @return Matriz de representa la transformaci�n del mundo para verse a
	 *         traves de la camara.
	 */
	public N3Matrix4D getCameraTransformation() {
		inverseTransform.setData(getAccMatrix());
		inverseTransform.inverse();
		return inverseTransform;
	}

	/**
	 * Notifica al nodo que los datos de su c�mara interna han cambiado.
	 */
	public void setDirtyCamera() {
		dirty = true;
	}

	/**
	 * Obtiene la referencia al objeto que contiene los datos de la c�mara.
	 * 
	 * @return Datos de la c�mara
	 */
	public N3CameraData getCameraData() {
		return internalCamera;
	}

	/**
	 * Establece el objeto que contiene los datos de la c�mara
	 * 
	 * @param cd
	 *            Datos de la c�mara
	 */
	public void setCameraData(N3CameraData cd) {
		if (internalCamera != null)
			internalCamera.removeCamera(this);
		internalCamera = cd;
		internalCamera.addCamera(this);
	}

	/**
	 * Permite obtener un rayo de la longitud indicada que parte desde la
	 * situaci�n de la camara teniendo en cuenta las coordenadas del punto
	 * indicado.
	 */

	public N3Ray getRay(N3Point2D p, float length) {
		// Normalizar las coordenadas
		Rectangle v = internalCamera.getViewport();
		float persp = (float) Math
				.tan(Math.toRadians(internalCamera.getFovy()) * 0.5f);
		float dx = persp * ((p.x - v.x) / (v.width / 2.0f) - 1.0f)
				/ internalCamera.getAspect();
		float dy = -persp * ((p.y - v.y) / (v.height / 2.0f) - 1.0f)
				/ internalCamera.getAspect();

		N3Point3D src = new N3Point3D(dx * internalCamera.getZNear(), dy
				* internalCamera.getZNear(), -internalCamera.getZNear());
		N3Point3D dest = new N3Point3D(dx * internalCamera.getZFar(), dy
				* internalCamera.getZFar(), -internalCamera.getZFar());

		src = getAccMatrix().mult(src);
		dest = getAccMatrix().mult(dest);

		return new N3Ray(src, dest);
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
		resources.addResource(top);
		resources.addResource(internalCamera);
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = super.getXMLDescription(doc, resources);

		Element data;
		if (target != null) {
			data = doc.createElement("targetname");
			data.setAttribute("value", target.getName());
			result.appendChild(data);
		}

		if (top != null) {
			data = doc.createElement("top");
			data.setAttribute("index", "" + resources.indexOf(top));
			result.appendChild(data);
		}

		data = doc.createElement("internal");
		data.setAttribute("index", "" + resources.indexOf(internalCamera));
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
		N3CameraData c = (N3CameraData) resources.resourceAt(index);
		N3Camera result = new N3Camera(c, scene, name);

		data = (Element) infoNode.getElementsByTagName("collisionable").item(0);
		result.setCollisionable((new Boolean(data.getAttribute("value")))
				.booleanValue());

		return result;
	}

	public void ressolveNames(Element info, N3NameManager manager,
			N3PersistentResourceList resources) {
		if (info.getElementsByTagName("targetname").getLength() > 0) {
			String name = ((Element) info.getElementsByTagName("targetname")
					.item(0)).getAttribute("value");
			N3Node target = (N3Node) manager.getNamedObject(name);
			int index = Integer.parseInt(((Element) info.getElementsByTagName(
					"top").item(0)).getAttribute("index"));
			N3Vector3D top = (N3Vector3D) resources.resourceAt(index);
			setTarget(target, top);
		}
	}
}
