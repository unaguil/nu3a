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

import java.io.FileOutputStream;
import java.lang.reflect.Method;

import nu3a.names.exception.N3NameException;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;
import nu3a.scene.N3Scene;
import nu3a.util.imageLoader.N3ImageLoader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class N3Texture2D extends N3Texture {
	/**
	 * Ancho en pixels de la textura.
	 */
	protected int width;

	/**
	 * Altura en pixels de la textura.
	 */
	protected int height;

	/**
	 * Utilizada para persistencia independiente del render.
	 */
	private int loaderFormatValue;

	/**
	 * Constructor de la textura 2D.
	 */
	public N3Texture2D(N3Scene scene, String name) throws N3NameException {
		super(scene, name);
		this.width = -1;
		this.height = -1;
	}

	/**
	 * Permite obtener el alto en pixels de la textura.
	 * 
	 * @return Alto en pixels de la textura.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Permite obtener el ancho en pixels de la textura.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Genera la textura a partir de un cargador, para un render concreto
	 * 
	 * @param loader
	 *            Cargador de textura
	 * @param render
	 *            Render para el que se crea la textura
	 */
	public void genTexture(N3ImageLoader loader, N3Render render) {
		int format = 0;
		int loaderFormat;
		loaderFormat = loader.getDataFormat();
		switch (loaderFormat) {
		case N3ImageLoader.RGB:
			format = render.N3_RGB;
			break;
		case N3ImageLoader.RGBA:
			format = render.N3_RGBA;
			break;
		}
		;
		genTexture(loader.getData(), format, loader.getWidth(),
				loader.getHeight(), render);
	}

	/**
	 * Guarda el formato de color en el formato del cargador de im�genes.
	 * 
	 * @param render
	 *            Render para el que se ha generado la textura
	 * @param dataFormat
	 *            Formato de color de la textura, seg�n el render
	 */
	private void saveLoaderFormatValue(N3Render render, int dataFormat) {
		if (dataFormat == render.N3_RGBA)
			loaderFormatValue = N3ImageLoader.RGBA;
		else if (dataFormat == render.N3_RGB)
			loaderFormatValue = N3ImageLoader.RGB;
	}

	/**
	 * Genera la textura a partir de los datos indicados
	 * 
	 * @param data
	 *            Datos de color de la textura
	 * @param dataFormat
	 *            Formato de color de la textura (seg�n el render)
	 * @param width
	 *            Anchura de la textura
	 * @param height
	 *            Altura de la textura
	 * @param render
	 *            Render para el que se genera la textura
	 */
	public void genTexture(byte[] data, int dataFormat, int width, int height,
			N3Render render) {
		this.data = data;
		this.dataFormat = dataFormat;
		this.width = width;
		this.height = height;
		this.render = render;

		this.id = render.genTexture2D(data, dataFormat,
				render.N3_UNSIGNED_BYTE, width, height);
		saveLoaderFormatValue(render, dataFormat);
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = super.getXMLDescription(doc, resources);

		Element data;
		Element loaderinfo = (Element) result
				.getElementsByTagName("loaderinfo").item(0);
		loaderinfo.setAttribute("class",
				"nu3a.util.imageLoader.N3RawImageLoader");

		try {
			FileOutputStream f = new FileOutputStream(name + ".texture.raw");
			f.write(this.data);
			f.close();

			data = doc.createElement("filename");
			data.setAttribute("value", name + ".texture.raw");
			loaderinfo.appendChild(data);

		} catch (Exception e) {
			System.err.println("Could not save " + name + ".texture.raw");
		}

		data = doc.createElement("alpha");
		data.setAttribute("value", "" + alpha);
		loaderinfo.appendChild(data);

		data = doc.createElement("dataformat");
		data.setAttribute("value", "" + loaderFormatValue);
		loaderinfo.appendChild(data);

		data = doc.createElement("size");
		data.setAttribute("width", "" + width);
		data.setAttribute("height", "" + height);
		loaderinfo.appendChild(data);

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
		N3Texture2D result;

		Element data = (Element) infoNode.getElementsByTagName("name").item(0);
		result = new N3Texture2D(scene, data.getAttribute("data"));

		data = (Element) infoNode.getElementsByTagName("loaderinfo").item(0);
		Class c = Class.forName(data.getAttribute("class"));
		Class[] parTypes = new Class[] { Class.forName("org.w3c.dom.Element"),
				Class.forName("nu3a.render.N3Render") };
		Method m = c.getMethod("parseInstance", parTypes);
		Object[] pars = new Object[] { data, render };
		N3ImageLoader l = (N3ImageLoader) m.invoke(null, pars);

		result.genTexture(l, render);

		return result;
	}
}
