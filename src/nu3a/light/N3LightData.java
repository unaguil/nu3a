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

package nu3a.light;

import nu3a.material.color.N3ColorRGBA;
import nu3a.math.N3Vector3D;
import nu3a.persistence.N3PersistentResource;
import nu3a.persistence.N3PersistentResourceList;
import nu3a.persistence.N3SceneReader;
import nu3a.render.N3Render;
import nu3a.scene.N3Scene;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Esta clase representa los datos de una luz: sus componentes de color
 * ambiental, especular y difuso, y sus atenuaciones. Tambi�n mantiene datos
 * como el �ngulo de apertura y la direcci�n, que determinar�n el comportamiento
 * exacto de la luz.
 * 
 * Existen tres tipos b�sicos de comportamiento: - Luz puntual: es una luz
 * posicional que emite luz en todas las direcciones. - Luz focal: es una luz
 * posicional que emite luz dentro de un determinado cono, con un determinado
 * �ngulo de apertura. - Luz direccional: es una luz sin posici�n, que emite
 * haces de luz paralelos en una determinada direcci�n.
 */

public class N3LightData implements N3PersistentResource {
	/**
	 * Componente ambiental de la luz.
	 */
	protected N3ColorRGBA ambiental;
	/**
	 * Componente difusa de la luz.
	 */
	protected N3ColorRGBA diffuse;
	/**
	 * Componente especular de la luz.
	 */
	protected N3ColorRGBA specular;

	/**
	 * Vector de direcci�n.
	 */
	protected N3Vector3D direction;

	/**
	 * Indica si la luz es posicional o direccional.
	 */
	protected boolean positional;

	/**
	 * �ngulo de apertura
	 */
	protected float angle;

	/**
	 * Concentraci�n de la luz del foco.
	 */
	protected float spotExponent;

	/**
	 * Atenuaci�n de la luz. La atenuaci�n de la luz se calcula en base a la
	 * siguiente f�rmula:
	 * 
	 * factor de atenuaci�n = 1/(kc + kl*d + kq*d^2)
	 */
	protected float constantAttenuation;
	protected float linearAttenuation;
	protected float quadraticAttenuation;

	/**
	 * Crea una instancia de la clase con los valores de color a blanco,
	 */
	protected N3LightData() {
		ambiental = new N3ColorRGBA();
		diffuse = new N3ColorRGBA();
		specular = new N3ColorRGBA();
		constantAttenuation = 1.0f;
		linearAttenuation = 0.0f;
		quadraticAttenuation = 0.0f;
	}

	/**
	 * Crea una instancia de la clase con los valores de color a espeficados.
	 * 
	 * @param a
	 *            Componente ambiental
	 * @param s
	 *            Componente especular
	 * @param d
	 *            Componente difusa
	 */
	protected N3LightData(N3ColorRGBA a, N3ColorRGBA s, N3ColorRGBA d) {
		ambiental = a;
		diffuse = s;
		specular = d;
		constantAttenuation = 1.0f;
		linearAttenuation = 0.0f;
		quadraticAttenuation = 0.0f;
	}

	/**
	 * Obtiene la componente ambiental de la luz
	 * 
	 * @return Componente ambiental
	 */
	public N3ColorRGBA getAmbiental() {
		return ambiental;
	}

	/**
	 * Establece la componente ambiental de la luz
	 * 
	 * @param color
	 *            Componente ambiental
	 */
	public void setAmbiental(N3ColorRGBA color) {
		ambiental = color;
	}

	/**
	 * Obtiene la componente difusa de la luz
	 * 
	 * @return Componente difusa
	 */
	public N3ColorRGBA getDiffuse() {
		return diffuse;
	}

	/**
	 * Establece la componente difusa de la luz
	 * 
	 * @param color
	 *            Componente difusa
	 */
	public void setDiffuse(N3ColorRGBA color) {
		diffuse = color;
	}

	/**
	 * Obtiene la componente especular de la luz
	 * 
	 * @return Componente especular
	 */
	public N3ColorRGBA getSpecular() {
		return specular;
	}

	/**
	 * Establece la componente especular de la luz
	 * 
	 * @param color
	 *            Componente especular
	 */
	public void setSpecular(N3ColorRGBA color) {
		specular = color;
	}

	/**
	 * Establece el factor de atenuaci�n constante de la luz.
	 * 
	 * @param f
	 *            Factor de atenuaci�n constante
	 */
	public void setConstantAttenuation(float f) {
		constantAttenuation = f;
	}

	/**
	 * Obtiene el factor de atenuaci�n constante de la luz.
	 * 
	 * @return Factor de atenuaci�n constante
	 */
	public float getConstantAttenuation() {
		return constantAttenuation;
	}

