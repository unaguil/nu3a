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

import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nu3a.scene.N3Scene;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Esta clase proporciona la clase base para poder almacenar una escena en un
 * medio persistente.
 */
public abstract class N3SceneWriter {
	/**
	 * Documento DOM con la informaci�n de la escena.
	 */
	protected Document document;

	private N3Scene scene;
	private N3PersistentResourceList resources;
	private Element root;

	/**
	 * Crea una instancia de la clase
	 */
	protected N3SceneWriter() {
		resources = new N3PersistentResourceList();
	}

	private void getResources() {
		resources.clear();
		scene.getPersistentResources(resources);
		Element resourceRoot = document.createElement("resources");
		root.appendChild(resourceRoot);
		Element resource;
		for (int i = 0; i < resources.size(); i++) {
			resource = resources.resourceAt(i).getXMLDescription(document,
					resources);
			resourceRoot.appendChild(resource);
		}
	}

	private void getHierarchy() {
		Element sceneRoot = scene.getXMLDescription(document, resources);
		root.appendChild(sceneRoot);
	}

	private void createXMLDocument() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		document = db.newDocument();
		root = document.createElement("scenepackage");
		document.appendChild(root);
		getResources();
		getHierarchy();
	}

	/**
	 * Guarda la informaci�n de la escena, presente en el Documento DOM, en el
	 * Stream de salida.
	 */
	protected abstract void saveToInternalFormat(OutputStream os);

	/**
	 * Guarda la informaci�n de la escena en el Stream de salida.
	 * 
	 * @param scene
	 *            Escena a guardar
	 * @param os
	 *            Stream de salida en el que se escrbir� la informaci�n
	 */
	public void save(N3Scene scene, OutputStream os) {
		this.scene = scene;
		try {
			createXMLDocument();
			saveToInternalFormat(os);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}
