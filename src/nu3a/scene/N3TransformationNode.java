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

import java.util.StringTokenizer;

import nu3a.math.N3Matrix4D;
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
 * Nodo de agrupaci�n que soporta transformaciones en el espacio 3D. Estas
 * transformaciones se aplicar�n a todos los nodos hijos. Para actualizar las
 * transformaciones hechas a un nodo es necesario llamar al metodo update.
 */

public class N3TransformationNode extends N3GroupNode {
	/**
	 * Matriz de transformaci�n del nodo.
	 */
	protected N3Matrix4D matrix;

	/**
	 * Translaci�n del nodo.
	 */
	protected N3Vector3D translation;

	/**
	 * Indica que la transformaci�n es needa.
	 */
	protected boolean needTranslation;

	/**
	 * Escalado del nodo.
	 */
	protected N3Vector3D scale;

	/**
	 * Indica que el escalado es needo.
	 */
	protected boolean needScale;

	/**
	 * Angulo de rotaci�n del nodo.
	 */
	protected float angle;

	/**
	 * Vector de rotaci�n del nodo.
	 */
	protected N3Vector3D rotation;

	/**
	 * Indica que la rotaci�n es valida.
	 */
	protected boolean needRotation;

	/**
	 * Matriz de transformaci�n anterior del nodo. Utilizada por si hay que
	 * deshacer un cambio por ejemplo al detectar una colision.
	 */
	protected N3Matrix4D lastMatrix;

	/**
	 * Matriz temporal para realizar los calculos de la transformaci�n acumulada
	 * sin tener que sobrecargar al sistema con la creaci�n de una nueva
	 * instancia, cada vez que debamos recalcularla.
	 */
	private N3Matrix4D tmpAccMatrix;

	/**
	 * Constructor de la clase.
	 * 
	 * @param scene
	 *            Escena a la que pertenece el nodo.
	 * @param name
	 *            Nombre del nodo
	 */
	public N3TransformationNode(N3Scene scene, String name)
			throws N3NameException {
		super(scene, name);
		matrix = new N3Matrix4D();
		tmpAccMatrix = new N3Matrix4D();
		lastMatrix = new N3Matrix4D();
		translation = new N3Vector3D(0, 0, 0);
		scale = new N3Vector3D(1.0f, 1.0f, 1.0f);
		angle = 0;
		rotation = new N3Vector3D(1.0f, 1.0f, 1.0f);
		needTranslation = needScale = needRotation = false;
		matrix.identity();
	}

	/**
	 * M�todo que rota el nodo de transformaci�n el �ngulo indicado alrededor
	 * del eje determinado por el vector 'axis'.
	 * 
	 * @param angle
	 *            �ngulo a rotar
	 * @param axis
	 *            Eje de rotaci�n
	 */
	public void rotate(float angle, N3Vector3D axis) {
		this.angle = angle;
		rotation.x = axis.x;
		rotation.y = axis.y;
		rotation.z = axis.z;
		needRotation = true;
	}

	/**
	 * M�todo que traslada el nodo de transformaci�n en la cantidad especificada
	 * por el vector 'v'.
	 * 
	 * @param v
	 *            Vector que indica la traslaci�n en cada uno de los ejes.
	 */
	public void translate(N3Vector3D v) {
		translation.x = v.x;
		translation.y = v.y;
		translation.z = v.z;
		needTranslation = true;
	}

	/**
	 * M�todo que escala el nodo de transformaci�n en la proporci�n indicada por
	 * el vector 'v'.
	 * 
	 * @param v
	 *            Vector que indica la magnitud del escalado en cada uno de los
	 *            ejes
	 */
	public void scale(N3Vector3D v) {
		scale.x = v.x;
		scale.y = v.y;
		scale.z = v.z;
		needScale = true;
	}

	/**
	 * Postmultiplica la matriz de transformaci�n del nodo por la matriz
	 * indicada. Si se realiza un update a continuaci�n este no tendra ning�n
	 * efecto.
	 * 
	 * @param m
	 *            Matriz de transformaci�n por la que se postmultiplicar� la
	 *            matriz del nodo de transformaci�n
	 */
	public void mult(N3Matrix4D m) {
		lastMatrix.setData(matrix);
		matrix.mult(m);
		needTranslation = needScale = needRotation = false;
		setDirty();
	}

	/**
	 * Deshace la ultima transformaci�n realizada en el nodo. Si se realiza un
	 * update a continuaci�n este no tendra ning�n efecto.
	 */
	public void undo() {
		matrix.setData(lastMatrix);
		needTranslation = needScale = needRotation = false;
		setDirty();
	}

	/**
	 * Devuelve la matriz de transformaci�n del nodo.
	 * 
	 * @return Matriz de transformaci�n
	 */
	public N3Matrix4D getMatrix() {
		return matrix;
	}

	/**
	 * Establece como matriz de transformaci�n del nodo la matriz indicada.Si se
	 * realiza un update a continuaci�n este no tendra ning�n efecto.
	 * 
	 * @param m
	 *            Nueva matriz de transformaci�n
	 */

	public void setMatrix(N3Matrix4D m) {
		matrix = m;
		needTranslation = needScale = needRotation = false;
		setDirty();
	}

	/**
	 * Obtiene la matriz acumulada de transformaciones para el nodo.
	 * 
	 * @return Matriz acumulada de transformaciones
	 */
	public N3Matrix4D getAccMatrix() {
		if (isDirty) {
			super.getAccMatrix();
			tmpAccMatrix.setData(accMatrix);
			accMatrix = tmpAccMatrix;
			accMatrix.mult(matrix);
			isDirty = false;
		}
		return accMatrix;
	}

	/**
	 * Actualiza el nodo con las transformaciones que se han hecho.
	 */
	public void update() {
		if (needScale || needRotation || needTranslation) {
			lastMatrix.setData(matrix);
			if (needScale) {
				N3Matrix4D scaleMatrix = new N3Matrix4D();
				scaleMatrix.scale(scale);
				matrix.mult(scaleMatrix);
				needScale = false;
			}
			if (needRotation) {
				N3Matrix4D rotationMatrix = new N3Matrix4D();
				rotationMatrix.rotate(angle, rotation);
				matrix.mult(rotationMatrix);
				needRotation = false;
			}
			if (needTranslation) {
				N3Matrix4D translationMatrix = new N3Matrix4D();
				translationMatrix.translate(translation);
				matrix.mult(translationMatrix);
				needTranslation = false;
			}
			setDirty();
		}
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = super.getXMLDescription(doc, resources);

		String floatString = "";

		float[] m = matrix.getMatrix();

		for (int i = 0; i < 16; i++)
			floatString += ("" + m[i] + ((i < 15) ? " " : ""));

		Element data = doc.createElement("matrix");
		data.setAttribute("values", floatString);
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
		N3TransformationNode result = new N3TransformationNode(scene, name);

		Element data = (Element) infoNode.getElementsByTagName("collisionable")
				.item(0);
		result.setCollisionable((new Boolean(data.getAttribute("value")))
				.booleanValue());

		data = (Element) infoNode.getElementsByTagName("matrix").item(0);
		String floatstring = data.getAttribute("values");
		StringTokenizer st = new StringTokenizer(floatstring, " ");
		int i = 0;
		float[] matrix = result.matrix.getMatrix();
		while (st.hasMoreTokens()) {
			matrix[i] = Float.parseFloat(st.nextToken());
			i++;
		}

		return result;
	}
}
