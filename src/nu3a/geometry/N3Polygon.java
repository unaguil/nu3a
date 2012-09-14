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

import nu3a.collision.N3Collisionable;
import nu3a.material.color.N3ColorRGBA;
import nu3a.material.texture.N3TexCoord2D;
import nu3a.math.N3Matrix4D;
import nu3a.math.N3Vector2D;
import nu3a.math.N3Vector3D;
import nu3a.persistence.N3PersistentResourceContainer;
import nu3a.persistence.N3PersistentResourceList;

/**
 * La clase N3Polygon representa un pol�gono de n lados. La cara superior es
 * aquella que se forma recorriendo los lados de manera que estos est�n en
 * sentido contrario al movimiento de las agujas del reloj.
 * 
 * Por cada v�rtice se guarda: - Una referencia al v�rtice - Una referencia a su
 * color - Una referencia a su normal - Una referencia a sus coordenadas de
 * textura, que son opcionales, pero obligatorias en el caso de que al pol�gono
 * se le vaya a aplicar una textura en su visualizaci�n
 */

public class N3Polygon implements N3Collisionable,
		N3PersistentResourceContainer {

	/**
	 * Vector que contiene los v�rtices del pol�gono, ordenados en orden
	 * contrario al movimiento de las agujas del reloj.
	 */
	protected Vector vertexes;

	/**
	 * Vector que contiene los colores del pol�gono, cuyo orden se corresponder�
	 * con el de los v�rtices.
	 */
	protected Vector colors;

	/**
	 * Vector que contiene las normales del pol�gono, cuyo orden se
	 * corresponder� con el de los v�rtices.
	 */
	protected Vector normals;

	/**
	 * Vector que contiene las coordenadas uv del pol�gono, cuyo orden se
	 * corresponder� con el de los v�rtices.
	 */
	protected Vector uvs;

	/**
	 * Objeto de geometr�a al que pertenece.
	 */
	protected N3GeometryData parent;

	/**
	 * Crea un objeto de la clase, vac�o, que no pertenece a ning�n objeto de
	 * geometr�a.
	 */
	protected N3Polygon() {
		vertexes = new Vector(0, 1);
		colors = new Vector(0, 1);
		normals = new Vector(0, 1);
		uvs = new Vector(0, 1);
		parent = null;
	}

	/**
	 * Crea una instancia de la clase, como un pol�gono vac�o.
	 * 
	 * @param p
	 *            Objeto de geometr�a al que pertenece el pol�gono
	 */
	protected N3Polygon(N3GeometryData p) {
		vertexes = new Vector(0, 1);
		colors = new Vector(0, 1);
		normals = new Vector(0, 1);
		uvs = new Vector(0, 1);
		parent = p;
	}

	/**
	 * A�ade un v�rtice al pol�gono.
	 * 
	 * @param v
	 *            V�rtice a a�adir
	 * @param n
	 *            Normal para el v�rtice
	 * @param c
	 *            Color del v�rtice
	 */
	public void addVertex(N3Point2D v, N3Vector2D n, N3ColorRGBA c) {
		vertexes.add(v);
		normals.add(n);
		colors.add(c);
		parent.notifyGeometryListeners();
	}

	/**
	 * A�ade un v�rtice al pol�gono. Antes de poder utilizar este pol�gono para
	 * su dibujado con iluminaci�n, se deber� establecer la normal del mismo, ya
	 * sea a trav�s del m�todo setNormal, o del m�todo generateNormal.
	 * 
	 * @param v
	 *            V�rtice a a�adir
	 * @param c
	 *            Color del v�rtice
	 */
	public void addVertex(N3Point2D v, N3ColorRGBA c) {
		vertexes.add(v);
		colors.add(c);
		parent.notifyGeometryListeners();
	}

	/**
	 * A�ade un v�rtice al pol�gono.
	 * 
	 * @param v
	 *            V�rtice a a�adir
	 * @param n
	 *            Normal para el v�rtice
	 * @param c
	 *            Color del v�rtice
	 * @param uv
	 *            Coordenadas de textura del v�rtice
	 */
	public void addVertex(N3Point2D v, N3Vector2D n, N3ColorRGBA c,
			N3TexCoord2D uv) {
		vertexes.add(v);
		normals.add(n);
		colors.add(c);
		uvs.add(uv);
		parent.notifyGeometryListeners();
	}

	/**
	 * A�ade un v�rtice al pol�gono. Antes de poder utilizar este pol�gono para
	 * su dibujado con iluminaci�n, se deber� establecer la normal del mismo, ya
	 * sea a trav�s del m�todo setNormal, o del m�todo generateNormal.
	 * 
	 * @param v
	 *            V�rtice a a�adir
	 * @param c
	 *            Color del v�rtice
	 * @param uv
	 *            Coordenadas de textura del v�rtice
	 */
	public void addVertex(N3Point2D v, N3ColorRGBA c, N3TexCoord2D uv) {
		vertexes.add(v);
		colors.add(c);
		uvs.add(uv);
		parent.notifyGeometryListeners();
	}

	/**
	 * Elimina un v�rtice del pol�gono, su color, y su normal, seg�n su �ndice.
	 * 
	 * @param i
	 *            �ndice del vector
	 */
	public void removeVertex(int i) {
		vertexes.remove(i);
		if (normals.size() > 0)
			normals.remove(i);
		colors.remove(i);
		if (uvs.size() > 0)
			uvs.remove(i);
		parent.notifyGeometryListeners();
	}

	/**
	 * Establece el v�rtice 'i'.
	 * 
	 * @param p
	 *            Nuevo v�rtice
	 * @param i
	 *            �ndice del v�rtice a sustituir.
	 */
	public void setVertex(N3Point2D p, int i) {
		vertexes.set(i, p);
		parent.notifyGeometryListeners();
	}

	/**
	 * Obtiene el v�rtice 'i'.
	 * 
	 * @return V�rtice
	 * @param i
	 *            �ndice del v�rtice a obtener
	 */
	public N3Point2D getVertex(int i) {
		return (N3Point2D) vertexes.elementAt(i);
	}

	/**
	 * Establece el color del v�rtice 'i'.
	 * 
	 * @param c
	 *            Nuevo color
	 * @param i
	 *            �ndice del color a sustituir.
	 */
	public void setColor(N3ColorRGBA c, int i) {
		colors.set(i, c);
		parent.notifyGeometryListeners();
	}

	/**
	 * Obtiene el color del v�rtice 'i'.
	 * 
	 * @return Color
	 * @param i
	 *            �ndice del color a obtener
	 */
	public N3ColorRGBA getColor(int i) {
		return (N3ColorRGBA) colors.elementAt(i);
	}

	/**
	 * Establece la normal del v�rtice 'i'.
	 * 
	 * @param n
	 *            Nueva normal
	 * @param i
	 *            �ndice de la normal a sustituir.
	 */
	public void setNormal(N3Vector2D n, int i) {
		normals.set(i, n);
		parent.notifyGeometryListeners();
	}

	/**
	 * Obtiene la normal del v�rtice 'i'.
	 * 
	 * @return Normal
	 * @param i
	 *            �ndice de la normal a obtener
	 */
	public N3Vector2D getNormal(int i) {
		if (i < normals.size())
			return (N3Vector3D) normals.elementAt(i);
		else
			return null;
	}

	/**
	 * Establece las coordenadas de textura del v�rtice 'i'.
	 * 
	 * @param uv
	 *            Nueva coordenada de textura
	 * @param i
	 *            �ndice de la coordenada de textura a sustituir.
	 */
	public void setUV(N3TexCoord2D uv, int i) {
		uvs.set(i, uv);
		parent.notifyGeometryListeners();
	}

	/**
	 * Obtiene las coordenadas de textura del v�rtice 'i'.
	 * 
	 * @return Coordenadas de textunra
	 * @param i
	 *            �ndice de las coordenadas de textura a obtener
	 */
	public N3TexCoord2D getUV(int i) {
		if (i < uvs.size())
			return (N3TexCoord2D) uvs.elementAt(i);
		else
			return null;
	}

	/**
	 * Obtiene el n�mero de lados del pol�gono.
	 * 
	 * @return N�mero de lados del pol�gono
	 */
	public int getSides() {
		return vertexes.size();
	}

	/**
	 * Obtiene la normal del poligono.
	 */

	public N3Vector3D getPolygonNormal() {
		N3Vector3D normal = null;
		if (vertexes.size() > 2) {
			N3Point3D c = (N3Point3D) vertexes.elementAt(1);
			N3Point3D e1 = (N3Point3D) vertexes.elementAt(0);
			N3Point3D e2 = (N3Point3D) vertexes.elementAt(2);
			normal = new N3Vector3D(e2.x - c.x, e2.y - c.y, e2.z - c.z);
			normal.crossProduct(new N3Vector3D(e1.x - c.x, e1.y - c.y, e1.z
					- c.z));
			normal.normalize();
		}
		return normal;
	}

	/**
	 * Genera la normal del pol�gono de manera autom�tica para todas sus
	 * vertices.
	 * 
	 * Importante: si la normal del pol�gono ha sido generada por este m�todo,
	 * en el caso de a�adir nuevos v�rtices habr� que volver a generar las
	 * normales.
	 */
	public void generateNormal() {
		if (vertexes.size() > 2) {
			try {
				N3Vector3D normal = getPolygonNormal();
				normals.clear();
				for (int i = 0; i < vertexes.size(); i++)
					normals.add(normal.copy());
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Indica si el pol�gono tiene coordenadas de textura.
	 * 
	 * @return True si tiene coordenadas de textura; False en caso contrario
	 */
	public boolean isTextured() {
		return uvs.size() > 0;
	}

	public void setCollisionable(boolean c) {
	}

	public boolean getCollisionable() {
		return true;
	}

	public boolean test(N3Collisionable c) {
		return true;
	}

	public boolean test(N3Collisionable c, N3Matrix4D m) {
		// Obtenemos un poligono transformado por la matriz.
		N3Polygon p = new N3Polygon();
		for (int i = 0; i < vertexes.size(); i++) {
			N3Point3D v = (N3Point3D) vertexes.elementAt(i);
			v = m.mult(v);
			p.vertexes.add(v);
		}
		// Realizamos la comprobaci�n
		return c.test(p);
	}

	public void getPersistentResources(N3PersistentResourceList resources) {
		for (int i = 0; i < vertexes.size(); i++)
			resources.addResource(getVertex(i));
		for (int i = 0; i < colors.size(); i++)
			resources.addResource(getColor(i));
		for (int i = 0; i < normals.size(); i++)
			resources.addResource(getNormal(i));
		for (int i = 0; i < uvs.size(); i++)
			resources.addResource(getUV(i));
	}
}
