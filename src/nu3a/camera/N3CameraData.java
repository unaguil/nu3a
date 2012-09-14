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

package nu3a.camera;

import java.awt.Rectangle;
import java.util.Vector;

import nu3a.camera.exception.N3InvalidCameraValuesException;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;
import nu3a.scene.N3Camera;
import nu3a.scene.N3Scene;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Clase que aporta las caracter�sticas de una c�mara que aporta una vista en
 * perspectiva. El prop�sito de esta clase es la separaci�n de los datos de una
 * c�mara, de la c�mara en s�.
 */

public class N3CameraData implements N3PersistentResource {

	/**
	 * Vector de c�maras visuales que utilizan esta c�mara.
	 */
	protected Vector cameras;

	/**
	 * �ngulo de apertura del foco de la c�mara
	 */
	protected float fovy;

	/**
	 * Relaci�n entre el ancho y alto del vol�men de visualizaci�n
	 */
	protected float aspect;

	/**
	 * Distancia m�nima a la c�mara para que un objeto pueda ser visualizado.
	 */
	protected float zNear;

	/**
	 * Distancia m�xima a la c�mara para que un objeto pueda ser visualizado.
	 */
	protected float zFar;

	/**
	 * Longitud de la parte izquierda, derecha, superior e inferior del �rea de
	 * visualizaci�n. Se calculan a partir de los valores anteriores.
	 */
	protected float left, right, top, bottom;

	/**
	 * Define el viewport de la camara.
	 */
	protected Rectangle viewport;

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
	 */
	public N3CameraData(Rectangle v, float a, float wh, float zN, float zF)
			throws N3InvalidCameraValuesException {
		cameras = new Vector();
		viewport = v;
		fovy = a;
		aspect = wh;
		zNear = zN;
		zFar = zF;
		recalcule();
	}

	/**
	 * A�ade una c�mara al vector de c�maras.
	 * 
	 * @param c
	 *            C�mara a a�adir
	 */
	public void addCamera(N3Camera c) {
		cameras.add(c);
		c.setDirtyCamera();
	}

	/**
	 * Quita una c�mara del vector de c�maras.
	 * 
	 * @param c
	 *            C�mara a quitar
	 */
	public void removeCamera(N3Camera c) {
		cameras.remove(c);
	}

	/**
	 * Notifica a todas las c�maras que se ha cambiado alguno de los datos de la
	 * c�mara.
	 */
	protected void notifyCameras() {
		for (int i = 0; i < cameras.size(); i++)
			((N3Camera) cameras.elementAt(i)).setDirtyCamera();
	}

	/**
	 * Recalcula y verifica los datos que se utilizar�n para el c�lculo de la
	 * matriz de proyecci�n.
	 */
	protected void recalcule() throws N3InvalidCameraValuesException {
		top = zNear * (float) Math.tan(fovy * Math.PI / 360.0);
		bottom = -top;
		left = bottom * aspect;
		right = top * aspect;
		if (zNear <= 0.0 || zFar <= 0.0 || zNear == zFar || left == right
				|| top == bottom)
			throw new N3InvalidCameraValuesException("Invalid Values: zNear="
					+ zNear + "  zFar=" + zFar + " left=" + left + " right="
					+ right + " top=" + top + "  bottom=" + bottom);

		notifyCameras();
	}

	/**
	 * Establece el �ngulo de apertura de la c�mara.
	 * 
	 * @param a
	 *            �ngulo de apertura
	 */
	public void setFovy(float a) throws N3InvalidCameraValuesException {
		fovy = a;
		recalcule();
	}

	/**
	 * Obtiene el �ngulo de apertura de la c�mara.
	 * 
	 * @return �ngulo de apertura
	 */
	public float getFovy() {
		return fovy;
	}

	/**
	 * Establece la relaci�n anchura/altura del vol�men de visualizaci�n.
	 * 
	 * @param a
	 *            Relaci�n anchura/altura del vol�men de visualizaci�n
	 */
	public void setAspect(float a) throws N3InvalidCameraValuesException {
		aspect = a;
		recalcule();
	}

	/**
	 * Obtiene la relaci�n anchura/altura del vol�men de visualizaci�n.
	 * 
	 * @return Relaci�n anchura/altura del vol�men de visualizaci�n
	 */
	public float getAspect() {
		return aspect;
	}

