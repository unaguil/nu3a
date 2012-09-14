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

package nu3a.material.color;

import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;
import nu3a.scene.N3Scene;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Clase que representa un color dadas sus componentes RGBA (Rojo, Verde, Az�l,
 * canal Alfa).
 */
public class N3ColorRGBA implements N3PersistentResource {
	/**
	 * Componente de color rojo.
	 */
	public float R;
	/**
	 * Componente de color verde.
	 */
	public float G;
	/**
	 * Componente de color az�l.
	 */
	public float B;
	/**
	 * Componente del canal alfa (transparencia).
	 */
	public float A;

	/**
	 * Array temporal para devolver la informaci�n RGBA de la luz.
	 */
	private float[] c = new float[4];

	/**
	 * Constructor de la clase. Devuelve una instancia con el color negro y
	 * totalmente opaca -RGBA(0,0,0,1)-.
	 */
	public N3ColorRGBA() {
		R = 0;
		G = 0;
		B = 0;
		A = 1.0f;
	}

	/**
	 * Constructor de la clase. Devuelve una instancia con el color indicado.
	 * 
	 * @param red
	 *            Componente de color rojo
	 * @param green
	 *            Componente de color verde
	 * @param blue
	 *            Componente de color az�l
	 * @param alpha
	 *            Componente del canal alfa
	 */
	public N3ColorRGBA(float red, float green, float blue, float alpha) {
		R = red;
		G = green;
		B = blue;
		A = alpha;
	}

	/**
	 * Constructor de la clase. Devuelve una instancia con el color indicado con
	 * el valor alpha a 1.0f.
	 * 
	 * @param red
	 *            Componente de color rojo
	 * @param green
	 *            Componente de color verde
	 * @param blue
	 *            Componente de color az�l
	 */
	public N3ColorRGBA(float red, float green, float blue) {
		R = red;
		G = green;
		B = blue;
		A = 1.0f;
	}

	/**
	 * Copoa los datos del color indicado.
	 * 
	 * @param c
	 *            Color del que obtener los datos.
	 */
	public void setData(N3ColorRGBA c) {
		R = c.R;
		G = c.G;
		B = c.B;
		A = c.A;
	}

	/**
	 * Obtiene la representaci�n en forma de String del color.
	 * 
	 * @return String que representa el objeto.
	 */
	public String toString() {
		String res = "";
		res += "R:" + R + ",G:" + G + "B:" + B + "A:" + A;
		return res;
	}

	/**
	 * Obtiene la componente de color rojo.
	 * 
	 * @return Componente de color rojo
	 */
	public float getR() {
		return R;
	}

	/**
	 * Establece la componente de color rojo.
	 * 
	 * @param red
	 *            Componente de color rojo
	 */
	public void setR(float red) {
		R = red;
	}

	/**
	 * Obtiene la componente de color verde.
	 * 
	 * @return Componente de color verde
	 */
	public float getG() {
		return G;
	}

	/**
	 * Establece la componente de color verde.
	 * 
	 * @param green
	 *            Componente de color verde
	 */
	public void setG(float green) {
		G = green;
	}

	/**
	 * Obtiene la componente de color az�l.
	 * 
	 * @return Componente de color az�l
	 */
	public float getB() {
		return B;
	}

	/**
	 * Establece la componente de color az�l.
	 * 
	 * @param blue
	 *            Componente de color az�l
	 */
	public void setB(float blue) {
		B = blue;
	}

	/**
	 * Obtiene la componente alfa.
	 * 
	 * @return Componente alfa
	 */
	public float getA() {
		return A;
	}

	/**
	 * Establece la componente alfa.
	 * 
	 * @param alpha
	 *            Componente alfa
	 */
	public void setA(float alpha) {
		A = alpha;
	}

	/**
	 * Obtiene las componentes de color del objeto.
	 * 
	 * @return Array con la informaci�n RGBA del objeto.
	 */
	public float[] getColorArray() {
		c[0] = R;
		c[1] = G;
		c[2] = B;
		c[3] = A;

		return c;
	}

	/**
	 * Obtiene la representaci�n del color en un entero en el cual los dos bytes
	 * de menor peso corresponden al valor rojo, y los siguientes, en orden
	 * ascendente, al verde, az�l y canal alfa.
	 */
	public int getPackedValue() {
		return ((int) (255 * R)) + (((int) (255 * G)) << 8)
				+ (((int) (255 * B)) << 16) + (((int) (255 * A)) << 24);
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = doc.createElement("resource");
		result.setAttribute("class", getClass().getName());
		Element data = doc.createElement("data");
		data.setAttribute("r", "" + R);
		data.setAttribute("g", "" + G);
		data.setAttribute("b", "" + B);
		data.setAttribute("a", "" + A);
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
			N3SceneReader reader, N3Render render, N3Scene scene) {
		Element data = (Element) infoNode.getElementsByTagName("data").item(0);
		float r = Float.parseFloat(data.getAttribute("r"));
		float g = Float.parseFloat(data.getAttribute("g"));
		float b = Float.parseFloat(data.getAttribute("b"));
		float a = Float.parseFloat(data.getAttribute("a"));
		return new N3ColorRGBA(r, g, b, a);
	}
}
