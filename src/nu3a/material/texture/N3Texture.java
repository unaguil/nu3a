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

import java.util.Vector;

import nu3a.names.N3NameManager;
import nu3a.names.N3NamedObject;
import nu3a.names.exception.N3NameException;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.render.N3Render;
import nu3a.scene.N3Scene;
import nu3a.util.Observable;
import nu3a.util.Observer;
import nu3a.util.imageLoader.N3ImageLoader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Clase abstracta que define las caracteristicas de una textura gen�rica.
 */
public abstract class N3Texture implements Observable, N3NamedObject,
		N3PersistentResource {

	/**
	 * Nombre de la textura.
	 */
	protected String name;

	/**
	 * Escena a la que pertenece.
	 */
	protected N3Scene scene;

	/**
	 * Identificador de la textura.
	 */
	protected int id;

	/**
	 * Render en el que existe textura.
	 */
	protected N3Render render;

	/**
	 * Informaci�n de la imagen.
	 */
	public byte[] data;

	/**
	 * Valor alpha para hacer el blend de textura.
	 */
	protected float alpha;

	/**
	 * Formato de la informaci�n.
	 */
	protected int dataFormat;

	/**
	 * Observers de la textura.
	 */
	protected Vector obs;

	/**
	 * Constructor. Crea una textura, es necesario llamar al metodo genTexture
	 * para generar la textura, indicando el array de datos y el render donde se
	 * genera la textura. Cada textura se crea y existe en un unico render.
	 * 
	 * @param scene
	 *            Escena a la que pertenece la textura.
	 * @param n
	 *            Nombre de la textura.
	 */
	protected N3Texture(N3Scene scene, String n) throws N3NameException {
		name = n;
		this.scene = scene;
		obs = new Vector(0, 1);
		this.alpha = 0.0f;
		scene.addNamedObject(this);
	}

	// ////////////// Implementaci�n de la interfaz N3NamedObject
	/**
	 * Establece el nombre del nodo. Ha de ser �nico para toda la escena.
	 * 
	 * @param n
	 *            Nombre del nodo
	 */
	public void setName(String n) throws N3NameException {
		scene.removeNamedObject(this);
		name = n;
		scene.addNamedObject(this);
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

	// //////////////

	/**
	 * Realiza de forma correcta la liberaci�n de la textura en el render en el
	 * que se creo.
	 */
	protected void finalize() {
		render.deleteTexture(id);
	}

	/**
	 * Permite obtener el identificativo de la textura.
	 * 
	 * @return Identificativo de la textura.
	 */
	public int getID() {
		return id;
	}

	public byte[] getData() {
		return data;
	}

	/**
	 * Permite obtener el formato de informaci�n de la textura.
	 */
	public int getDataFormat() {
		return dataFormat;
	}

	/**
	 * Permite cambiar el alpha de la textura.
	 * 
	 * @param alpha
	 *            Valor entre 0.0 y 1.0 para realizar el blending.
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
		// Notificamos a los observers del cambio.
		notifyObservers();
	}

	/**
	 * Permite obtener el alpha asociado a la textura
	 * 
	 * @return Valor alpha asociado a la textura.
	 */
	public float getAlpha() {
		return alpha;
	}

	/**
	 * Permite registrar un observador que sera notificado cuando cambie alguna
	 * caracteritistica de la textura.
	 * 
	 * @param o
	 *            Observador de los cambios en la textura.
	 */
	public void registryObserver(Observer o) {
		obs.add(o);
	}

	/**
	 * Permite eliminar un observador de la textura.
	 * 
	 * @param o
	 *            Observador a eliminar.
	 */
	public void removeObserver(Observer o) {
		obs.remove(o);
	}

	/**
	 * Notifica a los observadores registrados de que se ha producido algun
	 * cambio en la textura.
	 */
	public void notifyObservers() {
		for (int i = 0; i < obs.size(); i++) {
			Observer o = (Observer) obs.elementAt(i);
			o.update();
		}
	}

	/**
	 * Genera la textura a partir del loader indicado y en el render indicado.
	 * Este loader es el encargado de convertir la informacion obtenida del
	 * InputStream al formato manejado internamente por la textura.
	 * 
	 * @param loader
	 *            Loader que carga la informacion de imagen para generar la
	 *            textura.
	 * @param render
	 *            Render en el que generar la informacion de textura.
	 */
	abstract public void genTexture(N3ImageLoader loader, N3Render render);

	/**
	 * Genera la textura en el render indicado a partir de los bytes pasados en
	 * el formato indicado.
	 * 
	 * @param data
	 *            Bytes de informaci�n de la imagen para generar la textura.
	 * @param dataFormat
	 *            Formado de los bytes pasados.
	 * @param width
	 *            Ancho de la imagen pasada.
	 * @param height
	 *            Alto de la imagen pasada.
	 */
	abstract public void genTexture(byte[] data, int dataFormat, int width,
			int height, N3Render render);

	/**
	 * Construye los mip-maps a partir de esta textura. Genera a partir de la
	 * textura actual nuevas texturas con distintas resoluciones para utilizar
	 * como mipmaps.
	 */
	public void buildMipMap() {
	}

	/**
	 * Permite cambiar el filtro de la textura. Es filtro afecta directamente a
	 * la calidad de la textura renderizada, asi como a la velocidad del
	 * dibujado.
	 */

	public void setFilter() {
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = doc.createElement("resource");
		result.setAttribute("class", getClass().getName());

		Element data = doc.createElement("name");
		data.setAttribute("value", name);
		result.appendChild(data);

		result.appendChild(doc.createElement("loaderinfo"));

		return result;
	}
}