	/**
	 * Establece el factor de atenuaci�n lineal de la luz.
	 * 
	 * @param f
	 *            Factor de atenuaci�n lineal
	 */
	public void setLinearAttenuation(float f) {
		linearAttenuation = f;
	}

	/**
	 * Obtiene el factor de atenuaci�n lineal de la luz.
	 * 
	 * @return Factor de atenuaci�n lineal
	 */
	public float getLinearAttenuation() {
		return linearAttenuation;
	}

	/**
	 * Establece el factor de atenuaci�n cuadr�tica de la luz.
	 * 
	 * @param f
	 *            Factor de atenuaci�n cuadr�tica
	 */
	public void setQuadraticAttenuation(float f) {
		quadraticAttenuation = f;
	}

	/**
	 * Obtiene el factor de atenuaci�n cuadr�tica de la luz.
	 * 
	 * @return Factor de atenuaci�n cuadr�tica
	 */
	public float getQuadraticAttenuation() {
		return quadraticAttenuation;
	}

	/**
	 * Establece el vector de direcci�n de la luz.
	 * 
	 * @param v
	 *            Vector de direcci�n
	 */
	public void setDirection(N3Vector3D v) {
		direction = v;
	}

	/**
	 * Obtiene el vector de direcci�n de la luz.
	 * 
	 * @return Vector de direcci�n
	 */
	public N3Vector3D getDirection() {
		return direction;
	}

	/**
	 * Establece el �ngulo de apertura de la luz.
	 * 
	 * @param a
	 *            �ngulo de apertura en grados. Si el valor es menor que 0, se
	 *            establecer� el valor 0, si es mayor que 180, se establecer�
	 *            180.
	 */
	public void setAngle(float a) {
		angle = (a < 0) ? 0 : ((a > 180.0f) ? 180.0f : a);
	}

	/**
	 * Obtiene el �ngulo de apertura de la luz.
	 * 
	 * @return �ngulo de apertura
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 * Establece el factor de concentraci�n del foco.
	 * 
	 * @param e
	 *            Factor de concentraci�n del foco
	 */
	public void setSpotExponent(float e) {
		spotExponent = e;
	}

	/**
	 * Obtiene el factor de concentraci�n del foco.
	 * 
	 * @return Factor de concentraci�n del foco
	 */
	public float getSpotExponent() {
		return spotExponent;
	}

	/**
	 * Establece si la luz es posicional o direccional.
	 * 
	 * @param p
	 *            True si la luz es posicional, false si se direccional.
	 */
	public void setPositional(boolean p) {
		positional = p;
	}

	/**
	 * Obtiene si la luz es posicional o direccional.
	 * 
	 * @return True si la luz es posicional, false si se direccional.
	 */
	public boolean getPositional() {
		return positional;
	}

	/**
	 * Crea un obteto de datos de luz para una luz puntual de color blanco.
	 * 
	 * @return Objeto de datos de luz para una luz puntual.
	 */
	public static N3LightData createPointLightData() {
		N3LightData result = new N3LightData();
		result.angle = 180.0f;
		result.direction = new N3Vector3D(0, 0, 1.0f);
		result.positional = true;

		return result;
	}

	/**
	 * Crea un obteto de datos de luz para una luz puntual con las componentes
	 * de color especificadas.
	 * 
	 * @param a
	 *            Componente ambiental
	 * @param s
	 *            Componente especular
	 * @param d
	 *            Componente difusa
	 * @return Objeto de datos de luz para una luz puntual.
	 */
	public static N3LightData createPointLightData(N3ColorRGBA a,
			N3ColorRGBA s, N3ColorRGBA d) {
		N3LightData result = new N3LightData(a, s, d);
		result.angle = 180.0f;
		result.positional = true;
		result.direction = new N3Vector3D(0, 0, 1.0f);

		return result;
	}

	/**
	 * Crea un obteto de datos de luz para una luz focal de color blanco.
	 * 
	 * @param dir
	 *            Direcci�n de la luz
	 * @param ang
	 *            �ngulo de apertura del foco
	 * @return Objeto de datos de luz para una luz focal.
	 */
	public static N3LightData createSpotLightData(N3Vector3D dir, float ang) {
		N3LightData result = new N3LightData();
		result.angle = ang;
		result.positional = true;
		result.direction = dir;

		return result;
	}

	/**
	 * Crea un obteto de datos de luz para una luz focal con las componentes de
	 * color especificadas.
	 * 
	 * @param a
	 *            Componente ambiental
	 * @param s
	 *            Componente especular
	 * @param d
	 *            Componente difusa
	 * @param dir
	 *            Direcci�n de la luz
	 * @param ang
	 *            �ngulo de apertura del foco
	 * @return Objeto de datos de luz para una luz focal.
	 */
	public static N3LightData createSpotLightData(N3ColorRGBA a, N3ColorRGBA s,
			N3ColorRGBA d, N3Vector3D dir, float ang) {
		N3LightData result = new N3LightData(a, s, d);
		result.angle = ang;
		result.positional = true;
		result.direction = dir;

		return result;
	}

