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

package nu3a.geometry;

import java.util.Vector;

import nu3a.collision.N3AABB;
import nu3a.collision.N3BoundingVolume;
import nu3a.collision.N3Collisionable;
import nu3a.collision.N3CollisionableVolume;
import nu3a.material.color.N3ColorRGBA;
import nu3a.material.texture.N3TexCoord2D;
import nu3a.math.N3Matrix4D;
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
 * Clase para la representaci�n de informacion geom�trica.
 */
public class N3GeometryData implements N3CollisionableVolume,
		N3PersistentResource {
	/**
	 * Vector que contiene las referencias a los objetos visuales que est�n
	 * utilizando esta geometr�a.
	 */
	protected Vector refs;

	/**
	 * Vector que contiene los pol�gonos de la gometr�a.
	 */
	protected Vector polys;

	/*
	 * Vector que almacena los poligonos de la geometria que han sido
	 * collisionado en el �ltimo test.
	 */
	protected Vector collisionPolys;

	/**
	 * Volumen de contenci�n de la geometr�a.
	 */
	protected N3BoundingVolume bVolume;

	/**
	 * Crea un objeto de la clase, que ser� utilizado para guardar informaci�n
	 * de geometr�a que ser� luego visualizada.
	 */
	public N3GeometryData() {
		refs = new Vector(0, 1);
		polys = new Vector(0, 1);
		collisionPolys = new Vector(0, 1);
	}

	/**
	 * Crea un nuevo pol�gono en el objeto de geometr�a, lo a�ade al repositorio
	 * de pol�gonos del objeto, y lo devuelve al usuario.
	 * 
	 * @return Nuevo pol�gono
	 */
	public N3Polygon createPolygon() {
		N3Polygon p = new N3Polygon(this);
		polys.add(p);
		return p;
	}

	/**
	 * Obtiene un pol�gono seg�n su �ndice.
	 * 
	 * @param i
	 *            �ndice el pol�gono
	 * @return Pol�gono
	 */
	public N3Polygon getPolygon(int i) {
		return (N3Polygon) polys.elementAt(i);
	}

	/**
	 * Elimina el pol�gono especificado.
	 * 
	 * @param p
	 *            Pol�gono a eliminar
	 */
	public void removePolygon(N3Polygon p) {
		polys.remove(p);
	}

	/**
	 * Obtiene el n�mero de pol�gonos de la geometr�a.
	 * 
	 * @return N�mero de pol�gonos
	 */
	public int polygonCount() {
		return polys.size();
	}

	/**
	 * A�ade un objeto que ser� notificado de los cambios de la geometr�a.
	 * 
	 * @param l
	 *            Objeto a ser notificado
	 */
	public void addGeometryListener(N3GeometryListener l) {
		refs.add(l);
		l.notifyGeometry();
	}

	/**
	 * Elimina un objeto para que no sea notificado de cambios en la geometr�a.
	 * 
	 * @param l
	 *            Objeto que dejar� de ser notificado
	 */
	public void removeGeometryListener(N3GeometryListener l) {
		refs.remove(l);
	}

	/**
	 * Notifica a todos los objetos que la geometr�a ha cambiado.
	 */
	public void notifyGeometryListeners() {
		for (int i = 0; i < refs.size(); i++) {
			((N3GeometryListener) refs.elementAt(i)).notifyGeometry();
		}
	}

	/**
	 * Crea una malla que representa un plano de la anchura y altura indicadas.
	 * 
	 * @param width
	 *            Anchura del plano
	 * @param height
	 *            Altura del plano
	 * @return Malla con la geometr�a del plano
	 */
	public static N3GeometryData createPlane(float width, float height,
			N3ColorRGBA c, boolean textured) {
		float wd2 = width / 2;
		float hd2 = height / 2;
		N3GeometryData mesh = new N3GeometryData();
		N3Point3D vertexes[] = new N3Point3D[4];
		vertexes[0] = new N3Point3D(-wd2, -hd2, 0);
		vertexes[1] = new N3Point3D(wd2, -hd2, 0);
		vertexes[2] = new N3Point3D(wd2, hd2, 0);
		vertexes[3] = new N3Point3D(-wd2, hd2, 0);
		N3Polygon p = mesh.createPolygon();
		if (!textured) {
			p.addVertex(vertexes[0], c);
			p.addVertex(vertexes[1], c);
			p.addVertex(vertexes[2], c);
			p.addVertex(vertexes[3], c);
		} else {
			p.addVertex(vertexes[0], c, new N3TexCoord2D(0, 0));
			p.addVertex(vertexes[1], c, new N3TexCoord2D(1, 0));
			p.addVertex(vertexes[2], c, new N3TexCoord2D(1, 1));
			p.addVertex(vertexes[3], c, new N3TexCoord2D(0, 1));
		}

		p.generateNormal();

		return mesh;
	}

	/**
	 * Crea una malla que representa un cubo de la largura, anchura y altura
	 * indicadas.
	 * 
	 * @param length
	 *            Largura del cubo
	 * @param width
	 *            Anchura del cubo
	 * @param height
	 *            Altura del cubo
	 * @return Malla con la geometr�a del cubo
	 */
	public static N3GeometryData createCube(float length, float width,
			float height, N3ColorRGBA c, boolean textured) {
		float wd2 = width / 2;
		float hd2 = height / 2;
		float ld2 = height / 2;
		N3GeometryData mesh = new N3GeometryData();

		N3TexCoord2D uvs[] = new N3TexCoord2D[4];
		uvs[0] = new N3TexCoord2D(0, 0);
		uvs[1] = new N3TexCoord2D(1, 0);
		uvs[2] = new N3TexCoord2D(1, 1);
		uvs[3] = new N3TexCoord2D(0, 1);

		N3Point3D vertexes[] = new N3Point3D[8];
		vertexes[0] = new N3Point3D(-wd2, -hd2, ld2);
		vertexes[1] = new N3Point3D(wd2, -hd2, ld2);
		vertexes[2] = new N3Point3D(wd2, hd2, ld2);
		vertexes[3] = new N3Point3D(-wd2, hd2, ld2);
		vertexes[4] = new N3Point3D(wd2, -hd2, -ld2);
		vertexes[5] = new N3Point3D(-wd2, -hd2, -ld2);
		vertexes[6] = new N3Point3D(-wd2, hd2, -ld2);
		vertexes[7] = new N3Point3D(wd2, hd2, -ld2);

		N3Polygon p = mesh.createPolygon();
		if (!textured) {
			p.addVertex(vertexes[0], c);
			p.addVertex(vertexes[1], c);
			p.addVertex(vertexes[2], c);
			p.addVertex(vertexes[3], c);
		} else {
			p.addVertex(vertexes[0], c, uvs[0]);
			p.addVertex(vertexes[1], c, uvs[1]);
			p.addVertex(vertexes[2], c, uvs[2]);
			p.addVertex(vertexes[3], c, uvs[3]);
		}
		p.generateNormal();

		p = mesh.createPolygon();
		if (!textured) {
			p.addVertex(vertexes[1], c);
			p.addVertex(vertexes[4], c);
			p.addVertex(vertexes[7], c);
			p.addVertex(vertexes[2], c);
		} else {
			p.addVertex(vertexes[1], c, uvs[0]);
			p.addVertex(vertexes[4], c, uvs[1]);
			p.addVertex(vertexes[7], c, uvs[2]);
			p.addVertex(vertexes[2], c, uvs[3]);
		}
		p.generateNormal();

		p = mesh.createPolygon();
		if (!textured) {
			p.addVertex(vertexes[4], c);
			p.addVertex(vertexes[5], c);
			p.addVertex(vertexes[6], c);
			p.addVertex(vertexes[7], c);
		} else {
			p.addVertex(vertexes[4], c, uvs[0]);
			p.addVertex(vertexes[5], c, uvs[1]);
			p.addVertex(vertexes[6], c, uvs[2]);
			p.addVertex(vertexes[7], c, uvs[3]);
		}
		p.generateNormal();

		p = mesh.createPolygon();
		if (!textured) {
			p.addVertex(vertexes[5], c);
			p.addVertex(vertexes[0], c);
			p.addVertex(vertexes[3], c);
			p.addVertex(vertexes[6], c);
		} else {
			p.addVertex(vertexes[5], c, uvs[0]);
			p.addVertex(vertexes[0], c, uvs[1]);
			p.addVertex(vertexes[3], c, uvs[2]);
			p.addVertex(vertexes[6], c, uvs[3]);
		}
		p.generateNormal();

		p = mesh.createPolygon();
		if (!textured) {
			p.addVertex(vertexes[3], c);
			p.addVertex(vertexes[2], c);
			p.addVertex(vertexes[7], c);
			p.addVertex(vertexes[6], c);
		} else {
			p.addVertex(vertexes[3], c, uvs[0]);
			p.addVertex(vertexes[2], c, uvs[1]);
			p.addVertex(vertexes[7], c, uvs[2]);
			p.addVertex(vertexes[6], c, uvs[3]);
		}
		p.generateNormal();

		p = mesh.createPolygon();
		if (!textured) {
			p.addVertex(vertexes[4], c);
			p.addVertex(vertexes[1], c);
			p.addVertex(vertexes[0], c);
			p.addVertex(vertexes[5], c);
		} else {
			p.addVertex(vertexes[4], c, uvs[0]);
			p.addVertex(vertexes[1], c, uvs[1]);
			p.addVertex(vertexes[0], c, uvs[2]);
			p.addVertex(vertexes[5], c, uvs[3]);
		}
		p.generateNormal();

		return mesh;
	}

	/**
	 * Crea una malla que representa una esfera de radio indicado, a partir de
	 * un n�mero de paralelos y meridianos.
	 * 
	 * @param length
	 *            Largura del cubo
	 * @param width
	 *            Anchura del cubo
	 * @param height
	 *            Altura del cubo
	 * @return Malla con la geometr�a del cubo
	 */
	public static N3GeometryData createSphere(int slices, int rings,
			float radius, N3ColorRGBA c) {
		float tmpCenter = 0;
		float tmpRadius = 0;
		float pass;
		N3GeometryData res = new N3GeometryData();
		N3Polygon p;
		N3Point3D[] vertexes = new N3Point3D[slices * (rings - 1) + 2];
		vertexes[0] = new N3Point3D(0, radius, 0); // V�rtice 0: parte superior
		tmpCenter = radius
				* (float) Math.sin(Math.PI / 2 - Math.PI / (2 * rings)); // V�rtices
																			// 1
																			// a
																			// slices:
																			// primer
																			// ring
		tmpRadius = radius
				* (float) Math.cos(Math.PI / 2 - Math.PI / (2 * rings)); // V�rtices
																			// 1
																			// a
																			// slices:
																			// primer
																			// ring
		for (int i = 1; i < slices + 1; i++) {
			pass = (float) (Math.PI * 2 * (i - 1) / slices);
			vertexes[i] = new N3Point3D(tmpRadius * (float) Math.cos(pass),
					tmpCenter, tmpRadius * (float) Math.sin(pass));
		}
		for (int i = 0; i < slices; i++) // Caras superiores
		{
			p = res.createPolygon();
			p.addVertex(vertexes[((i + 1) == (slices)) ? 1 : i + 2], c);
			p.addVertex(vertexes[i + 1], c);
			p.addVertex(vertexes[0], c);
			p.generateNormal();
		}
		float tmp;
		for (int i = 1; i < rings - 1; i++) // V�rtices
		{
			tmpCenter = radius
					* (float) Math.sin(Math.PI / 2 - Math.PI * (i + 1)
							/ (rings));
			tmpRadius = radius
					* (float) Math.cos(Math.PI / 2 - Math.PI * (i + 1)
							/ (rings));
			for (int j = slices * i + 1; j < slices * (i + 1) + 1; j++) {
				pass = (float) (Math.PI * 2 * (j - (slices * i + 1)) / slices);
				vertexes[j] = new N3Point3D(tmpRadius * (float) Math.cos(pass),
						tmpCenter, tmpRadius * (float) Math.sin(pass));
			}
		}
		for (int i = 1; i < rings - 1; i++)
			for (int j = slices * i + 1; j < slices * (i + 1) + 1; j++) {
				p = res.createPolygon();
				p.addVertex(vertexes[(j % slices == 0) ? (1 + j - slices)
						: (j + 1)], c);
				p.addVertex(vertexes[j], c);
				p.addVertex(vertexes[j - slices], c);
				p.addVertex(vertexes[(j % slices == 0) ? (1 + j - 2 * slices)
						: (j - slices + 1)], c);
				p.generateNormal();
			}

		vertexes[slices * (rings - 1) + 1] = new N3Point3D(0, -radius, 0);

		for (int i = slices * (rings - 2) + 1; i < slices * (rings - 1) + 1; i++) {
			p = res.createPolygon();
			p.addVertex(vertexes[slices * (rings - 1) + 1], c);
			p.addVertex(vertexes[i], c);
			p.addVertex(vertexes[(i % slices == 0) ? slices * (rings - 2) + 1
					: (i + 1)], c);
			p.generateNormal();
		}

		return res;
	}

	/**
	 * Permite obtener un vector con los pol�gonos de la geometr�a que han
	 * colisionado en el ultimo test.
	 * 
	 * @return Poligonos que han colisionado en el �ltimo test.
	 */
	public Vector getCollisionPolys() {
		return collisionPolys;
	}

	/**
	 * Este m�todo permite comprobar si con los poligonos de la geometr�a
	 * colisiona el objeto indicado, transformando momentaneamente para realizar
	 * el test cada pol�gono con la matriz indicada. Se puede indicar que
	 * compruebe todos los pol�gonos de la geometr�a o solo el primer pol�gono
	 * que colisiona.
	 * 
	 * @param c
	 *            Objeto con el que comprobar si hay colisi�n.
	 * @param m
	 *            Matriz por la que transformar momentaneamente la geometr�a.
	 * @param all
	 *            Indica que el test no se detenga al detectar la primera
	 *            collision.
	 * @return Indica si ha habido colisi�n o no.
	 */
	public boolean testGeometry(N3Collisionable c, N3Matrix4D m, boolean all) {
		collisionPolys = new Vector(0, 1);
		boolean result = false;
		for (int i = 0; i < polys.size(); i++) {
			N3Polygon p = (N3Polygon) polys.elementAt(i);
			if (p.test(c, m)) {
				collisionPolys.add(p);
				if (!all)
					return true;
				result = true;
			}
		}
		return result;
	}

	// /////Implementaci�n de la interfaz N3CollisionableVolume

	public N3BoundingVolume getBoundingVolume() {
		return bVolume;
	}

	public void calculeBV(N3Matrix4D m) {
		bVolume = new N3AABB(this);
		bVolume.calcule(m);
	}

	public void setCollisionable(boolean c) {
	}

	public boolean getCollisionable() {
		return true;
	}

	public boolean test(N3Collisionable c) {
		if (c instanceof N3CollisionableVolume) {
			N3BoundingVolume bv = ((N3CollisionableVolume) c)
					.getBoundingVolume();
			return ((bVolume != null) && (bv != null) && bVolume.test(bv));
		} else
			return ((bVolume != null) && bVolume.test(c));
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
		for (int i = 0; i < polys.size(); i++)
			getPolygon(i).getPersistentResources(resources);
	}

	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources) {
		Element result = doc.createElement("resource");
		result.setAttribute("class", getClass().getName());
		Element polyNode;
		Element vertexData;
		for (int i = 0; i < polys.size(); i++) {
			N3Polygon p;
			p = getPolygon(i);
			polyNode = doc.createElement("poly");
			result.appendChild(polyNode);
			for (int j = 0; j < p.getSides(); j++) {
				vertexData = doc.createElement("vertexinfo");
				vertexData.setAttribute("vIndex",
						"" + resources.indexOf(p.getVertex(j)));
				vertexData.setAttribute("nIndex",
						"" + resources.indexOf(p.getNormal(j)));
				vertexData.setAttribute("cIndex",
						"" + resources.indexOf(p.getColor(j)));
				vertexData.setAttribute("uvIndex",
						"" + resources.indexOf(p.getUV(j)));
				polyNode.appendChild(vertexData);
			}
		}

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
		NodeList polys = infoNode.getElementsByTagName("poly");
		NodeList vertexes;
		Element polyData;
		Element vertexData;
		N3Polygon p;

		int index;
		N3Point3D v;
		N3Vector3D n;
		N3ColorRGBA c;
		N3TexCoord2D uv;

		N3GeometryData result = new N3GeometryData();

		for (int i = 0; i < polys.getLength(); i++) {
			p = result.createPolygon();
			polyData = (Element) polys.item(i);
			vertexes = polyData.getElementsByTagName("vertexinfo");
			for (int j = 0; j < vertexes.getLength(); j++) {
				vertexData = (Element) vertexes.item(j);

				index = Integer.parseInt(vertexData.getAttribute("vIndex"));
				v = (N3Point3D) reader.getResource(index, nodes);

				index = Integer.parseInt(vertexData.getAttribute("cIndex"));
				c = (N3ColorRGBA) reader.getResource(index, nodes);

				index = Integer.parseInt(vertexData.getAttribute("nIndex"));
				if (index != -1)
					n = (N3Vector3D) reader.getResource(index, nodes);
				else
					n = null;

				index = Integer.parseInt(vertexData.getAttribute("uvIndex"));
				if (index != -1)
					uv = (N3TexCoord2D) reader.getResource(index, nodes);
				else
					uv = null;

				if (n == null) {
					if (uv == null)
						p.addVertex(v, c);
					else
						p.addVertex(v, c, uv);
				} else {
					if (uv == null)
						p.addVertex(v, n, c);
					else
						p.addVertex(v, n, c, uv);
				}
			}
		}

		return result;
	}
}