	/**
	 * Establece la m�nima distancia de visualizaci�n con respecto a la c�mara.
	 * 
	 * @param z
	 *            M�nima distancia de visualizaci�n con respecto a la c�mara
	 */
	public void setZNear(float z) throws N3InvalidCameraValuesException {
		zNear = z;
		recalcule();
	}

	/**
	 * Obtiene la m�nima distancia de visualizaci�n con respecto a la c�mara.
	 * 
	 * @return M�nima distancia de visualizaci�n con respecto a la c�mara
	 */
	public float getZNear() {
		return zNear;
	}

	/**
	 * Establece la m�xima distancia de visualizaci�n con respecto a la c�mara.
	 * 
	 * @param z
	 *            M�xima distancia de visualizaci�n con respecto a la c�mara
	 */
	public void setZFar(float z) throws N3InvalidCameraValuesException {
		zFar = z;
		recalcule();
	}

	/**
	 * Obtiene la m�xima distancia de visualizaci�n con respecto a la c�mara.
	 * 
	 * @return M�xima distancia de visualizaci�n con respecto a la c�mara
	 */
	public float getZFar() {
		return zFar;
	}

	/**
	 * Obtiene la distancia m�xima a la que un objeto puede encontrarse en el
	 * eje X, si se encuentra a la dist�ncia zNear, para ser visualizado.
	 * 
	 * @return Distancia
	 */
	public float left() {
		return left;
	}

	/**
	 * Obtiene la distancia m�nima a la que un objeto puede encontrarse en el
	 * eje X, si se encuentra a la dist�ncia zNear, para ser visualizado.
	 * 
	 * @return Distancia
	 */
	public float right() {
		return right;
	}

	/**
	 * Obtiene la distancia m�xima a la que un objeto puede encontrarse en el
	 * eje Y, si se encuentra a la dist�ncia zNear, para ser visualizado.
	 * 
	 * @return Distancia
	 */
	public float top() {
		return top;
	}

	/**
	 * Obtiene la distancia m�nima a la que un objeto puede encontrarse en el
	 * eje Y, si se encuentra a la dist�ncia zNear, para ser visualizado.
	 * 
	 * @return Distancia
	 */
	public float bottom() {
		return bottom;
	}

	/**
	 * Permite especificar el viewport de la camara.
	 * 
	 * @param x
	 *            Coordenada x del origen del viewport.
	 * @param y
	 *            Coordenada y del origen del viewport.
	 * @param width
	 *            Ancho del viewport.
	 * @param height
	 *            Alto del viewport.
	 */
	public void setViewport(int x, int y, int width, int height) {
		viewport = new Rectangle(x, y, width, height);
	}

	/**
	 * Permite obtener el viewport de la camara.
	 * 
	 * @return Viewport de la camara.
	 */
	public Rectangle getViewport() {
		return viewport;
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = doc.createElement("resource");
		result.setAttribute("class", getClass().getName());
		Element data = doc.createElement("data");
		data.setAttribute("fovy", "" + fovy);
		data.setAttribute("aspect", "" + aspect);
		data.setAttribute("zNear", "" + zNear);
		data.setAttribute("zFar", "" + zFar);
		data.setAttribute("viewPortX", "" + viewport.getX());
		data.setAttribute("viewPortY", "" + viewport.getY());
		data.setAttribute("viewPortWidth", "" + viewport.getWidth());
		data.setAttribute("viewPortHeight", "" + viewport.getHeight());
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
			throws N3InvalidCameraValuesException {
		Element data = (Element) infoNode.getElementsByTagName("data").item(0);
		int vX = (int) Float.parseFloat(data.getAttribute("viewPortX"));
		int vY = (int) Float.parseFloat(data.getAttribute("viewPortY"));
		int vW = (int) Float.parseFloat(data.getAttribute("viewPortWidth"));
		int vH = (int) Float.parseFloat(data.getAttribute("viewPortHeight"));
		float fovy = Float.parseFloat(data.getAttribute("fovy"));
		float aspect = Float.parseFloat(data.getAttribute("aspect"));
		float zNear = Float.parseFloat(data.getAttribute("zNear"));
		float zFar = Float.parseFloat(data.getAttribute("zFar"));
		if ((vW == 0) || (vH == 0)) {
			vW = render.getRenderComponent().getWidth();
			vH = render.getRenderComponent().getHeight();
			aspect = (float) (vW / vH);
		}
		Rectangle v = new Rectangle(vX, vY, vW, vH);
		return new N3CameraData(v, fovy, aspect, zNear, zFar);
	}
}