	/**
	 * Crea un obteto de datos de luz para una luz direccional de color blanco.
	 * 
	 * @param dir
	 *            Direcci�n de la luz
	 * @return Objeto de datos de luz para una luz direccional
	 */
	public static N3LightData createDirectionalLightData(N3Vector3D dir) {
		N3LightData result = new N3LightData();
		result.positional = false;
		result.direction = dir;

		return result;
	}

	/**
	 * Crea un obteto de datos de luz para una luz direccional con las
	 * componentes de color especificadas.
	 * 
	 * @param a
	 *            Componente ambiental
	 * @param s
	 *            Componente especular
	 * @param d
	 *            Componente difusa
	 * @param dir
	 *            Direcci�n de la luz
	 * @return Objeto de datos de luz para una luz direccional
	 */
	public static N3LightData createDirectionalLightData(N3ColorRGBA a,
			N3ColorRGBA s, N3ColorRGBA d, N3Vector3D dir) {
		N3LightData result = new N3LightData(a, s, d);
		result.positional = false;
		result.direction = dir;

		return result;
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
		resources.addResource(ambiental);
		resources.addResource(diffuse);
		resources.addResource(specular);
		resources.addResource(direction);
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = doc.createElement("resource");
		result.setAttribute("class", getClass().getName());

		Element data = doc.createElement("ambiental");
		data.setAttribute("index", "" + resources.indexOf(ambiental));
		result.appendChild(data);

		data = doc.createElement("diffuse");
		data.setAttribute("index", "" + resources.indexOf(diffuse));
		result.appendChild(data);

		data = doc.createElement("specular");
		data.setAttribute("index", "" + resources.indexOf(specular));
		result.appendChild(data);

		data = doc.createElement("direction");
		data.setAttribute("index", "" + resources.indexOf(direction));
		result.appendChild(data);

		data = doc.createElement("positional");
		data.setAttribute("value", "" + positional);
		result.appendChild(data);

		data = doc.createElement("angle");
		data.setAttribute("value", "" + angle);
		result.appendChild(data);

		data = doc.createElement("spotExponent");
		data.setAttribute("value", "" + spotExponent);
		result.appendChild(data);

		data = doc.createElement("attenuation");
		data.setAttribute("constant", "" + constantAttenuation);
		data.setAttribute("linear", "" + linearAttenuation);
		data.setAttribute("quadratic", "" + quadraticAttenuation);
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
		Element data = (Element) infoNode.getElementsByTagName("ambiental")
				.item(0);
		int index = Integer.parseInt(data.getAttribute("index"));
		N3ColorRGBA ambiental = (N3ColorRGBA) reader.getResource(index, nodes);

		data = (Element) infoNode.getElementsByTagName("diffuse").item(0);
		index = Integer.parseInt(data.getAttribute("index"));
		N3ColorRGBA diffuse = (N3ColorRGBA) reader.getResource(index, nodes);

		data = (Element) infoNode.getElementsByTagName("specular").item(0);
		index = Integer.parseInt(data.getAttribute("index"));
		N3ColorRGBA specular = (N3ColorRGBA) reader.getResource(index, nodes);

		data = (Element) infoNode.getElementsByTagName("direction").item(0);
		index = Integer.parseInt(data.getAttribute("index"));
		N3Vector3D direction = (N3Vector3D) reader.getResource(index, nodes);

		data = (Element) infoNode.getElementsByTagName("positional").item(0);
		boolean positional = (new Boolean(data.getAttribute("value")))
				.booleanValue();

		data = (Element) infoNode.getElementsByTagName("angle").item(0);
		float angle = Float.parseFloat(data.getAttribute("value"));

		data = (Element) infoNode.getElementsByTagName("spotExponent").item(0);
		float spotExponent = Float.parseFloat(data.getAttribute("value"));

		data = (Element) infoNode.getElementsByTagName("attenuation").item(0);
		float constant = Float.parseFloat(data.getAttribute("constant"));
		float linear = Float.parseFloat(data.getAttribute("linear"));
		float quadratic = Float.parseFloat(data.getAttribute("quadratic"));

		N3LightData result = new N3LightData(ambiental, specular, diffuse);
		result.setPositional(positional);
		result.setAngle(angle);
		result.setSpotExponent(spotExponent);
		result.setConstantAttenuation(constant);
		result.setLinearAttenuation(linear);
		result.setQuadraticAttenuation(quadratic);
		result.setDirection(direction);

		return result;
	}
}
