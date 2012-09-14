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

package nu3a.material;

import java.util.Vector;

import nu3a.material.color.N3ColorData;
import nu3a.material.color.N3ColorRGBA;
import nu3a.material.texture.N3Texture;
import nu3a.material.texture.N3Texture2D;
import nu3a.names.N3NameManager;
import nu3a.names.N3NamedObject;
import nu3a.names.exception.N3NameException;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;
import nu3a.scene.N3Scene;
import nu3a.util.Observer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Clase que contiene informaci�n de material de un objeto.
 */
public class N3Material implements Observer, N3NamedObject,
		N3PersistentResource {
	/**
	 * El material se aplica a la cara frontal.
	 */
	public final static int N3_FRONT = 0;
	/**
	 * El material se aplica a la cara trasera.
	 */
	public final static int N3_BACK = 1;
	/**
	 * El material se aplica por las dos caras.
	 */
	public final static int N3_FRONT_AND_BACK = 2;

	/**
	 * No se aplica textura.
	 */
	public final static int N3_NO_TEXTURE = 0;
	/**
	 * La textura reemplaza al color del objeto.
	 */
	public final static int N3_REPLACE = 1;
	/**
	 * La textura se aplica de forma decal.
	 */
	public final static int N3_DECAL = 2;
	/**
	 * La textura modula el color del objeto.
	 */
	public final static int N3_MODULATE = 3;
	/**
	 * La textura se aplica mediante haciendo blending.
	 */
	public final static int N3_BLEND = 4;

	/**
	 * Nombre del material
	 */
	protected String name;

	/**
	 * Escena a la que pertenece el material.
	 */
	protected N3Scene scene;

	/**
	 * Informaci�n del color del objeto.
	 */
	protected N3ColorData colorData;

	/**
	 * Texturas asociadas a este material.
	 */
	protected Vector textures;

	/**
	 * Indica que se aplica multitextura.
	 */
	protected boolean multitexture;

	/**
	 * Indica que la multitextura esta sucia y se debe recalcular.
	 */
	protected boolean isDirty;

	/**
	 * Almacena la textura resultado de la aplicaci�n de multitexturas.
	 */
	protected N3Texture texture_calc;

	/**
	 * Indica el modo seleccionado para aplicar la textura del material.
	 */
	protected int texture_mode;

	/**
	 * Indica cual era el estado del render
	 */
	protected boolean oldTextureStatus;

	/**
	 * Indica en el modo de textura �nica cual de las texturas que posee el
	 * material es la textura activa.
	 */
	protected N3Texture active_texture;

	/**
	 * Indica si se aplica las caracteristicas del material.
	 */
	protected boolean apply_material;

	/**
	 * Indica a que cara(s) se aplica el material.
	 */
	protected int face;

	/**
	 * Color ambiental del material
	 */
	protected N3ColorRGBA ambient_color;

	/**
	 * Color difuso del material
	 */
	protected N3ColorRGBA diffuse_color;

	/**
	 * Color especular del material.
	 */
	protected N3ColorRGBA specular_color;

	/**
	 * Color de la luz emitida por el objeto.
	 */
	protected N3ColorRGBA emission_color;

	/**
	 * Brillo de la emision de color.
	 */
	protected float shininess;

	/**
	 * Constructor el material. Caracteristicas iniciales aaaa
	 * 
	 * @param name
	 *            Nombre del material.
	 */
	public N3Material(N3Scene scene, String name) throws N3NameException {
		this.name = name;
		this.scene = scene;
		scene.addNamedObject(this);
		colorData = new N3ColorData();
		textures = new Vector(0, 1);
		texture_mode = N3_MODULATE;
		multitexture = false;
		isDirty = false;
		active_texture = null;
		apply_material = false;
		ambient_color = new N3ColorRGBA(1f, 1f, 1f);
		diffuse_color = new N3ColorRGBA(1f, 1f, 1f);
		specular_color = new N3ColorRGBA(1f, 1f, 1f);
		emission_color = new N3ColorRGBA(0f, 0f, 0f);
		face = N3_FRONT;
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
	 * Avisa del cambio de alguna de las texturas.
	 */
	public void update() {
		setDirty();
	}

	/**
	 * Permite cambiar la informaci�n de color del material
	 * 
	 * @param colorData
	 *            Informaci�n del color.
	 */
	public void setColorData(N3ColorData colorData) {
		this.colorData = colorData;
	}

	/**
	 * Permite obtener la informaci�n de color asociada a este material.
	 * 
	 * @return Informaci�n de color del material.
	 */
	public N3ColorData getColorData() {
		return colorData;
	}

	/**
	 * Permite determinar de que forma se mezclan la textura y el color que
	 * tiene asignado el material.
	 * 
	 * @param mode
	 *            Modo que se utilizar� para aplicar las texturas.
	 */
	public void setTextureMode(int mode) {
		texture_mode = mode;
	}

	/**
	 * Permite indicar si el material soporta multitextura.
	 */
	public void setMultitexture(boolean multitexture, N3Render render) {
		this.multitexture = multitexture;
		if (isDirty)
			calculeMultitexture(render);
	}

	/**
	 * Permite indicar que la multitextura esta sucia y es necesario
	 * recalcularla.
	 */
	protected void setDirty() {
		isDirty = true;
	}

	/**
	 * Realiza el blending de dos texturas.
	 * 
	 * @param dest
	 *            Datos de la textura destino
	 * @param src
	 *            Datos de la textura or�gen
	 * @param srcAlpha
	 *            Transparencia de la textura or�gen
	 */
	protected void blend(byte[] dest, byte[] src, float srcAlpha) {
		int end;
		end = (dest.length < src.length ? dest.length : src.length);
		float beta = (1.0f - srcAlpha);
		for (int i = 0; i < end; i++) {
			dest[i] = (byte) (((src[i] & 0xff) * srcAlpha) + ((dest[i] & 0xff) * beta));
		}
	}

	/**
	 * Recalcula la multitextura generando la textura en el render indicado.
	 */
	protected void calculeMultitexture(N3Render render) {
		if (!textures.isEmpty()) {
			N3Texture2D texture = (N3Texture2D) textures.elementAt(0);
			byte[] data = texture.getData();
			for (int i = 0; i < textures.size(); i++) {
				N3Texture2D texture2 = (N3Texture2D) textures.elementAt(i);
				blend(data, texture2.getData(), texture2.getAlpha());
			}
			try {
				texture_calc = new N3Texture2D(scene, name + "_multitext");
				texture_calc.genTexture(data, texture.getDataFormat(),
						texture.getWidth(), texture.getHeight(), render);
				active_texture = texture_calc;
			} catch (N3NameException ne) {
				System.out
						.println("Nombre de la multitextura repetida. Mal rollo!!!!");
				ne.printStackTrace();
			}
			isDirty = false;
		}
	}

	/*
	 * Permite obtener el modo en el que se aplica la textura del material.
	 * 
	 * @return Modo utilizado en la aplicaci�n de texturas.
	 */
	public int getTextureMode() {
		return texture_mode;
	}

	/**
	 * Permite a�adir una textura al material. En el modo de textura �nica la
	 * �ltima textura a�adida es la textura que se aplica.
	 * 
	 * @param texture
	 *            Textura a a�adir.
	 */
	public void addTexture(N3Texture texture) {
		active_texture = texture;
		texture.registryObserver(this);
		textures.add(texture);
		setDirty();
	}

	/**
	 * Permite eliminar del material la textura indicada. Si la textura
	 * eliminada era la activa en el modo de �nica textura, pasa a ser la
	 * textura activa la anterior si existe.
	 * 
	 * @param texture
	 *            Textura a eliminar del material.
	 */
	public void removeTexture(N3Texture texture) {

		if (!multitexture && texture == active_texture) {
			int i = textures.indexOf(texture);
			if (i > 0)
				active_texture = (N3Texture) textures.elementAt(i - 1);
			else
				active_texture = null;
		}
		texture.removeObserver(this);
		textures.remove(texture);
		setDirty();
	}

	/**
	 * Permite indicar si se aplican o no las caracteriscas del material al
	 * objeto. Es necesario ademas especificar las distintas componentes de
	 * color del material para que se aplique el material.
	 * 
	 * @param apply
	 *            True si se aplican las caracteristicas.
	 */
	public void applyMaterial(boolean apply) {
		apply_material = apply;
	}

	/**
	 * Permite especificar la componente ambiental del material.
	 * 
	 * @param c
	 *            Color ambiental del material.
	 */
	public void setAmbientColor(N3ColorRGBA c) {
		ambient_color = c;
	}

	/**
	 * Permite especificar la componente difusa del material
	 * 
	 * @param c
	 *            Color difuso del material.
	 */
	public void setDiffuseColor(N3ColorRGBA c) {
		diffuse_color = c;
	}

	/**
	 * Permite especificar la componente especular del material.
	 * 
	 * @param c
	 *            Color especular del material.
	 */
	public void setSpecularColor(N3ColorRGBA c, float shininess) {
		specular_color = c;
		this.shininess = shininess;
	}

	/**
	 * Permite especificar la componente de emisi�n de color del material.
	 * 
	 * @param c
	 *            Color de emisi�n del material.
	 * @param shininess
	 */
	public void setEmissionColor(N3ColorRGBA c) {
		emission_color = c;
	}

	/**
	 * Permite asignar todas las componentes del material.
	 * 
	 * @param c
	 *            Color ambiental del material.
	 * @param c
	 *            Color difuso del material.
	 * @param c
	 *            Color especular del material.
	 * @param shininess
	 * @param c
	 *            Color de emisi�n del material.
	 */
	public void setMaterialComponents(N3ColorRGBA ambient, N3ColorRGBA diffuse,
			N3ColorRGBA specular, float shininess, N3ColorRGBA emission) {
		ambient_color = ambient;
		diffuse_color = diffuse;
		specular_color = specular;
		emission_color = emission;
		this.shininess = shininess;
	}

	/**
	 * Pone el material actual por defecto en el render. Todos los objetos
	 * visuales se renderizaran con este material a partir de ahora.
	 * 
	 * @param render
	 *            Render en el que aplicar las caracter�sticas del material.
	 */
	public void renderMaterial(N3Render render) {
		switch (face) {
		case N3_FRONT:
			face = render.N3_FRONT;
			break;
		case N3_BACK:
			face = render.N3_BACK;
			break;
		case N3_FRONT_AND_BACK:
			face = render.N3_FRONT_AND_BACK;
			break;
		}
		;
		if (apply_material) {

			render.setColorMaterial(true);
			render.setColorMaterialAmbient(face, ambient_color);
			render.setColorMaterialDiffuse(face, diffuse_color);
			render.setColorMaterialSpecular(face, specular_color);
			render.setColorMaterialEmission(face, emission_color);
		} else {
			render.setColorMaterial(true);
			render.setColorMaterialAmbient(face, new N3ColorRGBA(1f, 1f, 1f));
			render.setColorMaterialDiffuse(face, new N3ColorRGBA(1f, 1f, 1f));
			render.setColorMaterialSpecular(face, new N3ColorRGBA(1f, 1f, 1f));
			render.setColorMaterialEmission(face, new N3ColorRGBA(0f, 0f, 0f));
		}

		if (texture_mode != N3_NO_TEXTURE && !textures.isEmpty()) {
			switch (texture_mode) {
			case N3_REPLACE:
				render.setTextureMode(render.N3_REPLACE);
				break;
			case N3_DECAL:
				render.setTextureMode(render.N3_DECAL);
				break;
			case N3_MODULATE:
				render.setTextureMode(render.N3_MODULATE);
				break;
			case N3_BLEND:
				render.setTextureMode(render.N3_BLEND);
				break;
			}
			render.setTexturing(true);
			render.selectTexture(active_texture);
			if (multitexture && isDirty)
				calculeMultitexture(render);
		} else {
			render.setTexturing(false);
		}
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
		for (int i = 0; i < textures.size(); i++)
			resources.addResource((N3Texture) textures.elementAt(i));
		resources.addResource(ambient_color);
		resources.addResource(specular_color);
		resources.addResource(diffuse_color);
		resources.addResource(emission_color);
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = doc.createElement("resource");
		result.setAttribute("class", getClass().getName());

		Element textNode;

		Element data = doc.createElement("name");
		data.setAttribute("value", name);
		result.appendChild(data);

		data = doc.createElement("textureinfo");
		for (int i = 0; i < textures.size(); i++) {
			N3Texture t;
			t = (N3Texture) textures.elementAt(i);
			textNode = doc.createElement("textureitem");
			textNode.setAttribute("index", "" + resources.indexOf(t));
			data.appendChild(textNode);
		}
		result.appendChild(data);

		data = doc.createElement("multitexture");
		data.setAttribute("value", "" + multitexture);
		result.appendChild(data);

		data = doc.createElement("texturemode");
		data.setAttribute("value", "" + texture_mode);
		result.appendChild(data);

		data = doc.createElement("activetexture");
		data.setAttribute("textureindex", "" + textures.indexOf(active_texture));
		result.appendChild(data);

		data = doc.createElement("applymaterial");
		data.setAttribute("value", "" + apply_material);
		result.appendChild(data);

		data = doc.createElement("face");
		data.setAttribute("value", "" + face);
		result.appendChild(data);

		data = doc.createElement("shininess");
		data.setAttribute("value", "" + shininess);
		result.appendChild(data);

		data = doc.createElement("ambient");
		data.setAttribute("index", "" + resources.indexOf(ambient_color));
		result.appendChild(data);

		data = doc.createElement("specular");
		data.setAttribute("index", "" + resources.indexOf(specular_color));
		result.appendChild(data);

		data = doc.createElement("diffuse");
		data.setAttribute("index", "" + resources.indexOf(diffuse_color));
		result.appendChild(data);

		data = doc.createElement("emission");
		data.setAttribute("index", "" + resources.indexOf(emission_color));
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
		Element texData;
		NodeList texts;
		N3Texture tex;
		int index;
		N3ColorRGBA a, s, d, e;
		float shininess;
		Element data = (Element) infoNode.getElementsByTagName("name").item(0);

		N3Material result = new N3Material(scene, data.getAttribute("value"));

		data = (Element) infoNode.getElementsByTagName("textureinfo").item(0);
		texts = infoNode.getElementsByTagName("textureitem");
		for (int i = 0; i < texts.getLength(); i++) {
			texData = (Element) texts.item(i);
			index = Integer.parseInt(texData.getAttribute("index"));
			tex = (N3Texture) reader.getResource(index, nodes);
			result.addTexture(tex);
		}
		data = (Element) infoNode.getElementsByTagName("multitexture").item(0);
		result.setMultitexture(
				(new Boolean(data.getAttribute("value"))).booleanValue(),
				render);
		data = (Element) infoNode.getElementsByTagName("texturemode").item(0);
		result.setTextureMode(Integer.parseInt(data.getAttribute("value")));
		data = (Element) infoNode.getElementsByTagName("activetexture").item(0);
		index = Integer.parseInt(data.getAttribute("textureindex"));
		if (index != -1)
			result.active_texture = (N3Texture) result.textures
					.elementAt(index);
		else
			result.active_texture = null;
		data = (Element) infoNode.getElementsByTagName("applymaterial").item(0);
		result.applyMaterial((new Boolean(data.getAttribute("value")))
				.booleanValue());
		data = (Element) infoNode.getElementsByTagName("face").item(0);
		result.face = Integer.parseInt(data.getAttribute("value"));

		data = (Element) infoNode.getElementsByTagName("shininess").item(0);
		shininess = Float.parseFloat(data.getAttribute("value"));
		data = (Element) infoNode.getElementsByTagName("ambient").item(0);
		index = Integer.parseInt(data.getAttribute("index"));
		a = (N3ColorRGBA) reader.getResource(index, nodes);
		data = (Element) infoNode.getElementsByTagName("diffuse").item(0);
		index = Integer.parseInt(data.getAttribute("index"));
		d = (N3ColorRGBA) reader.getResource(index, nodes);
		data = (Element) infoNode.getElementsByTagName("specular").item(0);
		index = Integer.parseInt(data.getAttribute("index"));
		s = (N3ColorRGBA) reader.getResource(index, nodes);
		data = (Element) infoNode.getElementsByTagName("emission").item(0);
		index = Integer.parseInt(data.getAttribute("index"));
		e = (N3ColorRGBA) reader.getResource(index, nodes);

		result.setMaterialComponents(a, d, s, shininess, e);

		return result;
	}
}
