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

package nu3a.persistence;

import java.io.InputStream;
import java.lang.reflect.Method;

import nu3a.render.N3Render;
import nu3a.scene.N3Scene;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Clase base para la implementaci�n de un cargador de escenas, capaz de obtener
 * y crear una escena en un medio persistente.
 */
public abstract class N3SceneReader {

	/**
	 * Documento DOM con la descripci�n de la escena.
	 */
	protected Document document;
	private N3PersistentResourceList resources;
	private N3Render render;
	private N3Scene scene;

	/**
	 * Constructor de la clase.
	 */
	protected N3SceneReader() {
	}

	private void loadResource(int i, NodeList resourceElements)
			throws Exception {
		if (resources.resourceAt(i) == null) {
			Element e = (Element) resourceElements.item(i);
			String type = e.getAttribute("class");
			Class c = Class.forName(type);
			Class[] parTypes = new Class[] {
					Class.forName("org.w3c.dom.Element"),
					Class.forName("org.w3c.dom.NodeList"),
					resources.getClass(),
					Class.forName("nu3a.persistence.N3SceneReader"),
					Class.forName("nu3a.render.N3Render"),
					Class.forName("nu3a.scene.N3Scene") };
			Method m = c.getMethod("loadInstance", parTypes);
			Object[] pars = new Object[] { e, resourceElements, resources,
					this, render, scene };
			N3PersistentResource res = (N3PersistentResource) m.invoke(null,
					pars);
			resources.setResource(res, i);
		}
	}

	/**
	 * Obtiene el i-�simo recurso de la lista de recursos.
	 * 
	 * @param i
	 *            �ndice del recurso
	 * @param resourceElements
	 *            Lista de nodos DOM con la informaci�n de los recursos
	 * @return Recurso solicitado
	 */
	public N3PersistentResource getResource(int i, NodeList resourceElements)
			throws Exception {
		loadResource(i, resourceElements);
		return resources.resourceAt(i);
	}

	private void parseResources() throws Exception {
		NodeList resourceElements = ((Element) document.getElementsByTagName(
				"resources").item(0)).getElementsByTagName("resource");
		resources = new N3PersistentResourceList(resourceElements.getLength());
		for (int i = 0; i < resources.size(); i++)
			loadResource(i, resourceElements);
	}

	private void parseHierarchy() throws Exception {
		scene.selfConfigure(document, resources, render, this);
	}

	private N3Scene parseScene() throws Exception {
		scene = new N3Scene();
		parseResources();
		parseHierarchy();
		return scene;
	}

	/**
	 * Obtiene un documento DOM a partir del Stream de entrada, con la
	 * informaci�n de la escena.
	 * 
	 * @param is
	 *            Stream de entrada con la informaci�n de la escena
	 * @return Documento DOM con la informaci�n de la escena
	 */
	protected abstract Document readFromInternalFormat(InputStream is);

	/**
	 * Obtiene la escena en el medio persistente.
	 * 
	 * @param is
	 *            Medio persistente en el que se encuentra la escena
	 * @param render
	 *            Render para el se que cargar� la escena
	 * @return Escena cargada
	 */
	public N3Scene read(InputStream is, N3Render render) throws Exception {
		document = readFromInternalFormat(is);
		this.render = render;
		return parseScene();
	}
}
